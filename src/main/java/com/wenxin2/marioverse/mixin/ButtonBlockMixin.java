package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.init.ConfigRegistry;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockItem.class)
public class ButtonBlockMixin {

    @Inject(method = "appendHoverText", at = @At("TAIL"))
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag options, CallbackInfo ci) {
        if (Minecraft.getInstance().options.advancedItemTooltips && ConfigRegistry.DISPLAY_BUTTON_TOOLTIP.get()) {
            Block block = ((BlockItem) (Object) this).getBlock();
            if (block instanceof ButtonBlock buttonBlock)
                list.add(Component.translatable("item.marioverse.button.ticksPressed", buttonBlock.ticksToStayPressed).withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
