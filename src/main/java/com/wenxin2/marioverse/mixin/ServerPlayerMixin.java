package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.blocks.CheckpointFlagBlock;
import com.wenxin2.marioverse.blocks.states.TripleBlockStates;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.utils.BlockWarpPlayerHandler;
import com.wenxin2.marioverse.utils.EntityWarpPlayerHandler;
import com.wenxin2.marioverse.utils.PowerUpHandler;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements BlockWarpPlayerHandler, EntityWarpPlayerHandler, PowerUpHandler {
    @Unique private boolean mv$hasFireFlower;
    @Unique private boolean mv$hasIceFlower;
    @Unique private boolean mv$hasMegaMushroom;
    @Unique private boolean mv$hasMushroom;
    @Unique private boolean mv$hasSuperStar;
    @Unique private boolean mv$preventWarp;
    @Unique private int mv$consecutiveBounces;
    @Unique private int mv$oneUpsRewarded;
    @Unique private int mv$preventWarpCooldown;
    @Unique private int mv$superStarCooldown;
    @Unique private int mv$warpCooldown;

    @Override
    public boolean mv$getBlockWarpTeleportConfig() {
        return ConfigRegistry.TELEPORT_PLAYERS.get();
    }

    @Override
    public boolean mv$getEntityWarpTeleportConfig() {
        return ConfigRegistry.TELEPORT_PLAYERS.get();
    }

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

    @Override
    public void mv$clearAllPowerUps() {
        mv$setFireFlower(false);
        mv$setIceFlower(false);
    }

    @Override
    public boolean mv$hasMushroom() {
        return this.mv$hasMushroom;
    }

    @Override
    public void mv$setMushroom(boolean hasMushroom) {
        this.mv$hasMushroom = hasMushroom;
    }

    @Override
    public boolean mv$hasMegaMushroom() {
        return this.mv$hasMegaMushroom;
    }

    @Override
    public void mv$setMegaMushroom(boolean hasMegaMushroom) {
        this.mv$hasMegaMushroom = hasMegaMushroom;
    }

    @Override
    public boolean mv$hasFireFlower() {
        return this.mv$hasFireFlower;
    }

    @Override
    public void mv$setFireFlower(boolean hasFireFlower) {
        this.mv$hasFireFlower = hasFireFlower;
    }

    @Override
    public boolean mv$hasIceFlower() {
        return this.mv$hasIceFlower;
    }

    @Override
    public void mv$setIceFlower(boolean hasIceFlower) {
        this.mv$hasIceFlower = hasIceFlower;
    }

    @Override
    public boolean mv$hasSuperStar() {
        return this.mv$hasSuperStar;
    }

    @Override
    public void mv$setSuperStar(boolean hasSuperStar) {
        this.mv$hasSuperStar = hasSuperStar;
    }

    @Override
    public int mv$getSuperStarCooldown() {
        return this.mv$superStarCooldown;
    }

    @Override
    public void mv$setSuperStarCooldown(int superStarCooldown) {
        this.mv$superStarCooldown = superStarCooldown;
    }

    @Override
    public int mv$getConsecutiveBounces() {
        return this.mv$consecutiveBounces;
    }

    @Override
    public void mv$setConsecutiveBounces(int consecutiveBounces) {
        this.mv$consecutiveBounces = consecutiveBounces;
    }

    @Override
    public int mv$getOneUpsRewarded() {
        return this.mv$oneUpsRewarded;
    }

    @Override
    public void mv$setOneUpsRewarded(int oneUpsRewarded) {
        this.mv$oneUpsRewarded = oneUpsRewarded;
    }

    @Override
    public boolean mv$doPreventWarp() {
        return this.mv$preventWarp;
    }

    @Override
    public void mv$setPreventWarp(boolean preventWarp) {
        this.mv$preventWarp = preventWarp;
    }

    @Override
    public int mv$getPreventWarpCooldown() {
        return this.mv$preventWarpCooldown;
    }

    @Override
    public void mv$setPreventWarpCooldown(int preventWarpCooldown) {
        this.mv$preventWarpCooldown = preventWarpCooldown;
    }

    @Override
    public int mv$getWarpCooldown() {
        return this.mv$warpCooldown;
    }

    @Override
    public void mv$setWarpCooldown(int warpCooldown) {
        this.mv$warpCooldown = warpCooldown;
    }
}