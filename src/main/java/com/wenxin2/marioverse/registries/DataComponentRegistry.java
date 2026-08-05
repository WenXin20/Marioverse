package com.wenxin2.marioverse.registries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.power_up.PowerUpType;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;

public class DataComponentRegistry {
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> HAS_COLLISION =
            Marioverse.COMPONENTS.register("has_collision", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> HIDE_ITEM_RENDERED =
            Marioverse.COMPONENTS.register("hide_item_rendered", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IS_BOUND =
            Marioverse.COMPONENTS.register("is_bound", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IS_INTERACTABLE =
            Marioverse.COMPONENTS.register("is_interactable", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IS_LINKED =
            Marioverse.COMPONENTS.register("is_linked", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IS_RIGHT_CLICKABLE =
            Marioverse.COMPONENTS.register("is_right_clickable", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IS_SNEAKING =
            Marioverse.COMPONENTS.register("is_sneaking", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IS_UNBREAKABLE =
            Marioverse.COMPONENTS.register("is_unbreakable", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> UNDYING_CHARM =
            Marioverse.COMPONENTS.register("undying_charm", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockPos>> LINKED_POS =
            Marioverse.COMPONENTS.register("linked_pos", () -> DataComponentType.<BlockPos>builder()
                    .persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> LINKED_BLOCK =
            Marioverse.COMPONENTS.register("linked_block", () -> DataComponentType.<String>builder()
                    .persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Component>> PIPE_NAME =
            Marioverse.COMPONENTS.register("pipe_name", () -> DataComponentType.<Component>builder()
                    .persistent(ComponentSerialization.FLAT_CODEC).networkSynchronized(ComponentSerialization.STREAM_CODEC)
                    .cacheEncoding().build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BLOCK_FACE =
            Marioverse.COMPONENTS.register("block_face", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FACING_DIRECTION =
            Marioverse.COMPONENTS.register("facing_direction", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MENU_TYPE =
            Marioverse.COMPONENTS.register("menu_type", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> PLACEMENT_DIRECTION =
            Marioverse.COMPONENTS.register("placement_direction", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> PLACEMENT_OFFSET =
            Marioverse.COMPONENTS.register("placement_offset", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> REFILL_COUNTDOWN =
            Marioverse.COMPONENTS.register("refill_countdown", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> REFILL_TIME_UNIT =
            Marioverse.COMPONENTS.register("refill_time_unit", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> POS_X =
            Marioverse.COMPONENTS.register("pos_x", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> POS_Y =
            Marioverse.COMPONENTS.register("pos_y", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> POS_Z =
            Marioverse.COMPONENTS.register("pos_z", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

    public static final Supplier<DataComponentType<Holder<PowerUpType>>> POWER_UP_TYPE =
            Marioverse.COMPONENTS.register("power_up_type", () -> DataComponentType.<Holder<PowerUpType>>builder()
                    .persistent(Marioverse.POWER_UP_REGISTRY.holderByNameCodec())
                    .networkSynchronized(ByteBufCodecs.holderRegistry(Marioverse.POWER_UP_REGISTRY_KEY))
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WarpTarget>> WARP_BLOCK =
            Marioverse.COMPONENTS.register("warp_block", () -> DataComponentType.<WarpTarget>builder()
                    .persistent(WarpTarget.CODEC).networkSynchronized(WarpTarget.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WarpTarget>> WARP_ENTITY =
            Marioverse.COMPONENTS.register("warp_entity", () -> DataComponentType.<WarpTarget>builder()
                    .persistent(WarpTarget.CODEC).networkSynchronized(WarpTarget.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WarpTarget>> WARP_PAINTING =
            Marioverse.COMPONENTS.register("warp_painting", () -> DataComponentType.<WarpTarget>builder()
                    .persistent(WarpTarget.CODEC).networkSynchronized(WarpTarget.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockPos>> WARP_POS =
            Marioverse.COMPONENTS.register("warp_pos", () -> DataComponentType.<BlockPos>builder()
                    .persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GlobalPos>> GLOBAL_WARP_POS =
            Marioverse.COMPONENTS.register("global_warp_pos", () -> DataComponentType.<GlobalPos>builder()
                    .persistent(GlobalPos.CODEC).networkSynchronized(GlobalPos.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> WARP_DIMENSION =
            Marioverse.COMPONENTS.register("warp_dimension", () -> DataComponentType.<String>builder()
                    .persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> WARP_UUID =
            Marioverse.COMPONENTS.register("warp_uuid", () -> DataComponentType.<UUID>builder()
                    .persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC).build());

    public static void init() {}

    public record WarpTarget(BlockPos pos, String name) {
        public static final Codec<WarpTarget> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(BlockPos.CODEC.fieldOf("pos").forGetter(WarpTarget::pos),
                        Codec.STRING.fieldOf("name").forGetter(WarpTarget::name))
                        .apply(instance, WarpTarget::new));

        public static final StreamCodec<FriendlyByteBuf, String> STRING_CODEC =
                StreamCodec.of(FriendlyByteBuf::writeUtf, buf -> buf.readUtf(32767));

        public static final StreamCodec<FriendlyByteBuf, WarpTarget> STREAM_CODEC =
                StreamCodec.composite(BlockPos.STREAM_CODEC, WarpTarget::pos,
                        STRING_CODEC, WarpTarget::name, WarpTarget::new);
    }
}
