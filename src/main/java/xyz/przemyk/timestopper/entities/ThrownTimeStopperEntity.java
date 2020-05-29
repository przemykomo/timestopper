package xyz.przemyk.timestopper.entities;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.*;

public class ThrownTimeStopperEntity extends Entity {

    private static final DataParameter<String> ENTITY_DATA_JSON = EntityDataManager.createKey(ThrownTimeStopperEntity.class, DataSerializers.STRING);

    private ThrownTimeStopperData stopperData = new ThrownTimeStopperData();
    private AxisAlignedBB scanEntities;

    private final Gson gson = new Gson();

    public ThrownTimeStopperEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    public ThrownTimeStopperEntity(World worldIn, Vec3d pos) {
        this(ModEntities.THROWN_TIME_STOPPER, worldIn);
        setPosition(pos.x, pos.y, pos.z);
        scanEntities = new AxisAlignedBB(-4, -4, -4, 4, 4, 4).offset(pos);

        stopperData.timeLeft = 60;

        for (Entity stoppedEntity : world.getEntitiesWithinAABBExcludingEntity(this, scanEntities)) {
            //TODO: Make it work on players
            if (stoppedEntity instanceof PlayerEntity) {
                continue;
            }

            UUID id = stoppedEntity.getUniqueID();

            stopperData.stoppedEntitiesID.add(id);

            //TODO: move to onAddedToWorld?
            stoppedEntity.setNoGravity(true);
            stopperData.savedMotion.put(id, stoppedEntity.getMotion());
            stopperData.savedPosition.put(id, stoppedEntity.getPositionVec());
            stopperData.savedRotation.put(id, stoppedEntity.getRotationYawHead());
            stoppedEntity.setMotion(0, 0, 0);
            stoppedEntity.setSilent(true);

            if (stoppedEntity instanceof LivingEntity) {
                ((LivingEntity) stoppedEntity).addPotionEffect(new EffectInstance(Effects.GLOWING, stopperData.timeLeft, 0, false, false));
                if (stoppedEntity instanceof MobEntity) {
                    ((MobEntity) stoppedEntity).setNoAI(true);
                }
            }
        }
    }

    @Override
    protected void registerData() {
        dataManager.register(ENTITY_DATA_JSON, "");
    }

    @Override
    protected void readAdditional(CompoundNBT nbt) {
        String stopperDataJson = nbt.getString("stopperData");
        stopperData = gson.fromJson(stopperDataJson, ThrownTimeStopperData.class);

        dataManager.set(ENTITY_DATA_JSON, stopperDataJson);
    }

    @Override
    protected void writeAdditional(CompoundNBT nbt) {
        nbt.putString("stopperData", gson.toJson(stopperData));
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);

        if (ENTITY_DATA_JSON.equals(key)) {
            stopperData = gson.fromJson(dataManager.get(ENTITY_DATA_JSON), ThrownTimeStopperData.class);
        }
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return new SSpawnObjectPacket(this);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();

        if (world instanceof ServerWorld) {
            for (UUID id : stopperData.stoppedEntitiesID) {
                Entity stoppedEntity = ((ServerWorld) world).getEntityByUuid(id);
                if (stoppedEntity == null) {
                    continue;
                }
                stoppedEntity.setNoGravity(false);
                stoppedEntity.setMotion(stopperData.savedMotion.get(id));
                stoppedEntity.setSilent(false);

                if (stoppedEntity instanceof MobEntity) {
                    ((MobEntity) stoppedEntity).setNoAI(false);
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(stopperData.timeLeft > 0) {
            --stopperData.timeLeft;

            if (world instanceof ServerWorld) {
            for (UUID id : stopperData.stoppedEntitiesID) {

                Entity stoppedEntity = ((ServerWorld) world).getEntityByUuid(id);
                if (stoppedEntity == null) {
                    continue;
                }
                stoppedEntity.setMotion(0, 0, 0);
                Vec3d pos = stopperData.savedPosition.get(id);
                stoppedEntity.setPosition(pos.x, pos.y, pos.z);
                stoppedEntity.setRotationYawHead(stopperData.savedRotation.get(id));
            }

            ((ServerWorld) world).spawnParticle(ParticleTypes.ENCHANT, getPosX(), getPosY(), getPosZ(), 2, 0.2, 0.2, 0.2, 0.0);
            }
        } else {
            remove();
        }
    }
}
