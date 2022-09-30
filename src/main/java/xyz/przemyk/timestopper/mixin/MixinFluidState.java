package xyz.przemyk.timestopper.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.przemyk.timestopper.TimeStopperMod;

@Mixin(FluidState.class)
public abstract class MixinFluidState extends StateHolder<Fluid, FluidState> implements net.minecraftforge.common.extensions.IForgeFluidState {

    protected MixinFluidState(Fluid p_61117_, ImmutableMap<Property<?>, Comparable<?>> p_61118_, MapCodec<FluidState> p_61119_) {
        super(p_61117_, p_61118_, p_61119_);
    }

    @Inject(at = @At(value = "HEAD"), method = "Lnet/minecraft/world/level/material/FluidState;tick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V", cancellable = true)
    public void timestopperTick(Level level, BlockPos blockPos, CallbackInfo callbackInfo) {
        if (!TimeStopperMod.canRandomTick(blockPos, level)) {
            callbackInfo.cancel();
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "Lnet/minecraft/world/level/material/FluidState;animateTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V", cancellable = true)
    public void timestopperAnimateTick(Level level, BlockPos blockPos, RandomSource randomSource, CallbackInfo callbackInfo) {
        if (!TimeStopperMod.canRandomTick(blockPos, level)) {
            callbackInfo.cancel();
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "Lnet/minecraft/world/level/material/FluidState;randomTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V", cancellable = true)
    public void timestopperRandomTick(Level level, BlockPos blockPos, RandomSource randomSource, CallbackInfo callbackInfo) {
        if (!TimeStopperMod.canRandomTick(blockPos, level)) {
            callbackInfo.cancel();
        }
    }
}
