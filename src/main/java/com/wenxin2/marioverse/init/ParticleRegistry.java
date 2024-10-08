package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ParticleRegistry {
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> COIN_GLINT;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GOOD;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MUSHROOM_TRANSFORM;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ONE_UP;

    static
    {
        COIN_GLINT = Marioverse.PARTICLES.register("coin_glint",
                () -> new SimpleParticleType(false));
        GOOD = Marioverse.PARTICLES.register("good",
                () -> new SimpleParticleType(false));
        MUSHROOM_TRANSFORM = Marioverse.PARTICLES.register("mushroom_transform",
                () -> new SimpleParticleType(false));
        ONE_UP = Marioverse.PARTICLES.register("one_up",
                () -> new SimpleParticleType(false));
    }

    public static void init()
    {}
}
