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
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.*;

public class ThrownTimeStopperEntity extends Entity {

    private int timeLeft;
    private AxisAlignedBB scanEntities;

    private List<UUID> stoppedEntitiesID = new ArrayList<>();
    private Map<UUID, Vec3d> savedMotion = new HashMap<>();
    private Map<UUID, Vec3d> savedPosition = new HashMap<>();
    private Map<UUID, Float> savedRotation = new HashMap<>();

    private List<Entity> stoppedEntities = new ArrayList<>();

    private final Gson gson = new Gson();

    public ThrownTimeStopperEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    public ThrownTimeStopperEntity(World worldIn, Vec3d pos) {
        this(ModEntities.THROWN_TIME_STOPPER, worldIn);
        setPosition(pos.x, pos.y, pos.z);
        scanEntities = new AxisAlignedBB(-4, -4, -4, 4, 4, 4).offset(pos);

        timeLeft = 60;

        for (Entity stoppedEntity : world.getEntitiesWithinAABBExcludingEntity(this, scanEntities)) {
            //TODO: Make it work on players
            if (stoppedEntity instanceof PlayerEntity) {
                continue;
            }

            UUID uuid = stoppedEntity.getUniqueID();

            stoppedEntitiesID.add(uuid);

            stoppedEntity.setNoGravity(true);
            savedMotion.put(uuid, stoppedEntity.getMotion());
            savedPosition.put(uuid, stoppedEntity.getPositionVec());
            savedRotation.put(uuid, stoppedEntity.getRotationYawHead());
            stoppedEntity.setMotion(0, 0, 0);
            stoppedEntity.setSilent(true);

            if (stoppedEntity instanceof LivingEntity) {
                ((LivingEntity) stoppedEntity).addPotionEffect(new EffectInstance(Effects.GLOWING, timeLeft));
                if (stoppedEntity instanceof MobEntity) {
                    ((MobEntity) stoppedEntity).setNoAI(true);
                }
            }
        }
    }

    @Override
    protected void registerData() {
    }

    @Override
    protected void readAdditional(CompoundNBT nbt) {
        timeLeft = nbt.getShort("TimeLeft");
        stoppedEntitiesID = gson.fromJson(nbt.getString("stoppedEntities"), new TypeToken<List<Integer>>(){}.getType());
        savedMotion = gson.fromJson(nbt.getString("savedMotion"), new TypeToken<Map<Integer, Vec3d>>(){}.getType());
        savedPosition = gson.fromJson(nbt.getString("savedPosition"), new TypeToken<Map<Integer, Vec3d>>(){}.getType());
        savedRotation = gson.fromJson(nbt.getString("savedRotation"), new TypeToken<Map<Integer, Float>>(){}.getType());
    }

    @Override
    protected void writeAdditional(CompoundNBT nbt) {
        nbt.putShort("TimeLeft", (short) timeLeft);
        nbt.putString("stoppedEntities", gson.toJson(stoppedEntitiesID));
        nbt.putString("savedMotion", gson.toJson(savedMotion));
        nbt.putString("savedPosition", gson.toJson(savedPosition));
        nbt.putString("savedRotation", gson.toJson(savedRotation));
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return new SSpawnObjectPacket(this);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        if (world instanceof ServerWorld) {
            for (UUID id : stoppedEntitiesID) {
                stoppedEntities.add(((ServerWorld) world).getEntityByUuid(id));
            }
        }
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();

        for (Entity stoppedEntity : stoppedEntities) {
            stoppedEntity.setNoGravity(false);
            stoppedEntity.setMotion(savedMotion.get(stoppedEntity.getUniqueID()));
            stoppedEntity.setSilent(false);

            if (stoppedEntity instanceof MobEntity) {
                ((MobEntity) stoppedEntity).setNoAI(false);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(timeLeft > 0) {
            timeLeft--;

            for (Entity stoppedEntity : stoppedEntities) {
                stoppedEntity.setMotion(0, 0, 0);
                Vec3d pos = savedPosition.get(stoppedEntity.getUniqueID());
                stoppedEntity.setPosition(pos.x, pos.y, pos.z);
                stoppedEntity.setRotationYawHead(savedRotation.get(stoppedEntity.getUniqueID()));
            }

            //Particles don't work idk why
//            if (this.world.isRemote) {
////                for (int i = 0; i < 5; ++i) {
////                    world.addParticle(ParticleTypes.ENCHANT, getPosX(), getPosY(), getPosZ(), 0, 0, 0);
////                }
//                this.world.addParticle(ParticleTypes.INSTANT_EFFECT, this.getPosX(), this.getPosY(), this.getPosZ(), 0.0D, 0.0D, 0.0D);
//            }
        } else {
            remove();
        }
    }
}
