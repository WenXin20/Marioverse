package com.wenxin2.marioverse.blocks;

public class RosalinaAbilityBlock extends AbilityBlock {
    public RosalinaAbilityBlock(Properties properties) {
        super(properties);
    }

    @Override
    public double getNormalJumpBoost() {
        return 0.5;
    }

    @Override
    public double getRunningJumpBoost() {
        return 0.6;
    }

    @Override
    public double getSafeFallDistance() {
        return 7.0;
    }

    @Override
    public double getVerticalMotionMultiplier() {
        return 0.4;
    }
}