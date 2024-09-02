package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.init.Config;
import com.wenxin2.marioverse.init.ModTags;
import java.util.Collection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
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

        for (Direction facing : Direction.values()) {
            BlockPos offsetPos = pos.relative(facing);
            BlockState offsetState = world.getBlockState(offsetPos);

            if (offsetState.getBlock() instanceof WarpPipeBlock) {
                this.marioverse$enterPipe(offsetPos);
            }
            if (state.getBlock() instanceof WarpPipeBlock) {
                this.marioverse$enterPipe(pos);
            }
        }

        if (stateAboveEntity.getBlock() instanceof WarpPipeBlock) {
            this.marioverse$enterPipeBelow(pos);
        }

        if (this.marioverse$warpCooldown > 0) {
            --this.marioverse$warpCooldown;
        }
    }

    @Unique
    public void marioverse$spawnParticles(Level world) {
        RandomSource random = world.getRandom();

        // Calculate a scaling factor based on entity dimensions
        float scaleFactor = this.getBbHeight() * this.getBbWidth();
        // Calculate the particle count based on the scaling factor
        int particleCount = (int) (scaleFactor * 40);
        // Ensure particle count does not exceed the maximum limit
        particleCount = Math.min(particleCount, MAX_PARTICLE_AMOUNT);

        Collection<ServerPlayer> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            for (int i = 0; i < particleCount; ++i) {
                player.connection.send(new ClientboundLevelParticlesPacket(
                        ParticleTypes.ENCHANT,      // Particle type
                        false,                       // Long distance
                        this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), // Position
                        (random.nextFloat() - 0.5F) * 2.0F, -random.nextFloat(),
                        (random.nextFloat() - 0.5F) * 2.0F, // Motion
                        0,                          // Particle data
                        2                           // Particle count
                ));
            }
        }
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
        Level world = this.level();
        BlockState stateAboveEntity = world.getBlockState(pos.above(Math.round(this.getBbHeight())));
        BlockEntity blockEntity = world.getBlockEntity(pos.above(Math.round(this.getBbHeight())));
        BlockPos warpPos;

        double entityX = this.getX();
        double entityZ = this.getZ();

        int blockX = pos.getX();
        int blockZ = pos.getZ();

        if (!stateAboveEntity.getValue(WarpPipeBlock.CLOSED) && blockEntity instanceof WarpPipeBlockEntity warpPipeBE && warpPipeBE.getLevel() != null
                && !warpPipeBE.preventWarp && Config.TELEPORT_PLAYERS.get() && !this.getType().is(ModTags.WARP_BLACKLIST)) {
            warpPos = warpPipeBE.destinationPos;
            int entityId = this.getId();

            if (!world.isClientSide() && WarpPipeBlock.teleportedEntities.getOrDefault(entityId, false)) {
                this.marioverse$spawnParticles(world);

                // Reset the teleport status for the entity
                WarpPipeBlock.teleportedEntities.put(entityId, false);
            }

            if (this.marioverse$getWarpCooldown() == 0) {
                if (stateAboveEntity.getValue(WarpPipeBlock.FACING) == Direction.DOWN
                        && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp((Entity) (Object) this, warpPos, world, stateAboveEntity);
                    else if (warpPipeBE.getUuid() != null && warpPipeBE.getWarpUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp((Entity) (Object) this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, stateAboveEntity);
                    this.marioverse$setWarpCooldown(Config.WARP_COOLDOWN.get());
                }
            }
        }
    }
    
    @Unique
    public void marioverse$enterPipe(BlockPos pos) {
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

        if (!state.getValue(WarpPipeBlock.CLOSED) && blockEntity instanceof WarpPipeBlockEntity warpPipeBE
                && !warpPipeBE.preventWarp && Config.TELEPORT_NON_MOBS.get() && !this.getType().is(ModTags.WARP_BLACKLIST)) {
            warpPos = warpPipeBE.destinationPos;
            int entityId = this.getId();

            if (!world.isClientSide() && WarpPipeBlock.teleportedEntities.getOrDefault(entityId, false)) {
                this.marioverse$spawnParticles(world);

                // Reset the teleport status for the entity
                WarpPipeBlock.teleportedEntities.put(entityId, false);
            }

            if (this.marioverse$getWarpCooldown() == 0) {
                if (state.getValue(WarpPipeBlock.FACING) == Direction.UP && (entityY > blockY - 1)
                        && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp((Entity) (Object) this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && warpPipeBE.getWarpUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp((Entity) (Object) this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.marioverse$setWarpCooldown(Config.WARP_COOLDOWN.get());
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.NORTH
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ)) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp((Entity) (Object) this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && warpPipeBE.getWarpUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp((Entity) (Object) this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.marioverse$setWarpCooldown(Config.WARP_COOLDOWN.get());
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.SOUTH
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ > blockZ)) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp((Entity) (Object) this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && warpPipeBE.getWarpUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp((Entity) (Object) this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.marioverse$setWarpCooldown(Config.WARP_COOLDOWN.get());
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.EAST
                        && (entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp((Entity) (Object) this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && warpPipeBE.getWarpUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp((Entity) (Object) this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.marioverse$setWarpCooldown(Config.WARP_COOLDOWN.get());
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.WEST
                        && (entityX < blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp((Entity) (Object) this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && warpPipeBE.getWarpUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp((Entity) (Object) this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.marioverse$setWarpCooldown(Config.WARP_COOLDOWN.get());
                }
            }
        }
    }
}
