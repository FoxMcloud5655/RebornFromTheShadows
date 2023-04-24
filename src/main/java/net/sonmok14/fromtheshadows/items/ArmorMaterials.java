package net.sonmok14.fromtheshadows.items;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public class ArmorMaterials implements ArmorMaterial {
	protected static int[] MAX_DAMAGE_ARRAY;
	private String name;
	private int durability;
	private int[] damageReduction;
	private int encantability;
	private SoundEvent sound;
	private float toughness;
	private Ingredient ingredient;
	public float knockbackResistance;

	public ArmorMaterials(String name, int durability, int[] damageReduction, int encantability, SoundEvent sound, float toughness) {
		ingredient = null;
		knockbackResistance = 0.0f;
		this.name = name;
		this.durability = durability;
		this.damageReduction = damageReduction;
		this.encantability = encantability;
		this.sound = sound;
		this.toughness = toughness;
		knockbackResistance = 0.0f;
	}

	public ArmorMaterials(String name, int durability, int[] damageReduction, int encantability, SoundEvent sound, float toughness, float knockbackResist) {
		ingredient = null;
		knockbackResistance = 0.0f;
		this.name = name;
		this.durability = durability;
		this.damageReduction = damageReduction;
		this.encantability = encantability;
		this.sound = sound;
		this.toughness = toughness;
		knockbackResistance = knockbackResist;
	}

	public int getDurabilityForSlot(EquipmentSlot slotIn) {
		return ArmorMaterials.MAX_DAMAGE_ARRAY[slotIn.getIndex()] * durability;
	}

	public int getDefenseForSlot(EquipmentSlot slotIn) {
		return damageReduction[slotIn.getIndex()];
	}

	public int getEnchantmentValue() {
		return encantability;
	}

	public SoundEvent getEquipSound() {
		return sound;
	}

	public Ingredient getRepairIngredient() {
		return (ingredient == null) ? Ingredient.EMPTY : ingredient;
	}

	public void setRepairMaterial(Ingredient ingredient) {
		this.ingredient = ingredient;
	}

	public String getName() {
		return name;
	}

	public float getToughness() {
		return toughness;
	}

	public float getKnockbackResistance() {
		return knockbackResistance;
	}

	static {
		MAX_DAMAGE_ARRAY = new int[] {13, 15, 16, 11};
	}
}
