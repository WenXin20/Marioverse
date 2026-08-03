package com.wenxin2.marioverse.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wenxin2.marioverse.registries.RecipeSerializerRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.*;
import org.jetbrains.annotations.NotNull;

public class HexColorShapedRecipe implements CraftingRecipe {
    private final Map<String, Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient>> key;
    private final List<Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient>> slots;
    private final CraftingBookCategory category;
    private final String group;
    private final List<String> pattern;
    private final Item result;
    private final int width;
    private final int height;
    private final int count;

    public HexColorShapedRecipe(String group, CraftingBookCategory category, List<String> pattern,
                                Map<String, Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient>> key,
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
                    Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient> entry = key.get(String.valueOf(c));
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

        Map<Object, Integer> resolvedColors = new IdentityHashMap<>();

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                ItemStack stack = input.getItem(x, y);
                Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient> slot = this.slots.get(y * this.width + x);

                boolean ok = slot.map(colorIngredient -> colorIngredient.map(tag -> {
                                    if (!tag.test(stack))
                                        return false;

                                    Integer color = tag.colorOf(stack);
                                    if (color == null)
                                        return false;

                                    Integer existing = resolvedColors.putIfAbsent(tag, color);
                                    return existing == null || existing.equals(color);
                                },
                                item -> {
                                    if (!item.test(stack))
                                        return false;

                                    Integer color = item.colorOf(stack);
                                    if (color == null)
                                        return false;

                                    Integer existing = resolvedColors.putIfAbsent(item, color);
                                    return existing == null || existing.equals(color);
                                }), ingredient -> ingredient.test(stack)
                );
                if (!ok)
                    return false;
            }
        }
        return true;
    }

    @NotNull
    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        Integer found = null;

        outer:
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient> slot = this.slots.get(y * this.width + x);
                if (slot.left().isPresent()) {
                    int finalTagX = x;
                    int finalTagY = y;
                    int finalX = x;
                    int finalY = y;
                    Integer color = slot.left().get()
                            .map(tag -> tag.colorOf(input.getItem(finalTagX, finalTagY)),
                                    item -> item.colorOf(input.getItem(finalX, finalY)));

                    if (color != null) {
                        found = color;
                        break outer;
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
            Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient> slot = this.slots.get(i);
            flattened.set(i, slot
                    .map(color -> color.map(TagColorIngredient::toIngredient, item -> Ingredient.of(item.item())),
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
        return RecipeSerializerRegistry.HEX_COLOR_SHAPED.get();
    }

    public static class Serializer implements RecipeSerializer<HexColorShapedRecipe> {
        private static final Codec<Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient>> SLOT_CODEC =
                Codec.either(Codec.either(TagColorIngredient.CODEC, ItemColorIngredient.CODEC),
                        Ingredient.CODEC);

        private static final MapCodec<HexColorShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(r -> r.group),
                        CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(r -> r.category),
                        Codec.STRING.listOf().fieldOf("pattern").forGetter(r -> r.pattern),
                        Codec.unboundedMap(Codec.STRING, SLOT_CODEC).fieldOf("key").forGetter(r -> r.key),
                        BuiltInRegistries.ITEM.byNameCodec().fieldOf("result").forGetter(r -> r.result),
                        Codec.INT.optionalFieldOf("count", 1).forGetter(r -> r.count)
                ).apply(instance, HexColorShapedRecipe::new)
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

        private static final StreamCodec<RegistryFriendlyByteBuf, HexColorShapedRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    buf.writeUtf(recipe.group);
                    buf.writeEnum(recipe.category);
                    buf.writeVarInt(recipe.pattern.size());
                    for (String row : recipe.pattern) buf.writeUtf(row);

                    buf.writeVarInt(recipe.key.size());
                    for (Map.Entry<String, Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient>> e : recipe.key.entrySet()) {
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
                    Map<String, Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient>> key = new HashMap<>();
                    for (int i = 0; i < keyCount; i++) {
                        String k = buf.readUtf();
                        key.put(k, SLOT_STREAM_CODEC.decode(buf));
                    }

                    Item result = BuiltInRegistries.ITEM.byId(buf.readVarInt());
                    int count = buf.readVarInt();
                    return new HexColorShapedRecipe(group, category, pattern, key, result, count);
                }
        );

        @NotNull
        @Override
        public MapCodec<HexColorShapedRecipe> codec() {
            return CODEC;
        }

        @NotNull
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HexColorShapedRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public List<Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient>> getSlots() {
        return this.slots;
    }
}