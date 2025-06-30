package com.wenxin2.marioverse.entities.ai.goals;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class ChaseEntityGoal extends Goal {
    private LivingEntity target;
    private final Mob mob;

    public ChaseEntityGoal(Mob mob, LivingEntity target) {
        this.mob = mob;
        this.target = target;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        this.findNearbyTarget();
        return true;
    }

    @Override
    public void tick() {
            Entity entity = this.findNearbyTarget();
        mob.getNavigation().moveTo(entity, 1.0);
    }

    private Entity findNearbyTarget() {
        Level level = mob.level();
        AABB searchBox = mob.getBoundingBox().inflate(8.0);

        Optional<Entity> entity = level.getEntities(target, searchBox)
                .stream().filter(e -> e.getDeltaMovement().lengthSqr() < 0.1 && e.isAlive())
                .min(Comparator.comparingDouble(mob::distanceToSqr));

        return entity.orElse(null);
    }
}
