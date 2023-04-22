package net.sonmok14.fromtheshadows.utils.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sonmok14.fromtheshadows.Fromtheshadows;
import net.sonmok14.fromtheshadows.entity.NehemothEntity;
import net.sonmok14.fromtheshadows.entity.ScreenShakeEntity;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES,
            Fromtheshadows.MODID);


    public static final RegistryObject<EntityType<NehemothEntity>> NEHEMOTH = ENTITY_TYPES.register("nehemoth",
            () -> EntityType.Builder.of(NehemothEntity::new, MobCategory.MONSTER).sized(1.0f, 3.0f)
                    .clientTrackingRange(9).build(new ResourceLocation(Fromtheshadows.MODID, "nehemoth").toString()));

    public static final RegistryObject<EntityType<ScreenShakeEntity>> SCREEN_SHAKE = ENTITY_TYPES.register("screen_shake", () -> EntityType.Builder.<ScreenShakeEntity>of(ScreenShakeEntity::new, MobCategory.MISC)
            .noSummon()
            .sized(1.0f, 1.0f)
            .setUpdateInterval(Integer.MAX_VALUE)
            .build(Fromtheshadows.MODID + ":screen_shake"));
}
