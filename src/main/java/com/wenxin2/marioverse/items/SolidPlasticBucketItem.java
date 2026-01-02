package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.registries.ItemRegistry;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class SolidPlasticBucketItem extends SolidBucketItem implements DispensibleContainerItem {
    int tooltipLineAmt = 0;

    public SolidPlasticBucketItem(Block block, SoundEvent soundEvent, Item.Properties properties) {
        super(block, soundEvent, properties);
    }

    public SolidPlasticBucketItem(int tooltipLineAmt, Block block, SoundEvent soundEvent, Item.Properties properties) {
        super(block, soundEvent, properties);
        this.tooltipLineAmt = tooltipLineAmt;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
        if (Screen.hasShiftDown()) {
            list.add(Component.literal(""));
            for (int lineAmt = 1; lineAmt <= tooltipLineAmt; lineAmt++)
                list.add(Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt));
            list.add(Component.literal(""));
        } else list.add(Component.translatable(this.getDescriptionId() + ".tooltip"));
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (player != null) {
            ItemStack newStack = new ItemStack(ItemRegistry.PLASTIC_BUCKET.get());
            newStack.applyComponents(stack.getComponents());

            if (!player.isCreative())
                player.setItemInHand(context.getHand(), newStack);
            else ItemUtils.createFilledResult(stack, player, SolidPlasticBucketItem.getEmptySuccessItem(stack, player));

            player.awardStat(Stats.ITEM_USED.get(this));
            this.place(new BlockPlaceContext(context));
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }

    public static ItemStack getEmptySuccessItem(ItemStack stack, Player player) {
        ItemStack newStack = new ItemStack(ItemRegistry.PLASTIC_BUCKET.get());
        newStack.applyComponents(stack.getComponents());
        return !player.hasInfiniteMaterials() ? newStack : stack;
    }
}
