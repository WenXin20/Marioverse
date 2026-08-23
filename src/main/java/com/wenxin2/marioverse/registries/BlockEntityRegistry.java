package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.StorageBrickBlock;
import com.wenxin2.marioverse.blocks.entities.ArrowSignBlockEntity;
import com.wenxin2.marioverse.blocks.entities.BlockSpawnerBlockEntity;
import com.wenxin2.marioverse.blocks.entities.CheckpointFlagBlockEntity;
import com.wenxin2.marioverse.blocks.entities.CoinBlockEntity;
import com.wenxin2.marioverse.blocks.entities.GoalPoleBlockEntity;
import com.wenxin2.marioverse.blocks.entities.PottedPiranhaPlantBlockEntity;
import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.blocks.entities.StarCoinBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpDoorBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpTrapDoorBlockEntity;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BlockEntityRegistry {
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ArrowSignBlockEntity>> ARROW_SIGN;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockSpawnerBlockEntity>> BLOCK_SPAWNER_BLOCK_ENTITY;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CoinBlockEntity>> COIN_BLOCK_ENTITY;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CheckpointFlagBlockEntity>> CHECKPOINT_FLAG_BLOCK_ENTITY;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GoalPoleBlockEntity>> GOAL_POLE_BLOCK_ENTITY;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<QuestionBlockEntity>> INVISIBLE_QUESTION_BLOCK_ENTITY;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PottedPiranhaPlantBlockEntity>> POTTED_PIRANHA_PLANT_BLOCK_ENTITY;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<QuestionBlockEntity>> QUESTION_BLOCK_ENTITY;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<QuestionBlockEntity>> STORAGE_BRICKS_BLOCK_ENTITY;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StarCoinBlockEntity>> STAR_COIN_BLOCK_ENTITY;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WarpDoorBlockEntity>> WARP_DOOR_BLOCK_ENTITY;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WarpPipeBlockEntity>> WARP_PIPE_BLOCK_ENTITY;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WarpTrapDoorBlockEntity>> WARP_TRAPDOOR_BLOCK_ENTITY;

    static {
        ARROW_SIGN = Marioverse.BLOCK_ENTITIES.register("arrow_sign",
                () -> BlockEntityType.Builder.of((pos, state) ->
                                new ArrowSignBlockEntity(BlockEntityRegistry.ARROW_SIGN.get(), pos, state),
                        BlockRegistry.MUSHROOT_ARROW_SIGN.get(),
                        BlockRegistry.MUSHROOT_WALL_ARROW_SIGN.get(),
                        BlockRegistry.MUSHROOT_HANGING_ARROW_SIGN.get(),
                        BlockRegistry.LARGE_MUSHROOT_ARROW_SIGN.get(),
                        BlockRegistry.LARGE_MUSHROOT_WALL_ARROW_SIGN.get()
                ).build(null));

        BLOCK_SPAWNER_BLOCK_ENTITY = Marioverse.BLOCK_ENTITIES.register("block_spawner",
                () -> BlockEntityType.Builder.of((pos, state) ->
                                new BlockSpawnerBlockEntity(BlockEntityRegistry.BLOCK_SPAWNER_BLOCK_ENTITY.get(), pos, state),
                        BlockRegistry.BLOCK_SPAWNER.get()).build(null));

        COIN_BLOCK_ENTITY = Marioverse.BLOCK_ENTITIES.register("coin",
                () -> BlockEntityType.Builder.of(CoinBlockEntity::new,
                                BlockRegistry.COIN.get()).build(null));

        POTTED_PIRANHA_PLANT_BLOCK_ENTITY = Marioverse.BLOCK_ENTITIES.register("potted_piranha_plant",
                () -> BlockEntityType.Builder.of(PottedPiranhaPlantBlockEntity::new,
                                BlockRegistry.POTTED_PIRANHA_PLANT.get()).build(null));

        STAR_COIN_BLOCK_ENTITY = Marioverse.BLOCK_ENTITIES.register("star_coin",
                () -> BlockEntityType.Builder.of(StarCoinBlockEntity::new,
                                BlockRegistry.STAR_COIN.get()).build(null));

        INVISIBLE_QUESTION_BLOCK_ENTITY = Marioverse.BLOCK_ENTITIES.register("invisible_question_block",
                () -> BlockEntityType.Builder.of((pos, state) ->
                                new QuestionBlockEntity(BlockEntityRegistry.INVISIBLE_QUESTION_BLOCK_ENTITY.get(), pos, state),
                        BlockRegistry.INVISIBLE_AMETHYST_QUESTION_BLOCK.get(),
                        BlockRegistry.INVISIBLE_BLACKSTONE_QUESTION_BRICKS.get(),
                        BlockRegistry.INVISIBLE_CALCITE_QUESTION_BLOCK.get(),
                        BlockRegistry.INVISIBLE_COPPER_QUESTION_BLOCK.get(),
                        BlockRegistry.INVISIBLE_DARK_PRISMARINE_QUESTION_BLOCK.get(),
                        BlockRegistry.INVISIBLE_DEEPSLATE_QUESTION_BRICKS.get(),
                        BlockRegistry.INVISIBLE_DEEPSLATE_QUESTION_TILES.get(),
                        BlockRegistry.INVISIBLE_DEEP_FUNGAL_QUESTION_BLOCK.get(),
                        BlockRegistry.INVISIBLE_END_STONE_QUESTION_BRICKS.get(),
                        BlockRegistry.INVISIBLE_EXPOSED_COPPER_QUESTION_BLOCK.get(),
                        BlockRegistry.INVISIBLE_FUNGAL_QUESTION_BLOCK.get(),
                        BlockRegistry.INVISIBLE_MOSSY_STONE_QUESTION_BRICKS.get(),
                        BlockRegistry.INVISIBLE_MUD_QUESTION_BRICKS.get(),
                        BlockRegistry.INVISIBLE_NETHER_QUESTION_BRICKS.get(),
                        BlockRegistry.INVISIBLE_OXIDIZED_COPPER_QUESTION_BLOCK.get(),
                        BlockRegistry.INVISIBLE_PRISMARINE_QUESTION_BRICKS.get(),
                        BlockRegistry.INVISIBLE_PURPUR_QUESTION_BLOCK.get(),
                        BlockRegistry.INVISIBLE_QUARTZ_QUESTION_BRICKS.get(),
                        BlockRegistry.INVISIBLE_QUESTION_BRICKS.get(),
                        BlockRegistry.INVISIBLE_RED_NETHER_QUESTION_BRICKS.get(),
                        BlockRegistry.INVISIBLE_RED_SANDSTONE_QUESTION_BLOCK.get(),
                        BlockRegistry.INVISIBLE_SANDSTONE_QUESTION_BLOCK.get(),
                        BlockRegistry.INVISIBLE_STONE_QUESTION_BRICKS.get(),
                        BlockRegistry.INVISIBLE_TUFF_QUESTION_BRICKS.get(),
                        BlockRegistry.INVISIBLE_WAXED_COPPER_QUESTION_BLOCK.get(),
                        BlockRegistry.INVISIBLE_WAXED_EXPOSED_COPPER_QUESTION_BLOCK.get(),
                        BlockRegistry.INVISIBLE_WAXED_OXIDIZED_COPPER_QUESTION_BLOCK.get(),
                        BlockRegistry.INVISIBLE_WAXED_WEATHERED_COPPER_QUESTION_BLOCK.get(),
                        BlockRegistry.INVISIBLE_WEATHERED_COPPER_QUESTION_BLOCK.get()).build(null));

        QUESTION_BLOCK_ENTITY = Marioverse.BLOCK_ENTITIES.register("question_block",
                () -> BlockEntityType.Builder.of((pos, state) ->
                                new QuestionBlockEntity(BlockEntityRegistry.QUESTION_BLOCK_ENTITY.get(), pos, state),
                        BlockRegistry.AMETHYST_QUESTION_BLOCK.get(),
                        BlockRegistry.BLACKSTONE_QUESTION_BRICKS.get(),
                        BlockRegistry.CALCITE_QUESTION_BLOCK.get(),
                        BlockRegistry.COPPER_QUESTION_BLOCK.get(),
                        BlockRegistry.DARK_PRISMARINE_QUESTION_BLOCK.get(),
                        BlockRegistry.DEEPSLATE_QUESTION_BRICKS.get(),
                        BlockRegistry.DEEPSLATE_QUESTION_TILES.get(),
                        BlockRegistry.DEEP_FUNGAL_QUESTION_BLOCK.get(),
                        BlockRegistry.END_STONE_QUESTION_BRICKS.get(),
                        BlockRegistry.EXPOSED_COPPER_QUESTION_BLOCK.get(),
                        BlockRegistry.FUNGAL_QUESTION_BLOCK.get(),
                        BlockRegistry.MOSSY_STONE_QUESTION_BRICKS.get(),
                        BlockRegistry.MUD_QUESTION_BRICKS.get(),
                        BlockRegistry.NETHER_QUESTION_BRICKS.get(),
                        BlockRegistry.OXIDIZED_COPPER_QUESTION_BLOCK.get(),
                        BlockRegistry.PRISMARINE_QUESTION_BRICKS.get(),
                        BlockRegistry.PURPUR_QUESTION_BLOCK.get(),
                        BlockRegistry.QUARTZ_QUESTION_BRICKS.get(),
                        BlockRegistry.QUESTION_BRICKS.get(),
                        BlockRegistry.RED_NETHER_QUESTION_BRICKS.get(),
                        BlockRegistry.RED_SANDSTONE_QUESTION_BLOCK.get(),
                        BlockRegistry.SANDSTONE_QUESTION_BLOCK.get(),
                        BlockRegistry.STONE_QUESTION_BRICKS.get(),
                        BlockRegistry.TUFF_QUESTION_BRICKS.get(),
                        BlockRegistry.WAXED_COPPER_QUESTION_BLOCK.get(),
                        BlockRegistry.WAXED_EXPOSED_COPPER_QUESTION_BLOCK.get(),
                        BlockRegistry.WAXED_OXIDIZED_COPPER_QUESTION_BLOCK.get(),
                        BlockRegistry.WAXED_WEATHERED_COPPER_QUESTION_BLOCK.get(),
                        BlockRegistry.WEATHERED_COPPER_QUESTION_BLOCK.get()).build(null));

        STORAGE_BRICKS_BLOCK_ENTITY = Marioverse.BLOCK_ENTITIES.register("storage_bricks",
                () -> BlockEntityType.Builder.of((pos, state) ->
                                new QuestionBlockEntity(BlockEntityRegistry.STORAGE_BRICKS_BLOCK_ENTITY.get(), pos, state),
                        BuiltInRegistries.BLOCK.stream().filter(block -> block instanceof StorageBrickBlock).toArray(Block[]::new)).build(null));

        CHECKPOINT_FLAG_BLOCK_ENTITY = Marioverse.BLOCK_ENTITIES.register("checkpoint_flag",
                () -> BlockEntityType.Builder.of(CheckpointFlagBlockEntity::new,
                        Stream.concat(BlockRegistry.CHECKPOINT_FLAGS.values().stream().map(DeferredBlock::get),
                                Stream.of(BlockRegistry.CLASSIC_CHECKPOINT_FLAG.get())).toArray(Block[]::new)).build(null));

        GOAL_POLE_BLOCK_ENTITY = Marioverse.BLOCK_ENTITIES.register("goal_pole",
                () -> BlockEntityType.Builder.of(GoalPoleBlockEntity::new,
                        Stream.concat(BlockRegistry.GOAL_POLES.values().stream().map(DeferredBlock::get),
                                Stream.of(BlockRegistry.CLASSIC_GOAL_POLE.get())).toArray(Block[]::new)).build(null));

        WARP_PIPE_BLOCK_ENTITY = Marioverse.BLOCK_ENTITIES.register("warp_pipe",
                () -> BlockEntityType.Builder.of(WarpPipeBlockEntity::new,
                        Stream.concat(BlockRegistry.WARP_PIPES.values().stream().map(DeferredBlock::get),
                                Stream.of(BlockRegistry.CLEAR_WARP_PIPE.get())).toArray(Block[]::new)).build(null));

        WARP_DOOR_BLOCK_ENTITY = Marioverse.BLOCK_ENTITIES.register("warp_door",
                () -> BlockEntityType.Builder.of(WarpDoorBlockEntity::new,
                        BuiltInRegistries.BLOCK.stream().filter(block -> block instanceof DoorBlock).toArray(Block[]::new)).build(null));

        WARP_TRAPDOOR_BLOCK_ENTITY = Marioverse.BLOCK_ENTITIES.register("warp_trapdoor",
                () -> BlockEntityType.Builder.of(WarpTrapDoorBlockEntity::new,
                        BuiltInRegistries.BLOCK.stream().filter(block -> block instanceof TrapDoorBlock).toArray(Block[]::new)).build(null));
    }

    public static void init() {
    }
}
