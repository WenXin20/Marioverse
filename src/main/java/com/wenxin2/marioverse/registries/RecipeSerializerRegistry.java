package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.data.HexColorShapedRecipe;
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
    public static final Supplier<RecipeSerializer<HexColorShapedRecipe>> HEX_COLOR_SHAPED;
    public static final Supplier<RecipeType<HexColorShapedRecipe>> HEX_COLOR_SHAPED_TYPE;

    static {
        HEX_COLOR_SHAPED = Marioverse.RECIPE_SERIALIZERS
                .register("hex_color_shaped", HexColorShapedRecipe.Serializer::new);
        HEX_COLOR_SHAPED_TYPE = Marioverse.RECIPE_TYPES.register("hex_color_shaped",
                () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "hex_color_shaped")));
        WARP_DOOR = Marioverse.RECIPE_SERIALIZERS.register("warp_door",
                () -> new SimpleCraftingRecipeSerializer<>(WarpDoorRecipe::new));
        WARP_TRAPDOOR = Marioverse.RECIPE_SERIALIZERS.register("warp_trapdoor",
                () -> new SimpleCraftingRecipeSerializer<>(WarpTrapDoorRecipe::new));
    }

    public static void init() {
    }
}