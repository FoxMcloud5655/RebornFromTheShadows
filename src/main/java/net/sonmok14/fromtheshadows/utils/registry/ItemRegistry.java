package net.sonmok14.fromtheshadows.utils.registry;

import java.util.function.Supplier;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sonmok14.fromtheshadows.Fromtheshadows;
import net.sonmok14.fromtheshadows.items.ArmorMaterials;
import net.sonmok14.fromtheshadows.items.DiaboliumArmorItem;
import net.sonmok14.fromtheshadows.items.PlagueArmorItem;

public class ItemRegistry {
	public static ArmorMaterials DIABOLIUM_ARMOR_MATERIAL = new ArmorMaterials("diabolium", 18, new int[] {4, 5, 7, 4}, 50, SoundEvents.ARMOR_EQUIP_DIAMOND, 2, 0.2f);
	public static ArmorMaterials PLAGUE_ARMOR_MATERIAL = new ArmorMaterials("plague", 18, new int[] {2, 3, 3, 2}, 30, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0f, 0.0f);;

	public static DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Fromtheshadows.MODID);

	public static RegistryObject<PlagueArmorItem> PLAGUE_MASK = ITEMS.register("plague_doctor_mask", () -> new PlagueArmorItem((ArmorMaterial) ItemRegistry.PLAGUE_ARMOR_MATERIAL, EquipmentSlot.HEAD, new Item.Properties().rarity(Rarity.COMMON)));
	public static RegistryObject<DiaboliumArmorItem> DIABOLIUM_HEAD = ITEMS.register("diabolium_helmet", () -> new DiaboliumArmorItem(DIABOLIUM_ARMOR_MATERIAL, EquipmentSlot.HEAD, new Item.Properties().fireResistant().rarity(Rarity.UNCOMMON)));
	public static RegistryObject<DiaboliumArmorItem> DIABOLIUM_CHEST = ITEMS.register("diabolium_chest", () -> new DiaboliumArmorItem(DIABOLIUM_ARMOR_MATERIAL, EquipmentSlot.CHEST, new Item.Properties().fireResistant().rarity(Rarity.UNCOMMON)));
	public static RegistryObject<DiaboliumArmorItem> DIABOLIUM_LEGGINGS = ITEMS.register("diabolium_leggings", () -> new DiaboliumArmorItem(DIABOLIUM_ARMOR_MATERIAL, EquipmentSlot.LEGS, new Item.Properties().fireResistant().rarity(Rarity.UNCOMMON)));

	public static RegistryObject<Item> NEHEMOTH_SPAWN_EGG = ITEMS.register("nehemoth_spawn_egg", () -> new ForgeSpawnEggItem(EntityRegistry.NEHEMOTH, 0x626A6F, 0xF3EAE8, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
	public static RegistryObject<Item> CULTIST_SPAWN_EGG = ITEMS.register("cultist_spawn_egg", () -> new ForgeSpawnEggItem(EntityRegistry.CULTIST, 0x615D5C, 0xE1E0DB, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

	public static RegistryObject<Item> DIABOLIUM_INGOT = ITEMS.register("diabolium_ingot", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC).rarity(Rarity.UNCOMMON).fireResistant()));
	public static RegistryObject<Item> BOTTLE_OF_BLOOD = ITEMS.register("bottle_of_blood", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC).rarity(Rarity.UNCOMMON)));
}
