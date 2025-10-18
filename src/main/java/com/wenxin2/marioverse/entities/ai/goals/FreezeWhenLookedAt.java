package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.entities.BooEntity;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

public class FreezeWhenLookedAt extends Goal {
    private final Mob mob;
    @Nullable private LivingEntity target;
    private final TagKey<EntityType<?>> entityTag;

    public FreezeWhenLookedAt(Mob mob, TagKey<EntityType<?>> entityTag) {
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        this.entityTag = entityTag;
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        this.target = this.mob.getTarget();
        if (this.target == null) {
            return false;
        } else if (this.target.getType().is(entityTag)) {
            double d0 = this.target.distanceToSqr(this.mob);
            return d0 > 256.0 ? false : this.mob instanceof BooEntity boo && boo.isLookingAtMe(this.target);
        }
        return false;
    }

    @Override
    public void start() {
        this.mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.target != null)
            this.mob.getLookControl().setLookAt(this.target.getX(), this.target.getEyeY(), this.target.getZ());
    }
}
