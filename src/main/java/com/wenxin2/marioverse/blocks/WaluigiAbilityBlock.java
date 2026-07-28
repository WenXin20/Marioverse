package com.wenxin2.marioverse.blocks;

public class WaluigiAbilityBlock extends AbilityBlock {
    public WaluigiAbilityBlock(Properties properties) {
        super(properties);
    }

    @Override
    public double getNormalJumpBoost() {
        return 0.65;
    }

    @Override
    public double getRunningJumpBoost() {
        return 0.75;
    }

    @Override
    public double getSafeFallDistance() {
        return 7.0;
    }

    @Override
    public double getGravityMultiplier() {
        return 0.85;
    }

    @Override
    public double getHeightScale() {
        return 1.1;
    }

    @Override
    public double getWidthScale() {
        return 0.8;
    }
}