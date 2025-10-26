package com.wenxin2.marioverse.registries;

import com.mojang.serialization.Codec;
import com.wenxin2.marioverse.Marioverse;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.attachment.AttachmentType;

public class DataAttachmentRegistry {
    public static final Supplier<AttachmentType<Boolean>> CRACKED = Marioverse.ATTACHMENT_TYPES.register(
        "cracked", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> IS_ATTACKING = Marioverse.ATTACHMENT_TYPES.register(
        "is_attacking", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> IS_CHARGING = Marioverse.ATTACHMENT_TYPES.register(
        "is_charging", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> IS_HIDING = Marioverse.ATTACHMENT_TYPES.register(
        "is_hiding", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());

    public static final Supplier<AttachmentType<Boolean>> HAS_HIT_BLOCK = Marioverse.ATTACHMENT_TYPES.register(
            "has_hit_block", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> HAS_SUPER_STAR = Marioverse.ATTACHMENT_TYPES.register(
            "has_super_star", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> PLAYED_SUPER_STAR_THEME = Marioverse.ATTACHMENT_TYPES.register(
            "played_super_star_theme", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());

    public static final Supplier<AttachmentType<Boolean>> PLAYED_ENTER_PIPE_SOUND = Marioverse.ATTACHMENT_TYPES.register(
            "played_enter_pipe_sound", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> PLAYED_EXIT_PIPE_SOUND = Marioverse.ATTACHMENT_TYPES.register(
            "played_exit_pipe_sound", () -> AttachmentType.builder(() -> true).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> PLAYED_INSIDE_PIPE_SOUND = Marioverse.ATTACHMENT_TYPES.register(
            "played_inside_pipe_sound", () -> AttachmentType.builder(() -> true).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());

    public static final Supplier<AttachmentType<Integer>> FAIL_TIMER = Marioverse.ATTACHMENT_TYPES.register(
            "fail_timer", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt)).build());
    public static final Supplier<AttachmentType<Integer>> REATTACHMENT_COUNTDOWN = Marioverse.ATTACHMENT_TYPES.register(
            "reattachment_countdown", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt)).build());
    public static final Supplier<AttachmentType<Integer>> SUPER_STAR_COOLDOWN = Marioverse.ATTACHMENT_TYPES.register(
            "super_star_cooldown", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt)).build());

    public static final Supplier<AttachmentType<String>> TYPE = Marioverse.ATTACHMENT_TYPES.register(
            "type", () -> AttachmentType.builder(() -> "").serialize(Codec.STRING)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeUtf, FriendlyByteBuf::readUtf)).build());

    public static void init() {}
}
