package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.registries.ConfigRegistry;

public class RosalinaAbilityBlock extends AbilityBlock {
    public RosalinaAbilityBlock(Properties properties) {
        super(properties);
    }

    @Override
    public double getNormalJumpBoost() {
        return ConfigRegistry.ROSALINA_ABILITY_JUMP_BOOST.get();
    }

    @Override
    public double getRunningJumpBoost() {
        return ConfigRegistry.ROSALINA_ABILITY_RUNNING_JUMP_BOOST.get();
    }

    @Override
    public double getSafeFallDistance() {
        return ConfigRegistry.ROSALINA_ABILITY_SAFE_FALL_DISTANCE.get();
    }

    @Override
    public double getGravityMultiplier() {
        return ConfigRegistry.ROSALINA_ABILITY_GRAVITY.get();
    }

    @Override
    public double getHeightScale() {
        return ConfigRegistry.ROSALINA_ABILITY_HEIGHT_SCALE.get();
    }

    @Override
    public double getWidthScale() {
        return ConfigRegistry.ROSALINA_ABILITY_WIDTH_SCALE.get();
    }

    @Override
    public boolean hasDoubleJump() {
        return ConfigRegistry.ROSALINA_ABILITY_DOUBLE_JUMP.get();
    }
}