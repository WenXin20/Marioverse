package com.wenxin2.marioverse.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import com.wenxin2.marioverse.registries.RecipeSerializerRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
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

/** Shaped counterpart of HexColorShapedRecipe: sets ARROW_SIGN_DYE_COLOR instead of DYED_COLOR. */
public class ArrowColorShapedRecipe implements CraftingRecipe {
    private final Map<String, Either<DyeColorIngredient, Ingredient>> key;
    private final List<Either<DyeColorIngredient, Ingredient>> slots;
    private final CraftingBookCategory category;
    private final String group;
    private final List<String> pattern;
    private final Item result;
    private final int width;
    private final int height;
    private final int count;

    public ArrowColorShapedRecipe(String group, CraftingBookCategory category, List<String> pattern,
                                   Map<String, Either<DyeColorIngredient, Ingredient>> key,
                                   Item result, int count) {
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
                    this.slots.add(Either.right(Ingredient.EMPTY));
                } else {
                    Either<DyeColorIngredient, Ingredient> entry = key.get(String.valueOf(c));
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

        Map<Object, DyeColor> resolvedColors = new IdentityHashMap<>();

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                ItemStack stack = input.getItem(x, y);
                Either<DyeColorIngredient, Ingredient> slot = this.slots.get(y * this.width + x);

                boolean ok = slot.map(dye -> {
                    if (!dye.test(stack))
                        return false;

                    DyeColor color = dye.colorOf(stack);
                    if (color == null)
                        return false;

                    DyeColor existing = resolvedColors.putIfAbsent(dye, color);
                    return existing == null || existing == color;
                }, ingredient -> ingredient.test(stack));

                if (!ok)
                    return false;
            }
        }
        return true;
    }

    @NotNull
    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        DyeColor found = null;

        outer:
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                Either<DyeColorIngredient, Ingredient> slot = this.slots.get(y * this.width + x);
                if (slot.left().isPresent()) {
                    DyeColor color = slot.left().get().colorOf(input.getItem(x, y));
                    if (color != null) {
                        found = color;
                        break outer;
                    }
                }
            }
        }

        ItemStack output = new ItemStack(this.result, this.count);
        if (found != null)
            output.set(DataComponentRegistry.DYE_COLOR.get(), found);
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
        for (int i = 0; i < this.slots.size(); i++) {
            Either<DyeColorIngredient, Ingredient> slot = this.slots.get(i);
            flattened.set(i, slot.map(DyeColorIngredient::toIngredient, ingredient -> ingredient));
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
        return RecipeSerializerRegistry.ARROW_COLOR_SHAPED.get();
    }

    public static class Serializer implements RecipeSerializer<ArrowColorShapedRecipe> {
        private static final Codec<Either<DyeColorIngredient, Ingredient>> SLOT_CODEC =
                Codec.either(DyeColorIngredient.CODEC, Ingredient.CODEC);

        private static final MapCodec<ArrowColorShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(r -> r.group),
                        CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(r -> r.category),
                        Codec.STRING.listOf().fieldOf("pattern").forGetter(r -> r.pattern),
                        Codec.unboundedMap(Codec.STRING, SLOT_CODEC).fieldOf("key").forGetter(r -> r.key),
                        BuiltInRegistries.ITEM.byNameCodec().fieldOf("result").forGetter(r -> r.result),
                        Codec.INT.optionalFieldOf("count", 1).forGetter(r -> r.count)
                ).apply(instance, ArrowColorShapedRecipe::new)
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

        private static final StreamCodec<RegistryFriendlyByteBuf, ArrowColorShapedRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    buf.writeUtf(recipe.group);
                    buf.writeEnum(recipe.category);
                    buf.writeVarInt(recipe.pattern.size());
                    for (String row : recipe.pattern) buf.writeUtf(row);

                    buf.writeVarInt(recipe.key.size());
                    for (Map.Entry<String, Either<DyeColorIngredient, Ingredient>> e : recipe.key.entrySet()) {
                        buf.writeUtf(e.getKey());
                        SLOT_STREAM_CODEC.encode(buf, e.getValue());
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
                    Map<String, Either<DyeColorIngredient, Ingredient>> key = new HashMap<>();
                    for (int i = 0; i < keyCount; i++) {
                        String k = buf.readUtf();
                        key.put(k, SLOT_STREAM_CODEC.decode(buf));
                    }

                    Item result = BuiltInRegistries.ITEM.byId(buf.readVarInt());
                    int count = buf.readVarInt();
                    return new ArrowColorShapedRecipe(group, category, pattern, key, result, count);
                }
        );

        @NotNull
        @Override
        public MapCodec<ArrowColorShapedRecipe> codec() {
            return CODEC;
        }

        @NotNull
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ArrowColorShapedRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public List<Either<DyeColorIngredient, Ingredient>> getSlots() {
        return this.slots;
    }

    public List<String> getPattern() {
        return this.pattern;
    }

    public Map<String, Either<DyeColorIngredient, Ingredient>> getKey() {
        return this.key;
    }
}
