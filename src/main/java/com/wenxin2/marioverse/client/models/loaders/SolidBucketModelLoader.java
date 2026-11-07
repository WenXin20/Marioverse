package com.wenxin2.marioverse.client.models.loaders;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.wenxin2.marioverse.client.models.geometry.SolidBucketGeometry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import org.jetbrains.annotations.NotNull;

public class SolidBucketModelLoader implements IGeometryLoader<SolidBucketGeometry> {
    @NotNull
    @Override
    public SolidBucketGeometry read(JsonObject json, JsonDeserializationContext context) {
        return new SolidBucketGeometry(ResourceLocation.parse(GsonHelper.getAsString(json, "block")));
    }
}
