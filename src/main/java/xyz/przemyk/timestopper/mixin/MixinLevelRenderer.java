package xyz.przemyk.timestopper.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.przemyk.timestopper.TimeStopperMod;

@Mixin(LevelRenderer.class)
public abstract class MixinLevelRenderer implements ResourceManagerReloadListener, AutoCloseable {

    @Final
    @Shadow
    private EntityRenderDispatcher entityRenderDispatcher;

    @SuppressWarnings("unused")
    private void renderEntity(Entity entityIn, double camX, double camY, double camZ, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn) {
        double d0 = Mth.lerp(partialTicks, entityIn.xOld, entityIn.getX());
        double d1 = Mth.lerp(partialTicks, entityIn.yOld, entityIn.getY());
        double d2 = Mth.lerp(partialTicks, entityIn.zOld, entityIn.getZ());
        float f = Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot());
        this.entityRenderDispatcher.render(entityIn, d0 - camX, d1 - camY, d2 - camZ, f, TimeStopperMod.canUpdateEntity(entityIn) ? partialTicks : 1.0f, poseStack, bufferIn, this.entityRenderDispatcher.getPackedLightCoords(entityIn, partialTicks));
    }
}
