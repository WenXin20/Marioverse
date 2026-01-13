package com.wenxin2.marioverse.client.models.blocks;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WarpDoorModel implements BakedModel {

    private final BakedModel source;
    public static final Map<ModelResourceLocation, WarpDoorModel> MODEL_CACHE = new IdentityHashMap<>();

    public WarpDoorModel(BakedModel sourceModel) {
        this.source = sourceModel;
    }

    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random, ModelData data, @Nullable RenderType renderType) {
        return source.getQuads(state, direction, random, data, renderType);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
        return source.getQuads(state, direction, random);
    }

    @NotNull
    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {
        return source.getModelData(level, pos, state, modelData);
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
        return source.getRenderTypes(state, rand, data);
    }

    @Override
    public List<RenderType> getRenderTypes(ItemStack stack, boolean fabulous) {
        return source.getRenderTypes(stack, fabulous);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return source.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return source.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return source.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return source.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return source.getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides() {
        return source.getOverrides();
    }
}
