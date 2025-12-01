package com.wenxin2.marioverse.utils;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class ListIntCodec {
    public static final Codec<IntList> CODEC =
            Codec.INT.listOf().xmap(
                    list -> {
                        IntArrayList result = new IntArrayList();
                        for (int v : list) result.add(v);
                        return result;
                    },
                    ints -> {
                        java.util.List<Integer> result = new java.util.ArrayList<>(ints.size());
                        for (int i = 0; i < ints.size(); i++)
                            result.add(ints.getInt(i));
                        return result;
                    }
            );

    public static final StreamCodec<FriendlyByteBuf, List<UUID>> STREAM_CODEC =
            StreamCodec.of(
                    (buf, list) -> {
                        buf.writeVarInt(list.size());
                        for (UUID uuid : list)
                            buf.writeUUID(uuid);
                    },
                    buf -> {
                        int size = buf.readVarInt();
                        List<UUID> list = new ArrayList<>(size);
                        for (int i = 0; i < size; i++)
                            list.add(buf.readUUID());
                        return list;
                    }
            );

}
