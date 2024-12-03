package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.WarpDoorBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract Level level();

    @Shadow public abstract double getX();

    @Shadow public abstract double getY();
    @Shadow public abstract double getZ();

    @Shadow public abstract float getBbHeight();

    @Shadow public abstract float getBbWidth();

    @Shadow public abstract int getId();

    @Shadow public abstract BlockPos blockPosition();

    @Shadow public abstract double getRandomX(double p_20209_);

    @Shadow public abstract double getRandomY();

    @Shadow public abstract double getRandomZ(double p_20263_);

    @Shadow public abstract EntityType<?> getType();

    @Shadow public abstract void setPos(Vec3 p_146885_);

    @Unique
    private static final int MAX_PARTICLE_AMOUNT = 100;
    @Unique
    private int marioverse$warpCooldown;

    @Inject(at = @At("TAIL"), method = "baseTick")
    public void baseTick(CallbackInfo ci) {
        Level world = this.level();
        BlockPos pos = this.blockPosition();
        BlockState state = world.getBlockState(pos);
        BlockState stateAboveEntity = world.getBlockState(pos.above(Math.round(this.getBbHeight())));
        Entity entity = (Entity) (Object) this;

        for (Direction facing : Direction.values()) {
            BlockPos offsetPos = pos.relative(facing);
            BlockState offsetState = world.getBlockState(offsetPos);

            if (offsetState.getBlock() instanceof WarpPipeBlock && !offsetState.getValue(WarpPipeBlock.CLOSED)
                    && !this.getType().is(TagRegistry.CANNOT_WARP) && !(entity instanceof LivingEntity))
                this.marioverse$enterPipe(offsetPos);
            if (state.getBlock() instanceof WarpPipeBlock && !state.getValue(WarpPipeBlock.CLOSED)
                    && !this.getType().is(TagRegistry.CANNOT_WARP) && !(entity instanceof LivingEntity))
                this.marioverse$enterPipe(pos);
        }

        if (stateAboveEntity.getBlock() instanceof WarpPipeBlock && !stateAboveEntity.getValue(WarpPipeBlock.CLOSED)
                && !this.getType().is(TagRegistry.CANNOT_WARP) && !(entity instanceof LivingEntity))
            this.marioverse$enterPipeBelow(pos);

        if (world.getBlockEntity(pos) instanceof WarpDoorBlockEntity
                && state.getBlock() instanceof DoorBlock && state.getValue(DoorBlock.OPEN)
                && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER
                && !this.getType().is(TagRegistry.CANNOT_WARP) && !(entity instanceof LivingEntity))
            this.marioverse$enterWarpDoor(pos);

        if (this.marioverse$warpCooldown > 0)
            --this.marioverse$warpCooldown;
    }

    @Unique
    public int marioverse$getWarpCooldown() {
        return marioverse$warpCooldown;
    }

    @Unique
    public void marioverse$setWarpCooldown(int cooldown) {
        this.marioverse$warpCooldown = cooldown;
    }

    @Unique
    public void marioverse$enterPipeBelow(BlockPos pos) {
        Entity entity = (Entity) (Object) this;
        Level world = this.level();
        BlockState stateAboveEntity = world.getBlockState(pos.above(Math.round(this.getBbHeight())));
        BlockEntity blockEntity = world.getBlockEntity(pos.above(Math.round(this.getBbHeight())));
        BlockPos warpPos;

        double entityX = this.getX();
        double entityZ = this.getZ();

        int blockX = pos.getX();
        int blockZ = pos.getZ();

        if (blockEntity instanceof WarpPipeBlockEntity warpPipeBE && warpPipeBE.getLevel() != null
                && !warpPipeBE.preventWarp && ConfigRegistry.TELEPORT_PLAYERS.get()) {
            warpPos = warpPipeBE.destinationPos;
            int entityId = this.getId();

            if (!world.isClientSide() && WarpPipeBlockEntity.teleportedEntities.getOrDefault(entityId, false)) {
                world.broadcastEntityEvent(entity, (byte) 120);

                // Reset the teleport status for the entity
                WarpPipeBlockEntity.teleportedEntities.put(entityId, false);
            }

            if (this.marioverse$getWarpCooldown() == 0) {
                if (stateAboveEntity.getValue(WarpPipeBlock.FACING) == Direction.DOWN
                        && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlockEntity.warp(entity, warpPos, world, stateAboveEntity);
                    else if (warpPipeBE.getUuid() != null && warpPipeBE.getWarpUuid() != null && WarpPipeBlockEntity.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlockEntity.warp(entity, WarpPipeBlockEntity.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, stateAboveEntity);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                }
            }
        }
    }
    
    @Unique
    public void marioverse$enterPipe(BlockPos pos) {
        Entity entity = (Entity) (Object) this;
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

        if (blockEntity instanceof WarpPipeBlockEntity warpPipeBE
                && !warpPipeBE.preventWarp && ConfigRegistry.TELEPORT_NON_MOBS.get()) {
            warpPos = warpPipeBE.destinationPos;
            int entityId = this.getId();

            if (!world.isClientSide() && WarpPipeBlockEntity.teleportedEntities.getOrDefault(entityId, false)) {
                world.broadcastEntityEvent(entity, (byte) 120);

                // Reset the teleport status for the entity
                WarpPipeBlockEntity.teleportedEntities.put(entityId, false);
            }

            if (this.marioverse$getWarpCooldown() == 0) {
                if (state.getValue(WarpPipeBlock.FACING) == Direction.UP && (entityY > blockY - 1)
                        && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlockEntity.warp(entity, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && warpPipeBE.getWarpUuid() != null && WarpPipeBlockEntity.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlockEntity.warp(entity, WarpPipeBlockEntity.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.NORTH
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ)) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlockEntity.warp(entity, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && warpPipeBE.getWarpUuid() != null && WarpPipeBlockEntity.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlockEntity.warp(entity, WarpPipeBlockEntity.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.SOUTH
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ > blockZ)) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlockEntity.warp(entity, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && warpPipeBE.getWarpUuid() != null && WarpPipeBlockEntity.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlockEntity.warp(entity, WarpPipeBlockEntity.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.EAST
                        && (entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlockEntity.warp(entity, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && warpPipeBE.getWarpUuid() != null && WarpPipeBlockEntity.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlockEntity.warp(entity, WarpPipeBlockEntity.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.WEST
                        && (entityX < blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlockEntity.warp(entity, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && warpPipeBE.getWarpUuid() != null && WarpPipeBlockEntity.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlockEntity.warp(entity, WarpPipeBlockEntity.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                }
            }
        }
    }

    @Unique
    public void marioverse$enterWarpDoor(BlockPos pos) {
        Level world = this.level();
        BlockState state = world.getBlockState(pos);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        BlockPos warpPos;
        Entity entity = (Entity) (Object) this;

        if (state.getBlock() instanceof DoorBlock doorBlock && blockEntity instanceof WarpDoorBlockEntity warpDoorBE
                && !warpDoorBE.preventWarp && ConfigRegistry.TELEPORT_NON_MOBS.get()) {
            warpPos = warpDoorBE.destinationPos;
            int entityId = this.getId();

            if (!world.isClientSide() && WarpDoorBlockEntity.teleportedEntities.getOrDefault(entityId, false)) {
                world.broadcastEntityEvent(entity, (byte) 120);

                // Reset the teleport status for the entity
                WarpDoorBlockEntity.teleportedEntities.put(entityId, false);
            }

            if (this.marioverse$getWarpCooldown() == 0) {
                if (warpPos != null) {
                    BlockState warpState = world.getBlockState(warpPos);

                    WarpDoorBlockEntity.warp(entity, warpPos, world, state, doorBlock, warpDoorBE);
                    if (state.getBlock() instanceof DoorBlock)
                        warpDoorBE.playDoorSounds(entity, world, pos, state.getValue(DoorBlock.OPEN), doorBlock.type());
                    if (warpState.getBlock() instanceof DoorBlock)
                        warpDoorBE.playDoorSounds(entity, world, warpPos, warpState.getValue(DoorBlock.OPEN), doorBlock.type());

                    if (!world.isClientSide) {
                        if (state.getBlock() instanceof DoorBlock)
                            world.setBlock(pos, state.setValue(DoorBlock.OPEN, Boolean.FALSE)
                                    .setValue(DoorBlock.FACING, state.getValue(DoorBlock.FACING)), 10);
                        if (world.getBlockEntity(warpPos) instanceof WarpDoorBlockEntity warpDoorBlockEntity
                                && !warpDoorBlockEntity.breakDoor)
                            world.setBlock(warpPos, warpState.setValue(DoorBlock.OPEN, Boolean.TRUE)
                                    .setValue(DoorBlock.FACING, state.getValue(DoorBlock.FACING)), 10);
                    }
                } else if (warpDoorBE.getUuid() != null && warpDoorBE.getWarpUuid() != null
                        && WarpDoorBlockEntity.findMatchingUUID(warpDoorBE.getUuid(), world, pos) != null) {
                    warpPos = WarpDoorBlockEntity.findMatchingUUID(warpDoorBE.getUuid(), world, pos);
                    BlockState warpState = world.getBlockState(warpPos);

                    WarpDoorBlockEntity.warp(entity, warpPos, world, state, doorBlock, warpDoorBE);
                    if (state.getBlock() instanceof DoorBlock)
                        warpDoorBE.playDoorSounds(entity, world, pos, state.getValue(DoorBlock.OPEN), doorBlock.type());
                    if (warpState.getBlock() instanceof DoorBlock)
                        warpDoorBE.playDoorSounds(entity, world, warpPos, warpState.getValue(DoorBlock.OPEN), doorBlock.type());

                    if (!world.isClientSide) {
                        if (state.getBlock() instanceof DoorBlock)
                            world.setBlock(pos, state.setValue(DoorBlock.OPEN, Boolean.FALSE)
                                    .setValue(DoorBlock.FACING, state.getValue(DoorBlock.FACING)), 10);
                        if (world.getBlockEntity(warpPos) instanceof WarpDoorBlockEntity warpDoorBlockEntity
                                && !warpDoorBlockEntity.breakDoor)
                            world.setBlock(warpPos, warpState.setValue(DoorBlock.OPEN, Boolean.TRUE)
                                    .setValue(DoorBlock.FACING, state.getValue(DoorBlock.FACING)), 10);
                    }
                }
                this.marioverse$setWarpCooldown(ConfigRegistry.WARP_DOOR_COOLDOWN.get());
            }
        }
    }
}
