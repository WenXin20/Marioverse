package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.ai.controls.AmphibiousMoveControl;
import com.wenxin2.marioverse.entities.ai.goals.GoombaRideGoombaGoal;
import java.util.List;
import java.util.Random;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoEntity;

public class MiniGoombaEntity extends GoombaEntity implements GeoEntity {
    private LivingEntity stuckTo;
    private double currentX, currentY, currentZ;
    private double targetX, targetY, targetZ;
    private final Random random = new Random();
    private static final ResourceLocation SLOWDOWN_MODIFIER_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "mini_goomba_slow");
    private static final double SLOWDOWN_FACTOR = 0.1;
    private static final double MOVE_SPEED = 0.075;
    private static final double POSITION_THRESHOLD = 0.05;

    public MiniGoombaEntity(EntityType<? extends Monster> type, Level world) {
        super(type, world);
        this.setPathfindingMalus(PathType.DOOR_OPEN, 1.0F);
        this.setPathfindingMalus(PathType.WATER, 2.0F);
        this.moveControl = new AmphibiousMoveControl(this, 85, 10, 0.6F, 1.0F, true);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new RandomStrollGoal(this, 0.4D));
        this.goalSelector.addGoal(1, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 0.6D, true));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new GoombaRideGoombaGoal(this, 0.001F));
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
    }

    @Override
    public void tick() {
        super.tick();
        this.checkForCollisions();

        if (stuckTo != null && stuckTo.getDeltaMovement().y > 0) {
            removeSpeedModifier(stuckTo);
            stuckTo = null;
        } else if (stuckTo != null && this.isDeadOrDying()) {
            removeSpeedModifier(stuckTo);
            stuckTo = null;
        } else if (stuckTo != null && stuckTo.isAlive() && this.getY() >= stuckTo.getY()
                && this.isAlive() && !(stuckTo.getDeltaMovement().y > 0)) {
            double distanceToTarget = distanceToTarget();
            if (distanceToTarget < POSITION_THRESHOLD) {
                generateRandomOffsets(stuckTo);
            }
            moveTowardsTarget();
            this.resetFallDistance();
            this.setPos(
                    stuckTo.getX() + currentX,
                    stuckTo.getY() + currentY,
                    stuckTo.getZ() + currentZ
            );
        }
    }

    public void checkForCollisions() {
        AABB boundingBox = this.getBoundingBox().inflate(0.1);
        List<Entity> entities = this.level().getEntities(this, boundingBox, entity -> entity != this);

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity livingEntity)
                stickToEntity(livingEntity);
            break;
        }
    }

    public void stickToEntity(LivingEntity entity) {
        this.stuckTo = entity;
        generateRandomOffsets(stuckTo);
        addSpeedModifier(stuckTo);
    }

    private void addSpeedModifier(LivingEntity livingEntity) {
        AttributeInstance speedAttribute = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null && !speedAttribute.hasModifier(SLOWDOWN_MODIFIER_RESOURCE)) {
            AttributeModifier slowdownModifier = new AttributeModifier(
                    SLOWDOWN_MODIFIER_RESOURCE, -SLOWDOWN_FACTOR,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            );
            speedAttribute.addTransientModifier(slowdownModifier);
        }
    }

    private void removeSpeedModifier(LivingEntity livingEntity) {
        AttributeInstance speedAttribute = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null && speedAttribute.hasModifier(SLOWDOWN_MODIFIER_RESOURCE)) {
            speedAttribute.removeModifier(SLOWDOWN_MODIFIER_RESOURCE);
        }
    }

    private double randomOffset(double min, double max) {
        return Mth.lerp(random.nextDouble(), min, max); // Mth.lerp for linear interpolation
    }

    private void generateRandomOffsets(Entity entity) {
        double width = entity.getBbWidth() + 0.8; // Width of the player's hitbox
        double height = entity.getBbHeight() + 0.8; // Height of the player's hitbox

        // Choose a random side (top, bottom, or any of the sides)
        int side = random.nextInt(6); // 0-5 for the 6 sides of the hitbox

        switch (side) {
            case 0: // Top side
                targetX = randomOffset(-width / 2, width / 2);
                targetY = height;
                targetZ = randomOffset(-width / 2, width / 2);
                break;
            case 1: // Bottom side
                targetX = randomOffset(-width / 2, width / 2);
                targetY = 0.5;
                targetZ = randomOffset(-width / 2, width / 2);
                break;
            case 2: // Front side (positive Z)
                targetX = randomOffset(-width / 2, width / 2);
                targetY = randomOffset(0, height);
                targetZ = width / 2;
                break;
            case 3: // Back side (negative Z)
                targetX = randomOffset(-width / 2, width / 2);
                targetY = randomOffset(0, height);
                targetZ = -width / 2;
                break;
            case 4: // Right side (positive X)
                targetX = width / 2;
                targetY = randomOffset(0, height);
                targetZ = randomOffset(-width / 2, width / 2);
                break;
            case 5: // Left side (negative X)
                targetX = -width / 2;
                targetY = randomOffset(0, height);
                targetZ = randomOffset(-width / 2, width / 2);
                break;
        }
    }

    private double distanceToTarget() {
        return Math.sqrt(
                (targetX - currentX) * (targetX - currentX) +
                        (targetY - currentY) * (targetY - currentY) +
                        (targetZ - currentZ) * (targetZ - currentZ)
        );
    }

    private void moveTowardsTarget() {
        currentX = moveToward(currentX, targetX, MOVE_SPEED);
        currentY = moveToward(currentY, targetY, MOVE_SPEED);
        currentZ = moveToward(currentZ, targetZ, MOVE_SPEED);
    }

    private double moveToward(double current, double target, double speed) {
        if (current < target)
            return Math.min(current + speed, target);
        else return Math.max(current - speed, target);
    }
}
