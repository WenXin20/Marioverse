package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.entities.ai.goals.NearestAttackableTagGoal;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.NotNull;

public class GoldKoopaTroopaEntity extends KoopaTroopaEntity {
    public GoldKoopaTroopaEntity(EntityType<? extends GoldKoopaTroopaEntity> type, Level world) {
        super(type, world);
        this.setPathfindingMalus(PathType.DOOR_OPEN, 1.0F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(0, new NearestAttackableTagGoal(this, this.getCanAttackTag(), true));
    }

    @Override
    public TagKey<EntityType<?>> getCanAttackTag() {
        return TagRegistry.GOLD_KOOPA_TROOPA_CAN_ATTACK;
    }

    @NotNull
    @Override
    public KoopaShellEntity getKoopaShellEntity() {
        return new GoldKoopaShellEntity(EntityRegistry.GOLD_KOOPA_SHELL.get(), this.level());
    }

    @NotNull
    @Override
    public Item getKoopaShoes() {
        return ItemRegistry.GOLDEN_KOOPA_SHOES.get();
    }

    @NotNull
    @Override
    public Integer getHideDuration() {
        return ConfigRegistry.GOLD_KOOPA_TROOPA_HIDE_DURATION.get();
    }
}
