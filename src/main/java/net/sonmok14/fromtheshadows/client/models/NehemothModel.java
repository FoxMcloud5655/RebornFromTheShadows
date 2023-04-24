package net.sonmok14.fromtheshadows.client.models;

import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.sonmok14.fromtheshadows.Fromtheshadows;
import net.sonmok14.fromtheshadows.entity.NehemothEntity;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class NehemothModel extends AnimatedGeoModel<NehemothEntity> {
	@Override
	public ResourceLocation getAnimationFileLocation(NehemothEntity entity) {
		return new ResourceLocation(Fromtheshadows.MODID, "animations/dracan.animation.json");
	}

	@Override
	public ResourceLocation getModelLocation(NehemothEntity entity) {
		return new ResourceLocation(Fromtheshadows.MODID, "geo/nehemoth.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(NehemothEntity entity) {
		String s = ChatFormatting.stripFormatting(entity.getName().getString());
		if (entity.isSilent()) {
			return new ResourceLocation(Fromtheshadows.MODID, "textures/entity/nehemoth_stone.png");
		}
		if (entity.getVariant() == 1 && s != null && !"dino".equals(s)) {
			return new ResourceLocation(Fromtheshadows.MODID, "textures/entity/soul_retexture.png");
		}
		return new ResourceLocation(Fromtheshadows.MODID, "textures/entity/nehemoth_retexture.png");
	}

	@Override
	public void setLivingAnimations(NehemothEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
		super.setLivingAnimations(entity, uniqueID, customPredicate);
		IBone root = getAnimationProcessor().getBone("root");
		IBone head = getAnimationProcessor().getBone("headrotate");
		IBone reverse = getAnimationProcessor().getBone("reversecontrol");
		EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
		head.setRotationX(extraData.headPitch * 0.01f);
		head.setRotationY(extraData.netHeadYaw * 0.01f);
		head.setRotationX(-1.0f);
		if (entity.attackID == 8) {
			reverse.setScaleX(-1.0f);
		}
		else {
			reverse.setScaleX(1.0f);
		}
	}
}
