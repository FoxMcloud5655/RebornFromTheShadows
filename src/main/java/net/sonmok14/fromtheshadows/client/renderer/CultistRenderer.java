package net.sonmok14.fromtheshadows.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.sonmok14.fromtheshadows.client.models.CultistModel;
import net.sonmok14.fromtheshadows.client.renderer.layer.CultistLayerRenderer;
import net.sonmok14.fromtheshadows.entity.CultistEntity;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class CultistRenderer extends GeoEntityRenderer<CultistEntity> {
	private ItemStack itemStack;
	private ResourceLocation whTexture;
	private MultiBufferSource rtb;

	public CultistRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, (AnimatedGeoModel) new CultistModel());
		addLayer((GeoLayerRenderer) new CultistLayerRenderer((IGeoRenderer<?>) this));
		shadowRadius = 0.5f;
		shadowStrength = 1.0f;
	}

	public RenderType getRenderType(CultistEntity animatable, float partialTicks, PoseStack stack, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	public void renderEarly(CultistEntity animatable, PoseStack stackIn, float ticks, MultiBufferSource rtb, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
		itemStack = animatable.getItemBySlot(EquipmentSlot.MAINHAND);
		this.rtb = rtb;
		whTexture = getTextureLocation(animatable);
		super.renderEarly(animatable, stackIn, ticks, rtb, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);
	}

	public void renderRecursively(GeoBone bone, PoseStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (bone.getName().equals("bone")) {
			stack.pushPose();
			stack.mulPose(Vector3f.XP.rotationDegrees(0.0f));
			stack.mulPose(Vector3f.YP.rotationDegrees(-5.0f));
			stack.mulPose(Vector3f.ZP.rotationDegrees(0.0f));
			stack.translate(0.0, 1.2, -0.35);
			stack.scale(1.0f, 1.0f, 1.0f);
			Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, packedLightIn, packedOverlayIn, stack, rtb, 0);
			stack.popPose();
			bufferIn = rtb.getBuffer(RenderType.entityTranslucent(whTexture));
		}
		super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
}
