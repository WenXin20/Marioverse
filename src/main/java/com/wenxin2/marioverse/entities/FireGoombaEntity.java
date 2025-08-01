package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.entities.ai.controls.AmphibiousMoveControl;
import com.wenxin2.marioverse.entities.ai.goals.GoombaRideGoal;
import com.wenxin2.marioverse.entities.ai.goals.GoombaSitGoal;
import com.wenxin2.marioverse.entities.ai.goals.GoombaSleepGoal;
import com.wenxin2.marioverse.entities.ai.goals.NearestAttackableTagGoal;
import com.wenxin2.marioverse.entities.ai.goals.ShootBouncingFireballGoal;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathType;
import software.bernie.geckolib.animatable.GeoEntity;

public class FireGoombaEntity extends GoombaEntity implements GeoEntity {
    public FireGoombaEntity(EntityType<? extends FireGoombaEntity> type, Level world) {
        super(type, world);
        this.setPathfindingMalus(PathType.DOOR_OPEN, 1.0F);
        this.setPathfindingMalus(PathType.LAVA, 2.0F);
        this.moveControl = new AmphibiousMoveControl(this, 85, 10, 0.6F, 1.0F, true);
    }

    @Override
    protected int getBaseExperienceReward() {
        return 1 + this.level().random.nextInt(3);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new ShootBouncingFireballGoal(this, ConfigRegistry.MAX_MOB_FIREBALLS.get(),
                2, false));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.6D, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.4D));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new GoombaSitGoal(this, 0.1F, 1200, 3000, 300));
        this.goalSelector.addGoal(6, new GoombaSleepGoal(this, 0.05F, 2400, 6000));
        this.goalSelector.addGoal(7, new GoombaRideGoal(this, 0.01F));
        this.targetSelector.addGoal(0, new NearestAttackableTagGoal(this, TagRegistry.FIRE_GOOMBA_CAN_ATTACK, true));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
    }

    public static boolean checkFireGoombaSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor serverWorld,
                                                    MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return !serverWorld.getBlockState(pos.below()).is(Blocks.NETHER_WART_BLOCK);
    }
}
