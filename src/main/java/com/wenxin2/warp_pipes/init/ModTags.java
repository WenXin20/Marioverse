package com.wenxin2.warp_pipes.init;

import com.wenxin2.warp_pipes.WarpPipes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static final TagKey<Block> DYEABLE_WARP_PIPE_BLOCKS = blockTags(WarpPipes.MODID, "dyeable_warp_pipes");
    public static final TagKey<Block> WARP_PIPE_BLOCKS = blockTags(WarpPipes.MODID, "warp_pipes");
    public static final TagKey<Block> WRENCH_EFFIECIENT = blockTags(WarpPipes.MODID, "wrench_efficient");
    public static final TagKey<Item> DYEABLE_WARP_PIPE_ITEMS = itemTags(WarpPipes.MODID, "dyeable_warp_pipes");
    public static final TagKey<Item> WARP_PIPE_ITEMS = itemTags(WarpPipes.MODID, "warp_pipes");
    public static final TagKey<EntityType<?>> WARP_BlACKLIST = entityTypeTags(WarpPipes.MODID, "warp_blacklist");
    public static final TagKey<EntityType<?>> QUICK_TRAVEL_BlACKLIST = entityTypeTags(WarpPipes.MODID, "quick_travel_blacklist");

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
