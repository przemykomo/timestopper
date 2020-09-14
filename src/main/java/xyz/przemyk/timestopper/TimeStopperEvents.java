package xyz.przemyk.timestopper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import xyz.przemyk.timestopper.capabilities.CapabilityTimeControl;
import xyz.przemyk.timestopper.capabilities.TimeControlProvider;
import xyz.przemyk.timestopper.entities.active.ActiveTimeStopperEntity;
import xyz.przemyk.timestopper.network.PacketChangeTimeState;
import xyz.przemyk.timestopper.network.TimeStopperPacketHandler;

import static xyz.przemyk.timestopper.entities.active.ActiveTimeStopperEntity.scan;

@Mod.EventBusSubscriber
public class TimeStopperEvents {

    @SubscribeEvent
    public static void onEntityEvent(EntityEvent event) {
        if (event instanceof EntityJoinWorldEvent || event instanceof RenderPlayerEvent || event instanceof ItemTossEvent) {
            return;
        }

        if (event.isCancelable() && event.getEntity() != null && !event.getEntity().world.getEntitiesWithinAABB(ActiveTimeStopperEntity.class, scan.offset(event.getEntity().getPositionVec())).isEmpty()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void attachCapabilitiesEvent(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            TimeControlProvider provider = new TimeControlProvider();
            event.addCapability(new ResourceLocation(TimeStopperMod.MODID, "time_control"), provider);
            event.addListener(provider::invalidate);
        }
    }

    @SubscribeEvent
    public static void playerLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity playerEntity = event.getPlayer();
        if (!playerEntity.world.isRemote) {
            playerEntity.getCapability(CapabilityTimeControl.TIME_CONTROL_CAPABILITY).ifPresent(h ->
                    TimeStopperPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new PacketChangeTimeState(h.getState(), playerEntity.getUniqueID())));
        }
    }
}
