package xyz.przemyk.timestopper.mixin;

import com.mojang.datafixers.util.Either;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.przemyk.timestopper.TimeStopperMod;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Mixin(ServerChunkProvider.class)
public abstract class MixinServerChunkProvider extends AbstractChunkProvider {

    protected MixinServerChunkProvider(ServerWorld world) {
        this.world = world;
    }

    @Shadow
    public final ServerWorld world;

    @SuppressWarnings("unused")
    @Shadow
    private boolean isChunkLoaded(long pos, Function<ChunkHolder, CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>>> p_222872_3_) {
        return false;
    }

    public boolean canTick(BlockPos pos) {
        long i = ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
        return this.isChunkLoaded(i, ChunkHolder::getTickingFuture) && TimeStopperMod.canUpdate(new Vector3d(pos.getX(), pos.getY(), pos.getZ()), world);
    }
}
