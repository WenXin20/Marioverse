package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.registries.DamageTypeRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class DamageTypeTagsGen extends DamageTypeTagsProvider {
    public DamageTypeTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Marioverse.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(DamageTypeTags.BYPASSES_ARMOR)
                .add(DamageTypeRegistry.LIGHT)
                .add(DamageTypeRegistry.SPIKED);

        tag(DamageTypeTags.IS_FIRE)
                .add(DamageTypeRegistry.FIREBALL)
                .add(DamageTypeRegistry.PLAYER_FIREBALL);

        tag(DamageTypeTags.NO_KNOCKBACK)
                .add(DamageTypeRegistry.SPIKED)
                .add(DamageTypeRegistry.STOMP)
                .add(DamageTypeRegistry.PLAYER_STOMP);

        tag(TagRegistry.BYPASSES_BOO_INVULNERABILITY)
                .addTag(TagRegistry.IS_SUPER_STAR)
                .addTag(DamageTypeTags.BYPASSES_INVULNERABILITY)
                .add(DamageTypeRegistry.LIGHT);

        tag(TagRegistry.BYPASSES_SUPER_STAR)
                .addTag(DamageTypeTags.BYPASSES_INVULNERABILITY)
                .add(DamageTypes.DROWN)
                .add(DamageTypes.FLY_INTO_WALL)
                .add(DamageTypes.LAVA)
                .add(DamageTypes.OUTSIDE_BORDER)
                .add(DamageTypes.SONIC_BOOM)
                .add(DamageTypes.STARVE);

        tag(TagRegistry.HIDES_KOOPA_TROOPA)
                .addTag(TagRegistry.IS_BONKED)
                .addTag(TagRegistry.IS_SHRAPNEL)
                .addTag(TagRegistry.IS_STOMP);

        tag(TagRegistry.FLIPS_KOOPA_SHELL)
                .addTag(TagRegistry.IS_BONKED)
                .addTag(TagRegistry.IS_SHRAPNEL);

        tag(TagRegistry.STOPS_KOOPA_SHELL)
                .addTag(TagRegistry.IS_BONKED)
                .addTag(TagRegistry.IS_SHRAPNEL)
                .addTag(TagRegistry.IS_STOMP);

        tag(TagRegistry.IS_BONKED)
                .add(DamageTypeRegistry.BONKED)
                .add(DamageTypeRegistry.PLAYER_BONKED);

        tag(TagRegistry.IS_DEFEATED)
                .add(DamageTypeRegistry.MINI_GOOMBA_DEFEATED)
                .add(DamageTypeRegistry.PLAYER_MINI_GOOMBA_DEFEATED);

        tag(TagRegistry.IS_FIREBALL)
                .add(DamageTypeRegistry.FIREBALL)
                .add(DamageTypeRegistry.PLAYER_FIREBALL);

        tag(TagRegistry.IS_ICE_BALL)
                .add(DamageTypeRegistry.ICE_BALL)
                .add(DamageTypeRegistry.PLAYER_ICE_BALL);

        tag(TagRegistry.IS_ICE_CUBE_CRUSHED)
                .add(DamageTypeRegistry.ICE_CUBE_CRUSHED)
                .add(DamageTypeRegistry.PLAYER_ICE_CUBE_CRUSHED);

        tag(TagRegistry.IS_PIRANHA_CHOMP)
                .add(DamageTypeRegistry.PIRANHA_CHOMP)
                .add(DamageTypeRegistry.PLAYER_PIRANHA_CHOMP);

        tag(TagRegistry.IS_SHRAPNEL)
                .add(DamageTypeRegistry.SHRAPNEL)
                .add(DamageTypeRegistry.PLAYER_SHRAPNEL);

        tag(TagRegistry.IS_SPINNING_SHELL)
                .add(DamageTypeRegistry.SPINNING_SHELL)
                .add(DamageTypeRegistry.PLAYER_SPINNING_SHELL);

        tag(TagRegistry.IS_STOMP)
                .add(DamageTypeRegistry.STOMP)
                .add(DamageTypeRegistry.PLAYER_STOMP);

        tag(TagRegistry.IS_SUPER_STAR)
                .add(DamageTypeRegistry.SUPER_STAR)
                .add(DamageTypeRegistry.PLAYER_SUPER_STAR);

        tag(TagRegistry.SHIELD_BLOCKS)
                .addTag(TagRegistry.IS_FIREBALL)
                .addTag(TagRegistry.IS_ICE_BALL)
                .addTag(TagRegistry.IS_ICE_CUBE_CRUSHED)
                .addTag(TagRegistry.IS_PIRANHA_CHOMP)
                .addTag(TagRegistry.IS_SPINNING_SHELL);

        tag(TagRegistry.PREVENTS_DRY_BONES_RESURRECTION)
                .addTag(TagRegistry.IS_ICE_BALL)
                .addTag(TagRegistry.IS_ICE_CUBE_CRUSHED)
                .addTag(TagRegistry.IS_SPINNING_SHELL)
                .addTag(TagRegistry.IS_SUPER_STAR)
                .addTag(DamageTypeTags.BYPASSES_INVULNERABILITY)
                .addTag(DamageTypeTags.IS_EXPLOSION)
                .addTag(DamageTypeTags.IS_FREEZING)
                .add(DamageTypes.DROWN);
    }
}