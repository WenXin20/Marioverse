package com.wenxin2.marioverse.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.datafixers.util.Pair;
import com.wenxin2.marioverse.entities.ai.behaviors.UseClearPipe;
import com.wenxin2.marioverse.entities.ai.behaviors.UseWarpPipe;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VillagerGoalPackages.class)
public abstract class VillagerGoalPackagesMixin {
    @ModifyReturnValue(method = "getCorePackage", at = @At("RETURN"))
    private static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> mv$addClearPipeBehavior(
            ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> behaviors, VillagerProfession profession, float speed) {
        return ImmutableList.<Pair<Integer, ? extends BehaviorControl<? super Villager>>>builder()
                .addAll(behaviors)
                .add(Pair.of(0, new UseClearPipe()))
                .add(Pair.of(0, new UseWarpPipe()))
                .build();
    }
}
