package com.wenxin2.marioverse.init;

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
    public static final String CATEGORY_GAMEPLAY = "gameplay";
    public static final String CATEGORY_MISC = "misc";
    public static final String CATEGORY_MOBS = "mobs";
    public static final String CATEGORY_POWER_UPS = "power_ups";
    public static final String CATEGORY_QUESTION_BLOCK = "question_block";
    public static final String CATEGORY_TELEPORTATION = "teleportation";
    public static final String CATEGORY_WARP_PIPES = "warp_pipes";
    public static final String CATEGORY_WATER_SPOUTS = "water_spouts";

    private final ModConfigSpec CONFIG_SPEC;
    public static ModConfigSpec.BooleanValue ALL_MOBS_CAN_STOMP;
    public static ModConfigSpec.BooleanValue ALLOW_FAST_TRAVEL;
    public static ModConfigSpec.BooleanValue ALLOW_PIPE_UNWAXING;
    public static ModConfigSpec.BooleanValue CREATIVE_BUBBLES;
    public static ModConfigSpec.BooleanValue BLINDNESS_EFFECT;
    public static ModConfigSpec.BooleanValue CREATIVE_CLOSE_PIPES;
    public static ModConfigSpec.BooleanValue CREATIVE_WATER_SPOUT;
    public static ModConfigSpec.BooleanValue CREATIVE_WRENCH_PIPE_LINKING;
    public static ModConfigSpec.BooleanValue DAMAGE_SHRINKS_ALL_MOBS;
    public static ModConfigSpec.BooleanValue DAMAGE_SHRINKS_PLAYERS;
    public static ModConfigSpec.BooleanValue DEBUG_PIPE_BUBBLES_SELECTION_BOX;
    public static ModConfigSpec.BooleanValue DEBUG_SELECTION_BOX;
    public static ModConfigSpec.BooleanValue DEBUG_SELECTION_BOX_CREATIVE;
    public static ModConfigSpec.BooleanValue DEBUG_WATER_SPOUT_SELECTION_BOX;
    public static ModConfigSpec.BooleanValue DISABLE_CONSECUTIVE_BOUNCING;
    public static ModConfigSpec.BooleanValue DISABLE_REWARD_PARTICLES;
    public static ModConfigSpec.BooleanValue DISABLE_TEXT;
    public static ModConfigSpec.BooleanValue ENABLE_STOMPABLE_ENEMIES;
    public static ModConfigSpec.BooleanValue EQUIP_COSTUMES_MOBS;
    public static ModConfigSpec.BooleanValue EQUIP_COSTUMES_PLAYERS;
    public static ModConfigSpec.BooleanValue FIRE_FLOWER_POWERS_ALL_MOBS;
    public static ModConfigSpec.IntValue FIREBALL_COOLDOWN;
    public static ModConfigSpec.DoubleValue HEALTH_SHRINK_MOBS;
    public static ModConfigSpec.DoubleValue HEALTH_SHRINK_PLAYERS;
    public static ModConfigSpec.IntValue MAX_MOB_FIREBALLS;
    public static ModConfigSpec.IntValue MAX_PLAYER_FIREBALLS;
    public static ModConfigSpec.IntValue MAX_ONE_UP_BOUNCE_REWARD;
    public static ModConfigSpec.BooleanValue MINI_GOOMBAS_ATTACH_ALL_MOBS;
    public static ModConfigSpec.BooleanValue MINI_GOOMBAS_PUSH;
    public static ModConfigSpec.DoubleValue MUSHROOM_HEAL_AMT;
    public static ModConfigSpec.DoubleValue ONE_UP_HEAL_AMT;
    public static ModConfigSpec.BooleanValue ONE_UP_HEALS_ALL_MOBS;
    public static ModConfigSpec.BooleanValue QUESTION_ADD_ITEMS;
    public static ModConfigSpec.BooleanValue QUESTION_REMOVE_ITEMS;
    public static ModConfigSpec.BooleanValue QUESTION_SPAWNS_MOBS;
    public static ModConfigSpec.BooleanValue QUESTION_SPAWNS_POWER_UPS;
    public static ModConfigSpec.BooleanValue REDSTONE_OPENS_QUESTION;
    public static ModConfigSpec.BooleanValue RENDER_ONE_UP_CHARM;
    public static ModConfigSpec.BooleanValue RUNNING_ACTIVATES_POWER_UPS;
    public static ModConfigSpec.BooleanValue SELECT_INVISIBLE_QUESTION;
    public static ModConfigSpec.BooleanValue STOMP_ALL_MOBS;
    public static ModConfigSpec.DoubleValue STOMP_BOUNCE_HEIGHT;
    public static ModConfigSpec.DoubleValue STOMP_BOUNCE_HEIGHT_JUMP;
    public static ModConfigSpec.DoubleValue STOMP_DAMAGE;
    public static ModConfigSpec.BooleanValue TELEPORT_MOBS;
    public static ModConfigSpec.BooleanValue TELEPORT_NON_MOBS;
    public static ModConfigSpec.BooleanValue TELEPORT_PLAYERS;
    public static ModConfigSpec.IntValue WARP_COOLDOWN;
    public static ModConfigSpec.BooleanValue WARP_COOLDOWN_MESSAGE;
    public static ModConfigSpec.BooleanValue WARP_COOLDOWN_MESSAGE_TICKS;
    public static ModConfigSpec.BooleanValue WATER_SPOUTS_BUCKETABLE;
    public static ModConfigSpec.BooleanValue WAX_DISABLES_BUBBLES;
    public static ModConfigSpec.BooleanValue WAX_DISABLES_CLOSING;
    public static ModConfigSpec.BooleanValue WAX_DISABLES_RENAMING;
    public static ModConfigSpec.BooleanValue WAX_DISABLES_WATER_SPOUTS;

    private ConfigRegistry() {
        ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
        BUILDER.push(CATEGORY_CLIENT);
            DISABLE_TEXT = BUILDER.translation("configuration.marioverse.disable_text")
                    .comment("Disable text rendering on pipes.")
                    .comment("§9[Default: false]")
                    .define("disable_text", false);
            DISABLE_REWARD_PARTICLES = BUILDER.translation("configuration.marioverse.disable_reward_particles")
                    .comment("Enable reward particles when squashing enemies.")
                    .comment("§9[Default: false]")
                    .define("disable_reward_particles", false);
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

            BUILDER.push(CATEGORY_WARP_PIPES);
                ALLOW_FAST_TRAVEL = BUILDER.translation("configuration.marioverse.allow_fast_travel")
                        .comment("Allow fast travel through Clear Warp Pipes.")
                        .comment("§9[Default: true]")
                        .define("allow_fast_travel", true);
                ALLOW_PIPE_UNWAXING = BUILDER.translation("configuration.marioverse.allow_pipe_unwaxing")
                        .comment("Allow pipes to be unwaxed with an axe.")
                        .comment("§9[Default: false]")
                        .define("allow_pipe_unwaxing", false);
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
                CREATIVE_WRENCH_PIPE_LINKING = BUILDER.translation("configuration.marioverse.creative_wrench_pipe_linking")
                        .comment("Require creative to link pipes.")
                        .comment("§9[Default: false]")
                        .define("creative_wrench_pipe_linking", false);
            BUILDER.pop();

            BUILDER.push(CATEGORY_QUESTION_BLOCK);
                QUESTION_SPAWNS_POWER_UPS = BUILDER.translation("configuration.marioverse.question_spawns_power_ups")
                        .comment("Allow question blocks to spawn power ups.")
                        .comment("§9[Default: true]")
                        .define("question_spawns_power_ups", true);
                QUESTION_SPAWNS_MOBS = BUILDER.translation("configuration.marioverse.question_spawns_mobs")
                        .comment("Allow question blocks to spawn mobs.")
                        .comment("§9[Default: true]")
                        .define("question_spawns_mobs", true);
                QUESTION_ADD_ITEMS = BUILDER.translation("configuration.marioverse.question_add_items")
                        .comment("Allow players to add items to question blocks using right-click.")
                        .comment("§9[Default: true]")
                        .define("question_add_items", true);
                QUESTION_REMOVE_ITEMS = BUILDER.translation("configuration.marioverse.question_remove_items")
                        .comment("Allow players to activate question blocks using right-click.")
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

            BUILDER.push(CATEGORY_WATER_SPOUTS);
                WATER_SPOUTS_BUCKETABLE = BUILDER.translation("configuration.marioverse.water_spouts_bucketable")
                        .comment("Allow players to bucket water spouts.")
                        .comment("§9[Default: true]")
                        .define("water_spouts_bucketable", true);
            BUILDER.pop();

            BUILDER.push(CATEGORY_MOBS);
                MINI_GOOMBAS_ATTACH_ALL_MOBS = BUILDER.translation("configuration.marioverse.mini_goombas_attach_all_mobs")
                        .comment("Allow mini goombas to attach to all mobs.")
                        .comment("§9[Default: false]")
                        .define("mini_goombas_attach_all_mobs", false);
                MINI_GOOMBAS_PUSH = BUILDER.translation("configuration.marioverse.mini_goombas_push")
                        .comment("Allow mini goombas to push the mobs it is attached to.")
                        .comment("§9[Default: false]")
                        .define("mini_goombas_push", false);
            BUILDER.pop();

            BUILDER.push(CATEGORY_TELEPORTATION);
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
                WARP_COOLDOWN = BUILDER.translation("configuration.marioverse.warp_cooldown")
                        .comment("Cooldown between teleports in ticks.")
                        .comment("§6[20 ticks = 1 second]")
                        .comment("§9[Default: 50]§b")
                        .defineInRange("warp_cooldown", 50, 0, 72000);
            BUILDER.pop();

            BUILDER.push(CATEGORY_POWER_UPS);
                RUNNING_ACTIVATES_POWER_UPS = BUILDER.translation("configuration.marioverse.running_activates_power_ups")
                        .comment("Allow running to activate power ups.")
                        .comment("§9[Default: true]")
                        .define("running_activates_power_ups", true);
                MUSHROOM_HEAL_AMT = BUILDER.translation("configuration.marioverse.mushroom_heal_amount")
                        .comment("Amount of health Mushrooms heals.")
                        .comment("§9[Default: 5.0F]§b")
                        .defineInRange("mushroom_heal_amount", 5.0F, 0.0F, 100.0F);
                ONE_UP_HEAL_AMT = BUILDER.translation("configuration.marioverse.one_up_heal_amount")
                        .comment("Amount of health 1-Up Mushrooms heals.")
                        .comment("§9[Default: 8.0F]§b")
                        .defineInRange("one_up_heal_amount", 8.0F, 0.0F, 100.0F);
                ONE_UP_HEALS_ALL_MOBS = BUILDER.translation("configuration.marioverse.one_up_heals_mobs")
                        .comment("Allow 1-Ups to heal all mobs.")
                        .comment("§9[Default: false]")
                        .define("one_up_heals_mobs", false);
                MAX_ONE_UP_BOUNCE_REWARD = BUILDER.translation("configuration.marioverse.max_one_up_bounce_reward")
                        .comment("Max amount of 1-Ups that can be rewarded from consecutive bounces.")
                        .comment("§9[Default: 2]§b")
                        .defineInRange("max_one_up_bounce_reward", 2, 0, 64);
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
                        .comment("Max amount of fireballs that can be shot before a cooldown by the player.")
                        .comment("§9[Default: 2]§b")
                        .defineInRange("max_player_fireballs", 2, 0, 100);
                MAX_MOB_FIREBALLS = BUILDER.translation("configuration.marioverse.max_mob_fireballs")
                        .comment("Base amount of fireballs that can be shot before a cooldown by mobs.")
                        .comment("§9[Default: 2]§b")
                        .defineInRange("max_mob_fireballs", 2, 0, 100);
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
                HEALTH_SHRINK_PLAYERS = BUILDER.translation("configuration.marioverse.health_shrink_players")
                        .comment("Health to shrink player at.")
                        .comment("§9[Default: 10.0F]§b")
                        .defineInRange("health_shrink_players", 10.0F, 0.0F, 100.0F);
                HEALTH_SHRINK_MOBS = BUILDER.translation("configuration.marioverse.health_shrink_mobs")
                        .comment("Health in percent to shrink mobs at.")
                        .comment("§9[Default: 2%]§b")
                        .defineInRange("health_shrink_mobs", 0.2F, 0.0F, 1.0F);
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
            BUILDER.pop();

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
