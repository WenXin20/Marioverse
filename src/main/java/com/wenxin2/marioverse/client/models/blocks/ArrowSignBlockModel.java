package com.wenxin2.marioverse.client.models.blocks;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.LargeStandingArrowSignBlock;
import com.wenxin2.marioverse.blocks.LargeWallArrowSignBlock;
import com.wenxin2.marioverse.blocks.entities.ArrowSignBlockEntity;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.WoodType;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

import java.util.Optional;

public class ArrowSignBlockModel extends GeoModel<ArrowSignBlockEntity> {
    private static final ResourceLocation STANDING_MODEL =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/block/arrow_sign.geo.json");
    private static final ResourceLocation WALL_MODEL =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/block/wall_arrow_sign.geo.json");
    private static final ResourceLocation HANGING_MODEL =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/block/hanging_arrow_sign.geo.json");
    private static final ResourceLocation ATTACHED_HANGING_MODEL =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/block/hanging_arrow_sign_attached.geo.json");
    private static final ResourceLocation LARGE_STANDING_MODEL =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/block/large_arrow_sign.geo.json");
    private static final ResourceLocation LARGE_WALL_MODEL =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/block/large_wall_arrow_sign.geo.json");
    private final ResourceLocation ANIMATION =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/block/arrow_sign.animation.json");

    @Override
    public ResourceLocation getModelResource(ArrowSignBlockEntity blockEntity) {
        BlockState state = blockEntity.getBlockState();
        Block block = state.getBlock();

        if (block instanceof LargeWallArrowSignBlock)
            return LARGE_WALL_MODEL;
        else if (block instanceof LargeStandingArrowSignBlock)
            return LARGE_STANDING_MODEL;
        else if (block instanceof StandingSignBlock)
            return STANDING_MODEL;
        else if (block instanceof WallSignBlock)
            return WALL_MODEL;
        else if (block instanceof CeilingHangingSignBlock) {
            if (state.hasProperty(BlockStateProperties.ATTACHED)
                    && state.getValue(BlockStateProperties.ATTACHED))
                return ATTACHED_HANGING_MODEL;
            return HANGING_MODEL;
        }
        return STANDING_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(ArrowSignBlockEntity blockEntity) {
        BlockState state = blockEntity.getBlockState();
        Block block = state.getBlock();
        var arrowDirection = state.getValue(BlockStatePropertyRegistry.ARROW_DIRECTION);

        WoodType woodType = SignBlock.getWoodType(block);
        ResourceLocation woodTypeName = ResourceLocation.parse(woodType.name());

        boolean isLarge = block instanceof LargeStandingArrowSignBlock || block instanceof LargeWallArrowSignBlock;
        String folder = isLarge ? "large_arrow" : "arrow";
        String prefix = isLarge ? "large_" + woodTypeName.getPath() : woodTypeName.getPath();

        if (arrowDirection.getSerializedName().equals("none"))
            return ResourceLocation.fromNamespaceAndPath(woodTypeName.getNamespace(),
                    "textures/entity/signs/" + folder + "/" + prefix + "_arrow_sign.png");
        return ResourceLocation.fromNamespaceAndPath(woodTypeName.getNamespace(),
                "textures/entity/signs/" + folder + "/" + prefix + "_arrow_sign_" + arrowDirection.getSerializedName() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(ArrowSignBlockEntity blockEntity) {
        return this.ANIMATION;
    }

    @Override
    public void setCustomAnimations(ArrowSignBlockEntity animatable, long instanceId, AnimationState<ArrowSignBlockEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        BlockState state = animatable.getBlockState();
        boolean hasBoard = state.getValue(BlockStatePropertyRegistry.BOARD);

        float rotationDegrees;
        if (state.hasProperty(BlockStateProperties.ROTATION_16)) {
            int rotationState = state.getValue(BlockStateProperties.ROTATION_16);
            float[] rotationDegreesArray = {
                    90.0F, 67.5F, 45.0F, 22.5F, 0.0F, 337.5F,
                    315.0F, 292.5F, 270.0F, 247.5F, 225.0F,
                    202.5F, 180.0F, 157.5F, 135.0F, 112.5F
            };
            rotationDegrees = (rotationDegreesArray[rotationState] - 90.0F) % 360.0F;
        } else if (state.hasProperty(BlockStateProperties.FACING))
            rotationDegrees = state.getValue(BlockStateProperties.FACING).toYRot();
        else rotationDegrees = 0.0F;

        Optional<GeoBone> boardBone = this.getBone("board");
        boardBone.ifPresent(geoBone -> {
            geoBone.setHidden(!hasBoard);
            if (hasBoard)
                geoBone.setRotY((float) Math.toRadians(rotationDegrees));
        });

        Optional<GeoBone> postBone = this.getBone("post");
        if (state.hasProperty(BlockStatePropertyRegistry.POST)) {
            boolean hasPost = state.getValue(BlockStatePropertyRegistry.POST);
            boolean snapToCardinal = state.getBlock() instanceof LargeStandingArrowSignBlock;

            postBone.ifPresent(geoBone -> {
                geoBone.setHidden(!hasPost);
                if (hasPost && snapToCardinal) {
                    float postRotationDegrees = Math.round(rotationDegrees / 90.0F) * 90.0F;
                    geoBone.setRotY((float) Math.toRadians(postRotationDegrees));
                }
            });
        }
    }
}