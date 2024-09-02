package com.wenxin2.warp_pipes.mixin;

import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.warp_pipes.init.Config;
import com.wenxin2.warp_pipes.init.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class PlayerMixin extends Entity {
    @Shadow protected abstract float getBlockSpeedFactor();

    @Shadow public abstract void displayClientMessage(Component component, boolean isAboveHotbar);

    @Unique
    private static final int MAX_PARTICLE_AMOUNT = 40;

    @Unique
    private int warpPipes$warpCooldown;

    public PlayerMixin(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void baseTick() {
        Level world = this.level();
        BlockPos pos = this.blockPosition();
        BlockState state = world.getBlockState(pos);
        BlockState stateAboveEntity = world.getBlockState(pos.above(Math.round(this.getBbHeight())));

        for (Direction facing : Direction.values()) {
            BlockPos offsetPos = pos.relative(facing);
            BlockState offsetState = world.getBlockState(offsetPos);

            if (offsetState.getBlock() instanceof WarpPipeBlock) {
                this.warpPipes$enterPipe(offsetPos);
            }
            if (state.getBlock() instanceof WarpPipeBlock) {
                this.warpPipes$enterPipe(pos);
            }
        }

        if (stateAboveEntity.getBlock() instanceof WarpPipeBlock) {
            this.warpPipes$enterPipeBelow(pos);
        }

        if (this.warpPipes$warpCooldown > 0) {
            --this.warpPipes$warpCooldown;
        }
        super.baseTick();

//        if (stateAboveEntity.is(Blocks.BRICKS) && this.getDeltaMovement().y > 0)
//        {
//            world.destroyBlock(pos.above(Math.round(this.getBbHeight())), true);
//        }
    }

    @Unique
    public void warpPipes$spawnParticles(Entity entity, Level world) {
        RandomSource random = world.getRandom();
        for(int i = 0; i < MAX_PARTICLE_AMOUNT; ++i) {
            world.addParticle(ParticleTypes.ENCHANT,
                    entity.getRandomX(0.5D), entity.getRandomY(), entity.getRandomZ(0.5D),
                    (random.nextDouble() - 0.5D) * 2.0D, -random.nextDouble(),
                    (random.nextDouble() - 0.5D) * 2.0D);
        }
    }

    @Unique
    public int warpPipes$getWarpCooldown() {
        return warpPipes$warpCooldown;
    }

    @Unique
    public void warpPipes$setWarpCooldown(int cooldown) {
        this.warpPipes$warpCooldown = cooldown;
    }

    @Unique
    public void warpPipes$enterPipeBelow(BlockPos pos) {
        Level world = this.level();
        BlockState stateAboveEntity = world.getBlockState(pos.above(Math.round(this.getBbHeight())));
        BlockEntity blockEntity = world.getBlockEntity(pos.above(Math.round(this.getBbHeight())));
        BlockPos warpPos;

        double entityX = this.getX();
        double entityZ = this.getZ();

        int blockX = pos.getX();
        int blockZ = pos.getZ();

        if (!stateAboveEntity.getValue(WarpPipeBlock.CLOSED) && blockEntity instanceof WarpPipeBlockEntity warpPipeBE && warpPipeBE.getLevel() != null
                && !warpPipeBE.preventWarp && Config.TELEPORT_PLAYERS.get() && !this.getType().is(ModTags.WARP_BlACKLIST)
                && !this.getPersistentData().getBoolean("warp_pipes:prevent_warp")) {
            warpPos = warpPipeBE.destinationPos;
            int entityId = this.getId();

            if (world.isClientSide() && WarpPipeBlock.teleportedEntities.getOrDefault(entityId, false)) {
                this.warpPipes$spawnParticles(this, world);

                // Reset the teleport status for the entity
                WarpPipeBlock.teleportedEntities.put(entityId, false);
            }

            if (stateAboveEntity.getValue(WarpPipeBlock.FACING) == Direction.DOWN && this.getDeltaMovement().y > 0
                    && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                if (this.warpPipes$getWarpCooldown() == 0) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp(this, warpPos, world, stateAboveEntity);
                    else if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, stateAboveEntity);
                    this.warpPipes$setWarpCooldown(Config.WARP_COOLDOWN.get());
                } /* else if (this.getWarpCooldown() <= 10)
                displayDestinationMissingMessage(); */ else if (warpPipeBE.hasDestinationPos()) this.warpPipes$displayCooldownMessage();
            }
        }
    }

    @Unique
    public void warpPipes$enterPipe(BlockPos pos) {

        Level world = this.level();
        BlockState state = world.getBlockState(pos);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        BlockPos warpPos;

        double entityX = this.getX();
        double entityY = this.getY();
        double entityZ = this.getZ();

        int blockX = pos.getX();
        int blockY = pos.getY();
        int blockZ = pos.getZ();

        if (!state.getValue(WarpPipeBlock.CLOSED) && blockEntity instanceof WarpPipeBlockEntity warpPipeBE && warpPipeBE.getLevel() != null
                && !warpPipeBE.preventWarp && Config.TELEPORT_PLAYERS.get() && !this.getType().is(ModTags.WARP_BlACKLIST)
                && !this.getPersistentData().getBoolean("warp_pipes:prevent_warp")) {
//            WarpData warpData = WarpProxy.getInstance().getWarp(warpPipeBE.warpUuid);
            warpPos = warpPipeBE.destinationPos;
            int entityId = this.getId();

            if (world.isClientSide() && WarpPipeBlock.teleportedEntities.getOrDefault(entityId, false)) {
                this.warpPipes$spawnParticles(this, world);

                // Reset the teleport status for the entity
                WarpPipeBlock.teleportedEntities.put(entityId, false);
            }

            if (state.getValue(WarpPipeBlock.FACING) == Direction.UP && this.isShiftKeyDown() && (entityY + this.getBbHeight() >= blockY - 1)
                    && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                if (this.warpPipes$getWarpCooldown() == 0) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp(this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.warpPipes$setWarpCooldown(Config.WARP_COOLDOWN.get());
                } /* else if (this.getWarpCooldown() <= 10)
                displayDestinationMissingMessage(); */ else if (warpPipeBE.hasDestinationPos()) this.warpPipes$displayCooldownMessage();
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.NORTH && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.SOUTH
                    && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ)) {
                if (this.warpPipes$getWarpCooldown() == 0) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp(this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.warpPipes$setWarpCooldown(Config.WARP_COOLDOWN.get());
                } /* else if (this.getWarpCooldown() <= 10)
                displayDestinationMissingMessage(); */ else if (warpPipeBE.hasDestinationPos()) this.warpPipes$displayCooldownMessage();
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.SOUTH && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.NORTH
                    && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ > blockZ + 0.25)) {
                if (this.warpPipes$getWarpCooldown() == 0) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp(this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.warpPipes$setWarpCooldown(Config.WARP_COOLDOWN.get());
                } /* else if (this.getWarpCooldown() <= 10)
                displayDestinationMissingMessage(); */ else if (warpPipeBE.hasDestinationPos()) this.warpPipes$displayCooldownMessage();
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.EAST && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.WEST
                    && (entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                if (this.warpPipes$getWarpCooldown() == 0) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp(this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.warpPipes$setWarpCooldown(Config.WARP_COOLDOWN.get());
                } /* else if (this.getWarpCooldown() <= 10)
                displayDestinationMissingMessage(); */ else if (warpPipeBE.hasDestinationPos()) this.warpPipes$displayCooldownMessage();
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.WEST && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.EAST
                    && (entityX < blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                if (this.warpPipes$getWarpCooldown() == 0) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp(this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.warpPipes$setWarpCooldown(Config.WARP_COOLDOWN.get());
                } /* else if (this.getWarpCooldown() <= 10)
                displayDestinationMissingMessage(); */ else if (warpPipeBE.hasDestinationPos()) this.warpPipes$displayCooldownMessage();
            }
        } else if (!state.getValue(WarpPipeBlock.CLOSED) && (!Config.TELEPORT_PLAYERS.get() || this.getType().is(ModTags.WARP_BlACKLIST))) {
            if (state.getValue(WarpPipeBlock.FACING) == Direction.UP && this.isShiftKeyDown() && (entityY + this.getBbHeight() >= blockY - 1)
                    && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                this.warpPipes$displayNoTeleportMessage();
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.DOWN && (this.getBlockY() < blockY)
                    && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                this.warpPipes$displayNoTeleportMessage();
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.NORTH && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.SOUTH
                    && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ)) {
                this.warpPipes$displayNoTeleportMessage();
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.SOUTH && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.NORTH
                    && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ > blockZ + 0.25)) {
                this.warpPipes$displayNoTeleportMessage();
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.EAST && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.WEST
                    && (entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                this.warpPipes$displayNoTeleportMessage();
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.WEST && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.EAST
                    && (entityX < blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                this.warpPipes$displayNoTeleportMessage();
            }
        }

    }

    @Unique

    public void warpPipes$displayCooldownMessage() {
        if (this.warpPipes$getWarpCooldown() >= 10) {
            if (Config.WARP_COOLDOWN_MESSAGE.get()) {
                if (Config.WARP_COOLDOWN_MESSAGE_TICKS.get())
                    this.displayClientMessage(Component.translatable("display.warp_pipes.warp_cooldown.ticks",
                            this.warpPipes$getWarpCooldown()).withStyle(ChatFormatting.RED), true);
                else this.displayClientMessage(Component.translatable("display.warp_pipes.warp_cooldown")
                        .withStyle(ChatFormatting.RED), true);
            }
        }
    }

    @Unique
    public void warpPipes$displayNoTeleportMessage() {
        if (!Config.TELEPORT_PLAYERS.get() || this.getType().is(ModTags.WARP_BlACKLIST)) {
            this.displayClientMessage(Component.translatable("display.warp_pipes.players_cannot_teleport")
                    .withStyle(ChatFormatting.RED), true);
        }
    }

    @Unique
    public void warpPipes$displayDestinationMissingMessage() {
        this.displayClientMessage(Component.translatable("display.warp_pipes.warp_destination_missing")
                .withStyle(ChatFormatting.RED), true);
    }
}
