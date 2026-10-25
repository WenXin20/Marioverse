package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.registries.ItemRegistry;
import java.util.List;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

public class PlasticMilkBucketItem extends MilkBucketItem {
    int tooltipLineAmt = 0;

    public PlasticMilkBucketItem(Properties properties) {
        super(properties);
    }

    public PlasticMilkBucketItem(int tooltipLineAmt, Properties properties) {
        super(properties);
        this.tooltipLineAmt = tooltipLineAmt;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
        if (Screen.hasShiftDown() && this.tooltipLineAmt > 0) {
            list.add(Component.literal(""));
            for (int lineAmt = 1; lineAmt <= tooltipLineAmt; lineAmt++)
                list.add(Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt));
            list.add(Component.literal(""));
        } else if (this.tooltipLineAmt > 0)
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip"));
    }

    @NotNull
    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        if (!this.hasCraftingRemainingItem(stack))
            return ItemStack.EMPTY;

        ItemStack newStack = new ItemStack(this.getCraftingRemainingItem());
        newStack.applyComponents(stack.getComponents());
        return newStack;
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Fluid milk = PlasticMilkBucketItem.getPlaceableMilk();
        if (milk == Fluids.EMPTY)
            return super.useOn(context);

        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        Direction direction = context.getClickedFace();
        BlockPos clickedPos = context.getClickedPos();
        BlockPos pos = level.getBlockState(clickedPos).canBeReplaced(milk) ? clickedPos : clickedPos.relative(direction);
        BlockState state = level.getBlockState(pos);
        boolean isContainer = state.getBlock() instanceof LiquidBlockContainer container
                && container.canPlaceLiquid(player, level, pos, state, milk);

        if (player != null && !player.mayUseItemAt(pos, direction, stack))
            return InteractionResult.FAIL;
        if (!state.isAir() && !state.canBeReplaced(milk) && !isContainer)
            return InteractionResult.FAIL;

        if (isContainer)
            ((LiquidBlockContainer) state.getBlock()).placeLiquid(level, pos, state, milk.defaultFluidState());
        else {
            if (!level.isClientSide && !state.liquid() && !state.isAir())
                level.destroyBlock(pos, true);
            level.setBlock(pos, milk.defaultFluidState().createLegacyBlock(), 11);
        }

        SoundEvent sound = milk.getFluidType().getSound(player, level, pos, SoundActions.BUCKET_EMPTY);
        level.playSound(player, pos, sound != null ? sound : SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS,
                1.0F, 0.9F + level.random.nextFloat() * 0.2F);
        level.gameEvent(player, GameEvent.FLUID_PLACE, pos);

        if (player != null) {
            if (player instanceof ServerPlayer serverPlayer)
                CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, pos, stack);
            player.awardStat(Stats.ITEM_USED.get(this));

            if (!player.hasInfiniteMaterials()) {
                ItemStack newStack = new ItemStack(ItemRegistry.PLASTIC_BUCKET.get());
                newStack.applyComponents(stack.getComponents());
                player.setItemInHand(context.getHand(), newStack);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @NotNull
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }

        if (!level.isClientSide)
            entity.removeEffectsCuredBy(EffectCures.MILK);

        if (entity instanceof Player player) {
            ItemStack newStack = new ItemStack(ItemRegistry.PLASTIC_BUCKET.get());
            newStack.applyComponents(stack.getComponents());
            return ItemUtils.createFilledResult(stack, player, newStack, false);
        }

        stack.consume(1, entity);
        return stack;
    }

    public static Fluid getPlaceableMilk() {
        for (Holder<Fluid> holder : BuiltInRegistries.FLUID.getTagOrEmpty(Tags.Fluids.MILK)) {
            Fluid fluid = holder.value();
            if (fluid.isSource(fluid.defaultFluidState()) && !fluid.defaultFluidState().createLegacyBlock().isAir())
                return fluid;
        }
        return Fluids.EMPTY;
    }
}
