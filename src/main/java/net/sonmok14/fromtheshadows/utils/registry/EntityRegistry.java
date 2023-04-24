package net.sonmok14.fromtheshadows.utils.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sonmok14.fromtheshadows.Fromtheshadows;
import net.sonmok14.fromtheshadows.entity.*;

public class EntityRegistry {
	public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, Fromtheshadows.MODID);
	public static RegistryObject<EntityType<CultistEntity>> CULTIST = ENTITY_TYPES.register("cultist", () -> EntityType.Builder.<CultistEntity>of(CultistEntity::new, MobCategory.MONSTER).sized(0.6f, 1.95f).fireImmune().clientTrackingRange(9).build(new ResourceLocation(Fromtheshadows.MODID, "cultist").toString()));
	public static RegistryObject<EntityType<NehemothEntity>> NEHEMOTH = ENTITY_TYPES.register("nehemoth", () -> EntityType.Builder.<NehemothEntity>of(NehemothEntity::new, MobCategory.MONSTER).sized(1.25f, 3.65f).fireImmune().clientTrackingRange(9).build(new ResourceLocation(Fromtheshadows.MODID, "nehemoth").toString()));
	public static RegistryObject<EntityType<ArmoredNehemothEntity>> ARMORED_NEHEMOTH = ENTITY_TYPES.register("armored_nehemoth", () -> EntityType.Builder.<ArmoredNehemothEntity>of(ArmoredNehemothEntity::new, MobCategory.MONSTER).sized(1.0f, 3.0f).fireImmune().clientTrackingRange(9).build(new ResourceLocation(Fromtheshadows.MODID, "armored_nehemoth").toString()));
	public static RegistryObject<EntityType<ScreenShakeEntity>> SCREEN_SHAKE = ENTITY_TYPES.register("screen_shake", () -> EntityType.Builder.<ScreenShakeEntity>of(ScreenShakeEntity::new, MobCategory.MISC).noSummon().sized(1.0f, 1.0f).setUpdateInterval(Integer.MAX_VALUE).build(new ResourceLocation(Fromtheshadows.MODID, "screen_shake").toString()));
	public static RegistryObject<EntityType<FallingBlockEntity>> FALLING_BLOCK = ENTITY_TYPES.register("falling_block", () -> EntityType.Builder.<FallingBlockEntity>of(FallingBlockEntity::new, MobCategory.MISC).noSummon().sized(1.0f, 1.0f).setUpdateInterval(Integer.MAX_VALUE).build(new ResourceLocation(Fromtheshadows.MODID, "falling_block").toString()));
	public static RegistryObject<EntityType<BreathEntity>> BREATH = ENTITY_TYPES.register("breath", () -> EntityType.Builder.<BreathEntity>of(BreathEntity::new, MobCategory.MISC).noSummon().sized(1.0f, 1.0f).setUpdateInterval(Integer.MAX_VALUE).build(new ResourceLocation(Fromtheshadows.MODID, "breath").toString()));
	public static RegistryObject<EntityType<SoulBreathEntity>> SOUL_BREATH = ENTITY_TYPES.register("soul_breath", () -> EntityType.Builder.<SoulBreathEntity>of(SoulBreathEntity::new, MobCategory.MISC).noSummon().sized(1.0f, 1.0f).setUpdateInterval(Integer.MAX_VALUE).build(new ResourceLocation(Fromtheshadows.MODID, "soul_breath").toString()));
}
