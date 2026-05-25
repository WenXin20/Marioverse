package com.wenxin2.marioverse.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class DamageSourceRegistry {
    public static DamageSource bonked(@Nullable Entity damagedEntity, @Nullable Entity attackingEntity) {
        if (damagedEntity != null && attackingEntity != null) {
            return new DamageSource(attackingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.PLAYER_BONKED), damagedEntity, attackingEntity);
        } else if (attackingEntity != null) {
            return new DamageSource(attackingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.BONKED), null, attackingEntity);
        } else return null;
    }

    public static DamageSource cheepCheepBite(@Nullable Entity attackingEntity) {
        if (attackingEntity != null) {
            return new DamageSource(attackingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.CHEEP_CHEEP_BITE), attackingEntity);
        } else return null;
    }

    public static DamageSource deepCheepBite(@Nullable Entity attackingEntity) {
        if (attackingEntity != null) {
            return new DamageSource(attackingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.DEEP_CHEEP_BITE), attackingEntity);
        } else return null;
    }

    public static DamageSource eepCheepBite(@Nullable Entity attackingEntity) {
        if (attackingEntity != null) {
            return new DamageSource(attackingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.EEP_CHEEP_BITE), attackingEntity);
        } else return null;
    }

    public static DamageSource spinyCheepCheepBite(@Nullable Entity attackingEntity) {
        if (attackingEntity != null) {
            return new DamageSource(attackingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.SPINY_CHEEP_CHEEP_BITE), attackingEntity);
        } else return null;
    }

    public static DamageSource defeated(@Nullable Entity damagedEntity, @Nullable Entity attackingEntity) {
        if (damagedEntity != null && attackingEntity != null) {
            return new DamageSource(attackingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.PLAYER_MINI_GOOMBA_DEFEATED), damagedEntity, attackingEntity);
        } else if (attackingEntity != null) {
            return new DamageSource(attackingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.MINI_GOOMBA_DEFEATED), null, attackingEntity);
        } else return null;
    }

    public static DamageSource fireball(@Nullable Entity projectile, @Nullable Entity shooter) {
        if (shooter != null && projectile != null) {
            return new DamageSource(shooter.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.PLAYER_FIREBALL), projectile, shooter);
        } else if (shooter != null) {
            return new DamageSource(shooter.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.FIREBALL), null, shooter);
        } else return null;
    }

    public static DamageSource iceBall(@Nullable Entity projectile, @Nullable Entity shooter) {
        if (shooter != null && projectile != null) {
            return new DamageSource(shooter.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.PLAYER_ICE_BALL), projectile, shooter);
        } else if (shooter != null) {
            return new DamageSource(shooter.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.ICE_BALL), null, shooter);
        } else return null;
    }

    public static DamageSource iceCubeCrushed(@Nullable Entity damagedEntity, @Nullable Entity attackingEntity) {
        if (attackingEntity != null && damagedEntity != null) {
            return new DamageSource(attackingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.PLAYER_ICE_CUBE_CRUSHED), damagedEntity, attackingEntity);
        } else if (attackingEntity != null) {
            return new DamageSource(attackingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.ICE_CUBE_CRUSHED), null, attackingEntity);
        } else return null;
    }

    public static DamageSource instakill(@Nullable Entity damagedEntity) {
        if (damagedEntity != null) {
            return new DamageSource(damagedEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.INSTAKILL));
        } else return null;
    }

    public static DamageSource largeSnowball(@Nullable Entity projectile, @Nullable Entity shooter) {
        if (shooter != null && projectile != null) {
            return new DamageSource(shooter.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.PLAYER_LARGE_SNOWBALL), projectile, shooter);
        } else if (shooter != null) {
            return new DamageSource(shooter.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.LARGE_SNOWBALL), null, shooter);
        } else return null;
    }

    public static DamageSource light(@Nullable Entity damagedEntity, @Nullable Entity attackingEntity) {
        if (attackingEntity != null && damagedEntity != null) {
            return new DamageSource(attackingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.LIGHT), damagedEntity, attackingEntity);
        } else if (attackingEntity != null) {
            return new DamageSource(attackingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.LIGHT), null, attackingEntity);
        } else return null;
    }

    public static DamageSource megaMushroom(@Nullable Entity damagedEntity, @Nullable Entity attackingEntity) {
        if (damagedEntity != null && attackingEntity != null) {
            return new DamageSource(attackingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.PLAYER_MEGA_MUSHROOM_SQUASH), damagedEntity, attackingEntity);
        } else if (attackingEntity != null) {
            return new DamageSource(attackingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.MEGA_MUSHROOM_SQUASH), null, attackingEntity);
        } else return null;
    }

    public static DamageSource shrapnel(@Nullable Entity damagedEntity, @Nullable Entity attackingEntity) {
        if (damagedEntity != null && attackingEntity != null) {
            return new DamageSource(attackingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.PLAYER_SHRAPNEL), damagedEntity, attackingEntity);
        } else if (attackingEntity != null) {
            return new DamageSource(attackingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.SHRAPNEL), null, attackingEntity);
        } else return null;
    }

    public static DamageSource spiked(@Nullable Entity damagedEntity) {
        if (damagedEntity != null) {
            return new DamageSource(damagedEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.SPIKED));
        } else return null;
    }

    public static DamageSource spinningShell(@Nullable Entity damagedEntity, @Nullable Entity attackingEntity) {
        if (damagedEntity != null && attackingEntity != null) {
            return new DamageSource(attackingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.PLAYER_SPINNING_SHELL), damagedEntity, attackingEntity);
        } else if (attackingEntity != null) {
            return new DamageSource(attackingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.SPINNING_SHELL), null, attackingEntity);
        } else return null;
    }

    public static DamageSource stomp(@Nullable Entity entity, @Nullable Entity stomper) {
        if (stomper != null && entity != null) {
            return new DamageSource(stomper.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.PLAYER_STOMP), entity, stomper);
        } else if (stomper != null) {
            return new DamageSource(stomper.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.STOMP), null, stomper);
        } else return null;
    }

    public static DamageSource superStar(@Nullable Entity damagedEntity, @Nullable Entity attackingEntity) {
        if (attackingEntity != null && damagedEntity != null) {
            return new DamageSource(attackingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.PLAYER_SUPER_STAR), damagedEntity, attackingEntity);
        } else if (attackingEntity != null) {
            return new DamageSource(attackingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.SUPER_STAR), null, attackingEntity);
        } else return null;
    }

    public static DamageSource piranhaChomp(@Nullable Entity damagedEntity, @Nullable Entity attackingEntity) {
        if (attackingEntity != null && damagedEntity != null) {
            return new DamageSource(attackingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.PLAYER_PIRANHA_CHOMP), damagedEntity, attackingEntity);
        } else if (attackingEntity != null) {
            return new DamageSource(attackingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.PIRANHA_CHOMP), null, attackingEntity);
        } else return null;
    }

    public static DamageSource pokeyThorns(@Nullable Entity attackingEntity) {
        if (attackingEntity != null) {
            return new DamageSource(attackingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.POKEY_THORNS), attackingEntity);
        } else return null;
    }

    public static DamageSource snowPokeyThorns(@Nullable Entity attackingEntity) {
        if (attackingEntity != null) {
            return new DamageSource(attackingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypeRegistry.SNOW_POKEY_THORNS), attackingEntity);
        } else return null;
    }
}
