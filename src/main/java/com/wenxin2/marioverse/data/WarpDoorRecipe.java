package com.wenxin2.marioverse.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wenxin2.marioverse.event_handlers.RegistryEventHandlers;
import com.wenxin2.marioverse.registries.RecipeSerializerRegistry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.util.RecipeMatcher;
import org.jetbrains.annotations.NotNull;

public class WarpDoorRecipe implements CraftingRecipe {
    private final Kind kind;
    private final String group;
    private final CraftingBookCategory category;
    private final List<String> pattern;
    private final Map<String, Ingredient> key;
    private final List<Ingredient> ingredients;
    private final Optional<Item> result;
    private final int count;
    private final boolean enabled;
    private final boolean shaped;
    private final int width;
    private final int height;
    private final List<Ingredient> slots;
    private int sourceIndex = -2;
    private List<Block> sources;
    private List<Block> warps;

    public WarpDoorRecipe(Kind kind, String group, CraftingBookCategory category, List<String> pattern,
                          Map<String, Ingredient> key, List<Ingredient> ingredients,
                          Optional<Item> result, int count, boolean enabled) {
        this.kind = kind;
        this.group = group;
        this.category = category;
        this.pattern = pattern;
        this.key = key;
        this.ingredients = ingredients;
        this.result = result;
        this.count = count;
        this.enabled = enabled;
        this.shaped = !pattern.isEmpty();

        if (this.shaped == !ingredients.isEmpty())
            throw new IllegalArgumentException("Warp door recipe must define either \"pattern\" and \"key\" or \"ingredients\"");

        if (this.shaped) {
            this.height = pattern.size();
            this.width = pattern.getFirst().length();
            if (this.width > 3 || this.height > 3)
                throw new IllegalArgumentException("Warp door recipe pattern must be at most 3x3");

            this.slots = new ArrayList<>(this.width * this.height);
            for (String row : pattern) {
                if (row.length() != this.width)
                    throw new IllegalArgumentException("Warp door recipe pattern must be the same width on every line");

                for (int x = 0; x < row.length(); x++) {
                    char c = row.charAt(x);
                    if (c == ' ') {
                        this.slots.add(Ingredient.EMPTY);
                        continue;
                    }

                    Ingredient entry = key.get(String.valueOf(c));
                    if (entry == null)
                        throw new IllegalArgumentException("Pattern references undefined key '" + c + "'");
                    this.slots.add(entry);
                }
            }
        } else {
            this.width = 0;
            this.height = 0;
            if (ingredients.size() > 9)
                throw new IllegalArgumentException("Warp door recipe has too many ingredients");
            this.slots = ingredients;
        }
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        ItemStack[] matched = this.matchSlots(input);
        if (matched == null)
            return false;
        return this.result.isPresent() || this.convert(matched) != null;
    }

    @NotNull
    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack[] matched = this.matchSlots(input);
        if (matched == null)
            return ItemStack.EMPTY;

        if (this.result.isPresent())
            return new ItemStack(this.result.get(), this.count);

        Block warp = this.convert(matched);
        return warp == null ? ItemStack.EMPTY : new ItemStack(warp, this.count);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return this.shaped ? width >= this.width && height >= this.height : width * height >= this.slots.size();
    }

    @NotNull
    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        if (this.result.isPresent())
            return new ItemStack(this.result.get(), this.count);
        List<Block> outputs = this.getWarps();
        return outputs.isEmpty() ? ItemStack.EMPTY : new ItemStack(outputs.getFirst(), this.count);
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
        return this.kind.serializer();
    }

    public static void removeReplacedRecipes(RecipeManager manager) {
        Collection<RecipeHolder<?>> recipes = manager.getRecipes();
        Set<Kind> replaced = EnumSet.noneOf(Kind.class);
        boolean changed = false;

        for (RecipeHolder<?> holder : recipes) {
            if (!(holder.value() instanceof WarpDoorRecipe recipe))
                continue;
            if (!recipe.enabled)
                changed = true;
            else if (recipe.isGeneric())
                replaced.add(recipe.kind);
        }

        if (!changed && replaced.isEmpty())
            return;

        List<RecipeHolder<?>> kept = new ArrayList<>(recipes.size());
        for (RecipeHolder<?> holder : recipes) {
            if (holder.value() instanceof WarpDoorRecipe recipe
                    && (!recipe.enabled || (!recipe.isGeneric() && replaced.contains(recipe.kind))))
                continue;
            kept.add(holder);
        }

        manager.replaceRecipes(kept);
    }

    public Kind getKind() {
        return this.kind;
    }

    public boolean isShaped() {
        return this.shaped;
    }

    public boolean isGeneric() {
        return this.result.isEmpty();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getSourceIndex() {
        if (this.sourceIndex == -2)
            this.cacheConversions();
        return this.sourceIndex;
    }

    public List<ItemStack> getSourceStacks() {
        return this.getSources().stream().map(ItemStack::new).toList();
    }

    public List<ItemStack> getOutputStacks() {
        if (this.result.isPresent())
            return List.of(new ItemStack(this.result.get(), this.count));
        return this.getWarps().stream().map(warp -> new ItemStack(warp, this.count)).toList();
    }

    private List<Block> getSources() {
        if (this.sourceIndex == -2)
            this.cacheConversions();
        return this.sources;
    }

    private List<Block> getWarps() {
        if (this.sourceIndex == -2)
            this.cacheConversions();
        return this.warps;
    }

    private void cacheConversions() {
        List<Block> sourceList = new ArrayList<>();
        List<Block> warpList = new ArrayList<>();
        int index = -1;

        if (this.result.isEmpty()) {
            for (int i = 0; i < this.slots.size() && index == -1; i++) {
                Ingredient slot = this.slots.get(i);
                if (slot.isEmpty())
                    continue;

                for (Map.Entry<Block, Block> entry : this.kind.conversions().entrySet()) {
                    if (!slot.test(new ItemStack(entry.getKey())))
                        continue;
                    sourceList.add(entry.getKey());
                    warpList.add(entry.getValue());
                    index = i;
                }
            }
        }

        this.sources = sourceList;
        this.warps = warpList;
        this.sourceIndex = index;
    }

    private Block convert(ItemStack[] matched) {
        int index = this.getSourceIndex();
        if (index < 0 || !(matched[index].getItem() instanceof BlockItem blockItem))
            return null;
        return this.kind.conversions().get(blockItem.getBlock());
    }

    private ItemStack[] matchSlots(CraftingInput input) {
        return this.shaped ? this.matchShaped(input) : this.matchShapeless(input);
    }

    private ItemStack[] matchShaped(CraftingInput input) {
        if (input.width() != this.width || input.height() != this.height)
            return null;

        ItemStack[] matched = this.matchShaped(input, false);
        return matched != null ? matched : this.matchShaped(input, true);
    }

    private ItemStack[] matchShaped(CraftingInput input, boolean mirrored) {
        ItemStack[] matched = new ItemStack[this.slots.size()];
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                int index = y * this.width + x;
                ItemStack stack = input.getItem(mirrored ? this.width - 1 - x : x, y);
                if (!this.slots.get(index).test(stack))
                    return null;
                matched[index] = stack;
            }
        }
        return matched;
    }

    private ItemStack[] matchShapeless(CraftingInput input) {
        List<ItemStack> nonEmpty = new ArrayList<>();
        for (ItemStack stack : input.items())
            if (!stack.isEmpty())
                nonEmpty.add(stack);

        if (nonEmpty.size() != this.slots.size())
            return null;

        List<Predicate<ItemStack>> tests = new ArrayList<>(this.slots);
        int[] assignment = RecipeMatcher.findMatches(nonEmpty, tests);
        if (assignment == null)
            return null;

        ItemStack[] matched = new ItemStack[this.slots.size()];
        for (int i = 0; i < assignment.length; i++)
            matched[assignment[i]] = nonEmpty.get(i);
        return matched;
    }

    public enum Kind {
        DOOR,
        TRAPDOOR;

        public Map<Block, Block> conversions() {
            return this == DOOR ? RegistryEventHandlers.WARP_DOORS : RegistryEventHandlers.WARP_TRAPDOORS;
        }

        public RecipeSerializer<WarpDoorRecipe> serializer() {
            return this == DOOR ? RecipeSerializerRegistry.WARP_DOOR.get() : RecipeSerializerRegistry.WARP_TRAPDOOR.get();
        }
    }

    public static class Serializer implements RecipeSerializer<WarpDoorRecipe> {
        private final MapCodec<WarpDoorRecipe> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, WarpDoorRecipe> streamCodec;

        public Serializer(Kind kind) {
            this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.STRING.optionalFieldOf("group", "").forGetter(r -> r.group),
                    CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.BUILDING).forGetter(r -> r.category),
                    Codec.STRING.listOf().optionalFieldOf("pattern", List.of()).forGetter(r -> r.pattern),
                    Codec.unboundedMap(Codec.STRING, Ingredient.CODEC_NONEMPTY).optionalFieldOf("key", Map.of()).forGetter(r -> r.key),
                    Ingredient.CODEC_NONEMPTY.listOf().optionalFieldOf("ingredients", List.of()).forGetter(r -> r.ingredients),
                    BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("result").forGetter(r -> r.result),
                    Codec.INT.optionalFieldOf("count", 1).forGetter(r -> r.count),
                    Codec.BOOL.optionalFieldOf("enabled", true).forGetter(r -> r.enabled)
            ).apply(instance, (group, category, pattern, key, ingredients, result, count, enabled) ->
                    new WarpDoorRecipe(kind, group, category, pattern, key, ingredients, result, count, enabled)));

            this.streamCodec = StreamCodec.of(
                    (buf, recipe) -> {
                        buf.writeUtf(recipe.group);
                        buf.writeEnum(recipe.category);

                        buf.writeVarInt(recipe.pattern.size());
                        for (String row : recipe.pattern)
                            buf.writeUtf(row);

                        buf.writeVarInt(recipe.key.size());
                        for (Map.Entry<String, Ingredient> entry : recipe.key.entrySet()) {
                            buf.writeUtf(entry.getKey());
                            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, entry.getValue());
                        }

                        buf.writeVarInt(recipe.ingredients.size());
                        for (Ingredient ingredient : recipe.ingredients)
                            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);

                        buf.writeBoolean(recipe.result.isPresent());
                        recipe.result.ifPresent(item -> buf.writeVarInt(BuiltInRegistries.ITEM.getId(item)));
                        buf.writeVarInt(recipe.count);
                        buf.writeBoolean(recipe.enabled);
                    },
                    buf -> {
                        String group = buf.readUtf();
                        CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);

                        int rowCount = buf.readVarInt();
                        List<String> pattern = new ArrayList<>(rowCount);
                        for (int i = 0; i < rowCount; i++)
                            pattern.add(buf.readUtf());

                        int keyCount = buf.readVarInt();
                        Map<String, Ingredient> key = new HashMap<>();
                        for (int i = 0; i < keyCount; i++) {
                            String symbol = buf.readUtf();
                            key.put(symbol, Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
                        }

                        int ingredientCount = buf.readVarInt();
                        List<Ingredient> ingredients = new ArrayList<>(ingredientCount);
                        for (int i = 0; i < ingredientCount; i++)
                            ingredients.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buf));

                        Optional<Item> result = buf.readBoolean()
                                ? Optional.of(BuiltInRegistries.ITEM.byId(buf.readVarInt())) : Optional.empty();
                        int count = buf.readVarInt();
                        boolean enabled = buf.readBoolean();
                        return new WarpDoorRecipe(kind, group, category, pattern, key, ingredients, result, count, enabled);
                    }
            );
        }

        @NotNull
        @Override
        public MapCodec<WarpDoorRecipe> codec() {
            return this.codec;
        }

        @NotNull
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, WarpDoorRecipe> streamCodec() {
            return this.streamCodec;
        }
    }
}
