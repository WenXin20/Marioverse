package com.wenxin2.marioverse.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wenxin2.marioverse.blocks.states.ArrowDirection;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import com.wenxin2.marioverse.registries.RecipeSerializerRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.jetbrains.annotations.NotNull;

public class ArrowSignUpgradeRecipe implements CraftingRecipe {
    private final Map<String, Ingredient> key;
    private final List<Ingredient> slots;
    private final CraftingBookCategory category;
    private final String group;
    private final List<String> pattern;
    private final Item result;
    private final int width;
    private final int height;
    private final int count;

    public ArrowSignUpgradeRecipe(String group, CraftingBookCategory category, List<String> pattern,
                                   Map<String, Ingredient> key, Item result, int count) {
        this.category = category;
        this.group = group;
        this.pattern = pattern;
        this.key = key;
        this.result = result;
        this.count = count;

        this.height = pattern.size();
        this.width = pattern.isEmpty() ? 0 : pattern.get(0).length();
        this.slots = new ArrayList<>(this.width * this.height);

        for (String row : pattern) {
            for (int x = 0; x < row.length(); x++) {
                char c = row.charAt(x);
                if (c == ' ') {
                    this.slots.add(Ingredient.EMPTY);
                } else {
                    Ingredient entry = key.get(String.valueOf(c));
                    if (entry == null)
                        throw new IllegalArgumentException("Pattern references undefined key '" + c + "'");
                    this.slots.add(entry);
                }
            }
        }
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.width() != this.width || input.height() != this.height)
            return false;

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                if (!this.slots.get(y * this.width + x).test(input.getItem(x, y)))
                    return false;
            }
        }
        return true;
    }

    @NotNull
    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack output = new ItemStack(this.result, this.count);

        for (ItemStack stack : input.items()) {
            if (stack.isEmpty())
                continue;

            output.set(DataComponentRegistry.DYE_COLOR.get(),
                    stack.getOrDefault(DataComponentRegistry.DYE_COLOR.get(), DyeColor.RED));
            output.set(DataComponentRegistry.ARROW_SIGN_DIRECTION.get(),
                    stack.getOrDefault(DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.UP));
            output.set(DataComponentRegistry.GLOWING.get(),
                    stack.getOrDefault(DataComponentRegistry.GLOWING.get(), false));
            break;
        }

        return output;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= this.width && height >= this.height;
    }

    @NotNull
    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(this.result, this.count);
    }

    @NotNull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> flattened = NonNullList.withSize(this.slots.size(), Ingredient.EMPTY);
        for (int i = 0; i < this.slots.size(); i++)
            flattened.set(i, this.slots.get(i));
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
        return RecipeSerializerRegistry.ARROW_SIGN_UPGRADE.get();
    }

    public static class Serializer implements RecipeSerializer<ArrowSignUpgradeRecipe> {
        private static final MapCodec<ArrowSignUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(r -> r.group),
                        CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(r -> r.category),
                        Codec.STRING.listOf().fieldOf("pattern").forGetter(r -> r.pattern),
                        Codec.unboundedMap(Codec.STRING, Ingredient.CODEC).fieldOf("key").forGetter(r -> r.key),
                        BuiltInRegistries.ITEM.byNameCodec().fieldOf("result").forGetter(r -> r.result),
                        Codec.INT.optionalFieldOf("count", 1).forGetter(r -> r.count)
                ).apply(instance, ArrowSignUpgradeRecipe::new)
        );

        private static final StreamCodec<RegistryFriendlyByteBuf, ArrowSignUpgradeRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    buf.writeUtf(recipe.group);
                    buf.writeEnum(recipe.category);
                    buf.writeVarInt(recipe.pattern.size());
                    for (String row : recipe.pattern) buf.writeUtf(row);

                    buf.writeVarInt(recipe.key.size());
                    for (Map.Entry<String, Ingredient> e : recipe.key.entrySet()) {
                        buf.writeUtf(e.getKey());
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, e.getValue());
                    }

                    buf.writeVarInt(BuiltInRegistries.ITEM.getId(recipe.result));
                    buf.writeVarInt(recipe.count);
                },
                buf -> {
                    String group = buf.readUtf();
                    CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);

                    int rowCount = buf.readVarInt();
                    List<String> pattern = new ArrayList<>(rowCount);
                    for (int i = 0; i < rowCount; i++) pattern.add(buf.readUtf());

                    int keyCount = buf.readVarInt();
                    Map<String, Ingredient> key = new HashMap<>();
                    for (int i = 0; i < keyCount; i++) {
                        String k = buf.readUtf();
                        key.put(k, Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
                    }

                    Item result = BuiltInRegistries.ITEM.byId(buf.readVarInt());
                    int count = buf.readVarInt();
                    return new ArrowSignUpgradeRecipe(group, category, pattern, key, result, count);
                }
        );

        @NotNull
        @Override
        public MapCodec<ArrowSignUpgradeRecipe> codec() {
            return CODEC;
        }

        @NotNull
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ArrowSignUpgradeRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public List<Ingredient> getSlots() {
        return this.slots;
    }

    public List<String> getPattern() {
        return this.pattern;
    }

    public Map<String, Ingredient> getKey() {
        return this.key;
    }
}
