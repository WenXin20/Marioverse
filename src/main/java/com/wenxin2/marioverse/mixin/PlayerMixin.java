package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.blocks.CoinBlock;
import com.wenxin2.marioverse.blocks.InvisibleQuestionBlock;
import com.wenxin2.marioverse.blocks.QuestionBlock;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpDoorBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpTrapDoorBlockEntity;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import com.wenxin2.marioverse.items.BasePowerUpItem;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorStandItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class PlayerMixin extends Entity {
    @Shadow protected abstract float getBlockSpeedFactor();

    @Shadow public abstract void displayClientMessage(Component component, boolean isAboveHotbar);

    @Shadow public abstract Inventory getInventory();

    @Shadow @Nullable public abstract ItemEntity drop(ItemStack p_36177_, boolean p_36178_);

    @Unique
    private static final int MAX_PARTICLE_AMOUNT = 40;
    @Unique
    private int marioverse$warpCooldown;

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

        if (this.marioverse$warpCooldown > 0)
            --this.marioverse$warpCooldown;

        int preventWarpCooldown = this.getPersistentData().getInt("marioverse:prevent_warp_cooldown");
        if (preventWarpCooldown > 0)
            this.getPersistentData().putInt("marioverse:prevent_warp_cooldown", preventWarpCooldown - 1);
        if (preventWarpCooldown == 0 && this.getPersistentData().getBoolean("marioverse:prevent_warp"))
            this.getPersistentData().putBoolean("marioverse:prevent_warp", false);

        if (world.getBlockEntity(posAboveEntity) instanceof QuestionBlockEntity questionBlockEntity
                && this.getDeltaMovement().y > 0)
            this.marioverse$hitQuestionBlock(world, posAboveEntity, questionBlockEntity);

        super.baseTick();
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
    public void marioverse$dropCoin(Level world, BlockPos pos, Entity entity) {
        if (world.getBlockState(pos.above()).getBlock() instanceof CoinBlock) {
            ItemStack coinItem = new ItemStack(world.getBlockState(pos.above()).getBlock());

            this.level().broadcastEntityEvent(entity, (byte) 125); // Coin Glint particle
            world.playSound(null, pos.above(), SoundRegistry.COIN_PICKUP.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            world.removeBlock(pos.above(), false);
            this.getInventory().add(coinItem);

            if (!this.getInventory().add(coinItem)) {
                this.drop(coinItem, false);
            }
        }
    }

    @Unique
    public void marioverse$hitQuestionBlock(Level world, BlockPos pos, QuestionBlockEntity questionBlockEntity) {
        if (world.getBlockState(pos).getBlock() instanceof QuestionBlock questionBlock) {
            ItemStack storedItem = questionBlockEntity.getItems().getFirst();

            if (questionBlockEntity.getLootTable() != null)
                questionBlock.unpackLootTable(this, questionBlockEntity);

            if (!storedItem.isEmpty() && !world.getBlockState(pos).getValue(QuestionBlock.EMPTY)) {
                this.marioverse$dropCoin(world, pos, this);

                if (storedItem.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CoinBlock)
                    questionBlock.playCoinSound(world, pos);
                else if (storedItem.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof TntBlock)
                    questionBlock.playPrimedTNTSound(world, pos);
                else if (storedItem.getItem() instanceof BasePowerUpItem)
                    questionBlock.playPowerUpSound(world, pos);
                else if (storedItem.getItem() instanceof SpawnEggItem)
                    questionBlock.playMobSound(world, pos);
                else if (storedItem.getItem() instanceof ArmorStandItem)
                    questionBlock.playArmorStandSound(world, pos);
                else if (storedItem.getItem() instanceof BoatItem)
                    questionBlock.playBoatSound(world, pos);
                else if (storedItem.getItem() instanceof MinecartItem)
                    questionBlock.playMinecartSound(world, pos);
                else questionBlock.playItemSound(world, pos);

                if (!world.isClientSide)
                    questionBlock.spawnEntity(world, pos, storedItem);

                questionBlockEntity.removeItems();
                questionBlockEntity.setChanged();
            }

            if (storedItem.isEmpty() && !world.getBlockState(pos).getValue(QuestionBlock.EMPTY)) {
                BlockState currentState = world.getBlockState(pos);
                if (currentState.getBlock() instanceof QuestionBlock)
                    world.setBlock(pos, currentState.setValue(QuestionBlock.EMPTY, Boolean.TRUE), 3);
                world.gameEvent(this, GameEvent.BLOCK_CHANGE, pos);
            }

            if (world.getBlockState(pos).getBlock() instanceof InvisibleQuestionBlock && world.getBlockState(pos).getValue(InvisibleQuestionBlock.INVISIBLE)) {
                BlockState currentState = world.getBlockState(pos);
                world.setBlock(pos, currentState.setValue(InvisibleQuestionBlock.INVISIBLE, Boolean.FALSE), 3);
                world.gameEvent(this, GameEvent.BLOCK_CHANGE, pos);
            }

            if (!world.getBlockState(pos).getValue(QuestionBlock.EMPTY)) {
                AABB boundingBox = new AABB(pos.above()).inflate(0.5);
                List<Entity> entitiesAbove = world.getEntities(null, boundingBox);

                for (Entity entity : entitiesAbove) {
                    if (entity instanceof LivingEntity livingEntity) {
                        livingEntity.hurt(world.damageSources().generic(), 4.0F);
                    }
                }
            }
        }
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

            if (world.isClientSide() && BaseWarpBlockEntity.teleportedEntities.getOrDefault(entityId, false)) {
                // Reset the teleport status for the entity
                BaseWarpBlockEntity.teleportedEntities.put(entityId, false);
                world.broadcastEntityEvent(this, (byte) 120);
            }

            if (state.getBlock() instanceof DoorBlock || state.getBlock() instanceof TrapDoorBlock)
                this.marioverse$enterWarpDoor(pos, warpPos, warpBE);

            if (state.getBlock() instanceof WarpPipeBlock)
                this.marioverse$enterWarpPipe(pos, warpPos, warpBE);
        }

        if (blockEntityAbove instanceof BaseWarpBlockEntity warpBE && warpBE.getLevel() != null
                && !warpBE.preventWarp) {
            warpPos = warpBE.destinationPos;
            int entityId = this.getId();

            if (world.isClientSide() && BaseWarpBlockEntity.teleportedEntities.getOrDefault(entityId, false)) {
                BaseWarpBlockEntity.teleportedEntities.put(entityId, false);
                world.broadcastEntityEvent(this, (byte) 120);
            }

            if (stateAboveEntity.getBlock() instanceof WarpPipeBlock)
                this.marioverse$enterWarpPipeAbove(pos, warpPos, warpBE);
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
        } else if (warpBE.getUuid() != null && warpBE.getWarpUuid() != null
                && BaseWarpBlockEntity.findMatchingUUID(warpBE.getUuid(), world, pos) != null) {
            warpPos = BaseWarpBlockEntity.findMatchingUUID(warpBE.getUuid(), world, pos);
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
                } /* else if (this.getWarpCooldown() <= 10)
                displayDestinationMissingMessage(); */
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
            if (stateAboveEntity.getValue(WarpPipeBlock.FACING) == Direction.DOWN && player.getDeltaMovement().y > 0
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
