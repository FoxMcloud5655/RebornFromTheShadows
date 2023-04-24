package net.sonmok14.fromtheshadows.utils.registry;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sonmok14.fromtheshadows.Fromtheshadows;
import net.sonmok14.fromtheshadows.effect.EffectHealblock;
import net.sonmok14.fromtheshadows.effect.EffectHemorrhage;

public class EffectRegistry {
	public static DeferredRegister<MobEffect> EFFECT;
	public static RegistryObject<MobEffect> HEAL_BLOCK;
	public static RegistryObject<MobEffect> BLEEDING;

	static {
		EFFECT = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Fromtheshadows.MODID);
		HEAL_BLOCK = EffectRegistry.EFFECT.register("heal_block", () -> new EffectHealblock(MobEffectCategory.HARMFUL, 7897742));
		BLEEDING = EffectRegistry.EFFECT.register("bleeding", () -> new EffectHemorrhage(MobEffectCategory.HARMFUL, 13249837));
	}
}
