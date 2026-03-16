package com.wenxin2.marioverse.client.models.blocks;

import com.wenxin2.marioverse.blocks.entities.BlockSpawnerBlockEntity;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DisguisedBlockBakedModel implements IDynamicBakedModel {
    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random) {
        return getQuads(state, side, random, ModelData.EMPTY, null);
    }

    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random,
            ModelData data, @Nullable RenderType renderType) {
        BlockState disguiseState = data.get(BlockSpawnerBlockEntity.DISGUISED);

        if (disguiseState == null || disguiseState.isAir()
                || disguiseState.is(TagRegistry.BLOCK_SPAWNER_CANNOT_DISGUISE)) {
            if (state != null && !state.isAir()
                    && !state.is(TagRegistry.BLOCK_SPAWNER_CANNOT_DISGUISE)) {
                BakedModel originalModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
                return originalModel.getQuads(disguiseState, side, random, data, renderType);
            }
            return Collections.emptyList();
        }

        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(disguiseState);

        return model.getQuads(disguiseState, side, random, data, renderType);
    }

    @NotNull
    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random, ModelData data) {
        BlockState disguiseState = data.get(BlockSpawnerBlockEntity.DISGUISED);

        if (disguiseState != null && !disguiseState.isAir()) {
            BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(disguiseState);
            return model.getRenderTypes(disguiseState, random, data);
        }
        return ChunkRenderTypeSet.none();
    }

    @NotNull
    @Override
    public TextureAtlasSprite getParticleIcon(ModelData data) {
        BlockState disguiseState = data.get(BlockSpawnerBlockEntity.DISGUISED);

        if (disguiseState != null && !disguiseState.isAir()) {
            BakedModel model = Minecraft.getInstance()
                    .getBlockRenderer().getBlockModel(disguiseState);

            return model.getParticleIcon(ModelData.EMPTY);
        }
        return Minecraft.getInstance().getBlockRenderer()
                .getBlockModel(BlockRegistry.BLOCK_SPAWNER.get().defaultBlockState())
                .getParticleIcon(ModelData.EMPTY);
    }

    @NotNull
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return Minecraft.getInstance().getBlockRenderer()
                .getBlockModel(BlockRegistry.BLOCK_SPAWNER.get().defaultBlockState())
                .getParticleIcon(ModelData.EMPTY);
    }

    @NotNull
    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }
}