package xyz.przemyk.timestopper.entities.active;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import static net.minecraft.client.renderer.RenderType.makeType;

public abstract class MyRenderState extends RenderState {

    public MyRenderState(String nameIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, setupTaskIn, clearTaskIn);
    }

    public static RenderType transparentEntity(ResourceLocation texture, boolean outlineIn) {
        RenderType.State renderTypeState = RenderType.State.getBuilder().texture(new RenderState.TextureState(texture, false, false)).transparency(TRANSLUCENT_TRANSPARENCY).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).alpha(DEFAULT_ALPHA).cull(CULL_DISABLED).lightmap(LIGHTMAP_ENABLED).overlay(OVERLAY_ENABLED).depthTest(DepthTestState.DEPTH_ALWAYS).build(outlineIn);
        return makeType("entity_translucent", DefaultVertexFormats.ENTITY, 7, 256, true, true, renderTypeState);
    }
}
