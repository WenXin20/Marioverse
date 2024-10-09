package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class TagRegistry {
    public static final TagKey<Block> BONKABLE_BLOCKS = blockTags(Marioverse.MOD_ID, "bonkable_blocks");
    public static final TagKey<Block> DYEABLE_WARP_PIPE_BLOCKS = blockTags(Marioverse.MOD_ID, "dyeable_warp_pipes");
    public static final TagKey<Block> FIREBALL_SETS_ON_FIRE = blockTags(Marioverse.MOD_ID, "fireball_sets_on_fire");
    public static final TagKey<Block> ITEM_BRICK_BLOCKS = blockTags(Marioverse.MOD_ID, "storage_bricks");
    public static final TagKey<Block> MELTS = blockTags(Marioverse.MOD_ID, "melts");
    public static final TagKey<Block> MELTS_INTO_ICE = blockTags(Marioverse.MOD_ID, "melts_into_ice");
    public static final TagKey<Block> MELTS_INTO_PACKED_ICE = blockTags(Marioverse.MOD_ID, "melts_into_packed_ice");
    public static final TagKey<Block> MELTS_INTO_WATER = blockTags(Marioverse.MOD_ID, "melts_into_water");
    public static final TagKey<Block> QUESTION_BLOCK_BLOCKS = blockTags(Marioverse.MOD_ID, "question_blocks");
    public static final TagKey<Block> SMASHABLE_BLOCKS = blockTags(Marioverse.MOD_ID, "smashable_blocks");
    public static final TagKey<Block> WARP_PIPE_BLOCKS = blockTags(Marioverse.MOD_ID, "warp_pipes");
    public static final TagKey<Block> WRENCH_EFFICIENT = blockTags(Marioverse.MOD_ID, "wrench_efficient");
    public static final TagKey<Item> DYEABLE_WARP_PIPE_ITEMS = itemTags(Marioverse.MOD_ID, "dyeable_warp_pipes");
    public static final TagKey<Item> ITEM_BRICK_ITEMS = itemTags(Marioverse.MOD_ID, "storage_bricks");
    public static final TagKey<Item> POWER_UP_ITEMS = itemTags(Marioverse.MOD_ID, "power_ups");
    public static final TagKey<Item> POWER_UP_COSTUME_ITEMS = itemTags(Marioverse.MOD_ID, "power_up_costumes");
    public static final TagKey<Item> QUESTION_BLOCK_ITEMS = itemTags(Marioverse.MOD_ID, "question_blocks");
    public static final TagKey<Item> QUESTION_BLOCK_ITEM_BLACKLIST = itemTags(Marioverse.MOD_ID, "question_block_blacklist");
    public static final TagKey<Item> WARP_PIPE_ITEMS = itemTags(Marioverse.MOD_ID, "warp_pipes");
    public static final TagKey<EntityType<?>> CANNOT_CONSUME_ONE_UPS = entityTypeTags(Marioverse.MOD_ID, "cannot_consume_one_ups");
    public static final TagKey<EntityType<?>> CANNOT_CONSUME_POWER_UPS = entityTypeTags(Marioverse.MOD_ID, "cannot_consume_power_ups");
    public static final TagKey<EntityType<?>> CANNOT_DROP_COINS = entityTypeTags(Marioverse.MOD_ID, "cannot_drop_coins");
    public static final TagKey<EntityType<?>> CANNOT_LOSE_POWER_UP = entityTypeTags(Marioverse.MOD_ID, "cannot_lose_power_up");
    public static final TagKey<EntityType<?>> CANNOT_QUICK_TRAVEL = entityTypeTags(Marioverse.MOD_ID, "cannot_quick_travel");
    public static final TagKey<EntityType<?>> CANNOT_WARP = entityTypeTags(Marioverse.MOD_ID, "cannot_warp");
    public static final TagKey<EntityType<?>> CAN_BE_STOMPED = entityTypeTags(Marioverse.MOD_ID, "can_be_stomped");
    public static final TagKey<EntityType<?>> CAN_CONSUME_FIRE_FLOWERS = entityTypeTags(Marioverse.MOD_ID, "can_consume_fire_flowers");
    public static final TagKey<EntityType<?>> CAN_STOMP_ENEMIES = entityTypeTags(Marioverse.MOD_ID, "can_stomp_enemies");
    public static final TagKey<EntityType<?>> CAN_WEAR_COSTUMES = entityTypeTags(Marioverse.MOD_ID, "can_wear_costumes");
    public static final TagKey<EntityType<?>> DAMAGE_CANNOT_SHRINK = entityTypeTags(Marioverse.MOD_ID, "damage_cannot_shrink");
    public static final TagKey<EntityType<?>> FIREBALL_IMMUNE = entityTypeTags(Marioverse.MOD_ID, "fireball_immune");
    public static final TagKey<EntityType<?>> GOOMBA_ENTITIES = entityTypeTags(Marioverse.MOD_ID, "goombas");
    public static final TagKey<EntityType<?>> POWER_UP_ENTITIES = entityTypeTags(Marioverse.MOD_ID, "power_ups");
    public static final TagKey<EntityType<?>> QUESTION_BLOCK_CANNOT_SPAWN = entityTypeTags(Marioverse.MOD_ID, "question_block_cannot_spawn");

    public static TagKey<Block> blockTags(String id, String name) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(id, name));
    }

    public static TagKey<Item> itemTags(String id, String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(id, name));
    }

    public static TagKey<EntityType<?>> entityTypeTags(String id, String name) {
        return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(id, name));
    }
}
