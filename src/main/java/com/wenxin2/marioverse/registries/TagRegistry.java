package com.wenxin2.marioverse.registries;

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
import net.minecraft.world.level.material.Fluid;

public class TagRegistry {
    public static final TagKey<BannerPattern> BOWSER_BANNER_PATTERN = bannerPatternTags("pattern_item/bowser");
    public static final TagKey<BannerPattern> PLUMBER_BANNER_PATTERN = bannerPatternTags("pattern_item/plumber");

    public static final TagKey<Biome> HAS_BRICK_LANES = biomeTags("has_structure/has_brick_lanes");
    public static final TagKey<Biome> HAS_CLASSIC_UNDERGROUND = biomeTags("has_structure/has_classic_underground");
    public static final TagKey<Biome> HAS_FIRE_GOOMBA = biomeTags("has_fire_goomba");
    public static final TagKey<Biome> HAS_GOOMBA = biomeTags("has_goomba");
    public static final TagKey<Biome> HAS_KOOPA_TROOPA = biomeTags("has_koopa_troopa");
    public static final TagKey<Biome> HAS_KOOPA_TROOPA_RARE = biomeTags("has_koopa_troopa_rare");
    public static final TagKey<Biome> HAS_PIRANHA_PLANT = biomeTags("has_piranha_plant");

    public static final TagKey<Block> BONKABLE_BLOCKS = blockTags("bonkable_blocks");
    public static final TagKey<Block> BOUNCY_BLOCKS = blockTags("bouncy_blocks");
    public static final TagKey<Block> BRICK_PEDESTAL_BLOCKS = blockTags("brick_pedestals");
    public static final TagKey<Block> CALCITE_BRICK_BLOCKS = blockTags("calcite_bricks");
    public static final TagKey<Block> CHECKPOINT_FLAG_BLOCKS = blockTags("checkpoint_flags");
    public static final TagKey<Block> DYEABLE_CHECKPOINT_FLAG_BLOCKS = blockTags("dyeable_checkpoint_flags");
    public static final TagKey<Block> DYEABLE_GOAL_POLE_BLOCKS = blockTags("dyeable_goal_poles");
    public static final TagKey<Block> DYEABLE_WARP_PIPE_BLOCKS = blockTags("dyeable_warp_pipes");
    public static final TagKey<Block> FIREBALL_SETS_ON_FIRE = blockTags("fireball_sets_on_fire");
    public static final TagKey<Block> FREEZES_INTO_PACKED_ICE = blockTags("freezes_into_packed_ice");
    public static final TagKey<Block> GOAL_POLE_BLOCKS = blockTags("goal_poles");
    public static final TagKey<Block> ICE_BALL_EXTINGUISHES = blockTags("ice_ball_extinguishes");
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

    public static final TagKey<DamageType> BYPASSES_SUPER_STAR = damageTypeTags("bypasses_super_star");
    public static final TagKey<DamageType> HIDES_KOOPA_TROOPA = damageTypeTags("hides_koopa_troopa");
    public static final TagKey<DamageType> FLIPS_KOOPA_SHELL = damageTypeTags("flips_koopa_shell");
    public static final TagKey<DamageType> STOPS_KOOPA_SHELL = damageTypeTags("stops_koopa_shell");
    public static final TagKey<DamageType> IS_BONKED = damageTypeTags("is_bonked");
    public static final TagKey<DamageType> IS_DEFEATED = damageTypeTags("is_defeated");
    public static final TagKey<DamageType> IS_FIREBALL = damageTypeTags("is_fireball");
    public static final TagKey<DamageType> IS_ICE_BALL = damageTypeTags("ice_ball");
    public static final TagKey<DamageType> IS_ICE_CUBE_CRUSHED = damageTypeTags("ice_cube_crushed");
    public static final TagKey<DamageType> IS_PIRANHA_CHOMP = damageTypeTags("is_piranha_chomp");
    public static final TagKey<DamageType> IS_SPINNING_SHELL = damageTypeTags("is_spinning_shell");
    public static final TagKey<DamageType> IS_SHRAPNEL = damageTypeTags("is_shrapnel");
    public static final TagKey<DamageType> IS_STOMP = damageTypeTags("is_stomp");
    public static final TagKey<DamageType> IS_SUPER_STAR = damageTypeTags("is_super_star");
    public static final TagKey<DamageType> SHIELD_BLOCKS = damageTypeTags("shield_blocks");

    public static final TagKey<Fluid> FREEZES_INTO_COBBLESTONE = fluidTags("freezes_into_cobblestone");
    public static final TagKey<Fluid> FREEZES_INTO_FROSTED_ICE = fluidTags("freezes_into_frosted_ice");
    public static final TagKey<Fluid> FREEZES_INTO_OBSIDIAN = fluidTags("freezes_into_obsidian");

    public static final TagKey<Item> BONKABLE_BLOCK_ITEMS = itemTags("bonkable_blocks");
    public static final TagKey<Item> BRICK_PEDESTAL_ITEMS = itemTags("brick_pedestals");
    public static final TagKey<Item> CALCITE_BRICK_ITEMS = itemTags("calcite_bricks");
    public static final TagKey<Item> CAN_SELECT_CLEAR_WARP_PIPES = itemTags("can_select_clear_warp_pipes");
    public static final TagKey<Item> CAN_SELECT_WATER_SPOUTS = itemTags("can_select_water_spouts");
    public static final TagKey<Item> CANNOT_PLACE_IN_CHECKPOINT_FLAGS = itemTags("cannot_place_in_checkpoint_flags");
    public static final TagKey<Item> CANNOT_PLACE_IN_QUESTION_BLOCKS = itemTags("cannot_place_in_question_blocks");
    public static final TagKey<Item> CHECKPOINT_FLAG_ITEMS = itemTags("checkpoint_flags");
    public static final TagKey<Item> CHARMS = itemTags("accessories", "charm");
    public static final TagKey<Item> COSTUMES = itemTags("costumes");
    public static final TagKey<Item> COSTUME_HAT = itemTags("accessories", "costume_hat");
    public static final TagKey<Item> COSTUME_PANTS = itemTags("accessories", "costume_pants");
    public static final TagKey<Item> COSTUME_SHIRT = itemTags("accessories", "costume_shirt");
    public static final TagKey<Item> COSTUME_SHOES = itemTags("accessories", "costume_shoes");
    public static final TagKey<Item> DYEABLE_CHECKPOINT_FLAG_ITEMS = itemTags("dyeable_checkpoint_flags");
    public static final TagKey<Item> DYEABLE_GOAL_POLE_ITEMS = itemTags("dyeable_goal_poles");
    public static final TagKey<Item> DYEABLE_WARP_PIPE_ITEMS = itemTags("dyeable_warp_pipes");
    public static final TagKey<Item> FIRE_COSTUMES = itemTags("costumes/fire");
    public static final TagKey<Item> GOAL_POLE_ITEMS = itemTags("goal_poles");
    public static final TagKey<Item> HATS = itemTags("hats");
    public static final TagKey<Item> ICE_COSTUMES = itemTags("costumes/ice");
    public static final TagKey<Item> INVISIBLE_QUESTION_BLOCK_ITEMS = itemTags("invisible_question_blocks");
    public static final TagKey<Item> KOOPA_SHELL_ITEMS = itemTags("koopa_shells");
    public static final TagKey<Item> KOOPA_SHOES = itemTags("koopa_shoes");
    public static final TagKey<Item> KOOPA_TROOPA_SPAWN_EGGS = itemTags("spawn_eggs/koopa_troopa");
    public static final TagKey<Item> REPAIRS_KOOPA_SHELLS = itemTags("repairs_koopa_shells");
    public static final TagKey<Item> LUIGI_COSTUMES = itemTags("costumes/luigi");;
    public static final TagKey<Item> LUIGI_FIRE_COSTUMES = itemTags("costumes/fire/luigi");
    public static final TagKey<Item> LUIGI_HATS = itemTags("hats/luigi");
    public static final TagKey<Item> LUIGI_ICE_COSTUMES = itemTags("costumes/ice/luigi");
    public static final TagKey<Item> LUIGI_PANTS = itemTags("pants/luigi");
    public static final TagKey<Item> LUIGI_POWER_UP_COSTUMES = itemTags("power_up_costumes/luigi");
    public static final TagKey<Item> LUIGI_SHIRTS = itemTags("shirts/luigi");
    public static final TagKey<Item> LUIGI_SHOES = itemTags("shoes/luigi");
    public static final TagKey<Item> MARIO_COSTUMES = itemTags("costumes/mario");
    public static final TagKey<Item> MARIO_FIRE_COSTUMES = itemTags("costumes/fire/mario");
    public static final TagKey<Item> MARIO_HATS = itemTags("hats/mario");
    public static final TagKey<Item> MARIO_ICE_COSTUMES = itemTags("costumes/ice/mario");
    public static final TagKey<Item> MARIO_PANTS = itemTags("pants/mario");
    public static final TagKey<Item> MARIO_POWER_UP_COSTUMES = itemTags("power_up_costumes/mario");
    public static final TagKey<Item> MARIO_SHIRTS = itemTags("shirts/mario");
    public static final TagKey<Item> MARIO_SHOES = itemTags("shoes/mario");
    public static final TagKey<Item> PANTS = itemTags("pants");
    public static final TagKey<Item> PEACH_COSTUMES = itemTags("costumes/peach");;
    public static final TagKey<Item> PEACH_FIRE_COSTUMES = itemTags("costumes/fire/peach");
    public static final TagKey<Item> PEACH_HATS = itemTags("hats/peach");
    public static final TagKey<Item> PEACH_ICE_COSTUMES = itemTags("costumes/ice/peach");
    public static final TagKey<Item> PEACH_PANTS = itemTags("pants/peach");
    public static final TagKey<Item> PEACH_POWER_UP_COSTUMES = itemTags("power_up_costumes/peach");
    public static final TagKey<Item> PEACH_SHIRTS = itemTags("shirts/peach");
    public static final TagKey<Item> PEACH_SHOES = itemTags("shoes/peach");
    public static final TagKey<Item> PIRANHA_FOOD = itemTags("piranha_food");
    public static final TagKey<Item> POWER_UP_COSTUMES = itemTags("power_up_costumes");
    public static final TagKey<Item> POWER_UP_ITEMS = itemTags("power_ups");
    public static final TagKey<Item> QUESTION_BLOCK_ITEMS = itemTags("question_blocks");
    public static final TagKey<Item> SHIRTS = itemTags("shirts");
    public static final TagKey<Item> SHOES = itemTags("shoes");
    public static final TagKey<Item> SMASHABLE_BLOCK_ITEMS = itemTags("smashable_blocks");
    public static final TagKey<Item> STORAGE_BRICK_ITEMS = itemTags("storage_bricks");
    public static final TagKey<Item> WARP_FUEL = itemTags("warp_fuel");
    public static final TagKey<Item> WARP_PIPE_CANNOT_SPAWN_ITEMS = itemTags("warp_pipe_cannot_spawn");
    public static final TagKey<Item> WARP_PIPE_ITEMS = itemTags("warp_pipes");
    public static final TagKey<Item> WRENCHES = itemTags("tools/wrenches");

    public static final TagKey<EntityType<?>> CANNOT_BOUNCE_ON_BLOCKS = entityTypeTags("cannot_bounce_on_blocks");
    public static final TagKey<EntityType<?>> CANNOT_CONSUME_POWER_UPS = entityTypeTags("cannot_consume_power_ups");
    public static final TagKey<EntityType<?>> CANNOT_DROP_COINS = entityTypeTags("cannot_drop_coins");
    public static final TagKey<EntityType<?>> CANNOT_LOSE_POWER_UP = entityTypeTags("cannot_lose_power_up");
    public static final TagKey<EntityType<?>> CANNOT_QUICK_TRAVEL = entityTypeTags("cannot_quick_travel");
    public static final TagKey<EntityType<?>> CANNOT_WARP = entityTypeTags("cannot_warp");
    public static final TagKey<EntityType<?>> CAN_BE_INSTAKILL_STOMPED = entityTypeTags("can_be_instakill_stomped");
    public static final TagKey<EntityType<?>> CAN_BE_STOMPED = entityTypeTags("can_be_stomped");
    public static final TagKey<EntityType<?>> CAN_BONK_BLOCKS = entityTypeTags("can_bonk_blocks");
    public static final TagKey<EntityType<?>> CAN_CLAIM_CHECKPOINT_FLAGS = entityTypeTags("can_claim_checkpoint_flags");
    public static final TagKey<EntityType<?>> CAN_COLLECT_COINS = entityTypeTags("can_collect_coins");
    public static final TagKey<EntityType<?>> CAN_COLLECT_STAR_COINS = entityTypeTags("can_collect_star_coins");
    public static final TagKey<EntityType<?>> CAN_CONSUME_FIRE_FLOWERS = entityTypeTags("can_consume_fire_flowers");
    public static final TagKey<EntityType<?>> CAN_CONSUME_ICE_FLOWERS = entityTypeTags("can_consume_ice_flowers");
    public static final TagKey<EntityType<?>> CAN_CONSUME_ONE_UPS = entityTypeTags("can_consume_one_ups");
    public static final TagKey<EntityType<?>> CAN_CONSUME_SUPER_MUSHROOMS = entityTypeTags("can_consume_super_mushrooms");
    public static final TagKey<EntityType<?>> CAN_CONSUME_SUPER_STARS = entityTypeTags("can_consume_super_stars");
    public static final TagKey<EntityType<?>> CAN_HIT_QUESTION_BLOCKS = entityTypeTags("can_hit_question_blocks");
    public static final TagKey<EntityType<?>> CAN_LOWER_FLAGS = entityTypeTags("can_lower_flags");
    public static final TagKey<EntityType<?>> CAN_PICKUP_AND_THROW_SHELLS = entityTypeTags("can_pickup_and_throw_shells");
    public static final TagKey<EntityType<?>> CAN_SHOOT_SUPPLEMENTARIES_CANNON = entityTypeTags("can_shoot_supplementaries_cannon");
    public static final TagKey<EntityType<?>> CAN_SMASH_BLOCKS = entityTypeTags("can_smash_blocks");
    public static final TagKey<EntityType<?>> CAN_STOMP_ENEMIES = entityTypeTags("can_stomp_enemies");
    public static final TagKey<EntityType<?>> CAN_WEAR_COSTUMES = entityTypeTags("can_wear_costumes");
    public static final TagKey<EntityType<?>> CAN_WEAR_HATS = entityTypeTags("can_wear_hats");
    public static final TagKey<EntityType<?>> CAN_WEAR_PANTS = entityTypeTags("can_wear_pants");
    public static final TagKey<EntityType<?>> CAN_WEAR_SHIRTS = entityTypeTags("can_wear_shirts");
    public static final TagKey<EntityType<?>> CAN_WEAR_SHOES = entityTypeTags("can_wear_shoes");
    public static final TagKey<EntityType<?>> CHECKPOINT_FLAG_CANNOT_SPAWN = entityTypeTags("checkpoint_flag_cannot_spawn");
    public static final TagKey<EntityType<?>> DAMAGE_CANNOT_SHRINK = entityTypeTags("damage_cannot_shrink");
    public static final TagKey<EntityType<?>> DASH_MUSHROOM_CANNOT_BOOST = entityTypeTags("dash_mushroom_cannot_boost");
    public static final TagKey<EntityType<?>> DECORATED_POT_CANNOT_SPAWN = entityTypeTags("decorated_pot_cannot_spawn");
    public static final TagKey<EntityType<?>> EQUIP_COSTUMES_IN_ARMOR_SLOTS = entityTypeTags("equip_costumes_in_armor_slots");
    public static final TagKey<EntityType<?>> FIREBALL_CAN_INSTAKILL = entityTypeTags("fireball_can_instakill");
    public static final TagKey<EntityType<?>> FIREBALL_IMMUNE = entityTypeTags("fireball_immune");
    public static final TagKey<EntityType<?>> FIRE_GOOMBA_CAN_ATTACK = entityTypeTags("fire_goomba_can_attack");
    public static final TagKey<EntityType<?>> GOLD_KOOPA_SHELL_CAN_INSTAKILL = entityTypeTags("gold_koopa_shell_can_instakill");
    public static final TagKey<EntityType<?>> GOLD_KOOPA_TROOPA_CAN_ATTACK = entityTypeTags("gold_koopa_troopa_can_attack");
    public static final TagKey<EntityType<?>> GOOMBA_CAN_ATTACK = entityTypeTags("goomba_can_attack");
    public static final TagKey<EntityType<?>> GOOMBA_CAN_RIDE = entityTypeTags("goomba_can_ride");
    public static final TagKey<EntityType<?>> GOOMBA_ENTITIES = entityTypeTags("goombas");
    public static final TagKey<EntityType<?>> GREEN_KOOPA_SHELL_CAN_INSTAKILL = entityTypeTags("green_koopa_shell_can_instakill");
    public static final TagKey<EntityType<?>> GREEN_KOOPA_TROOPA_CAN_ATTACK = entityTypeTags("green_koopa_troopa_can_attack");
    public static final TagKey<EntityType<?>> HAS_INFINITE_SHELL_AMMO = entityTypeTags("has_infinite_shell_ammo");
    public static final TagKey<EntityType<?>> HAS_NO_DELTA_MOVEMENT = entityTypeTags("has_no_delta_movement");
    public static final TagKey<EntityType<?>> HEFTY_GOOMBA_CAN_ATTACK = entityTypeTags("hefty_goomba_can_attack");
    public static final TagKey<EntityType<?>> ICE_BALL_CAN_INSTAKILL = entityTypeTags("ice_ball_can_instakill");
    public static final TagKey<EntityType<?>> ICE_BALL_IMMUNE = entityTypeTags("ice_ball_immune");
    public static final TagKey<EntityType<?>> ICE_CUBE_COLLISION_CANNOT_DAMAGE = entityTypeTags("ice_cube_collision_cannot_damage");
    public static final TagKey<EntityType<?>> ICE_CUBE_SHATTERS_INSTANTLY = entityTypeTags("ice_cube_shatters_instantly");
    public static final TagKey<EntityType<?>> ICE_CUBE_SHATTER_CANNOT_DAMAGE = entityTypeTags("ice_cube_shatter_cannot_damage");
    public static final TagKey<EntityType<?>> IRON_SPIKE_IMMUNE = entityTypeTags("iron_spike_immune");
    public static final TagKey<EntityType<?>> KOOPA_CAN_RIDE = entityTypeTags("koopa_can_ride");
    public static final TagKey<EntityType<?>> KOOPA_SHELL_CANNOT_DAMAGE = entityTypeTags("koopa_shell_cannot_damage");
    public static final TagKey<EntityType<?>> KOOPA_SHELL_ENTITIES = entityTypeTags("koopa_shells");
    public static final TagKey<EntityType<?>> KOOPA_TROOPA_ENTITIES = entityTypeTags("koopa_troopas");
    public static final TagKey<EntityType<?>> MEGA_GOOMBA_CAN_ATTACK = entityTypeTags("mega_goomba_can_attack");
    public static final TagKey<EntityType<?>> MINI_GOOMBA_CAN_ATTACH = entityTypeTags("mini_goomba_can_attach");
    public static final TagKey<EntityType<?>> PIRANHA_PLANT_CAN_ATTACK = entityTypeTags("piranha_plant_can_attack");
    public static final TagKey<EntityType<?>> POWER_UP_ENTITIES = entityTypeTags("power_ups");
    public static final TagKey<EntityType<?>> QUESTION_BLOCK_CANNOT_SPAWN = entityTypeTags("question_block_cannot_spawn");
    public static final TagKey<EntityType<?>> RED_KOOPA_SHELL_CANNOT_ATTACK = entityTypeTags("red_koopa_shell_cannot_attack");
    public static final TagKey<EntityType<?>> RED_KOOPA_SHELL_CAN_INSTAKILL = entityTypeTags("red_koopa_shell_can_instakill");
    public static final TagKey<EntityType<?>> RED_KOOPA_TROOPA_CAN_ATTACK = entityTypeTags("red_koopa_troopa_can_attack");
    public static final TagKey<EntityType<?>> SUPER_STAR_IMMUNE = entityTypeTags("super_star_immune");
    public static final TagKey<EntityType<?>> WARP_PIPE_CANNOT_SPAWN = entityTypeTags("warp_pipe_cannot_spawn");

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

    public static TagKey<Fluid> fluidTags(String name) {
        return TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, name));
    }

    public static TagKey<Item> itemTags(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, name));
    }

    public static TagKey<Item> itemTags(String mod_id, String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(mod_id, name));
    }
}
