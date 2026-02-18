package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.entities.ai.goals.GoombaRideGoal;
import com.wenxin2.marioverse.entities.ai.goals.GoombaSitGoal;
import com.wenxin2.marioverse.entities.ai.goals.GoombaSleepGoal;
import com.wenxin2.marioverse.entities.ai.goals.LookAtEntityTagGoal;
import com.wenxin2.marioverse.entities.ai.goals.NearestAttackableTagGoal;
import com.wenxin2.marioverse.entities.ai.goals.ShootBouncingFireballGoal;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;

public class FireGoombaEntity extends GoombaEntity implements GeoEntity {
    public FireGoombaEntity(EntityType<? extends FireGoombaEntity> type, Level world) {
        super(type, world);
        this.setPathfindingMalus(PathType.DOOR_OPEN, 1.0F);
        this.setPathfindingMalus(PathType.LAVA, 2.0F);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.xpReward = 8;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ShootBouncingFireballGoal(this,
                ConfigRegistry.MAX_MOB_FIREBALLS.get(), 2, false));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.6D, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.4D));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new LookAtEntityTagGoal(this, TagRegistry.FIRE_GOOMBA_CAN_ATTACK, 8.0F, 1.0F));
        this.goalSelector.addGoal(6, new GoombaSitGoal(this, 0.1F, 1200, 3000, 300));
        this.goalSelector.addGoal(7, new GoombaSleepGoal(this, 0.05F, 2400, 6000));
        this.goalSelector.addGoal(8, new GoombaRideGoal(this, 0.01F));
        this.targetSelector.addGoal(0, new NearestAttackableTagGoal(this, TagRegistry.FIRE_GOOMBA_CAN_ATTACK, true));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
    }

    public static boolean checkFireGoombaSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor serverWorld,
                                                    MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return !serverWorld.getBlockState(pos.below()).is(Blocks.NETHER_WART_BLOCK);
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        return new GroundPathNavigation(this, world);
    }

    @Override
    public boolean isPushedByFluid() {
        return true;
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isControlledByLocalInstance() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.4));
        } else {
            super.travel(travelVector);
        }
    }
}
