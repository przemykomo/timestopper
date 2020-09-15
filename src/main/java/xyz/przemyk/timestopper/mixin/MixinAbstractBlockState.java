package xyz.przemyk.timestopper.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.Property;
import net.minecraft.state.StateHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.przemyk.timestopper.TimeStopperMod;
import xyz.przemyk.timestopper.entities.active.ActiveTimeStopperEntity;

import java.util.List;

@SuppressWarnings({"unused", "deprecation"})
@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class MixinAbstractBlockState extends StateHolder<Block, BlockState> {

    protected MixinAbstractBlockState(Block p_i231879_1_, ImmutableMap<Property<?>, Comparable<?>> p_i231879_2_, MapCodec<BlockState> p_i231879_3_) {
        super(p_i231879_1_, p_i231879_2_, p_i231879_3_);
    }

    @Shadow
    public Block getBlock() {
        return this.field_235892_c_;
    }

    @Shadow
    protected abstract BlockState func_230340_p_();

    //TODO: work with capability
    public void neighborChanged(World worldIn, BlockPos posIn, Block blockIn, BlockPos fromPosIn, boolean isMoving) {
        List<ActiveTimeStopperEntity> activeTimeStoppers = worldIn.getEntitiesWithinAABB(ActiveTimeStopperEntity.class, TimeStopperMod.scan.offset(posIn));
        if (activeTimeStoppers.isEmpty()) {
            this.getBlock().neighborChanged(this.func_230340_p_(), worldIn, posIn, blockIn, fromPosIn, isMoving);
            return;
        }

        activeTimeStoppers.get(0).catchBlockUpdate(getBlock(), posIn, blockIn, fromPosIn, isMoving);
    }
}
