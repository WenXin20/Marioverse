package com.wenxin2.marioverse.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.GlowParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SuspendedTownParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class GlowingSuspendedTownParticle extends GlowParticle {
    public GlowingSuspendedTownParticle(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
    }

    @NotNull
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float partialTick) {
        float f = ((float)this.age + partialTick) / (float)this.lifetime;
        f = Mth.clamp(f, 0.0F, 1.0F);
        int i = super.getLightColor(partialTick);
        int j = i & 0xFF;
        int k = i >> 16 & 0xFF;
        j += (int)(f * 15.0F * 16.0F);
        if (j > 240) {
            j = 240;
        }

        return j | k << 16;
    }

    @OnlyIn(Dist.CLIENT)
    public static class FireProvider extends GlowParticle.WaxOffProvider {
        private final SpriteSet sprite;
        public FireProvider(SpriteSet spriteSet) {
            super(spriteSet);
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            Particle particle = new GlowingSuspendedTownParticle(world, x, y, z, xSpeed, ySpeed, zSpeed, this.sprite);

            particle.setColor(1.0F, 1.0F, 1.0F);
            particle.scale(1.5F);
            particle.setParticleSpeed(0.0, 0.5, 0.0);
            return particle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class GlowingProvider extends GlowParticle.WaxOffProvider {
        private final SpriteSet sprite;
        public GlowingProvider(SpriteSet spriteSet) {
            super(spriteSet);
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            Particle particle = new GlowingSuspendedTownParticle(world, x, y, z, xSpeed, ySpeed, zSpeed, this.sprite);

            particle.setColor(1.0F, 1.0F, 1.0F);
            particle.setLifetime(world.random.nextInt(30) + 10);
            particle.setParticleSpeed(xSpeed * 0.02 / 2.0, ySpeed * 0.04, zSpeed * 0.02 / 2.0);
            return particle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class CoinGlintProvider extends GlowParticle.WaxOffProvider {
        private final SpriteSet sprite;
        public CoinGlintProvider(SpriteSet spriteSet) {
            super(spriteSet);
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            Particle particle = new GlowingSuspendedTownParticle(world, x, y, z, xSpeed, ySpeed, zSpeed, this.sprite);

            particle.setColor(1.0F, 1.0F, 1.0F);
            particle.setLifetime(world.random.nextInt(30) + 10);
            particle.setParticleSpeed(xSpeed * 0.01 / 2.0, ySpeed * 0.01, zSpeed * 0.01 / 2.0);
            return particle;
        }
    }
}
