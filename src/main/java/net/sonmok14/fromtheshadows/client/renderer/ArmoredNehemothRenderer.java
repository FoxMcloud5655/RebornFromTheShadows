package net.sonmok14.fromtheshadows.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.sonmok14.fromtheshadows.client.models.ArmoredNehemothModel;
import net.sonmok14.fromtheshadows.client.renderer.layer.NehemothArmorRenderer;
import net.sonmok14.fromtheshadows.entity.ArmoredNehemothEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class ArmoredNehemothRenderer extends GeoEntityRenderer<ArmoredNehemothEntity> {
	public ArmoredNehemothRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, (AnimatedGeoModel) new ArmoredNehemothModel());
		addLayer((GeoLayerRenderer) new NehemothArmorRenderer((IGeoRenderer<?>) this));
		shadowRadius = 1.0f;
		shadowStrength = 2.0f;
	}

	protected float getDeathMaxRotation(ArmoredNehemothEntity entityLivingBaseIn) {
		return 0.0f;
	}

	protected int getBlockLightLevel(ArmoredNehemothEntity p_113910_, BlockPos p_113911_) {
		if (p_113910_.attackID == 2 && p_113910_.attacktick <= 12) {
			return 15;
		}
		return 0;
	}

	public void renderEarly(ArmoredNehemothEntity animatable, PoseStack stackIn, float ticks, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
		super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);
		stackIn.scale(1.0f, 1.0f, 1.0f);
	}
}
