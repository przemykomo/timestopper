package xyz.przemyk.timestopper.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings({"deprecation", "unused"})
@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer implements IResourceManagerReloadListener, AutoCloseable {

    public MixinWorldRenderer(Minecraft mcIn) {
        this.renderManager = mcIn.getRenderManager();
    }

    @Shadow
    private final EntityRendererManager renderManager;

    private void renderEntity(Entity entityIn, double camX, double camY, double camZ, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn) {
        double d0 = MathHelper.lerp(partialTicks, entityIn.lastTickPosX, entityIn.getPosX());
        double d1 = MathHelper.lerp(partialTicks, entityIn.lastTickPosY, entityIn.getPosY());
        double d2 = MathHelper.lerp(partialTicks, entityIn.lastTickPosZ, entityIn.getPosZ());
        float f = MathHelper.lerp(partialTicks, entityIn.prevRotationYaw, entityIn.rotationYaw);
        this.renderManager.renderEntityStatic(entityIn, d0 - camX, d1 - camY, d2 - camZ, f, entityIn.canUpdate() ? partialTicks : 1.0f, matrixStackIn, bufferIn, this.renderManager.getPackedLight(entityIn, partialTicks));
    }
}
