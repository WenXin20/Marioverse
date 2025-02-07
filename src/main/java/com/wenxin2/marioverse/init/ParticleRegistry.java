package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ParticleRegistry {
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> COIN_GLINT;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> EXCELLENT;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FANTASTIC;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FIRE_POWERED_UP;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GOOD;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GREAT;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INCREDIBLE;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INVISIBLE_AMETHYST_QUESTION;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INVISIBLE_BRICK_QUESTION;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INVISIBLE_DEEP_FUNGAL_QUESTION;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INVISIBLE_DEEPSLATE_BRICK_QUESTION;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INVISIBLE_DEEPSLATE_TILE_QUESTION;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INVISIBLE_END_STONE_BRICK_QUESTION;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INVISIBLE_FUNGAL_QUESTION;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INVISIBLE_MOSSY_STONE_BRICK_QUESTION;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INVISIBLE_MUD_BRICK_QUESTION;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INVISIBLE_NETHER_BRICK_QUESTION;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INVISIBLE_PURPUR_QUESTION;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INVISIBLE_RED_NETHER_BRICK_QUESTION;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INVISIBLE_STONE_BRICK_QUESTION;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INVISIBLE_TUFF_BRICK_QUESTION;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> POWERED_UP;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ONE_UP;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SUPER;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WONDERFUL;

    static {
        COIN_GLINT = Marioverse.PARTICLES.register("coin_glint", () -> new SimpleParticleType(false));
        EXCELLENT = Marioverse.PARTICLES.register("excellent", () -> new SimpleParticleType(false));
        FANTASTIC = Marioverse.PARTICLES.register("fantastic", () -> new SimpleParticleType(false));
        FIRE_POWERED_UP = Marioverse.PARTICLES.register("fire_powered_up", () -> new SimpleParticleType(false));
        GOOD = Marioverse.PARTICLES.register("good", () -> new SimpleParticleType(false));
        GREAT = Marioverse.PARTICLES.register("great", () -> new SimpleParticleType(false));
        INCREDIBLE = Marioverse.PARTICLES.register("incredible", () -> new SimpleParticleType(false));
        INVISIBLE_AMETHYST_QUESTION = Marioverse.PARTICLES.register("invisible_amethyst_question", () -> new SimpleParticleType(false));
        INVISIBLE_BRICK_QUESTION = Marioverse.PARTICLES.register("invisible_brick_question", () -> new SimpleParticleType(false));
        INVISIBLE_DEEPSLATE_BRICK_QUESTION = Marioverse.PARTICLES.register("invisible_deepslate_brick_question", () -> new SimpleParticleType(false));
        INVISIBLE_DEEPSLATE_TILE_QUESTION = Marioverse.PARTICLES.register("invisible_deepslate_tile_question", () -> new SimpleParticleType(false));
        INVISIBLE_DEEP_FUNGAL_QUESTION = Marioverse.PARTICLES.register("invisible_deep_fungal_question", () -> new SimpleParticleType(false));
        INVISIBLE_END_STONE_BRICK_QUESTION = Marioverse.PARTICLES.register("invisible_end_stone_brick_question", () -> new SimpleParticleType(false));
        INVISIBLE_FUNGAL_QUESTION = Marioverse.PARTICLES.register("invisible_fungal_question", () -> new SimpleParticleType(false));
        INVISIBLE_MOSSY_STONE_BRICK_QUESTION = Marioverse.PARTICLES.register("invisible_mossy_stone_brick_question", () -> new SimpleParticleType(false));
        INVISIBLE_MUD_BRICK_QUESTION = Marioverse.PARTICLES.register("invisible_mud_brick_question", () -> new SimpleParticleType(false));
        INVISIBLE_NETHER_BRICK_QUESTION = Marioverse.PARTICLES.register("invisible_nether_brick_question", () -> new SimpleParticleType(false));
        INVISIBLE_PURPUR_QUESTION = Marioverse.PARTICLES.register("invisible_purpur_question", () -> new SimpleParticleType(false));
        INVISIBLE_RED_NETHER_BRICK_QUESTION = Marioverse.PARTICLES.register("invisible_red_nether_brick_question", () -> new SimpleParticleType(false));
        INVISIBLE_STONE_BRICK_QUESTION = Marioverse.PARTICLES.register("invisible_stone_brick_question", () -> new SimpleParticleType(false));
        INVISIBLE_TUFF_BRICK_QUESTION = Marioverse.PARTICLES.register("invisible_tuff_brick_question", () -> new SimpleParticleType(false));
        POWERED_UP = Marioverse.PARTICLES.register("powered_up", () -> new SimpleParticleType(false));
        ONE_UP = Marioverse.PARTICLES.register("one_up", () -> new SimpleParticleType(false));
        SUPER = Marioverse.PARTICLES.register("super", () -> new SimpleParticleType(false));
        WONDERFUL = Marioverse.PARTICLES.register("wonderful", () -> new SimpleParticleType(false));
    }

    public static void init() {}
}
