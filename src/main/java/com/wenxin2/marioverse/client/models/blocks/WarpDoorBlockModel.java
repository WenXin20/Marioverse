package com.wenxin2.marioverse.client.models.blocks;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.entities.WarpDoorBlockEntity;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class WarpDoorBlockModel extends GeoModel<WarpDoorBlockEntity> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/block/warp_door.geo.json");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/warp_door/warp_door.png");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/block/warp_door.animation.json");

    @Override
    public ResourceLocation getModelResource(WarpDoorBlockEntity animatable) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(WarpDoorBlockEntity animatable) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(WarpDoorBlockEntity animatable) {
        return this.animations;
    }

    @Override
    public void setCustomAnimations(WarpDoorBlockEntity animatable, long instanceId, AnimationState<WarpDoorBlockEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        BlockState state = animatable.getBlockState();
        Direction facing = state.getValue(DoorBlock.FACING);

        float rotationAngle = switch (facing) {
            case NORTH -> 0f;
            case SOUTH -> 180f;
            case EAST -> 90f;
            case WEST -> -90f;
            default -> 0f;
        };

        Optional<GeoBone> portalBone = getBone("portal");
        if (state.getValue(DoorBlock.FACING) == Direction.EAST || state.getValue(DoorBlock.FACING) == Direction.WEST)
            portalBone.ifPresent(geoBone -> geoBone.setRotY((float) Math.toRadians(180)));
        else portalBone.ifPresent(geoBone -> geoBone.setRotY((float) Math.toRadians(0)));
    }
}
