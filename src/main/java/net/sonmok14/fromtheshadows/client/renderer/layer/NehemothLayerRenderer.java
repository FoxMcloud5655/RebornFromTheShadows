package net.sonmok14.fromtheshadows.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.sonmok14.fromtheshadows.Fromtheshadows;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class NehemothLayerRenderer extends GeoLayerRenderer {
    // A resource location for the texture of the layer. This will be applied onto pre-existing cubes on the model
    private static final ResourceLocation LAYER = new ResourceLocation(Fromtheshadows.MODID, "textures/entity/nehemoth_eye.png");
    // A resource location for the model of the entity. This model is put on top of the normal one, which is then given the texture
    private static final ResourceLocation MODEL = new ResourceLocation(Fromtheshadows.MODID, "geo/nehemoth.geo.json");

    @SuppressWarnings("unchecked")
    public NehemothLayerRenderer(IGeoRenderer<?> entityRendererIn) {
        super(entityRendererIn);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, Entity entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        RenderType cameo =  RenderType.eyes(LAYER);
        matrixStackIn.pushPose();
        //Move or scale the model as you see fit
        matrixStackIn.scale(1.0f, 1.0f, 1.0f);
        matrixStackIn.translate(0.0d, 0.0d, 0.0d);
        this.getRenderer().render(this.getEntityModel().getModel(MODEL), entityLivingBaseIn, partialTicks, cameo, matrixStackIn, bufferIn,
                bufferIn.getBuffer(cameo), packedLightIn, OverlayTexture.NO_OVERLAY, 2f, 1f, 1f, 1f);
        matrixStackIn.popPose();
    }
}
