package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RedMushroomTrampolineBlock extends OnBlock implements ToggleableBlock {
    public RedMushroomTrampolineBlock(Properties properties) {
        super(properties);
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
                && state.hasProperty(ACTIVE) && state.getValue(ACTIVE)
                && !(state.getBlock() instanceof BlueMushroomTrampolineBlock))
            RedMushroomTrampolineBlock.bounceEntity(entity.level(), pos, entity, false, entity.getDeltaMovement().y);
        else super.updateEntityAfterFallOn(blockGetter, entity);
    }

    public static void bounceEntity(Level level, BlockPos pos, Entity entity, boolean holdingJump, double clientMotionY) {
        float pitch = 0.8F + level.random.nextFloat() * 0.2F;
        Vec3 vec3 = entity.getDeltaMovement();

        if (entity instanceof Player player && player.getAbilities().flying)
            return;

        double motionY = (entity instanceof Player) ? clientMotionY : vec3.y;

        if (motionY >= 0.0)
            return;

        VoxelShape shape = level.getBlockState(pos).getCollisionShape(level, pos);
        double blockTopY = pos.getY() + (shape.isEmpty() ? 1.0 : shape.max(Direction.Axis.Y));
        double entityMinY = entity.getBoundingBox().minY;
        double verticalEpsilon = 0.1;

        if (entityMinY < blockTopY - verticalEpsilon || entityMinY > blockTopY + verticalEpsilon)
            return;

        AABB entityBox = entity.getBoundingBox();
        double horizontalEpsilon = 0.05;
        boolean overlapsX = entityBox.maxX > pos.getX() + horizontalEpsilon
                && entityBox.minX < pos.getX() + 1.0 - horizontalEpsilon;
        boolean overlapsZ = entityBox.maxZ > pos.getZ() + horizontalEpsilon
                && entityBox.minZ < pos.getZ() + 1.0 - horizontalEpsilon;

        if (!overlapsX || !overlapsZ)
            return;

        double baseBounce = 0.0552;
        double bounceFactor = (entity instanceof LivingEntity ? 1.0 : 0.8);
        double jumpMultiplier = 1.0;

        if (entity instanceof LivingEntity livingEntity) {
            AttributeInstance gravityAttribute = livingEntity.getAttribute(Attributes.GRAVITY);
            AttributeInstance jumpAttribute = livingEntity.getAttribute(Attributes.JUMP_STRENGTH);
            if (gravityAttribute != null)
                baseBounce /= gravityAttribute.getValue();
            if (jumpAttribute != null)
                jumpMultiplier = jumpAttribute.getValue();
        } else baseBounce = 0.69;

        double fallMultiplier = Math.min(entity.fallDistance / 10.0, 2.0);
        double fallBounce = -motionY * bounceFactor * fallMultiplier * jumpMultiplier;
        double newY = Math.max(fallBounce, baseBounce);

        if (holdingJump)
            newY *= 1.5;

        if (level instanceof ServerLevel serverWorld && entity instanceof LivingEntity)
            ServerParticleUtils.spawnParticleRingBelowEntity(ParticleTypes.POOF, serverWorld, entity,
                    entity.getBbWidth() / 2, 0.0, 3);
        if (ConfigRegistry.PLAY_BOUNCE_SOUND.get())
            level.playSound(null, entity.blockPosition(), SoundRegistry.BLOCK_BOUNCE.get(),
                    SoundSource.BLOCKS, 1.0F, pitch);

        entity.resetFallDistance();
        entity.setDeltaMovement(vec3.x, newY, vec3.z);
    }
}