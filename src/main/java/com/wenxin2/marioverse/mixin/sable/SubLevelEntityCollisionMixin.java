package com.wenxin2.marioverse.mixin.sable;

import com.wenxin2.marioverse.blocks.SubLevelEntityCollisionBlock;
import dev.ryanhcode.sable.api.math.LevelReusedVectors;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.util.LevelAccelerator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "dev.ryanhcode.sable.sublevel.entity_collision.SubLevelEntityCollision")
public abstract class SubLevelEntityCollisionMixin {
    @Inject(method = "getSubLevelEntityCollisionShape", at = @At("HEAD"), cancellable = true, require = 0, remap = false)
    private static void mv$getEntityAwareShape(Entity entity, Vector3dc entityCenter, Pose3dc pose, BlockState state, LevelAccelerator accelerator,
                                               BlockPos pos, LevelReusedVectors vectors, CallbackInfoReturnable<VoxelShape> cir) {
        if (entity != null && state.getBlock() instanceof SubLevelEntityCollisionBlock)
            cir.setReturnValue(state.getCollisionShape(accelerator, pos, CollisionContext.of(entity)));
    }
}
