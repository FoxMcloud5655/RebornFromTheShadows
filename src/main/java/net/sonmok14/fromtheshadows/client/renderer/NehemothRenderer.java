package net.sonmok14.fromtheshadows.client.renderer;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.sonmok14.fromtheshadows.client.models.NehemothModel;
import net.sonmok14.fromtheshadows.client.renderer.layer.NehemothLayerRenderer;
import net.sonmok14.fromtheshadows.entity.NehemothEntity;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class NehemothRenderer extends GeoEntityRenderer<NehemothEntity> {
	public NehemothRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, (AnimatedGeoModel) new NehemothModel());
		addLayer((GeoLayerRenderer) new NehemothLayerRenderer((IGeoRenderer<?>) this));
		shadowRadius = 1.5f;
		shadowStrength = 1.0f;
	}

	@Override
	protected float getDeathMaxRotation(NehemothEntity entityLivingBaseIn) {
		return 0.0f;
	}

	@Override
	public RenderType getRenderType(NehemothEntity animatable, float partialTicks, PoseStack stack, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
		stack.scale(1.2f, 1.2f, 1.2f);
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	public void render(GeoModel model, NehemothEntity animatable, float partialTicks, RenderType type, PoseStack matrixStackIn, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	public void renderEarly(NehemothEntity animatable, PoseStack stackIn, float ticks, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
		super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);
	}
}
