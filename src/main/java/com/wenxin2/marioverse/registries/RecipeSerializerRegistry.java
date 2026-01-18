package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.data.WarpDoorRecipe;
import com.wenxin2.marioverse.data.WarpTrapDoorRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;

public class RecipeSerializerRegistry {
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<WarpDoorRecipe>> WARP_DOOR;
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<WarpTrapDoorRecipe>> WARP_TRAPDOOR;

    static {
        WARP_DOOR = Marioverse.RECIPE_SERIALIZERS.register("warp_door",
                () -> new SimpleCraftingRecipeSerializer<>(WarpDoorRecipe::new));
        WARP_TRAPDOOR = Marioverse.RECIPE_SERIALIZERS.register("warp_trapdoor",
                () -> new SimpleCraftingRecipeSerializer<>(WarpTrapDoorRecipe::new));
    }

    public static void init() {
    }
}