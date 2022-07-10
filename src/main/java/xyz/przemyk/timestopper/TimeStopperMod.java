package xyz.przemyk.timestopper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import xyz.przemyk.timestopper.capabilities.control.TimeState;
import xyz.przemyk.timestopper.capabilities.control.TimeStateHandler;
import xyz.przemyk.timestopper.capabilities.control.TimeStateHandlerProvider;
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
    }

    public static final CreativeModeTab TIME_STOPPER_ITEM_GROUP = new CreativeModeTab(MODID) {
        @Override
        public ItemStack makeIcon() {
            return TimeStopperItems.STOPPER.get().getDefaultInstance();
        }
    };

    public static boolean canUpdateEntity(Entity entity) {
        AABB toScan = scan.move(entity.position());
        if (entity.getCapability(TimeStateHandlerProvider.TIME_STATE_CAP).map(h -> h.timeState == TimeState.STOPPED).orElse(false)) {
            return true;
        }

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

    public static void updateBlockEntity(TickingBlockEntity tickingBlockEntity, Level level) {
        TimeState timeState = TimeState.NORMAL;

        for (Player playerEntity : level.getEntitiesOfClass(Player.class, scan.move(tickingBlockEntity.getPos()))) {
            TimeState newTimeState = playerEntity.getCapability(TimeStateHandlerProvider.TIME_STATE_CAP).map(handler -> handler.timeState).orElse(TimeState.NORMAL);
            if (newTimeState.ordinal() > timeState.ordinal()) {
                timeState = newTimeState;
            }
        }

        switch (timeState) {
            case NORMAL:
                tickingBlockEntity.tick();
                break;
            case SLOW:
                if (level.getGameTime() % 2 == 0) {
                    tickingBlockEntity.tick();
                }
                break;
            case FAST:
                tickingBlockEntity.tick();
                tickingBlockEntity.tick();
        }
    }

    public static void updateEntity(Entity entity) {
        TimeState timeState = TimeState.NORMAL;

        for (Player playerEntity : entity.level.getEntitiesOfClass(Player.class, scan.move(entity.position()))) {
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
            case NORMAL:
                entity.tick();
                break;
            case SLOW:
                if (entity.level.getGameTime() % 2 == 0) {
                    entity.tick();
                }
                break;
            case FAST:
                entity.tick();
                entity.tick();
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
            case NORMAL:
                particle.tick();
                break;
            case SLOW:
                if (level.getGameTime() % 2 == 0) {
                    particle.tick();
                }
                break;
            case FAST:
                particle.tick();
                particle.tick();
        }
    }
}
