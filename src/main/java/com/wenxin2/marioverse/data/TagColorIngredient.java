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

public record TagColorIngredient(TagKey<Item> tag) {
    public static final Codec<TagColorIngredient> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(TagKey.codec(Registries.ITEM).fieldOf("color_tag").forGetter(TagColorIngredient::tag))
            .apply(instance, TagColorIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TagColorIngredient> STREAM_CODEC = StreamCodec
            .of((buf, ci) -> buf.writeResourceLocation(ci.tag().location()),
            buf -> new TagColorIngredient(TagKey.create(net.minecraft.core.registries.Registries.ITEM, buf.readResourceLocation())));

    private static final Map<Item, Integer> TEXTURE_COLOR_CACHE = new IdentityHashMap<>();
    private static final Map<DyeColor, Integer> COLOR_OVERRIDES = new EnumMap<>(DyeColor.class);

    static {
        COLOR_OVERRIDES.put(DyeColor.RED, 0xFFF6343A);
        COLOR_OVERRIDES.put(DyeColor.BLUE, 0xFF325EFF);
        COLOR_OVERRIDES.put(DyeColor.LIGHT_BLUE, 0xFF89F4EB);
        COLOR_OVERRIDES.put(DyeColor.ORANGE, 0xFFFF992B);
        COLOR_OVERRIDES.put(DyeColor.PINK, 0xFFF4A1BD);
    }

    public boolean test(ItemStack stack) {
        return stack.is(this.tag);
    }

    public Integer colorOf(ItemStack stack) {
        if (stack.isEmpty())
            return null;

        Integer dyed = this.colorFromDyedComponent(stack);
        if (dyed != null)
            return dyed;

        if (this.tag.equals(ItemTags.WOOL)) {
            DyeColor named = this.colorFromName(stack);
            if (named != null) {
                Integer override = COLOR_OVERRIDES.get(named);
                return override != null ? override : named.getTextureDiffuseColor();
            }
            return null;
        }
        return TagColorIngredient.averageColorFromTexture(stack.getItem());
    }

    private DyeColor colorFromName(ItemStack stack) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        String[] pathSegments = id.getPath().split("_");

        DyeColor best = null;
        int bestSegmentCount = 0;

        for (DyeColor color : DyeColor.values()) {
            String[] colorSegments = color.getName().split("_");
            if (colorSegments.length <= bestSegmentCount) continue;

            if (TagColorIngredient.containsSubsequence(pathSegments, colorSegments)) {
                best = color;
                bestSegmentCount = colorSegments.length;
            }
        }

        return best;
    }

    private Integer colorFromDyedComponent(ItemStack stack) {
        DyedItemColor dyed = stack.get(DataComponents.DYED_COLOR);
        return dyed != null ? dyed.rgb() : null;
    }

    private static Integer averageColorFromTexture(Item item) {
        return TEXTURE_COLOR_CACHE.computeIfAbsent(item, TagColorIngredient::computeAverageColorFromTexture);
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
        try (InputStream stream = TagColorIngredient.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null)
                return null;
            BufferedImage image = ImageIO.read(stream);
            if (image == null)
                return null;

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

            if (count == 0)
                return null;
            return ((int) (r / count) << 16) | ((int) (g / count) << 8) | (int) (b / count);
        } catch (IOException e) {
            return null;
        }
    }

    private static boolean containsSubsequence(String[] haystack, String[] needle) {
        outer:
        for (int i = 0; i <= haystack.length - needle.length; i++) {
            for (int j = 0; j < needle.length; j++) {
                if (!haystack[i + j].equals(needle[j])) continue outer;
            }
            return true;
        }
        return false;
    }

    public Ingredient toIngredient() {
        return Ingredient.of(this.tag);
    }
}