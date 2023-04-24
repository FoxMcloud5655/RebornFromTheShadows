package net.sonmok14.fromtheshadows.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LaserParticle extends SimpleAnimatedParticle {
	private LaserParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, SpriteSet sprites) {
		super(world, x, y, z, sprites, 0.0f);
		xd = (float) motionX;
		yd = (float) motionY;
		zd = (float) motionZ;
		quadSize *= 4.5f + random.nextFloat() * 4.0f;
		lifetime = 7 + random.nextInt(2);
		gravity = 0.0f;
		setSpriteFromAge(sprites);
	}

	public int getLightColor(float p_189214_1_) {
		int lvt_2_1_ = super.getLightColor(p_189214_1_);
		int lvt_4_1_ = lvt_2_1_ >> 16 & 0xFF;
		return 0xF0 | lvt_4_1_ << 16;
	}

	public void tick() {
		super.tick();
		xd *= 0.8;
		yd *= 0.8;
		zd *= 0.8;
		double lvt_1_1_ = x - xo;
		double lvt_5_1_ = z - zo;
		float lvt_9_1_ = (float) (Mth.atan2(lvt_5_1_, lvt_1_1_) * 57.2957763671875) - 180.0f;
		roll = lvt_9_1_;
		oRoll = roll;
		setSpriteFromAge(sprites);
	}

	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Factory implements ParticleProvider<SimpleParticleType> {
		private SpriteSet spriteSet;

		public Factory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			LaserParticle p = new LaserParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
			return (Particle) p;
		}
	}
}
