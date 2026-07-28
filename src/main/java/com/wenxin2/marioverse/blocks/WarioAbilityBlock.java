package com.wenxin2.marioverse.blocks;

public class WarioAbilityBlock extends AbilityBlock {
    public WarioAbilityBlock(Properties properties) {
        super(properties);
    }

    @Override
    public double getNormalJumpBoost() {
        return 0.3;
    }

    @Override
    public double getRunningJumpBoost() {
        return 0.45;
    }

    @Override
    public double getSafeFallDistance() {
        return 7.0;
    }

    @Override
    public double getHeightScale() {
        return 0.85;
    }

    @Override
    public double getWidthScale() {
        return 1.2;
    }

    @Override
    public double getGravityMultiplier() {
        return 1.25;
    }

    @Override
    public boolean hasDoubleJump() {
        return true;
    }
}