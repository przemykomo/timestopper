package xyz.przemyk.timestopper.entities.active;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import xyz.przemyk.timestopper.TimeStopperMod;

public class ActiveTimeStopperEntityRenderer extends EntityRenderer<ActiveTimeStopperEntity> {

    private final ResourceLocation fieldTexture = new ResourceLocation(TimeStopperMod.MODID, "textures/entity/field.png");

    public ActiveTimeStopperEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

//    private final FieldModel fieldModel = new FieldModel();
//    private final RenderType renderType = RenderType.func_230168_b_(fieldTexture, false);

    @Override
    public void render(ActiveTimeStopperEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
//        matrixStackIn.push();
//        matrixStackIn.scale(TIME_FIELD_SIZE, TIME_FIELD_SIZE, TIME_FIELD_SIZE);
//        fieldModel.render(matrixStackIn, bufferIn.getBuffer(renderType), 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F,  0.6F - ((float) entityIn.ticksExisted + partialTicks) / TICKS_OF_STOPPED_TIME / 2);
//        matrixStackIn.pop();

        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getEntityTexture(ActiveTimeStopperEntity entity) {
        return fieldTexture;
    }
}
