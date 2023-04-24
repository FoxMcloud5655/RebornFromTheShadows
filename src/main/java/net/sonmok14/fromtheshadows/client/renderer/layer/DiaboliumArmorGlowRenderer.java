package net.sonmok14.fromtheshadows.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.sonmok14.fromtheshadows.Fromtheshadows;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class DiaboliumArmorGlowRenderer extends GeoLayerRenderer {
	private static ResourceLocation LAYER;
	private static ResourceLocation MODEL;

	public DiaboliumArmorGlowRenderer(IGeoRenderer<?> entityRendererIn) {
		super(entityRendererIn);
	}

	public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, Entity entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		RenderType cameo = RenderType.eyes(DiaboliumArmorGlowRenderer.LAYER);
		matrixStackIn.pushPose();
		matrixStackIn.scale(1.0f, 1.0f, 1.0f);
		matrixStackIn.translate(0.0, 0.0, 0.0);
		getRenderer().render(getEntityModel().getModel(DiaboliumArmorGlowRenderer.MODEL), entityLivingBaseIn, partialTicks, cameo, matrixStackIn, bufferIn, bufferIn.getBuffer(cameo), packedLightIn, OverlayTexture.NO_OVERLAY, 2.0f, 1.0f, 1.0f, 1.0f);
		matrixStackIn.popPose();
	}

	static {
		LAYER = new ResourceLocation(Fromtheshadows.MODID, "textures/armor/glow_1.png");
		MODEL = new ResourceLocation(Fromtheshadows.MODID, "geo/diabolium_armor.geo.json");
	}
}
