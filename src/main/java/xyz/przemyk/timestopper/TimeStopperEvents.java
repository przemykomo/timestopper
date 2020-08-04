package xyz.przemyk.timestopper;

import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.przemyk.timestopper.entities.ThrownTimeStopperEntity;

import static xyz.przemyk.timestopper.entities.ThrownTimeStopperEntity.scan;

@Mod.EventBusSubscriber
public class TimeStopperEvents {

    @SubscribeEvent
    public static void onEntityEvent(EntityEvent event) {
        if (event instanceof EntityJoinWorldEvent || event instanceof RenderPlayerEvent) {
            return;
        }
        if (event.isCancelable() && event.getEntity() != null && !event.getEntity().world.getEntitiesWithinAABB(ThrownTimeStopperEntity.class, scan.offset(event.getEntity().getPositionVec())).isEmpty()) {
            event.setCanceled(true);
        }
    }
}
