package com.wenxin2.marioverse.client.particles;

import com.wenxin2.marioverse.particles.WarpStarParticleOption;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class WarpStarParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final Vector3f axis;
    private final float spin;
    private final boolean orbit;
    private final Vector3d orbitCenter;
    private final Vector3d orbitAxisA = new Vector3d();
    private final Vector3d orbitAxisB = new Vector3d();
    private double orbitStartAngle;

    protected WarpStarParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed,
                               WarpStarParticleOption option, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.axis = option.axis().lengthSquared() > 0.0F ? new Vector3f(option.axis()).normalize() : new Vector3f(0.0F, 1.0F, 0.0F);
        this.spin = option.spin();
        this.orbit = option.orbit();
        this.orbitCenter = new Vector3d(x + xSpeed, y + ySpeed, z + zSpeed);
        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.quadSize *= 0.75F;
        this.roll = this.random.nextFloat() * Mth.TWO_PI;
        this.oRoll = this.roll;

        if (this.orbit) {
            Vector3d offset = new Vector3d(-xSpeed, -ySpeed, -zSpeed);
            Vector3d normal = new Vector3d(this.axis.x(), this.axis.y(), this.axis.z());
            boolean hasSemiAxis = option.semiAxis().lengthSquared() > 1.0E-6F;
            float axisRatio = hasSemiAxis ? option.axisRatio() : 1.0F;

            this.orbitAxisA.set(hasSemiAxis ? new Vector3d(option.semiAxis().x(), option.semiAxis().y(), option.semiAxis().z()) : offset);
            this.orbitAxisB.set(normal).cross(this.orbitAxisA).mul(axisRatio);

            double lengthA = this.orbitAxisA.lengthSquared();
            double lengthB = this.orbitAxisB.lengthSquared();
            this.orbitStartAngle = lengthA > 1.0E-6 && lengthB > 1.0E-6
                    ? Math.atan2(offset.dot(this.orbitAxisB) / lengthB, offset.dot(this.orbitAxisA) / lengthA) : 0.0;

            this.xd = 0.0;
            this.yd = 0.0;
            this.zd = 0.0;
            this.lifetime = 18 + this.random.nextInt(5);
        } else {
            this.xd = xSpeed;
            this.yd = ySpeed;
            this.zd = zSpeed;
            this.friction = 0.88F;
            this.lifetime = 14 + this.random.nextInt(8);
        }
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        if (this.orbit) {
            this.tickOrbit();
            return;
        }

        if (this.spin != 0.0F) {
            float cos = Mth.cos(this.spin);
            float sin = Mth.sin(this.spin);
            double dot = this.axis.x() * this.xd + this.axis.y() * this.yd + this.axis.z() * this.zd;
            double crossX = this.axis.y() * this.zd - this.axis.z() * this.yd;
            double crossY = this.axis.z() * this.xd - this.axis.x() * this.zd;
            double crossZ = this.axis.x() * this.yd - this.axis.y() * this.xd;

            this.xd = this.xd * cos + crossX * sin + this.axis.x() * dot * (1.0F - cos);
            this.yd = this.yd * cos + crossY * sin + this.axis.y() * dot * (1.0F - cos);
            this.zd = this.zd * cos + crossZ * sin + this.axis.z() * dot * (1.0F - cos);
        }

        this.oRoll = this.roll;
        this.roll += this.spin;
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    private void tickOrbit() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.oRoll = this.roll;
        this.roll += this.spin;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        double angle = this.orbitStartAngle + this.spin * this.age;
        double radiusScale = 1.0 - (double) this.age / this.lifetime;
        double cos = Math.cos(angle) * radiusScale;
        double sin = Math.sin(angle) * radiusScale;

        this.setPos(this.orbitCenter.x + this.orbitAxisA.x * cos + this.orbitAxisB.x * sin,
                this.orbitCenter.y + this.orbitAxisA.y * cos + this.orbitAxisB.y * sin,
                this.orbitCenter.z + this.orbitAxisA.z * cos + this.orbitAxisB.z * sin);
        this.setSpriteFromAge(this.sprites);
    }

    @NotNull
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<WarpStarParticleOption> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(@NotNull WarpStarParticleOption option, @NotNull ClientLevel level, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new WarpStarParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, option, this.sprites);
        }
    }
}
