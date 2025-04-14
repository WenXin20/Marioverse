package com.wenxin2.marioverse.client.renderers.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.entities.CrackableEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Crackiness;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.Color;

public class CrackedGeoLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {
    private final ResourceLocation textureLowCracks;
    private final ResourceLocation textureMediumCracks;
    private final ResourceLocation textureHighCracks;

    public CrackedGeoLayer(GeoRenderer<T> renderer, ResourceLocation textureLowCracks, ResourceLocation textureMediumCracks, ResourceLocation textureHighCracks) {
        super(renderer);
        this.textureLowCracks = textureLowCracks;
        this.textureMediumCracks = textureMediumCracks;
        this.textureHighCracks = textureHighCracks;
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderType armorRenderType = RenderType.armorCutoutNoCull(textureLowCracks);

        if (animatable instanceof Entity entity && !entity.isInvisible()) {
            if (entity instanceof CrackableEntity crackableEntity) {
                Crackiness.Level crackinessLevel = crackableEntity.getCrackiness();
                if (crackinessLevel == Crackiness.Level.LOW) {
                    getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, armorRenderType,
                            bufferSource.getBuffer(armorRenderType), partialTick, packedLight, OverlayTexture.NO_OVERLAY,
                            Color.WHITE.argbInt());
                } else if (crackinessLevel == Crackiness.Level.MEDIUM) {
                    armorRenderType = RenderType.armorCutoutNoCull(textureMediumCracks);
                    getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, armorRenderType,
                            bufferSource.getBuffer(armorRenderType), partialTick, packedLight, OverlayTexture.NO_OVERLAY,
                            Color.WHITE.argbInt());
                } else if (crackinessLevel == Crackiness.Level.HIGH) {
                    armorRenderType = RenderType.armorCutoutNoCull(textureHighCracks);
                    getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, armorRenderType,
                            bufferSource.getBuffer(armorRenderType), partialTick, packedLight, OverlayTexture.NO_OVERLAY,
                            Color.WHITE.argbInt());
                }
            }
        }
    }
}
