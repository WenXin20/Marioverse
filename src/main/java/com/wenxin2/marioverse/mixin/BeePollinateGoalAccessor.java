package com.wenxin2.marioverse.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.world.entity.animal.Bee$BeePollinateGoal")
public interface BeePollinateGoalAccessor {
    @Accessor("pollinating")
    boolean mv$isPollinating();

    @Accessor("pollinating")
    void mv$setPollinating(boolean pollinating);
}
