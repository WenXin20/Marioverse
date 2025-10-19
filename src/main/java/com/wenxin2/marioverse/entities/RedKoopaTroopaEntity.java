package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.entities.ai.goals.LookAtTagGoal;
import com.wenxin2.marioverse.entities.ai.goals.NearestAttackableTagGoal;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.NotNull;

public class RedKoopaTroopaEntity extends KoopaTroopaEntity {
    public RedKoopaTroopaEntity(EntityType<? extends RedKoopaTroopaEntity> type, Level world) {
        super(type, world);
        this.setPathfindingMalus(PathType.DOOR_OPEN, 1.0F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RandomStrollGoal(this, 0.4D));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.6D, false));
        this.goalSelector.addGoal(3, new LookAtTagGoal(this, TagRegistry.RED_KOOPA_TROOPA_CAN_ATTACK, 8.0F, 1.0F));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(0, new NearestAttackableTagGoal(this, TagRegistry.RED_KOOPA_TROOPA_CAN_ATTACK, true));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
    }

    @NotNull
    @Override
    public KoopaShellEntity getKoopaShellEntity() {
        return new RedKoopaShellEntity(EntityRegistry.RED_KOOPA_SHELL.get(), this.level());
    }

    @NotNull
    @Override
    public Item getKoopaShoes() {
        return ItemRegistry.RED_KOOPA_SHOES.get();
    }

    @NotNull
    @Override
    public Integer getHideDuration() {
        return ConfigRegistry.RED_KOOPA_TROOPA_HIDE_DURATION.get();
    }
}
