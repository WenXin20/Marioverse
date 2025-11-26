package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.entities.ai.goals.LookAtTagGoal;
import com.wenxin2.marioverse.entities.ai.goals.NearestAttackableTagGoal;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PokeyBodyEntity extends PokeyEntity implements GeoEntity, NeutralMob {
    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("pokey_body.idle");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    @Nullable private UUID persistentAngerTarget;
    private int remainingPersistentAngerTime;
    public int deathCountdown = 0;
    private boolean initialized = false;
    private boolean createdStack = false;

    public PokeyBodyEntity(EntityType<? extends PokeyBodyEntity> type, Level world) {
        super(type, world);
        this.setPathfindingMalus(PathType.DOOR_OPEN, 1.0F);
        this.xpReward = 2;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(2, new LookAtTagGoal(this, TagRegistry.GREEN_KOOPA_TROOPA_CAN_ATTACK, 8.0F, 1.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(0, new NearestAttackableTagGoal(this, TagRegistry.GREEN_KOOPA_TROOPA_CAN_ATTACK, true)); // TODO
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
//        controllers.add(new AnimationController<>(this, "Idle", 5, this::animController));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isPassenger())
            this.getLookControl().tick();

        if (this.deathCountdown > 0)
            this.deathCountdown--;

        if (this.getHeadSegment() == null && this.deathCountdown == 0)
            this.kill();

        if (this.isPassenger() && this.getVehicle() instanceof PokeyEntity) {
            LivingEntity bottom = this.getBottomSegment();

            this.setYRot(bottom.getYRot());
            this.yRotO = bottom.yRotO;

            this.setYHeadRot(bottom.getYHeadRot());
            this.yHeadRotO = bottom.yHeadRotO;
        }
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.remainingPersistentAngerTime;
    }

    @Override
    public void setRemainingPersistentAngerTime(int angerTime) {
        this.remainingPersistentAngerTime = angerTime;
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID angerTarget) {
        this.persistentAngerTarget = angerTarget;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverWorld, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData groupData) {
        this.spawnPokeyStack(serverWorld.getLevel(), difficulty);
        return super.finalizeSpawn(serverWorld, difficulty, spawnType, groupData);
    }

    private void spawnPokeyStack(ServerLevel serverWorld, DifficultyInstance difficulty) {
        PokeyEntity head = EntityRegistry.POKEY.get().create(serverWorld);

        RandomSource random = serverWorld.getRandom();
        int bodyCount = random.nextInt(10); // TODO: Add config
        Mob currentTop = this;

        for (int i = 0; i < bodyCount; i++) {
            UUID uuid;
            do uuid = UUID.randomUUID();
            while (serverWorld.getEntity(uuid) != null);

            PokeyBodyEntity body = EntityRegistry.POKEY_BODY.get().create(serverWorld);
            if (body == null)
                continue;

            body.setUUID(uuid);
            body.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
            serverWorld.addFreshEntity(body);
            body.startRiding(currentTop, true);
            body.deathCountdown = 2;
            currentTop = body;
        }

        if (head != null) {
            UUID uuid;
            do uuid = UUID.randomUUID();
            while (serverWorld.getLevel().getEntity(uuid) != null);

            head.setUUID(uuid);
            head.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
            head.finalizeSpawn(serverWorld, difficulty, MobSpawnType.MOB_SUMMONED, null);
            serverWorld.addFreshEntity(head);
            head.startRiding(currentTop, true);
        }
    }
}