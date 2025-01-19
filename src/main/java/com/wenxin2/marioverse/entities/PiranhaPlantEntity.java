package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.blocks.ClearWarpPipeBlock;
import com.wenxin2.marioverse.entities.ai.goals.NearestAttackableTagGoal;
import com.wenxin2.marioverse.entities.part_entities.PiranhaPlantPart;
import com.wenxin2.marioverse.init.AttributesRegistry;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.DamageSourceRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PiranhaPlantEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<Byte> DATA_ID_HIDE_FLAGS = SynchedEntityData.defineId(PiranhaPlantEntity.class, EntityDataSerializers.BYTE);
    public static final RawAnimation CONSTANT_BITES_ANIM = RawAnimation.begin().thenLoop("piranha_plant.constant_bite");
    public static final RawAnimation DEATH_ANIM = RawAnimation.begin().thenPlayAndHold("piranha_plant.death");
    public static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("piranha_plant.idle");
    public static final RawAnimation SQUASH_ANIM = RawAnimation.begin().thenPlayAndHold("piranha_plant.squash");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private BlockPos attachedBlockPos;
    private Direction attachedSide;
    private PiranhaPlantPart[] subEntities;
    public PiranhaPlantPart head;
    private float lastWidth = 1.0F;
    private float lastHeight = 1.0F;

    @Nullable private BlockPos targetPosition;

    public PiranhaPlantEntity(EntityType<? extends PiranhaPlantEntity> type, Level world) {
        super(type, world);
        this.head = new PiranhaPlantPart(this, "head", 1.0F * this.getWidthAttribute(), 1.0F * this.getHeightAttribute());
        this.subEntities = new PiranhaPlantPart[]{this.head};
    }

    @Override
    protected int getBaseExperienceReward() {
        return 1 + this.level().random.nextInt(1);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.GOOMBA_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.GOOMBA_STOMP.get();
    }

    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundRegistry.GOOMBA_STEP.get(), 1.0F, 1.0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_HIDE_FLAGS, (byte) 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 0.6D, true));
        this.targetSelector.addGoal(0, new NearestAttackableTagGoal(this, TagRegistry.PIRANHA_PLANT_CAN_ATTACK, true));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Death", 5, this::squashAnimController));
        controllers.add(new AnimationController<>(this, "Idle", 5, this::walkAnimController));
        controllers.add(new AnimationController<>(this, "Run", 5, this::walkAnimController));
        controllers.add(new AnimationController<>(this, "Squash", 5, this::squashAnimController));
        controllers.add(DefaultAnimations.genericAttackAnimation(this, DefaultAnimations.ATTACK_BITE).transitionLength(1));
    }

    protected <E extends GeoAnimatable> PlayState walkAnimController(final AnimationState<E> event) {
        List<Entity> nearbyEntities = this.level().getEntities(this,
                this.getBoundingBox().inflate(5.0D), entity -> !entity.isSpectator()
                        && entity instanceof LivingEntity && !(entity instanceof PiranhaPlantEntity));

        if (!nearbyEntities.isEmpty()) {
            for (Entity collidingEntity : nearbyEntities) {
                if (!(collidingEntity instanceof PiranhaPlantEntity)
                        || collidingEntity.getType().is(TagRegistry.PIRANHA_PLANT_CAN_ATTACK))
                    event.setAndContinue(CONSTANT_BITES_ANIM);
            }
        } else event.setAndContinue(IDLE_ANIM);
        return PlayState.CONTINUE;
    }

    protected <E extends GeoAnimatable> PlayState squashAnimController(final AnimationState<E> event) {
        if (this.dead) {
            if (this.getLastDamageSource() != null
                && (this.getLastDamageSource().is(DamageSourceRegistry.STOMP)
                    || this.getLastDamageSource().is(DamageSourceRegistry.PLAYER_STOMP))) {
                event.setAndContinue(SQUASH_ANIM);
                return PlayState.CONTINUE;
            } else {
                event.setAndContinue(DEATH_ANIM);
                return PlayState.CONTINUE;
            }
        }
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(DATA_ID_HIDE_FLAGS, tag.getByte("HideFlags"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("HideFlags", this.entityData.get(DATA_ID_HIDE_FLAGS));
    }

    @Override
    public void tick() {
        float currentWidth = getWidthAttribute();
        float currentHeight = getHeightAttribute();

        super.tick();
        this.checkForCollisions();
        this.hideInBlock();

        if (this.isInWaterOrBubble())
            this.ejectPassengers();

        AttributeInstance scale = this.getAttribute(Attributes.SCALE);
        if (scale != null && this.level().getBlockState(this.blockPosition()).getBlock() instanceof FlowerPotBlock)
            scale.setBaseValue(0.4F);

        if (attachedBlockPos != null) {
            BlockPos newPos = this.findValidBlockPos();
            if (this.level().isEmptyBlock(attachedBlockPos) && !this.isHiding()) {
                this.detachFromBlock();
            } else if (newPos == null && !this.isHiding()) {
                this.detachFromBlock();
            } else {
                this.setNoGravity(true);
                this.setDeltaMovement(Vec3.ZERO);
            }
        }

        if (attachedBlockPos == null) {
            BlockPos newPos = this.findValidBlockPos();
            if (newPos != null)
                this.attachToBlock(newPos, this.determineAttachmentSide(newPos));
            else if (this.onGround() && this.getAttachedSide() == Direction.UP)
                this.setNoGravity(false);
        }

        if (currentWidth != lastWidth || currentHeight != lastHeight) {
            lastWidth = currentWidth;
            lastHeight = currentHeight;
            this.recreateHeadPart(currentWidth, currentHeight);
        }
    }

    @Override
    public void baseTick() {
        int i = this.getAirSupply();

        super.baseTick();
        this.handleAirSupply(i);
    }

    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        Direction attachedSide = this.getAttachedSide();

        if (attachedSide == Direction.NORTH || attachedSide == Direction.SOUTH
                || attachedSide == Direction.EAST || attachedSide == Direction.WEST) {
            return EntityDimensions.scalable(1.0F, 1.0F);
        } else if (attachedSide == Direction.DOWN)
            return EntityDimensions.scalable(1.0F, 1.0F).withEyeHeight(-0.9F);

        return super.getDefaultDimensions(pose);
    }

    public static boolean checkPiranhaPlantSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor serverWorld,
                                                      MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return serverWorld.getDifficulty() != Difficulty.PEACEFUL
                && (MobSpawnType.ignoresLightRequirements(spawnType) || isBrightEnoughToSpawn(serverWorld, pos))
                && serverWorld.getBlockState(pos.below()).is(TagRegistry.PIRANHA_PLANTS_SPAWNABLE_ON);
    }

    protected static boolean isBrightEnoughToSpawn(BlockAndTintGetter blockGetter, BlockPos pos) {
        int skyLight = blockGetter.getRawBrightness(pos, 0);
        int blockLight = blockGetter.getBrightness(LightLayer.BLOCK, pos);
        return skyLight > 7 && blockLight <= 7;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean isInWall() {
        if (isHiding())
            return false;
        else return super.isInWall();
    }

    @Override
    protected boolean wouldNotSuffocateAtTargetPose(Pose pose) {
        AABB aabb = this.getDimensions(pose).makeBoundingBox(this.position());
        return this.level().noBlockCollision(this, aabb) || this.isHiding();
    }

    @Override
    public boolean canBeLeashed() {
        return true;
    }

    @NotNull
    @Override
    protected Vec3 getLeashOffset() {
        return new Vec3(0.0, this.getEyeHeight() / 1.75, this.getBbWidth() / 2);
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public PartEntity<?> @NotNull [] getParts() {
        return this.subEntities;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        Direction attachedSide = this.getAttachedSide();
        if (attachedSide != null) {
            Vec3 offset = calculateHitboxOffset(attachedSide);
            if (attachedSide == Direction.NORTH || attachedSide == Direction.SOUTH
                    || attachedSide == Direction.EAST || attachedSide == Direction.WEST
                    || attachedSide == Direction.DOWN)
                this.tickPart(this.head, offset.x, offset.y, offset.z);
        }
    }

    private void tickPart(PiranhaPlantPart part, double offsetX, double offsetY, double offsetZ) {
        part.setPos(this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ);
    }

    private void recreateHeadPart(float width, float height) {
        this.head = new PiranhaPlantPart(this, "head", width, height);
        this.subEntities = new PiranhaPlantPart[]{this.head};
    }

    private Vec3 calculateHitboxOffset(Direction attachedSide) {
        double width = this.getWidthAttribute();
        double height = this.getHeightAttribute();
        double offsetX = 0.0;
        double offsetY = 0.0;
        double offsetZ = 0.0;

        switch (attachedSide) {
            case UP -> offsetY = 1.0 * height;
            case DOWN -> offsetY = -1.0 * height;
            case NORTH -> offsetZ = -1.0 * width;
            case SOUTH -> offsetZ = 1.0 * width;
            case WEST -> offsetX = -1.0 * width;
            case EAST -> offsetX = 1.0 * width;
        }

        return new Vec3(offsetX, offsetY, offsetZ);
    }

    private float getWidthAttribute() {
        return (float) this.getAttributeValue(AttributesRegistry.WIDTH_SCALE);
    }

    private float getHeightAttribute() {
        return (float) this.getAttributeValue(AttributesRegistry.HEIGHT_SCALE);
    }

    public Direction getAttachedSide() {
        return attachedSide;
    }

    public BlockState getBlockAttachedTo(Level world) {
        return world.getBlockState(attachedBlockPos);
    }

    public void setAttachedSide(Direction side) {
        this.attachedSide = side;
    }

    public void attachToBlock(BlockPos blockPos, Direction side) {
        this.attachedBlockPos = blockPos;
        this.attachedSide = side;
        this.setNoGravity(true);
        this.setDeltaMovement(Vec3.ZERO);
    }

    public void detachFromBlock() {
        this.attachedBlockPos = null;
        this.attachedSide = null;
        this.setNoGravity(false);
    }

    private BlockPos findValidBlockPos() {
        BlockPos entityPos = this.blockPosition();
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = entityPos.relative(direction);
            BlockState neighborState = this.level().getBlockState(neighborPos);
            if (!neighborState.isAir() && !neighborState.is(TagRegistry.PIRANHA_PLANTS_CANNOT_ATTACH)
                    && (neighborState.isFaceSturdy(this.level(), neighborPos, direction.getOpposite())
                        || neighborState.getBlock() instanceof LeavesBlock)) {
                return neighborPos;
            }
        }
        return null;
    }

    private Direction determineAttachmentSide(BlockPos blockPos) {
        BlockPos entityPos = this.blockPosition();
        for (Direction direction : Direction.values()) {
            if (blockPos.relative(direction.getOpposite()).equals(entityPos)) {
                return direction.getOpposite();
            }
        }
        return Direction.UP;
    }

    private float currentScale = 1.0F;
    private float targetScale = 1.0F;
    private float scaleCooldown;
    private boolean isLerping = false;
    private static final float SCALING_SPEED = 0.1F;

    public void hideInBlock() {
        AttributeInstance scale = this.getAttribute(Attributes.SCALE);
        Level world = this.level();
        BlockPos pos = this.blockPosition();
        BlockPos posAbove = pos.above();
        BlockPos posBelow = pos.below();
        BlockState state = world.getBlockState(pos);
        BlockState stateAbove = world.getBlockState(posAbove);
        BlockState stateBelow = world.getBlockState(posBelow);
        double speed = 0.02;

        if (scaleCooldown > 0)
            scaleCooldown--;

        if (!isLerping && scaleCooldown == 0) {
            if (this.isHiding())
                targetScale = 0.3F;
            else targetScale = 1.0F;

            if (Math.abs(currentScale - targetScale) > 0.01F)
                isLerping = true;
        }

        if (isLerping) {
            currentScale = Mth.lerp(SCALING_SPEED, currentScale, targetScale);
            if (scale != null)
                scale.setBaseValue(currentScale);

            if (Math.abs(currentScale - targetScale) < 0.01F) {
                currentScale = targetScale;
                if (scale != null)
                    scale.setBaseValue(currentScale);
                isLerping = false;
                scaleCooldown = 20;
            }

        }

        if (this.isHiding() && !state.is(TagRegistry.PIRANHA_PLANTS_CAN_HIDE)
                && !stateBelow.is(TagRegistry.PIRANHA_PLANTS_CAN_HIDE)
                && !state.hasProperty(BlockStateProperties.FACING)
                && !stateBelow.hasProperty(BlockStateProperties.FACING)) {
            this.stopHiding();
            return;
        }

        Direction[] prioritizedDirections = new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

        if (this.isHiding()) {
            // Handle emerging from hiding
            for (Direction direction : prioritizedDirections) {
                BlockPos offsetPos = pos.relative(direction.getOpposite());
                BlockState offsetState = world.getBlockState(offsetPos);

                // Check if the block has a FACING property or proceed without it
                if (state.hasProperty(BlockStateProperties.FACING)
                        && state.getValue(BlockStateProperties.FACING) == direction
                        && state.is(TagRegistry.PIRANHA_PLANTS_CAN_HIDE) && !offsetState.isSolid()) {

                    // Check if it's safe to emerge
                    if (world.getGameTime() % ConfigRegistry.PIRANHA_PLANT_HIDE_DURATION.get() == 0L) {
                        double deltaX = offsetPos.getX() - this.getX();
                        double deltaY = offsetPos.getY() - this.getY();
                        double deltaZ = offsetPos.getZ() - this.getZ();
                        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

                        this.setNoGravity(false);
                        this.noPhysics = false;
                        if (distance > 0) {
                            this.setDeltaMovement((deltaX / distance) * speed, (deltaY / distance) * speed, (deltaZ / distance) * speed);
                            this.move(MoverType.SELF, this.getDeltaMovement());
                        }
                        this.stopHiding();
                        return;
                    }
                }

                if (!state.is(TagRegistry.PIRANHA_PLANTS_CAN_HIDE)
                        && !offsetState.is(TagRegistry.PIRANHA_PLANTS_CAN_HIDE)) {
                    this.stopHiding();
                    return;
                }
            }
        } else {
            // Handle hiding logic
            for (Direction direction : prioritizedDirections) {
                BlockPos offsetPos = pos.relative(direction.getOpposite());
                BlockState offsetState = world.getBlockState(offsetPos);

                // Check if the block has a FACING property or proceed without it
                if (offsetState.hasProperty(BlockStateProperties.FACING)
                        && offsetState.getValue(BlockStateProperties.FACING) == direction
                        && offsetState.is(TagRegistry.PIRANHA_PLANTS_CAN_HIDE)) {

                    // Check if it's safe to hide
                    if (world.getGameTime() % ConfigRegistry.PIRANHA_PLANT_HIDE_DURATION.get() == 0L && !isLerping && scaleCooldown == 0) {
                        double deltaX = offsetPos.getX() - this.getX();
                        double deltaY = offsetPos.getY() - this.getY();
                        double deltaZ = offsetPos.getZ() - this.getZ();
//                        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                        double distance = Math.abs(deltaY);;

                        if (distance > 0) {
                            this.setDeltaMovement(0, (deltaY / distance) * speed, 0);
//                            this.setDeltaMovement((deltaX / distance) * speed, (deltaY / distance) * speed, (deltaZ / distance) * speed);
                        }
                        this.setNoGravity(true);
                        this.noPhysics = true;
                        this.tryToHide();
                    }
                }
            }
        }

        if (this.isHiding() && !state.hasProperty(BlockStateProperties.FACING)) {
            if (world.getGameTime() % ConfigRegistry.PIRANHA_PLANT_HIDE_DURATION.get() == 0L
                    && state.is(TagRegistry.PIRANHA_PLANTS_CAN_HIDE)
                    && !stateAbove.isSolid()) {
                double deltaYAbove = posAbove.getY() - this.getY();
                double distanceAbove = Math.abs(deltaYAbove);

                this.setNoGravity(false);
                this.noPhysics = false;
                if (distanceAbove > 0) {
                    if (state.getBlock() instanceof ClearWarpPipeBlock)
                        this.setDeltaMovement(0, 0.8, 0);
                    else this.setDeltaMovement(0, 0.3, 0);
                    this.move(MoverType.SELF, this.getDeltaMovement());
                }
                this.stopHiding();
            }
        } else if (world.getGameTime() % ConfigRegistry.PIRANHA_PLANT_HIDE_DURATION.get() == 0L
                && stateBelow.is(TagRegistry.PIRANHA_PLANTS_CAN_HIDE)
                && !stateBelow.hasProperty(BlockStateProperties.FACING) && !isLerping && scaleCooldown == 0) {
            double deltaYBelow = posBelow.getY() - this.getY();
            double distanceBelow = Math.abs(deltaYBelow);

            if (distanceBelow > 0)
                this.setDeltaMovement(0, (deltaYBelow / distanceBelow) * speed, 0);

            this.setNoGravity(true);
            this.noPhysics = true;
            this.tryToHide();
        } else this.setNoGravity(false);
    }

    public void checkForCollisions() {
        List<Entity> nearbyEntities = this.level().getEntities(this,
                this.getBoundingBox().inflate(0.01D), entity -> !entity.isSpectator()
                        && entity instanceof LivingEntity && !(entity instanceof PiranhaPlantEntity));

        if (!nearbyEntities.isEmpty() && this.isHiding()) {
            for (Entity collidingEntity : nearbyEntities) {
                if (collidingEntity instanceof PiranhaPlantEntity
                        || !(collidingEntity.getType().is(TagRegistry.PIRANHA_PLANT_CAN_ATTACK)))
                    return;

                this.swing(InteractionHand.MAIN_HAND);
                this.doHurtTarget(collidingEntity);
                break;
            }
        }
    }

    protected void handleAirSupply(int airSupplyAmount) {
        if (this.isAlive() && this.isInWaterOrBubble()) {
            this.setAirSupply(airSupplyAmount);
        }
    }

    public boolean isHiding() {
        return this.getHideFlag(8);
    }

    public void hide(boolean isHiding) {
        this.setHideFlag(8, isHiding);
    }

    private boolean getHideFlag(int i) {
        return (this.entityData.get(DATA_ID_HIDE_FLAGS) & i) != 0;
    }

    public void tryToHide() {
        this.hide(Boolean.TRUE);
        this.stopInPlace();
    }

    public void stopHiding() {
        this.hide(Boolean.FALSE);
        this.isHideStopping();
    }

    public void isHideStopping() {
        this.getHideFlag(8);
    }

    private void setHideFlag(int i, boolean b) {
        byte b0 = this.entityData.get(DATA_ID_HIDE_FLAGS);
        if (b) {
            this.entityData.set(DATA_ID_HIDE_FLAGS, (byte)(b0 | i));
        } else {
            this.entityData.set(DATA_ID_HIDE_FLAGS, (byte)(b0 & ~i));
        }
    }
}
