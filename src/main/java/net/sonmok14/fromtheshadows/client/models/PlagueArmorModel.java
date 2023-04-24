package net.sonmok14.fromtheshadows.client.models;

import net.minecraft.resources.ResourceLocation;
import net.sonmok14.fromtheshadows.Fromtheshadows;
import net.sonmok14.fromtheshadows.items.PlagueArmorItem;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class PlagueArmorModel extends AnimatedGeoModel<PlagueArmorItem> {

	@Override
	public ResourceLocation getModelLocation(PlagueArmorItem object) {
		return new ResourceLocation(Fromtheshadows.MODID, "geo/plague.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(PlagueArmorItem object) {
		return new ResourceLocation(Fromtheshadows.MODID, "textures/armor/plague.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(PlagueArmorItem animatable) {
		return new ResourceLocation(Fromtheshadows.MODID, "animations/diabolium_armor.animation.json");
	}
}
