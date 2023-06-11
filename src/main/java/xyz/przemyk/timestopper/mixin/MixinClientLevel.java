package xyz.przemyk.timestopper.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.przemyk.timestopper.TimeStopperMod;

import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class MixinClientLevel extends Level {

    @Shadow protected abstract void tickPassenger(Entity p_104642_, Entity p_104643_);
    
    protected MixinClientLevel(WritableLevelData p_270739_, ResourceKey<Level> p_270683_, RegistryAccess p_270200_, Holder<DimensionType> p_270240_, Supplier<ProfilerFiller> p_270692_, boolean p_270904_, boolean p_270470_, long p_270248_, int p_270466_) {
        super(p_270739_, p_270683_, p_270200_, p_270240_, p_270692_, p_270904_, p_270470_, p_270248_, p_270466_);
    }

    @SuppressWarnings("unused")
    public void tickNonPassenger(Entity p_104640_) {
        p_104640_.setOldPosAndRot();
        ++p_104640_.tickCount;
        this.getProfiler().push(() -> ForgeRegistries.ENTITY_TYPES.getKey(p_104640_.getType()).toString());

        if (p_104640_.canUpdate()) {
            TimeStopperMod.updateEntity(p_104640_);
        }

        this.getProfiler().pop();

        for(Entity entity : p_104640_.getPassengers()) {
            this.tickPassenger(p_104640_, entity);
        }

    }
}
