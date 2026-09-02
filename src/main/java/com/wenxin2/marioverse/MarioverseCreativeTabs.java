package com.wenxin2.marioverse;

import com.wenxin2.marioverse.blocks.states.ArrowDirection;
import com.wenxin2.marioverse.entities.variants.CheepCheepVariants;
import com.wenxin2.marioverse.entities.variants.PiranhaPlantVariants;
import com.wenxin2.marioverse.entities.variants.PorcupufferVariants;
import com.wenxin2.marioverse.event_handlers.RegistryEventHandlers;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = Marioverse.MOD_ID)
public class MarioverseCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Marioverse.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MARIOVERSE_FUNCTIONAL_BLOCKS_TAB = TABS.register("marioverse_functional_blocks_tab",
            () -> CreativeModeTab.builder().title(Component.translatable("creative_tab.marioverse_functional_blocks"))
            .icon(() -> new ItemStack(BlockRegistry.FUNGAL_QUESTION_BLOCK.get())).build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MARIOVERSE_BUILDING_BLOCKS_TAB = TABS.register("marioverse_building_blocks_tab",
            () -> CreativeModeTab.builder().title(Component.translatable("creative_tab.marioverse_building_blocks"))
            .icon(() -> new ItemStack(BlockRegistry.FUNGAL_COBBLESTONE.get())).build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MARIOVERSE_ITEMS_TAB = TABS.register("marioverse_items_tab",
            () -> CreativeModeTab.builder().title(Component.translatable("creative_tab.marioverse_items"))
            .icon(() -> new ItemStack(ItemRegistry.SUPER_MUSHROOM.get())).build());

    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == MARIOVERSE_ITEMS_TAB.getKey() && !ConfigRegistry.DISABLE_MARIOVERSE_TABS.get()) {
            add(event, ItemRegistry.WRENCH);
            add(event, ItemRegistry.CREATIVE_WRENCH);
            add(event, ItemRegistry.WARP_DISRUPTOR);

            add(event, ItemRegistry.SUPER_MUSHROOM);
            add(event, ItemRegistry.DASH_MUSHROOM);
            add(event, ItemRegistry.ONE_UP_MUSHROOM);
            add(event, ItemRegistry.MINI_MUSHROOM);
            add(event, ItemRegistry.MEGA_MUSHROOM);
            add(event, ItemRegistry.FIRE_FLOWER);
            add(event, ItemRegistry.ICE_FLOWER);
            add(event, ItemRegistry.SUPER_STAR);

            add(event, ItemRegistry.GREEN_KOOPA_SHELL);
            add(event, ItemRegistry.RED_KOOPA_SHELL);
            add(event, ItemRegistry.GOLD_KOOPA_SHELL);
            add(event, ItemRegistry.LARGE_SNOWBALL);
            add(event, ItemRegistry.PLASTIC_BUCKET);
            add(event, ItemRegistry.PLASTIC_WATER_BUCKET);
            add(event, ItemRegistry.PLASTIC_POWDER_SNOW_BUCKET);
            add(event, ItemRegistry.PLASTIC_QUICKSAND_BUCKET);
            add(event, ItemRegistry.PLASTIC_RED_QUICKSAND_BUCKET);
            add(event, ItemRegistry.QUICKSAND_BUCKET);
            add(event, ItemRegistry.RED_QUICKSAND_BUCKET);
            add(event, ItemRegistry.CHEEP_CHEEP_BUCKET);
            addBucket(event, ItemRegistry.CHEEP_CHEEP_BUCKET, tag -> tag.putString("Variant", "cold"));
            addBucket(event, ItemRegistry.CHEEP_CHEEP_BUCKET, tag -> tag.putString("Variant", "warm"));
            add(event, ItemRegistry.EEP_CHEEP_BUCKET);
            add(event, ItemRegistry.DEEP_CHEEP_BUCKET);
            add(event, ItemRegistry.SPINY_CHEEP_CHEEP_BUCKET);

            add(event, ItemRegistry.CHEEP_CHEEP);
            add(event, ItemRegistry.COLD_CHEEP_CHEEP);
            add(event, ItemRegistry.WARM_CHEEP_CHEEP);
            add(event, ItemRegistry.EEP_CHEEP);
            add(event, ItemRegistry.DEEP_CHEEP);
            add(event, ItemRegistry.COOKED_CHEEP_CHEEP);
            add(event, ItemRegistry.SPINY_CHEEP_CHEEP);
            add(event, ItemRegistry.COOKED_SPINY_CHEEP_CHEEP);
            add(event, ItemRegistry.PORCUPUFFER);
            add(event, ItemRegistry.COOKED_PORCUPUFFER);

            add(event, ItemRegistry.PIRANHA_PLANT_POD);
            add(event, ItemRegistry.PIRANHA_PLANT_POD, DataComponentRegistry.VARIANT.get(), PiranhaPlantVariants.TROPICAL);
            add(event, ItemRegistry.PIRANHA_PLANT_POD, DataComponentRegistry.VARIANT.get(), PiranhaPlantVariants.CAVE);
            add(event, ItemRegistry.PIRANHA_PLANT_POD, DataComponentRegistry.VARIANT.get(), PiranhaPlantVariants.DEEP_CAVE);
            add(event, ItemRegistry.PIRANHA_PLANT_POD, DataComponentRegistry.VARIANT.get(), PiranhaPlantVariants.CHOMPER);

            add(event, ItemRegistry.MARIO_ARMOR_TRIM_SMITHING_TEMPLATE);
            add(event, ItemRegistry.LUIGI_ARMOR_TRIM_SMITHING_TEMPLATE);
            add(event, ItemRegistry.WARIO_ARMOR_TRIM_SMITHING_TEMPLATE);
            add(event, ItemRegistry.WALUIGI_ARMOR_TRIM_SMITHING_TEMPLATE);
            add(event, ItemRegistry.PRINCESS_ARMOR_TRIM_SMITHING_TEMPLATE);

            add(event, ItemRegistry.CHRISTMAS_HAT);

            add(event, ItemRegistry.HAT);
            add(event, ItemRegistry.SHIRT);
            add(event, ItemRegistry.PANTS);
            add(event, ItemRegistry.SHOES);

            add(event, ItemRegistry.HAT, DataComponents.DYED_COLOR, new DyedItemColor(0x43B237, true));
            add(event, ItemRegistry.SHIRT, DataComponents.DYED_COLOR, new DyedItemColor(0x43B237, true));
            add(event, ItemRegistry.PANTS, DataComponents.DYED_COLOR, new DyedItemColor(0x43B237, true));
            add(event, ItemRegistry.SHOES, DataComponents.DYED_COLOR, new DyedItemColor(0x9C6042, true));

            add(event, ItemRegistry.HAT, DataComponents.DYED_COLOR, new DyedItemColor(0xFFCD00, true));
            add(event, ItemRegistry.SHIRT, DataComponents.DYED_COLOR, new DyedItemColor(0xFFCD00, true));
            add(event, ItemRegistry.PANTS, DataComponents.DYED_COLOR, new DyedItemColor(0xFFCD00, true));
            add(event, ItemRegistry.SHOES, DataComponents.DYED_COLOR, new DyedItemColor(0xA94536, true));

            add(event, ItemRegistry.HAT, DataComponents.DYED_COLOR, new DyedItemColor(0x8800FD, true));
            add(event, ItemRegistry.SHIRT, DataComponents.DYED_COLOR, new DyedItemColor(0x8800FD, true));
            add(event, ItemRegistry.PANTS, DataComponents.DYED_COLOR, new DyedItemColor(0x8800FD, true));
            add(event, ItemRegistry.SHOES, DataComponents.DYED_COLOR, new DyedItemColor(0xA94537, true));

            add(event, ItemRegistry.CROWN);
            add(event, ItemRegistry.BODICE);
            add(event, ItemRegistry.DRESS);
            add(event, ItemRegistry.HEELS);

            add(event, ItemRegistry.CROWN, DataComponents.DYED_COLOR, new DyedItemColor(0xA4FDF0, true));
            add(event, ItemRegistry.BODICE, DataComponents.DYED_COLOR, new DyedItemColor(0xFF992B, true));
            add(event, ItemRegistry.DRESS, DataComponents.DYED_COLOR, new DyedItemColor(0xFF992B, true));
            add(event, ItemRegistry.HEELS, DataComponents.DYED_COLOR, new DyedItemColor(0xFF992B, true));

            add(event, ItemRegistry.CROWN, DataComponents.DYED_COLOR, new DyedItemColor(0xFF647E, true));
            add(event, ItemRegistry.BODICE, DataComponents.DYED_COLOR, new DyedItemColor(0x89F4EB, true));
            add(event, ItemRegistry.DRESS, DataComponents.DYED_COLOR, new DyedItemColor(0x89F4EB, true));
            add(event, ItemRegistry.HEELS, DataComponents.DYED_COLOR, new DyedItemColor(0x89F4EB, true));

            add(event, ItemRegistry.GREEN_KOOPA_SHOES);
            add(event, ItemRegistry.RED_KOOPA_SHOES);
            add(event, ItemRegistry.GOLDEN_KOOPA_SHOES);
            add(event, ItemRegistry.WHITE_KOOPA_SHOES);

            add(event, ItemRegistry.BOWSER_BANNER_PATTERN);
            add(event, ItemRegistry.PLUMBER_BANNER_PATTERN);
            add(event, ItemRegistry.BOWSER_POTTERY_SHERD);
            add(event, ItemRegistry.PLUMBER_POTTERY_SHERD);

            add(event, ItemRegistry.MUSHROOT_BOAT);
            add(event, ItemRegistry.MUSHROOT_CHEST_BOAT);

            add(event, ItemRegistry.SUPER_MUSHROOM_SPAWN_EGG);
            add(event, ItemRegistry.DASH_MUSHROOM_SPAWN_EGG);
            add(event, ItemRegistry.ONE_UP_MUSHROOM_SPAWN_EGG);
            add(event, ItemRegistry.MINI_MUSHROOM_SPAWN_EGG);
            add(event, ItemRegistry.MEGA_MUSHROOM_SPAWN_EGG);
            add(event, ItemRegistry.FIRE_FLOWER_SPAWN_EGG);
            add(event, ItemRegistry.ICE_FLOWER_SPAWN_EGG);
            add(event, ItemRegistry.SUPER_STAR_SPAWN_EGG);
            add(event, ItemRegistry.MINI_GOOMBA_SPAWN_EGG);
            add(event, ItemRegistry.GOOMBA_SPAWN_EGG);
            add(event, ItemRegistry.GOOMBA_SPAWN_EGG, Component.literal("Goombella"));
            add(event, ItemRegistry.HEFTY_GOOMBA_SPAWN_EGG);
            add(event, ItemRegistry.MEGA_GOOMBA_SPAWN_EGG);
            add(event, ItemRegistry.FIRE_GOOMBA_SPAWN_EGG);
            add(event, ItemRegistry.SPLUNKIN_SPAWN_EGG);
            add(event, ItemRegistry.GREEN_KOOPA_TROOPA_SPAWN_EGG);
            add(event, ItemRegistry.RED_KOOPA_TROOPA_SPAWN_EGG);
            add(event, ItemRegistry.GOLD_KOOPA_TROOPA_SPAWN_EGG);
            add(event, ItemRegistry.DRY_BONES_SPAWN_EGG);
            add(event, ItemRegistry.POKEY_SPAWN_EGG);
            add(event, ItemRegistry.SNOW_POKEY_SPAWN_EGG);
            add(event, ItemRegistry.PIRANHA_PLANT_SPAWN_EGG);
            add(event, ItemRegistry.PIRANHA_PLANT_SPAWN_EGG, DataComponentRegistry.VARIANT.get(), PiranhaPlantVariants.TROPICAL);
            add(event, ItemRegistry.PIRANHA_PLANT_SPAWN_EGG, DataComponentRegistry.VARIANT.get(), PiranhaPlantVariants.CAVE);
            add(event, ItemRegistry.PIRANHA_PLANT_SPAWN_EGG, DataComponentRegistry.VARIANT.get(), PiranhaPlantVariants.DEEP_CAVE);
            add(event, ItemRegistry.PIRANHA_PLANT_SPAWN_EGG, DataComponentRegistry.VARIANT.get(), PiranhaPlantVariants.CHOMPER);
            add(event, ItemRegistry.BOO_SPAWN_EGG);
            add(event, ItemRegistry.CHEEP_CHEEP_SPAWN_EGG);
            add(event, ItemRegistry.CHEEP_CHEEP_SPAWN_EGG, DataComponentRegistry.VARIANT.get(), CheepCheepVariants.WARM);
            add(event, ItemRegistry.CHEEP_CHEEP_SPAWN_EGG, DataComponentRegistry.VARIANT.get(), CheepCheepVariants.COLD);
            add(event, ItemRegistry.EEP_CHEEP_SPAWN_EGG);
            add(event, ItemRegistry.DEEP_CHEEP_SPAWN_EGG);
            add(event, ItemRegistry.SPINY_CHEEP_CHEEP_SPAWN_EGG);
            add(event, ItemRegistry.PORCUPUFFER_SPAWN_EGG);
            add(event, ItemRegistry.PORCUPUFFER_SPAWN_EGG, DataComponentRegistry.VARIANT.get(), PorcupufferVariants.MRS_PUFF);
            add(event, ItemRegistry.PORCUPUFFER_SPAWN_EGG, DataComponentRegistry.VARIANT.get(), PorcupufferVariants.QWILFISH);
        }

        if (event.getTabKey() == MARIOVERSE_BUILDING_BLOCKS_TAB.getKey() && !ConfigRegistry.DISABLE_MARIOVERSE_TABS.get()) {
            add(event, BlockRegistry.STAR_COIN);
            add(event, BlockRegistry.COIN);
            add(event, BlockRegistry.DANGO_BLOSSOM);
            add(event, BlockRegistry.MUSHROOT_SAPLING);
            add(event, BlockRegistry.RED_TRAMPOLINE_CAP);
            add(event, BlockRegistry.BLUE_TRAMPOLINE_CAP);
            add(event, BlockRegistry.TUBE_CORAL_TOWER);
            add(event, BlockRegistry.BRAIN_CORAL_TOWER);
            add(event, BlockRegistry.BUBBLE_CORAL_TOWER);
            add(event, BlockRegistry.FIRE_CORAL_TOWER);
            add(event, BlockRegistry.HORN_CORAL_TOWER);
            add(event, BlockRegistry.DEAD_TUBE_CORAL_TOWER);
            add(event, BlockRegistry.DEAD_BRAIN_CORAL_TOWER);
            add(event, BlockRegistry.DEAD_BUBBLE_CORAL_TOWER);
            add(event, BlockRegistry.DEAD_FIRE_CORAL_TOWER);
            add(event, BlockRegistry.DEAD_HORN_CORAL_TOWER);

            add(event, BlockRegistry.MUSHROOT_LEAVES);
            add(event, BlockRegistry.MUSHROOT_LOG);
            add(event, BlockRegistry.MUSHROOT_WOOD);
            add(event, BlockRegistry.STRIPPED_MUSHROOT_LOG);
            add(event, BlockRegistry.STRIPPED_MUSHROOT_WOOD);
            add(event, BlockRegistry.MUSHROOT_PLANKS);
            add(event, BlockRegistry.MUSHROOT_STAIRS);
            add(event, BlockRegistry.MUSHROOT_SLAB);
            add(event, BlockRegistry.MUSHROOT_FENCE);
            add(event, BlockRegistry.MUSHROOT_FENCE_GATE);
            add(event, BlockRegistry.MUSHROOT_PRESSURE_PLATE);
            add(event, BlockRegistry.MUSHROOT_BUTTON);
            add(event, BlockRegistry.MUSHROOT_BOARDS);
            add(event, BlockRegistry.MUSHROOT_BOARD_STAIRS);
            add(event, BlockRegistry.MUSHROOT_BOARD_SLAB);
            add(event, BlockRegistry.MUSHROOT_BOARD_WALL);
            add(event, BlockRegistry.MUSHROOT_PANELS);
            add(event, BlockRegistry.MUSHROOT_PANEL_SLAB);
            add(event, BlockRegistry.MUSHROOT_PANEL_STAIRS);
            add(event, BlockRegistry.MUSHROOT_PANEL_WALL);
            add(event, BlockRegistry.HARD_MUSHROOT_BLOCK);
            add(event, BlockRegistry.HARD_MUSHROOT_SLAB);
            add(event, BlockRegistry.HARD_MUSHROOT_STAIRS);
            add(event, BlockRegistry.HARD_MUSHROOT_WALL);
            add(event, BlockRegistry.HARD_OAK_BLOCK);
            add(event, BlockRegistry.HARD_OAK_SLAB);
            add(event, BlockRegistry.HARD_OAK_STAIRS);
            add(event, BlockRegistry.HARD_OAK_WALL);
            add(event, BlockRegistry.HARD_SPRUCE_BLOCK);
            add(event, BlockRegistry.HARD_SPRUCE_SLAB);
            add(event, BlockRegistry.HARD_SPRUCE_STAIRS);
            add(event, BlockRegistry.HARD_SPRUCE_WALL);
            add(event, BlockRegistry.HARD_BIRCH_BLOCK);
            add(event, BlockRegistry.HARD_BIRCH_SLAB);
            add(event, BlockRegistry.HARD_BIRCH_STAIRS);
            add(event, BlockRegistry.HARD_BIRCH_WALL);
            add(event, BlockRegistry.HARD_JUNGLE_BLOCK);
            add(event, BlockRegistry.HARD_JUNGLE_SLAB);
            add(event, BlockRegistry.HARD_JUNGLE_STAIRS);
            add(event, BlockRegistry.HARD_JUNGLE_WALL);
            add(event, BlockRegistry.HARD_ACACIA_BLOCK);
            add(event, BlockRegistry.HARD_ACACIA_SLAB);
            add(event, BlockRegistry.HARD_ACACIA_STAIRS);
            add(event, BlockRegistry.HARD_ACACIA_WALL);
            add(event, BlockRegistry.HARD_DARK_OAK_BLOCK);
            add(event, BlockRegistry.HARD_DARK_OAK_SLAB);
            add(event, BlockRegistry.HARD_DARK_OAK_STAIRS);
            add(event, BlockRegistry.HARD_DARK_OAK_WALL);
            add(event, BlockRegistry.HARD_MANGROVE_BLOCK);
            add(event, BlockRegistry.HARD_MANGROVE_SLAB);
            add(event, BlockRegistry.HARD_MANGROVE_STAIRS);
            add(event, BlockRegistry.HARD_MANGROVE_WALL);
            add(event, BlockRegistry.HARD_CHERRY_BLOCK);
            add(event, BlockRegistry.HARD_CHERRY_SLAB);
            add(event, BlockRegistry.HARD_CHERRY_STAIRS);
            add(event, BlockRegistry.HARD_CHERRY_WALL);
            add(event, BlockRegistry.HARD_BAMBOO_BLOCK);
            add(event, BlockRegistry.HARD_BAMBOO_SLAB);
            add(event, BlockRegistry.HARD_BAMBOO_STAIRS);
            add(event, BlockRegistry.HARD_BAMBOO_WALL);
            add(event, BlockRegistry.HARD_CRIMSON_BLOCK);
            add(event, BlockRegistry.HARD_CRIMSON_SLAB);
            add(event, BlockRegistry.HARD_CRIMSON_STAIRS);
            add(event, BlockRegistry.HARD_CRIMSON_WALL);
            add(event, BlockRegistry.HARD_WARPED_BLOCK);
            add(event, BlockRegistry.HARD_WARPED_SLAB);
            add(event, BlockRegistry.HARD_WARPED_STAIRS);
            add(event, BlockRegistry.HARD_WARPED_WALL);
            add(event, BlockRegistry.MUSHROOT_FRAMED_WINDOW);
            add(event, BlockRegistry.MUSHROOT_FRAMED_WINDOW_PANE);
            add(event, ItemRegistry.MUSHROOT_SIGN);
            add(event, ItemRegistry.MUSHROOT_HANGING_SIGN);
            add(event, ItemRegistry.MUSHROOT_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.UP);
            add(event, ItemRegistry.MUSHROOT_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_RIGHT);
            add(event, ItemRegistry.MUSHROOT_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.RIGHT);
            add(event, ItemRegistry.MUSHROOT_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_RIGHT);
            add(event, ItemRegistry.MUSHROOT_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.DOWN);
            add(event, ItemRegistry.MUSHROOT_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_LEFT);
            add(event, ItemRegistry.MUSHROOT_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.LEFT);
            add(event, ItemRegistry.MUSHROOT_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_LEFT);
            add(event, ItemRegistry.MUSHROOT_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.NONE);
            add(event, ItemRegistry.LARGE_MUSHROOT_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.UP);
            add(event, ItemRegistry.LARGE_MUSHROOT_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_RIGHT);
            add(event, ItemRegistry.LARGE_MUSHROOT_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.RIGHT);
            add(event, ItemRegistry.LARGE_MUSHROOT_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_RIGHT);
            add(event, ItemRegistry.LARGE_MUSHROOT_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.DOWN);
            add(event, ItemRegistry.LARGE_MUSHROOT_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_LEFT);
            add(event, ItemRegistry.LARGE_MUSHROOT_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.LEFT);
            add(event, ItemRegistry.LARGE_MUSHROOT_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_LEFT);
            add(event, ItemRegistry.LARGE_MUSHROOT_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.NONE);

            add(event, ItemRegistry.OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.UP);
            add(event, ItemRegistry.OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_RIGHT);
            add(event, ItemRegistry.OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.RIGHT);
            add(event, ItemRegistry.OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_RIGHT);
            add(event, ItemRegistry.OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.DOWN);
            add(event, ItemRegistry.OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_LEFT);
            add(event, ItemRegistry.OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.LEFT);
            add(event, ItemRegistry.OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_LEFT);
            add(event, ItemRegistry.OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.NONE);
            add(event, ItemRegistry.LARGE_OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.UP);
            add(event, ItemRegistry.LARGE_OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_RIGHT);
            add(event, ItemRegistry.LARGE_OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.RIGHT);
            add(event, ItemRegistry.LARGE_OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_RIGHT);
            add(event, ItemRegistry.LARGE_OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.DOWN);
            add(event, ItemRegistry.LARGE_OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_LEFT);
            add(event, ItemRegistry.LARGE_OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.LEFT);
            add(event, ItemRegistry.LARGE_OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_LEFT);
            add(event, ItemRegistry.LARGE_OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.NONE);

            add(event, ItemRegistry.SPRUCE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.UP);
            add(event, ItemRegistry.SPRUCE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_RIGHT);
            add(event, ItemRegistry.SPRUCE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.RIGHT);
            add(event, ItemRegistry.SPRUCE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_RIGHT);
            add(event, ItemRegistry.SPRUCE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.DOWN);
            add(event, ItemRegistry.SPRUCE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_LEFT);
            add(event, ItemRegistry.SPRUCE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.LEFT);
            add(event, ItemRegistry.SPRUCE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_LEFT);
            add(event, ItemRegistry.SPRUCE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.NONE);
            add(event, ItemRegistry.LARGE_SPRUCE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.UP);
            add(event, ItemRegistry.LARGE_SPRUCE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_RIGHT);
            add(event, ItemRegistry.LARGE_SPRUCE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.RIGHT);
            add(event, ItemRegistry.LARGE_SPRUCE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_RIGHT);
            add(event, ItemRegistry.LARGE_SPRUCE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.DOWN);
            add(event, ItemRegistry.LARGE_SPRUCE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_LEFT);
            add(event, ItemRegistry.LARGE_SPRUCE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.LEFT);
            add(event, ItemRegistry.LARGE_SPRUCE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_LEFT);
            add(event, ItemRegistry.LARGE_SPRUCE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.NONE);

            add(event, ItemRegistry.BIRCH_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.UP);
            add(event, ItemRegistry.BIRCH_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_RIGHT);
            add(event, ItemRegistry.BIRCH_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.RIGHT);
            add(event, ItemRegistry.BIRCH_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_RIGHT);
            add(event, ItemRegistry.BIRCH_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.DOWN);
            add(event, ItemRegistry.BIRCH_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_LEFT);
            add(event, ItemRegistry.BIRCH_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.LEFT);
            add(event, ItemRegistry.BIRCH_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_LEFT);
            add(event, ItemRegistry.BIRCH_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.NONE);
            add(event, ItemRegistry.LARGE_BIRCH_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.UP);
            add(event, ItemRegistry.LARGE_BIRCH_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_RIGHT);
            add(event, ItemRegistry.LARGE_BIRCH_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.RIGHT);
            add(event, ItemRegistry.LARGE_BIRCH_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_RIGHT);
            add(event, ItemRegistry.LARGE_BIRCH_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.DOWN);
            add(event, ItemRegistry.LARGE_BIRCH_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_LEFT);
            add(event, ItemRegistry.LARGE_BIRCH_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.LEFT);
            add(event, ItemRegistry.LARGE_BIRCH_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_LEFT);
            add(event, ItemRegistry.LARGE_BIRCH_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.NONE);

            add(event, ItemRegistry.JUNGLE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.UP);
            add(event, ItemRegistry.JUNGLE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_RIGHT);
            add(event, ItemRegistry.JUNGLE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.RIGHT);
            add(event, ItemRegistry.JUNGLE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_RIGHT);
            add(event, ItemRegistry.JUNGLE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.DOWN);
            add(event, ItemRegistry.JUNGLE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_LEFT);
            add(event, ItemRegistry.JUNGLE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.LEFT);
            add(event, ItemRegistry.JUNGLE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_LEFT);
            add(event, ItemRegistry.JUNGLE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.NONE);
            add(event, ItemRegistry.LARGE_JUNGLE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.UP);
            add(event, ItemRegistry.LARGE_JUNGLE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_RIGHT);
            add(event, ItemRegistry.LARGE_JUNGLE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.RIGHT);
            add(event, ItemRegistry.LARGE_JUNGLE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_RIGHT);
            add(event, ItemRegistry.LARGE_JUNGLE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.DOWN);
            add(event, ItemRegistry.LARGE_JUNGLE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_LEFT);
            add(event, ItemRegistry.LARGE_JUNGLE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.LEFT);
            add(event, ItemRegistry.LARGE_JUNGLE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_LEFT);
            add(event, ItemRegistry.LARGE_JUNGLE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.NONE);

            add(event, ItemRegistry.ACACIA_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.UP);
            add(event, ItemRegistry.ACACIA_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_RIGHT);
            add(event, ItemRegistry.ACACIA_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.RIGHT);
            add(event, ItemRegistry.ACACIA_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_RIGHT);
            add(event, ItemRegistry.ACACIA_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.DOWN);
            add(event, ItemRegistry.ACACIA_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_LEFT);
            add(event, ItemRegistry.ACACIA_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.LEFT);
            add(event, ItemRegistry.ACACIA_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_LEFT);
            add(event, ItemRegistry.ACACIA_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.NONE);
            add(event, ItemRegistry.LARGE_ACACIA_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.UP);
            add(event, ItemRegistry.LARGE_ACACIA_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_RIGHT);
            add(event, ItemRegistry.LARGE_ACACIA_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.RIGHT);
            add(event, ItemRegistry.LARGE_ACACIA_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_RIGHT);
            add(event, ItemRegistry.LARGE_ACACIA_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.DOWN);
            add(event, ItemRegistry.LARGE_ACACIA_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_LEFT);
            add(event, ItemRegistry.LARGE_ACACIA_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.LEFT);
            add(event, ItemRegistry.LARGE_ACACIA_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_LEFT);
            add(event, ItemRegistry.LARGE_ACACIA_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.NONE);

            add(event, ItemRegistry.DARK_OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.UP);
            add(event, ItemRegistry.DARK_OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_RIGHT);
            add(event, ItemRegistry.DARK_OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.RIGHT);
            add(event, ItemRegistry.DARK_OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_RIGHT);
            add(event, ItemRegistry.DARK_OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.DOWN);
            add(event, ItemRegistry.DARK_OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_LEFT);
            add(event, ItemRegistry.DARK_OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.LEFT);
            add(event, ItemRegistry.DARK_OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_LEFT);
            add(event, ItemRegistry.DARK_OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.NONE);
            add(event, ItemRegistry.LARGE_DARK_OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.UP);
            add(event, ItemRegistry.LARGE_DARK_OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_RIGHT);
            add(event, ItemRegistry.LARGE_DARK_OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.RIGHT);
            add(event, ItemRegistry.LARGE_DARK_OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_RIGHT);
            add(event, ItemRegistry.LARGE_DARK_OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.DOWN);
            add(event, ItemRegistry.LARGE_DARK_OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_LEFT);
            add(event, ItemRegistry.LARGE_DARK_OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.LEFT);
            add(event, ItemRegistry.LARGE_DARK_OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_LEFT);
            add(event, ItemRegistry.LARGE_DARK_OAK_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.NONE);

            add(event, ItemRegistry.MANGROVE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.UP);
            add(event, ItemRegistry.MANGROVE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_RIGHT);
            add(event, ItemRegistry.MANGROVE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.RIGHT);
            add(event, ItemRegistry.MANGROVE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_RIGHT);
            add(event, ItemRegistry.MANGROVE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.DOWN);
            add(event, ItemRegistry.MANGROVE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_LEFT);
            add(event, ItemRegistry.MANGROVE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.LEFT);
            add(event, ItemRegistry.MANGROVE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_LEFT);
            add(event, ItemRegistry.MANGROVE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.NONE);
            add(event, ItemRegistry.LARGE_MANGROVE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.UP);
            add(event, ItemRegistry.LARGE_MANGROVE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_RIGHT);
            add(event, ItemRegistry.LARGE_MANGROVE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.RIGHT);
            add(event, ItemRegistry.LARGE_MANGROVE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_RIGHT);
            add(event, ItemRegistry.LARGE_MANGROVE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.DOWN);
            add(event, ItemRegistry.LARGE_MANGROVE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_LEFT);
            add(event, ItemRegistry.LARGE_MANGROVE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.LEFT);
            add(event, ItemRegistry.LARGE_MANGROVE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_LEFT);
            add(event, ItemRegistry.LARGE_MANGROVE_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.NONE);

            add(event, ItemRegistry.CHERRY_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.UP);
            add(event, ItemRegistry.CHERRY_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_RIGHT);
            add(event, ItemRegistry.CHERRY_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.RIGHT);
            add(event, ItemRegistry.CHERRY_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_RIGHT);
            add(event, ItemRegistry.CHERRY_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.DOWN);
            add(event, ItemRegistry.CHERRY_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_LEFT);
            add(event, ItemRegistry.CHERRY_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.LEFT);
            add(event, ItemRegistry.CHERRY_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_LEFT);
            add(event, ItemRegistry.CHERRY_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.NONE);
            add(event, ItemRegistry.LARGE_CHERRY_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.UP);
            add(event, ItemRegistry.LARGE_CHERRY_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_RIGHT);
            add(event, ItemRegistry.LARGE_CHERRY_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.RIGHT);
            add(event, ItemRegistry.LARGE_CHERRY_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_RIGHT);
            add(event, ItemRegistry.LARGE_CHERRY_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.DOWN);
            add(event, ItemRegistry.LARGE_CHERRY_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_LEFT);
            add(event, ItemRegistry.LARGE_CHERRY_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.LEFT);
            add(event, ItemRegistry.LARGE_CHERRY_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_LEFT);
            add(event, ItemRegistry.LARGE_CHERRY_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.NONE);

            add(event, ItemRegistry.BAMBOO_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.UP);
            add(event, ItemRegistry.BAMBOO_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_RIGHT);
            add(event, ItemRegistry.BAMBOO_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.RIGHT);
            add(event, ItemRegistry.BAMBOO_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_RIGHT);
            add(event, ItemRegistry.BAMBOO_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.DOWN);
            add(event, ItemRegistry.BAMBOO_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_LEFT);
            add(event, ItemRegistry.BAMBOO_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.LEFT);
            add(event, ItemRegistry.BAMBOO_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_LEFT);
            add(event, ItemRegistry.BAMBOO_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.NONE);
            add(event, ItemRegistry.LARGE_BAMBOO_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.UP);
            add(event, ItemRegistry.LARGE_BAMBOO_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_RIGHT);
            add(event, ItemRegistry.LARGE_BAMBOO_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.RIGHT);
            add(event, ItemRegistry.LARGE_BAMBOO_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_RIGHT);
            add(event, ItemRegistry.LARGE_BAMBOO_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.DOWN);
            add(event, ItemRegistry.LARGE_BAMBOO_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_LEFT);
            add(event, ItemRegistry.LARGE_BAMBOO_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.LEFT);
            add(event, ItemRegistry.LARGE_BAMBOO_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_LEFT);
            add(event, ItemRegistry.LARGE_BAMBOO_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.NONE);

            add(event, ItemRegistry.CRIMSON_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.UP);
            add(event, ItemRegistry.CRIMSON_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_RIGHT);
            add(event, ItemRegistry.CRIMSON_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.RIGHT);
            add(event, ItemRegistry.CRIMSON_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_RIGHT);
            add(event, ItemRegistry.CRIMSON_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.DOWN);
            add(event, ItemRegistry.CRIMSON_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_LEFT);
            add(event, ItemRegistry.CRIMSON_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.LEFT);
            add(event, ItemRegistry.CRIMSON_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_LEFT);
            add(event, ItemRegistry.CRIMSON_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.NONE);
            add(event, ItemRegistry.LARGE_CRIMSON_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.UP);
            add(event, ItemRegistry.LARGE_CRIMSON_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_RIGHT);
            add(event, ItemRegistry.LARGE_CRIMSON_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.RIGHT);
            add(event, ItemRegistry.LARGE_CRIMSON_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_RIGHT);
            add(event, ItemRegistry.LARGE_CRIMSON_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.DOWN);
            add(event, ItemRegistry.LARGE_CRIMSON_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_LEFT);
            add(event, ItemRegistry.LARGE_CRIMSON_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.LEFT);
            add(event, ItemRegistry.LARGE_CRIMSON_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_LEFT);
            add(event, ItemRegistry.LARGE_CRIMSON_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.NONE);

            add(event, ItemRegistry.WARPED_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.UP);
            add(event, ItemRegistry.WARPED_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_RIGHT);
            add(event, ItemRegistry.WARPED_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.RIGHT);
            add(event, ItemRegistry.WARPED_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_RIGHT);
            add(event, ItemRegistry.WARPED_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.DOWN);
            add(event, ItemRegistry.WARPED_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_LEFT);
            add(event, ItemRegistry.WARPED_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.LEFT);
            add(event, ItemRegistry.WARPED_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_LEFT);
            add(event, ItemRegistry.WARPED_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.NONE);
            add(event, ItemRegistry.LARGE_WARPED_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.UP);
            add(event, ItemRegistry.LARGE_WARPED_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_RIGHT);
            add(event, ItemRegistry.LARGE_WARPED_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.RIGHT);
            add(event, ItemRegistry.LARGE_WARPED_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_RIGHT);
            add(event, ItemRegistry.LARGE_WARPED_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.DOWN);
            add(event, ItemRegistry.LARGE_WARPED_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.BOTTOM_LEFT);
            add(event, ItemRegistry.LARGE_WARPED_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.LEFT);
            add(event, ItemRegistry.LARGE_WARPED_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.TOP_LEFT);
            add(event, ItemRegistry.LARGE_WARPED_ARROW_SIGN, DataComponentRegistry.ARROW_SIGN_DIRECTION.get(), ArrowDirection.NONE);

            add(event, BlockRegistry.MUSHROOT_DOOR);
            add(event, BlockRegistry.MUSHROOT_TRAPDOOR);

            add(event, BlockRegistry.FUNGAL_STONE);
            add(event, BlockRegistry.FUNGAL_STONE_STAIRS);
            add(event, BlockRegistry.FUNGAL_STONE_SLAB);
            add(event, BlockRegistry.FUNGAL_STONE_WALL);
            add(event, BlockRegistry.FUNGAL_STONE_PRESSURE_PLATE);
            add(event, BlockRegistry.FUNGAL_STONE_BUTTON);

            add(event, BlockRegistry.ROCKY_FUNGAL_STONE);
            add(event, BlockRegistry.ROCKY_FUNGAL_STONE_STAIRS);
            add(event, BlockRegistry.ROCKY_FUNGAL_STONE_SLAB);
            add(event, BlockRegistry.ROCKY_FUNGAL_STONE_WALL);

            add(event, BlockRegistry.FUNGAL_COBBLESTONE);
            add(event, BlockRegistry.FUNGAL_COBBLESTONE_STAIRS);
            add(event, BlockRegistry.FUNGAL_COBBLESTONE_SLAB);
            add(event, BlockRegistry.FUNGAL_COBBLESTONE_WALL);

            add(event, BlockRegistry.FUNGAL_BRICKS);
            add(event, BlockRegistry.CRACKED_FUNGAL_BRICKS);
            add(event, BlockRegistry.FUNGAL_BRICK_STAIRS);
            add(event, BlockRegistry.FUNGAL_BRICK_SLAB);
            add(event, BlockRegistry.FUNGAL_BRICK_WALL);
            add(event, BlockRegistry.CHISELED_FUNGAL_BRICKS);

            add(event, BlockRegistry.POLISHED_FUNGAL_STONE);
            add(event, BlockRegistry.POLISHED_FUNGAL_STONE_STAIRS);
            add(event, BlockRegistry.POLISHED_FUNGAL_STONE_SLAB);
            add(event, BlockRegistry.POLISHED_FUNGAL_STONE_WALL);

            add(event, BlockRegistry.POLISHED_FUNGAL_BRICKS);
            add(event, BlockRegistry.CRACKED_POLISHED_FUNGAL_BRICKS);
            add(event, BlockRegistry.POLISHED_FUNGAL_BRICK_STAIRS);
            add(event, BlockRegistry.POLISHED_FUNGAL_BRICK_SLAB);
            add(event, BlockRegistry.POLISHED_FUNGAL_BRICK_WALL);
            add(event, BlockRegistry.CHISELED_POLISHED_FUNGAL_BRICKS);

            add(event, BlockRegistry.HARD_FUNGAL_BLOCK);
            add(event, BlockRegistry.HARD_FUNGAL_STAIRS);
            add(event, BlockRegistry.HARD_FUNGAL_SLAB);
            add(event, BlockRegistry.HARD_FUNGAL_WALL);

            add(event, BlockRegistry.DEEP_FUNGAL_STONE);
            add(event, BlockRegistry.DEEP_FUNGAL_STONE_STAIRS);
            add(event, BlockRegistry.DEEP_FUNGAL_STONE_SLAB);
            add(event, BlockRegistry.DEEP_FUNGAL_STONE_WALL);
            add(event, BlockRegistry.DEEP_FUNGAL_STONE_PRESSURE_PLATE);
            add(event, BlockRegistry.DEEP_FUNGAL_STONE_BUTTON);

            add(event, BlockRegistry.ROCKY_DEEP_FUNGAL_STONE);
            add(event, BlockRegistry.ROCKY_DEEP_FUNGAL_STONE_STAIRS);
            add(event, BlockRegistry.ROCKY_DEEP_FUNGAL_STONE_SLAB);
            add(event, BlockRegistry.ROCKY_DEEP_FUNGAL_STONE_WALL);

            add(event, BlockRegistry.DEEP_FUNGAL_COBBLESTONE);
            add(event, BlockRegistry.DEEP_FUNGAL_COBBLESTONE_STAIRS);
            add(event, BlockRegistry.DEEP_FUNGAL_COBBLESTONE_SLAB);
            add(event, BlockRegistry.DEEP_FUNGAL_COBBLESTONE_WALL);

            add(event, BlockRegistry.DEEP_FUNGAL_BRICKS);
            add(event, BlockRegistry.CRACKED_DEEP_FUNGAL_BRICKS);
            add(event, BlockRegistry.DEEP_FUNGAL_BRICK_STAIRS);
            add(event, BlockRegistry.DEEP_FUNGAL_BRICK_SLAB);
            add(event, BlockRegistry.DEEP_FUNGAL_BRICK_WALL);
            add(event, BlockRegistry.CHISELED_DEEP_FUNGAL_BRICKS);

            add(event, BlockRegistry.POLISHED_DEEP_FUNGAL_STONE);
            add(event, BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_STAIRS);
            add(event, BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_SLAB);
            add(event, BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_WALL);

            add(event, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICKS);
            add(event, BlockRegistry.CRACKED_POLISHED_DEEP_FUNGAL_BRICKS);
            add(event, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_STAIRS);
            add(event, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_SLAB);
            add(event, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_WALL);
            add(event, BlockRegistry.CHISELED_POLISHED_DEEP_FUNGAL_BRICKS);

            add(event, BlockRegistry.HARD_DEEP_FUNGAL_BLOCK);
            add(event, BlockRegistry.HARD_DEEP_FUNGAL_STAIRS);
            add(event, BlockRegistry.HARD_DEEP_FUNGAL_SLAB);
            add(event, BlockRegistry.HARD_DEEP_FUNGAL_WALL);

            add(event, Blocks.AMETHYST_BLOCK);
            add(event, BlockRegistry.AMETHYST_STAIRS);
            add(event, BlockRegistry.AMETHYST_SLAB);
            add(event, BlockRegistry.AMETHYST_WALL);
            add(event, BlockRegistry.AMETHYST_PRESSURE_PLATE);
            add(event, BlockRegistry.AMETHYST_BUTTON);
            add(event, BlockRegistry.POLISHED_AMETHYST);
            add(event, BlockRegistry.POLISHED_AMETHYST_STAIRS);
            add(event, BlockRegistry.POLISHED_AMETHYST_SLAB);
            add(event, BlockRegistry.POLISHED_AMETHYST_WALL);
            add(event, BlockRegistry.AMETHYST_BRICKS);
            add(event, BlockRegistry.CRACKED_AMETHYST_BRICKS);
            add(event, BlockRegistry.AMETHYST_BRICK_STAIRS);
            add(event, BlockRegistry.AMETHYST_BRICK_SLAB);
            add(event, BlockRegistry.AMETHYST_BRICK_WALL);
            add(event, BlockRegistry.CHISELED_AMETHYST_BRICKS);

            add(event, Blocks.SANDSTONE);
            add(event, Blocks.CUT_SANDSTONE);
            add(event, Blocks.CHISELED_SANDSTONE);
            add(event, BlockRegistry.SANDSTONE_BRICKS);
            add(event, BlockRegistry.CRACKED_SANDSTONE_BRICKS);
            add(event, BlockRegistry.SANDSTONE_BRICK_STAIRS);
            add(event, BlockRegistry.SANDSTONE_BRICK_SLAB);
            add(event, BlockRegistry.SANDSTONE_BRICK_WALL);
            add(event, BlockRegistry.CHISELED_SANDSTONE_BRICKS);

            add(event, Blocks.RED_SANDSTONE);
            add(event, Blocks.CUT_RED_SANDSTONE);
            add(event, Blocks.CHISELED_RED_SANDSTONE);
            add(event, BlockRegistry.RED_SANDSTONE_BRICKS);
            add(event, BlockRegistry.CRACKED_RED_SANDSTONE_BRICKS);
            add(event, BlockRegistry.RED_SANDSTONE_BRICK_STAIRS);
            add(event, BlockRegistry.RED_SANDSTONE_BRICK_SLAB);
            add(event, BlockRegistry.RED_SANDSTONE_BRICK_WALL);
            add(event, BlockRegistry.CHISELED_RED_SANDSTONE_BRICKS);

            add(event, BlockRegistry.SMASHABLE_STONE_BRICKS);
            add(event, BlockRegistry.SMASHABLE_MOSSY_STONE_BRICKS);
            add(event, BlockRegistry.SMASHABLE_DEEPSLATE_BRICKS);
            add(event, BlockRegistry.SMASHABLE_DEEPSLATE_TILES);
            add(event, BlockRegistry.SMASHABLE_TUFF_BRICKS);
            add(event, BlockRegistry.SMASHABLE_BRICKS);
            add(event, BlockRegistry.SMASHABLE_MUD_BRICKS);
            add(event, BlockRegistry.SMASHABLE_PRISMARINE_BRICKS);
            add(event, BlockRegistry.SMASHABLE_DARK_PRISMARINE);
            add(event, BlockRegistry.SMASHABLE_NETHER_BRICKS);
            add(event, BlockRegistry.SMASHABLE_RED_NETHER_BRICKS);
            add(event, BlockRegistry.SMASHABLE_BLACKSTONE_BRICKS);
            add(event, BlockRegistry.SMASHABLE_QUARTZ_BRICKS);
            add(event, BlockRegistry.SMASHABLE_END_STONE_BRICKS);
            add(event, BlockRegistry.SMASHABLE_PURPUR_BLOCK);
            add(event, BlockRegistry.SMASHABLE_CUT_COPPER);
            add(event, BlockRegistry.SMASHABLE_EXPOSED_CUT_COPPER);
            add(event, BlockRegistry.SMASHABLE_WEATHERED_CUT_COPPER);
            add(event, BlockRegistry.SMASHABLE_OXIDIZED_CUT_COPPER);
            add(event, BlockRegistry.SMASHABLE_WAXED_CUT_COPPER);
            add(event, BlockRegistry.SMASHABLE_WAXED_EXPOSED_CUT_COPPER);
            add(event, BlockRegistry.SMASHABLE_WAXED_WEATHERED_CUT_COPPER);
            add(event, BlockRegistry.SMASHABLE_WAXED_OXIDIZED_CUT_COPPER);

            add(event, BlockRegistry.FUNGAL_BRICK_PEDESTAL);
            add(event, BlockRegistry.POLISHED_FUNGAL_BRICK_PEDESTAL);
            add(event, BlockRegistry.DEEP_FUNGAL_BRICK_PEDESTAL);
            add(event, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_PEDESTAL);
            add(event, BlockRegistry.AMETHYST_BRICK_PEDESTAL);
            add(event, BlockRegistry.STONE_BRICK_PEDESTAL);
            add(event, BlockRegistry.MOSSY_STONE_BRICK_PEDESTAL);
            add(event, BlockRegistry.DEEPSLATE_BRICK_PEDESTAL);
            add(event, BlockRegistry.DEEPSLATE_TILE_PEDESTAL);
            add(event, BlockRegistry.TUFF_BRICK_PEDESTAL);
            add(event, BlockRegistry.BRICK_PEDESTAL);
            add(event, BlockRegistry.MUD_BRICK_PEDESTAL);
            add(event, BlockRegistry.SANDSTONE_BRICK_PEDESTAL);
            add(event, BlockRegistry.RED_SANDSTONE_BRICK_PEDESTAL);
            add(event, BlockRegistry.PRISMARINE_BRICK_PEDESTAL);
            add(event, BlockRegistry.DARK_PRISMARINE_PEDESTAL);
            add(event, BlockRegistry.NETHER_BRICK_PEDESTAL);
            add(event, BlockRegistry.RED_NETHER_BRICK_PEDESTAL);
            add(event, BlockRegistry.BLACKSTONE_BRICK_PEDESTAL);
            add(event, BlockRegistry.QUARTZ_BRICK_PEDESTAL);
            add(event, BlockRegistry.END_STONE_BRICK_PEDESTAL);
            add(event, BlockRegistry.PURPUR_BLOCK_PEDESTAL);
            add(event, BlockRegistry.CUT_COPPER_PEDESTAL);
            add(event, BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL);
            add(event, BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL);
            add(event, BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL);
            add(event, BlockRegistry.WAXED_CUT_COPPER_PEDESTAL);
            add(event, BlockRegistry.WAXED_EXPOSED_CUT_COPPER_PEDESTAL);
            add(event, BlockRegistry.WAXED_WEATHERED_CUT_COPPER_PEDESTAL);
            add(event, BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL);

            add(event, BlockRegistry.OAK_PICKET_FENCE);
            add(event, BlockRegistry.SPRUCE_PICKET_FENCE);
            add(event, BlockRegistry.BIRCH_PICKET_FENCE);
            add(event, BlockRegistry.JUNGLE_PICKET_FENCE);
            add(event, BlockRegistry.ACACIA_PICKET_FENCE);
            add(event, BlockRegistry.CHERRY_PICKET_FENCE);
            add(event, BlockRegistry.DARK_OAK_PICKET_FENCE);
            add(event, BlockRegistry.MANGROVE_PICKET_FENCE);
            add(event, BlockRegistry.BAMBOO_PICKET_FENCE);
            add(event, BlockRegistry.CRIMSON_PICKET_FENCE);
            add(event, BlockRegistry.WARPED_PICKET_FENCE);
            add(event, BlockRegistry.MUSHROOT_PICKET_FENCE);
            add(event, BlockRegistry.RED_PICKET_FENCE);
            add(event, BlockRegistry.WHITE_PICKET_FENCE);

            add(event, BlockRegistry.MUSHROOT_LOG_PLATFORM);
            add(event, BlockRegistry.STRIPPED_MUSHROOT_LOG_PLATFORM);

            add(event, BlockRegistry.OAK_LOG_BRIDGE);
            add(event, BlockRegistry.STRIPPED_OAK_LOG_BRIDGE);
            add(event, BlockRegistry.SPRUCE_LOG_BRIDGE);
            add(event, BlockRegistry.STRIPPED_SPRUCE_LOG_BRIDGE);
            add(event, BlockRegistry.BIRCH_LOG_BRIDGE);
            add(event, BlockRegistry.STRIPPED_BIRCH_LOG_BRIDGE);
            add(event, BlockRegistry.JUNGLE_LOG_BRIDGE);
            add(event, BlockRegistry.STRIPPED_JUNGLE_LOG_BRIDGE);
            add(event, BlockRegistry.ACACIA_LOG_BRIDGE);
            add(event, BlockRegistry.STRIPPED_ACACIA_LOG_BRIDGE);
            add(event, BlockRegistry.DARK_OAK_LOG_BRIDGE);
            add(event, BlockRegistry.STRIPPED_DARK_OAK_LOG_BRIDGE);
            add(event, BlockRegistry.MANGROVE_LOG_BRIDGE);
            add(event, BlockRegistry.STRIPPED_MANGROVE_LOG_BRIDGE);
            add(event, BlockRegistry.CHERRY_LOG_BRIDGE);
            add(event, BlockRegistry.STRIPPED_CHERRY_LOG_BRIDGE);
            add(event, BlockRegistry.BAMBOO_BRIDGE);
            add(event, BlockRegistry.STRIPPED_BAMBOO_BRIDGE);
            add(event, BlockRegistry.MUSHROOT_LOG_BRIDGE);
            add(event, BlockRegistry.STRIPPED_MUSHROOT_LOG_BRIDGE);
            add(event, BlockRegistry.CRIMSON_STEM_BRIDGE);
            add(event, BlockRegistry.STRIPPED_CRIMSON_STEM_BRIDGE);
            add(event, BlockRegistry.WARPED_STEM_BRIDGE);
            add(event, BlockRegistry.STRIPPED_WARPED_STEM_BRIDGE);

            add(event, BlockRegistry.OAK_LOG_BRIDGE_STAIRS);
            add(event, BlockRegistry.STRIPPED_OAK_LOG_BRIDGE_STAIRS);
            add(event, BlockRegistry.SPRUCE_LOG_BRIDGE_STAIRS);
            add(event, BlockRegistry.STRIPPED_SPRUCE_LOG_BRIDGE_STAIRS);
            add(event, BlockRegistry.BIRCH_LOG_BRIDGE_STAIRS);
            add(event, BlockRegistry.STRIPPED_BIRCH_LOG_BRIDGE_STAIRS);
            add(event, BlockRegistry.JUNGLE_LOG_BRIDGE_STAIRS);
            add(event, BlockRegistry.STRIPPED_JUNGLE_LOG_BRIDGE_STAIRS);
            add(event, BlockRegistry.ACACIA_LOG_BRIDGE_STAIRS);
            add(event, BlockRegistry.STRIPPED_ACACIA_LOG_BRIDGE_STAIRS);
            add(event, BlockRegistry.DARK_OAK_LOG_BRIDGE_STAIRS);
            add(event, BlockRegistry.STRIPPED_DARK_OAK_LOG_BRIDGE_STAIRS);
            add(event, BlockRegistry.MANGROVE_LOG_BRIDGE_STAIRS);
            add(event, BlockRegistry.STRIPPED_MANGROVE_LOG_BRIDGE_STAIRS);
            add(event, BlockRegistry.CHERRY_LOG_BRIDGE_STAIRS);
            add(event, BlockRegistry.STRIPPED_CHERRY_LOG_BRIDGE_STAIRS);
            add(event, BlockRegistry.BAMBOO_BRIDGE_STAIRS);
            add(event, BlockRegistry.STRIPPED_BAMBOO_BRIDGE_STAIRS);
            add(event, BlockRegistry.MUSHROOT_LOG_BRIDGE_STAIRS);
            add(event, BlockRegistry.STRIPPED_MUSHROOT_LOG_BRIDGE_STAIRS);
            add(event, BlockRegistry.CRIMSON_STEM_BRIDGE_STAIRS);
            add(event, BlockRegistry.STRIPPED_CRIMSON_STEM_BRIDGE_STAIRS);
            add(event, BlockRegistry.WARPED_STEM_BRIDGE_STAIRS);
            add(event, BlockRegistry.STRIPPED_WARPED_STEM_BRIDGE_STAIRS);

            addDyedBlocks(event, BlockRegistry.STRIPPED_WARPED_STEM_BRIDGE_STAIRS, BlockRegistry.PIPE_JUNCTION, true, true);
            add(event, Blocks.CALCITE);
            addDyedBlocks(event, Blocks.CALCITE, BlockRegistry.CALCITE, true, true);
            addDyedBlocks(event, BlockRegistry.CALCITE.get(DyeColor.PINK), BlockRegistry.POLISHED_CALCITE, true, true);
            addDyedBlocks(event, BlockRegistry.POLISHED_CALCITE.get(DyeColor.PINK), BlockRegistry.CALCITE_BRICKS, true, true);
            addDyedBlocks(event, BlockRegistry.CALCITE_BRICKS.get(DyeColor.PINK), BlockRegistry.CRACKED_CALCITE_BRICKS, true, true);
            addDyedBlocks(event, BlockRegistry.CRACKED_CALCITE_BRICKS.get(DyeColor.PINK), BlockRegistry.CHISELED_CALCITE_BRICKS, true, true);
            addDyedBlocks(event, BlockRegistry.CHISELED_CALCITE_BRICKS.get(DyeColor.PINK), BlockRegistry.CALCITE_BRICK_PEDESTAL, true, true);
            add(event, BlockRegistry.CALCITE_STAIRS);
            add(event, BlockRegistry.CALCITE_SLAB);
            add(event, BlockRegistry.CALCITE_WALL);
            add(event, BlockRegistry.CALCITE_PRESSURE_PLATE);
            add(event, BlockRegistry.CALCITE_BUTTON);
            add(event, BlockRegistry.POLISHED_WHITE_CALCITE_STAIRS);
            add(event, BlockRegistry.POLISHED_WHITE_CALCITE_SLAB);
            add(event, BlockRegistry.POLISHED_WHITE_CALCITE_WALL);
            add(event, BlockRegistry.WHITE_CALCITE_BRICK_STAIRS);
            add(event, BlockRegistry.WHITE_CALCITE_BRICK_SLAB);
            add(event, BlockRegistry.WHITE_CALCITE_BRICK_WALL);
            add(event, BlockRegistry.CALCITE_CHECKERED_TILES);
            add(event, BlockRegistry.CALCITE_CHECKERED_TILE_SLAB);
            add(event, BlockRegistry.CALCITE_CHECKERED_TILE_STAIRS);
            add(event, BlockRegistry.CALCITE_CHECKERED_TILE_WALL);
        }

        if (event.getTabKey() == MARIOVERSE_FUNCTIONAL_BLOCKS_TAB.getKey() && !ConfigRegistry.DISABLE_MARIOVERSE_TABS.get()) {
            add(event, BlockRegistry.IRON_SPIKE);
            add(event, BlockRegistry.SPIKE_PANEL);
            add(event, ItemRegistry.MUSHROOT_SIGN);
            add(event, ItemRegistry.MUSHROOT_HANGING_SIGN);
            add(event, ItemRegistry.MUSHROOT_ARROW_SIGN);
            add(event, ItemRegistry.LARGE_MUSHROOT_ARROW_SIGN);
            add(event, ItemRegistry.OAK_ARROW_SIGN);
            add(event, ItemRegistry.LARGE_OAK_ARROW_SIGN);
            add(event, ItemRegistry.SPRUCE_ARROW_SIGN);
            add(event, ItemRegistry.LARGE_SPRUCE_ARROW_SIGN);
            add(event, ItemRegistry.BIRCH_ARROW_SIGN);
            add(event, ItemRegistry.LARGE_BIRCH_ARROW_SIGN);
            add(event, ItemRegistry.JUNGLE_ARROW_SIGN);
            add(event, ItemRegistry.LARGE_JUNGLE_ARROW_SIGN);
            add(event, ItemRegistry.ACACIA_ARROW_SIGN);
            add(event, ItemRegistry.LARGE_ACACIA_ARROW_SIGN);
            add(event, ItemRegistry.DARK_OAK_ARROW_SIGN);
            add(event, ItemRegistry.LARGE_DARK_OAK_ARROW_SIGN);
            add(event, ItemRegistry.MANGROVE_ARROW_SIGN);
            add(event, ItemRegistry.LARGE_MANGROVE_ARROW_SIGN);
            add(event, ItemRegistry.CHERRY_ARROW_SIGN);
            add(event, ItemRegistry.LARGE_CHERRY_ARROW_SIGN);
            add(event, ItemRegistry.BAMBOO_ARROW_SIGN);
            add(event, ItemRegistry.LARGE_BAMBOO_ARROW_SIGN);
            add(event, ItemRegistry.CRIMSON_ARROW_SIGN);
            add(event, ItemRegistry.LARGE_CRIMSON_ARROW_SIGN);
            add(event, ItemRegistry.WARPED_ARROW_SIGN);
            add(event, ItemRegistry.LARGE_WARPED_ARROW_SIGN);

            add(event, BlockRegistry.CLASSIC_CHECKPOINT_FLAG);
            addDyedBlocks(event, BlockRegistry.CLASSIC_CHECKPOINT_FLAG, BlockRegistry.CHECKPOINT_FLAGS, true, true);
            add(event, BlockRegistry.CHECKPOINT_FLAGS.get(DyeColor.WHITE), Component.literal("Wonder Flag"));
            add(event, BlockRegistry.CHECKPOINT_FLAGS.get(DyeColor.WHITE), Component.literal("America Flag"));
            add(event, BlockRegistry.CLASSIC_GOAL_POLE);
            addDyedBlocks(event, BlockRegistry.CLASSIC_GOAL_POLE, BlockRegistry.GOAL_POLES, true, true);
            add(event, BlockRegistry.GOAL_POLES.get(DyeColor.WHITE), Component.literal("Wonder Flag"));
            add(event, BlockRegistry.GOAL_POLES.get(DyeColor.WHITE), Component.literal("America Flag"));

            add(event, BlockRegistry.GLOW_BLOCK);
            add(event, BlockRegistry.SPLUNKIN_CARVED_PUMPKIN);
            add(event, BlockRegistry.SPLUNKIN_O_LANTERN);
            add(event, BlockRegistry.MARIO_ABILITY_BLOCK);
            add(event, BlockRegistry.LUIGI_ABILITY_BLOCK);
            add(event, BlockRegistry.WARIO_ABILITY_BLOCK);
            add(event, BlockRegistry.WALUIGI_ABILITY_BLOCK);
            add(event, BlockRegistry.PEACH_ABILITY_BLOCK);
            add(event, BlockRegistry.DAISY_ABILITY_BLOCK);
            add(event, BlockRegistry.ROSALINA_ABILITY_BLOCK);
            add(event, BlockRegistry.STEVE_ABILITY_BLOCK);
            add(event, BlockRegistry.ON_OFF_SWITCH);
            add(event, BlockRegistry.RED_DOTTED_LINE_BLOCK);
            add(event, BlockRegistry.BLUE_DOTTED_LINE_BLOCK);
            add(event, BlockRegistry.RED_MUSHROOM_TRAMPOLINE);
            add(event, BlockRegistry.BLUE_MUSHROOM_TRAMPOLINE);
            add(event, BlockRegistry.DEATH_BLOCK);
            add(event, BlockRegistry.PLAYER_DEATH_BLOCK);
            add(event, BlockRegistry.MONSTER_DEATH_BLOCK);
            add(event, BlockRegistry.PASSIVE_DEATH_BLOCK);
            add(event, BlockRegistry.BLOCK_SPAWNER);

            add(event, BlockRegistry.CLEAR_WARP_PIPE);
            addDyedBlocks(event, BlockRegistry.CLEAR_WARP_PIPE, BlockRegistry.WARP_PIPES, true, true);

            add(event, BlockRegistry.FUNGAL_QUESTION_PANEL);
            add(event, BlockRegistry.DEEP_FUNGAL_QUESTION_PANEL);

            add(event, BlockRegistry.FUNGAL_QUESTION_BLOCK);
            add(event, BlockRegistry.DEEP_FUNGAL_QUESTION_BLOCK);
            add(event, BlockRegistry.AMETHYST_QUESTION_BLOCK);
            add(event, BlockRegistry.CALCITE_QUESTION_BLOCK);
            add(event, BlockRegistry.STONE_QUESTION_BRICKS);
            add(event, BlockRegistry.MOSSY_STONE_QUESTION_BRICKS);
            add(event, BlockRegistry.DEEPSLATE_QUESTION_BRICKS);
            add(event, BlockRegistry.DEEPSLATE_QUESTION_TILES);
            add(event, BlockRegistry.TUFF_QUESTION_BRICKS);
            add(event, BlockRegistry.QUESTION_BRICKS);
            add(event, BlockRegistry.MUD_QUESTION_BRICKS);
            add(event, BlockRegistry.SANDSTONE_QUESTION_BLOCK);
            add(event, BlockRegistry.RED_SANDSTONE_QUESTION_BLOCK);
            add(event, BlockRegistry.PRISMARINE_QUESTION_BRICKS);
            add(event, BlockRegistry.DARK_PRISMARINE_QUESTION_BLOCK);
            add(event, BlockRegistry.NETHER_QUESTION_BRICKS);
            add(event, BlockRegistry.RED_NETHER_QUESTION_BRICKS);
            add(event, BlockRegistry.BLACKSTONE_QUESTION_BRICKS);
            add(event, BlockRegistry.QUARTZ_QUESTION_BRICKS);
            add(event, BlockRegistry.END_STONE_QUESTION_BRICKS);
            add(event, BlockRegistry.PURPUR_QUESTION_BLOCK);
            add(event, BlockRegistry.COPPER_QUESTION_BLOCK);
            add(event, BlockRegistry.EXPOSED_COPPER_QUESTION_BLOCK);
            add(event, BlockRegistry.WEATHERED_COPPER_QUESTION_BLOCK);
            add(event, BlockRegistry.OXIDIZED_COPPER_QUESTION_BLOCK);
            add(event, BlockRegistry.WAXED_COPPER_QUESTION_BLOCK);
            add(event, BlockRegistry.WAXED_EXPOSED_COPPER_QUESTION_BLOCK);
            add(event, BlockRegistry.WAXED_WEATHERED_COPPER_QUESTION_BLOCK);
            add(event, BlockRegistry.WAXED_OXIDIZED_COPPER_QUESTION_BLOCK);

            add(event, BlockRegistry.INVISIBLE_FUNGAL_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_DEEP_FUNGAL_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_AMETHYST_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_CALCITE_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_STONE_QUESTION_BRICKS);
            add(event, BlockRegistry.INVISIBLE_MOSSY_STONE_QUESTION_BRICKS);
            add(event, BlockRegistry.INVISIBLE_DEEPSLATE_QUESTION_BRICKS);
            add(event, BlockRegistry.INVISIBLE_DEEPSLATE_QUESTION_TILES);
            add(event, BlockRegistry.INVISIBLE_TUFF_QUESTION_BRICKS);
            add(event, BlockRegistry.INVISIBLE_QUESTION_BRICKS);
            add(event, BlockRegistry.INVISIBLE_MUD_QUESTION_BRICKS);
            add(event, BlockRegistry.INVISIBLE_SANDSTONE_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_RED_SANDSTONE_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_PRISMARINE_QUESTION_BRICKS);
            add(event, BlockRegistry.INVISIBLE_DARK_PRISMARINE_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_NETHER_QUESTION_BRICKS);
            add(event, BlockRegistry.INVISIBLE_RED_NETHER_QUESTION_BRICKS);
            add(event, BlockRegistry.INVISIBLE_BLACKSTONE_QUESTION_BRICKS);
            add(event, BlockRegistry.INVISIBLE_QUARTZ_QUESTION_BRICKS);
            add(event, BlockRegistry.INVISIBLE_END_STONE_QUESTION_BRICKS);
            add(event, BlockRegistry.INVISIBLE_PURPUR_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_COPPER_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_EXPOSED_COPPER_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_WEATHERED_COPPER_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_OXIDIZED_COPPER_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_WAXED_COPPER_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_WAXED_EXPOSED_COPPER_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_WAXED_WEATHERED_COPPER_QUESTION_BLOCK);
            add(event, BlockRegistry.INVISIBLE_WAXED_OXIDIZED_COPPER_QUESTION_BLOCK);

            add(event, BlockRegistry.STORAGE_FUNGAL_BRICKS);
            add(event, BlockRegistry.STORAGE_POLISHED_FUNGAL_BRICKS);
            add(event, BlockRegistry.STORAGE_DEEP_FUNGAL_BRICKS);
            add(event, BlockRegistry.STORAGE_POLISHED_DEEP_FUNGAL_BRICKS);
            add(event, BlockRegistry.STORAGE_AMETHYST_BRICKS);
            add(event, BlockRegistry.STORAGE_STONE_BRICKS);
            add(event, BlockRegistry.STORAGE_MOSSY_STONE_BRICKS);
            add(event, BlockRegistry.STORAGE_DEEPSLATE_BRICKS);
            add(event, BlockRegistry.STORAGE_DEEPSLATE_TILES);
            add(event, BlockRegistry.STORAGE_TUFF_BRICKS);
            add(event, BlockRegistry.STORAGE_BRICKS);
            add(event, BlockRegistry.STORAGE_MUD_BRICKS);
            add(event, BlockRegistry.STORAGE_SANDSTONE_BRICKS);
            add(event, BlockRegistry.STORAGE_RED_SANDSTONE_BRICKS);
            add(event, BlockRegistry.STORAGE_PRISMARINE_BRICKS);
            add(event, BlockRegistry.STORAGE_DARK_PRISMARINE);
            add(event, BlockRegistry.STORAGE_NETHER_BRICKS);
            add(event, BlockRegistry.STORAGE_RED_NETHER_BRICKS);
            add(event, BlockRegistry.STORAGE_BLACKSTONE_BRICKS);
            add(event, BlockRegistry.STORAGE_QUARTZ_BRICKS);
            add(event, BlockRegistry.STORAGE_END_STONE_BRICKS);
            add(event, BlockRegistry.STORAGE_PURPUR_BLOCK);
            add(event, BlockRegistry.STORAGE_CUT_COPPER);
            add(event, BlockRegistry.STORAGE_EXPOSED_CUT_COPPER);
            add(event, BlockRegistry.STORAGE_WEATHERED_CUT_COPPER);
            add(event, BlockRegistry.STORAGE_OXIDIZED_CUT_COPPER);
            add(event, BlockRegistry.STORAGE_WAXED_CUT_COPPER);
            add(event, BlockRegistry.STORAGE_WAXED_EXPOSED_CUT_COPPER);
            add(event, BlockRegistry.STORAGE_WAXED_WEATHERED_CUT_COPPER);
            add(event, BlockRegistry.STORAGE_WAXED_OXIDIZED_CUT_COPPER);
            addDyedBlocks(event, BlockRegistry.STORAGE_WAXED_OXIDIZED_CUT_COPPER, BlockRegistry.STORAGE_CALCITE_BRICKS, true, true);

            RegistryEventHandlers.WARP_DOORS.entrySet().stream()
                    .sorted(Comparator.comparing((Map.Entry<Block, Block> e) -> BuiltInRegistries.BLOCK.getKey(e.getKey()).getNamespace())
                            .thenComparing(e -> BuiltInRegistries.BLOCK.getKey(e.getKey()).getPath()))
                    .forEach(block -> {
                        Block warpDoor = block.getValue();
                        add(event, warpDoor);
                    });

            RegistryEventHandlers.WARP_TRAPDOORS.entrySet().stream()
                    .sorted(Comparator.comparing((Map.Entry<Block, Block> e) -> BuiltInRegistries.BLOCK.getKey(e.getKey()).getNamespace())
                            .thenComparing(e -> BuiltInRegistries.BLOCK.getKey(e.getKey()).getPath()))
                    .forEach(block -> {
                        Block warpTrapdoor = block.getValue();
                        add(event, warpTrapdoor);
                    });
        }

        if (!ConfigRegistry.DISABLE_VANILLA_TABS.get()) {
            if (event.getTabKey() == CreativeModeTabs.OP_BLOCKS && event.hasPermissions()) {
                addAfter(event, Items.DEBUG_STICK, ItemRegistry.CREATIVE_WRENCH);
                addAfter(event, Blocks.STRUCTURE_BLOCK, BlockRegistry.BLOCK_SPAWNER);
                addAfter(event, BlockRegistry.BLOCK_SPAWNER, BlockRegistry.DEATH_BLOCK);
                addAfter(event, BlockRegistry.DEATH_BLOCK, BlockRegistry.PLAYER_DEATH_BLOCK);
                addAfter(event, BlockRegistry.PLAYER_DEATH_BLOCK, BlockRegistry.MONSTER_DEATH_BLOCK);
                addAfter(event, BlockRegistry.MONSTER_DEATH_BLOCK, BlockRegistry.PASSIVE_DEATH_BLOCK);
            }

            if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
                ItemStack piranhaPlantPod = new ItemStack(ItemRegistry.PIRANHA_PLANT_POD.get());
                ItemStack cavePiranhaPlantPod = variant(ItemRegistry.PIRANHA_PLANT_POD.get(), PiranhaPlantVariants.CAVE);
                ItemStack chomperPod = variant(ItemRegistry.PIRANHA_PLANT_POD.get(), PiranhaPlantVariants.CHOMPER);
                ItemStack deepCavePiranhaPlantPod = variant(ItemRegistry.PIRANHA_PLANT_POD.get(), PiranhaPlantVariants.DEEP_CAVE);
                ItemStack tropicalPiranhaPlantPod = variant(ItemRegistry.PIRANHA_PLANT_POD.get(), PiranhaPlantVariants.TROPICAL);

                addAfter(event, Blocks.PRISMARINE, BlockRegistry.FUNGAL_STONE);
                addAfter(event, BlockRegistry.FUNGAL_STONE, BlockRegistry.ROCKY_FUNGAL_STONE);
                addAfter(event, BlockRegistry.ROCKY_FUNGAL_STONE, BlockRegistry.DEEP_FUNGAL_STONE);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_STONE, BlockRegistry.ROCKY_DEEP_FUNGAL_STONE);

                addAfter(event, Blocks.CHERRY_LOG, BlockRegistry.MUSHROOT_LOG);

                addAfter(event, Blocks.FLOWERING_AZALEA_LEAVES, BlockRegistry.MUSHROOT_LEAVES);

                addAfter(event, Blocks.CHERRY_SAPLING, BlockRegistry.MUSHROOT_SAPLING);

                addAfter(event, Blocks.RED_MUSHROOM, BlockRegistry.RED_TRAMPOLINE_CAP);
                addAfter(event, BlockRegistry.RED_TRAMPOLINE_CAP, BlockRegistry.BLUE_TRAMPOLINE_CAP);
                addAfter(event, Blocks.SPORE_BLOSSOM, BlockRegistry.DANGO_BLOSSOM);
                addAfter(event, Items.PITCHER_POD, piranhaPlantPod);
                addAfter(event, piranhaPlantPod, tropicalPiranhaPlantPod);
                addAfter(event, tropicalPiranhaPlantPod, cavePiranhaPlantPod);
                addAfter(event, cavePiranhaPlantPod, deepCavePiranhaPlantPod);
                addAfter(event, deepCavePiranhaPlantPod, chomperPod);

                addAfter(event, Blocks.PEARLESCENT_FROGLIGHT, BlockRegistry.GLOW_BLOCK);

                addAfter(event, Blocks.DEAD_HORN_CORAL_FAN, BlockRegistry.TUBE_CORAL_TOWER);
                addAfter(event, BlockRegistry.TUBE_CORAL_TOWER, BlockRegistry.BRAIN_CORAL_TOWER);
                addAfter(event, BlockRegistry.BRAIN_CORAL_TOWER, BlockRegistry.BUBBLE_CORAL_TOWER);
                addAfter(event, BlockRegistry.BUBBLE_CORAL_TOWER, BlockRegistry.FIRE_CORAL_TOWER);
                addAfter(event, BlockRegistry.FIRE_CORAL_TOWER, BlockRegistry.HORN_CORAL_TOWER);
                addAfter(event, BlockRegistry.HORN_CORAL_TOWER, BlockRegistry.DEAD_TUBE_CORAL_TOWER);
                addAfter(event, BlockRegistry.DEAD_TUBE_CORAL_TOWER, BlockRegistry.DEAD_BRAIN_CORAL_TOWER);
                addAfter(event, BlockRegistry.DEAD_BRAIN_CORAL_TOWER, BlockRegistry.DEAD_BUBBLE_CORAL_TOWER);
                addAfter(event, BlockRegistry.DEAD_BUBBLE_CORAL_TOWER, BlockRegistry.DEAD_FIRE_CORAL_TOWER);
                addAfter(event, BlockRegistry.DEAD_FIRE_CORAL_TOWER, BlockRegistry.DEAD_HORN_CORAL_TOWER);

                addAfter(event, Blocks.JACK_O_LANTERN, BlockRegistry.SPLUNKIN_CARVED_PUMPKIN);
                addAfter(event, BlockRegistry.SPLUNKIN_CARVED_PUMPKIN, BlockRegistry.SPLUNKIN_O_LANTERN);
            }

            if (event.getTabKey() == CreativeModeTabs.COLORED_BLOCKS) {
                addAfter(event, Blocks.PINK_GLAZED_TERRACOTTA, Blocks.CALCITE);
                addDyedBlocks(event, Blocks.CALCITE, BlockRegistry.CALCITE, true, true);
                addDyedBlocks(event, BlockRegistry.CALCITE.get(DyeColor.PINK), BlockRegistry.POLISHED_CALCITE, true, true);
                addDyedBlocks(event, BlockRegistry.POLISHED_CALCITE.get(DyeColor.PINK), BlockRegistry.CALCITE_BRICKS, true, true);
                addDyedBlocks(event, BlockRegistry.CALCITE_BRICKS.get(DyeColor.PINK), BlockRegistry.CRACKED_CALCITE_BRICKS, true, true);
                addDyedBlocks(event, BlockRegistry.CRACKED_CALCITE_BRICKS.get(DyeColor.PINK), BlockRegistry.CHISELED_CALCITE_BRICKS, true, true);
                addDyedBlocks(event, BlockRegistry.CHISELED_CALCITE_BRICKS.get(DyeColor.PINK), BlockRegistry.STORAGE_CALCITE_BRICKS, true, true);
                addDyedBlocks(event, BlockRegistry.STORAGE_CALCITE_BRICKS.get(DyeColor.PINK), BlockRegistry.CALCITE_BRICK_PEDESTAL, true, true);

                addAfter(event, Blocks.PINK_SHULKER_BOX, BlockRegistry.CLEAR_WARP_PIPE);
                addDyedBlocks(event, BlockRegistry.CLEAR_WARP_PIPE, BlockRegistry.WARP_PIPES, true, true);
                addDyedBlocks(event, BlockRegistry.WARP_PIPES.get(DyeColor.PINK), BlockRegistry.PIPE_JUNCTION, true, true);

                addAfter(event, Blocks.PINK_BANNER, BlockRegistry.CLASSIC_CHECKPOINT_FLAG);
                addDyedBlocks(event, BlockRegistry.CLASSIC_CHECKPOINT_FLAG, BlockRegistry.CHECKPOINT_FLAGS, true, true);

                addAfter(event, BlockRegistry.CHECKPOINT_FLAGS.get(DyeColor.PINK), BlockRegistry.CLASSIC_GOAL_POLE);
                addDyedBlocks(event, BlockRegistry.CLASSIC_GOAL_POLE, BlockRegistry.GOAL_POLES, true, true);
            }

            if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
                addAfter(event, Items.PUFFERFISH, ItemRegistry.CHEEP_CHEEP);
                addAfter(event, ItemRegistry.CHEEP_CHEEP, ItemRegistry.COLD_CHEEP_CHEEP);
                addAfter(event, ItemRegistry.COLD_CHEEP_CHEEP, ItemRegistry.WARM_CHEEP_CHEEP);
                addAfter(event, ItemRegistry.WARM_CHEEP_CHEEP, ItemRegistry.EEP_CHEEP);
                addAfter(event, ItemRegistry.EEP_CHEEP, ItemRegistry.DEEP_CHEEP);
                addAfter(event, ItemRegistry.DEEP_CHEEP, ItemRegistry.COOKED_CHEEP_CHEEP);
                addAfter(event, ItemRegistry.COOKED_CHEEP_CHEEP, ItemRegistry.SPINY_CHEEP_CHEEP);
                addAfter(event, ItemRegistry.SPINY_CHEEP_CHEEP, ItemRegistry.COOKED_SPINY_CHEEP_CHEEP);
                addAfter(event, ItemRegistry.COOKED_SPINY_CHEEP_CHEEP, ItemRegistry.PORCUPUFFER);
                addAfter(event, ItemRegistry.PORCUPUFFER, ItemRegistry.COOKED_PORCUPUFFER);
            }

            if (event.getTabKey() == CreativeModeTabs.COMBAT) {
                addBefore(event, Items.SHIELD, ItemRegistry.WRENCH);
                addAfter(event, Items.SNOWBALL, ItemRegistry.LARGE_SNOWBALL);

                addAfter(event, Items.TOTEM_OF_UNDYING, ItemRegistry.SUPER_MUSHROOM);
                addAfter(event, ItemRegistry.SUPER_MUSHROOM, ItemRegistry.DASH_MUSHROOM);
                addAfter(event, ItemRegistry.DASH_MUSHROOM, ItemRegistry.ONE_UP_MUSHROOM);
                addAfter(event, ItemRegistry.ONE_UP_MUSHROOM, ItemRegistry.MINI_MUSHROOM);
                addAfter(event, ItemRegistry.MINI_MUSHROOM, ItemRegistry.MEGA_MUSHROOM);
                addAfter(event, ItemRegistry.MEGA_MUSHROOM, ItemRegistry.FIRE_FLOWER);
                addAfter(event, ItemRegistry.FIRE_FLOWER, ItemRegistry.ICE_FLOWER);
                addAfter(event, ItemRegistry.ICE_FLOWER, ItemRegistry.SUPER_STAR);
                addAfter(event, ItemRegistry.SUPER_STAR, ItemRegistry.GREEN_KOOPA_SHELL);
                addAfter(event, ItemRegistry.GREEN_KOOPA_SHELL, ItemRegistry.RED_KOOPA_SHELL);
                addAfter(event, ItemRegistry.RED_KOOPA_SHELL, ItemRegistry.GOLD_KOOPA_SHELL);

                addAfter(event, Items.TURTLE_HELMET, ItemRegistry.HAT);
                addAfter(event, ItemRegistry.HAT, ItemRegistry.SHIRT);
                addAfter(event, ItemRegistry.SHIRT, ItemRegistry.PANTS);
                addAfter(event, ItemRegistry.PANTS, ItemRegistry.SHOES);
                addAfter(event, ItemRegistry.SHOES, ItemRegistry.CROWN);
                addAfter(event, ItemRegistry.CROWN, ItemRegistry.BODICE);
                addAfter(event, ItemRegistry.BODICE, ItemRegistry.DRESS);
                addAfter(event, ItemRegistry.DRESS, ItemRegistry.HEELS);

                addAfter(event, ItemRegistry.HEELS, ItemRegistry.PLASTIC_BUCKET);
                addAfter(event, ItemRegistry.PLASTIC_BUCKET, ItemRegistry.CHRISTMAS_HAT);
                addAfter(event, ItemRegistry.CHRISTMAS_HAT, ItemRegistry.GREEN_KOOPA_SHOES);
                addAfter(event, ItemRegistry.GREEN_KOOPA_SHOES, ItemRegistry.RED_KOOPA_SHOES);
                addAfter(event, ItemRegistry.RED_KOOPA_SHOES, ItemRegistry.GOLDEN_KOOPA_SHOES);
                addAfter(event, ItemRegistry.GOLDEN_KOOPA_SHOES, ItemRegistry.WHITE_KOOPA_SHOES);
            }

            if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
                addAfter(event, Blocks.TARGET, BlockRegistry.ON_OFF_SWITCH);
                addAfter(event, BlockRegistry.ON_OFF_SWITCH, BlockRegistry.RED_DOTTED_LINE_BLOCK);
                addAfter(event, BlockRegistry.RED_DOTTED_LINE_BLOCK, BlockRegistry.BLUE_DOTTED_LINE_BLOCK);
                addAfter(event, BlockRegistry.BLUE_DOTTED_LINE_BLOCK, BlockRegistry.RED_MUSHROOM_TRAMPOLINE);
                addAfter(event, BlockRegistry.RED_MUSHROOM_TRAMPOLINE, BlockRegistry.BLUE_MUSHROOM_TRAMPOLINE);

                addAfter(event, Blocks.STONE_BUTTON, BlockRegistry.FUNGAL_STONE_BUTTON);
                addAfter(event, BlockRegistry.FUNGAL_STONE_BUTTON, BlockRegistry.DEEP_FUNGAL_STONE_BUTTON);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_STONE_BUTTON, BlockRegistry.AMETHYST_BUTTON);

                addAfter(event, Blocks.STONE_PRESSURE_PLATE, BlockRegistry.FUNGAL_STONE_PRESSURE_PLATE);
                addAfter(event, BlockRegistry.FUNGAL_STONE_PRESSURE_PLATE, BlockRegistry.DEEP_FUNGAL_STONE_PRESSURE_PLATE);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_STONE_PRESSURE_PLATE, BlockRegistry.AMETHYST_PRESSURE_PLATE);

                addAfter(event, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, BlockRegistry.FUNGAL_QUESTION_PANEL);
                addAfter(event, BlockRegistry.FUNGAL_QUESTION_PANEL, BlockRegistry.DEEP_FUNGAL_QUESTION_PANEL);

                addAfter(event, Items.DECORATED_POT, BlockRegistry.FUNGAL_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.FUNGAL_QUESTION_BLOCK, BlockRegistry.INVISIBLE_FUNGAL_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_FUNGAL_QUESTION_BLOCK, BlockRegistry.STORAGE_FUNGAL_BRICKS);
                addAfter(event, Items.REDSTONE_LAMP, BlockRegistry.CLEAR_WARP_PIPE);
                addAfter(event, BlockRegistry.CLEAR_WARP_PIPE, BlockRegistry.WARP_PIPES.get(DyeColor.GREEN));
                addAfter(event, BlockRegistry.WARP_PIPES.get(DyeColor.GREEN), BlockRegistry.CHECKPOINT_FLAGS.get(DyeColor.RED));
                addAfter(event, BlockRegistry.CHECKPOINT_FLAGS.get(DyeColor.RED), BlockRegistry.GOAL_POLES.get(DyeColor.RED));
                addAfter(event, BlockRegistry.GOAL_POLES.get(DyeColor.RED), BlockRegistry.BRICK_PEDESTAL);
            }

            if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
                ItemStack normal = new ItemStack(ItemRegistry.CHEEP_CHEEP_BUCKET.get());
                ItemStack cold = bucketVariant(ItemRegistry.CHEEP_CHEEP_BUCKET.get(), "cold");
                ItemStack warm = bucketVariant(ItemRegistry.CHEEP_CHEEP_BUCKET.get(), "warm");

                addAfter(event, Items.FISHING_ROD, ItemRegistry.WRENCH);
                addBefore(event, ItemRegistry.WRENCH, ItemRegistry.WARP_DISRUPTOR);

                addAfter(event, Items.TADPOLE_BUCKET, normal);
                addAfter(event, normal, cold);
                addAfter(event, cold, warm);
                addAfter(event, warm, ItemRegistry.EEP_CHEEP_BUCKET);
                addAfter(event, ItemRegistry.EEP_CHEEP_BUCKET, ItemRegistry.DEEP_CHEEP_BUCKET);
                addAfter(event, ItemRegistry.DEEP_CHEEP_BUCKET, ItemRegistry.SPINY_CHEEP_CHEEP_BUCKET);
                addAfter(event, Items.POWDER_SNOW_BUCKET, ItemRegistry.QUICKSAND_BUCKET);
                addAfter(event, ItemRegistry.QUICKSAND_BUCKET, ItemRegistry.RED_QUICKSAND_BUCKET);
                addAfter(event, Items.MILK_BUCKET, ItemRegistry.PLASTIC_BUCKET);
                addAfter(event, ItemRegistry.PLASTIC_BUCKET, ItemRegistry.PLASTIC_WATER_BUCKET);
                addAfter(event, ItemRegistry.PLASTIC_WATER_BUCKET, ItemRegistry.PLASTIC_POWDER_SNOW_BUCKET);
                addAfter(event, ItemRegistry.PLASTIC_POWDER_SNOW_BUCKET, ItemRegistry.PLASTIC_QUICKSAND_BUCKET);
                addAfter(event, ItemRegistry.PLASTIC_QUICKSAND_BUCKET, ItemRegistry.PLASTIC_RED_QUICKSAND_BUCKET);

                addAfter(event, Items.BAMBOO_CHEST_RAFT, ItemRegistry.MUSHROOT_BOAT);
                addAfter(event, ItemRegistry.MUSHROOT_BOAT, ItemRegistry.MUSHROOT_CHEST_BOAT);
            }

            if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
                addAfter(event, Items.SNOWBALL, ItemRegistry.LARGE_SNOWBALL);

                addAfter(event, Items.PUFFERFISH, ItemRegistry.SPINY_CHEEP_CHEEP);
                addAfter(event, ItemRegistry.SPINY_CHEEP_CHEEP, ItemRegistry.PORCUPUFFER);

                addAfter(event, Items.GUSTER_BANNER_PATTERN, ItemRegistry.BOWSER_BANNER_PATTERN);
                addAfter(event, ItemRegistry.BOWSER_BANNER_PATTERN, ItemRegistry.PLUMBER_BANNER_PATTERN);
                addAfter(event, Items.SNORT_POTTERY_SHERD, ItemRegistry.BOWSER_POTTERY_SHERD);
                addAfter(event, ItemRegistry.BOWSER_POTTERY_SHERD, ItemRegistry.PLUMBER_POTTERY_SHERD);

                addAfter(event, Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, ItemRegistry.MARIO_ARMOR_TRIM_SMITHING_TEMPLATE);
                addAfter(event, ItemRegistry.MARIO_ARMOR_TRIM_SMITHING_TEMPLATE, ItemRegistry.LUIGI_ARMOR_TRIM_SMITHING_TEMPLATE);
                addAfter(event, ItemRegistry.LUIGI_ARMOR_TRIM_SMITHING_TEMPLATE, ItemRegistry.WARIO_ARMOR_TRIM_SMITHING_TEMPLATE);
                addAfter(event, ItemRegistry.WARIO_ARMOR_TRIM_SMITHING_TEMPLATE, ItemRegistry.WALUIGI_ARMOR_TRIM_SMITHING_TEMPLATE);
                addAfter(event, ItemRegistry.WALUIGI_ARMOR_TRIM_SMITHING_TEMPLATE, ItemRegistry.PRINCESS_ARMOR_TRIM_SMITHING_TEMPLATE);
            }

            if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
                ItemStack cheepCheep = new ItemStack(ItemRegistry.CHEEP_CHEEP_SPAWN_EGG.get());
                ItemStack coldCheepCheep = variant(ItemRegistry.CHEEP_CHEEP_SPAWN_EGG.get(), CheepCheepVariants.COLD);
                ItemStack warmCheepCheep = variant(ItemRegistry.CHEEP_CHEEP_SPAWN_EGG.get(), CheepCheepVariants.WARM);

                ItemStack goomba = new ItemStack(ItemRegistry.GOOMBA_SPAWN_EGG.get());
                ItemStack goombella = customName(ItemRegistry.GOOMBA_SPAWN_EGG.get(), Component.literal("Goombella"));

                ItemStack porcupuffer = new ItemStack(ItemRegistry.PORCUPUFFER_SPAWN_EGG.get());
                ItemStack mrsPuff = variant(ItemRegistry.PORCUPUFFER_SPAWN_EGG.get(), PorcupufferVariants.MRS_PUFF);
                ItemStack qwilfish = variant(ItemRegistry.PORCUPUFFER_SPAWN_EGG.get(), PorcupufferVariants.QWILFISH);

                ItemStack piranhaPlant = new ItemStack(ItemRegistry.PIRANHA_PLANT_SPAWN_EGG.get());
                ItemStack cavePiranhaPlant = variant(ItemRegistry.PIRANHA_PLANT_SPAWN_EGG.get(), PiranhaPlantVariants.CAVE);
                ItemStack chomper = variant(ItemRegistry.PIRANHA_PLANT_SPAWN_EGG.get(), PiranhaPlantVariants.CHOMPER);
                ItemStack deepCavePiranhaPlant = variant(ItemRegistry.PIRANHA_PLANT_SPAWN_EGG.get(), PiranhaPlantVariants.DEEP_CAVE);
                ItemStack tropicalPiranhaPlant = variant(ItemRegistry.PIRANHA_PLANT_SPAWN_EGG.get(), PiranhaPlantVariants.TROPICAL);

                addAfter(event, Items.ZOMBIFIED_PIGLIN_SPAWN_EGG, ItemRegistry.SUPER_MUSHROOM_SPAWN_EGG);
                addAfter(event, ItemRegistry.SUPER_MUSHROOM_SPAWN_EGG, ItemRegistry.DASH_MUSHROOM_SPAWN_EGG);
                addAfter(event, ItemRegistry.DASH_MUSHROOM_SPAWN_EGG, ItemRegistry.ONE_UP_MUSHROOM_SPAWN_EGG);
                addAfter(event, ItemRegistry.ONE_UP_MUSHROOM_SPAWN_EGG, ItemRegistry.MINI_MUSHROOM_SPAWN_EGG);
                addAfter(event, ItemRegistry.MINI_MUSHROOM_SPAWN_EGG, ItemRegistry.MEGA_MUSHROOM_SPAWN_EGG);
                addAfter(event, ItemRegistry.MEGA_MUSHROOM_SPAWN_EGG, ItemRegistry.FIRE_FLOWER_SPAWN_EGG);
                addAfter(event, ItemRegistry.FIRE_FLOWER_SPAWN_EGG, ItemRegistry.ICE_FLOWER_SPAWN_EGG);
                addAfter(event, ItemRegistry.ICE_FLOWER_SPAWN_EGG, ItemRegistry.SUPER_STAR_SPAWN_EGG);
                addAfter(event, ItemRegistry.SUPER_STAR_SPAWN_EGG, ItemRegistry.MINI_GOOMBA_SPAWN_EGG);
                addAfter(event, ItemRegistry.MINI_GOOMBA_SPAWN_EGG, goomba);
                addAfter(event, goomba, goombella);
                addAfter(event, goombella, ItemRegistry.HEFTY_GOOMBA_SPAWN_EGG);
                addAfter(event, ItemRegistry.HEFTY_GOOMBA_SPAWN_EGG, ItemRegistry.MEGA_GOOMBA_SPAWN_EGG);
                addAfter(event, ItemRegistry.MEGA_GOOMBA_SPAWN_EGG, ItemRegistry.FIRE_GOOMBA_SPAWN_EGG);
                addAfter(event, ItemRegistry.FIRE_GOOMBA_SPAWN_EGG, ItemRegistry.SPLUNKIN_SPAWN_EGG);
                addAfter(event, ItemRegistry.SPLUNKIN_SPAWN_EGG, ItemRegistry.GREEN_KOOPA_TROOPA_SPAWN_EGG);
                addAfter(event, ItemRegistry.GREEN_KOOPA_TROOPA_SPAWN_EGG, ItemRegistry.RED_KOOPA_TROOPA_SPAWN_EGG);
                addAfter(event, ItemRegistry.RED_KOOPA_TROOPA_SPAWN_EGG, ItemRegistry.GOLD_KOOPA_TROOPA_SPAWN_EGG);
                addAfter(event, ItemRegistry.GOLD_KOOPA_TROOPA_SPAWN_EGG, ItemRegistry.DRY_BONES_SPAWN_EGG);
                addAfter(event, ItemRegistry.DRY_BONES_SPAWN_EGG, ItemRegistry.POKEY_SPAWN_EGG);
                addAfter(event, ItemRegistry.POKEY_SPAWN_EGG, ItemRegistry.SNOW_POKEY_SPAWN_EGG);
                addAfter(event, ItemRegistry.SNOW_POKEY_SPAWN_EGG, piranhaPlant);
                addAfter(event, piranhaPlant, tropicalPiranhaPlant);
                addAfter(event, tropicalPiranhaPlant, cavePiranhaPlant);
                addAfter(event, cavePiranhaPlant, deepCavePiranhaPlant);
                addAfter(event, deepCavePiranhaPlant, chomper);
                addAfter(event, chomper, ItemRegistry.BOO_SPAWN_EGG);
                addAfter(event, ItemRegistry.BOO_SPAWN_EGG, cheepCheep);
                addAfter(event, cheepCheep, warmCheepCheep);
                addAfter(event, warmCheepCheep, coldCheepCheep);
                addAfter(event, coldCheepCheep, ItemRegistry.EEP_CHEEP_SPAWN_EGG);
                addAfter(event, ItemRegistry.EEP_CHEEP_SPAWN_EGG, ItemRegistry.DEEP_CHEEP_SPAWN_EGG);
                addAfter(event, ItemRegistry.DEEP_CHEEP_SPAWN_EGG, ItemRegistry.SPINY_CHEEP_CHEEP_SPAWN_EGG);
                addAfter(event, ItemRegistry.SPINY_CHEEP_CHEEP_SPAWN_EGG, porcupuffer);
                addAfter(event, porcupuffer, mrsPuff);
                addAfter(event, mrsPuff, qwilfish);
            }

            if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
                addAfter(event, Blocks.OAK_FENCE_GATE, BlockRegistry.OAK_PICKET_FENCE);
                addAfter(event, BlockRegistry.OAK_PICKET_FENCE, BlockRegistry.HARD_OAK_BLOCK);
                addAfter(event, BlockRegistry.HARD_OAK_BLOCK, BlockRegistry.HARD_OAK_STAIRS);
                addAfter(event, BlockRegistry.HARD_OAK_STAIRS, BlockRegistry.HARD_OAK_SLAB);
                addAfter(event, BlockRegistry.HARD_OAK_SLAB, BlockRegistry.HARD_OAK_WALL);

                addAfter(event, Blocks.OAK_BUTTON, BlockRegistry.OAK_LOG_BRIDGE_STAIRS);
                addAfter(event, BlockRegistry.OAK_LOG_BRIDGE_STAIRS, BlockRegistry.STRIPPED_OAK_LOG_BRIDGE_STAIRS);
                addAfter(event, BlockRegistry.STRIPPED_OAK_LOG_BRIDGE_STAIRS, BlockRegistry.OAK_LOG_BRIDGE);
                addAfter(event, BlockRegistry.OAK_LOG_BRIDGE, BlockRegistry.STRIPPED_OAK_LOG_BRIDGE);

                addAfter(event, Blocks.SPRUCE_FENCE_GATE, BlockRegistry.SPRUCE_PICKET_FENCE);
                addAfter(event, BlockRegistry.SPRUCE_PICKET_FENCE, BlockRegistry.HARD_SPRUCE_BLOCK);
                addAfter(event, BlockRegistry.HARD_SPRUCE_BLOCK, BlockRegistry.HARD_SPRUCE_STAIRS);
                addAfter(event, BlockRegistry.HARD_SPRUCE_STAIRS, BlockRegistry.HARD_SPRUCE_SLAB);
                addAfter(event, BlockRegistry.HARD_SPRUCE_SLAB, BlockRegistry.HARD_SPRUCE_WALL);

                addAfter(event, Blocks.SPRUCE_BUTTON, BlockRegistry.SPRUCE_LOG_BRIDGE_STAIRS);
                addAfter(event, BlockRegistry.SPRUCE_LOG_BRIDGE_STAIRS, BlockRegistry.STRIPPED_SPRUCE_LOG_BRIDGE_STAIRS);
                addAfter(event, BlockRegistry.STRIPPED_SPRUCE_LOG_BRIDGE_STAIRS, BlockRegistry.SPRUCE_LOG_BRIDGE);
                addAfter(event, BlockRegistry.SPRUCE_LOG_BRIDGE, BlockRegistry.STRIPPED_SPRUCE_LOG_BRIDGE);

                addAfter(event, Blocks.BIRCH_FENCE_GATE, BlockRegistry.BIRCH_PICKET_FENCE);
                addAfter(event, BlockRegistry.BIRCH_PICKET_FENCE, BlockRegistry.HARD_BIRCH_BLOCK);
                addAfter(event, BlockRegistry.HARD_BIRCH_BLOCK, BlockRegistry.HARD_BIRCH_STAIRS);
                addAfter(event, BlockRegistry.HARD_BIRCH_STAIRS, BlockRegistry.HARD_BIRCH_SLAB);
                addAfter(event, BlockRegistry.HARD_BIRCH_SLAB, BlockRegistry.HARD_BIRCH_WALL);

                addAfter(event, Blocks.BIRCH_BUTTON, BlockRegistry.BIRCH_LOG_BRIDGE_STAIRS);
                addAfter(event, BlockRegistry.BIRCH_LOG_BRIDGE_STAIRS, BlockRegistry.STRIPPED_BIRCH_LOG_BRIDGE_STAIRS);
                addAfter(event, BlockRegistry.STRIPPED_BIRCH_LOG_BRIDGE_STAIRS, BlockRegistry.BIRCH_LOG_BRIDGE);
                addAfter(event, BlockRegistry.BIRCH_LOG_BRIDGE, BlockRegistry.STRIPPED_BIRCH_LOG_BRIDGE);

                addAfter(event, Blocks.JUNGLE_FENCE_GATE, BlockRegistry.JUNGLE_PICKET_FENCE);
                addAfter(event, BlockRegistry.JUNGLE_PICKET_FENCE, BlockRegistry.HARD_JUNGLE_BLOCK);
                addAfter(event, BlockRegistry.HARD_JUNGLE_BLOCK, BlockRegistry.HARD_JUNGLE_STAIRS);
                addAfter(event, BlockRegistry.HARD_JUNGLE_STAIRS, BlockRegistry.HARD_JUNGLE_SLAB);
                addAfter(event, BlockRegistry.HARD_JUNGLE_SLAB, BlockRegistry.HARD_JUNGLE_WALL);

                addAfter(event, Blocks.JUNGLE_BUTTON, BlockRegistry.JUNGLE_LOG_BRIDGE_STAIRS);
                addAfter(event, BlockRegistry.JUNGLE_LOG_BRIDGE_STAIRS, BlockRegistry.STRIPPED_JUNGLE_LOG_BRIDGE_STAIRS);
                addAfter(event, BlockRegistry.STRIPPED_JUNGLE_LOG_BRIDGE_STAIRS, BlockRegistry.JUNGLE_LOG_BRIDGE);
                addAfter(event, BlockRegistry.JUNGLE_LOG_BRIDGE, BlockRegistry.STRIPPED_JUNGLE_LOG_BRIDGE);

                addAfter(event, Blocks.ACACIA_FENCE_GATE, BlockRegistry.ACACIA_PICKET_FENCE);
                addAfter(event, BlockRegistry.ACACIA_PICKET_FENCE, BlockRegistry.HARD_ACACIA_BLOCK);
                addAfter(event, BlockRegistry.HARD_ACACIA_BLOCK, BlockRegistry.HARD_ACACIA_STAIRS);
                addAfter(event, BlockRegistry.HARD_ACACIA_STAIRS, BlockRegistry.HARD_ACACIA_SLAB);
                addAfter(event, BlockRegistry.HARD_ACACIA_SLAB, BlockRegistry.HARD_ACACIA_WALL);

                addAfter(event, Blocks.ACACIA_BUTTON, BlockRegistry.ACACIA_LOG_BRIDGE_STAIRS);
                addAfter(event, BlockRegistry.ACACIA_LOG_BRIDGE_STAIRS, BlockRegistry.STRIPPED_ACACIA_LOG_BRIDGE_STAIRS);
                addAfter(event, BlockRegistry.STRIPPED_ACACIA_LOG_BRIDGE_STAIRS, BlockRegistry.ACACIA_LOG_BRIDGE);
                addAfter(event, BlockRegistry.ACACIA_LOG_BRIDGE, BlockRegistry.STRIPPED_ACACIA_LOG_BRIDGE);

                addAfter(event, Blocks.DARK_OAK_FENCE_GATE, BlockRegistry.DARK_OAK_PICKET_FENCE);
                addAfter(event, BlockRegistry.DARK_OAK_PICKET_FENCE, BlockRegistry.HARD_DARK_OAK_BLOCK);
                addAfter(event, BlockRegistry.HARD_DARK_OAK_BLOCK, BlockRegistry.HARD_DARK_OAK_STAIRS);
                addAfter(event, BlockRegistry.HARD_DARK_OAK_STAIRS, BlockRegistry.HARD_DARK_OAK_SLAB);
                addAfter(event, BlockRegistry.HARD_DARK_OAK_SLAB, BlockRegistry.HARD_DARK_OAK_WALL);

                addAfter(event, Blocks.DARK_OAK_BUTTON, BlockRegistry.DARK_OAK_LOG_BRIDGE_STAIRS);
                addAfter(event, BlockRegistry.DARK_OAK_LOG_BRIDGE_STAIRS, BlockRegistry.STRIPPED_DARK_OAK_LOG_BRIDGE_STAIRS);
                addAfter(event, BlockRegistry.STRIPPED_DARK_OAK_LOG_BRIDGE_STAIRS, BlockRegistry.DARK_OAK_LOG_BRIDGE);
                addAfter(event, BlockRegistry.DARK_OAK_LOG_BRIDGE, BlockRegistry.STRIPPED_DARK_OAK_LOG_BRIDGE);

                addAfter(event, Blocks.MANGROVE_FENCE_GATE, BlockRegistry.MANGROVE_PICKET_FENCE);
                addAfter(event, BlockRegistry.MANGROVE_PICKET_FENCE, BlockRegistry.HARD_MANGROVE_BLOCK);
                addAfter(event, BlockRegistry.HARD_MANGROVE_BLOCK, BlockRegistry.HARD_MANGROVE_STAIRS);
                addAfter(event, BlockRegistry.HARD_MANGROVE_STAIRS, BlockRegistry.HARD_MANGROVE_SLAB);
                addAfter(event, BlockRegistry.HARD_MANGROVE_SLAB, BlockRegistry.HARD_MANGROVE_WALL);

                addAfter(event, Blocks.MANGROVE_BUTTON, BlockRegistry.MANGROVE_LOG_BRIDGE_STAIRS);
                addAfter(event, BlockRegistry.MANGROVE_LOG_BRIDGE_STAIRS, BlockRegistry.STRIPPED_MANGROVE_LOG_BRIDGE_STAIRS);
                addAfter(event, BlockRegistry.STRIPPED_MANGROVE_LOG_BRIDGE_STAIRS, BlockRegistry.MANGROVE_LOG_BRIDGE);
                addAfter(event, BlockRegistry.MANGROVE_LOG_BRIDGE, BlockRegistry.STRIPPED_MANGROVE_LOG_BRIDGE);

                addAfter(event, Blocks.CHERRY_FENCE_GATE, BlockRegistry.CHERRY_PICKET_FENCE);
                addAfter(event, BlockRegistry.CHERRY_PICKET_FENCE, BlockRegistry.HARD_CHERRY_BLOCK);
                addAfter(event, BlockRegistry.HARD_CHERRY_BLOCK, BlockRegistry.HARD_CHERRY_STAIRS);
                addAfter(event, BlockRegistry.HARD_CHERRY_STAIRS, BlockRegistry.HARD_CHERRY_SLAB);
                addAfter(event, BlockRegistry.HARD_CHERRY_SLAB, BlockRegistry.HARD_CHERRY_WALL);

                addAfter(event, Blocks.CHERRY_BUTTON, BlockRegistry.CHERRY_LOG_BRIDGE_STAIRS);
                addAfter(event, BlockRegistry.CHERRY_LOG_BRIDGE_STAIRS, BlockRegistry.STRIPPED_CHERRY_LOG_BRIDGE_STAIRS);
                addAfter(event, BlockRegistry.STRIPPED_CHERRY_LOG_BRIDGE_STAIRS, BlockRegistry.CHERRY_LOG_BRIDGE);
                addAfter(event, BlockRegistry.CHERRY_LOG_BRIDGE, BlockRegistry.STRIPPED_CHERRY_LOG_BRIDGE);

                addAfter(event, Blocks.BAMBOO_FENCE_GATE, BlockRegistry.BAMBOO_PICKET_FENCE);
                addAfter(event, BlockRegistry.BAMBOO_PICKET_FENCE, BlockRegistry.HARD_BAMBOO_BLOCK);
                addAfter(event, BlockRegistry.HARD_BAMBOO_BLOCK, BlockRegistry.HARD_BAMBOO_STAIRS);
                addAfter(event, BlockRegistry.HARD_BAMBOO_STAIRS, BlockRegistry.HARD_BAMBOO_SLAB);
                addAfter(event, BlockRegistry.HARD_BAMBOO_SLAB, BlockRegistry.HARD_BAMBOO_WALL);

                addAfter(event, Blocks.BAMBOO_BUTTON, BlockRegistry.BAMBOO_BRIDGE_STAIRS);
                addAfter(event, BlockRegistry.BAMBOO_BRIDGE_STAIRS, BlockRegistry.STRIPPED_BAMBOO_BRIDGE_STAIRS);
                addAfter(event, BlockRegistry.STRIPPED_BAMBOO_BRIDGE_STAIRS, BlockRegistry.BAMBOO_BRIDGE);
                addAfter(event, BlockRegistry.BAMBOO_BRIDGE, BlockRegistry.STRIPPED_BAMBOO_BRIDGE);

                addAfter(event, BlockRegistry.STRIPPED_BAMBOO_BRIDGE, BlockRegistry.MUSHROOT_LOG);
                addAfter(event, BlockRegistry.MUSHROOT_LOG, BlockRegistry.MUSHROOT_WOOD);
                addAfter(event, BlockRegistry.MUSHROOT_WOOD, BlockRegistry.STRIPPED_MUSHROOT_LOG);
                addAfter(event, BlockRegistry.STRIPPED_MUSHROOT_LOG, BlockRegistry.STRIPPED_MUSHROOT_WOOD);
                addAfter(event, BlockRegistry.STRIPPED_MUSHROOT_WOOD, BlockRegistry.MUSHROOT_PLANKS);
                addAfter(event, BlockRegistry.MUSHROOT_PLANKS, BlockRegistry.MUSHROOT_STAIRS);
                addAfter(event, BlockRegistry.MUSHROOT_STAIRS, BlockRegistry.MUSHROOT_SLAB);
                addAfter(event, BlockRegistry.MUSHROOT_SLAB, BlockRegistry.MUSHROOT_FENCE);
                addAfter(event, BlockRegistry.MUSHROOT_FENCE, BlockRegistry.MUSHROOT_FENCE_GATE);
                addAfter(event, BlockRegistry.MUSHROOT_FENCE_GATE, BlockRegistry.MUSHROOT_PICKET_FENCE);
                addAfter(event, BlockRegistry.MUSHROOT_PICKET_FENCE, BlockRegistry.MUSHROOT_BOARDS);
                addAfter(event, BlockRegistry.MUSHROOT_BOARDS, BlockRegistry.MUSHROOT_BOARD_STAIRS);
                addAfter(event, BlockRegistry.MUSHROOT_BOARD_STAIRS, BlockRegistry.MUSHROOT_BOARD_SLAB);
                addAfter(event, BlockRegistry.MUSHROOT_BOARD_SLAB, BlockRegistry.MUSHROOT_BOARD_WALL);
                addAfter(event, BlockRegistry.MUSHROOT_BOARD_WALL, BlockRegistry.MUSHROOT_PANELS);
                addAfter(event, BlockRegistry.MUSHROOT_PANELS, BlockRegistry.MUSHROOT_PANEL_STAIRS);
                addAfter(event, BlockRegistry.MUSHROOT_PANEL_STAIRS, BlockRegistry.MUSHROOT_PANEL_SLAB);
                addAfter(event, BlockRegistry.MUSHROOT_PANEL_SLAB, BlockRegistry.MUSHROOT_PANEL_WALL);
                addAfter(event, BlockRegistry.MUSHROOT_PANEL_WALL, BlockRegistry.HARD_MUSHROOT_BLOCK);
                addAfter(event, BlockRegistry.HARD_MUSHROOT_BLOCK, BlockRegistry.HARD_MUSHROOT_STAIRS);
                addAfter(event, BlockRegistry.HARD_MUSHROOT_STAIRS, BlockRegistry.HARD_MUSHROOT_SLAB);
                addAfter(event, BlockRegistry.HARD_MUSHROOT_SLAB, BlockRegistry.HARD_MUSHROOT_WALL);
                addAfter(event, BlockRegistry.HARD_MUSHROOT_WALL, BlockRegistry.MUSHROOT_FRAMED_WINDOW);
                addAfter(event, BlockRegistry.MUSHROOT_FRAMED_WINDOW, BlockRegistry.MUSHROOT_FRAMED_WINDOW_PANE);
                addAfter(event, BlockRegistry.MUSHROOT_FRAMED_WINDOW_PANE, BlockRegistry.MUSHROOT_DOOR);
                addAfter(event, BlockRegistry.MUSHROOT_DOOR, BlockRegistry.MUSHROOT_TRAPDOOR);
                addAfter(event, BlockRegistry.MUSHROOT_TRAPDOOR, BlockRegistry.MUSHROOT_PRESSURE_PLATE);
                addAfter(event, BlockRegistry.MUSHROOT_PRESSURE_PLATE, BlockRegistry.MUSHROOT_BUTTON);
                addAfter(event, BlockRegistry.MUSHROOT_BUTTON, BlockRegistry.MUSHROOT_LOG_PLATFORM);
                addAfter(event, BlockRegistry.MUSHROOT_LOG_PLATFORM, BlockRegistry.STRIPPED_MUSHROOT_LOG_PLATFORM);
                addAfter(event, BlockRegistry.STRIPPED_MUSHROOT_LOG_PLATFORM, BlockRegistry.MUSHROOT_LOG_BRIDGE_STAIRS);
                addAfter(event, BlockRegistry.MUSHROOT_LOG_BRIDGE_STAIRS, BlockRegistry.STRIPPED_MUSHROOT_LOG_BRIDGE_STAIRS);
                addAfter(event, BlockRegistry.STRIPPED_MUSHROOT_LOG_BRIDGE_STAIRS, BlockRegistry.MUSHROOT_LOG_BRIDGE);
                addAfter(event, BlockRegistry.MUSHROOT_LOG_BRIDGE, BlockRegistry.STRIPPED_MUSHROOT_LOG_BRIDGE);

                addAfter(event, Blocks.CRIMSON_FENCE_GATE, BlockRegistry.CRIMSON_PICKET_FENCE);
                addAfter(event, BlockRegistry.CRIMSON_PICKET_FENCE, BlockRegistry.HARD_CRIMSON_BLOCK);
                addAfter(event, BlockRegistry.HARD_CRIMSON_BLOCK, BlockRegistry.HARD_CRIMSON_STAIRS);
                addAfter(event, BlockRegistry.HARD_CRIMSON_STAIRS, BlockRegistry.HARD_CRIMSON_SLAB);
                addAfter(event, BlockRegistry.HARD_CRIMSON_SLAB, BlockRegistry.HARD_CRIMSON_WALL);

                addAfter(event, Blocks.CRIMSON_BUTTON, BlockRegistry.CRIMSON_STEM_BRIDGE_STAIRS);
                addAfter(event, BlockRegistry.CRIMSON_STEM_BRIDGE_STAIRS, BlockRegistry.STRIPPED_CRIMSON_STEM_BRIDGE_STAIRS);
                addAfter(event, BlockRegistry.STRIPPED_CRIMSON_STEM_BRIDGE_STAIRS, BlockRegistry.CRIMSON_STEM_BRIDGE);
                addAfter(event, BlockRegistry.CRIMSON_STEM_BRIDGE, BlockRegistry.STRIPPED_CRIMSON_STEM_BRIDGE);

                addAfter(event, Blocks.WARPED_FENCE_GATE, BlockRegistry.WARPED_PICKET_FENCE);
                addAfter(event, BlockRegistry.WARPED_PICKET_FENCE, BlockRegistry.HARD_WARPED_BLOCK);
                addAfter(event, BlockRegistry.HARD_WARPED_BLOCK, BlockRegistry.HARD_WARPED_STAIRS);
                addAfter(event, BlockRegistry.HARD_WARPED_STAIRS, BlockRegistry.HARD_WARPED_SLAB);
                addAfter(event, BlockRegistry.HARD_WARPED_SLAB, BlockRegistry.HARD_WARPED_WALL);

                addAfter(event, Blocks.WARPED_BUTTON, BlockRegistry.WARPED_STEM_BRIDGE_STAIRS);
                addAfter(event, BlockRegistry.WARPED_STEM_BRIDGE_STAIRS, BlockRegistry.STRIPPED_WARPED_STEM_BRIDGE_STAIRS);
                addAfter(event, BlockRegistry.STRIPPED_WARPED_STEM_BRIDGE_STAIRS, BlockRegistry.WARPED_STEM_BRIDGE);
                addAfter(event, BlockRegistry.WARPED_STEM_BRIDGE, BlockRegistry.STRIPPED_WARPED_STEM_BRIDGE);

                addAfter(event, Blocks.CHAIN, BlockRegistry.IRON_SPIKE);
                addAfter(event, BlockRegistry.IRON_SPIKE, BlockRegistry.SPIKE_PANEL);

                addAfter(event, Blocks.POLISHED_ANDESITE_SLAB, BlockRegistry.FUNGAL_STONE);
                addAfter(event, BlockRegistry.FUNGAL_STONE, BlockRegistry.FUNGAL_STONE_STAIRS);
                addAfter(event, BlockRegistry.FUNGAL_STONE_STAIRS, BlockRegistry.FUNGAL_STONE_SLAB);
                addAfter(event, BlockRegistry.FUNGAL_STONE_SLAB, BlockRegistry.FUNGAL_STONE_WALL);
                addAfter(event, BlockRegistry.FUNGAL_STONE_WALL, BlockRegistry.FUNGAL_STONE_PRESSURE_PLATE);
                addAfter(event, BlockRegistry.FUNGAL_STONE_PRESSURE_PLATE, BlockRegistry.FUNGAL_STONE_BUTTON);

                addAfter(event, BlockRegistry.FUNGAL_STONE_BUTTON, BlockRegistry.ROCKY_FUNGAL_STONE);
                addAfter(event, BlockRegistry.ROCKY_FUNGAL_STONE, BlockRegistry.ROCKY_FUNGAL_STONE_STAIRS);
                addAfter(event, BlockRegistry.ROCKY_FUNGAL_STONE_STAIRS, BlockRegistry.ROCKY_FUNGAL_STONE_SLAB);
                addAfter(event, BlockRegistry.ROCKY_FUNGAL_STONE_SLAB, BlockRegistry.ROCKY_FUNGAL_STONE_WALL);

                addAfter(event, BlockRegistry.ROCKY_FUNGAL_STONE_WALL, BlockRegistry.FUNGAL_COBBLESTONE);
                addAfter(event, BlockRegistry.FUNGAL_COBBLESTONE, BlockRegistry.FUNGAL_COBBLESTONE_STAIRS);
                addAfter(event, BlockRegistry.FUNGAL_COBBLESTONE_STAIRS, BlockRegistry.FUNGAL_COBBLESTONE_SLAB);
                addAfter(event, BlockRegistry.FUNGAL_COBBLESTONE_SLAB, BlockRegistry.FUNGAL_COBBLESTONE_WALL);

                addAfter(event, BlockRegistry.FUNGAL_COBBLESTONE_WALL, BlockRegistry.POLISHED_FUNGAL_STONE);
                addAfter(event, BlockRegistry.POLISHED_FUNGAL_STONE, BlockRegistry.POLISHED_FUNGAL_STONE_STAIRS);
                addAfter(event, BlockRegistry.POLISHED_FUNGAL_STONE_STAIRS, BlockRegistry.POLISHED_FUNGAL_STONE_SLAB);
                addAfter(event, BlockRegistry.POLISHED_FUNGAL_STONE_SLAB, BlockRegistry.POLISHED_FUNGAL_STONE_WALL);

                addAfter(event, BlockRegistry.POLISHED_FUNGAL_STONE_WALL, BlockRegistry.HARD_FUNGAL_BLOCK);
                addAfter(event, BlockRegistry.HARD_FUNGAL_BLOCK, BlockRegistry.HARD_FUNGAL_STAIRS);
                addAfter(event, BlockRegistry.HARD_FUNGAL_STAIRS, BlockRegistry.HARD_FUNGAL_SLAB);
                addAfter(event, BlockRegistry.HARD_FUNGAL_SLAB, BlockRegistry.HARD_FUNGAL_WALL);

                addAfter(event, BlockRegistry.HARD_FUNGAL_WALL, BlockRegistry.DEEP_FUNGAL_STONE);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_STONE, BlockRegistry.DEEP_FUNGAL_STONE_STAIRS);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_STONE_STAIRS, BlockRegistry.DEEP_FUNGAL_STONE_SLAB);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_STONE_SLAB, BlockRegistry.DEEP_FUNGAL_STONE_WALL);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_STONE_WALL, BlockRegistry.DEEP_FUNGAL_STONE_PRESSURE_PLATE);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_STONE_PRESSURE_PLATE, BlockRegistry.DEEP_FUNGAL_STONE_BUTTON);

                addAfter(event, BlockRegistry.DEEP_FUNGAL_STONE_BUTTON, BlockRegistry.POLISHED_DEEP_FUNGAL_STONE);
                addAfter(event, BlockRegistry.POLISHED_DEEP_FUNGAL_STONE, BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_STAIRS);
                addAfter(event, BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_STAIRS, BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_SLAB);
                addAfter(event, BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_SLAB, BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_WALL);

                addAfter(event, Blocks.STONE_BRICK_WALL, BlockRegistry.STONE_BRICK_PEDESTAL);
                addAfter(event, Blocks.CHISELED_STONE_BRICKS, BlockRegistry.SMASHABLE_STONE_BRICKS);

                addAfter(event, Blocks.MOSSY_STONE_BRICK_WALL, BlockRegistry.MOSSY_STONE_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.MOSSY_STONE_BRICK_PEDESTAL, BlockRegistry.SMASHABLE_MOSSY_STONE_BRICKS);

                addAfter(event, Blocks.REINFORCED_DEEPSLATE, Blocks.CALCITE);
                addAfter(event, Blocks.CALCITE, BlockRegistry.CALCITE_STAIRS);
                addAfter(event, BlockRegistry.CALCITE_STAIRS, BlockRegistry.CALCITE_SLAB);
                addAfter(event, BlockRegistry.CALCITE_SLAB, BlockRegistry.CALCITE_WALL);
                addAfter(event, BlockRegistry.CALCITE_WALL, BlockRegistry.CALCITE_PRESSURE_PLATE);
                addAfter(event, BlockRegistry.CALCITE_PRESSURE_PLATE, BlockRegistry.CALCITE_BUTTON);

                addAfter(event, BlockRegistry.CALCITE_BUTTON, BlockRegistry.POLISHED_CALCITE.get(DyeColor.WHITE));
                addAfter(event, BlockRegistry.POLISHED_CALCITE.get(DyeColor.WHITE), BlockRegistry.POLISHED_WHITE_CALCITE_STAIRS);
                addAfter(event, BlockRegistry.POLISHED_WHITE_CALCITE_STAIRS, BlockRegistry.POLISHED_WHITE_CALCITE_SLAB);
                addAfter(event, BlockRegistry.POLISHED_WHITE_CALCITE_SLAB, BlockRegistry.POLISHED_WHITE_CALCITE_WALL);
                addAfter(event, BlockRegistry.POLISHED_WHITE_CALCITE_WALL, BlockRegistry.CALCITE_BRICKS.get(DyeColor.WHITE));

                addAfter(event, BlockRegistry.CALCITE_BRICKS.get(DyeColor.WHITE), BlockRegistry.WHITE_CALCITE_BRICK_STAIRS);
                addAfter(event, BlockRegistry.WHITE_CALCITE_BRICK_STAIRS, BlockRegistry.WHITE_CALCITE_BRICK_SLAB);
                addAfter(event, BlockRegistry.WHITE_CALCITE_BRICK_SLAB, BlockRegistry.WHITE_CALCITE_BRICK_WALL);
                addAfter(event, BlockRegistry.WHITE_CALCITE_BRICK_WALL, BlockRegistry.CHISELED_CALCITE_BRICKS.get(DyeColor.WHITE));
                addAfter(event, BlockRegistry.CHISELED_CALCITE_BRICKS.get(DyeColor.WHITE), BlockRegistry.CALCITE_BRICK_PEDESTAL.get(DyeColor.WHITE));

                addAfter(event, BlockRegistry.CALCITE_BRICK_PEDESTAL.get(DyeColor.WHITE), BlockRegistry.CALCITE_CHECKERED_TILES);
                addAfter(event, BlockRegistry.CALCITE_CHECKERED_TILES, BlockRegistry.CALCITE_CHECKERED_TILE_SLAB);
                addAfter(event, BlockRegistry.CALCITE_CHECKERED_TILE_SLAB, BlockRegistry.CALCITE_CHECKERED_TILE_STAIRS);
                addAfter(event, BlockRegistry.CALCITE_CHECKERED_TILE_STAIRS, BlockRegistry.CALCITE_CHECKERED_TILE_WALL);

                addAfter(event, Blocks.DEEPSLATE_BRICK_WALL, BlockRegistry.DEEPSLATE_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.DEEPSLATE_BRICK_PEDESTAL, BlockRegistry.SMASHABLE_DEEPSLATE_BRICKS);
                addAfter(event, Blocks.DEEPSLATE_TILE_WALL, BlockRegistry.DEEPSLATE_TILE_PEDESTAL);
                addAfter(event, BlockRegistry.DEEPSLATE_TILE_PEDESTAL, BlockRegistry.SMASHABLE_DEEPSLATE_TILES);

                addAfter(event, Blocks.TUFF_BRICK_WALL, BlockRegistry.TUFF_BRICK_PEDESTAL);
                addAfter(event, Blocks.CHISELED_TUFF_BRICKS, BlockRegistry.SMASHABLE_TUFF_BRICKS);

                addAfter(event, Blocks.BRICK_WALL, BlockRegistry.BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.BRICK_PEDESTAL, BlockRegistry.SMASHABLE_BRICKS);

                addAfter(event, Blocks.MUD_BRICK_WALL, BlockRegistry.MUD_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.MUD_BRICK_PEDESTAL, BlockRegistry.SMASHABLE_MUD_BRICKS);

                addAfter(event, Blocks.CUT_SANDSTONE_SLAB, BlockRegistry.SANDSTONE_BRICKS);
                addAfter(event, BlockRegistry.SANDSTONE_BRICKS, BlockRegistry.CRACKED_SANDSTONE_BRICKS);
                addAfter(event, BlockRegistry.CRACKED_SANDSTONE_BRICKS, BlockRegistry.SANDSTONE_BRICK_STAIRS);
                addAfter(event, BlockRegistry.SANDSTONE_BRICK_STAIRS, BlockRegistry.SANDSTONE_BRICK_SLAB);
                addAfter(event, BlockRegistry.SANDSTONE_BRICK_SLAB, BlockRegistry.SANDSTONE_BRICK_WALL);
                addAfter(event, BlockRegistry.SANDSTONE_BRICK_WALL, BlockRegistry.SANDSTONE_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.SANDSTONE_BRICK_PEDESTAL, BlockRegistry.CHISELED_SANDSTONE_BRICKS);

                addAfter(event, Blocks.CUT_RED_SANDSTONE_SLAB, BlockRegistry.RED_SANDSTONE_BRICKS);
                addAfter(event, BlockRegistry.RED_SANDSTONE_BRICKS, BlockRegistry.CRACKED_RED_SANDSTONE_BRICKS);
                addAfter(event, BlockRegistry.CRACKED_RED_SANDSTONE_BRICKS, BlockRegistry.RED_SANDSTONE_BRICK_STAIRS);
                addAfter(event, BlockRegistry.RED_SANDSTONE_BRICK_STAIRS, BlockRegistry.RED_SANDSTONE_BRICK_SLAB);
                addAfter(event, BlockRegistry.RED_SANDSTONE_BRICK_SLAB, BlockRegistry.RED_SANDSTONE_BRICK_WALL);
                addAfter(event, BlockRegistry.RED_SANDSTONE_BRICK_WALL, BlockRegistry.RED_SANDSTONE_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.RED_SANDSTONE_BRICK_PEDESTAL, BlockRegistry.CHISELED_RED_SANDSTONE_BRICKS);

                addAfter(event, Blocks.PRISMARINE_BRICK_SLAB, BlockRegistry.PRISMARINE_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.PRISMARINE_BRICK_PEDESTAL, BlockRegistry.SMASHABLE_PRISMARINE_BRICKS);

                addAfter(event, Blocks.DARK_PRISMARINE_SLAB, BlockRegistry.DARK_PRISMARINE_PEDESTAL);
                addAfter(event, BlockRegistry.DARK_PRISMARINE_PEDESTAL, BlockRegistry.SMASHABLE_DARK_PRISMARINE);

                addAfter(event, Blocks.AMETHYST_BLOCK, BlockRegistry.AMETHYST_STAIRS);
                addAfter(event, BlockRegistry.AMETHYST_STAIRS, BlockRegistry.AMETHYST_SLAB);
                addAfter(event, BlockRegistry.AMETHYST_SLAB, BlockRegistry.AMETHYST_WALL);
                addAfter(event, BlockRegistry.AMETHYST_WALL, BlockRegistry.AMETHYST_PRESSURE_PLATE);
                addAfter(event, BlockRegistry.AMETHYST_PRESSURE_PLATE, BlockRegistry.AMETHYST_BUTTON);
                addAfter(event, BlockRegistry.AMETHYST_BUTTON, BlockRegistry.POLISHED_AMETHYST);
                addAfter(event, BlockRegistry.POLISHED_AMETHYST, BlockRegistry.POLISHED_AMETHYST_STAIRS);
                addAfter(event, BlockRegistry.POLISHED_AMETHYST_STAIRS, BlockRegistry.POLISHED_AMETHYST_SLAB);
                addAfter(event, BlockRegistry.POLISHED_AMETHYST_SLAB, BlockRegistry.POLISHED_AMETHYST_WALL);
                addAfter(event, BlockRegistry.POLISHED_AMETHYST_WALL, BlockRegistry.AMETHYST_BRICKS);
                addAfter(event, BlockRegistry.AMETHYST_BRICKS, BlockRegistry.CRACKED_AMETHYST_BRICKS);
                addAfter(event, BlockRegistry.CRACKED_AMETHYST_BRICKS, BlockRegistry.AMETHYST_BRICK_STAIRS);
                addAfter(event, BlockRegistry.AMETHYST_BRICK_STAIRS, BlockRegistry.AMETHYST_BRICK_SLAB);
                addAfter(event, BlockRegistry.AMETHYST_BRICK_SLAB, BlockRegistry.AMETHYST_BRICK_WALL);
                addAfter(event, BlockRegistry.AMETHYST_BRICK_WALL, BlockRegistry.AMETHYST_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.AMETHYST_BRICK_PEDESTAL, BlockRegistry.CHISELED_AMETHYST_BRICKS);

                addAfter(event, BlockRegistry.CHISELED_AMETHYST_BRICKS, BlockRegistry.FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.FUNGAL_BRICKS, BlockRegistry.CRACKED_FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.CRACKED_FUNGAL_BRICKS, BlockRegistry.FUNGAL_BRICK_STAIRS);
                addAfter(event, BlockRegistry.FUNGAL_BRICK_STAIRS, BlockRegistry.FUNGAL_BRICK_SLAB);
                addAfter(event, BlockRegistry.FUNGAL_BRICK_SLAB, BlockRegistry.FUNGAL_BRICK_WALL);
                addAfter(event, BlockRegistry.FUNGAL_BRICK_WALL, BlockRegistry.FUNGAL_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.FUNGAL_BRICK_PEDESTAL, BlockRegistry.CHISELED_FUNGAL_BRICKS);

                addAfter(event, BlockRegistry.CHISELED_FUNGAL_BRICKS, BlockRegistry.POLISHED_FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.POLISHED_FUNGAL_BRICKS, BlockRegistry.CRACKED_POLISHED_FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.CRACKED_POLISHED_FUNGAL_BRICKS, BlockRegistry.POLISHED_FUNGAL_BRICK_STAIRS);
                addAfter(event, BlockRegistry.POLISHED_FUNGAL_BRICK_STAIRS, BlockRegistry.POLISHED_FUNGAL_BRICK_SLAB);
                addAfter(event, BlockRegistry.POLISHED_FUNGAL_BRICK_SLAB, BlockRegistry.POLISHED_FUNGAL_BRICK_WALL);
                addAfter(event, BlockRegistry.POLISHED_FUNGAL_BRICK_WALL, BlockRegistry.POLISHED_FUNGAL_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.POLISHED_FUNGAL_BRICK_PEDESTAL, BlockRegistry.CHISELED_POLISHED_FUNGAL_BRICKS);

                addAfter(event, BlockRegistry.CHISELED_POLISHED_FUNGAL_BRICKS, BlockRegistry.DEEP_FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_BRICKS, BlockRegistry.CRACKED_DEEP_FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.CRACKED_DEEP_FUNGAL_BRICKS, BlockRegistry.DEEP_FUNGAL_BRICK_STAIRS);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_BRICK_STAIRS, BlockRegistry.DEEP_FUNGAL_BRICK_SLAB);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_BRICK_SLAB, BlockRegistry.DEEP_FUNGAL_BRICK_WALL);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_BRICK_WALL, BlockRegistry.DEEP_FUNGAL_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_BRICK_PEDESTAL, BlockRegistry.CHISELED_DEEP_FUNGAL_BRICKS);

                addAfter(event, BlockRegistry.CHISELED_DEEP_FUNGAL_BRICKS, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICKS, BlockRegistry.CRACKED_POLISHED_DEEP_FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.CRACKED_POLISHED_DEEP_FUNGAL_BRICKS, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_STAIRS);
                addAfter(event, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_STAIRS, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_SLAB);
                addAfter(event, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_SLAB, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_WALL);
                addAfter(event, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_WALL, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_PEDESTAL, BlockRegistry.CHISELED_POLISHED_DEEP_FUNGAL_BRICKS);

                addAfter(event, Blocks.NETHER_BRICK_FENCE, BlockRegistry.NETHER_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.NETHER_BRICK_PEDESTAL, BlockRegistry.SMASHABLE_NETHER_BRICKS);

                addAfter(event, Blocks.RED_NETHER_BRICK_WALL, BlockRegistry.RED_NETHER_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.RED_NETHER_BRICK_PEDESTAL, BlockRegistry.SMASHABLE_RED_NETHER_BRICKS);

                addAfter(event, Blocks.POLISHED_BLACKSTONE_BRICK_WALL, BlockRegistry.BLACKSTONE_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.BLACKSTONE_BRICK_PEDESTAL, BlockRegistry.SMASHABLE_BLACKSTONE_BRICKS);

                addAfter(event, Blocks.END_STONE_BRICK_WALL, BlockRegistry.END_STONE_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.END_STONE_BRICK_PEDESTAL, BlockRegistry.SMASHABLE_END_STONE_BRICKS);

                addAfter(event, Blocks.PURPUR_SLAB, BlockRegistry.PURPUR_BLOCK_PEDESTAL);
                addAfter(event, BlockRegistry.PURPUR_BLOCK_PEDESTAL, BlockRegistry.SMASHABLE_PURPUR_BLOCK);

                addAfter(event, Blocks.QUARTZ_BRICKS, BlockRegistry.SMASHABLE_QUARTZ_BRICKS);
                addAfter(event, BlockRegistry.SMASHABLE_QUARTZ_BRICKS, BlockRegistry.QUARTZ_BRICK_PEDESTAL);

                addAfter(event, Blocks.CUT_COPPER_SLAB, BlockRegistry.CUT_COPPER_PEDESTAL);
                addAfter(event, BlockRegistry.CUT_COPPER_PEDESTAL, BlockRegistry.SMASHABLE_CUT_COPPER);

                addAfter(event, Blocks.EXPOSED_CUT_COPPER_SLAB, BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL);
                addAfter(event, BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL, BlockRegistry.SMASHABLE_EXPOSED_CUT_COPPER);

                addAfter(event, Blocks.WEATHERED_CUT_COPPER_SLAB, BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL);
                addAfter(event, BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL, BlockRegistry.SMASHABLE_WEATHERED_CUT_COPPER);

                addAfter(event, Blocks.OXIDIZED_CUT_COPPER_SLAB, BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL);
                addAfter(event, BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL, BlockRegistry.SMASHABLE_OXIDIZED_CUT_COPPER);

                addAfter(event, Blocks.WAXED_CUT_COPPER_SLAB, BlockRegistry.WAXED_CUT_COPPER_PEDESTAL);
                addAfter(event, BlockRegistry.WAXED_CUT_COPPER_PEDESTAL, BlockRegistry.SMASHABLE_WAXED_CUT_COPPER);

                addAfter(event, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, BlockRegistry.WAXED_EXPOSED_CUT_COPPER_PEDESTAL);
                addAfter(event, BlockRegistry.WAXED_EXPOSED_CUT_COPPER_PEDESTAL, BlockRegistry.SMASHABLE_WAXED_EXPOSED_CUT_COPPER);

                addAfter(event, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, BlockRegistry.WAXED_WEATHERED_CUT_COPPER_PEDESTAL);
                addAfter(event, BlockRegistry.WAXED_WEATHERED_CUT_COPPER_PEDESTAL, BlockRegistry.SMASHABLE_WAXED_WEATHERED_CUT_COPPER);

                addAfter(event, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB, BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL);
                addAfter(event, BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL, BlockRegistry.SMASHABLE_WAXED_OXIDIZED_CUT_COPPER);
            }

            if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
                addAfter(event, Blocks.PEARLESCENT_FROGLIGHT, BlockRegistry.GLOW_BLOCK);

                addBefore(event, Items.LIGHTNING_ROD, BlockRegistry.MARIO_ABILITY_BLOCK);
                addAfter(event, BlockRegistry.MARIO_ABILITY_BLOCK, BlockRegistry.LUIGI_ABILITY_BLOCK);
                addAfter(event, BlockRegistry.LUIGI_ABILITY_BLOCK, BlockRegistry.WARIO_ABILITY_BLOCK);
                addAfter(event, BlockRegistry.WARIO_ABILITY_BLOCK, BlockRegistry.WALUIGI_ABILITY_BLOCK);
                addAfter(event, BlockRegistry.WALUIGI_ABILITY_BLOCK, BlockRegistry.PEACH_ABILITY_BLOCK);
                addAfter(event, BlockRegistry.PEACH_ABILITY_BLOCK, BlockRegistry.DAISY_ABILITY_BLOCK);
                addAfter(event, BlockRegistry.DAISY_ABILITY_BLOCK, BlockRegistry.ROSALINA_ABILITY_BLOCK);
                addAfter(event, BlockRegistry.ROSALINA_ABILITY_BLOCK, BlockRegistry.STEVE_ABILITY_BLOCK);
                addAfter(event, BlockRegistry.STEVE_ABILITY_BLOCK, BlockRegistry.FUNGAL_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.FUNGAL_QUESTION_BLOCK, BlockRegistry.DEEP_FUNGAL_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_QUESTION_BLOCK, BlockRegistry.AMETHYST_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.AMETHYST_QUESTION_BLOCK, BlockRegistry.CALCITE_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.CALCITE_QUESTION_BLOCK, BlockRegistry.STONE_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.STONE_QUESTION_BRICKS, BlockRegistry.MOSSY_STONE_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.MOSSY_STONE_QUESTION_BRICKS, BlockRegistry.DEEPSLATE_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.DEEPSLATE_QUESTION_BRICKS, BlockRegistry.DEEPSLATE_QUESTION_TILES);
                addAfter(event, BlockRegistry.DEEPSLATE_QUESTION_TILES, BlockRegistry.TUFF_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.TUFF_QUESTION_BRICKS, BlockRegistry.QUESTION_BRICKS);
                addAfter(event, BlockRegistry.QUESTION_BRICKS, BlockRegistry.MUD_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.MUD_QUESTION_BRICKS, BlockRegistry.SANDSTONE_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.SANDSTONE_QUESTION_BLOCK, BlockRegistry.RED_SANDSTONE_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.RED_SANDSTONE_QUESTION_BLOCK, BlockRegistry.PRISMARINE_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.PRISMARINE_QUESTION_BRICKS, BlockRegistry.NETHER_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.NETHER_QUESTION_BRICKS, BlockRegistry.DARK_PRISMARINE_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.DARK_PRISMARINE_QUESTION_BLOCK, BlockRegistry.RED_NETHER_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.RED_NETHER_QUESTION_BRICKS, BlockRegistry.BLACKSTONE_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.BLACKSTONE_QUESTION_BRICKS, BlockRegistry.QUARTZ_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.QUARTZ_QUESTION_BRICKS, BlockRegistry.END_STONE_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.END_STONE_QUESTION_BRICKS, BlockRegistry.PURPUR_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.PURPUR_QUESTION_BLOCK, BlockRegistry.COPPER_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.COPPER_QUESTION_BLOCK, BlockRegistry.EXPOSED_COPPER_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.EXPOSED_COPPER_QUESTION_BLOCK, BlockRegistry.WEATHERED_COPPER_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.WEATHERED_COPPER_QUESTION_BLOCK, BlockRegistry.OXIDIZED_COPPER_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.OXIDIZED_COPPER_QUESTION_BLOCK, BlockRegistry.WAXED_COPPER_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.WAXED_COPPER_QUESTION_BLOCK, BlockRegistry.WAXED_EXPOSED_COPPER_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.WAXED_EXPOSED_COPPER_QUESTION_BLOCK, BlockRegistry.WAXED_WEATHERED_COPPER_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.WAXED_WEATHERED_COPPER_QUESTION_BLOCK, BlockRegistry.WAXED_OXIDIZED_COPPER_QUESTION_BLOCK);

                addAfter(event, BlockRegistry.WAXED_OXIDIZED_COPPER_QUESTION_BLOCK, BlockRegistry.INVISIBLE_FUNGAL_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_FUNGAL_QUESTION_BLOCK, BlockRegistry.INVISIBLE_DEEP_FUNGAL_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_DEEP_FUNGAL_QUESTION_BLOCK, BlockRegistry.INVISIBLE_AMETHYST_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_AMETHYST_QUESTION_BLOCK, BlockRegistry.INVISIBLE_STONE_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.INVISIBLE_STONE_QUESTION_BRICKS, BlockRegistry.INVISIBLE_CALCITE_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_CALCITE_QUESTION_BLOCK, BlockRegistry.INVISIBLE_MOSSY_STONE_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.INVISIBLE_MOSSY_STONE_QUESTION_BRICKS, BlockRegistry.INVISIBLE_DEEPSLATE_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.INVISIBLE_DEEPSLATE_QUESTION_BRICKS, BlockRegistry.INVISIBLE_DEEPSLATE_QUESTION_TILES);
                addAfter(event, BlockRegistry.INVISIBLE_DEEPSLATE_QUESTION_TILES, BlockRegistry.INVISIBLE_TUFF_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.INVISIBLE_TUFF_QUESTION_BRICKS, BlockRegistry.INVISIBLE_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.INVISIBLE_QUESTION_BRICKS, BlockRegistry.INVISIBLE_MUD_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.INVISIBLE_MUD_QUESTION_BRICKS, BlockRegistry.INVISIBLE_SANDSTONE_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_SANDSTONE_QUESTION_BLOCK, BlockRegistry.INVISIBLE_RED_SANDSTONE_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_RED_SANDSTONE_QUESTION_BLOCK, BlockRegistry.INVISIBLE_PRISMARINE_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.INVISIBLE_PRISMARINE_QUESTION_BRICKS, BlockRegistry.INVISIBLE_DARK_PRISMARINE_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_DARK_PRISMARINE_QUESTION_BLOCK, BlockRegistry.INVISIBLE_NETHER_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.INVISIBLE_NETHER_QUESTION_BRICKS, BlockRegistry.INVISIBLE_RED_NETHER_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.INVISIBLE_RED_NETHER_QUESTION_BRICKS, BlockRegistry.INVISIBLE_BLACKSTONE_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.INVISIBLE_BLACKSTONE_QUESTION_BRICKS, BlockRegistry.INVISIBLE_QUARTZ_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.INVISIBLE_QUARTZ_QUESTION_BRICKS, BlockRegistry.INVISIBLE_END_STONE_QUESTION_BRICKS);
                addAfter(event, BlockRegistry.INVISIBLE_END_STONE_QUESTION_BRICKS, BlockRegistry.INVISIBLE_PURPUR_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_PURPUR_QUESTION_BLOCK, BlockRegistry.INVISIBLE_COPPER_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_COPPER_QUESTION_BLOCK, BlockRegistry.INVISIBLE_EXPOSED_COPPER_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_EXPOSED_COPPER_QUESTION_BLOCK, BlockRegistry.INVISIBLE_WEATHERED_COPPER_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_WEATHERED_COPPER_QUESTION_BLOCK, BlockRegistry.INVISIBLE_OXIDIZED_COPPER_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_OXIDIZED_COPPER_QUESTION_BLOCK, BlockRegistry.INVISIBLE_WAXED_COPPER_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_WAXED_COPPER_QUESTION_BLOCK, BlockRegistry.INVISIBLE_WAXED_EXPOSED_COPPER_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_WAXED_EXPOSED_COPPER_QUESTION_BLOCK, BlockRegistry.INVISIBLE_WAXED_WEATHERED_COPPER_QUESTION_BLOCK);
                addAfter(event, BlockRegistry.INVISIBLE_WAXED_WEATHERED_COPPER_QUESTION_BLOCK, BlockRegistry.INVISIBLE_WAXED_OXIDIZED_COPPER_QUESTION_BLOCK);

                addAfter(event, BlockRegistry.INVISIBLE_WAXED_OXIDIZED_COPPER_QUESTION_BLOCK, BlockRegistry.STORAGE_FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_FUNGAL_BRICKS, BlockRegistry.STORAGE_POLISHED_FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_POLISHED_FUNGAL_BRICKS, BlockRegistry.STORAGE_DEEP_FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_DEEP_FUNGAL_BRICKS, BlockRegistry.STORAGE_POLISHED_DEEP_FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_POLISHED_DEEP_FUNGAL_BRICKS, BlockRegistry.STORAGE_AMETHYST_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_AMETHYST_BRICKS, BlockRegistry.STORAGE_STONE_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_STONE_BRICKS, BlockRegistry.STORAGE_MOSSY_STONE_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_MOSSY_STONE_BRICKS, BlockRegistry.STORAGE_DEEPSLATE_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_DEEPSLATE_BRICKS, BlockRegistry.STORAGE_DEEPSLATE_TILES);
                addAfter(event, BlockRegistry.STORAGE_DEEPSLATE_TILES, BlockRegistry.STORAGE_TUFF_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_TUFF_BRICKS, BlockRegistry.STORAGE_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_BRICKS, BlockRegistry.STORAGE_MUD_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_MUD_BRICKS, BlockRegistry.STORAGE_SANDSTONE_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_SANDSTONE_BRICKS, BlockRegistry.STORAGE_RED_SANDSTONE_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_RED_SANDSTONE_BRICKS, BlockRegistry.STORAGE_PRISMARINE_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_PRISMARINE_BRICKS, BlockRegistry.STORAGE_DARK_PRISMARINE);
                addAfter(event, BlockRegistry.STORAGE_DARK_PRISMARINE, BlockRegistry.STORAGE_NETHER_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_NETHER_BRICKS, BlockRegistry.STORAGE_RED_NETHER_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_RED_NETHER_BRICKS, BlockRegistry.STORAGE_BLACKSTONE_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_BLACKSTONE_BRICKS, BlockRegistry.STORAGE_QUARTZ_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_QUARTZ_BRICKS, BlockRegistry.STORAGE_END_STONE_BRICKS);
                addAfter(event, BlockRegistry.STORAGE_END_STONE_BRICKS, BlockRegistry.STORAGE_PURPUR_BLOCK);
                addAfter(event, BlockRegistry.STORAGE_PURPUR_BLOCK, BlockRegistry.STORAGE_CUT_COPPER);
                addAfter(event, BlockRegistry.STORAGE_CUT_COPPER, BlockRegistry.STORAGE_EXPOSED_CUT_COPPER);
                addAfter(event, BlockRegistry.STORAGE_EXPOSED_CUT_COPPER, BlockRegistry.STORAGE_WEATHERED_CUT_COPPER);
                addAfter(event, BlockRegistry.STORAGE_WEATHERED_CUT_COPPER, BlockRegistry.STORAGE_OXIDIZED_CUT_COPPER);
                addAfter(event, BlockRegistry.STORAGE_OXIDIZED_CUT_COPPER, BlockRegistry.STORAGE_WAXED_CUT_COPPER);
                addAfter(event, BlockRegistry.STORAGE_WAXED_CUT_COPPER, BlockRegistry.STORAGE_WAXED_EXPOSED_CUT_COPPER);
                addAfter(event, BlockRegistry.STORAGE_WAXED_EXPOSED_CUT_COPPER, BlockRegistry.STORAGE_WAXED_WEATHERED_CUT_COPPER);
                addAfter(event, BlockRegistry.STORAGE_WAXED_WEATHERED_CUT_COPPER, BlockRegistry.STORAGE_WAXED_OXIDIZED_CUT_COPPER);
                addDyedBlocks(event, BlockRegistry.STORAGE_WAXED_OXIDIZED_CUT_COPPER, BlockRegistry.STORAGE_CALCITE_BRICKS, false, false);

                addAfter(event, BlockRegistry.STORAGE_WAXED_OXIDIZED_CUT_COPPER, BlockRegistry.FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.FUNGAL_BRICKS, BlockRegistry.POLISHED_FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.POLISHED_FUNGAL_BRICKS, BlockRegistry.DEEP_FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_BRICKS, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICKS);
                addAfter(event, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICKS, BlockRegistry.AMETHYST_BRICKS);
                addAfter(event, BlockRegistry.AMETHYST_BRICKS, BlockRegistry.SMASHABLE_STONE_BRICKS);
                addAfter(event, BlockRegistry.SMASHABLE_STONE_BRICKS, BlockRegistry.SMASHABLE_MOSSY_STONE_BRICKS);
                addAfter(event, BlockRegistry.SMASHABLE_MOSSY_STONE_BRICKS, BlockRegistry.SMASHABLE_DEEPSLATE_BRICKS);
                addAfter(event, BlockRegistry.SMASHABLE_DEEPSLATE_BRICKS, BlockRegistry.SMASHABLE_DEEPSLATE_TILES);
                addAfter(event, BlockRegistry.SMASHABLE_DEEPSLATE_TILES, BlockRegistry.SMASHABLE_TUFF_BRICKS);
                addAfter(event, BlockRegistry.SMASHABLE_TUFF_BRICKS, BlockRegistry.SMASHABLE_BRICKS);
                addAfter(event, BlockRegistry.SMASHABLE_BRICKS, BlockRegistry.SMASHABLE_MUD_BRICKS);
                addAfter(event, BlockRegistry.SMASHABLE_MUD_BRICKS, BlockRegistry.CRACKED_SANDSTONE_BRICKS);
                addAfter(event, BlockRegistry.CRACKED_SANDSTONE_BRICKS, BlockRegistry.CRACKED_RED_SANDSTONE_BRICKS);
                addAfter(event, BlockRegistry.CRACKED_RED_SANDSTONE_BRICKS, BlockRegistry.SMASHABLE_PRISMARINE_BRICKS);
                addAfter(event, BlockRegistry.SMASHABLE_PRISMARINE_BRICKS, BlockRegistry.SMASHABLE_DARK_PRISMARINE);
                addAfter(event, BlockRegistry.SMASHABLE_DARK_PRISMARINE, BlockRegistry.SMASHABLE_NETHER_BRICKS);
                addAfter(event, BlockRegistry.SMASHABLE_NETHER_BRICKS, BlockRegistry.SMASHABLE_RED_NETHER_BRICKS);
                addAfter(event, BlockRegistry.SMASHABLE_RED_NETHER_BRICKS, BlockRegistry.SMASHABLE_BLACKSTONE_BRICKS);
                addAfter(event, BlockRegistry.SMASHABLE_BLACKSTONE_BRICKS, BlockRegistry.SMASHABLE_QUARTZ_BRICKS);
                addAfter(event, BlockRegistry.SMASHABLE_QUARTZ_BRICKS, BlockRegistry.SMASHABLE_END_STONE_BRICKS);
                addAfter(event, BlockRegistry.SMASHABLE_END_STONE_BRICKS, BlockRegistry.SMASHABLE_PURPUR_BLOCK);
                addAfter(event, BlockRegistry.SMASHABLE_PURPUR_BLOCK, BlockRegistry.SMASHABLE_CUT_COPPER);
                addAfter(event, BlockRegistry.SMASHABLE_CUT_COPPER, BlockRegistry.SMASHABLE_EXPOSED_CUT_COPPER);
                addAfter(event, BlockRegistry.SMASHABLE_EXPOSED_CUT_COPPER, BlockRegistry.SMASHABLE_WEATHERED_CUT_COPPER);
                addAfter(event, BlockRegistry.SMASHABLE_WEATHERED_CUT_COPPER, BlockRegistry.SMASHABLE_OXIDIZED_CUT_COPPER);
                addAfter(event, BlockRegistry.SMASHABLE_OXIDIZED_CUT_COPPER, BlockRegistry.SMASHABLE_WAXED_CUT_COPPER);
                addAfter(event, BlockRegistry.SMASHABLE_WAXED_CUT_COPPER, BlockRegistry.SMASHABLE_WAXED_EXPOSED_CUT_COPPER);
                addAfter(event, BlockRegistry.SMASHABLE_WAXED_EXPOSED_CUT_COPPER, BlockRegistry.SMASHABLE_WAXED_WEATHERED_CUT_COPPER);
                addAfter(event, BlockRegistry.SMASHABLE_WAXED_WEATHERED_CUT_COPPER, BlockRegistry.SMASHABLE_WAXED_OXIDIZED_CUT_COPPER);

                addAfter(event, BlockRegistry.SMASHABLE_WAXED_OXIDIZED_CUT_COPPER, BlockRegistry.FUNGAL_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.FUNGAL_BRICK_PEDESTAL, BlockRegistry.POLISHED_FUNGAL_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.POLISHED_FUNGAL_BRICK_PEDESTAL, BlockRegistry.DEEP_FUNGAL_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.DEEP_FUNGAL_BRICK_PEDESTAL, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_PEDESTAL, BlockRegistry.AMETHYST_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.AMETHYST_BRICK_PEDESTAL, BlockRegistry.STONE_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.STONE_BRICK_PEDESTAL, BlockRegistry.MOSSY_STONE_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.MOSSY_STONE_BRICK_PEDESTAL, BlockRegistry.DEEPSLATE_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.DEEPSLATE_BRICK_PEDESTAL, BlockRegistry.DEEPSLATE_TILE_PEDESTAL);
                addAfter(event, BlockRegistry.DEEPSLATE_TILE_PEDESTAL, BlockRegistry.TUFF_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.TUFF_BRICK_PEDESTAL, BlockRegistry.BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.BRICK_PEDESTAL, BlockRegistry.MUD_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.MUD_BRICK_PEDESTAL, BlockRegistry.PRISMARINE_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.PRISMARINE_BRICK_PEDESTAL, BlockRegistry.DARK_PRISMARINE_PEDESTAL);
                addAfter(event, BlockRegistry.DARK_PRISMARINE_PEDESTAL, BlockRegistry.NETHER_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.NETHER_BRICK_PEDESTAL, BlockRegistry.RED_NETHER_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.RED_NETHER_BRICK_PEDESTAL, BlockRegistry.BLACKSTONE_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.BLACKSTONE_BRICK_PEDESTAL, BlockRegistry.QUARTZ_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.QUARTZ_BRICK_PEDESTAL, BlockRegistry.END_STONE_BRICK_PEDESTAL);
                addAfter(event, BlockRegistry.END_STONE_BRICK_PEDESTAL, BlockRegistry.PURPUR_BLOCK_PEDESTAL);
                addAfter(event, BlockRegistry.PURPUR_BLOCK_PEDESTAL, BlockRegistry.CUT_COPPER_PEDESTAL);
                addAfter(event, BlockRegistry.CUT_COPPER_PEDESTAL, BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL);
                addAfter(event, BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL, BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL);
                addAfter(event, BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL, BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL);
                addAfter(event, BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL, BlockRegistry.WAXED_EXPOSED_CUT_COPPER_PEDESTAL);
                addAfter(event, BlockRegistry.WAXED_EXPOSED_CUT_COPPER_PEDESTAL, BlockRegistry.WAXED_WEATHERED_CUT_COPPER_PEDESTAL);
                addAfter(event, BlockRegistry.WAXED_WEATHERED_CUT_COPPER_PEDESTAL, BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL);
                addDyedBlocks(event, BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL, BlockRegistry.CALCITE_BRICK_PEDESTAL, false, false);

                addAfter(event, Items.OAK_HANGING_SIGN, ItemRegistry.OAK_ARROW_SIGN);
                addAfter(event, ItemRegistry.OAK_ARROW_SIGN, ItemRegistry.LARGE_OAK_ARROW_SIGN);

                addAfter(event, Items.SPRUCE_HANGING_SIGN, ItemRegistry.SPRUCE_ARROW_SIGN);
                addAfter(event, ItemRegistry.SPRUCE_ARROW_SIGN, ItemRegistry.LARGE_SPRUCE_ARROW_SIGN);

                addAfter(event, Items.BIRCH_HANGING_SIGN, ItemRegistry.BIRCH_ARROW_SIGN);
                addAfter(event, ItemRegistry.BIRCH_ARROW_SIGN, ItemRegistry.LARGE_BIRCH_ARROW_SIGN);

                addAfter(event, Items.JUNGLE_HANGING_SIGN, ItemRegistry.JUNGLE_ARROW_SIGN);
                addAfter(event, ItemRegistry.JUNGLE_ARROW_SIGN, ItemRegistry.LARGE_JUNGLE_ARROW_SIGN);

                addAfter(event, Items.ACACIA_HANGING_SIGN, ItemRegistry.ACACIA_ARROW_SIGN);
                addAfter(event, ItemRegistry.ACACIA_ARROW_SIGN, ItemRegistry.LARGE_ACACIA_ARROW_SIGN);

                addAfter(event, Items.DARK_OAK_HANGING_SIGN, ItemRegistry.DARK_OAK_ARROW_SIGN);
                addAfter(event, ItemRegistry.DARK_OAK_ARROW_SIGN, ItemRegistry.LARGE_DARK_OAK_ARROW_SIGN);

                addAfter(event, Items.MANGROVE_HANGING_SIGN, ItemRegistry.MANGROVE_ARROW_SIGN);
                addAfter(event, ItemRegistry.MANGROVE_ARROW_SIGN, ItemRegistry.LARGE_MANGROVE_ARROW_SIGN);

                addAfter(event, Items.CHERRY_HANGING_SIGN, ItemRegistry.CHERRY_ARROW_SIGN);
                addAfter(event, ItemRegistry.CHERRY_ARROW_SIGN, ItemRegistry.LARGE_CHERRY_ARROW_SIGN);

                addAfter(event, Items.BAMBOO_HANGING_SIGN, ItemRegistry.BAMBOO_ARROW_SIGN);
                addAfter(event, ItemRegistry.BAMBOO_ARROW_SIGN, ItemRegistry.LARGE_BAMBOO_ARROW_SIGN);

                addAfter(event, ItemRegistry.LARGE_BAMBOO_ARROW_SIGN, ItemRegistry.MUSHROOT_SIGN);
                addAfter(event, ItemRegistry.MUSHROOT_SIGN, ItemRegistry.MUSHROOT_HANGING_SIGN);
                addAfter(event, ItemRegistry.MUSHROOT_HANGING_SIGN, ItemRegistry.MUSHROOT_ARROW_SIGN);
                addAfter(event, ItemRegistry.MUSHROOT_ARROW_SIGN, ItemRegistry.LARGE_MUSHROOT_ARROW_SIGN);

                addAfter(event, Items.CRIMSON_HANGING_SIGN, ItemRegistry.CRIMSON_ARROW_SIGN);
                addAfter(event, ItemRegistry.CRIMSON_ARROW_SIGN, ItemRegistry.LARGE_CRIMSON_ARROW_SIGN);

                addAfter(event, Items.WARPED_HANGING_SIGN, ItemRegistry.WARPED_ARROW_SIGN);
                addAfter(event, ItemRegistry.WARPED_ARROW_SIGN, ItemRegistry.LARGE_WARPED_ARROW_SIGN);

                addAfter(event, BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL, BlockRegistry.IRON_SPIKE);

                addAfter(event, Items.RESPAWN_ANCHOR, BlockRegistry.CLEAR_WARP_PIPE);
                addDyedBlocks(event, BlockRegistry.CLEAR_WARP_PIPE, BlockRegistry.WARP_PIPES, true, true);

                addBefore(event, Blocks.SKELETON_SKULL, BlockRegistry.CLASSIC_GOAL_POLE);
                addBefore(event, BlockRegistry.CLASSIC_GOAL_POLE, BlockRegistry.CLASSIC_CHECKPOINT_FLAG);
                addDyedBlocks(event, BlockRegistry.CLASSIC_CHECKPOINT_FLAG, BlockRegistry.CHECKPOINT_FLAGS, false, false);
                addDyedBlocks(event, BlockRegistry.CLASSIC_GOAL_POLE, BlockRegistry.GOAL_POLES, false, false);
            }
        }
    }

    private static boolean alreadyExists(BuildCreativeModeTabContentsEvent event, ItemStack stack) {
        return event.getParentEntries().contains(stack) || event.getSearchEntries().contains(stack);
    }

    public static void add(BuildCreativeModeTabContentsEvent event, ItemLike item) {
        ItemStack stack = new ItemStack(item);
        add(event, stack);
    }

    public static ItemStack addStack(BuildCreativeModeTabContentsEvent event, ItemLike item) {
        ItemStack stack = new ItemStack(item);
        add(event, stack);
        return stack;
    }

    public static void add(BuildCreativeModeTabContentsEvent event, ItemLike item, Component name) {
        ItemStack stack = new ItemStack(item);
        stack.set(DataComponents.CUSTOM_NAME, name);
        add(event, stack);
    }

    public static <T> ItemStack add(BuildCreativeModeTabContentsEvent event, ItemLike item,
                                     DataComponentType<T> componentType, T value) {
        ItemStack stack = new ItemStack(item);
        stack.set(componentType, value);
        add(event, stack);
        return stack;
    }

    public static void addBucket(BuildCreativeModeTabContentsEvent event, ItemLike item, Consumer<CompoundTag> tagConsumer) {
        ItemStack stack = new ItemStack(item);
        CompoundTag tag = new CompoundTag();
        tagConsumer.accept(tag);
        stack.set(DataComponents.BUCKET_ENTITY_DATA, CustomData.of(tag));
        add(event, stack);
    }

    public static void add(BuildCreativeModeTabContentsEvent event, ItemStack stack) {
        if (stack.isEmpty()) {
            System.out.println("Warning, attempting to register an empty stack to tab!");
            return;
        }

        if (!alreadyExists(event, stack))
         event.accept(stack);
    }

    public static void addAfter(BuildCreativeModeTabContentsEvent event, ItemLike afterItem, ItemLike item) {
        addAfter(event, new ItemStack(afterItem), new ItemStack(item));
    }

    public static void addAfter(BuildCreativeModeTabContentsEvent event, ItemLike afterItem, ItemStack stack) {
        addAfter(event, new ItemStack(afterItem), stack);
    }

    public static void addAfter(BuildCreativeModeTabContentsEvent event, ItemStack afterStack, ItemLike item) {
        addAfter(event, afterStack, new ItemStack(item));
    }

    public static void addAfter(BuildCreativeModeTabContentsEvent event, ItemStack afterStack, ItemStack stack) {
        if (alreadyExists(event, stack))
            return;

        if (alreadyExists(event, afterStack))
            event.insertAfter(afterStack, stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        else add(event, stack);
    }

    public static <T> ItemStack addAfter(BuildCreativeModeTabContentsEvent event, ItemStack afterStack, ItemLike item,
                                         DataComponentType<T> componentType, T value) {
        ItemStack stack = new ItemStack(item);
        stack.set(componentType, value);
        addAfter(event, afterStack, stack);
        return stack;
    }

    public static void addBefore(BuildCreativeModeTabContentsEvent event, ItemLike beforeItem, ItemLike item) {
        addBefore(event, new ItemStack(beforeItem), new ItemStack(item));
    }

    public static void addBefore(BuildCreativeModeTabContentsEvent event, ItemStack beforeStack, ItemStack stack) {
        if (alreadyExists(event, stack))
            return;

        if (alreadyExists(event, beforeStack))
            event.insertBefore(beforeStack, stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        else add(event, stack);
    }

    private static void addDyedBlocks(BuildCreativeModeTabContentsEvent event, ItemLike existingItem,
                                      EnumMap<DyeColor, DeferredBlock<Block>> dyedBlock, boolean isReversed, boolean addAfter) {
        List<DyeColor> rainbowOrder = Arrays.asList(DyeColor.WHITE, DyeColor.LIGHT_GRAY, DyeColor.GRAY, DyeColor.BLACK,
                DyeColor.BROWN, DyeColor.RED, DyeColor.ORANGE, DyeColor.YELLOW, DyeColor.LIME, DyeColor.GREEN, DyeColor.CYAN,
                DyeColor.LIGHT_BLUE, DyeColor.BLUE, DyeColor.PURPLE, DyeColor.MAGENTA, DyeColor.PINK);
        List<DeferredHolder<Block, Block>> dyedBlocks = new ArrayList<>();
        Set<DyeColor> processedColors = new HashSet<>();

        if (isReversed)
            Collections.reverse(rainbowOrder);

        for (DyeColor color : rainbowOrder) {
            DeferredBlock<Block> coloredBlock = dyedBlock.get(color);
            if (coloredBlock != null) {
                dyedBlocks.add(coloredBlock);
                processedColors.add(color);
            }
        }

        // Track blocks not in the rainbow order
        Set<Block> additionalBlocks = new HashSet<>();
        for (Map.Entry<DyeColor, DeferredBlock<Block>> entry : dyedBlock.entrySet()) {
            DyeColor color = entry.getKey();
            if (!processedColors.contains(color))
                additionalBlocks.add(entry.getValue().get());
        }

        Set<Block> listedBlocks = new HashSet<>();

        // Adds all dyed blocks
        Block lastRainbowBlock = null;
        for (DeferredHolder<Block, Block> block : dyedBlocks) {
            Block coloredBlock = block.get();
            if (!listedBlocks.contains(coloredBlock)) {
                if (addAfter)
                    addAfter(event, existingItem, coloredBlock);
                else addBefore(event, existingItem, coloredBlock);

                listedBlocks.add(coloredBlock);
                lastRainbowBlock = dyedBlock.get(DyeColor.PINK).get();
            }
        }

        // Adds any additional blocks that were not in the dyed blocks
        for (Block additionalBlock : additionalBlocks) {
            if (!listedBlocks.contains(additionalBlock)) {
                if (lastRainbowBlock != null && addAfter)
                    addAfter(event, lastRainbowBlock, additionalBlock);
                else addBefore(event, existingItem, additionalBlock);
                listedBlocks.add(additionalBlock);
            }
        }
    }

    public static ItemStack bucketVariant(ItemLike item, String variant) {
        ItemStack stack = new ItemStack(item);
        CompoundTag tag = new CompoundTag();

        tag.putString("Variant", variant);
        stack.set(DataComponents.BUCKET_ENTITY_DATA, CustomData.of(tag));
        return stack;
    }

    public static ItemStack variant(ItemLike item, String variant) {
        ItemStack stack = new ItemStack(item);

        stack.set(DataComponentRegistry.VARIANT, variant);
        return stack;
    }

    public static ItemStack customName(ItemLike item, Component name) {
        ItemStack stack = new ItemStack(item);

        stack.set(DataComponents.CUSTOM_NAME, name);
        return stack;
    }
}
