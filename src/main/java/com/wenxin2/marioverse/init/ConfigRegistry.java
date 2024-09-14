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
    public static final String CATEGORY_QUESTION_BLOCK = "question_block";
    public static final String CATEGORY_TELEPORTATION = "teleportation";
    public static final String CATEGORY_WARP_PIPES = "warp_pipes";
    public static final String CATEGORY_WATER_SPOUTS = "water_spouts";

    private final ModConfigSpec CONFIG_SPEC;
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
    public static ModConfigSpec.BooleanValue DEBUG_WATER_SPOUT_SELECTION_BOX;
    public static ModConfigSpec.BooleanValue DEBUG_SELECTION_BOX;
    public static ModConfigSpec.BooleanValue DEBUG_SELECTION_BOX_CREATIVE;
    public static ModConfigSpec.BooleanValue DISABLE_TEXT;
    public static ModConfigSpec.DoubleValue HEALTH_SHRINK_MOBS;
    public static ModConfigSpec.DoubleValue HEALTH_SHRINK_PLAYERS;
    public static ModConfigSpec.DoubleValue MUSHROOM_HEAL_AMT;
    public static ModConfigSpec.DoubleValue ONE_UP_HEAL_AMT;
    public static ModConfigSpec.BooleanValue QUESTION_ADD_ITEMS;
    public static ModConfigSpec.BooleanValue QUESTION_REMOVE_ITEMS;
    public static ModConfigSpec.BooleanValue QUESTION_SPAWNS_MOBS;
    public static ModConfigSpec.BooleanValue QUESTION_SPAWNS_POWER_UPS;
    public static ModConfigSpec.BooleanValue REDSTONE_OPENS_QUESTION;
    public static ModConfigSpec.BooleanValue SELECT_INVISIBLE_QUESTION;
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
        WARP_COOLDOWN_MESSAGE = BUILDER.translation("configuration.marioverse.warp_cooldown_message")
                .comment("Display a warp cooldown message.")
                .comment("§9[Default: false]")
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
                    .defineInRange("warp_cooldown", 50, 0, 8000);
            BUILDER.pop();

            BUILDER.push(CATEGORY_GAMEPLAY);
            DAMAGE_SHRINKS_PLAYERS = BUILDER.translation("configuration.marioverse.damage_shrinks_players")
                    .comment("Allow damage to shrink players.")
                    .comment("§9[Default: true]")
                    .define("damage_shrinks_players", true);
            DAMAGE_SHRINKS_ALL_MOBS = BUILDER.translation("configuration.marioverse.damage_shrinks_all_mobs")
                    .comment("Allow damage to shrink all mobs.")
                    .comment("§9[Default: false]")
                    .define("damage_shrinks_all_mobs", false);
            MUSHROOM_HEAL_AMT = BUILDER.translation("configuration.marioverse.mushroom_heal_amount")
                    .comment("Amount of health Mushrooms heals.")
                    .comment("§9[Default: 2.5F]§b")
                    .defineInRange("mushroom_heal_amount", 5.0F, 0.0F, 100.0F);
            ONE_UP_HEAL_AMT = BUILDER.translation("configuration.marioverse.one_up_mushroom_heal_amount")
                    .comment("Amount of health 1-Up Mushrooms heals.")
                    .comment("§9[Default: 3.0F]§b")
                    .defineInRange("one_up_mushroom_heal_amount", 8.0F, 0.0F, 100.0F);
            HEALTH_SHRINK_PLAYERS = BUILDER.translation("configuration.marioverse.health_shrink_players")
                    .comment("Health to shrink player at.")
                    .comment("§9[Default: 10.0F]§b")
                    .defineInRange("health_shrink_players", 10.0F, 0.0F, 100.0F);
            HEALTH_SHRINK_MOBS = BUILDER.translation("configuration.marioverse.health_shrink_mobs")
                    .comment("Health in percent to shrink mobs at.")
                    .comment("§9[Default: 0.2%]§b")
                    .defineInRange("health_shrink_mobs", 0.2F, 0.0F, 1.0F);
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
