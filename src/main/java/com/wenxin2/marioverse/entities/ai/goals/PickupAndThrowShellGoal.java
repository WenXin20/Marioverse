package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.entities.KoopaShellEntity;
import com.wenxin2.marioverse.items.KoopaShellItem;
import com.wenxin2.marioverse.registries.SoundRegistry;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Optional;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class PickupAndThrowShellGoal extends Goal {
    private ItemStack heldShell = ItemStack.EMPTY;
    private final Mob mob;
    private LivingEntity target;
    private int cooldown;

    public PickupAndThrowShellGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        }

        if (mob.getMainHandItem().getItem() instanceof KoopaShellItem || !heldShell.isEmpty()) {
            this.target = mob.getTarget();
            return target != null && mob.hasLineOfSight(target) && mob.distanceToSqr(target) < 16 * 16;
        } else return this.findNearbyShell() != null;
    }

    @Override
    public void tick() {
        if (mob.getMainHandItem().getItem() instanceof KoopaShellItem && target != null) {
            mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (mob.distanceToSqr(target) < 16 * 16) {
                this.throwShellAt(target, mob.getMainHandItem());
                cooldown = 100;
            }
        } else if (!heldShell.isEmpty() && target != null) {
            mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (mob.distanceToSqr(target) < 16 * 16) {
                this.throwShellAt(target, heldShell);
                cooldown = 100;
            }
        } else {
            Entity shell = this.findNearbyShell();
            if (shell != null) {
                mob.getNavigation().moveTo(shell, 1.2);
                if (mob.distanceToSqr(shell) < 2.0)
                    this.pickUpShell(shell);
            }
        }
    }

    private Entity findNearbyShell() {
        Level level = mob.level();
        AABB searchBox = mob.getBoundingBox().inflate(8.0);

        Optional<KoopaShellEntity> entityShell = level.getEntitiesOfClass(KoopaShellEntity.class, searchBox)
                .stream().filter(e -> e.getDeltaMovement().lengthSqr() < 0.01 && e.isAlive())
                .min(Comparator.comparingDouble(mob::distanceToSqr));

        if (entityShell.isPresent()) return entityShell.get();

        Optional<ItemEntity> itemShell = level.getEntitiesOfClass(ItemEntity.class, searchBox)
                .stream().filter(e -> e.getItem().getItem() instanceof KoopaShellItem)
                .min(Comparator.comparingDouble(mob::distanceToSqr));

        return itemShell.orElse(null);
    }

    private void pickUpShell(Entity entity) {
        if (entity instanceof ItemEntity itemEntity) {
            if (this.hasHandSlot(mob))
                mob.setItemInHand(InteractionHand.MAIN_HAND, itemEntity.getItem().copy());
            else heldShell = itemEntity.getItem().copy();

            mob.swing(mob.getUsedItemHand());
            mob.level().playSound(mob, mob.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL, 1.0F, 1.0F);
            itemEntity.discard();
        } else if (entity instanceof KoopaShellEntity shell && shell.getPickResult() != null) {
            ItemStack shellItem = new ItemStack(shell.getPickResult().getItem());

            if (this.hasHandSlot(mob))
                mob.setItemInHand(InteractionHand.MAIN_HAND, shellItem);
            else heldShell = shellItem;

            mob.swing(mob.getUsedItemHand());
            mob.level().playSound(mob, mob.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL, 1.0F, 1.0F);
            shell.discard();
        }
    }

    private void throwShellAt(LivingEntity target, ItemStack stack) {
        if (!(stack.getItem() instanceof KoopaShellItem shellItem)) return;

        EntityType<?> type = heldShell.isEmpty() ? shellItem.getType(stack) : shellItem.getType(heldShell);
        Entity entity = type.create(mob.level());

        if (entity instanceof KoopaShellEntity koopaShell) {
            double speed = 1.0;
            double spawnDistance = 1.0;

            mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            mob.yRotO = mob.getYRot();
            mob.setYRot(Mth.wrapDegrees(mob.getYRot()));
            mob.swing(mob.getUsedItemHand());

            Vec3 look = mob.getLookAngle();
            Vec3 spawnPos = mob.position()
                    .add(look.x * spawnDistance, mob.getEyeHeight() - 0.6 + look.y * spawnDistance, look.z * spawnDistance);

            entity.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            mob.level().addFreshEntity(entity);

            if (look.y >= 0.9) {
                entity.setDeltaMovement(look.x, 1.25, look.z);
                mob.level().playSound(mob, mob.blockPosition(), SoundRegistry.KOOPA_SHELL_THROWN_UP.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
            } else {
                entity.setDeltaMovement(look.x * speed, look.y * speed, look.z * speed);
                mob.level().playSound(mob, mob.blockPosition(), SoundRegistry.KOOPA_SHELL_THROWN.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
            }

            koopaShell.hasImpulse = true;
            koopaShell.setOwner(mob);
            mob.level().gameEvent(mob, GameEvent.ENTITY_PLACE, spawnPos);
            stack.consume(1, mob);
            heldShell = ItemStack.EMPTY;
            cooldown = 100;
        }
    }

    private boolean hasHandSlot(LivingEntity entity) {
        try {
            entity.getItemBySlot(EquipmentSlot.MAINHAND);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
