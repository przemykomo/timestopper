package xyz.przemyk.timestopper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import xyz.przemyk.timestopper.capabilities.control.TimeStateHandlerProvider;
import xyz.przemyk.timestopper.network.PacketChangeTimeState;
import xyz.przemyk.timestopper.network.TimeStopperPacketHandler;

@Mod.EventBusSubscriber
public class TimeStopperEvents {

    @SubscribeEvent
    public static void onEntityEvent(EntityEvent event) {
        if (event instanceof EntityJoinWorldEvent || event instanceof RenderPlayerEvent || event instanceof ItemTossEvent) {
            return;
        }

        if (event.isCancelable() && event.getEntity() != null && !TimeStopperMod.canUpdateEntity(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void attachCapabilitiesEvent(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            TimeStateHandlerProvider provider = new TimeStateHandlerProvider();
            event.addCapability(new ResourceLocation(TimeStopperMod.MODID, "time_control"), provider);
            event.addListener(provider::invalidate);
        }
    }

    @SubscribeEvent
    public static void playerLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {
        Player playerEntity = event.getPlayer();
        if (!playerEntity.level.isClientSide) {
            playerEntity.getCapability(TimeStateHandlerProvider.TIME_STATE_CAP).ifPresent(handler ->
                    TimeStopperPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new PacketChangeTimeState(handler.timeState, playerEntity.getUUID())));
        }
    }
}
