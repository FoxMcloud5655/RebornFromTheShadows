package net.sonmok14.fromtheshadows.client.models;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.sonmok14.fromtheshadows.Fromtheshadows;
import net.sonmok14.fromtheshadows.entity.CultistEntity;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class CultistModel extends AnimatedGeoModel<CultistEntity> {
	public ResourceLocation getAnimationFileLocation(CultistEntity entity) {
		return new ResourceLocation(Fromtheshadows.MODID, "animations/cultist.animation.json");
	}

	public ResourceLocation getModelLocation(CultistEntity entity) {
		return new ResourceLocation(Fromtheshadows.MODID, "geo/cultist.geo.json");
	}

	public ResourceLocation getTextureLocation(CultistEntity entity) {
		BlockPos blockpos = new BlockPos(entity.getX(), entity.getEyeY(), entity.getZ());
		return new ResourceLocation(Fromtheshadows.MODID, "textures/entity/cultist.png");
	}

	public void setLivingAnimations(CultistEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
		super.setLivingAnimations(entity, uniqueID, customPredicate);
		IBone head = getAnimationProcessor().getBone("head");
		IBone arms = getAnimationProcessor().getBone("arms");
		IBone left_arm = getAnimationProcessor().getBone("left_arm");
		IBone right_arm = getAnimationProcessor().getBone("right_arm");
		EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
		head.setRotationX(extraData.headPitch * 0.01f);
		head.setRotationY(extraData.netHeadYaw * 0.01f);
		if ((entity.attackID != 0 && !entity.isUsingItem()) || entity.isCelebrating()) {
			left_arm.setHidden(false);
			right_arm.setHidden(false);
			arms.setHidden(true);
		}
		else {
			left_arm.setHidden(true);
			right_arm.setHidden(true);
			arms.setHidden(false);
		}
	}
}
