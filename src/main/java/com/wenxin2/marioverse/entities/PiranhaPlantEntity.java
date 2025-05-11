package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.entities.part_entities.PiranhaPlantPart;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageTypeRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
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
    public static final RawAnimation CONSTANT_BITES = RawAnimation.begin().thenLoop("piranha_plant.constant_bite");
    public static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("piranha_plant.death");
    public static final RawAnimation EMERGE = RawAnimation.begin().thenPlayAndHold("piranha_plant.emerge");
    public static final RawAnimation HIDE = RawAnimation.begin().thenPlayAndHold("piranha_plant.hide");
    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("piranha_plant.idle");
    public static final RawAnimation SQUASH = RawAnimation.begin().thenPlayAndHold("piranha_plant.squash");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private BlockPos attachedBlockPos;
    private Direction attachedSide;
    private PiranhaPlantPart[] subEntities;
    public PiranhaPlantPart head;
    public boolean isHiding;
    public int hideTicks = -1;
    public int hideAnimationTicks = 0;
    private int attackCooldown = 0;

    public PiranhaPlantEntity(EntityType<? extends PiranhaPlantEntity> type, Level world) {
        super(type, world);
        this.head = new PiranhaPlantPart(this, "head",
                1.0F * this.getWidthAttribute() * this.getScaleAttribute(), 1.0F * this.getHeightAttribute() * this.getScaleAttribute());
        this.subEntities = new PiranhaPlantPart[]{this.head};
    }

    @Override
    protected int getBaseExperienceReward() {
        return 1 + this.level().random.nextInt(1);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.PIRANHA_PLANT_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.PIRANHA_PLANT_DEATH.get();
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
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(DATA_ID_HIDE_FLAGS, tag.getByte("HideFlags"));
        this.isHiding = tag.getBoolean("isHiding");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("HideFlags", this.entityData.get(DATA_ID_HIDE_FLAGS));
        tag.putBoolean("isHiding", this.isHiding);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Death", 5, this::squashAnimController));
        controllers.add(new AnimationController<>(this, "Idle", 5, this::walkAnimController));
        controllers.add(new AnimationController<>(this, "Run", 5, this::walkAnimController));
        controllers.add(new AnimationController<>(this, "Squash", 5, this::squashAnimController));
        controllers.add(DefaultAnimations.genericAttackAnimation(this, DefaultAnimations.ATTACK_BITE).transitionLength(1));
        controllers.add(new AnimationController<>(this, "emerge_controller", 5, state -> PlayState.STOP)
                .triggerableAnim("emerge", EMERGE));
        controllers.add(new AnimationController<>(this, "hide_controller", 5, state -> PlayState.STOP)
                .triggerableAnim("hide", HIDE));
    }

    protected <E extends GeoAnimatable> PlayState walkAnimController(final AnimationState<E> event) {
        List<Entity> nearbyEntities = this.level().getEntities(this,
                this.getBoundingBox().inflate(5.0D), entity -> !entity.isSpectator()
                        && entity instanceof LivingEntity && !(entity instanceof PiranhaPlantEntity));

        if (!nearbyEntities.isEmpty() && !this.isHiding()) {
            for (Entity collidingEntity : nearbyEntities) {
                if (!(collidingEntity instanceof PiranhaPlantEntity)
                        || collidingEntity.getType().is(TagRegistry.PIRANHA_PLANT_CAN_ATTACK))
                    event.setAndContinue(CONSTANT_BITES);
            }
        } else event.setAndContinue(IDLE);
        return PlayState.CONTINUE;
    }

    protected <E extends GeoAnimatable> PlayState squashAnimController(final AnimationState<E> event) {
        if (this.dead) {
            if (this.getLastDamageSource() != null
                && (this.getLastDamageSource().is(DamageTypeRegistry.STOMP)
                    || this.getLastDamageSource().is(DamageTypeRegistry.PLAYER_STOMP))) {
                event.setAndContinue(SQUASH);
                return PlayState.CONTINUE;
            } else {
                event.setAndContinue(DEATH);
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
    public void tick() {
        super.tick();
        this.head.tick();

        if (this.attackCooldown > 0)
            this.attackCooldown--;

        if (this.hideTicks > 0)
            this.hideTicks--;

        if (this.hideAnimationTicks > 0)
            this.hideAnimationTicks--;

        this.biteEntity();
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
            if (newPos != null && this.determineAttachmentSide(newPos) != Direction.UP)
                this.attachToBlock(newPos, this.determineAttachmentSide(newPos));
            else if (newPos != null && this.onGround() && this.getAttachedSide() == Direction.UP)
                this.attachToBlock(newPos, this.determineAttachmentSide(newPos));
        }

        BlockPos newPos = this.findValidBlockPos();
        if (newPos != null && !newPos.equals(attachedBlockPos))
            this.setAttachedBlockPos(newPos);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        Direction attachedSide = this.getAttachedSide();

        if (attachedSide != null) {
            Vec3 offset = calculateHitboxOffset(attachedSide);
            this.tickPart(this.head, offset.x, offset.y, offset.z);
        } else this.tickPart(this.head, 0.0, 1.0, 00.0);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.isHiding())
            return super.hurt(source, amount);
        else return false;
    }

    @NotNull
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

    @NotNull
    @Override
    public AABB makeBoundingBox() {
        Direction facing = this.getAttachedSide();
        double height = this.getHeightAttribute();
        double width = this.getWidthAttribute();
        double scale = this.getScaleAttribute();

        if (facing == null)
            facing = Direction.UP;

        return switch (facing) {
            case UP -> new AABB(
                    this.getX() - 0.5 * width * scale, this.getY(), this.getZ() - 0.5 * width * scale,
                    this.getX() + 0.5 * width * scale, this.getY() + 2.3125 * height * scale, this.getZ() + 0.5 * width * scale);
            case DOWN -> new AABB(
                    this.getX() - 0.5 * width * scale, this.getY() - 1.3125, this.getZ() - 0.5 * width * scale,
                    this.getX() + 0.5 * width * scale, this.getY() + 1.0 * height * scale, this.getZ() + 0.5 * width * scale);
            case NORTH -> new AABB(
                    this.getX() - 0.5 * width * scale, this.getY(), this.getZ() - 1.8125 * width * scale,
                    this.getX() + 0.5 * width * scale, this.getY() + 1.0 * height * scale, this.getZ() + 0.5 * width * scale);
            case SOUTH -> new AABB(
                    this.getX() - 0.5 * width * scale, this.getY(), this.getZ() - 0.5 * width * scale,
                    this.getX() + 0.5 * width * scale, this.getY() + 1.0 * height * scale, this.getZ() + 1.8125 * width * scale);
            case EAST -> new AABB(
                    this.getX() - 0.5 * width * scale, this.getY(), this.getZ() - 0.5 * width * scale,
                    this.getX() + 1.8125 * width * scale, this.getY() + 1.0 * height * scale, this.getZ() + 0.5 * width * scale);
            case WEST -> new AABB(
                    this.getX() - 1.8125 * width * scale, this.getY(), this.getZ() - 0.5  * width * scale,
                    this.getX() + 0.5 * width * scale, this.getY() + 1.0 * height * scale, this.getZ() + 0.5 * width * scale);
        };
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
    protected void pushEntities() {
        if (!isHiding())
            super.pushEntities();
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

    public PiranhaPlantPart[] getSubEntities() {
        return this.subEntities;
    }

    @Override
    public PartEntity<?> @NotNull [] getParts() {
        return this.subEntities;
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        if (true) return;
        PiranhaPlantPart[] piranhaPlantPart = this.getSubEntities();

        for (int i = 0; i < piranhaPlantPart.length; i++)
            piranhaPlantPart[i].setId(i + packet.getId());
    }

    private void tickPart(PiranhaPlantPart part, double offsetX, double offsetY, double offsetZ) {
        double height = part.getBbHeight();
        double width = part.getBbWidth() / 2.0;
        double heightScale = this.getHeightAttribute();
        double widthScale = this.getWidthAttribute();
        double scale = this.getScaleAttribute();

        part.setPos(this.getX() + offsetX + 0.5, this.getY() + offsetY, this.getZ() + offsetZ + 0.5);
        part.setBoundingBox(new AABB(this.getX() + offsetX - width, this.getY() + offsetY, this.getZ() + offsetZ - width,
                this.getX() + (offsetX + width * widthScale * (scale / 2)), this.getY() + (offsetY + height * heightScale * scale),
                this.getZ() + (offsetZ + width * widthScale * (scale / 2))));
    }

    private Vec3 calculateHitboxOffset(Direction attachedSide) {
        double height = this.getHeightAttribute();
        double width = this.getWidthAttribute();
        double scale = this.getScaleAttribute();
        double offsetX = 0.0;
        double offsetY = 0.0;
        double offsetZ = 0.0;

        switch (attachedSide) {
            case UP -> offsetY = 1.0 * height * scale;
            case DOWN -> offsetY = -1.0 * height * scale;
            case NORTH -> offsetZ = -1.0 * width * scale;
            case SOUTH -> offsetZ = 1.0 * width * scale;
            case WEST -> offsetX = -1.0 * width * scale;
            case EAST -> offsetX = 1.0 * width * scale;
        }

        return new Vec3(offsetX, offsetY, offsetZ);
    }

    private float getHeightAttribute() {
        AttributeMap attributeMap = this.getAttributes();
        if (attributeMap != null)
            return (float) this.getAttributeValue(AttributesRegistry.HEIGHT_SCALE);
        else return 1.0F;
    }

    private float getWidthAttribute() {
        AttributeMap attributeMap = this.getAttributes();
        if (attributeMap != null)
            return (float) this.getAttributeValue(AttributesRegistry.WIDTH_SCALE);
        else return 1.0F;
    }

    private float getScaleAttribute() {
        AttributeMap attributeMap = this.getAttributes();
        if (attributeMap != null)
            return (float) this.getAttributeValue(Attributes.SCALE);
        else return 1.0F;
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

    public void setAttachedBlockPos(BlockPos pos) {
        this.attachedBlockPos = pos;
    }

    public void attachToBlock(BlockPos blockPos, Direction direction) {
        this.attachedBlockPos = blockPos;
        this.attachedSide = direction;
        this.setNoGravity(true);
        this.setDeltaMovement(Vec3.ZERO);
    }

    public void detachFromBlock() {
        this.attachedBlockPos = null;
        this.attachedSide = null;
        this.setNoGravity(false);
    }

    public BlockPos findValidBlockPos() {
        BlockPos entityPos = this.blockPosition();
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = entityPos.relative(direction);
            BlockState neighborState = this.level().getBlockState(neighborPos);
            if (!neighborState.isAir() && !neighborState.is(TagRegistry.PIRANHA_PLANTS_CANNOT_ATTACH)
                    && (neighborState.isSolid()
                        || neighborState.getBlock() instanceof LeavesBlock)) {
                return neighborPos;
            }
        }
        return null;
    }

    public Direction determineAttachmentSide(BlockPos blockPos) {
        BlockPos entityPos = this.blockPosition();
        for (Direction direction : Direction.values()) {
            if (blockPos.relative(direction.getOpposite()).equals(entityPos)) {
                return direction.getOpposite();
            }
        }
        return Direction.UP;
    }

    private int getHideDuration() {
        return ConfigRegistry.PIRANHA_PLANT_HIDE_DURATION.get();
    }

    public void hideInBlock() {
        Level world = this.level();
        BlockPos pos = this.blockPosition();
        BlockPos posBelow = pos.below();
        BlockState state = world.getBlockState(pos);
        BlockState stateBelow = world.getBlockState(posBelow);

        Direction[] prioritizedDirections = new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

        if (this.isHiding()) {
            for (Direction direction : prioritizedDirections) {
                BlockPos offsetPos = pos.relative(direction);
                BlockState offsetState = world.getBlockState(offsetPos);

                if (offsetState.hasProperty(BlockStateProperties.FACING)
                        && offsetState.getValue(BlockStateProperties.FACING) == direction
                        && offsetState.is(TagRegistry.PIRANHA_PLANTS_CAN_HIDE) && !state.isSolid()) {
                    if (world.getGameTime() % this.getHideDuration() == 0L && this.hideTicks == 0L) {
                        this.stopTriggeredAnim("hide_controller", "hide");
                        this.triggerAnim("emerge_controller", "emerge");
                        this.stopHiding();
                    }
                }
            }
        } else if (world.getGameTime() % this.getHideDuration() == 0L) {
            for (Direction direction : prioritizedDirections) {
                BlockPos oppositePos = pos.relative(direction.getOpposite());
                BlockState offsetState = world.getBlockState(oppositePos);

                if (offsetState.hasProperty(BlockStateProperties.FACING)
                        && offsetState.getValue(BlockStateProperties.FACING) == direction
                        && offsetState.is(TagRegistry.PIRANHA_PLANTS_CAN_HIDE)) {
                    this.hideAnimationTicks = 15;
                    this.hideTicks = this.getHideDuration();
                    this.stopTriggeredAnim("emerge_controller", "emerge");
                    this.triggerAnim("hide_controller", "hide");
                    this.tryToHide();
                }
            }
        }

        if (this.isHiding() && !state.hasProperty(BlockStateProperties.FACING)) {
            if (world.getGameTime() % this.getHideDuration() == 0L && this.hideTicks == 0L
                    && stateBelow.is(TagRegistry.PIRANHA_PLANTS_CAN_HIDE)
                    && !state.isSolid()) {
                this.stopTriggeredAnim("hide_controller", "hide");
                this.triggerAnim("emerge_controller", "emerge");
                this.stopHiding();
            }
        } else if (world.getGameTime() % this.getHideDuration() == 0L
                && stateBelow.is(TagRegistry.PIRANHA_PLANTS_CAN_HIDE)
                && !stateBelow.hasProperty(BlockStateProperties.FACING)) {
            this.hideAnimationTicks = 15;
            this.hideTicks = this.getHideDuration();
            this.stopTriggeredAnim("emerge_controller", "emerge");
            this.triggerAnim("hide_controller", "hide");
            this.tryToHide();
        }
    }

    public void biteEntity() {
        if (this.attackCooldown > 0) {
            this.attackCooldown--;
            return;
        }

        List<Entity> nearbyEntities = this.level().getEntities(this,
                this.getBoundingBox().inflate(0.01), entity -> !entity.isSpectator()
                        && entity instanceof LivingEntity && !(entity instanceof PiranhaPlantEntity));

        if (!nearbyEntities.isEmpty() && !this.isHiding()) {
            for (Entity collidingEntity : nearbyEntities) {
                if (collidingEntity instanceof PiranhaPlantEntity
                        || !(collidingEntity.getType().is(TagRegistry.PIRANHA_PLANT_CAN_ATTACK)))
                    return;

                this.swing(InteractionHand.MAIN_HAND);
                collidingEntity.hurt(DamageTypeRegistry.piranhaChomp(collidingEntity, this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                this.playSound(SoundRegistry.PIRANHA_PLANT_CHOMP.get(), 1.0F, 1.0F);
                this.attackCooldown = 20;
                break;
            }
        }
    }

    public boolean isHiding() {
        return this.isHiding;
    }

    public void hide(boolean isHiding) {
        this.setHideFlag(8, isHiding);
        this.isHiding = isHiding;
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
