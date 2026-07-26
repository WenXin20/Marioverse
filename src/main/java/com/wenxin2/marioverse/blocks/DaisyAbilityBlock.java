package com.wenxin2.marioverse.blocks;

public class DaisyAbilityBlock extends AbilityBlock {
    public DaisyAbilityBlock(Properties properties) {
        super(properties);
    }

    @Override
    public double getNormalJumpBoost() {
        return 0.35;
    }

    @Override
    public double getRunningJumpBoost() {
        return 0.4;
    }

    @Override
    public double getSafeFallDistance() {
        return 7.0;
    }

    @Override
    public double getVerticalMotionMultiplier() {
        return 0.8;
    }

    @Override
    public boolean hasDoubleJump() {
        return true;
    }
}