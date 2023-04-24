package net.sonmok14.fromtheshadows.entity;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import net.sonmok14.fromtheshadows.utils.registry.EffectRegistry;
import net.sonmok14.fromtheshadows.utils.registry.ItemRegistry;
import net.sonmok14.fromtheshadows.utils.registry.SoundRegistry;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class NehemothEntity extends Monster implements Enemy, IAnimatable, IAnimationTickable {
	@OnlyIn(Dist.CLIENT)
	public Vec3[] socketPosArray;
	private static EntityDataAccessor<Boolean> CROUCH;
	private static EntityDataAccessor<Integer> VARIANT;
	private static Predicate<Entity> NO_NEHEMOTH_AND_ALIVE;
	public int attackID;
	private int stunnedTick;
	public int attacktick;
	private Vec3 prevBladePos;
	private Vec3 prevBladePos2;
	public static byte MELEE_ATTACK = 1;
	public static byte BITE_ATTACK = 2;
	public static byte ROAR_ATTACK = 3;
	public static byte SMASH_ATTACK = 4;
	public static byte GUARD_ATTACK = 5;
	public static byte SHOT_ATTACK = 6;
	public static byte THROWING_ATTACK = 7;
	public static byte MELEE_ATTACK_REVERSE = 8;
	private AnimationFactory factory;

	public NehemothEntity(EntityType<? extends NehemothEntity> type, Level world) {
		super(type, world);
		prevBladePos = new Vec3(0.0, 0.0, 0.0);
		prevBladePos2 = new Vec3(0.0, 0.0, 0.0);
		factory = new AnimationFactory((IAnimatable) this);
		maxUpStep = 1.0f;
		xpReward = 30;
		if (world.isClientSide) {
			socketPosArray = new Vec3[] {new Vec3(0.0, 0.0, 0.0)};
		}
	}

	public static AttributeSupplier.Builder createAttributes() {
		return LivingEntity.createLivingAttributes().add(Attributes.FOLLOW_RANGE, 25.0).add(Attributes.MOVEMENT_SPEED, 0.35).add(Attributes.MAX_HEALTH, 60.0).add(Attributes.ATTACK_DAMAGE, 9.0).add(Attributes.ATTACK_KNOCKBACK, 0.0).add(Attributes.KNOCKBACK_RESISTANCE, 9.0).add(Attributes.ARMOR, 9.0).add(Attributes.ARMOR_TOUGHNESS, 5.0).add(Attributes.ATTACK_SPEED, 2.0);
	}

	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("Variant", getVariant());
	}

	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		setVariant(compound.getInt("Variant"));
	}

	protected float nextStep() {
		if (isAggressive()) {
			return moveDist + 2.0f;
		}
		return moveDist + 1.0f;
	}

	@OnlyIn(Dist.CLIENT)
	public void setSocketPosArray(int index, Vec3 pos) {
		if (socketPosArray != null && socketPosArray.length > index) {
			socketPosArray[index] = pos;
		}
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define((EntityDataAccessor) NehemothEntity.VARIANT, 0);
		entityData.define((EntityDataAccessor) NehemothEntity.CROUCH, false);
	}

	public boolean isCrouch() {
		return (boolean) entityData.get((EntityDataAccessor) NehemothEntity.CROUCH);
	}

	public void setCrouch(boolean p_32759_) {
		entityData.set((EntityDataAccessor) NehemothEntity.CROUCH, p_32759_);
	}

	public int getVariant() {
		return (int) entityData.get((EntityDataAccessor) NehemothEntity.VARIANT);
	}

	public void setVariant(int variant) {
		entityData.set((EntityDataAccessor) NehemothEntity.VARIANT, variant);
	}

	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
		if (isBiomeNether((LevelAccessor) worldIn, blockPosition())) {
			setVariant(1);
		}
		else {
			setVariant(0);
		}
		setAirSupply(getMaxAirSupply());
		setXRot(0.0f);
		return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	private static boolean isBiomeNether(LevelAccessor worldIn, BlockPos position) {
		return worldIn.getBiome(position).is(Biomes.SOUL_SAND_VALLEY);
	}

	public int tickTimer() {
		return tickCount;
	}

	private <E extends IAnimatable> PlayState predicate4(AnimationEvent<E> event) {
		event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.none", Boolean.valueOf(true)));
		return PlayState.CONTINUE;
	}

	private <E extends IAnimatable> PlayState predicate3(AnimationEvent<E> event) {
		if (!this.isSilent() && !event.isMoving() && attackID == 0) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.ldle", Boolean.valueOf(true)));
			return PlayState.CONTINUE;
		}
		if (isSilent()) {
			switch (random.nextInt(2)) {
			case 0: {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.none", Boolean.valueOf(true)));
			}
			case 1: {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.none", Boolean.valueOf(true)));
				break;
			}
			}
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.stop1", Boolean.valueOf(true)));
			return PlayState.CONTINUE;
		}
		return PlayState.STOP;
	}

	private <E extends IAnimatable> PlayState predicate2(AnimationEvent<E> event) {
		if (attackID == 4 && isOnGround() && attacktick < 15) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.smashstart", Boolean.valueOf(true)));
			return PlayState.CONTINUE;
		}
		if (attackID == 4 && !this.isOnGround()) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.jump", Boolean.valueOf(true)));
			return PlayState.CONTINUE;
		}
		if (attackID == 1 && isAlive()) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.meleeattack1", Boolean.valueOf(true)));
			return PlayState.CONTINUE;
		}
		if (attackID == 8 && isAlive()) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.meleeattack1", Boolean.valueOf(true)));
			return PlayState.CONTINUE;
		}
		return PlayState.STOP;
	}

	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		if (!this.isSilent()) {
			if (event.isMoving() && isAggressive() && attackID == 0) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.run", Boolean.valueOf(true)));
				return PlayState.CONTINUE;
			}
			if (attackID == 3) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.roar", Boolean.valueOf(true)));
				return PlayState.CONTINUE;
			}
			if (!this.isImmobile()) {
				if (attackID == 1) {
					event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.none", Boolean.valueOf(true)));
					return PlayState.CONTINUE;
				}
				if (attackID == 2) {
					event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.bite", Boolean.valueOf(true)));
					return PlayState.CONTINUE;
				}
				if (attackID == 7 && attacktick < 15) {
					event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.bite", Boolean.valueOf(true)));
					return PlayState.CONTINUE;
				}
				if (attackID == 7 && attacktick > 15) {
					event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.roar", Boolean.valueOf(true)));
					return PlayState.CONTINUE;
				}
			}
			if (attackID == 7 && !this.isOnGround()) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.jump", Boolean.valueOf(true)));
				return PlayState.CONTINUE;
			}
			if (attackID == 4 && isOnGround() && attacktick < 15) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.none", Boolean.valueOf(true)));
				return PlayState.CONTINUE;
			}
			if (attackID == 4 && isOnGround() && attacktick > 15) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.smash", Boolean.valueOf(true)));
				return PlayState.CONTINUE;
			}
			if (!this.isAlive() || getHealth() == 0.1 || isDeadOrDying()) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.death", Boolean.valueOf(false)));
				return PlayState.CONTINUE;
			}
			if (attackID == 4 && !this.isOnGround()) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.none", Boolean.valueOf(true)));
				return PlayState.CONTINUE;
			}
			if (attackID == 5 && attacktick < 55) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.guard", Boolean.valueOf(true)));
				return PlayState.CONTINUE;
			}
			if (attackID == 5 && attacktick > 55) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.bulk", Boolean.valueOf(true)));
				return PlayState.CONTINUE;
			}
			if (attackID == 6 && attacktick < 5) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.bulk", Boolean.valueOf(true)));
				return PlayState.CONTINUE;
			}
			if (attackID == 6) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.breath", Boolean.valueOf(true)));
				return PlayState.CONTINUE;
			}
			if (isImmobile() && isAlive()) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.stun", Boolean.valueOf(true)));
				return PlayState.CONTINUE;
			}
			if (event.isMoving()) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.walk", Boolean.valueOf(true)));
				return PlayState.CONTINUE;
			}
			if (!event.isMoving() && attackID == 0) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.ldle", Boolean.valueOf(true)));
				return PlayState.CONTINUE;
			}
		}
		else if (isSilent()) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.none", Boolean.valueOf(true)));
			return PlayState.CONTINUE;
		}
		return PlayState.CONTINUE;
	}

	public int getMaxSpawnClusterSize() {
		return 2;
	}

	public static <T extends Mob> boolean canNehemothSpawn(EntityType<NehemothEntity> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, Random random) {
		if (isBiomeNether((LevelAccessor) iServerWorld, pos)) {
			return reason == MobSpawnType.SPAWNER || checkMonsterSpawnRules(entityType, iServerWorld, reason, pos, random);
		}
		return reason == MobSpawnType.SPAWNER || (!iServerWorld.canSeeSky(pos) && pos.getY() <= 0 && checkMonsterSpawnRules(entityType, iServerWorld, reason, pos, random));
	}

	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController((IAnimatable) this, "controller", 3.0f, this::predicate));
		data.addAnimationController(new AnimationController((IAnimatable) this, "controller2", 8.0f, this::predicate2));
		data.addAnimationController(new AnimationController((IAnimatable) this, "controller3", 20.0f, this::predicate3));
		data.addAnimationController(new AnimationController((IAnimatable) this, "reverse", 0.0f, this::predicate4));
	}

	public MobType getMobType() {
		if (getVariant() == 1) {
			return MobType.UNDEAD;
		}
		return super.getMobType();
	}

	public AnimationFactory getFactory() {
		return factory;
	}

	public boolean isPushedByFluid() {
		return false;
	}

	protected void registerGoals() {
		goalSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.MeleeAttackGoal(this, 1.2, false));
		goalSelector.addGoal(0, new MeleeAttackGoal(this));
		goalSelector.addGoal(0, new BiteAttackGoal(this));
		goalSelector.addGoal(0, new FloatGoal(this));
		goalSelector.addGoal(0, new RoarGoal(this));
		goalSelector.addGoal(0, new SmashGoal(this));
		goalSelector.addGoal(0, new BreathGoal(this));
		goalSelector.addGoal(0, new DoNothingGoal(this));
		goalSelector.addGoal(0, new GuardandRevengeGoal(this));
		goalSelector.addGoal(0, new ThrowingGoal(this));
		goalSelector.addGoal(0, new MeleeAttackReverseGoal(this));
		goalSelector.addGoal(1, new RandomLookAroundGoal(this));
		goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0f));
		goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0f));
		targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, IronGolem.class, true));
		targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, Piglin.class, true));
		targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, Axolotl.class, true));
		targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Raider.class, true));
		targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Player.class, true));
		targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true, (p_199899_) -> {
			return !p_199899_.isBaby();
		}));
		targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
		targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[] {NehemothEntity.class}));
		goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
		goalSelector.addGoal(2, new RandomStrollGoal(this, 0.5, 25, true));
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
		else if (id == 39) {
			stunnedTick = 40;
		}
		else {
			super.handleEntityEvent(id);
		}
	}

	public boolean doHurtTarget(Entity p_85031_1_) {
		if (!this.level.isClientSide && attackID == 0) {
			if (random.nextInt(2) != 0) {
				attackID = 8;
			}
			else {
				attackID = 1;
			}
		}
		return true;
	}

	protected void tickDeath() {
		++this.deathTime;
		if (deathTime == 50) {
			remove(Entity.RemovalReason.KILLED);
			dropExperience();
		}
	}

	public void tick() {
		super.tick();
		setYRot(yBodyRot);
		refreshDimensions();
		BlockPos hasblock = new BlockPos(getX(), (double) (getBbHeight() + 1.0f), getZ());
		BlockState state = level.getBlockState(hasblock);
		boolean validAboveState = state.isAir();
		if (!validAboveState) {
			setCrouch(true);
		}
		if (level.isNight()) {
			setInvulnerable(false);
			setSilent(false);
		}
		Level level = this.level;
		++this.tickCount;
		if (attackID != 0) {
			++this.attacktick;
		}
		if (getTarget() != null) {
			if (attackID == 7) {
				if (attacktick == 2) {
					float f1 = (float) Math.cos(Math.toRadians(getYRot() + 90.0f));
					float f2 = (float) Math.sin(Math.toRadians(getYRot() + 90.0f));
					push(f1 * 0.2, 0.0, (double) (f2 * 1.0f));
				}
				if (attacktick == 22) {
					ejectPassengers();
				}
				if (attacktick == 17 && hasPassenger(getTarget())) {
					float f1 = (float) Math.cos(Math.toRadians(getYRot() + 90.0f));
					float f2 = (float) Math.sin(Math.toRadians(getYRot() + 90.0f));
					getTarget().stopRiding();
					getTarget().push(f1 * 2.5, 0.7, f2 * 2.5);
				}
				if (attacktick > 17 && getTarget().horizontalCollision) {
					getTarget().hurt(DamageSource.mobAttack((LivingEntity) this), 6.0f);
				}
			}
			if (attackID == 5 && isSilent()) {
				attacktick = 80;
			}
			if (attackID == 6 && isSilent()) {
				attacktick = 16;
			}
			if (attackID == 3 && isSilent()) {
				attacktick = 25;
			}
			if (attackID == 4 && isSilent()) {
				attacktick = 38;
			}
			if (attackID == 2 && isSilent()) {
				attacktick = 23;
			}
			if (attackID == 1 && isSilent()) {
				attacktick = 39;
			}
			if (attackID == 6 && attacktick == 8) {
				playSound(SoundEvents.FIREWORK_ROCKET_LAUNCH, 2.0f, 0.1f + getRandom().nextFloat() * 0.1f);
				double d1 = 4.0;
				LivingEntity livingEntity = getTarget();
				Vec3 vec3 = getViewVector(1.0f);
				double d2 = livingEntity.getX() - (getX() + vec3.x * 4.0);
				double d3 = livingEntity.getY(0.5) - (0.5 + getY(0.5));
				double d4 = livingEntity.getZ() - (getZ() + vec3.z * 4.0);
				SoulBreathEntity largefireball = new SoulBreathEntity(level, (LivingEntity) this, d2, d3, d4);
				largefireball.setPos(getX() + vec3.x * 0.3, getY(0.52), largefireball.getZ() + vec3.z * 0.1);
				level.addFreshEntity(largefireball);
			}
			if (attackID == 2 && attacktick == 2) {
				float f1 = (float) Math.cos(Math.toRadians(getYRot() + 90.0f));
				float f2 = (float) Math.sin(Math.toRadians(getYRot() + 90.0f));
				push(f1 * 0.2, 0.0, (double) (f2 * 1.0f));
			}
			if (attacktick == 17 && attackID == 2 && hasPassenger(getTarget())) {
				getTarget().hurt(DamageSource.mobAttack((LivingEntity) this), (float) getAttributeValue(Attributes.ATTACK_DAMAGE));
				if (attackID == 2 && !this.getTarget().isBlocking()) {
					level.playSound((Player) null, getX(), getY(), getZ(), SoundEvents.STRIDER_EAT, getSoundSource(), 3.0f, 0.3f + (random.nextFloat() - random.nextFloat()) * 0.2f);
					getTarget().addEffect(new MobEffectInstance((MobEffect) EffectRegistry.HEAL_BLOCK.get(), 400), this);
					heal((float) getAttributeValue(Attributes.ATTACK_DAMAGE));
				}
				if (attackID == 2 && getTarget().isBlocking() && random.nextDouble() < 0.5) {
					blockedByShield((LivingEntity) this);
					ejectPassengers();
					attacktick = 19;
				}
			}
			if (attackID == 4) {
				if (attacktick < 38 && getTarget().isAlive()) {
					yBodyRot = yHeadRot;
					lookAt(getTarget(), 30.0f, 30.0f);
				}
				if (attacktick == 2) {
					float f1 = (float) Math.cos(Math.toRadians(getYRot() + 90.0f));
					float f2 = (float) Math.sin(Math.toRadians(getYRot() + 90.0f));
					push(f1 * 0.4, 0.0, f2 * 0.4);
				}
				if (attacktick == 9) {
					setJumping(true);
					setShiftKeyDown(true);
					setDeltaMovement((getTarget().getX() - getX()) * 0.22, 0.8, (getTarget().getZ() - getZ()) * 0.22);
				}
				if (attackID == 4 && isOnGround() && isShiftKeyDown() && attacktick > 15) {
					if (getTarget() instanceof Player player) {
						if (player.isBlocking()) {
							player.disableShield(true);
						}
					}
					if (distanceTo(getTarget()) <= 2.0) {
						getTarget().hurt(DamageSource.mobAttack((LivingEntity) this), (float) getAttributeValue(Attributes.ATTACK_DAMAGE));
					}
					setShiftKeyDown(false);
					ScreenShakeEntity.ScreenShake(level, position(), 15.0f, 0.2f, 0, 10);
					roar();
					smash(4);
					playSound(SoundEvents.GENERIC_EXPLODE, 2.0f, 0.2f + getRandom().nextFloat() * 0.1f);
				}
			}
			if (attackID == 5 && attacktick == 55) {
				ServerLevel serverlevel = (ServerLevel) level;
				level.getProfiler().pop();
				roar();
				ScreenShakeEntity.ScreenShake(level, position(), 15.0f, 0.2f, 0, 10);
				playSound((SoundEvent) SoundRegistry.NEHEMOTH_ROAR.get(), 1.5f, 1.0f + getRandom().nextFloat() * 0.1f);
			}
			if (attackID == 1 || attackID == 8) {
				if (attacktick == 7) {
					yBodyRot = yHeadRot;
					float f1 = (float) Math.cos(Math.toRadians(getYRot() + 90.0f));
					float f2 = (float) Math.sin(Math.toRadians(getYRot() + 90.0f));
					push(f1 * 0.6, 0.0, f2 * 0.6);
				}
				if (attacktick == 25) {
					yBodyRot = yHeadRot;
					float f1 = (float) Math.cos(Math.toRadians(getYRot() + 90.0f));
					float f2 = (float) Math.sin(Math.toRadians(getYRot() + 90.0f));
					push(f1 * 0.6, 0.0, f2 * 0.6);
				}
				if (attacktick == 9) {
					meleeattack();
					playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 3.0f, 0.5f + getRandom().nextFloat() * 0.1f);
				}
				if (attacktick == 31) {
					meleeattack();
					playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 3.0f, 0.5f + getRandom().nextFloat() * 0.1f);
				}
			}
			if (attackID == 3) {
				if (attacktick < 25 && getTarget().isAlive() && getTarget() != null) {
					lookAt(getTarget(), 30.0f, 30.0f);
				}
				roar();
				if (attacktick == 1) {
					playSound((SoundEvent) SoundRegistry.NEHEMOTH_ROAR.get(), 1.5f, 1.0f + getRandom().nextFloat() * 0.1f);
					ScreenShakeEntity.ScreenShake(level, position(), 20.0f, 0.2f, 20, 10);
				}
			}
		}
		BlockPos blockpos = new BlockPos(getX(), getEyeY(), getZ());
		if (stunnedTick > 0) {
			--this.stunnedTick;
			stunEffect();
		}
		if (isSilent()) {
			setAttackID(0);
		}
		if (!level.canSeeSky(blockpos) && isSilent()) {
			setInvulnerable(false);
			setSilent(false);
		}
		if (attackID == 4 && isOnGround() && attacktick > 15) {
			Vec3 vec4 = getBoundingBox().getCenter();
			BlockState block = level.getBlockState(blockPosition().below());
			for (int j = 0; j < 32; ++j) {
				float f3 = random.nextFloat() * 12.566371f;
				float f4 = random.nextFloat() * 0.8f + 0.8f;
				float f5 = Mth.sin(f3) * 2.0f * 0.8f * f4;
				float f6 = Mth.cos(f3) * 2.0f * 0.8f * f4;
				double d5 = random.nextGaussian() * 0.5;
				double d6 = random.nextGaussian() * 0.5;
				double d7 = random.nextGaussian() * 0.5;
				level.addParticle((ParticleOptions) ParticleTypes.SMOKE, getX() + f5, getY(), getZ() + f6, d5, d6, d7);
				level.addParticle((ParticleOptions) new BlockParticleOption(ParticleTypes.BLOCK, block), getX() + f5, getY(), getZ() + f6, d5, d6, d7);
			}
			for (int j = 0; j < 16; ++j) {
				float f3 = random.nextFloat() * 6.2831855f;
				float f4 = random.nextFloat() * 0.8f + 0.8f;
				float f5 = Mth.sin(f3) * 4.0f * 0.8f * f4;
				float f6 = Mth.cos(f3) * 4.0f * 0.8f * f4;
				double d5 = random.nextGaussian() * 0.05;
				double d6 = random.nextGaussian() * 0.05;
				double d7 = random.nextGaussian() * 0.05;
				level.addParticle((ParticleOptions) ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, getX() + f5, getY(), getZ() + f6, d5, d6, d7);
			}
			for (int j = 0; j < 4; ++j) {
				float f3 = random.nextFloat() * 3.1415927f;
				float f4 = random.nextFloat() * 0.8f + 0.8f;
				float f5 = Mth.sin(f3) * 1.0f * 0.8f * f4;
				float f6 = Mth.cos(f3) * 1.0f * 0.8f * f4;
				double d5 = random.nextGaussian() * 0.2;
				double d6 = random.nextGaussian() * 0.2;
				double d7 = random.nextGaussian() * 0.2;
				level.addParticle((ParticleOptions) ParticleTypes.CAMPFIRE_COSY_SMOKE, getX() + f5, getY(), getZ() + f6, d5, d6, d7);
				if (getVariant() == 1) {
					level.addParticle((ParticleOptions) ParticleTypes.SOUL_FIRE_FLAME, getX() + f5, getY(), getZ() + f6, d5, d6, d7);
				}
				else {
					level.addParticle((ParticleOptions) ParticleTypes.FLAME, getX() + f5, getY(), getZ() + f6, d5, d6, d7);
				}
			}
		}
		if (level.isClientSide && getVariant() == 1) {
			if (random.nextInt(24) == 0 && !this.isSilent()) {
				level.playLocalSound(getX() + 0.5, getY() + 0.5, getZ() + 0.5, SoundEvents.BLAZE_BURN, getSoundSource(), 1.0f + random.nextFloat(), random.nextFloat() * 0.7f + 0.3f, false);
			}
			for (int i = 0; i < 1; ++i) {
				level.addParticle((ParticleOptions) ParticleTypes.LARGE_SMOKE, getRandomX(0.5), getRandomY(), getRandomZ(0.5), 0.0, 0.0, 0.0);
			}
		}
	}

	public EntityDimensions getDimensions(Pose p_29531_) {
		return super.getDimensions(p_29531_);
	}

	protected PathNavigation createNavigation(Level p_33802_) {
		return (PathNavigation) new GroundPathNavigation(this, p_33802_);
	}

	protected boolean isImmobile() {
		return super.isImmobile() || stunnedTick > 0;
	}

	public boolean hasLineOfSight(Entity p_149755_) {
		return stunnedTick <= 0 && super.hasLineOfSight(p_149755_);
	}

	public int getStunnedTick() {
		return stunnedTick;
	}

	public boolean isAlliedTo(Entity p_32665_) {
		return p_32665_ != null && (p_32665_ == this || super.isAlliedTo(p_32665_) || p_32665_ instanceof ArmoredNehemothEntity);
	}

	private void smash(int distance) {
		double perpFacing = yBodyRot * 0.017453292519943295;
		double facingAngle = perpFacing + 1.5707963267948966;
		int hitY = Mth.floor(getBoundingBox().minY - 0.5);
		double spread = 6.283185307179586;
		for (int arcLen = Mth.ceil(distance * spread), i = 0; i < arcLen; ++i) {
			double theta = (i / (arcLen - 1.0) - 0.5) * spread + facingAngle;
			double vx = Math.cos(theta);
			double vz = Math.sin(theta);
			double px = getX() + vx * distance;
			double pz = getZ() + vz * distance;
			if (ForgeEventFactory.getMobGriefingEvent(level, this)) {
				int hitX = Mth.floor(px);
				int hitZ = Mth.floor(pz);
				BlockPos pos = new BlockPos(hitX, hitY, hitZ);
				BlockPos abovePos = new BlockPos((Vec3i) pos).above();
				BlockState block = level.getBlockState(pos);
				BlockState blockAbove = level.getBlockState(abovePos);
				if (block.getMaterial() != Material.AIR && !block.hasBlockEntity() && !blockAbove.getMaterial().blocksMotion()) {
					FallingBlockEntity fallingBlockEntity = new FallingBlockEntity(level, hitX + 0.5, hitY + 0.5, hitZ + 0.5, block);
					level.setBlock(pos, block.getFluidState().createLegacyBlock(), 3);
					fallingBlockEntity.push(getRandom().nextGaussian() * 3.0, 0.2 + getRandom().nextGaussian() * 0.2, getRandom().nextGaussian() * 3.0);
					level.addFreshEntity(fallingBlockEntity);
				}
			}
		}
	}

	private void stunEffect() {
		if (random.nextInt(6) == 0) {
			double d0 = getX() - getBbWidth() * Math.sin(yBodyRot * 0.017453292f) + (random.nextDouble() * 0.6 - 0.3);
			double d2 = getY() + getBbHeight() - 0.3;
			double d3 = getZ() + getBbWidth() * Math.cos(yBodyRot * 0.017453292f) + (random.nextDouble() * 0.6 - 0.3);
			level.addParticle((ParticleOptions) ParticleTypes.ENTITY_EFFECT, d0, d2, d3, 0.4980392156862745, 0.5137254901960784, 0.5725490196078431);
		}
	}

	private void strongKnockback(Entity p_33340_) {
		double d0 = p_33340_.getX() - getX();
		double d2 = p_33340_.getZ() - getZ();
		double d3 = Math.max(d0 * d0 + d2 * d2, 0.001);
		p_33340_.push(d0 / d3 * 2.0, 0.2, d2 / d3 * 2.0);
	}

	public boolean isPushable() {
		return false;
	}

	protected void blockedByShield(LivingEntity p_33361_) {
		if (random.nextDouble() < 0.5 && attackID == 2) {
			stunnedTick = 40;
			level.broadcastEntityEvent(this, (byte) 39);
			p_33361_.push(this);
		}
		p_33361_.hurtMarked = true;
	}

	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		Item item = itemstack.getItem();
		if (itemstack.is(Items.GLASS_BOTTLE) && getHealth() < 10.0f && !this.isSilent()) {
			boolean flag = hurt(DamageSource.mobAttack((LivingEntity) player), 2.0f);
			hurt(DamageSource.MAGIC, 2.0f);
			if (flag) {
				itemstack.shrink(1);
				player.playSound(SoundEvents.BOTTLE_FILL, 0.1f, 1.0f);
				player.getTicksUsingItem();
				if (itemstack.isEmpty()) {
					player.setItemInHand(hand, new ItemStack(ItemRegistry.BOTTLE_OF_BLOOD.get()));
				}
				else if (!player.getInventory().add(new ItemStack(ItemRegistry.BOTTLE_OF_BLOOD.get()))) {
					player.drop(new ItemStack(ItemRegistry.BOTTLE_OF_BLOOD.get()), false);
				}
			}
			return super.mobInteract(player, hand);
		}
		return InteractionResult.FAIL;
	}

	protected int calculateFallDamage(float p_21237_, float p_21238_) {
		return 0;
	}

	public List<LivingEntity> getEntityLivingBaseNearby(double distanceX, double distanceY, double distanceZ, double radius) {
		return getEntitiesNearby(LivingEntity.class, distanceX, distanceY, distanceZ, radius);
	}

	public <T extends Entity> List<T> getEntitiesNearby(Class<T> entityClass, double dX, double dY, double dZ, double r) {
		return level.getEntitiesOfClass(entityClass, getBoundingBox().inflate(dX, dY, dZ), e -> e != this && distanceTo(e) <= r + e.getBbWidth() / 2.0f && e.getY() <= getY() + dY);
	}

	private void meleeattack() {
		float range = 2.5f;
		float arc = 80.0f;
		if (ForgeEventFactory.getMobGriefingEvent(level, this)) {
			boolean flag = false;
			AABB aabb = getBoundingBox().inflate(0.2);
			for (BlockPos blockpos : BlockPos.betweenClosed(Mth.floor(aabb.minX), Mth.floor(aabb.getYsize()), Mth.floor(aabb.minZ), Mth.floor(aabb.maxX), Mth.floor(aabb.getYsize()), Mth.floor(aabb.maxZ))) {
				BlockState blockstate = level.getBlockState(blockpos);
				Block block = blockstate.getBlock();
				flag = (level.destroyBlock(blockpos, true, this) || flag);
			}
		}
		List<LivingEntity> entitiesHit = getEntityLivingBaseNearby(range, 2.5, range, range);
		for (LivingEntity entityHit : entitiesHit) {
			float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - getZ(), entityHit.getX() - getX()) * 57.29577951308232 - 90.0) % 360.0);
			float entityAttackingAngle = yHeadRot % 360.0f;
			if (entityHitAngle < 0.0f) {
				entityHitAngle += 360.0f;
			}
			if (entityAttackingAngle < 0.0f) {
				entityAttackingAngle += 360.0f;
			}
			float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
			float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - getZ()) * (entityHit.getZ() - getZ()) + (entityHit.getX() - getX()) * (entityHit.getX() - getX()));
			if (entityHitDistance <= range && entityRelativeAngle <= arc / 2.0f && entityRelativeAngle >= -arc / 2.0f && entityRelativeAngle >= 360.0f - arc / 2.0f == entityRelativeAngle <= -360.0f + arc / 2.0f && !(entityHit instanceof NehemothEntity)) {
				boolean flag2 = entityHit.hurt(DamageSource.mobAttack((LivingEntity) this), (float) getAttributeValue(Attributes.ATTACK_DAMAGE));
				if (entityHit.isBlocking()) {
					continue;
				}
				entityHit.invulnerableTime = 0;
				if (!flag2) {
					continue;
				}
				entityHit.addEffect(new MobEffectInstance((MobEffect) EffectRegistry.BLEEDING.get(), 100), this);
				entityHit.playSound(SoundEvents.BEE_STING, 2.0f, 0.4f + getRandom().nextFloat() * 0.1f);
			}
		}
	}

	public void positionRider(Entity passenger) {
		super.positionRider(passenger);
		if (hasPassenger(passenger)) {
			int tick = 5;
			if (attackID == 2) {
				tick = attacktick;
				if (attacktick == 22) {
					passenger.stopRiding();
					float f1 = (float) Math.cos(Math.toRadians(getYRot() + 90.0f));
					float n = (float) Math.sin(Math.toRadians(getYRot() + 90.0f));
				}
				setYRot(yRotO);
				yBodyRot = getYRot();
				yHeadRot = getYRot();
			}
			if (attackID == 7) {
				tick = attacktick;
				if (attacktick == 17) {
					passenger.stopRiding();
					Vec3 throwVec = getLookAngle().scale(2.0);
					throwVec = new Vec3(throwVec.x(), 0.9, throwVec.z());
					float f2 = (float) Math.cos(Math.toRadians(getYRot() + 90.0f));
					float f3 = (float) Math.sin(Math.toRadians(getYRot() + 90.0f));
					passenger.setPos(f2 * 1.5, throwVec.y(), f3 * 1.5);
				}
				setYRot(yRotO);
				yBodyRot = getYRot();
				yHeadRot = getYRot();
			}
			float radius = 0.3f;
			float math = 1.0f;
			float angle = 0.017453292f * yBodyRot;
			float f4 = Mth.cos(getYRot() * 0.017453292f);
			float f5 = Mth.sin(getYRot() * 0.017453292f);
			double extraX = radius * Mth.sin((float) (3.141592653589793 + angle));
			double extraZ = radius * Mth.cos(angle);
			double extraY = (tick < 5) ? 0.0 : (0.2f * Mth.clamp(tick - 5, 0, 5));
			if (passenger.getBbHeight() < 1.0f) {
				passenger.setPos(getX() + f4 * math + extraX, getY() + extraY + 1.399999976158142, getZ() + f5 * math + extraZ);
			}
			else {
				passenger.setPos(getX() + f4 * math + extraX, getY() + extraY + 0.699999988079071, getZ() + f5 * math + extraZ);
			}
		}
	}

	@javax.annotation.Nullable
	private Vec3 getDismountLocationInDirection(Vec3 p_30562_, LivingEntity p_30563_) {
		double d0 = getX() + p_30562_.x;
		double d2 = getBoundingBox().minY;
		double d3 = getZ() + p_30562_.z;
		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
		for (Pose pose : p_30563_.getDismountPoses()) {
			blockpos$mutableblockpos.set(d0, d2, d3);
			double d4 = getBoundingBox().maxY + 0.75;
			do {
				double d5 = level.getBlockFloorHeight((BlockPos) blockpos$mutableblockpos);
				if (blockpos$mutableblockpos.getY() + d5 > d4) {
					break;
				}
				if (DismountHelper.isBlockFloorValid(d5)) {
					AABB aabb = p_30563_.getLocalBoundsForPose(pose);
					Vec3 vec3 = new Vec3(d0, blockpos$mutableblockpos.getY() + d5, d3);
					if (DismountHelper.canDismountTo((CollisionGetter) level, p_30563_, aabb.move(vec3))) {
						p_30563_.setPose(pose);
						return vec3;
					}
				}
				blockpos$mutableblockpos.move(Direction.UP);
			}
			while (blockpos$mutableblockpos.getY() < d4);
		}
		return null;
	}

	public Vec3 getDismountLocationForPassenger(LivingEntity p_30576_) {
		Vec3 vec3 = getCollisionHorizontalEscapeVector((double) getBbWidth(), (double) p_30576_.getBbWidth(), getYRot() + ((p_30576_.getMainArm() == HumanoidArm.RIGHT) ? 90.0f : -90.0f));
		Vec3 vec4 = getDismountLocationInDirection(vec3, p_30576_);
		if (vec4 != null) {
			return vec4;
		}
		Vec3 vec5 = getCollisionHorizontalEscapeVector((double) getBbWidth(), (double) p_30576_.getBbWidth(), getYRot() + ((p_30576_.getMainArm() == HumanoidArm.LEFT) ? 90.0f : -90.0f));
		Vec3 vec6 = getDismountLocationInDirection(vec5, p_30576_);
		return (vec6 != null) ? vec6 : position();
	}

	public void onSyncedDataUpdated(EntityDataAccessor<?> p_21104_) {
		super.onSyncedDataUpdated((EntityDataAccessor) p_21104_);
	}

	private void roar() {
		if (isAlive()) {
			List<LivingEntity> entityList = level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(3.0), NehemothEntity.NO_NEHEMOTH_AND_ALIVE);
			for (LivingEntity livingentity : entityList) {
				if (!(livingentity instanceof NehemothEntity)) {
					livingentity.hurt(DamageSource.mobAttack((LivingEntity) this), 3.0f);
				}
				strongKnockback(livingentity);
			}
		}
	}

	@Nullable
	protected SoundEvent getAmbientSound() {
		return (SoundEvent) SoundRegistry.NEHEMOTH_IDLE.get();
	}

	protected SoundEvent getHurtSound(DamageSource p_33034_) {
		return SoundEvents.RAVAGER_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.RAVAGER_DEATH;
	}

	protected void playStepSound(BlockPos p_33350_, BlockState p_33351_) {
		playSound((SoundEvent) SoundRegistry.STOMP.get(), 0.5f, 0.7f);
	}

	public float getVoicePitch() {
		return 0.7f;
	}

	public boolean hurt(DamageSource p_21016_, float p_21017_) {
		if (p_21016_.isProjectile()) {
			p_21017_ = Math.min(1.0f, p_21017_);
		}
		if (attackID == 5 && attacktick < 55) {
			return false;
		}
		if (level.isClientSide && p_21016_.getEntity() instanceof LivingEntity && isInvulnerable()) {
			LivingEntity entity = (LivingEntity) p_21016_.getEntity();
			if (isSilent() && entity.getMainHandItem().getItem() instanceof PickaxeItem) {
				for (int j = 0; j < 24; ++j) {
					float f = random.nextFloat() * 12.566371f;
					float f2 = random.nextFloat() * 0.8f + 0.8f;
					float f3 = Mth.sin(f) * 3.0f * 0.8f * f2;
					float f4 = Mth.cos(f) * 3.0f * 0.8f * f2;
					level.addParticle((ParticleOptions) new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()), getX() + f3, getY(), getZ() + f4, 0.0, 0.0, 0.0);
				}
				entity.playSound(SoundEvents.STONE_BREAK, 2.0f, 1.0f + getRandom().nextFloat() * 0.1f);
			}
		}
		if (!this.level.isClientSide && p_21016_.getEntity() instanceof LivingEntity && isInvulnerable()) {
			LivingEntity entity = (LivingEntity) p_21016_.getEntity();
			if (isSilent() && entity.getMainHandItem().getItem() instanceof PickaxeItem && random.nextInt(22) == 0) {
				discard();
			}
		}
		return (!this.isSilent() || p_21016_ == DamageSource.OUT_OF_WORLD) && super.hurt(p_21016_, p_21017_);
	}

	public static boolean checkMonsterSpawnRules(EntityType<? extends Monster> p_33018_, ServerLevelAccessor p_33019_, MobSpawnType p_33020_, BlockPos p_33021_, Random p_33022_) {
		return p_33019_.getDifficulty() != Difficulty.PEACEFUL && checkMobSpawnRules(p_33018_, (LevelAccessor) p_33019_, p_33020_, p_33021_, p_33022_);
	}

	public boolean canRiderInteract() {
		return true;
	}

	public boolean shouldRiderSit() {
		return false;
	}

	public void travel(Vec3 travelVector) {
		if (attackID != 0 || isSilent()) {
			if (getNavigation().getPath() != null) {
				getNavigation().stop();
			}
			travelVector = Vec3.ZERO;
			super.travel(travelVector);
			return;
		}
		super.travel(travelVector);
	}

	static {
		CROUCH = SynchedEntityData.defineId(NehemothEntity.class, EntityDataSerializers.BOOLEAN);
		VARIANT = SynchedEntityData.defineId(NehemothEntity.class, EntityDataSerializers.INT);
		NO_NEHEMOTH_AND_ALIVE = (p_33346_ -> p_33346_.isAlive() && !(p_33346_ instanceof NehemothEntity));
	}

	private class MeleeAttackGoal extends Goal {
		private NehemothEntity nehemoth;
		private LivingEntity attackTarget;

		public MeleeAttackGoal(NehemothEntity p_i45837_1_) {
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
			return nehemoth.attacktick < 39;
		}

		public void tick() {
			NehemothEntity.this.setYRot(NehemothEntity.this.yBodyRot);
			if (nehemoth.attacktick < 39 && attackTarget.isAlive()) {
				NehemothEntity.this.lookAt(attackTarget, 30.0f, 30.0f);
			}
			NehemothEntity.this.getNavigation().recomputePath();
		}
	}

	private class MeleeAttackReverseGoal extends Goal {
		private NehemothEntity nehemoth;
		private LivingEntity attackTarget;

		public MeleeAttackReverseGoal(NehemothEntity p_i45837_1_) {
			setFlags((EnumSet) EnumSet.of(Goal.Flag.JUMP, Goal.Flag.LOOK, Goal.Flag.MOVE));
			nehemoth = p_i45837_1_;
		}

		public boolean canUse() {
			attackTarget = nehemoth.getTarget();
			return attackTarget != null && nehemoth.attackID == 8;
		}

		public void start() {
			nehemoth.setAttackID(8);
		}

		public void stop() {
			nehemoth.setAttackID(0);
			attackTarget = null;
		}

		public boolean canContinueToUse() {
			return nehemoth.attacktick < 39;
		}

		public void tick() {
			NehemothEntity.this.setYRot(NehemothEntity.this.yBodyRot);
			if (nehemoth.attacktick < 39 && attackTarget.isAlive()) {
				NehemothEntity.this.lookAt(attackTarget, 30.0f, 30.0f);
			}
			NehemothEntity.this.getNavigation().recomputePath();
		}
	}

	private class ThrowingGoal extends Goal {
		private NehemothEntity nehemoth;
		private LivingEntity attackTarget;

		public ThrowingGoal(NehemothEntity p_i45837_1_) {
			setFlags((EnumSet) EnumSet.of(Goal.Flag.JUMP, Goal.Flag.LOOK, Goal.Flag.MOVE));
			nehemoth = p_i45837_1_;
		}

		public boolean canUse() {
			attackTarget = nehemoth.getTarget();
			return attackTarget != null && nehemoth.attackID == 0 && NehemothEntity.this.distanceTo(attackTarget) <= 3.0f && NehemothEntity.this.random.nextInt(15) == 0 && !(attackTarget instanceof ServerPlayer);
		}

		public void start() {
			nehemoth.setAttackID(7);
		}

		public void stop() {
			nehemoth.setAttackID(0);
			attackTarget = null;
			NehemothEntity.this.ejectPassengers();
		}

		public boolean canContinueToUse() {
			return nehemoth.attacktick < 23;
		}

		public void tick() {
			if (nehemoth.attacktick > 6 && nehemoth.attacktick < 16 && attackTarget != null && !this.nehemoth.hasPassenger(attackTarget)) {
				NehemothEntity.this.attacktick = 22;
			}
			NehemothEntity.this.stuckSpeedMultiplier = Vec3.ZERO;
			double dist = nehemoth.distanceTo(attackTarget);
			if (nehemoth.attacktick < 23 && attackTarget.isAlive()) {
				NehemothEntity.this.lookAt(attackTarget, 30.0f, 30.0f);
			}
			if (nehemoth.attacktick == 6 && dist <= 3.0) {
				attackTarget.startRiding(nehemoth, true);
			}
			if (nehemoth.attacktick == 22) {
				NehemothEntity.this.ejectPassengers();
			}
			NehemothEntity.this.getNavigation().recomputePath();
		}
	}

	private class BiteAttackGoal extends Goal {
		private NehemothEntity nehemoth;
		private LivingEntity attackTarget;

		public BiteAttackGoal(NehemothEntity p_i45837_1_) {
			setFlags((EnumSet) EnumSet.of(Goal.Flag.JUMP, Goal.Flag.LOOK, Goal.Flag.MOVE));
			nehemoth = p_i45837_1_;
		}

		public boolean canUse() {
			attackTarget = nehemoth.getTarget();
			return attackTarget != null && nehemoth.attackID == 0 && NehemothEntity.this.distanceTo(attackTarget) <= 3.0f && NehemothEntity.this.random.nextInt(22) == 0;
		}

		public void start() {
			nehemoth.setAttackID(2);
		}

		public void stop() {
			nehemoth.setAttackID(0);
			attackTarget = null;
			NehemothEntity.this.ejectPassengers();
		}

		public boolean canContinueToUse() {
			return nehemoth.attacktick < 23;
		}

		public void tick() {
			if (nehemoth.attacktick > 6 && attackTarget != null && !this.nehemoth.hasPassenger(attackTarget)) {
				NehemothEntity.this.attacktick = 22;
			}
			NehemothEntity.this.stuckSpeedMultiplier = Vec3.ZERO;
			double dist = nehemoth.distanceTo(attackTarget);
			if (nehemoth.attacktick < 23 && attackTarget.isAlive()) {
				NehemothEntity.this.lookAt(attackTarget, 30.0f, 30.0f);
			}
			if (nehemoth.attacktick == 6 && dist <= 3.0) {
				attackTarget.startRiding(nehemoth, true);
			}
			if (nehemoth.attacktick == 17 && dist <= 3.0) {
				attackTarget.hurt(DamageSource.mobAttack((LivingEntity) nehemoth), (float) nehemoth.getAttributeValue(Attributes.ATTACK_DAMAGE));
			}
			if (nehemoth.attacktick == 22) {
				NehemothEntity.this.ejectPassengers();
			}
			NehemothEntity.this.getNavigation().recomputePath();
		}
	}

	private class SmashGoal extends Goal {
		private NehemothEntity nehemoth;
		private LivingEntity attackTarget;

		public SmashGoal(NehemothEntity p_i45837_1_) {
			setFlags((EnumSet) EnumSet.of(Goal.Flag.JUMP, Goal.Flag.LOOK, Goal.Flag.MOVE));
			nehemoth = p_i45837_1_;
		}

		public boolean canUse() {
			attackTarget = nehemoth.getTarget();
			return attackTarget != null && nehemoth.attackID == 0 && (NehemothEntity.this.distanceTo(attackTarget) > 5.0 || (nehemoth.getY() < attackTarget.getY() + 3.0 && attackTarget.isOnGround())) && NehemothEntity.this.isOnGround() && NehemothEntity.this.random.nextInt(38) == 0;
		}

		public void start() {
			nehemoth.setAttackID(4);
		}

		public void stop() {
			nehemoth.setAttackID(0);
			attackTarget = null;
		}

		public boolean canContinueToUse() {
			return nehemoth.attacktick < 38;
		}

		public void tick() {
			NehemothEntity.this.stuckSpeedMultiplier = Vec3.ZERO;
			if (nehemoth.attacktick > 11 && nehemoth.attacktick < 14 && NehemothEntity.this.isOnGround()) {
				NehemothEntity.this.setShiftKeyDown(false);
				nehemoth.attacktick = 38;
			}
			if (NehemothEntity.this.isOnGround() && nehemoth.attacktick > 9) {
				NehemothEntity.this.setJumping(false);
				NehemothEntity.this.getNavigation().recomputePath();
			}
		}
	}

	private class RoarGoal extends Goal {
		private NehemothEntity nehemoth;
		private LivingEntity attackTarget;

		public RoarGoal(NehemothEntity p_i45837_1_) {
			setFlags((EnumSet) EnumSet.of(Goal.Flag.JUMP, Goal.Flag.LOOK, Goal.Flag.MOVE));
			nehemoth = p_i45837_1_;
		}

		public boolean canUse() {
			attackTarget = nehemoth.getTarget();
			return attackTarget != null && nehemoth.attackID == 0 && NehemothEntity.this.distanceTo(attackTarget) > 9.0 && NehemothEntity.this.isOnGround() && NehemothEntity.this.random.nextInt(120) == 0;
		}

		public void start() {
			nehemoth.setAttackID(3);
		}

		public void stop() {
			nehemoth.setAttackID(0);
			attackTarget = null;
		}

		public boolean canContinueToUse() {
			return nehemoth.attacktick < 25;
		}

		public void tick() {
			NehemothEntity.this.stuckSpeedMultiplier = Vec3.ZERO;
			if (nehemoth.attacktick < 25 && attackTarget.isAlive()) {
				NehemothEntity.this.lookAt(attackTarget, 30.0f, 30.0f);
			}
			NehemothEntity.this.getNavigation().recomputePath();
		}
	}

	private class GuardandRevengeGoal extends Goal {
		private NehemothEntity nehemoth;
		private LivingEntity attackTarget;

		public GuardandRevengeGoal(NehemothEntity p_i45837_1_) {
			setFlags((EnumSet) EnumSet.of(Goal.Flag.JUMP, Goal.Flag.LOOK, Goal.Flag.MOVE));
			nehemoth = p_i45837_1_;
		}

		public boolean canUse() {
			attackTarget = nehemoth.getTarget();
			LivingEntity livingentity = nehemoth.getLastHurtByMob();
			return attackTarget != null && nehemoth.attackID == 0 && NehemothEntity.this.distanceTo(attackTarget) <= 3.0 && NehemothEntity.this.random.nextInt(12) == 0 && nehemoth.getHealth() <= 25.0f && nehemoth.getLastHurtByMob() != null;
		}

		public void start() {
			nehemoth.setAttackID(5);
		}

		public void stop() {
			nehemoth.setAttackID(0);
			attackTarget = null;
		}

		public boolean canContinueToUse() {
			return nehemoth.attacktick < 80;
		}

		public void tick() {
			NehemothEntity.this.stuckSpeedMultiplier = Vec3.ZERO;
			if (nehemoth.attacktick < 80 && attackTarget.isAlive()) {
				NehemothEntity.this.yBodyRot = NehemothEntity.this.yHeadRot;
				NehemothEntity.this.lookAt(attackTarget, 30.0f, 30.0f);
			}
			NehemothEntity.this.getNavigation().recomputePath();
		}
	}

	class DoNothingGoal extends Goal {
		private NehemothEntity nehemoth;
		private LivingEntity attackTarget;

		public DoNothingGoal(NehemothEntity p_i45837_1_) {
			setFlags((EnumSet) EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
			nehemoth = p_i45837_1_;
		}

		public boolean canUse() {
			attackTarget = nehemoth.getTarget();
			BlockPos blockpos = new BlockPos(NehemothEntity.this.getX(), NehemothEntity.this.getEyeY(), NehemothEntity.this.getZ());
			return NehemothEntity.this.level.isDay() && NehemothEntity.this.level.canSeeSky(blockpos) && NehemothEntity.this.getVariant() == 0;
		}

		public void tick() {
			NehemothEntity.this.attackID = 0;
			attackTarget = null;
			NehemothEntity.this.setSilent(true);
			NehemothEntity.this.setInvulnerable(true);
		}
	}

	private class BreathGoal extends Goal {
		private NehemothEntity nehemoth;
		private LivingEntity attackTarget;

		public BreathGoal(NehemothEntity p_i45837_1_) {
			setFlags((EnumSet) EnumSet.of(Goal.Flag.JUMP, Goal.Flag.LOOK, Goal.Flag.MOVE));
			nehemoth = p_i45837_1_;
		}

		public boolean canUse() {
			attackTarget = nehemoth.getTarget();
			return attackTarget != null && nehemoth.attackID == 0 && NehemothEntity.this.distanceTo(attackTarget) > 5.0 && NehemothEntity.this.isOnGround() && NehemothEntity.this.random.nextInt(32) == 0 && NehemothEntity.this.getVariant() == 1;
		}

		public void start() {
			nehemoth.setAttackID(6);
		}

		public void stop() {
			nehemoth.setAttackID(0);
			attackTarget = null;
		}

		public boolean canContinueToUse() {
			return nehemoth.attacktick < 16;
		}

		public void tick() {
			NehemothEntity.this.stuckSpeedMultiplier = Vec3.ZERO;
			if (nehemoth.attacktick < 16 && attackTarget.isAlive()) {
				NehemothEntity.this.lookAt(attackTarget, 30.0f, 30.0f);
			}
			NehemothEntity.this.getNavigation().recomputePath();
		}
	}
}
