package xyz.przemyk.timestopper.entities.active;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import xyz.przemyk.timestopper.TimeStopperMod;

import net.minecraft.client.renderer.texture.OverlayTexture;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;

import static xyz.przemyk.timestopper.TimeStopperMod.TIME_FIELD_SIZE;
import static xyz.przemyk.timestopper.entities.active.ActiveTimeStopperEntity.TICKS_OF_STOPPED_TIME;

public class ActiveTimeStopperEntityRenderer extends EntityRenderer<ActiveTimeStopperEntity> {

    private final ResourceLocation fieldTexture = new ResourceLocation(TimeStopperMod.MODID, "textures/entity/field.png");

    public ActiveTimeStopperEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    private final FieldModel fieldModel = new FieldModel();
    private final RenderType renderType = MyRenderState.transparentEntity(fieldTexture, false);

    @Override
    public void render(ActiveTimeStopperEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.push();
        matrixStackIn.scale(TIME_FIELD_SIZE, TIME_FIELD_SIZE, TIME_FIELD_SIZE);
        fieldModel.render(matrixStackIn, bufferIn.getBuffer(renderType), 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F,  0.6F - ((float) entityIn.ticksExisted + partialTicks) / TICKS_OF_STOPPED_TIME / 2);
        matrixStackIn.pop();

        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getEntityTexture(ActiveTimeStopperEntity entity) {
        return fieldTexture;
    }
}
