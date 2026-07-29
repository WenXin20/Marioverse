package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.registries.ConfigRegistry;

public class PeachAbilityBlock extends AbilityBlock {
    public PeachAbilityBlock(Properties properties) {
        super(properties);
    }

    @Override
    public double getNormalJumpBoost() {
        return ConfigRegistry.PEACH_ABILITY_JUMP_BOOST.get();
    }

    @Override
    public double getRunningJumpBoost() {
        return ConfigRegistry.PEACH_ABILITY_RUNNING_JUMP_BOOST.get();
    }

    @Override
    public double getSafeFallDistance() {
        return ConfigRegistry.PEACH_ABILITY_SAFE_FALL_DISTANCE.get();
    }

    @Override
    public double getGravityMultiplier() {
        return ConfigRegistry.PEACH_ABILITY_GRAVITY.get();
    }

    @Override
    public double getHeightScale() {
        return ConfigRegistry.PEACH_ABILITY_HEIGHT_SCALE.get();
    }

    @Override
    public double getWidthScale() {
        return ConfigRegistry.PEACH_ABILITY_WIDTH_SCALE.get();
    }

    @Override
    public boolean hasDoubleJump() {
        return ConfigRegistry.PEACH_ABILITY_DOUBLE_JUMP.get();
    }
}