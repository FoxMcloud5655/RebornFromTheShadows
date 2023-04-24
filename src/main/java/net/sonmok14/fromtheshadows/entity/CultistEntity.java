package net.sonmok14.fromtheshadows.entity;

import java.util.EnumSet;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.UseItemGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.sonmok14.fromtheshadows.utils.registry.SoundRegistry;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.CustomInstructionKeyframeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class CultistEntity extends AbstractIllager implements IAnimatable, IAnimationTickable {
	private AnimationFactory factory;
	public int attackID;
	public int attacktick;
	public static byte MELEE_ATTACK = 1;
	public static byte SHOT_ATTACK = 2;
	public static byte FANG_ATTACK = 3;

	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		if (attackID == 1 || attackID == 3) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.cultist.spell", Boolean.valueOf(true)));
			return PlayState.CONTINUE;
		}
		if (event.isMoving() && animationSpeed < 0.35f) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.cultist.walk", Boolean.valueOf(true)));
			return PlayState.CONTINUE;
		}
		if (isCelebrating() && !this.isAggressive() && !event.isMoving()) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.cultist.celebrate", Boolean.valueOf(true)));
			return PlayState.CONTINUE;
		}
		if (animationSpeed > 0.35f) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.cultist.run", Boolean.valueOf(true)));
			return PlayState.CONTINUE;
		}
		event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.cultist.idle", Boolean.valueOf(true)));
		return PlayState.CONTINUE;
	}

	public CultistEntity(EntityType<? extends CultistEntity> type, Level worldIn) {
		super(type, worldIn);
		factory = new AnimationFactory((IAnimatable) this);
		noCulling = true;
		maxUpStep = 1.0f;
	}

	public boolean canBeLeader() {
		return false;
	}

	public int tickTimer() {
		return tickCount;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return LivingEntity.createLivingAttributes().add(Attributes.FOLLOW_RANGE, 25.0).add(Attributes.MOVEMENT_SPEED, 0.25).add(Attributes.MAX_HEALTH, 30.0).add(Attributes.ATTACK_DAMAGE, 5.0).add(Attributes.ATTACK_KNOCKBACK, 0.0);
	}

	public boolean doHurtTarget(Entity p_85031_1_) {
		if (!this.level.isClientSide && attackID == 0) {
			if (random.nextInt(4) != 0) {
				attackID = 1;
			}
			else {
				attackID = 1;
			}
		}
		return true;
	}

	public void setAttackID(int id) {
		attackID = id;
		attacktick = 0;
		level.broadcastEntityEvent(this, (byte) (-id));
	}

	public void handleEntityEvent(byte id) {
		if (id <= 0) {
			attackID = Math.abs(id);
			attacktick = 0;
		}
		else {
			super.handleEntityEvent(id);
		}
	}

	private void createSpellEntity(double p_32673_, double p_32674_, double p_32675_, double p_32676_, float p_32677_, int p_32678_) {
		BlockPos blockpos = new BlockPos(p_32673_, p_32676_, p_32674_);
		boolean flag = false;
		double d0 = 0.0;
		do {
			BlockPos blockpos2 = blockpos.below();
			BlockState blockstate = level.getBlockState(blockpos2);
			if (blockstate.isFaceSturdy((BlockGetter) level, blockpos2, Direction.UP)) {
				if (!this.level.isEmptyBlock(blockpos)) {
					BlockState blockstate2 = level.getBlockState(blockpos);
					VoxelShape voxelshape = blockstate2.getCollisionShape((BlockGetter) level, blockpos);
					if (!voxelshape.isEmpty()) {
						d0 = voxelshape.max(Direction.Axis.Y);
					}
				}
				flag = true;
				break;
			}
			blockpos = blockpos.below();
		}
		while (blockpos.getY() >= Mth.floor(p_32675_) - 1);
		if (flag) {
			level.addFreshEntity(new EvokerFangs(level, p_32673_, blockpos.getY() + d0, p_32674_, p_32677_, p_32678_, (LivingEntity) this));
		}
	}

	public void travel(Vec3 travelVector) {
		if (attackID == 1 || attackID == 3) {
			if (getNavigation().getPath() != null) {
				getNavigation().stop();
			}
			travelVector = Vec3.ZERO;
			super.travel(travelVector);
			return;
		}
		super.travel(travelVector);
	}

	public void tick() {
		super.tick();
		if (attackID != 0) {
			++this.attacktick;
		}
		if (getTarget() != null) {
			float f = (float) Mth.atan2(getTarget().getZ() - getZ(), getTarget().getX() - getX());
			double d02 = Math.min(getTarget().getY(), getY());
			double d3 = Math.max(getTarget().getY(), getY()) + 1.0;
			if (attacktick == 15 && attackID == 1) {
				playSound((SoundEvent) SoundRegistry.CULTIST_ATTACK.get(), 2.0f, 0.7f + getRandom().nextFloat() * 0.1f);
				for (int i = 0; i < 5; ++i) {
					float f2 = f + i * 3.1415927f * 0.4f;
					createSpellEntity(getX() + Mth.cos(f2) * 1.5, getZ() + Mth.sin(f2) * 1.5, d02, d3, f2, 0);
				}
				for (int k = 0; k < 8; ++k) {
					float f3 = f + k * 3.1415927f * 2.0f / 8.0f + 1.2566371f;
					createSpellEntity(getX() + Mth.cos(f3) * 2.5, getZ() + Mth.sin(f3) * 2.5, d02, d3, f3, 3);
				}
			}
			if (attacktick == 15 && attackID == 3) {
				double d4 = Math.min(getTarget().getY(), getY());
				double d5 = Math.max(getTarget().getY(), getY()) + 1.0;
				playSound((SoundEvent) SoundRegistry.CULTIST_ATTACK.get(), 2.0f, 0.7f + getRandom().nextFloat() * 0.1f);
				for (int l = 0; l < 16; ++l) {
					double d6 = 1.25 * (l + 1);
					int j = 1 * l;
					createSpellEntity(getX() + Mth.cos(f) * d6, getZ() + Mth.sin(f) * d6, d4, d5, f, j);
				}
			}
			if (attacktick == 5 && attackID == 2) {
				Vec3 vec3 = getDeltaMovement();
				double d7 = getTarget().getX() + vec3.x - getX();
				double d8 = getTarget().getEyeY() - 1.100000023841858 - getY();
				double d6 = getTarget().getZ() + vec3.z - getZ();
				double d9 = Math.sqrt(d7 * d7 + d6 * d6);
				Potion potion = Potions.HARMING;
				if (d9 >= 8.0 && !this.getTarget().hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
					potion = Potions.SLOWNESS;
				}
				else if (getTarget().getHealth() >= 8.0f && !this.hasEffect(MobEffects.POISON)) {
					potion = Potions.POISON;
				}
				else if (d9 <= 3.0 && !this.getTarget().hasEffect(MobEffects.WEAKNESS) && random.nextFloat() < 0.25f) {
					potion = Potions.WEAKNESS;
				}
				ThrownPotion thrownpotion = new ThrownPotion(level, (LivingEntity) this);
				thrownpotion.setItem(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion));
				thrownpotion.setXRot(thrownpotion.getXRot() + 20.0f);
				thrownpotion.shoot(d7, d8 + d9 * 0.2, d6, 0.75f, 8.0f);
				level.addFreshEntity(thrownpotion);
				playSound((SoundEvent) SoundRegistry.CULTIST_PREATTACK.get(), 2.0f, 0.7f + getRandom().nextFloat() * 0.1f);
			}
		}
	}

	public void registerControllers(AnimationData data) {
		AnimationController<CultistEntity> controller = (AnimationController<CultistEntity>) new AnimationController((IAnimatable) this, "controller", 5.0f, this::predicate);
		controller.registerCustomInstructionListener(this::customListener);
		data.addAnimationController((AnimationController) controller);
	}

	private <ENTITY extends IAnimatable> void customListener(CustomInstructionKeyframeEvent<ENTITY> event) {}

	@Nullable
	protected SoundEvent getAmbientSound() {
		return (SoundEvent) SoundRegistry.CULTIST_IDLE.get();
	}

	protected SoundEvent getHurtSound(DamageSource p_33034_) {
		return (SoundEvent) SoundRegistry.CULTIST_HURT.get();
	}

	protected SoundEvent getDeathSound() {
		return (SoundEvent) SoundRegistry.CULTIST_DEATH.get();
	}

	public float getVoicePitch() {
		return 0.7f;
	}

	public AnimationFactory getFactory() {
		return factory;
	}

	protected void registerGoals() {
		super.registerGoals();
		goalSelector.addGoal(0, new UseItemGoal(this, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.SWIFTNESS), SoundEvents.EVOKER_CELEBRATE, p_35882_ -> getTarget() != null && attackID == 0 && random.nextFloat() < 0.05f && !this.hasEffect(MobEffects.MOVEMENT_SPEED)));
		goalSelector.addGoal(0, new UseItemGoal(this, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.HEALING), SoundEvents.EVOKER_CELEBRATE, p_35882_ -> attackID == 0 && getHealth() < 15.0f && random.nextFloat() < 0.05f));
		goalSelector.addGoal(0, new UseItemGoal(this, new ItemStack(Items.MILK_BUCKET), SoundEvents.EVOKER_CELEBRATE, p_35880_ -> getTarget() == null && attackID == 0 && hasEffect(MobEffects.MOVEMENT_SPEED)));
		goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.5, false));
		goalSelector.addGoal(0, new FangAttackGoal(this));
		goalSelector.addGoal(0, new FangAttackGoalMelee(this));
		goalSelector.addGoal(0, new BreathGoal(this));
		goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0f));
		goalSelector.addGoal(1, new AvoidEntityGoal(this, Player.class, 8.0f, 1.5, 1.5));
		goalSelector.addGoal(1, new AvoidEntityGoal(this, NehemothEntity.class, 8.0f, 1.5, 1.5));
		goalSelector.addGoal(1, new AvoidEntityGoal(this, Monster.class, 8.0f, 1.5, 1.5));
		goalSelector.addGoal(1, new AvoidEntityGoal(this, IronGolem.class, 8.0f, 1.5, 1.5));
		goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.7));
		goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0f));
		goalSelector.addGoal(0, new FloatGoal(this));
		targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[] {Raider.class}).setAlertOthers(new Class[0]));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true).setUnseenMemoryTicks(300));
		targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, AbstractVillager.class, false).setUnseenMemoryTicks(300));
		targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, false));
		super.registerGoals();
	}

	public void applyRaidBuffs(int p_37844_, boolean p_37845_) {}

	protected float getDamageAfterMagicAbsorb(DamageSource p_34149_, float p_34150_) {
		p_34150_ = super.getDamageAfterMagicAbsorb(p_34149_, p_34150_);
		if (p_34149_.getEntity() == this) {
			p_34150_ = 0.0f;
		}
		if (p_34149_.isMagic()) {
			p_34150_ *= 0.15f;
		}
		return p_34150_;
	}

	public SoundEvent getCelebrateSound() {
		return (SoundEvent) SoundRegistry.CULTIST_IDLE.get();
	}

	public boolean isAlliedTo(Entity p_32665_) {
		return p_32665_ != null && (p_32665_ == this || super.isAlliedTo(p_32665_) || (p_32665_ instanceof LivingEntity && ((LivingEntity) p_32665_).getMobType() == MobType.ILLAGER && getTeam() == null && p_32665_.getTeam() == null));
	}

	private class FangAttackGoalMelee extends Goal {
		private CultistEntity nehemoth;
		private LivingEntity attackTarget;

		public FangAttackGoalMelee(CultistEntity p_i45837_1_) {
			setFlags((EnumSet) EnumSet.of(Goal.Flag.JUMP, Goal.Flag.LOOK, Goal.Flag.MOVE));
			nehemoth = p_i45837_1_;
		}

		public boolean canUse() {
			attackTarget = nehemoth.getTarget();
			return attackTarget != null && nehemoth.attackID == 1;
		}

		public void start() {
			nehemoth.setAttackID(1);
		}

		public void stop() {
			nehemoth.setAttackID(0);
			attackTarget = null;
		}

		public boolean canContinueToUse() {
			return nehemoth.attacktick < 30;
		}

		public void tick() {
			if (nehemoth.attacktick < 30 && attackTarget.isAlive()) {
				nehemoth.getLookControl().setLookAt(attackTarget, 30.0f, 30.0f);
			}
			double dist = nehemoth.distanceTo(attackTarget);
		}
	}

	private class FangAttackGoal extends Goal {
		private CultistEntity nehemoth;
		private LivingEntity attackTarget;

		public FangAttackGoal(CultistEntity p_i45837_1_) {
			setFlags((EnumSet) EnumSet.of(Goal.Flag.JUMP, Goal.Flag.LOOK, Goal.Flag.MOVE));
			nehemoth = p_i45837_1_;
		}

		public boolean canUse() {
			attackTarget = nehemoth.getTarget();
			return attackTarget != null && nehemoth.attackID == 0 && CultistEntity.this.distanceTo(attackTarget) > 6.0 && CultistEntity.this.random.nextInt(15) == 0;
		}

		public void start() {
			nehemoth.setAttackID(3);
		}

		public void stop() {
			nehemoth.setAttackID(0);
			attackTarget = null;
		}

		public boolean canContinueToUse() {
			return nehemoth.attacktick < 30;
		}

		public void tick() {
			if (nehemoth.attacktick < 30 && attackTarget.isAlive()) {
				nehemoth.getLookControl().setLookAt(attackTarget, 30.0f, 30.0f);
			}
			double dist = nehemoth.distanceTo(attackTarget);
		}
	}

	private class BreathGoal extends Goal {
		private CultistEntity nehemoth;
		private LivingEntity attackTarget;

		public BreathGoal(CultistEntity p_i45837_1_) {
			setFlags((EnumSet) EnumSet.of(Goal.Flag.JUMP, Goal.Flag.LOOK, Goal.Flag.MOVE));
			nehemoth = p_i45837_1_;
		}

		public boolean canUse() {
			attackTarget = nehemoth.getTarget();
			return attackTarget != null && nehemoth.attackID == 0 && CultistEntity.this.distanceTo(attackTarget) > 6.0 && CultistEntity.this.random.nextInt(22) == 0;
		}

		public void start() {
			nehemoth.setAttackID(2);
		}

		public void stop() {
			nehemoth.setAttackID(0);
			attackTarget = null;
		}

		public boolean canContinueToUse() {
			return nehemoth.attacktick < 15;
		}

		public void tick() {
			double dist = nehemoth.distanceTo(attackTarget);
			if (nehemoth.attacktick < 15 && attackTarget.isAlive()) {
				nehemoth.getLookControl().setLookAt(attackTarget, 30.0f, 30.0f);
			}
		}
	}
}
