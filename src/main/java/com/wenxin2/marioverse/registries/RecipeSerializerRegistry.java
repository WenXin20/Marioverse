package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.data.WarpDoorRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;

public class RecipeSerializerRegistry {
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<WarpDoorRecipe>> WARP_DOOR;

    static {
        WARP_DOOR = Marioverse.RECIPE_SERIALIZERS.register("warp_door", () -> new SimpleCraftingRecipeSerializer<>(WarpDoorRecipe::new));
    }

    public static void init() {}
}
