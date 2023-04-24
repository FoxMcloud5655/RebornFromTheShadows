package net.sonmok14.fromtheshadows.utils.event;

import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.raid.Raider;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sonmok14.fromtheshadows.Fromtheshadows;
import net.sonmok14.fromtheshadows.entity.ArmoredNehemothEntity;
import net.sonmok14.fromtheshadows.entity.CultistEntity;
import net.sonmok14.fromtheshadows.entity.NehemothEntity;

@Mod.EventBusSubscriber(modid = Fromtheshadows.MODID)
public class EntityEvent {
	@SubscribeEvent
	public static void addSpawn(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof Villager abstractVillager) {
			abstractVillager.goalSelector.addGoal(1, new AvoidEntityGoal(abstractVillager, ArmoredNehemothEntity.class, 16.0f, 0.800000011920929, 0.8500000238418579));
			abstractVillager.goalSelector.addGoal(1, new AvoidEntityGoal(abstractVillager, NehemothEntity.class, 16.0f, 0.800000011920929, 0.8500000238418579));
			abstractVillager.goalSelector.addGoal(1, new AvoidEntityGoal(abstractVillager, CultistEntity.class, 16.0f, 0.800000011920929, 0.8500000238418579));
		}
		if (event.getEntity() instanceof WanderingTrader wanderingTraderEntity) {
			wanderingTraderEntity.goalSelector.addGoal(1, new AvoidEntityGoal(wanderingTraderEntity, ArmoredNehemothEntity.class, 16.0f, 0.800000011920929, 0.8500000238418579));
			wanderingTraderEntity.goalSelector.addGoal(1, new AvoidEntityGoal(wanderingTraderEntity, NehemothEntity.class, 16.0f, 0.800000011920929, 0.8500000238418579));
			wanderingTraderEntity.goalSelector.addGoal(1, new AvoidEntityGoal(wanderingTraderEntity, CultistEntity.class, 16.0f, 0.800000011920929, 0.8500000238418579));
		}
		if (event.getEntity() instanceof Raider raider) {
			raider.goalSelector.addGoal(2, new NearestAttackableTargetGoal(raider, NehemothEntity.class, true).setUnseenMemoryTicks(300));
			raider.goalSelector.addGoal(2, new NearestAttackableTargetGoal(raider, ArmoredNehemothEntity.class, true).setUnseenMemoryTicks(300));
		}
	}
}
