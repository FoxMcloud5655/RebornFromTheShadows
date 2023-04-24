package net.sonmok14.fromtheshadows.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.sonmok14.fromtheshadows.utils.registry.ItemRegistry;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.item.GeoArmorItem;

public class PlagueArmorItem extends GeoArmorItem implements IAnimatable {
	private AnimationFactory factory;

	public PlagueArmorItem(ArmorMaterial materialIn, EquipmentSlot slot, Item.Properties builder) {
		super(materialIn, slot, builder.tab(CreativeModeTab.TAB_COMBAT));
		factory = new AnimationFactory((IAnimatable) this);
	}

	private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {
		List<EquipmentSlot> slotData = event.getExtraDataOfType(EquipmentSlot.class);
		List<ItemStack> stackData = event.getExtraDataOfType(ItemStack.class);
		LivingEntity livingEntity = event.getExtraDataOfType(LivingEntity.class).get(0);
		event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.diabolium.none", Boolean.valueOf(true)));
		if (livingEntity instanceof ArmorStand) {
			return PlayState.CONTINUE;
		}
		if (livingEntity instanceof Player player) {
			List<Item> equipmentList = new ArrayList<Item>();
			player.getAllSlots().forEach(x -> equipmentList.add(x.getItem()));
			List<Item> armorList = equipmentList.subList(2, 6);
			boolean isWearingAll = armorList.containsAll(Arrays.asList((DiaboliumArmorItem) ItemRegistry.DIABOLIUM_LEGGINGS.get()));
			return isWearingAll ? PlayState.CONTINUE : PlayState.STOP;
		}
		return PlayState.STOP;
	}

	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController((IAnimatable) this, "controller", 20.0f, this::predicate));
	}

	public AnimationFactory getFactory() {
		return factory;
	}

	public void onArmorTick(ItemStack stack, Level level, Player player) {
		Iterator<MobEffectInstance> itr = player.getActiveEffectsMap().values().iterator();
		while (itr.hasNext()) {
			int i = 1;
			MobEffectInstance effect = itr.next();
			MobEffect mobeffect = effect.getEffect();
			if (effect != null && player.hasEffect(mobeffect) && effect.getAmplifier() == 0) {
				MobEffectInstance effectinstance = new MobEffectInstance(mobeffect, effect.getDuration(), 1);
				player.addEffect(effectinstance);
			}
		}
		super.onArmorTick(stack, level, player);
	}
}
