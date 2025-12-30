package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.registries.ItemRegistry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class SolidPlasticBucketItem extends SolidBucketItem implements DispensibleContainerItem {
    public SolidPlasticBucketItem(Block block, SoundEvent soundEvent, Item.Properties properties) {
        super(block, soundEvent, properties);
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (player != null) {
            ItemStack newStack = new ItemStack(ItemRegistry.PLASTIC_BUCKET.get());
            newStack.applyComponents(stack.getComponents());

            player.awardStat(Stats.ITEM_USED.get(this));
            player.setItemInHand(context.getHand(), newStack);
            stack.consume(1, player);
            this.place(new BlockPlaceContext(context));
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }
}
