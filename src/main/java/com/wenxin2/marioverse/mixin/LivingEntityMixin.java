package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.ItemRegistry;
import com.wenxin2.marioverse.init.ParticleRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import com.wenxin2.marioverse.items.OneUpMushroomItem;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow public abstract void tick();

    @Unique
    private static final int MAX_PARTICLE_AMOUNT = 100;
    @Unique
    private int marioverse$warpCooldown;

    public LivingEntityMixin(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

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

//        if (this.getPersistentData().contains("marioverse:has_mega_mushroom") && this.getPersistentData().getBoolean("marioverse:has_mega_mushroom")) {
//            ScaleTypes.WIDTH.getScaleData(this).setTargetScale(5.0F);
//            ScaleTypes.HEIGHT.getScaleData(this).setTargetScale(5.0F);
//            ScaleTypes.JUMP_HEIGHT.getScaleData(this).setTargetScale(20.0F);
//            ScaleTypes.STEP_HEIGHT.getScaleData(this).setTargetScale(5.0F);
//            ScaleTypes.REACH.getScaleData(this).setTargetScale(5.0F);
//            ScaleTypes.ATTACK.getScaleData(this).setTargetScale(5.0F);
//        }
    }

    @Inject(method = "checkTotemDeathProtection", at = @At("RETURN"), cancellable = true)
    private void checkTotemDeathProtection(DamageSource source, CallbackInfoReturnable<Boolean> info) {
        LivingEntity livingEntity = (LivingEntity)(Object)this;

        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return;
        } else {
            Optional<ICuriosItemHandler> curiosHandler = CuriosApi.getCuriosInventory(livingEntity);
            ItemStack stack = livingEntity.getOffhandItem();

            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack stackInHand = livingEntity.getItemInHand(hand);
                if (stackInHand.getItem() instanceof OneUpMushroomItem) {
                    stack = stackInHand.copy();
                    stackInHand.shrink(1);
                    break;
                }
            }

            if (curiosHandler.isPresent()) {
                ICuriosItemHandler handler = curiosHandler.get();
                Optional<SlotResult> charmSlot = handler.findCurio("charm", 0);
                if (charmSlot.isPresent()) {
                    ItemStack itemInSlot = charmSlot.get().stack();
                    if (itemInSlot.getItem() instanceof OneUpMushroomItem) {
                        info.setReturnValue(true);
                        this.level().playSound(null, livingEntity.blockPosition(), SoundRegistry.ONE_UP_COLLECTED.get(),
                                SoundSource.PLAYERS, 1.0F, 1.0F);
                        livingEntity.setHealth(1.0F);
                        livingEntity.heal(ConfigRegistry.ONE_UP_HEAL_AMT.get().floatValue());
                        itemInSlot.shrink(1);
                        this.level().broadcastEntityEvent(livingEntity, (byte) 125); // Mushroom Transform particle
                        this.level().broadcastEntityEvent(livingEntity, (byte) 126); // 1-Up Collected particle
                        this.level().broadcastEntityEvent(livingEntity, (byte) 127); // 1-Up Pop Up

                        if (livingEntity instanceof ServerPlayer serverplayer) {
                            serverplayer.awardStat(Stats.ITEM_USED.get(ItemRegistry.ONE_UP_MUSHROOM.get()), 1);
                            CriteriaTriggers.USED_TOTEM.trigger(serverplayer, stack);
                            this.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
                        }
                    }
                }
            }

            if (!stack.isEmpty() && stack.getItem() instanceof OneUpMushroomItem) {
                info.setReturnValue(true);
                this.level().playSound(null, livingEntity.blockPosition(), SoundRegistry.ONE_UP_COLLECTED.get(),
                        SoundSource.PLAYERS, 1.0F, 1.0F);
                livingEntity.setHealth(1.0F);
                livingEntity.heal(ConfigRegistry.ONE_UP_HEAL_AMT.get().floatValue());
                stack.shrink(1);
                this.level().broadcastEntityEvent(livingEntity, (byte) 125); // Mushroom Transform particle
                this.level().broadcastEntityEvent(livingEntity, (byte) 126); // 1-Up Collected particle
                this.level().broadcastEntityEvent(livingEntity, (byte) 127); // 1-Up Pop Up

                if (livingEntity instanceof ServerPlayer serverplayer) {
                    serverplayer.awardStat(Stats.ITEM_USED.get(ItemRegistry.ONE_UP_MUSHROOM.get()), 1);
                    CriteriaTriggers.USED_TOTEM.trigger(serverplayer, stack);
                    this.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
                }
            }
        }
    }

    @Inject(method = "handleEntityEvent", at = @At("HEAD"))
    private void handleEntityEvent(byte id, CallbackInfo info) {
        LivingEntity livingEntity = (LivingEntity)(Object)this;
        if (id == 125) {
            if (this.level().isClientSide) {
                ParticleUtils.spawnParticlesOnBlockFaces(this.level(), this.blockPosition(),
                        ParticleRegistry.MUSHROOM_TRANSFORM.get(), UniformInt.of(1, 3));
            }
        } else if (id == 126) {
            if (this.level().isClientSide) {
                this.level().addParticle(ParticleRegistry.ONE_UP.get(),
                        this.getX(), this.getY() + 2.0, this.getZ(),
                        0.0, 1.0, 0.0);
            }
        } else if (id == 127) {
            if (livingEntity instanceof Player player) {
                Minecraft.getInstance().gameRenderer.displayItemActivation(marioverse$find1Up(player));
            }
        } else super.handleEntityEvent(id);
    }

    @Unique
    private static ItemStack marioverse$find1Up(Player player) {
        for (InteractionHand interactionhand : InteractionHand.values()) {
            ItemStack stack = player.getItemInHand(interactionhand);
            if (stack.getItem() instanceof OneUpMushroomItem) {
                return stack;
            }
        }

        return new ItemStack(ItemRegistry.ONE_UP_MUSHROOM.get());
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
                && !warpPipeBE.preventWarp && this.getType() != EntityType.PLAYER && ConfigRegistry.TELEPORT_PLAYERS.get() && !this.getType().is(TagRegistry.WARP_BLACKLIST)
                && !this.getPersistentData().getBoolean("marioverse:prevent_warp")) {
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
                        WarpPipeBlock.warp(this, warpPos, world, stateAboveEntity);
                    else if (warpPipeBE.getUuid() != null && warpPipeBE.getWarpUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, stateAboveEntity);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_COOLDOWN.get());
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

        if (!state.getValue(WarpPipeBlock.CLOSED) && blockEntity instanceof WarpPipeBlockEntity warpPipeBE && this.getType() != EntityType.PLAYER
                && !warpPipeBE.preventWarp && ConfigRegistry.TELEPORT_MOBS.get() && !this.getType().is(TagRegistry.WARP_BLACKLIST)
                && !this.getPersistentData().getBoolean("marioverse:prevent_warp")) {
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
                        WarpPipeBlock.warp(this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && warpPipeBE.getWarpUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_COOLDOWN.get());
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.NORTH
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ)) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp(this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && warpPipeBE.getWarpUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_COOLDOWN.get());
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.SOUTH
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ > blockZ)) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp(this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && warpPipeBE.getWarpUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_COOLDOWN.get());
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.EAST
                        && (entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp(this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && warpPipeBE.getWarpUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_COOLDOWN.get());
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.WEST
                        && (entityX < blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp(this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && warpPipeBE.getWarpUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_COOLDOWN.get());
                }
            }
        }
    }
}
