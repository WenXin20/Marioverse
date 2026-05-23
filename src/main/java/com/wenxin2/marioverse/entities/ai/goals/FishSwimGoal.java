package com.wenxin2.marioverse.entities.ai.goals;

import java.util.Comparator;
import java.util.List;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class FishSwimGoal extends RandomSwimmingGoal {
    private final PathfinderMob mob;
    private final boolean canRandomSwim;
    private final TagKey<EntityType<?>> lureEntityTag;
    private final double lureRadius;

    public FishSwimGoal(PathfinderMob mob, TagKey<EntityType<?>> lureEntityTag, double lureRadius, double speedModifier, int interval, boolean canRandomSwim) {
        super(mob, speedModifier, interval);
        this.canRandomSwim = canRandomSwim;
        this.mob = mob;
        this.lureEntityTag = lureEntityTag;
        this.lureRadius = lureRadius;
    }

    @Override
    public boolean canUse() {
        if (this.findLureTarget() != null)
            return this.mob.getRandom().nextInt(5) == 0;
        return this.canRandomSwim && super.canUse();
    }

    @Override
    public void stop() {
        this.mob.setSwimming(false);
        super.stop();
    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity target = this.findLureTarget();

        if (target != null) {
            Vec3 toTarget = target.position().subtract(this.mob.position());
            Vec3 horizontal = new Vec3(toTarget.x, 0, toTarget.z);
            if (horizontal.lengthSqr() > 1.0E-6)
                horizontal = horizontal.normalize().scale(2.0);
            double vertical = Mth.clamp(toTarget.y * 0.75, 0.5, 4.0);

            Vec3 moveTo = this.mob.position().add(horizontal.x, vertical, horizontal.z);
            float targetYaw = (float) (Mth.atan2(toTarget.z, toTarget.x) * (180F / Math.PI)) - 90.0F;

            this.mob.setYRot(Mth.rotLerp(0.15F, this.mob.getYRot(), targetYaw));
            this.mob.yBodyRot = this.mob.getYRot();

            this.mob.getLookControl().setLookAt(target, 10.0F, 10.0F);
            this.mob.getNavigation().moveTo(moveTo.x, moveTo.y, moveTo.z, 1.6);
            this.mob.setSwimming(true);
        }

        if (this.mob.getNavigation().isDone()) {
            Vec3 pos = this.getPosition();
            if (pos != null)
                this.mob.getNavigation().moveTo(pos.x, pos.y, pos.z, 1.0);
        }
    }

    @Nullable
    private LivingEntity findLureTarget() {
        List<LivingEntity> list = this.mob.level().getEntitiesOfClass(LivingEntity.class,
                this.mob.getBoundingBox().inflate(this.lureRadius), this::isValidLureTarget);

        return list.stream().min(Comparator.comparingDouble(entity -> entity.distanceToSqr(this.mob)))
                .orElse(null);
    }

    private boolean isValidLureTarget(LivingEntity entity) {
        return entity != this.mob && entity.getType().is(this.lureEntityTag) && !entity.isSpectator()
                && (!(entity instanceof Player player) || !player.isCreative())
                && !entity.isInWaterOrBubble() && !this.mob.isAlliedTo(entity);
    }
}