package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.registries.ItemRegistry;
import java.util.List;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class FluidPlasticBucketItem extends BucketItem implements DispensibleContainerItem {
    int tooltipLineAmt = 0;

    public FluidPlasticBucketItem(Fluid fluid, Properties properties) {
        super(fluid, properties);
    }

    public FluidPlasticBucketItem(int tooltipLineAmt, Fluid fluid, Properties properties) {
        super(fluid, properties);
        this.tooltipLineAmt = tooltipLineAmt;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
        if (Screen.hasShiftDown() && this.tooltipLineAmt > 0) {
            list.add(Component.literal(""));
            for (int lineAmt = 1; lineAmt <= tooltipLineAmt; lineAmt++)
                list.add(Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt));
            list.add(Component.literal(""));
        } else if (this.tooltipLineAmt > 0) list.add(Component.translatable(this.getDescriptionId() + ".tooltip"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        ItemStack stack = player.getItemInHand(hand);
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = level.getBlockState(pos);

        if (hitResult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(stack);
        } else if (hitResult.getType() != HitResult.Type.BLOCK
                && state.getBlock() instanceof BucketPickup)
            return InteractionResultHolder.pass(stack);
        else {
            Direction direction = hitResult.getDirection();
            BlockPos posRelative = pos.relative(direction);

            if (!level.mayInteract(player, pos) || !player.mayUseItemAt(posRelative, direction, stack))
                return InteractionResultHolder.fail(stack);
            else {
                BlockPos posFluid = canBlockContainFluid(player, level, pos, state) ? pos : posRelative;
                if (this.emptyContents(player, level, posFluid, hitResult, stack)) {
                    ItemStack newStack = new ItemStack(ItemRegistry.PLASTIC_BUCKET.get());
                    newStack.applyComponents(stack.getComponents());
                    this.checkExtraContent(player, level, stack, posFluid);

                    if (!player.isCreative())
                        player.setItemInHand(hand, newStack);
                    else ItemUtils.createFilledResult(newStack, player, FluidPlasticBucketItem.getEmptySuccessItem(stack, player));

                    if (player instanceof ServerPlayer)
                        CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, posFluid, stack);
                    player.awardStat(Stats.ITEM_USED.get(this));

                    return InteractionResultHolder.sidedSuccess(newStack, level.isClientSide());
                } else return InteractionResultHolder.fail(stack);
            }
        }
    }

    public static ItemStack getEmptySuccessItem(ItemStack stack, Player player) {
        ItemStack newStack = new ItemStack(ItemRegistry.PLASTIC_BUCKET.get());
        newStack.applyComponents(stack.getComponents());
        return !player.hasInfiniteMaterials() ? newStack : stack;
    }
}
