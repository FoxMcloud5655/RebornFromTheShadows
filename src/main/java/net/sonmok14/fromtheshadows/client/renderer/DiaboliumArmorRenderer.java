package net.sonmok14.fromtheshadows.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.sonmok14.fromtheshadows.client.models.DiaboliumArmorModel;
import net.sonmok14.fromtheshadows.items.DiaboliumArmorItem;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class DiaboliumArmorRenderer extends GeoArmorRenderer<DiaboliumArmorItem> {
	public DiaboliumArmorRenderer() {
		super(new DiaboliumArmorModel());
		headBone = "bipedHead";
		bodyBone = "bipedBody";
		rightArmBone = "bipedRightArm";
		leftArmBone = "bipedLeftArm";
		rightLegBone = "bipedLeftLeg";
		leftLegBone = "bipedRightLeg";
		rightBootBone = "armorRightBoot";
		leftBootBone = "armorLeftBoot";
	}

		// These values are what each bone name is in blockbench. So if your head bone
		// is named "bone545", make sure to do this.headBone = "bone545";
		// The default values are the ones that come with the default armor template in
		// the geckolib blockbench plugin.
	public RenderType getRenderType(DiaboliumArmorItem animatable, float partialTicks, PoseStack stack, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
