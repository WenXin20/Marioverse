package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class SoundRegistry {
    public static final SoundType WATER_SPOUT = new SoundType(1.0F, 1.0F, SoundEvents.BUCKET_FILL,
            SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY, SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_FILL);
    public static final DeferredHolder<SoundEvent, SoundEvent> PIPES_LINKED;
    public static final DeferredHolder<SoundEvent, SoundEvent> PIPE_CLOSES;
    public static final DeferredHolder<SoundEvent, SoundEvent> PIPE_OPENS;
    public static final DeferredHolder<SoundEvent, SoundEvent> PIPE_WARPS;
    public static final DeferredHolder<SoundEvent, SoundEvent> WATER_SPOUT_BREAK;
    public static final DeferredHolder<SoundEvent, SoundEvent> WATER_SPOUT_FALL;
    public static final DeferredHolder<SoundEvent, SoundEvent> WATER_SPOUT_HIT;
    public static final DeferredHolder<SoundEvent, SoundEvent> WATER_SPOUT_PLACE;
    public static final DeferredHolder<SoundEvent, SoundEvent> WATER_SPOUT_STEP;
    public static final DeferredHolder<SoundEvent, SoundEvent> WRENCH_BOUND;

    static {
        PIPES_LINKED = Marioverse.SOUNDS.register("block.pipes_linked",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MODID, "block.pipes_linked")));
        PIPE_CLOSES = Marioverse.SOUNDS.register("block.pipe_closes",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MODID, "block.pipe_closes")));
        PIPE_OPENS = Marioverse.SOUNDS.register("block.pipe_opens",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MODID, "block.pipe_opens")));
        PIPE_WARPS = Marioverse.SOUNDS.register("block.pipe_warps",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MODID, "block.pipe_warps")));
        WATER_SPOUT_BREAK = Marioverse.SOUNDS.register("block.water_spout.break",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MODID, "block.water_spout.break")));
        WATER_SPOUT_FALL = Marioverse.SOUNDS.register("block.water_spout.fall",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MODID, "block.water_spout.fall")));
        WATER_SPOUT_HIT = Marioverse.SOUNDS.register("block.water_spout.hit",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MODID, "block.water_spout.hit")));
        WATER_SPOUT_PLACE = Marioverse.SOUNDS.register("block.water_spout.place",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MODID, "block.water_spout.place")));
        WATER_SPOUT_STEP = Marioverse.SOUNDS.register("block.water_spout.step",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MODID, "block.water_spout.step")));
        WRENCH_BOUND = Marioverse.SOUNDS.register("item.wrench_bound",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MODID, "item.wrench_bound")));
    }

    public static void init()
    {}
}
