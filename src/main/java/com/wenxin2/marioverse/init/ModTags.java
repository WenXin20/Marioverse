package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static final TagKey<Block> DYEABLE_WARP_PIPE_BLOCKS = blockTags(Marioverse.MOD_ID, "dyeable_marioverse");
    public static final TagKey<Block> WARP_PIPE_BLOCKS = blockTags(Marioverse.MOD_ID, "marioverse");
    public static final TagKey<Block> WRENCH_EFFICIENT = blockTags(Marioverse.MOD_ID, "wrench_efficient");
    public static final TagKey<Item> DYEABLE_WARP_PIPE_ITEMS = itemTags(Marioverse.MOD_ID, "dyeable_marioverse");
    public static final TagKey<Item> WARP_PIPE_ITEMS = itemTags(Marioverse.MOD_ID, "marioverse");
    public static final TagKey<EntityType<?>> WARP_BLACKLIST = entityTypeTags(Marioverse.MOD_ID, "warp_blacklist");
    public static final TagKey<EntityType<?>> QUESTION_BLOCK_BLACKLIST = entityTypeTags(Marioverse.MOD_ID, "question_block_blacklist");
    public static final TagKey<EntityType<?>> QUICK_TRAVEL_BLACKLIST = entityTypeTags(Marioverse.MOD_ID, "quick_travel_blacklist");

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
