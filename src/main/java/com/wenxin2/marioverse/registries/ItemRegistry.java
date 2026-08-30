package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.items.ArrowSignItem;
import com.wenxin2.marioverse.items.LargeArrowSignItem;
import com.wenxin2.marioverse.items.FireFlowerItem;
import com.wenxin2.marioverse.items.IceFlowerItem;
import com.wenxin2.marioverse.items.MegaMushroomItem;
import com.wenxin2.marioverse.items.MiniMushroomItem;
import com.wenxin2.marioverse.items.OneUpMushroomItem;
import com.wenxin2.marioverse.items.PowerUpSpawnEggItem;
import com.wenxin2.marioverse.items.BetterSpawnEggItem;
import com.wenxin2.marioverse.items.CheckpointFlagBlockItem;
import com.wenxin2.marioverse.items.ChristmasHatItem;
import com.wenxin2.marioverse.items.CreativeWrenchItem;
import com.wenxin2.marioverse.items.DashMushroomItem;
import com.wenxin2.marioverse.items.FemaleCostumeItem;
import com.wenxin2.marioverse.items.FluidPlasticBucketItem;
import com.wenxin2.marioverse.items.KoopaShellItem;
import com.wenxin2.marioverse.items.KoopaShoesItem;
import com.wenxin2.marioverse.items.LargeSnowballItem;
import com.wenxin2.marioverse.items.MaleCostumeItem;
import com.wenxin2.marioverse.items.OneUpMushroomSpawnEggItem;
import com.wenxin2.marioverse.items.PiranhaPlantPodItem;
import com.wenxin2.marioverse.items.PlasticBucketItem;
import com.wenxin2.marioverse.items.PokeySpawnEggItem;
import com.wenxin2.marioverse.items.SnowPokeySpawnEggItem;
import com.wenxin2.marioverse.items.SolidPlasticBucketItem;
import com.wenxin2.marioverse.items.StarCoinBlockItem;
import com.wenxin2.marioverse.items.SuperMushroomItem;
import com.wenxin2.marioverse.items.SuperStarItem;
import com.wenxin2.marioverse.items.WarpDisruptorItem;
import com.wenxin2.marioverse.items.WoodTypeBoatItem;
import com.wenxin2.marioverse.items.WrenchItem;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredItem;

public class ItemRegistry {
    public static final EnumMap<DyeColor, DeferredItem<Item>> CHECKPOINT_FLAGS =
            new EnumMap<>(DyeColor.class);

    public static final DeferredItem<Item> BODICE;
    public static final DeferredItem<Item> BOO_SPAWN_EGG;
    public static final DeferredItem<Item> BOWSER_BANNER_PATTERN;
    public static final DeferredItem<Item> BOWSER_POTTERY_SHERD;
    public static final DeferredItem<Item> CHEEP_CHEEP;
    public static final DeferredItem<Item> CHEEP_CHEEP_BUCKET;
    public static final DeferredItem<Item> CHEEP_CHEEP_SPAWN_EGG;
    public static final DeferredItem<Item> CHRISTMAS_HAT;
    public static final DeferredItem<Item> CLASSIC_CHECKPOINT_FLAG;
    public static final DeferredItem<Item> COLD_CHEEP_CHEEP;
    public static final DeferredItem<Item> COOKED_CHEEP_CHEEP;
    public static final DeferredItem<Item> COOKED_PORCUPUFFER;
    public static final DeferredItem<Item> COOKED_SPINY_CHEEP_CHEEP;
    public static final DeferredItem<Item> CREATIVE_WRENCH;
    public static final DeferredItem<Item> CROWN;
    public static final DeferredItem<Item> DASH_MUSHROOM;
    public static final DeferredItem<Item> DASH_MUSHROOM_SPAWN_EGG;
    public static final DeferredItem<Item> DEEP_CHEEP;
    public static final DeferredItem<Item> DEEP_CHEEP_BUCKET;
    public static final DeferredItem<Item> DEEP_CHEEP_SPAWN_EGG;
    public static final DeferredItem<Item> DRESS;
    public static final DeferredItem<Item> DRY_BONES_SPAWN_EGG;
    public static final DeferredItem<Item> EEP_CHEEP;
    public static final DeferredItem<Item> EEP_CHEEP_BUCKET;
    public static final DeferredItem<Item> EEP_CHEEP_SPAWN_EGG;
    public static final DeferredItem<Item> FIRE_FLOWER;
    public static final DeferredItem<Item> FIRE_FLOWER_SPAWN_EGG;
    public static final DeferredItem<Item> FIRE_GOOMBA_SPAWN_EGG;
    public static final DeferredItem<Item> GOLDEN_KOOPA_SHOES;
    public static final DeferredItem<Item> GOLD_KOOPA_SHELL;
    public static final DeferredItem<Item> GOLD_KOOPA_TROOPA_SPAWN_EGG;
    public static final DeferredItem<Item> GOOMBA_SPAWN_EGG;
    public static final DeferredItem<Item> GREEN_KOOPA_SHELL;
    public static final DeferredItem<Item> GREEN_KOOPA_SHOES;
    public static final DeferredItem<Item> GREEN_KOOPA_TROOPA_SPAWN_EGG;
    public static final DeferredItem<Item> HAT;
    public static final DeferredItem<Item> HEELS;
    public static final DeferredItem<Item> HEFTY_GOOMBA_SPAWN_EGG;
    public static final DeferredItem<Item> ICE_FLOWER;
    public static final DeferredItem<Item> ICE_FLOWER_SPAWN_EGG;
    public static final DeferredItem<Item> LARGE_MUSHROOT_ARROW_SIGN;
    public static final DeferredItem<Item> LARGE_OAK_ARROW_SIGN;
    public static final DeferredItem<Item> LARGE_SPRUCE_ARROW_SIGN;
    public static final DeferredItem<Item> LARGE_BIRCH_ARROW_SIGN;
    public static final DeferredItem<Item> LARGE_JUNGLE_ARROW_SIGN;
    public static final DeferredItem<Item> LARGE_ACACIA_ARROW_SIGN;
    public static final DeferredItem<Item> LARGE_DARK_OAK_ARROW_SIGN;
    public static final DeferredItem<Item> LARGE_MANGROVE_ARROW_SIGN;
    public static final DeferredItem<Item> LARGE_CHERRY_ARROW_SIGN;
    public static final DeferredItem<Item> LARGE_BAMBOO_ARROW_SIGN;
    public static final DeferredItem<Item> LARGE_CRIMSON_ARROW_SIGN;
    public static final DeferredItem<Item> LARGE_WARPED_ARROW_SIGN;
    public static final DeferredItem<Item> LARGE_SNOWBALL;
    public static final DeferredItem<Item> LUIGI_ARMOR_TRIM_SMITHING_TEMPLATE;
    public static final DeferredItem<Item> MARIO_ARMOR_TRIM_SMITHING_TEMPLATE;
    public static final DeferredItem<Item> MEGA_GOOMBA_SPAWN_EGG;
    public static final DeferredItem<Item> MEGA_MUSHROOM;
    public static final DeferredItem<Item> MEGA_MUSHROOM_SPAWN_EGG;
    public static final DeferredItem<Item> MINI_GOOMBA_SPAWN_EGG;
    public static final DeferredItem<Item> MINI_MUSHROOM;
    public static final DeferredItem<Item> MINI_MUSHROOM_SPAWN_EGG;
    public static final DeferredItem<Item> MUSHROOT_ARROW_SIGN;
    public static final DeferredItem<Item> OAK_ARROW_SIGN;
    public static final DeferredItem<Item> SPRUCE_ARROW_SIGN;
    public static final DeferredItem<Item> BIRCH_ARROW_SIGN;
    public static final DeferredItem<Item> JUNGLE_ARROW_SIGN;
    public static final DeferredItem<Item> ACACIA_ARROW_SIGN;
    public static final DeferredItem<Item> DARK_OAK_ARROW_SIGN;
    public static final DeferredItem<Item> MANGROVE_ARROW_SIGN;
    public static final DeferredItem<Item> CHERRY_ARROW_SIGN;
    public static final DeferredItem<Item> BAMBOO_ARROW_SIGN;
    public static final DeferredItem<Item> CRIMSON_ARROW_SIGN;
    public static final DeferredItem<Item> WARPED_ARROW_SIGN;
    public static final DeferredItem<Item> MUSHROOT_BOAT;
    public static final DeferredItem<Item> MUSHROOT_CHEST_BOAT;
    public static final DeferredItem<Item> MUSHROOT_HANGING_SIGN;
    public static final DeferredItem<Item> MUSHROOT_SIGN;
    public static final DeferredItem<Item> ONE_UP_MUSHROOM;
    public static final DeferredItem<Item> ONE_UP_MUSHROOM_SPAWN_EGG;
    public static final DeferredItem<Item> PANTS;
    public static final DeferredItem<Item> PIRANHA_PLANT_POD;
    public static final DeferredItem<Item> PIRANHA_PLANT_SPAWN_EGG;
    public static final DeferredItem<Item> PLASTIC_BUCKET;
    public static final DeferredItem<Item> PLASTIC_POWDER_SNOW_BUCKET;
    public static final DeferredItem<Item> PLASTIC_QUICKSAND_BUCKET;
    public static final DeferredItem<Item> PLASTIC_RED_QUICKSAND_BUCKET;
    public static final DeferredItem<Item> PLASTIC_WATER_BUCKET;
    public static final DeferredItem<Item> PLUMBER_BANNER_PATTERN;
    public static final DeferredItem<Item> PLUMBER_POTTERY_SHERD;
    public static final DeferredItem<Item> POKEY_SPAWN_EGG;
    public static final DeferredItem<Item> PORCUPUFFER;
    public static final DeferredItem<Item> PORCUPUFFER_SPAWN_EGG;
    public static final DeferredItem<Item> PRINCESS_ARMOR_TRIM_SMITHING_TEMPLATE;
    public static final DeferredItem<Item> QUICKSAND_BUCKET;
    public static final DeferredItem<Item> RED_KOOPA_SHELL;
    public static final DeferredItem<Item> RED_KOOPA_SHOES;
    public static final DeferredItem<Item> RED_KOOPA_TROOPA_SPAWN_EGG;
    public static final DeferredItem<Item> RED_QUICKSAND_BUCKET;
    public static final DeferredItem<Item> SHIRT;
    public static final DeferredItem<Item> SHOES;
    public static final DeferredItem<Item> SNOW_POKEY_SPAWN_EGG;
    public static final DeferredItem<Item> SPINY_CHEEP_CHEEP;
    public static final DeferredItem<Item> SPINY_CHEEP_CHEEP_BUCKET;
    public static final DeferredItem<Item> SPINY_CHEEP_CHEEP_SPAWN_EGG;
    public static final DeferredItem<Item> SPLUNKIN_SPAWN_EGG;
    public static final DeferredItem<Item> STAR_COIN;
    public static final DeferredItem<Item> SUPER_MUSHROOM;
    public static final DeferredItem<Item> SUPER_MUSHROOM_SPAWN_EGG;
    public static final DeferredItem<Item> SUPER_STAR;
    public static final DeferredItem<Item> SUPER_STAR_SPAWN_EGG;
    public static final DeferredItem<Item> WALUIGI_ARMOR_TRIM_SMITHING_TEMPLATE;
    public static final DeferredItem<Item> WARIO_ARMOR_TRIM_SMITHING_TEMPLATE;
    public static final DeferredItem<Item> WARM_CHEEP_CHEEP;
    public static final DeferredItem<Item> WARP_DISRUPTOR;
    public static final DeferredItem<Item> WHITE_KOOPA_SHOES;
    public static final DeferredItem<Item> WRENCH;

    static {
        STAR_COIN = registerItem("star_coin", () -> new StarCoinBlockItem(BlockRegistry.STAR_COIN.get(), new Item.Properties()));
        CLASSIC_CHECKPOINT_FLAG = registerItem("classic_checkpoint_flag",
                () -> new CheckpointFlagBlockItem(BlockRegistry.CLASSIC_CHECKPOINT_FLAG.get(), new Item.Properties()));

        MUSHROOT_BOAT = Marioverse.ITEMS.register("mushroot_boat",
                () -> new WoodTypeBoatItem(false, EntityRegistry.MUSHROOT_BOAT.get(),
                        new Item.Properties().stacksTo(1)));
        MUSHROOT_CHEST_BOAT = Marioverse.ITEMS.register("mushroot_chest_boat",
                () -> new WoodTypeBoatItem(true, EntityRegistry.MUSHROOT_CHEST_BOAT.get(),
                        new Item.Properties().stacksTo(1)));

        MUSHROOT_HANGING_SIGN = registerItem("mushroot_hanging_sign",
                () -> new HangingSignItem(BlockRegistry.MUSHROOT_HANGING_SIGN.get(),
                        BlockRegistry.MUSHROOT_WALL_HANGING_SIGN.get(),
                        new Item.Properties().stacksTo(16)));

        MUSHROOT_SIGN = registerItem("mushroot_sign",
                () -> new SignItem(new Item.Properties().stacksTo(16),
                        BlockRegistry.MUSHROOT_SIGN.get(), BlockRegistry.MUSHROOT_WALL_SIGN.get()));

        MUSHROOT_ARROW_SIGN = registerItem("mushroot_arrow_sign",
                () -> new ArrowSignItem(new Item.Properties().stacksTo(16),
                        BlockRegistry.MUSHROOT_ARROW_SIGN.get(), BlockRegistry.MUSHROOT_WALL_ARROW_SIGN.get(),
                        BlockRegistry.MUSHROOT_HANGING_ARROW_SIGN.get()));

        LARGE_MUSHROOT_ARROW_SIGN = registerItem("large_mushroot_arrow_sign",
                () -> new LargeArrowSignItem(new Item.Properties().stacksTo(16),
                        BlockRegistry.LARGE_MUSHROOT_ARROW_SIGN.get(), BlockRegistry.LARGE_MUSHROOT_WALL_ARROW_SIGN.get()));

        OAK_ARROW_SIGN = registerItem("oak_arrow_sign",
                () -> new ArrowSignItem(new Item.Properties().stacksTo(16),
                        BlockRegistry.OAK_ARROW_SIGN.get(), BlockRegistry.OAK_WALL_ARROW_SIGN.get(),
                        BlockRegistry.OAK_HANGING_ARROW_SIGN.get()));
        LARGE_OAK_ARROW_SIGN = registerItem("large_oak_arrow_sign",
                () -> new LargeArrowSignItem(new Item.Properties().stacksTo(16),
                        BlockRegistry.LARGE_OAK_ARROW_SIGN.get(), BlockRegistry.LARGE_OAK_WALL_ARROW_SIGN.get()));

        SPRUCE_ARROW_SIGN = registerItem("spruce_arrow_sign",
                () -> new ArrowSignItem(new Item.Properties().stacksTo(16),
                        BlockRegistry.SPRUCE_ARROW_SIGN.get(), BlockRegistry.SPRUCE_WALL_ARROW_SIGN.get(),
                        BlockRegistry.SPRUCE_HANGING_ARROW_SIGN.get()));
        LARGE_SPRUCE_ARROW_SIGN = registerItem("large_spruce_arrow_sign",
                () -> new LargeArrowSignItem(new Item.Properties().stacksTo(16),
                        BlockRegistry.LARGE_SPRUCE_ARROW_SIGN.get(), BlockRegistry.LARGE_SPRUCE_WALL_ARROW_SIGN.get()));

        BIRCH_ARROW_SIGN = registerItem("birch_arrow_sign",
                () -> new ArrowSignItem(new Item.Properties().stacksTo(16),
                        BlockRegistry.BIRCH_ARROW_SIGN.get(), BlockRegistry.BIRCH_WALL_ARROW_SIGN.get(),
                        BlockRegistry.BIRCH_HANGING_ARROW_SIGN.get()));
        LARGE_BIRCH_ARROW_SIGN = registerItem("large_birch_arrow_sign",
                () -> new LargeArrowSignItem(new Item.Properties().stacksTo(16),
                        BlockRegistry.LARGE_BIRCH_ARROW_SIGN.get(), BlockRegistry.LARGE_BIRCH_WALL_ARROW_SIGN.get()));

        JUNGLE_ARROW_SIGN = registerItem("jungle_arrow_sign",
                () -> new ArrowSignItem(new Item.Properties().stacksTo(16),
                        BlockRegistry.JUNGLE_ARROW_SIGN.get(), BlockRegistry.JUNGLE_WALL_ARROW_SIGN.get(),
                        BlockRegistry.JUNGLE_HANGING_ARROW_SIGN.get()));
        LARGE_JUNGLE_ARROW_SIGN = registerItem("large_jungle_arrow_sign",
                () -> new LargeArrowSignItem(new Item.Properties().stacksTo(16),
                        BlockRegistry.LARGE_JUNGLE_ARROW_SIGN.get(), BlockRegistry.LARGE_JUNGLE_WALL_ARROW_SIGN.get()));

        ACACIA_ARROW_SIGN = registerItem("acacia_arrow_sign",
                () -> new ArrowSignItem(new Item.Properties().stacksTo(16),
                        BlockRegistry.ACACIA_ARROW_SIGN.get(), BlockRegistry.ACACIA_WALL_ARROW_SIGN.get(),
                        BlockRegistry.ACACIA_HANGING_ARROW_SIGN.get()));
        LARGE_ACACIA_ARROW_SIGN = registerItem("large_acacia_arrow_sign",
                () -> new LargeArrowSignItem(new Item.Properties().stacksTo(16),
                        BlockRegistry.LARGE_ACACIA_ARROW_SIGN.get(), BlockRegistry.LARGE_ACACIA_WALL_ARROW_SIGN.get()));

        DARK_OAK_ARROW_SIGN = registerItem("dark_oak_arrow_sign",
                () -> new ArrowSignItem(new Item.Properties().stacksTo(16),
                        BlockRegistry.DARK_OAK_ARROW_SIGN.get(), BlockRegistry.DARK_OAK_WALL_ARROW_SIGN.get(),
                        BlockRegistry.DARK_OAK_HANGING_ARROW_SIGN.get()));
        LARGE_DARK_OAK_ARROW_SIGN = registerItem("large_dark_oak_arrow_sign",
                () -> new LargeArrowSignItem(new Item.Properties().stacksTo(16),
                        BlockRegistry.LARGE_DARK_OAK_ARROW_SIGN.get(), BlockRegistry.LARGE_DARK_OAK_WALL_ARROW_SIGN.get()));

        MANGROVE_ARROW_SIGN = registerItem("mangrove_arrow_sign",
                () -> new ArrowSignItem(new Item.Properties().stacksTo(16),
                        BlockRegistry.MANGROVE_ARROW_SIGN.get(), BlockRegistry.MANGROVE_WALL_ARROW_SIGN.get(),
                        BlockRegistry.MANGROVE_HANGING_ARROW_SIGN.get()));
        LARGE_MANGROVE_ARROW_SIGN = registerItem("large_mangrove_arrow_sign",
                () -> new LargeArrowSignItem(new Item.Properties().stacksTo(16),
                        BlockRegistry.LARGE_MANGROVE_ARROW_SIGN.get(), BlockRegistry.LARGE_MANGROVE_WALL_ARROW_SIGN.get()));

        CHERRY_ARROW_SIGN = registerItem("cherry_arrow_sign",
                () -> new ArrowSignItem(new Item.Properties().stacksTo(16),
                        BlockRegistry.CHERRY_ARROW_SIGN.get(), BlockRegistry.CHERRY_WALL_ARROW_SIGN.get(),
                        BlockRegistry.CHERRY_HANGING_ARROW_SIGN.get()));
        LARGE_CHERRY_ARROW_SIGN = registerItem("large_cherry_arrow_sign",
                () -> new LargeArrowSignItem(new Item.Properties().stacksTo(16),
                        BlockRegistry.LARGE_CHERRY_ARROW_SIGN.get(), BlockRegistry.LARGE_CHERRY_WALL_ARROW_SIGN.get()));

        BAMBOO_ARROW_SIGN = registerItem("bamboo_arrow_sign",
                () -> new ArrowSignItem(new Item.Properties().stacksTo(16),
                        BlockRegistry.BAMBOO_ARROW_SIGN.get(), BlockRegistry.BAMBOO_WALL_ARROW_SIGN.get(),
                        BlockRegistry.BAMBOO_HANGING_ARROW_SIGN.get()));
        LARGE_BAMBOO_ARROW_SIGN = registerItem("large_bamboo_arrow_sign",
                () -> new LargeArrowSignItem(new Item.Properties().stacksTo(16),
                        BlockRegistry.LARGE_BAMBOO_ARROW_SIGN.get(), BlockRegistry.LARGE_BAMBOO_WALL_ARROW_SIGN.get()));

        CRIMSON_ARROW_SIGN = registerItem("crimson_arrow_sign",
                () -> new ArrowSignItem(new Item.Properties().stacksTo(16),
                        BlockRegistry.CRIMSON_ARROW_SIGN.get(), BlockRegistry.CRIMSON_WALL_ARROW_SIGN.get(),
                        BlockRegistry.CRIMSON_HANGING_ARROW_SIGN.get()));
        LARGE_CRIMSON_ARROW_SIGN = registerItem("large_crimson_arrow_sign",
                () -> new LargeArrowSignItem(new Item.Properties().stacksTo(16),
                        BlockRegistry.LARGE_CRIMSON_ARROW_SIGN.get(), BlockRegistry.LARGE_CRIMSON_WALL_ARROW_SIGN.get()));

        WARPED_ARROW_SIGN = registerItem("warped_arrow_sign",
                () -> new ArrowSignItem(new Item.Properties().stacksTo(16),
                        BlockRegistry.WARPED_ARROW_SIGN.get(), BlockRegistry.WARPED_WALL_ARROW_SIGN.get(),
                        BlockRegistry.WARPED_HANGING_ARROW_SIGN.get()));
        LARGE_WARPED_ARROW_SIGN = registerItem("large_warped_arrow_sign",
                () -> new LargeArrowSignItem(new Item.Properties().stacksTo(16),
                        BlockRegistry.LARGE_WARPED_ARROW_SIGN.get(), BlockRegistry.LARGE_WARPED_WALL_ARROW_SIGN.get()));

        Arrays.stream(DyeColor.values()).forEach(color ->
                CHECKPOINT_FLAGS.put(color, registerItem(color.getName() + "_checkpoint_flag",
                        () -> new CheckpointFlagBlockItem(BlockRegistry.CHECKPOINT_FLAGS.get(color).get(), new Item.Properties()))));

        WRENCH = registerItem("wrench",
                () -> new WrenchItem(new Item.Properties()
                        .attributes(WrenchItem.createAttributes(Tiers.IRON, 3, -3.2F))
                        .durability(128), Tiers.IRON));

        CREATIVE_WRENCH = registerItem("creative_wrench",
                () -> new CreativeWrenchItem(new Item.Properties()
                        .attributes(WrenchItem.createAttributes(Tiers.IRON, 3, -3.2F))
                        .durability(128), Tiers.IRON));

        CHEEP_CHEEP = registerItem("cheep_cheep",
                () -> new Item(new Item.Properties().food(FoodRegistry.RAW_CHEEP_CHEEP)));
        COLD_CHEEP_CHEEP = registerItem("cold_cheep_cheep",
                () -> new Item(new Item.Properties().food(FoodRegistry.RAW_CHEEP_CHEEP)));
        WARM_CHEEP_CHEEP = registerItem("warm_cheep_cheep",
                () -> new Item(new Item.Properties().food(FoodRegistry.RAW_CHEEP_CHEEP)));
        DEEP_CHEEP = registerItem("deep_cheep",
                () -> new Item(new Item.Properties().food(FoodRegistry.RAW_CHEEP_CHEEP)));
        EEP_CHEEP = registerItem("eep_cheep",
                () -> new Item(new Item.Properties().food(FoodRegistry.RAW_CHEEP_CHEEP)));
        COOKED_CHEEP_CHEEP = registerItem("cooked_cheep_cheep",
                () -> new Item(new Item.Properties().food(FoodRegistry.COOKED_CHEEP_CHEEP)));
        SPINY_CHEEP_CHEEP = registerItem("spiny_cheep_cheep",
                () -> new Item(new Item.Properties().food(FoodRegistry.RAW_SPINY_CHEEP_CHEEP)));
        COOKED_SPINY_CHEEP_CHEEP = registerItem("cooked_spiny_cheep_cheep",
                () -> new Item(new Item.Properties().food(FoodRegistry.COOKED_SPINY_CHEEP_CHEEP)));
        PORCUPUFFER = registerItem("porcupuffer",
                () -> new Item(new Item.Properties().food(FoodRegistry.RAW_PORCUPUFFER)));
        COOKED_PORCUPUFFER = registerItem("cooked_porcupuffer",
                () -> new Item(new Item.Properties().food(FoodRegistry.COOKED_PORCUPUFFER)));

        CHEEP_CHEEP_BUCKET = registerItem("cheep_cheep_bucket",
                () -> new MobBucketItem(EntityRegistry.CHEEP_CHEEP.get(), Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH,
                        new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)
                                .component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)));
        DEEP_CHEEP_BUCKET = registerItem("deep_cheep_bucket",
                () -> new MobBucketItem(EntityRegistry.DEEP_CHEEP.get(), Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH,
                        new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)
                                .component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)));
        EEP_CHEEP_BUCKET = registerItem("eep_cheep_bucket",
                () -> new MobBucketItem(EntityRegistry.EEP_CHEEP.get(), Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH,
                        new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)
                                .component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)));
        SPINY_CHEEP_CHEEP_BUCKET = registerItem("spiny_cheep_cheep_bucket",
                () -> new MobBucketItem(EntityRegistry.SPINY_CHEEP_CHEEP.get(), Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH,
                        new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)
                                .component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)));
        QUICKSAND_BUCKET = registerItem("quicksand_bucket",
                () -> new SolidBucketItem(BlockRegistry.QUICKSAND.get(), SoundEvents.BUCKET_EMPTY_POWDER_SNOW,
                        new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)));
        RED_QUICKSAND_BUCKET = registerItem("red_quicksand_bucket",
                () -> new SolidBucketItem(BlockRegistry.RED_QUICKSAND.get(), SoundEvents.BUCKET_EMPTY_POWDER_SNOW,
                        new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)));
        PLASTIC_BUCKET = registerItem("plastic_bucket",
                () -> new PlasticBucketItem(Ingredient.of(ItemTags.COALS), ArmorMaterials.IRON, ArmorItem.Type.HELMET,
                        3, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.HELMET.getDurability(10))));
        PLASTIC_WATER_BUCKET = registerItem("plastic_water_bucket",
                () -> new FluidPlasticBucketItem(3, Fluids.WATER, new Item.Properties().stacksTo(1)
                        .durability(128)));
        PLASTIC_POWDER_SNOW_BUCKET = registerItem("plastic_powder_snow_bucket",
                () -> new SolidPlasticBucketItem(2, Blocks.POWDER_SNOW, SoundEvents.BUCKET_EMPTY_POWDER_SNOW,
                        new Item.Properties().stacksTo(1).durability(128)
                                .craftRemainder(ItemRegistry.PLASTIC_BUCKET.get())));
        PLASTIC_QUICKSAND_BUCKET = registerItem("plastic_quicksand_bucket",
                () -> new SolidPlasticBucketItem(2, BlockRegistry.QUICKSAND.get(), SoundEvents.BUCKET_EMPTY_POWDER_SNOW,
                        new Item.Properties().stacksTo(1).durability(128)
                                .craftRemainder(ItemRegistry.PLASTIC_BUCKET.get())));
        PLASTIC_RED_QUICKSAND_BUCKET = registerItem("plastic_red_quicksand_bucket",
                () -> new SolidPlasticBucketItem(2, BlockRegistry.RED_QUICKSAND.get(), SoundEvents.BUCKET_EMPTY_POWDER_SNOW,
                        new Item.Properties().stacksTo(1).durability(128)
                                .craftRemainder(ItemRegistry.PLASTIC_BUCKET.get())));

        SUPER_MUSHROOM = registerItem("super_mushroom",
                () -> new SuperMushroomItem(4, new Item.Properties()));
        MEGA_MUSHROOM = registerItem("mega_mushroom",
                () -> new MegaMushroomItem(7, new Item.Properties()));
        MINI_MUSHROOM = registerItem("mini_mushroom",
                () -> new MiniMushroomItem(7, new Item.Properties()));
        DASH_MUSHROOM = registerItem("dash_mushroom",
                () -> new DashMushroomItem(5, new Item.Properties().food(FoodRegistry.DASH_MUSHROOM)));
        ONE_UP_MUSHROOM = registerItem("one_up_mushroom",
                () -> new OneUpMushroomItem(5, new Item.Properties().stacksTo(1)
                        .component(DataComponentRegistry.UNDYING_CHARM, true)));
        SUPER_STAR = registerItem("super_star",
                () -> new SuperStarItem(5, new Item.Properties()));
        FIRE_FLOWER = registerItem("fire_flower",
                () -> new FireFlowerItem(4, new Item.Properties()));
        ICE_FLOWER = registerItem("ice_flower",
                () -> new IceFlowerItem(4, new Item.Properties()));

        DASH_MUSHROOM_SPAWN_EGG = registerItem("dash_mushroom_spawn_egg",
                () -> new PowerUpSpawnEggItem(EntityRegistry.DASH_MUSHROOM, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        SUPER_MUSHROOM_SPAWN_EGG = registerItem("super_mushroom_spawn_egg",
                () -> new PowerUpSpawnEggItem(EntityRegistry.SUPER_MUSHROOM, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        MEGA_MUSHROOM_SPAWN_EGG = registerItem("mega_mushroom_spawn_egg",
                () -> new PowerUpSpawnEggItem(EntityRegistry.MEGA_MUSHROOM, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        MINI_MUSHROOM_SPAWN_EGG = registerItem("mini_mushroom_spawn_egg",
                () -> new PowerUpSpawnEggItem(EntityRegistry.MINI_MUSHROOM, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        ONE_UP_MUSHROOM_SPAWN_EGG = registerItem("one_up_mushroom_spawn_egg",
                () -> new OneUpMushroomSpawnEggItem(EntityRegistry.ONE_UP_MUSHROOM, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        SUPER_STAR_SPAWN_EGG = registerItem("super_star_spawn_egg",
                () -> new PowerUpSpawnEggItem(EntityRegistry.SUPER_STAR, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        FIRE_FLOWER_SPAWN_EGG = registerItem("fire_flower_spawn_egg",
                () -> new PowerUpSpawnEggItem(EntityRegistry.FIRE_FLOWER, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        ICE_FLOWER_SPAWN_EGG = registerItem("ice_flower_spawn_egg",
                () -> new PowerUpSpawnEggItem(EntityRegistry.ICE_FLOWER, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));

        LARGE_SNOWBALL = registerItem("large_snowball",
                () -> new LargeSnowballItem(new Item.Properties().stacksTo(16)));
        GOLD_KOOPA_SHELL = registerItem("gold_koopa_shell",
                () -> new KoopaShellItem(3, EntityRegistry.GOLD_KOOPA_SHELL, 0xFFFFFF, 0xFFFFFF, new Item.Properties().stacksTo(16)));
        GREEN_KOOPA_SHELL = registerItem("green_koopa_shell",
                () -> new KoopaShellItem(2, EntityRegistry.GREEN_KOOPA_SHELL, 0xFFFFFF, 0xFFFFFF, new Item.Properties().stacksTo(16)));
        RED_KOOPA_SHELL = registerItem("red_koopa_shell",
                () -> new KoopaShellItem(2, EntityRegistry.RED_KOOPA_SHELL, 0xFFFFFF, 0xFFFFFF, new Item.Properties().stacksTo(16)));

        WARP_DISRUPTOR = registerItem("warp_disruptor",
                () -> new WarpDisruptorItem(new Item.Properties().durability(128)));

        PIRANHA_PLANT_POD = registerItem("piranha_plant_pod",
                () -> new PiranhaPlantPodItem(6, EntityRegistry.PIRANHA_PLANT, 0xFFFFFF, 0xFFFFFF,
                        new Item.Properties().component(DataComponentRegistry.VARIANT, "normal")));

        MARIO_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("mario_armor_trim_smithing_template",
                () -> SmithingTemplateItem.createArmorTrimTemplate(TrimPatternRegistry.MARIO));
        LUIGI_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("luigi_armor_trim_smithing_template",
                () -> SmithingTemplateItem.createArmorTrimTemplate(TrimPatternRegistry.LUIGI));
        PRINCESS_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("princess_armor_trim_smithing_template",
                () -> SmithingTemplateItem.createArmorTrimTemplate(TrimPatternRegistry.PRINCESS));
        WARIO_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("wario_armor_trim_smithing_template",
                () -> SmithingTemplateItem.createArmorTrimTemplate(TrimPatternRegistry.WARIO));
        WALUIGI_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("waluigi_armor_trim_smithing_template",
                () -> SmithingTemplateItem.createArmorTrimTemplate(TrimPatternRegistry.WALUIGI));

        CHRISTMAS_HAT = registerItem("christmas_hat",
                () -> new ChristmasHatItem(Ingredient.of(ItemTags.WOOL), ArmorMaterials.LEATHER, ArmorItem.Type.HELMET,
                        3, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.HELMET.getDurability(8))
                        .component(DataComponents.DYED_COLOR, new DyedItemColor(0xFFED0011, true))));
        GOLDEN_KOOPA_SHOES = registerItem("golden_koopa_shoes",
                () -> new KoopaShoesItem(Ingredient.of(Tags.Items.INGOTS_GOLD), ArmorMaterials.GOLD, ArmorItem.Type.BOOTS,
                        2, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.BOOTS.getDurability(12))));
        GREEN_KOOPA_SHOES = registerItem("green_koopa_shoes",
                () -> new KoopaShoesItem(Ingredient.of(Items.TURTLE_SCUTE), ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS,
                        2, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.BOOTS.getDurability(10))));
        RED_KOOPA_SHOES = registerItem("red_koopa_shoes",
                () -> new KoopaShoesItem(Ingredient.of(Items.TURTLE_SCUTE), ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS,
                        2, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.BOOTS.getDurability(10))));
        WHITE_KOOPA_SHOES = registerItem("white_koopa_shoes",
                () -> new KoopaShoesItem(Ingredient.of(Items.TURTLE_SCUTE), ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS,
                        2, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.BOOTS.getDurability(10))));

        HAT = registerItem("hat",
                () -> new MaleCostumeItem(Ingredient.of(ItemTags.WOOL), ArmorMaterials.LEATHER, ArmorItem.Type.HELMET,
                        "male_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.HELMET.getDurability(10))
                        .component(DataComponents.DYED_COLOR, new DyedItemColor(0xF6343A, true))));
        SHIRT = registerItem("shirt",
                () -> new MaleCostumeItem(Ingredient.of(ItemTags.WOOL), ArmorMaterials.LEATHER, ArmorItem.Type.CHESTPLATE,
                        "male_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.CHESTPLATE.getDurability(10))
                        .component(DataComponents.DYED_COLOR, new DyedItemColor(0xF6343A, true))));
        PANTS = registerItem("pants",
                () -> new MaleCostumeItem(Ingredient.of(ItemTags.WOOL), ArmorMaterials.LEATHER, ArmorItem.Type.LEGGINGS,
                        "male_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.LEGGINGS.getDurability(10))
                        .component(DataComponents.DYED_COLOR, new DyedItemColor(0xF6343A, true))));
        SHOES = registerItem("shoes",
                () -> new MaleCostumeItem(Ingredient.of(ItemTags.WOOL), ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS,
                        "male_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.BOOTS.getDurability(10))
                        .component(DataComponents.DYED_COLOR, new DyedItemColor(0xA94535, true))));

        CROWN = registerItem("crown",
                () -> new FemaleCostumeItem(Ingredient.of(ItemTags.WOOL), ArmorMaterials.GOLD, ArmorItem.Type.HELMET,
                        "female_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.HELMET.getDurability(12))
                        .component(DataComponents.DYED_COLOR, new DyedItemColor(0xFF647D, true))));
        BODICE = registerItem("bodice",
                () -> new FemaleCostumeItem(Ingredient.of(ItemTags.WOOL), ArmorMaterials.LEATHER, ArmorItem.Type.CHESTPLATE,
                        "female_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.CHESTPLATE.getDurability(10))
                        .component(DataComponents.DYED_COLOR, new DyedItemColor(0xFFC1D7, true))));
        DRESS = registerItem("dress",
                () -> new FemaleCostumeItem(Ingredient.of(ItemTags.WOOL), ArmorMaterials.LEATHER, ArmorItem.Type.LEGGINGS,
                        "female_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.LEGGINGS.getDurability(10))
                        .component(DataComponents.DYED_COLOR, new DyedItemColor(0xFFC1D7, true))));
        HEELS = registerItem("heels",
                () -> new FemaleCostumeItem(Ingredient.of(ItemTags.WOOL), ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS,
                        "female_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.BOOTS.getDurability(10))
                        .component(DataComponents.DYED_COLOR, new DyedItemColor(0xFFC1D7, true))));

        BOWSER_BANNER_PATTERN = registerItem("bowser_banner_pattern",
                () -> new BannerPatternItem(TagRegistry.BOWSER_BANNER_PATTERN, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
        BOWSER_POTTERY_SHERD = registerItem("bowser_pottery_sherd",
                () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

        PLUMBER_BANNER_PATTERN = registerItem("plumber_banner_pattern",
                () -> new BannerPatternItem(TagRegistry.PLUMBER_BANNER_PATTERN, new Item.Properties().stacksTo(1)));
        PLUMBER_POTTERY_SHERD = registerItem("plumber_pottery_sherd",
                () -> new Item(new Item.Properties()));

        BOO_SPAWN_EGG = registerItem("boo_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.BOO, 0xFFFFFF, 0xFFFFFF,
                        new Item.Properties()));
        CHEEP_CHEEP_SPAWN_EGG = registerItem("cheep_cheep_spawn_egg",
                () -> new BetterSpawnEggItem(EntityRegistry.CHEEP_CHEEP, 0xFFFFFF, 0xFFFFFF,
                        new Item.Properties().component(DataComponentRegistry.VARIANT, "normal")));
        DEEP_CHEEP_SPAWN_EGG = registerItem("deep_cheep_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.DEEP_CHEEP, 0xFFFFFF, 0xFFFFFF,
                        new Item.Properties()));
        DRY_BONES_SPAWN_EGG = registerItem("dry_bones_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.DRY_BONES, 0xFFFFFF, 0xFFFFFF,
                        new Item.Properties()));
        EEP_CHEEP_SPAWN_EGG = registerItem("eep_cheep_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.EEP_CHEEP, 0xFFFFFF, 0xFFFFFF,
                        new Item.Properties()));
        FIRE_GOOMBA_SPAWN_EGG = registerItem("fire_goomba_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.FIRE_GOOMBA, 0xFFFFFF, 0xFFFFFF,
                        new Item.Properties()));
        GOLD_KOOPA_TROOPA_SPAWN_EGG = registerItem("gold_koopa_troopa_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.GOLD_KOOPA_TROOPA, 0xFFFFFF, 0xFFFFFF,
                        new Item.Properties()));
        GOOMBA_SPAWN_EGG = registerItem("goomba_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.GOOMBA, 0xFFFFFF, 0xFFFFFF,
                        new Item.Properties()));
        GREEN_KOOPA_TROOPA_SPAWN_EGG = registerItem("green_koopa_troopa_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.GREEN_KOOPA_TROOPA, 0xFFFFFF, 0xFFFFFF,
                        new Item.Properties()));
        HEFTY_GOOMBA_SPAWN_EGG = registerItem("hefty_goomba_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.HEFTY_GOOMBA, 0xFFFFFF, 0xFFFFFF,
                        new Item.Properties()));
        MEGA_GOOMBA_SPAWN_EGG = registerItem("mega_goomba_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.MEGA_GOOMBA, 0xFFFFFF, 0xFFFFFF,
                        new Item.Properties()));
        MINI_GOOMBA_SPAWN_EGG = registerItem("mini_goomba_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.MINI_GOOMBA, 0xFFFFFF, 0xFFFFFF,
                        new Item.Properties()));
        PIRANHA_PLANT_SPAWN_EGG = registerItem("piranha_plant_spawn_egg",
                () -> new BetterSpawnEggItem(EntityRegistry.PIRANHA_PLANT, 0xFFFFFF, 0xFFFFFF,
                        new Item.Properties().component(DataComponentRegistry.VARIANT, "normal")));
        POKEY_SPAWN_EGG = registerItem("pokey_spawn_egg",
                () -> new PokeySpawnEggItem(EntityRegistry.POKEY_BODY, 0xFFFFFF, 0xFFFFFF,
                        new Item.Properties()));
        PORCUPUFFER_SPAWN_EGG = registerItem("porcupuffer_spawn_egg",
                () -> new BetterSpawnEggItem(EntityRegistry.PORCUPUFFER, 0xFFFFFF, 0xFFFFFF,
                        new Item.Properties().component(DataComponentRegistry.VARIANT, "normal")));
        RED_KOOPA_TROOPA_SPAWN_EGG = registerItem("red_koopa_troopa_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.RED_KOOPA_TROOPA, 0xFFFFFF, 0xFFFFFF,
                        new Item.Properties()));
        SNOW_POKEY_SPAWN_EGG = registerItem("snow_pokey_spawn_egg",
                () -> new SnowPokeySpawnEggItem(EntityRegistry.SNOW_POKEY_BODY, 0xFFFFFF, 0xFFFFFF,
                        new Item.Properties()));
        SPINY_CHEEP_CHEEP_SPAWN_EGG = registerItem("spiny_cheep_cheep_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.SPINY_CHEEP_CHEEP, 0xFFFFFF, 0xFFFFFF,
                        new Item.Properties()));
        SPLUNKIN_SPAWN_EGG = registerItem("splunkin_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.SPLUNKIN, 0xFFFFFF, 0xFFFFFF,
                        new Item.Properties()));
    }

    public static <T extends Item> DeferredItem<T> registerItem(String name, Supplier<T> item) {
        return Marioverse.ITEMS.register(name, item);
    }

    public static void init() {}

    public static void registerAliases() {
        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "mario_costume_smithing_template"), MARIO_ARMOR_TRIM_SMITHING_TEMPLATE.getId());
        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "luigi_costume_smithing_template"), LUIGI_ARMOR_TRIM_SMITHING_TEMPLATE.getId());
        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "peach_costume_smithing_template"), PRINCESS_ARMOR_TRIM_SMITHING_TEMPLATE.getId());
        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "fire_costume_smithing_template"), MARIO_ARMOR_TRIM_SMITHING_TEMPLATE.getId());
        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "ice_costume_smithing_template"), LUIGI_ARMOR_TRIM_SMITHING_TEMPLATE.getId());

        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "mario_hat"), HAT.getId());
        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "mario_shirt"), SHIRT.getId());
        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "mario_pants"), PANTS.getId());
        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "mario_shoes"), SHOES.getId());

        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "mario_fire_hat"), HAT.getId());
        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "mario_fire_shirt"), SHIRT.getId());
        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "mario_fire_pants"), PANTS.getId());
        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "mario_fire_shoes"), SHOES.getId());

        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "mario_ice_hat"), HAT.getId());
        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "mario_ice_shirt"), SHIRT.getId());
        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "mario_ice_pants"), PANTS.getId());
        Marioverse.ENTITIES.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "mario_ice_shoes"), SHOES.getId());

        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "luigi_hat"), HAT.getId());
        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "luigi_shirt"), SHIRT.getId());
        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "luigi_pants"), PANTS.getId());
        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "luigi_shoes"), SHOES.getId());

        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "luigi_fire_hat"), HAT.getId());
        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "luigi_fire_shirt"), SHIRT.getId());
        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "luigi_fire_pants"), PANTS.getId());
        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "luigi_fire_shoes"), SHOES.getId());

        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "luigi_ice_hat"), HAT.getId());
        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "luigi_ice_shirt"), SHIRT.getId());
        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "luigi_ice_pants"), PANTS.getId());
        Marioverse.ENTITIES.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "luigi_ice_shoes"), SHOES.getId());

        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "peach_crown"), CROWN.getId());
        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "peach_bodice"), BODICE.getId());
        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "peach_dress"), DRESS.getId());
        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "peach_shoes"), HEELS.getId());

        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "peach_fire_bodice"), BODICE.getId());
        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "peach_fire_dress"), DRESS.getId());
        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "peach_fire_shoes"), HEELS.getId());

        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "peach_ice_bodice"), BODICE.getId());
        Marioverse.ITEMS.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "peach_ice_dress"), DRESS.getId());
        Marioverse.ENTITIES.addAlias(ResourceLocation
                .fromNamespaceAndPath(Marioverse.MOD_ID, "peach_ice_shoes"), HEELS.getId());
    }
}
