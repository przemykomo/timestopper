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

@SuppressWarnings({"unused", "deprecation"})
@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class MixinAbstractBlockState extends StateHolder<Block, BlockState> {

    protected MixinAbstractBlockState(Block block, ImmutableMap<Property<?>, Comparable<?>> propertyValueMap, MapCodec<BlockState> stateCodec) {
        super(block, propertyValueMap, stateCodec);
    }

    @Shadow protected abstract BlockState getSelf();

    @Shadow public abstract Block getBlock();

    //TODO: work with capability
    public void neighborChanged(World worldIn, BlockPos posIn, Block blockIn, BlockPos fromPosIn, boolean isMoving) {
        this.getBlock().neighborChanged(this.getSelf(), worldIn, posIn, blockIn, fromPosIn, isMoving);
    }
}
