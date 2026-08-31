package com.wenxin2.marioverse.datagen;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.wenxin2.marioverse.data.ArrowColorShapelessRecipe;
import com.wenxin2.marioverse.data.DyeColorIngredient;
import java.util.List;
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
import java.util.LinkedHashMap;
import java.util.Map;

/** Shapeless counterpart of ArrowColorRecipeBuilder, producing ArrowColorShapelessRecipe. */
public class ArrowColorShapelessRecipeBuilder implements RecipeBuilder {
    private final RecipeCategory category;
    private final Item result;
    private final int count;
    private final List<Either<DyeColorIngredient, Ingredient>> ingredients = Lists.newArrayList();
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable private String group;

    public ArrowColorShapelessRecipeBuilder(RecipeCategory category, ItemLike result, int outputAmt) {
        this.category = category;
        this.result = result.asItem();
        this.count = outputAmt;
    }

    public static ArrowColorShapelessRecipeBuilder shapeless(RecipeCategory category, ItemLike result, int outputAmt) {
        return new ArrowColorShapelessRecipeBuilder(category, result, outputAmt);
    }

    public ArrowColorShapelessRecipeBuilder requires(ItemLike item) {
        this.ingredients.add(Either.right(Ingredient.of(item)));
        return this;
    }

    public ArrowColorShapelessRecipeBuilder requires(TagKey<Item> tag) {
        this.ingredients.add(Either.right(Ingredient.of(tag)));
        return this;
    }

    public ArrowColorShapelessRecipeBuilder requiresColorDye(TagKey<Item> dyeTag) {
        this.ingredients.add(Either.left(new DyeColorIngredient(dyeTag)));
        return this;
    }

    @Override
    public ArrowColorShapelessRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public ArrowColorShapelessRecipeBuilder group(@Nullable String group) {
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

        ArrowColorShapelessRecipe recipe = new ArrowColorShapelessRecipe(Objects
                .requireNonNullElse(this.group, ""), RecipeBuilder.determineBookCategory(this.category),
                this.ingredients, this.result, this.count);

        output.accept(id, recipe, advancement.build(id.withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    private void ensureValid(ResourceLocation id) {
        if (this.criteria.isEmpty())
            throw new IllegalStateException("No way of obtaining recipe " + id);
    }
}
