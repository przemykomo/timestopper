package xyz.przemyk.timestopper.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class ThrownTimeStopperEntity extends Entity {

    public static final AxisAlignedBB scan = new AxisAlignedBB(-6, -6, -6, 6, 6, 6);

    private static final DataParameter<Integer> TICKS_LEFT = EntityDataManager.createKey(ThrownTimeStopperEntity.class, DataSerializers.VARINT);

    public ThrownTimeStopperEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
        dataManager.set(TICKS_LEFT, 60);
    }

    public ThrownTimeStopperEntity(World worldIn, Vector3d pos) {
        this(ModEntities.THROWN_TIME_STOPPER, worldIn);
        setPosition(pos.x, pos.y, pos.z);
        dataManager.set(TICKS_LEFT, 60);
    }

    @Override
    protected void registerData() {
        dataManager.register(TICKS_LEFT, 60);
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
        if(ticksLeft > 0) {
            dataManager.set(TICKS_LEFT, ticksLeft - 1);
        } else {
            remove();
        }
    }

    @Override
    public boolean canUpdate() {
        return true;
    }
}
