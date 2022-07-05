//package xyz.przemyk.timestopper.mixin;
//
//import com.google.common.collect.ImmutableMap;
//import com.mojang.serialization.MapCodec;
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.state.BlockBehaviour;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.block.state.StateHolder;
//import net.minecraft.world.level.block.state.properties.Property;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//
//@SuppressWarnings({"unused", "deprecation"})
//@Mixin(BlockBehaviour.BlockStateBase.class)
//public abstract class MixinAbstractBlockState extends StateHolder<Block, BlockState> {
//
//    protected MixinAbstractBlockState(Block p_61117_, ImmutableMap<Property<?>, Comparable<?>> p_61118_, MapCodec<BlockState> p_61119_) {
//        super(p_61117_, p_61118_, p_61119_);
//    }
//
//    @Shadow protected abstract BlockState asState();
//
//    @Shadow public abstract Block getBlock();
//
//    //TODO: work with capability
//    public void neighborChanged(Level worldIn, BlockPos posIn, Block blockIn, BlockPos fromPosIn, boolean isMoving) {
//        this.getBlock().neighborChanged(this.asState(), worldIn, posIn, blockIn, fromPosIn, isMoving);
//    }
//}
