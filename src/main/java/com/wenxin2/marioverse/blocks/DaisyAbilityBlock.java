package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.registries.ConfigRegistry;

public class DaisyAbilityBlock extends AbilityBlock {
    public DaisyAbilityBlock(Properties properties) {
        super(properties);
    }

    @Override
    public double getNormalJumpBoost() {
        return ConfigRegistry.DAISY_ABILITY_JUMP_BOOST.get();
    }

    @Override
    public double getRunningJumpBoost() {
        return ConfigRegistry.DAISY_ABILITY_RUNNING_JUMP_BOOST.get();
    }

    @Override
    public double getSafeFallDistance() {
        return ConfigRegistry.DAISY_ABILITY_SAFE_FALL_DISTANCE.get();
    }

    @Override
    public double getGravityMultiplier() {
        return ConfigRegistry.DAISY_ABILITY_GRAVITY.get();
    }

    @Override
    public double getHeightScale() {
        return ConfigRegistry.DAISY_ABILITY_HEIGHT_SCALE.get();
    }

    @Override
    public double getWidthScale() {
        return ConfigRegistry.DAISY_ABILITY_WIDTH_SCALE.get();
    }

    @Override
    public boolean hasDoubleJump() {
        return ConfigRegistry.DAISY_ABILITY_DOUBLE_JUMP.get();
    }
}