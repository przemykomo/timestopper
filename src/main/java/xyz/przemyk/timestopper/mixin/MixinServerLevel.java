package xyz.przemyk.timestopper.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.przemyk.timestopper.TimeStopperMod;

import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class MixinServerLevel extends Level implements WorldGenLevel { //TODO: stop scheduled ticks

    @Shadow protected abstract void tickPassenger(Entity p_8663_, Entity p_8664_);

    protected MixinServerLevel(WritableLevelData p_220352_, ResourceKey<Level> p_220353_, Holder<DimensionType> p_220354_, Supplier<ProfilerFiller> p_220355_, boolean p_220356_, boolean p_220357_, long p_220358_, int p_220359_) {
        super(p_220352_, p_220353_, p_220354_, p_220355_, p_220356_, p_220357_, p_220358_, p_220359_);
    }

    @SuppressWarnings({"deprecation", "unused"})
    public void tickNonPassenger(Entity p_8648_) {
        p_8648_.setOldPosAndRot();
        ProfilerFiller profilerfiller = this.getProfiler();
        ++p_8648_.tickCount;
        this.getProfiler().push(() -> Registry.ENTITY_TYPE.getKey(p_8648_.getType()).toString());
        profilerfiller.incrementCounter("tickNonPassenger");
        TimeStopperMod.updateEntity(p_8648_);
        this.getProfiler().pop();

        for(Entity entity : p_8648_.getPassengers()) {
            this.tickPassenger(p_8648_, entity);
        }

    }
}
