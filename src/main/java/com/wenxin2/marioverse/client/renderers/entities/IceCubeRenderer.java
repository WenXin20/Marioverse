package com.wenxin2.marioverse.client.renderers.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wenxin2.marioverse.client.models.entities.IceCubeModel;
import com.wenxin2.marioverse.entities.IceCubeEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IceCubeRenderer extends GeoEntityRenderer<IceCubeEntity> {
    public IceCubeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new IceCubeModel());
    }

    @Override
    public void render(IceCubeEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        Entity frozenEntity = entity.getOrCreateDisplayEntity(entity.level());
        if (frozenEntity != null) {
            float width = frozenEntity.getBbWidth();
            float height = frozenEntity.getBbHeight();

            poseStack.pushPose();
            this.withScale(width * 2.0F, height * 1.5F);
            poseStack.translate(0, 0.05, 0);
            Minecraft.getInstance().getEntityRenderDispatcher().render(frozenEntity,
                    0, 0, 0, 0, partialTicks, poseStack, buffer, packedLight);
            poseStack.popPose();
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
