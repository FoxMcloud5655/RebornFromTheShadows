package net.sonmok14.fromtheshadows.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.sonmok14.fromtheshadows.client.models.NehemothModel;
import net.sonmok14.fromtheshadows.client.renderer.layer.NehemothLayerRenderer;
import net.sonmok14.fromtheshadows.entity.NehemothEntity;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class NehemothRenderer extends GeoEntityRenderer<NehemothEntity> {
	public NehemothRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new NehemothModel());
		this.addLayer(new NehemothLayerRenderer(this));
		shadowRadius = 1f;
		shadowStrength = 2f;
	}

	@Override
	protected float getDeathMaxRotation(NehemothEntity entityLivingBaseIn) {
		return 0;
	}

	@Override
	public RenderType getRenderType(NehemothEntity animatable, float partialTicks, PoseStack stack, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
