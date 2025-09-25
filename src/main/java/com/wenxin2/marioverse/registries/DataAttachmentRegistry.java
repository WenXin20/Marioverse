package com.wenxin2.marioverse.registries;

import com.mojang.serialization.Codec;
import com.wenxin2.marioverse.Marioverse;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.attachment.AttachmentType;

public class DataAttachmentRegistry {
    public static final Supplier<AttachmentType<Boolean>> HAS_SUPER_STAR = Marioverse.ATTACHMENT_TYPES.register(
            "has_super_star", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).sync(StreamCodec.of(             // sync to client
                    FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());

    public static void init() {}
}
