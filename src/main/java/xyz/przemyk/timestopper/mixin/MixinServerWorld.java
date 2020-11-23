package xyz.przemyk.timestopper.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.DimensionType;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.ISpawnWorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.przemyk.timestopper.TimeStopperMod;

import java.util.function.Supplier;

@SuppressWarnings("unused")
@Mixin(ServerWorld.class)
public abstract class MixinServerWorld extends World implements ISeedReader, net.minecraftforge.common.extensions.IForgeWorldServer {

    protected MixinServerWorld(ISpawnWorldInfo p_i241925_1_, RegistryKey<World> p_i241925_2_, DimensionType p_i241925_3_, Supplier<IProfiler> p_i241925_4_, boolean p_i241925_5_, boolean p_i241925_6_, long p_i241925_7_) {
        super(p_i241925_1_, p_i241925_2_, p_i241925_3_, p_i241925_4_, p_i241925_5_, p_i241925_6_, p_i241925_7_);
    }

    @Shadow
    public ServerChunkProvider getChunkProvider() {
        return null;
    }

    @Shadow
    public void chunkCheck(Entity entityIn) {}

    @Shadow
    public void tickPassenger(Entity ridingEntity, Entity passengerEntity) {}

    public void updateEntity(Entity entityIn) {
        if (!(entityIn instanceof PlayerEntity) && !this.getChunkProvider().isChunkLoaded(entityIn)) {
            this.chunkCheck(entityIn);
        } else {
            entityIn.forceSetPosition(entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ());
            entityIn.prevRotationYaw = entityIn.rotationYaw;
            entityIn.prevRotationPitch = entityIn.rotationPitch;
            if (entityIn.addedToChunk) {
                ++entityIn.ticksExisted;
                IProfiler iprofiler = this.getProfiler();
                iprofiler.startSection(() -> entityIn.getType().getRegistryName() == null ? entityIn.getType().toString() : entityIn.getType().getRegistryName().toString());
                iprofiler.func_230035_c_("tickNonPassenger");
                TimeStopperMod.updateEntity(entityIn, true);
                iprofiler.endSection();
            }

            this.chunkCheck(entityIn);
            if (entityIn.addedToChunk) {
                for(Entity entity : entityIn.getPassengers()) {
                    this.tickPassenger(entityIn, entity);
                }
            }

        }
    }
}
