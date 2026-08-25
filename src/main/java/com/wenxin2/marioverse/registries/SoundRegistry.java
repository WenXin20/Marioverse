package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

public class SoundRegistry {
    public static final DeferredHolder<SoundEvent, SoundEvent> ABILITY_APPLIED;
    public static final DeferredHolder<SoundEvent, SoundEvent> AMETHYST_BUTTON_CLICK_OFF;
    public static final DeferredHolder<SoundEvent, SoundEvent> AMETHYST_BUTTON_CLICK_ON;
    public static final DeferredHolder<SoundEvent, SoundEvent> AMETHYST_PRESSURE_PLATE_CLICK_OFF;
    public static final DeferredHolder<SoundEvent, SoundEvent> AMETHYST_PRESSURE_PLATE_CLICK_ON;
    public static final DeferredHolder<SoundEvent, SoundEvent> ARROW_ROTATES;
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_BONK;
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_BOUNCE;
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_SMASH;
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_SMASH_FAIL;
    public static final DeferredHolder<SoundEvent, SoundEvent> BOO_DEATH;
    public static final DeferredHolder<SoundEvent, SoundEvent> BOO_HIDE;
    public static final DeferredHolder<SoundEvent, SoundEvent> BOO_HURT;
    public static final DeferredHolder<SoundEvent, SoundEvent> BOO_LAUGH;
    public static final DeferredHolder<SoundEvent, SoundEvent> BOO_POOF;
    public static final DeferredHolder<SoundEvent, SoundEvent> CHECKPOINT_FLAG_CLAIMED;
    public static final DeferredHolder<SoundEvent, SoundEvent> CHEEP_CHEEP_DEATH;
    public static final DeferredHolder<SoundEvent, SoundEvent> CHEEP_CHEEP_FLOP;
    public static final DeferredHolder<SoundEvent, SoundEvent> CHEEP_CHEEP_HURT;
    public static final DeferredHolder<SoundEvent, SoundEvent> CHEEP_CHEEP_JUMP;
    public static final DeferredHolder<SoundEvent, SoundEvent> CHEEP_CHEEP_SPLASH;
    public static final DeferredHolder<SoundEvent, SoundEvent> CHEEP_CHEEP_SWIM;
    public static final DeferredHolder<SoundEvent, SoundEvent> CLEAR_PIPE_ENTER;
    public static final DeferredHolder<SoundEvent, SoundEvent> CLEAR_PIPE_EXIT;
    public static final DeferredHolder<SoundEvent, SoundEvent> CLEAR_PIPE_INSIDE;
    public static final DeferredHolder<SoundEvent, SoundEvent> COIN_PICKUP;
    public static final DeferredHolder<SoundEvent, SoundEvent> COIN_PLACE;
    public static final DeferredHolder<SoundEvent, SoundEvent> DAMAGE_TAKEN;
    public static final DeferredHolder<SoundEvent, SoundEvent> DRY_BONES_AMBIENT;
    public static final DeferredHolder<SoundEvent, SoundEvent> DRY_BONES_DEATH;
    public static final DeferredHolder<SoundEvent, SoundEvent> DRY_BONES_HURT;
    public static final DeferredHolder<SoundEvent, SoundEvent> DRY_BONES_REASSEMBLE;
    public static final DeferredHolder<SoundEvent, SoundEvent> FIREBALL_EXTINGUISHED;
    public static final DeferredHolder<SoundEvent, SoundEvent> FIREBALL_SIZZLES;
    public static final DeferredHolder<SoundEvent, SoundEvent> FIREBALL_THROWN;
    public static final DeferredHolder<SoundEvent, SoundEvent> GLASS_HIT;
    public static final DeferredHolder<SoundEvent, SoundEvent> GLASS_FALL;
    public static final DeferredHolder<SoundEvent, SoundEvent> GLASS_STEP;
    public static final DeferredHolder<SoundEvent, SoundEvent> GOAL_POLE_FINISH;
    public static final DeferredHolder<SoundEvent, SoundEvent> GOOMBA_AMBIENT;
    public static final DeferredHolder<SoundEvent, SoundEvent> GOOMBA_BUMP;
    public static final DeferredHolder<SoundEvent, SoundEvent> GOOMBA_DEATH;
    public static final DeferredHolder<SoundEvent, SoundEvent> GOOMBA_HURT;
    public static final DeferredHolder<SoundEvent, SoundEvent> GOOMBA_RUN;
    public static final DeferredHolder<SoundEvent, SoundEvent> GOOMBA_STEP;
    public static final DeferredHolder<SoundEvent, SoundEvent> GOOMBA_STOMP;
    public static final DeferredHolder<SoundEvent, SoundEvent> HEFTY_GOOMBA_BUMP;
    public static final DeferredHolder<SoundEvent, SoundEvent> HEFTY_GOOMBA_HURT;
    public static final DeferredHolder<SoundEvent, SoundEvent> HEFTY_GOOMBA_RUN;
    public static final DeferredHolder<SoundEvent, SoundEvent> HEFTY_GOOMBA_STEP;
    public static final DeferredHolder<SoundEvent, SoundEvent> HEFTY_GOOMBA_STOMP;
    public static final DeferredHolder<SoundEvent, SoundEvent> ICE_BALL_BOUNCED;
    public static final DeferredHolder<SoundEvent, SoundEvent> ICE_BALL_EXTINGUISHED_FIREBALL;
    public static final DeferredHolder<SoundEvent, SoundEvent> ICE_BALL_FROZE_ENEMY;
    public static final DeferredHolder<SoundEvent, SoundEvent> ICE_BALL_SHATTERED;
    public static final DeferredHolder<SoundEvent, SoundEvent> ICE_BALL_SHATTERED_ON_ENEMY;
    public static final DeferredHolder<SoundEvent, SoundEvent> ICE_BALL_THROWN;
    public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_INSERTED;
    public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_SPAWNS;
    public static final DeferredHolder<SoundEvent, SoundEvent> KOOPA_SHELL_BOUNCED;
    public static final DeferredHolder<SoundEvent, SoundEvent> KOOPA_SHELL_SHATTER;
    public static final DeferredHolder<SoundEvent, SoundEvent> KOOPA_SHELL_STOMP;
    public static final DeferredHolder<SoundEvent, SoundEvent> KOOPA_SHELL_THROWN;
    public static final DeferredHolder<SoundEvent, SoundEvent> KOOPA_SHELL_THROWN_UP;
    public static final DeferredHolder<SoundEvent, SoundEvent> KOOPA_TROOPA_AMBIENT;
    public static final DeferredHolder<SoundEvent, SoundEvent> KOOPA_TROOPA_DEATH;
    public static final DeferredHolder<SoundEvent, SoundEvent> KOOPA_TROOPA_HURT;
    public static final DeferredHolder<SoundEvent, SoundEvent> KOOPA_TROOPA_STOMP;
    public static final DeferredHolder<SoundEvent, SoundEvent> MEGA_GOOMBA_BUMP;
    public static final DeferredHolder<SoundEvent, SoundEvent> MEGA_GOOMBA_HURT;
    public static final DeferredHolder<SoundEvent, SoundEvent> MEGA_GOOMBA_RUN;
    public static final DeferredHolder<SoundEvent, SoundEvent> MEGA_GOOMBA_STEP;
    public static final DeferredHolder<SoundEvent, SoundEvent> MEGA_GOOMBA_STOMP;
    public static final DeferredHolder<SoundEvent, SoundEvent> MEGA_MUSHROOM_SPAWNS;
    public static final DeferredHolder<SoundEvent, SoundEvent> MEGA_MUSHROOM_THEME;
    public static final DeferredHolder<SoundEvent, SoundEvent> MINI_GOOMBA_ATTACH;
    public static final DeferredHolder<SoundEvent, SoundEvent> MINI_GOOMBA_BUMP;
    public static final DeferredHolder<SoundEvent, SoundEvent> MINI_GOOMBA_DEFEATED;
    public static final DeferredHolder<SoundEvent, SoundEvent> MINI_GOOMBA_HURT;
    public static final DeferredHolder<SoundEvent, SoundEvent> MINI_GOOMBA_RUN;
    public static final DeferredHolder<SoundEvent, SoundEvent> MINI_GOOMBA_STEP;
    public static final DeferredHolder<SoundEvent, SoundEvent> MINI_GOOMBA_STOMP;
    public static final DeferredHolder<SoundEvent, SoundEvent> MOB_JUMP;
    public static final DeferredHolder<SoundEvent, SoundEvent> MOB_SPAWNS;
    public static final DeferredHolder<SoundEvent, SoundEvent> ONE_UP_COLLECTED;
    public static final DeferredHolder<SoundEvent, SoundEvent> PAINTING_WARPS;
    public static final DeferredHolder<SoundEvent, SoundEvent> PARROT_IMITATES_BOO;
    public static final DeferredHolder<SoundEvent, SoundEvent> PARROT_IMITATES_CHEEP_CHEEP;
    public static final DeferredHolder<SoundEvent, SoundEvent> PARROT_IMITATES_DRY_BONES;
    public static final DeferredHolder<SoundEvent, SoundEvent> PARROT_IMITATES_GOOMBA;
    public static final DeferredHolder<SoundEvent, SoundEvent> PARROT_IMITATES_HEFTY_GOOMBA;
    public static final DeferredHolder<SoundEvent, SoundEvent> PARROT_IMITATES_KOOPA_SHELL;
    public static final DeferredHolder<SoundEvent, SoundEvent> PARROT_IMITATES_KOOPA_TROOPA;
    public static final DeferredHolder<SoundEvent, SoundEvent> PARROT_IMITATES_MEGA_GOOMBA;
    public static final DeferredHolder<SoundEvent, SoundEvent> PARROT_IMITATES_MINI_GOOMBA;
    public static final DeferredHolder<SoundEvent, SoundEvent> PARROT_IMITATES_PIRANHA_PLANT;
    public static final DeferredHolder<SoundEvent, SoundEvent> PARROT_IMITATES_PORCUPUFFER;
    public static final DeferredHolder<SoundEvent, SoundEvent> PARROT_IMITATES_SPLUNKIN;
    public static final DeferredHolder<SoundEvent, SoundEvent> PARROT_IMITATES_SUPER_STAR;
    public static final DeferredHolder<SoundEvent, SoundEvent> PIPE_CLOSES;
    public static final DeferredHolder<SoundEvent, SoundEvent> PIPE_OPENS;
    public static final DeferredHolder<SoundEvent, SoundEvent> PIPE_WARPS;
    public static final DeferredHolder<SoundEvent, SoundEvent> PIRANHA_PLANT_CHOMP;
    public static final DeferredHolder<SoundEvent, SoundEvent> PIRANHA_PLANT_DEATH;
    public static final DeferredHolder<SoundEvent, SoundEvent> PIRANHA_PLANT_HURT;
    public static final DeferredHolder<SoundEvent, SoundEvent> PLAYER_JUMP;
    public static final DeferredHolder<SoundEvent, SoundEvent> PORCUPUFFER_BLOW_OUT;
    public static final DeferredHolder<SoundEvent, SoundEvent> PORCUPUFFER_BLOW_UP;
    public static final DeferredHolder<SoundEvent, SoundEvent> PORCUPUFFER_DEATH;
    public static final DeferredHolder<SoundEvent, SoundEvent> PORCUPUFFER_FLOP;
    public static final DeferredHolder<SoundEvent, SoundEvent> PORCUPUFFER_HURT;
    public static final DeferredHolder<SoundEvent, SoundEvent> PORCUPUFFER_JUMP;
    public static final DeferredHolder<SoundEvent, SoundEvent> PORCUPUFFER_SPLASH;
    public static final DeferredHolder<SoundEvent, SoundEvent> PORCUPUFFER_STING;
    public static final DeferredHolder<SoundEvent, SoundEvent> PORCUPUFFER_SWIM;
    public static final DeferredHolder<SoundEvent, SoundEvent> POWERS_UP;
    public static final DeferredHolder<SoundEvent, SoundEvent> POWERS_UP_MEGA_MUSHROOM;
    public static final DeferredHolder<SoundEvent, SoundEvent> POWERS_UP_MINI_MUSHROOM;
    public static final DeferredHolder<SoundEvent, SoundEvent> POWERS_UP_SUPER_STAR;
    public static final DeferredHolder<SoundEvent, SoundEvent> POWER_UP_SPAWNS;
    public static final DeferredHolder<SoundEvent, SoundEvent> QUESTION_PANEL_ACTIVATED;
    public static final DeferredHolder<SoundEvent, SoundEvent> QUESTION_PANEL_DEACTIVATED;
    public static final DeferredHolder<SoundEvent, SoundEvent> REFILL_CONFIRMED;
    public static final DeferredHolder<SoundEvent, SoundEvent> SPIKES_EXTEND;
    public static final DeferredHolder<SoundEvent, SoundEvent> SPIKES_PEEK;
    public static final DeferredHolder<SoundEvent, SoundEvent> SPIKES_RETRACT;
    public static final DeferredHolder<SoundEvent, SoundEvent> SPLUNKIN_CRACKS;
    public static final DeferredHolder<SoundEvent, SoundEvent> SPLUNKIN_DEATH;
    public static final DeferredHolder<SoundEvent, SoundEvent> STAR_COIN_PICKUP;
    public static final DeferredHolder<SoundEvent, SoundEvent> SUPER_STAR_BOUNCE;
    public static final DeferredHolder<SoundEvent, SoundEvent> SWITCH_OFF;
    public static final DeferredHolder<SoundEvent, SoundEvent> SWITCH_ON;
    public static final DeferredHolder<SoundEvent, SoundEvent> SWITCH_RADIUS_TOGGLED;
    public static final DeferredHolder<SoundEvent, SoundEvent> SUPER_STAR_THEME;
    public static final DeferredHolder<SoundEvent, SoundEvent> WARP_COMPLETED;
    public static final DeferredHolder<SoundEvent, SoundEvent> WARP_FUEL_FILLS;
    public static final DeferredHolder<SoundEvent, SoundEvent> WATER_SPOUT_BREAK;
    public static final DeferredHolder<SoundEvent, SoundEvent> WATER_SPOUT_FALL;
    public static final DeferredHolder<SoundEvent, SoundEvent> WATER_SPOUT_HIT;
    public static final DeferredHolder<SoundEvent, SoundEvent> WATER_SPOUT_PLACE;
    public static final DeferredHolder<SoundEvent, SoundEvent> WATER_SPOUT_STEP;
    public static final DeferredHolder<SoundEvent, SoundEvent> WATER_MINI_STEP;
    public static final DeferredHolder<SoundEvent, SoundEvent> WRENCH_LINKED_BLOCK;
    public static final DeferredHolder<SoundEvent, SoundEvent> WRENCH_LINK_CREATED;
    public static final DeferredHolder<SoundEvent, SoundEvent> WRENCH_LINK_FAILED;
    public static final DeferredHolder<SoundEvent, SoundEvent> WRENCH_UNLINKED_BLOCK;
    public static final DeferredHolder<SoundEvent, SoundEvent> WRENCH_WARP_CREATED;
    public static final DeferredHolder<SoundEvent, SoundEvent> WRENCH_WARP_LINKED;

    static {
        AMETHYST_BUTTON_CLICK_OFF = Marioverse.SOUNDS.register("block.amethyst_button.click_off",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.amethyst_button.click_off")));
        AMETHYST_BUTTON_CLICK_ON = Marioverse.SOUNDS.register("block.amethyst_button.click_on",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.amethyst_button.click_on")));
        AMETHYST_PRESSURE_PLATE_CLICK_OFF = Marioverse.SOUNDS.register("block.amethyst_pressure_plate.click_off",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.amethyst_pressure_plate.click_off")));
        AMETHYST_PRESSURE_PLATE_CLICK_ON = Marioverse.SOUNDS.register("block.amethyst_pressure_plate.click_on",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.amethyst_pressure_plate.click_on")));

        ABILITY_APPLIED = Marioverse.SOUNDS.register("block.ability_applied",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.ability_applied")));
        ARROW_ROTATES = Marioverse.SOUNDS.register("block.arrow_rotates",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.arrow_rotates")));
        BLOCK_BONK = Marioverse.SOUNDS.register("block.block_bonk",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.block_bonk")));
        BLOCK_BOUNCE = Marioverse.SOUNDS.register("block.block_bounce",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.block_bounce")));
        BLOCK_SMASH = Marioverse.SOUNDS.register("block.block_smash",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.block_smash")));
        BLOCK_SMASH_FAIL = Marioverse.SOUNDS.register("block.block_smash_fail",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.block_smash_fail")));

        CHECKPOINT_FLAG_CLAIMED = Marioverse.SOUNDS.register("block.checkpoint_flag_claimed",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.checkpoint_flag_claimed")));

        CLEAR_PIPE_ENTER = Marioverse.SOUNDS.register("block.clear_pipe_enter",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.clear_pipe_enter")));
        CLEAR_PIPE_EXIT = Marioverse.SOUNDS.register("block.clear_pipe_exit",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.clear_pipe_exit")));
        CLEAR_PIPE_INSIDE = Marioverse.SOUNDS.register("block.clear_pipe_inside",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.clear_pipe_inside")));

        STAR_COIN_PICKUP = Marioverse.SOUNDS.register("block.star_coin_pickup",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.star_coin_pickup")));
        COIN_PICKUP = Marioverse.SOUNDS.register("block.coin_pickup",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.coin_pickup")));
        COIN_PLACE = Marioverse.SOUNDS.register("block.coin_place",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.coin_place")));

        GLASS_HIT = Marioverse.SOUNDS.register("block.glass_hit",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.glass_hit")));
        GLASS_FALL = Marioverse.SOUNDS.register("block.glass_fall",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.glass_fall")));
        GLASS_STEP = Marioverse.SOUNDS.register("block.glass_step",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.glass_step")));

        GOAL_POLE_FINISH = Marioverse.SOUNDS.register("block.goal_pole_finish",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.goal_pole_finish")));

        ITEM_SPAWNS = Marioverse.SOUNDS.register("block.item_spawns",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.item_spawns")));
        MOB_SPAWNS = Marioverse.SOUNDS.register("block.mob_spawns",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.mob_spawns")));

        PIPE_CLOSES = Marioverse.SOUNDS.register("block.pipe_closes",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.pipe_closes")));
        PIPE_OPENS = Marioverse.SOUNDS.register("block.pipe_opens",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.pipe_opens")));
        PIPE_WARPS = Marioverse.SOUNDS.register("block.pipe_warps",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.pipe_warps")));

        MEGA_MUSHROOM_SPAWNS = Marioverse.SOUNDS.register("block.mega_mushroom_spawns",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.mega_mushroom_spawns")));
        POWER_UP_SPAWNS = Marioverse.SOUNDS.register("block.power_up_spawns",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.power_up_spawns")));

        QUESTION_PANEL_ACTIVATED = Marioverse.SOUNDS.register("block.question_panel_activated",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.question_panel_activated")));
        QUESTION_PANEL_DEACTIVATED = Marioverse.SOUNDS.register("block.question_panel_deactivated",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.question_panel_deactivated")));

        REFILL_CONFIRMED = Marioverse.SOUNDS.register("block.refill_confirmed",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.refill_confirmed")));

        SPIKES_EXTEND = Marioverse.SOUNDS.register("block.spikes_extend",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.spikes_extend")));
        SPIKES_PEEK = Marioverse.SOUNDS.register("block.spikes_peek",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.spikes_peek")));
        SPIKES_RETRACT = Marioverse.SOUNDS.register("block.spikes_retract",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.spikes_retract")));

        SWITCH_OFF = Marioverse.SOUNDS.register("block.switch_off",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.switch_off")));
        SWITCH_ON = Marioverse.SOUNDS.register("block.switch_on",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.switch_on")));
        SWITCH_RADIUS_TOGGLED = Marioverse.SOUNDS.register("block.switch_radius_toggled",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.switch_radius_toggled")));

        WARP_COMPLETED = Marioverse.SOUNDS.register("block.warp_fuel.completed",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.warp_fuel.completed")));
        WARP_FUEL_FILLS = Marioverse.SOUNDS.register("block.warp_fuel.fills",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.warp_fuel.fills")));

        WATER_MINI_STEP = Marioverse.SOUNDS.register("block.water_mini_step",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block.water_mini_step")));

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

        DAMAGE_TAKEN = Marioverse.SOUNDS.register("entity.damage_taken",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.damage_taken")));

        BOO_DEATH = Marioverse.SOUNDS.register("entity.boo_death",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.boo_death")));
        BOO_HIDE = Marioverse.SOUNDS.register("entity.boo_hide",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.boo_hide")));
        BOO_HURT = Marioverse.SOUNDS.register("entity.boo_hurt",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.boo_hurt")));
        BOO_LAUGH = Marioverse.SOUNDS.register("entity.boo_laugh",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.boo_laugh")));
        BOO_POOF = Marioverse.SOUNDS.register("entity.boo_poof",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.boo_poof")));

        CHEEP_CHEEP_DEATH = Marioverse.SOUNDS.register("entity.cheep_cheep_death",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.cheep_cheep_death")));
        CHEEP_CHEEP_FLOP = Marioverse.SOUNDS.register("entity.cheep_cheep_flop",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.cheep_cheep_flop")));
        CHEEP_CHEEP_HURT = Marioverse.SOUNDS.register("entity.cheep_cheep_hurt",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.cheep_cheep_hurt")));
        CHEEP_CHEEP_JUMP = Marioverse.SOUNDS.register("entity.cheep_cheep_jump",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.cheep_cheep_jump")));
        CHEEP_CHEEP_SPLASH = Marioverse.SOUNDS.register("entity.cheep_cheep_splash",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.cheep_cheep_splash")));
        CHEEP_CHEEP_SWIM = Marioverse.SOUNDS.register("entity.cheep_cheep_swim",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.cheep_cheep_swim")));

        DRY_BONES_AMBIENT = Marioverse.SOUNDS.register("entity.dry_bones_ambient",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.dry_bones_ambient")));
        DRY_BONES_DEATH = Marioverse.SOUNDS.register("entity.dry_bones_death",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.dry_bones_death")));
        DRY_BONES_HURT = Marioverse.SOUNDS.register("entity.dry_bones_hurt",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.dry_bones_hurt")));
        DRY_BONES_REASSEMBLE = Marioverse.SOUNDS.register("entity.dry_bones_reassemble",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.dry_bones_reassemble")));

        KOOPA_SHELL_BOUNCED = Marioverse.SOUNDS.register("entity.koopa_shell_bounced",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.koopa_shell_bounced")));
        KOOPA_SHELL_SHATTER = Marioverse.SOUNDS.register("entity.koopa_shell_shatter",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.koopa_shell_shatter")));
        KOOPA_SHELL_STOMP = Marioverse.SOUNDS.register("entity.koopa_shell_stomp",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.koopa_shell_stomp")));

        KOOPA_TROOPA_AMBIENT = Marioverse.SOUNDS.register("entity.koopa_troopa_ambient",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.koopa_troopa_ambient")));
        KOOPA_TROOPA_DEATH = Marioverse.SOUNDS.register("entity.koopa_troopa_death",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.koopa_troopa_death")));
        KOOPA_TROOPA_HURT = Marioverse.SOUNDS.register("entity.koopa_troopa_hurt",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.koopa_troopa_hurt")));
        KOOPA_TROOPA_STOMP = Marioverse.SOUNDS.register("entity.koopa_troopa_stomp",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.koopa_troopa_stomp")));

        GOOMBA_AMBIENT = Marioverse.SOUNDS.register("entity.goomba_ambient",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.goomba_ambient")));
        GOOMBA_BUMP = Marioverse.SOUNDS.register("entity.goomba_bump",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.goomba_bump")));
        GOOMBA_DEATH = Marioverse.SOUNDS.register("entity.goomba_death",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.goomba_death")));
        GOOMBA_HURT = Marioverse.SOUNDS.register("entity.goomba_hurt",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.goomba_hurt")));
        GOOMBA_RUN = Marioverse.SOUNDS.register("entity.goomba_run",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.goomba_run")));
        GOOMBA_STEP = Marioverse.SOUNDS.register("entity.goomba_step",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.goomba_step")));
        GOOMBA_STOMP = Marioverse.SOUNDS.register("entity.goomba_stomp",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.goomba_stomp")));

        HEFTY_GOOMBA_BUMP = Marioverse.SOUNDS.register("entity.hefty_goomba_bump",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.hefty_goomba_bump")));
        HEFTY_GOOMBA_HURT = Marioverse.SOUNDS.register("entity.hefty_goomba_hurt",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.hefty_goomba_hurt")));
        HEFTY_GOOMBA_RUN = Marioverse.SOUNDS.register("entity.hefty_goomba_run",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.hefty_goomba_run")));
        HEFTY_GOOMBA_STEP = Marioverse.SOUNDS.register("entity.hefty_goomba_step",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.hefty_goomba_step")));
        HEFTY_GOOMBA_STOMP = Marioverse.SOUNDS.register("entity.hefty_goomba_stomp",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.hefty_goomba_stomp")));

        MEGA_GOOMBA_BUMP = Marioverse.SOUNDS.register("entity.mega_goomba_bump",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.mega_goomba_bump")));
        MEGA_GOOMBA_HURT = Marioverse.SOUNDS.register("entity.mega_goomba_hurt",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.mega_goomba_hurt")));
        MEGA_GOOMBA_RUN = Marioverse.SOUNDS.register("entity.mega_goomba_run",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.mega_goomba_run")));
        MEGA_GOOMBA_STEP = Marioverse.SOUNDS.register("entity.mega_goomba_step",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.mega_goomba_step")));
        MEGA_GOOMBA_STOMP = Marioverse.SOUNDS.register("entity.mega_goomba_stomp",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.mega_goomba_stomp")));

        MINI_GOOMBA_ATTACH = Marioverse.SOUNDS.register("entity.mini_goomba_attach",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.mini_goomba_attach")));
        MINI_GOOMBA_BUMP = Marioverse.SOUNDS.register("entity.mini_goomba_bump",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.mini_goomba_bump")));
        MINI_GOOMBA_DEFEATED = Marioverse.SOUNDS.register("entity.mini_goomba_defeated",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.mini_goomba_defeated")));
        MINI_GOOMBA_HURT = Marioverse.SOUNDS.register("entity.mini_goomba_hurt",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.mini_goomba_hurt")));
        MINI_GOOMBA_RUN = Marioverse.SOUNDS.register("entity.mini_goomba_run",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.mini_goomba_run")));
        MINI_GOOMBA_STEP = Marioverse.SOUNDS.register("entity.mini_goomba_step",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.mini_goomba_step")));
        MINI_GOOMBA_STOMP = Marioverse.SOUNDS.register("entity.mini_goomba_stomp",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.mini_goomba_stomp")));

        PAINTING_WARPS = Marioverse.SOUNDS.register("entity.painting_warps",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.painting_warps")));

        PIRANHA_PLANT_CHOMP = Marioverse.SOUNDS.register("entity.piranha_plant_chomp",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.piranha_plant_chomp")));
        PIRANHA_PLANT_DEATH = Marioverse.SOUNDS.register("entity.piranha_plant_death",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.piranha_plant_death")));
        PIRANHA_PLANT_HURT = Marioverse.SOUNDS.register("entity.piranha_plant_hurt",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.piranha_plant_hurt")));

        PORCUPUFFER_BLOW_OUT = Marioverse.SOUNDS.register("entity.porcupuffer_blow_out",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.porcupuffer_blow_out")));
        PORCUPUFFER_BLOW_UP = Marioverse.SOUNDS.register("entity.porcupuffer_blow_up",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.porcupuffer_blow_up")));
        PORCUPUFFER_DEATH = Marioverse.SOUNDS.register("entity.porcupuffer_death",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.porcupuffer_death")));
        PORCUPUFFER_FLOP = Marioverse.SOUNDS.register("entity.porcupuffer_flop",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.porcupuffer_flop")));
        PORCUPUFFER_HURT = Marioverse.SOUNDS.register("entity.porcupuffer_hurt",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.porcupuffer_hurt")));
        PORCUPUFFER_JUMP = Marioverse.SOUNDS.register("entity.porcupuffer_jump",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.porcupuffer_jump")));
        PORCUPUFFER_SPLASH = Marioverse.SOUNDS.register("entity.porcupuffer_splash",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.porcupuffer_splash")));
        PORCUPUFFER_STING = Marioverse.SOUNDS.register("entity.porcupuffer_sting",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.porcupuffer_sting")));
        PORCUPUFFER_SWIM = Marioverse.SOUNDS.register("entity.porcupuffer_swim",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.porcupuffer_swim")));

        SPLUNKIN_CRACKS = Marioverse.SOUNDS.register("entity.splunkin_cracks",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.splunkin_cracks")));
        SPLUNKIN_DEATH = Marioverse.SOUNDS.register("entity.splunkin_death",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.splunkin_death")));

        ONE_UP_COLLECTED = Marioverse.SOUNDS.register("entity.one_up_collected",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.one_up_collected")));
        POWERS_UP = Marioverse.SOUNDS.register("entity.powers_up",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.powers_up")));
        POWERS_UP_MEGA_MUSHROOM = Marioverse.SOUNDS.register("entity.powers_up_mega_mushroom",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.powers_up_mega_mushroom")));
        POWERS_UP_MINI_MUSHROOM = Marioverse.SOUNDS.register("entity.powers_up_mini_mushroom",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.powers_up_mini_mushroom")));
        POWERS_UP_SUPER_STAR = Marioverse.SOUNDS.register("entity.powers_up_super_star",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.powers_up_super_star")));

        MEGA_MUSHROOM_THEME = Marioverse.SOUNDS.register("entity.mega_mushroom_theme",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.mega_mushroom_theme")));

        SUPER_STAR_BOUNCE = Marioverse.SOUNDS.register("entity.super_star_bounce",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.super_star_bounce")));
        SUPER_STAR_THEME = Marioverse.SOUNDS.register("entity.super_star_theme",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.super_star_theme")));

        MOB_JUMP = Marioverse.SOUNDS.register("entity.jump",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.jump")));
        PLAYER_JUMP = Marioverse.SOUNDS.register("player.jump",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "player.jump")));

        PARROT_IMITATES_BOO = Marioverse.SOUNDS.register("entity.parrot.imitate.boo",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.parrot.imitate.boo")));
        PARROT_IMITATES_CHEEP_CHEEP = Marioverse.SOUNDS.register("entity.parrot.imitate.cheep_cheep",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.parrot.imitate.cheep_cheep")));
        PARROT_IMITATES_DRY_BONES = Marioverse.SOUNDS.register("entity.parrot.imitate.dry_bones",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.parrot.imitate.dry_bones")));
        PARROT_IMITATES_GOOMBA = Marioverse.SOUNDS.register("entity.parrot.imitate.goomba",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.parrot.imitate.goomba")));
        PARROT_IMITATES_HEFTY_GOOMBA = Marioverse.SOUNDS.register("entity.parrot.imitate.hefty_goomba",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.parrot.imitate.hefty_goomba")));
        PARROT_IMITATES_KOOPA_SHELL = Marioverse.SOUNDS.register("entity.parrot.imitate.koopa_shell",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.parrot.imitate.koopa_shell")));
        PARROT_IMITATES_KOOPA_TROOPA = Marioverse.SOUNDS.register("entity.parrot.imitate.koopa_troopa",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.parrot.imitate.koopa_troopa")));
        PARROT_IMITATES_MEGA_GOOMBA = Marioverse.SOUNDS.register("entity.parrot.imitate.mega_goomba",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.parrot.imitate.mega_goomba")));
        PARROT_IMITATES_MINI_GOOMBA = Marioverse.SOUNDS.register("entity.parrot.imitate.mini_goomba",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.parrot.imitate.mini_goomba")));
        PARROT_IMITATES_PIRANHA_PLANT = Marioverse.SOUNDS.register("entity.parrot.imitate.piranha_plant",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.parrot.imitate.piranha_plant")));
        PARROT_IMITATES_PORCUPUFFER = Marioverse.SOUNDS.register("entity.parrot.imitate.porcupuffer",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.parrot.imitate.porcupuffer")));
        PARROT_IMITATES_SPLUNKIN = Marioverse.SOUNDS.register("entity.parrot.imitate.splunkin",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.parrot.imitate.splunkin")));
        PARROT_IMITATES_SUPER_STAR = Marioverse.SOUNDS.register("entity.parrot.imitate.super_star",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity.parrot.imitate.super_star")));

        ITEM_INSERTED = Marioverse.SOUNDS.register("item.item_inserted",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "item.item_inserted")));
        WRENCH_LINKED_BLOCK = Marioverse.SOUNDS.register("item.wrench_linked_block",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "item.wrench_linked_block")));
        WRENCH_LINK_CREATED = Marioverse.SOUNDS.register("item.wrench_link_created",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "item.wrench_link_created")));
        WRENCH_LINK_FAILED = Marioverse.SOUNDS.register("item.wrench_link_failed",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "item.wrench_link_failed")));
        WRENCH_WARP_CREATED = Marioverse.SOUNDS.register("item.wrench_warp_created",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "item.wrench_warp_created")));
        WRENCH_WARP_LINKED = Marioverse.SOUNDS.register("item.wrench_warp_linked",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "item.wrench_warp_linked")));
        WRENCH_UNLINKED_BLOCK = Marioverse.SOUNDS.register("item.wrench_unlinked_block",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "item.wrench_unlinked_block")));

        FIREBALL_THROWN = Marioverse.SOUNDS.register("player.fireball_thrown",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "player.fireball_thrown")));
        ICE_BALL_THROWN = Marioverse.SOUNDS.register("player.ice_ball_thrown",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "player.ice_ball_thrown")));
        KOOPA_SHELL_THROWN = Marioverse.SOUNDS.register("player.koopa_shell_thrown",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "player.koopa_shell_thrown")));
        KOOPA_SHELL_THROWN_UP = Marioverse.SOUNDS.register("player.koopa_shell_thrown_up",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "player.koopa_shell_thrown_up")));

        FIREBALL_SIZZLES = Marioverse.SOUNDS.register("projectile.fireball_sizzles",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "projectile.fireball_sizzles")));
        FIREBALL_EXTINGUISHED = Marioverse.SOUNDS.register("projectile.fireball_extinguished",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "projectile.fireball_extinguished")));
        ICE_BALL_BOUNCED = Marioverse.SOUNDS.register("projectile.ice_ball_bounced",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "projectile.ice_ball_bounced")));
        ICE_BALL_EXTINGUISHED_FIREBALL = Marioverse.SOUNDS.register("projectile.ice_ball_extinguished_fireball",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "projectile.ice_ball_extinguished_fireball")));
        ICE_BALL_FROZE_ENEMY = Marioverse.SOUNDS.register("projectile.ice_ball_froze_enemy",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "projectile.ice_ball_froze_enemy")));
        ICE_BALL_SHATTERED = Marioverse.SOUNDS.register("projectile.ice_ball_shattered",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "projectile.ice_ball_shattered")));
        ICE_BALL_SHATTERED_ON_ENEMY = Marioverse.SOUNDS.register("projectile.ice_ball_shattered_on_enemy",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "projectile.ice_ball_shattered_on_enemy")));
    }

    public static void init() {}
}
