package net.sonmok14.fromtheshadows.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.sonmok14.fromtheshadows.entity.ScreenShakeEntity;

@OnlyIn(Dist.CLIENT)
public enum ClientEvent {
	INSTANCE;

	@SubscribeEvent
	public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
		Player player = (Player) Minecraft.getInstance().player;
		float delta = Minecraft.getInstance().getFrameTime();
		float ticksExistedDelta = player.tickCount + delta;
		if (player != null) {
			float shakeAmplitude = 0.0f;
			for (ScreenShakeEntity ScreenShake : player.level.getEntitiesOfClass(ScreenShakeEntity.class, player.getBoundingBox().inflate(20.0, 20.0, 20.0))) {
				if (ScreenShake.distanceTo(player) < ScreenShake.getRadius()) {
					shakeAmplitude += ScreenShake.getShakeAmount(player, delta);
				}
				if (shakeAmplitude > 1.0f) {
					shakeAmplitude = 1.0f;
				}
				event.setPitch((float) (event.getPitch() + shakeAmplitude * Math.cos(ticksExistedDelta * 3.0f + 2.0f) * 25.0));
				event.setYaw((float) (event.getYaw() + shakeAmplitude * Math.cos(ticksExistedDelta * 5.0f + 1.0f) * 25.0));
				event.setRoll((float) (event.getRoll() + shakeAmplitude * Math.cos(ticksExistedDelta * 4.0f) * 25.0));
			}
		}
	}
}
