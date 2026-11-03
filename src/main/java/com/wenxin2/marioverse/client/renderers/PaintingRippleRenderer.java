package com.wenxin2.marioverse.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.integration.iris_compat.IrisShaderProvider;
import com.wenxin2.marioverse.utils.PaintingRipples;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class PaintingRippleRenderer {
    private static final boolean IRIS_LOADED = ModList.get().isLoaded("iris") || ModList.get().isLoaded("oculus");
    private static final float ASPECT_STEPS = 7.875F;
    private static RippleContext current;

    public static void begin(Entity painting, float partialTick, PoseStack poseStack) {
        current = null;

        if (PaintingRippleRenderTypes.ripplingPaintingShader == null)
            return;

        float progress = PaintingRipples.getProgress(painting, partialTick);
        if (progress < 0.0F || PaintingRippleRenderer.isShaderPackInUse())
            return;

        Direction facing = painting.getDirection();
        Direction.Axis uAxis = facing.getAxis() == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        Direction.Axis vAxis = facing.getAxis() == Direction.Axis.Y ? Direction.Axis.Z : Direction.Axis.Y;
        AABB bounds = painting.getBoundingBox();
        Vec3 center = bounds.getCenter().subtract(painting.position());

        float width = (float) Math.max(bounds.max(uAxis) - bounds.min(uAxis), 0.0625);
        float height = (float) Math.max(bounds.max(vAxis) - bounds.min(vAxis), 0.0625);
        int aspect = Mth.clamp(Math.round(((float) (Math.log(width / height) / Math.log(2.0)) + 4.0F) * ASPECT_STEPS), 0, 63);

        current = new RippleContext(new Matrix4f(poseStack.last().pose()).invert(),
                new Vector3f((float) center.x, (float) center.y, (float) center.z), uAxis, vAxis, width, height,
                Mth.clamp((int) (progress * 127.0F), 0, 127), aspect);
    }

    public static void end() {
        current = null;
    }

    public static RenderType renderType(RenderType original, ResourceLocation texture, boolean translucent) {
        if (current == null || PaintingRippleRenderTypes.ripplingPaintingShader == null)
            return original;
        return PaintingRippleRenderTypes.ripplingPainting(texture, translucent);
    }

    public static VertexConsumer buffer(RenderType renderType, VertexConsumer consumer) {
        if (current == null || !PaintingRippleRenderTypes.isRippling(renderType))
            return consumer;
        return new RipplingPaintingVertexConsumer(consumer, current, PaintingRippleRenderTypes.isTranslucent(renderType));
    }

    private static boolean isShaderPackInUse() {
        if (!IRIS_LOADED)
            return false;

        try {
            return IrisShaderProvider.isShaderPackInUse();
        } catch (Throwable throwable) {
            return true;
        }
    }

    public record RippleContext(Matrix4f inverseOrigin, Vector3f center, Direction.Axis uAxis, Direction.Axis vAxis,
                                float width, float height, int progress, int aspect) {
        public int encodeU(Vector3f localPos) {
            float u = (PaintingRippleRenderer.component(localPos, this.uAxis) - PaintingRippleRenderer.component(this.center, this.uAxis))
                    / this.width + 0.5F;
            return Mth.clamp(Math.round(u * 255.0F), 0, 255) | (this.progress << 8);
        }

        public int encodeV(Vector3f localPos, boolean translucent) {
            float v = (PaintingRippleRenderer.component(localPos, this.vAxis) - PaintingRippleRenderer.component(this.center, this.vAxis))
                    / this.height + 0.5F;
            return Mth.clamp(Math.round(v * 255.0F), 0, 255) | ((translucent ? 1 : 0) << 8) | (this.aspect << 9);
        }
    }

    private static float component(Vector3f vector, Direction.Axis axis) {
        return switch (axis) {
            case X -> vector.x();
            case Y -> vector.y();
            case Z -> vector.z();
        };
    }
}
