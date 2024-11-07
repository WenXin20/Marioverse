package com.wenxin2.marioverse.client.models.blocks;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.GoalPoleBlock;
import com.wenxin2.marioverse.blocks.entities.GoalPoleBlockEntity;
import com.wenxin2.marioverse.blocks.states.ColumnBlockStates;
import com.wenxin2.marioverse.init.BlockRegistry;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class GoalPoleBlockModel extends GeoModel<GoalPoleBlockEntity> {
    private final ResourceLocation MODEL =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/block/goal_pole.geo.json");
    private final ResourceLocation ANIMATION =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/block/goal_pole.animation.json");
    private final ResourceLocation AMERICAN_FLAG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/goal_pole/american_flag.png");
    private final ResourceLocation RED_FLAG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/goal_pole/red_flag.png");
    private final ResourceLocation BOWSER_FLAG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/goal_pole/bowser_flag.png");
    private final ResourceLocation AMERICAN_FLAG_SMALL_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/goal_pole/american_flag_small.png");
    private final ResourceLocation RED_FLAG_SMALL_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/goal_pole/red_flag_small.png");
    private final ResourceLocation BOWSER_FLAG_SMALL_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/goal_pole/bowser_flag_small.png");

    private static final int DELAY_SECONDS = 2;
    private long targetTime = -1;


    @Override
    public ResourceLocation getModelResource(GoalPoleBlockEntity block) {
        return this.MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(GoalPoleBlockEntity blockEntity) {
        BlockState state = blockEntity.getBlockState();
        Block block = blockEntity.getBlockState().getBlock();

        if (state.getValue(GoalPoleBlock.COLUMN) == ColumnBlockStates.NONE) {
            if (blockEntity.isAmericanFlag(blockEntity))
                return this.AMERICAN_FLAG_SMALL_TEXTURE;
            else if (state.getValue(GoalPoleBlock.LOWERED)/* && this.isTextureChangeDue(blockEntity)*/) {
                this.resetTextureChange(blockEntity);
                if (block == BlockRegistry.RED_GOAL_POLE.get())
                    return this.RED_FLAG_SMALL_TEXTURE;
                else return this.RED_FLAG_SMALL_TEXTURE;
            } else return this.BOWSER_FLAG_SMALL_TEXTURE;
        } else if (blockEntity.isAmericanFlag(blockEntity))
            return this.AMERICAN_FLAG_TEXTURE;
        else if (state.getValue(GoalPoleBlock.LOWERED)/* && this.isTextureChangeDue(blockEntity)*/) {
            this.resetTextureChange(blockEntity);
            if (state.getValue(GoalPoleBlock.COLUMN) == ColumnBlockStates.MIDDLE)
                return this.BOWSER_FLAG_TEXTURE;
            else if (block == BlockRegistry.RED_GOAL_POLE.get())
                return this.RED_FLAG_TEXTURE;
            else return this.RED_FLAG_TEXTURE;
        } else return this.BOWSER_FLAG_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(GoalPoleBlockEntity block) {
        return this.ANIMATION;
    }

    @Override
    public void setCustomAnimations(GoalPoleBlockEntity animatable, long instanceId, AnimationState<GoalPoleBlockEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        int rotationState = animatable.getBlockState().getValue(GoalPoleBlock.ROTATION);
        float[] rotationDegreesArray = {
                90.0F, 67.5F, 45.0F, 22.5F, 0.0F, 337.5F,
                315.0F, 292.5F, 270.0F, 247.5F, 225.0F,
                202.5F, 180.0F, 157.5F, 135.0F, 112.5F
        };
        float rotationDegrees = rotationDegreesArray[rotationState];

        Optional<GeoBone> poleBone = getBone("pole");
        poleBone.ifPresent(geoBone -> geoBone.setRotY((float) Math.toRadians(rotationDegrees)));
    }

    public void startTextureChangeDelay(GoalPoleBlockEntity blockEntity) {
        if (blockEntity.getLevel() != null) {
            targetTime = blockEntity.getLevel().getGameTime() + DELAY_SECONDS * 20;
        }
    }

    public boolean isTextureChangeDue(GoalPoleBlockEntity blockEntity) {
        return blockEntity.getLevel() != null && targetTime != -1 && blockEntity.getLevel().getGameTime() >= targetTime;
    }

    public void resetTextureChange(GoalPoleBlockEntity blockEntity) {
        targetTime = -1;
    }
}