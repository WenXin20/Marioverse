package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.items.OneUpMushroomItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityClientMixin extends Entity {
    public LivingEntityClientMixin(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "handleEntityEvent", at = @At("HEAD"))
    private void handleEntityEvent(byte id, CallbackInfo info) {
        LivingEntity livingEntity = (LivingEntity)(Object)this;
        if (id == 127) {
            if (this.level().isClientSide) {
                if (livingEntity instanceof Player player) {
                    Minecraft.getInstance().gameRenderer.displayItemActivation(mv$find1Up(player));
                }
            }
        } else super.handleEntityEvent(id);
    }

    @Unique
    private static ItemStack mv$find1Up(Player player) {
        for (InteractionHand interactionhand : InteractionHand.values()) {
            ItemStack stack = player.getItemInHand(interactionhand);
            if (stack.getItem() instanceof OneUpMushroomItem) {
                return stack;
            }
        }

        return new ItemStack(ItemRegistry.ONE_UP_MUSHROOM.get());
    }
}
