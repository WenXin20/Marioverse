package com.wenxin2.marioverse.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.SnowGolemHeadLayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SnowGolemHeadLayer.class)
public abstract class SnowGolemHeadLayerMixin {

    @ModifyExpressionValue(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/animal/SnowGolem;FFFFFF)V", require = 0,
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/Blocks;CARVED_PUMPKIN:Lnet/minecraft/world/level/block/Block;"))
    private Block mv$render(Block original, PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                            SnowGolem golem, float limbSwing, float limbSwingAmount, float partialTick,
                            float ageInTicks, float netHeadYaw, float headPitch) {
        String head = golem.getData(DataAttachmentRegistry.SNOW_GOLEM_HEAD);
        if (head.isEmpty())
            return original;

        ResourceLocation id = ResourceLocation.tryParse(head);
        if (id == null || !BuiltInRegistries.BLOCK.containsKey(id))
            return original;
        return BuiltInRegistries.BLOCK.get(id);
    }
}
