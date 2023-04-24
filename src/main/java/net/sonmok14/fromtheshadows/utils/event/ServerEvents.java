package net.sonmok14.fromtheshadows.utils.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.sonmok14.fromtheshadows.items.DiaboliumArmorItem;
import net.sonmok14.fromtheshadows.utils.registry.EffectRegistry;
import net.sonmok14.fromtheshadows.utils.registry.ItemRegistry;

public class ServerEvents {
	@SubscribeEvent
	public void onLivingDamage(LivingHurtEvent event) {
		if (event.getSource() instanceof EntityDamageSource && event.getSource().getEntity() instanceof LivingEntity) {
			LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
			LivingEntity target = (LivingEntity) event.getEntity();
			List<Item> equipmentList = new ArrayList<Item>();
			attacker.getAllSlots().forEach(x -> equipmentList.add(x.getItem()));
			List<Item> armorList = new ArrayList<Item>(3);
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				if (slot.getType() == EquipmentSlot.Type.ARMOR && attacker.getItemBySlot(slot) != null) {
					armorList.add(attacker.getItemBySlot(slot).getItem());
				}
				boolean isWearingAll = armorList.containsAll(Arrays.asList((DiaboliumArmorItem) ItemRegistry.DIABOLIUM_LEGGINGS.get(), (DiaboliumArmorItem) ItemRegistry.DIABOLIUM_CHEST.get(), (DiaboliumArmorItem) ItemRegistry.DIABOLIUM_HEAD.get()));
				if (isWearingAll && !target.hasEffect((MobEffect) EffectRegistry.BLEEDING.get())) {
					target.playSound(SoundEvents.BEE_STING, 2.0f, 0.4f);
					target.addEffect(new MobEffectInstance((MobEffect) EffectRegistry.BLEEDING.get(), 100), attacker);
				}
			}
		}
	}
}
