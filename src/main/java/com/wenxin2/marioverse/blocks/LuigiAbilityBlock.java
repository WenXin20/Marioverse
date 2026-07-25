package com.wenxin2.marioverse.blocks;

public class LuigiAbilityBlock extends AbilityBlock {
    public LuigiAbilityBlock(Properties properties) {
        super(properties);
    }

    @Override
    public double getNormalJumpBoost() {
        return 0.6;
    }

    @Override
    public double getRunningJumpBoost() {
        return 0.7;
    }

    @Override
    public double getSafeFallDistance() {
        return 7.0;
    }
}