package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class DamageTypeRegistry {
    public static final ResourceKey<DamageType> FIREBALL =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "fireball"));
    public static final ResourceKey<DamageType> PLAYER_FIREBALL =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "player_fireball"));

    public static final ResourceKey<DamageType> PIRANHA_CHOMP =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "piranha_chomp"));
    public static final ResourceKey<DamageType> PLAYER_PIRANHA_CHOMP =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "player_piranha_chomp"));

    public static final ResourceKey<DamageType> STOMP =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "stomp"));
    public static final ResourceKey<DamageType> PLAYER_STOMP =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "player_stomp"));

    public static final ResourceKey<DamageType> SUPER_STAR =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "super_star"));
    public static final ResourceKey<DamageType> PLAYER_SUPER_STAR =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "player_super_star"));

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

    public static DamageSource superStar(@Nullable Entity damagedEntity, @Nullable Entity attackingEntity) {
        if (attackingEntity != null && damagedEntity != null) {
            return new DamageSource(attackingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(PLAYER_SUPER_STAR), damagedEntity, attackingEntity);
        } else if (attackingEntity != null) {
            return new DamageSource(attackingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(SUPER_STAR), null, attackingEntity);
        } else return null;
    }

    public static DamageSource piranhaChomp(@Nullable Entity damagedEntity, @Nullable Entity attackingEntity) {
        if (attackingEntity != null && damagedEntity != null) {
            return new DamageSource(attackingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(PLAYER_PIRANHA_CHOMP), damagedEntity, attackingEntity);
        } else if (attackingEntity != null) {
            return new DamageSource(attackingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(PIRANHA_CHOMP), null, attackingEntity);
        } else return null;
    }

    public static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(FIREBALL, new DamageType(Marioverse.MOD_ID + ".fireball",
                DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1f, DamageEffects.BURNING));
        context.register(PLAYER_FIREBALL, new DamageType(Marioverse.MOD_ID + ".fireball.player",
                DamageScaling.ALWAYS, 0.1f, DamageEffects.BURNING));

        context.register(PIRANHA_CHOMP, new DamageType(Marioverse.MOD_ID + ".piranha_chomp",
                DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.5f, DamageEffects.THORNS));
        context.register(PLAYER_PIRANHA_CHOMP, new DamageType(Marioverse.MOD_ID + ".piranha_chomp.player",
                DamageScaling.ALWAYS, 0.5f, DamageEffects.THORNS));

        context.register(STOMP, new DamageType(Marioverse.MOD_ID + ".stomp",
                DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.5f, DamageEffects.HURT));
        context.register(PLAYER_STOMP, new DamageType(Marioverse.MOD_ID + ".stomp.player",
                DamageScaling.ALWAYS, 0.5f, DamageEffects.HURT));

        context.register(SUPER_STAR, new DamageType(Marioverse.MOD_ID + ".super_star",
                DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.75f, DamageEffects.HURT));
        context.register(PLAYER_SUPER_STAR, new DamageType(Marioverse.MOD_ID + ".super_star.player",
                DamageScaling.ALWAYS, 0.75f, DamageEffects.HURT));
    }
}
