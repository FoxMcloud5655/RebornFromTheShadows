package net.sonmok14.fromtheshadows.utils.registry;

import java.util.List;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.sonmok14.fromtheshadows.Fromtheshadows;

@Mod.EventBusSubscriber(modid = Fromtheshadows.MODID)
public class EntitySpawnRegistry {
	@SubscribeEvent
	public static void onBiomesLoad(BiomeLoadingEvent event) {
		BiomeGenerationSettingsBuilder builder = event.getGeneration();
		ResourceLocation biomeName = event.getName();
		ResourceKey<Biome> biomeKey = (ResourceKey<Biome>) ResourceKey.create(ForgeRegistries.Keys.BIOMES, event.getName());
		List<MobSpawnSettings.SpawnerData> base = event.getSpawns().getSpawner(MobCategory.MONSTER);
		if (biomeName != null) {
			if (biomeName.equals(Biomes.SOUL_SAND_VALLEY.location())) {
				base.add(new MobSpawnSettings.SpawnerData(EntityRegistry.NEHEMOTH.get(), 1, 1, 1));
			}
			if (biomeKey != null) {
				if (BiomeDictionary.hasType((ResourceKey) biomeKey, BiomeDictionary.Type.OCEAN)) {
					base.add(new MobSpawnSettings.SpawnerData(EntityRegistry.NEHEMOTH.get(), 25, 1, 1));
				}
				if (BiomeDictionary.hasType((ResourceKey) biomeKey, BiomeDictionary.Type.OVERWORLD)) {
					base.add(new MobSpawnSettings.SpawnerData(EntityRegistry.NEHEMOTH.get(), 25, 1, 1));
				}
			}
		}
	}
}
