package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.entities.ai.controls.BounceMoveControl;
import com.wenxin2.marioverse.entities.ai.controls.JumpInPlaceMoveControl;
import java.util.EnumSet;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class ContinuousJumpGoal extends Goal {
    private final Mob mob;

    public ContinuousJumpGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return !this.mob.isPassenger();
    }

    @Override
    public void tick() {
        if (this.mob.getMoveControl() instanceof BounceMoveControl moveControl)
            moveControl.triggerJump(1.0);
        if (this.mob.getMoveControl() instanceof JumpInPlaceMoveControl moveControl)
            moveControl.triggerJump(1.0);
    }
}
