package com.wenxin2.marioverse.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SuspendedTownParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class FirePoweredUpParticle extends SuspendedTownParticle.HappyVillagerProvider {

    public FirePoweredUpParticle(SpriteSet spriteSet) {
        super(spriteSet);
    }

    @Override
    public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        Particle particle = super.createParticle(type, world, x, y, z, xSpeed, ySpeed, zSpeed);

        if (particle instanceof SuspendedTownParticle oneUpParticle) {
            oneUpParticle.setColor(1.0F, 1.0F, 1.0F);
            oneUpParticle.scale(1.5F);
        }
        return particle;
    }
}
