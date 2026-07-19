package com.wenxin2.marioverse.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.neoforged.fml.loading.FMLEnvironment;

public final class TextureColorSampler {
    private static final Map<Item, Integer> CACHE = new IdentityHashMap<>();
    private static final Map<DyeColor, Integer> COLOR_OVERRIDES = new EnumMap<>(DyeColor.class);

    static {
        COLOR_OVERRIDES.put(DyeColor.RED, 0xFFF6343A);
        COLOR_OVERRIDES.put(DyeColor.BLUE, 0xFF325EFF);
        COLOR_OVERRIDES.put(DyeColor.LIGHT_BLUE, 0xFF89F4EB);
        COLOR_OVERRIDES.put(DyeColor.ORANGE, 0xFFFF992B);
        COLOR_OVERRIDES.put(DyeColor.PINK, 0xFFF4A1BD);
    }

    private TextureColorSampler() {
    }

    public static Integer woolColorOverride(DyeColor color) {
        return COLOR_OVERRIDES.get(color);
    }

    public static Integer averageColorOf(Item item) {
        return CACHE.computeIfAbsent(item, TextureColorSampler::compute);
    }

    private static Integer compute(Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);

        ResourceLocation itemTexture = ResourceLocation
                .fromNamespaceAndPath(id.getNamespace(), "textures/item/" + id.getPath() + ".png");
        Integer color = TextureColorSampler.sample(itemTexture);
        if (color != null)
            return color;

        ResourceLocation blockTexture = ResourceLocation
                .fromNamespaceAndPath(id.getNamespace(), "textures/block/" + id.getPath() + ".png");
        return TextureColorSampler.sample(blockTexture);
    }

    private static Integer sample(ResourceLocation texture) {
        try (InputStream stream = TextureColorSampler.openStream(texture)) {
            if (stream == null)
                return null;
            BufferedImage image = ImageIO.read(stream);
            if (image == null)
                return null;
            return TextureColorSampler.averagePixels(image);
        } catch (IOException e) {
            return null;
        }
    }

    private static InputStream openStream(ResourceLocation texture) throws IOException {
        if (FMLEnvironment.dist.isClient()) {
            Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(texture);
            if (resource.isPresent())
                return resource.get().open();
        }

        String jarPath = "assets/" + texture.getNamespace() + "/" + texture.getPath();
        return TextureColorSampler.class.getClassLoader().getResourceAsStream(jarPath);
    }

    private static Integer averagePixels(BufferedImage image) {
        double weightedR = 0, weightedG = 0, weightedB = 0;
        double totalWeight = 0;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int argb = image.getRGB(x, y);
                int alpha = (argb >>> 24) & 0xFF;
                if (alpha < 32) continue;

                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;

                double luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255.0;
                double weight = luminance * luminance;

                weightedR += r * weight;
                weightedG += g * weight;
                weightedB += b * weight;
                totalWeight += weight;
            }
        }

        if (totalWeight == 0)
            return null;

        int avgR = (int) Math.round(weightedR / totalWeight);
        int avgG = (int) Math.round(weightedG / totalWeight);
        int avgB = (int) Math.round(weightedB / totalWeight);

        return (avgR << 16) | (avgG << 8) | avgB;
    }
}