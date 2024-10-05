package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class DamageSourceRegistry {
    public static final ResourceKey<DamageType> FIREBALL =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "fireball"));
    public static final ResourceKey<DamageType> PLAYER_FIREBALL =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "player_fireball"));

    public static final ResourceKey<DamageType> STOMP =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "stomp"));
    public static final ResourceKey<DamageType> PLAYER_STOMP =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "player_stomp"));

    public static DamageSource fireball(@Nullable Entity projectile, @Nullable Entity shooter) {
        if (shooter != null && projectile != null) {
            return new DamageSource(shooter.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(PLAYER_FIREBALL), projectile, shooter);
        } else if (shooter != null) {
            return new DamageSource(shooter.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(FIREBALL), null, shooter);
        } else return null;
    }

    public static DamageSource stomp(@Nullable Entity entity, @Nullable Entity stomper) {
        if (stomper != null && entity != null) {
            return new DamageSource(stomper.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(PLAYER_STOMP), entity, stomper);
        } else if (stomper != null) {
            return new DamageSource(stomper.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(STOMP), null, stomper);
        } else return null;
    }
}
