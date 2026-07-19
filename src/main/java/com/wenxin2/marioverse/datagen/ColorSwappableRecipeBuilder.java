package com.wenxin2.marioverse.datagen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.wenxin2.marioverse.data.ColorSwappableShapedRecipe;
import com.wenxin2.marioverse.data.ItemColorIngredient;
import com.wenxin2.marioverse.data.TagColorIngredient;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class ColorSwappableRecipeBuilder implements RecipeBuilder {
    private final RecipeCategory category;
    private final Item result;
    private final List<String> rows = Lists.newArrayList();
    private final Map<String, Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient>> key = Maps.newLinkedHashMap();
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable private String group;
    private final int count;

    public ColorSwappableRecipeBuilder(RecipeCategory category, ItemLike result, int outputAmt) {
        this.category = category;
        this.result = result.asItem();
        this.count = outputAmt;
    }

    public static ColorSwappableRecipeBuilder shaped(RecipeCategory category, ItemLike result, int outputAmt) {
        return new ColorSwappableRecipeBuilder(category, result, outputAmt);
    }

    public ColorSwappableRecipeBuilder define(Character symbol, ItemLike item) {
        return this.defineEither(symbol, Either.right(Ingredient.of(item)));
    }

    public ColorSwappableRecipeBuilder define(Character symbol, TagKey<Item> tag) {
        return this.defineEither(symbol, Either.right(Ingredient.of(tag)));
    }

    public ColorSwappableRecipeBuilder defineColorTag(Character symbol, TagKey<Item> colorTag) {
        return this.defineEither(symbol, Either.left(Either.left(new TagColorIngredient(colorTag))));
    }

    public ColorSwappableRecipeBuilder defineColorItem(Character symbol, Item colorItem) {
        return this.defineEither(symbol, Either.left(Either.right(new ItemColorIngredient(colorItem))));
    }

    private ColorSwappableRecipeBuilder defineEither(Character symbol,
                                                     Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient> value) {
        if (this.key.containsKey(String.valueOf(symbol)))
            throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
        else if (symbol == ' ')
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");

        this.key.put(String.valueOf(symbol), value);
        return this;
    }

    public ColorSwappableRecipeBuilder pattern(String row) {
        if (!this.rows.isEmpty() && row.length() != this.rows.get(0).length())
            throw new IllegalArgumentException("Pattern must be the same width on every line!");

        this.rows.add(row);
        return this;
    }

    @Override
    public ColorSwappableRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public ColorSwappableRecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    @Override
    public Item getResult() {
        return this.result;
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        this.ensureValid(id);

        Advancement.Builder advancement = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement::addCriterion);

        ColorSwappableShapedRecipe recipe = new ColorSwappableShapedRecipe(Objects
                .requireNonNullElse(this.group, ""), RecipeBuilder.determineBookCategory(this.category),
                this.rows, this.key, this.result, this.count);

        output.accept(id, recipe, advancement.build(id.withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    private void ensureValid(ResourceLocation id) {
        if (this.criteria.isEmpty())
            throw new IllegalStateException("No way of obtaining recipe " + id);
    }
}
