package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.items.BasePowerUpItem;
import com.wenxin2.marioverse.items.OneUpMushroomItem;
import com.wenxin2.marioverse.items.WrenchItem;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredItem;

public class ItemRegistry {
    public static final DeferredItem<Item> PIPE_WRENCH;
    public static final DeferredItem<Item> MUSHROOM;
    public static final DeferredItem<Item> ONE_UP_MUSHROOM;

    static
    {
        PIPE_WRENCH = registerItem("pipe_wrench",
                () -> new WrenchItem(new Item.Properties()
                        .attributes(WrenchItem.createAttributes(Tiers.IRON, 3, -3.2F))
                        .durability(128), Tiers.IRON));
        MUSHROOM = registerItem("mushroom",
                () -> new BasePowerUpItem(EntityRegistry.MUSHROOM, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        ONE_UP_MUSHROOM = registerItem("one_up_mushroom",
                () -> new OneUpMushroomItem(EntityRegistry.ONE_UP_MUSHROOM, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
    }

    public static <T extends Item> DeferredItem<T> registerItem(String name, Supplier<T> item)
    {
        return Marioverse.ITEMS.register(name, item);
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
