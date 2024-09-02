package com.wenxin2.warp_pipes.init;

import com.wenxin2.warp_pipes.WarpPipes;
import com.wenxin2.warp_pipes.blocks.ClearWarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.PipeBubblesBlock;
import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.WaterSpoutBlock;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.warp_pipes.inventory.WarpPipeMenu;
import com.wenxin2.warp_pipes.items.WrenchItem;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModRegistry {
    public static final EnumMap<DyeColor, DeferredBlock<Block>> WARP_PIPES =
            new EnumMap<>(DyeColor.class);
    public static final DeferredItem<Item> PIPE_WRENCH;
    public static final DeferredBlock<Block> CLEAR_WARP_PIPE;
    public static final DeferredBlock<Block> PIPE_BUBBLES;
    public static final DeferredBlock<Block> WATER_SPOUT;

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WarpPipeBlockEntity>> WARP_PIPE_BLOCK_ENTITY;
    public static final DeferredHolder<MenuType<?>, MenuType<WarpPipeMenu>> WARP_PIPE_MENU;

    static
    {

        PIPE_WRENCH = registerItem("pipe_wrench",
                () -> new WrenchItem(new Item.Properties()
                        .attributes(WrenchItem.createAttributes(Tiers.IRON, 3, -3.2F))
                        .durability(128), Tiers.IRON));

        CLEAR_WARP_PIPE = registerBlock("clear_warp_pipe",
                () -> new ClearWarpPipeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.NONE)
                        .sound(SoundType.GLASS).isSuffocating(ModRegistry::never).isViewBlocking(ModRegistry::never)
                        .strength(3.0F, 500.0F).requiresCorrectToolForDrops().noOcclusion()));

        // Keep below CLEAR_WARP_PIPE to prevent crash
        Arrays.stream(DyeColor.values()).forEach(color ->
                WARP_PIPES.put(color, registerBlock(color.getName() + "_warp_pipe",
                        () -> new WarpPipeBlock(BlockBehaviour.Properties.of().mapColor(color)
                                .sound(SoundType.NETHERITE_BLOCK).strength(3.5F, 1000.0F)
                                .isViewBlocking(ModRegistry::always).requiresCorrectToolForDrops()))));

        PIPE_BUBBLES = registerNoItemBlock("pipe_bubbles",
                () -> new PipeBubblesBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.DESTROY)
                        .replaceable().noCollission().noLootTable().liquid()));

        WATER_SPOUT = registerNoItemBlock("water_spout",
                () -> new WaterSpoutBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).sound(SoundRegistry.WATER_SPOUT)
                        .pushReaction(PushReaction.DESTROY).isRedstoneConductor(ModRegistry::never)
                        .isSuffocating(ModRegistry::never).isViewBlocking(ModRegistry::never)
                        .replaceable().noCollission().noLootTable()));

        WARP_PIPE_BLOCK_ENTITY = WarpPipes.BLOCK_ENTITIES.register("warp_pipe",
                () -> BlockEntityType.Builder.of(WarpPipeBlockEntity::new,
                                Stream.concat(WARP_PIPES.values().stream().map(DeferredBlock::get),
                                        Stream.of(CLEAR_WARP_PIPE.get())).toArray(Block[]::new))
                        .build(null));

        WARP_PIPE_MENU = WarpPipes.MENUS.register("warp_pipe", () -> new MenuType<>(WarpPipeMenu::new, FeatureFlags.REGISTRY.allFlags()));
    }

    public static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block)
    {
        DeferredBlock<T> blocks = WarpPipes.BLOCKS.register(name, block);
        WarpPipes.ITEMS.register(name, () -> new BlockItem(blocks.get(), new Item.Properties()));
        return blocks;
    }

    public static <T extends Block> DeferredBlock<T> registerNoItemBlock(String name, Supplier<T> block)
    {
        return WarpPipes.BLOCKS.register(name, block);
    }

    public static <T extends Item> DeferredItem<T> registerItem(String name, Supplier<T> item)
    {
        return WarpPipes.ITEMS.register(name, item);
    }

    private static boolean always(BlockState state, BlockGetter block, BlockPos pos)
    {
        return true;
    }

    private static boolean never(BlockState state, BlockGetter block, BlockPos pos)
    {
        return false;
    }

    public static void init()
    {}
}
