package com.wenxin2.marioverse.utils;

public interface AbilitiesHandler {
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


    int mv$getCheckpointFlagCooldown();
    void mv$setCheckpointFlagCooldown(int checkpointFlagCooldown);

    int mv$getConsecutiveBounces();
    void mv$setConsecutiveBounces(int consecutiveBounces);

    int mv$getFreezeImmunityCooldown();
    void mv$setFreezeImmunityCooldown(int freezeImmunityCooldown);

    int mv$getFrozenCooldown();
    void mv$setFrozenCooldown(int frozenCooldown);

    int mv$getOneUpsRewarded();
    void mv$setOneUpsRewarded(int oneUpsRewarded);

    int mv$getSuperStarCooldown();
    void mv$setSuperStarCooldown(int superStarCooldown);
}
