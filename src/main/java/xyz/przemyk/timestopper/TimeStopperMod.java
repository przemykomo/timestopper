package xyz.przemyk.timestopper;

import com.google.common.collect.Iterables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import xyz.przemyk.timestopper.capabilities.control.TimeState;
import xyz.przemyk.timestopper.capabilities.control.TimeStateHandler;
import xyz.przemyk.timestopper.capabilities.control.TimeStateHandlerProvider;
import xyz.przemyk.timestopper.capabilities.item.PearlEnergyProvider;
import xyz.przemyk.timestopper.items.TimeStateSwitcherItem;
import xyz.przemyk.timestopper.network.PacketChangeTimeState;
import xyz.przemyk.timestopper.network.TimeStopperPacketHandler;
import xyz.przemyk.timestopper.setup.TimeStopperCapabilities;
import xyz.przemyk.timestopper.setup.TimeStopperItems;

@Mod(TimeStopperMod.MODID)
public class TimeStopperMod {

    public static final String MODID = "timestopper";
    public static final float TIME_FIELD_SIZE = 12.0f;
    public static final AABB scan = new AABB(-TIME_FIELD_SIZE / 2, -TIME_FIELD_SIZE / 2, -TIME_FIELD_SIZE / 2, TIME_FIELD_SIZE / 2, TIME_FIELD_SIZE / 2, TIME_FIELD_SIZE / 2);

    public TimeStopperMod() {
        TimeStopperPacketHandler.init();
        TimeStopperItems.init();
        TimeStopperCapabilities.init();

        MinecraftForge.EVENT_BUS.addListener(TimeStopperMod::onPlayerTick);
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            event.player.getCapability(TimeStateHandlerProvider.TIME_STATE_CAP).ifPresent(timeStateHandler -> {
                if (timeStateHandler.timeState != TimeState.NORMAL) {
                    Inventory inventory = event.player.getInventory();
                    for (ItemStack itemStack : Iterables.concat(inventory.items, inventory.offhand)) {
                        if (itemStack.getItem() instanceof TimeStateSwitcherItem timeStateSwitcherItem) {
                            if (timeStateHandler.timeState == timeStateSwitcherItem.OTHER_STATE) {
                                if (itemStack.getCapability(PearlEnergyProvider.PEARL_ENERGY_STORAGE_CAP).map(energyStorage -> {
                                    int energy = energyStorage.getEnergy();
                                    if (energy > 0) {
                                        energy--;
                                        energyStorage.setEnergy(energy);
                                        if (energy == 0) {
                                            setTimeState(event.player, TimeState.NORMAL, timeStateHandler);
                                        }
                                        return true;
                                    }
                                    return false;
                                }).orElse(false)) {
                                    return;
                                };
                            }
                        }
                    }
                }
            });
        }
    }

    public static boolean canUpdateEntity(Entity entity) {
        AABB toScan = scan.move(entity.position());
        if (entity.getCapability(TimeStateHandlerProvider.TIME_STATE_CAP).map(h -> h.timeState == TimeState.STOPPED).orElse(false)) {
            return true;
        }

        for (Player playerEntity : entity.level().getEntitiesOfClass(Player.class, toScan)) {
            if (playerEntity == entity) {
                continue;
            }

            if (playerEntity.getCapability(TimeStateHandlerProvider.TIME_STATE_CAP).map(h -> h.timeState == TimeState.STOPPED).orElse(false)) {
                return false;
            }
        }
        return true;
    }

    public static boolean canRandomTick(BlockPos blockPos, Level level) {
        for (Player playerEntity : level.getEntitiesOfClass(Player.class, scan.move(blockPos))) {
            if (playerEntity.getCapability(TimeStateHandlerProvider.TIME_STATE_CAP).map(h -> h.timeState == TimeState.STOPPED).orElse(false)) {
                return false;
            }
        }
        return true;
    }

    public static void setTimeState(Player player, TimeState timeState, TimeStateHandler timeStateHandler) {
        timeStateHandler.timeState = timeState;
        player.displayClientMessage(timeStateHandler.timeState.toTextComponent(), true);
        if (!player.level().isClientSide) {
            TimeStopperPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new PacketChangeTimeState(timeState, player.getUUID()));
        }
    }

    public static void updateBlockEntity(TickingBlockEntity tickingBlockEntity, Level level) {
        TimeState timeState = TimeState.NORMAL;

        for (Player playerEntity : level.getEntitiesOfClass(Player.class, scan.move(tickingBlockEntity.getPos()))) {
            TimeState newTimeState = playerEntity.getCapability(TimeStateHandlerProvider.TIME_STATE_CAP).map(handler -> handler.timeState).orElse(TimeState.NORMAL);
            if (newTimeState.ordinal() > timeState.ordinal()) {
                timeState = newTimeState;
            }
        }

        switch (timeState) {
            case NORMAL -> tickingBlockEntity.tick();
            case SLOW -> {
                if (level.getGameTime() % 2 == 0) {
                    tickingBlockEntity.tick();
                }
            }
            case FAST -> {
                tickingBlockEntity.tick();
                tickingBlockEntity.tick();
            }
        }
    }

    public static void updateEntity(Entity entity) {
        TimeState timeState = TimeState.NORMAL;

        for (Player playerEntity : entity.level().getEntitiesOfClass(Player.class, scan.move(entity.position()))) {
            if (entity != playerEntity) {
                TimeState newTimeState = playerEntity.getCapability(TimeStateHandlerProvider.TIME_STATE_CAP).map(handler -> {
                    if (handler.timeState == TimeState.STOPPED &&
                            entity.getCapability(TimeStateHandlerProvider.TIME_STATE_CAP).map(handlerEntity -> handlerEntity.timeState == TimeState.STOPPED).orElse(false)) {
                        return TimeState.NORMAL;
                    } else {
                        return handler.timeState;
                    }
                }).orElse(TimeState.NORMAL);
                if (newTimeState.ordinal() > timeState.ordinal()) {
                    timeState = newTimeState;
                }
            }
        }

        switch (timeState) {
            case NORMAL -> entity.tick();
            case SLOW -> {
                if (entity.level().getGameTime() % 2 == 0) {
                    entity.tick();
                }
            }
            case FAST -> {
                entity.tick();
                entity.tick();
            }
        }
    }

    public static void updateParticle(Particle particle) {
        TimeState timeState = TimeState.NORMAL;
        Level level = Minecraft.getInstance().level;

        for (Player playerEntity : level.getEntitiesOfClass(Player.class, scan.move(particle.x, particle.y, particle.z))) {
            TimeState newTimeState = playerEntity.getCapability(TimeStateHandlerProvider.TIME_STATE_CAP).map(handler -> handler.timeState).orElse(TimeState.NORMAL);
            if (newTimeState.ordinal() > timeState.ordinal()) {
                timeState = newTimeState;
            }
        }

        switch (timeState) {
            case NORMAL -> particle.tick();
            case SLOW -> {
                if (level.getGameTime() % 2 == 0) {
                    particle.tick();
                }
            }
            case FAST -> {
                particle.tick();
                particle.tick();
            }
        }
    }
}
