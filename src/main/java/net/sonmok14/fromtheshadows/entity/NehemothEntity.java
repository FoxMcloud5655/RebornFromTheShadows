package net.sonmok14.fromtheshadows.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.sonmok14.fromtheshadows.utils.registry.ItemRegistry;
import net.sonmok14.fromtheshadows.utils.registry.SoundRegistry;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class NehemothEntity extends Monster implements Enemy, IAnimatable {
    private static final Predicate<Entity> NO_NEHEMOTH_AND_ALIVE = (p_33346_) -> {
        return p_33346_.isAlive() && !(p_33346_ instanceof NehemothEntity);
    };
    public int attackID;
    private int stunnedTick;
    public int attacktick;
    public static final byte MELEE_ATTACK = 1;
    public static final byte BITE_ATTACK = 2;
    public static final byte ROAR_ATTACK = 3;
    public static final byte SMASH_ATTACK = 4;
    public static final byte GUARD_ATTACK = 5;

    public NehemothEntity(EntityType<? extends NehemothEntity> type, Level world) {
        super(type, world);
        maxUpStep = 3;
        xpReward = 30;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.FOLLOW_RANGE, 25.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.ATTACK_DAMAGE, 10D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 9.0D)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 5.0D)
                .add(Attributes.ATTACK_SPEED, 2.0D);
    }


    private AnimationFactory factory = new AnimationFactory(this);

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        float f = getBrightness();
        BlockPos blockpos = new BlockPos(getX(), getEyeY(), getZ());
            if (!isSilent()) {
                if (event.isMoving() && isAggressive() && attackID == 0) {
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.run", true));
                    return PlayState.CONTINUE;
                }
                if (attackID == 3) {
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.roar", true));
                    return PlayState.CONTINUE;
                }
                if (!this.isImmobile()) {
                    if (attackID == 1) {
                        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.meleeattack", true));
                        return PlayState.CONTINUE;
                    }

                    if (attackID == 2) {
                        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.attack", true));
                        return PlayState.CONTINUE;
                    }
                }
                if (attackID == 4 && isOnGround() && this.attacktick > 8) {
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.smash", true));
                    return PlayState.CONTINUE;
                }

                if (!isAlive() || getHealth() == 0.1 || isDeadOrDying()) {
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.death", false));
                    return PlayState.CONTINUE;
                }

                if (attackID == 4 && !isOnGround()) {
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.jump", true));
                    return PlayState.CONTINUE;
                }

                if (attackID == 5 && attacktick < 55) {
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.guard", true));
                    return PlayState.CONTINUE;
                }
                if (attackID == 5 && attacktick > 55) {
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.bulk", true));
                    return PlayState.CONTINUE;
                }

                if (isImmobile() && isAlive()) {
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.stun", true));
                    return PlayState.CONTINUE;
                }

                if (event.isMoving()) {
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.walk", true));
                    return PlayState.CONTINUE;
                }

                if (!event.isMoving() && attackID == 0) {
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.ldle", true));
                    return PlayState.CONTINUE;
                }
            }

        else if(isSilent()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.stop1", true));
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;
    }



    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<NehemothEntity>(this, "controller", 3, this::predicate));
    }

    @Override
    public boolean doHurtTarget(Entity p_85031_1_) {
        if (!this.level.isClientSide && this.attackID == 0) {
            if (this.random.nextInt(4) != 0) {
                this.attackID = MELEE_ATTACK;
            } else {
                this.attackID = BITE_ATTACK;
            }
        }
        return true;
    }


    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 50) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience();
        }
    }

//animation


    //ai

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.MeleeAttackGoal(this, 1.2, false));
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this));
        this.goalSelector.addGoal(0, new BiteAttackGoal(this));
        this.goalSelector.addGoal(0, new SmashGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new RoarGoal(this));
        this.goalSelector.addGoal(0, new DoNothingGoal());
        this.goalSelector.addGoal(0, new GuardandRevengeGoal(this));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Piglin.class, true));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Axolotl.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Raider.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true, (p_199899_) -> {
            return !p_199899_.isBaby();
        }));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this).setAlertOthers(NehemothEntity.class)));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.5D));
    }
    public void setAttackID(int id) {
        this.attackID = id;
        this.attacktick = 0;
        this.level.broadcastEntityEvent(this, (byte) -id);
    }



    @Override
    public void handleEntityEvent(byte id) {
        if (id <= 0) {
            this.attackID = Math.abs(id);
            this.attacktick = 0;
        } else if (id == 39) {
            this.stunnedTick = 40;
        }else {
            super.handleEntityEvent(id);
        }
    }

    //ai


    //tick

    @Override
    public void tick() {
        super.tick();
        if(level.isNight())
        {
            setSilent(false);
        }
        ++this.tickCount;
        if (this.attackID != 0) {
            ++this.attacktick;
        }
        if(this.getTarget() != null ) {
            if (this.attacktick == 15 && this.attackID == 2 && this.hasPassenger(getTarget())) {
                getTarget().hurt(DamageSource.mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                if (this.attackID == 2 && !getTarget().isBlocking()) {
                    this.level.playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.STRIDER_EAT, this.getSoundSource(), 2F, 0.3F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
                    heal((float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                }



                if (this.attackID == 2 && getTarget().isBlocking() && this.random.nextDouble() < 0.5D){
                    blockedByShield(this);
                    ejectPassengers();
                    attacktick = 19;
                }
            }
            if(this.attackID == 4 && isOnGround() && isShiftKeyDown() && this.attacktick > 8)
            {
                if (getTarget() instanceof Player) {
                    Player player = (Player) getTarget();
                    if (player.isBlocking())
                        player.disableShield(true);
                }
                setShiftKeyDown(false);
                ScreenShakeEntity.ScreenShake(level, this.position(), 15, 0.2f, 0, 10);
                roar();
                this.playSound(SoundEvents.GENERIC_EXPLODE, 2f, 1F + this.getRandom().nextFloat() * 0.1F);
                if (this.level.isClientSide) {
                    BlockState block = level.getBlockState(blockPosition().below());
                    for(int j = 0; j < 3 * 8; ++j) {
                        float f = this.random.nextFloat() * ((float)Math.PI * 4F);
                        float f1 = this.random.nextFloat() * 0.8F + 0.8F;
                        float f2 = Mth.sin(f) * (float)3 * 0.8F * f1;
                        float f3 = Mth.cos(f) * (float)3 * 0.8F * f1;
                        this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, block), this.getX() + (double)f2, this.getY(), this.getZ() + (double)f3, 0.0D, 0.0D, 0.0D);
                    }
                    }
                }

            if (this.attacktick == 2) {

            }
            if(attackID == 5 && this.attacktick == 55)
            {
                roar();
                ScreenShakeEntity.ScreenShake(level, this.position(), 15, 0.2f, 0, 10);
                this.playSound(SoundRegistry.NEHEMOTH_ROAR.get(), 1.5f, 1F + this.getRandom().nextFloat() * 0.1F);
            }
            }

            if (this.attacktick == 10 && this.attackID == 1) {
                meleeattack();
            }
            if(this.attackID == 3)
            {
                roar();
                if(this.attacktick == 1)
                {
                    this.playSound(SoundRegistry.NEHEMOTH_ROAR.get(), 1.5f, 1F + this.getRandom().nextFloat() * 0.1F);
                    ScreenShakeEntity.ScreenShake(level, this.position(), 20, 0.2f, 20, 10);
                }
            }
            float f = getBrightness();
            BlockPos blockpos = new BlockPos(getX(), getEyeY(), getZ());


        if (this.stunnedTick > 0) {
            --this.stunnedTick;
            this.stunEffect();
        }

        if(!level.canSeeSky(blockpos) &&isSilent())
        {
            setSilent(false);
        }

        }

    protected boolean isImmobile() {
        return super.isImmobile() || this.stunnedTick > 0;
    }

    public boolean hasLineOfSight(Entity p_149755_) {
        return this.stunnedTick <= 0 ? super.hasLineOfSight(p_149755_) : false;
    }

    public int getStunnedTick() {
        return this.stunnedTick;
    }


    private void stunEffect() {
        if (this.random.nextInt(6) == 0) {
            double d0 = this.getX() - (double) this.getBbWidth() * Math.sin((double) (this.yBodyRot * ((float) Math.PI / 180F))) + (this.random.nextDouble() * 0.6D - 0.3D);
            double d1 = this.getY() + (double) this.getBbHeight() - 0.3D;
            double d2 = this.getZ() + (double) this.getBbWidth() * Math.cos((double) (this.yBodyRot * ((float) Math.PI / 180F))) + (this.random.nextDouble() * 0.6D - 0.3D);
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, d0, d1, d2, 0.4980392156862745D, 0.5137254901960784D, 0.5725490196078431D);
        }
    }

    private void strongKnockback(Entity p_33340_) {
        double d0 = p_33340_.getX() - this.getX();
        double d1 = p_33340_.getZ() - this.getZ();
        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
        p_33340_.push(d0 / d2 * 2.0D, 0.2D, d1 / d2 * 2.0D);
    }

    public boolean canBeControlledByRider() {
        return false;
    }


    @Override
    public boolean isPushable() {
        return false;
    }

    protected void blockedByShield(LivingEntity p_33361_) {
            if (this.random.nextDouble() < 0.5D && this.attackID == 2) {
                this.stunnedTick = 40;
                this.level.broadcastEntityEvent(this, (byte)39);
                p_33361_.push(this);
            }

            p_33361_.hurtMarked = true;

    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        if(itemstack.is(Items.GLASS_BOTTLE) && this.getHealth() < 10 && !this.isSilent())
        {

            boolean flag = this.hurt(DamageSource.mobAttack(player), 2);
            this.hurt(DamageSource.MAGIC, (float) 2);
            if(flag)
            {
                itemstack.shrink(1);
                player.playSound(SoundEvents.BOTTLE_FILL, 0.1F, 1.0F);
                player.getTicksUsingItem();
                if (itemstack.isEmpty()) {
                    player.setItemInHand(hand, new ItemStack(ItemRegistry.BOTTLE_OF_BLOOD.get()));
                } else if (!player.getInventory().add(new ItemStack(ItemRegistry.BOTTLE_OF_BLOOD.get()))) {

                    player.drop(new ItemStack(ItemRegistry.BOTTLE_OF_BLOOD.get()), false);
                }
            }
        }
        else
        {
            return InteractionResult.FAIL;
        }

            return super.mobInteract(player, hand);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_21434_, DifficultyInstance p_21435_, MobSpawnType p_21436_, @Nullable SpawnGroupData p_21437_, @Nullable CompoundTag p_21438_) {
        return super.finalizeSpawn(p_21434_, p_21435_, p_21436_, p_21437_, p_21438_);
    }

    @Override
    protected int calculateFallDamage(float p_21237_, float p_21238_) {
        return 0;
    }

    private void meleeattack() {
        float range = 3f;
        float arc = 60;
        List<LivingEntity> entitiesHit = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(3.0D), NO_NEHEMOTH_AND_ALIVE);
        for (LivingEntity entityHit : entitiesHit) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - this.getZ(), entityHit.getX() - this.getX()) * (180 / Math.PI) - 90) % 360);
            float entityAttackingAngle = this.yHeadRot % 360;
            if (entityHitAngle < 0) {
                entityHitAngle += 360;
            }
            if (entityAttackingAngle < 0) {
                entityAttackingAngle += 360;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - this.getZ()) * (entityHit.getZ() - this.getZ()) + (entityHit.getX() - this.getX()) * (entityHit.getX() - this.getX()));
            if (entityHitDistance <= range && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) && (entityRelativeAngle >= 360 - arc / 2 == entityRelativeAngle <= -360 + arc / 2)) {
                if (!(entityHit instanceof NehemothEntity)) {
                    entityHit.invulnerableTime = 0;
                    entityHit.hurt(DamageSource.mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                }

            }
        }
    }

    public void positionRider(Entity passenger) {

        if (this.hasPassenger(passenger)) {
            float radius = 0.5F;
            float angle = 0.017453292F * this.yBodyRot;
            double extraX = (double)(radius * Mth.sin((float)(3.141592653589792D + (double)angle)));
            double extraZ = (double)(radius * Mth.cos(angle));
            passenger.setPos(this.getX() + extraX, this.getY() - 0.17000000178813934D, this.getZ() + extraZ);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> p_21104_) {
        super.onSyncedDataUpdated(p_21104_);
    }



    private void roar() {
        if (this.isAlive()) {
            for(LivingEntity livingentity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(3.5D), NO_NEHEMOTH_AND_ALIVE)) {
                if (!(livingentity instanceof NehemothEntity)) {
                    livingentity.hurt(DamageSource.mobAttack(this), 6.0F);
                }

                this.strongKnockback(livingentity);
            }


            this.level.gameEvent(this, GameEvent.RAVAGER_ROAR, this.eyeBlockPosition());
        }

    }
    public static boolean canNehemothSpawnInLight(EntityType<? extends NehemothEntity> p_223325_0_, ServerLevelAccessor p_223325_1_, MobSpawnType p_223325_2_, BlockPos p_223325_3_, Random p_223325_4_) {
        return checkMobSpawnRules(p_223325_0_, p_223325_1_, p_223325_2_, p_223325_3_, p_223325_4_);
    }

    public static <T extends Mob> boolean canNehemothSpawn(EntityType<NehemothEntity> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, Random random) {
        BlockState blockstate = iServerWorld.getBlockState(pos.below());
        return reason == MobSpawnType.SPAWNER || !iServerWorld.canSeeSky(pos) && pos.getY() <= 64 && canNehemothSpawnInLight(entityType, iServerWorld, reason, pos, random);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SCULK_CLICKING;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return SoundEvents.RAVAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.RAVAGER_DEATH;
    }

    protected void playStepSound(BlockPos p_33350_, BlockState p_33351_) {
        this.playSound(SoundEvents.RAVAGER_STEP, 0.3F, 0.1F);
    }

    @Override
    public float getVoicePitch() {
        return 0.1F;
    }

    @Override
    public boolean hurt(DamageSource p_21016_, float p_21017_) {
        if(p_21016_.isProjectile())
        {
            p_21017_ = Math.min(1.0F, p_21017_);
        }
        if(this.attackID == 5 && this.attacktick < 55)
        {
            return false;
        }

        if(this.isSilent() && p_21016_ != DamageSource.OUT_OF_WORLD) {
            return false;
        }

        return super.hurt(p_21016_, p_21017_);
    }


    //tick

    //advanced ai

    private class MeleeAttackGoal extends Goal {
        private final NehemothEntity nehemoth;
        private LivingEntity attackTarget;

        public MeleeAttackGoal(NehemothEntity p_i45837_1_) {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
            this.nehemoth = p_i45837_1_;
        }

        public boolean canUse() {
            this.attackTarget = this.nehemoth.getTarget();
            return attackTarget != null && this.nehemoth.attackID == 1;
        }

        public void start() {
            this.nehemoth.setAttackID(1);
        }

        public void stop() {
            this.nehemoth.setAttackID(0);
            this.attackTarget = null;
        }


        @Override
        public boolean canContinueToUse() {
            return nehemoth.attacktick < 23;
        }


        public void tick() {
            double dist = (double) this.nehemoth.distanceTo(attackTarget);
            stuckSpeedMultiplier = Vec3.ZERO;
            if (nehemoth.attacktick < 23 && attackTarget.isAlive()) {
                nehemoth.getLookControl().setLookAt(attackTarget, 30.0F, 30.0F);
            }

            if (nehemoth.attacktick == 11) {
                meleeattack();
            }

            if (nehemoth.attacktick == 20) {
               meleeattack();
            }

        }
    }

    private class BiteAttackGoal extends Goal {
        private final NehemothEntity nehemoth;
        private LivingEntity attackTarget;

        public BiteAttackGoal(NehemothEntity p_i45837_1_) {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
            this.nehemoth = p_i45837_1_;
        }

        public boolean canUse() {
            this.attackTarget = this.nehemoth.getTarget();
            return attackTarget != null && this.nehemoth.attackID == 2;
        }

        public void start() {
            this.nehemoth.setAttackID(2);
        }

        public void stop() {
            this.nehemoth.setAttackID(0);
            this.attackTarget = null;
        }


        @Override
        public boolean canContinueToUse() {
            return nehemoth.attacktick < 19;
        }

        public void tick() {
            stuckSpeedMultiplier = Vec3.ZERO;
            double dist = (double) this.nehemoth.distanceTo(attackTarget);
            if (nehemoth.attacktick < 19 && attackTarget.isAlive()) {
                nehemoth.getLookControl().setLookAt(attackTarget, 30.0F, 30.0F);
            }
            if (nehemoth.attacktick == 6 && dist <= 3) {

                attackTarget.startRiding(nehemoth, true);
            }

            if (nehemoth.attacktick == 17 && dist <= 3) {
                attackTarget.hurt(DamageSource.mobAttack(nehemoth), (float) nehemoth.getAttributeValue(Attributes.ATTACK_DAMAGE));
            }

            if (nehemoth.attacktick == 18) {
                ejectPassengers();
            }

        }
    }

    private class SmashGoal extends Goal {
        private final NehemothEntity nehemoth;
        private LivingEntity attackTarget;

        public SmashGoal(NehemothEntity p_i45837_1_) {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
            this.nehemoth = p_i45837_1_;
        }

        public boolean canUse() {
            this.attackTarget = this.nehemoth.getTarget();
            return attackTarget != null && this.nehemoth.attackID == 0 && (distanceTo(attackTarget) > 5.0D || (nehemoth.getY() < attackTarget.getY() + 3.0D && attackTarget.isOnGround())) && isOnGround() && random.nextInt(38) == 0;
        }

        public void start() {
            this.nehemoth.setAttackID(SMASH_ATTACK);
        }

        public void stop() {
            this.nehemoth.setAttackID(0);
            this.attackTarget = null;
        }

        @Override
        public boolean canContinueToUse() {
            return nehemoth.attacktick < 31;
        }

        public void tick() {
            stuckSpeedMultiplier = Vec3.ZERO;
            double dist = (double) this.nehemoth.distanceTo(attackTarget);
            if (nehemoth.attacktick < 31 && attackTarget.isAlive()) {
                yBodyRot = yHeadRot;
                nehemoth.getLookControl().setLookAt(attackTarget, 30.0F, 30.0F);
            }

            if (nehemoth.attacktick == 2) {
                setJumping(true);
                setShiftKeyDown(true);
                if(nehemoth.getY() < attackTarget.getY() + 3.0D && attackTarget.isOnGround())
                {
                    setDeltaMovement((attackTarget.getX() - getX()) * 0.2D, 1.1D, (attackTarget.getZ() - getZ()) * 0.2D);
                }
                if(nehemoth.getY() >= attackTarget.getY())
                {
                    setDeltaMovement((attackTarget.getX() - getX()) * 0.2D, 0.8D, (attackTarget.getZ() - getZ()) * 0.2D);
                }
            }
            if(nehemoth.attacktick > 2 && nehemoth.attacktick < 7 && isOnGround())
            {
                setShiftKeyDown(false);
                nehemoth.attacktick = 31;
            }

            if (isOnGround() && nehemoth.attacktick > 2) {
                setJumping(false);




            }


        }
    }

    private class RoarGoal extends Goal {
        private final NehemothEntity nehemoth;
        private LivingEntity attackTarget;

        public RoarGoal(NehemothEntity p_i45837_1_) {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
            this.nehemoth = p_i45837_1_;
        }

        public boolean canUse() {
            this.attackTarget = this.nehemoth.getTarget();
            return attackTarget != null && this.nehemoth.attackID == 0 && distanceTo(attackTarget)  > 9.0D && isOnGround() && random.nextInt(52) == 0;
        }

        public void start() {
            this.nehemoth.setAttackID(ROAR_ATTACK);
        }

        public void stop() {
            this.nehemoth.setAttackID(0);
            this.attackTarget = null;
        }


        @Override
        public boolean canContinueToUse() {
            return nehemoth.attacktick < 25;
        }

        public void tick() {
            stuckSpeedMultiplier = Vec3.ZERO;
            double dist = (double) this.nehemoth.distanceTo(attackTarget);
            if (nehemoth.attacktick < 25 && attackTarget.isAlive()) {
                nehemoth.getLookControl().setLookAt(attackTarget, 30.0F, 30.0F);
            }
        }
    }



    private class GuardandRevengeGoal extends Goal {
        private final NehemothEntity nehemoth;
        private LivingEntity attackTarget;

        public GuardandRevengeGoal(NehemothEntity p_i45837_1_) {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
            this.nehemoth = p_i45837_1_;
        }
        public boolean canUse() {
            this.attackTarget = this.nehemoth.getTarget();
            LivingEntity livingentity = this.nehemoth.getLastHurtByMob();
            return attackTarget != null && this.nehemoth.attackID == 0 && distanceTo(attackTarget) <= 4.0D && isOnGround() && random.nextInt(22) == 0 && nehemoth.getHealth() <= 25 && nehemoth.getLastHurtByMob() != null;
        }

        public void start() {
            this.nehemoth.setAttackID(GUARD_ATTACK);
        }

        public void stop() {
            this.nehemoth.setAttackID(0);
            this.attackTarget = null;
        }

        @Override
        public boolean canContinueToUse() {
            return nehemoth.attacktick < 80;
        }

        public void tick() {
            stuckSpeedMultiplier = Vec3.ZERO;
            double dist = (double) this.nehemoth.distanceTo(attackTarget);
            if (nehemoth.attacktick < 80 && attackTarget.isAlive()) {
                yBodyRot = yHeadRot;
                nehemoth.getLookControl().setLookAt(attackTarget, 30.0F, 30.0F);
            }




            }


        }



    class DoNothingGoal extends Goal {
        public DoNothingGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
        }
        public boolean canUse() {
            float f = getBrightness();
            BlockPos blockpos = new BlockPos(getX(), getEyeY(), getZ());
            return (level.isDay() && level.canSeeSky(blockpos));
        }

        @Override
        public void tick() {
            setSilent(true);
        }
    }
    }
