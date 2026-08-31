package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.data.ArrowColorShapedRecipe;
import com.wenxin2.marioverse.data.ArrowColorShapelessRecipe;
import com.wenxin2.marioverse.data.ArrowSignUpgradeRecipe;
import com.wenxin2.marioverse.data.HexColorShapedRecipe;
import com.wenxin2.marioverse.data.HexColorShapelessRecipe;
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
    public static final Supplier<RecipeSerializer<HexColorShapelessRecipe>> HEX_COLOR_SHAPELESS;
    public static final Supplier<RecipeType<HexColorShapelessRecipe>> HEX_COLOR_SHAPELESS_TYPE;
    public static final Supplier<RecipeSerializer<ArrowColorShapedRecipe>> ARROW_COLOR_SHAPED;
    public static final Supplier<RecipeType<ArrowColorShapedRecipe>> ARROW_COLOR_SHAPED_TYPE;
    public static final Supplier<RecipeSerializer<ArrowColorShapelessRecipe>> ARROW_COLOR_SHAPELESS;
    public static final Supplier<RecipeType<ArrowColorShapelessRecipe>> ARROW_COLOR_SHAPELESS_TYPE;
    public static final Supplier<RecipeSerializer<ArrowSignUpgradeRecipe>> ARROW_SIGN_UPGRADE;
    public static final Supplier<RecipeType<ArrowSignUpgradeRecipe>> ARROW_SIGN_UPGRADE_TYPE;

    static {
        HEX_COLOR_SHAPED = Marioverse.RECIPE_SERIALIZERS
                .register("hex_color_shaped", HexColorShapedRecipe.Serializer::new);
        HEX_COLOR_SHAPED_TYPE = Marioverse.RECIPE_TYPES.register("hex_color_shaped",
                () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "hex_color_shaped")));

        HEX_COLOR_SHAPELESS = Marioverse.RECIPE_SERIALIZERS
                .register("hex_color_shapeless", HexColorShapelessRecipe.Serializer::new);
        HEX_COLOR_SHAPELESS_TYPE = Marioverse.RECIPE_TYPES.register("hex_color_shapeless",
                () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "hex_color_shapeless")));

        ARROW_COLOR_SHAPED = Marioverse.RECIPE_SERIALIZERS
                .register("arrow_color_shaped", ArrowColorShapedRecipe.Serializer::new);
        ARROW_COLOR_SHAPED_TYPE = Marioverse.RECIPE_TYPES.register("arrow_color_shaped",
                () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "arrow_color_shaped")));

        ARROW_COLOR_SHAPELESS = Marioverse.RECIPE_SERIALIZERS
                .register("arrow_color_shapeless", ArrowColorShapelessRecipe.Serializer::new);
        ARROW_COLOR_SHAPELESS_TYPE = Marioverse.RECIPE_TYPES.register("arrow_color_shapeless",
                () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "arrow_color_shapeless")));

        ARROW_SIGN_UPGRADE = Marioverse.RECIPE_SERIALIZERS
                .register("arrow_sign_upgrade", ArrowSignUpgradeRecipe.Serializer::new);
        ARROW_SIGN_UPGRADE_TYPE = Marioverse.RECIPE_TYPES.register("arrow_sign_upgrade",
                () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "arrow_sign_upgrade")));

        WARP_DOOR = Marioverse.RECIPE_SERIALIZERS.register("warp_door",
                () -> new SimpleCraftingRecipeSerializer<>(WarpDoorRecipe::new));
        WARP_TRAPDOOR = Marioverse.RECIPE_SERIALIZERS.register("warp_trapdoor",
                () -> new SimpleCraftingRecipeSerializer<>(WarpTrapDoorRecipe::new));
    }

    public static void init() {
    }
}
