package com.wenxin2.marioverse.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import com.wenxin2.marioverse.registries.RecipeSerializerRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;
import org.jetbrains.annotations.NotNull;

/**
 * Shapeless counterpart of ArrowColorShapedRecipe - unordered ingredients, one of which may be a dye-tag
 * color slot. If the input already contains a stack of the result item (e.g. an existing arrow sign),
 * that stack is copied (preserving direction/waxed/etc.) rather than building a fresh one, so this also
 * serves as the "sign + dye recolors the sign" recipe.
 */
public class ArrowColorShapelessRecipe implements CraftingRecipe {
    private final String group;
    private final CraftingBookCategory category;
    private final Item result;
    private final int count;
    private final List<Either<DyeColorIngredient, Ingredient>> ingredients;

    public ArrowColorShapelessRecipe(String group, CraftingBookCategory category,
                                      List<Either<DyeColorIngredient, Ingredient>> ingredients, Item result, int count) {
        this.group = group;
        this.category = category;
        this.ingredients = ingredients;
        this.result = result;
        this.count = count;
    }

    private static List<ItemStack> nonEmptyItems(CraftingInput input) {
        List<ItemStack> nonEmpty = new ArrayList<>();
        for (ItemStack stack : input.items())
            if (!stack.isEmpty())
                nonEmpty.add(stack);
        return nonEmpty;
    }

    private List<Predicate<ItemStack>> tests() {
        List<Predicate<ItemStack>> tests = new ArrayList<>(this.ingredients.size());
        for (Either<DyeColorIngredient, Ingredient> slot : this.ingredients)
            tests.add(stack -> slot.map(dye -> dye.test(stack), ingredient -> ingredient.test(stack)));
        return tests;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        List<ItemStack> nonEmpty = nonEmptyItems(input);
        if (nonEmpty.size() != this.ingredients.size())
            return false;
        return RecipeMatcher.findMatches(nonEmpty, this.tests()) != null;
    }

    @NotNull
    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        List<ItemStack> nonEmpty = nonEmptyItems(input);
        int[] assignment = RecipeMatcher.findMatches(nonEmpty, this.tests());

        DyeColor found = null;
        if (assignment != null) {
            for (int i = 0; i < assignment.length; i++) {
                int slotIndex = assignment[i];
                if (slotIndex < 0)
                    continue;
                Either<DyeColorIngredient, Ingredient> slot = this.ingredients.get(slotIndex);
                if (slot.left().isPresent()) {
                    DyeColor color = slot.left().get().colorOf(nonEmpty.get(i));
                    if (color != null) {
                        found = color;
                        break;
                    }
                }
            }
        }

        ItemStack base = null;
        for (ItemStack stack : nonEmpty) {
            if (stack.is(this.result)) {
                base = stack;
                break;
            }
        }

        ItemStack output = base != null ? base.copy() : new ItemStack(this.result, this.count);
        output.setCount(this.count);
        if (found != null)
            output.set(DataComponentRegistry.DYE_COLOR.get(), found);
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= this.ingredients.size();
    }

    @NotNull
    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(this.result, this.count);
    }

    @NotNull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> flattened = NonNullList.withSize(this.ingredients.size(), Ingredient.EMPTY);
        for (int i = 0; i < this.ingredients.size(); i++)
            flattened.set(i, this.ingredients.get(i).map(DyeColorIngredient::toIngredient, ingredient -> ingredient));
        return flattened;
    }

    @NotNull
    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public boolean isSpecial() {
        return false;
    }

    @NotNull
    @Override
    public CraftingBookCategory category() {
        return this.category;
    }

    @NotNull
    @Override
    public RecipeSerializer<? extends CraftingRecipe> getSerializer() {
        return RecipeSerializerRegistry.ARROW_COLOR_SHAPELESS.get();
    }

    public List<Either<DyeColorIngredient, Ingredient>> getSlots() {
        return this.ingredients;
    }

    public Item getResult() {
        return this.result;
    }

    public static class Serializer implements RecipeSerializer<ArrowColorShapelessRecipe> {
        private static final Codec<Either<DyeColorIngredient, Ingredient>> SLOT_CODEC =
                Codec.either(DyeColorIngredient.CODEC, Ingredient.CODEC);

        private static final MapCodec<ArrowColorShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(r -> r.group),
                        CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(r -> r.category),
                        SLOT_CODEC.listOf().fieldOf("ingredients").forGetter(r -> r.ingredients),
                        BuiltInRegistries.ITEM.byNameCodec().fieldOf("result").forGetter(r -> r.result),
                        Codec.INT.optionalFieldOf("count", 1).forGetter(r -> r.count)
                ).apply(instance, ArrowColorShapelessRecipe::new)
        );

        private static final StreamCodec<RegistryFriendlyByteBuf, Either<DyeColorIngredient, Ingredient>> SLOT_STREAM_CODEC =
                StreamCodec.of(
                        (buf, slot) -> {
                            if (slot.left().isPresent()) {
                                buf.writeBoolean(true);
                                DyeColorIngredient.STREAM_CODEC.encode(buf, slot.left().get());
                            } else {
                                buf.writeBoolean(false);
                                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, slot.right().get());
                            }
                        },
                        buf -> buf.readBoolean()
                                ? Either.left(DyeColorIngredient.STREAM_CODEC.decode(buf))
                                : Either.right(Ingredient.CONTENTS_STREAM_CODEC.decode(buf))
                );

        private static final StreamCodec<RegistryFriendlyByteBuf, ArrowColorShapelessRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    buf.writeUtf(recipe.group);
                    buf.writeEnum(recipe.category);
                    buf.writeVarInt(recipe.ingredients.size());
                    for (Either<DyeColorIngredient, Ingredient> slot : recipe.ingredients)
                        SLOT_STREAM_CODEC.encode(buf, slot);
                    buf.writeVarInt(BuiltInRegistries.ITEM.getId(recipe.result));
                    buf.writeVarInt(recipe.count);
                },
                buf -> {
                    String group = buf.readUtf();
                    CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);

                    int count = buf.readVarInt();
                    List<Either<DyeColorIngredient, Ingredient>> ingredients = new ArrayList<>(count);
                    for (int i = 0; i < count; i++)
                        ingredients.add(SLOT_STREAM_CODEC.decode(buf));

                    Item result = BuiltInRegistries.ITEM.byId(buf.readVarInt());
                    int outputCount = buf.readVarInt();
                    return new ArrowColorShapelessRecipe(group, category, ingredients, result, outputCount);
                }
        );

        @NotNull
        @Override
        public MapCodec<ArrowColorShapelessRecipe> codec() {
            return CODEC;
        }

        @NotNull
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ArrowColorShapelessRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
