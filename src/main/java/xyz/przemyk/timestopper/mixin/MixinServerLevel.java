package xyz.przemyk.timestopper.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.przemyk.timestopper.TimeStopperMod;

import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class MixinServerLevel extends Level implements WorldGenLevel { //TODO: stop scheduled ticks

    @Shadow protected abstract void tickPassenger(Entity p_8663_, Entity p_8664_);

    protected MixinServerLevel(WritableLevelData p_270739_, ResourceKey<Level> p_270683_, RegistryAccess p_270200_, Holder<DimensionType> p_270240_, Supplier<ProfilerFiller> p_270692_, boolean p_270904_, boolean p_270470_, long p_270248_, int p_270466_) {
        super(p_270739_, p_270683_, p_270200_, p_270240_, p_270692_, p_270904_, p_270470_, p_270248_, p_270466_);
    }

    @SuppressWarnings({"unused"})
    public void tickNonPassenger(Entity p_8648_) {
        p_8648_.setOldPosAndRot();
        ProfilerFiller profilerfiller = this.getProfiler();
        ++p_8648_.tickCount;
        this.getProfiler().push(() -> ForgeRegistries.ENTITY_TYPES.getKey(p_8648_.getType()).toString());
        profilerfiller.incrementCounter("tickNonPassenger");
        TimeStopperMod.updateEntity(p_8648_);
        this.getProfiler().pop();

        for(Entity entity : p_8648_.getPassengers()) {
            this.tickPassenger(p_8648_, entity);
        }

    }
}
