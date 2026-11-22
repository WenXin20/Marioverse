package com.wenxin2.marioverse.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CarvedPumpkinBlock.class)
public abstract class CarvedPumpkinBlockMixin {

    @ModifyExpressionValue(method = {"getOrCreateSnowGolemFull", "getOrCreateIronGolemFull"}, require = 0,
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/CarvedPumpkinBlock;PUMPKINS_PREDICATE:Ljava/util/function/Predicate;"))
    private Predicate<BlockState> mv$getOrCreateGolemFull(Predicate<BlockState> original) {
        return state -> original.test(state) || (state != null && state.is(TagRegistry.GOLEM_HEADS));
    }

    @WrapOperation(method = "trySpawnGolem", require = 0, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/CarvedPumpkinBlock;spawnGolemInWorld(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/pattern/BlockPattern$BlockPatternMatch;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;)V"))
    private void mv$trySpawnGolem(Level level, BlockPattern.BlockPatternMatch match, Entity golem, BlockPos pos,
                                  Operation<Void> original) {
        if (golem instanceof SnowGolem) {
            BlockState head = CarvedPumpkinBlockMixin.mv$findGolemHead(match);
            if (head != null)
                golem.setData(DataAttachmentRegistry.SNOW_GOLEM_HEAD,
                        BuiltInRegistries.BLOCK.getKey(head.getBlock()).toString());
        }
        original.call(level, match, golem, pos);
    }

    @Unique
    private static BlockState mv$findGolemHead(BlockPattern.BlockPatternMatch match) {
        for (int i = 0; i < match.getWidth(); i++) {
            BlockState state = match.getBlock(i, 0, 0).getState();
            if (state != null && state.is(TagRegistry.GOLEM_HEADS))
                return state;
        }
        return null;
    }
}
