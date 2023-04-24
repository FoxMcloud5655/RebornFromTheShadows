package net.sonmok14.fromtheshadows.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.sonmok14.fromtheshadows.Fromtheshadows;
import net.sonmok14.fromtheshadows.entity.NehemothEntity;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class NehemothLayerRenderer extends GeoLayerRenderer {
	private static ResourceLocation LAYER;
	private static ResourceLocation LAYER_SOUL;
	private static ResourceLocation LAYER_SOUL2;
	private static ResourceLocation MODEL;

	public NehemothLayerRenderer(IGeoRenderer<?> entityRendererIn) {
		super(entityRendererIn);
	}

	@Override
	public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, Entity entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		RenderType cameo = RenderType.eyes(NehemothLayerRenderer.LAYER);
		RenderType cameo2 = RenderType.entityTranslucent(NehemothLayerRenderer.LAYER_SOUL);
		RenderType cameo3 = RenderType.eyes(NehemothLayerRenderer.LAYER_SOUL2);
		matrixStackIn.pushPose();
		matrixStackIn.scale(1.0f, 1.0f, 1.0f);
		matrixStackIn.translate(0.0, 0.0, 0.0);
		if (entityLivingBaseIn instanceof NehemothEntity && !entityLivingBaseIn.isSilent()) {
			if (((NehemothEntity) entityLivingBaseIn).getVariant() == 1) {
				getRenderer().render(getEntityModel().getModel(NehemothLayerRenderer.MODEL), entityLivingBaseIn, partialTicks, cameo3, matrixStackIn, bufferIn, bufferIn.getBuffer(cameo3), packedLightIn, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
				getRenderer().render(getEntityModel().getModel(NehemothLayerRenderer.MODEL), entityLivingBaseIn, partialTicks, cameo2, matrixStackIn, bufferIn, bufferIn.getBuffer(cameo2), packedLightIn, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 2.0f);
			}
			if (((NehemothEntity) entityLivingBaseIn).getVariant() == 0) {
				getRenderer().render(getEntityModel().getModel(NehemothLayerRenderer.MODEL), entityLivingBaseIn, partialTicks, cameo, matrixStackIn, bufferIn, bufferIn.getBuffer(cameo), packedLightIn, OverlayTexture.NO_OVERLAY, 2.0f, 1.0f, 1.0f, 2.0f);
			}
		}
		matrixStackIn.popPose();
	}

	static {
		LAYER = new ResourceLocation(Fromtheshadows.MODID, "textures/entity/nehemoth_eye.png");
		LAYER_SOUL = new ResourceLocation(Fromtheshadows.MODID, "textures/entity/nehemoth_eye_2.png");
		LAYER_SOUL2 = new ResourceLocation(Fromtheshadows.MODID, "textures/entity/nehemoth_eye_3.png");
		MODEL = new ResourceLocation(Fromtheshadows.MODID, "geo/nehemoth.geo.json");
	}
}
