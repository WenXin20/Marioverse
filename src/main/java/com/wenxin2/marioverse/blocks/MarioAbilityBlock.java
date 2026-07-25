package com.wenxin2.marioverse.blocks;

public class MarioAbilityBlock extends AbilityBlock {
    public MarioAbilityBlock(Properties properties) {
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
}