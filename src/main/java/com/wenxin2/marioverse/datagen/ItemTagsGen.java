package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ItemTagsGen extends ItemTagsProvider {
    private static final ResourceLocation CREATE_CARDBOARD_HELMET = ResourceLocation.fromNamespaceAndPath("create", "cardboard_helmet");
    private static final ResourceLocation CREATE_SUPER_GLUE = ResourceLocation.fromNamespaceAndPath("create", "super_glue");
    private static final ResourceLocation SUPP_DEEPSLATE_LAMP = ResourceLocation.fromNamespaceAndPath("supplementaries", "deepslate_lamp");
    private static final ResourceLocation SUPP_ENDERMAN_HEAD = ResourceLocation.fromNamespaceAndPath("supplementaries", "enderman_head");
    private static final ResourceLocation VISTA_TV = ResourceLocation.fromNamespaceAndPath("vista", "television");

    public  ItemTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                       CompletableFuture<TagsProvider.TagLookup<Block>> blockTagProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagProvider, Marioverse.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        copy(TagRegistry.BONKABLE_BLOCKS, TagRegistry.BONKABLE_BLOCK_ITEMS);
        copy(TagRegistry.BRICK_PEDESTAL_BLOCKS, TagRegistry.BRICK_PEDESTAL_ITEMS);
        copy(TagRegistry.BRIDGE_BLOCKS, TagRegistry.BRIDGE_ITEMS);
        copy(TagRegistry.BRIDGE_STAIR_BLOCKS, TagRegistry.BRIDGE_STAIR_ITEMS);
        copy(TagRegistry.CALCITE_BLOCKS, TagRegistry.CALCITE_ITEMS);
        copy(TagRegistry.CALCITE_BRICK_BLOCKS, TagRegistry.CALCITE_BRICK_ITEMS);
        copy(TagRegistry.CALCITE_BRICK_PEDESTAL_BLOCKS, TagRegistry.CALCITE_BRICK_PEDESTAL_ITEMS);
        copy(TagRegistry.CHECKPOINT_FLAG_BLOCKS, TagRegistry.CHECKPOINT_FLAG_ITEMS);
        copy(TagRegistry.CHISELED_CALCITE_BRICK_BLOCKS, TagRegistry.CHISELED_CALCITE_BRICK_ITEMS);
        copy(TagRegistry.CRACKED_CALCITE_BRICK_BLOCKS, TagRegistry.CRACKED_CALCITE_BRICK_ITEMS);
        copy(TagRegistry.DYEABLE_CHECKPOINT_FLAG_BLOCKS, TagRegistry.DYEABLE_CHECKPOINT_FLAG_ITEMS);
        copy(TagRegistry.DYEABLE_GOAL_POLE_BLOCKS, TagRegistry.DYEABLE_GOAL_POLE_ITEMS);
        copy(TagRegistry.DYEABLE_WARP_PIPE_BLOCKS, TagRegistry.DYEABLE_WARP_PIPE_ITEMS);
        copy(TagRegistry.DYED_CALCITE_BLOCKS, TagRegistry.DYED_CALCITE_ITEMS);
        copy(TagRegistry.FLAMMABLE_WOODEN_BRIDGE_BLOCKS, TagRegistry.FLAMMABLE_WOODEN_BRIDGE_ITEMS);
        copy(TagRegistry.FLAMMABLE_WOODEN_BRIDGE_STAIR_BLOCKS, TagRegistry.FLAMMABLE_WOODEN_BRIDGE_STAIR_ITEMS);
        copy(TagRegistry.GOAL_POLE_BLOCKS, TagRegistry.GOAL_POLE_ITEMS);
        copy(TagRegistry.INVISIBLE_QUESTION_BLOCKS, TagRegistry.INVISIBLE_QUESTION_BLOCK_ITEMS);
        copy(TagRegistry.POLISHED_CALCITE_BLOCKS, TagRegistry.POLISHED_CALCITE_ITEMS);
        copy(TagRegistry.QUESTION_BLOCKS, TagRegistry.QUESTION_BLOCK_ITEMS);
        copy(TagRegistry.QUESTION_PANEL_BLOCKS, TagRegistry.QUESTION_PANELS_ITEMS);
        copy(TagRegistry.SMASHABLE_BLOCKS, TagRegistry.SMASHABLE_BLOCK_ITEMS);
        copy(TagRegistry.STORAGE_BRICK_BLOCKS, TagRegistry.STORAGE_BRICK_ITEMS);
        copy(TagRegistry.STORAGE_CALCITE_BRICK_BLOCKS, TagRegistry.STORAGE_CALCITE_BRICK_ITEMS);
        copy(TagRegistry.WARP_PIPE_BLOCKS, TagRegistry.WARP_PIPE_ITEMS);
        copy(TagRegistry.WOODEN_BRIDGE_BLOCKS, TagRegistry.WOODEN_BRIDGE_ITEMS);
        copy(TagRegistry.WOODEN_BRIDGE_STAIR_BLOCKS, TagRegistry.WOODEN_BRIDGE_STAIR_ITEMS);

        copy(Tags.Blocks.PUMPKINS_CARVED, Tags.Items.PUMPKINS_CARVED);
        copy(Tags.Blocks.PUMPKINS_JACK_O_LANTERNS, Tags.Items.PUMPKINS_JACK_O_LANTERNS);
        copy(Tags.Blocks.STONES, Tags.Items.STONES);

        copy(BlockTags.SLABS, ItemTags.SLABS);
        copy(BlockTags.STAIRS, ItemTags.STAIRS);
        copy(BlockTags.STONE_BUTTONS, ItemTags.STONE_BUTTONS);
        copy(BlockTags.WALLS, ItemTags.WALLS);
        copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);

        for (DyeColor color : DyeColor.values())
            copy(TagRegistry.blockTags("c", "dyed/" + color), TagRegistry.itemTags("c", "dyed/" + color));

        tag(ItemTags.DECORATED_POT_SHERDS)
                .add(ItemRegistry.BOWSER_POTTERY_SHERD.get())
                .add(ItemRegistry.PLUMBER_POTTERY_SHERD.get());

        tag(ItemTags.DURABILITY_ENCHANTABLE)
                .add(ItemRegistry.WRENCH.get())
                .add(ItemRegistry.WARP_DISRUPTOR.get());

        tag(ItemTags.EQUIPPABLE_ENCHANTABLE)
                .add(BlockRegistry.SPLUNKIN_CARVED_PUMPKIN.asItem());

        tag(ItemTags.FIRE_ASPECT_ENCHANTABLE)
                .add(ItemRegistry.WRENCH.get());

        tag(ItemTags.VANISHING_ENCHANTABLE)
                .add(BlockRegistry.SPLUNKIN_CARVED_PUMPKIN.asItem());

        tag(ItemTags.WEAPON_ENCHANTABLE)
                .add(ItemRegistry.WRENCH.get());

        tag(Tags.Items.ENCHANTABLES)
                .add(BlockRegistry.SPLUNKIN_CARVED_PUMPKIN.asItem());

        tag(ItemTags.FREEZE_IMMUNE_WEARABLES)
                .add(ItemRegistry.LUIGI_ICE_HAT.get())
                .add(ItemRegistry.LUIGI_ICE_PANTS.get())
                .add(ItemRegistry.LUIGI_ICE_SHIRT.get())
                .add(ItemRegistry.LUIGI_ICE_SHOES.get())
                .add(ItemRegistry.MARIO_ICE_HAT.get())
                .add(ItemRegistry.MARIO_ICE_PANTS.get())
                .add(ItemRegistry.MARIO_ICE_SHIRT.get())
                .add(ItemRegistry.MARIO_ICE_SHOES.get());

        tag(ItemTags.NON_FLAMMABLE_WOOD)
                .add(BlockRegistry.CRIMSON_STEM_BRIDGE.asItem())
                .add(BlockRegistry.WARPED_STEM_BRIDGE.asItem())
                .add(BlockRegistry.STRIPPED_CRIMSON_STEM_BRIDGE.asItem())
                .add(BlockRegistry.STRIPPED_WARPED_STEM_BRIDGE.asItem());

        tag(ItemTags.PIGLIN_LOVED)
                .add(BlockRegistry.COIN.asItem())
                .add(BlockRegistry.FUNGAL_QUESTION_BLOCK.asItem())
                .add(BlockRegistry.FUNGAL_QUESTION_PANEL.asItem())
                .add(ItemRegistry.GOLDEN_KOOPA_SHOES.get())
                .add(ItemRegistry.GOLD_KOOPA_SHELL.get())
                .add(ItemRegistry.GOLD_KOOPA_TROOPA_SPAWN_EGG.get())
                .add(ItemRegistry.PEACH_CROWN.get())
                .add(BlockRegistry.INVISIBLE_FUNGAL_QUESTION_BLOCK.asItem())
                .add(BlockRegistry.STAR_COIN.asItem())
                .add(ItemRegistry.SUPER_STAR.get());

        tag(ItemTags.STONE_CRAFTING_MATERIALS)
                .add(BlockRegistry.DEEP_FUNGAL_STONE.asItem())
                .add(BlockRegistry.FUNGAL_STONE.asItem());

        tag(ItemTags.STONE_TOOL_MATERIALS)
                .add(BlockRegistry.DEEP_FUNGAL_STONE.asItem())
                .add(BlockRegistry.FUNGAL_STONE.asItem());

        tag(ItemTags.TRIMMABLE_ARMOR);

        tag(TagRegistry.CHARMS).add(ItemRegistry.ONE_UP_MUSHROOM.get());

        tag(ItemTags.HEAD_ARMOR).addTag(TagRegistry.HATS);

        tag(ItemTags.CHEST_ARMOR).addTag(TagRegistry.SHIRTS);

        tag(ItemTags.LEG_ARMOR).addTag(TagRegistry.PANTS);

        tag(ItemTags.FOOT_ARMOR).addTag(TagRegistry.SHOES);

        tag(TagRegistry.COSTUME_HAT).addTag(TagRegistry.HATS);

        tag(TagRegistry.COSTUME_SHIRT).addTag(TagRegistry.SHIRTS);

        tag(TagRegistry.COSTUME_PANTS).addTag(TagRegistry.PANTS);

        tag(TagRegistry.COSTUME_SHOES).addTag(TagRegistry.SHOES);

        tag(Tags.Items.MELEE_WEAPON_TOOLS)
                .add(ItemRegistry.WRENCH.get());

        tag(Tags.Items.TOOLS_WRENCH)
                .add(ItemRegistry.WRENCH.get());

        tag(TagRegistry.CAN_SELECT_CLEAR_WARP_PIPES)
                .addTag(ItemTags.AXES)
                .addTag(ItemTags.HOES)
                .addTag(ItemTags.PICKAXES)
                .addTag(ItemTags.SHOVELS)
                .add(ItemRegistry.WRENCH.get())
                .add(Items.DEBUG_STICK)
                .addOptional(CREATE_SUPER_GLUE);

        tag(TagRegistry.CAN_SELECT_WATER_SPOUTS)
                .add(Items.DEBUG_STICK)
                .addOptional(CREATE_SUPER_GLUE);

        tag(TagRegistry.CANNOT_PLACE_IN_CHECKPOINT_FLAGS);

        tag(TagRegistry.CANNOT_PLACE_IN_QUESTION_BLOCKS);

        tag(TagRegistry.HALLOWEEN_MASKS)
                .addTag(Tags.Items.PUMPKINS_CARVED)
                .addTag(Tags.Items.PUMPKINS_JACK_O_LANTERNS)
                .add(BlockRegistry.GLOW_BLOCK.asItem())
                .add(Items.SCULK_SHRIEKER)
                .addOptional(CREATE_CARDBOARD_HELMET)
                .addOptional(SUPP_DEEPSLATE_LAMP)
                .addOptional(SUPP_ENDERMAN_HEAD)
                .addOptional(VISTA_TV);

        tag(TagRegistry.KOOPA_SHELL_ITEMS)
                .add(ItemRegistry.GOLD_KOOPA_SHELL.get())
                .add(ItemRegistry.GREEN_KOOPA_SHELL.get())
                .add(ItemRegistry.RED_KOOPA_SHELL.get());

        tag(TagRegistry.KOOPA_TROOPA_SPAWN_EGGS)
                .add(ItemRegistry.GOLD_KOOPA_TROOPA_SPAWN_EGG.get())
                .add(ItemRegistry.GREEN_KOOPA_TROOPA_SPAWN_EGG.get())
                .add(ItemRegistry.RED_KOOPA_TROOPA_SPAWN_EGG.get());

        tag(TagRegistry.PIRANHA_FOOD)
                .addTag(ItemTags.FISHES)
                .addTag(ItemTags.MEAT)
                .add(Items.BONE_MEAL);

        tag(TagRegistry.WARP_FUEL)
                .addTag(Tags.Items.ENDER_PEARLS);

        tag(TagRegistry.WARP_PIPE_CANNOT_SPAWN_ITEMS)
                .addTag(Tags.Items.DYES)
                .addTag(Tags.Items.TOOLS_WRENCH)
                .addTag(TagRegistry.WARP_PIPE_ITEMS)
                .add(ItemRegistry.WRENCH.get())
                .add(ItemRegistry.WARP_DISRUPTOR.get())
                .add(Items.DEBUG_STICK)
                .add(Items.GLOW_INK_SAC)
                .add(Items.INK_SAC);

        tag(TagRegistry.WRENCHES)
                .add(ItemRegistry.WRENCH.get());

        tag(TagRegistry.KOOPA_SHOES)
                .add(ItemRegistry.GOLDEN_KOOPA_SHOES.get())
                .add(ItemRegistry.GREEN_KOOPA_SHOES.get())
                .add(ItemRegistry.RED_KOOPA_SHOES.get())
                .add(ItemRegistry.WHITE_KOOPA_SHOES.get());

        tag(TagRegistry.COSTUMES)
                .addTag(TagRegistry.LUIGI_COSTUMES)
                .addTag(TagRegistry.MARIO_COSTUMES)
                .addTag(TagRegistry.PEACH_COSTUMES);

        tag(TagRegistry.FIRE_COSTUMES)
                .addTag(TagRegistry.LUIGI_FIRE_COSTUMES)
                .addTag(TagRegistry.MARIO_FIRE_COSTUMES)
                .addTag(TagRegistry.PEACH_FIRE_COSTUMES);

        tag(TagRegistry.ICE_COSTUMES)
                .addTag(TagRegistry.LUIGI_ICE_COSTUMES)
                .addTag(TagRegistry.MARIO_ICE_COSTUMES)
                .addTag(TagRegistry.PEACH_ICE_COSTUMES);

        tag(TagRegistry.POWER_UP_COSTUMES)
                .addTag(TagRegistry.LUIGI_POWER_UP_COSTUMES)
                .addTag(TagRegistry.MARIO_POWER_UP_COSTUMES)
                .addTag(TagRegistry.PEACH_POWER_UP_COSTUMES);

        tag(TagRegistry.POWER_UP_ITEMS)
                .add(ItemRegistry.DASH_MUSHROOM.get())
                .add(ItemRegistry.FIRE_FLOWER.get())
                .add(ItemRegistry.ICE_FLOWER.get())
                .add(ItemRegistry.SUPER_MUSHROOM.get())
                .add(ItemRegistry.ONE_UP_MUSHROOM.get())
                .add(ItemRegistry.SUPER_STAR.get());

        tag(TagRegistry.HATS)
                .addTag(TagRegistry.LUIGI_HATS)
                .addTag(TagRegistry.MARIO_HATS)
                .addTag(TagRegistry.PEACH_HATS);

        tag(TagRegistry.PANTS)
                .addTag(TagRegistry.LUIGI_PANTS)
                .addTag(TagRegistry.MARIO_PANTS)
                .addTag(TagRegistry.PEACH_PANTS);

        tag(TagRegistry.SHIRTS)
                .addTag(TagRegistry.LUIGI_SHIRTS)
                .addTag(TagRegistry.MARIO_SHIRTS)
                .addTag(TagRegistry.PEACH_SHIRTS);

        tag(TagRegistry.SHOES)
                .addTag(TagRegistry.KOOPA_SHOES)
                .addTag(TagRegistry.LUIGI_SHOES)
                .addTag(TagRegistry.MARIO_SHOES)
                .addTag(TagRegistry.PEACH_SHOES);

        tag(TagRegistry.MARIO_COSTUMES)
                .addTag(TagRegistry.MARIO_FIRE_COSTUMES)
                .addTag(TagRegistry.MARIO_ICE_COSTUMES)
                .add(ItemRegistry.MARIO_HAT.get())
                .add(ItemRegistry.MARIO_PANTS.get())
                .add(ItemRegistry.MARIO_SHIRT.get())
                .add(ItemRegistry.MARIO_SHOES.get());

        tag(TagRegistry.MARIO_FIRE_COSTUMES)
                .add(ItemRegistry.MARIO_FIRE_HAT.get())
                .add(ItemRegistry.MARIO_FIRE_PANTS.get())
                .add(ItemRegistry.MARIO_FIRE_SHIRT.get())
                .add(ItemRegistry.MARIO_FIRE_SHOES.get());

        tag(TagRegistry.MARIO_ICE_COSTUMES)
                .add(ItemRegistry.MARIO_ICE_HAT.get())
                .add(ItemRegistry.MARIO_ICE_PANTS.get())
                .add(ItemRegistry.MARIO_ICE_SHIRT.get())
                .add(ItemRegistry.MARIO_ICE_SHOES.get());

        tag(TagRegistry.MARIO_POWER_UP_COSTUMES)
                .add(ItemRegistry.MARIO_FIRE_HAT.get())
                .add(ItemRegistry.MARIO_FIRE_PANTS.get())
                .add(ItemRegistry.MARIO_FIRE_SHIRT.get())
                .add(ItemRegistry.MARIO_FIRE_SHOES.get())
                .add(ItemRegistry.MARIO_ICE_HAT.get())
                .add(ItemRegistry.MARIO_ICE_PANTS.get())
                .add(ItemRegistry.MARIO_ICE_SHIRT.get())
                .add(ItemRegistry.MARIO_ICE_SHOES.get());

        tag(TagRegistry.MARIO_HATS)
                .add(ItemRegistry.MARIO_FIRE_HAT.get())
                .add(ItemRegistry.MARIO_ICE_HAT.get())
                .add(ItemRegistry.MARIO_HAT.get());

        tag(TagRegistry.MARIO_PANTS)
                .add(ItemRegistry.MARIO_FIRE_PANTS.get())
                .add(ItemRegistry.MARIO_ICE_PANTS.get())
                .add(ItemRegistry.MARIO_PANTS.get());

        tag(TagRegistry.MARIO_SHIRTS)
                .add(ItemRegistry.MARIO_FIRE_SHIRT.get())
                .add(ItemRegistry.MARIO_ICE_SHIRT.get())
                .add(ItemRegistry.MARIO_SHIRT.get());

        tag(TagRegistry.MARIO_SHOES)
                .add(ItemRegistry.MARIO_FIRE_SHOES.get())
                .add(ItemRegistry.MARIO_ICE_SHOES.get())
                .add(ItemRegistry.MARIO_SHOES.get());

        tag(TagRegistry.LUIGI_COSTUMES)
                .addTag(TagRegistry.LUIGI_FIRE_COSTUMES)
                .addTag(TagRegistry.LUIGI_ICE_COSTUMES)
                .add(ItemRegistry.LUIGI_HAT.get())
                .add(ItemRegistry.LUIGI_PANTS.get())
                .add(ItemRegistry.LUIGI_SHIRT.get())
                .add(ItemRegistry.LUIGI_SHOES.get());

        tag(TagRegistry.LUIGI_FIRE_COSTUMES)
                .add(ItemRegistry.LUIGI_FIRE_HAT.get())
                .add(ItemRegistry.LUIGI_FIRE_PANTS.get())
                .add(ItemRegistry.LUIGI_FIRE_SHIRT.get())
                .add(ItemRegistry.LUIGI_FIRE_SHOES.get());

        tag(TagRegistry.LUIGI_ICE_COSTUMES)
                .add(ItemRegistry.LUIGI_ICE_HAT.get())
                .add(ItemRegistry.LUIGI_ICE_PANTS.get())
                .add(ItemRegistry.LUIGI_ICE_SHIRT.get())
                .add(ItemRegistry.LUIGI_ICE_SHOES.get());

        tag(TagRegistry.LUIGI_POWER_UP_COSTUMES)
                .add(ItemRegistry.LUIGI_FIRE_HAT.get())
                .add(ItemRegistry.LUIGI_FIRE_PANTS.get())
                .add(ItemRegistry.LUIGI_FIRE_SHIRT.get())
                .add(ItemRegistry.LUIGI_FIRE_SHOES.get())
                .add(ItemRegistry.LUIGI_ICE_HAT.get())
                .add(ItemRegistry.LUIGI_ICE_PANTS.get())
                .add(ItemRegistry.LUIGI_ICE_SHIRT.get())
                .add(ItemRegistry.LUIGI_ICE_SHOES.get());

        tag(TagRegistry.LUIGI_HATS)
                .add(ItemRegistry.LUIGI_FIRE_HAT.get())
                .add(ItemRegistry.LUIGI_ICE_HAT.get())
                .add(ItemRegistry.LUIGI_HAT.get());

        tag(TagRegistry.LUIGI_PANTS)
                .add(ItemRegistry.LUIGI_FIRE_PANTS.get())
                .add(ItemRegistry.LUIGI_ICE_PANTS.get())
                .add(ItemRegistry.LUIGI_PANTS.get());

        tag(TagRegistry.LUIGI_SHIRTS)
                .add(ItemRegistry.LUIGI_FIRE_SHIRT.get())
                .add(ItemRegistry.LUIGI_ICE_SHIRT.get())
                .add(ItemRegistry.LUIGI_SHIRT.get());

        tag(TagRegistry.LUIGI_SHOES)
                .add(ItemRegistry.LUIGI_FIRE_SHOES.get())
                .add(ItemRegistry.LUIGI_ICE_SHOES.get())
                .add(ItemRegistry.LUIGI_SHOES.get());

        tag(TagRegistry.PEACH_COSTUMES)
                .addTag(TagRegistry.PEACH_FIRE_COSTUMES)
                .addTag(TagRegistry.PEACH_ICE_COSTUMES)
                .add(ItemRegistry.PEACH_BODICE.get())
                .add(ItemRegistry.PEACH_CROWN.get())
                .add(ItemRegistry.PEACH_DRESS.get())
                .add(ItemRegistry.PEACH_SHOES.get());

        tag(TagRegistry.PEACH_FIRE_COSTUMES)
                .add(ItemRegistry.PEACH_CROWN.get())
                .add(ItemRegistry.PEACH_FIRE_BODICE.get())
                .add(ItemRegistry.PEACH_FIRE_DRESS.get())
                .add(ItemRegistry.PEACH_FIRE_SHOES.get());

        tag(TagRegistry.PEACH_ICE_COSTUMES)
                .add(ItemRegistry.PEACH_CROWN.get())
                .add(ItemRegistry.PEACH_ICE_BODICE.get())
                .add(ItemRegistry.PEACH_ICE_DRESS.get())
                .add(ItemRegistry.PEACH_ICE_SHOES.get());

        tag(TagRegistry.PEACH_POWER_UP_COSTUMES)
                .add(ItemRegistry.PEACH_CROWN.get())
                .add(ItemRegistry.PEACH_FIRE_BODICE.get())
                .add(ItemRegistry.PEACH_FIRE_DRESS.get())
                .add(ItemRegistry.PEACH_FIRE_SHOES.get())
                .add(ItemRegistry.PEACH_ICE_BODICE.get())
                .add(ItemRegistry.PEACH_ICE_DRESS.get())
                .add(ItemRegistry.PEACH_ICE_SHOES.get());

        tag(TagRegistry.PEACH_HATS).add(ItemRegistry.PEACH_CROWN.get());

        tag(TagRegistry.PEACH_PANTS)
                .add(ItemRegistry.PEACH_DRESS.get())
                .add(ItemRegistry.PEACH_FIRE_DRESS.get())
                .add(ItemRegistry.PEACH_ICE_DRESS.get());

        tag(TagRegistry.PEACH_SHIRTS)
                .add(ItemRegistry.PEACH_BODICE.get())
                .add(ItemRegistry.PEACH_FIRE_BODICE.get())
                .add(ItemRegistry.PEACH_ICE_BODICE.get());

        tag(TagRegistry.PEACH_SHOES)
                .add(ItemRegistry.PEACH_FIRE_SHOES.get())
                .add(ItemRegistry.PEACH_ICE_SHOES.get())
                .add(ItemRegistry.PEACH_SHOES.get());
    }
}