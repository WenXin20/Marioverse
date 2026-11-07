package com.wenxin2.marioverse.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.wenxin2.marioverse.blocks.ClearWarpPipeBlock;
import com.wenxin2.marioverse.integration.sable_compat.SableProvider;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PipeTravelRotations {
    private static final double MIN_TRAVEL_SPEED_SQR = 0.0025;
    private static final float TURN_SPEED = 0.8F;
    private static final float HEAD_FIRST_HEIGHT_RATIO = 1.75F;
    private static final Map<Entity, PipeRotation> ROTATIONS = new WeakHashMap<>();

    public static BlockState getClearPipeState(Entity entity) {
        BlockState state = entity.level().getBlockState(entity.blockPosition());

        if (!(state.getBlock() instanceof ClearWarpPipeBlock) && SableProvider.isLoaded()
                && SableProvider.getContext(entity.level(), entity) instanceof SableProvider.SableContext context) {
            BlockState subLevelState = entity.level().getBlockState(context.posWorld);

            if (subLevelState.getBlock() instanceof ClearWarpPipeBlock)
                return subLevelState;
        }
        return state.getBlock() instanceof ClearWarpPipeBlock ? state : Blocks.AIR.defaultBlockState();
    }

    public static void tick(LivingEntity entity, boolean inClearPipe) {
        if (!inClearPipe || entity.isShiftKeyDown()) {
            ROTATIONS.remove(entity);
            return;
        }

        Vec3 travel = entity.position().subtract(entity.xo, entity.yo, entity.zo);
        PipeRotation rotation = ROTATIONS.get(entity);

        if (rotation == null) {
            if (travel.lengthSqr() < MIN_TRAVEL_SPEED_SQR)
                return;

            rotation = new PipeRotation(entity.yBodyRot, 0.0F);
            ROTATIONS.put(entity, rotation);
        }

        rotation.yawO = rotation.yaw;
        rotation.pitchO = rotation.pitch;

        if (travel.lengthSqr() < MIN_TRAVEL_SPEED_SQR)
            return;

        float targetPitch = (float) (-Mth.atan2(travel.y, travel.horizontalDistance()) * Mth.RAD_TO_DEG);
        if (travel.horizontalDistanceSqr() > 1.0E-6) {
            float targetYaw = (float) (Mth.atan2(-travel.x, travel.z) * Mth.RAD_TO_DEG);
            rotation.yaw += Mth.wrapDegrees(targetYaw - rotation.yaw) * TURN_SPEED;
        }
        rotation.pitch += (targetPitch - rotation.pitch) * TURN_SPEED;
    }

    public static boolean applyRotations(LivingEntity entity, PoseStack poseStack, float partialTick, float scale) {
        PipeRotation rotation = ROTATIONS.get(entity);
        if (rotation == null || entity.deathTime > 0)
            return false;

        float yaw = Mth.rotLerp(partialTick, rotation.yawO, rotation.yaw);
        float pitch = Mth.lerp(partialTick, rotation.pitchO, rotation.pitch);

        if (entity instanceof Player) {
            if (!entity.isVisuallySwimming())
                return false;

            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F - pitch));
            poseStack.translate(0.0F, -1.0F, 0.3F);
            return true;
        }

        EntityDimensions standingSize = entity.getDimensions(Pose.STANDING);
        float hitboxCenter = entity.getBbHeight() * 0.5F / scale;
        float modelCenter = standingSize.height() * 0.5F / scale;
        poseStack.translate(0.0F, hitboxCenter, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(PipeTravelRotations.travelsHeadFirst(entity, standingSize) ? -90.0F - pitch : -pitch));
        poseStack.translate(0.0F, -modelCenter, 0.0F);
        return true;
    }

    private static boolean travelsHeadFirst(LivingEntity entity, EntityDimensions standingSize) {
        return standingSize.height() > standingSize.width() * HEAD_FIRST_HEIGHT_RATIO || entity.getType().is(TagRegistry.TRAVELS_HEAD_FIRST_IN_PIPES);
    }

    private static class PipeRotation {
        private float yaw;
        private float yawO;
        private float pitch;
        private float pitchO;

        private PipeRotation(float yaw, float pitch) {
            this.yaw = yaw;
            this.yawO = yaw;
            this.pitch = pitch;
            this.pitchO = pitch;
        }
    }
}
