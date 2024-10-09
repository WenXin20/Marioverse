package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.entities.projectiles.BouncingFireballProjectile;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.DamageSourceRegistry;
import com.wenxin2.marioverse.init.EntityRegistry;
import com.wenxin2.marioverse.init.ItemRegistry;
import com.wenxin2.marioverse.init.ParticleRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import com.wenxin2.marioverse.items.OneUpMushroomItem;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.data.SlotTypeLoader;
import java.util.Collection;
import java.util.List;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow public abstract void tick();

    @Shadow public abstract float getSpeed();

    @Unique
    private static final int MAX_PARTICLE_AMOUNT = 100;
    @Unique
    private static final int FIREBALL_COOLDOWN = 5;
    @Unique
    private int marioverse$warpCooldown;
    @Unique
    private int marioverse$consecutiveBounces;
    @Unique
    private int marioverse$oneUpsRewarded;

    public LivingEntityMixin(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(at = @At("TAIL"), method = "baseTick")
    public void baseTick(CallbackInfo ci) {
        Level world = this.level();
        BlockPos pos = this.blockPosition();
        BlockState state = world.getBlockState(pos);
        BlockState stateAboveEntity = world.getBlockState(pos.above(Math.round(this.getBbHeight())));
        LivingEntity livingEntity = (LivingEntity) (Object) this;

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

        if (ConfigRegistry.ENABLE_STOMPABLE_ENEMIES.get())
            this.marioverse$squashEntity(livingEntity);

        if (ConfigRegistry.ENABLE_STOMPABLE_ENEMIES.get() && livingEntity.onGround()) {
            marioverse$consecutiveBounces = 0;
            marioverse$oneUpsRewarded = 0;
        }

        if (stateAboveEntity.getBlock() instanceof WarpPipeBlock) {
            this.marioverse$enterPipeBelow(pos);
        }

        if (this.marioverse$warpCooldown > 0) {
            --this.marioverse$warpCooldown;
        }

        if (livingEntity.getPersistentData().getBoolean("marioverse:has_fire_flower")
                && (livingEntity.getType().is(TagRegistry.CAN_CONSUME_FIRE_FLOWERS)
                    || ConfigRegistry.FIRE_FLOWER_POWERS_ALL_MOBS.get())
                && !(livingEntity instanceof Player) && !(livingEntity instanceof ArmorStand)
                && (this.getDeltaMovement().x > 0.0F || this.getDeltaMovement().z > 0.0F)) {
            if (livingEntity instanceof Monster monster && monster.getTarget() != null)
                this.marioverse$handleFireballShooting(livingEntity);
            else if (livingEntity instanceof AbstractGolem golem && golem.getTarget() != null)
                this.marioverse$handleFireballShooting(livingEntity);
            else if (!(livingEntity instanceof Monster) && !(livingEntity instanceof AbstractGolem))
                this.marioverse$handleFireballShooting(livingEntity);
        }

        int fireballCooldown = this.getPersistentData().getInt("marioverse:fireball_cooldown");
        if (fireballCooldown > 0) {
            this.getPersistentData().putInt("marioverse:fireball_cooldown", fireballCooldown - 1);
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
            ItemStack stack = livingEntity.getOffhandItem();

            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack stackInHand = livingEntity.getItemInHand(hand);
                if (stackInHand.getItem() instanceof OneUpMushroomItem) {
                    stack = stackInHand.copy();
                    stackInHand.shrink(1);
                    break;
                }
            }

            AccessoriesCapability capability = AccessoriesCapability.get(livingEntity);
            if (capability != null) {
                AccessoriesContainer containerCharm = capability.getContainer(SlotTypeLoader.getSlotType(livingEntity, "charm"));
                if (containerCharm != null) {
                    ItemStack stackCharm = containerCharm.getAccessories().getItem(0);
                    if (stackCharm.getItem() instanceof OneUpMushroomItem) {
                        info.setReturnValue(true);
                        this.level().playSound(null, livingEntity.blockPosition(), SoundRegistry.ONE_UP_COLLECTED.get(),
                                SoundSource.PLAYERS, 1.0F, 1.0F);
                        livingEntity.setHealth(1.0F);
                        livingEntity.heal(ConfigRegistry.ONE_UP_HEAL_AMT.get().floatValue());
                        stackCharm.shrink(1);
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
        if (id == 124) {
            if (this.level().isClientSide) {
                ParticleUtils.spawnParticlesOnBlockFaces(this.level(), this.blockPosition().above(Math.round(this.getBbHeight())).above(),
                        ParticleRegistry.COIN_GLINT.get(), UniformInt.of(1, 1));
            }
        } else if (id == 125) {
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
        } else super.handleEntityEvent(id);
    }

    @Unique
    public void marioverse$squashEntity(LivingEntity stompingEntity) {
        List<Entity> nearbyEntities = stompingEntity.level().getEntities(stompingEntity, stompingEntity.getBoundingBox().inflate(0.2));

        for (Entity entity : nearbyEntities) {
            if (entity instanceof LivingEntity damagedEntity && damagedEntity != stompingEntity
                    && (stompingEntity.getType().is(TagRegistry.CAN_STOMP_ENEMIES) || ConfigRegistry.ALL_MOBS_CAN_STOMP.get())
                    && (damagedEntity.getType().is(TagRegistry.CAN_BE_STOMPED) || ConfigRegistry.STOMP_ALL_MOBS.get())
                    && !damagedEntity.getType().is(TagRegistry.POWER_UP_ENTITIES) && !damagedEntity.isVehicle() ) {

                // Check if the colliding entity is above the current entity and falling
                if (stompingEntity.getY() >= damagedEntity.getY() + damagedEntity.getEyeHeight()
                        && stompingEntity.fallDistance > 0) {
                    double bounceBlockHeight = ConfigRegistry.STOMP_BOUNCE_HEIGHT.getAsDouble();
                    if (stompingEntity instanceof Player)
                        if (Minecraft.getInstance().options.keyJump.isDown())
                            bounceBlockHeight = ConfigRegistry.STOMP_BOUNCE_HEIGHT_JUMP.getAsDouble();
                    double gravity = 0.08; // Approximate Minecraft gravity value
                    double bounceVelocity = Math.sqrt(2 * gravity * bounceBlockHeight);

                    stompingEntity.setDeltaMovement(stompingEntity.getDeltaMovement().x, bounceVelocity, stompingEntity.getDeltaMovement().z);
                    stompingEntity.fallDistance = 0; // Reset fall damage

                    float scaleFactor = this.getBbHeight() * this.getBbWidth();
                    int numParticles = (int) (scaleFactor * 20);
                    double radius = damagedEntity.getBbWidth() / 2;
                    double height = damagedEntity.getBbHeight() / 2;

                    for (int i = 0; i < numParticles; i++) {
                        // Calculate angle for each particle
                        double angle = 2 * Math.PI * i / numParticles;
                        // Calculate the X and Z offset using sine and cosine to spread in an ellipse
                        double offsetX = Math.cos(angle) * radius;
                        double offsetY = damagedEntity.getBbHeight();
                        double offsetZ = Math.sin(angle) * radius;

                        double x = damagedEntity.getX() + offsetX;
                        double y = damagedEntity.getY() + offsetY;
                        double z = damagedEntity.getZ() + offsetZ;

                        this.level().addParticle(ParticleTypes.CRIT, x, y, z, 0, 1.0, 0);
                    }

                    if (!stompingEntity.level().isClientSide() && damagedEntity.isAlive()) {
                        damagedEntity.hurt(DamageSourceRegistry.stomp(damagedEntity, stompingEntity), ConfigRegistry.STOMP_DAMAGE.get().floatValue());
                        if (!ConfigRegistry.DISABLE_CONSECUTIVE_BOUNCING.get())
                            this.marioverse$consecutiveBounces(stompingEntity, damagedEntity);
                    }
                }
            }
        }
    }

    @Unique
    public void marioverse$consecutiveBounces(LivingEntity stompingEntity, LivingEntity damagedEntity) {
        marioverse$consecutiveBounces++;

        if (marioverse$consecutiveBounces == 1) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get())
                this.marioverse$rewardParticles(damagedEntity, ParticleRegistry.GOOD.get());
        }
        else if (marioverse$consecutiveBounces == 2) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get())
                this.marioverse$rewardParticles(damagedEntity, ParticleRegistry.GREAT.get());
        }
        else if (marioverse$consecutiveBounces == 3) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get())
                this.marioverse$rewardParticles(damagedEntity, ParticleRegistry.SUPER.get());
        }
        else if (marioverse$consecutiveBounces == 4) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get())
                this.marioverse$rewardParticles(damagedEntity, ParticleRegistry.FANTASTIC.get());
        }
        else if (marioverse$consecutiveBounces == 5) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get())
                this.marioverse$rewardParticles(damagedEntity, ParticleRegistry.EXCELLENT.get());
        }
        else if (marioverse$consecutiveBounces == 6) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get())
                this.marioverse$rewardParticles(damagedEntity, ParticleRegistry.INCREDIBLE.get());
        }
        else if (marioverse$consecutiveBounces == 7) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get())
                this.marioverse$rewardParticles(damagedEntity, ParticleRegistry.WONDERFUL.get());
        }
        else if (marioverse$consecutiveBounces >= 8 && ConfigRegistry.MAX_ONE_UP_BOUNCE_REWARD.get() > marioverse$oneUpsRewarded) {
            marioverse$oneUpsRewarded++;
            this.marioverse$bounceReward(stompingEntity);
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get())
                this.marioverse$rewardParticles(damagedEntity, ParticleRegistry.ONE_UP.get());
        }
    }

    @Unique
    public void marioverse$rewardParticles(LivingEntity entity, ParticleOptions particleType) {
        if (entity.level() instanceof ServerLevel serverWorld)
            serverWorld.sendParticles(particleType, entity.getX(),
                    entity.getY() + entity.getBbHeight() + 1.0,
                    entity.getZ(), 1, 0, 1.0, 0, 0.5);
    }

    @Unique
    public void marioverse$bounceReward(LivingEntity entity) {
        ItemLike item = ItemRegistry.ONE_UP_MUSHROOM;
        if (entity instanceof Player player && !player.isSpectator()) {
            AccessoriesCapability capability = AccessoriesCapability.get(player);
            ItemStack offhandStack = player.getOffhandItem();

            if (capability != null && !capability.isEquipped(ItemRegistry.ONE_UP_MUSHROOM.get()))
                capability.attemptToEquipAccessory(new ItemStack(ItemRegistry.ONE_UP_MUSHROOM.get()));
            else if (offhandStack.isEmpty())
                player.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(item));
            else if (offhandStack.getCount() >= 1)
                player.addItem(new ItemStack(ItemRegistry.ONE_UP_MUSHROOM.get()));
            this.level().playSound(null, this.blockPosition(), SoundRegistry.ONE_UP_COLLECTED.get(),
                    SoundSource.PLAYERS, 1.0F, 1.0F);

        } else if (entity instanceof LivingEntity livingEntity && ConfigRegistry.ONE_UP_HEALS_ALL_MOBS.get()) {
            AccessoriesCapability capability = AccessoriesCapability.get(livingEntity);
            ItemStack offhandStack = livingEntity.getOffhandItem();

            if (capability != null && !capability.isEquipped(ItemRegistry.ONE_UP_MUSHROOM.get()))
                capability.attemptToEquipAccessory(new ItemStack(ItemRegistry.ONE_UP_MUSHROOM.get()));
            else if (offhandStack.isEmpty())
                livingEntity.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(item));
            else if (offhandStack.getItem() instanceof OneUpMushroomItem)
                offhandStack.grow(1);
            this.level().playSound(null, this.blockPosition(), SoundRegistry.ONE_UP_COLLECTED.get(),
                    SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    @Unique
    public void marioverse$enchantParticles(Level world) {
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
    public void marioverse$handleFireballShooting(LivingEntity entity) {
        int fireballCount = entity.getPersistentData().getInt("marioverse:fireball_count");
        int fireballCooldown = entity.getPersistentData().getInt("marioverse:fireball_cooldown");

        // Check if the player can shoot a fireball
        if (fireballCooldown == 0 && fireballCount < ConfigRegistry.MAX_FIREBALLS.get()) {
            this.marioverse$shootFireball(entity);
            entity.getPersistentData().putInt("marioverse:fireball_cooldown", FIREBALL_COOLDOWN); // Reset cooldown
            entity.getPersistentData().putInt("marioverse:fireball_count", fireballCount + 1); // Increase active fireball count
        } else if (fireballCount >= ConfigRegistry.MAX_FIREBALLS.get()) {
            entity.getPersistentData().putInt("marioverse:fireball_cooldown", ConfigRegistry.FIREBALL_COOLDOWN.get()); // Reset with higher cooldown
            entity.getPersistentData().putInt("marioverse:fireball_count", 0);
        }
    }

    @Unique
    public void marioverse$shootFireball(LivingEntity entity) {
        Level world = entity.level();

        BouncingFireballProjectile fireball = new BouncingFireballProjectile(EntityRegistry.BOUNCING_FIREBALL.get(), world);
        fireball.setOwner(entity);
        fireball.setPos(entity.getX(), entity.getEyeY() - 0.5, entity.getZ());
        fireball.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0.0F, 1.2F, 1.0F);
        world.playSound(null, entity.blockPosition(), SoundRegistry.FIREBALL_THROWN.get(),
                SoundSource.PLAYERS, 1.0F, 1.0F);

        Vec3 look = entity.getLookAngle();
        fireball.setDeltaMovement(look.scale(0.5));

        // Set the fireball's rotation based on the look direction
        fireball.setYRot((float) Math.toDegrees(Math.atan2(look.z, look.x)) + 90); // Adjust for correct facing
        fireball.setXRot((float) Math.toDegrees(Math.atan2(look.y, Math.sqrt(look.x * look.x + look.z * look.z))));

        world.addFreshEntity(fireball);
        entity.swing(InteractionHand.MAIN_HAND);
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
                && !warpPipeBE.preventWarp && this.getType() != EntityType.PLAYER && ConfigRegistry.TELEPORT_PLAYERS.get() && !this.getType().is(TagRegistry.CANNOT_WARP)
                && !this.getPersistentData().getBoolean("marioverse:prevent_warp")) {
            warpPos = warpPipeBE.destinationPos;
            int entityId = this.getId();

            if (!world.isClientSide() && WarpPipeBlock.teleportedEntities.getOrDefault(entityId, false)) {
                this.marioverse$enchantParticles(world);

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
                && !warpPipeBE.preventWarp && ConfigRegistry.TELEPORT_MOBS.get() && !this.getType().is(TagRegistry.CANNOT_WARP)
                && !this.getPersistentData().getBoolean("marioverse:prevent_warp")) {
            warpPos = warpPipeBE.destinationPos;
            int entityId = this.getId();

            if (!world.isClientSide() && WarpPipeBlock.teleportedEntities.getOrDefault(entityId, false)) {
                this.marioverse$enchantParticles(world);

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
