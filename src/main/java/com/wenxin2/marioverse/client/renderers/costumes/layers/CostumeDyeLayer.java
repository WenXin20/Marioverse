package com.wenxin2.marioverse.client.renderers.costumes.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.client.renderers.costumes.CostumeRendererAccess;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class CostumeDyeLayer<T extends Item & GeoAnimatable> extends GeoRenderLayer<T> {
    public CostumeDyeLayer(GeoRenderer<T> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource,
                       VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        CostumeRendererAccess renderer = (CostumeRendererAccess) this.getRenderer();
        ItemStack stack = renderer.getCurrentStack();
        if (stack == null || stack.isEmpty())
            return;
        ResourceLocation baseTexture = this.renderer.getTextureLocation(animatable);
        ResourceLocation overlayTexture = this.deriveOverlayPath(baseTexture);

        this.renderLayer(poseStack, animatable, bakedModel, bufferSource,
                partialTick, packedLight, packedOverlay, overlayTexture, 0xFFFFFFFF);
    }

    private void renderLayer(PoseStack poseStack, T animatable, BakedGeoModel model, MultiBufferSource bufferSource,
                             float partialTick, int light, int overlay, ResourceLocation texture, int colour) {
        RenderType type = RenderType.entityTranslucent(texture);

        this.getRenderer().reRender(model, poseStack, bufferSource, animatable, type,
                bufferSource.getBuffer(type), partialTick, light, overlay, colour);
    }

    private ResourceLocation deriveOverlayPath(ResourceLocation base) {
        String path = base.getPath();
        String overlayPath = path.endsWith(".png")
                ? path.substring(0, path.length() - 4) + "_overlay.png"
                : path + "_overlay";
        return ResourceLocation.fromNamespaceAndPath(base.getNamespace(), overlayPath);
    }
}
