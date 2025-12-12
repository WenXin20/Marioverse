package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;

public class DamageTypeRegistry extends DamageSources {

    public static final ResourceKey<DamageType> BONKED =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "bonked"));
    public static final ResourceKey<DamageType> PLAYER_BONKED =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "player_bonked"));

    public static final ResourceKey<DamageType> FIREBALL =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "fireball"));
    public static final ResourceKey<DamageType> PLAYER_FIREBALL =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "player_fireball"));

    public static final ResourceKey<DamageType> ICE_BALL =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "ice_ball"));
    public static final ResourceKey<DamageType> PLAYER_ICE_BALL =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "player_ice_ball"));

    public static final ResourceKey<DamageType> ICE_CUBE_CRUSHED =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "ice_cube_crushed"));
    public static final ResourceKey<DamageType> PLAYER_ICE_CUBE_CRUSHED =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "player_ice_cube_crushed"));

    public static final ResourceKey<DamageType> LIGHT =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "light"));

    public static final ResourceKey<DamageType> MINI_GOOMBA_DEFEATED =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "mini_goomba_defeated"));
    public static final ResourceKey<DamageType> PLAYER_MINI_GOOMBA_DEFEATED =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "player_mini_goomba_defeated"));

    public static final ResourceKey<DamageType> PIRANHA_CHOMP =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "piranha_chomp"));
    public static final ResourceKey<DamageType> PLAYER_PIRANHA_CHOMP =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "player_piranha_chomp"));

    public static final ResourceKey<DamageType> POKEY_THORNS =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "pokey_thorns"));

    public static final ResourceKey<DamageType> SHRAPNEL =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "shrapnel"));
    public static final ResourceKey<DamageType> PLAYER_SHRAPNEL =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "player_shrapnel"));

    public static final ResourceKey<DamageType> SNOW_POKEY_THORNS =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "snow_pokey_thorns"));

    public static final ResourceKey<DamageType> SPIKED =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "spiked"));

    public static final ResourceKey<DamageType> SPINNING_SHELL =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "spinning_shell"));
    public static final ResourceKey<DamageType> PLAYER_SPINNING_SHELL =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "player_spinning_shell"));

    public static final ResourceKey<DamageType> STOMP =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "stomp"));
    public static final ResourceKey<DamageType> PLAYER_STOMP =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "player_stomp"));

    public static final ResourceKey<DamageType> SUPER_STAR =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "super_star"));
    public static final ResourceKey<DamageType> PLAYER_SUPER_STAR =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "player_super_star"));

    public DamageTypeRegistry(RegistryAccess access) {
        super(access);
    }

    public static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(BONKED, new DamageType(Marioverse.MOD_ID + ".bonked",
                DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1f, DamageEffects.HURT));
        context.register(PLAYER_BONKED, new DamageType(Marioverse.MOD_ID + ".bonked.player",
                DamageScaling.ALWAYS, 0.1f, DamageEffects.HURT));

        context.register(FIREBALL, new DamageType(Marioverse.MOD_ID + ".fireball",
                DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1f, DamageEffects.BURNING));
        context.register(PLAYER_FIREBALL, new DamageType(Marioverse.MOD_ID + ".fireball.player",
                DamageScaling.ALWAYS, 0.1f, DamageEffects.BURNING));

        context.register(ICE_BALL, new DamageType(Marioverse.MOD_ID + ".ice_ball",
                DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1f, DamageEffects.FREEZING));
        context.register(PLAYER_ICE_BALL, new DamageType(Marioverse.MOD_ID + ".ice_ball.player",
                DamageScaling.ALWAYS, 0.1f, DamageEffects.FREEZING));

        context.register(ICE_CUBE_CRUSHED, new DamageType(Marioverse.MOD_ID + ".ice_cube_crushed",
                DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1f, DamageEffects.FREEZING));
        context.register(PLAYER_ICE_CUBE_CRUSHED, new DamageType(Marioverse.MOD_ID + ".ice_cube_crushed.player",
                DamageScaling.ALWAYS, 0.1f, DamageEffects.FREEZING));

        context.register(LIGHT, new DamageType(Marioverse.MOD_ID + ".light",
                DamageScaling.ALWAYS, 0.0f, DamageEffects.HURT));

        context.register(MINI_GOOMBA_DEFEATED, new DamageType(Marioverse.MOD_ID + ".mini_goomba_defeated",
                DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1f, DamageEffects.HURT));
        context.register(PLAYER_MINI_GOOMBA_DEFEATED, new DamageType(Marioverse.MOD_ID + ".mini_goomba_defeated.player",
                DamageScaling.ALWAYS, 0.1f, DamageEffects.HURT));

        context.register(PIRANHA_CHOMP, new DamageType(Marioverse.MOD_ID + ".piranha_chomp",
                DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.5f, DamageEffects.THORNS));
        context.register(PLAYER_PIRANHA_CHOMP, new DamageType(Marioverse.MOD_ID + ".piranha_chomp.player",
                DamageScaling.ALWAYS, 0.5f, DamageEffects.THORNS));

        context.register(POKEY_THORNS, new DamageType(Marioverse.MOD_ID + ".pokey_thorns",
                DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.5f, DamageEffects.THORNS));

        context.register(SHRAPNEL, new DamageType(Marioverse.MOD_ID + ".shrapnel",
                DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1f, DamageEffects.THORNS));
        context.register(PLAYER_SHRAPNEL, new DamageType(Marioverse.MOD_ID + ".shrapnel.player",
                DamageScaling.ALWAYS, 0.1f, DamageEffects.THORNS));

        context.register(SNOW_POKEY_THORNS, new DamageType(Marioverse.MOD_ID + ".snow_pokey_thorns",
                DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.5f, DamageEffects.THORNS));

        context.register(SPIKED, new DamageType(Marioverse.MOD_ID + ".spiked",
                DamageScaling.ALWAYS, 0.1f, DamageEffects.THORNS));

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
