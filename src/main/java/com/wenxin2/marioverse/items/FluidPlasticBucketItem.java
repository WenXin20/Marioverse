package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.registries.ItemRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class FluidPlasticBucketItem extends BucketItem implements DispensibleContainerItem {
    public FluidPlasticBucketItem(Fluid fluid, Properties properties) {
        super(fluid, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player,
                this.content == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);

        if (hitResult.getType() == HitResult.Type.MISS)
            return InteractionResultHolder.pass(stack);
        else if (hitResult.getType() != HitResult.Type.BLOCK)
            return InteractionResultHolder.pass(stack);
        else {
            BlockPos pos = hitResult.getBlockPos();
            Direction direction = hitResult.getDirection();
            BlockPos posRelative = pos.relative(direction);

            if (!level.mayInteract(player, pos) || !player.mayUseItemAt(posRelative, direction, stack))
                return InteractionResultHolder.fail(stack);
            else if (this.content == Fluids.EMPTY) {
                BlockState state = level.getBlockState(pos);

                if (state.getBlock() instanceof BucketPickup bucketPickup) {
                    ItemStack stackPickup = bucketPickup.pickupBlock(player, level, pos, state);
                    if (!stackPickup.isEmpty()) {
                        player.awardStat(Stats.ITEM_USED.get(this));
                        bucketPickup.getPickupSound(state).ifPresent(p_150709_ -> player.playSound(p_150709_, 1.0F, 1.0F));
                        level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
                        ItemStack itemstack2 = ItemUtils.createFilledResult(stack, player, stackPickup);
                        if (!level.isClientSide)
                            CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)player, stackPickup);

                        return InteractionResultHolder.sidedSuccess(itemstack2, level.isClientSide());
                    }
                }

                return InteractionResultHolder.fail(stack);
            } else {
                BlockState state = level.getBlockState(pos);
                BlockPos posFluid = canBlockContainFluid(player, level, pos, state) ? pos : posRelative;
                if (this.emptyContents(player, level, posFluid, hitResult, stack)) {
                    this.checkExtraContent(player, level, stack, posFluid);
                    ItemStack newStack = ItemUtils.createFilledResult(stack, player, FluidPlasticBucketItem.getEmptySuccessItem(stack, player));

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
