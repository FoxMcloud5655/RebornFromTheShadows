package net.sonmok14.fromtheshadows.utils;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sonmok14.fromtheshadows.Fromtheshadows;
import net.sonmok14.fromtheshadows.entity.ArmoredNehemothEntity;
import net.sonmok14.fromtheshadows.entity.CultistEntity;
import net.sonmok14.fromtheshadows.entity.NehemothEntity;
import net.sonmok14.fromtheshadows.utils.registry.EntityRegistry;

@Mod.EventBusSubscriber(modid = Fromtheshadows.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventSubscriber {
	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
		SpawnPlacements.register(EntityRegistry.NEHEMOTH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, NehemothEntity::canNehemothSpawn);
	}

	@SubscribeEvent
	public static void entityAttributes(EntityAttributeCreationEvent event) {
		event.put(EntityRegistry.CULTIST.get(), CultistEntity.createAttributes().build());
		event.put(EntityRegistry.NEHEMOTH.get(), NehemothEntity.createAttributes().build());
		event.put(EntityRegistry.ARMORED_NEHEMOTH.get(), ArmoredNehemothEntity.createAttributes().build());
	}
}
