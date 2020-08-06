package xyz.przemyk.timestopper.entities.active;
// Made with Blockbench 3.5.4
// Exported for Minecraft version 1.15
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class FieldModel extends EntityModel<ActiveTimeStopperEntity> {
	private final ModelRenderer bb_main;

	public FieldModel() {
		textureWidth = 64;
		textureHeight = 64;

		bb_main = new ModelRenderer(this);
		bb_main.addBox(-8.0F, 0.0F, -8.0F, 16.0F, 16.0F, 16.0F);
	}

	@Override
	public void setRotationAngles(ActiveTimeStopperEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		bb_main.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}