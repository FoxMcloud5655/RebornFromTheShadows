package net.sonmok14.fromtheshadows.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.sonmok14.fromtheshadows.utils.registry.EntityRegistry;
import net.sonmok14.fromtheshadows.utils.registry.ParticleRegistry;

public class BreathEntity extends AbstractHurtingProjectile {
	protected int timeInAir;
	protected boolean inAir;
	private int ticksInAir;
	private LivingEntity shooter;

	public BreathEntity(EntityType<? extends BreathEntity> p_i50160_1_, Level p_i50160_2_) {
		super(p_i50160_1_, p_i50160_2_);
	}

	public BreathEntity(Level worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ) {
		super(EntityRegistry.BREATH.get(), shooter, accelX, accelY, accelZ, worldIn);
		this.shooter = shooter;
	}

	protected void defineSynchedData() {}

	public boolean shouldRenderAtSqrDistance(double p_36837_) {
		double d0 = getBoundingBox().getSize() * 4.0;
		if (Double.isNaN(d0)) {
			d0 = 4.0;
		}
		d0 *= 64.0;
		return p_36837_ < d0 * d0;
	}

	public void tick() {
		Entity entity = getOwner();
		if (level.isClientSide || ((entity == null || !entity.isRemoved()) && level.hasChunkAt(blockPosition()))) {
			super.tick();
			if (shouldBurn()) {
				setSecondsOnFire(0);
			}
			HitResult hitresult = ProjectileUtil.getHitResult(this, this::canHitEntity);
			if (hitresult.getType() != HitResult.Type.MISS && !ForgeEventFactory.onProjectileImpact((Projectile) this, hitresult)) {
				onHit(hitresult);
			}
			Vec3 vec3 = getDeltaMovement();
			double d0 = getX() + vec3.x;
			double d2 = getY() + vec3.y;
			double d3 = getZ() + vec3.z;
			checkInsideBlocks();
			ProjectileUtil.rotateTowardsMovement(this, 0.2f);
			float f = getInertia();
			if (isInWater()) {
				for (int i = 0; i < 4; ++i) {
					float f2 = 0.25f;
					level.addParticle((ParticleOptions) ParticleTypes.CAMPFIRE_COSY_SMOKE, d0 - vec3.x * 0.25, d2 - vec3.y * 0.25, d3 - vec3.z * 0.25, vec3.x, vec3.y, vec3.z);
				}
				f = 0.8f;
			}
			ProjectileUtil.rotateTowardsMovement(this, 0.2f);
			if (level.isClientSide) {
				for (int i = 0; i < 15; ++i) {
					Vec3 vec4 = getDeltaMovement();
					setPos(getX() + vec4.x, getY() + vec4.y, getZ() + vec4.z);
					level.addParticle((ParticleOptions) ParticleRegistry.LASER.get(), getX() - vec4.x, getY() - vec4.y + 0.15, getZ() - vec4.z, 0.1, 0.1, 0.1);
				}
			}
			setDeltaMovement(vec3.add(xPower, yPower, zPower).scale((double) f));
			level.addParticle(getTrailParticle(), d0, d2 + 0.5, d3, 0.0, 0.0, 0.0);
			setPos(d0, d2, d3);
		}
		else {
			discard();
		}
	}

	private void roar() {
		if (isAlive()) {
			for (LivingEntity livingentity : level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(4.0))) {
				if (!(livingentity instanceof ArmoredNehemothEntity)) {
					livingentity.hurt(DamageSource.mobAttack(shooter), 6.0f);
				}
			}
			level.gameEvent(this, GameEvent.RAVAGER_ROAR, eyeBlockPosition());
		}
	}

	protected boolean canHitEntity(Entity p_36842_) {
		return super.canHitEntity(p_36842_) && !p_36842_.noPhysics;
	}

	protected boolean shouldBurn() {
		return false;
	}

	protected float getInertia() {
		return 0.95f;
	}

	public boolean isPickable() {
		return false;
	}

	private void smash(int distance) {
		double perpFacing = getYRot() * 0.017453292519943295;
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
					fallingBlockEntity.push(0.0, 0.2 + random.nextGaussian() * 0.15, 0.0);
					level.addFreshEntity(fallingBlockEntity);
				}
			}
		}
	}

	public boolean hurt(DamageSource p_36839_, float p_36840_) {
		if (isInvulnerableTo(p_36839_)) {
			return false;
		}
		markHurt();
		Entity entity = p_36839_.getEntity();
		if (entity != null) {
			Vec3 vec3 = entity.getLookAngle();
			setDeltaMovement(vec3);
			xPower = vec3.x * 0.1;
			yPower = vec3.y * 0.1;
			zPower = vec3.z * 0.1;
			setOwner(entity);
			return true;
		}
		return false;
	}

	public float getBrightness() {
		return 1.0f;
	}

	public Packet<?> getAddEntityPacket() {
		Entity entity = getOwner();
		int i = (entity == null) ? 0 : entity.getId();
		return (Packet<?>) new ClientboundAddEntityPacket(getId(), getUUID(), getX(), getY(), getZ(), getXRot(), getYRot(), getType(), i, new Vec3(xPower, yPower, zPower));
	}

	public void recreateFromPacket(ClientboundAddEntityPacket p_150128_) {
		super.recreateFromPacket(p_150128_);
		double d0 = p_150128_.getXa();
		double d2 = p_150128_.getYa();
		double d3 = p_150128_.getZa();
		double d4 = Math.sqrt(d0 * d0 + d2 * d2 + d3 * d3);
		if (d4 != 0.0) {
			xPower = d0 / d4 * 0.1;
			yPower = d2 / d4 * 0.1;
			zPower = d3 / d4 * 0.1;
		}
	}

	protected void onHit(HitResult p_37218_) {
		super.onHit(p_37218_);
		if (!this.level.isClientSide) {
			playSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 1.5f, 1.0f + random.nextFloat() * 0.1f);
			ScreenShakeEntity.ScreenShake(level, position(), 15.0f, 0.2f, 0, 10);
			smash(3);
			roar();
			discard();
		}
	}

	protected void onHitEntity(EntityHitResult p_37216_) {
		super.onHitEntity(p_37216_);
		if (!this.level.isClientSide) {
			playSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 1.5f, 1.0f + random.nextFloat() * 0.1f);
			Entity entity = p_37216_.getEntity();
			Entity entity2 = getOwner();
			ScreenShakeEntity.ScreenShake(level, position(), 15.0f, 0.2f, 0, 10);
			entity.hurt(DamageSource.MAGIC, 6.0f);
			smash(4);
			roar();
			if (entity2 instanceof LivingEntity livingEntity) {
				doEnchantDamageEffects(livingEntity, entity);
			}
		}
	}
}
