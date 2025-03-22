package com.wenxin2.marioverse.registries;

import com.mojang.serialization.Codec;
import com.wenxin2.marioverse.Marioverse;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;

public class DataComponentRegistry {
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IS_BOUND =
            Marioverse.COMPONENTS.register("is_bound",
                    () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL)
                            .networkSynchronized(ByteBufCodecs.BOOL).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Component>> PIPE_NAME =
            Marioverse.COMPONENTS.register("pipe_name",
                    () -> DataComponentType.<Component>builder().persistent(ComponentSerialization.FLAT_CODEC)
                            .networkSynchronized(ComponentSerialization.STREAM_CODEC).cacheEncoding().build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> POS_X =
            Marioverse.COMPONENTS.register("pos_x",
                    () -> DataComponentType.<Integer>builder().persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.INT).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> POS_Y =
            Marioverse.COMPONENTS.register("pos_y",
                    () -> DataComponentType.<Integer>builder().persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.INT).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> POS_Z =
            Marioverse.COMPONENTS.register("pos_z",
                    () -> DataComponentType.<Integer>builder().persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.INT).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockPos>> WARP_POS =
            Marioverse.COMPONENTS.register("warp_pos",
                    () -> DataComponentType.<BlockPos>builder().persistent(BlockPos.CODEC)
                            .networkSynchronized(BlockPos.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GlobalPos>> GLOBAL_WARP_POS =
            Marioverse.COMPONENTS.register("global_warp_pos",
                    () -> DataComponentType.<GlobalPos>builder().persistent(GlobalPos.CODEC)
                            .networkSynchronized(GlobalPos.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> WARP_DIMENSION =
            Marioverse.COMPONENTS.register("warp_dimension",
                    () -> DataComponentType.<String>builder().persistent(Codec.STRING)
                            .networkSynchronized(ByteBufCodecs.STRING_UTF8).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> WARP_UUID =
            Marioverse.COMPONENTS.register("warp_uuid",
                    () -> DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC)
                            .networkSynchronized(UUIDUtil.STREAM_CODEC).build());

    public static void init() {}
}
