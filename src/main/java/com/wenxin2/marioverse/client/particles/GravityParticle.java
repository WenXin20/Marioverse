package com.wenxin2.marioverse.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class GravityParticle extends TextureSheetParticle {
    public GravityParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprite) {
        super(level, x, y, z, dx, dy, dz);
        this.setSprite(sprite.get(level.random));
        this.gravity = 1.0F;
        this.quadSize /= 2.0F;
    }

    @NotNull
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class BreakEntityProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public BreakEntityProvider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            GravityParticle particle = new GravityParticle(level, x, y, z, dx, dy, dz, this.sprites);
            particle.setParticleSpeed(level.random.nextGaussian() / 30.0,
                    dy + level.random.nextGaussian() / 2.0, level.random.nextGaussian() / 30.0);
            particle.setLifetime(level.random.nextInt(20) + 20);
            return particle;
        }
    }
}

