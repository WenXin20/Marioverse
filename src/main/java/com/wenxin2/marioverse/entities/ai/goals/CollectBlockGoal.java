package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.blocks.CoinBlock;
import com.wenxin2.marioverse.blocks.StarCoinBlock;
import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class CollectBlockGoal extends Goal {
    private final Mob mob;
    private final double speedModifier;
    private final int searchRadius;
    private final Predicate<BlockState> targetBlockState;

    private BlockPos targetPos;

    public CollectBlockGoal(Mob mob, int searchRadius, double speedModifier, Predicate<BlockState> targetBlockState) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.searchRadius = searchRadius;
        this.targetBlockState = targetBlockState;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        targetPos = this.findBlock();
        return targetPos != null;
    }

    @Override
    public void start() {
        if (targetPos != null)
            this.mob.getNavigation()
                    .moveTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, speedModifier);
    }

    @Override
    public boolean canContinueToUse() {
        return targetPos != null && !mob.getNavigation().isDone();
    }

    @Override
    public void tick() {
        if (targetPos != null) {
            this.mob.getNavigation().moveTo(this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), speedModifier);

            if (mob.blockPosition().closerToCenterThan(Vec3.atCenterOf(targetPos), mob.getBbWidth() + 1.2)) {
                this.collectBlock();
                targetPos = null;
            }
        }
    }

    private BlockPos findBlock() {
        BlockPos mobPos = mob.blockPosition();
        Level world = mob.level();

        for (BlockPos pos : BlockPos.betweenClosed(
                mobPos.offset(-searchRadius, -2, -searchRadius),
                mobPos.offset(searchRadius, 2, searchRadius))) {
            if (targetBlockState.test(world.getBlockState(pos)))
                return pos.immutable();
        }
        return null;
    }

    private void collectBlock() {
        Level world = mob.level();
        BlockState state = world.getBlockState(targetPos);
        ItemStack stack = new ItemStack(state.getBlock().asItem());

        if (targetBlockState.test(state)) {
            if (state.getBlock() instanceof StarCoinBlock starCoinBlock)
                StarCoinBlock.collectCoin(starCoinBlock, world, state, targetPos, mob, stack);
            else if (state.getBlock() instanceof CoinBlock)
                CoinBlock.collectCoin(world, state, targetPos, mob, stack);
            else {
                world.destroyBlock(targetPos, false);
                world.playSound(mob, targetPos, state.getBlock().asItem().getBreakingSound(), SoundSource.BLOCKS, 1.0F, 1.0F);

                if (mob.getMainHandItem().isEmpty()) {
                    mob.setItemInHand(InteractionHand.MAIN_HAND, stack);
                    mob.swing(InteractionHand.MAIN_HAND);
                } else if (mob.getOffhandItem().isEmpty()) {
                    mob.setItemInHand(InteractionHand.OFF_HAND, stack);
                    mob.swing(InteractionHand.OFF_HAND);
                } else if (mob instanceof InventoryCarrier carrier) {
                    SimpleContainer inventory = carrier.getInventory();
                    mob.swing(InteractionHand.MAIN_HAND);

                    for (int i = 0; i < inventory.getContainerSize(); i++) {
                        if (inventory.getItem(i).isEmpty()) {
                            inventory.setItem(i, stack);
                            break;
                        }
                    }
                } else if (mob instanceof Container container) {
                    mob.swing(InteractionHand.MAIN_HAND);

                    for (int i = 0; i < container.getContainerSize(); i++) {
                        if (container.getItem(i).isEmpty()) {
                            container.setItem(i, stack);
                            break;
                        }
                    }
                }
            }
        }
    }
}