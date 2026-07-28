package com.wenxin2.marioverse.blocks;

public class PeachAbilityBlock extends AbilityBlock {
    public PeachAbilityBlock(Properties properties) {
        super(properties);
    }

    @Override
    public double getNormalJumpBoost() {
        return 0.4;
    }

    @Override
    public double getRunningJumpBoost() {
        return 0.5;
    }

    @Override
    public double getSafeFallDistance() {
        return 7.0;
    }

    @Override
    public double getGravityMultiplier() {
        return 0.7;
    }
}