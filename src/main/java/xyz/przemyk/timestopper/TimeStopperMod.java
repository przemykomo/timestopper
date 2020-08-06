package xyz.przemyk.timestopper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import xyz.przemyk.timestopper.entities.ModEntities;
import xyz.przemyk.timestopper.entities.active.ActiveTimeStopperEntity;
import xyz.przemyk.timestopper.entities.active.ActiveTimeStopperEntityRenderer;
import xyz.przemyk.timestopper.entities.thrown.ThrownTimeStopperEntity;
import xyz.przemyk.timestopper.items.ModItems;
import xyz.przemyk.timestopper.items.TimeStopperItem;

@Mod(TimeStopperMod.MODID)
public class TimeStopperMod {

    public static final String MODID = "timestopper";
    public static final float TIME_FIELD_SIZE = 12.0f;

    public TimeStopperMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.ACTIVE_TIME_STOPPER, ActiveTimeStopperEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.THROWN_TIME_STOPPER, renderManager -> new SpriteRenderer<>(renderManager, Minecraft.getInstance().getItemRenderer()));
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
            itemRegistryEvent.getRegistry().register(new TimeStopperItem());
        }

        @SubscribeEvent
        public static void onEntityRegistry(final RegistryEvent.Register<EntityType<?>> entityRegistryEvent) {
            entityRegistryEvent.getRegistry()
                    .register(EntityType.Builder.create(ActiveTimeStopperEntity::new, EntityClassification.MISC)
                        .size(12.0F, 12.0F)
                        .setShouldReceiveVelocityUpdates(false)
                        .build("active_timestopper").setRegistryName("active_timestopper"));

            entityRegistryEvent.getRegistry()
                    .register(EntityType.Builder.<ThrownTimeStopperEntity>create(ThrownTimeStopperEntity::new, EntityClassification.MISC)
                            .size(0.25F, 0.25F)
                            .setShouldReceiveVelocityUpdates(false)
                            .build("thrown_timestopper").setRegistryName("thrown_timestopper"));
        }
    }
}
