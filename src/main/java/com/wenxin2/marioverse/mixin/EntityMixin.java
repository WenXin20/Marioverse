package com.wenxin2.marioverse.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpDoorBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpTrapDoorBlockEntity;
import com.wenxin2.marioverse.entities.IceCubeEntity;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract Level level();
    @Shadow public abstract double getX();
    @Shadow public abstract double getY();
    @Shadow public abstract double getZ();
    @Shadow public abstract float getBbHeight();
    @Shadow public abstract int getId();
    @Shadow public abstract BlockPos blockPosition();
    @Shadow public abstract EntityType<?> getType();
    @Shadow public abstract void setPos(Vec3 vec3);

    @Inject(at = @At("TAIL"), method = "tick")
    public void tick(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        Level world = entity.level();
        BlockPos pos = entity.blockPosition();
        BlockPos posBelow = entity.blockPosition().below();
        BlockPos posAboveEntity = pos.above(Math.round(entity.getBbHeight()));
        BlockPos posBelowEntity = BlockPos.containing(entity.position().x, entity.position().y - 0.3, entity.position().z);
        BlockPos posInBlock = pos.above(Math.round(entity.getBbHeight()) - 1);
        BlockState state = world.getBlockState(pos);
        BlockState stateAboveEntity = world.getBlockState(posAboveEntity);
        BlockState stateInBlock = world.getBlockState(posInBlock);
        BlockState stateBelowEntity = world.getBlockState(posBelowEntity);
        BlockState stateBelow = world.getBlockState(posBelow);

        int bounceCooldown = entity.getPersistentData().getInt("marioverse:bounce_cooldown");
        int warpCooldown = entity.getPersistentData().getInt("marioverse:warp_cooldown");

        if (bounceCooldown > 0)
            entity.getPersistentData().putInt("marioverse:bounce_cooldown", bounceCooldown - 1);
        if (warpCooldown > 0)
            entity.getPersistentData().putInt("marioverse:warp_cooldown", warpCooldown - 1);

        if (stateBelowEntity.is(TagRegistry.BOUNCY_BLOCKS)
                && !entity.getType().is(TagRegistry.CANNOT_BOUNCE_ON_BLOCKS)
                && !entity.isSuppressingBounce() && !entity.isNoGravity()) {
            marioverse$bounceEntity(entity, bounceCooldown, world);
        }

        marioverse$rideIceCube(entity);

        for (Direction facing : Direction.values()) {
            BlockPos offsetPos = pos.relative(facing);
            BlockState offsetState = world.getBlockState(offsetPos);

            if (!entity.getPersistentData().getBoolean("marioverse:prevent_warp")) {
                if (offsetState.getBlock() instanceof WarpPipeBlock && !offsetState.getValue(WarpPipeBlock.CLOSED)
                        && !(entity instanceof LivingEntity))
                    this.marioverse$enterWarp(offsetPos);
                if (state.getBlock() instanceof WarpPipeBlock && !state.getValue(WarpPipeBlock.CLOSED)
                        && !(entity instanceof LivingEntity))
                    this.marioverse$enterWarp(pos);
            }
        }

        if (stateAboveEntity.getBlock() instanceof WarpPipeBlock && !stateAboveEntity.getValue(WarpPipeBlock.CLOSED)
                && !(entity instanceof LivingEntity) && !entity.getPersistentData().getBoolean("marioverse:prevent_warp"))
            this.marioverse$enterWarp(pos);

        if (!ConfigRegistry.DISABLE_WARP_DOORS.get() && !(entity instanceof LivingEntity)
                && world.getBlockEntity(pos) instanceof WarpDoorBlockEntity
                && state.getBlock() instanceof DoorBlock && state.getValue(DoorBlock.OPEN)
                && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER
                && !entity.getPersistentData().getBoolean("marioverse:prevent_warp"))
            this.marioverse$enterWarp(pos);

        if (!ConfigRegistry.DISABLE_WARP_TRAPDOORS.get() && !(entity instanceof LivingEntity)
                && world.getBlockEntity(pos) instanceof WarpTrapDoorBlockEntity
                && state.getBlock() instanceof TrapDoorBlock && state.getValue(TrapDoorBlock.OPEN)
                && !entity.getPersistentData().getBoolean("marioverse:prevent_warp"))
            this.marioverse$enterWarp(pos);

        if (!ConfigRegistry.DISABLE_WARP_TRAPDOORS.get() && !(entity instanceof LivingEntity)
                && world.getBlockEntity(posInBlock) instanceof WarpTrapDoorBlockEntity
                && stateInBlock.getBlock() instanceof TrapDoorBlock && stateInBlock.getValue(TrapDoorBlock.OPEN)
                && !entity.getPersistentData().getBoolean("marioverse:prevent_warp"))
            this.marioverse$enterWarp(posInBlock);
    }

    @Unique
    private static void marioverse$rideIceCube(Entity entity) {
        Entity belowEntity = null;
        for (Entity e : entity.level().getEntities(entity, entity.getBoundingBox().move(0, -1, 0))) {
            if (e instanceof IceCubeEntity) {
                belowEntity = e;
                break;
            }
        }

        if (belowEntity instanceof IceCubeEntity iceCube) {
            Vec3 iceMovement = iceCube.getDeltaMovement();

            if (!iceMovement.equals(Vec3.ZERO)) {
                entity.setDeltaMovement(iceMovement.x, 0, iceMovement.z);
                entity.move(MoverType.SELF, iceMovement);

                if (entity instanceof Mob mob) {
                    mob.getNavigation().stop();
                }
            }
        }
    }

    @Unique
    private static void marioverse$bounceEntity(Entity entity, int bounceCooldown, Level world) {
        Vec3 vec3 = entity.getDeltaMovement();

        entity.resetFallDistance();
        if (vec3.y < 0.0) {
            double baseBounce = 0.42;
            double bounceFactor = (entity instanceof LivingEntity ? 1.0 : 0.8);
            double fallMultiplier = Math.min(entity.fallDistance / 10.0, 2.0);
            double newBounce = Math.max(-vec3.y * bounceFactor * fallMultiplier, baseBounce);
            Minecraft minecraft = Minecraft.getInstance();
            KeyMapping jumpKey = minecraft.options.keyJump;

            if (InputConstants.isKeyDown(minecraft.getWindow().getWindow(), jumpKey.getKey().getValue())
                    && entity instanceof Player)
                newBounce *= 2;

            if (bounceCooldown <= 0) {
                if (world instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnParticleRingBelowEntity(ParticleTypes.POOF, serverWorld, entity, entity.getBbWidth() / 2, 3);
                entity.getPersistentData().putInt("marioverse:bounce_cooldown", 1);
                entity.resetFallDistance();
                entity.setDeltaMovement(vec3.x, newBounce, vec3.z);
            }
        }
    }

    @Inject(method = "handleEntityEvent", at = @At("HEAD"))
    private void handleEntityEvent(byte id, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        RandomSource random = entity.getRandom();

        if (id == 120) {
            for(int i = 0; i < 100; ++i) {
                this.level().addParticle(ParticleTypes.ENCHANT,
                        entity.getRandomX(0.5D), entity.getRandomY(), entity.getRandomZ(0.5D),
                        (random.nextDouble() - 0.5D) * 2.0D, -random.nextDouble(),
                        (random.nextDouble() - 0.5D) * 2.0D);
            }
        }
    }

    public float getEyeHeightScale() {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity livingEntity) {
            AttributeMap attributemap = livingEntity.getAttributes();
            return attributemap == null ? 1.0F : this.sanitizeScales((float) attributemap.getValue(AttributesRegistry.EYE_HEIGHT_SCALE));
        }
        return 1.0F;
    }

    public float getHeightScale() {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity livingEntity) {
            AttributeMap attributemap = livingEntity.getAttributes();
            return attributemap == null ? 1.0F : this.sanitizeScales((float) attributemap.getValue(AttributesRegistry.HEIGHT_SCALE));
        }
        return 1.0F;
    }

    public float getWidthScale() {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity livingEntity) {
            AttributeMap attributemap = livingEntity.getAttributes();
            return attributemap == null ? 1.0F : this.sanitizeScales((float) attributemap.getValue(AttributesRegistry.WIDTH_SCALE));
        }
        return 1.0F;
    }

    public float sanitizeScales(float scale) {
        return scale;
    }

    @Inject(method = "getBoundingBox", at = @At("RETURN"), cancellable = true)
    public void getBoundingBox(CallbackInfoReturnable<AABB> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity livingEntity) {
            AttributeMap attributeMap = livingEntity.getAttributes();

            if (livingEntity.getPose() != Pose.SLEEPING) {
                double width = entity.getDimensions(entity.getPose()).width() / 2;
                double height = entity.getDimensions(entity.getPose()).height();
                double heightScale;
                double widthScale;

                if (this.getHeightScale() > 1)
                    heightScale = height;
                else if (this.getHeightScale() == 1)
                    heightScale = height;
                else heightScale = height;

                if (this.getWidthScale() > 1)
                    widthScale = width;
                else if (this.getWidthScale() == 1)
                    widthScale = width;
                else widthScale = width;

                AABB updatedBox = new AABB(entity.getX() - widthScale, entity.getY(), entity.getZ() - widthScale,
                        entity.getX() + widthScale, entity.getY() + heightScale, entity.getZ() + widthScale);

                entity.refreshDimensions();
                cir.setReturnValue(updatedBox);
            }
        }
    }

//    @Inject(method = "getBbHeight", at = @At("HEAD"), cancellable = true)
//    private void getBbHeight(CallbackInfoReturnable<Float> cir) {
//        Entity entity = (Entity) (Object) this;
//        if (entity instanceof LivingEntity livingEntity) {
//            AttributeMap attributeMap = livingEntity.getAttributes();
//            if (attributeMap != null) {
//                float heightScale = (float) attributeMap.getValue(AttributesRegistry.HEIGHT_SCALE);
//                entity.refreshDimensions();
//                if (heightScale != 1) {
//                    /*if (heightScale < 1)
//                        cir.setReturnValue((entity.getDimensions(entity.getPose()).height()));
//                    else*/ cir.setReturnValue((entity.getDimensions(entity.getPose()).height()) * heightScale);
//                }
//            }
//        }
//    }
//
//    @Inject(method = "getBbWidth", at = @At("HEAD"), cancellable = true)
//    private void getBbWidth(CallbackInfoReturnable<Float> cir) {
//        Entity entity = (Entity) (Object) this;
//        if (entity instanceof LivingEntity livingEntity) {
//            AttributeMap attributeMap = livingEntity.getAttributes();
//            if (attributeMap != null) {
//                float widthScale = (float) attributeMap.getValue(AttributesRegistry.WIDTH_SCALE);
////                entity.refreshDimensions();
//                if (widthScale != 1)
//                    cir.setReturnValue((entity.getDimensions(entity.getPose()).width()) * widthScale);
//            }
//        }
//    }

    @Inject(method = "isInWall", at = @At("HEAD"), cancellable = true)
    public void modifyIsInWall(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity.noPhysics) {
            cir.setReturnValue(false);
            return;
        }

        if (entity instanceof LivingEntity livingEntity) {
            AttributeMap attributeMap = livingEntity.getAttributes();
            if (attributeMap != null) {
                float widthScale = (float) attributeMap.getValue(AttributesRegistry.WIDTH_SCALE);

                if (widthScale != 1.0F) {
                    float scaledWidth = entity.getDimensions(entity.getPose()).width() * 0.8F * widthScale;
                    AABB aabb = AABB.ofSize(entity.getEyePosition(), scaledWidth, 1.0E-6, scaledWidth);

                    boolean isInWall = BlockPos.betweenClosedStream(aabb)
                            .anyMatch(
                                    pos -> {
                                        BlockState blockState = entity.level().getBlockState(pos);
                                        return !blockState.isAir()
                                                && blockState.isSuffocating(entity.level(), pos)
                                                && Shapes.joinIsNotEmpty(
                                                blockState.getCollisionShape(entity.level(), pos)
                                                        .move(pos.getX(), pos.getY(), pos.getZ()),
                                                Shapes.create(aabb), BooleanOp.AND
                                        );
                                    }
                            );
                    cir.setReturnValue(isInWall);
                } else cir.setReturnValue(cir.getReturnValue());
            }
        }
    }

    @Unique
    public int marioverse$getWarpCooldown() {
        Entity entity = (Entity) (Object) this;
        return entity.getPersistentData().getInt("marioverse:warp_cooldown");
    }

    @Unique
    public void marioverse$setWarpCooldown(int cooldown) {
        Entity entity = (Entity) (Object) this;
        entity.getPersistentData().putInt("marioverse:warp_cooldown", cooldown);
    }

    @Unique
    public void marioverse$enterWarp(BlockPos pos) {
        Entity entity = (Entity) (Object) this;
        Level world = entity.level();
        BlockState state = world.getBlockState(pos);
        BlockState stateAboveEntity = world.getBlockState(pos.above(Math.round(this.getBbHeight())));
        BlockEntity blockEntity = world.getBlockEntity(pos);
        BlockEntity blockEntityAbove = world.getBlockEntity(pos.above(Math.round(this.getBbHeight())));
        BlockPos warpPos;

        if (blockEntity instanceof BaseWarpBlockEntity warpBE && warpBE.getLevel() != null
                && !warpBE.preventWarp) {
            warpPos = warpBE.destinationPos;
            int entityId = this.getId();

            if (BaseWarpBlockEntity.teleportedEntities.getOrDefault(entityId, true))
                // Reset the teleport status for the entity
                BaseWarpBlockEntity.teleportedEntities.put(entityId, false);

            if (state.getBlock() instanceof DoorBlock || state.getBlock() instanceof TrapDoorBlock)
                this.marioverse$enterWarpDoor(pos, warpPos, warpBE);

            if (state.getBlock() instanceof WarpPipeBlock)
                this.marioverse$enterWarpPipe(pos, warpPos, warpBE);
        }

        if (blockEntityAbove instanceof BaseWarpBlockEntity warpBE && warpBE.getLevel() != null
                && !warpBE.preventWarp) {
            warpPos = warpBE.destinationPos;
            int entityId = this.getId();

            if (BaseWarpBlockEntity.teleportedEntities.getOrDefault(entityId, true))
                BaseWarpBlockEntity.teleportedEntities.put(entityId, false);

            if (stateAboveEntity.getBlock() instanceof WarpPipeBlock)
                this.marioverse$enterWarpPipeAbove(pos, warpPos, warpBE);
        }
    }

    @Unique
    public void marioverse$warp(BlockPos pos, BlockState state, BlockPos warpPos, BaseWarpBlockEntity warpBE) {
        Entity entity = (Entity) (Object) this;
        Level world = entity.level();

        if (warpPos != null && world.getBlockEntity(warpPos) instanceof BaseWarpBlockEntity) {
            BlockState warpState = world.getBlockState(warpPos);

            if (warpState.getBlock() instanceof DoorBlock doorblock)
                WarpDoorBlockEntity.warp(entity, warpPos, world, warpState, doorblock, warpBE);
            if (warpState.getBlock() instanceof TrapDoorBlock trapdoorBlock)
                WarpTrapDoorBlockEntity.warp(entity, warpPos, world, warpState, trapdoorBlock, warpBE);
            if (warpState.getBlock() instanceof WarpPipeBlock)
                WarpPipeBlockEntity.warp(entity, warpPos, world, warpState);
            if (state.getBlock() instanceof WarpPipeBlock)
                world.playSound(null, pos, SoundRegistry.PIPE_WARPS.get(), SoundSource.BLOCKS);
            this.marioverse$updateDoor(pos, state, warpPos, warpState);
        } else if (warpBE.getUuid() != null && warpBE.getWarpUuid() != null
                && BaseWarpBlockEntity.findMatchingUUID(warpBE.getUuid()) != null) {
            warpPos = BaseWarpBlockEntity.findMatchingUUID(warpBE.getUuid());
            BlockState warpState = world.getBlockState(warpPos);

            if (warpState.getBlock() instanceof DoorBlock doorblock)
                WarpDoorBlockEntity.warp(entity, warpPos, world, warpState, doorblock, warpBE);
            if (warpState.getBlock() instanceof TrapDoorBlock trapdoorBlock)
                WarpTrapDoorBlockEntity.warp(entity, warpPos, world, warpState, trapdoorBlock, warpBE);
            if (warpState.getBlock() instanceof WarpPipeBlock)
                WarpPipeBlockEntity.warp(entity, warpPos, world, warpState);
            if (state.getBlock() instanceof WarpPipeBlock)
                world.playSound(null, pos, SoundRegistry.PIPE_WARPS.get(), SoundSource.BLOCKS);
            this.marioverse$updateDoor(pos, state, warpPos, warpState);
        }
    }

    @Unique
    public void marioverse$enterWarpDoor(BlockPos pos, BlockPos warpPos, BaseWarpBlockEntity warpBE) {
        Entity entity = (Entity) (Object) this;
        Level world = entity.level();
        BlockState state = world.getBlockState(pos);

        if (ConfigRegistry.TELEPORT_NON_MOBS.get() && !entity.getType().is(TagRegistry.CANNOT_WARP)
                && !entity.getPersistentData().getBoolean("marioverse:prevent_warp")) {
            if (this.marioverse$getWarpCooldown() == 0 && !entity.isShiftKeyDown()) {
                this.marioverse$warp(pos, state, warpPos, warpBE);
                if (state.getBlock() instanceof DoorBlock)
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_DOOR_COOLDOWN.get());
                else this.marioverse$setWarpCooldown(ConfigRegistry.WARP_TRAPDOOR_COOLDOWN.get());
            }
        }
    }

    @Unique
    public void marioverse$enterWarpPipe(BlockPos pos, BlockPos warpPos, BaseWarpBlockEntity warpBE) {
        Entity entity = (Entity) (Object) this;
        Level world = entity.level();
        BlockState state = world.getBlockState(pos);

        double entityX = entity.getX();
        double entityY = entity.getY();
        double entityZ = entity.getZ();
        int blockX = pos.getX();
        int blockY = pos.getY();
        int blockZ = pos.getZ();

        if (ConfigRegistry.TELEPORT_NON_MOBS.get() && !entity.getType().is(TagRegistry.CANNOT_WARP)
                && !entity.getPersistentData().getBoolean("marioverse:prevent_warp")) {
            if (state.getValue(WarpPipeBlock.FACING) == Direction.UP && !entity.isShiftKeyDown() && (entityY + entity.getBbHeight() >= blockY - 1)
                    && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                if (this.marioverse$getWarpCooldown() == 0) {
                    this.marioverse$warp(pos, state, warpPos, warpBE);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                }
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.NORTH && !entity.isShiftKeyDown()
                    && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ)) {
                if (this.marioverse$getWarpCooldown() == 0) {
                    this.marioverse$warp(pos, state, warpPos, warpBE);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                }
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.SOUTH && !entity.isShiftKeyDown()
                    && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ > blockZ + 0.25)) {
                if (this.marioverse$getWarpCooldown() == 0) {
                    this.marioverse$warp(pos, state, warpPos, warpBE);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                }
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.EAST && !entity.isShiftKeyDown()
                    && (entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                if (this.marioverse$getWarpCooldown() == 0) {
                    this.marioverse$warp(pos, state, warpPos, warpBE);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                }
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.WEST && !entity.isShiftKeyDown()
                    && (entityX < blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                if (this.marioverse$getWarpCooldown() == 0) {
                    this.marioverse$warp(pos, state, warpPos, warpBE);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                }
            }
        }
    }

    @Unique
    public void marioverse$enterWarpPipeAbove(BlockPos pos, BlockPos warpPos, BaseWarpBlockEntity warpBE) {
        Entity entity = (Entity) (Object) this;
        Level world = entity.level();
        BlockState stateAboveEntity = world.getBlockState(pos.above(Math.round(entity.getBbHeight())));

        double entityX = this.getX();
        double entityZ = this.getZ();
        int blockX = pos.getX();
        int blockZ = pos.getZ();

        if (ConfigRegistry.TELEPORT_NON_MOBS.get() && !entity.getType().is(TagRegistry.CANNOT_WARP)
                && !entity.getPersistentData().getBoolean("marioverse:prevent_warp")) {
            if (stateAboveEntity.getValue(WarpPipeBlock.FACING) == Direction.DOWN
                    && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                if (this.marioverse$getWarpCooldown() == 0) {
                    this.marioverse$warp(pos, stateAboveEntity, warpPos, warpBE);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                }
            }
        }
    }

    @Unique
    public void marioverse$updateDoor(BlockPos pos, BlockState state, BlockPos warpPos, BlockState warpState) {
        Entity entity = (Entity) (Object) this;
        Level world = entity.level();
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
}
