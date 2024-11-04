package com.wenxin2.marioverse.client.models.blocks;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.GoalPoleBlock;
import com.wenxin2.marioverse.blocks.entities.GoalPoleBlockEntity;
import com.wenxin2.marioverse.init.BlockRegistry;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class GoalPoleBlockModel extends GeoModel<GoalPoleBlockEntity> {
    private final ResourceLocation model =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/block/goal_pole.geo.json");
    private final ResourceLocation animations =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/block/goal_pole.animation.json");
    private final ResourceLocation red_flag_texture =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/goal_pole/red_flag.png");

    @Override
    public ResourceLocation getModelResource(GoalPoleBlockEntity block) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(GoalPoleBlockEntity block) {
        if (block.getBlockState().getValue(GoalPoleBlock.LOWERED)) {
            if (block.getBlockState().getBlock() == BlockRegistry.RED_GOAL_POLE.get())
                return this.red_flag_texture;
            else return this.red_flag_texture;
        }
        else return this.red_flag_texture;
    }

    @Override
    public ResourceLocation getAnimationResource(GoalPoleBlockEntity block) {
        return this.animations;
    }

    @Override
    public void setCustomAnimations(GoalPoleBlockEntity animatable, long instanceId, AnimationState<GoalPoleBlockEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        int rotationState = animatable.getBlockState().getValue(GoalPoleBlock.ROTATION);
        float[] rotationDegreesArray = {
                90.0F, 292.5F, 315.0F, 337.5F, 0.0F, 202.5F,
                225.0F, 247.5F, 270.0F, 112.5F, 135.0F,
                157.5F, 180.0F, 22.5F, 45.0F, 67.5F
        };
        float rotationDegrees = rotationDegreesArray[rotationState];

        Optional<GeoBone> poleBone = getBone("pole");
        poleBone.ifPresent(geoBone -> geoBone.setRotY((float) Math.toRadians(rotationDegrees)));
    }
}