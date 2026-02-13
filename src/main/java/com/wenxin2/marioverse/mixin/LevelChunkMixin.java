package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.blocks.OnBlock;
import com.wenxin2.marioverse.blocks.ToggleableBlock;
import com.wenxin2.marioverse.world.SwitchSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunk.class)
public class LevelChunkMixin {
//    @Inject(method = "setBlockState", at = @At("RETURN"))
//    private void setBlockState(BlockPos pos, BlockState state, boolean moved, CallbackInfoReturnable<BlockState> cir) {
//        LevelChunk chunk = (LevelChunk) (Object) this;
//
//        if (!(chunk.getLevel() instanceof ServerLevel serverLevel)) return;
//        if (!(state.getBlock() instanceof ToggleableBlock)) return;
//
//        SwitchSavedData data = SwitchSavedData.get(serverLevel);
//        data.add(pos);
//        boolean isActive = data.isActive();
//
//        if (state.hasProperty(OnBlock.ACTIVE) && state.getValue(OnBlock.ACTIVE) != isActive)
//            serverLevel.setBlock(pos, state.setValue(OnBlock.ACTIVE, isActive), Block.UPDATE_CLIENTS);
//    }

    @Inject(method = "setBlockState", at = @At("RETURN"))
    private void afterSetBlockState(BlockPos pos, BlockState oldState, boolean moved,
                                    CallbackInfoReturnable<BlockState> cir) {
        BlockState result = cir.getReturnValue();
        if (result == null)
            return;
        if (!(result.getBlock() instanceof ToggleableBlock))
            return;
        LevelChunk chunk = (LevelChunk)(Object)this;
        if (!(chunk.getLevel() instanceof ServerLevel serverLevel))
            return;
        SwitchSavedData.get(serverLevel).add(pos);
    }
}
