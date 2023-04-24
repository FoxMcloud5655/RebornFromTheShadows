package net.sonmok14.fromtheshadows.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.sonmok14.fromtheshadows.utils.registry.EntityRegistry;

public class ScreenShakeEntity extends Entity {
	private static EntityDataAccessor<Float> RADIUS;
	private static EntityDataAccessor<Float> MAGNITUDE;
	private static EntityDataAccessor<Integer> DURATION;
	private static EntityDataAccessor<Integer> FADE_DURATION;

	public ScreenShakeEntity(EntityType<?> type, Level world) {
		super(type, world);
	}

	public ScreenShakeEntity(Level world, Vec3 position, float radius, float magnitude, int duration, int fadeDuration) {
		super(EntityRegistry.SCREEN_SHAKE.get(), world);
		setRadius(radius);
		setMagnitude(magnitude);
		setDuration(duration);
		setFadeDuration(fadeDuration);
		setPos(position.x, position.y, position.z);
	}

	@OnlyIn(Dist.CLIENT)
	public float getShakeAmount(Player player, float delta) {
		float ticksDelta = tickCount + delta;
		float timeFrac = 1.0f - (ticksDelta - getDuration()) / (getFadeDuration() + 1.0f);
		float baseAmount = (ticksDelta < getDuration()) ? getMagnitude() : (timeFrac * timeFrac * getMagnitude());
		Vec3 playerPos = player.getEyePosition(delta);
		float distFrac = (float) (1.0 - Mth.clamp(position().distanceTo(playerPos) / getRadius(), 0.0, 1.0));
		return baseAmount * distFrac * distFrac;
	}

	public void tick() {
		super.tick();
		if (tickCount > getDuration() + getFadeDuration()) {
			discard();
		}
	}

	protected void defineSynchedData() {
		entityData.define((EntityDataAccessor) ScreenShakeEntity.RADIUS, 10.0f);
		entityData.define((EntityDataAccessor) ScreenShakeEntity.MAGNITUDE, 1.0f);
		entityData.define((EntityDataAccessor) ScreenShakeEntity.DURATION, 0);
		entityData.define((EntityDataAccessor) ScreenShakeEntity.FADE_DURATION, 5);
	}

	public float getRadius() {
		return (float) entityData.get((EntityDataAccessor) ScreenShakeEntity.RADIUS);
	}

	public void setRadius(float radius) {
		entityData.set((EntityDataAccessor) ScreenShakeEntity.RADIUS, radius);
	}

	public float getMagnitude() {
		return (float) entityData.get((EntityDataAccessor) ScreenShakeEntity.MAGNITUDE);
	}

	public void setMagnitude(float magnitude) {
		entityData.set((EntityDataAccessor) ScreenShakeEntity.MAGNITUDE, magnitude);
	}

	public int getDuration() {
		return (int) entityData.get((EntityDataAccessor) ScreenShakeEntity.DURATION);
	}

	public void setDuration(int duration) {
		entityData.set((EntityDataAccessor) ScreenShakeEntity.DURATION, duration);
	}

	public int getFadeDuration() {
		return (int) entityData.get((EntityDataAccessor) ScreenShakeEntity.FADE_DURATION);
	}

	public void setFadeDuration(int fadeDuration) {
		entityData.set((EntityDataAccessor) ScreenShakeEntity.FADE_DURATION, fadeDuration);
	}

	protected void readAdditionalSaveData(CompoundTag compound) {
		setRadius(compound.getFloat("radius"));
		setMagnitude(compound.getFloat("magnitude"));
		setDuration(compound.getInt("duration"));
		setFadeDuration(compound.getInt("fade_duration"));
		tickCount = compound.getInt("ticks_existed");
	}

	protected void addAdditionalSaveData(CompoundTag compound) {
		compound.putFloat("radius", getRadius());
		compound.putFloat("magnitude", getMagnitude());
		compound.putInt("duration", getDuration());
		compound.putInt("fade_duration", getFadeDuration());
		compound.putInt("ticks_existed", tickCount);
	}

	public Packet<?> getAddEntityPacket() {
		return (Packet<?>) NetworkHooks.getEntitySpawningPacket(this);
	}

	public static void ScreenShake(Level world, Vec3 position, float radius, float magnitude, int duration, int fadeDuration) {
		if (!world.isClientSide) {
			ScreenShakeEntity ScreenShake = new ScreenShakeEntity(world, position, radius, magnitude, duration, fadeDuration);
			world.addFreshEntity(ScreenShake);
		}
	}

	static {
		RADIUS = SynchedEntityData.defineId(ScreenShakeEntity.class, EntityDataSerializers.FLOAT);
		MAGNITUDE = SynchedEntityData.defineId(ScreenShakeEntity.class, EntityDataSerializers.FLOAT);
		DURATION = SynchedEntityData.defineId(ScreenShakeEntity.class, EntityDataSerializers.INT);
		FADE_DURATION = SynchedEntityData.defineId(ScreenShakeEntity.class, EntityDataSerializers.INT);
	}
}
