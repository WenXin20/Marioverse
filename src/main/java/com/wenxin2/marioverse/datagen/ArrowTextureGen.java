package com.wenxin2.marioverse.datagen;

import com.google.common.hash.Hashing;
import com.mojang.serialization.JsonOps;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.states.ArrowDirection;
import com.wenxin2.marioverse.data.ArrowPalette;
import com.wenxin2.marioverse.registries.ArrowPaletteRegistry;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import javax.imageio.ImageIO;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

/** Recolors the grayscale arrow patterns per DyeColor via each color's palette, armor-trim style. */
public class ArrowTextureGen implements DataProvider {
    private record TemplateSet(String patternDir, String outputDir, String outputPrefix, int padWidth, int padHeight) {
        boolean padToCanvas() {
            return padWidth > 0;
        }
    }

    private static final TemplateSet[] TEMPLATE_SETS = {
            new TemplateSet("assets/marioverse/textures/entity/signs/arrow/pattern", "entity/signs/arrow", "", 64, 32),
            new TemplateSet("assets/marioverse/textures/entity/signs/large_arrow/pattern", "entity/signs/large_arrow", "large_", 64, 64),
            new TemplateSet("assets/marioverse/textures/item/arrow_sign/pattern", "item/arrow_sign", "", 0, 0),
            new TemplateSet("assets/marioverse/textures/item/large_arrow_sign/pattern", "item/large_arrow_sign", "large_", 0, 0),
    };

    private static final int[] GRAY_LEVELS = {0, 64, 128, 191, 255};

    private final PackOutput.PathProvider pathProvider;
    private final PackOutput.PathProvider paletteJsonProvider;

    public ArrowTextureGen(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "textures");
        // Own path, not Registries.elementsDirPath, so this lands at data/marioverse/palettes/arrow (no doubled namespace).
        this.paletteJsonProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "palettes/arrow");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return CompletableFuture.runAsync(() -> {
            try {
                this.generateAll(cache);
            } catch (IOException e) {
                throw new RuntimeException("Failed to generate arrow overlay textures", e);
            }
        });
    }

    private void generateAll(CachedOutput cache) throws IOException {
        for (DyeColor dyeColor : DyeColor.values()) {
            String colorName = dyeColor.getSerializedName();
            ArrowPalette arrowPalette = ArrowPaletteRegistry.defaultPalette(dyeColor);
            int[] palette = readPalette(arrowPalette);

            writePaletteJson(cache, arrowPalette,
                    this.paletteJsonProvider.json(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, colorName)));

            for (TemplateSet set : TEMPLATE_SETS) {
                for (ArrowDirection direction : ArrowDirection.values()) {
                    if (direction == ArrowDirection.NONE)
                        continue;

                    BufferedImage pattern = readImage(set.patternDir() + "/" + direction.getSerializedName() + ".png");
                    BufferedImage recolored = recolor(pattern, palette);
                    if (set.padToCanvas())
                        recolored = padToCanvas(recolored, set.padWidth(), set.padHeight());

                    String fileName = set.outputPrefix() + colorName + "_arrow_" + direction.getSerializedName();
                    ResourceLocation outputLocation = ResourceLocation.fromNamespaceAndPath(
                            Marioverse.MOD_ID, set.outputDir() + "/" + fileName);
                    writeImage(cache, recolored, this.pathProvider.file(outputLocation, "png"));
                }
            }
        }
    }

    private static int[] readPalette(ArrowPalette palette) throws IOException {
        ResourceLocation texture = palette.texture();
        BufferedImage paletteImage = readImage("assets/" + texture.getNamespace() + "/" + texture.getPath());
        int[] colors = new int[GRAY_LEVELS.length];
        for (int i = 0; i < colors.length; i++)
            colors[i] = paletteImage.getRGB(i, 0);
        return colors;
    }

    private static void writePaletteJson(CachedOutput cache, ArrowPalette palette, Path path) {
        DataProvider.saveStable(cache, ArrowPalette.CODEC.encodeStart(JsonOps.INSTANCE, palette).getOrThrow(), path).join();
    }

    private static BufferedImage readImage(String classpathPath) throws IOException {
        try (InputStream stream = openClasspathResource(classpathPath)) {
            if (stream == null)
                throw new IOException("Missing arrow texture resource: " + classpathPath);
            BufferedImage image = ImageIO.read(stream);
            if (image == null)
                throw new IOException("Failed to decode arrow texture resource: " + classpathPath);
            return image;
        }
    }

    private static InputStream openClasspathResource(String path) {
        return ArrowTextureGen.class.getClassLoader().getResourceAsStream(path);
    }

    private static BufferedImage recolor(BufferedImage pattern, int[] palette) {
        int width = pattern.getWidth();
        int height = pattern.getHeight();
        BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = pattern.getRGB(x, y);
                int alpha = (argb >>> 24) & 0xFF;
                if (alpha == 0)
                    continue;

                int gray = argb & 0xFF;
                int color = palette[closestGrayIndex(gray)];
                out.setRGB(x, y, (alpha << 24) | (color & 0xFFFFFF));
            }
        }
        return out;
    }

    private static int closestGrayIndex(int gray) {
        int bestIndex = 0;
        int bestDelta = Integer.MAX_VALUE;

        for (int i = 0; i < GRAY_LEVELS.length; i++) {
            int delta = Math.abs(GRAY_LEVELS[i] - gray);
            if (delta < bestDelta) {
                bestDelta = delta;
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    private static BufferedImage padToCanvas(BufferedImage content, int width, int height) {
        BufferedImage canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        canvas.getGraphics().drawImage(content, 0, 0, null);
        return canvas;
    }

    private static void writeImage(CachedOutput cache, BufferedImage image, Path path) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bytes);
        byte[] data = bytes.toByteArray();
        cache.writeIfNeeded(path, data, Hashing.sha1().hashBytes(data));
    }

    @Override
    public String getName() {
        return "Arrow Overlay Textures";
    }
}
