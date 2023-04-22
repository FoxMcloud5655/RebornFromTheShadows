package net.sonmok14.fromtheshadows.utils.registry;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sonmok14.fromtheshadows.Fromtheshadows;
import net.sonmok14.fromtheshadows.items.DiaboliumArmorItem;

public class ItemRegistry {

    public static net.sonmok14.fromtheshadows.items.ArmorMaterials DIABOLIUM_ARMOR_MATERIAL = new net.sonmok14.fromtheshadows.items.ArmorMaterials("diabolium", 18, new int[]{4, 5, 7, 4}, 50, SoundEvents.ARMOR_EQUIP_DIAMOND, 2, 0.2f);

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Fromtheshadows.MODID);

    public static final RegistryObject<DiaboliumArmorItem> DIABOLIUM_HEAD = ITEMS.register("diabolium_helmet",
            () -> new DiaboliumArmorItem(DIABOLIUM_ARMOR_MATERIAL, EquipmentSlot.HEAD, new Item.Properties().fireResistant().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<DiaboliumArmorItem> DIABOLIUM_CHEST = ITEMS.register("diabolium_chest",
            () -> new DiaboliumArmorItem(DIABOLIUM_ARMOR_MATERIAL, EquipmentSlot.CHEST, new Item.Properties().fireResistant().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<DiaboliumArmorItem> DIABOLIUM_LEGGINGS = ITEMS.register("diabolium_leggings",
            () -> new DiaboliumArmorItem(DIABOLIUM_ARMOR_MATERIAL, EquipmentSlot.LEGS, new Item.Properties().fireResistant().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> NEHEMOTH_SPAWN_EGG = ITEMS.register("nehemoth_spawn_egg",
            () -> new ForgeSpawnEggItem(EntityRegistry.NEHEMOTH, 0x626A6F,0xF3EAE8, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<Item> DIABOLIUM_INGOT = ITEMS.register("diabolium_ingot",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC).rarity(Rarity.UNCOMMON).fireResistant()));
    public static final RegistryObject<Item> BOTTLE_OF_BLOOD = ITEMS.register("bottle_of_blood",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC).rarity(Rarity.UNCOMMON)));
}
