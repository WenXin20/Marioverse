package com.wenxin2.marioverse.client.models.blocks;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.GoalPoleBlock;
import com.wenxin2.marioverse.blocks.entities.GoalPoleBlockEntity;
import com.wenxin2.marioverse.blocks.states.ColumnBlockStates;
import com.wenxin2.marioverse.init.BlockRegistry;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
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


    @Override
    public ResourceLocation getModelResource(GoalPoleBlockEntity block) {
        return this.MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(GoalPoleBlockEntity blockEntity) {
        BlockState state = blockEntity.getBlockState();
        Block block = blockEntity.getBlockState().getBlock();

        if (state.getValue(GoalPoleBlock.COLUMN) == ColumnBlockStates.NONE) {
            if (blockEntity.isAmericanFlag())
                return this.AMERICAN_FLAG_SMALL_TEXTURE;
            else if (state.getValue(GoalPoleBlock.LOWERED)) {
                if (blockEntity.getLevel() != null && !blockEntity.playedSwitchAnim())
                    this.spawnPoofParticles(blockEntity, ParticleTypes.POOF, 15);

                if (block == BlockRegistry.RED_GOAL_POLE.get())
                    return this.RED_FLAG_SMALL_TEXTURE;
                else return this.RED_FLAG_SMALL_TEXTURE;
            } else return this.BOWSER_FLAG_SMALL_TEXTURE;
        } else if (blockEntity.isAmericanFlag())
            return this.AMERICAN_FLAG_TEXTURE;
        else if (state.getValue(GoalPoleBlock.LOWERED)) {
            if (blockEntity.getLevel() != null && !blockEntity.playedSwitchAnim())
                this.spawnPoofParticles(blockEntity, ParticleTypes.POOF, 10);

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

    public void spawnPoofParticles(GoalPoleBlockEntity blockEntity, ParticleOptions particleType, int avgAmount) {
        if (blockEntity.getLevel() != null && this.getBone("flag").isPresent()) {
            float scaleFactor = this.getBone("flag").get().getScaleY();
            int numParticles = (int) (scaleFactor * avgAmount);
            double radius = this.getBone("flag").get().getScaleX() / 2;

            for (int i = 0; i < numParticles; i++) {
                // Calculate angle for each particle
                double angle = 2 * Math.PI * i / numParticles;
                // Calculate the X and Z offset using sine and cosine to spread in an ellipse
                double offsetX = Math.cos(angle) * radius;
                double offsetY = this.getBone("flag").get().getScaleY() / 2;
                double offsetZ = Math.sin(angle) * radius;

                double x = blockEntity.getBlockPos().getX() + offsetX;
                double y = blockEntity.getBlockPos().getY() + offsetY;
                double z = blockEntity.getBlockPos().getZ() + offsetZ;

                blockEntity.getLevel().addParticle(particleType, x, y, z, 0, 0, 0);
            }
        }
    }
}