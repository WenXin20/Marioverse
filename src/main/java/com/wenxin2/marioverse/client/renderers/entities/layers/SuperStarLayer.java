package com.wenxin2.marioverse.client.renderers.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.client.renderers.SuperStarRenderType;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class SuperStarLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    public SuperStarLayer(RenderLayerParent<T, M> parentRenderer) {
        super(parentRenderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        ShaderInstance shader = SuperStarRenderType.SUPER_STAR_SHADER;
        M model = this.getParentModel();

        if (entity.getData(DataAttachmentRegistry.HAS_SUPER_STAR)) {
            if (shader != null) {
                float time = (entity.level().getGameTime() + partialTicks) * 0.2F;
                shader.safeGetUniform("Time").set(time);
            }

            ResourceLocation texture = this.getTextureLocation(entity);
            VertexConsumer consumer = bufferSource.getBuffer(SuperStarRenderType.superStar(texture));

            poseStack.pushPose();
                model.renderToBuffer(poseStack, consumer, 0xF000F0,
                        LivingEntityRenderer.getOverlayCoords(entity, 0.0F));
            poseStack.popPose();
        }
    }
}
