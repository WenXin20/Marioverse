package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.registries.ParticleRegistry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;

public class RedKoopaShellEntity extends KoopaShellEntity implements CrackableEntity, GeoEntity, TraceableEntity {

    public RedKoopaShellEntity(EntityType<? extends RedKoopaShellEntity> type, Level world) {
        super(type, world);
    }

    @NotNull
    @Override
    public SimpleParticleType getShatterParticle() {
        return ParticleRegistry.RED_KOOPA_SHELL_SHATTER.get();
    }
}