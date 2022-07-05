package xyz.przemyk.timestopper;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import xyz.przemyk.timestopper.capabilities.control.TimeState;
import xyz.przemyk.timestopper.capabilities.control.TimeStateHandler;
import xyz.przemyk.timestopper.capabilities.control.TimeStateHandlerProvider;
import xyz.przemyk.timestopper.network.PacketChangeTimeState;
import xyz.przemyk.timestopper.network.TimeStopperPacketHandler;
import xyz.przemyk.timestopper.setup.TimeStopperCapabilities;
import xyz.przemyk.timestopper.setup.TimeStopperItems;

import java.util.concurrent.atomic.AtomicInteger;

@Mod(TimeStopperMod.MODID)
public class TimeStopperMod {

    public static final String MODID = "timestopper";
    public static final float TIME_FIELD_SIZE = 12.0f;
    public static final AABB scan = new AABB(-TIME_FIELD_SIZE / 2, -TIME_FIELD_SIZE / 2, -TIME_FIELD_SIZE / 2, TIME_FIELD_SIZE / 2, TIME_FIELD_SIZE / 2, TIME_FIELD_SIZE / 2);

    public TimeStopperMod() {
        TimeStopperPacketHandler.init();
        TimeStopperItems.init();
        TimeStopperCapabilities.init();
    }

    public static final CreativeModeTab TIME_STOPPER_ITEM_GROUP = new CreativeModeTab(MODID) {
        @Override
        public ItemStack makeIcon() {
            return TimeStopperItems.STOPPER.get().getDefaultInstance();
        }
    };

    public static boolean canUpdate(Vec3 position, Level world) {
        AABB toScan = scan.move(position);

        for (Player playerEntity : world.getEntitiesOfClass(Player.class, toScan)) {
            if (playerEntity.getCapability(TimeStateHandlerProvider.TIME_STATE_CAP).map(h -> h.timeState == TimeState.STOPPED).orElse(false)) {
                return false;
            }
        }

        return true;
    }

    public static boolean canUpdateEntity(Entity entity) {
        AABB toScan = scan.move(entity.position());

        for (Player playerEntity : entity.level.getEntitiesOfClass(Player.class, toScan)) {
            if (playerEntity == entity) {
                continue;
            }
            if (playerEntity.getCapability(TimeStateHandlerProvider.TIME_STATE_CAP).map(h -> h.timeState == TimeState.STOPPED).orElse(false)) {
                return false;
            }
        }
        return true;
    }

    public static void setTimeState(Player player, TimeState timeState, TimeStateHandler timeStateHandler) {
        timeStateHandler.timeState = timeState;
        player.displayClientMessage(timeStateHandler.timeState.toTextComponent(), true);
        if (!player.level.isClientSide) {
            TimeStopperPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new PacketChangeTimeState(timeState, player.getUUID()));
        }
    }

    //TODO: move time state detection to some other generic method so there won't be duplicates in canUpdate and here
    public static void updateEntity(Entity entity) {
        if (entity.canUpdate()) {

            AtomicInteger fastTimeCounter = new AtomicInteger();

            for (Player playerEntity : entity.level.getEntitiesOfClass(Player.class, scan.move(entity.position()))) {
                if (entity != playerEntity) {
                    playerEntity.getCapability(TimeStateHandlerProvider.TIME_STATE_CAP).ifPresent(handler -> {
                        if (handler.timeState == TimeState.FAST) {
                            fastTimeCounter.incrementAndGet();
                        } else if (handler.timeState == TimeState.SLOW) {
                            fastTimeCounter.decrementAndGet();
                        }
                    });
                }
            }

            if (fastTimeCounter.get() >= 0) {
                entity.tick();
                if (fastTimeCounter.get() > 0) {
                    entity.tick(); // tick entity twice if time state is fast
                }
            } else {
                if (entity.level.getGameTime() % 2 == 0) {
                    entity.tick();
                }
            }
        }
    }
}
