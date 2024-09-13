package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ParticleRegistry {
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MUSHROOM_TRANSFORM;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> COIN_GLINT;

    static
    {
        COIN_GLINT = Marioverse.PARTICLES.register("coin_glint",
                () -> new SimpleParticleType(false));
        MUSHROOM_TRANSFORM = Marioverse.PARTICLES.register("mushroom_transform",
                () -> new SimpleParticleType(false));
    }

    public static void init()
    {}
}
