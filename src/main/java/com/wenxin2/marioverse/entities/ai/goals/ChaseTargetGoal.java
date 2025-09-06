package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.entities.power_ups.BaseMushroomEntity;
import com.wenxin2.marioverse.entities.power_ups.BasePowerUpEntity;
import com.wenxin2.marioverse.entities.power_ups.FireFlowerEntity;
import com.wenxin2.marioverse.entities.power_ups.IceFlowerEntity;
import com.wenxin2.marioverse.entities.power_ups.MushroomEntity;
import com.wenxin2.marioverse.entities.power_ups.OneUpMushroomEntity;
import com.wenxin2.marioverse.entities.power_ups.SuperStarEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class ChaseTargetGoal<T extends LivingEntity> extends Goal {
    private final Class<T> targetClass;
    private final Mob mob;
    private T target;
    private int chaseTick;
    private final double speedModifier;

    public ChaseTargetGoal(Mob mob, double speedModifier, Class<T> targetClass) {
        this.mob = mob;
        this.targetClass = targetClass;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        this.target = this.findTarget();

        if (this.mob instanceof AbilitiesHandler handler) {
            if (this.target instanceof MushroomEntity && !handler.mv$hasSuperMushroom())
                return true;
            else if (this.target instanceof FireFlowerEntity && !handler.mv$hasFireFlower())
                return true;
            else if (this.target instanceof IceFlowerEntity && !handler.mv$hasIceFlower())
                return true;
            else if (this.target instanceof OneUpMushroomEntity
                    && (this.mob.getType().is(TagRegistry.CAN_CONSUME_ONE_UPS) || ConfigRegistry.ONE_UP_HEALS_ALL_MOBS.get()))
                return true;
            else if (this.target instanceof SuperStarEntity
                    && (this.mob.getType().is(TagRegistry.CAN_CONSUME_SUPER_STARS) || ConfigRegistry.SUPER_STAR_POWERS_ALL_MOBS.get()))
                return true;
            else if (!(this.target instanceof BasePowerUpEntity) && !(this.target instanceof BaseMushroomEntity))
                return this.target != null;
            else return false;
        } else return false;
    }

    @Override
    public boolean canContinueToUse() {
        return this.target != null && this.target.isAlive() && this.mob.distanceToSqr(this.target) > 1.0;
    }

    @Override
    public void start() {
        if (this.target != null)
            this.mob.getNavigation().moveTo(this.target, speedModifier);
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
        this.target = null;
    }

    @Override
    public void tick() {
        Level world = this.mob.level();

        if (this.mob.getDeltaMovement().horizontalDistance() == 0)
            chaseTick++;

        if (chaseTick == 50)
            this.mob.getNavigation().stop();

        if (this.target != null) {
            this.mob.getLookControl().setLookAt(this.target.getX(), this.target.getEyeY(), this.target.getZ());
            this.mob.getNavigation().moveTo(this.target, speedModifier);

            if (this.mob.distanceToSqr(this.target) < mob.getBbWidth() + 2.5 && this.mob instanceof AbilitiesHandler handler) {
                if (this.target instanceof MushroomEntity powerUp && !handler.mv$hasSuperMushroom())
                    handler.applyMushroomPowerUp(world, this.mob, powerUp, ConfigRegistry.SUPER_MUSHROOM_HEALTH_HEALED.get().floatValue());
                else if (this.target instanceof FireFlowerEntity powerUp && !handler.mv$hasFireFlower())
                    handler.applyFireFlowerPowerUp(world, this.mob, powerUp);
                else if (this.target instanceof IceFlowerEntity powerUp && !handler.mv$hasIceFlower())
                    handler.applyIceFlowerPowerUp(world, this.mob, powerUp);
                else if (this.target instanceof OneUpMushroomEntity powerUp
                        && (this.mob.getType().is(TagRegistry.CAN_CONSUME_ONE_UPS) || ConfigRegistry.ONE_UP_HEALS_ALL_MOBS.get()))
                    handler.applyOneUpMushroomPowerUp(world, new ItemStack(ItemRegistry.ONE_UP_MUSHROOM.get()), this.mob, powerUp);
                else if (this.target instanceof SuperStarEntity powerUp
                        && (this.mob.getType().is(TagRegistry.CAN_CONSUME_SUPER_STARS) || ConfigRegistry.SUPER_STAR_POWERS_ALL_MOBS.get()))
                    handler.applySuperStarPowerUp(world, this.mob, powerUp);

                if (this.target instanceof BasePowerUpEntity || this.target instanceof BaseMushroomEntity) {
                    this.target.discard();
                    this.mob.swing(this.mob.getUsedItemHand());
                }
            }
        }
    }

    private T findTarget() {
        Level world = this.mob.level();
        AABB searchBox = this.mob.getBoundingBox().inflate(8.0);

        List<T> entities = world.getEntitiesOfClass(this.targetClass, searchBox,
                entity -> entity != this.mob && entity.isAlive());

        return entities.isEmpty() ? null : entities.getFirst();
    }
}
