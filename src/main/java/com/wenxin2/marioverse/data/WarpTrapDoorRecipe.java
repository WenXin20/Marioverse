package com.wenxin2.marioverse.data;

import com.wenxin2.marioverse.event_handlers.RegistryEventHandlers;
import com.wenxin2.marioverse.registries.RecipeSerializerRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import org.jetbrains.annotations.NotNull;

public class WarpTrapDoorRecipe extends CustomRecipe {
    public static final WarpTrapDoorRecipe INSTANCE = new WarpTrapDoorRecipe(CraftingBookCategory.BUILDING);
    public static final TagKey<Item> INGREDIENTS = TagRegistry.CRAFTS_WARP_TRAPDOOR;

    public WarpTrapDoorRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        boolean foundTrapDoor = false;
        boolean foundExtra = false;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty())
                continue;

            if (stack.getItem() instanceof BlockItem blockItem &&
                    blockItem.getBlock() instanceof TrapDoorBlock trapDoor &&
                    RegistryEventHandlers.WARP_TRAPDOORS.containsKey(trapDoor)) {

                if (foundTrapDoor)
                    return false;
                foundTrapDoor = true;
                continue;
            }

            if (stack.is(INGREDIENTS)) {
                foundExtra = true;
                continue;
            }
            return false;
        }

        return foundTrapDoor && foundExtra;
    }


    @NotNull
    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);

            if (stack.getItem() instanceof BlockItem blockItem) {
                Block warp = RegistryEventHandlers.WARP_TRAPDOORS.get(blockItem.getBlock());
                if (warp != null)
                    return new ItemStack(warp);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializerRegistry.WARP_TRAPDOOR.get();
    }
}