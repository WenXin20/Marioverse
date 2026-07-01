package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.entities.KoopaShellEntity;
import com.wenxin2.marioverse.items.KoopaShellItem;
import com.wenxin2.marioverse.registries.TagRegistry;
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
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class PickupAndThrowShellGoal extends Goal {
    private ItemStack heldShell = ItemStack.EMPTY;
    private LivingEntity target;
    private final Mob mob;
    private int aimingTicks = 0;
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

        if (this.mob.getMainHandItem().getItem() instanceof KoopaShellItem
                || this.mob.getOffhandItem().getItem() instanceof KoopaShellItem
                || !heldShell.isEmpty()) {
            this.target = this.mob.getTarget();
            return true;
        } else return this.findNearbyShell() != null;
    }

    @Override
    public void tick() {
        if (cooldown > 0) {
            cooldown--;
            return;
        }

        boolean hasShellInMainHand = this.mob.getMainHandItem().getItem() instanceof KoopaShellItem;
        boolean hasShellInOffHand = this.mob.getOffhandItem().getItem() instanceof KoopaShellItem;
        boolean hasShellInMemory = !heldShell.isEmpty();

        if ((hasShellInMainHand || hasShellInOffHand || hasShellInMemory)) {
            if (target != null)
                this.mob.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ());
            this.mob.yRotO = this.mob.getYHeadRot();
            this.mob.setYRot(Mth.wrapDegrees(this.mob.getYHeadRot()));

            if (aimingTicks < 10) {
                aimingTicks++;
                return;
            }

            if (!(mob instanceof Monster) && !(mob instanceof AbstractGolem)) {
                this.throwShellAt(hasShellInMainHand ? this.mob.getMainHandItem() : heldShell);
                this.throwShellAt(hasShellInOffHand ? this.mob.getOffhandItem() : heldShell);
                cooldown = 100;
            } else if (target != null && this.mob.distanceToSqr(target) < 16 * 16
                    && this.mob.getSensing().hasLineOfSight(target)
                    && this.mob.getLookControl().isLookingAtTarget()) {
                this.throwShellAt(hasShellInMainHand ? this.mob.getMainHandItem() : heldShell);
                this.throwShellAt(hasShellInOffHand ? this.mob.getOffhandItem() : heldShell);
                cooldown = 100;
                aimingTicks = 0;
            }
        } else {
            aimingTicks = 0;
            Entity shell = this.findNearbyShell();
            if (shell != null) {
                this.mob.getNavigation().moveTo(shell, 1.0);
                if (this.mob.distanceToSqr(shell) < this.mob.getBbWidth() + 2.5)
                    this.pickUpShell(shell);
            }
        }
    }

    private Entity findNearbyShell() {
        Level level = this.mob.level();
        AABB searchBox = this.mob.getBoundingBox().inflate(8.0);

        Optional<KoopaShellEntity> entityShell = level.getEntitiesOfClass(KoopaShellEntity.class, searchBox)
                .stream().filter(e -> e.getDeltaMovement().lengthSqr() < 0.1 && e.isAlive())
                .min(Comparator.comparingDouble(mob::distanceToSqr));

        if (entityShell.isPresent()) return entityShell.get();

        Optional<ItemEntity> itemShell = level.getEntitiesOfClass(ItemEntity.class, searchBox)
                .stream().filter(e -> e.getItem().getItem() instanceof KoopaShellItem)
                .min(Comparator.comparingDouble(mob::distanceToSqr));

        return itemShell.orElse(null);
    }

    private void pickUpShell(Entity entity) {
        float pitch = 0.9F + this.mob.level().random.nextFloat() * 0.2F;

        if (entity instanceof ItemEntity itemEntity) {
            if (this.hasMainHandSlot(mob) && this.mob.getItemInHand(InteractionHand.MAIN_HAND).isEmpty())
                this.mob.setItemInHand(InteractionHand.MAIN_HAND, itemEntity.getItem());
            else if (this.hasOffHandSlot(mob) && this.mob.getItemInHand(InteractionHand.OFF_HAND).isEmpty())
                this.mob.setItemInHand(InteractionHand.OFF_HAND, itemEntity.getItem());

            heldShell = itemEntity.getItem().copy();
            this.mob.swing(this.mob.getUsedItemHand());
            this.mob.level().playSound(mob, this.mob.blockPosition(), SoundEvents.ITEM_PICKUP,
                    SoundSource.NEUTRAL, 1.0F, pitch);
            itemEntity.discard();
        } else if (entity instanceof KoopaShellEntity shell && shell.getPickResult() != null) {
            ItemStack shellItem = new ItemStack(shell.getPickResult().getItem());

            if (this.hasMainHandSlot(mob) && this.mob.getItemInHand(InteractionHand.MAIN_HAND).isEmpty())
                this.mob.setItemInHand(InteractionHand.MAIN_HAND, shellItem);
            else if (this.hasOffHandSlot(mob) && this.mob.getItemInHand(InteractionHand.OFF_HAND).isEmpty())
                this.mob.setItemInHand(InteractionHand.OFF_HAND, shellItem);

            heldShell = shellItem;
            this.mob.swing(this.mob.getUsedItemHand());
            this.mob.level().playSound(mob, this.mob.blockPosition(), SoundEvents.ITEM_PICKUP,
                    SoundSource.NEUTRAL, 1.0F, pitch);
            shell.discard();
        }
    }

    private void throwShellAt(ItemStack stack) {
        if (!(stack.getItem() instanceof KoopaShellItem shellItem)) return;

        EntityType<?> type = heldShell.isEmpty() ? shellItem.getType(stack) : shellItem.getType(heldShell);
        Entity entity = type.create(this.mob.level());

        if (entity instanceof KoopaShellEntity shell) {
            shellItem.throwShell(this.mob.level(), mob, shell, stack);

            this.mob.yRotO = this.mob.getYRot();
            this.mob.setYRot(Mth.wrapDegrees(this.mob.getYRot()));
            this.mob.swing(this.mob.getUsedItemHand());

            if (!this.mob.getType().is(TagRegistry.HAS_INFINITE_SHELL_AMMO))
                heldShell = ItemStack.EMPTY;
            cooldown = 100;
        }
    }

    private boolean hasMainHandSlot(LivingEntity entity) {
        try {
            entity.getItemBySlot(EquipmentSlot.MAINHAND);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean hasOffHandSlot(LivingEntity entity) {
        try {
            entity.getItemBySlot(EquipmentSlot.OFFHAND);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
