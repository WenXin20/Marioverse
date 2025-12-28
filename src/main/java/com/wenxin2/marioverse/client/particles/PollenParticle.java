package com.wenxin2.marioverse.client.particles;

import java.util.Optional;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SuspendedParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleGroup;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class PollenParticle extends TextureSheetParticle {
    public PollenParticle(ClientLevel level, SpriteSet set, double x, double y, double z, double dx, double dy, double dz) {
        super(level, x, y - 0.125, z, dx, dy, dz);
        this.setSize(0.01F, 0.01F);
        this.pickSprite(set);
        this.quadSize = this.quadSize * (this.random.nextFloat() * 0.6F + 0.6F);
        this.lifetime = (int)(16.0 / (Math.random() * 0.8 + 0.2));
        this.hasPhysics = false;
        this.friction = 1.0F;
        this.gravity = 0.0F;
    }

    @NotNull
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class PollenProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public PollenProvider(SpriteSet set) {
            this.sprite = set;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z, double dx, double dy, double dz) {
            PollenParticle suspendedParticle = new PollenParticle(level, this.sprite, x, y, z, 0.0, -0.8F, 0.0) {
                @NotNull
                @Override
                public Optional<ParticleGroup> getParticleGroup() {
                    return Optional.of(ParticleGroup.SPORE_BLOSSOM);
                }
            };

            suspendedParticle.lifetime = Mth.randomBetweenInclusive(level.random, 500, 1000);
            suspendedParticle.gravity = 0.01F;
            return suspendedParticle;
        }
    }
}