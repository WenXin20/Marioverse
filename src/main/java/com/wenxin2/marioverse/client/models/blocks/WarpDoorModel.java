package com.wenxin2.marioverse.client.models.blocks;

import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
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
    private final BakedModel blockModel;
    private final BakedModel itemModel;

    public WarpDoorModel(BakedModel blockModel, BakedModel itemModel) {
        this.blockModel = blockModel;
        this.itemModel = itemModel;
    }

    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random, ModelData data, @Nullable RenderType renderType) {
        return this.blockModel.getQuads(state, direction, random, data, renderType);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
        return this.blockModel.getQuads(state, direction, random);
    }

    @NotNull
    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {
        return this.blockModel.getModelData(level, pos, state, modelData);
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
        return this.blockModel.getRenderTypes(state, rand, data);
    }

    @Override
    public List<RenderType> getRenderTypes(ItemStack stack, boolean fabulous) {
        return this.blockModel.getRenderTypes(stack, fabulous);
    }

    @NotNull
    @Override
    public ItemTransforms getTransforms() {
        return itemModel.getTransforms();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.blockModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.blockModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return this.blockModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return this.blockModel.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.blockModel.getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides() {
        return this.blockModel.getOverrides();
    }
}
