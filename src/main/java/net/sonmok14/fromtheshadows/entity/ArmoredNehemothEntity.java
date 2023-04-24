package net.sonmok14.fromtheshadows.entity;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.sonmok14.fromtheshadows.utils.registry.ItemRegistry;
import net.sonmok14.fromtheshadows.utils.registry.SoundRegistry;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class ArmoredNehemothEntity extends Monster implements Enemy, IAnimatable {
	private static Predicate<Entity> NO_NEHEMOTH_AND_ALIVE;
	public int attackID;
	private int stunnedTick;
	public int attacktick;
	public int armortick;
	public static byte MELEE_ATTACK = 1;
	public static byte SHOT_ATTACK = 2;
	public static byte ROAR_ATTACK = 3;
	public static byte SMASH_ATTACK = 4;
	public static byte DASH_ATTACK = 5;
	private static EntityDataAccessor<Boolean> ARMORED;
	private AnimationFactory factory;

	public ArmoredNehemothEntity(EntityType<? extends ArmoredNehemothEntity> type, Level world) {
		super(type, world);
		factory = new AnimationFactory((IAnimatable) this);
		maxUpStep = 3.0f;
		xpReward = 80;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return LivingEntity.createLivingAttributes().add(Attributes.FOLLOW_RANGE, 25.0).add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.MAX_HEALTH, 60.0).add(Attributes.ATTACK_DAMAGE, 5.0).add(Attributes.ATTACK_KNOCKBACK, 0.0).add(Attributes.KNOCKBACK_RESISTANCE, 9.0).add(Attributes.ARMOR, 8.0).add(Attributes.ARMOR_TOUGHNESS, 5.0).add(Attributes.ATTACK_SPEED, 2.0);
	}

	private <E extends IAnimatable> PlayState predicate2(AnimationEvent<E> event) {
		if (attackID == 1 && isAlive()) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.meleeattack2", Boolean.valueOf(true)));
			return PlayState.CONTINUE;
		}
		return PlayState.STOP;
	}

	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		float f = getBrightness();
		BlockPos blockpos = new BlockPos(getX(), getEyeY(), getZ());
		if (!this.isSilent()) {
			if (event.isMoving() && isAggressive() && attackID == 0) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.run", Boolean.valueOf(true)));
				return PlayState.CONTINUE;
			}
			if (attackID == 1) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.none2", Boolean.valueOf(true)));
				return PlayState.CONTINUE;
			}
			if (attackID == 3) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.roar", Boolean.valueOf(true)));
				return PlayState.CONTINUE;
			}
			if (!this.isImmobile()) {
				if (attackID == 1) {
					event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.meleeattack", Boolean.valueOf(true)));
					return PlayState.CONTINUE;
				}
				if (attackID == 2 && attacktick < 5) {
					event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.bulk", Boolean.valueOf(true)));
					return PlayState.CONTINUE;
				}
				if (attackID == 2) {
					event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.breath", Boolean.valueOf(true)));
					return PlayState.CONTINUE;
				}
			}
			if (attackID == 4 && isOnGround() && attacktick > 8) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.smash", Boolean.valueOf(true)));
				return PlayState.CONTINUE;
			}
			if (!this.isAlive() || getHealth() == 0.1 || isDeadOrDying()) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.death", Boolean.valueOf(false)));
				return PlayState.CONTINUE;
			}
			if (attackID == 4 && !this.isOnGround()) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.jump", Boolean.valueOf(true)));
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
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.stop1", Boolean.valueOf(true)));
			return PlayState.CONTINUE;
		}
		return PlayState.CONTINUE;
	}

	public boolean isArmored() {
		return (boolean) entityData.get((EntityDataAccessor) ArmoredNehemothEntity.ARMORED);
	}

	public void setArmored(boolean p_32759_) {
		entityData.set((EntityDataAccessor) ArmoredNehemothEntity.ARMORED, p_32759_);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define((EntityDataAccessor) ArmoredNehemothEntity.ARMORED, true);
	}

	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController((IAnimatable) this, "controller", 3.0f, this::predicate));
		data.addAnimationController(new AnimationController((IAnimatable) this, "controller2", 8.0f, this::predicate2));
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

	public AnimationFactory getFactory() {
		return factory;
	}

	protected void tickDeath() {
		++this.deathTime;
		if (deathTime == 50) {
			remove(Entity.RemovalReason.KILLED);
			dropExperience();
		}
	}

	protected void registerGoals() {
		goalSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.MeleeAttackGoal(this, 1.2, false));
		goalSelector.addGoal(0, new MeleeAttackGoal(this));
		goalSelector.addGoal(0, new SmashGoal(this));
		goalSelector.addGoal(0, new BreathGoal(this));
		goalSelector.addGoal(0, new FloatGoal(this));
		goalSelector.addGoal(0, new RoarGoal(this));
		goalSelector.addGoal(0, new DoNothingGoal());
		goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0f));
		goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0f));
		targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, Piglin.class, true));
		targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, Axolotl.class, true));
		targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Raider.class, true));
		targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Player.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true, (p_199899_) -> {
			return !p_199899_.isBaby();
		}));
		targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
		targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[] {ArmoredNehemothEntity.class}));
		targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, IronGolem.class, true));
		goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
		goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.5));
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

	public void tick() {
		super.tick();
		if (level.isNight()) {
			setInvulnerable(false);
			setSilent(false);
		}
		if (attackID != 0) {
			++this.attacktick;
		}
		if (getTarget() != null) {
			if (attackID == 4 && isOnGround() && isShiftKeyDown() && attacktick > 8) {
				if (getTarget() instanceof Player player) {
					if (player.isBlocking()) {
						player.disableShield(true);
					}
				}
				setShiftKeyDown(false);
				ScreenShakeEntity.ScreenShake(level, position(), 15.0f, 0.2f, 0, 10);
				smash(4);
				roar();
				playSound(SoundEvents.GENERIC_EXPLODE, 2.0f, 1.0f + getRandom().nextFloat() * 0.1f);
				if (level.isClientSide) {
					BlockState block = level.getBlockState(blockPosition().below());
					for (int j = 0; j < 24; ++j) {
						float f = random.nextFloat() * 12.566371f;
						float f2 = random.nextFloat() * 0.8f + 0.8f;
						float f3 = Mth.sin(f) * 3.0f * 0.8f * f2;
						float f4 = Mth.cos(f) * 3.0f * 0.8f * f2;
						level.addParticle((ParticleOptions) new BlockParticleOption(ParticleTypes.BLOCK, block), getX() + f3, getY(), getZ() + f4, 0.0, 0.0, 0.0);
					}
				}
			}
			if (attackID == 5 && attacktick == 55) {
				roar();
				ScreenShakeEntity.ScreenShake(level, position(), 15.0f, 0.2f, 0, 10);
				playSound((SoundEvent) SoundRegistry.NEHEMOTH_ROAR.get(), 1.5f, 1.0f + getRandom().nextFloat() * 0.1f);
			}
			if (attackID == 2 && attacktick == 1) {
				playSound((SoundEvent) SoundRegistry.WARNING.get(), 1.5f, 0.7f);
			}
			if (attackID == 2 && attacktick == 8) {
				playSound(SoundEvents.FIREWORK_ROCKET_LAUNCH, 2.0f, 0.1f + getRandom().nextFloat() * 0.1f);
				double d1 = 4.0;
				LivingEntity livingEntity = getTarget();
				Vec3 vec3 = getViewVector(1.0f);
				double d2 = livingEntity.getX() - (getX() + vec3.x * 4.0);
				double d3 = livingEntity.getY(0.5) - (0.5 + getY(0.5));
				double d4 = livingEntity.getZ() - (getZ() + vec3.z * 4.0);
				BreathEntity largefireball = new BreathEntity(level, (LivingEntity) this, d2, d3, d4);
				largefireball.setPos(getX() + vec3.x * 0.3, getY(0.52), largefireball.getZ() + vec3.z * 0.1);
				level.addFreshEntity(largefireball);
			}
		}
		if (attackID == 1) {
			getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.24);
		}
		if (attackID != 1) {
			getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.35);
		}
		if (attacktick == 7 && attackID == 1) {
			setYRot(yBodyRot);
			move(MoverType.SELF, new Vec3(Math.cos(Math.toRadians(getYRot() + 90.0f)), 0.0, Math.sin(Math.toRadians(getYRot() + 90.0f))));
		}
		if (attacktick == 28 && attackID == 1) {
			setYRot(yBodyRot);
			move(MoverType.SELF, new Vec3(Math.cos(Math.toRadians(getYRot() + 90.0f)), 0.0, Math.sin(Math.toRadians(getYRot() + 90.0f))));
		}
		if (attacktick == 11 && attackID == 1) {
			meleeattack();
			playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 3.0f, 0.5f + getRandom().nextFloat() * 0.1f);
		}
		if (attacktick == 31 && attackID == 1) {
			meleeattack();
			playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 3.0f, 0.5f + getRandom().nextFloat() * 0.1f);
		}
		if (attackID == 3) {
			roar();
			if (attacktick == 1) {
				playSound((SoundEvent) SoundRegistry.NEHEMOTH_ROAR.get(), 1.5f, 1.0f + getRandom().nextFloat() * 0.1f);
				ScreenShakeEntity.ScreenShake(level, position(), 20.0f, 0.2f, 20, 10);
			}
		}
		float f5 = getBrightness();
		BlockPos blockpos = new BlockPos(getX(), getEyeY(), getZ());
		if (stunnedTick > 0) {
			--this.stunnedTick;
			stunEffect();
		}
		if (!this.level.canSeeSky(blockpos) && isSilent()) {
			setInvulnerable(false);
			setSilent(false);
		}
		if (!this.isArmored()) {
			++this.armortick;
			if (armortick > 200) {
				armortick = 0;
				sphereparticle(2.0f, 5.0f);
				if (!this.level.isClientSide) {
					setArmored(true);
					playSound(SoundEvents.TOTEM_USE, 1.5f, 0.2f + getRandom().nextFloat() * 0.1f);
				}
			}
		}
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

	private void stunEffect() {
		if (random.nextInt(6) == 0) {
			double d0 = getX() - getBbWidth() * Math.sin(yBodyRot * 0.017453292f) + (random.nextDouble() * 0.6 - 0.3);
			double d2 = getY() + getBbHeight() - 0.3;
			double d3 = getZ() + getBbWidth() * Math.cos(yBodyRot * 0.017453292f) + (random.nextDouble() * 0.6 - 0.3);
			level.addParticle((ParticleOptions) ParticleTypes.ENTITY_EFFECT, d0, d2, d3, 0.4980392156862745, 0.5137254901960784, 0.5725490196078431);
		}
	}

	public boolean isAlliedTo(Entity p_32665_) {
		return p_32665_ != null && (p_32665_ == this || super.isAlliedTo(p_32665_) || p_32665_ instanceof NehemothEntity);
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
					fallingBlockEntity.push(0.0, 0.2 + getRandom().nextGaussian() * 0.15, 0.0);
					level.addFreshEntity(fallingBlockEntity);
				}
			}
		}
	}

	private void strongKnockback(Entity p_33340_) {
		double d0 = p_33340_.getX() - getX();
		double d2 = p_33340_.getZ() - getZ();
		double d3 = Math.max(d0 * d0 + d2 * d2, 0.001);
		p_33340_.push(d0 / d3 * 2.0, 0.2, d2 / d3 * 2.0);
	}

	public boolean canBeControlledByRider() {
		return false;
	}

	public boolean isPushable() {
		return false;
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

	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_21434_, DifficultyInstance p_21435_, MobSpawnType p_21436_, @Nullable SpawnGroupData p_21437_, @Nullable CompoundTag p_21438_) {
		return super.finalizeSpawn(p_21434_, p_21435_, p_21436_, p_21437_, p_21438_);
	}

	protected int calculateFallDamage(float p_21237_, float p_21238_) {
		return 0;
	}

	private void meleeattack() {
		float range = 3.0f;
		float arc = 60.0f;
		List<LivingEntity> entitiesHit = level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(3.0), ArmoredNehemothEntity.NO_NEHEMOTH_AND_ALIVE);
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
			if (entityHitDistance <= range && entityRelativeAngle <= arc / 2.0f && entityRelativeAngle >= -arc / 2.0f && entityRelativeAngle >= 360.0f - arc / 2.0f == entityRelativeAngle <= -360.0f + arc / 2.0f && !(entityHit instanceof ArmoredNehemothEntity)) {
				entityHit.invulnerableTime = 0;
				entityHit.hurt(DamageSource.mobAttack((LivingEntity) this), (float) getAttributeValue(Attributes.ATTACK_DAMAGE));
			}
		}
	}

	public void positionRider(Entity passenger) {
		if (hasPassenger(passenger)) {
			float radius = 0.5f;
			float angle = 0.017453292f * yBodyRot;
			double extraX = radius * Mth.sin((float) (3.141592653589792 + angle));
			double extraZ = radius * Mth.cos(angle);
			passenger.setPos(getX() + extraX, getY() - 0.17000000178813934, getZ() + extraZ);
		}
	}

	public void onSyncedDataUpdated(EntityDataAccessor<?> p_21104_) {
		super.onSyncedDataUpdated((EntityDataAccessor) p_21104_);
	}

	private void roar() {
		if (isAlive()) {
			List<LivingEntity> entityList = level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(3.5), ArmoredNehemothEntity.NO_NEHEMOTH_AND_ALIVE);
			for (LivingEntity livingentity : entityList) {
				if (!(livingentity instanceof ArmoredNehemothEntity)) {
					livingentity.hurt(DamageSource.mobAttack((LivingEntity) this), 6.0f);
				}
				strongKnockback(livingentity);
			}
			level.gameEvent(this, GameEvent.RAVAGER_ROAR, eyeBlockPosition());
		}
	}

	public static boolean canNehemothSpawnInLight(EntityType<? extends ArmoredNehemothEntity> p_223325_0_, ServerLevelAccessor p_223325_1_, MobSpawnType p_223325_2_, BlockPos p_223325_3_, Random p_223325_4_) {
		return checkMobSpawnRules(p_223325_0_, (LevelAccessor) p_223325_1_, p_223325_2_, p_223325_3_, p_223325_4_);
	}

	public static <T extends Mob> boolean canNehemothSpawn(EntityType<ArmoredNehemothEntity> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, Random random) {
		BlockState blockstate = iServerWorld.getBlockState(pos.below());
		return reason == MobSpawnType.SPAWNER || (!iServerWorld.canSeeSky(pos) && pos.getY() <= 64 && canNehemothSpawnInLight(entityType, iServerWorld, reason, pos, random));
	}

	@Nullable
	protected SoundEvent getAmbientSound() {
		return SoundEvents.SCULK_CLICKING;
	}

	protected SoundEvent getHurtSound(DamageSource p_33034_) {
		return SoundEvents.RAVAGER_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.RAVAGER_DEATH;
	}

	protected void playStepSound(BlockPos p_33350_, BlockState p_33351_) {
		playSound(SoundEvents.RAVAGER_STEP, 0.3f, 0.1f);
	}

	public float getVoicePitch() {
		return 0.1f;
	}

	public boolean hurt(DamageSource p_21016_, float p_21017_) {
		if (p_21016_.isProjectile()) {
			p_21017_ = Math.min(1.0f, p_21017_);
		}
		if (attackID == 5 && attacktick < 55) {
			return false;
		}
		if (isArmored() && attackID == 2 && attacktick < 13 && p_21017_ > 0.0f) {
			if (level.isClientSide) {
				sphereparticle(2.0f, 5.0f);
				p_21017_ = Math.min(3.0f, p_21017_);
				return true;
			}
			setArmored(false);
			playSound(SoundEvents.TOTEM_USE, 1.5f, 0.2f + getRandom().nextFloat() * 0.1f);
		}
		return (!this.isSilent() || p_21016_ == DamageSource.OUT_OF_WORLD) && (!this.isArmored() || p_21016_ == DamageSource.OUT_OF_WORLD || (attackID == 2 && attacktick < 11)) && super.hurt(p_21016_, p_21017_);
	}

	private void sphereparticle(float height, float size) {
		double d0 = getX();
		double d2 = getY() + height;
		double d3 = getZ();
		for (float i = -size; i <= size; ++i) {
			for (float j = -size; j <= size; ++j) {
				for (float k = -size; k <= size; ++k) {
					double d4 = j + (random.nextDouble() - random.nextDouble()) * 0.5;
					double d5 = i + (random.nextDouble() - random.nextDouble()) * 0.5;
					double d6 = k + (random.nextDouble() - random.nextDouble()) * 0.5;
					double d7 = Mth.sqrt((float) (d4 * d4 + d5 * d5 + d6 * d6)) / 0.5 + random.nextGaussian() * 0.05;
					level.addParticle((ParticleOptions) ParticleTypes.FIREWORK, d0, d2, d3, d4 / d7, d5 / d7, d6 / d7);
					if (i != -size && i != size && j != -size && j != size) {
						k += size * 2.0f - 1.0f;
					}
				}
			}
		}
	}

	public static boolean checkMonsterSpawnRules(EntityType<? extends Monster> p_33018_, ServerLevelAccessor p_33019_, MobSpawnType p_33020_, BlockPos p_33021_, Random p_33022_) {
		return p_33019_.getDifficulty() != Difficulty.PEACEFUL && checkMobSpawnRules(p_33018_, (LevelAccessor) p_33019_, p_33020_, p_33021_, p_33022_);
	}

	static {
		NO_NEHEMOTH_AND_ALIVE = (p_33346_ -> p_33346_.isAlive() && !(p_33346_ instanceof ArmoredNehemothEntity));
		ARMORED = SynchedEntityData.defineId(ArmoredNehemothEntity.class, EntityDataSerializers.BOOLEAN);
	}

	private class MeleeAttackGoal extends Goal {
		private ArmoredNehemothEntity nehemoth;
		private LivingEntity attackTarget;

		public MeleeAttackGoal(ArmoredNehemothEntity p_i45837_1_) {
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
			return nehemoth.attacktick < 37;
		}

		public void tick() {
			if (nehemoth.attacktick < 37 && attackTarget.isAlive()) {
				nehemoth.getLookControl().setLookAt(attackTarget, 30.0f, 30.0f);
			}
			double dist = nehemoth.distanceTo(attackTarget);
		}
	}

	private class SmashGoal extends Goal {
		private ArmoredNehemothEntity nehemoth;
		private LivingEntity attackTarget;

		public SmashGoal(ArmoredNehemothEntity p_i45837_1_) {
			setFlags((EnumSet) EnumSet.of(Goal.Flag.JUMP, Goal.Flag.LOOK, Goal.Flag.MOVE));
			nehemoth = p_i45837_1_;
		}

		public boolean canUse() {
			attackTarget = nehemoth.getTarget();
			return attackTarget != null && nehemoth.attackID == 0 && (ArmoredNehemothEntity.this.distanceTo(attackTarget) > 5.0 || (nehemoth.getY() < attackTarget.getY() + 3.0 && attackTarget.isOnGround())) && ArmoredNehemothEntity.this.isOnGround() && ArmoredNehemothEntity.this.random.nextInt(38) == 0;
		}

		public void start() {
			nehemoth.setAttackID(4);
		}

		public void stop() {
			nehemoth.setAttackID(0);
			attackTarget = null;
		}

		public boolean canContinueToUse() {
			return nehemoth.attacktick < 31;
		}

		public void tick() {
			ArmoredNehemothEntity.this.stuckSpeedMultiplier = Vec3.ZERO;
			double dist = nehemoth.distanceTo(attackTarget);
			if (nehemoth.attacktick < 31 && attackTarget.isAlive()) {
				ArmoredNehemothEntity.this.yBodyRot = ArmoredNehemothEntity.this.yHeadRot;
				nehemoth.getLookControl().setLookAt(attackTarget, 30.0f, 30.0f);
			}
			if (nehemoth.attacktick == 2) {
				ArmoredNehemothEntity.this.setJumping(true);
				ArmoredNehemothEntity.this.setShiftKeyDown(true);
				if (nehemoth.getY() < attackTarget.getY() + 3.0 && attackTarget.isOnGround()) {
					ArmoredNehemothEntity.this.setDeltaMovement((attackTarget.getX() - ArmoredNehemothEntity.this.getX()) * 0.2, 1.1, (attackTarget.getZ() - ArmoredNehemothEntity.this.getZ()) * 0.2);
				}
				if (nehemoth.getY() >= attackTarget.getY()) {
					ArmoredNehemothEntity.this.setDeltaMovement((attackTarget.getX() - ArmoredNehemothEntity.this.getX()) * 0.2, 0.8, (attackTarget.getZ() - ArmoredNehemothEntity.this.getZ()) * 0.2);
				}
			}
			if (nehemoth.attacktick > 2 && nehemoth.attacktick < 7 && ArmoredNehemothEntity.this.isOnGround()) {
				ArmoredNehemothEntity.this.setShiftKeyDown(false);
				nehemoth.attacktick = 31;
			}
			if (ArmoredNehemothEntity.this.isOnGround() && nehemoth.attacktick > 2) {
				ArmoredNehemothEntity.this.setJumping(false);
			}
		}
	}

	private class RoarGoal extends Goal {
		private ArmoredNehemothEntity nehemoth;
		private LivingEntity attackTarget;

		public RoarGoal(ArmoredNehemothEntity p_i45837_1_) {
			setFlags((EnumSet) EnumSet.of(Goal.Flag.JUMP, Goal.Flag.LOOK, Goal.Flag.MOVE));
			nehemoth = p_i45837_1_;
		}

		public boolean canUse() {
			attackTarget = nehemoth.getTarget();
			return attackTarget != null && nehemoth.attackID == 0 && ArmoredNehemothEntity.this.distanceTo(attackTarget) > 9.0 && ArmoredNehemothEntity.this.isOnGround() && ArmoredNehemothEntity.this.random.nextInt(52) == 0;
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
			ArmoredNehemothEntity.this.stuckSpeedMultiplier = Vec3.ZERO;
			double dist = nehemoth.distanceTo(attackTarget);
			if (nehemoth.attacktick < 25 && attackTarget.isAlive()) {
				nehemoth.getLookControl().setLookAt(attackTarget, 30.0f, 30.0f);
			}
		}
	}

	private class BreathGoal extends Goal {
		private ArmoredNehemothEntity nehemoth;
		private LivingEntity attackTarget;

		public BreathGoal(ArmoredNehemothEntity p_i45837_1_) {
			setFlags((EnumSet) EnumSet.of(Goal.Flag.JUMP, Goal.Flag.LOOK, Goal.Flag.MOVE));
			nehemoth = p_i45837_1_;
		}

		public boolean canUse() {
			attackTarget = nehemoth.getTarget();
			return attackTarget != null && nehemoth.attackID == 0 && ArmoredNehemothEntity.this.distanceTo(attackTarget) > 5.0 && ArmoredNehemothEntity.this.isOnGround() && ArmoredNehemothEntity.this.random.nextInt(32) == 0 && ArmoredNehemothEntity.this.isArmored();
		}

		public void start() {
			nehemoth.setAttackID(2);
		}

		public void stop() {
			nehemoth.setAttackID(0);
			attackTarget = null;
		}

		public boolean canContinueToUse() {
			return nehemoth.attacktick < 16;
		}

		public void tick() {
			ArmoredNehemothEntity.this.stuckSpeedMultiplier = Vec3.ZERO;
			double dist = nehemoth.distanceTo(attackTarget);
			if (nehemoth.attacktick < 16 && attackTarget.isAlive()) {
				nehemoth.getLookControl().setLookAt(attackTarget, 30.0f, 30.0f);
			}
		}
	}

	class DoNothingGoal extends Goal {
		public DoNothingGoal() {
			setFlags((EnumSet) EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
		}

		public boolean canUse() {
			float f = ArmoredNehemothEntity.this.getBrightness();
			BlockPos blockpos = new BlockPos(ArmoredNehemothEntity.this.getX(), ArmoredNehemothEntity.this.getEyeY(), ArmoredNehemothEntity.this.getZ());
			return ArmoredNehemothEntity.this.level.isDay() && ArmoredNehemothEntity.this.level.canSeeSky(blockpos);
		}

		public void tick() {
			ArmoredNehemothEntity.this.setSilent(true);
			ArmoredNehemothEntity.this.setInvulnerable(true);
		}
	}
}
