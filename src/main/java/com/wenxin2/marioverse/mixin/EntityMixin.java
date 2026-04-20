package com.wenxin2.marioverse.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.WarpDoorBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpTrapDoorBlockEntity;
import com.wenxin2.marioverse.entities.IceCubeEntity;
import com.wenxin2.marioverse.entities.KoopaShellEntity;
import com.wenxin2.marioverse.entities.KoopaTroopaEntity;
import com.wenxin2.marioverse.entities.WarpLinkableEntity;
import com.wenxin2.marioverse.entities.power_ups.OneUpMushroomEntity;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.EntityWarpEntityHandler;
import com.wenxin2.marioverse.utils.BlockWarpEntityHandler;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements BlockWarpEntityHandler, EntityWarpEntityHandler, WarpLinkableEntity {
    @Shadow protected abstract float nextStep();
    @Shadow protected abstract void playStepSound(BlockPos pos, BlockState state);
    @Shadow protected float moveDist;
    @Shadow protected float nextStep;
    @Shadow public abstract BlockPos blockPosition();
    @Shadow public abstract EntityType<?> getType();
    @Shadow public abstract Level level();
    @Shadow public abstract double getX();
    @Shadow public abstract double getY();
    @Shadow public abstract double getZ();
    @Shadow public abstract float getBbHeight();
    @Shadow public abstract int getId();
    @Shadow public abstract void setPos(Vec3 vec3);

    @Unique private static final String UUID = "UUID";
    @Unique private static final String WARP_DIMENSION = "Dimension";
    @Unique private static final String WARP_POS = "WarpPos";
    @Unique private static final String WARP_UUID = "WarpUUID";

    @Unique public BlockPos mv$destinationPos;
    @Unique public Entity mv$warpEntity;
    @Unique public String mv$dimensionTag;
    @Unique public UUID mv$UUID;
    @Unique public UUID mv$warpUUID;
    @Unique protected float mv$appliedHeightScale = 1.0F;
    @Unique protected float mv$appliedWidthScale = 1.0F;

    @Override
    public boolean mv$getBlockWarpTeleportConfig() {
        return ConfigRegistry.TELEPORT_NON_MOBS.get();
    }

    @Override
    public boolean mv$getEntityWarpTeleportConfig() {
        return ConfigRegistry.TELEPORT_NON_MOBS.get();
    }

    @Inject(method = "save", at = @At("TAIL"))
    private void addAdditionalSaveData(CompoundTag tag, CallbackInfoReturnable<Boolean> ci) {
        Entity entity = (Entity) (Object) this;
        WARP_ENTITY_LOCATIONS.put(this.blockPosition(), this.mv$getWarpEntity());
//        tag.putBoolean(BREAK_PAINTING, this.mv$breakPainting);
//        tag.putBoolean(IS_WAXED, this.mv$isWaxed);
//        tag.putBoolean(PREVENT_WARP, this.mv$preventWarp);
//        tag.putInt(WARP_FUEL_COUNT, this.mv$warpFuelCount);

        if (this.mv$hasDestinationPos() && this.mv$destinationPos != null)
            tag.put(WARP_POS, NbtUtils.writeBlockPos(this.mv$destinationPos));

        if (this.mv$dimensionTag != null)
            tag.putString(WARP_DIMENSION, this.mv$dimensionTag);

        if (this.mv$UUID != null)
            tag.putUUID(UUID, entity.getUUID());

        if (this.mv$warpUUID != null)
            tag.putUUID(WARP_UUID, this.mv$getWarpUUID());
    }

    @Inject(method = "load", at = @At("TAIL"))
    private void load(CompoundTag tag, CallbackInfo ci) {
        WARP_ENTITY_LOCATIONS.put(this.blockPosition(), this.mv$getWarpEntity());

        if (tag.contains(WARP_POS)) {
            this.mv$destinationPos = NbtUtils.readBlockPos(tag, WARP_POS).orElse(null);
            this.mv$setDestinationPos(this.mv$destinationPos);
        }

        if (tag.contains(UUID))
            this.mv$UUID = tag.getUUID(UUID);

        if (tag.contains(WARP_DIMENSION))
            this.mv$dimensionTag = tag.getString(WARP_DIMENSION);

        if (tag.contains(WARP_UUID))
            this.mv$warpUUID = tag.getUUID(WARP_UUID);
    }

    @Inject(method = "move", at = @At("HEAD"))
    public void move(MoverType type, Vec3 movement, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        Level level = entity.level();
        Vec3 motion = entity.getDeltaMovement();

        if (ConfigRegistry.ENABLE_STOMPABLE_ENEMIES.get()
                && (entity.getType().is(TagRegistry.CAN_STOMP_ENEMIES) || ConfigRegistry.ALL_MOBS_CAN_STOMP.get()
                    || level.getGameRules().getBoolean(Marioverse.ALL_MOBS_CAN_STOMP))
                && (motion.y < 0 || entity.isInWaterOrBubble())
                && entity instanceof LivingEntity livingEntity
                && !(entity instanceof Player)
                && !entity.isSpectator())
            this.mv$squashEntity(livingEntity);
    }

    @Inject(at = @At("TAIL"), method = "tick")
    public void tick(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        Level world = entity.level();
        BlockPos pos = entity.blockPosition();
        BlockPos posAboveEntity = pos.above(Math.round(entity.getBbHeight()));
        BlockPos posInBlock = pos.above(Math.round(entity.getBbHeight()) - 1);
        BlockState state = world.getBlockState(pos);
        BlockState stateAboveEntity = world.getBlockState(posAboveEntity);
        BlockState stateInBlock = world.getBlockState(posInBlock);

        if (this.moveDist > this.nextStep && entity.getData(DataAttachmentRegistry.HAS_MINI_MUSHROOM)
                && (entity.isSprinting() || entity.getDeltaMovement().horizontalDistance() >= 0.25D)
                && world.getFluidState(pos).is(FluidTags.WATER) && !world.getFluidState(pos.above()).is(FluidTags.WATER)) {
            this.playStepSound(pos, state);
            this.nextStep = this.nextStep();
        }

        mv$rideIceCube(entity);

        for (Direction facing : Direction.values()) {
            BlockPos offsetPos = pos.relative(facing);
            BlockState offsetState = world.getBlockState(offsetPos);

            if (!entity.getData(DataAttachmentRegistry.PREVENT_WARP) || entity instanceof Player) {
                if (offsetState.getBlock() instanceof WarpPipeBlock && !offsetState.getValue(WarpPipeBlock.CLOSED))
                    this.enterWarp(entity, world, offsetPos);
                if (state.getBlock() instanceof WarpPipeBlock && !state.getValue(WarpPipeBlock.CLOSED))
                    this.enterWarp(entity, world, pos);
            }
        }

        if (stateAboveEntity.getBlock() instanceof WarpPipeBlock && !stateAboveEntity.getValue(WarpPipeBlock.CLOSED)
                && !entity.getData(DataAttachmentRegistry.PREVENT_WARP))
            this.enterWarp(entity, world, pos);


        if (!ConfigRegistry.DISABLE_WARP_PAINTINGS.get()
                && !entity.getData(DataAttachmentRegistry.PREVENT_WARP)) {
            this.enterWarp(entity, world);
        }

        float f6 = this.mv$getHeightScale();
        if (f6 != this.mv$appliedHeightScale) {
            this.mv$appliedHeightScale = f6;
            entity.refreshDimensions();
        }

        float f7 = this.mv$getWidthScale();
        if (f7 != this.mv$appliedWidthScale) {
            this.mv$appliedWidthScale = f6;
            entity.refreshDimensions();
        }
    }

    @Inject(method = "playStepSound", at = @At("HEAD"), cancellable = true)
    private void playStepSound(BlockPos pos, BlockState state, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        Level level = entity.level();
        BlockPos posEntity = entity.blockPosition();

        if (entity.getData(DataAttachmentRegistry.HAS_MINI_MUSHROOM)
                && (entity.isSprinting() || entity.getDeltaMovement().horizontalDistance() >= 0.25D)
                && level.getFluidState(posEntity).is(FluidTags.WATER) && !level.getFluidState(posEntity.above()).is(FluidTags.WATER)) {
            if (level instanceof ServerLevel serverLevel)
                ServerParticleUtils.spawnParticleTrail(ParticleTypes.SPLASH, serverLevel, entity, false, true, 5, 0.1);
            entity.playSound(SoundRegistry.WATER_MINI_STEP.get(), 1.0F, 1.0F + (entity.getRandom().nextFloat() - 0.5F) * 0.2F);
            ci.cancel();
        }
    }

    @ModifyReturnValue(method = "isInWaterOrBubble", at = @At("RETURN"))
    private boolean isInWaterOrBubble(boolean original) {
        BlockState state = this.level().getBlockState(this.blockPosition());
        if (!original) {
            if (state.is(BlockRegistry.PIPE_BUBBLES.get()))
                return true;
        }
        return original;
    }

    @ModifyReturnValue(method = "isInWaterRainOrBubble", at = @At("RETURN"))
    private boolean isInWaterRainOrBubble(boolean original) {
        BlockState state = this.level().getBlockState(this.blockPosition());
        if (!original) {
            if (state.is(BlockRegistry.PIPE_BUBBLES.get()))
                return true;
        }
        return original;
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

    @ModifyReturnValue(method = "getEyeHeight(Lnet/minecraft/world/entity/Pose;)F", at = @At("TAIL"))
    private float getEyeHeight(float original, Pose pose) {
        float eyeScale = this.mv$getEyeHeightScale();
        return original * eyeScale;
    }

    @Inject(method = "getBbHeight", at = @At("HEAD"), cancellable = true)
    private void getBbHeight(CallbackInfoReturnable<Float> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity livingEntity) {
            AttributeMap attributeMap = livingEntity.getAttributes();
            if (attributeMap != null) {
                float height = entity.getDimensions(entity.getPose()).height();
                cir.setReturnValue(height);
            }
        }
    }

    @Inject(method = "getBbWidth", at = @At("HEAD"), cancellable = true)
    private void getBbWidth(CallbackInfoReturnable<Float> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity livingEntity) {
            AttributeMap attributeMap = livingEntity.getAttributes();
            if (attributeMap != null) {
                float width = entity.getDimensions(entity.getPose()).width();
                cir.setReturnValue(width);
            }
        }
    }

    @Unique
    public float mv$getEyeHeightScale() {
        Entity entity = (Entity) (Object) this;
        if (!(entity instanceof LivingEntity livingEntity))
            return 1.0F;

        AttributeMap attributemap = livingEntity.getAttributes();
        return attributemap == null ? 1.0F : this.mv$sanitizeScales((float) attributemap.getValue(AttributesRegistry.EYE_HEIGHT_SCALE));
    }

    @Unique
    private static void mv$rideIceCube(Entity entity) {
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
    public float mv$getHeightScale() {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity livingEntity) {
            AttributeMap attributeMap = livingEntity.getAttributes();
            return attributeMap == null ? 1.0F : this.mv$sanitizeScales((float) attributeMap.getValue(AttributesRegistry.HEIGHT_SCALE));
        }
        return 1.0F;
    }

    @Unique
    public float mv$getWidthScale() {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity livingEntity) {
            AttributeMap attributeMap = livingEntity.getAttributes();
            return attributeMap == null ? 1.0F : this.mv$sanitizeScales((float) attributeMap.getValue(AttributesRegistry.WIDTH_SCALE));
        }
        return 1.0F;
    }

    @Unique
    public float mv$sanitizeScales(float scale) {
        return scale;
    }

    @Unique
    public void mv$squashEntity(LivingEntity stompingEntity) {
        Vec3 motion = stompingEntity.getDeltaMovement();
        AABB inflatedBox = stompingEntity.getBoundingBox().expandTowards(0, motion.y, 0)
                .inflate(0.5, 0.0, 0.5);

        List<Entity> nearbyEntities = stompingEntity.level().getEntities(stompingEntity, inflatedBox);

        if (!nearbyEntities.isEmpty()) {
            for (Entity entity : nearbyEntities) {
                if (entity instanceof LivingEntity damagedEntity && !damagedEntity.isVehicle()
                        && (stompingEntity.getType().is(TagRegistry.CAN_STOMP_ENEMIES) || ConfigRegistry.ALL_MOBS_CAN_STOMP.get()
                            || stompingEntity.level().getGameRules().getBoolean(Marioverse.ALL_MOBS_CAN_STOMP))

                        && !damagedEntity.getType().is(TagRegistry.POWER_UP_ENTITIES)

                        && (damagedEntity.getType().is(TagRegistry.CAN_BE_STOMPED)
                            || damagedEntity.getType().is(TagRegistry.CAN_BE_INSTAKILL_STOMPED)
                            || (damagedEntity.level().getGameRules().getBoolean(Marioverse.STOMP_ALL_MOBS)
                                && damagedEntity instanceof LivingEntity)

                            || (ConfigRegistry.STOMP_ALL_MOBS.get() && damagedEntity instanceof LivingEntity))) {

                    if (stompingEntity instanceof Player player && player.getAbilities().flying)
                        return;

                    if (stompingEntity.getData(DataAttachmentRegistry.HAS_SUPER_STAR)
                            || damagedEntity.getData(DataAttachmentRegistry.HAS_SUPER_STAR))
                        return;

                    double startY = stompingEntity.getBoundingBox().minY;
                    double endY = startY + motion.y;
                    double targetTop = damagedEntity.getBoundingBox().maxY;

                    if (startY >= targetTop && endY <= targetTop
                            && (motion.y < 0 || stompingEntity.isInWaterOrBubble())) {
                        double bounceBlockHeight = ConfigRegistry.STOMP_BOUNCE_HEIGHT.getAsDouble();
                        double gravity = 0.08;
                        double bounceVelocity = Math.sqrt(2 * gravity * bounceBlockHeight);

                        if (damagedEntity.isAlive()) {
                            stompingEntity.setDeltaMovement(stompingEntity.getDeltaMovement().x, bounceVelocity, stompingEntity.getDeltaMovement().z);
                            stompingEntity.hasImpulse = true;
                            if (stompingEntity instanceof ServerPlayer serverPlayer)
                                serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(stompingEntity));
                        }

                        float scaleFactor = damagedEntity.getBbHeight() * damagedEntity.getBbWidth();
                        int numParticles = (int) (scaleFactor * 20);
                        double radius = damagedEntity.getBbWidth() / 2;

                        if (stompingEntity.level() instanceof ServerLevel serverWorld)
                            ServerParticleUtils.spawnParticleRingAboveEntity(ParticleTypes.CRIT, serverWorld, damagedEntity, radius, 0, numParticles);

                        boolean hasNoArmor = true;
                        for (ItemStack armorSlot : damagedEntity.getArmorSlots()) {
                            if (!armorSlot.isEmpty()) {
                                hasNoArmor = false;
                                break;
                            }
                        }

                        if (!stompingEntity.level().isClientSide() && !damagedEntity.isDeadOrDying()) {
                            if (damagedEntity.getType().is(TagRegistry.CAN_BE_INSTAKILL_STOMPED) && hasNoArmor
                                    && !stompingEntity.getData(DataAttachmentRegistry.HAS_MINI_MUSHROOM))
                                damagedEntity.hurt(DamageSourceRegistry.stomp(damagedEntity, stompingEntity), damagedEntity.getHealth());
                            else if (damagedEntity.getType().is(TagRegistry.CAN_BE_STOMPED) || ConfigRegistry.STOMP_ALL_MOBS.get()
                                    || stompingEntity.level().getGameRules().getBoolean(Marioverse.STOMP_ALL_MOBS)) {
                                if (stompingEntity.getData(DataAttachmentRegistry.HAS_MINI_MUSHROOM)
                                        || damagedEntity instanceof KoopaTroopaEntity
                                        || damagedEntity instanceof KoopaShellEntity)
                                    damagedEntity.hurt(DamageSourceRegistry.stomp(damagedEntity, stompingEntity), 0);
                                else damagedEntity.hurt(DamageSourceRegistry.stomp(damagedEntity, stompingEntity), ConfigRegistry.STOMP_DAMAGE.get().floatValue());
                            }
                            if (!ConfigRegistry.DISABLE_CONSECUTIVE_BOUNCING.get() && !stompingEntity.getData(DataAttachmentRegistry.HAS_MINI_MUSHROOM))
                                OneUpMushroomEntity.consecutiveReward(stompingEntity, damagedEntity, stompingEntity.getData(DataAttachmentRegistry.CONSECUTIVE_BOUNCES));
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean mv$hasDestinationPos() {
        return this.mv$destinationPos != null;
    }

    @Override
    public void mv$setDestinationPos(@Nullable BlockPos pos) {
        this.mv$destinationPos = pos;
    }

    @Override
    public BlockPos mv$getDestinationPos() {
        if (this.mv$destinationPos != null) {
            return this.mv$destinationPos;
        }
        return null;
    }

    @Override
    public ResourceKey<Level> mv$getDestinationDim() {
        if (this.mv$dimensionTag != null) {
            ResourceLocation location = ResourceLocation.tryParse(this.mv$dimensionTag);
            if (location != null) {
                return ResourceKey.create(Registries.DIMENSION, location);
            }
        }
        return null;
    }

    @Override
    public void mv$setDestinationDim(@Nullable ResourceKey<Level> dimension) {
        if (dimension != null)
            this.mv$dimensionTag = dimension.location().toString();
    }

    @Override
    public UUID mv$getWarpUUID() {
        return this.mv$warpUUID;
    }

    @Override
    public void mv$setWarpUuid(UUID uuid) {
        this.mv$warpUUID = uuid;
    }

    @Override
    public Entity mv$getWarpEntity() {
        return this.mv$warpEntity;
    }

    @Override
    public void mv$setWarpEntity(Entity entity) {
        this.mv$warpEntity = entity;
    }
}