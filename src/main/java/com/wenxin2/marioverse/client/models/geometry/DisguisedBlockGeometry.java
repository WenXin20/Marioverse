package com.wenxin2.marioverse.client.models.geometry;

import com.wenxin2.marioverse.client.models.blocks.DisguisedBlockBakedModel;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.NotNull;

public class DisguisedBlockGeometry implements IUnbakedGeometry<DisguisedBlockGeometry> {
    @NotNull
    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material,
            TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
        return new DisguisedBlockBakedModel();
    }
}