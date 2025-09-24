package com.wenxin2.marioverse.registries;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.Marioverse;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ParticleRegistry {
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> COIN_GLINT;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> EXCELLENT;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FANTASTIC;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FIRE_POWERED_UP;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GLOWING_STAR;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GOOD;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GREAT;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GOLD_KOOPA_SHELL_SHATTER;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GREEN_KOOPA_SHELL_SHATTER;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ICE_CUBE_SHATTER;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ICE_POWERED_UP;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ICE_STAR;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INCREDIBLE;
    public static final DeferredHolder<ParticleType<?>, ParticleType<ItemParticleOption>> NO_MOVEMENT_ITEM;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> POWERED_UP;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ONE_UP;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RAINBOW_GLINT;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RED_KOOPA_SHELL_SHATTER;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SUPER;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SUSPENDED_FIRE;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WONDERFUL;

    static {
        COIN_GLINT = Marioverse.PARTICLES.register("coin_glint", () -> new SimpleParticleType(false));
        EXCELLENT = Marioverse.PARTICLES.register("excellent", () -> new SimpleParticleType(false));
        FANTASTIC = Marioverse.PARTICLES.register("fantastic", () -> new SimpleParticleType(false));
        FIRE_POWERED_UP = Marioverse.PARTICLES.register("fire_powered_up", () -> new SimpleParticleType(false));
        GLOWING_STAR = Marioverse.PARTICLES.register("glowing_star", () -> new SimpleParticleType(true));
        GOOD = Marioverse.PARTICLES.register("good", () -> new SimpleParticleType(false));
        GREAT = Marioverse.PARTICLES.register("great", () -> new SimpleParticleType(false));
        GOLD_KOOPA_SHELL_SHATTER = Marioverse.PARTICLES.register("gold_koopa_shell_shatter", () -> new SimpleParticleType(false));
        GREEN_KOOPA_SHELL_SHATTER = Marioverse.PARTICLES.register("green_koopa_shell_shatter", () -> new SimpleParticleType(false));
        ICE_CUBE_SHATTER = Marioverse.PARTICLES.register("ice_cube_shatter", () -> new SimpleParticleType(false));
        ICE_POWERED_UP = Marioverse.PARTICLES.register("ice_powered_up", () -> new SimpleParticleType(false));
        ICE_STAR = Marioverse.PARTICLES.register("ice_star", () -> new SimpleParticleType(false));
        INCREDIBLE = Marioverse.PARTICLES.register("incredible", () -> new SimpleParticleType(false));
        POWERED_UP = Marioverse.PARTICLES.register("powered_up", () -> new SimpleParticleType(false));
        ONE_UP = Marioverse.PARTICLES.register("one_up", () -> new SimpleParticleType(false));
        RAINBOW_GLINT = Marioverse.PARTICLES.register("rainbow_glint", () -> new SimpleParticleType(false));
        RED_KOOPA_SHELL_SHATTER = Marioverse.PARTICLES.register("red_koopa_shell_shatter", () -> new SimpleParticleType(false));
        SUPER = Marioverse.PARTICLES.register("super", () -> new SimpleParticleType(false));
        SUSPENDED_FIRE = Marioverse.PARTICLES.register("suspended_fire", () -> new SimpleParticleType(false));
        WONDERFUL = Marioverse.PARTICLES.register("wonderful", () -> new SimpleParticleType(false));

        NO_MOVEMENT_ITEM = Marioverse.PARTICLES.register("no_movement_item", () -> new ParticleType<ItemParticleOption>(false) {
            @Override
            public MapCodec<ItemParticleOption> codec() {
                return ItemParticleOption.codec(this);
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, ItemParticleOption> streamCodec() {
                return ItemParticleOption.streamCodec(this);
            }
        });
    }

    public static void init() {}
}
