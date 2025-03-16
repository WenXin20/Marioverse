package com.wenxin2.marioverse.mixin;

import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.data.SlotTypeLoader;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.PowderSnowBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PowderSnowBlock.class)
public class PowderSnowBlockMixin {
    @Inject(method = "canEntityWalkOnPowderSnow", at = @At("HEAD"), cancellable = true)
    private static void modifyCanWalkOnPowderSnow(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.getItemBySlot(EquipmentSlot.FEET).is(ItemTags.FREEZE_IMMUNE_WEARABLES))
                cir.setReturnValue(true);

            AccessoriesCapability capability = AccessoriesCapability.get(livingEntity);
            if (capability != null) {
                AccessoriesContainer containerShoes = capability.getContainer(SlotTypeLoader.getSlotType(livingEntity, "costume_shoes"));

                if (containerShoes != null && containerShoes.getAccessories().getItem(0).is(ItemTags.FREEZE_IMMUNE_WEARABLES))
                    cir.setReturnValue(true);
            }
        }
    }
}
