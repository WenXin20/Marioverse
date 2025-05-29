package com.wenxin2.marioverse.utils;

public interface PowerUpHandler {
    void mv$clearAllPowerUps();

    boolean mv$hasFireFlower();
    void mv$setFireFlower(boolean hasFireFlower);

    boolean mv$hasIceFlower();
    void mv$setIceFlower(boolean hasIceFlower);
}
