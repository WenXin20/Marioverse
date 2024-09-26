package com.wenxin2.marioverse.client.renderers.accesories;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.wenxin2.marioverse.client.renderers.ArmorRenderingExtension;
import com.wenxin2.marioverse.init.ConfigRegistry;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.client.Side;
import io.wispforest.accessories.api.client.SimpleAccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class OneUpRenderer implements SimpleItemRenderer {
    @Override
    public <M extends LivingEntity> void render(ItemStack stack, SlotReference slotReference, PoseStack poseStack,
                                                EntityModel<M> model, MultiBufferSource buffer,
                                                int light, float limbSwing, float limbSwingAmount, float partialTicks,
                                                float ageInTicks, float netHeadYaw, float headPitch) {
        if (ConfigRegistry.RENDER_ONE_UP_CURIO.get()) {
            poseStack.pushPose();
                this.translateIfSneaking(poseStack, slotReference.entity());
                EntityRenderer<? super LivingEntity> render = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(slotReference.entity());
                if (render instanceof LivingEntityRenderer) {
                    if (model instanceof HumanoidModel humanoidModel) {
                        this.rotateIfSneaking(poseStack, slotReference.entity(), humanoidModel);
                    }
                }
                poseStack.mulPose(Direction.DOWN.getRotation());
                poseStack.translate(0.15F, -0.45F, 0.145F);
                poseStack.scale(0.25F, 0.25F, 0.45F);
                Minecraft.getInstance().getItemRenderer()
                        .renderStatic(stack, ItemDisplayContext.NONE, light, OverlayTexture.NO_OVERLAY,
                                poseStack, buffer, slotReference.entity().level(), 0);
            poseStack.popPose();
        }
    }
}
