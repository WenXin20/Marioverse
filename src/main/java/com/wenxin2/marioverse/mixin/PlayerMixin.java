package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpDoorBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpTrapDoorBlockEntity;
import com.wenxin2.marioverse.entities.WarpLinkableEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class PlayerMixin extends Entity {
    @Shadow protected abstract float getBlockSpeedFactor();

    @Shadow public abstract void displayClientMessage(Component component, boolean isAboveHotbar);

    public PlayerMixin(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void baseTick() {
        Player player = (Player) (Object) this;
        Level world = player.level();
        BlockPos pos = player.blockPosition();
        BlockPos posAboveEntity = pos.above(Math.round(player.getBbHeight()));
        BlockPos posInBlock = pos.above(Math.round(player.getBbHeight()) - 1);
        BlockState state = world.getBlockState(pos);
        BlockState stateAboveEntity = world.getBlockState(posAboveEntity);
        BlockState stateInBlock = world.getBlockState(posInBlock);

        int warpCooldown = player.getPersistentData().getInt("marioverse:warp_cooldown");

        for (Direction facing : Direction.values()) {
            BlockPos offsetPos = pos.relative(facing);
            BlockState offsetState = world.getBlockState(offsetPos);

            if (!player.getPersistentData().getBoolean("marioverse:prevent_warp")) {
                if (offsetState.getBlock() instanceof WarpPipeBlock && !offsetState.getValue(WarpPipeBlock.CLOSED))
                    this.marioverse$enterWarp(offsetPos);
                if (state.getBlock() instanceof WarpPipeBlock && !state.getValue(WarpPipeBlock.CLOSED))
                    this.marioverse$enterWarp(pos);
            }
        }

        if (stateAboveEntity.getBlock() instanceof WarpPipeBlock && !stateAboveEntity.getValue(WarpPipeBlock.CLOSED)
                && !player.getPersistentData().getBoolean("marioverse:prevent_warp"))
            this.marioverse$enterWarp(pos);

        if (!ConfigRegistry.DISABLE_WARP_DOORS.get()
                && world.getBlockEntity(pos) instanceof WarpDoorBlockEntity
                && state.getBlock() instanceof DoorBlock && state.getValue(DoorBlock.OPEN)
                && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER
                && !player.getPersistentData().getBoolean("marioverse:prevent_warp"))
            this.marioverse$enterWarp(pos);

        if (!ConfigRegistry.DISABLE_WARP_TRAPDOORS.get()
                && world.getBlockEntity(pos) instanceof WarpTrapDoorBlockEntity
                && state.getBlock() instanceof TrapDoorBlock && state.getValue(TrapDoorBlock.OPEN)
                && !player.getPersistentData().getBoolean("marioverse:prevent_warp"))
            this.marioverse$enterWarp(pos);

        if (!ConfigRegistry.DISABLE_WARP_TRAPDOORS.get()
                && world.getBlockEntity(posInBlock) instanceof WarpTrapDoorBlockEntity
                && stateInBlock.getBlock() instanceof TrapDoorBlock && stateInBlock.getValue(TrapDoorBlock.OPEN)
                && !player.getPersistentData().getBoolean("marioverse:prevent_warp"))
            this.marioverse$enterWarp(posInBlock);

        if (warpCooldown > 0)
            player.getPersistentData().putInt("marioverse:warp_cooldown", warpCooldown - 1);

        int preventWarpCooldown = this.getPersistentData().getInt("marioverse:prevent_warp_cooldown");
        if (preventWarpCooldown > 0)
            player.getPersistentData().putInt("marioverse:prevent_warp_cooldown", preventWarpCooldown - 1);

        if (preventWarpCooldown == 0 && this.getPersistentData().getBoolean("marioverse:prevent_warp"))
            player.getPersistentData().putBoolean("marioverse:prevent_warp", false);
        super.baseTick();
    }

    @Unique
    public int marioverse$getWarpCooldown() {
        Player player = (Player) (Object) this;
        return player.getPersistentData().getInt("marioverse:warp_cooldown");
    }

    @Unique
    public void marioverse$setWarpCooldown(int cooldown) {
        Player player = (Player) (Object) this;
        player.getPersistentData().putInt("marioverse:warp_cooldown", cooldown);
    }

    @Unique
    public void marioverse$enterWarp(BlockPos pos) {
        Player player = (Player) (Object) this;
        Level world = player.level();
        BlockState state = world.getBlockState(pos);
        BlockState stateAboveEntity = world.getBlockState(pos.above(Math.round(this.getBbHeight())));
        BlockEntity blockEntity = world.getBlockEntity(pos);
        BlockEntity blockEntityAbove = world.getBlockEntity(pos.above(Math.round(this.getBbHeight())));
        BlockPos warpPos;

        if (blockEntity instanceof BaseWarpBlockEntity warpBE && warpBE.getLevel() != null
                && !warpBE.preventWarp) {
            warpPos = warpBE.destinationPos;
            int entityId = this.getId();

            if (BaseWarpBlockEntity.WARPED_ENTITIES.getOrDefault(entityId, true))
                // Reset the teleport status for the entity
                BaseWarpBlockEntity.WARPED_ENTITIES.put(entityId, false);


            if (state.getBlock() instanceof DoorBlock || state.getBlock() instanceof TrapDoorBlock)
                this.marioverse$enterWarpDoor(pos, warpPos, warpBE);

            if (state.getBlock() instanceof WarpPipeBlock)
                this.marioverse$enterWarpPipe(pos, warpPos, warpBE);
        }

        if (blockEntityAbove instanceof BaseWarpBlockEntity warpBE && warpBE.getLevel() != null
                && !warpBE.preventWarp) {
            warpPos = warpBE.destinationPos;
            int entityId = this.getId();

            if (BaseWarpBlockEntity.WARPED_ENTITIES.getOrDefault(entityId, true))
                BaseWarpBlockEntity.WARPED_ENTITIES.put(entityId, false);

            if (stateAboveEntity.getBlock() instanceof WarpPipeBlock)
                this.marioverse$enterWarpPipeAbove(pos, warpPos, warpBE);
        }

        List<Painting> nearbyPaintings = world.getEntitiesOfClass(Painting.class, player.getBoundingBox());
        for (Painting painting : nearbyPaintings) {
            if (painting instanceof WarpLinkableEntity linkableEntity && !linkableEntity.marioverse$getPreventWarp()) {
                int entityId = this.getId();

                if (WarpLinkableEntity.WARPED_ENTITIES.getOrDefault(entityId, false))
                    WarpLinkableEntity.WARPED_ENTITIES.put(entityId, false);

                this.marioverse$enterWarpPainting(linkableEntity);
            }
            break;
        }
    }

    @Unique
    public void marioverse$warp(BlockPos pos, BlockState state, BlockPos warpPos, BaseWarpBlockEntity warpBE) {
        Player player = (Player) (Object) this;
        Level world = player.level();

        if (warpPos != null && world.getBlockEntity(warpPos) instanceof BaseWarpBlockEntity) {
            BlockState warpState = world.getBlockState(warpPos);

            this.marioverse$updateDoor(pos, state, warpPos, warpState);
            if (warpState.getBlock() instanceof DoorBlock doorBlock)
                WarpDoorBlockEntity.warp(player, warpPos, world, warpState, doorBlock, warpBE);
            if (warpState.getBlock() instanceof TrapDoorBlock trapdoorBlock)
                WarpTrapDoorBlockEntity.warp(player, warpPos, world, warpState, trapdoorBlock, warpBE);
            if (warpState.getBlock() instanceof WarpPipeBlock)
                WarpPipeBlockEntity.warp(player, warpPos, world, warpState);

            if (state.getBlock() instanceof WarpPipeBlock)
                world.playSound(null, pos, SoundRegistry.PIPE_WARPS.get(), SoundSource.BLOCKS);
        } else if (warpBE.getUUID() != null && warpBE.getWarpUuid() != null
                && BaseWarpBlockEntity.findMatchingUUID(warpBE.getUUID()) != null) {
            warpPos = BaseWarpBlockEntity.findMatchingUUID(warpBE.getUUID());
            BlockState warpState = world.getBlockState(warpPos);

            this.marioverse$updateDoor(pos, state, warpPos, warpState);
            if (warpState.getBlock() instanceof DoorBlock doorblock)
                WarpDoorBlockEntity.warp(player, warpPos, world, warpState, doorblock, warpBE);
            if (warpState.getBlock() instanceof TrapDoorBlock trapdoorBlock)
                WarpTrapDoorBlockEntity.warp(player, warpPos, world, warpState, trapdoorBlock, warpBE);
            if (warpState.getBlock() instanceof WarpPipeBlock)
                WarpPipeBlockEntity.warp(player, warpPos, world, warpState);

            if (state.getBlock() instanceof WarpPipeBlock)
                world.playSound(null, pos, SoundRegistry.PIPE_WARPS.get(), SoundSource.BLOCKS);
        }
    }

    @Unique
    public void marioverse$warp(WarpLinkableEntity warpLinkableEntity) {
        LivingEntity entity = (LivingEntity) (Object) this;
        Level world = entity.level();

        if (world instanceof ServerLevel serverWorld && warpLinkableEntity.marioverse$getWarpUUID() != null) {
            UUID warpUUID = warpLinkableEntity.marioverse$getWarpUUID();
            Entity warpEntity = serverWorld.getEntity(warpLinkableEntity.marioverse$getWarpUUID());
            if (warpEntity != null) {
                if (warpEntity instanceof Painting painting) {
                    int width = painting.getVariant().value().width();
                    Direction direction = painting.getDirection();
                    BlockPos basePos = painting.getPos();

                    marioverse$warpPaintingDirection(basePos, direction, width, entity, world);

                    if (painting instanceof WarpLinkableEntity warpPainting && warpPainting.marioverse$isBreakPainting()) {
                        painting.kill();
                        painting.dropItem(painting);
                    }
                } else {
                    BlockPos warpPos = warpEntity.blockPosition();
                    WarpLinkableEntity.warp(entity, warpPos.getX(), warpPos.getY(), warpPos.getZ(), world);
                }
            } else {
                WarpLinkableEntity.WarpTarget savedTarget = WarpLinkableEntity.getWarpPos(warpUUID);

                if (savedTarget != null) {
                    BlockPos basePos = savedTarget.pos();
                    Direction direction = savedTarget.direction();
                    int width = savedTarget.width();

                    marioverse$warpPaintingDirection(basePos, direction, width, entity, world);

                    List<Entity> entitiesAtPos = world.getEntities(null, new AABB(basePos));
                    for (Entity targetEntity : entitiesAtPos) {
                        if (targetEntity.getUUID().equals(warpUUID) && targetEntity instanceof WarpLinkableEntity linkableEntity
                                && linkableEntity.marioverse$isBreakPainting()) {
                            targetEntity.kill();
                            if (targetEntity instanceof Painting painting)
                                painting.dropItem(painting);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Unique
    private static void marioverse$warpPaintingDirection(BlockPos basePos, Direction direction, double width, LivingEntity entity, Level world) {
        double centerX = basePos.getX();
        double centerY = basePos.getY();
        double centerZ = basePos.getZ();

        switch (direction) {
            case NORTH -> centerZ += 0.5;
            case SOUTH -> { centerX += width / 2; centerZ += 0.5; }
            case EAST  -> centerX += 0.5;
            case WEST  -> { centerZ += width / 2; centerX += 0.5; }
        }

        WarpLinkableEntity.warp(entity, centerX, centerY, centerZ, world);
    }

    @Unique
    public void marioverse$enterWarpDoor(BlockPos pos, BlockPos warpPos, BaseWarpBlockEntity warpBE) {
        Player player = (Player) (Object) this;
        Level world = player.level();
        BlockState state = world.getBlockState(pos);

        if (!ConfigRegistry.TELEPORT_PLAYERS.get() || player.getType().is(TagRegistry.CANNOT_WARP)
                || player.getPersistentData().getBoolean("marioverse:prevent_warp")) {
            this.marioverse$displayNoTeleportMessage();
        } else {
            if (this.marioverse$getWarpCooldown() == 0 && !player.isShiftKeyDown()) {
                this.marioverse$warp(pos, state, warpPos, warpBE);
                if (state.getBlock() instanceof DoorBlock)
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_DOOR_COOLDOWN.get());
                else this.marioverse$setWarpCooldown(ConfigRegistry.WARP_TRAPDOOR_COOLDOWN.get());
            } else if (warpBE.hasDestinationPos()) this.marioverse$displayCooldownMessage(state);
        }
    }

    @Unique
    public void marioverse$enterWarpPipe(BlockPos pos, BlockPos warpPos, BaseWarpBlockEntity warpBE) {
        Player player = (Player) (Object) this;
        Level world = player.level();
        BlockState state = world.getBlockState(pos);

        double entityX = player.getX();
        double entityY = player.getY();
        double entityZ = player.getZ();
        int blockX = pos.getX();
        int blockY = pos.getY();
        int blockZ = pos.getZ();

        if (!ConfigRegistry.TELEPORT_PLAYERS.get() || player.getType().is(TagRegistry.CANNOT_WARP)
                || player.getPersistentData().getBoolean("marioverse:prevent_warp")) {
            if (state.getValue(WarpPipeBlock.FACING) == Direction.UP && player.isShiftKeyDown() && (entityY + player.getBbHeight() >= blockY - 1)
                    && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                this.marioverse$displayNoTeleportMessage();
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.NORTH && !player.isShiftKeyDown() && player.getMotionDirection() == Direction.SOUTH
                    && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ)) {
                this.marioverse$displayNoTeleportMessage();
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.SOUTH && !player.isShiftKeyDown() && player.getMotionDirection() == Direction.NORTH
                    && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ > blockZ + 0.25)) {
                this.marioverse$displayNoTeleportMessage();
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.EAST && !player.isShiftKeyDown() && player.getMotionDirection() == Direction.WEST
                    && (entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                this.marioverse$displayNoTeleportMessage();
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.WEST && !player.isShiftKeyDown() && player.getMotionDirection() == Direction.EAST
                    && (entityX < blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                this.marioverse$displayNoTeleportMessage();
            }
        } else {
            if (state.getValue(WarpPipeBlock.FACING) == Direction.UP && player.isShiftKeyDown() && (entityY + player.getBbHeight() >= blockY - 1)
                    && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                if (this.marioverse$getWarpCooldown() == 0) {
                    this.marioverse$warp(pos, state, warpPos, warpBE);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                } /* else if (this.marioverse$getWarpCooldown() <= 10 && BaseWarpBlockEntity.findMatchingUUID(warpBE.getWarpUuid()) == null && !warpBE.hasDestinationPos())
                    marioverse$displayDestinationMissingMessage();*/
                else if (warpBE.hasDestinationPos())
                    this.marioverse$displayCooldownMessage(state);
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.NORTH && !player.isShiftKeyDown() && player.getMotionDirection() == Direction.SOUTH
                    && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ)) {
                if (this.marioverse$getWarpCooldown() == 0) {
                    this.marioverse$warp(pos, state, warpPos, warpBE);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                } /* else if (this.getWarpCooldown() <= 10)
                displayDestinationMissingMessage(); */
                else if (warpBE.hasDestinationPos())
                    this.marioverse$displayCooldownMessage(state);
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.SOUTH && !player.isShiftKeyDown() && player.getMotionDirection() == Direction.NORTH
                    && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ > blockZ + 0.25)) {
                if (this.marioverse$getWarpCooldown() == 0) {
                    this.marioverse$warp(pos, state, warpPos, warpBE);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                } /* else if (this.getWarpCooldown() <= 10)
                displayDestinationMissingMessage(); */
                else if (warpBE.hasDestinationPos())
                    this.marioverse$displayCooldownMessage(state);
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.EAST && !player.isShiftKeyDown() && player.getMotionDirection() == Direction.WEST
                    && (entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                if (this.marioverse$getWarpCooldown() == 0) {
                    this.marioverse$warp(pos, state, warpPos, warpBE);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                } /* else if (this.getWarpCooldown() <= 10)
                displayDestinationMissingMessage(); */
                else if (warpBE.hasDestinationPos())
                    this.marioverse$displayCooldownMessage(state);
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.WEST && !player.isShiftKeyDown() && player.getMotionDirection() == Direction.EAST
                    && (entityX < blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                if (this.marioverse$getWarpCooldown() == 0) {
                    this.marioverse$warp(pos, state, warpPos, warpBE);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                } /* else if (this.getWarpCooldown() <= 10)
                displayDestinationMissingMessage(); */
                else if (warpBE.hasDestinationPos())
                    this.marioverse$displayCooldownMessage(state);
            }
        }
    }

    @Unique
    public void marioverse$enterWarpPipeAbove(BlockPos pos, BlockPos warpPos, BaseWarpBlockEntity warpBE) {
        Player player = (Player) (Object) this;
        Level world = player.level();
        BlockState stateAboveEntity = world.getBlockState(pos.above(Math.round(player.getBbHeight())));

        double deltaY = player.getDeltaMovement().y;
        double entityX = this.getX();
        double entityZ = this.getZ();
        int blockX = pos.getX();
        int blockY = pos.getY();
        int blockZ = pos.getZ();

        if (!ConfigRegistry.TELEPORT_PLAYERS.get() || player.getType().is(TagRegistry.CANNOT_WARP)
                || player.getPersistentData().getBoolean("marioverse:prevent_warp")) {
            if (stateAboveEntity.getValue(WarpPipeBlock.FACING) == Direction.DOWN && (player.getBlockY() < blockY)
                    && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                this.marioverse$displayNoTeleportMessage();
            }
        } else {
            if (stateAboveEntity.getValue(WarpPipeBlock.FACING) == Direction.DOWN && deltaY > -0.079
                    && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                if (this.marioverse$getWarpCooldown() == 0) {
                    this.marioverse$warp(pos, stateAboveEntity, warpPos, warpBE);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                } /* else if (this.getWarpCooldown() <= 10)
                displayDestinationMissingMessage(); */
                else if (warpBE.hasDestinationPos())
                    this.marioverse$displayCooldownMessage(stateAboveEntity);
            }
        }
    }

    @Unique
    public void marioverse$enterWarpPainting(WarpLinkableEntity warpLinkableEntity) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (ConfigRegistry.TELEPORT_MOBS.get() && !entity.getType().is(TagRegistry.CANNOT_WARP)
                && !entity.getPersistentData().getBoolean("marioverse:prevent_warp")) {
            if (this.marioverse$getWarpCooldown() == 0 && !entity.isShiftKeyDown()) {
                this.marioverse$warp(warpLinkableEntity);
                this.marioverse$setWarpCooldown(ConfigRegistry.WARP_PAINTING_COOLDOWN.get());
            }
        }
    }

    @Unique
    public void marioverse$updateDoor(BlockPos pos, BlockState state, BlockPos warpPos, BlockState warpState) {
        Player player = (Player) (Object) this;
        Level world = player.level();
        BlockEntity blockEntity = world.getBlockEntity(pos);
        BlockEntity warpBE = world.getBlockEntity(warpPos);

        if (!world.isClientSide) {
            if (warpBE instanceof WarpDoorBlockEntity warpDoorBE && warpDoorBE.breakDoor)
                WarpDoorBlockEntity.breakDoor(warpPos, world);
            if (warpBE instanceof WarpTrapDoorBlockEntity warpTrapdoorBE && warpTrapdoorBE.breakTrapdoor)
                WarpTrapDoorBlockEntity.breakTrapdoor(warpPos, world);

            if (state.getBlock() instanceof DoorBlock)
                world.setBlock(pos, state.setValue(DoorBlock.OPEN, Boolean.FALSE)
                        .setValue(DoorBlock.FACING, state.getValue(DoorBlock.FACING)), 10);
            if (state.getBlock() instanceof TrapDoorBlock)
                world.setBlock(pos, state.setValue(TrapDoorBlock.OPEN, Boolean.FALSE)
                        .setValue(TrapDoorBlock.FACING, state.getValue(TrapDoorBlock.FACING)), 10);

            if (warpBE instanceof WarpDoorBlockEntity warpDoorBE && !warpDoorBE.breakDoor)
                world.setBlock(warpPos, warpState.setValue(DoorBlock.OPEN, Boolean.TRUE)
                        .setValue(DoorBlock.FACING, warpState.getValue(DoorBlock.FACING)), 10);
            if (warpBE instanceof WarpTrapDoorBlockEntity warpDoorBE && !warpDoorBE.breakTrapdoor)
                world.setBlock(warpPos, warpState.setValue(TrapDoorBlock.OPEN, Boolean.TRUE)
                        .setValue(TrapDoorBlock.FACING, warpState.getValue(TrapDoorBlock.FACING)), 10);
        }

        if (blockEntity instanceof BaseWarpBlockEntity warpDoorBE) {
            if (state.getBlock() instanceof DoorBlock doorBlock)
                warpDoorBE.playDoorSounds(null, world, pos, state.getValue(DoorBlock.OPEN), doorBlock.type());
            if (warpState.getBlock() instanceof DoorBlock doorBlock)
                warpDoorBE.playDoorSounds(null, world, warpPos, warpState.getValue(DoorBlock.OPEN), doorBlock.type());

            if (state.getBlock() instanceof TrapDoorBlock trapdoorBlock)
                warpDoorBE.playDoorSounds(null, world, pos, state.getValue(TrapDoorBlock.OPEN), trapdoorBlock.getType());
            if (warpState.getBlock() instanceof TrapDoorBlock trapdoorBlock)
                warpDoorBE.playDoorSounds(null, world, warpPos, warpState.getValue(TrapDoorBlock.OPEN), trapdoorBlock.getType());
        }
    }

    @Unique
    public void marioverse$displayCooldownMessage(BlockState state) {
        if (this.marioverse$getWarpCooldown() >= 10) {
            if (state.getBlock() instanceof WarpPipeBlock) {
                if (ConfigRegistry.WARP_COOLDOWN_MESSAGE.get()) {
                    if (ConfigRegistry.WARP_COOLDOWN_MESSAGE_TICKS.get())
                        this.displayClientMessage(Component.translatable("display.marioverse.warp_pipe_cooldown.ticks",
                                this.marioverse$getWarpCooldown()), true);
                    else this.displayClientMessage(Component.translatable("display.marioverse.warp_pipe_cooldown"), true);
                }
            } else if (state.getBlock() instanceof DoorBlock) {
                if (ConfigRegistry.WARP_COOLDOWN_MESSAGE.get()) {
                    if (ConfigRegistry.WARP_COOLDOWN_MESSAGE_TICKS.get())
                        this.displayClientMessage(Component.translatable("display.marioverse.warp_door_cooldown.ticks",
                                this.marioverse$getWarpCooldown()), true);
                    else this.displayClientMessage(Component.translatable("display.marioverse.warp_door_cooldown"), true);
                }
            } else if (state.getBlock() instanceof TrapDoorBlock) {
                if (ConfigRegistry.WARP_COOLDOWN_MESSAGE.get()) {
                    if (ConfigRegistry.WARP_COOLDOWN_MESSAGE_TICKS.get())
                        this.displayClientMessage(Component.translatable("display.marioverse.warp_trapdoor_cooldown.ticks",
                                this.marioverse$getWarpCooldown()), true);
                    else this.displayClientMessage(Component.translatable("display.marioverse.warp_trapdoor_cooldown"), true);
                }
            }
        }
    }

    @Unique
    public void marioverse$displayNoTeleportMessage() {
        if (!ConfigRegistry.TELEPORT_PLAYERS.get() || this.getType().is(TagRegistry.CANNOT_WARP)) {
            this.displayClientMessage(Component.translatable("display.marioverse.players_cannot_teleport"), true);
        }
    }

    @Unique
    public void marioverse$displayDestinationMissingMessage() {
        this.displayClientMessage(Component.translatable("display.marioverse.warp_destination_missing"), true);
    }
}
