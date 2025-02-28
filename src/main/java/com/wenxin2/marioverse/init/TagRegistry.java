package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;

public class TagRegistry {
    public static final TagKey<BannerPattern> BOWSER_BANNER_PATTERN = bannerPatternTags("pattern_item/bowser");
    public static final TagKey<BannerPattern> PLUMBER_BANNER_PATTERN = bannerPatternTags("pattern_item/plumber");

    public static final TagKey<Biome> HAS_FIRE_GOOMBA = biomeTags("has_fire_goomba");
    public static final TagKey<Biome> HAS_GOOMBA = biomeTags("has_goomba");
    public static final TagKey<Biome> HAS_PIRANHA_PLANT = biomeTags("has_piranha_plant");
    public static final TagKey<Biome> HAS_WORLD_1_2 = biomeTags("has_structure/has_world_1-2");

    public static final TagKey<Block> BONKABLE_BLOCKS = blockTags("bonkable_blocks");
    public static final TagKey<Block> BRICK_PEDESTAL_BLOCKS = blockTags("brick_pedestals");
    public static final TagKey<Block> CHECKPOINT_FLAG_BLOCKS = blockTags("checkpoint_flags");
    public static final TagKey<Block> DYEABLE_CHECKPOINT_FLAG_BLOCKS = blockTags("dyeable_checkpoint_flags");
    public static final TagKey<Block> DYEABLE_GOAL_POLE_BLOCKS = blockTags("dyeable_goal_poles");
    public static final TagKey<Block> DYEABLE_WARP_PIPE_BLOCKS = blockTags("dyeable_warp_pipes");
    public static final TagKey<Block> FIREBALL_SETS_ON_FIRE = blockTags("fireball_sets_on_fire");
    public static final TagKey<Block> GOAL_POLE_BLOCKS = blockTags("goal_poles");
    public static final TagKey<Block> INVISIBLE_QUESTION_BLOCKS = blockTags("invisible_question_blocks");
    public static final TagKey<Block> MELTS = blockTags("melts");
    public static final TagKey<Block> MELTS_INTO_ICE = blockTags("melts_into_ice");
    public static final TagKey<Block> MELTS_INTO_PACKED_ICE = blockTags("melts_into_packed_ice");
    public static final TagKey<Block> MELTS_INTO_WATER = blockTags("melts_into_water");
    public static final TagKey<Block> PIRANHA_PLANTS_CANNOT_ATTACH = blockTags("piranha_plants_cannot_attach");
    public static final TagKey<Block> PIRANHA_PLANTS_CAN_HIDE = blockTags("piranha_plants_can_hide");
    public static final TagKey<Block> PIRANHA_PLANTS_SPAWNABLE_ON = blockTags("piranha_plants_spawnable_on");
    public static final TagKey<Block> QUESTION_BLOCKS = blockTags("question_blocks");
    public static final TagKey<Block> SMASHABLE_BLOCKS = blockTags("smashable_blocks");
    public static final TagKey<Block> STORAGE_BRICK_BLOCKS = blockTags("storage_bricks");
    public static final TagKey<Block> WARP_PIPE_BLOCKS = blockTags("warp_pipes");
    public static final TagKey<Block> WRENCH_EFFICIENT = blockTags("wrench_efficient");

    public static final TagKey<DamageType> IS_FIREBALL = damageTypeTags("is_fireball");
    public static final TagKey<DamageType> IS_PIRANHA_CHOMP = damageTypeTags("is_piranha_chomp");
    public static final TagKey<DamageType> IS_SUPER_STAR = damageTypeTags("is_super_star");
    public static final TagKey<DamageType> SHIELD_BLOCKS = damageTypeTags("shield_blocks");

    public static final TagKey<Item> BONKABLE_BLOCK_ITEMS = itemTags("bonkable_blocks");
    public static final TagKey<Item> BRICK_PEDESTAL_ITEMS = itemTags("brick_pedestals");
    public static final TagKey<Item> CANNOT_PLACE_IN_QUESTION_BLOCKS = itemTags("cannot_place_in_question_blocks");
    public static final TagKey<Item> CHECKPOINT_FLAG_ITEMS = itemTags("checkpoint_flags");
    public static final TagKey<Item> DYEABLE_CHECKPOINT_FLAG_ITEMS = itemTags("dyeable_checkpoint_flags");
    public static final TagKey<Item> DYEABLE_GOAL_POLE_ITEMS = itemTags("dyeable_goal_poles");
    public static final TagKey<Item> DYEABLE_WARP_PIPE_ITEMS = itemTags("dyeable_warp_pipes");
    public static final TagKey<Item> GOAL_POLE_ITEMS = itemTags("goal_poles");
    public static final TagKey<Item> INVISIBLE_QUESTION_BLOCK_ITEMS = itemTags("invisible_question_blocks");
    public static final TagKey<Item> POWER_UP_COSTUME_ITEMS = itemTags("power_up_costumes");
    public static final TagKey<Item> POWER_UP_ITEMS = itemTags("power_ups");
    public static final TagKey<Item> QUESTION_BLOCK_ITEMS = itemTags("question_blocks");
    public static final TagKey<Item> SMASHABLE_BLOCK_ITEMS = itemTags("smashable_blocks");
    public static final TagKey<Item> STORAGE_BRICK_ITEMS = itemTags("storage_bricks");
    public static final TagKey<Item> WARP_PIPE_ITEMS = itemTags("warp_pipes");

    public static final TagKey<EntityType<?>> CANNOT_CONSUME_POWER_UPS = entityTypeTags("cannot_consume_power_ups");
    public static final TagKey<EntityType<?>> CANNOT_DROP_COINS = entityTypeTags("cannot_drop_coins");
    public static final TagKey<EntityType<?>> CANNOT_LOSE_POWER_UP = entityTypeTags("cannot_lose_power_up");
    public static final TagKey<EntityType<?>> CANNOT_QUICK_TRAVEL = entityTypeTags("cannot_quick_travel");
    public static final TagKey<EntityType<?>> CANNOT_WARP = entityTypeTags("cannot_warp");
    public static final TagKey<EntityType<?>> CAN_BE_INSTAKILL_STOMPED = entityTypeTags("can_be_instakill_stomped");
    public static final TagKey<EntityType<?>> CAN_BE_STOMPED = entityTypeTags("can_be_stomped");
    public static final TagKey<EntityType<?>> CAN_BONK_BLOCKS = entityTypeTags("can_bonk_blocks");
    public static final TagKey<EntityType<?>> CAN_CLAIM_CHECKPOINT_FLAGS = entityTypeTags("can_claim_checkpoint_flags");
    public static final TagKey<EntityType<?>> CAN_CONSUME_FIRE_FLOWERS = entityTypeTags("can_consume_fire_flowers");
    public static final TagKey<EntityType<?>> CAN_CONSUME_ONE_UPS = entityTypeTags("can_consume_one_ups");
    public static final TagKey<EntityType<?>> CAN_CONSUME_SUPER_STARS = entityTypeTags("can_consume_super_stars");
    public static final TagKey<EntityType<?>> CAN_HIT_QUESTION_BLOCKS = entityTypeTags("can_hit_question_blocks");
    public static final TagKey<EntityType<?>> CAN_LOWER_FLAGS = entityTypeTags("can_lower_flags");
    public static final TagKey<EntityType<?>> CAN_PICK_UP_COINS = entityTypeTags("can_pick_up_coins");
    public static final TagKey<EntityType<?>> CAN_SMASH_BLOCKS = entityTypeTags("can_smash_blocks");
    public static final TagKey<EntityType<?>> CAN_STOMP_ENEMIES = entityTypeTags("can_stomp_enemies");
    public static final TagKey<EntityType<?>> CAN_WEAR_COSTUMES = entityTypeTags("can_wear_costumes");
    public static final TagKey<EntityType<?>> DAMAGE_CANNOT_SHRINK = entityTypeTags("damage_cannot_shrink");
    public static final TagKey<EntityType<?>> FIREBALL_IMMUNE = entityTypeTags("fireball_immune");
    public static final TagKey<EntityType<?>> FIREBALL_CAN_INSTAKILL = entityTypeTags("fireball_can_instakill");
    public static final TagKey<EntityType<?>> FIRE_GOOMBA_CAN_ATTACK = entityTypeTags("fire_goomba_can_attack");
    public static final TagKey<EntityType<?>> GOOMBA_CAN_ATTACK = entityTypeTags("goomba_can_attack");
    public static final TagKey<EntityType<?>> GOOMBA_CAN_RIDE = entityTypeTags("goomba_can_ride");
    public static final TagKey<EntityType<?>> GOOMBA_ENTITIES = entityTypeTags("goombas");
    public static final TagKey<EntityType<?>> HAS_NO_DELTA_MOVEMENT = entityTypeTags("has_no_delta_movement");
    public static final TagKey<EntityType<?>> HEFTY_GOOMBA_CAN_ATTACK = entityTypeTags("hefty_goomba_can_attack");
    public static final TagKey<EntityType<?>> MEGA_GOOMBA_CAN_ATTACK = entityTypeTags("mega_goomba_can_attack");
    public static final TagKey<EntityType<?>> MINI_GOOMBA_CAN_ATTACH = entityTypeTags("mini_goomba_can_attach");
    public static final TagKey<EntityType<?>> PIRANHA_PLANT_CAN_ATTACK = entityTypeTags("piranha_plant_can_attack");
    public static final TagKey<EntityType<?>> POWER_UP_ENTITIES = entityTypeTags("power_ups");
    public static final TagKey<EntityType<?>> QUESTION_BLOCK_CANNOT_SPAWN = entityTypeTags("question_block_cannot_spawn");
    public static final TagKey<EntityType<?>> DECORATED_POT_CANNOT_SPAWN = entityTypeTags("decorated_pot_cannot_spawn");
    public static final TagKey<EntityType<?>> SUPER_STAR_IMMUNE = entityTypeTags("super_star_immune");


    private static TagKey<BannerPattern> bannerPatternTags(String name) {
        return TagKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, name));
    }

    private static TagKey<Biome> biomeTags(String name) {
        return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, name));
    }

    public static TagKey<Block> blockTags(String name) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, name));
    }

    public static TagKey<Block> blockTags(String mod_id, String name) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(mod_id, name));
    }

    public static TagKey<DamageType> damageTypeTags(String name) {
        return TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, name));
    }

    public static TagKey<EntityType<?>> entityTypeTags(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, name));
    }

    public static TagKey<Item> itemTags(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, name));
    }

    public static TagKey<Item> itemTags(String mod_id, String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(mod_id, name));
    }
}
