package net.sonmok14.fromtheshadows.client.models;

import net.minecraft.resources.ResourceLocation;
import net.sonmok14.fromtheshadows.Fromtheshadows;
import net.sonmok14.fromtheshadows.entity.ArmoredNehemothEntity;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class ArmoredNehemothModel extends AnimatedGeoModel<ArmoredNehemothEntity> {
	public ResourceLocation getAnimationFileLocation(ArmoredNehemothEntity entity) {
		return new ResourceLocation(Fromtheshadows.MODID, "animations/armored.animation.json");
	}

	public ResourceLocation getModelLocation(ArmoredNehemothEntity entity) {
		return new ResourceLocation(Fromtheshadows.MODID, "geo/armored_nehemoth.geo.json");
	}

	public ResourceLocation getTextureLocation(ArmoredNehemothEntity entity) {
		if (entity.isSilent()) {
			return new ResourceLocation(Fromtheshadows.MODID, "textures/entity/nehemoth_stone.png");
		}
		return new ResourceLocation(Fromtheshadows.MODID, "textures/entity/nehemoth.png");
	}

	public void setLivingAnimations(ArmoredNehemothEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
		super.setLivingAnimations(entity, uniqueID, customPredicate);
		IBone root = getAnimationProcessor().getBone("root");
		IBone head = getAnimationProcessor().getBone("headrotate");
		EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
		head.setRotationX(extraData.headPitch * 0.01f);
		head.setRotationY(extraData.netHeadYaw * 0.01f);
		head.setRotationX(-1.0f);
	}
}
