package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.entities.PorcupufferEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import java.util.Comparator;
import java.util.EnumSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.Path;

public class PickupItemGoal extends Goal {
    private final PathfinderMob mob;
    private final TagKey<Item> itemTag;
    private final double speedModifier;
    private final double searchRadius;
    private final boolean eatIfEdible;

    private ItemEntity targetItem;

    public PickupItemGoal(PathfinderMob mob, TagKey<Item> itemTag, double searchRadius, double speedModifier, boolean eatIfEdible) {
        this.mob = mob;
        this.itemTag = itemTag;
        this.speedModifier = speedModifier;
        this.searchRadius = searchRadius;
        this.eatIfEdible = eatIfEdible;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        this.targetItem = this.findNearestItem();
        if (this.targetItem == null)
            return false;

        Path path = this.mob.getNavigation().createPath(this.targetItem, 0);

        if (path == null)
            return false;
        return path.canReach();
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetItem != null && this.targetItem.isAlive()
                && this.targetItem.getItem().is(itemTag)
                && this.mob.distanceToSqr(this.targetItem) <= searchRadius * searchRadius;
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.targetItem, this.speedModifier);
    }

    @Override
    public void tick() {
        if (this.targetItem == null)
            return;

        this.mob.getNavigation().moveTo(this.targetItem, this.speedModifier);

        if (this.mob.distanceToSqr(this.targetItem) < 2.25D) {
            ItemStack stack = this.targetItem.getItem();
            boolean shouldOpenMouth = this.eatIfEdible
                    && !this.mob.getData(DataAttachmentRegistry.IS_EATING);

            if (this.mob instanceof PorcupufferEntity porcupuffer)
                porcupuffer.setMouthOpen(shouldOpenMouth);
            else this.mob.setData(DataAttachmentRegistry.IS_MOUTH_OPEN, shouldOpenMouth);

            if (this.eatIfEdible && stack.has(DataComponents.FOOD) && this.mob.getHealth() < this.mob.getMaxHealth()) {
                FoodProperties foodProperties = stack.getFoodProperties(this.mob);
                float nutrition = foodProperties != null ? (float) foodProperties.nutrition() : 1.0F;

                this.mob.playSound(this.mob.getEatingSound(stack), 1.0F, 1.0F);
                this.mob.heal(ConfigRegistry.PORCUPUFFER_HEALTH_HEALED.get().floatValue() * nutrition);
                this.mob.level().broadcastEntityEvent(this.mob, (byte)45);
                this.mob.gameEvent(GameEvent.EAT);
                stack.consume(1, this.mob);

                if (stack.isEmpty())
                    this.targetItem.discard();
            } else {
                this.mob.onItemPickup(this.targetItem);

                ItemStack remainder = this.mob.equipItemIfPossible(stack.copy());

                if (remainder.isEmpty())
                    this.targetItem.discard();
                else this.targetItem.setItem(remainder);
            }

            this.targetItem = null;
        }
    }

    @Override
    public void stop() {
        this.targetItem = null;
        this.mob.getNavigation().stop();

        if (this.eatIfEdible) {
            if (this.mob instanceof PorcupufferEntity porcupuffer)
                porcupuffer.setMouthOpen(false);
            else this.mob.setData(DataAttachmentRegistry.IS_MOUTH_OPEN, false);
        }
    }

    private ItemEntity findNearestItem() {
        return this.mob.level()
                .getEntitiesOfClass(ItemEntity.class, this.mob.getBoundingBox().inflate(this.searchRadius),
                        item -> item.isAlive() && !item.hasPickUpDelay()
                                && item.getItem().is(this.itemTag)).stream()
                .min(Comparator.comparingDouble(this.mob::distanceToSqr)).orElse(null);
    }
}