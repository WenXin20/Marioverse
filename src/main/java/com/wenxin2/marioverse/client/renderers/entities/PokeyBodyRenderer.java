package com.wenxin2.marioverse.client.renderers.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.wenxin2.marioverse.client.models.entities.PokeyBodyModel;
import com.wenxin2.marioverse.entities.PokeyBodyEntity;
import com.wenxin2.marioverse.entities.PokeyEntity;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EquipmentSlot;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PokeyBodyRenderer extends GeoEntityRenderer<PokeyBodyEntity> {
    public PokeyBodyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PokeyBodyModel());
        this.shadowRadius = 0.5F;
    }

    @Override
    public void preRender(PoseStack poseStack, PokeyBodyEntity animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource,
                          @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
//        this.model.getBone("spikes").ifPresent(spike -> {
//            spike.setHidden(false);
//            if (!animatable.getItemBySlot(EquipmentSlot.HEAD).isEmpty())
//                spike.setHidden(true);
//
////            spike.setHidden(animatable.getData(DataAttachmentRegistry.IS_BLOOMING));
//        });
//
//        this.model.getBone("leaves").ifPresent(leaves -> {
//            if (animatable.getData(DataAttachmentRegistry.IS_BLOOMING))
//                leaves.setHidden(false);
//            else leaves.setHidden(true);
//        });
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}