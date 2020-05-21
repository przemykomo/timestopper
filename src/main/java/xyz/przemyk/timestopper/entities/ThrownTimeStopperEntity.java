package xyz.przemyk.timestopper.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThrownTimeStopperEntity extends Entity {

    private int timeLeft;
    private AxisAlignedBB scanEntities;

    public void setTimeLeft(int timeIn) {
        timeLeft = timeIn;
    }

    public ThrownTimeStopperEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    public ThrownTimeStopperEntity(World worldIn, Vec3d pos) {
        this(ModEntities.THROWN_TIME_STOPPER, worldIn);
        setPosition(pos.x, pos.y, pos.z);
        scanEntities = new AxisAlignedBB(-4, -4, -4, 4, 4, 4).offset(pos);
    }

    @Override
    protected void registerData() {
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        setTimeLeft(compound.getShort("TimeLeft"));
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.putShort("TimeLeft", (short) timeLeft);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return new SSpawnObjectPacket(this);
    }

    List<Entity> stoppedEntities;
    Map<Entity, Vec3d> savedMotion = new HashMap<>();
    Map<Entity, Vec3d> savedPosition = new HashMap<>();
    Map<Entity, Float> savedRotation = new HashMap<>();

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        setTimeLeft(60);

        stoppedEntities = world.getEntitiesWithinAABBExcludingEntity(this, scanEntities);

        for (Entity stoppedEntity : stoppedEntities) {
            if (stoppedEntity instanceof PlayerEntity) {
                continue;
            }
            stoppedEntity.setNoGravity(true);
            savedMotion.put(stoppedEntity, stoppedEntity.getMotion());
            savedPosition.put(stoppedEntity, stoppedEntity.getPositionVec());
            savedRotation.put(stoppedEntity, stoppedEntity.getRotationYawHead());
            stoppedEntity.setMotion(0, 0, 0);
            stoppedEntity.setSilent(true);

//            if (stoppedEntity instanceof LivingEntity) {
//                ((LivingEntity) stoppedEntity).deserializeNBT();
//            }
        }
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();

        for (Entity stoppedEntity : stoppedEntities) {
            if (stoppedEntity instanceof PlayerEntity) {
                continue;
            }
            stoppedEntity.setNoGravity(false);
            stoppedEntity.setMotion(savedMotion.get(stoppedEntity));
            stoppedEntity.setSilent(false);
        }
    }


    @Override
    public void tick() {
        super.tick();
        if(timeLeft > 0) {
            timeLeft--;

            for (Entity stoppedEntity : stoppedEntities) {
                if (stoppedEntity instanceof PlayerEntity) {
                    continue;
                }
                stoppedEntity.setMotion(0, 0, 0);
                Vec3d pos = savedPosition.get(stoppedEntity);
                stoppedEntity.setPosition(pos.x, pos.y, pos.z);
                stoppedEntity.setRotationYawHead(savedRotation.get(stoppedEntity));
            }

        } else {
            remove();
        }
    }
}
