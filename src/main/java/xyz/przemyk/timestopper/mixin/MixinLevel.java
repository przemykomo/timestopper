package xyz.przemyk.timestopper.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.przemyk.timestopper.TimeStopperMod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings({"unused", "UnstableApiUsage"})
@Mixin(Level.class)
public abstract class MixinLevel extends net.minecraftforge.common.capabilities.CapabilityProvider<Level> implements LevelAccessor, AutoCloseable, net.minecraftforge.common.extensions.IForgeLevel {
    @Shadow public abstract ProfilerFiller getProfiler();

    @Shadow @Final private ArrayList<BlockEntity> pendingFreshBlockEntities;

    @Shadow @Final private ArrayList<BlockEntity> freshBlockEntities;

    @SuppressWarnings("FieldCanBeLocal")
    @Shadow private boolean tickingBlockEntities;

    @Shadow @Final private List<TickingBlockEntity> pendingBlockEntityTickers;

    @Shadow @Final protected List<TickingBlockEntity> blockEntityTickers;

    @Shadow public abstract boolean shouldTickBlocksAt(BlockPos p_220394_);

    protected MixinLevel(Class<Level> baseClass) {
        super(baseClass);
    }

    protected void tickBlockEntities() {
        ProfilerFiller profilerfiller = this.getProfiler();
        profilerfiller.push("blockEntities");
        if (!this.pendingFreshBlockEntities.isEmpty()) {
            this.freshBlockEntities.addAll(this.pendingFreshBlockEntities);
            this.pendingFreshBlockEntities.clear();
        }
        this.tickingBlockEntities = true;
        if (!this.freshBlockEntities.isEmpty()) {
            this.freshBlockEntities.forEach(BlockEntity::onLoad);
            this.freshBlockEntities.clear();
        }
        if (!this.pendingBlockEntityTickers.isEmpty()) {
            this.blockEntityTickers.addAll(this.pendingBlockEntityTickers);
            this.pendingBlockEntityTickers.clear();
        }

        Iterator<TickingBlockEntity> iterator = this.blockEntityTickers.iterator();

        while(iterator.hasNext()) {
            TickingBlockEntity tickingblockentity = iterator.next();
            if (tickingblockentity.isRemoved()) {
                iterator.remove();
            } else if (this.shouldTickBlocksAt(tickingblockentity.getPos())) {
                TimeStopperMod.updateBlockEntity(tickingblockentity, (Level) (Object) this);
            }
        }

        this.tickingBlockEntities = false;
        profilerfiller.pop();
    }
}
