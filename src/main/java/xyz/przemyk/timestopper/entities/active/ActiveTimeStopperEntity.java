package xyz.przemyk.timestopper.entities.active;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.przemyk.timestopper.entities.ModEntities;

import java.util.LinkedList;
import java.util.Queue;

import static xyz.przemyk.timestopper.TimeStopperMod.TIME_FIELD_SIZE;

public class ActiveTimeStopperEntity extends Entity {

    public static final int TICKS_OF_STOPPED_TIME = 60;

    public static final AxisAlignedBB scan = new AxisAlignedBB(-TIME_FIELD_SIZE / 2, -TIME_FIELD_SIZE / 2, -TIME_FIELD_SIZE / 2, TIME_FIELD_SIZE / 2, TIME_FIELD_SIZE / 2, TIME_FIELD_SIZE / 2);

    private static final DataParameter<Integer> TICKS_LEFT = EntityDataManager.createKey(ActiveTimeStopperEntity.class, DataSerializers.VARINT);

    public ActiveTimeStopperEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    public ActiveTimeStopperEntity(World worldIn, Vector3d pos) {
        this(ModEntities.ACTIVE_TIME_STOPPER, worldIn);
        setPosition(pos.x, pos.y, pos.z);
    }

    private static class CachedBlockUpdate {
        public final Block frozenBlock;
        public final BlockPos pos;
        public final Block fromBlock;
        public final BlockPos fromPos;
        public final boolean isMoving;

        private CachedBlockUpdate(Block frozenBlock, BlockPos pos, Block fromBlock, BlockPos fromPos, boolean isMoving) {
            this.frozenBlock = frozenBlock;
            this.pos = pos;
            this.fromBlock = fromBlock;
            this.fromPos = fromPos;
            this.isMoving = isMoving;
        }
    }

    private final Queue<CachedBlockUpdate> cachedBlockUpdates = new LinkedList<>();

    @Override
    protected void registerData() {
        dataManager.register(TICKS_LEFT, TICKS_OF_STOPPED_TIME);
    }

    @Override
    protected void writeAdditional(CompoundNBT nbt) {
        nbt.putInt("ticksLeft", dataManager.get(TICKS_LEFT));

        CompoundNBT blockUpdatesCompound = new CompoundNBT();

        for (int i = 0; !cachedBlockUpdates.isEmpty(); ++i) {
            CompoundNBT blockUpdate = new CompoundNBT();
            CachedBlockUpdate cachedBlockUpdate = cachedBlockUpdates.poll();
            blockUpdate.putString("frozenBlock", cachedBlockUpdate.frozenBlock.getRegistryName().toString());
            blockUpdate.putInt("x", cachedBlockUpdate.pos.getX());
            blockUpdate.putInt("y", cachedBlockUpdate.pos.getY());
            blockUpdate.putInt("z", cachedBlockUpdate.pos.getZ());
            blockUpdate.putString("fromBlock", cachedBlockUpdate.fromBlock.getRegistryName().toString());
            blockUpdate.putInt("from_x", cachedBlockUpdate.fromPos.getX());
            blockUpdate.putInt("from_y", cachedBlockUpdate.fromPos.getY());
            blockUpdate.putInt("from_z", cachedBlockUpdate.fromPos.getZ());
            blockUpdate.putBoolean("isMoving", cachedBlockUpdate.isMoving);

            blockUpdatesCompound.put(Integer.toString(i), blockUpdate);
        }

        nbt.put("blockUpdates", blockUpdatesCompound);
    }

    @Override
    protected void readAdditional(CompoundNBT nbt) {
        dataManager.set(TICKS_LEFT, nbt.getInt("ticksLeft"));

        CompoundNBT blockUpdatesCompound = nbt.getCompound("blockUpdates"); //TODO: FIX UNSAVED BLOCK UPDATES: this compound is empty, idk why
        cachedBlockUpdates.clear();
        int i = 0;
        while (blockUpdatesCompound.contains(Integer.toString(i))) {
            CompoundNBT blockUpdate = blockUpdatesCompound.getCompound(Integer.toString(i));

            cachedBlockUpdates.add(new CachedBlockUpdate(
                    ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockUpdate.getString("frozenBlock"))),
                    new BlockPos(blockUpdate.getInt("x"), blockUpdate.getInt("y"), blockUpdate.getInt("z")),
                    ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockUpdate.getString("fromBlock"))),
                    new BlockPos(blockUpdate.getInt("from_x"), blockUpdate.getInt("from_y"), blockUpdate.getInt("from_z")),
                    blockUpdate.getBoolean("isMoving")));

            ++i;
        }
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @SuppressWarnings("deprecation")
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
            while (!cachedBlockUpdates.isEmpty()) {
                CachedBlockUpdate cachedBlockUpdate = cachedBlockUpdates.poll();
                cachedBlockUpdate.frozenBlock.neighborChanged(world.getBlockState(cachedBlockUpdate.pos), world, cachedBlockUpdate.pos, cachedBlockUpdate.fromBlock, cachedBlockUpdate.fromPos, cachedBlockUpdate.isMoving);
            }
            remove();
        }
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    public void catchBlockUpdate(Block frozenBlock, BlockPos posIn, Block blockIn, BlockPos fromPosIn, boolean isMoving) {
        cachedBlockUpdates.add(new CachedBlockUpdate(frozenBlock, posIn, blockIn, fromPosIn, isMoving));
    }
}
