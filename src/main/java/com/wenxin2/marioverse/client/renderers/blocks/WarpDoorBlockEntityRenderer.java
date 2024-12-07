package com.wenxin2.marioverse.client.renderers.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wenxin2.marioverse.blocks.entities.WarpDoorBlockEntity;
import com.wenxin2.marioverse.client.models.blocks.WarpDoorBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class WarpDoorBlockEntityRenderer extends GeoBlockRenderer<WarpDoorBlockEntity> {
    public WarpDoorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new WarpDoorBlockModel());
    }

    @Override
    public void render(WarpDoorBlockEntity animatable, float partialTicks, PoseStack stack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (animatable.getLevel() != null && animatable.destinationPos != null) {
            super.render(animatable, partialTicks, stack, buffer, packedLight, packedOverlay);
        }
    }

    @NotNull
    @Override
    public AABB getRenderBoundingBox(WarpDoorBlockEntity animatable) {
        if (animatable.destinationPos != null) {
            return new AABB(
                    animatable.getBlockPos().getX(),
                    animatable.getBlockPos().getY(),
                    animatable.getBlockPos().getZ(),
                    animatable.getBlockPos().getX() + 1.0,
                    animatable.getBlockPos().getY() + 2.0,
                    animatable.getBlockPos().getZ() + 1.0
            );
        } else return new AABB(0, 0, 0, 0, 0, 0);
    }
}
