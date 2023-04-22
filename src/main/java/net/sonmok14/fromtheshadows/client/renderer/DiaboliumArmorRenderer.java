package net.sonmok14.fromtheshadows.client.renderer;

import net.sonmok14.fromtheshadows.client.models.DiaboliumArmorModel;
import net.sonmok14.fromtheshadows.items.DiaboliumArmorItem;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class DiaboliumArmorRenderer extends GeoArmorRenderer<DiaboliumArmorItem> {
	public DiaboliumArmorRenderer() {
		super(new DiaboliumArmorModel());

		// These values are what each bone name is in blockbench. So if your head bone
		// is named "bone545", make sure to do this.headBone = "bone545";
		// The default values are the ones that come with the default armor template in
		// the geckolib blockbench plugin.
		this.headBone = "bipedHead";
		this.bodyBone = "bipedBody";
		this.rightArmBone = "bipedRightArm";
		this.leftArmBone = "bipedLeftArm";
		this.rightLegBone = "bipedLeftLeg";
		this.leftLegBone = "bipedRightLeg";
		this.rightBootBone = "armorRightBoot";
		this.leftBootBone = "armorLeftBoot";
	}
}
