package com.wenxin2.marioverse.registries;

import com.mojang.serialization.Codec;
import com.wenxin2.marioverse.Marioverse;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.attachment.AttachmentType;

public class DataAttachmentRegistry {
    public static final Supplier<AttachmentType<Boolean>> CRACKED = Marioverse.ATTACHMENT_TYPES
            .register("cracked", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> HAS_CARROT = Marioverse.ATTACHMENT_TYPES
            .register("has_carrot", () -> AttachmentType.builder(() -> true).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> HAS_FLOWER = Marioverse.ATTACHMENT_TYPES
            .register("has_flower", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> HAS_GOLDEN_CARROT = Marioverse.ATTACHMENT_TYPES
            .register("has_golden_carrot", () -> AttachmentType.builder(() -> true).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> HAS_HIT_BLOCK = Marioverse.ATTACHMENT_TYPES
            .register("has_hit_block", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> IS_ATTACKING = Marioverse.ATTACHMENT_TYPES
            .register("is_attacking", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> IS_BLOOMING = Marioverse.ATTACHMENT_TYPES
            .register("is_blooming", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> IS_CHARGING = Marioverse.ATTACHMENT_TYPES
            .register("is_charging", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> IS_HIDING = Marioverse.ATTACHMENT_TYPES
            .register("is_hiding", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> IS_SLIDING = Marioverse.ATTACHMENT_TYPES
            .register("is_sliding", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());

    public static final Supplier<AttachmentType<Boolean>> HAS_DASH_MUSHROOM_BOOST = Marioverse.ATTACHMENT_TYPES
            .register("has_dash_mushroom_boost", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> HAS_FIRE_FLOWER = Marioverse.ATTACHMENT_TYPES
            .register("has_fire_flower", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> HAS_ICE_FLOWER = Marioverse.ATTACHMENT_TYPES
            .register("has_ice_flower", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> HAS_MEGA_MUSHROOM = Marioverse.ATTACHMENT_TYPES
            .register("has_mega_mushroom", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> HAS_MINI_MUSHROOM = Marioverse.ATTACHMENT_TYPES
            .register("has_mini_mushroom", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> HAS_SUPER_MUSHROOM = Marioverse.ATTACHMENT_TYPES
            .register("has_super_mushroom", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> HAS_SUPER_MUSHROOM_OVERRIDE = Marioverse.ATTACHMENT_TYPES
            .register("has_super_mushroom_override", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> HAS_SUPER_STAR = Marioverse.ATTACHMENT_TYPES
            .register("has_super_star", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> PLAYED_SUPER_STAR_THEME = Marioverse.ATTACHMENT_TYPES
            .register("played_super_star_theme", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());

    public static final Supplier<AttachmentType<Boolean>> PLAYED_ENTER_PIPE_SOUND = Marioverse.ATTACHMENT_TYPES
            .register("played_enter_pipe_sound", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> PLAYED_EXIT_PIPE_SOUND = Marioverse.ATTACHMENT_TYPES
            .register("played_exit_pipe_sound", () -> AttachmentType.builder(() -> true).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> PLAYED_INSIDE_PIPE_SOUND = Marioverse.ATTACHMENT_TYPES
            .register("played_inside_pipe_sound", () -> AttachmentType.builder(() -> true).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());

    public static final Supplier<AttachmentType<Integer>> ATTACK_COOLDOWN = Marioverse.ATTACHMENT_TYPES
            .register("attack_cooldown", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt)).build());
    public static final Supplier<AttachmentType<Integer>> CHECKPOINT_FLAG_COOLDOWN = Marioverse.ATTACHMENT_TYPES
            .register("checkpoint_flag_cooldown", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt)).build());
    public static final Supplier<AttachmentType<Integer>> CONSECUTIVE_BOUNCES = Marioverse.ATTACHMENT_TYPES
            .register("consecutive_bounces", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt)).build());
    public static final Supplier<AttachmentType<Integer>> DEATH_DURATION = Marioverse.ATTACHMENT_TYPES
            .register("death_duration", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt)).build());
    public static final Supplier<AttachmentType<Integer>> FIREBALL_COUNT = Marioverse.ATTACHMENT_TYPES
            .register("fireball_count", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt)).build());
    public static final Supplier<AttachmentType<Integer>> FIREBALL_COOLDOWN = Marioverse.ATTACHMENT_TYPES
            .register("fireball_cooldown", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt)).build());
    public static final Supplier<AttachmentType<Integer>> FREEZE_IMMUNITY_DURATION = Marioverse.ATTACHMENT_TYPES
            .register("freeze_immunity_duration", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt)).build());
    public static final Supplier<AttachmentType<Integer>> FROZEN_DURATION = Marioverse.ATTACHMENT_TYPES
            .register("frozen_duration", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt)).build());
    public static final Supplier<AttachmentType<Integer>> MEGA_MUSHROOM_DURATION = Marioverse.ATTACHMENT_TYPES
            .register("mega_mushroom_duration", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt)).build());
    public static final Supplier<AttachmentType<Integer>> ONE_UPS_COOLDOWN = Marioverse.ATTACHMENT_TYPES
            .register("one_ups_cooldown", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt)).build());
    public static final Supplier<AttachmentType<Integer>> ONE_UPS_REWARDED = Marioverse.ATTACHMENT_TYPES
            .register("one_ups_rewarded", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt)).build());
    public static final Supplier<AttachmentType<Integer>> REASSEMBLE_DURATION = Marioverse.ATTACHMENT_TYPES
            .register("reassemble_duration", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt)).build());
    public static final Supplier<AttachmentType<Integer>> SUPER_STAR_DURATION = Marioverse.ATTACHMENT_TYPES
            .register("super_star_duration", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt)).build());

    public static final Supplier<AttachmentType<Float>> BODY_ROTATION = Marioverse.ATTACHMENT_TYPES
            .register("body_rotation", () -> AttachmentType.builder(() -> 1.0F).serialize(Codec.FLOAT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeFloat, FriendlyByteBuf::readFloat)).build());
    public static final Supplier<AttachmentType<Float>> HEAD_ROTATION = Marioverse.ATTACHMENT_TYPES
            .register("head_rotation", () -> AttachmentType.builder(() -> 1.0F).serialize(Codec.FLOAT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeFloat, FriendlyByteBuf::readFloat)).build());
    public static final Supplier<AttachmentType<Float>> HEIGHT = Marioverse.ATTACHMENT_TYPES
            .register("height", () -> AttachmentType.builder(() -> 1.0F).serialize(Codec.FLOAT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeFloat, FriendlyByteBuf::readFloat)).build());
    public static final Supplier<AttachmentType<Float>> WIDTH = Marioverse.ATTACHMENT_TYPES
            .register("width", () -> AttachmentType.builder(() -> 1.0F).serialize(Codec.FLOAT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeFloat, FriendlyByteBuf::readFloat)).build());
    public static final Supplier<AttachmentType<Float>> PITCH = Marioverse.ATTACHMENT_TYPES
            .register("pitch", () -> AttachmentType.builder(() -> 1.0F).serialize(Codec.FLOAT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeFloat, FriendlyByteBuf::readFloat)).build());
    public static final Supplier<AttachmentType<Float>> Y_BODY_ROT = Marioverse.ATTACHMENT_TYPES
            .register("y_body_rot", () -> AttachmentType.builder(() -> 1.0F).serialize(Codec.FLOAT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeFloat, FriendlyByteBuf::readFloat)).build());

    public static final Supplier<AttachmentType<Float>> EYE_HEIGHT_SCALE = Marioverse.ATTACHMENT_TYPES
            .register("eye_height_scale", () -> AttachmentType.builder(() -> 1.0F).serialize(Codec.FLOAT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeFloat, FriendlyByteBuf::readFloat)).build());
    public static final Supplier<AttachmentType<Float>> HEIGHT_SCALE = Marioverse.ATTACHMENT_TYPES
            .register("height_scale", () -> AttachmentType.builder(() -> 1.0F).serialize(Codec.FLOAT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeFloat, FriendlyByteBuf::readFloat)).build());
    public static final Supplier<AttachmentType<Float>> WIDTH_SCALE = Marioverse.ATTACHMENT_TYPES
            .register("width_scale", () -> AttachmentType.builder(() -> 1.0F).serialize(Codec.FLOAT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeFloat, FriendlyByteBuf::readFloat)).build());
    public static final Supplier<AttachmentType<Float>> SCALE = Marioverse.ATTACHMENT_TYPES
            .register("scale", () -> AttachmentType.builder(() -> 1.0F).serialize(Codec.FLOAT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeFloat, FriendlyByteBuf::readFloat)).build());

    public static final Supplier<AttachmentType<String>> TYPE = Marioverse.ATTACHMENT_TYPES
            .register("type", () -> AttachmentType.builder(() -> "").serialize(Codec.STRING)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeUtf, FriendlyByteBuf::readUtf)).build());

    public static final Supplier<AttachmentType<Integer>> ENTITY_FROZEN_DURATION = Marioverse.ATTACHMENT_TYPES
            .register("entity_frozen_duration", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt)).build());

    public static final Supplier<AttachmentType<Integer>> TICKS_IN_AIR = Marioverse.ATTACHMENT_TYPES
            .register("ticks_in_air", () -> AttachmentType.builder(() -> ConfigRegistry.ICE_CUBE_LIFESPAN.get()).serialize(Codec.INT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt)).build());


    public static void init() {}
}
