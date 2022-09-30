package xyz.przemyk.timestopper.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.przemyk.timestopper.TimeStopperMod;

@SuppressWarnings("deprecation")
@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class MixinBlockStateBase extends StateHolder<Block, BlockState> {
    @Shadow public abstract Block getBlock();

    @Shadow protected abstract BlockState asState();

    protected MixinBlockStateBase(Block p_61117_, ImmutableMap<Property<?>, Comparable<?>> p_61118_, MapCodec<BlockState> p_61119_) {
        super(p_61117_, p_61118_, p_61119_);
    }

    @SuppressWarnings("unused")
    public void randomTick(ServerLevel level, BlockPos blockPos, RandomSource randomSource) {
        if (TimeStopperMod.canRandomTick(blockPos, level)) {
            getBlock().randomTick(asState(), level, blockPos, randomSource);
        }
    }
}
