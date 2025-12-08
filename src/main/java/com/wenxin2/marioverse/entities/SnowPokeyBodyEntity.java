package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;

public class SnowPokeyBodyEntity extends PokeyBodyEntity implements GeoEntity, NeutralMob {
    public SnowPokeyBodyEntity(EntityType<? extends SnowPokeyBodyEntity> type, Level world) {
        super(type, world);
        this.setPathfindingMalus(PathType.DOOR_OPEN, 1.0F);
        this.xpReward = 2;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SNOW_GOLEM_HURT;
    }

    @Override
    public TagKey<EntityType<?>> getCanAttackTag() {
        return TagRegistry.SNOW_POKEY_CAN_ATTACK;
    }

    @NotNull
    @Override
    public PokeyEntity getPokeyHeadEntity() {
        return new PokeyEntity(EntityRegistry.SNOW_POKEY.get(), this.level());
    }

    @NotNull
    @Override
    public PokeyBodyEntity getPokeyBodyEntity() {
        return new PokeyBodyEntity(EntityRegistry.SNOW_POKEY_BODY.get(), this.level());
    }

    @NotNull
    public Integer getMaxHeightConfig() {
        return ConfigRegistry.MAX_POKEY_HEIGHT.get(); //TODO
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getData(DataAttachmentRegistry.IS_BLOOMING))
            this.setData(DataAttachmentRegistry.IS_BLOOMING, false);
    }
}