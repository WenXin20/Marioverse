package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.registries.AttributesRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin {
    @ModifyArgs(method = "renderEntityInInventory", at = @At(value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"))
    private static void renderEntityInInventory(Args args, GuiGraphics graphics, float x, float y, float baseScale,
            Vector3f translation, Quaternionf rotation, @Nullable Quaternionf cameraRotation, LivingEntity entity) {
        float entityScale = mv$getScaleFromAttributes(entity);
        float zoom = 1.0F / entityScale;

        args.set(0, args.<Float>get(0) * zoom);
        args.set(1, args.<Float>get(1) * zoom);
        args.set(2, args.<Float>get(2) * zoom);
    }

    @Unique
    private static float mv$getScaleFromAttributes(LivingEntity living) {
        float height = 1.0F;
        float width = 1.0F;

        var heightScale = living.getAttribute(AttributesRegistry.HEIGHT_SCALE);
        if (heightScale != null)
            height = (float) heightScale.getValue();

        var widthScale = living.getAttribute(AttributesRegistry.WIDTH_SCALE);
        if (widthScale != null)
            width = (float) widthScale.getValue();

        return Math.max(height, width);
    }
}
