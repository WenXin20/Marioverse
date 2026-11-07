package com.wenxin2.marioverse.datagen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wenxin2.marioverse.data.ArrowSignUpgradeRecipe;
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

/** Shaped builder for ArrowSignUpgradeRecipe (arrow signs -> large arrow sign, components carried over). */
public class ArrowSignUpgradeRecipeBuilder implements RecipeBuilder {
    private final RecipeCategory category;
    private final Item result;
    private final List<String> rows = Lists.newArrayList();
    private final Map<String, Ingredient> key = Maps.newLinkedHashMap();
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable private String group;
    private final int count;

    public ArrowSignUpgradeRecipeBuilder(RecipeCategory category, ItemLike result, int outputAmt) {
        this.category = category;
        this.result = result.asItem();
        this.count = outputAmt;
    }

    public static ArrowSignUpgradeRecipeBuilder shaped(RecipeCategory category, ItemLike result, int outputAmt) {
        return new ArrowSignUpgradeRecipeBuilder(category, result, outputAmt);
    }

    public ArrowSignUpgradeRecipeBuilder define(Character symbol, ItemLike item) {
        return this.define(symbol, Ingredient.of(item));
    }

    public ArrowSignUpgradeRecipeBuilder define(Character symbol, TagKey<Item> tag) {
        return this.define(symbol, Ingredient.of(tag));
    }

    public ArrowSignUpgradeRecipeBuilder define(Character symbol, Ingredient ingredient) {
        if (this.key.containsKey(String.valueOf(symbol)))
            throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
        else if (symbol == ' ')
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");

        this.key.put(String.valueOf(symbol), ingredient);
        return this;
    }

    public ArrowSignUpgradeRecipeBuilder pattern(String row) {
        if (!this.rows.isEmpty() && row.length() != this.rows.get(0).length())
            throw new IllegalArgumentException("Pattern must be the same width on every line!");

        this.rows.add(row);
        return this;
    }

    @Override
    public ArrowSignUpgradeRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public ArrowSignUpgradeRecipeBuilder group(@Nullable String group) {
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

        ArrowSignUpgradeRecipe recipe = new ArrowSignUpgradeRecipe(Objects
                .requireNonNullElse(this.group, ""), RecipeBuilder.determineBookCategory(this.category),
                this.rows, this.key, this.result, this.count);

        output.accept(id, recipe, advancement.build(id.withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    private void ensureValid(ResourceLocation id) {
        if (this.criteria.isEmpty())
            throw new IllegalStateException("No way of obtaining recipe " + id);
    }
}
