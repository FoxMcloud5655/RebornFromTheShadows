package net.sonmok14.fromtheshadows.utils.registry;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sonmok14.fromtheshadows.Fromtheshadows;

import java.util.List;

@Mod.EventBusSubscriber(modid = Fromtheshadows.MODID)
public class EntitySpawnRegistry {


    @SubscribeEvent
    public static void onBiomesLoad(BiomeLoadingEvent event) {
        List<MobSpawnSettings.SpawnerData> base = event.getSpawns().getSpawner(MobCategory.MONSTER);
        if (event.getCategory().equals(Biome.BiomeCategory.UNDERGROUND)) {
            base.add(new MobSpawnSettings.SpawnerData(EntityRegistry.NEHEMOTH.get(), 35,
                    1, 2));
        }

        if (event.getCategory().equals(Biome.BiomeCategory.byName("minecraft:nether_wastes"))) {
            base.add(new MobSpawnSettings.SpawnerData(EntityRegistry.NEHEMOTH.get(), 10,
                    1, 2));
        }


    }
}
