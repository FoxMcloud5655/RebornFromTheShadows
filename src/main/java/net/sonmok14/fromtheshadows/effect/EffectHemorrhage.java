package net.sonmok14.fromtheshadows.effect;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EffectHemorrhage extends MobEffect {
	private int lastDuration;

	public EffectHemorrhage(MobEffectCategory p_19451_, int p_19452_) {
		super(MobEffectCategory.HARMFUL, 13249837);
		lastDuration = -1;
		MinecraftForge.EVENT_BUS.addListener(this::chill);
	}

	@SubscribeEvent
	public void chill(LivingHealEvent event) {
		LivingEntity e = event.getEntityLiving();
		if (e.hasEffect((MobEffect) this)) {
			event.setCanceled(true);
		}
	}

	public void applyEffectTick(LivingEntity p_19467_, int p_19468_) {
		if (p_19467_.isSprinting() || p_19467_.isUsingItem() || p_19467_.getSpeed() >= 0.25 || p_19467_.swinging) {
			p_19467_.hurt(DamageSource.MAGIC, (float) Math.min(p_19468_ + 1, Math.round(lastDuration / 20.0f)));
		}
		super.applyEffectTick(p_19467_, p_19468_);
	}

	public boolean isDurationEffectTick(int duration, int amplifier) {
		lastDuration = duration;
		return duration > 0 && duration % 20 == 0;
	}
}
