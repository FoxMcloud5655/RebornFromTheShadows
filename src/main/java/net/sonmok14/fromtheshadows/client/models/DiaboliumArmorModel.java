package net.sonmok14.fromtheshadows.client.models;

import net.minecraft.resources.ResourceLocation;
import net.sonmok14.fromtheshadows.Fromtheshadows;
import net.sonmok14.fromtheshadows.items.DiaboliumArmorItem;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class DiaboliumArmorModel extends AnimatedGeoModel<DiaboliumArmorItem> {
	@Override
	public ResourceLocation getModelLocation(DiaboliumArmorItem object) {
		return new ResourceLocation(Fromtheshadows.MODID, "geo/diabolium_armor.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(DiaboliumArmorItem object) {
		return new ResourceLocation(Fromtheshadows.MODID, "textures/armor/diabolium_armor.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(DiaboliumArmorItem animatable) {
		return new ResourceLocation(Fromtheshadows.MODID, "animations/diabolium_armor.animation.json");
	}
}
