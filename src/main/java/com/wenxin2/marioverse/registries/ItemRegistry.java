package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.items.BasePowerUpItem;
import com.wenxin2.marioverse.items.BetterSpawnEggItem;
import com.wenxin2.marioverse.items.CharacterSmithingTemplateItem;
import com.wenxin2.marioverse.items.CheckpointFlagBlockItem;
import com.wenxin2.marioverse.items.ChristmasHatItem;
import com.wenxin2.marioverse.items.CostumeItem;
import com.wenxin2.marioverse.items.DashMushroomItem;
import com.wenxin2.marioverse.items.FluidPlasticBucketItem;
import com.wenxin2.marioverse.items.KoopaShellItem;
import com.wenxin2.marioverse.items.KoopaShoesItem;
import com.wenxin2.marioverse.items.LargeSnowballItem;
import com.wenxin2.marioverse.items.OneUpMushroomItem;
import com.wenxin2.marioverse.items.PiranhaPlantPodItem;
import com.wenxin2.marioverse.items.PlasticBucketItem;
import com.wenxin2.marioverse.items.PokeySpawnEggItem;
import com.wenxin2.marioverse.items.SnowPokeySpawnEggItem;
import com.wenxin2.marioverse.items.SolidPlasticBucketItem;
import com.wenxin2.marioverse.items.StarCoinBlockItem;
import com.wenxin2.marioverse.items.WarpDisruptorItem;
import com.wenxin2.marioverse.items.WrenchItem;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredItem;

public class ItemRegistry {
    public static final EnumMap<DyeColor, DeferredItem<Item>> CHECKPOINT_FLAGS =
            new EnumMap<>(DyeColor.class);

    public static final DeferredItem<Item> BOO_SPAWN_EGG;
    public static final DeferredItem<Item> BOWSER_BANNER_PATTERN;
    public static final DeferredItem<Item> BOWSER_POTTERY_SHERD;
    public static final DeferredItem<Item> CHRISTMAS_HAT;
    public static final DeferredItem<Item> CLASSIC_CHECKPOINT_FLAG;
    public static final DeferredItem<Item> DASH_MUSHROOM;
    public static final DeferredItem<Item> DRY_BONES_SPAWN_EGG;
    public static final DeferredItem<Item> FIRE_COSTUME_SMITHING_TEMPLATE;
    public static final DeferredItem<Item> FIRE_FLOWER;
    public static final DeferredItem<Item> FIRE_GOOMBA_SPAWN_EGG;
    public static final DeferredItem<Item> GOLDEN_KOOPA_SHOES;
    public static final DeferredItem<Item> GOLD_KOOPA_SHELL;
    public static final DeferredItem<Item> GOLD_KOOPA_TROOPA_SPAWN_EGG;
    public static final DeferredItem<Item> GOOMBA_SPAWN_EGG;
    public static final DeferredItem<Item> GREEN_KOOPA_SHELL;
    public static final DeferredItem<Item> GREEN_KOOPA_SHOES;
    public static final DeferredItem<Item> GREEN_KOOPA_TROOPA_SPAWN_EGG;
    public static final DeferredItem<Item> HEFTY_GOOMBA_SPAWN_EGG;
    public static final DeferredItem<Item> ICE_COSTUME_SMITHING_TEMPLATE;
    public static final DeferredItem<Item> ICE_FLOWER;
    public static final DeferredItem<Item> LARGE_SNOWBALL;
    public static final DeferredItem<Item> LUIGI_COSTUME_SMITHING_TEMPLATE;
    public static final DeferredItem<Item> LUIGI_FIRE_HAT;
    public static final DeferredItem<Item> LUIGI_FIRE_PANTS;
    public static final DeferredItem<Item> LUIGI_FIRE_SHIRT;
    public static final DeferredItem<Item> LUIGI_FIRE_SHOES;
    public static final DeferredItem<Item> LUIGI_HAT;
    public static final DeferredItem<Item> LUIGI_ICE_HAT;
    public static final DeferredItem<Item> LUIGI_ICE_PANTS;
    public static final DeferredItem<Item> LUIGI_ICE_SHIRT;
    public static final DeferredItem<Item> LUIGI_ICE_SHOES;
    public static final DeferredItem<Item> LUIGI_PANTS;
    public static final DeferredItem<Item> LUIGI_SHIRT;
    public static final DeferredItem<Item> LUIGI_SHOES;
    public static final DeferredItem<Item> MARIO_COSTUME_SMITHING_TEMPLATE;
    public static final DeferredItem<Item> MARIO_FIRE_HAT;
    public static final DeferredItem<Item> MARIO_FIRE_PANTS;
    public static final DeferredItem<Item> MARIO_FIRE_SHIRT;
    public static final DeferredItem<Item> MARIO_FIRE_SHOES;
    public static final DeferredItem<Item> MARIO_HAT;
    public static final DeferredItem<Item> MARIO_ICE_HAT;
    public static final DeferredItem<Item> MARIO_ICE_PANTS;
    public static final DeferredItem<Item> MARIO_ICE_SHIRT;
    public static final DeferredItem<Item> MARIO_ICE_SHOES;
    public static final DeferredItem<Item> MARIO_PANTS;
    public static final DeferredItem<Item> MARIO_SHIRT;
    public static final DeferredItem<Item> MARIO_SHOES;
    public static final DeferredItem<Item> MEGA_GOOMBA_SPAWN_EGG;
    public static final DeferredItem<Item> MINI_GOOMBA_SPAWN_EGG;
    public static final DeferredItem<Item> MINI_MUSHROOM;
    public static final DeferredItem<Item> ONE_UP_MUSHROOM;
    public static final DeferredItem<Item> PEACH_BODICE;
    public static final DeferredItem<Item> PEACH_COSTUME_SMITHING_TEMPLATE;
    public static final DeferredItem<Item> PEACH_CROWN;
    public static final DeferredItem<Item> PEACH_DRESS;
    public static final DeferredItem<Item> PEACH_FIRE_BODICE;
    public static final DeferredItem<Item> PEACH_FIRE_DRESS;
    public static final DeferredItem<Item> PEACH_FIRE_SHOES;
    public static final DeferredItem<Item> PEACH_ICE_BODICE;
    public static final DeferredItem<Item> PEACH_ICE_DRESS;
    public static final DeferredItem<Item> PEACH_ICE_SHOES;
    public static final DeferredItem<Item> PEACH_SHOES;
    public static final DeferredItem<Item> PIRANHA_PLANT_POD;
    public static final DeferredItem<Item> PIRANHA_PLANT_SPAWN_EGG;
    public static final DeferredItem<Item> PLASTIC_BUCKET;
    public static final DeferredItem<Item> PLUMBER_BANNER_PATTERN;
    public static final DeferredItem<Item> PLUMBER_POTTERY_SHERD;
    public static final DeferredItem<Item> POKEY_SPAWN_EGG;
    public static final DeferredItem<Item> PLASTIC_POWDER_SNOW_BUCKET;
    public static final DeferredItem<Item> QUICKSAND_BUCKET;
    public static final DeferredItem<Item> PLASTIC_QUICKSAND_BUCKET;
    public static final DeferredItem<Item> RED_KOOPA_SHELL;
    public static final DeferredItem<Item> RED_KOOPA_SHOES;
    public static final DeferredItem<Item> RED_KOOPA_TROOPA_SPAWN_EGG;
    public static final DeferredItem<Item> RED_QUICKSAND_BUCKET;
    public static final DeferredItem<Item> PLASTIC_RED_QUICKSAND_BUCKET;
    public static final DeferredItem<Item> SNOW_POKEY_SPAWN_EGG;
    public static final DeferredItem<Item> SPLUNKIN_SPAWN_EGG;
    public static final DeferredItem<Item> STAR_COIN;
    public static final DeferredItem<Item> SUPER_MUSHROOM;
    public static final DeferredItem<Item> SUPER_STAR;
    public static final DeferredItem<Item> WARP_DISRUPTOR;
    public static final DeferredItem<Item> PLASTIC_WATER_BUCKET;
    public static final DeferredItem<Item> WHITE_KOOPA_SHOES;
    public static final DeferredItem<Item> WRENCH;

    static {
        STAR_COIN = registerItem("star_coin", () -> new StarCoinBlockItem(BlockRegistry.STAR_COIN.get(), new Item.Properties()));
        CLASSIC_CHECKPOINT_FLAG = registerItem("classic_checkpoint_flag",
                () -> new CheckpointFlagBlockItem(BlockRegistry.CLASSIC_CHECKPOINT_FLAG.get(), new Item.Properties()));

        Arrays.stream(DyeColor.values()).forEach(color ->
                CHECKPOINT_FLAGS.put(color, registerItem(color.getName() + "_checkpoint_flag",
                        () -> new CheckpointFlagBlockItem(BlockRegistry.CHECKPOINT_FLAGS.get(color).get(), new Item.Properties()))));

        WRENCH = registerItem("wrench",
                () -> new WrenchItem(new Item.Properties()
                        .attributes(WrenchItem.createAttributes(Tiers.IRON, 3, -3.2F))
                        .durability(128), Tiers.IRON));

        QUICKSAND_BUCKET = registerItem("quicksand_bucket",
                () -> new SolidBucketItem(BlockRegistry.QUICKSAND.get(), SoundEvents.BUCKET_EMPTY_POWDER_SNOW,
                        new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)));
        RED_QUICKSAND_BUCKET = registerItem("red_quicksand_bucket",
                () -> new SolidBucketItem(BlockRegistry.RED_QUICKSAND.get(), SoundEvents.BUCKET_EMPTY_POWDER_SNOW,
                        new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)));
        PLASTIC_BUCKET = registerItem("plastic_bucket",
                () -> new PlasticBucketItem(2, Ingredient.of(ItemTags.COALS), ArmorMaterials.IRON, ArmorItem.Type.HELMET,
                        new Item.Properties().stacksTo(1)
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
                () -> new BasePowerUpItem(2, EntityRegistry.SUPER_MUSHROOM, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        MINI_MUSHROOM = registerItem("mini_mushroom",
                () -> new BasePowerUpItem(5, EntityRegistry.MINI_MUSHROOM, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        DASH_MUSHROOM = registerItem("dash_mushroom",
                () -> new DashMushroomItem(3, new Item.Properties().food(FoodRegistry.DASH_MUSHROOM)));
        ONE_UP_MUSHROOM = registerItem("one_up_mushroom",
                () -> new OneUpMushroomItem(4, EntityRegistry.ONE_UP_MUSHROOM, 0xFFFFFF, 0xFFFFFF, new Item.Properties().stacksTo(1)));
        SUPER_STAR = registerItem("super_star",
                () -> new BasePowerUpItem(3, EntityRegistry.SUPER_STAR, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        FIRE_FLOWER = registerItem("fire_flower",
                () -> new BasePowerUpItem(2, EntityRegistry.FIRE_FLOWER, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        ICE_FLOWER = registerItem("ice_flower",
                () -> new BasePowerUpItem(2, EntityRegistry.ICE_FLOWER, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));

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
                () -> new PiranhaPlantPodItem(5, EntityRegistry.PIRANHA_PLANT, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));

        MARIO_COSTUME_SMITHING_TEMPLATE = registerItem("mario_costume_smithing_template",
                CharacterSmithingTemplateItem::createCharacterUpgradeTemplate);
        LUIGI_COSTUME_SMITHING_TEMPLATE = registerItem("luigi_costume_smithing_template",
                CharacterSmithingTemplateItem::createCharacterUpgradeTemplate);
        PEACH_COSTUME_SMITHING_TEMPLATE = registerItem("peach_costume_smithing_template",
                CharacterSmithingTemplateItem::createCharacterUpgradeTemplate);
        FIRE_COSTUME_SMITHING_TEMPLATE = registerItem("fire_costume_smithing_template",
                CharacterSmithingTemplateItem::createFireUpgradeTemplate);
        ICE_COSTUME_SMITHING_TEMPLATE = registerItem("ice_costume_smithing_template",
                CharacterSmithingTemplateItem::createIceUpgradeTemplate);

        CHRISTMAS_HAT = registerItem("christmas_hat",
                () -> new ChristmasHatItem(Ingredient.of(ItemTags.WOOL), ArmorMaterials.LEATHER, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.HELMET.getDurability(8))));
        GOLDEN_KOOPA_SHOES = registerItem("golden_koopa_shoes",
                () -> new KoopaShoesItem(Ingredient.of(Tags.Items.INGOTS_GOLD), ArmorMaterials.GOLD, ArmorItem.Type.BOOTS, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.BOOTS.getDurability(12))));
        GREEN_KOOPA_SHOES = registerItem("green_koopa_shoes",
                () -> new KoopaShoesItem(Ingredient.of(Items.TURTLE_SCUTE), ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.BOOTS.getDurability(10))));
        RED_KOOPA_SHOES = registerItem("red_koopa_shoes",
                () -> new KoopaShoesItem(Ingredient.of(Items.TURTLE_SCUTE), ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.BOOTS.getDurability(10))));
        WHITE_KOOPA_SHOES = registerItem("white_koopa_shoes",
                () -> new KoopaShoesItem(Ingredient.of(Items.TURTLE_SCUTE), ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.BOOTS.getDurability(10))));

        MARIO_HAT = registerItem("mario_hat",
                () -> new CostumeItem(Ingredient.of(ItemTags.WOOL), ArmorMaterials.LEATHER, ArmorItem.Type.HELMET,
                        "mario_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.HELMET.getDurability(10))));
        MARIO_SHIRT = registerItem("mario_shirt",
                () -> new CostumeItem(Ingredient.of(ItemTags.WOOL), ArmorMaterials.LEATHER, ArmorItem.Type.CHESTPLATE,
                        "mario_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.CHESTPLATE.getDurability(10))));
        MARIO_PANTS = registerItem("mario_pants",
                () -> new CostumeItem(Ingredient.of(ItemTags.WOOL), ArmorMaterials.LEATHER, ArmorItem.Type.LEGGINGS,
                        "mario_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.LEGGINGS.getDurability(10))));
        MARIO_SHOES = registerItem("mario_shoes",
                () -> new CostumeItem(Ingredient.of(ItemTags.WOOL), ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS,
                        "mario_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.BOOTS.getDurability(10))));

        LUIGI_HAT = registerItem("luigi_hat",
                () -> new CostumeItem(Ingredient.of(ItemTags.WOOL), ArmorMaterials.LEATHER, ArmorItem.Type.HELMET,
                        "luigi_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.HELMET.getDurability(10))));
        LUIGI_SHIRT = registerItem("luigi_shirt",
                () -> new CostumeItem(Ingredient.of(ItemTags.WOOL), ArmorMaterials.LEATHER, ArmorItem.Type.CHESTPLATE,
                        "luigi_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.CHESTPLATE.getDurability(10))));
        LUIGI_PANTS = registerItem("luigi_pants",
                () -> new CostumeItem(Ingredient.of(ItemTags.WOOL), ArmorMaterials.LEATHER, ArmorItem.Type.LEGGINGS,
                        "luigi_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.LEGGINGS.getDurability(10))));
        LUIGI_SHOES = registerItem("luigi_shoes",
                () -> new CostumeItem(Ingredient.of(ItemTags.WOOL), ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS,
                        "luigi_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.BOOTS.getDurability(10))));

        PEACH_CROWN = registerItem("peach_crown",
                () -> new CostumeItem(Ingredient.of(Tags.Items.INGOTS_GOLD), ArmorMaterials.GOLD, ArmorItem.Type.HELMET,
                        "peach_costume", 5, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.HELMET.getDurability(12))));
        PEACH_BODICE = registerItem("peach_bodice",
                () -> new CostumeItem(Ingredient.of(ItemTags.WOOL), ArmorMaterials.LEATHER, ArmorItem.Type.CHESTPLATE,
                        "peach_costume", 5, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.CHESTPLATE.getDurability(10))));
        PEACH_DRESS = registerItem("peach_dress",
                () -> new CostumeItem(Ingredient.of(ItemTags.WOOL), ArmorMaterials.LEATHER, ArmorItem.Type.LEGGINGS,
                        "peach_costume", 5, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.LEGGINGS.getDurability(10))));
        PEACH_SHOES = registerItem("peach_shoes",
                () -> new CostumeItem(Ingredient.of(ItemTags.WOOL), ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS,
                        "peach_costume", 5, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.BOOTS.getDurability(10))));

        MARIO_FIRE_HAT = registerItem("mario_fire_hat",
                () -> new CostumeItem(Ingredient.of(ItemRegistry.FIRE_FLOWER), ArmorMaterials.LEATHER, ArmorItem.Type.HELMET,
                        "mario_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.HELMET.getDurability(10))));
        MARIO_FIRE_SHIRT = registerItem("mario_fire_shirt",
                () -> new CostumeItem(Ingredient.of(ItemRegistry.FIRE_FLOWER), ArmorMaterials.LEATHER, ArmorItem.Type.CHESTPLATE,
                        "mario_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.CHESTPLATE.getDurability(10))));
        MARIO_FIRE_PANTS = registerItem("mario_fire_pants",
                () -> new CostumeItem(Ingredient.of(ItemRegistry.FIRE_FLOWER), ArmorMaterials.LEATHER, ArmorItem.Type.LEGGINGS,
                        "mario_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.LEGGINGS.getDurability(10))));
        MARIO_FIRE_SHOES = registerItem("mario_fire_shoes",
                () -> new CostumeItem(Ingredient.of(ItemRegistry.FIRE_FLOWER), ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS,
                        "mario_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.BOOTS.getDurability(10))));
        LUIGI_FIRE_HAT = registerItem("luigi_fire_hat",
                () -> new CostumeItem(Ingredient.of(ItemRegistry.FIRE_FLOWER), ArmorMaterials.LEATHER, ArmorItem.Type.HELMET,
                        "luigi_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.HELMET.getDurability(10))));
        LUIGI_FIRE_SHIRT = registerItem("luigi_fire_shirt",
                () -> new CostumeItem(Ingredient.of(ItemRegistry.FIRE_FLOWER), ArmorMaterials.LEATHER, ArmorItem.Type.CHESTPLATE,
                        "luigi_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.CHESTPLATE.getDurability(10))));
        LUIGI_FIRE_PANTS = registerItem("luigi_fire_pants",
                () -> new CostumeItem(Ingredient.of(ItemRegistry.FIRE_FLOWER), ArmorMaterials.LEATHER, ArmorItem.Type.LEGGINGS,
                        "luigi_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.LEGGINGS.getDurability(10))));
        LUIGI_FIRE_SHOES = registerItem("luigi_fire_shoes",
                () -> new CostumeItem(Ingredient.of(ItemRegistry.FIRE_FLOWER), ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS,
                        "luigi_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.BOOTS.getDurability(10))));
        PEACH_FIRE_BODICE = registerItem("peach_fire_bodice",
                () -> new CostumeItem(Ingredient.of(ItemRegistry.FIRE_FLOWER), ArmorMaterials.LEATHER, ArmorItem.Type.CHESTPLATE,
                        "peach_costume", 5, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.CHESTPLATE.getDurability(10))));
        PEACH_FIRE_DRESS = registerItem("peach_fire_dress",
                () -> new CostumeItem(Ingredient.of(ItemRegistry.FIRE_FLOWER), ArmorMaterials.LEATHER, ArmorItem.Type.LEGGINGS,
                        "peach_costume", 5, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.LEGGINGS.getDurability(10))));
        PEACH_FIRE_SHOES = registerItem("peach_fire_shoes",
                () -> new CostumeItem(Ingredient.of(ItemRegistry.FIRE_FLOWER), ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS,
                        "peach_costume", 5, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.BOOTS.getDurability(10))));

        MARIO_ICE_HAT = registerItem("mario_ice_hat",
                () -> new CostumeItem(Ingredient.of(ItemRegistry.ICE_FLOWER), ArmorMaterials.LEATHER, ArmorItem.Type.HELMET,
                        "mario_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.HELMET.getDurability(10))));
        MARIO_ICE_SHIRT = registerItem("mario_ice_shirt",
                () -> new CostumeItem(Ingredient.of(ItemRegistry.ICE_FLOWER), ArmorMaterials.LEATHER, ArmorItem.Type.CHESTPLATE,
                        "mario_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.CHESTPLATE.getDurability(10))));
        MARIO_ICE_PANTS = registerItem("mario_ice_pants",
                () -> new CostumeItem(Ingredient.of(ItemRegistry.ICE_FLOWER), ArmorMaterials.LEATHER, ArmorItem.Type.LEGGINGS,
                        "mario_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.LEGGINGS.getDurability(10))));
        MARIO_ICE_SHOES = registerItem("mario_ice_shoes",
                () -> new CostumeItem(Ingredient.of(ItemRegistry.ICE_FLOWER), ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS,
                        "mario_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.BOOTS.getDurability(10))));
        LUIGI_ICE_HAT = registerItem("luigi_ice_hat",
                () -> new CostumeItem(Ingredient.of(ItemRegistry.ICE_FLOWER), ArmorMaterials.LEATHER, ArmorItem.Type.HELMET,
                        "luigi_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.HELMET.getDurability(10))));
        LUIGI_ICE_SHIRT = registerItem("luigi_ice_shirt",
                () -> new CostumeItem(Ingredient.of(ItemRegistry.ICE_FLOWER), ArmorMaterials.LEATHER, ArmorItem.Type.CHESTPLATE,
                        "luigi_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.CHESTPLATE.getDurability(10))));
        LUIGI_ICE_PANTS = registerItem("luigi_ice_pants",
                () -> new CostumeItem(Ingredient.of(ItemRegistry.ICE_FLOWER), ArmorMaterials.LEATHER, ArmorItem.Type.LEGGINGS,
                        "luigi_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.LEGGINGS.getDurability(10))));
        LUIGI_ICE_SHOES = registerItem("luigi_ice_shoes",
                () -> new CostumeItem(Ingredient.of(ItemRegistry.ICE_FLOWER), ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS,
                        "luigi_costume", 4, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.BOOTS.getDurability(10))));
        PEACH_ICE_BODICE = registerItem("peach_ice_bodice",
                () -> new CostumeItem(Ingredient.of(ItemRegistry.ICE_FLOWER), ArmorMaterials.LEATHER, ArmorItem.Type.CHESTPLATE,
                        "peach_costume", 5, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.CHESTPLATE.getDurability(10))));
        PEACH_ICE_DRESS = registerItem("peach_ice_dress",
                () -> new CostumeItem(Ingredient.of(ItemRegistry.ICE_FLOWER), ArmorMaterials.LEATHER, ArmorItem.Type.LEGGINGS,
                        "peach_costume", 5, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.LEGGINGS.getDurability(10))));
        PEACH_ICE_SHOES = registerItem("peach_ice_shoes",
                () -> new CostumeItem(Ingredient.of(ItemRegistry.ICE_FLOWER), ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS,
                        "peach_costume", 5, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.BOOTS.getDurability(10))));

        BOWSER_BANNER_PATTERN = registerItem("bowser_banner_pattern",
                () -> new BannerPatternItem(TagRegistry.BOWSER_BANNER_PATTERN, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
        BOWSER_POTTERY_SHERD = registerItem("bowser_pottery_sherd",
                () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

        PLUMBER_BANNER_PATTERN = registerItem("plumber_banner_pattern",
                () -> new BannerPatternItem(TagRegistry.PLUMBER_BANNER_PATTERN, new Item.Properties().stacksTo(1)));
        PLUMBER_POTTERY_SHERD = registerItem("plumber_pottery_sherd",
                () -> new Item(new Item.Properties()));

        BOO_SPAWN_EGG = registerItem("boo_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.BOO, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        DRY_BONES_SPAWN_EGG = registerItem("dry_bones_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.DRY_BONES, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        FIRE_GOOMBA_SPAWN_EGG = registerItem("fire_goomba_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.FIRE_GOOMBA, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        GOLD_KOOPA_TROOPA_SPAWN_EGG = registerItem("gold_koopa_troopa_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.GOLD_KOOPA_TROOPA, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        GOOMBA_SPAWN_EGG = registerItem("goomba_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.GOOMBA, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        GREEN_KOOPA_TROOPA_SPAWN_EGG = registerItem("green_koopa_troopa_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.GREEN_KOOPA_TROOPA, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        HEFTY_GOOMBA_SPAWN_EGG = registerItem("hefty_goomba_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.HEFTY_GOOMBA, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        MEGA_GOOMBA_SPAWN_EGG = registerItem("mega_goomba_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.MEGA_GOOMBA, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        MINI_GOOMBA_SPAWN_EGG = registerItem("mini_goomba_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.MINI_GOOMBA, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        PIRANHA_PLANT_SPAWN_EGG = registerItem("piranha_plant_spawn_egg",
                () -> new BetterSpawnEggItem(EntityRegistry.PIRANHA_PLANT, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        POKEY_SPAWN_EGG = registerItem("pokey_spawn_egg",
                () -> new PokeySpawnEggItem(EntityRegistry.POKEY_BODY, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        RED_KOOPA_TROOPA_SPAWN_EGG = registerItem("red_koopa_troopa_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.RED_KOOPA_TROOPA, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        SNOW_POKEY_SPAWN_EGG = registerItem("snow_pokey_spawn_egg",
                () -> new SnowPokeySpawnEggItem(EntityRegistry.SNOW_POKEY_BODY, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        SPLUNKIN_SPAWN_EGG = registerItem("splunkin_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.SPLUNKIN, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
    }

    public static <T extends Item> DeferredItem<T> registerItem(String name, Supplier<T> item) {
        return Marioverse.ITEMS.register(name, item);
    }

    public static void init() {}
}
