package com.wenxin2.marioverse.client.renderers.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.wenxin2.marioverse.client.models.entities.IceCubeModel;
import com.wenxin2.marioverse.entities.IceCubeEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IceCubeRenderer extends GeoEntityRenderer<IceCubeEntity> {
    private final EntityRenderDispatcher entityRenderer;

    public IceCubeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new IceCubeModel());
        this.entityRenderer = renderManager.getEntityRenderDispatcher();
    }

    @Override
    public void render(IceCubeEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        Entity frozenEntity = entity.getOrCreateDisplayEntity(entity.level());
        Entity frozenPlayer = entity.getPlayer(entity.level());

        if (frozenEntity != null) {
            float scale = 1.0F;
            float heightScale = 1.0F;
            float widthScale = 1.0F;

            if (frozenEntity instanceof LivingEntity livingEntity) {
                if (livingEntity.getPersistentData().contains("Scale"))
                    scale = livingEntity.getPersistentData().getFloat("Scale");
                if (livingEntity.getPersistentData().contains("HeightScale"))
                    heightScale = livingEntity.getPersistentData().getFloat("HeightScale");
                if (livingEntity.getPersistentData().contains("WidthScale"))
                    widthScale = livingEntity.getPersistentData().getFloat("WidthScale");
            }

            float frozenHeight = entity.getFrozenHeight(frozenEntity);
            float frozenWidth = entity.getFrozenWidth(frozenEntity);
            float height = frozenHeight * scale * heightScale * 1.55F;
            float width = frozenWidth * scale * widthScale * 1.55F;
            float offset = (height - (height / 1.55F)) / 2;
            Vec3 frozenOffset = entity.getFrozenOffset();

            if (frozenHeight >= frozenWidth * 3)
                width *= 2.0F;

            entity.applyFrozenRotation(frozenEntity);

            poseStack.pushPose();

                this.withScale(width, height);
                entity.setSize(width, height);

                poseStack.translate(frozenOffset.x, offset + frozenOffset.y, frozenOffset.z);
                poseStack.scale(scale * widthScale, scale * heightScale, scale * widthScale);
                renderEntityInIceCube(frozenEntity.getYRot(), poseStack, buffer, packedLight, frozenEntity, this.entityRenderer);

            poseStack.popPose();
        }

        if (frozenPlayer != null) {
            float width = frozenPlayer.getDimensions(Pose.STANDING).width() * 2.55F;
            float height = frozenPlayer.getDimensions(Pose.STANDING).height() * 1.55F;

            poseStack.pushPose();
                this.withScale(width, height);
                entity.setSize(width, height);
            poseStack.popPose();
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public void renderCube(PoseStack poseStack, GeoCube cube, VertexConsumer buffer, int packedLight, int packedOverlay, int colour) {
        Vector3f min = new Vector3f(Float.MAX_VALUE);
        Vector3f max = new Vector3f(-Float.MAX_VALUE);

        for (GeoQuad quad : cube.quads()) {
            if (quad == null)
                continue;
            for (GeoVertex vertex : quad.vertices()) {
                min.min(vertex.position());
                max.max(vertex.position());
            }
        }

        if (min.x > max.x)
            return;

        PoseStack.Pose pose = poseStack.last();
        float sizeX = max.x - min.x;
        float sizeY = max.y - min.y;
        float sizeZ = max.z - min.z;
        int tilesX = IceCubeRenderer.getTileCount(sizeX * this.scaleWidth);
        int tilesY = IceCubeRenderer.getTileCount(sizeY * this.scaleHeight);
        int tilesZ = IceCubeRenderer.getTileCount(sizeZ * this.scaleWidth);

        IceCubeRenderer.renderPiecedFace(pose, buffer, new Vector3f(max.x, max.y, min.z), new Vector3f(-sizeX, 0, 0), new Vector3f(0, -sizeY, 0),
                tilesX, tilesY, 0, 0, -1, packedLight, packedOverlay, colour);
        IceCubeRenderer.renderPiecedFace(pose, buffer, new Vector3f(min.x, max.y, max.z), new Vector3f(sizeX, 0, 0), new Vector3f(0, -sizeY, 0),
                tilesX, tilesY, 0, 0, 1, packedLight, packedOverlay, colour);
        IceCubeRenderer.renderPiecedFace(pose, buffer, new Vector3f(max.x, max.y, max.z), new Vector3f(0, 0, -sizeZ), new Vector3f(0, -sizeY, 0),
                tilesZ, tilesY, 1, 0, 0, packedLight, packedOverlay, colour);
        IceCubeRenderer.renderPiecedFace(pose, buffer, new Vector3f(min.x, max.y, min.z), new Vector3f(0, 0, sizeZ), new Vector3f(0, -sizeY, 0),
                tilesZ, tilesY, -1, 0, 0, packedLight, packedOverlay, colour);
        IceCubeRenderer.renderPiecedFace(pose, buffer, new Vector3f(min.x, max.y, min.z), new Vector3f(sizeX, 0, 0), new Vector3f(0, 0, sizeZ),
                tilesX, tilesZ, 0, 1, 0, packedLight, packedOverlay, colour);
        IceCubeRenderer.renderPiecedFace(pose, buffer, new Vector3f(min.x, min.y, max.z), new Vector3f(sizeX, 0, 0), new Vector3f(0, 0, -sizeZ),
                tilesX, tilesZ, 0, -1, 0, packedLight, packedOverlay, colour);
    }

    public static void renderEntityInIceCube(float entityYaw, PoseStack poseStack, MultiBufferSource buffer, int packedLight, Entity frozenEntity,
                                             EntityRenderDispatcher renderDispatcher) {
        poseStack.pushPose();
            poseStack.pushPose();
                poseStack.mulPose(Axis.YP.rotationDegrees(entityYaw));
                poseStack.mulPose(Axis.XP.rotationDegrees(frozenEntity.getXRot()));
            poseStack.popPose();

            renderDispatcher.render(frozenEntity, 0.0, 0.0, 0.0, 0.0F, entityYaw, poseStack, buffer, packedLight);
        poseStack.popPose();
    }

    private static int getTileCount(float worldSize) {
        return Math.max(1, Math.round(worldSize));
    }

    private static void renderPiecedFace(PoseStack.Pose pose, VertexConsumer buffer, Vector3f origin, Vector3f uAxis, Vector3f vAxis,
                                         int tilesU, int tilesV, float normalX, float normalY, float normalZ,
                                         int packedLight, int packedOverlay, int colour) {
        int piecesU = tilesU * 2;
        int piecesV = tilesV * 2;

        for (int i = 0; i < piecesU; i++) {
            for (int j = 0; j < piecesV; j++) {
                int quadrantU = i % 2;
                int quadrantV = j % 2;
                boolean borderU = i == 0 || i == piecesU - 1;
                boolean borderV = j == 0 || j == piecesV - 1;
                int section = borderU ? (borderV ? 0 : 2) : (borderV ? 3 : 1);
                float minU = section * 0.2F + quadrantU * 0.1F;
                float minV = quadrantV * 0.5F;
                float maxU = minU + 0.1F;
                float maxV = minV + 0.5F;
                float startU = (float) i / piecesU;
                float endU = (float) (i + 1) / piecesU;
                float startV = (float) j / piecesV;
                float endV = (float) (j + 1) / piecesV;

                IceCubeRenderer.addPiecedVertex(pose, buffer, origin, uAxis, vAxis, startU, startV, minU, minV,
                        normalX, normalY, normalZ, packedLight, packedOverlay, colour);
                IceCubeRenderer.addPiecedVertex(pose, buffer, origin, uAxis, vAxis, startU, endV, minU, maxV,
                        normalX, normalY, normalZ, packedLight, packedOverlay, colour);
                IceCubeRenderer.addPiecedVertex(pose, buffer, origin, uAxis, vAxis, endU, endV, maxU, maxV,
                        normalX, normalY, normalZ, packedLight, packedOverlay, colour);
                IceCubeRenderer.addPiecedVertex(pose, buffer, origin, uAxis, vAxis, endU, startV, maxU, minV,
                        normalX, normalY, normalZ, packedLight, packedOverlay, colour);
            }
        }
    }

    private static void addPiecedVertex(PoseStack.Pose pose, VertexConsumer buffer, Vector3f origin, Vector3f uAxis, Vector3f vAxis,
                                        float u, float v, float texU, float texV, float normalX, float normalY, float normalZ,
                                        int packedLight, int packedOverlay, int colour) {
        float x = origin.x + uAxis.x * u + vAxis.x * v;
        float y = origin.y + uAxis.y * u + vAxis.y * v;
        float z = origin.z + uAxis.z * u + vAxis.z * v;

        buffer.addVertex(pose, x, y, z)
                .setColor(colour)
                .setUv(texU, texV)
                .setOverlay(packedOverlay)
                .setLight(packedLight)
                .setNormal(pose, normalX, normalY, normalZ);
    }
}
