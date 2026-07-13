package com.wenxin2.marioverse.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.Ingredient;

public record ItemColorIngredient(Item item) {
    public static final Codec<ItemColorIngredient> CODEC = RecordCodecBuilder
            .create(instance -> instance.group(BuiltInRegistries.ITEM.byNameCodec().fieldOf("color_item").forGetter(ItemColorIngredient::item))
                    .apply(instance, ItemColorIngredient::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemColorIngredient> STREAM_CODEC = StreamCodec
            .of((buf, ingredient) -> buf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(ingredient.item())),
                    buf -> new ItemColorIngredient(BuiltInRegistries.ITEM.get(buf.readResourceLocation())));

    private static final Map<Item, Integer> TEXTURE_COLOR_CACHE = new IdentityHashMap<>();

    public boolean test(ItemStack stack) {
        return stack.is(this.item);
    }

    public Integer colorOf(ItemStack stack) {
        if (stack.isEmpty())
            return null;

        Integer dyed = this.colorFromDyedComponent(stack);
        if (dyed != null)
            return dyed;

        return ItemColorIngredient.averageColorFromTexture(stack.getItem());
    }

    private Integer colorFromDyedComponent(ItemStack stack) {
        DyedItemColor dyed = stack.get(DataComponents.DYED_COLOR);
        return dyed != null ? dyed.rgb() : null;
    }

    private static Integer averageColorFromTexture(Item item) {
        return TEXTURE_COLOR_CACHE.computeIfAbsent(item, ItemColorIngredient::computeAverageColorFromTexture);
    }

    private static Integer computeAverageColorFromTexture(Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);

        String itemPath = "assets/" + id.getNamespace() + "/textures/item/" + id.getPath() + ".png";
        Integer color = sampleTexture(itemPath);
        if (color != null)
            return color;

        String blockPath = "assets/" + id.getNamespace() + "/textures/block/" + id.getPath() + ".png";
        return sampleTexture(blockPath);
    }

    private static Integer sampleTexture(String path) {
        try (InputStream stream = ItemColorIngredient.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) return null;
            BufferedImage image = ImageIO.read(stream);
            if (image == null) return null;

            long r = 0, g = 0, b = 0;
            int count = 0;

            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int argb = image.getRGB(x, y);
                    int alpha = (argb >>> 24) & 0xFF;
                    if (alpha < 32) continue;

                    r += (argb >> 16) & 0xFF;
                    g += (argb >> 8) & 0xFF;
                    b += argb & 0xFF;
                    count++;
                }
            }

            if (count == 0) return null;
            return ((int) (r / count) << 16) | ((int) (g / count) << 8) | (int) (b / count);
        } catch (IOException e) {
            return null;
        }
    }

    public Ingredient toIngredient() {
        return Ingredient.of(this.item);
    }
}