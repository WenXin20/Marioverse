package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
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
    private static final ResourceLocation SIMULATED_HONEY_GLUE = ResourceLocation.fromNamespaceAndPath("simulated", "honey_glue");
    private static final ResourceLocation SUPP_DEEPSLATE_LAMP = ResourceLocation.fromNamespaceAndPath("supplementaries", "deepslate_lamp");
    private static final ResourceLocation SUPP_ENDERMAN_HEAD = ResourceLocation.fromNamespaceAndPath("supplementaries", "enderman_head");
    private static final ResourceLocation SUPP_SOAP = ResourceLocation.fromNamespaceAndPath("supplementaries", "soap");
    private static final ResourceLocation VISTA_TV = ResourceLocation.fromNamespaceAndPath("vista", "television");

    public  ItemTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                       CompletableFuture<TagsProvider.TagLookup<Block>> blockTagProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagProvider, Marioverse.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        copy(TagRegistry.ABILITY_BLOCKS, TagRegistry.ABILITY_BLOCK_ITEMS);
        copy(TagRegistry.ARROW_SIGNS, TagRegistry.ARROW_SIGN_ITEMS);
        copy(TagRegistry.BONKABLE_BLOCKS, TagRegistry.BONKABLE_BLOCK_ITEMS);
        copy(TagRegistry.BRICK_PEDESTAL_BLOCKS, TagRegistry.BRICK_PEDESTAL_ITEMS);
        copy(TagRegistry.BRIDGE_BLOCKS, TagRegistry.BRIDGE_ITEMS);
        copy(TagRegistry.BRIDGE_STAIR_BLOCKS, TagRegistry.BRIDGE_STAIR_ITEMS);
        copy(TagRegistry.CALCITE_BLOCKS, TagRegistry.CALCITE_ITEMS);
        copy(TagRegistry.CALCITE_BRICK_BLOCKS, TagRegistry.CALCITE_BRICK_ITEMS);
        copy(TagRegistry.CALCITE_BRICK_PEDESTAL_BLOCKS, TagRegistry.CALCITE_BRICK_PEDESTAL_ITEMS);
        copy(TagRegistry.CHECKPOINT_FLAG_BLOCKS, TagRegistry.CHECKPOINT_FLAG_ITEMS);
        copy(TagRegistry.CHISELED_CALCITE_BRICK_BLOCKS, TagRegistry.CHISELED_CALCITE_BRICK_ITEMS);
        copy(TagRegistry.CORAL_TOWER_BLOCKS, TagRegistry.CORAL_TOWERS);
        copy(TagRegistry.DEAD_CORAL_TOWER_BLOCKS, TagRegistry.DEAD_CORAL_TOWERS);
        copy(TagRegistry.CRACKED_CALCITE_BRICK_BLOCKS, TagRegistry.CRACKED_CALCITE_BRICK_ITEMS);
        copy(TagRegistry.DEATH_BLOCKS, TagRegistry.DEATH_BLOCK_ITEMS);
        copy(TagRegistry.DOTTED_LINE_BLOCKS, TagRegistry.DOTTED_LINE_BLOCK_ITEMS);
        copy(TagRegistry.DYEABLE_CHECKPOINT_FLAG_BLOCKS, TagRegistry.DYEABLE_CHECKPOINT_FLAG_ITEMS);
        copy(TagRegistry.DYEABLE_GOAL_POLE_BLOCKS, TagRegistry.DYEABLE_GOAL_POLE_ITEMS);
        copy(TagRegistry.DYEABLE_WARP_PIPE_BLOCKS, TagRegistry.DYEABLE_WARP_PIPE_ITEMS);
        copy(TagRegistry.DYED_CALCITE_BLOCKS, TagRegistry.DYED_CALCITE_ITEMS);
        copy(TagRegistry.DYED_PICKET_FENCES, TagRegistry.DYED_PICKET_FENCE_ITEMS);
        copy(TagRegistry.FLAMMABLE_BRIDGE_BLOCKS, TagRegistry.FLAMMABLE_BRIDGE_ITEMS);
        copy(TagRegistry.FLAMMABLE_BRIDGE_STAIR_BLOCKS, TagRegistry.FLAMMABLE_BRIDGE_STAIR_ITEMS);
        copy(TagRegistry.FLAMMABLE_HARD_BLOCKS, TagRegistry.FLAMMABLE_HARD_BLOCK_ITEMS);
        copy(TagRegistry.FLAMMABLE_HARD_SLABS, TagRegistry.FLAMMABLE_HARD_SLAB_ITEMS);
        copy(TagRegistry.FLAMMABLE_HARD_STAIRS, TagRegistry.FLAMMABLE_HARD_STAIR_ITEMS);
        copy(TagRegistry.FLAMMABLE_HARD_WALLS, TagRegistry.FLAMMABLE_HARD_WALL_ITEMS);
        copy(TagRegistry.FLAMMABLE_PICKET_FENCES, TagRegistry.FLAMMABLE_PICKET_FENCE_ITEMS);
        copy(TagRegistry.FLAMMABLE_PLATFORMS, TagRegistry.FLAMMABLE_PLATFORM_ITEMS);
        copy(TagRegistry.FLAMMABLE_WALLS, TagRegistry.FLAMMABLE_WALL_ITEMS);
        copy(TagRegistry.FLAMMABLE_WINDOWS, TagRegistry.FLAMMABLE_WINDOW_ITEMS);
        copy(TagRegistry.FLAMMABLE_WINDOW_PANES, TagRegistry.FLAMMABLE_WINDOW_PANE_ITEMS);
        copy(TagRegistry.GOAL_POLE_BLOCKS, TagRegistry.GOAL_POLE_ITEMS);
        copy(TagRegistry.HARD_BLOCKS, TagRegistry.HARD_BLOCK_ITEMS);
        copy(TagRegistry.HARD_SLABS, TagRegistry.HARD_SLAB_ITEMS);
        copy(TagRegistry.HARD_STAIRS, TagRegistry.HARD_STAIR_ITEMS);
        copy(TagRegistry.HARD_WALLS, TagRegistry.HARD_WALL_ITEMS);
        copy(TagRegistry.INVISIBLE_QUESTION_BLOCKS, TagRegistry.INVISIBLE_QUESTION_BLOCK_ITEMS);
        copy(TagRegistry.LARGE_ARROW_SIGNS, TagRegistry.LARGE_ARROW_SIGN_ITEMS);
        copy(TagRegistry.MUSHROOM_TRAMPOLINE_BLOCKS, TagRegistry.MUSHROOM_TRAMPOLINE_BLOCK_ITEMS);
        copy(TagRegistry.MUSHROOT_LOGS, TagRegistry.MUSHROOT_LOG_ITEMS);
        copy(TagRegistry.MUSHROOT_PLANKS, TagRegistry.MUSHROOT_PLANK_ITEMS);
        copy(TagRegistry.PICKET_FENCES, TagRegistry.PICKET_FENCE_ITEMS);
        copy(TagRegistry.PIPE_JUNCTION_BLOCKS, TagRegistry.PIPE_JUNCTION_BLOCK_ITEMS);
        copy(TagRegistry.PLATFORMS, TagRegistry.PLATFORM_ITEMS);
        copy(TagRegistry.POLISHED_CALCITE_BLOCKS, TagRegistry.POLISHED_CALCITE_ITEMS);
        copy(TagRegistry.QUESTION_BLOCKS, TagRegistry.QUESTION_BLOCK_ITEMS);
        copy(TagRegistry.QUESTION_PANEL_BLOCKS, TagRegistry.QUESTION_PANELS_ITEMS);
        copy(TagRegistry.SMASHABLE_BLOCKS, TagRegistry.SMASHABLE_BLOCK_ITEMS);
        copy(TagRegistry.STONE_HARD_BLOCKS, TagRegistry.STONE_HARD_BLOCK_ITEMS);
        copy(TagRegistry.STONE_HARD_SLABS, TagRegistry.STONE_HARD_SLAB_ITEMS);
        copy(TagRegistry.STONE_HARD_STAIRS, TagRegistry.STONE_HARD_STAIR_ITEMS);
        copy(TagRegistry.STONE_HARD_WALLS, TagRegistry.STONE_HARD_WALL_ITEMS);
        copy(TagRegistry.STORAGE_BRICK_BLOCKS, TagRegistry.STORAGE_BRICK_ITEMS);
        copy(TagRegistry.STORAGE_CALCITE_BRICK_BLOCKS, TagRegistry.STORAGE_CALCITE_BRICK_ITEMS);
        copy(TagRegistry.WARP_PIPE_BLOCKS, TagRegistry.WARP_PIPE_ITEMS);
        copy(TagRegistry.WINDOWS, TagRegistry.WINDOW_ITEMS);
        copy(TagRegistry.WINDOW_PANES, TagRegistry.WINDOW_PANE_ITEMS);
        copy(TagRegistry.WOODEN_BRIDGE_BLOCKS, TagRegistry.WOODEN_BRIDGE_ITEMS);
        copy(TagRegistry.WOODEN_BRIDGE_STAIR_BLOCKS, TagRegistry.WOODEN_BRIDGE_STAIR_ITEMS);
        copy(TagRegistry.WOODEN_HARD_BLOCKS, TagRegistry.WOODEN_HARD_BLOCK_ITEMS);
        copy(TagRegistry.WOODEN_HARD_SLABS, TagRegistry.WOODEN_HARD_SLAB_ITEMS);
        copy(TagRegistry.WOODEN_HARD_STAIRS, TagRegistry.WOODEN_HARD_STAIR_ITEMS);
        copy(TagRegistry.WOODEN_HARD_WALLS, TagRegistry.WOODEN_HARD_WALL_ITEMS);
        copy(TagRegistry.WOODEN_PICKET_FENCES, TagRegistry.WOODEN_PICKET_FENCE_ITEMS);
        copy(TagRegistry.WOODEN_PLATFORMS, TagRegistry.WOODEN_PLATFORM_ITEMS);
        copy(TagRegistry.WOODEN_WALLS, TagRegistry.WOODEN_WALL_ITEMS);
        copy(TagRegistry.WOODEN_WINDOWS, TagRegistry.WOODEN_WINDOW_ITEMS);
        copy(TagRegistry.WOODEN_WINDOW_PANES, TagRegistry.WOODEN_WINDOW_PANE_ITEMS);

        copy(Tags.Blocks.COBBLESTONES, Tags.Items.COBBLESTONES);
        copy(Tags.Blocks.FENCES_WOODEN, Tags.Items.FENCES_WOODEN);
        copy(Tags.Blocks.FENCE_GATES_WOODEN, Tags.Items.FENCE_GATES_WOODEN);
        copy(Tags.Blocks.GLASS_BLOCKS_COLORLESS, Tags.Items.GLASS_BLOCKS_COLORLESS);
        copy(Tags.Blocks.GLASS_PANES_COLORLESS, Tags.Items.GLASS_PANES_COLORLESS);
        copy(Tags.Blocks.PUMPKINS_CARVED, Tags.Items.PUMPKINS_CARVED);
        copy(Tags.Blocks.PUMPKINS_JACK_O_LANTERNS, Tags.Items.PUMPKINS_JACK_O_LANTERNS);
        copy(Tags.Blocks.STONES, Tags.Items.STONES);
        copy(Tags.Blocks.STRIPPED_LOGS, Tags.Items.STRIPPED_LOGS);
        copy(Tags.Blocks.STRIPPED_WOODS, Tags.Items.STRIPPED_WOODS);

        copy(BlockTags.FENCE_GATES, ItemTags.FENCE_GATES);
        copy(BlockTags.FLOWERS, ItemTags.FLOWERS);
        copy(BlockTags.LEAVES, ItemTags.LEAVES);
        copy(BlockTags.LOGS, ItemTags.LOGS);
        copy(BlockTags.PLANKS, ItemTags.PLANKS);
        copy(BlockTags.SLABS, ItemTags.SLABS);
        copy(BlockTags.STAIRS, ItemTags.STAIRS);
        copy(BlockTags.STONE_BUTTONS, ItemTags.STONE_BUTTONS);
        copy(BlockTags.WALLS, ItemTags.WALLS);
        copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
        copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
        copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);

        for (DyeColor color : DyeColor.values())
            copy(TagRegistry.blockTags("c", "dyed/" + color), TagRegistry.itemTags("c", "dyed/" + color));
        copy(TagRegistry.blockTags("c", "glass_blocks/wooden"), TagRegistry.itemTags("c", "glass_blocks/wooden"));
        copy(TagRegistry.blockTags("c", "glass_panes/wooden"), TagRegistry.itemTags("c", "glass_panes/wooden"));

        tag(Tags.Items.ANIMAL_FOODS)
                .addTag(TagRegistry.PIRANHA_PLANT_FOOD);

        tag(Tags.Items.BUCKETS)
                .addTag(TagRegistry.itemTags("c", "buckets/quicksand"));

        tag(Tags.Items.BUCKETS_EMPTY)
                .add(ItemRegistry.PLASTIC_BUCKET.get());

        tag(Tags.Items.BUCKETS_ENTITY_WATER)
                .add(ItemRegistry.CHEEP_CHEEP_BUCKET.get())
                .add(ItemRegistry.DEEP_CHEEP_BUCKET.get())
                .add(ItemRegistry.EEP_CHEEP_BUCKET.get())
                .add(ItemRegistry.SPINY_CHEEP_CHEEP_BUCKET.get());

        tag(Tags.Items.BUCKETS_POWDER_SNOW)
                .add(ItemRegistry.PLASTIC_POWDER_SNOW_BUCKET.get());

        tag(TagRegistry.itemTags("c", "buckets/quicksand"))
                .add(ItemRegistry.QUICKSAND_BUCKET.get())
                .add(ItemRegistry.PLASTIC_QUICKSAND_BUCKET.get())
                .add(ItemRegistry.RED_QUICKSAND_BUCKET.get())
                .add(ItemRegistry.PLASTIC_RED_QUICKSAND_BUCKET.get());

        tag(Tags.Items.BUCKETS_WATER)
                .add(ItemRegistry.PLASTIC_WATER_BUCKET.get());

        tag(Tags.Items.ENCHANTABLES)
                .add(BlockRegistry.SPLUNKIN_CARVED_PUMPKIN.asItem());

        tag(Tags.Items.FENCES)
                .addTag(TagRegistry.PICKET_FENCE_ITEMS);

        tag(Tags.Items.FENCES_WOODEN)
                .addTag(TagRegistry.WOODEN_PICKET_FENCE_ITEMS);

        tag(Tags.Items.FOODS_COOKED_FISH)
                .add(ItemRegistry.COOKED_CHEEP_CHEEP.get())
                .add(ItemRegistry.COOKED_PORCUPUFFER.get())
                .add(ItemRegistry.COOKED_SPINY_CHEEP_CHEEP.get());

        tag(Tags.Items.FOODS_RAW_FISH)
                .addTag(TagRegistry.CHEEP_CHEEP_ITEMS)
                .add(ItemRegistry.PORCUPUFFER.get())
                .add(ItemRegistry.SPINY_CHEEP_CHEEP.get());

        tag(Tags.Items.MUSHROOMS)
                .add(BlockRegistry.BLUE_TRAMPOLINE_CAP.asItem())
                .add(BlockRegistry.RED_TRAMPOLINE_CAP.asItem());

        tag(ItemTags.ARMOR_ENCHANTABLE)
                .add(ItemRegistry.BODICE.get())
                .add(ItemRegistry.CROWN.get())
                .add(ItemRegistry.DRESS.get())
                .add(ItemRegistry.HAT.get())
                .add(ItemRegistry.HEELS.get())
                .add(ItemRegistry.PANTS.get())
                .add(ItemRegistry.PLASTIC_POWDER_SNOW_BUCKET.get())
                .add(ItemRegistry.PLASTIC_QUICKSAND_BUCKET.get())
                .add(ItemRegistry.PLASTIC_RED_QUICKSAND_BUCKET.get())
                .add(ItemRegistry.PLASTIC_WATER_BUCKET.get())
                .add(ItemRegistry.SHIRT.get())
                .add(ItemRegistry.SHOES.get());

        this.tag(ItemTags.BOATS)
                .add(ItemRegistry.MUSHROOT_BOAT.get());

        tag(ItemTags.CAT_FOOD)
                .addTag(TagRegistry.CHEEP_CHEEP_ITEMS)
                .add(ItemRegistry.PORCUPUFFER.get())
                .add(ItemRegistry.SPINY_CHEEP_CHEEP.get());

        this.tag(ItemTags.CHEST_BOATS)
                .add(ItemRegistry.MUSHROOT_CHEST_BOAT.get());

        tag(ItemTags.DECORATED_POT_SHERDS)
                .add(ItemRegistry.BOWSER_POTTERY_SHERD.get())
                .add(ItemRegistry.PLUMBER_POTTERY_SHERD.get());

        tag(ItemTags.DYEABLE)
                .add(ItemRegistry.BODICE.get())
                .add(ItemRegistry.CHRISTMAS_HAT.get())
                .add(ItemRegistry.CROWN.get())
                .add(ItemRegistry.DRESS.get())
                .add(ItemRegistry.HAT.get())
                .add(ItemRegistry.HEELS.get())
                .add(ItemRegistry.PANTS.get())
                .add(ItemRegistry.SHIRT.get())
                .add(ItemRegistry.SHOES.get());

        tag(ItemTags.DURABILITY_ENCHANTABLE)
                .add(ItemRegistry.BODICE.get())
                .add(ItemRegistry.CREATIVE_WRENCH.get())
                .add(ItemRegistry.CROWN.get())
                .add(ItemRegistry.DRESS.get())
                .add(ItemRegistry.HAT.get())
                .add(ItemRegistry.HEELS.get())
                .add(ItemRegistry.PANTS.get())
                .add(ItemRegistry.PLASTIC_POWDER_SNOW_BUCKET.get())
                .add(ItemRegistry.PLASTIC_QUICKSAND_BUCKET.get())
                .add(ItemRegistry.PLASTIC_RED_QUICKSAND_BUCKET.get())
                .add(ItemRegistry.PLASTIC_WATER_BUCKET.get())
                .add(ItemRegistry.SHIRT.get())
                .add(ItemRegistry.SHOES.get())
                .add(ItemRegistry.WRENCH.get())
                .add(ItemRegistry.WARP_DISRUPTOR.get());

        tag(ItemTags.EQUIPPABLE_ENCHANTABLE)
                .add(ItemRegistry.BODICE.get())
                .add(ItemRegistry.CROWN.get())
                .add(ItemRegistry.DRESS.get())
                .add(ItemRegistry.HAT.get())
                .add(ItemRegistry.HEELS.get())
                .add(ItemRegistry.PANTS.get())
                .add(ItemRegistry.PLASTIC_POWDER_SNOW_BUCKET.get())
                .add(ItemRegistry.PLASTIC_QUICKSAND_BUCKET.get())
                .add(ItemRegistry.PLASTIC_RED_QUICKSAND_BUCKET.get())
                .add(ItemRegistry.PLASTIC_WATER_BUCKET.get())
                .add(ItemRegistry.SHIRT.get())
                .add(ItemRegistry.SHOES.get())
                .add(BlockRegistry.SPLUNKIN_CARVED_PUMPKIN.asItem());

        tag(ItemTags.FENCES)
                .addTag(TagRegistry.PICKET_FENCE_ITEMS);

        tag(ItemTags.FIRE_ASPECT_ENCHANTABLE)
                .add(ItemRegistry.CREATIVE_WRENCH.get())
                .add(ItemRegistry.WRENCH.get());

        tag(ItemTags.FISHES)
                .addTag(TagRegistry.CHEEP_CHEEP_ITEMS)
                .add(ItemRegistry.COOKED_CHEEP_CHEEP.get())
                .add(ItemRegistry.COOKED_PORCUPUFFER.get())
                .add(ItemRegistry.COOKED_SPINY_CHEEP_CHEEP.get())
                .add(ItemRegistry.PORCUPUFFER.get())
                .add(ItemRegistry.SPINY_CHEEP_CHEEP.get());

        tag(ItemTags.FREEZE_IMMUNE_WEARABLES)
                .addTag(TagRegistry.POWER_UP_COSTUMES)
                .add(ItemRegistry.CHRISTMAS_HAT.get());

        tag(ItemTags.HANGING_SIGNS)
                .add(ItemRegistry.ACACIA_ARROW_SIGN.get())
                .add(ItemRegistry.BAMBOO_ARROW_SIGN.get())
                .add(ItemRegistry.BIRCH_ARROW_SIGN.get())
                .add(ItemRegistry.CHERRY_ARROW_SIGN.get())
                .add(ItemRegistry.DARK_OAK_ARROW_SIGN.get())
                .add(ItemRegistry.JUNGLE_ARROW_SIGN.get())
                .add(ItemRegistry.MANGROVE_ARROW_SIGN.get())
                .add(ItemRegistry.MUSHROOT_ARROW_SIGN.get())
                .add(ItemRegistry.MUSHROOT_HANGING_SIGN.get())
                .add(ItemRegistry.OAK_ARROW_SIGN.get())
                .add(ItemRegistry.SPRUCE_ARROW_SIGN.get());

        tag(ItemTags.NON_FLAMMABLE_WOOD)
                .add(BlockRegistry.CRIMSON_PICKET_FENCE.asItem())
                .add(BlockRegistry.CRIMSON_STEM_BRIDGE.asItem())
                .add(BlockRegistry.STRIPPED_CRIMSON_STEM_BRIDGE.asItem())
                .add(BlockRegistry.STRIPPED_WARPED_STEM_BRIDGE.asItem())
                .add(BlockRegistry.WARPED_PICKET_FENCE.asItem())
                .add(BlockRegistry.WARPED_STEM_BRIDGE.asItem())
                .add(ItemRegistry.CRIMSON_ARROW_SIGN.get())
                .add(ItemRegistry.LARGE_CRIMSON_ARROW_SIGN.get())
                .add(ItemRegistry.LARGE_WARPED_ARROW_SIGN.get())
                .add(ItemRegistry.WARPED_ARROW_SIGN.get());

        tag(ItemTags.OCELOT_FOOD)
                .addTag(TagRegistry.CHEEP_CHEEP_ITEMS)
                .add(ItemRegistry.PORCUPUFFER.get())
                .add(ItemRegistry.SPINY_CHEEP_CHEEP.get());

        tag(ItemTags.PIGLIN_LOVED)
                .add(BlockRegistry.COIN.asItem())
                .add(ItemRegistry.CROWN.get())
                .add(BlockRegistry.FUNGAL_QUESTION_BLOCK.asItem())
                .add(BlockRegistry.FUNGAL_QUESTION_PANEL.asItem())
                .add(ItemRegistry.GOLDEN_KOOPA_SHOES.get())
                .add(ItemRegistry.GOLD_KOOPA_SHELL.get())
                .add(ItemRegistry.GOLD_KOOPA_TROOPA_SPAWN_EGG.get())
                .add(BlockRegistry.INVISIBLE_FUNGAL_QUESTION_BLOCK.asItem())
                .add(BlockRegistry.STAR_COIN.asItem())
                .add(ItemRegistry.SUPER_STAR.get())
                .add(ItemRegistry.SUPER_STAR_SPAWN_EGG.get());

        tag(ItemTags.SIGNS)
                .addTag(TagRegistry.WOODEN_ARROW_SIGN_ITEMS)
                .addTag(TagRegistry.WOODEN_LARGE_ARROW_SIGN_ITEMS);

        tag(ItemTags.STONE_CRAFTING_MATERIALS)
                .add(BlockRegistry.DEEP_FUNGAL_COBBLESTONE.asItem())
                .add(BlockRegistry.FUNGAL_COBBLESTONE.asItem())
                .add(BlockRegistry.ROCKY_DEEP_FUNGAL_STONE.asItem())
                .add(BlockRegistry.ROCKY_FUNGAL_STONE.asItem());

        tag(ItemTags.STONE_TOOL_MATERIALS)
                .add(BlockRegistry.DEEP_FUNGAL_COBBLESTONE.asItem())
                .add(BlockRegistry.FUNGAL_COBBLESTONE.asItem())
                .add(BlockRegistry.ROCKY_DEEP_FUNGAL_STONE.asItem())
                .add(BlockRegistry.ROCKY_FUNGAL_STONE.asItem());

        tag(ItemTags.TRIM_TEMPLATES)
                .add(ItemRegistry.LUIGI_ARMOR_TRIM_SMITHING_TEMPLATE.get())
                .add(ItemRegistry.MARIO_ARMOR_TRIM_SMITHING_TEMPLATE.get())
                .add(ItemRegistry.PRINCESS_ARMOR_TRIM_SMITHING_TEMPLATE.get())
                .add(ItemRegistry.WALUIGI_ARMOR_TRIM_SMITHING_TEMPLATE.get())
                .add(ItemRegistry.WARIO_ARMOR_TRIM_SMITHING_TEMPLATE.get());

        tag(ItemTags.TRIMMABLE_ARMOR)
                .add(ItemRegistry.BODICE.get())
                .add(ItemRegistry.CHRISTMAS_HAT.get())
                .add(ItemRegistry.CROWN.get())
                .add(ItemRegistry.DRESS.get())
                .add(ItemRegistry.GOLDEN_KOOPA_SHOES.get())
                .add(ItemRegistry.GREEN_KOOPA_SHOES.get())
                .add(ItemRegistry.HAT.get())
                .add(ItemRegistry.HEELS.get())
                .add(ItemRegistry.PANTS.get())
                .add(ItemRegistry.PLASTIC_BUCKET.get())
                .add(ItemRegistry.PLASTIC_POWDER_SNOW_BUCKET.get())
                .add(ItemRegistry.PLASTIC_QUICKSAND_BUCKET.get())
                .add(ItemRegistry.PLASTIC_RED_QUICKSAND_BUCKET.get())
                .add(ItemRegistry.PLASTIC_WATER_BUCKET.get())
                .add(ItemRegistry.RED_KOOPA_SHOES.get())
                .add(ItemRegistry.SHIRT.get())
                .add(ItemRegistry.SHOES.get())
                .add(ItemRegistry.WHITE_KOOPA_SHOES.get());

        tag(ItemTags.VANISHING_ENCHANTABLE)
                .add(ItemRegistry.BODICE.get())
                .add(ItemRegistry.CROWN.get())
                .add(ItemRegistry.DRESS.get())
                .add(ItemRegistry.HAT.get())
                .add(ItemRegistry.HEELS.get())
                .add(ItemRegistry.PANTS.get())
                .add(ItemRegistry.PLASTIC_POWDER_SNOW_BUCKET.get())
                .add(ItemRegistry.PLASTIC_QUICKSAND_BUCKET.get())
                .add(ItemRegistry.PLASTIC_RED_QUICKSAND_BUCKET.get())
                .add(ItemRegistry.PLASTIC_WATER_BUCKET.get())
                .add(ItemRegistry.SHIRT.get())
                .add(ItemRegistry.SHOES.get())
                .add(BlockRegistry.SPLUNKIN_CARVED_PUMPKIN.asItem());

        tag(ItemTags.WEAPON_ENCHANTABLE)
                .add(ItemRegistry.CREATIVE_WRENCH.get())
                .add(ItemRegistry.WRENCH.get());

        tag(ItemTags.WOODEN_FENCES)
                .addTag(TagRegistry.WOODEN_PICKET_FENCE_ITEMS);

        tag(TagRegistry.CHARMS_CURIO).add(ItemRegistry.ONE_UP_MUSHROOM.get());

        tag(ItemTags.HEAD_ARMOR).addTag(TagRegistry.HATS);

        tag(ItemTags.CHEST_ARMOR).addTag(TagRegistry.SHIRTS);

        tag(ItemTags.LEG_ARMOR).addTag(TagRegistry.PANTS);

        tag(ItemTags.FOOT_ARMOR).addTag(TagRegistry.SHOES);

        tag(TagRegistry.ARROW_ERASERS)
                .addTag(Tags.Items.TOOLS_BRUSH)
                .addTag(Tags.Items.TOOLS_SHEAR)
                .add(Items.SPONGE)
                .addOptional(SUPP_SOAP);

        tag(TagRegistry.COSTUME_HAT_CURIO)
                .addTag(TagRegistry.HATS);

        tag(TagRegistry.COSTUME_SHIRT_CURIO)
                .addTag(TagRegistry.SHIRTS);

        tag(TagRegistry.COSTUME_PANTS_CURIO)
                .addTag(TagRegistry.PANTS);

        tag(TagRegistry.COSTUME_SHOES_CURIO)
                .addTag(TagRegistry.SHOES);

        tag(Tags.Items.MELEE_WEAPON_TOOLS)
                .add(ItemRegistry.CREATIVE_WRENCH.get())
                .add(ItemRegistry.WRENCH.get());

        tag(Tags.Items.TOOLS_WRENCH)
                .add(ItemRegistry.CREATIVE_WRENCH.get())
                .add(ItemRegistry.WRENCH.get());

        tag(TagRegistry.BLOCK_SPAWNER_CANNOT_DISPLAY);

        tag(TagRegistry.CAN_SELECT_CLEAR_WARP_PIPES)
                .addTag(Tags.Items.TOOLS_WRENCH)
                .addTag(ItemTags.AXES)
                .addTag(ItemTags.HOES)
                .addTag(ItemTags.PICKAXES)
                .addTag(ItemTags.SHOVELS)
                .add(BlockRegistry.CLEAR_WARP_PIPE.asItem())
                .add(ItemRegistry.CREATIVE_WRENCH.get())
                .add(ItemRegistry.WRENCH.get())
                .add(Items.DEBUG_STICK)
                .addOptional(CREATE_SUPER_GLUE)
                .addOptional(SIMULATED_HONEY_GLUE);

        tag(TagRegistry.CAN_SELECT_WATER_SPOUTS)
                .add(Items.DEBUG_STICK)
                .addOptional(CREATE_SUPER_GLUE)
                .addOptional(SIMULATED_HONEY_GLUE);

        tag(TagRegistry.CANNOT_PLACE_IN_CHECKPOINT_FLAGS);

        tag(TagRegistry.CANNOT_PLACE_IN_QUESTION_BLOCKS);

        tag(TagRegistry.CHARACTER_TRIMS)
                .add(ItemRegistry.LUIGI_ARMOR_TRIM_SMITHING_TEMPLATE.get())
                .add(ItemRegistry.MARIO_ARMOR_TRIM_SMITHING_TEMPLATE.get())
                .add(ItemRegistry.PRINCESS_ARMOR_TRIM_SMITHING_TEMPLATE.get())
                .add(ItemRegistry.WALUIGI_ARMOR_TRIM_SMITHING_TEMPLATE.get())
                .add(ItemRegistry.WARIO_ARMOR_TRIM_SMITHING_TEMPLATE.get());

        tag(TagRegistry.CHEEP_CHEEP_ITEMS)
                .add(ItemRegistry.CHEEP_CHEEP.get())
                .add(ItemRegistry.COLD_CHEEP_CHEEP.get())
                .add(ItemRegistry.DEEP_CHEEP.get())
                .add(ItemRegistry.EEP_CHEEP.get())
                .add(ItemRegistry.WARM_CHEEP_CHEEP.get());

        tag(TagRegistry.CRAFTS_WARP_DOOR)
                .addTag(Tags.Items.ENDER_PEARLS);

        tag(TagRegistry.CRAFTS_WARP_PAINTING)
                .addTag(Tags.Items.ENDER_PEARLS);

        tag(TagRegistry.CRAFTS_WARP_TRAPDOOR)
                .addTag(Tags.Items.ENDER_PEARLS);

        tag(TagRegistry.FLAMMABLE_ARROW_SIGN_ITEMS)
                .add(ItemRegistry.ACACIA_ARROW_SIGN.get())
                .add(ItemRegistry.BAMBOO_ARROW_SIGN.get())
                .add(ItemRegistry.BIRCH_ARROW_SIGN.get())
                .add(ItemRegistry.CHERRY_ARROW_SIGN.get())
                .add(ItemRegistry.DARK_OAK_ARROW_SIGN.get())
                .add(ItemRegistry.JUNGLE_ARROW_SIGN.get())
                .add(ItemRegistry.MANGROVE_ARROW_SIGN.get())
                .add(ItemRegistry.MUSHROOT_ARROW_SIGN.get())
                .add(ItemRegistry.OAK_ARROW_SIGN.get())
                .add(ItemRegistry.SPRUCE_ARROW_SIGN.get());

        tag(TagRegistry.FLAMMABLE_LARGE_ARROW_SIGN_ITEMS)
                .add(ItemRegistry.LARGE_ACACIA_ARROW_SIGN.get())
                .add(ItemRegistry.LARGE_BAMBOO_ARROW_SIGN.get())
                .add(ItemRegistry.LARGE_BIRCH_ARROW_SIGN.get())
                .add(ItemRegistry.LARGE_CHERRY_ARROW_SIGN.get())
                .add(ItemRegistry.LARGE_DARK_OAK_ARROW_SIGN.get())
                .add(ItemRegistry.LARGE_JUNGLE_ARROW_SIGN.get())
                .add(ItemRegistry.LARGE_MANGROVE_ARROW_SIGN.get())
                .add(ItemRegistry.LARGE_MUSHROOT_ARROW_SIGN.get())
                .add(ItemRegistry.LARGE_OAK_ARROW_SIGN.get())
                .add(ItemRegistry.LARGE_SPRUCE_ARROW_SIGN.get());

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

        tag(TagRegistry.PIRANHA_PLANT_FOOD)
                .addTag(ItemTags.FISHES)
                .addTag(ItemTags.MEAT)
                .add(Items.BONE_MEAL);

        tag(TagRegistry.PORCUPUFFER_FOOD)
                .addTag(ItemTags.FISHES);

        tag(TagRegistry.WARP_PIPE_CANNOT_SPAWN_ITEMS)
                .addTag(Tags.Items.DYES)
                .addTag(Tags.Items.TOOLS_WRENCH)
                .addTag(TagRegistry.WARP_PIPE_ITEMS)
                .add(ItemRegistry.CREATIVE_WRENCH.get())
                .add(ItemRegistry.WRENCH.get())
                .add(ItemRegistry.WARP_DISRUPTOR.get())
                .add(Items.DEBUG_STICK)
                .add(Items.GLOW_INK_SAC)
                .add(Items.INK_SAC);

        tag(TagRegistry.WOODEN_ARROW_SIGN_ITEMS)
                .addTag(TagRegistry.FLAMMABLE_ARROW_SIGN_ITEMS)
                .add(ItemRegistry.CRIMSON_ARROW_SIGN.get())
                .add(ItemRegistry.WARPED_ARROW_SIGN.get());

        tag(TagRegistry.WOODEN_LARGE_ARROW_SIGN_ITEMS)
                .addTag(TagRegistry.FLAMMABLE_LARGE_ARROW_SIGN_ITEMS)
                .add(ItemRegistry.LARGE_CRIMSON_ARROW_SIGN.get())
                .add(ItemRegistry.LARGE_WARPED_ARROW_SIGN.get());

        tag(TagRegistry.WRENCHES)
                .add(ItemRegistry.CREATIVE_WRENCH.get())
                .add(ItemRegistry.WRENCH.get());

        tag(TagRegistry.KOOPA_SHOES)
                .add(ItemRegistry.GOLDEN_KOOPA_SHOES.get())
                .add(ItemRegistry.GREEN_KOOPA_SHOES.get())
                .add(ItemRegistry.RED_KOOPA_SHOES.get())
                .add(ItemRegistry.WHITE_KOOPA_SHOES.get());

        tag(TagRegistry.COSTUMES)
                .addTag(TagRegistry.FEMALE_COSTUMES)
                .addTag(TagRegistry.MALE_COSTUMES);

        tag(TagRegistry.POWER_UP_COSTUMES)
                .addTag(TagRegistry.FEMALE_COSTUMES)
                .addTag(TagRegistry.MALE_COSTUMES)
                .addTag(TagRegistry.POWER_UP_HAT_COSTUMES)
                .addTag(TagRegistry.POWER_UP_PANTS_COSTUMES)
                .addTag(TagRegistry.POWER_UP_SHIRT_COSTUMES)
                .addTag(TagRegistry.POWER_UP_SHOES_COSTUMES);

        tag(TagRegistry.POWER_UP_HAT_COSTUMES)
                .add(ItemRegistry.CROWN.get())
                .add(ItemRegistry.HAT.get());

        tag(TagRegistry.POWER_UP_PANTS_COSTUMES)
                .add(ItemRegistry.DRESS.get())
                .add(ItemRegistry.PANTS.get());

        tag(TagRegistry.POWER_UP_SHIRT_COSTUMES)
                .add(ItemRegistry.BODICE.get())
                .add(ItemRegistry.SHIRT.get());

        tag(TagRegistry.POWER_UP_SHOES_COSTUMES)
                .add(ItemRegistry.HEELS.get())
                .add(ItemRegistry.SHOES.get());

        tag(TagRegistry.POWER_UP_ITEMS)
                .add(ItemRegistry.DASH_MUSHROOM.get())
                .add(ItemRegistry.FIRE_FLOWER.get())
                .add(ItemRegistry.ICE_FLOWER.get())
                .add(ItemRegistry.MEGA_MUSHROOM.get())
                .add(ItemRegistry.MINI_MUSHROOM.get())
                .add(ItemRegistry.SUPER_MUSHROOM.get())
                .add(ItemRegistry.ONE_UP_MUSHROOM.get())
                .add(ItemRegistry.SUPER_STAR.get());

        tag(TagRegistry.POWER_UP_SPAWN_EGGS)
                .add(ItemRegistry.DASH_MUSHROOM_SPAWN_EGG.get())
                .add(ItemRegistry.FIRE_FLOWER_SPAWN_EGG.get())
                .add(ItemRegistry.ICE_FLOWER_SPAWN_EGG.get())
                .add(ItemRegistry.MEGA_MUSHROOM_SPAWN_EGG.get())
                .add(ItemRegistry.MINI_MUSHROOM_SPAWN_EGG.get())
                .add(ItemRegistry.SUPER_MUSHROOM_SPAWN_EGG.get())
                .add(ItemRegistry.ONE_UP_MUSHROOM_SPAWN_EGG.get())
                .add(ItemRegistry.SUPER_STAR_SPAWN_EGG.get());

        tag(TagRegistry.HATS)
                .add(ItemRegistry.CHRISTMAS_HAT.get())
                .add(ItemRegistry.CROWN.get())
                .add(ItemRegistry.HAT.get())
                .add(ItemRegistry.PLASTIC_BUCKET.get());

        tag(TagRegistry.PANTS)
                .add(ItemRegistry.DRESS.get())
                .add(ItemRegistry.PANTS.get());

        tag(TagRegistry.SHIRTS)
                .add(ItemRegistry.BODICE.get())
                .add(ItemRegistry.SHIRT.get());

        tag(TagRegistry.SHOES)
                .addTag(TagRegistry.KOOPA_SHOES)
                .add(ItemRegistry.HEELS.get())
                .add(ItemRegistry.SHOES.get());

        tag(TagRegistry.FEMALE_COSTUMES)
                .add(ItemRegistry.BODICE.get())
                .add(ItemRegistry.CROWN.get())
                .add(ItemRegistry.DRESS.get())
                .add(ItemRegistry.HEELS.get());

        tag(TagRegistry.MALE_COSTUMES)
                .add(ItemRegistry.HAT.get())
                .add(ItemRegistry.PANTS.get())
                .add(ItemRegistry.SHIRT.get())
                .add(ItemRegistry.SHOES.get());
    }
}