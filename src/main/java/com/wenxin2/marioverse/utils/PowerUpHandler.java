package com.wenxin2.marioverse.utils;

public interface PowerUpHandler {
    void mv$clearAllPowerUps();

    boolean mv$hasMushroom();
    void mv$setMushroom(boolean hasMushroom);

    boolean mv$hasMegaMushroom();
    void mv$setMegaMushroom(boolean hasMushroom);

    boolean mv$hasFireFlower();
    void mv$setFireFlower(boolean hasFireFlower);

    boolean mv$hasIceFlower();
    void mv$setIceFlower(boolean hasIceFlower);

    boolean mv$hasSuperStar();
    void mv$setSuperStar(boolean hasSuperStar);

    int mv$getSuperStarCooldown();
    void mv$setSuperStarCooldown(int superStarCooldown);
}
