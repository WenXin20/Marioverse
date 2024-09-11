package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

public class SoundRegistry {
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_BONK;
    public static final DeferredHolder<SoundEvent, SoundEvent> COIN_PICKUP;
    public static final DeferredHolder<SoundEvent, SoundEvent> COIN_PLACE;
    public static final DeferredHolder<SoundEvent, SoundEvent> DAMAGE_TAKEN;
    public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_SPAWNS;
    public static final DeferredHolder<SoundEvent, SoundEvent> MOB_SPAWNS;
    public static final DeferredHolder<SoundEvent, SoundEvent> PIPES_LINKED;
    public static final DeferredHolder<SoundEvent, SoundEvent> PIPE_CLOSES;
    public static final DeferredHolder<SoundEvent, SoundEvent> PIPE_OPENS;
    public static final DeferredHolder<SoundEvent, SoundEvent> PIPE_WARPS;
    public static final DeferredHolder<SoundEvent, SoundEvent> POWER_UP_SPAWNS;
    public static final DeferredHolder<SoundEvent, SoundEvent> WATER_SPOUT_BREAK;
    public static final DeferredHolder<SoundEvent, SoundEvent> WATER_SPOUT_FALL;
    public static final DeferredHolder<SoundEvent, SoundEvent> WATER_SPOUT_HIT;
    public static final DeferredHolder<SoundEvent, SoundEvent> WATER_SPOUT_PLACE;
    public static final DeferredHolder<SoundEvent, SoundEvent> WATER_SPOUT_STEP;
    public static final DeferredHolder<SoundEvent, SoundEvent> WRENCH_BOUND;

    static {
        BLOCK_BONK = Marioverse.SOUNDS.register("block.block_bonk",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.block_bonk")));
        COIN_PICKUP = Marioverse.SOUNDS.register("block.coin_pickup",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.coin_pickup")));
        COIN_PLACE = Marioverse.SOUNDS.register("block.coin_place",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.coin_place")));

        DAMAGE_TAKEN = Marioverse.SOUNDS.register("block.damage_taken",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.damage_taken")));

        ITEM_SPAWNS = Marioverse.SOUNDS.register("block.item_spawns",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.item_spawns")));
        MOB_SPAWNS = Marioverse.SOUNDS.register("block.mob_spawns",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.mob_spawns")));

        PIPES_LINKED = Marioverse.SOUNDS.register("block.pipes_linked",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.pipes_linked")));
        PIPE_CLOSES = Marioverse.SOUNDS.register("block.pipe_closes",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.pipe_closes")));
        PIPE_OPENS = Marioverse.SOUNDS.register("block.pipe_opens",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.pipe_opens")));
        PIPE_WARPS = Marioverse.SOUNDS.register("block.pipe_warps",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.pipe_warps")));

        POWER_UP_SPAWNS = Marioverse.SOUNDS.register("block.power_up_spawns",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.power_up_spawns")));

        WATER_SPOUT_BREAK = Marioverse.SOUNDS.register("block.water_spout.break",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.water_spout.break")));
        WATER_SPOUT_FALL = Marioverse.SOUNDS.register("block.water_spout.fall",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.water_spout.fall")));
        WATER_SPOUT_HIT = Marioverse.SOUNDS.register("block.water_spout.hit",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.water_spout.hit")));
        WATER_SPOUT_PLACE = Marioverse.SOUNDS.register("block.water_spout.place",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.water_spout.place")));
        WATER_SPOUT_STEP = Marioverse.SOUNDS.register("block.water_spout.step",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.water_spout.step")));
        WRENCH_BOUND = Marioverse.SOUNDS.register("item.wrench_bound",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "item.wrench_bound")));
    }

    public static void init()
    {}
}
