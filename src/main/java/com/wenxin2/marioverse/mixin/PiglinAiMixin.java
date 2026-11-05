package com.wenxin2.marioverse.mixin;

import com.google.common.collect.ImmutableList;
import com.wenxin2.marioverse.entities.ai.behaviors.ReturnThroughWarp;
import com.wenxin2.marioverse.entities.ai.behaviors.UseClearPipe;
import com.wenxin2.marioverse.entities.ai.behaviors.UseWarpLink;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.schedule.Activity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PiglinAi.class)
public abstract class PiglinAiMixin {
    @Inject(method = "initCoreActivity", at = @At("TAIL"))
    private static void mv$initCoreActivity(Brain<Piglin> brain, CallbackInfo ci) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(new UseClearPipe(), new UseWarpLink(), new ReturnThroughWarp()));
    }
}
