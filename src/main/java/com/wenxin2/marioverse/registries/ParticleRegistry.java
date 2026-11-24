package com.wenxin2.marioverse.registries;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.particles.EntityShatterParticleOption;
import com.wenxin2.marioverse.particles.WarpLinkParticleOption;
import com.wenxin2.marioverse.particles.WarpStarParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ParticleRegistry {
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLUE_STAR;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> COIN_GLINT;
    public static final DeferredHolder<ParticleType<?>, ParticleType<EntityShatterParticleOption>> ENTITY_SHATTER;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> EXCELLENT;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FANTASTIC;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FIRE_POWERED_UP;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GLOWING_STAR;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GOOD;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GREAT;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GOLD_POLLEN;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ICE_CUBE_SHATTER;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ICE_POWERED_UP;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ICE_STAR;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INCREDIBLE;
    public static final DeferredHolder<ParticleType<?>, ParticleType<ItemParticleOption>> NO_MOVEMENT_ITEM;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> POWERED_UP;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ONE_UP;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RAINBOW_GLINT;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RED_STAR;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SUPER;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SUSPENDED_FIRE;
    public static final DeferredHolder<ParticleType<?>, ParticleType<WarpLinkParticleOption>> WARP_LINK;
    public static final DeferredHolder<ParticleType<?>, ParticleType<WarpStarParticleOption>> RAINBOW_STAR;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WONDERFUL;

    static {
        BLUE_STAR = Marioverse.PARTICLES.register("blue_star", () -> new SimpleParticleType(false));
        COIN_GLINT = Marioverse.PARTICLES.register("coin_glint", () -> new SimpleParticleType(false));
        ENTITY_SHATTER = Marioverse.PARTICLES.register("entity_shatter", () -> new ParticleType<EntityShatterParticleOption>(false) {
            @Override
            public MapCodec<EntityShatterParticleOption> codec() {
                return EntityShatterParticleOption.CODEC;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, EntityShatterParticleOption> streamCodec() {
                return EntityShatterParticleOption.STREAM_CODEC;
            }
        });
        EXCELLENT = Marioverse.PARTICLES.register("excellent", () -> new SimpleParticleType(false));
        FANTASTIC = Marioverse.PARTICLES.register("fantastic", () -> new SimpleParticleType(false));
        FIRE_POWERED_UP = Marioverse.PARTICLES.register("fire_powered_up", () -> new SimpleParticleType(false));
        GLOWING_STAR = Marioverse.PARTICLES.register("glowing_star", () -> new SimpleParticleType(true));
        GOOD = Marioverse.PARTICLES.register("good", () -> new SimpleParticleType(false));
        GREAT = Marioverse.PARTICLES.register("great", () -> new SimpleParticleType(false));
        GOLD_POLLEN = Marioverse.PARTICLES.register("gold_pollen", () -> new SimpleParticleType(false));
        ICE_CUBE_SHATTER = Marioverse.PARTICLES.register("ice_cube_shatter", () -> new SimpleParticleType(false));
        ICE_POWERED_UP = Marioverse.PARTICLES.register("ice_powered_up", () -> new SimpleParticleType(false));
        ICE_STAR = Marioverse.PARTICLES.register("ice_star", () -> new SimpleParticleType(false));
        INCREDIBLE = Marioverse.PARTICLES.register("incredible", () -> new SimpleParticleType(false));
        POWERED_UP = Marioverse.PARTICLES.register("powered_up", () -> new SimpleParticleType(false));
        ONE_UP = Marioverse.PARTICLES.register("one_up", () -> new SimpleParticleType(false));
        RAINBOW_GLINT = Marioverse.PARTICLES.register("rainbow_glint", () -> new SimpleParticleType(false));
        RED_STAR = Marioverse.PARTICLES.register("red_star", () -> new SimpleParticleType(false));
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

        RAINBOW_STAR = Marioverse.PARTICLES.register("rainbow_star", () -> new ParticleType<WarpStarParticleOption>(false) {
            @Override
            public MapCodec<WarpStarParticleOption> codec() {
                return WarpStarParticleOption.CODEC;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, WarpStarParticleOption> streamCodec() {
                return WarpStarParticleOption.STREAM_CODEC;
            }
        });

        WARP_LINK = Marioverse.PARTICLES.register("warp_link", () -> new ParticleType<WarpLinkParticleOption>(true) {
            @Override
            public MapCodec<WarpLinkParticleOption> codec() {
                return WarpLinkParticleOption.CODEC;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, WarpLinkParticleOption> streamCodec() {
                return WarpLinkParticleOption.STREAM_CODEC;
            }
        });
    }

    public static void init() {}
}
