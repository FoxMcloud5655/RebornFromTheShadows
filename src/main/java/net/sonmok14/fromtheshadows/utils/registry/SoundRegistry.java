package net.sonmok14.fromtheshadows.utils.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sonmok14.fromtheshadows.Fromtheshadows;

@Mod.EventBusSubscriber(modid = Fromtheshadows.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SoundRegistry {
	public static DeferredRegister<SoundEvent> MOD_SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Fromtheshadows.MODID);

	public static RegistryObject<SoundEvent> NEHEMOTH_IDLE = MOD_SOUNDS.register("fromtheshadows.nehemoth_idle", () -> new SoundEvent(new ResourceLocation(Fromtheshadows.MODID, "fromtheshadows.nehemoth_idle")));
	public static RegistryObject<SoundEvent> NEHEMOTH_ROAR = MOD_SOUNDS.register("fromtheshadows.nehemoth_roar", () -> new SoundEvent(new ResourceLocation(Fromtheshadows.MODID, "fromtheshadows.nehemoth_roar")));
	public static RegistryObject<SoundEvent> WARNING = MOD_SOUNDS.register("fromtheshadows.warning", () -> new SoundEvent(new ResourceLocation(Fromtheshadows.MODID, "fromtheshadows.warning")));
	public static RegistryObject<SoundEvent> CULTIST_IDLE = MOD_SOUNDS.register("fromtheshadows.cultist_idle", () -> new SoundEvent(new ResourceLocation(Fromtheshadows.MODID, "fromtheshadows.cultist_idle")));
	public static RegistryObject<SoundEvent> CULTIST_HURT = MOD_SOUNDS.register("fromtheshadows.cultist_hurt", () -> new SoundEvent(new ResourceLocation(Fromtheshadows.MODID, "fromtheshadows.cultist_hurt")));
	public static RegistryObject<SoundEvent> CULTIST_DEATH = MOD_SOUNDS.register("fromtheshadows.cultist_death", () -> new SoundEvent(new ResourceLocation(Fromtheshadows.MODID, "fromtheshadows.cultist_death")));
	public static RegistryObject<SoundEvent> CULTIST_PREATTACK = MOD_SOUNDS.register("fromtheshadows.cultist_preattack", () -> new SoundEvent(new ResourceLocation(Fromtheshadows.MODID, "fromtheshadows.cultist_preattack")));
	public static RegistryObject<SoundEvent> CULTIST_ATTACK = MOD_SOUNDS.register("fromtheshadows.cultist_attack", () -> new SoundEvent(new ResourceLocation(Fromtheshadows.MODID, "fromtheshadows.cultist_attack")));
	public static RegistryObject<SoundEvent> STOMP = MOD_SOUNDS.register("fromtheshadows.nehemoth_stomp", () -> new SoundEvent(new ResourceLocation(Fromtheshadows.MODID, "fromtheshadows.nehemoth_stomp")));
}
