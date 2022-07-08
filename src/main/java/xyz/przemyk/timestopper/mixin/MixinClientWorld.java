package xyz.przemyk.timestopper.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.przemyk.timestopper.TimeStopperMod;

import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class MixinClientWorld extends Level {

    @Shadow protected abstract void tickPassenger(Entity p_104642_, Entity p_104643_);

    protected MixinClientWorld(WritableLevelData p_220352_, ResourceKey<Level> p_220353_, Holder<DimensionType> p_220354_, Supplier<ProfilerFiller> p_220355_, boolean p_220356_, boolean p_220357_, long p_220358_, int p_220359_) {
        super(p_220352_, p_220353_, p_220354_, p_220355_, p_220356_, p_220357_, p_220358_, p_220359_);
    }

    @SuppressWarnings("unused")
    public void tickNonPassenger(Entity p_104640_) {
        p_104640_.setOldPosAndRot();
        ++p_104640_.tickCount;
        this.getProfiler().push(() -> {
            return Registry.ENTITY_TYPE.getKey(p_104640_.getType()).toString();
        });
        if (p_104640_.canUpdate())
            TimeStopperMod.updateEntity(p_104640_);
        this.getProfiler().pop();

        for(Entity entity : p_104640_.getPassengers()) {
            this.tickPassenger(p_104640_, entity);
        }

    }
}
