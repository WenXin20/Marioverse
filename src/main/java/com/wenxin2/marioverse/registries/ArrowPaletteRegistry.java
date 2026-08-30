package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.data.ArrowPalette;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

/** Default palette-per-DyeColor mapping, written to data/marioverse/palettes/arrow/*.json by ArrowTextureGen. */
public class ArrowPaletteRegistry {
    private static final Map<DyeColor, ArrowPalette> DEFAULTS = new EnumMap<>(DyeColor.class);

    static {
        for (DyeColor dyeColor : DyeColor.values()) {
            ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID,
                    "textures/palettes/arrow/" + dyeColor.getSerializedName() + ".png");
            DEFAULTS.put(dyeColor, new ArrowPalette(texture));
        }
    }

    public static ArrowPalette defaultPalette(DyeColor dyeColor) {
        return DEFAULTS.get(dyeColor);
    }
}
