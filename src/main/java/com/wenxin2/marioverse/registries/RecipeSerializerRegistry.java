package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.data.ColorSwappableShapedRecipe;
import com.wenxin2.marioverse.data.WarpDoorRecipe;
import com.wenxin2.marioverse.data.WarpTrapDoorRecipe;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;

public class RecipeSerializerRegistry {
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<WarpDoorRecipe>> WARP_DOOR;
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<WarpTrapDoorRecipe>> WARP_TRAPDOOR;
    public static final Supplier<RecipeSerializer<ColorSwappableShapedRecipe>> COLOR_SWAPPABLE_SHAPED;
    public static final Supplier<RecipeType<ColorSwappableShapedRecipe>> COLOR_SWAPPABLE_SHAPED_TYPE;

    static {
        COLOR_SWAPPABLE_SHAPED = Marioverse.RECIPE_SERIALIZERS
                .register("color_swappable_shaped", ColorSwappableShapedRecipe.Serializer::new);
        COLOR_SWAPPABLE_SHAPED_TYPE = Marioverse.RECIPE_TYPES.register("color_swappable_shaped",
                () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "color_swappable_shaped")));
        WARP_DOOR = Marioverse.RECIPE_SERIALIZERS.register("warp_door",
                () -> new SimpleCraftingRecipeSerializer<>(WarpDoorRecipe::new));
        WARP_TRAPDOOR = Marioverse.RECIPE_SERIALIZERS.register("warp_trapdoor",
                () -> new SimpleCraftingRecipeSerializer<>(WarpTrapDoorRecipe::new));
    }

    public static void init() {
    }
}