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
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class WarpStarParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final Vector3f axis;
    private final float spin;

    protected WarpStarParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed,
                               Vector3f axis, float spin, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.axis = axis.lengthSquared() > 0.0F ? new Vector3f(axis).normalize() : new Vector3f(0.0F, 1.0F, 0.0F);
        this.spin = spin;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.friction = 0.88F;
        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.quadSize *= 0.75F;
        this.lifetime = 14 + this.random.nextInt(8);
        this.roll = this.random.nextFloat() * Mth.TWO_PI;
        this.oRoll = this.roll;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
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
            return new WarpStarParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, option.axis(), option.spin(), this.sprites);
        }
    }
}
