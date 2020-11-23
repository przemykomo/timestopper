package xyz.przemyk.timestopper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.PacketDistributor;
import xyz.przemyk.timestopper.capabilities.control.CapabilityTimeControl;
import xyz.przemyk.timestopper.capabilities.control.ITimeStateHandler;
import xyz.przemyk.timestopper.capabilities.control.TimeState;
import xyz.przemyk.timestopper.capabilities.tick.CapabilityConditionalTick;
import xyz.przemyk.timestopper.items.ModItems;
import xyz.przemyk.timestopper.items.TimeStateSwitcherItem;
import xyz.przemyk.timestopper.network.PacketChangeConditionalTick;
import xyz.przemyk.timestopper.network.PacketChangeTimeState;
import xyz.przemyk.timestopper.network.TimeStopperPacketHandler;

import java.util.concurrent.atomic.AtomicInteger;

@Mod(TimeStopperMod.MODID)
public class TimeStopperMod {

    public static final String MODID = "timestopper";
    public static final float TIME_FIELD_SIZE = 12.0f;
    public static final AxisAlignedBB scan = new AxisAlignedBB(-TIME_FIELD_SIZE / 2, -TIME_FIELD_SIZE / 2, -TIME_FIELD_SIZE / 2, TIME_FIELD_SIZE / 2, TIME_FIELD_SIZE / 2, TIME_FIELD_SIZE / 2);

    public TimeStopperMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        CapabilityTimeControl.register();
        CapabilityConditionalTick.register();
        TimeStopperPacketHandler.registerMessages();
    }

    public static final ItemGroup TIME_STOPPER_ITEM_GROUP = new ItemGroup(ItemGroup.GROUPS.length, "timestoppergroup") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.TIME_STOPPER_ITEM);
        }
    };

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onItemRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
            itemRegistryEvent.getRegistry().registerAll(
                    new TimeStateSwitcherItem(TimeState.STOPPED).setRegistryName("timestopper"),
                    new TimeStateSwitcherItem(TimeState.FAST).setRegistryName("timeaccelerator"),
                    new TimeStateSwitcherItem(TimeState.SLOW).setRegistryName("timedecelerator")
            );
        }
    }

    public static boolean canUpdate(Vector3d position, World world) {
        AxisAlignedBB toScan = scan.offset(position);

        for (PlayerEntity playerEntity : world.getEntitiesWithinAABB(PlayerEntity.class, toScan)) {
            if (playerEntity.getCapability(CapabilityTimeControl.TIME_CONTROL_CAPABILITY).map(h -> h.getState() == TimeState.STOPPED).orElse(false)) {
                return false;
            }
        }

        return true;
    }

    public static boolean canUpdateEntity(Entity entity) {
        AxisAlignedBB toScan = scan.offset(entity.getPositionVec());

        for (PlayerEntity playerEntity : entity.world.getEntitiesWithinAABB(PlayerEntity.class, toScan)) {
            if (playerEntity == entity) {
                continue;
            }
            if (playerEntity.getCapability(CapabilityTimeControl.TIME_CONTROL_CAPABILITY).map(h -> h.getState() == TimeState.STOPPED).orElse(false)) {
                return false;
            }
        }
        return true;
    }

    public static void setTimeState(PlayerEntity player, TimeState timeState, ITimeStateHandler timeStateHandler) {
        timeStateHandler.setState(timeState);
        player.sendStatusMessage(timeStateHandler.getState().toTextComponent(), true);
        if (!player.world.isRemote) {
            TimeStopperPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new PacketChangeTimeState(timeState, player.getUniqueID()));
        }
    }

    //TODO: move time state detection to some other generic method so there won't be duplicates in canUpdate and here
    public static void updateEntity(Entity entity, boolean server) {
        if (entity.canUpdate()) {

            AtomicInteger fastTimeCounter = new AtomicInteger();

            for (PlayerEntity playerEntity : entity.world.getEntitiesWithinAABB(PlayerEntity.class, scan.offset(entity.getPositionVec()))) {
                playerEntity.getCapability(CapabilityTimeControl.TIME_CONTROL_CAPABILITY).ifPresent(h -> {
                    if (h.getState() == TimeState.FAST) {
                        fastTimeCounter.incrementAndGet();
                    } else if (h.getState() == TimeState.SLOW) {
                        fastTimeCounter.decrementAndGet();
                    }
                });
            }

            if (fastTimeCounter.get() >= 0) {
                entity.tick();
                if (fastTimeCounter.get() > 0) {
                    entity.tick(); // tick entity twice if time state is fast
                }
            } else {
                entity.getCapability(CapabilityConditionalTick.CONDITIONAL_TICK_CAPABILITY).ifPresent(h -> {
                    if (h.canTick()) { //TODO I probably don't need to use new capability, I could use entity.ticksExisted
                        entity.tick();
                    }
                    h.switchState();
                    if (server) {
                        TimeStopperPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new PacketChangeConditionalTick(h.canTick(), entity.getEntityId()));
                    }
                });
            }
        }
    }
}
