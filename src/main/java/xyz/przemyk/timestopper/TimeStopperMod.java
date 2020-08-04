package xyz.przemyk.timestopper;

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
import xyz.przemyk.timestopper.entities.ThrownTimeStopperEntity;
import xyz.przemyk.timestopper.entities.ThrownTimeStopperEntityRenderer;
import xyz.przemyk.timestopper.items.ModItems;
import xyz.przemyk.timestopper.items.TimeStopperItem;

@Mod(TimeStopperMod.MODID)
public class TimeStopperMod {

    public static final String MODID = "timestopper";

    public TimeStopperMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.THROWN_TIME_STOPPER, ThrownTimeStopperEntityRenderer::new);
    }

    public static final ItemGroup TIME_STOPPER_ITEM_GROUP = new ItemGroup(ItemGroup.GROUPS.length, "timestoppergroup") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.timeStopperItem);
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
                    .register(EntityType.Builder.create(ThrownTimeStopperEntity::new, EntityClassification.MISC)
                    .size(0.25F, 0.25F)
                    .setShouldReceiveVelocityUpdates(false)
                    .build("thrown_timestopper").setRegistryName("thrown_timestopper"));
        }
    }
}
