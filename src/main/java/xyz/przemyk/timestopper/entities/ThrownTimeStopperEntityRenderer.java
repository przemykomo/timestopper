package xyz.przemyk.timestopper.entities;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class ThrownTimeStopperEntityRenderer extends EntityRenderer<ThrownTimeStopperEntity> {

    private final FieldModel fieldModel = new FieldModel();
    private final ResourceLocation fieldTexture = new ResourceLocation("minecraft", "textures/block/white_concrete.png");

    public ThrownTimeStopperEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public void render(ThrownTimeStopperEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
//        fieldModel.render(matrixStackIn, bufferIn.getBuffer(fieldModel.getRenderType(getEntityTexture(entityIn))), packedLightIn, OverlayTexture.NO_OVERLAY, 0.0F, 1.0F, 1.0F, 0.3F);
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getEntityTexture(ThrownTimeStopperEntity entity) {
        return fieldTexture;
    }
}
