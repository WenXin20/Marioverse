package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class BouncyOnBlock extends OnBlock {
    public BouncyOnBlock(Properties properties) {
        super(properties);
    }

    @NotNull
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        return Shapes.block();
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (entity.isSuppressingBounce())
            super.fallOn(level, state, pos, entity, fallDistance);
        else entity.causeFallDamage(fallDistance, 0.0F, level.damageSources().fall());
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter blockGetter, Entity entity) {
        BlockPos pos = entity.getOnPos();
        BlockState state = blockGetter.getBlockState(pos);

        if (!entity.isSuppressingBounce() && !(entity instanceof Player)
                && state.hasProperty(ACTIVE) && state.getValue(ACTIVE))
            BouncyOnBlock.bounceEntity(entity.level(), entity, false);
        else super.updateEntityAfterFallOn(blockGetter, entity);
    }

    public static void bounceEntity(Level level, Entity entity, boolean holdingJump) {
        Vec3 vec3 = entity.getDeltaMovement();

        if (vec3.y < 0.0) {
            double baseBounce = 0.0552;
            double bounceFactor = (entity instanceof LivingEntity ? 1.0 : 0.8);
            double fallMultiplier = Math.min(entity.fallDistance / 10.0, 2.0);
            double jumpMultiplier = 1.0;

            if (entity instanceof LivingEntity livingEntity) {
                AttributeInstance gravityAttribute = livingEntity.getAttribute(Attributes.GRAVITY);
                AttributeInstance jumpAttribute = livingEntity.getAttribute(Attributes.JUMP_STRENGTH);
                if (gravityAttribute != null)
                    baseBounce /= gravityAttribute.getValue();
                if (jumpAttribute != null)
                    jumpMultiplier = jumpAttribute.getValue();
            } else baseBounce = 0.69;

            double computedBounce = -vec3.y * bounceFactor * fallMultiplier * jumpMultiplier;
            double newBounce = Math.max(computedBounce, baseBounce);

            if (holdingJump)
                newBounce *= 1.5;

            if (level instanceof ServerLevel serverWorld && entity instanceof LivingEntity)
                ServerParticleUtils.spawnParticleRingBelowEntity(ParticleTypes.POOF, serverWorld, entity,
                        entity.getBbWidth() / 2, 0.0, 3);
            level.playSound(null, entity.blockPosition(), SoundRegistry.BLOCK_BOUNCE.get(), SoundSource.BLOCKS);
            entity.resetFallDistance();
            entity.setDeltaMovement(vec3.x, newBounce, vec3.z);
        }
    }
}
