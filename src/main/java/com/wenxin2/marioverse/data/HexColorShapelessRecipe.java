package com.wenxin2.marioverse.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wenxin2.marioverse.registries.RecipeSerializerRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;
import org.jetbrains.annotations.NotNull;

/** Shapeless counterpart of HexColorShapedRecipe - unordered ingredients, one of which may be a color slot. */
public class HexColorShapelessRecipe implements CraftingRecipe {
    private final String group;
    private final CraftingBookCategory category;
    private final Item result;
    private final int count;
    private final List<Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient>> ingredients;

    public HexColorShapelessRecipe(String group, CraftingBookCategory category,
                                    List<Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient>> ingredients,
                                    Item result, int count) {
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
        for (Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient> slot : this.ingredients) {
            tests.add(stack -> slot.map(color -> color.map(tag -> tag.test(stack), item -> item.test(stack)),
                    ingredient -> ingredient.test(stack)));
        }
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

        Integer found = null;
        if (assignment != null) {
            for (int i = 0; i < assignment.length; i++) {
                int slotIndex = assignment[i];
                if (slotIndex < 0)
                    continue;
                Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient> slot = this.ingredients.get(slotIndex);
                if (slot.left().isPresent()) {
                    ItemStack matched = nonEmpty.get(i);
                    Integer color = slot.left().get().map(tag -> tag.colorOf(matched), item -> item.colorOf(matched));
                    if (color != null) {
                        found = color;
                        break;
                    }
                }
            }
        }

        ItemStack output = new ItemStack(this.result, this.count);
        if (found != null)
            output.set(DataComponents.DYED_COLOR, new DyedItemColor(found, true));
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
        for (int i = 0; i < this.ingredients.size(); i++) {
            Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient> slot = this.ingredients.get(i);
            flattened.set(i, slot.map(color -> color.map(TagColorIngredient::toIngredient, item -> Ingredient.of(item.item())),
                    ingredient -> ingredient));
        }
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
        return RecipeSerializerRegistry.HEX_COLOR_SHAPELESS.get();
    }

    public List<Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient>> getSlots() {
        return this.ingredients;
    }

    public static class Serializer implements RecipeSerializer<HexColorShapelessRecipe> {
        private static final Codec<Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient>> SLOT_CODEC =
                Codec.either(Codec.either(TagColorIngredient.CODEC, ItemColorIngredient.CODEC), Ingredient.CODEC);

        private static final MapCodec<HexColorShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(r -> r.group),
                        CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(r -> r.category),
                        SLOT_CODEC.listOf().fieldOf("ingredients").forGetter(r -> r.ingredients),
                        BuiltInRegistries.ITEM.byNameCodec().fieldOf("result").forGetter(r -> r.result),
                        Codec.INT.optionalFieldOf("count", 1).forGetter(r -> r.count)
                ).apply(instance, HexColorShapelessRecipe::new)
        );

        private static final StreamCodec<RegistryFriendlyByteBuf, Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient>> SLOT_STREAM_CODEC =
                StreamCodec.of(
                        (buf, slot) -> {
                            if (slot.left().isPresent()) {
                                buf.writeBoolean(true);
                                Either<TagColorIngredient, ItemColorIngredient> color = slot.left().get();

                                if (color.left().isPresent()) {
                                    buf.writeBoolean(true);
                                    TagColorIngredient.STREAM_CODEC.encode(buf, color.left().get());
                                } else {
                                    buf.writeBoolean(false);
                                    ItemColorIngredient.STREAM_CODEC.encode(buf, color.right().get());
                                }
                            } else {
                                buf.writeBoolean(false);
                                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, slot.right().get());
                            }
                        },
                        buf -> {
                            if (buf.readBoolean()) {
                                if (buf.readBoolean())
                                    return Either.left(Either.left(TagColorIngredient.STREAM_CODEC.decode(buf)));
                                else return Either.left(Either.right(ItemColorIngredient.STREAM_CODEC.decode(buf)));
                            }
                            return Either.right(Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
                        }
                );

        private static final StreamCodec<RegistryFriendlyByteBuf, HexColorShapelessRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    buf.writeUtf(recipe.group);
                    buf.writeEnum(recipe.category);
                    buf.writeVarInt(recipe.ingredients.size());
                    for (Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient> slot : recipe.ingredients)
                        SLOT_STREAM_CODEC.encode(buf, slot);
                    buf.writeVarInt(BuiltInRegistries.ITEM.getId(recipe.result));
                    buf.writeVarInt(recipe.count);
                },
                buf -> {
                    String group = buf.readUtf();
                    CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);

                    int count = buf.readVarInt();
                    List<Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient>> ingredients = new ArrayList<>(count);
                    for (int i = 0; i < count; i++)
                        ingredients.add(SLOT_STREAM_CODEC.decode(buf));

                    Item result = BuiltInRegistries.ITEM.byId(buf.readVarInt());
                    int outputCount = buf.readVarInt();
                    return new HexColorShapelessRecipe(group, category, ingredients, result, outputCount);
                }
        );

        @NotNull
        @Override
        public MapCodec<HexColorShapelessRecipe> codec() {
            return CODEC;
        }

        @NotNull
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HexColorShapelessRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
