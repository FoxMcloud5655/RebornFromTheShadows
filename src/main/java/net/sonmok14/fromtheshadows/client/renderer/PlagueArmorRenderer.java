package net.sonmok14.fromtheshadows.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.sonmok14.fromtheshadows.client.models.PlagueArmorModel;
import net.sonmok14.fromtheshadows.items.PlagueArmorItem;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class PlagueArmorRenderer extends GeoArmorRenderer<PlagueArmorItem> {
	public PlagueArmorRenderer() {
		super((AnimatedGeoModel) new PlagueArmorModel());
		headBone = "bipedHead";
	}

	public RenderType getRenderType(PlagueArmorItem animatable, float partialTicks, PoseStack stack, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
