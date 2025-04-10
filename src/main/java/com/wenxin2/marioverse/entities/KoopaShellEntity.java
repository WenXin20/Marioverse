package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.entities.ai.controls.AmphibiousMoveControl;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageTypeRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.data.SlotTypeLoader;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

public class KoopaShellEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<Byte> DATA_ID_HIDE_FLAGS = SynchedEntityData.defineId(KoopaShellEntity.class, EntityDataSerializers.BYTE);
    public static final RawAnimation EMERGE = RawAnimation.begin().thenPlayAndHold("move.emerge");
    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private Vec3 slidingDirection = new Vec3(this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z);
    private boolean isSliding = false;
    private int hideTicks = -1;
    private int emergeAnimationTicks = -1;

    public KoopaShellEntity(EntityType<? extends KoopaShellEntity> type, Level world) {
        super(type, world);
        this.setPathfindingMalus(PathType.DOOR_OPEN, 1.0F);
        this.setPathfindingMalus(PathType.WATER, 2.0F);
        this.moveControl = new AmphibiousMoveControl(this, 85, 10, 0.6F, 1.0F, true);
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
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundRegistry.GOOMBA_STEP.get(), 1.0F, 1.0F);
    }

    protected SoundEvent getBumpSound() {
        return SoundRegistry.GOOMBA_BUMP.get();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_HIDE_FLAGS, (byte)0);
    }

    @Override
    protected void registerGoals() {}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericIdleController(this).transitionLength(0));
        controllers.add(new AnimationController<>(this, "emerge_controller", 5, state -> PlayState.CONTINUE)
                .triggerableAnim("emerge", EMERGE));
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
        super.tick();
        int i = this.getAirSupply();
        Vec3 motion = this.getDeltaMovement();

        this.handleAirSupply(i);
        this.collideWithWall(this.level());

        if (isSliding) {
            BlockPos posBelow = this.blockPosition().below();
            BlockState stateBelow = level().getBlockState(posBelow);
            float friction = stateBelow.getFriction(level(), posBelow, this);
            double slideSpeed = (friction > 0.8) ? 0.4 + friction / 1.5 : 1.0;

            // Continue sliding in the saved direction
            Vec3 slideMotion = this.slidingDirection.normalize().scale(slideSpeed);
            this.setDeltaMovement(slideMotion);
            this.slidingDirection = slideMotion;
        }

        // Detect whether we should still be sliding
        if (motion.horizontalDistanceSqr() < 0.0001) {
            isSliding = false;
        } else if (!isSliding && motion.horizontalDistanceSqr() > 0.0001) {
            // Only update slidingDirection if we weren't already sliding
            this.slidingDirection = motion;
            isSliding = true;
        }

        if (hideTicks > 0 && this.getDeltaMovement().horizontalDistance() == 0 && this.onGround())
            hideTicks--;

        if (emergeAnimationTicks > 0)
            emergeAnimationTicks--;

        if (!this.level().isClientSide && emergeAnimationTicks == 0) {
            KoopaTroopaEntity entity = new KoopaTroopaEntity(EntityRegistry.GREEN_KOOPA_TROOPA.get(), this.level());

            entity.setPos(this.getX(), this.getY(), this.getZ());
            entity.setYRot(this.getYRot());
            entity.setXRot(this.getXRot());
            entity.yBodyRot = this.yBodyRot;
            entity.setYHeadRot(this.getYHeadRot());
            entity.setHealth(this.getHealth());
            for (EquipmentSlot slot : EquipmentSlot.values())
                entity.setItemSlot(slot, this.getItemBySlot(slot).copy());

            AccessoriesCapability capability = AccessoriesCapability.get(this);
            if (capability != null && ConfigRegistry.EQUIP_COSTUMES_MOBS.get()
                    && !this.getType().is(TagRegistry.CANNOT_LOSE_POWER_UP)) {
                String[] slotTypes = {"costume_hat", "costume_shirt", "costume_pants", "costume_shoes"};
                for (String slotType : slotTypes) {
                    AccessoriesContainer container = capability.getContainer(SlotTypeLoader.getSlotType(this, slotType));
                    AccessoriesContainer containerEntity = capability.getContainer(SlotTypeLoader.getSlotType(entity, slotType));
                    if (container != null) {
                        ItemStack stack = container.getAccessories().getItem(0);
                        if (containerEntity != null)
                            containerEntity.getAccessories().setItem(0, stack);
                    }
                }
            }

            this.level().addFreshEntity(entity);
            this.remove(RemovalReason.DISCARDED);
        }

        if (hideTicks == 0 && emergeAnimationTicks <= 0) {
            this.triggerAnim("emerge_controller", "emerge");
            this.emergeAnimationTicks = 80;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Level world = this.level();
        BlockPos posBelow = this.blockPosition().below();
        BlockState stateBelow = world.getBlockState(posBelow);

        if (source.is(DamageTypeRegistry.STOMP) || source.is(DamageTypeRegistry.PLAYER_STOMP)) {
            this.getNavigation().stop();
            this.setXxa(0.0F);
            this.setSpeed(0.0F);
        }

        if (!source.is(DamageTypeRegistry.STOMP) && !source.is(DamageTypeRegistry.PLAYER_STOMP)) {
            float friction = stateBelow.getFriction(world, posBelow, this);
            double slideSpeed;

            if (friction > 0.6)
                slideSpeed = 0.4 + friction / 1.5;
            else slideSpeed = 1.0;

            Vec3 slideDirection = new Vec3(this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z);

            if (source.getEntity() != null) {
                Vec3 attackerPos = source.getEntity().position();
                Vec3 hitPos = this.position();
                Vec3 slideDirRaw = hitPos.subtract(attackerPos).normalize();
                slideDirection = new Vec3(slideDirRaw.x, this.getDeltaMovement().y, slideDirRaw.z).normalize();
            } else if (source.getDirectEntity() != null)
                slideDirection = source.getDirectEntity().getDeltaMovement().normalize();
            Vec3 movement = slideDirection.scale(slideSpeed);

            if (!isNoAi()) {
                this.setDeltaMovement(movement);
                this.isSliding = true;
                this.slidingDirection = movement;
            }
        }

        return super.hurt(source, amount);
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isControlledByLocalInstance() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        return new AmphibiousPathNavigation(this, world);
    }

    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }

    protected void handleAirSupply(int airSupplyAmount) {
        if (this.isAlive() && this.isInWaterOrBubble()) {
            this.setAirSupply(airSupplyAmount);
        }
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canBeLeashed() {
        return true;
    }

    public void setHideTicks(int hideTicks) {
        this.hideTicks = hideTicks;
    }

    private void collideWithWall(Level world) {
        if (!world.isClientSide) {
            AABB bb = this.getBoundingBox();

            for (Direction dir : Direction.Plane.HORIZONTAL) {
                AABB movedBox = bb.move(dir.getStepX() * 0.1, 0, dir.getStepZ() * 0.1);
                BlockPos min = BlockPos.containing(movedBox.minX, movedBox.minY, movedBox.minZ);
                BlockPos max = BlockPos.containing(movedBox.maxX, movedBox.maxY, movedBox.maxZ);
                BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

                for (int x = min.getX(); x <= max.getX(); x++) {
                    for (int y = min.getY(); y <= max.getY(); y++) {
                        for (int z = min.getZ(); z <= max.getZ(); z++) {
                            pos.set(x, y, z);
                            BlockState state = world.getBlockState(pos);
                            VoxelShape shape = state.getCollisionShape(world, pos);

                            if (!shape.isEmpty()) {
                                AABB shapeBox = shape.bounds().move(pos);

                                if (shapeBox.intersects(movedBox)) {
                                    Vec3 motion = this.slidingDirection;
                                    double maxHeight = shape.max(Direction.Axis.Y);
                                    double newX = motion.x;
                                    double newZ = motion.z;

                                    if (dir.getAxis() == Direction.Axis.X)
                                        newX = -motion.x;
                                    if (dir.getAxis() == Direction.Axis.Z)
                                        newZ = -motion.z;

                                    if (maxHeight <= 0.5)
                                        continue;

                                    this.setDeltaMovement(new Vec3(newX, motion.y, newZ));
                                    this.slidingDirection = new Vec3(newX, motion.y, newZ);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public @NotNull AABB makeBoundingBox() {
        return super.makeBoundingBox();
    }
}