package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.client.renderers.SuperStarBufferSource;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

@Mixin(GeoArmorRenderer.class)
public abstract class GeoArmorRendererMixin {
    @Shadow(remap = false)
    protected Entity currentEntity;

    @ModifyArg(method = "renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;getArmorFoilBuffer(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;Z)Lcom/mojang/blaze3d/vertex/VertexConsumer;"),
            index = 0)
    private MultiBufferSource mv$useSuperStarFoilBuffer(MultiBufferSource bufferSource) {
        SuperStarBufferSource superStarBuffer = SuperStarBufferSource.getActive(this.currentEntity);
        return superStarBuffer != null ? superStarBuffer : bufferSource;
    }

    @ModifyArg(method = "renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V",
            at = @At(value = "INVOKE", target = "Lsoftware/bernie/geckolib/renderer/GeoArmorRenderer;defaultRender(Lcom/mojang/blaze3d/vertex/PoseStack;Lsoftware/bernie/geckolib/animatable/GeoAnimatable;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/VertexConsumer;FFI)V"),
            index = 2, remap = false)
    private MultiBufferSource mv$useSuperStarRenderBuffer(MultiBufferSource bufferSource) {
        SuperStarBufferSource superStarBuffer = SuperStarBufferSource.getActive(this.currentEntity);
        return superStarBuffer != null ? superStarBuffer : bufferSource;
    }
}
