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
	public static final DeferredRegister<SoundEvent> MOD_SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Fromtheshadows.MODID);

	public static final RegistryObject<SoundEvent> NEHEMOTH_ROAR = MOD_SOUNDS.register("fromtheshadows.nehemoth_roar", () -> new SoundEvent(new ResourceLocation(Fromtheshadows.MODID, "fromtheshadows.nehemoth_roar")));

}
