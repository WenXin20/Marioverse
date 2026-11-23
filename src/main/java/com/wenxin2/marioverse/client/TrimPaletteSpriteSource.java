package com.wenxin2.marioverse.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public record TrimPaletteSpriteSource(List<ResourceLocation> textures, ResourceLocation paletteKey) implements SpriteSource {
    public static final MapCodec<TrimPaletteSpriteSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.list(ResourceLocation.CODEC).fieldOf("textures").forGetter(TrimPaletteSpriteSource::textures),
            ResourceLocation.CODEC.fieldOf("palette_key").forGetter(TrimPaletteSpriteSource::paletteKey)
    ).apply(instance, TrimPaletteSpriteSource::new));
    public static final SpriteSourceType TYPE = new SpriteSourceType(CODEC);
    private static final String PALETTE_DIRECTORY = "trims/color_palettes";
    private static final FileToIdConverter PALETTE_CONVERTER = new FileToIdConverter("textures/" + PALETTE_DIRECTORY, ".png");

    @Override
    public void run(ResourceManager resourceManager, Output output) {
        Optional<Resource> keyResource = resourceManager.getResource(TEXTURE_ID_CONVERTER.idToFile(this.paletteKey));
        int keySize = keyResource.map(TrimPaletteSpriteSource::getPaletteSize).orElse(-1);

        if (keySize <= 0)
            return;

        Map<String, ResourceLocation> permutations = new TreeMap<>();

        for (Map.Entry<ResourceLocation, Resource> entry : PALETTE_CONVERTER.listMatchingResources(resourceManager).entrySet()) {
            ResourceLocation fileId = PALETTE_CONVERTER.fileToId(entry.getKey());
            ResourceLocation paletteId = fileId.withPrefix(PALETTE_DIRECTORY + "/");
            String suffix = fileId.getPath();

            if (paletteId.equals(this.paletteKey) || suffix.equals("trim_palette") || suffix.contains("/"))
                continue;
            if (TrimPaletteSpriteSource.getPaletteSize(entry.getValue()) != keySize)
                continue;
            if (!permutations.containsKey(suffix) || paletteId.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE))
                permutations.put(suffix, paletteId);
        }

        new PalettedPermutations(this.textures, this.paletteKey, permutations).run(resourceManager, output);
    }

    @Override
    public SpriteSourceType type() {
        return TYPE;
    }

    private static int getPaletteSize(Resource resource) {
        try (InputStream stream = resource.open(); NativeImage image = NativeImage.read(stream)) {
            return image.getWidth() * image.getHeight();
        } catch (Exception exception) {
            return -1;
        }
    }
}
