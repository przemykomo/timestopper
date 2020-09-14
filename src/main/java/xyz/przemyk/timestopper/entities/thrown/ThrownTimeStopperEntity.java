package xyz.przemyk.timestopper.entities.thrown;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import xyz.przemyk.timestopper.entities.ModEntities;
import xyz.przemyk.timestopper.entities.active.ActiveTimeStopperEntity;
import xyz.przemyk.timestopper.items.ModItems;

public class ThrownTimeStopperEntity extends ProjectileItemEntity {
    public ThrownTimeStopperEntity(EntityType<? extends ProjectileItemEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @SuppressWarnings("ConstantConditions")
    public ThrownTimeStopperEntity(World world, LivingEntity thrower) {
        super(ModEntities.THROWN_TIME_STOPPER, thrower, world);
    }

    @SuppressWarnings("ConstantConditions")
    public ThrownTimeStopperEntity(World worldIn, double x, double y, double z) {
        super(ModEntities.THROWN_TIME_STOPPER, x, y, z, worldIn);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.THROWABLE_TIME_STOPPER_ITEM;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onImpact(RayTraceResult result) {
        super.onImpact(result);
        world.addParticle(ParticleTypes.FLASH, getPosX(), getPosY() + 0.5, getPosZ(), 0.0, 0.0, 0.0);
        if (!(world.isRemote() || removed)) {
            world.addEntity(new ActiveTimeStopperEntity(world, getPositionVec()));
            playSound(SoundEvents.ENTITY_SPLASH_POTION_BREAK, 1.0F, 1.0F);
            remove();
        }
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean canUpdate() {
        return true;
    }
}
