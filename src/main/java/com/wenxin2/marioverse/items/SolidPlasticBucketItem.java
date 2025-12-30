package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.registries.ItemRegistry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;

public class SolidPlasticBucketItem extends SolidBucketItem implements DispensibleContainerItem {
    public SolidPlasticBucketItem(Block block, SoundEvent soundEvent, Item.Properties properties) {
        super(block, soundEvent, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext p_151197_) {
        InteractionResult result = super.useOn(p_151197_);
        Player player = p_151197_.getPlayer();
        if (result.consumesAction() && player != null)
            player.setItemInHand(p_151197_.getHand(),
                    SolidPlasticBucketItem.getEmptySuccessItem(p_151197_.getItemInHand(), player));

        return result;
    }

    public static ItemStack getEmptySuccessItem(ItemStack stack, Player player) {
        return !player.hasInfiniteMaterials() ? new ItemStack(ItemRegistry.PLASTIC_BUCKET.get()) : stack;
    }
}
