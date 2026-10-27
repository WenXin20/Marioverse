package com.wenxin2.marioverse.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.wenxin2.marioverse.entities.ai.goals.BeePollinatesEntityGoal;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Bee;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.world.entity.animal.Bee$BeePollinateGoal")
public class BeePollinateGoalMixin {
    @Shadow @Final Bee this$0;

    @ModifyExpressionValue(method = "canBeeUse", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/animal/Bee$BeePollinateGoal;findNearbyFlower()Ljava/util/Optional;"))
    private Optional<BlockPos> mv$sometimesPreferEntityFlower(Optional<BlockPos> original) {
        if (original.isPresent()) {
            BeePollinatesEntityGoal goal = BeePollinatesEntityGoal.getFrom(this.this$0);

            if (goal != null && goal.tryPreferEntityFlower())
                return Optional.empty();
        }
        return original;
    }
}
