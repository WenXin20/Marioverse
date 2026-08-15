package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.AbilityBlock;
import com.wenxin2.marioverse.blocks.BlockSpawnerBlock;
import com.wenxin2.marioverse.blocks.BlueDottedLineBlock;
import com.wenxin2.marioverse.blocks.BlueMushroomTrampolineBlock;
import com.wenxin2.marioverse.blocks.CoralTowerBlock;
import com.wenxin2.marioverse.blocks.DaisyAbilityBlock;
import com.wenxin2.marioverse.blocks.DeadCoralTowerBlock;
import com.wenxin2.marioverse.blocks.DeathBlock;
import com.wenxin2.marioverse.blocks.LuigiAbilityBlock;
import com.wenxin2.marioverse.blocks.MarioAbilityBlock;
import com.wenxin2.marioverse.blocks.MonsterDeathBlock;
import com.wenxin2.marioverse.blocks.PassiveDeathBlock;
import com.wenxin2.marioverse.blocks.PeachAbilityBlock;
import com.wenxin2.marioverse.blocks.PicketFenceBlock;
import com.wenxin2.marioverse.blocks.PlayerDeathBlock;
import com.wenxin2.marioverse.blocks.PottedTrampolineCapBlock;
import com.wenxin2.marioverse.blocks.RedDottedLineBlock;
import com.wenxin2.marioverse.blocks.RedMushroomTrampolineBlock;
import com.wenxin2.marioverse.blocks.BrickPedestalBlock;
import com.wenxin2.marioverse.blocks.BridgeBlock;
import com.wenxin2.marioverse.blocks.BridgeStairBlock;
import com.wenxin2.marioverse.blocks.CheckpointFlagBlock;
import com.wenxin2.marioverse.blocks.ClearWarpPipeBlock;
import com.wenxin2.marioverse.blocks.CoinBlock;
import com.wenxin2.marioverse.blocks.DangoBlossomBlock;
import com.wenxin2.marioverse.blocks.GlowBlock;
import com.wenxin2.marioverse.blocks.GoalPoleBlock;
import com.wenxin2.marioverse.blocks.InvisibleQuestionBlock;
import com.wenxin2.marioverse.blocks.IronSpikeBlock;
import com.wenxin2.marioverse.blocks.RosalinaAbilityBlock;
import com.wenxin2.marioverse.blocks.SmashableBrickBlock;
import com.wenxin2.marioverse.blocks.TrampolineCapBlock;
import com.wenxin2.marioverse.blocks.OnBlock;
import com.wenxin2.marioverse.blocks.OnOffSwitchBlock;
import com.wenxin2.marioverse.blocks.PottedPiranhaPlantBlock;
import com.wenxin2.marioverse.blocks.QuestionPanelBlock;
import com.wenxin2.marioverse.blocks.QuicksandBlock;
import com.wenxin2.marioverse.blocks.SpikePanelBlock;
import com.wenxin2.marioverse.blocks.SplunkinCarvedPumpkinBlock;
import com.wenxin2.marioverse.blocks.StarCoinBlock;
import com.wenxin2.marioverse.blocks.StorageBrickBlock;
import com.wenxin2.marioverse.blocks.PipeBubblesBlock;
import com.wenxin2.marioverse.blocks.QuestionBlock;
import com.wenxin2.marioverse.blocks.WaluigiAbilityBlock;
import com.wenxin2.marioverse.blocks.WarioAbilityBlock;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.WaterSpoutBlock;
import com.wenxin2.marioverse.blocks.WeatheringCopperInvisibleQuestionBlock;
import com.wenxin2.marioverse.blocks.WeatheringCopperPedestalBlock;
import com.wenxin2.marioverse.blocks.WeatheringCopperQuestionBlock;
import com.wenxin2.marioverse.blocks.WeatheringCopperStorageBrickBlock;
import com.wenxin2.marioverse.sounds.MarioverseSoundTypes;
import com.wenxin2.marioverse.world.grower.SuperTreeGrower;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.EquipableCarvedPumpkinBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopperFullBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;

public class BlockRegistry {
    public static final EnumMap<DyeColor, DeferredBlock<Block>> CALCITE =
            new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, DeferredBlock<Block>> CALCITE_BRICKS =
            new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, DeferredBlock<Block>> CALCITE_BRICK_PEDESTAL =
            new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, DeferredBlock<Block>> CHISELED_CALCITE_BRICKS =
            new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, DeferredBlock<Block>> CRACKED_CALCITE_BRICKS =
            new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, DeferredBlock<Block>> CHECKPOINT_FLAGS =
            new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, DeferredBlock<Block>> GOAL_POLES =
            new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, DeferredBlock<Block>> PIPE_JUNCTION =
            new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, DeferredBlock<Block>> POLISHED_CALCITE =
            new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, DeferredBlock<Block>> STORAGE_CALCITE_BRICKS =
            new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, DeferredBlock<Block>> WARP_PIPES =
            new EnumMap<>(DyeColor.class);
    public static final DeferredBlock<Block> RED_PICKET_FENCE;
    public static final DeferredBlock<Block> WHITE_PICKET_FENCE;

    public static final DeferredBlock<Block> ACACIA_LOG_BRIDGE;
    public static final DeferredBlock<Block> ACACIA_LOG_BRIDGE_STAIRS;
    public static final DeferredBlock<Block> ACACIA_PICKET_FENCE;
    public static final DeferredBlock<Block> AMETHYST_BRICKS;
    public static final DeferredBlock<Block> AMETHYST_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> AMETHYST_BRICK_SLAB;
    public static final DeferredBlock<Block> AMETHYST_BRICK_STAIRS;
    public static final DeferredBlock<Block> AMETHYST_BRICK_WALL;
    public static final DeferredBlock<Block> AMETHYST_BUTTON;
    public static final DeferredBlock<Block> AMETHYST_PRESSURE_PLATE;
    public static final DeferredBlock<Block> AMETHYST_QUESTION_BLOCK;
    public static final DeferredBlock<Block> AMETHYST_SLAB;
    public static final DeferredBlock<Block> AMETHYST_STAIRS;
    public static final DeferredBlock<Block> AMETHYST_WALL;
    public static final DeferredBlock<Block> BAMBOO_BRIDGE;
    public static final DeferredBlock<Block> BAMBOO_BRIDGE_STAIRS;
    public static final DeferredBlock<Block> BAMBOO_PICKET_FENCE;
    public static final DeferredBlock<Block> BIRCH_LOG_BRIDGE;
    public static final DeferredBlock<Block> BIRCH_LOG_BRIDGE_STAIRS;
    public static final DeferredBlock<Block> BIRCH_PICKET_FENCE;
    public static final DeferredBlock<Block> BLACKSTONE_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> BLACKSTONE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> BLOCK_SPAWNER;
    public static final DeferredBlock<Block> BLUE_DOTTED_LINE_BLOCK;
    public static final DeferredBlock<Block> BLUE_MUSHROOM_TRAMPOLINE;
    public static final DeferredBlock<Block> BLUE_TRAMPOLINE_CAP;
    public static final DeferredBlock<Block> BRAIN_CORAL_TOWER;
    public static final DeferredBlock<Block> BRICK_PEDESTAL;
    public static final DeferredBlock<Block> BUBBLE_CORAL_TOWER;
    public static final DeferredBlock<Block> CALCITE_BUTTON;
    public static final DeferredBlock<Block> CALCITE_CHECKERED_TILES;
    public static final DeferredBlock<Block> CALCITE_CHECKERED_TILE_SLAB;
    public static final DeferredBlock<Block> CALCITE_CHECKERED_TILE_STAIRS;
    public static final DeferredBlock<Block> CALCITE_CHECKERED_TILE_WALL;
    public static final DeferredBlock<Block> CALCITE_PRESSURE_PLATE;
    public static final DeferredBlock<Block> CALCITE_QUESTION_BLOCK;
    public static final DeferredBlock<Block> CALCITE_SLAB;
    public static final DeferredBlock<Block> CALCITE_STAIRS;
    public static final DeferredBlock<Block> CALCITE_WALL;
    public static final DeferredBlock<Block> CHERRY_LOG_BRIDGE;
    public static final DeferredBlock<Block> CHERRY_LOG_BRIDGE_STAIRS;
    public static final DeferredBlock<Block> CHERRY_PICKET_FENCE;
    public static final DeferredBlock<Block> CHISELED_AMETHYST_BRICKS;
    public static final DeferredBlock<Block> CHISELED_DEEP_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> CHISELED_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> CHISELED_POLISHED_DEEP_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> CHISELED_POLISHED_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> CHISELED_RED_SANDSTONE_BRICKS;
    public static final DeferredBlock<Block> CHISELED_SANDSTONE_BRICKS;
    public static final DeferredBlock<Block> CLASSIC_CHECKPOINT_FLAG;
    public static final DeferredBlock<Block> CLASSIC_GOAL_POLE;
    public static final DeferredBlock<Block> CLEAR_WARP_PIPE;
    public static final DeferredBlock<Block> COIN;
    public static final DeferredBlock<Block> COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> CRACKED_AMETHYST_BRICKS;
    public static final DeferredBlock<Block> CRACKED_DEEP_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> CRACKED_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> CRACKED_POLISHED_DEEP_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> CRACKED_POLISHED_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> CRACKED_RED_SANDSTONE_BRICKS;
    public static final DeferredBlock<Block> CRACKED_SANDSTONE_BRICKS;
    public static final DeferredBlock<Block> CRIMSON_PICKET_FENCE;
    public static final DeferredBlock<Block> CRIMSON_STEM_BRIDGE;
    public static final DeferredBlock<Block> CRIMSON_STEM_BRIDGE_STAIRS;
    public static final DeferredBlock<Block> CUT_COPPER_PEDESTAL;
    public static final DeferredBlock<Block> DAISY_ABILITY_BLOCK;
    public static final DeferredBlock<Block> DANGO_BLOSSOM;
    public static final DeferredBlock<Block> DARK_OAK_LOG_BRIDGE;
    public static final DeferredBlock<Block> DARK_OAK_LOG_BRIDGE_STAIRS;
    public static final DeferredBlock<Block> DARK_OAK_PICKET_FENCE;
    public static final DeferredBlock<Block> DARK_PRISMARINE_PEDESTAL;
    public static final DeferredBlock<Block> DARK_PRISMARINE_QUESTION_BLOCK;
    public static final DeferredBlock<Block> DEAD_BRAIN_CORAL_TOWER;
    public static final DeferredBlock<Block> DEAD_BUBBLE_CORAL_TOWER;
    public static final DeferredBlock<Block> DEAD_FIRE_CORAL_TOWER;
    public static final DeferredBlock<Block> DEAD_HORN_CORAL_TOWER;
    public static final DeferredBlock<Block> DEAD_TUBE_CORAL_TOWER;
    public static final DeferredBlock<Block> DEATH_BLOCK;
    public static final DeferredBlock<Block> DEEPSLATE_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> DEEPSLATE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> DEEPSLATE_QUESTION_TILES;
    public static final DeferredBlock<Block> DEEPSLATE_TILE_PEDESTAL;
    public static final DeferredBlock<Block> DEEP_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> DEEP_FUNGAL_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> DEEP_FUNGAL_BRICK_SLAB;
    public static final DeferredBlock<Block> DEEP_FUNGAL_BRICK_STAIRS;
    public static final DeferredBlock<Block> DEEP_FUNGAL_BRICK_WALL;
    public static final DeferredBlock<Block> DEEP_FUNGAL_COBBLESTONE;
    public static final DeferredBlock<Block> DEEP_FUNGAL_COBBLESTONE_SLAB;
    public static final DeferredBlock<Block> DEEP_FUNGAL_COBBLESTONE_STAIRS;
    public static final DeferredBlock<Block> DEEP_FUNGAL_COBBLESTONE_WALL;
    public static final DeferredBlock<Block> DEEP_FUNGAL_QUESTION_BLOCK;
    public static final DeferredBlock<Block> DEEP_FUNGAL_QUESTION_PANEL;
    public static final DeferredBlock<Block> DEEP_FUNGAL_STONE;
    public static final DeferredBlock<Block> DEEP_FUNGAL_STONE_BUTTON;
    public static final DeferredBlock<Block> DEEP_FUNGAL_STONE_PRESSURE_PLATE;
    public static final DeferredBlock<Block> DEEP_FUNGAL_STONE_SLAB;
    public static final DeferredBlock<Block> DEEP_FUNGAL_STONE_STAIRS;
    public static final DeferredBlock<Block> DEEP_FUNGAL_STONE_WALL;
    public static final DeferredBlock<Block> END_STONE_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> END_STONE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> EXPOSED_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> EXPOSED_CUT_COPPER_PEDESTAL;
    public static final DeferredBlock<Block> FIRE_CORAL_TOWER;
    public static final DeferredBlock<Block> FUNGAL_BRICKS;
    public static final DeferredBlock<Block> FUNGAL_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> FUNGAL_BRICK_SLAB;
    public static final DeferredBlock<Block> FUNGAL_BRICK_STAIRS;
    public static final DeferredBlock<Block> FUNGAL_BRICK_WALL;
    public static final DeferredBlock<Block> FUNGAL_COBBLESTONE;
    public static final DeferredBlock<Block> FUNGAL_COBBLESTONE_SLAB;
    public static final DeferredBlock<Block> FUNGAL_COBBLESTONE_STAIRS;
    public static final DeferredBlock<Block> FUNGAL_COBBLESTONE_WALL;
    public static final DeferredBlock<Block> FUNGAL_QUESTION_BLOCK;
    public static final DeferredBlock<Block> FUNGAL_QUESTION_PANEL;
    public static final DeferredBlock<Block> FUNGAL_STONE;
    public static final DeferredBlock<Block> FUNGAL_STONE_BUTTON;
    public static final DeferredBlock<Block> FUNGAL_STONE_PRESSURE_PLATE;
    public static final DeferredBlock<Block> FUNGAL_STONE_SLAB;
    public static final DeferredBlock<Block> FUNGAL_STONE_STAIRS;
    public static final DeferredBlock<Block> FUNGAL_STONE_WALL;
    public static final DeferredBlock<Block> GLOW_BLOCK;
    public static final DeferredBlock<Block> HARD_DEEP_FUNGAL_BLOCK;
    public static final DeferredBlock<Block> HARD_DEEP_FUNGAL_SLAB;
    public static final DeferredBlock<Block> HARD_DEEP_FUNGAL_STAIRS;
    public static final DeferredBlock<Block> HARD_DEEP_FUNGAL_WALL;
    public static final DeferredBlock<Block> HARD_FUNGAL_BLOCK;
    public static final DeferredBlock<Block> HARD_FUNGAL_SLAB;
    public static final DeferredBlock<Block> HARD_FUNGAL_STAIRS;
    public static final DeferredBlock<Block> HARD_FUNGAL_WALL;
    public static final DeferredBlock<Block> HORN_CORAL_TOWER;
    public static final DeferredBlock<Block> INVISIBLE_AMETHYST_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_BLACKSTONE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_CALCITE_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_DARK_PRISMARINE_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_DEEPSLATE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_DEEPSLATE_QUESTION_TILES;
    public static final DeferredBlock<Block> INVISIBLE_DEEP_FUNGAL_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_END_STONE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_EXPOSED_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_FUNGAL_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_MOSSY_STONE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_MUD_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_NETHER_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_OXIDIZED_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_PRISMARINE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_PURPUR_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_QUARTZ_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_RED_NETHER_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_RED_SANDSTONE_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_SANDSTONE_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_STONE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_TUFF_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_WAXED_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_WAXED_EXPOSED_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_WAXED_OXIDIZED_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_WAXED_WEATHERED_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_WEATHERED_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> IRON_SPIKE;
    public static final DeferredBlock<Block> JUNGLE_LOG_BRIDGE;
    public static final DeferredBlock<Block> JUNGLE_LOG_BRIDGE_STAIRS;
    public static final DeferredBlock<Block> JUNGLE_PICKET_FENCE;
    public static final DeferredBlock<Block> LUIGI_ABILITY_BLOCK;
    public static final DeferredBlock<Block> MANGROVE_LOG_BRIDGE;
    public static final DeferredBlock<Block> MANGROVE_LOG_BRIDGE_STAIRS;
    public static final DeferredBlock<Block> MANGROVE_PICKET_FENCE;
    public static final DeferredBlock<Block> MARIO_ABILITY_BLOCK;
    public static final DeferredBlock<Block> MONSTER_DEATH_BLOCK;
    public static final DeferredBlock<Block> MOSSY_STONE_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> MOSSY_STONE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> MUD_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> MUD_QUESTION_BRICKS;
    public static final DeferredBlock<Block> MUSHROOT_LEAVES;
    public static final DeferredBlock<Block> MUSHROOT_LOG;
    public static final DeferredBlock<Block> MUSHROOT_PICKET_FENCE;
    public static final DeferredBlock<Block> MUSHROOT_PLANKS;
    public static final DeferredBlock<Block> MUSHROOT_SAPLING;
    public static final DeferredBlock<Block> MUSHROOT_WOOD;
    public static final DeferredBlock<Block> NETHER_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> NETHER_QUESTION_BRICKS;
    public static final DeferredBlock<Block> OAK_LOG_BRIDGE;
    public static final DeferredBlock<Block> OAK_LOG_BRIDGE_STAIRS;
    public static final DeferredBlock<Block> OAK_PICKET_FENCE;
    public static final DeferredBlock<Block> ON_OFF_SWITCH;
    public static final DeferredBlock<Block> OXIDIZED_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> OXIDIZED_CUT_COPPER_PEDESTAL;
    public static final DeferredBlock<Block> PASSIVE_DEATH_BLOCK;
    public static final DeferredBlock<Block> PEACH_ABILITY_BLOCK;
    public static final DeferredBlock<Block> PIPE_BUBBLES;
    public static final DeferredBlock<Block> PLAYER_DEATH_BLOCK;
    public static final DeferredBlock<Block> POLISHED_AMETHYST;
    public static final DeferredBlock<Block> POLISHED_AMETHYST_SLAB;
    public static final DeferredBlock<Block> POLISHED_AMETHYST_STAIRS;
    public static final DeferredBlock<Block> POLISHED_AMETHYST_WALL;
    public static final DeferredBlock<Block> POLISHED_DEEP_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> POLISHED_DEEP_FUNGAL_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> POLISHED_DEEP_FUNGAL_BRICK_SLAB;
    public static final DeferredBlock<Block> POLISHED_DEEP_FUNGAL_BRICK_STAIRS;
    public static final DeferredBlock<Block> POLISHED_DEEP_FUNGAL_BRICK_WALL;
    public static final DeferredBlock<Block> POLISHED_DEEP_FUNGAL_STONE;
    public static final DeferredBlock<Block> POLISHED_DEEP_FUNGAL_STONE_SLAB;
    public static final DeferredBlock<Block> POLISHED_DEEP_FUNGAL_STONE_STAIRS;
    public static final DeferredBlock<Block> POLISHED_DEEP_FUNGAL_STONE_WALL;
    public static final DeferredBlock<Block> POLISHED_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> POLISHED_FUNGAL_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> POLISHED_FUNGAL_BRICK_SLAB;
    public static final DeferredBlock<Block> POLISHED_FUNGAL_BRICK_STAIRS;
    public static final DeferredBlock<Block> POLISHED_FUNGAL_BRICK_WALL;
    public static final DeferredBlock<Block> POLISHED_FUNGAL_STONE;
    public static final DeferredBlock<Block> POLISHED_FUNGAL_STONE_SLAB;
    public static final DeferredBlock<Block> POLISHED_FUNGAL_STONE_STAIRS;
    public static final DeferredBlock<Block> POLISHED_FUNGAL_STONE_WALL;
    public static final DeferredBlock<Block> POLISHED_WHITE_CALCITE_SLAB;
    public static final DeferredBlock<Block> POLISHED_WHITE_CALCITE_STAIRS;
    public static final DeferredBlock<Block> POLISHED_WHITE_CALCITE_WALL;
    public static final DeferredBlock<Block> POTTED_BLUE_TRAMPOLINE_CAP;
    public static final DeferredBlock<Block> POTTED_DANGO_BLOSSOM;
    public static final DeferredBlock<Block> POTTED_MUSHROOT_SAPLING;
    public static final DeferredBlock<Block> POTTED_PIRANHA_PLANT;
    public static final DeferredBlock<Block> POTTED_RED_TRAMPOLINE_CAP;
    public static final DeferredBlock<Block> PRISMARINE_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> PRISMARINE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> PURPUR_BLOCK_PEDESTAL;
    public static final DeferredBlock<Block> PURPUR_QUESTION_BLOCK;
    public static final DeferredBlock<Block> QUARTZ_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> QUARTZ_QUESTION_BRICKS;
    public static final DeferredBlock<Block> QUESTION_BRICKS;
    public static final DeferredBlock<Block> QUICKSAND;
    public static final DeferredBlock<Block> RED_DOTTED_LINE_BLOCK;
    public static final DeferredBlock<Block> RED_MUSHROOM_TRAMPOLINE;
    public static final DeferredBlock<Block> RED_NETHER_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> RED_NETHER_QUESTION_BRICKS;
    public static final DeferredBlock<Block> RED_QUICKSAND;
    public static final DeferredBlock<Block> RED_SANDSTONE_BRICKS;
    public static final DeferredBlock<Block> RED_SANDSTONE_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> RED_SANDSTONE_BRICK_SLAB;
    public static final DeferredBlock<Block> RED_SANDSTONE_BRICK_STAIRS;
    public static final DeferredBlock<Block> RED_SANDSTONE_BRICK_WALL;
    public static final DeferredBlock<Block> RED_SANDSTONE_QUESTION_BLOCK;
    public static final DeferredBlock<Block> RED_TRAMPOLINE_CAP;
    public static final DeferredBlock<Block> ROCKY_DEEP_FUNGAL_STONE;
    public static final DeferredBlock<Block> ROCKY_DEEP_FUNGAL_STONE_SLAB;
    public static final DeferredBlock<Block> ROCKY_DEEP_FUNGAL_STONE_STAIRS;
    public static final DeferredBlock<Block> ROCKY_DEEP_FUNGAL_STONE_WALL;
    public static final DeferredBlock<Block> ROCKY_FUNGAL_STONE;
    public static final DeferredBlock<Block> ROCKY_FUNGAL_STONE_SLAB;
    public static final DeferredBlock<Block> ROCKY_FUNGAL_STONE_STAIRS;
    public static final DeferredBlock<Block> ROCKY_FUNGAL_STONE_WALL;
    public static final DeferredBlock<Block> ROSALINA_ABILITY_BLOCK;
    public static final DeferredBlock<Block> SANDSTONE_BRICKS;
    public static final DeferredBlock<Block> SANDSTONE_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> SANDSTONE_BRICK_SLAB;
    public static final DeferredBlock<Block> SANDSTONE_BRICK_STAIRS;
    public static final DeferredBlock<Block> SANDSTONE_BRICK_WALL;
    public static final DeferredBlock<Block> SANDSTONE_QUESTION_BLOCK;
    public static final DeferredBlock<Block> SMASHABLE_BLACKSTONE_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_CUT_COPPER;
    public static final DeferredBlock<Block> SMASHABLE_DARK_PRISMARINE;
    public static final DeferredBlock<Block> SMASHABLE_DEEPSLATE_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_DEEPSLATE_TILES;
    public static final DeferredBlock<Block> SMASHABLE_END_STONE_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_EXPOSED_CUT_COPPER;
    public static final DeferredBlock<Block> SMASHABLE_MOSSY_STONE_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_MUD_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_NETHER_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_OXIDIZED_CUT_COPPER;
    public static final DeferredBlock<Block> SMASHABLE_PRISMARINE_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_PURPUR_BLOCK;
    public static final DeferredBlock<Block> SMASHABLE_QUARTZ_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_RED_NETHER_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_STONE_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_TUFF_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_WAXED_CUT_COPPER;
    public static final DeferredBlock<Block> SMASHABLE_WAXED_EXPOSED_CUT_COPPER;
    public static final DeferredBlock<Block> SMASHABLE_WAXED_OXIDIZED_CUT_COPPER;
    public static final DeferredBlock<Block> SMASHABLE_WAXED_WEATHERED_CUT_COPPER;
    public static final DeferredBlock<Block> SMASHABLE_WEATHERED_CUT_COPPER;
    public static final DeferredBlock<Block> SPIKE_PANEL;
    public static final DeferredBlock<Block> SPLUNKIN_CARVED_PUMPKIN;
    public static final DeferredBlock<Block> SPLUNKIN_O_LANTERN;
    public static final DeferredBlock<Block> SPRUCE_LOG_BRIDGE;
    public static final DeferredBlock<Block> SPRUCE_LOG_BRIDGE_STAIRS;
    public static final DeferredBlock<Block> SPRUCE_PICKET_FENCE;
    public static final DeferredBlock<Block> STAR_COIN;
    public static final DeferredBlock<Block> STEVE_ABILITY_BLOCK;
    public static final DeferredBlock<Block> STONE_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> STONE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> STORAGE_AMETHYST_BRICKS;
    public static final DeferredBlock<Block> STORAGE_BLACKSTONE_BRICKS;
    public static final DeferredBlock<Block> STORAGE_BRICKS;
    public static final DeferredBlock<Block> STORAGE_CUT_COPPER;
    public static final DeferredBlock<Block> STORAGE_DARK_PRISMARINE;
    public static final DeferredBlock<Block> STORAGE_DEEPSLATE_BRICKS;
    public static final DeferredBlock<Block> STORAGE_DEEPSLATE_TILES;
    public static final DeferredBlock<Block> STORAGE_DEEP_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> STORAGE_END_STONE_BRICKS;
    public static final DeferredBlock<Block> STORAGE_EXPOSED_CUT_COPPER;
    public static final DeferredBlock<Block> STORAGE_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> STORAGE_MOSSY_STONE_BRICKS;
    public static final DeferredBlock<Block> STORAGE_MUD_BRICKS;
    public static final DeferredBlock<Block> STORAGE_NETHER_BRICKS;
    public static final DeferredBlock<Block> STORAGE_OXIDIZED_CUT_COPPER;
    public static final DeferredBlock<Block> STORAGE_POLISHED_DEEP_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> STORAGE_POLISHED_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> STORAGE_PRISMARINE_BRICKS;
    public static final DeferredBlock<Block> STORAGE_PURPUR_BLOCK;
    public static final DeferredBlock<Block> STORAGE_QUARTZ_BRICKS;
    public static final DeferredBlock<Block> STORAGE_RED_NETHER_BRICKS;
    public static final DeferredBlock<Block> STORAGE_RED_SANDSTONE_BRICKS;
    public static final DeferredBlock<Block> STORAGE_SANDSTONE_BRICKS;
    public static final DeferredBlock<Block> STORAGE_STONE_BRICKS;
    public static final DeferredBlock<Block> STORAGE_TUFF_BRICKS;
    public static final DeferredBlock<Block> STORAGE_WAXED_CUT_COPPER;
    public static final DeferredBlock<Block> STORAGE_WAXED_EXPOSED_CUT_COPPER;
    public static final DeferredBlock<Block> STORAGE_WAXED_OXIDIZED_CUT_COPPER;
    public static final DeferredBlock<Block> STORAGE_WAXED_WEATHERED_CUT_COPPER;
    public static final DeferredBlock<Block> STORAGE_WEATHERED_CUT_COPPER;
    public static final DeferredBlock<Block> STRIPPED_ACACIA_LOG_BRIDGE;
    public static final DeferredBlock<Block> STRIPPED_ACACIA_LOG_BRIDGE_STAIRS;
    public static final DeferredBlock<Block> STRIPPED_BAMBOO_BRIDGE;
    public static final DeferredBlock<Block> STRIPPED_BAMBOO_BRIDGE_STAIRS;
    public static final DeferredBlock<Block> STRIPPED_BIRCH_LOG_BRIDGE;
    public static final DeferredBlock<Block> STRIPPED_BIRCH_LOG_BRIDGE_STAIRS;
    public static final DeferredBlock<Block> STRIPPED_CHERRY_LOG_BRIDGE;
    public static final DeferredBlock<Block> STRIPPED_CHERRY_LOG_BRIDGE_STAIRS;
    public static final DeferredBlock<Block> STRIPPED_CRIMSON_STEM_BRIDGE;
    public static final DeferredBlock<Block> STRIPPED_CRIMSON_STEM_BRIDGE_STAIRS;
    public static final DeferredBlock<Block> STRIPPED_DARK_OAK_LOG_BRIDGE;
    public static final DeferredBlock<Block> STRIPPED_DARK_OAK_LOG_BRIDGE_STAIRS;
    public static final DeferredBlock<Block> STRIPPED_JUNGLE_LOG_BRIDGE;
    public static final DeferredBlock<Block> STRIPPED_JUNGLE_LOG_BRIDGE_STAIRS;
    public static final DeferredBlock<Block> STRIPPED_MANGROVE_LOG_BRIDGE;
    public static final DeferredBlock<Block> STRIPPED_MANGROVE_LOG_BRIDGE_STAIRS;
    public static final DeferredBlock<Block> STRIPPED_MUSHROOT_LOG;
    public static final DeferredBlock<Block> STRIPPED_MUSHROOT_WOOD;
    public static final DeferredBlock<Block> STRIPPED_OAK_LOG_BRIDGE;
    public static final DeferredBlock<Block> STRIPPED_OAK_LOG_BRIDGE_STAIRS;
    public static final DeferredBlock<Block> STRIPPED_SPRUCE_LOG_BRIDGE;
    public static final DeferredBlock<Block> STRIPPED_SPRUCE_LOG_BRIDGE_STAIRS;
    public static final DeferredBlock<Block> STRIPPED_WARPED_STEM_BRIDGE;
    public static final DeferredBlock<Block> STRIPPED_WARPED_STEM_BRIDGE_STAIRS;
    public static final DeferredBlock<Block> TUBE_CORAL_TOWER;
    public static final DeferredBlock<Block> TUFF_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> TUFF_QUESTION_BRICKS;
    public static final DeferredBlock<Block> WALUIGI_ABILITY_BLOCK;
    public static final DeferredBlock<Block> WARIO_ABILITY_BLOCK;
    public static final DeferredBlock<Block> WARPED_PICKET_FENCE;
    public static final DeferredBlock<Block> WARPED_STEM_BRIDGE;
    public static final DeferredBlock<Block> WARPED_STEM_BRIDGE_STAIRS;
    public static final DeferredBlock<Block> WATER_SPOUT;
    public static final DeferredBlock<Block> WAXED_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> WAXED_CUT_COPPER_PEDESTAL;
    public static final DeferredBlock<Block> WAXED_EXPOSED_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> WAXED_EXPOSED_CUT_COPPER_PEDESTAL;
    public static final DeferredBlock<Block> WAXED_OXIDIZED_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> WAXED_OXIDIZED_CUT_COPPER_PEDESTAL;
    public static final DeferredBlock<Block> WAXED_WEATHERED_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> WAXED_WEATHERED_CUT_COPPER_PEDESTAL;
    public static final DeferredBlock<Block> WEATHERED_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> WEATHERED_CUT_COPPER_PEDESTAL;
    public static final DeferredBlock<Block> WHITE_CALCITE_BRICK_SLAB;
    public static final DeferredBlock<Block> WHITE_CALCITE_BRICK_STAIRS;
    public static final DeferredBlock<Block> WHITE_CALCITE_BRICK_WALL;

    static {
        BLOCK_SPAWNER = registerBlock("block_spawner",
                () -> new BlockSpawnerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SPAWNER)
                        .sound(SoundType.TRIAL_SPAWNER).strength(-1.0F, 3600000.0F).dynamicShape()));

        DEATH_BLOCK = registerBlock("death_block",
                () -> new DeathBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK)
                        .sound(SoundType.NETHERITE_BLOCK)));
        MONSTER_DEATH_BLOCK = registerBlock("monster_death_block",
                () -> new MonsterDeathBlock(BlockBehaviour.Properties.ofFullCopy(DEATH_BLOCK.get())));
        PASSIVE_DEATH_BLOCK = registerBlock("passive_death_block",
                () -> new PassiveDeathBlock(BlockBehaviour.Properties.ofFullCopy(DEATH_BLOCK.get())));
        PLAYER_DEATH_BLOCK = registerBlock("player_death_block",
                () -> new PlayerDeathBlock(BlockBehaviour.Properties.ofFullCopy(DEATH_BLOCK.get())));
        DAISY_ABILITY_BLOCK = registerBlock("daisy_ability_block",
                () -> new DaisyAbilityBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE)
                        .sound(SoundType.NETHERITE_BLOCK).strength(5.0F, 6.0F)
                        .requiresCorrectToolForDrops()));
        LUIGI_ABILITY_BLOCK = registerBlock("luigi_ability_block",
                () -> new LuigiAbilityBlock(BlockBehaviour.Properties.ofFullCopy(DAISY_ABILITY_BLOCK.get())
                        .mapColor(MapColor.COLOR_GREEN)));
        MARIO_ABILITY_BLOCK = registerBlock("mario_ability_block",
                () -> new MarioAbilityBlock(BlockBehaviour.Properties.ofFullCopy(DAISY_ABILITY_BLOCK.get())
                        .mapColor(MapColor.COLOR_RED)));
        PEACH_ABILITY_BLOCK = registerBlock("peach_ability_block",
                () -> new PeachAbilityBlock(BlockBehaviour.Properties.ofFullCopy(DAISY_ABILITY_BLOCK.get())
                        .mapColor(MapColor.COLOR_PINK)));
        ROSALINA_ABILITY_BLOCK = registerBlock("rosalina_ability_block",
                () -> new RosalinaAbilityBlock(BlockBehaviour.Properties.ofFullCopy(DAISY_ABILITY_BLOCK.get())
                        .mapColor(MapColor.COLOR_LIGHT_BLUE)));
        STEVE_ABILITY_BLOCK = registerBlock("steve_ability_block",
                () -> new AbilityBlock(BlockBehaviour.Properties.ofFullCopy(DAISY_ABILITY_BLOCK.get())
                        .mapColor(MapColor.COLOR_BLUE)));
        WALUIGI_ABILITY_BLOCK = registerBlock("waluigi_ability_block",
                () -> new WaluigiAbilityBlock(BlockBehaviour.Properties.ofFullCopy(DAISY_ABILITY_BLOCK.get())
                        .mapColor(MapColor.COLOR_PURPLE)));
        WARIO_ABILITY_BLOCK = registerBlock("wario_ability_block",
                () -> new WarioAbilityBlock(BlockBehaviour.Properties.ofFullCopy(DAISY_ABILITY_BLOCK.get())
                        .mapColor(MapColor.COLOR_YELLOW)));

        STAR_COIN = registerNoItemBlock("star_coin",
                () -> new StarCoinBlock(BlockBehaviour.Properties.of().mapColor(MapColor.GOLD)
                        .sound(MarioverseSoundTypes.COIN_TYPE).instrument(NoteBlockInstrument.CHIME)
                        .pushReaction(PushReaction.DESTROY).isSuffocating(BlockRegistry::never)
                        .isViewBlocking(BlockRegistry::never).strength(0.5F, 0.5F)
                        .instabreak().noCollission()));

        COIN = registerBlock("coin",
                () -> new CoinBlock(BlockBehaviour.Properties.of().mapColor(MapColor.GOLD)
                        .sound(MarioverseSoundTypes.COIN_TYPE).instrument(NoteBlockInstrument.CHIME)
                        .pushReaction(PushReaction.DESTROY).isSuffocating(BlockRegistry::never)
                        .isViewBlocking(BlockRegistry::never).strength(0.5F, 0.5F)
                        .instabreak().noCollission()));


        IRON_SPIKE = registerBlock("iron_spike",
                () -> new IronSpikeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                        .sound(SoundType.NETHERITE_BLOCK).instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                        .isSuffocating(BlockRegistry::never).isViewBlocking(BlockRegistry::never)
                        .strength(25.0F, 1200.0F).requiresCorrectToolForDrops()));
        SPIKE_PANEL = registerBlock("spike_panel",
                () -> new SpikePanelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                        .sound(SoundType.NETHERITE_BLOCK).instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                        .strength(25.0F, 1200.0F).noCollission().forceSolidOn()
                        .requiresCorrectToolForDrops()));


        GLOW_BLOCK = registerBlock("glow_block",
                () -> new GlowBlock(BlockBehaviour.Properties.of().mapColor(MapColor.SAND)
                        .sound(SoundType.FROGLIGHT).lightLevel(light -> 15).strength(0.3F)));
        SPLUNKIN_CARVED_PUMPKIN = registerBlock("splunkin_carved_pumpkin",
                () -> new EquipableCarvedPumpkinBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CARVED_PUMPKIN)));
        SPLUNKIN_O_LANTERN = registerBlock("splunkin_o_lantern",
                () -> new SplunkinCarvedPumpkinBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JACK_O_LANTERN)
                        .lightLevel(state -> state.getValue(SplunkinCarvedPumpkinBlock.CRACKED) ? 15 : 10)));


        ON_OFF_SWITCH = registerBlock("on_off_switch",
                () -> new OnOffSwitchBlock(BlockBehaviour.Properties.of()
                        .mapColor(state -> state.getValue(OnBlock.ACTIVE) ? MapColor.COLOR_RED : MapColor.COLOR_BLUE)
                        .sound(SoundType.NETHERITE_BLOCK).instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                        .strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
        RED_DOTTED_LINE_BLOCK = registerBlock("red_dotted_line_block",
                () -> new RedDottedLineBlock(BlockBehaviour.Properties.ofFullCopy(ON_OFF_SWITCH.get())
                        .mapColor(state -> state.getValue(OnBlock.ACTIVE) ? MapColor.COLOR_RED : MapColor.NONE)
                        .isValidSpawn(BlockRegistry::isActive).isRedstoneConductor(BlockRegistry::isActive)
                        .isSuffocating(BlockRegistry::isActive).isViewBlocking(BlockRegistry::isActive)
                        .noOcclusion()));
        BLUE_DOTTED_LINE_BLOCK = registerBlock("blue_dotted_line_block",
                () -> new BlueDottedLineBlock(BlockBehaviour.Properties.ofFullCopy(ON_OFF_SWITCH.get())
                        .mapColor(state -> !state.getValue(OnBlock.ACTIVE) ? MapColor.COLOR_BLUE : MapColor.NONE)
                        .isValidSpawn(BlockRegistry::isActive).isRedstoneConductor(BlockRegistry::isActive)
                        .isSuffocating(BlockRegistry::isActive).isViewBlocking(BlockRegistry::isActive)
                        .noOcclusion()));
        RED_MUSHROOM_TRAMPOLINE = registerBlock("red_mushroom_trampoline",
                () -> new RedMushroomTrampolineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.RED_MUSHROOM_BLOCK)
                        .mapColor(state -> state.getValue(OnBlock.ACTIVE) ? MapColor.COLOR_RED : MapColor.COLOR_LIGHT_GRAY)));
        BLUE_MUSHROOM_TRAMPOLINE = registerBlock("blue_mushroom_trampoline",
                () -> new BlueMushroomTrampolineBlock(BlockBehaviour.Properties.ofFullCopy(RED_MUSHROOM_TRAMPOLINE.get())
                        .mapColor(state -> !state.getValue(OnBlock.ACTIVE) ? MapColor.COLOR_BLUE : MapColor.COLOR_LIGHT_GRAY)));
        RED_TRAMPOLINE_CAP = registerBlock("red_trampoline_cap",
                () -> new TrampolineCapBlock(TreeRegistry.HUGE_RED_TRAMPOLINE_CAP.getKey(), BlockBehaviour.Properties.ofFullCopy(Blocks.BROWN_MUSHROOM)
                        .mapColor(state -> state.getValue(OnBlock.ACTIVE) ? MapColor.COLOR_RED : MapColor.COLOR_LIGHT_GRAY)
                        .lightLevel(state -> 0).offsetType(BlockBehaviour.OffsetType.XYZ)));
        BLUE_TRAMPOLINE_CAP = registerBlock("blue_trampoline_cap",
                () -> new TrampolineCapBlock(TreeRegistry.HUGE_BLUE_TRAMPOLINE_CAP.getKey(), BlockBehaviour.Properties.ofFullCopy(Blocks.BROWN_MUSHROOM)
                        .mapColor(state -> !state.getValue(OnBlock.ACTIVE) ? MapColor.COLOR_BLUE : MapColor.COLOR_LIGHT_GRAY)
                        .lightLevel(state -> 0).offsetType(BlockBehaviour.OffsetType.XYZ)));
        POTTED_RED_TRAMPOLINE_CAP = registerNoItemBlock("potted_red_trampoline_cap",
                () -> new PottedTrampolineCapBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, BlockRegistry.RED_TRAMPOLINE_CAP,
                        BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_BROWN_MUSHROOM)));
        POTTED_BLUE_TRAMPOLINE_CAP = registerNoItemBlock("potted_blue_trampoline_cap",
                () -> new PottedTrampolineCapBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, BlockRegistry.BLUE_TRAMPOLINE_CAP,
                        BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_BROWN_MUSHROOM)));


        BRAIN_CORAL_TOWER = registerBlock("brain_coral_tower",
                () -> new CoralTowerBlock(BlockRegistry.DEAD_BRAIN_CORAL_TOWER, BlockBehaviour.Properties.of()
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F).mapColor(MapColor.COLOR_PINK)
                        .sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY)));
        BUBBLE_CORAL_TOWER = registerBlock("bubble_coral_tower",
                () -> new CoralTowerBlock(BlockRegistry.DEAD_BUBBLE_CORAL_TOWER, BlockBehaviour.Properties.of()
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F).mapColor(MapColor.COLOR_PURPLE)
                        .sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY)));
        FIRE_CORAL_TOWER = registerBlock("fire_coral_tower",
                () -> new CoralTowerBlock(BlockRegistry.DEAD_FIRE_CORAL_TOWER, BlockBehaviour.Properties.of()
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F).mapColor(MapColor.COLOR_RED)
                        .sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY)));
        HORN_CORAL_TOWER = registerBlock("horn_coral_tower",
                () -> new CoralTowerBlock(BlockRegistry.DEAD_HORN_CORAL_TOWER, BlockBehaviour.Properties.of()
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F).mapColor(MapColor.COLOR_YELLOW)
                        .sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY)));
        TUBE_CORAL_TOWER = registerBlock("tube_coral_tower",
                () -> new CoralTowerBlock(BlockRegistry.DEAD_TUBE_CORAL_TOWER, BlockBehaviour.Properties.of()
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F).mapColor(MapColor.COLOR_BLUE)
                        .sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY)));

        DEAD_BRAIN_CORAL_TOWER = registerBlock("dead_brain_coral_tower",
                () -> new DeadCoralTowerBlock(BlockRegistry.BRAIN_CORAL_TOWER, BlockBehaviour.Properties.of()
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F).mapColor(MapColor.COLOR_GRAY)
                        .sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY)));
        DEAD_BUBBLE_CORAL_TOWER = registerBlock("dead_bubble_coral_tower",
                () -> new DeadCoralTowerBlock(BlockRegistry.BUBBLE_CORAL_TOWER, BlockBehaviour.Properties.of()
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F).mapColor(MapColor.COLOR_GRAY)
                        .sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY)));
        DEAD_FIRE_CORAL_TOWER = registerBlock("dead_fire_coral_tower",
                () -> new DeadCoralTowerBlock(BlockRegistry.FIRE_CORAL_TOWER, BlockBehaviour.Properties.of()
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F).mapColor(MapColor.COLOR_GRAY)
                        .sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY)));
        DEAD_HORN_CORAL_TOWER = registerBlock("dead_horn_coral_tower",
                () -> new DeadCoralTowerBlock(BlockRegistry.HORN_CORAL_TOWER, BlockBehaviour.Properties.of()
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F).mapColor(MapColor.COLOR_GRAY)
                        .sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY)));
        DEAD_TUBE_CORAL_TOWER = registerBlock("dead_tube_coral_tower",
                () -> new DeadCoralTowerBlock(BlockRegistry.TUBE_CORAL_TOWER, BlockBehaviour.Properties.of()
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F).mapColor(MapColor.COLOR_GRAY)
                        .sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY)));

        DANGO_BLOSSOM = registerBlock("dango_blossom",
                () -> new DangoBlossomBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SPORE_BLOSSOM)));

        POTTED_DANGO_BLOSSOM = registerNoItemBlock("potted_dango_blossom",
                () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, BlockRegistry.DANGO_BLOSSOM,
                        BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));

        POTTED_PIRANHA_PLANT = registerNoItemBlock("potted_piranha_plant",
                () -> new PottedPiranhaPlantBlock(null, () -> Blocks.AIR,
                        BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));


        MUSHROOT_LOG = registerBlock("mushroot_log", () -> log(MapColor.WOOD, MapColor.TERRACOTTA_ORANGE));

        STRIPPED_MUSHROOT_LOG = registerBlock("stripped_mushroot_log", () -> log(MapColor.WOOD, MapColor.WOOD));

        MUSHROOT_WOOD = registerBlock("mushroot_wood", () -> log(MapColor.TERRACOTTA_ORANGE, MapColor.TERRACOTTA_ORANGE));

        STRIPPED_MUSHROOT_WOOD = registerBlock("stripped_mushroot_wood", () -> log(MapColor.WOOD, MapColor.WOOD));

        MUSHROOT_LEAVES = registerBlock("mushroot_leaves", () -> leaves(SoundType.GRASS));

        MUSHROOT_PLANKS = registerBlock("mushroot_planks",
                () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD)
                        .instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD)
                        .strength(2.0F, 3.0F).ignitedByLava()));

        MUSHROOT_SAPLING = registerBlock("mushroot_sapling",
                () -> new SaplingBlock(SuperTreeGrower.MUSHROOT, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT)
                        .sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY)
                        .noCollission().randomTicks().instabreak()));

        POTTED_MUSHROOT_SAPLING = registerNoItemBlock("potted_mushroot_sapling",
                () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, BlockRegistry.MUSHROOT_SAPLING,
                        BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));


        OAK_PICKET_FENCE = registerBlock("oak_picket_fence",
                () -> new PicketFenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE)));
        SPRUCE_PICKET_FENCE = registerBlock("spruce_picket_fence",
                () -> new PicketFenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_FENCE)));
        BIRCH_PICKET_FENCE = registerBlock("birch_picket_fence",
                () -> new PicketFenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BIRCH_FENCE)));
        ACACIA_PICKET_FENCE = registerBlock("acacia_picket_fence",
                () -> new PicketFenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_FENCE)));
        JUNGLE_PICKET_FENCE = registerBlock("jungle_picket_fence",
                () -> new PicketFenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_FENCE)));
        CHERRY_PICKET_FENCE = registerBlock("cherry_picket_fence",
                () -> new PicketFenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHERRY_FENCE)));
        DARK_OAK_PICKET_FENCE = registerBlock("dark_oak_picket_fence",
                () -> new PicketFenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_OAK_FENCE)));
        MANGROVE_PICKET_FENCE = registerBlock("mangrove_picket_fence",
                () -> new PicketFenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MANGROVE_FENCE)));
        BAMBOO_PICKET_FENCE = registerBlock("bamboo_picket_fence",
                () -> new PicketFenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BAMBOO_FENCE)));
        CRIMSON_PICKET_FENCE = registerBlock("crimson_picket_fence",
                () -> new PicketFenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_FENCE)));
        WARPED_PICKET_FENCE = registerBlock("warped_picket_fence",
                () -> new PicketFenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_FENCE)));
        MUSHROOT_PICKET_FENCE = registerBlock("mushroot_picket_fence",
                () -> new PicketFenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE)));
        WHITE_PICKET_FENCE = registerBlock("white_picket_fence",
                () -> new PicketFenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE).mapColor(MapColor.SNOW)));
        RED_PICKET_FENCE = registerBlock("red_picket_fence",
                () -> new PicketFenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE).mapColor(MapColor.COLOR_RED)));


        OAK_LOG_BRIDGE = registerBlock("oak_log_bridge",
                () -> new BridgeBlock(Blocks.OAK_LOG, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG).mapColor(MapColor.PODZOL)));

        STRIPPED_OAK_LOG_BRIDGE = registerBlock("stripped_oak_log_bridge",
                () -> new BridgeBlock(Blocks.STRIPPED_OAK_LOG, BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG).mapColor(MapColor.WOOD)));

        SPRUCE_LOG_BRIDGE = registerBlock("spruce_log_bridge",
                () -> new BridgeBlock(Blocks.SPRUCE_LOG, BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_LOG).mapColor(MapColor.COLOR_BROWN)));

        STRIPPED_SPRUCE_LOG_BRIDGE = registerBlock("stripped_spruce_log_bridge",
                () -> new BridgeBlock(Blocks.STRIPPED_SPRUCE_LOG, BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_SPRUCE_LOG).mapColor(MapColor.PODZOL)));

        BIRCH_LOG_BRIDGE = registerBlock("birch_log_bridge",
                () -> new BridgeBlock(Blocks.BIRCH_LOG, BlockBehaviour.Properties.ofFullCopy(Blocks.BIRCH_LOG).mapColor(MapColor.QUARTZ)));

        STRIPPED_BIRCH_LOG_BRIDGE = registerBlock("stripped_birch_log_bridge",
                () -> new BridgeBlock(Blocks.STRIPPED_BIRCH_LOG, BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_BIRCH_LOG).mapColor(MapColor.SAND)));

        JUNGLE_LOG_BRIDGE = registerBlock("jungle_log_bridge",
                () -> new BridgeBlock(Blocks.JUNGLE_LOG, BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_LOG).mapColor(MapColor.PODZOL)));

        STRIPPED_JUNGLE_LOG_BRIDGE = registerBlock("stripped_jungle_log_bridge",
                () -> new BridgeBlock(Blocks.STRIPPED_JUNGLE_LOG, BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_JUNGLE_LOG).mapColor(MapColor.DIRT)));

        ACACIA_LOG_BRIDGE = registerBlock("acacia_log_bridge",
                () -> new BridgeBlock(Blocks.ACACIA_LOG, BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_LOG).mapColor(MapColor.STONE)));

        STRIPPED_ACACIA_LOG_BRIDGE = registerBlock("stripped_acacia_log_bridge",
                () -> new BridgeBlock(Blocks.STRIPPED_ACACIA_LOG, BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_ACACIA_LOG).mapColor(MapColor.COLOR_ORANGE)));

        DARK_OAK_LOG_BRIDGE = registerBlock("dark_oak_log_bridge",
                () -> new BridgeBlock(Blocks.DARK_OAK_LOG, BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_OAK_LOG).mapColor(MapColor.COLOR_BROWN)));

        STRIPPED_DARK_OAK_LOG_BRIDGE = registerBlock("stripped_dark_oak_log_bridge",
                () -> new BridgeBlock(Blocks.STRIPPED_DARK_OAK_LOG, BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_DARK_OAK_LOG).mapColor(MapColor.COLOR_BROWN)));

        MANGROVE_LOG_BRIDGE = registerBlock("mangrove_log_bridge",
                () -> new BridgeBlock(Blocks.MANGROVE_LOG, BlockBehaviour.Properties.ofFullCopy(Blocks.MANGROVE_LOG).mapColor(MapColor.PODZOL)));

        STRIPPED_MANGROVE_LOG_BRIDGE = registerBlock("stripped_mangrove_log_bridge",
                () -> new BridgeBlock(Blocks.STRIPPED_MANGROVE_LOG, BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_MANGROVE_LOG).mapColor(MapColor.COLOR_RED)));

        CHERRY_LOG_BRIDGE = registerBlock("cherry_log_bridge",
                () -> new BridgeBlock(Blocks.CHERRY_LOG, BlockBehaviour.Properties.ofFullCopy(Blocks.CHERRY_LOG).mapColor(MapColor.TERRACOTTA_GRAY)));

        STRIPPED_CHERRY_LOG_BRIDGE = registerBlock("stripped_cherry_log_bridge",
                () -> new BridgeBlock(Blocks.STRIPPED_CHERRY_LOG, BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_CHERRY_LOG).mapColor(MapColor.TERRACOTTA_PINK)));

        BAMBOO_BRIDGE = registerBlock("bamboo_bridge",
                () -> new BridgeBlock(Blocks.BAMBOO_BLOCK, BlockBehaviour.Properties.ofFullCopy(Blocks.BAMBOO_BLOCK).mapColor(MapColor.PLANT)));

        STRIPPED_BAMBOO_BRIDGE = registerBlock("stripped_bamboo_bridge",
                () -> new BridgeBlock(Blocks.STRIPPED_BAMBOO_BLOCK, BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_BAMBOO_BLOCK).mapColor(MapColor.COLOR_YELLOW)));

        CRIMSON_STEM_BRIDGE = registerBlock("crimson_stem_bridge",
                () -> new BridgeBlock(Blocks.CRIMSON_STEM, BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_STEM)));

        STRIPPED_CRIMSON_STEM_BRIDGE = registerBlock("stripped_crimson_stem_bridge",
                () -> new BridgeBlock(Blocks.STRIPPED_CRIMSON_STEM, BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_CRIMSON_STEM)));

        WARPED_STEM_BRIDGE = registerBlock("warped_stem_bridge",
                () -> new BridgeBlock(Blocks.WARPED_STEM, BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_STEM)));

        STRIPPED_WARPED_STEM_BRIDGE = registerBlock("stripped_warped_stem_bridge",
                () -> new BridgeBlock(Blocks.STRIPPED_WARPED_STEM, BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_WARPED_STEM)));


        OAK_LOG_BRIDGE_STAIRS = registerBlock("oak_log_bridge_stairs",
                () -> new BridgeStairBlock(Blocks.OAK_LOG.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG).mapColor(MapColor.PODZOL)));

        STRIPPED_OAK_LOG_BRIDGE_STAIRS = registerBlock("stripped_oak_log_bridge_stairs",
                () -> new BridgeStairBlock(Blocks.STRIPPED_OAK_LOG.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG).mapColor(MapColor.WOOD)));

        SPRUCE_LOG_BRIDGE_STAIRS = registerBlock("spruce_log_bridge_stairs",
                () -> new BridgeStairBlock(Blocks.SPRUCE_LOG.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_LOG).mapColor(MapColor.COLOR_BROWN)));

        STRIPPED_SPRUCE_LOG_BRIDGE_STAIRS = registerBlock("stripped_spruce_log_bridge_stairs",
                () -> new BridgeStairBlock(Blocks.STRIPPED_SPRUCE_LOG.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_SPRUCE_LOG).mapColor(MapColor.PODZOL)));

        BIRCH_LOG_BRIDGE_STAIRS = registerBlock("birch_log_bridge_stairs",
                () -> new BridgeStairBlock(Blocks.BIRCH_LOG.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.BIRCH_LOG).mapColor(MapColor.QUARTZ)));

        STRIPPED_BIRCH_LOG_BRIDGE_STAIRS = registerBlock("stripped_birch_log_bridge_stairs",
                () -> new BridgeStairBlock(Blocks.STRIPPED_BIRCH_LOG.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_BIRCH_LOG).mapColor(MapColor.SAND)));

        JUNGLE_LOG_BRIDGE_STAIRS = registerBlock("jungle_log_bridge_stairs",
                () -> new BridgeStairBlock(Blocks.JUNGLE_LOG.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_LOG).mapColor(MapColor.PODZOL)));

        STRIPPED_JUNGLE_LOG_BRIDGE_STAIRS = registerBlock("stripped_jungle_log_bridge_stairs",
                () -> new BridgeStairBlock(Blocks.STRIPPED_JUNGLE_LOG.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_JUNGLE_LOG).mapColor(MapColor.DIRT)));

        ACACIA_LOG_BRIDGE_STAIRS = registerBlock("acacia_log_bridge_stairs",
                () -> new BridgeStairBlock(Blocks.ACACIA_LOG.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_LOG).mapColor(MapColor.STONE)));

        STRIPPED_ACACIA_LOG_BRIDGE_STAIRS = registerBlock("stripped_acacia_log_bridge_stairs",
                () -> new BridgeStairBlock(Blocks.STRIPPED_ACACIA_LOG.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_ACACIA_LOG).mapColor(MapColor.COLOR_ORANGE)));

        DARK_OAK_LOG_BRIDGE_STAIRS = registerBlock("dark_oak_log_bridge_stairs",
                () -> new BridgeStairBlock(Blocks.DARK_OAK_LOG.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_OAK_LOG).mapColor(MapColor.COLOR_BROWN)));

        STRIPPED_DARK_OAK_LOG_BRIDGE_STAIRS = registerBlock("stripped_dark_oak_log_bridge_stairs",
                () -> new BridgeStairBlock(Blocks.STRIPPED_DARK_OAK_LOG.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_DARK_OAK_LOG).mapColor(MapColor.COLOR_BROWN)));

        MANGROVE_LOG_BRIDGE_STAIRS = registerBlock("mangrove_log_bridge_stairs",
                () -> new BridgeStairBlock(Blocks.MANGROVE_LOG.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.MANGROVE_LOG).mapColor(MapColor.PODZOL)));

        STRIPPED_MANGROVE_LOG_BRIDGE_STAIRS = registerBlock("stripped_mangrove_log_bridge_stairs",
                () -> new BridgeStairBlock(Blocks.STRIPPED_MANGROVE_LOG.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_MANGROVE_LOG).mapColor(MapColor.COLOR_RED)));

        CHERRY_LOG_BRIDGE_STAIRS = registerBlock("cherry_log_bridge_stairs",
                () -> new BridgeStairBlock(Blocks.CHERRY_LOG.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.CHERRY_LOG).mapColor(MapColor.TERRACOTTA_GRAY)));

        STRIPPED_CHERRY_LOG_BRIDGE_STAIRS = registerBlock("stripped_cherry_log_bridge_stairs",
                () -> new BridgeStairBlock(Blocks.STRIPPED_CHERRY_LOG.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_CHERRY_LOG).mapColor(MapColor.TERRACOTTA_PINK)));

        BAMBOO_BRIDGE_STAIRS = registerBlock("bamboo_bridge_stairs",
                () -> new BridgeStairBlock(Blocks.BAMBOO_BLOCK.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.BAMBOO_BLOCK).mapColor(MapColor.PLANT)));

        STRIPPED_BAMBOO_BRIDGE_STAIRS = registerBlock("stripped_bamboo_bridge_stairs",
                () -> new BridgeStairBlock(Blocks.STRIPPED_BAMBOO_BLOCK.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_BAMBOO_BLOCK).mapColor(MapColor.COLOR_YELLOW)));

        CRIMSON_STEM_BRIDGE_STAIRS = registerBlock("crimson_stem_bridge_stairs",
                () -> new BridgeStairBlock(Blocks.CRIMSON_STEM.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_STEM)));

        STRIPPED_CRIMSON_STEM_BRIDGE_STAIRS = registerBlock("stripped_crimson_stem_bridge_stairs",
                () -> new BridgeStairBlock(Blocks.STRIPPED_CRIMSON_STEM.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_CRIMSON_STEM)));

        WARPED_STEM_BRIDGE_STAIRS = registerBlock("warped_stem_bridge_stairs",
                () -> new BridgeStairBlock(Blocks.WARPED_STEM.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_STEM)));

        STRIPPED_WARPED_STEM_BRIDGE_STAIRS = registerBlock("stripped_warped_stem_bridge_stairs",
                () -> new BridgeStairBlock(Blocks.STRIPPED_WARPED_STEM.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_WARPED_STEM)));


        FUNGAL_STONE = registerBlock("fungal_stone",
                () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_RED)
                        .sound(SoundType.TUFF_BRICKS).instrument(NoteBlockInstrument.BASEDRUM)
                        .strength(1.5F, 6.0F).requiresCorrectToolForDrops()));

        FUNGAL_STONE_BUTTON = registerBlock("fungal_stone_button", () -> button(FUNGAL_STONE.get(), BlockSetTypeRegistry.FUNGAL_STONE, 25));

        FUNGAL_STONE_PRESSURE_PLATE = registerBlock("fungal_stone_pressure_plate", () -> pressurePlate(FUNGAL_STONE.get(), BlockSetTypeRegistry.FUNGAL_STONE));

        FUNGAL_STONE_SLAB = registerBlock("fungal_stone_slab", () -> slab(FUNGAL_STONE.get()));

        FUNGAL_STONE_STAIRS = registerBlock("fungal_stone_stairs", () -> stair(FUNGAL_STONE.get()));

        FUNGAL_STONE_WALL = registerBlock("fungal_stone_wall", () -> wall(FUNGAL_STONE.get()));


        ROCKY_FUNGAL_STONE = registerBlock("rocky_fungal_stone",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(BlockRegistry.FUNGAL_STONE.get())
                        .strength(2.0F, 6.0F)));

        ROCKY_FUNGAL_STONE_SLAB = registerBlock("rocky_fungal_stone_slab", () -> slab(ROCKY_FUNGAL_STONE.get()));

        ROCKY_FUNGAL_STONE_STAIRS = registerBlock("rocky_fungal_stone_stairs", () -> stair(ROCKY_FUNGAL_STONE.get()));

        ROCKY_FUNGAL_STONE_WALL = registerBlock("rocky_fungal_stone_wall", () -> wall(ROCKY_FUNGAL_STONE.get()));


        FUNGAL_COBBLESTONE = registerBlock("fungal_cobblestone",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(BlockRegistry.FUNGAL_STONE.get())
                        .strength(2.0F, 6.0F)));

        FUNGAL_COBBLESTONE_SLAB = registerBlock("fungal_cobblestone_slab", () -> slab(FUNGAL_COBBLESTONE.get()));

        FUNGAL_COBBLESTONE_STAIRS = registerBlock("fungal_cobblestone_stairs", () -> stair(FUNGAL_COBBLESTONE.get()));

        FUNGAL_COBBLESTONE_WALL = registerBlock("fungal_cobblestone_wall", () -> wall(FUNGAL_COBBLESTONE.get()));


        FUNGAL_BRICKS = registerBlock("fungal_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(FUNGAL_STONE.get())));

        FUNGAL_BRICK_SLAB = registerBlock("fungal_brick_slab", () -> slab(FUNGAL_BRICKS.get()));

        FUNGAL_BRICK_STAIRS = registerBlock("fungal_brick_stairs", () -> stair(FUNGAL_BRICKS.get()));

        FUNGAL_BRICK_WALL = registerBlock("fungal_brick_wall", () -> wall(FUNGAL_BRICKS.get()));

        FUNGAL_BRICK_PEDESTAL = registerBlock("fungal_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(FUNGAL_BRICKS.get())));

        CHISELED_FUNGAL_BRICKS = registerBlock("chiseled_fungal_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(FUNGAL_BRICKS.get())));

        CRACKED_FUNGAL_BRICKS = registerBlock("cracked_fungal_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(FUNGAL_BRICKS.get())));

        STORAGE_FUNGAL_BRICKS = registerBlock("storage_fungal_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(FUNGAL_BRICKS.get())));


        POLISHED_FUNGAL_STONE = registerBlock("polished_fungal_stone",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(FUNGAL_STONE.get())));

        POLISHED_FUNGAL_STONE_STAIRS = registerBlock("polished_fungal_stone_stairs", () -> stair(POLISHED_FUNGAL_STONE.get()));

        POLISHED_FUNGAL_STONE_SLAB = registerBlock("polished_fungal_stone_slab", () -> slab(POLISHED_FUNGAL_STONE.get()));

        POLISHED_FUNGAL_STONE_WALL = registerBlock("polished_fungal_stone_wall", () -> wall(POLISHED_FUNGAL_STONE.get()));


        HARD_FUNGAL_BLOCK = registerBlock("hard_fungal_block",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(POLISHED_FUNGAL_STONE.get()).strength(2.5F, 8.0F)));

        HARD_FUNGAL_STAIRS = registerBlock("hard_fungal_stairs", () -> stair(HARD_FUNGAL_BLOCK.get()));

        HARD_FUNGAL_SLAB = registerBlock("hard_fungal_slab", () -> slab(HARD_FUNGAL_BLOCK.get()));

        HARD_FUNGAL_WALL = registerBlock("hard_fungal_wall", () -> wall(HARD_FUNGAL_BLOCK.get()));


        FUNGAL_QUESTION_BLOCK = registerBlock("fungal_question_block",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_FUNGAL_STONE.get())
                        .mapColor(state -> state.getValue(QuestionBlock.EMPTY) ? MapColor.TERRACOTTA_RED : MapColor.GOLD)));

        INVISIBLE_FUNGAL_QUESTION_BLOCK = registerBlock("invisible_fungal_question_block",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_FUNGAL_STONE.get())
                        .mapColor(state -> state.getValue(QuestionBlock.EMPTY) ? MapColor.TERRACOTTA_RED
                                : state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.GOLD)));

        FUNGAL_QUESTION_PANEL = registerBlock("fungal_question_panel",
                () -> new QuestionPanelBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_FUNGAL_STONE.get())
                        .mapColor(state -> state.getValue(QuestionPanelBlock.POWERED) ? MapColor.TERRACOTTA_RED : MapColor.GOLD)
                        .noCollission().forceSolidOn().strength(0.5F)));


        POLISHED_FUNGAL_BRICKS = registerBlock("polished_fungal_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(POLISHED_FUNGAL_STONE.get())));

        POLISHED_FUNGAL_BRICK_SLAB = registerBlock("polished_fungal_brick_slab", () -> slab(POLISHED_FUNGAL_BRICKS.get()));

        POLISHED_FUNGAL_BRICK_STAIRS = registerBlock("polished_fungal_brick_stairs", () -> stair(POLISHED_FUNGAL_BRICKS.get()));

        POLISHED_FUNGAL_BRICK_WALL = registerBlock("polished_fungal_brick_wall", () -> wall(POLISHED_FUNGAL_BRICKS.get()));

        POLISHED_FUNGAL_BRICK_PEDESTAL = registerBlock("polished_fungal_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_FUNGAL_BRICKS.get())));

        CHISELED_POLISHED_FUNGAL_BRICKS = registerBlock("chiseled_polished_fungal_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(POLISHED_FUNGAL_BRICKS.get())));

        CRACKED_POLISHED_FUNGAL_BRICKS = registerBlock("cracked_polished_fungal_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(POLISHED_FUNGAL_BRICKS.get())));


        STORAGE_POLISHED_FUNGAL_BRICKS = registerBlock("storage_polished_fungal_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_FUNGAL_BRICKS.get())));


        DEEP_FUNGAL_STONE = registerBlock("deep_fungal_stone",
                () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN)
                        .sound(SoundType.TUFF_BRICKS).instrument(NoteBlockInstrument.BASEDRUM)
                        .strength(3.0F, 6.5F).requiresCorrectToolForDrops()));

        DEEP_FUNGAL_STONE_BUTTON = registerBlock("deep_fungal_stone_button", () -> button(DEEP_FUNGAL_STONE.get(), BlockSetTypeRegistry.FUNGAL_STONE, 35));

        DEEP_FUNGAL_STONE_PRESSURE_PLATE = registerBlock("deep_fungal_stone_pressure_plate", () -> pressurePlate(DEEP_FUNGAL_STONE.get(), BlockSetTypeRegistry.FUNGAL_STONE));

        DEEP_FUNGAL_STONE_SLAB = registerBlock("deep_fungal_stone_slab", () -> slab(DEEP_FUNGAL_STONE.get()));

        DEEP_FUNGAL_STONE_STAIRS = registerBlock("deep_fungal_stone_stairs", () -> stair(DEEP_FUNGAL_STONE.get()));

        DEEP_FUNGAL_STONE_WALL = registerBlock("deep_fungal_stone_wall", () -> wall(DEEP_FUNGAL_STONE.get()));


        ROCKY_DEEP_FUNGAL_STONE = registerBlock("rocky_deep_fungal_stone",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(DEEP_FUNGAL_STONE.get())
                        .strength(3.5F, 6.0F)));

        ROCKY_DEEP_FUNGAL_STONE_SLAB = registerBlock("rocky_deep_fungal_stone_slab", () -> slab(ROCKY_DEEP_FUNGAL_STONE.get()));

        ROCKY_DEEP_FUNGAL_STONE_STAIRS = registerBlock("rocky_deep_fungal_stone_stairs", () -> stair(ROCKY_DEEP_FUNGAL_STONE.get()));

        ROCKY_DEEP_FUNGAL_STONE_WALL = registerBlock("rocky_deep_fungal_stone_wall", () -> wall(ROCKY_DEEP_FUNGAL_STONE.get()));


        DEEP_FUNGAL_COBBLESTONE = registerBlock("deep_fungal_cobblestone",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(DEEP_FUNGAL_STONE.get())
                        .strength(3.5F, 6.0F)));

        DEEP_FUNGAL_COBBLESTONE_SLAB = registerBlock("deep_fungal_cobblestone_slab", () -> slab(DEEP_FUNGAL_COBBLESTONE.get()));

        DEEP_FUNGAL_COBBLESTONE_STAIRS = registerBlock("deep_fungal_cobblestone_stairs", () -> stair(DEEP_FUNGAL_COBBLESTONE.get()));

        DEEP_FUNGAL_COBBLESTONE_WALL = registerBlock("deep_fungal_cobblestone_wall", () -> wall(DEEP_FUNGAL_COBBLESTONE.get()));


        DEEP_FUNGAL_BRICKS = registerBlock("deep_fungal_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(DEEP_FUNGAL_STONE.get())));

        DEEP_FUNGAL_BRICK_STAIRS = registerBlock("deep_fungal_brick_stairs", () -> stair(DEEP_FUNGAL_BRICKS.get()));

        DEEP_FUNGAL_BRICK_SLAB = registerBlock("deep_fungal_brick_slab", () -> slab(DEEP_FUNGAL_BRICKS.get()));

        DEEP_FUNGAL_BRICK_WALL = registerBlock("deep_fungal_brick_wall", () -> wall(DEEP_FUNGAL_BRICKS.get()));

        DEEP_FUNGAL_BRICK_PEDESTAL = registerBlock("deep_fungal_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(DEEP_FUNGAL_BRICKS.get())));

        CHISELED_DEEP_FUNGAL_BRICKS = registerBlock("chiseled_deep_fungal_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(DEEP_FUNGAL_BRICKS.get())));

        CRACKED_DEEP_FUNGAL_BRICKS = registerBlock("cracked_deep_fungal_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(DEEP_FUNGAL_BRICKS.get())));

        STORAGE_DEEP_FUNGAL_BRICKS = registerBlock("storage_deep_fungal_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(DEEP_FUNGAL_BRICKS.get())));


        POLISHED_DEEP_FUNGAL_STONE = registerBlock("polished_deep_fungal_stone",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(DEEP_FUNGAL_STONE.get())
                        .strength(2.5F, 6.0F)));

        POLISHED_DEEP_FUNGAL_STONE_STAIRS = registerBlock("polished_deep_fungal_stone_stairs", () -> stair(POLISHED_DEEP_FUNGAL_STONE.get()));

        POLISHED_DEEP_FUNGAL_STONE_SLAB = registerBlock("polished_deep_fungal_stone_slab", () -> slab(POLISHED_DEEP_FUNGAL_STONE.get()));

        POLISHED_DEEP_FUNGAL_STONE_WALL = registerBlock("polished_deep_fungal_stone_wall", () -> wall(POLISHED_DEEP_FUNGAL_STONE.get()));


        HARD_DEEP_FUNGAL_BLOCK = registerBlock("hard_deep_fungal_block",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(POLISHED_DEEP_FUNGAL_STONE.get())
                        .strength(4.0F, 8.0F)));

        HARD_DEEP_FUNGAL_STAIRS = registerBlock("hard_deep_fungal_stairs", () -> stair(HARD_DEEP_FUNGAL_BLOCK.get()));

        HARD_DEEP_FUNGAL_SLAB = registerBlock("hard_deep_fungal_slab", () -> slab(HARD_DEEP_FUNGAL_BLOCK.get()));

        HARD_DEEP_FUNGAL_WALL = registerBlock("hard_deep_fungal_wall", () -> wall(HARD_DEEP_FUNGAL_BLOCK.get()));


        DEEP_FUNGAL_QUESTION_BLOCK = registerBlock("deep_fungal_question_block",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_DEEP_FUNGAL_STONE.get())
                        .mapColor(state -> state.getValue(QuestionBlock.EMPTY) ? MapColor.COLOR_CYAN : MapColor.COLOR_GREEN)));

        INVISIBLE_DEEP_FUNGAL_QUESTION_BLOCK = registerBlock("invisible_deep_fungal_question_block",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_DEEP_FUNGAL_STONE.get())
                        .mapColor(state -> state.getValue(QuestionBlock.EMPTY) ? MapColor.COLOR_CYAN
                                : state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.COLOR_GREEN)));

        DEEP_FUNGAL_QUESTION_PANEL = registerBlock("deep_fungal_question_panel",
                () -> new QuestionPanelBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_DEEP_FUNGAL_STONE.get())
                        .mapColor(state -> state.getValue(QuestionPanelBlock.POWERED) ? MapColor.COLOR_CYAN : MapColor.COLOR_GREEN)
                        .noCollission().forceSolidOn().strength(0.5F)));


        POLISHED_DEEP_FUNGAL_BRICKS = registerBlock("polished_deep_fungal_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(POLISHED_DEEP_FUNGAL_STONE.get())));

        POLISHED_DEEP_FUNGAL_BRICK_STAIRS = registerBlock("polished_deep_fungal_brick_stairs", () -> stair(POLISHED_DEEP_FUNGAL_BRICKS.get()));

        POLISHED_DEEP_FUNGAL_BRICK_SLAB = registerBlock("polished_deep_fungal_brick_slab", () -> slab(POLISHED_DEEP_FUNGAL_BRICKS.get()));

        POLISHED_DEEP_FUNGAL_BRICK_WALL = registerBlock("polished_deep_fungal_brick_wall", () -> wall(POLISHED_DEEP_FUNGAL_BRICKS.get()));

        POLISHED_DEEP_FUNGAL_BRICK_PEDESTAL = registerBlock("polished_deep_fungal_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_DEEP_FUNGAL_BRICKS.get())));

        CHISELED_POLISHED_DEEP_FUNGAL_BRICKS = registerBlock("chiseled_polished_deep_fungal_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(POLISHED_DEEP_FUNGAL_BRICKS.get())));

        CRACKED_POLISHED_DEEP_FUNGAL_BRICKS = registerBlock("cracked_polished_deep_fungal_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(POLISHED_DEEP_FUNGAL_BRICKS.get())));

        STORAGE_POLISHED_DEEP_FUNGAL_BRICKS = registerBlock("storage_polished_deep_fungal_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_DEEP_FUNGAL_BRICKS.get())));


        AMETHYST_BUTTON = registerBlock("amethyst_button", () -> button(Blocks.AMETHYST_BLOCK, BlockSetTypeRegistry.AMETHYST, 40));

        AMETHYST_PRESSURE_PLATE = registerBlock("amethyst_pressure_plate", () -> pressurePlate(Blocks.AMETHYST_BLOCK, BlockSetTypeRegistry.AMETHYST));

        AMETHYST_SLAB = registerBlock("amethyst_slab", () -> slab(Blocks.AMETHYST_BLOCK));

        AMETHYST_STAIRS = registerBlock("amethyst_stairs", () -> stair(Blocks.AMETHYST_BLOCK));

        AMETHYST_WALL = registerBlock("amethyst_wall", () -> wall(Blocks.AMETHYST_BLOCK));

        POLISHED_AMETHYST = registerBlock("polished_amethyst",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).strength(1.25F)));

        POLISHED_AMETHYST_STAIRS = registerBlock("polished_amethyst_stairs", () -> stair(POLISHED_AMETHYST.get()));

        POLISHED_AMETHYST_SLAB = registerBlock("polished_amethyst_slab", () -> slab(POLISHED_AMETHYST.get()));

        POLISHED_AMETHYST_WALL = registerBlock("polished_amethyst_wall", () -> wall(POLISHED_AMETHYST.get()));

        AMETHYST_BRICKS = registerBlock("amethyst_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(POLISHED_AMETHYST.get())));

        CHISELED_AMETHYST_BRICKS = registerBlock("chiseled_amethyst_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(AMETHYST_BRICKS.get())));

        CRACKED_AMETHYST_BRICKS = registerBlock("cracked_amethyst_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(AMETHYST_BRICKS.get())));

        AMETHYST_BRICK_SLAB = registerBlock("amethyst_brick_slab", () -> slab(AMETHYST_BRICKS.get()));

        AMETHYST_BRICK_STAIRS = registerBlock("amethyst_brick_stairs", () -> stair(AMETHYST_BRICKS.get()));

        AMETHYST_BRICK_WALL = registerBlock("amethyst_brick_wall", () -> wall(AMETHYST_BRICKS.get()));

        AMETHYST_BRICK_PEDESTAL = registerBlock("amethyst_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(AMETHYST_BRICKS.get())));


        AMETHYST_QUESTION_BLOCK = registerBlock("amethyst_question_block",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_AMETHYST.get())));

        INVISIBLE_AMETHYST_QUESTION_BLOCK = registerBlock("invisible_amethyst_question_block",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_AMETHYST.get())
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.COLOR_PURPLE)));

        STORAGE_AMETHYST_BRICKS = registerBlock("storage_amethyst_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_AMETHYST.get())));


        CALCITE_BUTTON = registerBlock("calcite_button", () -> button(Blocks.CALCITE, BlockSetType.STONE, 10));

        CALCITE_PRESSURE_PLATE = registerBlock("calcite_pressure_plate", () -> pressurePlate(Blocks.CALCITE, BlockSetType.STONE));

        CALCITE_SLAB = registerBlock("calcite_slab", () -> slab(Blocks.CALCITE));

        CALCITE_STAIRS = registerBlock("calcite_stairs", () -> stair(Blocks.CALCITE));

        CALCITE_WALL = registerBlock("calcite_wall", () -> wall(Blocks.CALCITE));

        POLISHED_WHITE_CALCITE_SLAB = registerBlock("polished_white_calcite_slab", () -> slab(Blocks.CALCITE));

        POLISHED_WHITE_CALCITE_STAIRS = registerBlock("polished_white_calcite_stairs", () -> stair(Blocks.CALCITE));

        POLISHED_WHITE_CALCITE_WALL = registerBlock("polished_white_calcite_wall", () -> wall(Blocks.CALCITE));

        WHITE_CALCITE_BRICK_SLAB = registerBlock("white_calcite_brick_slab", () -> slab(Blocks.CALCITE));

        WHITE_CALCITE_BRICK_STAIRS = registerBlock("white_calcite_brick_stairs", () -> stair(Blocks.CALCITE));

        WHITE_CALCITE_BRICK_WALL = registerBlock("white_calcite_brick_wall", () -> wall(Blocks.CALCITE));


        CALCITE_QUESTION_BLOCK = registerBlock("calcite_question_block",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE)));

        INVISIBLE_CALCITE_QUESTION_BLOCK = registerBlock("invisible_calcite_question_block",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE)));


        QUICKSAND = registerNoItemBlock("quicksand",
                () -> new QuicksandBlock(new ColorRGBA(14406560), BlockBehaviour.Properties.ofFullCopy(Blocks.SAND)
                        .dynamicShape().isRedstoneConductor(BlockRegistry::never).isViewBlocking(BlockRegistry::never)));

        SANDSTONE_BRICKS = registerBlock("sandstone_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE)));

        SANDSTONE_BRICK_SLAB = registerBlock("sandstone_brick_slab", () -> slab(SANDSTONE_BRICKS.get()));

        SANDSTONE_BRICK_STAIRS = registerBlock("sandstone_brick_stairs", () -> stair(SANDSTONE_BRICKS.get()));

        SANDSTONE_BRICK_WALL = registerBlock("sandstone_brick_wall", () -> wall(SANDSTONE_BRICKS.get()));

        SANDSTONE_BRICK_PEDESTAL = registerBlock("sandstone_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(SANDSTONE_BRICKS.get())));

        CHISELED_SANDSTONE_BRICKS = registerBlock("chiseled_sandstone_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(SANDSTONE_BRICKS.get())));

        CRACKED_SANDSTONE_BRICKS = registerBlock("cracked_sandstone_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(SANDSTONE_BRICKS.get())));


        SANDSTONE_QUESTION_BLOCK = registerBlock("sandstone_question_block",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHISELED_SANDSTONE)));

        INVISIBLE_SANDSTONE_QUESTION_BLOCK = registerBlock("invisible_sandstone_question_block",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHISELED_SANDSTONE)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.SAND)));

        STORAGE_SANDSTONE_BRICKS = registerBlock("storage_sandstone_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(SANDSTONE_BRICKS.get())));


        RED_QUICKSAND = registerNoItemBlock("red_quicksand",
                () -> new QuicksandBlock(new ColorRGBA(11098145), BlockBehaviour.Properties.ofFullCopy(Blocks.RED_SAND)
                        .dynamicShape().isRedstoneConductor(BlockRegistry::never).isViewBlocking(BlockRegistry::never)));

        RED_SANDSTONE_BRICKS = registerBlock("red_sandstone_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.RED_SANDSTONE)));

        RED_SANDSTONE_BRICK_SLAB = registerBlock("red_sandstone_brick_slab", () -> slab(RED_SANDSTONE_BRICKS.get()));

        RED_SANDSTONE_BRICK_STAIRS = registerBlock("red_sandstone_brick_stairs", () -> stair(RED_SANDSTONE_BRICKS.get()));

        RED_SANDSTONE_BRICK_WALL = registerBlock("red_sandstone_brick_wall", () -> wall(RED_SANDSTONE_BRICKS.get()));

        RED_SANDSTONE_BRICK_PEDESTAL = registerBlock("red_sandstone_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(RED_SANDSTONE_BRICKS.get())));

        CHISELED_RED_SANDSTONE_BRICKS = registerBlock("chiseled_red_sandstone_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(RED_SANDSTONE_BRICKS.get())));

        CRACKED_RED_SANDSTONE_BRICKS = registerBlock("cracked_red_sandstone_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(RED_SANDSTONE_BRICKS.get())));


        RED_SANDSTONE_QUESTION_BLOCK = registerBlock("red_sandstone_question_block",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHISELED_RED_SANDSTONE)));

        INVISIBLE_RED_SANDSTONE_QUESTION_BLOCK = registerBlock("invisible_red_sandstone_question_block",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHISELED_RED_SANDSTONE)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.COLOR_ORANGE)));

        STORAGE_RED_SANDSTONE_BRICKS = registerBlock("storage_red_sandstone_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(RED_SANDSTONE_BRICKS.get())));


        STONE_QUESTION_BRICKS = registerBlock("stone_question_bricks",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));

        INVISIBLE_STONE_QUESTION_BRICKS = registerBlock("invisible_stone_question_bricks",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.STONE)));

        STORAGE_STONE_BRICKS = registerBlock("storage_stone_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));

        SMASHABLE_STONE_BRICKS = registerBlock("smashable_stone_bricks",
                () -> new SmashableBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));

        STONE_BRICK_PEDESTAL = registerBlock("stone_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));


        MOSSY_STONE_QUESTION_BRICKS = registerBlock("mossy_stone_question_bricks",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MOSSY_STONE_BRICKS)));

        INVISIBLE_MOSSY_STONE_QUESTION_BRICKS = registerBlock("invisible_mossy_stone_question_bricks",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MOSSY_STONE_BRICKS)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.STONE)));

        STORAGE_MOSSY_STONE_BRICKS = registerBlock("storage_mossy_stone_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MOSSY_STONE_BRICKS)));

        SMASHABLE_MOSSY_STONE_BRICKS = registerBlock("smashable_mossy_stone_bricks",
                () -> new SmashableBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MOSSY_STONE_BRICKS)));

        MOSSY_STONE_BRICK_PEDESTAL = registerBlock("mossy_stone_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MOSSY_STONE_BRICKS)));


        TUFF_QUESTION_BRICKS = registerBlock("tuff_question_bricks",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TUFF_BRICKS)));

        INVISIBLE_TUFF_QUESTION_BRICKS = registerBlock("invisible_tuff_question_bricks",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TUFF_BRICKS)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.TERRACOTTA_GRAY)));

        STORAGE_TUFF_BRICKS = registerBlock("storage_tuff_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TUFF_BRICKS)));

        SMASHABLE_TUFF_BRICKS = registerBlock("smashable_tuff_bricks",
                () -> new SmashableBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TUFF_BRICKS)));

        TUFF_BRICK_PEDESTAL = registerBlock("tuff_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TUFF_BRICKS)));


        DEEPSLATE_QUESTION_BRICKS = registerBlock("deepslate_question_bricks",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS)));

        INVISIBLE_DEEPSLATE_QUESTION_BRICKS = registerBlock("invisible_deepslate_question_bricks",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.DEEPSLATE)));

        STORAGE_DEEPSLATE_BRICKS = registerBlock("storage_deepslate_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS)));

        SMASHABLE_DEEPSLATE_BRICKS = registerBlock("smashable_deepslate_bricks",
                () -> new SmashableBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS)));

        DEEPSLATE_BRICK_PEDESTAL = registerBlock("deepslate_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS)));


        DEEPSLATE_QUESTION_TILES = registerBlock("deepslate_question_tiles",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_TILES)));

        INVISIBLE_DEEPSLATE_QUESTION_TILES = registerBlock("invisible_deepslate_question_tiles",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_TILES)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.DEEPSLATE)));

        STORAGE_DEEPSLATE_TILES = registerBlock("storage_deepslate_tiles",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_TILES)));

        SMASHABLE_DEEPSLATE_TILES = registerBlock("smashable_deepslate_tiles",
                () -> new SmashableBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_TILES)));

        DEEPSLATE_TILE_PEDESTAL = registerBlock("deepslate_tile_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_TILES)));


        QUESTION_BRICKS = registerBlock("question_bricks",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS)));

        INVISIBLE_QUESTION_BRICKS = registerBlock("invisible_question_bricks",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.COLOR_RED)));

        STORAGE_BRICKS = registerBlock("storage_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS)));

        SMASHABLE_BRICKS = registerBlock("smashable_bricks",
                () -> new SmashableBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS)));

        BRICK_PEDESTAL = registerBlock("brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS)));


        MUD_QUESTION_BRICKS = registerBlock("mud_question_bricks",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD_BRICKS)));

        INVISIBLE_MUD_QUESTION_BRICKS = registerBlock("invisible_mud_question_bricks",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD_BRICKS)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.TERRACOTTA_LIGHT_GRAY)));

        STORAGE_MUD_BRICKS = registerBlock("storage_mud_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD_BRICKS)));

        SMASHABLE_MUD_BRICKS = registerBlock("smashable_mud_bricks",
                () -> new SmashableBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD_BRICKS)));

        MUD_BRICK_PEDESTAL = registerBlock("mud_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD_BRICKS)));


        PRISMARINE_QUESTION_BRICKS = registerBlock("prismarine_question_bricks",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PRISMARINE_BRICKS)));

        INVISIBLE_PRISMARINE_QUESTION_BRICKS = registerBlock("invisible_prismarine_question_bricks",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PRISMARINE_BRICKS)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.DIAMOND)));

        STORAGE_PRISMARINE_BRICKS = registerBlock("storage_prismarine_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PRISMARINE_BRICKS)));

        SMASHABLE_PRISMARINE_BRICKS = registerBlock("smashable_prismarine_bricks",
                () -> new SmashableBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PRISMARINE_BRICKS)));

        PRISMARINE_BRICK_PEDESTAL = registerBlock("prismarine_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PRISMARINE_BRICKS)));


        DARK_PRISMARINE_QUESTION_BLOCK = registerBlock("dark_prismarine_question_block",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_PRISMARINE)));

        INVISIBLE_DARK_PRISMARINE_QUESTION_BLOCK = registerBlock("invisible_dark_prismarine_question_block",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_PRISMARINE)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.DIAMOND)));

        STORAGE_DARK_PRISMARINE = registerBlock("storage_dark_prismarine",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_PRISMARINE)));

        SMASHABLE_DARK_PRISMARINE = registerBlock("smashable_dark_prismarine",
                () -> new SmashableBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_PRISMARINE)));

        DARK_PRISMARINE_PEDESTAL = registerBlock("dark_prismarine_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_PRISMARINE)));


        NETHER_QUESTION_BRICKS = registerBlock("nether_question_bricks",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_BRICKS)));

        INVISIBLE_NETHER_QUESTION_BRICKS = registerBlock("invisible_nether_question_bricks",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_BRICKS)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.NETHER)));

        STORAGE_NETHER_BRICKS = registerBlock("storage_nether_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_BRICKS)));

        SMASHABLE_NETHER_BRICKS = registerBlock("smashable_nether_bricks",
                () -> new SmashableBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_BRICKS)));

        NETHER_BRICK_PEDESTAL = registerBlock("nether_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_BRICKS)));


        RED_NETHER_QUESTION_BRICKS = registerBlock("red_nether_question_bricks",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.RED_NETHER_BRICKS)));

        INVISIBLE_RED_NETHER_QUESTION_BRICKS = registerBlock("invisible_red_nether_question_bricks",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.RED_NETHER_BRICKS)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.NETHER)));

        STORAGE_RED_NETHER_BRICKS = registerBlock("storage_red_nether_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.RED_NETHER_BRICKS)));

        SMASHABLE_RED_NETHER_BRICKS = registerBlock("smashable_red_nether_bricks",
                () -> new SmashableBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.RED_NETHER_BRICKS)));

        RED_NETHER_BRICK_PEDESTAL = registerBlock("red_nether_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.RED_NETHER_BRICKS)));


        BLACKSTONE_QUESTION_BRICKS = registerBlock("blackstone_question_bricks",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_BLACKSTONE_BRICKS)));

        INVISIBLE_BLACKSTONE_QUESTION_BRICKS = registerBlock("invisible_blackstone_question_bricks",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_BLACKSTONE_BRICKS)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.COLOR_BLACK)));

        STORAGE_BLACKSTONE_BRICKS = registerBlock("storage_blackstone_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_BLACKSTONE_BRICKS)));

        SMASHABLE_BLACKSTONE_BRICKS = registerBlock("smashable_blackstone_bricks",
                () -> new SmashableBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_BLACKSTONE_BRICKS)));

        BLACKSTONE_BRICK_PEDESTAL = registerBlock("blackstone_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_BLACKSTONE_BRICKS)));


        END_STONE_QUESTION_BRICKS = registerBlock("end_stone_question_bricks",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE_BRICKS)));

        INVISIBLE_END_STONE_QUESTION_BRICKS = registerBlock("invisible_end_stone_question_bricks",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE_BRICKS)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.SAND)));

        STORAGE_END_STONE_BRICKS = registerBlock("storage_end_stone_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE_BRICKS)));

        SMASHABLE_END_STONE_BRICKS = registerBlock("smashable_end_stone_bricks",
                () -> new SmashableBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE_BRICKS)));

        END_STONE_BRICK_PEDESTAL = registerBlock("end_stone_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE_BRICKS)));


        PURPUR_QUESTION_BLOCK = registerBlock("purpur_question_block",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PURPUR_BLOCK)));

        INVISIBLE_PURPUR_QUESTION_BLOCK = registerBlock("invisible_purpur_question_block",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PURPUR_BLOCK)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.COLOR_MAGENTA)));

        STORAGE_PURPUR_BLOCK = registerBlock("storage_purpur_block",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PURPUR_BLOCK)));

        SMASHABLE_PURPUR_BLOCK = registerBlock("smashable_purpur_block",
                () -> new SmashableBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PURPUR_BLOCK)));

        PURPUR_BLOCK_PEDESTAL = registerBlock("purpur_block_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PURPUR_BLOCK)));


        QUARTZ_QUESTION_BRICKS = registerBlock("quartz_question_bricks",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.QUARTZ_BRICKS)));

        INVISIBLE_QUARTZ_QUESTION_BRICKS = registerBlock("invisible_quartz_question_bricks",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.QUARTZ_BRICKS)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.QUARTZ)));

        STORAGE_QUARTZ_BRICKS = registerBlock("storage_quartz_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.QUARTZ_BRICKS)));

        SMASHABLE_QUARTZ_BRICKS = registerBlock("smashable_quartz_bricks",
                () -> new SmashableBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.QUARTZ_BRICKS)));

        QUARTZ_BRICK_PEDESTAL = registerBlock("quartz_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.QUARTZ_BRICKS)));


        COPPER_QUESTION_BLOCK = registerBlock("copper_question_block",
                () -> new WeatheringCopperQuestionBlock(WeatheringCopper.WeatherState.UNAFFECTED, BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)));

        INVISIBLE_COPPER_QUESTION_BLOCK = registerBlock("invisible_copper_question_block",
                () -> new WeatheringCopperInvisibleQuestionBlock(WeatheringCopper.WeatherState.UNAFFECTED, BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.COLOR_ORANGE)));

        STORAGE_CUT_COPPER = registerBlock("storage_cut_copper",
                () -> new WeatheringCopperStorageBrickBlock(WeatheringCopper.WeatherState.UNAFFECTED, BlockBehaviour.Properties.ofFullCopy(Blocks.CUT_COPPER)));

        SMASHABLE_CUT_COPPER = registerBlock("smashable_cut_copper",
                () -> new WeatheringCopperFullBlock(WeatheringCopper.WeatherState.UNAFFECTED, BlockBehaviour.Properties.ofFullCopy(Blocks.CUT_COPPER)));

        CUT_COPPER_PEDESTAL = registerBlock("cut_copper_pedestal",
                () -> new WeatheringCopperPedestalBlock(WeatheringCopper.WeatherState.UNAFFECTED, BlockBehaviour.Properties.ofFullCopy(Blocks.CUT_COPPER)));


        EXPOSED_COPPER_QUESTION_BLOCK = registerBlock("exposed_copper_question_block",
                () -> new WeatheringCopperQuestionBlock(WeatheringCopper.WeatherState.EXPOSED, BlockBehaviour.Properties.ofFullCopy(Blocks.EXPOSED_COPPER)));

        INVISIBLE_EXPOSED_COPPER_QUESTION_BLOCK = registerBlock("invisible_exposed_copper_question_block",
                () -> new WeatheringCopperInvisibleQuestionBlock(WeatheringCopper.WeatherState.EXPOSED, BlockBehaviour.Properties.ofFullCopy(Blocks.EXPOSED_COPPER)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.TERRACOTTA_LIGHT_GRAY)));

        STORAGE_EXPOSED_CUT_COPPER = registerBlock("storage_exposed_cut_copper",
                () -> new WeatheringCopperStorageBrickBlock(WeatheringCopper.WeatherState.EXPOSED, BlockBehaviour.Properties.ofFullCopy(Blocks.EXPOSED_COPPER)));

        SMASHABLE_EXPOSED_CUT_COPPER = registerBlock("smashable_exposed_cut_copper",
                () -> new WeatheringCopperFullBlock(WeatheringCopper.WeatherState.EXPOSED, BlockBehaviour.Properties.ofFullCopy(Blocks.EXPOSED_COPPER)));

        EXPOSED_CUT_COPPER_PEDESTAL = registerBlock("exposed_cut_copper_pedestal",
                () -> new WeatheringCopperPedestalBlock(WeatheringCopper.WeatherState.EXPOSED, BlockBehaviour.Properties.ofFullCopy(Blocks.EXPOSED_COPPER)));


        WEATHERED_COPPER_QUESTION_BLOCK = registerBlock("weathered_copper_question_block",
                () -> new WeatheringCopperQuestionBlock(WeatheringCopper.WeatherState.WEATHERED, BlockBehaviour.Properties.ofFullCopy(Blocks.WEATHERED_COPPER)));

        INVISIBLE_WEATHERED_COPPER_QUESTION_BLOCK = registerBlock("invisible_weathered_copper_question_block",
                () -> new WeatheringCopperInvisibleQuestionBlock(WeatheringCopper.WeatherState.WEATHERED, BlockBehaviour.Properties.ofFullCopy(Blocks.WEATHERED_COPPER)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.WARPED_STEM)));

        STORAGE_WEATHERED_CUT_COPPER = registerBlock("storage_weathered_cut_copper",
                () -> new WeatheringCopperStorageBrickBlock(WeatheringCopper.WeatherState.WEATHERED, BlockBehaviour.Properties.ofFullCopy(Blocks.WEATHERED_COPPER)));

        SMASHABLE_WEATHERED_CUT_COPPER = registerBlock("smashable_weathered_cut_copper",
                () -> new WeatheringCopperFullBlock(WeatheringCopper.WeatherState.WEATHERED, BlockBehaviour.Properties.ofFullCopy(Blocks.WEATHERED_COPPER)));

        WEATHERED_CUT_COPPER_PEDESTAL = registerBlock("weathered_cut_copper_pedestal",
                () -> new WeatheringCopperPedestalBlock(WeatheringCopper.WeatherState.WEATHERED, BlockBehaviour.Properties.ofFullCopy(Blocks.WEATHERED_COPPER)));


        OXIDIZED_COPPER_QUESTION_BLOCK = registerBlock("oxidized_copper_question_block",
                () -> new WeatheringCopperQuestionBlock(WeatheringCopper.WeatherState.OXIDIZED, BlockBehaviour.Properties.ofFullCopy(Blocks.OXIDIZED_COPPER)));

        INVISIBLE_OXIDIZED_COPPER_QUESTION_BLOCK = registerBlock("invisible_oxidized_copper_question_block",
                () -> new WeatheringCopperInvisibleQuestionBlock(WeatheringCopper.WeatherState.OXIDIZED, BlockBehaviour.Properties.ofFullCopy(Blocks.OXIDIZED_COPPER)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.WARPED_NYLIUM)));

        STORAGE_OXIDIZED_CUT_COPPER = registerBlock("storage_oxidized_cut_copper",
                () -> new WeatheringCopperStorageBrickBlock(WeatheringCopper.WeatherState.OXIDIZED, BlockBehaviour.Properties.ofFullCopy(Blocks.OXIDIZED_COPPER)));

        SMASHABLE_OXIDIZED_CUT_COPPER = registerBlock("smashable_oxidized_cut_copper",
                () -> new WeatheringCopperFullBlock(WeatheringCopper.WeatherState.OXIDIZED, BlockBehaviour.Properties.ofFullCopy(Blocks.OXIDIZED_COPPER)));

        OXIDIZED_CUT_COPPER_PEDESTAL = registerBlock("oxidized_cut_copper_pedestal",
                () -> new WeatheringCopperPedestalBlock(WeatheringCopper.WeatherState.OXIDIZED, BlockBehaviour.Properties.ofFullCopy(Blocks.OXIDIZED_COPPER)));


        WAXED_COPPER_QUESTION_BLOCK = registerBlock("waxed_copper_question_block",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_COPPER_BLOCK)));

        INVISIBLE_WAXED_COPPER_QUESTION_BLOCK = registerBlock("invisible_waxed_copper_question_block",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_COPPER_BLOCK)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.COLOR_ORANGE)));

        STORAGE_WAXED_CUT_COPPER = registerBlock("storage_waxed_cut_copper",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_CUT_COPPER)));

        SMASHABLE_WAXED_CUT_COPPER = registerBlock("smashable_waxed_cut_copper",
                () -> new SmashableBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_CUT_COPPER)));

        WAXED_CUT_COPPER_PEDESTAL = registerBlock("waxed_cut_copper_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_CUT_COPPER)));


        WAXED_EXPOSED_COPPER_QUESTION_BLOCK = registerBlock("waxed_exposed_copper_question_block",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_EXPOSED_COPPER)));

        INVISIBLE_WAXED_EXPOSED_COPPER_QUESTION_BLOCK = registerBlock("invisible_waxed_exposed_copper_question_block",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_EXPOSED_COPPER)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.TERRACOTTA_LIGHT_GRAY)));

        STORAGE_WAXED_EXPOSED_CUT_COPPER = registerBlock("storage_waxed_exposed_cut_copper",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_EXPOSED_COPPER)));

        SMASHABLE_WAXED_EXPOSED_CUT_COPPER = registerBlock("smashable_waxed_exposed_cut_copper",
                () -> new SmashableBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_EXPOSED_COPPER)));

        WAXED_EXPOSED_CUT_COPPER_PEDESTAL = registerBlock("waxed_exposed_cut_copper_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_EXPOSED_COPPER)));


        WAXED_WEATHERED_COPPER_QUESTION_BLOCK = registerBlock("waxed_weathered_copper_question_block",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_WEATHERED_COPPER)));

        INVISIBLE_WAXED_WEATHERED_COPPER_QUESTION_BLOCK = registerBlock("invisible_waxed_weathered_copper_question_block",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_WEATHERED_COPPER)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.WARPED_STEM)));

        STORAGE_WAXED_WEATHERED_CUT_COPPER = registerBlock("storage_waxed_weathered_cut_copper",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_WEATHERED_COPPER)));

        SMASHABLE_WAXED_WEATHERED_CUT_COPPER = registerBlock("smashable_waxed_weathered_cut_copper",
                () -> new SmashableBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_WEATHERED_COPPER)));

        WAXED_WEATHERED_CUT_COPPER_PEDESTAL = registerBlock("waxed_weathered_cut_copper_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_WEATHERED_COPPER)));


        WAXED_OXIDIZED_COPPER_QUESTION_BLOCK = registerBlock("waxed_oxidized_copper_question_block",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_OXIDIZED_COPPER)));

        INVISIBLE_WAXED_OXIDIZED_COPPER_QUESTION_BLOCK = registerBlock("invisible_waxed_oxidized_copper_question_block",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_OXIDIZED_COPPER)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.WARPED_NYLIUM)));

        STORAGE_WAXED_OXIDIZED_CUT_COPPER = registerBlock("storage_waxed_oxidized_cut_copper",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_OXIDIZED_COPPER)));

        SMASHABLE_WAXED_OXIDIZED_CUT_COPPER = registerBlock("smashable_waxed_oxidized_cut_copper",
                () -> new SmashableBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_OXIDIZED_COPPER)));

        WAXED_OXIDIZED_CUT_COPPER_PEDESTAL = registerBlock("waxed_oxidized_cut_copper_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_OXIDIZED_COPPER)));


        CALCITE_CHECKERED_TILES = registerBlock("calcite_checkered_tiles",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE).mapColor(DyeColor.BLACK)));
        CALCITE_CHECKERED_TILE_SLAB = registerBlock("calcite_checkered_tile_slab", () -> slab(BlockRegistry.CALCITE_CHECKERED_TILES.get()));
        CALCITE_CHECKERED_TILE_STAIRS = registerBlock("calcite_checkered_tile_stairs", () -> stair(BlockRegistry.CALCITE_CHECKERED_TILES.get()));
        CALCITE_CHECKERED_TILE_WALL = registerBlock("calcite_checkered_tile_wall", () -> wall(BlockRegistry.CALCITE_CHECKERED_TILES.get()));

        Arrays.stream(DyeColor.values()).filter(color -> color != DyeColor.WHITE).forEach(color ->
                CALCITE.put(color, registerBlock(color.getName() + "_calcite",
                        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE).mapColor(color)))));

        Arrays.stream(DyeColor.values()).forEach(color ->
                POLISHED_CALCITE.put(color, registerBlock("polished_" + color.getName() + "_calcite",
                        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE)
                                .mapColor(color.getName().equals(DyeColor.WHITE.getName()) ? MapColor.TERRACOTTA_WHITE : color.getMapColor())))));

        Arrays.stream(DyeColor.values()).forEach(color ->
                CALCITE_BRICKS.put(color, registerBlock(color.getName() + "_calcite_bricks",
                        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE)
                                .mapColor(color.getName().equals(DyeColor.WHITE.getName()) ? MapColor.TERRACOTTA_WHITE : color.getMapColor())))));

        Arrays.stream(DyeColor.values()).forEach(color ->
                CHISELED_CALCITE_BRICKS.put(color, registerBlock("chiseled_" + color.getName() + "_calcite_bricks",
                        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE)
                                .mapColor(color.getName().equals(DyeColor.WHITE.getName()) ? MapColor.TERRACOTTA_WHITE : color.getMapColor())))));

        Arrays.stream(DyeColor.values()).forEach(color ->
                CRACKED_CALCITE_BRICKS.put(color, registerBlock("cracked_" + color.getName() + "_calcite_bricks",
                        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE)
                                .mapColor(color.getName().equals(DyeColor.WHITE.getName()) ? MapColor.TERRACOTTA_WHITE : color.getMapColor())))));

        Arrays.stream(DyeColor.values()).forEach(color ->
                CALCITE_BRICK_PEDESTAL.put(color, registerBlock(color.getName() + "_calcite_brick_pedestal",
                        () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE)
                                .mapColor(color.getName().equals(DyeColor.WHITE.getName()) ? MapColor.TERRACOTTA_WHITE : color.getMapColor())))));

        Arrays.stream(DyeColor.values()).forEach(color ->
                STORAGE_CALCITE_BRICKS.put(color, registerBlock("storage_" + color.getName() + "_calcite_bricks",
                        () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE)
                                .mapColor(color.getName().equals(DyeColor.WHITE.getName()) ? MapColor.TERRACOTTA_WHITE : color.getMapColor())))));

        CLASSIC_CHECKPOINT_FLAG = registerNoItemBlock("classic_checkpoint_flag",
                () -> new CheckpointFlagBlock(3, null, BlockBehaviour.Properties.of().mapColor(MapColor.GOLD)
                        .sound(SoundType.NETHERITE_BLOCK).isViewBlocking(BlockRegistry::never)
                        .strength(2.5F, 3.0F).requiresCorrectToolForDrops().noCollission()));

        Arrays.stream(DyeColor.values()).forEach(color ->
                CHECKPOINT_FLAGS.put(color, registerNoItemBlock(color.getName() + "_checkpoint_flag",
                        () -> new CheckpointFlagBlock(3, color, BlockBehaviour.Properties.of().mapColor(MapColor.GOLD)
                                .sound(SoundType.NETHERITE_BLOCK).isViewBlocking(BlockRegistry::never)
                                .strength(2.5F, 3.0F).requiresCorrectToolForDrops().noCollission()))));


        CLASSIC_GOAL_POLE = registerBlock("classic_goal_pole",
                () -> new GoalPoleBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN)
                        .sound(SoundType.NETHERITE_BLOCK).isViewBlocking(BlockRegistry::never)
                        .strength(5.0F, 6.0F).requiresCorrectToolForDrops()));

        Arrays.stream(DyeColor.values()).forEach(color ->
                GOAL_POLES.put(color, registerBlock(color.getName() + "_goal_pole",
                        () -> new GoalPoleBlock(BlockBehaviour.Properties.of().mapColor(MapColor.GOLD)
                                .sound(SoundType.NETHERITE_BLOCK).isViewBlocking(BlockRegistry::never)
                                .strength(5.0F, 6.0F).requiresCorrectToolForDrops()))));


        CLEAR_WARP_PIPE = registerBlock("clear_warp_pipe",
                () -> new ClearWarpPipeBlock(null, BlockBehaviour.Properties.of().mapColor(MapColor.NONE)
                        .sound(MarioverseSoundTypes.CLEAR_PIPE).instrument(NoteBlockInstrument.CHIME)
                        .isSuffocating(BlockRegistry::never).isViewBlocking(BlockRegistry::never)
                        .strength(3.0F, 500.0F).requiresCorrectToolForDrops().noOcclusion()));

        // Keep below CLEAR_WARP_PIPE to prevent crash
        Arrays.stream(DyeColor.values()).forEach(color ->
                WARP_PIPES.put(color, registerBlock(color.getName() + "_warp_pipe",
                        () -> new WarpPipeBlock(color, BlockBehaviour.Properties.of().mapColor(color)
                                .sound(SoundType.NETHERITE_BLOCK).instrument(NoteBlockInstrument.BASS)
                                .strength(3.5F, 1000.0F).isViewBlocking(BlockRegistry::always)
                                .requiresCorrectToolForDrops()))));

        Arrays.stream(DyeColor.values()).forEach(color ->
                PIPE_JUNCTION.put(color, registerBlock(color.getName() + "_pipe_junction",
                        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).mapColor(color)))));


        PIPE_BUBBLES = registerNoItemBlock("pipe_bubbles",
                () -> new PipeBubblesBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.DESTROY)
                        .replaceable().noCollission().noLootTable().liquid()));

        WATER_SPOUT = registerNoItemBlock("water_spout",
                () -> new WaterSpoutBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER)
                        .sound(MarioverseSoundTypes.WATER_SPOUT_TYPE).pushReaction(PushReaction.DESTROY)
                        .isRedstoneConductor(BlockRegistry::never).isSuffocating(BlockRegistry::never)
                        .isViewBlocking(BlockRegistry::never).replaceable().noCollission().noLootTable()));
    }

    public static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> blocks = Marioverse.BLOCKS.register(name, block);
        Marioverse.ITEMS.register(name, () -> new BlockItem(blocks.get(), new Item.Properties()));
        return blocks;
    }

    public static <T extends Block> DeferredBlock<T> registerNoItemBlock(String name, Supplier<T> block) {
        return Marioverse.BLOCKS.register(name, block);
    }

    public static void registerFlowerPots() {
        FlowerPotBlock pot = (FlowerPotBlock) Blocks.FLOWER_POT;
        pot.addPlant(BlockRegistry.BLUE_TRAMPOLINE_CAP.getId(), BlockRegistry.POTTED_BLUE_TRAMPOLINE_CAP);
        pot.addPlant(BlockRegistry.DANGO_BLOSSOM.getId(), BlockRegistry.POTTED_DANGO_BLOSSOM);
        pot.addPlant(BlockRegistry.MUSHROOT_SAPLING.getId(), BlockRegistry.POTTED_MUSHROOT_SAPLING);
        pot.addPlant(BlockRegistry.RED_TRAMPOLINE_CAP.getId(), BlockRegistry.POTTED_RED_TRAMPOLINE_CAP);
    }

    private static Block log(MapColor colorTop, MapColor color) {
        return new RotatedPillarBlock(BlockBehaviour.Properties.of()
                .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? colorTop : color)
                .instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD)
                .strength(2.0F).ignitedByLava());
    }

    private static Block log(MapColor colorTop, MapColor color, SoundType soundType) {
        return new RotatedPillarBlock(BlockBehaviour.Properties.of()
                .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? colorTop : color)
                .instrument(NoteBlockInstrument.BASS).sound(soundType)
                .strength(2.0F).ignitedByLava());
    }

    private static Block leaves(SoundType soundType) {
        return new LeavesBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT)
                .pushReaction(PushReaction.DESTROY).isValidSpawn(BlockRegistry::ocelotOrParrot)
                .isSuffocating(BlockRegistry::never).isViewBlocking(BlockRegistry::never)
                .isRedstoneConductor(BlockRegistry::never).strength(0.2F)
                .sound(soundType).randomTicks().noOcclusion().ignitedByLava());
    }

    private static Block button(Block block, BlockSetType blockSetType, int ticksPressed) {
        return new ButtonBlock(blockSetType, ticksPressed, BlockBehaviour.Properties.ofFullCopy(block).noCollission());
    }

    private static Block pressurePlate(Block block, BlockSetType blockSetType) {
        return new PressurePlateBlock(blockSetType, BlockBehaviour.Properties.ofFullCopy(block).noCollission());
    }

    private static Block slab(Block block) {
        return new SlabBlock(BlockBehaviour.Properties.ofFullCopy(block));
    }

    private static Block stair(Block block) {
        return new StairBlock(block.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(block));
    }

    private static Block wall(Block block) {
        return new WallBlock(BlockBehaviour.Properties.ofFullCopy(block).forceSolidOn());
    }

    private static boolean always(BlockState state, BlockGetter block, BlockPos pos) {
        return true;
    }

    private static boolean never(BlockState state, BlockGetter block, BlockPos pos) {
        return false;
    }

    public static Boolean isActive(BlockState state, BlockGetter blockGetter, BlockPos pos, EntityType<?> entity) {
        if (blockGetter instanceof BlueDottedLineBlock)
            return !state.getValue(OnBlock.ACTIVE);
        if (blockGetter instanceof OnBlock)
            return state.getValue(OnBlock.ACTIVE);
        return false;
    }

    private static boolean isActive(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        if (blockGetter instanceof BlueDottedLineBlock)
            return !state.getValue(OnBlock.ACTIVE);
        if (blockGetter instanceof OnBlock)
            return state.getValue(OnBlock.ACTIVE);
        return false;
    }

    public static Boolean ocelotOrParrot(BlockState state, BlockGetter blockGetter, BlockPos pos, EntityType<?> type) {
        return type == EntityType.OCELOT || type == EntityType.PARROT;
    }

    public static void init() {
    }

    public static void registerAliases() {
        Marioverse.BLOCKS.addAlias(ResourceLocation
                .parse("superbb:mushroot_picket_fence"), MUSHROOT_PICKET_FENCE.getId());
        Marioverse.ITEMS.addAlias(ResourceLocation
                .parse("superbb:mushroot_picket_fence"), MUSHROOT_PICKET_FENCE.getId());
        Marioverse.BLOCKS.addAlias(ResourceLocation
                .parse("superbb:white_picket_fence"), WHITE_PICKET_FENCE.getId());
        Marioverse.ITEMS.addAlias(ResourceLocation
                .parse("superbb:white_picket_fence"), WHITE_PICKET_FENCE.getId());
    }
}
