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
import xyz.przemyk.timestopper.capabilities.control.CapabilityTimeControl;
import xyz.przemyk.timestopper.capabilities.control.TimeStateHandlerProvider;
import xyz.przemyk.timestopper.capabilities.tick.CapabilityConditionalTick;
import xyz.przemyk.timestopper.capabilities.tick.ConditionalTickHandlerProvider;
import xyz.przemyk.timestopper.network.PacketChangeConditionalTick;
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
        if (event.getObject() instanceof PlayerEntity) {
            TimeStateHandlerProvider provider = new TimeStateHandlerProvider();
            event.addCapability(new ResourceLocation(TimeStopperMod.MODID, "time_control"), provider);
            event.addListener(provider::invalidate);
        }

        ConditionalTickHandlerProvider provider = new ConditionalTickHandlerProvider();
        event.addCapability(new ResourceLocation(TimeStopperMod.MODID, "conditional_tick"), provider);
        event.addListener(provider::invalidate);
    }

    @SubscribeEvent
    public static void playerLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity playerEntity = event.getPlayer();
        if (!playerEntity.world.isRemote) {
            playerEntity.getCapability(CapabilityTimeControl.TIME_CONTROL_CAPABILITY).ifPresent(h ->
                    TimeStopperPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new PacketChangeTimeState(h.getState(), playerEntity.getUniqueID())));
            playerEntity.getCapability(CapabilityConditionalTick.CONDITIONAL_TICK_CAPABILITY).ifPresent(h ->
                    TimeStopperPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new PacketChangeConditionalTick(h.canTick(), playerEntity.getEntityId())));
        }
    }
}
