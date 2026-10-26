package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.mixin.BeeAccessor;
import com.wenxin2.marioverse.mixin.BeePollinateGoalAccessor;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.phys.Vec3;

public class BeePollinatesEntityGoal extends Goal {
    private static final int MIN_POLLINATION_TICKS = 400;
    private static final int MIN_FIND_FLOWER_RETRY_COOLDOWN = 20;
    private static final int MAX_FIND_FLOWER_RETRY_COOLDOWN = 60;
    private static final int COOLDOWN_AFTER_POLLINATING = 200;
    private static final double ARRIVAL_THRESHOLD = 0.1;
    private static final int POSITION_CHANGE_CHANCE = 25;
    private static final float SPEED_MODIFIER = 0.35F;
    private static final float HOVER_HEIGHT_ABOVE_FLOWER = 0.4F;
    private static final float HOVER_POS_OFFSET = 0.33333334F;
    private static final int MAX_POLLINATING_TICKS = 600;
    private final Bee bee;
    private final double searchRange;
    @Nullable private final BeePollinateGoalAccessor vanillaPollinateGoal;
    @Nullable private Entity flower;
    @Nullable private Vec3 hoverPos;
    private int successfulPollinatingTicks;
    private int lastSoundPlayedTick;
    private int pollinatingTicks;
    private int remainingCooldownBeforeLocatingNewFlower;
    private boolean pollinating;

    public BeePollinatesEntityGoal(Bee bee, double searchRange) {
        this.bee = bee;
        this.searchRange = searchRange;
        this.vanillaPollinateGoal = bee.goalSelector.getAvailableGoals().stream()
                .map(WrappedGoal::getGoal)
                .filter(goal -> goal instanceof BeePollinateGoalAccessor)
                .map(goal -> (BeePollinateGoalAccessor) goal)
                .findFirst().orElse(null);
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.remainingCooldownBeforeLocatingNewFlower > 0) {
            this.remainingCooldownBeforeLocatingNewFlower--;
            return false;
        } else if (this.bee.isAngry() || this.bee.hasNectar() || this.bee.level().isRaining())
            return false;

        Optional<Entity> optional = this.findNearbyFlower();

        if (optional.isPresent()) {
            this.flower = optional.get();
            Vec3 target = this.getFlowerHoverPos(this.flower);
            this.bee.getNavigation().moveTo(target.x(), target.y(), target.z(), 1.2F);
            return true;
        } else {
            this.remainingCooldownBeforeLocatingNewFlower = Mth.nextInt(this.bee.getRandom(),
                    MIN_FIND_FLOWER_RETRY_COOLDOWN, MAX_FIND_FLOWER_RETRY_COOLDOWN);
            return false;
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (this.bee.isAngry() || !this.isPollinating())
            return false;
        else if (this.flower == null)
            return false;
        else if (this.bee.level().isRaining())
            return false;
        else if (this.hasPollinatedLongEnough())
            return this.bee.getRandom().nextFloat() < 0.2F;
        else if (this.bee.tickCount % 20 == 0 && !this.isFlowerValid(this.flower)) {
            this.flower = null;
            return false;
        } else return true;
    }

    @Override
    public void start() {
        this.successfulPollinatingTicks = 0;
        this.pollinatingTicks = 0;
        this.lastSoundPlayedTick = 0;
        this.hoverPos = null;
        this.setPollinating(true);
        this.bee.resetTicksWithoutNectarSinceExitingHive();
    }

    @Override
    public void stop() {
        if (this.hasPollinatedLongEnough())
            ((BeeAccessor) this.bee).mv$setHasNectar(true);

        this.setPollinating(false);
        this.flower = null;
        this.bee.getNavigation().stop();
        this.remainingCooldownBeforeLocatingNewFlower = COOLDOWN_AFTER_POLLINATING;
        ((BeeAccessor) this.bee).mv$setRemainingCooldownBeforeLocatingNewFlower(COOLDOWN_AFTER_POLLINATING);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (this.flower == null)
            return;

        this.pollinatingTicks++;

        if (this.pollinatingTicks > MAX_POLLINATING_TICKS) {
            this.flower = null;
            return;
        }

        Vec3 target = this.getFlowerHoverPos(this.flower);

        if (target.distanceTo(this.bee.position()) > 1.0) {
            this.hoverPos = target;
            this.setWantedPos();
        } else {
            if (this.hoverPos == null)
                this.hoverPos = target;

            boolean arrived = this.bee.position().distanceTo(this.hoverPos) <= ARRIVAL_THRESHOLD;
            boolean shouldMove = true;

            if (arrived) {
                if (this.bee.getRandom().nextInt(POSITION_CHANGE_CHANCE) == 0) {
                    this.hoverPos = new Vec3(target.x() + this.getOffset(), target.y(), target.z() + this.getOffset());
                    this.bee.getNavigation().stop();
                } else shouldMove = false;

                this.bee.getLookControl().setLookAt(target.x(), target.y(), target.z());
            }

            if (shouldMove)
                this.setWantedPos();

            this.successfulPollinatingTicks++;

            if (this.bee.getRandom().nextFloat() < 0.05F && this.successfulPollinatingTicks > this.lastSoundPlayedTick + 60) {
                this.lastSoundPlayedTick = this.successfulPollinatingTicks;
                this.bee.playSound(SoundEvents.BEE_POLLINATE, 1.0F, 1.0F + (this.bee.getRandom().nextFloat() - 0.5F) * 0.2F);
            }
        }
    }

    private boolean hasPollinatedLongEnough() {
        return this.successfulPollinatingTicks > MIN_POLLINATION_TICKS;
    }

    private boolean isPollinating() {
        return this.vanillaPollinateGoal != null ? this.vanillaPollinateGoal.mv$isPollinating() : this.pollinating;
    }

    private void setPollinating(boolean pollinating) {
        this.pollinating = pollinating;

        if (this.vanillaPollinateGoal != null)
            this.vanillaPollinateGoal.mv$setPollinating(pollinating);
    }

    private boolean isFlowerValid(Entity flower) {
        return flower.isAlive() && flower.getType().is(TagRegistry.BEE_CAN_POLLINATE)
                && this.bee.distanceToSqr(flower) <= this.searchRange * this.searchRange * 4.0;
    }

    private Optional<Entity> findNearbyFlower() {
        return this.bee.level().getEntitiesOfClass(Entity.class, this.bee.getBoundingBox().inflate(this.searchRange),
                        entity -> entity.isAlive() && entity.getType().is(TagRegistry.BEE_CAN_POLLINATE))
                .stream().min(Comparator.comparingDouble(this.bee::distanceToSqr));
    }

    private Vec3 getFlowerHoverPos(Entity flower) {
        return new Vec3(flower.getX(), flower.getY() + flower.getBbHeight() + HOVER_HEIGHT_ABOVE_FLOWER, flower.getZ());
    }

    private void setWantedPos() {
        if (this.hoverPos != null)
            this.bee.getMoveControl().setWantedPosition(this.hoverPos.x(), this.hoverPos.y(), this.hoverPos.z(), SPEED_MODIFIER);
    }

    private float getOffset() {
        return (this.bee.getRandom().nextFloat() * 2.0F - 1.0F) * HOVER_POS_OFFSET;
    }
}
