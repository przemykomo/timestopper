package xyz.przemyk.timestopper.entities.thrown;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import xyz.przemyk.timestopper.entities.ModEntities;
import xyz.przemyk.timestopper.entities.active.ActiveTimeStopperEntity;
import xyz.przemyk.timestopper.items.ModItems;

import static xyz.przemyk.timestopper.TimeStopperMod.TIME_FIELD_SIZE;

public class ThrownTimeStopperEntity extends ProjectileItemEntity {
    public ThrownTimeStopperEntity(EntityType<? extends ProjectileItemEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @SuppressWarnings("ConstantConditions")
    public ThrownTimeStopperEntity(World world, LivingEntity thrower) {
        super(ModEntities.THROWN_TIME_STOPPER, thrower, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.TIME_STOPPER_ITEM;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onImpact(RayTraceResult result) {
        super.onImpact(result);
        world.addParticle(ParticleTypes.FLASH, getPosX(), getPosY() + 0.5, getPosZ(), 0.0, 0.0, 0.0);
        if (!(world.isRemote() || removed)) {
            world.addEntity(new ActiveTimeStopperEntity(world, getPositionVec().subtract(0.0, TIME_FIELD_SIZE / 2.0, 0.0)));
            remove();
        }
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
