package com.wenxin2.marioverse.client.renderers.entities.power_ups;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.wenxin2.marioverse.client.models.entities.SuperStarModel;
import com.wenxin2.marioverse.entities.power_ups.SuperStarEntity;
import com.wenxin2.marioverse.registries.ItemRegistry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SuperStarRenderer extends GeoEntityRenderer<SuperStarEntity> {
    private final ItemRenderer itemRenderer;

    public SuperStarRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SuperStarModel());
        this.itemRenderer = renderManager.getItemRenderer();
        this.shadowRadius = 0.35F;
    }

    @Override
    public void render(SuperStarEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        ItemStack stack = ItemRegistry.SUPER_STAR.toStack();

        translateAndRotateToHead(poseStack, entity);

        this.itemRenderer.renderStatic(stack, ItemDisplayContext.HEAD, packedLight, OverlayTexture.NO_OVERLAY,
                poseStack, bufferSource, entity.level(), entity.getId());
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    private void translateAndRotateToHead(PoseStack poseStack, SuperStarEntity entity) {
        float headYaw = entity.yHeadRot;
        float headPitch = entity.getXRot();

        poseStack.mulPose(Axis.YP.rotationDegrees(headYaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(headPitch));

        poseStack.translate(0.0F, -0.45F, -0.45F);
    }
}
