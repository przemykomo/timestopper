package xyz.przemyk.timestopper;

import net.minecraft.block.DispenserBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import xyz.przemyk.timestopper.capabilities.CapabilityTimeControl;
import xyz.przemyk.timestopper.capabilities.TimeState;
import xyz.przemyk.timestopper.entities.ModEntities;
import xyz.przemyk.timestopper.entities.active.ActiveTimeStopperEntity;
import xyz.przemyk.timestopper.entities.active.ActiveTimeStopperEntityRenderer;
import xyz.przemyk.timestopper.entities.thrown.ThrownTimeStopperEntity;
import xyz.przemyk.timestopper.items.ModItems;
import xyz.przemyk.timestopper.items.ThrowableTimeStopperItem;
import xyz.przemyk.timestopper.items.TimeStopperItem;
import xyz.przemyk.timestopper.network.TimeStopperPacketHandler;

@Mod(TimeStopperMod.MODID)
public class TimeStopperMod {

    public static final String MODID = "timestopper";
    public static final float TIME_FIELD_SIZE = 12.0f;
    public static final AxisAlignedBB scan = new AxisAlignedBB(-TIME_FIELD_SIZE / 2, -TIME_FIELD_SIZE / 2, -TIME_FIELD_SIZE / 2, TIME_FIELD_SIZE / 2, TIME_FIELD_SIZE / 2, TIME_FIELD_SIZE / 2);

    public TimeStopperMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        CapabilityTimeControl.register();
        TimeStopperPacketHandler.registerMessages();
    }

    private void clientSetup(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.ACTIVE_TIME_STOPPER, ActiveTimeStopperEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.THROWN_TIME_STOPPER, new ThrownTimeStopperRenderFactory());
    }

    private static class ThrownTimeStopperRenderFactory implements IRenderFactory<ThrownTimeStopperEntity> {
        @Override
        public EntityRenderer<? super ThrownTimeStopperEntity> createRenderFor(EntityRendererManager manager) {
            return new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer());
        }
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
            ThrowableTimeStopperItem throwableTimeStopperItem = new ThrowableTimeStopperItem();
            itemRegistryEvent.getRegistry().registerAll(throwableTimeStopperItem, new TimeStopperItem());

            DispenserBlock.registerDispenseBehavior(throwableTimeStopperItem, new ProjectileDispenseBehavior() {
                @Override
                protected ProjectileEntity getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
                    return Util.make(new ThrownTimeStopperEntity(worldIn, position.getX(), position.getY(), position.getZ()), entity -> entity.setItem(stackIn));
                }
            });
        }

        @SubscribeEvent
        public static void onEntityRegistry(final RegistryEvent.Register<EntityType<?>> entityRegistryEvent) {
            entityRegistryEvent.getRegistry()
                    .register(EntityType.Builder.create(ActiveTimeStopperEntity::new, EntityClassification.MISC)
                        .size(0.25F, 0.25F)
                        .setShouldReceiveVelocityUpdates(false)
                        .build("active_timestopper").setRegistryName("active_timestopper"));

            entityRegistryEvent.getRegistry()
                    .register(EntityType.Builder.<ThrownTimeStopperEntity>create(ThrownTimeStopperEntity::new, EntityClassification.MISC)
                            .size(0.25F, 0.25F)
                            .setShouldReceiveVelocityUpdates(false)
                            .build("thrown_timestopper").setRegistryName("thrown_timestopper"));
        }
    }

    public static boolean canUpdate(Vector3d position, World world) {
        AxisAlignedBB toScan = scan.offset(position);
        if (!(world.getEntitiesWithinAABB(ActiveTimeStopperEntity.class, toScan).isEmpty())) {
            return false;
        }

        for (PlayerEntity playerEntity : world.getEntitiesWithinAABB(PlayerEntity.class, toScan)) {
            if (playerEntity.getCapability(CapabilityTimeControl.TIME_CONTROL_CAPABILITY).map(h -> h.getState() == TimeState.STOPPED).orElse(false)) {
                return false;
            }
        }

        return true;
    }

    public static boolean canUpdateEntity(Entity entity) {
        AxisAlignedBB toScan = scan.offset(entity.getPositionVec());
        if (!(entity.world.getEntitiesWithinAABB(ActiveTimeStopperEntity.class, toScan).isEmpty())) {
            return false;
        }

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
}
