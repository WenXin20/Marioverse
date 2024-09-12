package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ParticleRegistry {
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MUSHROOM_TRANSFORM;

    static
    {
        MUSHROOM_TRANSFORM = Marioverse.PARTICLES.register("mushroom_transform",
                () -> new SimpleParticleType(false));
    }

    public static void init()
    {}
}
