package xyz.przemyk.timestopper.entities.active;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import xyz.przemyk.timestopper.entities.ModEntities;

import static xyz.przemyk.timestopper.TimeStopperMod.TIME_FIELD_SIZE;

public class ActiveTimeStopperEntity extends Entity {

    public static final int TICKS_OF_STOPPED_TIME = 60;

    public static final AxisAlignedBB scan = new AxisAlignedBB(-TIME_FIELD_SIZE / 2, -TIME_FIELD_SIZE / 2, -TIME_FIELD_SIZE / 2, TIME_FIELD_SIZE / 2, TIME_FIELD_SIZE / 2, TIME_FIELD_SIZE / 2);
//    public static final AxisAlignedBB scan = new AxisAlignedBB(-0.25, -0.25, -0.25, 0.25, 0.25, 0.25);

    private static final DataParameter<Integer> TICKS_LEFT = EntityDataManager.createKey(ActiveTimeStopperEntity.class, DataSerializers.VARINT);

    public ActiveTimeStopperEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    public ActiveTimeStopperEntity(World worldIn, Vector3d pos) {
        this(ModEntities.ACTIVE_TIME_STOPPER, worldIn);
        setPosition(pos.x, pos.y, pos.z);
    }

    @Override
    protected void registerData() {
        dataManager.register(TICKS_LEFT, TICKS_OF_STOPPED_TIME);
    }

    @Override
    protected void readAdditional(CompoundNBT nbt) {
        dataManager.set(TICKS_LEFT, nbt.getInt("ticksLeft"));
    }

    @Override
    protected void writeAdditional(CompoundNBT nbt) {
        nbt.putInt("ticksLeft", dataManager.get(TICKS_LEFT));
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void tick() {
        super.tick();
        int ticksLeft = dataManager.get(TICKS_LEFT);
        if (ticksLeft > 0) {
            dataManager.set(TICKS_LEFT, ticksLeft - 1);
            for (int i = 0; i < 20; ++i) {
                world.addParticle(ParticleTypes.ENTITY_EFFECT, getPosX() + (rand.nextDouble() - 0.5) * TIME_FIELD_SIZE, getPosY() + (rand.nextDouble() - 0.5) * TIME_FIELD_SIZE, getPosZ() + (rand.nextDouble() - 0.5) * TIME_FIELD_SIZE, 0.0, 0.0, 0.0);
            }
        } else {
            remove();
        }
    }

    @Override
    public boolean canUpdate() {
        return true;
    }
}
