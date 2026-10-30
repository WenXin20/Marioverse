package com.wenxin2.marioverse.client.particles;

import com.wenxin2.marioverse.particles.WarpLinkParticleOption;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.VibrationSignalParticle;
import net.minecraft.world.level.gameevent.PositionSource;
import org.jetbrains.annotations.NotNull;

public class WarpLinkParticle extends VibrationSignalParticle {
    public WarpLinkParticle(ClientLevel level, double x, double y, double z, PositionSource destination, int arrivalInTicks) {
        super(level, x, y, z, destination, arrivalInTicks);
    }

    public static class Provider implements ParticleProvider<WarpLinkParticleOption> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(@NotNull WarpLinkParticleOption option, @NotNull ClientLevel level, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            WarpLinkParticle particle = new WarpLinkParticle(level, x, y, z, option.destination(), option.arrivalInTicks());
            particle.pickSprite(this.sprites);
            particle.setAlpha(1.0F);
            return particle;
        }
    }
}
