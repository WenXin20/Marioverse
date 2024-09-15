package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.blocks.CoinBlock;
import com.wenxin2.marioverse.blocks.InvisibleQuestionBlock;
import com.wenxin2.marioverse.blocks.QuestionBlock;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import com.wenxin2.marioverse.items.BasePowerUpItem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorStandItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
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
    private int marioverse$warpCooldown;

    public PlayerMixin(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void baseTick() {
        Level world = this.level();
        BlockPos pos = this.blockPosition();
        BlockPos posAboveEntity = pos.above(Math.round(this.getBbHeight()));
        BlockState state = world.getBlockState(pos);
        BlockState stateAboveEntity = world.getBlockState(posAboveEntity);

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

        if (stateAboveEntity.is(TagRegistry.SMASHABLE_BLOCKS) && this.getDeltaMovement().y > 0)
        {
            world.destroyBlock(posAboveEntity, false);
            world.gameEvent(this, GameEvent.BLOCK_CHANGE, posAboveEntity);
            world.playSound(null, posAboveEntity, SoundRegistry.BLOCK_SMASH.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        if (world.getBlockEntity(posAboveEntity) instanceof QuestionBlockEntity questionBlockEntity
                && this.getDeltaMovement().y > 0)
            this.marioverse$hitQuestionBlock(world, posAboveEntity, questionBlockEntity);

        if (stateAboveEntity.is(TagRegistry.BONKABLE_BLOCKS) && this.getDeltaMovement().y > 0)
            if (stateAboveEntity.getValue(QuestionBlock.EMPTY))
                world.playSound(null, pos, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            else if (!(stateAboveEntity.getBlock() instanceof QuestionBlock) && !(stateAboveEntity.getBlock() instanceof InvisibleQuestionBlock))
                world.playSound(null, pos, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

        super.baseTick();
    }

    @Unique
    public void marioverse$spawnParticles(Entity entity, Level world) {
        RandomSource random = world.getRandom();
        for(int i = 0; i < MAX_PARTICLE_AMOUNT; ++i) {
            world.addParticle(ParticleTypes.ENCHANT,
                    entity.getRandomX(0.5D), entity.getRandomY(), entity.getRandomZ(0.5D),
                    (random.nextDouble() - 0.5D) * 2.0D, -random.nextDouble(),
                    (random.nextDouble() - 0.5D) * 2.0D);
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
    public void marioverse$hitQuestionBlock(Level world, BlockPos pos, QuestionBlockEntity questionBlockEntity) {
        if (world.getBlockState(pos).getBlock() instanceof QuestionBlock questionBlock) {

            if (questionBlockEntity.getLootTable() != null)
                questionBlock.unpackLootTable(this, questionBlockEntity);

            ItemStack storedItem = questionBlockEntity.getItems().getFirst();
            if (!storedItem.isEmpty() && !world.getBlockState(pos).getValue(QuestionBlock.EMPTY)) {

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
                if (currentState.getBlock() instanceof InvisibleQuestionBlock)
                    world.setBlock(pos, currentState.setValue(InvisibleQuestionBlock.INVISIBLE, Boolean.FALSE), 3);
                world.gameEvent(this, GameEvent.BLOCK_CHANGE, pos);
            }

            if (!world.getBlockState(pos).getValue(QuestionBlock.EMPTY)) {
                AABB boundingBox = new AABB(pos.above()).inflate(0.5);
                List<Entity> entitiesAbove = world.getEntities(null, boundingBox);

                for (Entity entity : entitiesAbove) {
                    if (entity instanceof LivingEntity livingEntity) {
                        livingEntity.hurt(world.damageSources().generic(), 2.0F);
                    }
                }

            }
        }
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
                && !warpPipeBE.preventWarp && ConfigRegistry.TELEPORT_PLAYERS.get() && !this.getType().is(TagRegistry.WARP_BLACKLIST)
                && !this.getPersistentData().getBoolean("marioverse:prevent_warp")) {
            warpPos = warpPipeBE.destinationPos;
            int entityId = this.getId();

            if (world.isClientSide() && WarpPipeBlock.teleportedEntities.getOrDefault(entityId, false)) {
                this.marioverse$spawnParticles(this, world);

                // Reset the teleport status for the entity
                WarpPipeBlock.teleportedEntities.put(entityId, false);
            }

            if (stateAboveEntity.getValue(WarpPipeBlock.FACING) == Direction.DOWN && this.getDeltaMovement().y > 0
                    && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                if (this.marioverse$getWarpCooldown() == 0) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp(this, warpPos, world, stateAboveEntity);
                    else if (warpPipeBE.getUuid() != null && warpPipeBE.getWarpUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, stateAboveEntity);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_COOLDOWN.get());
                } /* else if (this.getWarpCooldown() <= 10)
                displayDestinationMissingMessage(); */ else if (warpPipeBE.hasDestinationPos()) this.marioverse$displayCooldownMessage();
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

        if (!state.getValue(WarpPipeBlock.CLOSED) && blockEntity instanceof WarpPipeBlockEntity warpPipeBE && warpPipeBE.getLevel() != null
                && !warpPipeBE.preventWarp && ConfigRegistry.TELEPORT_PLAYERS.get() && !this.getType().is(TagRegistry.WARP_BLACKLIST)
                && !this.getPersistentData().getBoolean("marioverse:prevent_warp")) {
//            WarpData warpData = WarpProxy.getInstance().getWarp(warpPipeBE.warpUuid);
            warpPos = warpPipeBE.destinationPos;
            int entityId = this.getId();

            if (world.isClientSide() && WarpPipeBlock.teleportedEntities.getOrDefault(entityId, false)) {
                this.marioverse$spawnParticles(this, world);

                // Reset the teleport status for the entity
                WarpPipeBlock.teleportedEntities.put(entityId, false);
            }

            if (state.getValue(WarpPipeBlock.FACING) == Direction.UP && this.isShiftKeyDown() && (entityY + this.getBbHeight() >= blockY - 1)
                    && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                if (this.marioverse$getWarpCooldown() == 0) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp(this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && warpPipeBE.getWarpUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_COOLDOWN.get());
                } /* else if (this.getWarpCooldown() <= 10)
                displayDestinationMissingMessage(); */ else if (warpPipeBE.hasDestinationPos()) this.marioverse$displayCooldownMessage();
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.NORTH && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.SOUTH
                    && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ)) {
                if (this.marioverse$getWarpCooldown() == 0) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp(this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && warpPipeBE.getWarpUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_COOLDOWN.get());
                } /* else if (this.getWarpCooldown() <= 10)
                displayDestinationMissingMessage(); */ else if (warpPipeBE.hasDestinationPos()) this.marioverse$displayCooldownMessage();
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.SOUTH && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.NORTH
                    && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ > blockZ + 0.25)) {
                if (this.marioverse$getWarpCooldown() == 0) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp(this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && warpPipeBE.getWarpUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_COOLDOWN.get());
                } /* else if (this.getWarpCooldown() <= 10)
                displayDestinationMissingMessage(); */ else if (warpPipeBE.hasDestinationPos()) this.marioverse$displayCooldownMessage();
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.EAST && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.WEST
                    && (entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                if (this.marioverse$getWarpCooldown() == 0) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp(this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && warpPipeBE.getWarpUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_COOLDOWN.get());
                } /* else if (this.getWarpCooldown() <= 10)
                displayDestinationMissingMessage(); */ else if (warpPipeBE.hasDestinationPos()) this.marioverse$displayCooldownMessage();
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.WEST && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.EAST
                    && (entityX < blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                if (this.marioverse$getWarpCooldown() == 0) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp(this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && warpPipeBE.getWarpUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_COOLDOWN.get());
                } /* else if (this.getWarpCooldown() <= 10)
                displayDestinationMissingMessage(); */ else if (warpPipeBE.hasDestinationPos()) this.marioverse$displayCooldownMessage();
            }
        } else if (!state.getValue(WarpPipeBlock.CLOSED) && (!ConfigRegistry.TELEPORT_PLAYERS.get() || this.getType().is(TagRegistry.WARP_BLACKLIST))) {
            if (state.getValue(WarpPipeBlock.FACING) == Direction.UP && this.isShiftKeyDown() && (entityY + this.getBbHeight() >= blockY - 1)
                    && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                this.marioverse$displayNoTeleportMessage();
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.DOWN && (this.getBlockY() < blockY)
                    && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                this.marioverse$displayNoTeleportMessage();
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.NORTH && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.SOUTH
                    && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ)) {
                this.marioverse$displayNoTeleportMessage();
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.SOUTH && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.NORTH
                    && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ > blockZ + 0.25)) {
                this.marioverse$displayNoTeleportMessage();
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.EAST && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.WEST
                    && (entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                this.marioverse$displayNoTeleportMessage();
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.WEST && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.EAST
                    && (entityX < blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                this.marioverse$displayNoTeleportMessage();
            }
        }

    }

    @Unique

    public void marioverse$displayCooldownMessage() {
        if (this.marioverse$getWarpCooldown() >= 10) {
            if (ConfigRegistry.WARP_COOLDOWN_MESSAGE.get()) {
                if (ConfigRegistry.WARP_COOLDOWN_MESSAGE_TICKS.get())
                    this.displayClientMessage(Component.translatable("display.marioverse.warp_cooldown.ticks",
                            this.marioverse$getWarpCooldown()).withStyle(ChatFormatting.RED), true);
                else this.displayClientMessage(Component.translatable("display.marioverse.warp_cooldown")
                        .withStyle(ChatFormatting.RED), true);
            }
        }
    }

    @Unique
    public void marioverse$displayNoTeleportMessage() {
        if (!ConfigRegistry.TELEPORT_PLAYERS.get() || this.getType().is(TagRegistry.WARP_BLACKLIST)) {
            this.displayClientMessage(Component.translatable("display.marioverse.players_cannot_teleport")
                    .withStyle(ChatFormatting.RED), true);
        }
    }

    @Unique
    public void marioverse$displayDestinationMissingMessage() {
        this.displayClientMessage(Component.translatable("display.marioverse.warp_destination_missing")
                .withStyle(ChatFormatting.RED), true);
    }
}
