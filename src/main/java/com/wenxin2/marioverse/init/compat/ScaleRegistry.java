package com.wenxin2.marioverse.init.compat;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.EventBusSubscriber;
import virtuoel.pehkui.api.ScaleModifier;
import virtuoel.pehkui.api.ScaleModifiers;
import virtuoel.pehkui.api.ScaleRegistries;
import virtuoel.pehkui.api.ScaleType;

@EventBusSubscriber(modid = Marioverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ScaleRegistry {
    public static final ScaleType MUSHROOM_SCALE;

    static {
        MUSHROOM_SCALE = registerDimensionScale("mushroom_scale", null, ScaleModifiers.HEIGHT_MULTIPLIER, ScaleModifiers.WIDTH_MULTIPLIER);
    }

    private static ScaleType register(ResourceLocation id, ScaleType.Builder builder) {
        return ScaleRegistries.register(ScaleRegistries.SCALE_TYPES, id, builder.build());
    }

    private static ScaleType registerDimensionScale(String path, ScaleModifier valueModifier, ScaleModifier... dependentModifiers) {
        ScaleType.Builder builder = ScaleType.Builder.create().affectsDimensions();
        if (valueModifier != null) {
            builder.addBaseValueModifier(valueModifier);
        }

        ScaleModifier[] var4 = dependentModifiers;
        int var5 = dependentModifiers.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            ScaleModifier scaleModifier = var4[var6];
            builder.addDependentModifier(scaleModifier);
        }

        return register(ResourceLocation.parse(Marioverse.MOD_ID), builder);
    }
}
