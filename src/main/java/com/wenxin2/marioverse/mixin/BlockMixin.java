package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "fallOn", at = @At("HEAD"), cancellable = true)
    private void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci) {
        if (state.is(TagRegistry.BOUNCY_BLOCKS)
                && !entity.getType().is(TagRegistry.CANNOT_BOUNCE_ON_BLOCKS)
                && !entity.isSuppressingBounce() && !entity.isNoGravity())
            ci.cancel();
    }

    @Inject(method = "updateEntityAfterFallOn", at = @At("HEAD"), cancellable = true)
    private void updateEntityAfterFallOn(BlockGetter blockGetter, Entity entity, CallbackInfo ci) {
        if (blockGetter.getBlockState(entity.blockPosition().below()).is(TagRegistry.BOUNCY_BLOCKS)
                && !entity.getType().is(TagRegistry.CANNOT_BOUNCE_ON_BLOCKS)
                && !entity.isSuppressingBounce() && !entity.isNoGravity()
                && !(entity instanceof Player)) {
            mv$bounceEntity(entity, entity.level());
            ci.cancel();
        }
    }

    @Unique
    private static void mv$bounceEntity(Entity entity, Level world) {
        Vec3 vec3 = entity.getDeltaMovement();

        if (vec3.y < 0.0) {
            double baseBounce = 0.0552;
            double bounceFactor = (entity instanceof LivingEntity ? 1.0 : 0.8);
            double fallMultiplier = Math.min(entity.fallDistance / 10.0, 2.0);

            if (entity instanceof LivingEntity livingEntity) {
                AttributeInstance gravityAttribute = livingEntity.getAttribute(Attributes.GRAVITY);
                if (gravityAttribute != null)
                    baseBounce /= gravityAttribute.getValue();
            } else baseBounce = 0.69;

            double newBounce = Math.max(-vec3.y * bounceFactor * fallMultiplier, baseBounce);
            if (world instanceof ServerLevel serverWorld && entity instanceof LivingEntity)
                ServerParticleUtils.spawnParticleRingBelowEntity(ParticleTypes.POOF, serverWorld, entity,
                        entity.getBbWidth() / 2, 0.0, 3);
            entity.resetFallDistance();
            entity.setDeltaMovement(vec3.x, newBounce, vec3.z);
        }
    }
}
