package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.init.EntityRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
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
    private static ResourceLocation TEST_DUMMY = ResourceLocation.fromNamespaceAndPath("dummmmmmy", "test_dummy");

    public EntityTypeTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Marioverse.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(EntityTypeTags.CAN_BREATHE_UNDER_WATER)
                .addTag(TagRegistry.POWER_UP_ENTITIES)
                .add(EntityRegistry.PIRANHA_PLANT.get());

        tag(EntityTypeTags.FALL_DAMAGE_IMMUNE)
                .addTag(TagRegistry.POWER_UP_ENTITIES)
                .add(EntityRegistry.PIRANHA_PLANT.get());

        tag(EntityTypeTags.FROG_FOOD)
                .add(EntityRegistry.MINI_GOOMBA.get());

        tag(EntityTypeTags.IMPACT_PROJECTILES)
                .add(EntityRegistry.BOUNCING_FIREBALL.get());

        tag(EntityTypeTags.NOT_SCARY_FOR_PUFFERFISH)
                .addTag(TagRegistry.POWER_UP_ENTITIES);

        tag(TagRegistry.CAN_BE_INSTAKILL_STOMPED)
                .add(EntityRegistry.GOOMBA.get())
                .add(EntityRegistry.HEFTY_GOOMBA.get())
                .add(EntityRegistry.MEGA_GOOMBA.get())
                .add(EntityRegistry.MINI_GOOMBA.get());

        tag(TagRegistry.CAN_BE_STOMPED)
                .addTag(TagRegistry.GOOMBA_ENTITIES)
                .add(EntityType.SLIME)
                .addOptional(TEST_DUMMY);

        tag(TagRegistry.CAN_CLAIM_CHECKPOINT_FLAGS)
                .add(EntityType.PLAYER)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER);

        tag(TagRegistry.CAN_BONK_BLOCKS)
                .add(EntityType.PLAYER)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER);

        tag(TagRegistry.CAN_CONSUME_FIRE_FLOWERS)
                .addTag(Tags.EntityTypes.BOSSES)
                .addTag(TagRegistry.GOOMBA_ENTITIES)
                .addTag(EntityTypeTags.ILLAGER)
                .addTag(EntityTypeTags.ILLAGER_FRIENDS)
                .add(EntityType.ARMOR_STAND)
                .add(EntityType.BOGGED)
                .add(EntityType.CREEPER)
                .add(EntityType.DROWNED)
                .add(EntityType.ENDERMAN)
                .add(EntityType.EVOKER)
                .add(EntityType.GIANT)
                .add(EntityType.HUSK)
                .add(EntityType.IRON_GOLEM)
                .add(EntityType.PIGLIN)
                .add(EntityType.PIGLIN_BRUTE)
                .add(EntityType.PLAYER)
                .add(EntityType.SKELETON)
                .add(EntityType.STRAY)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER)
                .add(EntityType.WITCH)
                .add(EntityType.WITHER_SKELETON)
                .add(EntityType.ZOMBIE)
                .add(EntityType.ZOMBIE_VILLAGER)
                .add(EntityType.ZOMBIFIED_PIGLIN)
                .addOptionalTag(CompatRegistry.TWILIGHT_FOREST_BOSSES)
                .addOptional(TEST_DUMMY);

        tag(TagRegistry.CAN_CONSUME_ONE_UPS)
                .add(EntityType.IRON_GOLEM)
                .add(EntityType.PLAYER)
                .add(EntityType.SNOW_GOLEM)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER);

        tag(TagRegistry.CAN_CONSUME_SUPER_STARS)
                .add(EntityType.IRON_GOLEM)
                .add(EntityType.PLAYER)
                .add(EntityType.SNOW_GOLEM);

        tag(TagRegistry.CAN_HIT_QUESTION_BLOCKS)
                .add(EntityType.PLAYER)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER);

        tag(TagRegistry.CAN_LOWER_FLAGS)
                .add(EntityType.PLAYER);

        tag(TagRegistry.CAN_PICK_UP_COINS)
                .add(EntityType.PIGLIN)
                .add(EntityType.PLAYER)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER);

        tag(TagRegistry.CAN_SMASH_BLOCKS)
                .add(EntityType.PLAYER)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER);

        tag(TagRegistry.CAN_STOMP_ENEMIES)
                .add(EntityType.PLAYER)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER);

        tag(TagRegistry.CAN_WEAR_COSTUMES)
                .addTag(TagRegistry.GOOMBA_ENTITIES)
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
                .add(EntityType.WITHER_SKELETON)
                .add(EntityType.ZOMBIE)
                .add(EntityType.ZOMBIFIED_PIGLIN)
                .addOptionalTag(CompatRegistry.ACCESSORIES_DEFAULT_TARGETS)
                .addOptional(TEST_DUMMY);

        tag(TagRegistry.CANNOT_CONSUME_POWER_UPS)
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

        tag(TagRegistry.CHECKPOINT_FLAG_CANNOT_SPAWN);

        tag(TagRegistry.DAMAGE_CANNOT_SHRINK)
                .addTag(TagRegistry.POWER_UP_ENTITIES);

        tag(TagRegistry.DECORATED_POT_CANNOT_SPAWN);

        tag(TagRegistry.FIRE_GOOMBA_CAN_ATTACK)
                .add(EntityType.IRON_GOLEM)
                .add(EntityType.PIGLIN)
                .add(EntityType.PIGLIN_BRUTE)
                .add(EntityType.PLAYER)
                .add(EntityType.SNOW_GOLEM)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER);

        tag(TagRegistry.FIREBALL_CAN_INSTAKILL)
                .add(EntityRegistry.PIRANHA_PLANT.get());

        tag(TagRegistry.FIREBALL_IMMUNE);

        tag(EntityTypeTags.REDIRECTABLE_PROJECTILE)
                .add(EntityRegistry.BOUNCING_FIREBALL.get());

        tag(TagRegistry.GOOMBA_CAN_ATTACK)
                .add(EntityType.IRON_GOLEM)
                .add(EntityType.PLAYER)
                .add(EntityType.SNOW_GOLEM)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER);

        tag(TagRegistry.GOOMBA_CAN_RIDE)
                .addTag(TagRegistry.GOOMBA_ENTITIES);

        tag(TagRegistry.GOOMBA_ENTITIES)
                .add(EntityRegistry.FIRE_GOOMBA.get())
                .add(EntityRegistry.GOOMBA.get())
                .add(EntityRegistry.HEFTY_GOOMBA.get())
                .add(EntityRegistry.MEGA_GOOMBA.get())
                .add(EntityRegistry.MINI_GOOMBA.get());

        tag(TagRegistry.HAS_NO_DELTA_MOVEMENT)
                .add(EntityRegistry.PIRANHA_PLANT.get());

        tag(TagRegistry.HEFTY_GOOMBA_CAN_ATTACK)
                .add(EntityType.IRON_GOLEM)
                .add(EntityType.PLAYER)
                .add(EntityType.SNOW_GOLEM)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER);

        tag(TagRegistry.MEGA_GOOMBA_CAN_ATTACK)
                .add(EntityType.IRON_GOLEM)
                .add(EntityType.PLAYER)
                .add(EntityType.SNOW_GOLEM)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER);

        tag(TagRegistry.MINI_GOOMBA_CAN_ATTACH)
                .add(EntityType.ARMOR_STAND)
                .add(EntityType.FROG)
                .add(EntityType.IRON_GOLEM)
                .add(EntityType.PLAYER)
                .add(EntityType.SNOW_GOLEM)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER)
                .addOptional(TEST_DUMMY);

        tag(TagRegistry.PIRANHA_PLANT_CAN_ATTACK)
                .add(EntityType.IRON_GOLEM)
                .add(EntityType.PLAYER)
                .add(EntityType.SNOW_GOLEM)
                .add(EntityType.VILLAGER)
                .add(EntityType.WANDERING_TRADER);

        tag(TagRegistry.POWER_UP_ENTITIES)
                .add(EntityRegistry.FIRE_FLOWER.get())
                .add(EntityRegistry.MUSHROOM.get())
                .add(EntityRegistry.ONE_UP_MUSHROOM.get())
                .add(EntityRegistry.SUPER_STAR.get());

        tag(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN);

        tag(TagRegistry.SUPER_STAR_IMMUNE)
                .addTag(Tags.EntityTypes.BOATS)
                .addTag(Tags.EntityTypes.BOSSES)
                .addTag(TagRegistry.POWER_UP_ENTITIES)
                .add(EntityType.ARMOR_STAND)
                .add(EntityType.ELDER_GUARDIAN)
                .add(EntityType.WARDEN)
                .addOptionalTag(CompatRegistry.TWILIGHT_FOREST_BOSSES);
    }
}