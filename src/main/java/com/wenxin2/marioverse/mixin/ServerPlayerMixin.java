package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.blocks.CheckpointFlagBlock;
import com.wenxin2.marioverse.blocks.states.TripleBlockStates;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @Inject(method = "findRespawnAndUseSpawnBlock", at = @At("HEAD"), cancellable = true)
    private static void findRespawnAndUseSpawnBlock(ServerLevel world, BlockPos pos, float angle, boolean forced, boolean anchor, CallbackInfoReturnable<Optional<ServerPlayer.RespawnPosAngle>> cir) {
        BlockState state = world.getBlockState(pos);

        if (state.getBlock() instanceof CheckpointFlagBlock) {
            Vec3 spawnPos;

            if (state.getValue(CheckpointFlagBlock.PART) == TripleBlockStates.TOP)
                spawnPos = Vec3.atBottomCenterOf(pos.below(2));
            else if (state.getValue(CheckpointFlagBlock.PART) == TripleBlockStates.MIDDLE)
                spawnPos = Vec3.atBottomCenterOf(pos.below());
            else spawnPos = Vec3.atBottomCenterOf(pos);

            cir.setReturnValue(Optional.of(new ServerPlayer.RespawnPosAngle(spawnPos, angle)));
        }
    }
}
