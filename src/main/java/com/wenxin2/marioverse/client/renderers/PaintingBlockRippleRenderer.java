package com.wenxin2.marioverse.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.integration.fast_paintings_compat.FastPaintingsCompat;
import com.wenxin2.marioverse.integration.fast_paintings_compat.PaintingBlockWarp;
import com.wenxin2.marioverse.integration.sable_compat.SableProvider;
import com.wenxin2.marioverse.utils.PaintingRipples;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class PaintingBlockRippleRenderer {
    private static final double FRONT_OFFSET = 0.4375;

    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES)
            return;

        List<BlockPos> ripplingBlocks = PaintingRipples.getRipplingBlocks();
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (ripplingBlocks.isEmpty() || level == null)
            return;

        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        Vec3 camera = event.getCamera().getPosition();
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();

        for (BlockPos pos : ripplingBlocks) {
            float[] progresses = PaintingRipples.getProgresses(level, pos, partialTick);
            if (progresses.length == 0)
                continue;

            PaintingBlockWarp painting = FastPaintingsCompat.getWarp(level, pos);
            if (painting != null)
                PaintingBlockRippleRenderer.renderPainting(minecraft, level, painting, progresses, camera, bufferSource);
        }
    }

    private static void renderPainting(Minecraft minecraft, ClientLevel level, PaintingBlockWarp painting, float[] progresses, Vec3 camera,
                                       MultiBufferSource.BufferSource bufferSource) {
        Direction facing = painting.getDirection();
        Direction along = facing.getCounterClockWise();
        BlockPos masterPos = painting.getMasterPos();
        int width = painting.getWidth();
        int height = painting.getHeight();
        PoseStack poseStack = new PoseStack();

        if (!PaintingBlockRippleRenderer.applyPaintingTransform(level, masterPos, camera, poseStack))
            return;

        Vec3 frontOrigin = new Vec3(0.5 - facing.getStepX() * FRONT_OFFSET, 0.5, 0.5 - facing.getStepZ() * FRONT_OFFSET);
        Vec3 center = frontOrigin.add(along.getStepX() * (width - 1) / 2.0, -(height - 1) / 2.0, along.getStepZ() * (width - 1) / 2.0);
        Direction.Axis uAxis = facing.getAxis() == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;

        PaintingRippleRenderer.begin(progresses, uAxis, Direction.Axis.Y, center, width, height, poseStack);
        if (!PaintingRippleRenderer.isActive())
            return;

        TextureAtlasSprite sprite = minecraft.getPaintingTextures().get(painting.getVariant().value());
        RenderType renderType = PaintingRippleRenderTypes.ripplingPaintingOverlay(sprite.atlasLocation());
        VertexConsumer consumer = PaintingRippleRenderer.buffer(renderType, bufferSource.getBuffer(renderType));
        PoseStack.Pose pose = poseStack.last();

        for (int down = 0; down < height; down++) {
            for (int right = 0; right < width; right++) {
                BlockPos part = masterPos.below(down).relative(along, right);
                int light = LevelRenderer.getLightColor(level, part);
                Vec3 partCenter = frontOrigin.add(along.getStepX() * right, -down, along.getStepZ() * right);
                Vec3 left = new Vec3(along.getStepX() * -0.5, 0.0, along.getStepZ() * -0.5);
                float u0 = sprite.getU((float) right / width);
                float u1 = sprite.getU((right + 1.0F) / width);
                float v0 = sprite.getV((float) down / height);
                float v1 = sprite.getV((down + 1.0F) / height);

                PaintingBlockRippleRenderer.vertex(consumer, pose, partCenter.add(left).add(0.0, 0.5, 0.0), u0, v0, light, facing);
                PaintingBlockRippleRenderer.vertex(consumer, pose, partCenter.add(left).add(0.0, -0.5, 0.0), u0, v1, light, facing);
                PaintingBlockRippleRenderer.vertex(consumer, pose, partCenter.subtract(left).add(0.0, -0.5, 0.0), u1, v1, light, facing);
                PaintingBlockRippleRenderer.vertex(consumer, pose, partCenter.subtract(left).add(0.0, 0.5, 0.0), u1, v0, light, facing);
            }
        }

        bufferSource.endBatch(renderType);
        PaintingRippleRenderer.end();
    }

    private static boolean applyPaintingTransform(ClientLevel level, BlockPos masterPos, Vec3 camera, PoseStack poseStack) {
        Vec3 corner = Vec3.atLowerCornerOf(masterPos);

        if (!SableProvider.isLoaded() || !SableProvider.isPlotPos(level, masterPos)) {
            poseStack.translate(corner.x - camera.x, corner.y - camera.y, corner.z - camera.z);
            return true;
        }

        Vec3 origin = SableProvider.toWorldVec(level, corner);
        Vec3 xEnd = SableProvider.toWorldVec(level, corner.add(1.0, 0.0, 0.0));
        Vec3 yEnd = SableProvider.toWorldVec(level, corner.add(0.0, 1.0, 0.0));
        Vec3 zEnd = SableProvider.toWorldVec(level, corner.add(0.0, 0.0, 1.0));
        if (origin == null || xEnd == null || yEnd == null || zEnd == null)
            return false;

        Vec3 xAxis = xEnd.subtract(origin);
        Vec3 yAxis = yEnd.subtract(origin);
        Vec3 zAxis = zEnd.subtract(origin);

        poseStack.translate(origin.x - camera.x, origin.y - camera.y, origin.z - camera.z);
        poseStack.mulPose(new Matrix4f(
                (float) xAxis.x, (float) xAxis.y, (float) xAxis.z, 0.0F,
                (float) yAxis.x, (float) yAxis.y, (float) yAxis.z, 0.0F,
                (float) zAxis.x, (float) zAxis.y, (float) zAxis.z, 0.0F,
                0.0F, 0.0F, 0.0F, 1.0F));
        return true;
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, Vec3 pos, float u, float v, int light, Direction facing) {
        consumer.addVertex(pose, (float) pos.x, (float) pos.y, (float) pos.z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, facing.getStepX(), facing.getStepY(), facing.getStepZ());
    }
}
