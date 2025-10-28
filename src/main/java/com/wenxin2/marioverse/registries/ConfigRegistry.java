package com.wenxin2.marioverse.registries;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ConfigRegistry
{
    public static final ConfigRegistry INSTANCE = new ConfigRegistry();

    public static final String CATEGORY_CLIENT = "client";
    public static final String CATEGORY_COMMON = "common";
    public static final String CATEGORY_DEBUG = "debug";

    public static final String CATEGORY_BLOCKS = "blocks";
    public static final String CATEGORY_GAMEPLAY = "gameplay";
    public static final String CATEGORY_HOLIDAY = "holiday";
    public static final String CATEGORY_ITEMS = "items";
    public static final String CATEGORY_MISC = "misc";
    public static final String CATEGORY_MOBS = "mobs";
    public static final String CATEGORY_POWER_UPS = "power_ups";
    public static final String CATEGORY_TELEPORTATION = "teleportation";

    public static final String CATEGORY_CHECKPOINT_FLAGS = "checkpoint_flags";
    public static final String CATEGORY_COINS = "coins";
    public static final String CATEGORY_DECORATED_POTS = "decorated_pots";
    public static final String CATEGORY_IRON_SPIKES = "iron_spikes";
    public static final String CATEGORY_QUESTION_BLOCKS = "question_blocks";
    public static final String CATEGORY_SPIKE_PANELS = "spike_panels";
    public static final String CATEGORY_STAR_COINS = "star_coins";
    public static final String CATEGORY_WARP_DOORS = "warp_doors";
    public static final String CATEGORY_WARP_PAINTINGS = "warp_paintings";
    public static final String CATEGORY_WARP_PIPES = "warp_pipes";
    public static final String CATEGORY_WARP_TRAPDOORS = "warp_trapdoors";
    public static final String CATEGORY_WATER_SPOUTS = "water_spouts";

    public static final String CATEGORY_WARP_DISRUPTOR = "warp_disruptor";
    public static final String CATEGORY_WRENCH = "wrench";

    public static final String CATEGORY_BOO = "boo";
    public static final String CATEGORY_DRY_BONES = "dry_bones";
    public static final String CATEGORY_GOLD_KOOPA_SHELL = "gold_koopa_shell";
    public static final String CATEGORY_GOLD_KOOPA_TROOPA = "gold_koopa_troopa";
    public static final String CATEGORY_GOOMBAS = "goombas";
    public static final String CATEGORY_GREEN_KOOPA_SHELL = "green_koopa_shell";
    public static final String CATEGORY_GREEN_KOOPA_TROOPA = "green_koopa_troopa";
    public static final String CATEGORY_HEFTY_GOOMBA = "hefty_goomba";
    public static final String CATEGORY_KOOPA_SHELLS = "koopa_shells";
    public static final String CATEGORY_KOOPA_TROOPAS = "koopa_troopas";
    public static final String CATEGORY_MEGA_GOOMBA = "mega_goomba";
    public static final String CATEGORY_MINI_GOOMBA = "mini_goomba";
    public static final String CATEGORY_PIRANHA_PLANT = "piranha_plant";
    public static final String CATEGORY_RED_KOOPA_SHELL = "red_koopa_shell";
    public static final String CATEGORY_RED_KOOPA_TROOPA = "red_koopa_troopa";

    public static final String CATEGORY_DASH_MUSHROOM = "dash_mushroom";
    public static final String CATEGORY_FIRE_FLOWER = "fire_flower";
    public static final String CATEGORY_ICE_FLOWER = "ice_flower";
    public static final String CATEGORY_ONE_UP = "one_up";
    public static final String CATEGORY_SUPER_MUSHROOM = "super_mushroom";
    public static final String CATEGORY_SUPER_STAR = "super_star";

    public static final String CATEGORY_HALLOWEEN = "halloween";

    private final ModConfigSpec CONFIG_SPEC;
    public static ModConfigSpec.BooleanValue ALLOW_FAST_TRAVEL;
    public static ModConfigSpec.BooleanValue ALLOW_WARP_UNWAXING;
    public static ModConfigSpec.BooleanValue ALL_MOBS_CAN_STOMP;
    public static ModConfigSpec.BooleanValue BLINDNESS_EFFECT;
    public static ModConfigSpec.BooleanValue CHECKPOINT_FLAG_ADD_ITEMS;
    public static ModConfigSpec.BooleanValue CHECKPOINT_FLAG_APPLIES_POWER_UPS;
    public static ModConfigSpec.BooleanValue CHECKPOINT_FLAG_BUCKET_TWEAKS;
    public static ModConfigSpec.BooleanValue CHECKPOINT_FLAG_CLAIM_USES_ITEMS;
    public static ModConfigSpec.BooleanValue CHECKPOINT_FLAG_MODIFY_HEALTH;
    public static ModConfigSpec.BooleanValue CHECKPOINT_FLAG_REMOVE_ITEMS;
    public static ModConfigSpec.BooleanValue CHECKPOINT_FLAG_RESPAWN_USES_ITEMS;
    public static ModConfigSpec.BooleanValue CHECKPOINT_FLAG_SPAWNS_MOBS;
    public static ModConfigSpec.BooleanValue COINS_COLLECTED_IN_CREATIVE;
    public static ModConfigSpec.BooleanValue COINS_COLLECTED_ON_COLLISION;
    public static ModConfigSpec.BooleanValue CREATIVE_BUBBLES;
    public static ModConfigSpec.BooleanValue CREATIVE_CLOSE_PIPES;
    public static ModConfigSpec.BooleanValue CREATIVE_WATER_SPOUT;
    public static ModConfigSpec.BooleanValue CREATIVE_WRENCH_LINKING;
    public static ModConfigSpec.BooleanValue DAMAGE_SHRINKS_ALL_MOBS;
    public static ModConfigSpec.BooleanValue DAMAGE_SHRINKS_PLAYERS;
    public static ModConfigSpec.BooleanValue DEBUG_PIPE_BUBBLES_SELECTION_BOX;
    public static ModConfigSpec.BooleanValue DEBUG_SELECTION_BOX;
    public static ModConfigSpec.BooleanValue DEBUG_SELECTION_BOX_CREATIVE;
    public static ModConfigSpec.BooleanValue DEBUG_WATER_SPOUT_SELECTION_BOX;
    public static ModConfigSpec.BooleanValue DECORATED_POT_BUCKET_TWEAKS;
    public static ModConfigSpec.BooleanValue DECORATED_POT_SPAWNS_MOBS;
    public static ModConfigSpec.BooleanValue DECORATED_POT_SPAWNS_POWER_UPS;
    public static ModConfigSpec.BooleanValue DISABLE_BOO_MASKS;
    public static ModConfigSpec.BooleanValue DISABLE_CONSECUTIVE_BOUNCING;
    public static ModConfigSpec.BooleanValue DISABLE_DECORATED_POT_TWEAKS;
    public static ModConfigSpec.BooleanValue DISABLE_DRY_BONES_MASKS;
    public static ModConfigSpec.BooleanValue DISABLE_GOOMBA_MASKS;
    public static ModConfigSpec.BooleanValue DISABLE_JEB_SHADER;
    public static ModConfigSpec.BooleanValue DISABLE_JUMP_SOUND;
    public static ModConfigSpec.BooleanValue DISABLE_KOOPA_MASKS;
    public static ModConfigSpec.BooleanValue DISABLE_MARIOVERSE_TABS;
    public static ModConfigSpec.BooleanValue DISABLE_PLAYER_WARP_DISRUPTING;
    public static ModConfigSpec.BooleanValue DISABLE_REWARD_PARTICLES;
    public static ModConfigSpec.BooleanValue DISABLE_TEXT;
    public static ModConfigSpec.BooleanValue DISABLE_VANILLA_TABS;
    public static ModConfigSpec.BooleanValue DISABLE_WARP_DOORS;
    public static ModConfigSpec.BooleanValue DISABLE_WARP_PAINTINGS;
    public static ModConfigSpec.BooleanValue DISABLE_WARP_TRAPDOORS;
    public static ModConfigSpec.BooleanValue DISPLAY_BUTTON_TOOLTIP;
    public static ModConfigSpec.BooleanValue ENABLE_STOMPABLE_ENEMIES;
    public static ModConfigSpec.BooleanValue EQUIP_COSTUMES_MOBS;
    public static ModConfigSpec.BooleanValue EQUIP_COSTUMES_PLAYERS;
    public static ModConfigSpec.BooleanValue FIRE_FLOWER_POWERS_ALL_MOBS;
    public static ModConfigSpec.BooleanValue FORCE_BOO_MASKS;
    public static ModConfigSpec.BooleanValue FORCE_DRY_BONES_MASKS;
    public static ModConfigSpec.BooleanValue FORCE_GOOMBA_MASKS;
    public static ModConfigSpec.BooleanValue FORCE_KOOPA_MASKS;
    public static ModConfigSpec.BooleanValue ICE_FLOWER_POWERS_ALL_MOBS;
    public static ModConfigSpec.BooleanValue MINI_GOOMBAS_ATTACH_ALL_MOBS;
    public static ModConfigSpec.BooleanValue MINI_GOOMBAS_PUSH;
    public static ModConfigSpec.BooleanValue SUPER_MUSHROOM_POWERS_ALL_MOBS;
    public static ModConfigSpec.BooleanValue ONE_UP_HEALS_ALL_MOBS;
    public static ModConfigSpec.BooleanValue QUESTION_ADD_ITEMS;
    public static ModConfigSpec.BooleanValue QUESTION_BUCKET_TWEAKS;
    public static ModConfigSpec.BooleanValue QUESTION_REMOVE_ITEMS;
    public static ModConfigSpec.BooleanValue QUESTION_SPAWNS_MOBS;
    public static ModConfigSpec.BooleanValue QUESTION_SPAWNS_POWER_UPS;
    public static ModConfigSpec.BooleanValue REDSTONE_OPENS_QUESTION;
    public static ModConfigSpec.BooleanValue RENDER_ONE_UP_CHARM;
    public static ModConfigSpec.BooleanValue RUNNING_ACTIVATES_POWER_UPS;
    public static ModConfigSpec.BooleanValue SELECT_INVISIBLE_QUESTION;
    public static ModConfigSpec.BooleanValue STAR_COINS_COLLECTED_IN_CREATIVE;
    public static ModConfigSpec.BooleanValue STAR_COINS_COLLECTED_ON_COLLISION;
    public static ModConfigSpec.BooleanValue STOMP_ALL_MOBS;
    public static ModConfigSpec.BooleanValue SUPER_STAR_POWERS_ALL_MOBS;
    public static ModConfigSpec.BooleanValue TELEPORT_MOBS;
    public static ModConfigSpec.BooleanValue TELEPORT_NON_MOBS;
    public static ModConfigSpec.BooleanValue TELEPORT_PLAYERS;
    public static ModConfigSpec.BooleanValue WARP_COOLDOWN_MESSAGE;
    public static ModConfigSpec.BooleanValue WARP_COOLDOWN_MESSAGE_TICKS;
    public static ModConfigSpec.BooleanValue WATER_SPOUTS_BUCKETABLE;
    public static ModConfigSpec.BooleanValue WAX_DISABLES_BUBBLES;
    public static ModConfigSpec.BooleanValue WAX_DISABLES_CLOSING;
    public static ModConfigSpec.BooleanValue WAX_DISABLES_RENAMING;
    public static ModConfigSpec.BooleanValue WAX_DISABLES_WARP_LINKING;
    public static ModConfigSpec.BooleanValue WAX_DISABLES_WATER_SPOUTS;

    public static ModConfigSpec.DoubleValue CHECKPOINT_FLAG_RESPAWN_HEALTH;
    public static ModConfigSpec.DoubleValue DASH_MUSHROOM_BOOST_STRENGTH;
    public static ModConfigSpec.DoubleValue DASH_MUSHROOM_HEALTH_HEALED;
    public static ModConfigSpec.DoubleValue FIREBALL_DAMAGE;
    public static ModConfigSpec.DoubleValue GOLD_KOOPA_SHELL_DAMAGE;
    public static ModConfigSpec.DoubleValue GREEN_KOOPA_SHELL_DAMAGE;
    public static ModConfigSpec.DoubleValue ICE_BALL_DAMAGE;
    public static ModConfigSpec.DoubleValue ICE_CUBE_DAMAGE;
    public static ModConfigSpec.DoubleValue IRON_SPIKE_DAMAGE;
    public static ModConfigSpec.DoubleValue ONE_UP_HEALTH_HEALED;
    public static ModConfigSpec.DoubleValue RED_KOOPA_SHELL_DAMAGE;
    public static ModConfigSpec.DoubleValue SHRINK_MOBS_AT_HEALTH;
    public static ModConfigSpec.DoubleValue SHRINK_PLAYERS_AT_HEALTH;
    public static ModConfigSpec.DoubleValue SPIKE_PANEL_DAMAGE;
    public static ModConfigSpec.DoubleValue STOMP_BOUNCE_HEIGHT;
    public static ModConfigSpec.DoubleValue STOMP_BOUNCE_HEIGHT_JUMP;
    public static ModConfigSpec.DoubleValue STOMP_DAMAGE;
    public static ModConfigSpec.DoubleValue SUPER_MUSHROOM_HEALTH_HEALED;
    public static ModConfigSpec.DoubleValue SUPER_STAR_DAMAGE;
    public static ModConfigSpec.DoubleValue VEHICLE_MUSHROOM_BOOST_STRENGTH;

    public static ModConfigSpec.IntValue BOO_LIGHT_LVL_DEATH;
    public static ModConfigSpec.IntValue CHECKPOINT_FLAG_FOOD_AMT;
    public static ModConfigSpec.IntValue DRY_BONES_REASSEMBLE_DURATION;
    public static ModConfigSpec.IntValue DRY_BONES_DEATH_DURATION;
    public static ModConfigSpec.IntValue FIREBALL_COOLDOWN;
    public static ModConfigSpec.IntValue GOLD_KOOPA_SHELL_COIN_CIRCLE_RADIUS;
    public static ModConfigSpec.IntValue GOLD_KOOPA_TROOPA_HIDE_DURATION;
    public static ModConfigSpec.IntValue GOOMBA_SPLIT_COUNT;
    public static ModConfigSpec.IntValue GOOMBA_SPLIT_RANDOM_COUNT;
    public static ModConfigSpec.IntValue GREEN_KOOPA_TROOPA_HIDE_DURATION;
    public static ModConfigSpec.IntValue HEFTY_GOOMBA_SPLIT_COUNT;
    public static ModConfigSpec.IntValue HEFTY_GOOMBA_SPLIT_RANDOM_COUNT;
    public static ModConfigSpec.IntValue ICE_BALL_COOLDOWN;
    public static ModConfigSpec.IntValue ICE_CUBE_FREEZE_DURATION;
    public static ModConfigSpec.IntValue ICE_CUBE_LIFESPAN;
    public static ModConfigSpec.IntValue KOOPA_SHELL_DAMAGE_FROM_KILLS;
    public static ModConfigSpec.IntValue MAX_GOLD_KOOPA_SHELL_CIRCLE_COINS;
    public static ModConfigSpec.IntValue MAX_GOLD_KOOPA_SHELL_TRAIL_COINS;
    public static ModConfigSpec.IntValue MAX_GOOMBA_STACK;
    public static ModConfigSpec.IntValue MAX_ICE_BALL_BOUNCES;
    public static ModConfigSpec.IntValue MAX_KOOPA_SHELL_DAMAGE_POINTS;
    public static ModConfigSpec.IntValue MAX_MOB_FIREBALLS;
    public static ModConfigSpec.IntValue MAX_MOB_ICE_BALLS;
    public static ModConfigSpec.IntValue MAX_ONE_UP_BOUNCE_REWARD;
    public static ModConfigSpec.IntValue MAX_ONE_UP_SHELL_KILL_REWARD;
    public static ModConfigSpec.IntValue MAX_PLAYER_FIREBALLS;
    public static ModConfigSpec.IntValue MAX_PLAYER_ICE_BALLS;
    public static ModConfigSpec.IntValue PIRANHA_PLANT_HIDE_DURATION;
    public static ModConfigSpec.IntValue RED_KOOPA_SHELL_MOB_DETECTION_RADIUS;
    public static ModConfigSpec.IntValue RED_KOOPA_SHELL_PLAYER_DETECTION_RADIUS;
    public static ModConfigSpec.IntValue RED_KOOPA_TROOPA_HIDE_DURATION;
    public static ModConfigSpec.IntValue SUPER_STAR_DURATION;
    public static ModConfigSpec.IntValue SUPER_STAR_SPEED_DURATION;
    public static ModConfigSpec.IntValue WARP_DISRUPTING_COOLDOWN;
    public static ModConfigSpec.IntValue WARP_DOOR_COOLDOWN;
    public static ModConfigSpec.IntValue WARP_DOOR_FUEL_AMT;
    public static ModConfigSpec.IntValue WARP_PAINTING_COOLDOWN;
    public static ModConfigSpec.IntValue WARP_PAINTING_FUEL_AMT;
    public static ModConfigSpec.IntValue WARP_PIPE_COOLDOWN;
    public static ModConfigSpec.IntValue WARP_TRAPDOOR_COOLDOWN;
    public static ModConfigSpec.IntValue WARP_TRAPDOOR_FUEL_AMT;

    private ConfigRegistry() {
        ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
        BUILDER.push(CATEGORY_CLIENT);
            DISABLE_JEB_SHADER = BUILDER.translation("configuration.marioverse.disable_jeb_shader")
                    .comment("Display the rainbow shader on mobs named \"jeb_\".")
                    .comment("§9[Default: false]")
                    .define("disable_jeb_shader", false);
            DISABLE_JUMP_SOUND = BUILDER.translation("configuration.marioverse.disable_jump_sound")
                    .comment("Disable the jump sound when wearing a costume.")
                    .comment("§9[Default: false]")
                    .define("disable_jump_sound", false);
            DISABLE_REWARD_PARTICLES = BUILDER.translation("configuration.marioverse.disable_reward_particles")
                    .comment("Disable reward particles when squashing enemies.")
                    .comment("§9[Default: false]")
                    .define("disable_reward_particles", false);
            DISABLE_TEXT = BUILDER.translation("configuration.marioverse.disable_text")
                    .comment("Disable text rendering on pipes.")
                    .comment("§9[Default: false]")
                    .define("disable_text", false);
            DISPLAY_BUTTON_TOOLTIP = BUILDER.translation("configuration.marioverse.display_button_tooltip")
                    .comment("Display \"ticks to stay pressed\" on button tooltips.")
                    .comment("§9[Default: true]")
                    .define("display_button_tooltip", true);
            RENDER_ONE_UP_CHARM = BUILDER.translation("configuration.marioverse.render_one_up_charm")
                    .comment("Render the 1-Up on the player when in an Accessory slot.")
                    .comment("§9[Default: true]")
                    .define("render_one_up_charm", true);
            WARP_COOLDOWN_MESSAGE = BUILDER.translation("configuration.marioverse.warp_cooldown_message")
                    .comment("Display a warp cooldown message.")
                    .comment("§9[Default: true]")
                    .define("warp_cooldown_message", true);
            WARP_COOLDOWN_MESSAGE_TICKS = BUILDER.translation("configuration.marioverse.warp_cooldown_message_with_ticks")
                    .comment("Display a warp cooldown message with ticks.")
                    .comment("§cRequires \"Warp Cooldown Message\"")
                    .comment("§9[Default: false]")
                    .define("warp_cooldown_message_with_ticks", false);
        BUILDER.pop();

        BUILDER.push(CATEGORY_COMMON);

            BUILDER.push(CATEGORY_BLOCKS);

                BUILDER.push(CATEGORY_COINS);
                    COINS_COLLECTED_ON_COLLISION = BUILDER.translation("configuration.marioverse.coins_collected_on_collision")
                            .comment("Coins can be collected during collision.")
                            .comment("§9[Default: true]")
                            .define("coins_collected_on_collision", true);
                    COINS_COLLECTED_IN_CREATIVE = BUILDER.translation("configuration.marioverse.coins_collected_in_creative")
                            .comment("Coins can be collected by players in creative.")
                            .comment("§9[Default: true]")
                            .define("coins_collected_in_creative", true);
                BUILDER.pop();

                BUILDER.push(CATEGORY_CHECKPOINT_FLAGS);
                    CHECKPOINT_FLAG_MODIFY_HEALTH = BUILDER.translation("configuration.marioverse.checkpoint_flag_modify_health")
                            .comment("Checkpoint flags respawn players with modified health & food levels.")
                            .comment("§9[Default: true]")
                            .define("checkpoint_flag_modify_health", true);
                    CHECKPOINT_FLAG_RESPAWN_HEALTH = BUILDER.translation("configuration.marioverse.checkpoint_flag_respawn_health")
                            .comment("Amount of health checkpoint flags respawn the player at.")
                            .comment("§9[Default: 5.0F]§b")
                            .defineInRange("checkpoint_flag_respawn_health", 10.0F, 0.0F, 100.0F);
                    CHECKPOINT_FLAG_FOOD_AMT = BUILDER.translation("configuration.marioverse.checkpoint_flag_food_amt")
                            .comment("Amount of food checkpoint flags respawn the player with.")
                            .comment("§9[Default: 16]§b")
                            .defineInRange("checkpoint_flag_food_amt", 16, 0, 100);
                    CHECKPOINT_FLAG_CLAIM_USES_ITEMS = BUILDER.translation("configuration.marioverse.checkpoint_flag_claim_uses_item")
                            .comment("Allow checkpoint flags to use stored items when the player claims the checkpoint.")
                            .comment("§9[Default: true]")
                            .define("checkpoint_flag_claim_uses_item", true);
                    CHECKPOINT_FLAG_RESPAWN_USES_ITEMS = BUILDER.translation("configuration.marioverse.checkpoint_flag_respawn_uses_item")
                            .comment("Allow checkpoint flags to use stored items when the player respawns.")
                            .comment("§9[Default: true]")
                            .define("checkpoint_flag_respawn_uses_item", true);
                    CHECKPOINT_FLAG_BUCKET_TWEAKS = BUILDER.translation("configuration.marioverse.checkpoint_flag_bucket_tweaks")
                            .comment("Allow checkpoint flags to place bucket liquids and blocks.")
                            .comment("§9[Default: true]")
                            .define("checkpoint_flag_bucket_tweaks", true);
                    CHECKPOINT_FLAG_APPLIES_POWER_UPS = BUILDER.translation("configuration.marioverse.checkpoint_flag_applies_power_ups")
                            .comment("Allow checkpoint flags to apply power ups when the player respawns.")
                            .comment("§9[Default: true]")
                            .define("checkpoint_flag_applies_power_ups", true);
                    CHECKPOINT_FLAG_SPAWNS_MOBS = BUILDER.translation("configuration.marioverse.checkpoint_flag_spawns_mobs")
                            .comment("Allow checkpoint flags to spawn mobs when the player respawns.")
                            .comment("§9[Default: true]")
                            .define("checkpoint_flag_spawns_mobs", true);
                    CHECKPOINT_FLAG_ADD_ITEMS = BUILDER.translation("configuration.marioverse.checkpoint_flag_add_items")
                            .comment("Allow survival players to add items using right-click.")
                            .comment("§9[Default: true]")
                            .define("checkpoint_flag_add_items", true);
                    CHECKPOINT_FLAG_REMOVE_ITEMS = BUILDER.translation("configuration.marioverse.checkpoint_flag_remove_items")
                            .comment("Allow survival players to remove items using right-click.")
                            .comment("§9[Default: true]")
                            .define("checkpoint_flag_remove_items", true);
                BUILDER.pop();

                BUILDER.push(CATEGORY_DECORATED_POTS);
                    DISABLE_DECORATED_POT_TWEAKS = BUILDER.translation("configuration.marioverse.disable_decorated_pot_tweaks")
                            .comment("Disables mob & power up spawning from decorated pots.")
                            .comment("§9[Default: false]")
                            .define("disable_decorated_pot_tweaks", false);
                    DECORATED_POT_BUCKET_TWEAKS = BUILDER.translation("configuration.marioverse.decorated_pot_bucket_tweaks")
                            .comment("Allow decorated pots to place bucket liquids.")
                            .comment("§9[Default: true]")
                            .define("decorated_pot_bucket_tweaks", true);
                    DECORATED_POT_SPAWNS_POWER_UPS = BUILDER.translation("configuration.marioverse.decorated_pot_spawns_power_ups")
                            .comment("Allow decorated pots to spawn power ups when broken.")
                            .comment("§9[Default: true]")
                            .define("decorated_pot_spawns_power_ups", true);
                    DECORATED_POT_SPAWNS_MOBS = BUILDER.translation("configuration.marioverse.decorated_pot_spawns_mobs")
                            .comment("Allow decorated pots to spawn mobs when broken.")
                            .comment("§9[Default: true]")
                            .define("decorated_pot_spawns_mobs", true);
                BUILDER.pop();

                BUILDER.push(CATEGORY_IRON_SPIKES);
                    IRON_SPIKE_DAMAGE = BUILDER.translation("configuration.marioverse.iron_spike_damage")
                            .comment("Amount of damage iron spikes cause.")
                            .comment("§6[1 point = 1/2 Heart]§b")
                            .defineInRange("iron_spike_damage", 2.0, 0.0, 16.0);
                BUILDER.pop();

                BUILDER.push(CATEGORY_QUESTION_BLOCKS);
                    QUESTION_BUCKET_TWEAKS = BUILDER.translation("configuration.marioverse.question_bucket_tweaks")
                            .comment("Allow question blocks to place bucket liquids and blocks.")
                            .comment("§9[Default: true]")
                            .define("question_bucket_tweaks", true);
                    QUESTION_SPAWNS_POWER_UPS = BUILDER.translation("configuration.marioverse.question_spawns_power_ups")
                            .comment("Allow question blocks to spawn power ups.")
                            .comment("§9[Default: true]")
                            .define("question_spawns_power_ups", true);
                    QUESTION_SPAWNS_MOBS = BUILDER.translation("configuration.marioverse.question_spawns_mobs")
                            .comment("Allow question blocks to spawn mobs.")
                            .comment("§9[Default: true]")
                            .define("question_spawns_mobs", true);
                    QUESTION_ADD_ITEMS = BUILDER.translation("configuration.marioverse.question_add_items")
                            .comment("Allow survival players to add items to question blocks using right-click.")
                            .comment("§9[Default: true]")
                            .define("question_add_items", true);
                    QUESTION_REMOVE_ITEMS = BUILDER.translation("configuration.marioverse.question_remove_items")
                            .comment("Allow survival players to activate question blocks using right-click.")
                            .comment("§9[Default: true]")
                            .define("question_remove_items", true);
                    REDSTONE_OPENS_QUESTION = BUILDER.translation("configuration.marioverse.redstone_opens_question")
                            .comment("Allow redstone to activate question blocks.")
                            .comment("§9[Default: true]")
                            .define("redstone_opens_question", true);
                    SELECT_INVISIBLE_QUESTION = BUILDER.translation("configuration.marioverse.select_invisible_question")
                            .comment("Allow invisible question blocks to be selectable in survival.")
                            .comment("§9[Default: false]")
                            .define("select_invisible_question", false);
                BUILDER.pop();

                BUILDER.push(CATEGORY_SPIKE_PANELS);
                    SPIKE_PANEL_DAMAGE = BUILDER.translation("configuration.marioverse.spike_panel_damage")
                            .comment("Amount of damage spike panels cause.")
                            .comment("§6[1 point = 1/2 Heart]§b")
                            .defineInRange("spike_panel_damage", 2.0, 0.0, 16.0);
                BUILDER.pop();

                BUILDER.push(CATEGORY_STAR_COINS);
                    STAR_COINS_COLLECTED_ON_COLLISION = BUILDER.translation("configuration.marioverse.star_coins_collected_on_collision")
                            .comment("Star coins can be collected during collision.")
                            .comment("§9[Default: true]")
                            .define("star_coins_collected_on_collision", true);
                    STAR_COINS_COLLECTED_IN_CREATIVE = BUILDER.translation("configuration.marioverse.star_coins_collected_in_creative")
                            .comment("Star coins can be collected by players in creative.")
                            .comment("§9[Default: true]")
                            .define("star_coins_collected_in_creative", true);
                BUILDER.pop();

                BUILDER.push(CATEGORY_WARP_DOORS);
                    DISABLE_WARP_DOORS = BUILDER.translation("configuration.marioverse.disable_warp_doors")
                            .comment("Disables the creation of warp doors.")
                            .comment("§9[Default: false]")
                            .define("disable_warp_doors", false);
                    WARP_DOOR_COOLDOWN = BUILDER.translation("configuration.marioverse.warp_door_cooldown")
                            .comment("Cooldown between teleports in ticks.")
                            .comment("§6[20 ticks = 1 second]§b")
                            .defineInRange("warp_door_cooldown", 70, 0, 72000);
                    WARP_DOOR_FUEL_AMT = BUILDER.translation("configuration.marioverse.warp_door_fuel_amt")
                            .comment("Amount of fuel required to create a warp door.§b")
                            .defineInRange("warp_door_fuel_amt", 2, 0, 64);
                BUILDER.pop();

                BUILDER.push(CATEGORY_WARP_PAINTINGS);
                    DISABLE_WARP_PAINTINGS = BUILDER.translation("configuration.marioverse.disable_warp_paintings")
                            .comment("Disables the creation of warp paintings.")
                            .comment("§9[Default: false]")
                            .define("disable_warp_paintings", false);
                    WARP_PAINTING_COOLDOWN = BUILDER.translation("configuration.marioverse.warp_painting_cooldown")
                            .comment("Cooldown between teleports in ticks.")
                            .comment("§6[20 ticks = 1 second]")
                            .comment("§9[Default: 100]§b")
                            .defineInRange("warp_painting_cooldown", 100, 0, 72000);
                    WARP_PAINTING_FUEL_AMT = BUILDER.translation("configuration.marioverse.warp_painting_fuel_amt")
                            .comment("Amount of fuel required to create a warp painting.§b")
                            .defineInRange("warp_painting_fuel_amt", 4, 0, 64);
                BUILDER.pop();

                BUILDER.push(CATEGORY_WARP_PIPES);
                    ALLOW_FAST_TRAVEL = BUILDER.translation("configuration.marioverse.allow_fast_travel")
                            .comment("Allow fast travel through Clear Warp Pipes.")
                            .comment("§9[Default: true]")
                            .define("allow_fast_travel", true);
                    WARP_PIPE_COOLDOWN = BUILDER.translation("configuration.marioverse.warp_pipe_cooldown")
                            .comment("Cooldown between teleports in ticks.")
                            .comment("§6[20 ticks = 1 second]")
                            .comment("§9[Default: 50]§b")
                            .defineInRange("warp_pipe_cooldown", 50, 0, 72000);
                    WAX_DISABLES_BUBBLES = BUILDER.translation("configuration.marioverse.wax_disables_bubbles")
                            .comment("Allows waxing pipes to disable the Pipe Bubbles button.")
                            .comment("§9[Default: true]")
                            .define("wax_disables_bubbles", true);
                    WAX_DISABLES_CLOSING = BUILDER.translation("configuration.marioverse.wax_disables_closing")
                            .comment("Allows waxing pipes to disable the Open/Close button.")
                            .comment("§9[Default: true]")
                            .define("wax_disables_closing", true);
                    WAX_DISABLES_RENAMING = BUILDER.translation("configuration.marioverse.wax_disables_renaming")
                            .comment("Allows waxing pipes to disable the Rename button.")
                            .comment("§9[Default: true]")
                            .define("wax_disables_renaming", true);
                    WAX_DISABLES_WATER_SPOUTS = BUILDER.translation("configuration.marioverse.wax_disables_water_spouts")
                            .comment("Allows waxing pipes to disable the Water Spout button.")
                            .comment("§9[Default: true]")
                            .define("wax_disables_water_spouts", true);
                    CREATIVE_BUBBLES = BUILDER.translation("configuration.marioverse.require_creative_bubbles")
                            .comment("Require creative to turn bubbles on/off.")
                            .comment("§9[Default: false]")
                            .define("require_creative_bubbles", false);
                    CREATIVE_CLOSE_PIPES = BUILDER.translation("configuration.marioverse.require_creative_close_pipes")
                            .comment("Require creative to open/close pipes.")
                            .comment("§9[Default: false]")
                            .define("require_creative_close_pipes", false);
                    CREATIVE_WATER_SPOUT = BUILDER.translation("configuration.marioverse.require_creative_water_spouts")
                            .comment("Require creative to turn water spouts on/off.")
                            .comment("§9[Default: false]")
                            .define("require_creative_water_spouts", false);
                BUILDER.pop();

                BUILDER.push(CATEGORY_WARP_TRAPDOORS);
                    DISABLE_WARP_TRAPDOORS = BUILDER.translation("configuration.marioverse.disable_warp_trapdoors")
                            .comment("Disables the creation of warp trapdoors.")
                            .comment("§9[Default: false]")
                            .define("disable_warp_trapdoors", false);
                    WARP_TRAPDOOR_COOLDOWN = BUILDER.translation("configuration.marioverse.warp_trapdoor_cooldown")
                            .comment("Cooldown between teleports in ticks for trapdoors.")
                            .comment("§6[20 ticks = 1 second]")
                            .comment("§9[Default: 70]§b")
                            .defineInRange("warp_trapdoor_cooldown", 70, 0, 72000);
                    WARP_TRAPDOOR_FUEL_AMT = BUILDER.translation("configuration.marioverse.warp_trapdoor_fuel_amt")
                            .comment("Amount of fuel required to create a warp trapdoor.§b")
                            .defineInRange("warp_trapdoor_fuel_amt", 2, 0, 64);
                BUILDER.pop();

                BUILDER.push(CATEGORY_WATER_SPOUTS);
                    WATER_SPOUTS_BUCKETABLE = BUILDER.translation("configuration.marioverse.water_spouts_bucketable")
                            .comment("Allow players to bucket water spouts.")
                            .comment("§9[Default: true]")
                            .define("water_spouts_bucketable", true);
                BUILDER.pop();

            BUILDER.pop();

            BUILDER.push(CATEGORY_GAMEPLAY);
                EQUIP_COSTUMES_PLAYERS = BUILDER.translation("configuration.marioverse.equip_costumes_players")
                        .comment("Equips power up costumes on Players.")
                        .comment("§9[Default: true]")
                        .define("equip_costumes_players", true);
                EQUIP_COSTUMES_MOBS = BUILDER.translation("configuration.marioverse.equip_costumes_mobs")
                        .comment("Equips power up costumes humanoid mobs, like zombies.")
                        .comment("§cMobs must whitelisted in the entity tag \"marioverse:costume_whitelist\".")
                        .comment("§9[Default: true]")
                        .define("equip_costumes_mobs", true);
                DAMAGE_SHRINKS_PLAYERS = BUILDER.translation("configuration.marioverse.damage_shrinks_players")
                        .comment("Allow damage to shrink players.")
                        .comment("§9[Default: true]")
                        .define("damage_shrinks_players", true);
                DAMAGE_SHRINKS_ALL_MOBS = BUILDER.translation("configuration.marioverse.damage_shrinks_all_mobs")
                        .comment("Allow damage to shrink all mobs.")
                        .comment("§9[Default: false]")
                        .define("damage_shrinks_all_mobs", false);
                SHRINK_PLAYERS_AT_HEALTH = BUILDER.translation("configuration.marioverse.shrink_players_at_health")
                        .comment("Health to shrink player at.")
                        .comment("§9[Default: 10.0F]§b")
                        .defineInRange("shrink_players_at_health", 10.0F, 0.0F, 100.0F);
                SHRINK_MOBS_AT_HEALTH = BUILDER.translation("configuration.marioverse.shrink_mobs_at_health")
                        .comment("Health in percent to shrink mobs at.")
                        .comment("§9[Default: 2%]§b")
                        .defineInRange("shrink_mobs_at_health", 0.2F, 0.0F, 1.0F);
                ENABLE_STOMPABLE_ENEMIES = BUILDER.translation("configuration.marioverse.enable_stompable_enemies")
                        .comment("Enable mobs to stomp other mobs.")
                        .comment("§cMobs must whitelisted in the entity tag \"marioverse:can_stomp_enemies\".")
                        .comment("§9[Default: true]")
                        .define("enable_stompable_enemies", true);
                ALL_MOBS_CAN_STOMP = BUILDER.translation("configuration.marioverse.all_mobs_can_stomp")
                        .comment("Allow all mobs to stomp other mobs.")
                        .comment("§9[Default: false]")
                        .define("all_mobs_can_stomp", false);
                STOMP_ALL_MOBS = BUILDER.translation("configuration.marioverse.stomp_all_mobs")
                        .comment("Allow all mobs to be stomped on.")
                        .comment("§9[Default: false]")
                        .define("stomp_all_mobs", false);
                DISABLE_CONSECUTIVE_BOUNCING = BUILDER.translation("configuration.marioverse.disable_consecutive_bouncing")
                        .comment("Disable consecutive bouncing, including the reward.")
                        .comment("§9[Default: false]")
                        .define("disable_consecutive_bouncing", false);
                STOMP_BOUNCE_HEIGHT = BUILDER.translation("configuration.marioverse.stomp_bounce_height")
                        .comment("The approx height mobs will bounce when stomping enemies.")
                        .comment("§9[Default: 1.5]§b")
                        .defineInRange("stomp_bounce_height", 1.5, 0.0, 100.0);
                STOMP_BOUNCE_HEIGHT_JUMP = BUILDER.translation("configuration.marioverse.stomp_bounce_height_jump")
                        .comment("The approx height players will bounce when stomping enemies while pressing the jump key.")
                        .comment("§9[Default: 5.5]§b")
                        .defineInRange("stomp_bounce_height_jump", 5.5, 0.0, 100.0);
                STOMP_DAMAGE = BUILDER.translation("configuration.marioverse.stomp_damage")
                        .comment("Amount of damage stomping causes.")
                        .comment("§6[1 point = 1/2 Heart]")
                        .comment("§9[Default: 4.0]§b")
                        .defineInRange("stomp_damage", 4.0, 0.0, 100.0);
                MAX_ONE_UP_BOUNCE_REWARD = BUILDER.translation("configuration.marioverse.max_one_up_bounce_reward")
                        .comment("Max amount of 1-Ups that can be rewarded from consecutive bounces.")
                        .comment("§9[Default: 2]§b")
                        .defineInRange("max_one_up_bounce_reward", 2, 0, 64);
                MAX_ONE_UP_SHELL_KILL_REWARD = BUILDER.translation("configuration.marioverse.max_one_up_shell_kill_reward")
                        .comment("Max amount of 1-Ups that can be rewarded from shell kills.")
                        .comment("§9[Default: 1]§b")
                        .defineInRange("max_one_up_shell_kill_reward", 1, 0, 64);
            BUILDER.pop();

            BUILDER.push(CATEGORY_HOLIDAY);

                BUILDER.push(CATEGORY_HALLOWEEN);
                    DISABLE_BOO_MASKS = BUILDER.translation("configuration.marioverse.disable_boo_masks")
                            .comment("Ban Boos from wearing masks on Halloween.")
                            .comment("§9[Default: false]")
                            .define("disable_boo_masks", false);
                    FORCE_BOO_MASKS = BUILDER.translation("configuration.marioverse.force_boo_masks")
                            .comment("Force Boos to spawn wearing masks at any time.")
                            .comment("§9[Default: false]")
                            .define("force_boo_masks", false);
                    DISABLE_DRY_BONES_MASKS = BUILDER.translation("configuration.marioverse.disable_dry_bones_masks")
                            .comment("Ban Dry Bones from wearing masks on Halloween.")
                            .comment("§9[Default: false]")
                            .define("disable_boo_masks", false);
                    FORCE_DRY_BONES_MASKS = BUILDER.translation("configuration.marioverse.force_dry_bones_masks")
                            .comment("Force Dry Bones to spawn wearing masks at any time.")
                            .comment("§9[Default: false]")
                            .define("force_boo_masks", false);
                    DISABLE_GOOMBA_MASKS = BUILDER.translation("configuration.marioverse.disable_goomba_masks")
                            .comment("Ban Goombas from wearing masks on Halloween.")
                            .comment("§9[Default: false]")
                            .define("disable_goomba_masks", false);
                    FORCE_GOOMBA_MASKS = BUILDER.translation("configuration.marioverse.force_goomba_masks")
                            .comment("Force Goombas to spawn wearing masks at any time.")
                            .comment("§9[Default: false]")
                            .define("force_goomba_masks", false);
                    DISABLE_KOOPA_MASKS = BUILDER.translation("configuration.marioverse.disable_koopa_masks")
                            .comment("Ban Koopa Troopas from wearing masks on Halloween.")
                            .comment("§9[Default: false]")
                            .define("disable_koopa_masks", false);
                    FORCE_KOOPA_MASKS = BUILDER.translation("configuration.marioverse.force_koopa_masks")
                            .comment("Force Koopa Troopas to spawn wearing masks at any time.")
                            .comment("§9[Default: false]")
                            .define("force_koopa_masks", false);
                BUILDER.pop();

            BUILDER.pop();

            BUILDER.push(CATEGORY_ITEMS);

                BUILDER.push(CATEGORY_WARP_DISRUPTOR);
                    DISABLE_PLAYER_WARP_DISRUPTING = BUILDER.translation("configuration.marioverse.disable_player_warp_disrupting")
                            .comment("Prevent the Warp Disruptor from working on players.")
                            .comment("§9[Default: false]")
                            .define("disable_player_warp_disrupting", false);
                    WARP_DISRUPTING_COOLDOWN = BUILDER.translation("configuration.marioverse.warp_disrupting_cooldown")
                            .comment("Cooldown before the player is able to warp again.")
                            .comment("§6[20 ticks = 1 second]")
                            .comment("§9[Default: 4800]§b")
                            .defineInRange("warp_disrupting_cooldown", 4800, 1, 72000);
                BUILDER.pop();

                BUILDER.push(CATEGORY_WRENCH);
                    CREATIVE_WRENCH_LINKING = BUILDER.translation("configuration.marioverse.creative_wrench_linking")
                            .comment("Require creative to link pipes, doors, trapdoors, & paintings.")
                            .comment("§9[Default: false]")
                            .define("creative_wrench_linking", false);
                BUILDER.pop();

            BUILDER.pop();

            BUILDER.push(CATEGORY_MOBS);

                BUILDER.push(CATEGORY_BOO);

                    BOO_LIGHT_LVL_DEATH = BUILDER.translation("configuration.marioverse.boo_light_level_death")
                            .comment("The minimum light level it takes to kill a Boo.")
                            .comment("§6 A light level of 1 will kill Boos in any light.")
                            .comment("§6 A light level of 16 will allow Boos to survive any light.")
                            .defineInRange("boo_light_level_death", 12, 1, 16);

                BUILDER.pop();

                BUILDER.push(CATEGORY_DRY_BONES);

                    DRY_BONES_DEATH_DURATION = BUILDER.translation("configuration.marioverse.dry_bones_death_duration")
                            .comment("Duration Dry Bones lay dead before reassembling in ticks.")
                            .comment("§6[20 ticks = 1 second]§b")
                            .defineInRange("dry_bones_death_duration", 300, 0, 999);
                    DRY_BONES_REASSEMBLE_DURATION = BUILDER.translation("configuration.marioverse.dry_bones_reassemble_duration")
                            .comment("Duration Dry Bones spend attempting to reassemble in ticks.")
                            .comment("§6[20 ticks = 1 second]§b")
                            .defineInRange("dry_bones_reassemble_duration", 600, 0, 999);

                BUILDER.pop();

                BUILDER.push(CATEGORY_KOOPA_SHELLS);

                    BUILDER.push(CATEGORY_GOLD_KOOPA_SHELL);
                        GOLD_KOOPA_SHELL_DAMAGE = BUILDER.translation("configuration.marioverse.gold_koopa_shell_damage")
                                .comment("Amount of damage gold koopa shells cause.")
                                .comment("§6[1 point = 1/2 Heart]§b")
                                .defineInRange("gold_koopa_shell_damage", 6.0, 0.0, 16.0);
                        MAX_GOLD_KOOPA_SHELL_TRAIL_COINS = BUILDER.translation("configuration.marioverse.max_gold_koopa_shell_trail_coins")
                                .comment("Max amount of coins gold koopa shells can place§b")
                                .defineInRange("max_gold_koopa_shell_trail_coins", 16, 0, 64);
                        MAX_GOLD_KOOPA_SHELL_CIRCLE_COINS = BUILDER.translation("configuration.marioverse.max_gold_koopa_shell_circle_coins")
                                .comment("Max amount of coins gold koopa shells can place in a circle§b")
                                .defineInRange("max_gold_koopa_shell_circle_coins", 8, 0, 64);
                        GOLD_KOOPA_SHELL_COIN_CIRCLE_RADIUS = BUILDER.translation("configuration.marioverse.gold_koopa_shell_coin_circle_radius")
                                .comment("Max amount of coins gold koopa shells can place in a circle§b")
                                .defineInRange("gold_koopa_shell_coin_circle_radius", 3, 0, 16);
                    BUILDER.pop();

                    BUILDER.push(CATEGORY_GREEN_KOOPA_SHELL);
                        GREEN_KOOPA_SHELL_DAMAGE = BUILDER.translation("configuration.marioverse.green_koopa_shell_damage")
                                .comment("Amount of damage green koopa shells cause.")
                                .comment("§6[1 point = 1/2 Heart]§b")
                                .defineInRange("green_koopa_shell_damage", 6.0, 0.0, 16.0);
                    BUILDER.pop();

                    BUILDER.push(CATEGORY_RED_KOOPA_SHELL);
                        RED_KOOPA_SHELL_DAMAGE = BUILDER.translation("configuration.marioverse.red_koopa_shell_damage")
                                .comment("Amount of damage red koopa shells cause.")
                                .comment("§6[1 point = 1/2 Heart]§b")
                                .defineInRange("red_koopa_shell_damage", 4.0, 0.0, 16.0);
                        RED_KOOPA_SHELL_MOB_DETECTION_RADIUS = BUILDER.translation("configuration.marioverse.red_koopa_shell_mob_detection_radius")
                                .comment("Mob detection radius of red koopa shells.§b")
                                .comment("§6A radius of 0 will disable the shell tracking.§b")
                                .defineInRange("red_koopa_shell_mob_detection_radius", 10, 0, 50);
                        RED_KOOPA_SHELL_PLAYER_DETECTION_RADIUS = BUILDER.translation("configuration.marioverse.red_koopa_shell_player_detection_radius")
                                .comment("Player detection radius of red koopa shells.§b")
                                .comment("§6A radius of 0 will disable the shell tracking.§b")
                                .defineInRange("red_koopa_shell_player_detection_radius", 15, 0, 50);
                    BUILDER.pop();

                    KOOPA_SHELL_DAMAGE_FROM_KILLS = BUILDER.translation("configuration.marioverse.koopa_shell_damage_from_kills")
                            .comment("Damage koopa shells take when insta-killing mobs.§b")
                            .defineInRange("koopa_shell_damage_from_kills", 20, 0, 999);
                    MAX_KOOPA_SHELL_DAMAGE_POINTS = BUILDER.translation("configuration.marioverse.max_koopa_shell_damage_points")
                            .comment("Max amount of damage a shell can take before shattering.")
                            .comment("§6Set to -1 to never break.§b")
                            .defineInRange("max_koopa_shell_damage_points", 200, -1, 999);

                BUILDER.pop();

                BUILDER.push(CATEGORY_KOOPA_TROOPAS);

                    BUILDER.push(CATEGORY_GOLD_KOOPA_TROOPA);
                        GOLD_KOOPA_TROOPA_HIDE_DURATION = BUILDER.translation("configuration.marioverse.gold_koopa_troopa_hide_duration")
                                .comment("Duration gold koopa troopas hide in their shells in ticks.")
                                .comment("§6[20 ticks = 1 second]§b")
                                .defineInRange("gold_koopa_troopa_hide_duration", 80, 0, 999);
                    BUILDER.pop();

                    BUILDER.push(CATEGORY_GREEN_KOOPA_TROOPA);
                        GREEN_KOOPA_TROOPA_HIDE_DURATION = BUILDER.translation("configuration.marioverse.green_koopa_troopa_hide_duration")
                                .comment("Duration green koopa troopas hide in their shells in ticks.")
                                .comment("§6[20 ticks = 1 second]§b")
                                .defineInRange("green_koopa_troopa_hide_duration", 140, 0, 999);
                    BUILDER.pop();

                    BUILDER.push(CATEGORY_RED_KOOPA_TROOPA);
                        RED_KOOPA_TROOPA_HIDE_DURATION = BUILDER.translation("configuration.marioverse.red_koopa_troopa_hide_duration")
                                .comment("Duration red koopa troopas hide in their shells in ticks.")
                                .comment("§6[20 ticks = 1 second]§b")
                                .defineInRange("red_koopa_troopa_hide_duration", 100, 0, 999);
                    BUILDER.pop();

                BUILDER.pop();

                BUILDER.push(CATEGORY_GOOMBAS);

                    BUILDER.push(CATEGORY_HEFTY_GOOMBA);
                        GOOMBA_SPLIT_COUNT = BUILDER.translation("configuration.marioverse.goomba_split_count")
                                .comment("Base count of goombas to spawn when a hefty goomba splits.§b")
                                .defineInRange("goomba_split_count", 2, 0, 16);
                        GOOMBA_SPLIT_RANDOM_COUNT = BUILDER.translation("configuration.marioverse.goomba_split_random_count")
                                .comment("Random count of goombas to spawn when a hefty goomba splits in addition to \"goomba_split_count\".§b")
                                .defineInRange("goomba_split_random_count", 1, 0, 16);
                    BUILDER.pop();

                    BUILDER.push(CATEGORY_MEGA_GOOMBA);
                        HEFTY_GOOMBA_SPLIT_COUNT = BUILDER.translation("configuration.marioverse.hefty_goomba_split_count")
                                .comment("Base count of hefty goombas to spawn when a mega goomba splits.§b")
                                .defineInRange("hefty_goomba_split_count", 2, 0, 8);
                        HEFTY_GOOMBA_SPLIT_RANDOM_COUNT = BUILDER.translation("configuration.marioverse.hefty_goomba_split_random_count")
                                .comment("Random count of hefty goombas to spawn when a mega goomba splits in addition to \"hefty_goomba_split_count\".§b")
                                .defineInRange("hefty_goomba_split_random_count", 1, 0, 8);
                    BUILDER.pop();

                    BUILDER.push(CATEGORY_MINI_GOOMBA);
                        MINI_GOOMBAS_ATTACH_ALL_MOBS = BUILDER.translation("configuration.marioverse.mini_goombas_attach_all_mobs")
                                .comment("Allow mini goombas to attach to all mobs.")
                                .comment("§9[Default: false]")
                                .define("mini_goombas_attach_all_mobs", false);
                        MINI_GOOMBAS_PUSH = BUILDER.translation("configuration.marioverse.mini_goombas_push")
                                .comment("Allow mini goombas to push the mobs it is attached to.")
                                .comment("§9[Default: false]")
                                .define("mini_goombas_push", false);
                    BUILDER.pop();

                    MAX_GOOMBA_STACK = BUILDER.translation("configuration.marioverse.max_goomba_stack")
                            .comment("Max stack size goombas can ride.§b")
                            .defineInRange("max_goomba_stack", 5, 0, 16);
                BUILDER.pop();

                BUILDER.push(CATEGORY_PIRANHA_PLANT);
                    PIRANHA_PLANT_HIDE_DURATION = BUILDER.translation("configuration.marioverse.piranha_plant_hide_duration")
                            .comment("Duration piranha plants hide and emerge from pipes in ticks.")
                            .comment("Applies to any block in the '#marioverse:piranha_plant_can_hide' block tag.")
                            .comment("§6[20 ticks = 1 second]§b")
                            .defineInRange("piranha_plant_hide_duration", 200, 80, 72000);
                BUILDER.pop();

            BUILDER.pop();

            BUILDER.push(CATEGORY_POWER_UPS);

                RUNNING_ACTIVATES_POWER_UPS = BUILDER.translation("configuration.marioverse.running_activates_power_ups")
                        .comment("Allow running to activate power ups.")
                        .comment("§9[Default: true]")
                        .define("running_activates_power_ups", true);

                BUILDER.push(CATEGORY_FIRE_FLOWER);
                    FIRE_FLOWER_POWERS_ALL_MOBS = BUILDER.translation("configuration.marioverse.fire_flower_powers_all_mobs")
                            .comment("Allow Fire Flowers to power all mobs.")
                            .comment("§9[Default: false]")
                            .define("fire_flower_powers_all_mobs", false);
                    FIREBALL_COOLDOWN = BUILDER.translation("configuration.marioverse.fireball_cooldown")
                            .comment("Cooldown between max amount of fireballs shot.")
                            .comment("§6[20 ticks = 1 second]")
                            .comment("§9[Default: 50]§b")
                            .defineInRange("fireball_cooldown", 50, 1, 72000);
                    MAX_PLAYER_FIREBALLS = BUILDER.translation("configuration.marioverse.max_player_fireballs")
                            .comment("Max amount of fireballs that can be shot before a cooldown for the player.")
                            .comment("§9[Default: 2]§b")
                            .defineInRange("max_player_fireballs", 2, 0, 100);
                    MAX_MOB_FIREBALLS = BUILDER.translation("configuration.marioverse.max_mob_fireballs")
                            .comment("Base amount of fireballs that can be shot before a cooldown for mobs.")
                            .comment("§9[Default: 2]§b")
                            .defineInRange("max_mob_fireballs", 2, 0, 100);
                    FIREBALL_DAMAGE = BUILDER.translation("configuration.marioverse.fireball_damage")
                            .comment("Amount of damage fireballs cause.")
                            .comment("§6[1 point = 1/2 Heart]")
                            .comment("§9[Default: 4.0]§b")
                            .defineInRange("fireball_damage", 4.0, 0.0, 16.0);
                BUILDER.pop();

                BUILDER.push(CATEGORY_ICE_FLOWER);
                    ICE_FLOWER_POWERS_ALL_MOBS = BUILDER.translation("configuration.marioverse.ice_flower_powers_all_mobs")
                            .comment("Allow Ice Flowers to power all mobs.")
                            .comment("§9[Default: false]")
                            .define("ice_flower_powers_all_mobs", false);
                    ICE_BALL_COOLDOWN = BUILDER.translation("configuration.marioverse.ice_ball_cooldown")
                            .comment("Cooldown between max amount of ice_balls shot.")
                            .comment("§6[20 ticks = 1 second]")
                            .comment("§9[Default: 50]§b")
                            .defineInRange("ice_ball_cooldown", 50, 1, 72000);
                    MAX_ICE_BALL_BOUNCES = BUILDER.translation("configuration.marioverse.max_ice_ball_bounces")
                            .comment("Max amount that ice_balls can bounce before disintegrating.")
                            .comment("§9[Default: 1]§b")
                            .defineInRange("max_ice_ball_bounces", 1, 0, 100);
                    MAX_PLAYER_ICE_BALLS = BUILDER.translation("configuration.marioverse.max_player_ice_balls")
                            .comment("Max amount of ice_balls that can be shot before a cooldown for the player.")
                            .comment("§9[Default: 2]§b")
                            .defineInRange("max_player_ice_balls", 2, 0, 100);
                    MAX_MOB_ICE_BALLS = BUILDER.translation("configuration.marioverse.max_mob_ice_balls")
                            .comment("Base amount of ice_balls that can be shot before a cooldown for mobs.")
                            .comment("§9[Default: 2]§b")
                            .defineInRange("max_mob_ice_balls", 2, 0, 100);
                    ICE_BALL_DAMAGE = BUILDER.translation("configuration.marioverse.ice_ball_damage")
                            .comment("Amount of damage ice balls cause.")
                            .comment("§6[1 point = 1/2 Heart]")
                            .comment("§9[Default: 2.0]§b")
                            .defineInRange("ice_ball_damage", 2.0, 0.0, 16.0);
                    ICE_CUBE_LIFESPAN = BUILDER.translation("configuration.marioverse.ice_cube_lifespan")
                            .comment("Lifespan of ice cubes before it shatters in ticks.")
                            .comment("§6[20 ticks = 1 second]")
                            .comment("§9[Default: 500]§b")
                            .defineInRange("ice_cube_lifespan", 500, 0, 72000);
                    ICE_CUBE_DAMAGE = BUILDER.translation("configuration.marioverse.ice_cube_damage")
                            .comment("Amount of damage ice cubes cause when crushing mobs.")
                            .comment("§6[1 point = 1/2 Heart]")
                            .comment("§9[Default: 2.0]§b")
                            .defineInRange("ice_cube_damage", 5.0, 0.0, 32.0);
                    ICE_CUBE_FREEZE_DURATION = BUILDER.translation("configuration.marioverse.ice_cube_freeze_duration")
                            .comment("Duration mobs experience the freeze effect in ticks.")
                            .comment("§6[20 ticks = 1 second]")
                            .comment("§9[Default: 180]§b")
                            .defineInRange("ice_cube_freeze_duration", 180, 0, 72000);
                BUILDER.pop();

                BUILDER.push(CATEGORY_DASH_MUSHROOM);
                    DASH_MUSHROOM_BOOST_STRENGTH = BUILDER.translation("configuration.marioverse.dash_mushroom_boost_strength")
                            .comment("Strength of Dash Mushroom boost.§b")
                            .defineInRange("dash_mushroom_boost_strength", 4.0F, 1.0F, 50.0F);
                    VEHICLE_MUSHROOM_BOOST_STRENGTH = BUILDER.translation("configuration.marioverse.vehicle_mushroom_boost_strength")
                            .comment("Strength of Dash Mushroom boost for vehicles.§b")
                            .defineInRange("vehicle_mushroom_boost_strength", 3.0F, 1.0F, 50.0F);
                    DASH_MUSHROOM_HEALTH_HEALED = BUILDER.translation("configuration.marioverse.dash_mushroom_health_healed")
                            .comment("Amount of health Dash Mushrooms heals.§b")
                            .defineInRange("dash_mushroom_health_healed", 2.0F, 0.0F, 100.0F);
                BUILDER.pop();

                BUILDER.push(CATEGORY_SUPER_MUSHROOM);
                    SUPER_MUSHROOM_POWERS_ALL_MOBS = BUILDER.translation("configuration.marioverse.super_mushroom_powers_all_mobs")
                            .comment("Allow Super Mushrooms to power all mobs.")
                            .comment("§9[Default: false]")
                            .define("super_mushroom_powers_all_mobs", false);
                    SUPER_MUSHROOM_HEALTH_HEALED = BUILDER.translation("configuration.marioverse.super_mushroom_health_healed")
                            .comment("Amount of health Super Mushrooms heals.§b")
                            .defineInRange("super_mushroom_health_healed", 5.0F, 0.0F, 100.0F);
                BUILDER.pop();

                BUILDER.push(CATEGORY_ONE_UP);
                    ONE_UP_HEALTH_HEALED = BUILDER.translation("configuration.marioverse.one_up_health_healed")
                            .comment("Amount of health 1-Up Mushrooms heals.")
                            .comment("§9[Default: 8.0F]§b")
                            .defineInRange("one_up_health_healed", 8.0F, 0.0F, 100.0F);
                    ONE_UP_HEALS_ALL_MOBS = BUILDER.translation("configuration.marioverse.one_up_heals_mobs")
                            .comment("Allow 1-Ups to heal all mobs.")
                            .comment("§9[Default: false]")
                            .define("one_up_heals_mobs", false);
                BUILDER.pop();

                BUILDER.push(CATEGORY_SUPER_STAR);
                    SUPER_STAR_POWERS_ALL_MOBS = BUILDER.translation("configuration.marioverse.super_star_powers_all_mobs")
                            .comment("Allow Super Stars to power all mobs.")
                            .comment("§9[Default: false]")
                            .define("super_star_powers_all_mobs", false);
                    SUPER_STAR_DURATION = BUILDER.translation("configuration.marioverse.super_star_duration")
                            .comment("Duration until the Super Star runs out.")
                            .comment("§6[20 ticks = 1 second]")
                            .comment("§9[Default: 300]§b")
                            .defineInRange("super_star_duration", 300, 1, 72000);
                    SUPER_STAR_SPEED_DURATION = BUILDER.translation("configuration.marioverse.super_star_speed_duration")
                            .comment("Duration until the speed effect runs out.")
                            .comment("§6[20 ticks = 1 second]")
                            .comment("§9[Default: 300]§b")
                            .defineInRange("super_star_speed_duration", 300, 1, 72000);
                    SUPER_STAR_DAMAGE = BUILDER.translation("configuration.marioverse.super_star_damage")
                            .comment("Amount of damage super star causes.")
                            .comment("§6[1 point = 1/2 Heart]")
                            .comment("§9[Default: 1000]§b")
                            .defineInRange("super_star_damage", 1000, 0.0, Float.MAX_VALUE);
                BUILDER.pop();

            BUILDER.pop();

            BUILDER.push(CATEGORY_TELEPORTATION);
                ALLOW_WARP_UNWAXING = BUILDER.translation("configuration.marioverse.allow_warp_unwaxing")
                        .comment("Allow all warp blocks to be unwaxed with an axe.")
                        .comment("§9[Default: false]")
                        .define("allow_warp_unwaxing", false);
                BLINDNESS_EFFECT = BUILDER.translation("configuration.marioverse.blindness_effect")
                        .comment("Warping gives the player a brief blindness effect.")
                        .comment("§9[Default: true]")
                        .define("blindness_effect", true);
                TELEPORT_MOBS = BUILDER.translation("configuration.marioverse.teleport_mobs")
                        .comment("Allow mobs to teleport.")
                        .comment("§9[Default: true]")
                        .define("teleport_mobs", true);
                TELEPORT_NON_MOBS = BUILDER.translation("configuration.marioverse.teleport_non_mobs")
                        .comment("Allow non-living entities to teleport.")
                        .comment("§9[Default: true]")
                        .define("teleport_non_mobs", true);
                TELEPORT_PLAYERS = BUILDER.translation("configuration.marioverse.teleport_players")
                        .comment("Allow players to teleport.")
                        .comment("§9[Default: true]")
                        .define("teleport_players", true);
                WAX_DISABLES_WARP_LINKING = BUILDER.translation("configuration.marioverse.wax_disables_warp_linking")
                        .comment("Allow waxing warp blocks to disable warp linking.")
                        .comment("§9[Default: true]")
                        .define("wax_disables_warp_linking", true);
            BUILDER.pop();

            DISABLE_MARIOVERSE_TABS = BUILDER.translation("configuration.marioverse.disable_marioverse_tabs")
                    .comment("Disable the Marioverse creative tab.")
                    .comment("§cRequires world reload")
                    .comment("§9[Default: false]")
                    .define("disable_marioverse_tabs", false);
            DISABLE_VANILLA_TABS = BUILDER.translation("configuration.marioverse.disable_vanilla_tabs")
                    .comment("Disable items in vanilla creative tabs.")
                    .comment("§cRequires world reload")
                    .comment("§9[Default: false]")
                    .define("disable_vanilla_tabs", false);

        BUILDER.pop();

        BUILDER.comment("Marioverse Config").push(CATEGORY_DEBUG);
            DEBUG_SELECTION_BOX = BUILDER.translation("configuration.marioverse.debug_selection_box")
                    .comment("Enable debug selection box for Clear Warp Pipes.")
                    .comment("§9[Default: false]")
                    .define("debug_selection_box", false);
            DEBUG_SELECTION_BOX_CREATIVE = BUILDER.translation("configuration.marioverse.debug_selection_box_creative")
                    .comment("Enable debug selection box for Clear Warp Pipes in Creative.")
                    .comment("§cCreative Only")
                    .comment("§9[Default: true]")
                    .define("debug_selection_box_creative", true);
            DEBUG_PIPE_BUBBLES_SELECTION_BOX = BUILDER.translation("configuration.marioverse.debug_pipe_bubbles_selection_box")
                    .comment("Enable debug selection box for Pipe Bubbles.")
                    .comment("§cCreative Only")
                    .comment("§9[Default: false]")
                    .define("debug_pipe_bubbles_selection_box", false);
            DEBUG_WATER_SPOUT_SELECTION_BOX = BUILDER.translation("configuration.marioverse.debug_water_spout_selection_box")
                    .comment("Enable debug selection box for Water Spouts.")
                    .comment("§cCreative Only")
                    .comment("§9[Default: false]")
                    .define("debug_water_spout_selection_box", false);
        BUILDER.pop();

        CONFIG_SPEC = BUILDER.build();
    }

    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, INSTANCE.CONFIG_SPEC, "marioverse-common.toml");
    }

    public static void registerClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
