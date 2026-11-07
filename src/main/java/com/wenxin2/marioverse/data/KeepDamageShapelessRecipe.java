package com.wenxin2.marioverse.data;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.registries.RecipeSerializerRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;

public class KeepDamageShapelessRecipe extends ShapelessRecipe {
    public KeepDamageShapelessRecipe(ShapelessRecipe recipe) {
        super(recipe.getGroup(), recipe.category(), recipe.getResultItem(RegistryAccess.EMPTY), recipe.getIngredients());
    }

    @NotNull
    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack result = super.assemble(input, registries);
        if (!result.isDamageableItem())
            return result;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isDamageableItem()) {
                result.setDamageValue(stack.getDamageValue());
                break;
            }
        }
        return result;
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializerRegistry.KEEP_DAMAGE_SHAPELESS.get();
    }

    public static class Serializer implements RecipeSerializer<KeepDamageShapelessRecipe> {
        private static final MapCodec<KeepDamageShapelessRecipe> CODEC = RecipeSerializer.SHAPELESS_RECIPE.codec()
                .xmap(KeepDamageShapelessRecipe::new, recipe -> recipe);
        private static final StreamCodec<RegistryFriendlyByteBuf, KeepDamageShapelessRecipe> STREAM_CODEC =
                RecipeSerializer.SHAPELESS_RECIPE.streamCodec().map(KeepDamageShapelessRecipe::new, recipe -> recipe);

        @NotNull
        @Override
        public MapCodec<KeepDamageShapelessRecipe> codec() {
            return CODEC;
        }

        @NotNull
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, KeepDamageShapelessRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
