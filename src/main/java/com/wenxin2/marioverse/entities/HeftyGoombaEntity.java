package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.entities.ai.controls.AmphibiousMoveControl;
import com.wenxin2.marioverse.entities.ai.goals.GoombaRideGoombaGoal;
import com.wenxin2.marioverse.entities.ai.goals.GoombaSitGoal;
import com.wenxin2.marioverse.entities.ai.goals.GoombaSleepGoal;
import com.wenxin2.marioverse.entities.ai.goals.NearestAttackableTagGoal;
import com.wenxin2.marioverse.init.EntityRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import software.bernie.geckolib.animatable.GeoEntity;

public class HeftyGoombaEntity extends GoombaEntity implements GeoEntity {
    public HeftyGoombaEntity(EntityType<? extends HeftyGoombaEntity> type, Level world) {
        super(type, world);
        this.setPathfindingMalus(PathType.DOOR_OPEN, 1.0F);
        this.setPathfindingMalus(PathType.WATER, 2.0F);
        this.moveControl = new AmphibiousMoveControl(this, 85, 10, 0.6F, 1.0F, true);
    }

    @Override
    protected int getBaseExperienceReward() {
        return 1 + this.level().random.nextInt(3);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.HEFTY_GOOMBA_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.HEFTY_GOOMBA_STOMP.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundRegistry.HEFTY_GOOMBA_STEP.get(), 1.0F, 1.0F);
    }

    @Override
    protected SoundEvent getBumpSound() {
        return SoundRegistry.HEFTY_GOOMBA_BUMP.get();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new RandomStrollGoal(this, 0.4D));
        this.goalSelector.addGoal(1, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(2, new GoombaSitGoal(this, 100, 1200, 3000, 300));
        this.goalSelector.addGoal(3, new GoombaSleepGoal(this, 25, 2400, 6000));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 0.6D, true));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new GoombaRideGoombaGoal(this, 0.001F));
        this.targetSelector.addGoal(0, new NearestAttackableTagGoal(this, TagRegistry.HEFTY_GOOMBA_CAN_ATTACK, true));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
    }

    @Override
    public void remove(Entity.RemovalReason removalReason) {
        if (!this.level().isClientSide && this.isDeadOrDying()) {
            Component component = this.getCustomName();
            boolean flag = this.isNoAi();
            float width = this.getDimensions(this.getPose()).width() / 2.0F;
            int amtSpawned = 2 + this.random.nextInt(2);
            var spawnedGoombas = new java.util.ArrayList<Mob>();

            for (int i = 0; i < amtSpawned; i++) {
                double angle = this.random.nextDouble() * Math.PI * 2;
                double xOffset = Math.cos(angle) * width;
                double zOffset = Math.sin(angle) * width;
                double upwardMotion = 0.2 + this.random.nextDouble() * 0.2;

                GoombaEntity goomba = EntityRegistry.GOOMBA.get().create(this.level());
                if (goomba != null) {
                    if (this.isPersistenceRequired()) {
                        goomba.setPersistenceRequired();
                    }

                    goomba.setCustomName(component);
                    goomba.setNoAi(flag);
                    goomba.setInvulnerable(this.isInvulnerable());
                    goomba.moveTo(this.getX() + xOffset, this.getY() + 0.5, this.getZ() + zOffset, this.random.nextFloat() * 360.0F, 0.0F);
                    goomba.setDeltaMovement(xOffset * 0.5, upwardMotion, zOffset * 0.5);
                    goomba.move(MoverType.SELF, goomba.getDeltaMovement());

                    spawnedGoombas.add(goomba);
                }
            }

            if (!net.neoforged.neoforge.event.EventHooks.onMobSplit(this, spawnedGoombas).isCanceled()) {
                spawnedGoombas.forEach(this.level()::addFreshEntity);
            }
        }
        super.remove(removalReason);
    }
}
