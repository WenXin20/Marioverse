package com.wenxin2.marioverse.client.models.loaders;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.wenxin2.marioverse.client.models.geometry.DisguisedBlockGeometry;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import org.jetbrains.annotations.NotNull;

public class DisguisedBlockModelLoader implements IGeometryLoader<DisguisedBlockGeometry> {
    @NotNull
    @Override
    public DisguisedBlockGeometry read(JsonObject json, JsonDeserializationContext context) {
        return new DisguisedBlockGeometry();
    }
}