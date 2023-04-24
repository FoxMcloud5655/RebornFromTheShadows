package net.sonmok14.fromtheshadows.utils.registry;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sonmok14.fromtheshadows.Fromtheshadows;
import net.sonmok14.fromtheshadows.client.particle.LaserParticle;
import net.sonmok14.fromtheshadows.client.particle.SoulParticle;

@Mod.EventBusSubscriber(modid = Fromtheshadows.MODID, value = {Dist.CLIENT}, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ParticleRegistry {
	public static DeferredRegister<ParticleType<?>> PARTICLES;
	public static RegistryObject<SimpleParticleType> LASER;
	public static RegistryObject<SimpleParticleType> SOUL;

	@SubscribeEvent
	public static void registry(ParticleFactoryRegisterEvent event) {
		Minecraft.getInstance().particleEngine.register((ParticleType) ParticleRegistry.LASER.get(), LaserParticle.Factory::new);
		Minecraft.getInstance().particleEngine.register((ParticleType) ParticleRegistry.SOUL.get(), SoulParticle.Factory::new);
	}

	static {
		PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Fromtheshadows.MODID);
		LASER = ParticleRegistry.PARTICLES.register("laser", () -> new SimpleParticleType(false));
		SOUL = ParticleRegistry.PARTICLES.register("soul", () -> new SimpleParticleType(false));
	}
}
