package xyz.przemyk.timestopper.mixin;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.storage.ISpawnWorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.przemyk.timestopper.TimeStopperMod;

import java.util.function.Supplier;

@SuppressWarnings({"deprecation", "unused"})
@Mixin(ClientWorld.class)
public abstract class MixinClientWorld extends World {

    protected MixinClientWorld(ISpawnWorldInfo p_i241925_1_, RegistryKey<World> p_i241925_2_, DimensionType p_i241925_3_, Supplier<IProfiler> p_i241925_4_, boolean p_i241925_5_, boolean p_i241925_6_, long p_i241925_7_) {
        super(p_i241925_1_, p_i241925_2_, p_i241925_3_, p_i241925_4_, p_i241925_5_, p_i241925_6_, p_i241925_7_);
    }

    @Shadow
    private void checkChunk(Entity entityIn) {}

    @Shadow
    public void updateEntityRidden(Entity p_217420_1_, Entity p_217420_2_) {}

    public void updateEntity(Entity entityIn) {
        if (!(entityIn instanceof PlayerEntity) && !this.getChunkProvider().isChunkLoaded(entityIn)) {
            this.checkChunk(entityIn);
        } else {
            entityIn.forceSetPosition(entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ());
            entityIn.prevRotationYaw = entityIn.rotationYaw;
            entityIn.prevRotationPitch = entityIn.rotationPitch;
            if (entityIn.addedToChunk || entityIn.isSpectator()) {
                ++entityIn.ticksExisted;
                this.getProfiler().startSection(() -> Registry.ENTITY_TYPE.getKey(entityIn.getType()).toString());
                TimeStopperMod.updateEntity(entityIn, false);
                this.getProfiler().endSection();
            }

            this.checkChunk(entityIn);
            if (entityIn.addedToChunk) {
                for(Entity entity : entityIn.getPassengers()) {
                    this.updateEntityRidden(entityIn, entity);
                }
            }

        }
    }
}
