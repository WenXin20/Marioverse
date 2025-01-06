package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.entities.PiranhaPlantEntity;
import com.wenxin2.marioverse.init.TagRegistry;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PiranhaPlantHideInBlockGoal extends Goal {
    private final PiranhaPlantEntity entity;
    private final int hideDuration; // Ticks to remain hidden
    private int hideCooldown;
    private int hideTime;
    private boolean isHiding;
    private double targetY;

    public PiranhaPlantHideInBlockGoal(PiranhaPlantEntity entity, int hideDuration, int hideCooldown) {
        this.entity = entity;
        this.hideCooldown = hideCooldown;
        this.hideDuration = hideDuration;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.isHiding = false;
    }

    @Override
    public boolean canUse() {
        if (hideCooldown > 0) {
            --hideCooldown;
            return false;
        }

        BlockPos posBelow = this.entity.blockPosition().below();
        BlockState blockBelow = this.entity.level().getBlockState(posBelow);
        return blockBelow.is(TagRegistry.PIRANHA_PLANTS_CAN_HIDE);
    }

    @Override
    public void start() {
        hideTime = 0;
        hideCooldown = hideDuration;
        BlockPos posBelow = entity.blockPosition().below();
        targetY = posBelow.getY();
        isHiding = false;
    }

    @Override
    public void tick() {
        double currentY = entity.getY();
        double speed = 0.1;

        if (!isHiding) {
            // Move down to hide
            if (currentY > targetY + speed) {
                entity.setDeltaMovement(0, -speed, 0);
                entity.tryToHide();
            } else {
                entity.setDeltaMovement(0, 0, 0);
                entity.setPos(entity.getX(), targetY, entity.getZ());
                entity.tryToHide(); // Activate hiding
                isHiding = true;
            }
        } else {
            // Stay hidden for `hideDuration` ticks
            hideTime++;
            if (hideTime >= hideDuration) {
                // Start rising after hiding duration
                targetY = entity.getY() + 1.0; // Move up by 1 block
                if (currentY < targetY - speed) {
                    entity.setDeltaMovement(0, speed, 0);
                } else {
                    entity.setDeltaMovement(0, 0, 0);
                    entity.setPos(entity.getX(), targetY, entity.getZ());
                    entity.stopHiding(); // Exit hiding state
                    isHiding = false;
                }
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        return hideCooldown > 0 || isHiding;
    }
}
