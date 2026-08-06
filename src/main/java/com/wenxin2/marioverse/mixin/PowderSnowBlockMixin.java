package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.registries.DataComponentRegistry;
import com.wenxin2.marioverse.registries.PowerUpTypeRegistry;
import java.util.Map;
import java.util.Optional;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.PowderSnowBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

@Mixin(PowderSnowBlock.class)
public class PowderSnowBlockMixin {
    @Inject(method = "canEntityWalkOnPowderSnow", at = @At("RETURN"), cancellable = true)
    private static void canEntityWalkOnPowderSnow(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ())
            return;

        if (entity instanceof LivingEntity livingEntity) {
            Optional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(livingEntity);

            if (curiosInventory.isPresent()) {
                Map<String, ICurioStacksHandler> curios = curiosInventory.get().getCurios();
                ICurioStacksHandler slotShoes = curios.get("costume_shoes");

                if (slotShoes != null) {
                    ItemStack stack = slotShoes.getStacks().getStackInSlot(0);
                    if (stack.is(ItemTags.FREEZE_IMMUNE_WEARABLES)
                            && stack.get(DataComponentRegistry.POWER_UP_TYPE.get()) == PowerUpTypeRegistry.ICE_FLOWER)
                        cir.setReturnValue(true);
                }
            }
        }
    }
}
