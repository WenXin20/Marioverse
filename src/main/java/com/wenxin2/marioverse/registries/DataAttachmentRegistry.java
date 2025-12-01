package com.wenxin2.marioverse.registries;

import com.mojang.serialization.Codec;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.utils.ListIntCodec;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.attachment.AttachmentType;

public class DataAttachmentRegistry {
    public static final Supplier<AttachmentType<Boolean>> CRACKED = Marioverse.ATTACHMENT_TYPES
            .register("cracked", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> IS_ATTACKING = Marioverse.ATTACHMENT_TYPES
            .register("is_attacking", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
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

    public static final Supplier<AttachmentType<Boolean>> HAS_HIT_BLOCK = Marioverse.ATTACHMENT_TYPES
            .register("has_hit_block", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
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

    public static final Supplier<AttachmentType<Integer>> REASSEMBLE_DURATION = Marioverse.ATTACHMENT_TYPES
            .register("reassemble_duration", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt)).build());
    public static final Supplier<AttachmentType<Integer>> DEATH_DURATION = Marioverse.ATTACHMENT_TYPES
            .register("death_duration", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt)).build());
    public static final Supplier<AttachmentType<Integer>> SUPER_STAR_COOLDOWN = Marioverse.ATTACHMENT_TYPES
            .register("super_star_cooldown", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT)
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

    public static final Supplier<AttachmentType<Integer>> ENTITY_FROZEN_COOLDOWN = Marioverse.ATTACHMENT_TYPES
            .register("entity_frozen_cooldown", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt)).build());

    public static final Supplier<AttachmentType<Integer>> TICKS_IN_AIR = Marioverse.ATTACHMENT_TYPES
            .register("ticks_in_air", () -> AttachmentType.builder(() -> ConfigRegistry.ICE_CUBE_LIFESPAN.get()).serialize(Codec.INT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt)).build());

    public static final Supplier<AttachmentType<List<UUID>>> POKEY_STACK = Marioverse.ATTACHMENT_TYPES
            .register("pokey_stack", () -> AttachmentType.builder((Supplier<List<UUID>>) ArrayList::new)
                    .serialize(Codec.list(UUIDUtil.CODEC)).sync(ListIntCodec.STREAM_CODEC).build());
    public static final Supplier<AttachmentType<List<Integer>>> POKEY_STACK_IDS = Marioverse.ATTACHMENT_TYPES
            .register("pokey_stack_ids", () -> AttachmentType.builder((Supplier<List<Integer>>) ArrayList::new)
                    .serialize(Codec.INT.listOf()).sync(StreamCodec.of(
                            (buf, list) -> {
                                buf.writeVarInt(list.size());
                                for (int id : list) buf.writeVarInt(id);
                            }, buf -> {
                                int size = buf.readVarInt();
                                List<Integer> list = new ArrayList<>(size);
                                for (int i = 0; i < size; i++) list.add(buf.readVarInt());
                                return list;
                            }
                    )).build());


    public static void init() {}
}
