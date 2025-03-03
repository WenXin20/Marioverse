package com.wenxin2.marioverse.client.models.blocks;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.CheckpointFlagBlock;
import com.wenxin2.marioverse.blocks.GoalPoleBlock;
import com.wenxin2.marioverse.blocks.entities.CheckpointFlagBlockEntity;
import com.wenxin2.marioverse.init.BlockRegistry;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredBlock;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class CheckpointFlagBlockModel extends GeoModel<CheckpointFlagBlockEntity> {
    private final ResourceLocation MODEL =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/block/checkpoint_flag.geo.json");
    private final ResourceLocation ANIMATION =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/block/checkpoint_flag.animation.json");
    private final ResourceLocation AMERICAN_FLAG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/checkpoint_flag/american_checkpoint_flag.png");
    private final ResourceLocation BOWSER_FLAG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/checkpoint_flag/bowser_checkpoint_flag.png");
    private final ResourceLocation CLASSIC_FLAG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/checkpoint_flag/classic_checkpoint_flag.png");
    private final ResourceLocation WONDER_FLAG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/checkpoint_flag/wonder_checkpoint_flag.png");

    @Override
    public ResourceLocation getModelResource(CheckpointFlagBlockEntity blockEntity) {
        return this.MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(CheckpointFlagBlockEntity blockEntity) {
        BlockState state = blockEntity.getBlockState();
        Block block = blockEntity.getBlockState().getBlock();
        DyeColor color = null;

        for (Map.Entry<DyeColor, DeferredBlock<Block>> entry : BlockRegistry.CHECKPOINT_FLAGS.entrySet()) {
            if (entry.getValue().get() == block) {
                color = entry.getKey();
                break;
            }
        }
        String texturePath = "textures/entity/checkpoint_flag/";
        String colorName = (color != null) ? color.getName().toLowerCase() : "white";

        if (blockEntity.hasAmericanFlag())
            return this.AMERICAN_FLAG_TEXTURE;

        else if (!blockEntity.hasWonderFlag() && block == BlockRegistry.CLASSIC_CHECKPOINT_FLAG.get())
            return this.CLASSIC_FLAG_TEXTURE;

        else if (state.getValue(CheckpointFlagBlock.CLAIMED))
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, texturePath + colorName + "_checkpoint_flag.png");

        else if (blockEntity.hasWonderFlag())
            return this.WONDER_FLAG_TEXTURE;

        else return this.BOWSER_FLAG_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(CheckpointFlagBlockEntity block) {
        return this.ANIMATION;
    }

    @Override
    public void setCustomAnimations(CheckpointFlagBlockEntity animatable, long instanceId, AnimationState<CheckpointFlagBlockEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        int rotationState = animatable.getBlockState().getValue(GoalPoleBlock.ROTATION);
        float[] rotationDegreesArray = {
                90.0F, 67.5F, 45.0F, 22.5F, 0.0F, 337.5F,
                315.0F, 292.5F, 270.0F, 247.5F, 225.0F,
                202.5F, 180.0F, 157.5F, 135.0F, 112.5F
        };
        float rotationDegrees = rotationDegreesArray[rotationState];

        Optional<GeoBone> poleBone = getBone("flag_rotator");
        poleBone.ifPresent(geoBone -> geoBone.setRotY((float) Math.toRadians(rotationDegrees)));
    }
}