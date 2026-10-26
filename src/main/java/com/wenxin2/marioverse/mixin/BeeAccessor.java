package com.wenxin2.marioverse.mixin;

import net.minecraft.world.entity.animal.Bee;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Bee.class)
public interface BeeAccessor {
    @Accessor("remainingCooldownBeforeLocatingNewFlower")
    void mv$setRemainingCooldownBeforeLocatingNewFlower(int cooldown);

    @Invoker("setHasNectar")
    void mv$setHasNectar(boolean hasNectar);
}
