package xyz.przemyk.timestopper.mixin;

import net.minecraft.client.multiplayer.ClientChunkProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.chunk.AbstractChunkProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.przemyk.timestopper.TimeStopperMod;

@Mixin(ClientChunkProvider.class)
public abstract class MixinClientChunkProvider extends AbstractChunkProvider {

    protected MixinClientChunkProvider(ClientWorld world) {
        this.world = world;
    }

    @Shadow
    private final ClientWorld world;

    public boolean canTick(BlockPos pos) {
        return this.chunkExists(pos.getX() >> 4, pos.getZ() >> 4) && TimeStopperMod.canUpdate(new Vector3d(pos.getX(), pos.getY(), pos.getZ()), world);
    }
}
