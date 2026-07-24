package com.wenxin2.marioverse.power_up;

import net.minecraft.core.Holder;

public interface PowerUpSource {
    Holder<PowerUpType> getPowerUpType();

    default void removePowerUpEntity() {
    }
}