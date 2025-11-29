package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.integration.CompatRegistry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class EntityTypeTagsGen extends EntityTypeTagsProvider {
    private static final ResourceLocation FEMALE_VILLAGER = ResourceLocation.fromNamespaceAndPath("mca", "female_villager");
    private static final ResourceLocation FEMALE_ZOMBIE_VILLAGER = ResourceLocation.fromNamespaceAndPath("mca", "female_zombie_villager");
    private static final ResourceLocation GUARD_VILLAGER = ResourceLocation.fromNamespaceAndPath("guardvillagers", "guard");
    private static final ResourceLocation HAT_STAND = ResourceLocation.fromNamespaceAndPath("supplementaries", "hat_stand");
    private static final ResourceLocation MALE_VILLAGER = ResourceLocation.fromNamespaceAndPath("mca", "male_villager");
    private static final ResourceLocation MALE_ZOMBIE_VILLAGER = ResourceLocation.fromNamespaceAndPath("mca", "male_zombie_villager");
    private static final ResourceLocation TEST_DUMMY = ResourceLocation.fromNamespaceAndPath("dummmmmmy", "target_dummy");

    public EntityTypeTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Marioverse.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(EntityTypeTags.CAN_BREATHE_UNDER_WATER)
                .addTag(TagRegistry.DRY_BONES_BONES)
                .addTag(TagRegistry.GOOMBA_ENTITIES)
                .addTag(TagRegistry.KOOPA_SHELL_ENTITIES)
                .addTag(TagRegistry.KOOPA_TROOPA_ENTITIES)
                .addTag(TagRegistry.POWER_UP_ENTITIES)
                .add(EntityRegistry.BOO.get())
                .add(EntityRegistry.DRY_BONES.get())
                .add(EntityRegistry.PIRANHA_PLANT.get());

        tag(EntityTypeTags.FALL_DAMAGE_IMMUNE)
                .addTag(TagRegistry.DRY_BONES_BONES)
                .addTag(TagRegistry.KOOPA_SHELL_ENTITIES)
                .addTag(TagRegistry.POWER_UP_ENTITIES)
                .add(EntityRegistry.BOO.get())
                .add(EntityRegistry.PIRANHA_PLANT.get());

        tag(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES)
                .add(EntityRegistry.BOO.get())
                .add(EntityRegistry.ICE_CUBE.get())
                .add(EntityRegistry.ICE_FLOWER.get());

        tag(EntityTypeTags.FROG_FOOD)
                .add(EntityRegistry.MINI_GOOMBA.get())
                .add(EntityRegistry.POKEY.get())
                .add(EntityRegistry.POKEY_BODY.get());

        tag(EntityTypeTags.IMPACT_PROJECTILES)
                .add(EntityRegistry.BOUNCING_FIREBALL.get())
                .add(EntityRegistry.BOUNCING_ICE_BALL.get());

        tag(EntityTypeTags.NOT_SCARY_FOR_PUFFERFISH)
                .addTag(TagRegistry.DRY_BONES_BONES)
                .addTag(TagRegistry.POWER_UP_ENTITIES);

        tag(EntityTypeTags.REDIRECTABLE_PROJECTILE)
                .add(EntityRegistry.BOUNCING_FIREBALL.get())
                .add(EntityRegistry.BOUNCING_ICE_BALL.get())
                .add(EntityRegistry.GOLD_KOOPA_SHELL.get())
                .add(EntityRegistry.GREEN_KOOPA_SHELL.get())
                .add(EntityRegistry.RED_KOOPA_SHELL.get());

        tag(EntityTypeTags.SKELETONS)
                .add(EntityRegistry.DRY_BONES.get());

        tag(EntityTypeTags.UNDEAD)
                .add(EntityRegistry.BOO.get());

        tag(TagRegistry.BOO_CAN_ATTACK)
                .add(EntityType.PLAYER);

        tag(TagRegistry.CANNOT_BOUNCE_ON_BLOCKS);

        tag(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                .addTag(TagRegistry.DRY_BONES_BONES)
                .addTag(TagRegistry.POWER_UP_ENTITIES);

        tag(TagRegistry.CANNOT_DROP_COINS)
                .addTag(TagRegistry.POWER_UP_ENTITIES);

        tag(TagRegistry.CANNOT_LOSE_POWER_UP)
                .addTag(Tags.EntityTypes.BOSSES)
                .add(EntityType.ELDER_GUARDIAN)
                .add(EntityType.GIANT)
                .addOptionalTag(CompatRegistry.TWILIGHT_FOREST_BOSSES);

        tag(TagRegistry.CANNOT_QUICK_TRAVEL)
                .add(EntityRegistry.PIRANHA_PLANT.get());

        tag(TagRegistry.CANNOT_WARP)
                .addTag(Tags.EntityTypes.BOSSES)
                .add(EntityType.ELDER_GUARDIAN)
                .add(EntityType.ENDER_DRAGON)
                .add(EntityType.WITHER)
                .addOptionalTag(CompatRegistry.TWILIGHT_FOREST_BOSSES);

        tag(TagRegistry.CAN_BE_INSTAKILL_STOMPED)
                .add(EntityRegistry.DRY_BONES.get())
                .add(EntityRegistry.GOOMBA.get())
                .add(EntityRegistry.HEFTY_GOOMBA.get())
                .add(EntityRegistry.MEGA_GOOMBA.get())
                .add(EntityRegistry.MINI_GOOMBA.get());

        tag(TagRegistry.CAN_BE_STOMPED)
                .addTag(TagRegistry.DRY_BONES_BONES)
                .addTag(TagRegistry.GOOMBA_ENTITIES)
                .addTag(TagRegistry.KOOPA_SHELL_ENTITIES)
                .addTag(TagRegistry.KOOPA_TROOPA_ENTITIES)
                .add(EntityRegistry.DRY_BONES.get())
                .add(EntityRegistry.SPLUNKIN.get())
                .add(EntityType.SLIME)
                .add(EntityType.TURTLE)
                .addOptional(TEST_DUMMY);

        tag(TagRegistry.CAN_BONK_BLOCKS)
                .addTag(TagRegistry.KOOPA_SHELL_ENTITIES)
                .add(EntityType.PLAYER)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER)
                .addOptional(GUARD_VILLAGER);

        tag(TagRegistry.CAN_CLAIM_CHECKPOINT_FLAGS)
                .add(EntityType.PLAYER)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER)
                .addOptional(GUARD_VILLAGER);

        tag(TagRegistry.CAN_COLLECT_COINS)
                .add(EntityType.PIGLIN)
                .add(EntityType.PLAYER)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER)
                .addOptional(GUARD_VILLAGER)
                .addOptional(FEMALE_VILLAGER)
                .addOptional(MALE_VILLAGER);

        tag(TagRegistry.CAN_COLLECT_STAR_COINS)
                .add(EntityType.PIGLIN)
                .add(EntityType.PLAYER)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER)
                .addOptional(GUARD_VILLAGER)
                .addOptional(FEMALE_VILLAGER)
                .addOptional(MALE_VILLAGER);

        tag(TagRegistry.CAN_CONSUME_FIRE_FLOWERS)
                .addTag(Tags.EntityTypes.BOSSES)
                .addTag(TagRegistry.GOOMBA_ENTITIES)
                .addTag(TagRegistry.KOOPA_SHELL_ENTITIES)
                .addTag(TagRegistry.KOOPA_TROOPA_ENTITIES)
                .addTag(EntityTypeTags.ILLAGER)
                .addTag(EntityTypeTags.ILLAGER_FRIENDS)
                .add(EntityRegistry.DRY_BONES.get())
                .add(EntityType.ARMOR_STAND)
                .add(EntityType.BOGGED)
                .add(EntityType.CREEPER)
                .add(EntityType.ENDERMAN)
                .add(EntityType.EVOKER)
                .add(EntityType.GIANT)
                .add(EntityType.HUSK)
                .add(EntityType.IRON_GOLEM)
                .add(EntityType.PIGLIN)
                .add(EntityType.PIGLIN_BRUTE)
                .add(EntityType.PLAYER)
                .add(EntityType.SKELETON)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER)
                .add(EntityType.WITCH)
                .add(EntityType.WITHER_SKELETON)
                .add(EntityType.ZOMBIE)
                .add(EntityType.ZOMBIE_VILLAGER)
                .add(EntityType.ZOMBIFIED_PIGLIN)
                .addOptionalTag(CompatRegistry.TWILIGHT_FOREST_BOSSES)
                .addOptional(GUARD_VILLAGER)
                .addOptional(FEMALE_VILLAGER)
                .addOptional(FEMALE_ZOMBIE_VILLAGER)
                .addOptional(MALE_VILLAGER)
                .addOptional(MALE_ZOMBIE_VILLAGER)
                .addOptional(TEST_DUMMY)
                .addOptional(HAT_STAND);

        tag(TagRegistry.CAN_CONSUME_ICE_FLOWERS)
                .addTag(Tags.EntityTypes.BOSSES)
                .addTag(TagRegistry.KOOPA_SHELL_ENTITIES)
                .addTag(TagRegistry.KOOPA_TROOPA_ENTITIES)
                .addTag(EntityTypeTags.ILLAGER)
                .addTag(EntityTypeTags.ILLAGER_FRIENDS)
                .add(EntityRegistry.DRY_BONES.get())
                .add(EntityRegistry.GOOMBA.get())
                .add(EntityRegistry.HEFTY_GOOMBA.get())
                .add(EntityRegistry.MEGA_GOOMBA.get())
                .add(EntityRegistry.MINI_GOOMBA.get())
                .add(EntityType.ARMOR_STAND)
                .add(EntityType.BOGGED)
                .add(EntityType.CREEPER)
                .add(EntityType.DROWNED)
                .add(EntityType.ENDERMAN)
                .add(EntityType.EVOKER)
                .add(EntityType.GIANT)
                .add(EntityType.IRON_GOLEM)
                .add(EntityType.PLAYER)
                .add(EntityType.SKELETON)
                .add(EntityType.STRAY)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER)
                .add(EntityType.WITCH)
                .add(EntityType.ZOMBIE)
                .add(EntityType.ZOMBIE_VILLAGER)
                .addOptionalTag(CompatRegistry.TWILIGHT_FOREST_BOSSES)
                .addOptional(GUARD_VILLAGER)
                .addOptional(FEMALE_VILLAGER)
                .addOptional(FEMALE_ZOMBIE_VILLAGER)
                .addOptional(MALE_VILLAGER)
                .addOptional(MALE_ZOMBIE_VILLAGER)
                .addOptional(TEST_DUMMY)
                .addOptional(HAT_STAND);

        tag(TagRegistry.CAN_CONSUME_SUPER_MUSHROOMS)
                .add(EntityType.PLAYER)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER)
                .addOptional(GUARD_VILLAGER)
                .addOptional(FEMALE_VILLAGER)
                .addOptional(MALE_VILLAGER);

        tag(TagRegistry.CAN_CONSUME_ONE_UPS)
                .add(EntityType.IRON_GOLEM)
                .add(EntityType.PLAYER)
                .add(EntityType.SNOW_GOLEM)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER)
                .addOptional(GUARD_VILLAGER)
                .addOptional(FEMALE_VILLAGER)
                .addOptional(MALE_VILLAGER);

        tag(TagRegistry.CAN_CONSUME_SUPER_STARS)
                .add(EntityType.IRON_GOLEM)
                .add(EntityType.PLAYER)
                .add(EntityType.SNOW_GOLEM);

        tag(TagRegistry.CAN_HIT_QUESTION_BLOCKS)
                .addTag(TagRegistry.KOOPA_SHELL_ENTITIES)
                .add(EntityType.PLAYER)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER)
                .addOptional(GUARD_VILLAGER)
                .addOptional(FEMALE_VILLAGER)
                .addOptional(MALE_VILLAGER);

        tag(TagRegistry.CAN_LOWER_FLAGS)
                .add(EntityType.PLAYER);

        tag(TagRegistry.CAN_PICKUP_AND_THROW_SHELLS)
                .addTag(EntityTypeTags.ILLAGER)
                .addTag(EntityTypeTags.SKELETONS)
                .addTag(EntityTypeTags.ZOMBIES)
                .addTag(Tags.EntityTypes.BOSSES)
                .add(EntityType.PIGLIN)
                .add(EntityType.PIGLIN_BRUTE)
                .add(EntityType.PLAYER)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER)
                .addOptional(GUARD_VILLAGER)
                .addOptional(FEMALE_VILLAGER)
                .addOptional(FEMALE_ZOMBIE_VILLAGER)
                .addOptional(MALE_VILLAGER)
                .addOptional(MALE_ZOMBIE_VILLAGER);

        tag(TagRegistry.CAN_SHOOT_SUPPLEMENTARIES_CANNON)
                .addTag(TagRegistry.GOOMBA_ENTITIES)
                .addTag(TagRegistry.KOOPA_TROOPA_ENTITIES)
                .add(EntityRegistry.BOO.get())
                .add(EntityRegistry.DRY_BONES.get())
                .add(EntityRegistry.POKEY.get())
                .add(EntityRegistry.POKEY_BODY.get());

        tag(TagRegistry.CAN_SMASH_BLOCKS)
                .addTag(TagRegistry.KOOPA_SHELL_ENTITIES)
                .add(EntityType.PLAYER)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER)
                .addOptional(GUARD_VILLAGER)
                .addOptional(FEMALE_VILLAGER)
                .addOptional(MALE_VILLAGER);

        tag(TagRegistry.CAN_STOMP_ENEMIES)
                .add(EntityType.PLAYER)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER)
                .addOptional(GUARD_VILLAGER)
                .addOptional(FEMALE_VILLAGER)
                .addOptional(MALE_VILLAGER);

        tag(TagRegistry.CAN_WEAR_COSTUMES)
                .addTag(Tags.EntityTypes.BOSSES)
                .addTag(TagRegistry.KOOPA_SHELL_ENTITIES)
                .addTag(TagRegistry.KOOPA_TROOPA_ENTITIES)
                .add(EntityRegistry.DRY_BONES.get())
                .add(EntityRegistry.POKEY.get())
                .add(EntityRegistry.POKEY_BODY.get())
                .add(EntityType.ARMOR_STAND)
                .add(EntityType.BOGGED)
                .add(EntityType.DROWNED)
                .add(EntityType.GIANT)
                .add(EntityType.HUSK)
                .add(EntityType.PIGLIN)
                .add(EntityType.PIGLIN_BRUTE)
                .add(EntityType.PLAYER)
                .add(EntityType.SKELETON)
                .add(EntityType.STRAY)
                .add(EntityType.VILLAGER)
                .add(EntityType.WITHER_SKELETON)
                .add(EntityType.ZOMBIE)
                .add(EntityType.ZOMBIE_VILLAGER)
                .add(EntityType.ZOMBIFIED_PIGLIN)
                .addOptionalTag(CompatRegistry.ACCESSORIES_DEFAULT_TARGETS)
                .addOptionalTag(CompatRegistry.TWILIGHT_FOREST_BOSSES)
                .addOptional(GUARD_VILLAGER)
                .addOptional(FEMALE_VILLAGER)
                .addOptional(FEMALE_ZOMBIE_VILLAGER)
                .addOptional(MALE_VILLAGER)
                .addOptional(MALE_ZOMBIE_VILLAGER)
                .addOptional(TEST_DUMMY)
                .addOptional(HAT_STAND);

        tag(TagRegistry.CAN_WEAR_HATS)
                .addTag(Tags.EntityTypes.BOSSES)
                .addTag(TagRegistry.GOOMBA_ENTITIES)
                .addTag(TagRegistry.KOOPA_SHELL_ENTITIES)
                .addTag(TagRegistry.KOOPA_TROOPA_ENTITIES)
                .add(EntityRegistry.BOO.get())
                .add(EntityRegistry.DRY_BONES.get())
                .add(EntityRegistry.POKEY.get())
                .add(EntityRegistry.POKEY_BODY.get())
                .add(EntityRegistry.SPLUNKIN.get())
                .add(EntityType.ARMOR_STAND)
                .add(EntityType.BOGGED)
                .add(EntityType.DROWNED)
                .add(EntityType.GIANT)
                .add(EntityType.HUSK)
                .add(EntityType.PIGLIN)
                .add(EntityType.PIGLIN_BRUTE)
                .add(EntityType.PLAYER)
                .add(EntityType.SKELETON)
                .add(EntityType.STRAY)
                .add(EntityType.VILLAGER)
                .add(EntityType.WITHER_SKELETON)
                .add(EntityType.ZOMBIE)
                .add(EntityType.ZOMBIE_VILLAGER)
                .add(EntityType.ZOMBIFIED_PIGLIN)
                .addOptionalTag(CompatRegistry.ACCESSORIES_DEFAULT_TARGETS)
                .addOptionalTag(CompatRegistry.TWILIGHT_FOREST_BOSSES)
                .addOptional(GUARD_VILLAGER)
                .addOptional(FEMALE_VILLAGER)
                .addOptional(FEMALE_ZOMBIE_VILLAGER)
                .addOptional(MALE_VILLAGER)
                .addOptional(MALE_ZOMBIE_VILLAGER)
                .addOptional(TEST_DUMMY)
                .addOptional(HAT_STAND);

        tag(TagRegistry.CAN_WEAR_PANTS)
                .addTag(Tags.EntityTypes.BOSSES)
                .addTag(TagRegistry.KOOPA_SHELL_ENTITIES)
                .addTag(TagRegistry.KOOPA_TROOPA_ENTITIES)
                .add(EntityRegistry.DRY_BONES.get())
                .add(EntityType.ARMOR_STAND)
                .add(EntityType.BOGGED)
                .add(EntityType.DROWNED)
                .add(EntityType.GIANT)
                .add(EntityType.HUSK)
                .add(EntityType.PIGLIN)
                .add(EntityType.PIGLIN_BRUTE)
                .add(EntityType.PLAYER)
                .add(EntityType.SKELETON)
                .add(EntityType.STRAY)
                .add(EntityType.VILLAGER)
                .add(EntityType.WITHER_SKELETON)
                .add(EntityType.ZOMBIE)
                .add(EntityType.ZOMBIE_VILLAGER)
                .add(EntityType.ZOMBIFIED_PIGLIN)
                .addOptionalTag(CompatRegistry.ACCESSORIES_DEFAULT_TARGETS)
                .addOptionalTag(CompatRegistry.TWILIGHT_FOREST_BOSSES)
                .addOptional(GUARD_VILLAGER)
                .addOptional(FEMALE_VILLAGER)
                .addOptional(FEMALE_ZOMBIE_VILLAGER)
                .addOptional(MALE_VILLAGER)
                .addOptional(MALE_ZOMBIE_VILLAGER)
                .addOptional(TEST_DUMMY);

        tag(TagRegistry.CAN_WEAR_SHIRTS)
                .addTag(Tags.EntityTypes.BOSSES)
                .addTag(TagRegistry.KOOPA_SHELL_ENTITIES)
                .addTag(TagRegistry.KOOPA_TROOPA_ENTITIES)
                .add(EntityRegistry.DRY_BONES.get())
                .add(EntityType.ARMOR_STAND)
                .add(EntityType.BOGGED)
                .add(EntityType.DROWNED)
                .add(EntityType.GIANT)
                .add(EntityType.HUSK)
                .add(EntityType.PIGLIN)
                .add(EntityType.PIGLIN_BRUTE)
                .add(EntityType.PLAYER)
                .add(EntityType.SKELETON)
                .add(EntityType.STRAY)
                .add(EntityType.VILLAGER)
                .add(EntityType.WITHER_SKELETON)
                .add(EntityType.ZOMBIE)
                .add(EntityType.ZOMBIE_VILLAGER)
                .add(EntityType.ZOMBIFIED_PIGLIN)
                .addOptionalTag(CompatRegistry.ACCESSORIES_DEFAULT_TARGETS)
                .addOptionalTag(CompatRegistry.TWILIGHT_FOREST_BOSSES)
                .addOptional(GUARD_VILLAGER)
                .addOptional(FEMALE_VILLAGER)
                .addOptional(FEMALE_ZOMBIE_VILLAGER)
                .addOptional(MALE_VILLAGER)
                .addOptional(MALE_ZOMBIE_VILLAGER)
                .addOptional(TEST_DUMMY);

        tag(TagRegistry.CAN_WEAR_SHOES)
                .addTag(Tags.EntityTypes.BOSSES)
                .addTag(TagRegistry.KOOPA_SHELL_ENTITIES)
                .addTag(TagRegistry.KOOPA_TROOPA_ENTITIES)
                .add(EntityRegistry.DRY_BONES.get())
                .add(EntityType.ARMOR_STAND)
                .add(EntityType.BOGGED)
                .add(EntityType.DROWNED)
                .add(EntityType.GIANT)
                .add(EntityType.HUSK)
                .add(EntityType.PIGLIN)
                .add(EntityType.PIGLIN_BRUTE)
                .add(EntityType.PLAYER)
                .add(EntityType.SKELETON)
                .add(EntityType.STRAY)
                .add(EntityType.VILLAGER)
                .add(EntityType.WITHER_SKELETON)
                .add(EntityType.ZOMBIE)
                .add(EntityType.ZOMBIE_VILLAGER)
                .add(EntityType.ZOMBIFIED_PIGLIN)
                .addOptionalTag(CompatRegistry.ACCESSORIES_DEFAULT_TARGETS)
                .addOptionalTag(CompatRegistry.TWILIGHT_FOREST_BOSSES)
                .addOptional(GUARD_VILLAGER)
                .addOptional(FEMALE_VILLAGER)
                .addOptional(FEMALE_ZOMBIE_VILLAGER)
                .addOptional(MALE_VILLAGER)
                .addOptional(MALE_ZOMBIE_VILLAGER)
                .addOptional(TEST_DUMMY);

        tag(TagRegistry.CHECKPOINT_FLAG_CANNOT_SPAWN);

        tag(TagRegistry.DAMAGE_CANNOT_SHRINK)
                .addTag(TagRegistry.POWER_UP_ENTITIES);

        tag(TagRegistry.DECORATED_POT_CANNOT_SPAWN);

        tag(TagRegistry.DRY_BONES_BONES)
                .add(EntityRegistry.DRY_BONES_HEAD.get())
                .add(EntityRegistry.DRY_BONES_LEFT_ARM.get())
                .add(EntityRegistry.DRY_BONES_LEFT_LEG.get())
                .add(EntityRegistry.DRY_BONES_RIGHT_ARM.get())
                .add(EntityRegistry.DRY_BONES_RIGHT_LEG.get())
                .add(EntityRegistry.DRY_BONES_SHELL.get())
                .add(EntityRegistry.DRY_BONES_TAIL.get());

        tag(TagRegistry.DRY_BONES_CAN_ATTACK)
                .add(EntityType.PLAYER)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER)
                .addOptional(GUARD_VILLAGER)
                .addOptional(FEMALE_VILLAGER)
                .addOptional(MALE_VILLAGER);

        tag(TagRegistry.FIRE_GOOMBA_CAN_ATTACK)
                .add(EntityType.IRON_GOLEM)
                .add(EntityType.PIGLIN)
                .add(EntityType.PIGLIN_BRUTE)
                .add(EntityType.PLAYER)
                .add(EntityType.SNOW_GOLEM)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER)
                .addOptional(GUARD_VILLAGER);

        tag(TagRegistry.FIREBALL_CAN_INSTAKILL)
                .addTag(TagRegistry.KOOPA_SHELL_ENTITIES)
                .addTag(TagRegistry.KOOPA_TROOPA_ENTITIES)
                .add(EntityRegistry.PIRANHA_PLANT.get())
                .add(EntityRegistry.POKEY.get())
                .add(EntityRegistry.POKEY_BODY.get())
                .add(EntityRegistry.SPLUNKIN.get());

        tag(TagRegistry.FIREBALL_IMMUNE)
                .add(EntityRegistry.BOO.get())
                .add(EntityRegistry.DRY_BONES.get())
                .add(EntityRegistry.FIRE_FLOWER.get());

        tag(TagRegistry.ICE_BALL_CAN_INSTAKILL);

        tag(TagRegistry.ICE_BALL_IMMUNE)
                .add(EntityRegistry.BOO.get())
                .add(EntityRegistry.ICE_CUBE.get())
                .add(EntityRegistry.ICE_FLOWER.get())
                .add(EntityType.ENDER_DRAGON);

        tag(TagRegistry.ICE_CUBE_COLLISION_CANNOT_DAMAGE)
                .addTag(TagRegistry.POWER_UP_ENTITIES)
                .add(EntityRegistry.BOO.get());

        tag(TagRegistry.ICE_CUBE_SHATTERS_INSTANTLY)
                .addTag(Tags.EntityTypes.BOSSES)
                .add(EntityRegistry.BOO.get())
                .add(EntityType.ELDER_GUARDIAN)
                .add(EntityType.WARDEN)
                .addOptionalTag(CompatRegistry.TWILIGHT_FOREST_BOSSES);

        tag(TagRegistry.ICE_CUBE_SHATTER_CANNOT_DAMAGE)
                .addTag(Tags.EntityTypes.BOSSES)
                .addTag(TagRegistry.POWER_UP_ENTITIES)
                .add(EntityRegistry.BOO.get())
                .addOptionalTag(CompatRegistry.TWILIGHT_FOREST_BOSSES);

        tag(TagRegistry.ICE_CUBE_SHATTER_CAN_INSTAKILL)
                .add(EntityRegistry.DRY_BONES.get());

        tag(TagRegistry.IRON_SPIKE_IMMUNE)
                .addTag(TagRegistry.POWER_UP_ENTITIES)
                .add(EntityRegistry.BOO.get());

        tag(TagRegistry.EQUIP_COSTUMES_IN_ARMOR_SLOTS)
                .addTag(TagRegistry.GOOMBA_ENTITIES)
                .addTag(TagRegistry.KOOPA_SHELL_ENTITIES)
                .addTag(TagRegistry.KOOPA_TROOPA_ENTITIES)
                .add(EntityRegistry.DRY_BONES.get());

        tag(TagRegistry.GOOMBA_CAN_ATTACK)
                .add(EntityType.IRON_GOLEM)
                .add(EntityType.PLAYER)
                .add(EntityType.SNOW_GOLEM)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER)
                .addOptional(GUARD_VILLAGER)
                .addOptional(FEMALE_VILLAGER)
                .addOptional(MALE_VILLAGER);

        tag(TagRegistry.GOOMBA_CAN_RIDE)
                .addTag(TagRegistry.GOOMBA_ENTITIES);

        tag(TagRegistry.GOOMBA_ENTITIES)
                .add(EntityRegistry.FIRE_GOOMBA.get())
                .add(EntityRegistry.GOOMBA.get())
                .add(EntityRegistry.HEFTY_GOOMBA.get())
                .add(EntityRegistry.MEGA_GOOMBA.get())
                .add(EntityRegistry.MINI_GOOMBA.get());

        tag(TagRegistry.HAS_INFINITE_SHELL_AMMO)
                .addTag(Tags.EntityTypes.BOSSES);

        tag(TagRegistry.HAS_NO_DELTA_MOVEMENT)
                .add(EntityRegistry.PIRANHA_PLANT.get());

        tag(TagRegistry.HEFTY_GOOMBA_CAN_ATTACK)
                .add(EntityType.IRON_GOLEM)
                .add(EntityType.PLAYER)
                .add(EntityType.SNOW_GOLEM)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER)
                .addOptional(GUARD_VILLAGER);

        tag(TagRegistry.KOOPA_CAN_RIDE)
                .addTag(TagRegistry.GOOMBA_ENTITIES);

        tag(TagRegistry.KOOPA_SHELL_CANNOT_DAMAGE)
                .addTag(TagRegistry.POWER_UP_ENTITIES);

        tag(TagRegistry.KOOPA_SHELL_ENTITIES)
                .add(EntityRegistry.GOLD_KOOPA_SHELL.get())
                .add(EntityRegistry.GREEN_KOOPA_SHELL.get())
                .add(EntityRegistry.RED_KOOPA_SHELL.get());

        tag(TagRegistry.KOOPA_TROOPA_ENTITIES)
                .add(EntityRegistry.GOLD_KOOPA_TROOPA.get())
                .add(EntityRegistry.GREEN_KOOPA_TROOPA.get())
                .add(EntityRegistry.RED_KOOPA_TROOPA.get());

        tag(TagRegistry.GOLD_KOOPA_SHELL_CAN_INSTAKILL)
                .addTag(TagRegistry.GOOMBA_ENTITIES)
                .addTag(TagRegistry.KOOPA_SHELL_ENTITIES)
                .addTag(TagRegistry.KOOPA_TROOPA_ENTITIES)
                .add(EntityRegistry.DRY_BONES.get())
                .add(EntityRegistry.PIRANHA_PLANT.get());

        tag(TagRegistry.GOLD_KOOPA_TROOPA_CAN_ATTACK)
                .add(EntityType.PLAYER)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER)
                .addOptional(GUARD_VILLAGER)
                .addOptional(FEMALE_VILLAGER)
                .addOptional(MALE_VILLAGER);

        tag(TagRegistry.GREEN_KOOPA_SHELL_CAN_INSTAKILL)
                .addTag(TagRegistry.GOOMBA_ENTITIES)
                .addTag(TagRegistry.KOOPA_SHELL_ENTITIES)
                .addTag(TagRegistry.KOOPA_TROOPA_ENTITIES)
                .add(EntityRegistry.DRY_BONES.get())
                .add(EntityRegistry.PIRANHA_PLANT.get());

        tag(TagRegistry.GREEN_KOOPA_TROOPA_CAN_ATTACK);

        tag(TagRegistry.RED_KOOPA_SHELL_CAN_INSTAKILL)
                .addTag(TagRegistry.GOOMBA_ENTITIES)
                .addTag(TagRegistry.KOOPA_SHELL_ENTITIES)
                .addTag(TagRegistry.KOOPA_TROOPA_ENTITIES)
                .add(EntityRegistry.DRY_BONES.get())
                .add(EntityRegistry.PIRANHA_PLANT.get());

        tag(TagRegistry.RED_KOOPA_TROOPA_CAN_ATTACK)
                .add(EntityType.PLAYER)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER)
                .addOptional(GUARD_VILLAGER)
                .addOptional(FEMALE_VILLAGER)
                .addOptional(MALE_VILLAGER);

        tag(TagRegistry.RED_KOOPA_SHELL_CANNOT_ATTACK)
                .addTag(TagRegistry.KOOPA_SHELL_ENTITIES);

        tag(TagRegistry.MEGA_GOOMBA_CAN_ATTACK)
                .add(EntityType.IRON_GOLEM)
                .add(EntityType.PLAYER)
                .add(EntityType.SNOW_GOLEM)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER)
                .addOptional(GUARD_VILLAGER)
                .addOptional(FEMALE_VILLAGER)
                .addOptional(MALE_VILLAGER);

        tag(TagRegistry.MINI_GOOMBA_CAN_ATTACH)
                .add(EntityType.ARMOR_STAND)
                .add(EntityType.FROG)
                .add(EntityType.IRON_GOLEM)
                .add(EntityType.PLAYER)
                .add(EntityType.SNOW_GOLEM)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER)
                .addOptional(GUARD_VILLAGER)
                .addOptional(FEMALE_VILLAGER)
                .addOptional(MALE_VILLAGER)
                .addOptional(TEST_DUMMY);

        tag(TagRegistry.DASH_MUSHROOM_CANNOT_BOOST)
                .addTag(TagRegistry.POWER_UP_ENTITIES);

        tag(TagRegistry.PIRANHA_PLANT_CAN_ATTACK)
                .add(EntityType.BEE)
                .add(EntityType.IRON_GOLEM)
                .add(EntityType.PLAYER)
                .add(EntityType.SNOW_GOLEM)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER)
                .addOptional(GUARD_VILLAGER)
                .addOptional(FEMALE_VILLAGER)
                .addOptional(MALE_VILLAGER);

        tag(TagRegistry.POWER_UP_ENTITIES)
                .add(EntityRegistry.FIRE_FLOWER.get())
                .add(EntityRegistry.ICE_FLOWER.get())
                .add(EntityRegistry.SUPER_MUSHROOM.get())
                .add(EntityRegistry.ONE_UP_MUSHROOM.get())
                .add(EntityRegistry.SUPER_STAR.get());

        tag(TagRegistry.POKEYS)
                .add(EntityRegistry.POKEY.get())
                .add(EntityRegistry.POKEY_BODY.get());

        tag(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN);

        tag(TagRegistry.SPIKE_PANEL_IMMUNE)
                .addTag(TagRegistry.POWER_UP_ENTITIES)
                .add(EntityRegistry.BOO.get());

        tag(TagRegistry.SPLUNKIN_CAN_ATTACK)
                .add(EntityType.PLAYER);

        tag(TagRegistry.SUPER_STAR_IMMUNE)
                .addTag(Tags.EntityTypes.BOATS)
                .addTag(Tags.EntityTypes.BOSSES)
                .addTag(TagRegistry.POWER_UP_ENTITIES)
                .add(EntityType.ARMOR_STAND)
                .add(EntityType.ELDER_GUARDIAN)
                .add(EntityType.WARDEN)
                .addOptionalTag(CompatRegistry.TWILIGHT_FOREST_BOSSES);

        tag(TagRegistry.WARP_PIPE_CANNOT_SPAWN);
    }
}