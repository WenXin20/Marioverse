package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.entities.BooEntity;
import com.wenxin2.marioverse.entities.ai.controls.BounceMoveControl;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Vex;

public class RandomMoveGoal extends Goal {
    private final Mob mob;
    public RandomMoveGoal(Mob mob) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        return !mob.getMoveControl().hasWanted() && mob.getRandom().nextInt(reducedTickDelay(7)) == 0;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void tick() {
        BlockPos blockpos = null;
        if (this.mob instanceof BooEntity boo)
            blockpos = boo.getBoundOrigin();
        if (blockpos == null)
            blockpos = mob.blockPosition();

        for (int i = 0; i < 3; i++) {
            BlockPos blockpos1 = blockpos.offset(mob.getRandom().nextInt(15) - 7,
                    mob.getRandom().nextInt(11) - 5,
                    mob.getRandom().nextInt(15) - 7);
            if (mob.level().isEmptyBlock(blockpos1)) {
                mob.getMoveControl().setWantedPosition((double)blockpos1.getX() + 0.5,
                        (double)blockpos1.getY() + 0.5,
                        (double)blockpos1.getZ() + 0.5, 0.25);
                if (mob.getTarget() == null) {
                    mob.getLookControl().setLookAt((double)blockpos1.getX() + 0.5,
                            (double)blockpos1.getY() + 0.5,
                            (double)blockpos1.getZ() + 0.5, 180.0F, 20.0F);
                }
                break;
            }
        }
    }
}