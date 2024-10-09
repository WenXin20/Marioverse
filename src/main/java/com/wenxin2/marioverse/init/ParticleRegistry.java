package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ParticleRegistry {
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> COIN_GLINT;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> EXCELLENT;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FANTASTIC;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GOOD;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GREAT;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INCREDIBLE;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MUSHROOM_TRANSFORM;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ONE_UP;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SUPER;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WONDERFUL;

    static
    {
        COIN_GLINT = Marioverse.PARTICLES.register("coin_glint", () -> new SimpleParticleType(false));
        EXCELLENT = Marioverse.PARTICLES.register("excellent", () -> new SimpleParticleType(false));
        FANTASTIC = Marioverse.PARTICLES.register("fantastic", () -> new SimpleParticleType(false));
        GOOD = Marioverse.PARTICLES.register("good", () -> new SimpleParticleType(false));
        GREAT = Marioverse.PARTICLES.register("great", () -> new SimpleParticleType(false));
        INCREDIBLE = Marioverse.PARTICLES.register("incredible", () -> new SimpleParticleType(false));
        MUSHROOM_TRANSFORM = Marioverse.PARTICLES.register("mushroom_transform", () -> new SimpleParticleType(false));
        ONE_UP = Marioverse.PARTICLES.register("one_up", () -> new SimpleParticleType(false));
        SUPER = Marioverse.PARTICLES.register("super", () -> new SimpleParticleType(false));
        WONDERFUL = Marioverse.PARTICLES.register("wonderful", () -> new SimpleParticleType(false));
    }

    public static void init()
    {}
}
