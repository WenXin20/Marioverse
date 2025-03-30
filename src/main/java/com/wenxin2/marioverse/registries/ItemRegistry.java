package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.items.BasePowerUpItem;
import com.wenxin2.marioverse.items.BetterSpawnEggItem;
import com.wenxin2.marioverse.items.CharacterSmithingTemplateItem;
import com.wenxin2.marioverse.items.FireCostumeItem;
import com.wenxin2.marioverse.items.IceCostumeItem;
import com.wenxin2.marioverse.items.LuigiCostumeItem;
import com.wenxin2.marioverse.items.MarioCostumeItem;
import com.wenxin2.marioverse.items.OneUpMushroomItem;
import com.wenxin2.marioverse.items.WarpDisruptorItem;
import com.wenxin2.marioverse.items.WrenchItem;
import java.util.function.Supplier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;

public class ItemRegistry {
    public static final DeferredItem<Item> BOWSER_BANNER_PATTERN;
    public static final DeferredItem<Item> BOWSER_POTTERY_SHERD;
    public static final DeferredItem<Item> FIRE_COSTUME_SMITHING_TEMPLATE;
    public static final DeferredItem<Item> FIRE_FLOWER;
    public static final DeferredItem<Item> FIRE_GOOMBA_SPAWN_EGG;
    public static final DeferredItem<Item> GOOMBA_SPAWN_EGG;
    public static final DeferredItem<Item> HEFTY_GOOMBA_SPAWN_EGG;
    public static final DeferredItem<Item> ICE_COSTUME_SMITHING_TEMPLATE;
    public static final DeferredItem<Item> ICE_FLOWER;
    public static final DeferredItem<Item> LUIGI_COSTUME_SMITHING_TEMPLATE;
    public static final DeferredItem<Item> LUIGI_HAT;
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
    public static final DeferredItem<Item> MUSHROOM;
    public static final DeferredItem<Item> ONE_UP_MUSHROOM;
    public static final DeferredItem<Item> PIPE_WRENCH;
    public static final DeferredItem<Item> PIRANHA_PLANT_SPAWN_EGG;
    public static final DeferredItem<Item> PLUMBER_BANNER_PATTERN;
    public static final DeferredItem<Item> PLUMBER_POTTERY_SHERD;
    public static final DeferredItem<Item> SUPER_STAR;
    public static final DeferredItem<Item> WARP_DISRUPTOR;

    static {
        PIPE_WRENCH = registerItem("pipe_wrench",
                () -> new WrenchItem(new Item.Properties()
                        .attributes(WrenchItem.createAttributes(Tiers.IRON, 3, -3.2F))
                        .durability(128), Tiers.IRON));
        MUSHROOM = registerItem("mushroom",
                () -> new BasePowerUpItem(EntityRegistry.MUSHROOM, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        ONE_UP_MUSHROOM = registerItem("one_up_mushroom",
                () -> new OneUpMushroomItem(EntityRegistry.ONE_UP_MUSHROOM, 0xFFFFFF, 0xFFFFFF, new Item.Properties().stacksTo(1)));
        SUPER_STAR = registerItem("super_star",
                () -> new BasePowerUpItem(EntityRegistry.SUPER_STAR, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));

        WARP_DISRUPTOR = registerItem("warp_disruptor",
                () -> new WarpDisruptorItem(new Item.Properties().durability(128)));

        MARIO_COSTUME_SMITHING_TEMPLATE = registerItem("mario_costume_smithing_template",
                CharacterSmithingTemplateItem::createCharacterUpgradeTemplate);
        LUIGI_COSTUME_SMITHING_TEMPLATE = registerItem("luigi_costume_smithing_template",
                CharacterSmithingTemplateItem::createCharacterUpgradeTemplate);
        FIRE_COSTUME_SMITHING_TEMPLATE = registerItem("fire_costume_smithing_template",
                CharacterSmithingTemplateItem::createFireUpgradeTemplate);
        ICE_COSTUME_SMITHING_TEMPLATE = registerItem("ice_costume_smithing_template",
                CharacterSmithingTemplateItem::createIceUpgradeTemplate);

        MARIO_HAT = registerItem("mario_hat",
                () -> new MarioCostumeItem(ArmorMaterials.LEATHER, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.HELMET.getDurability(10))));
        MARIO_SHIRT = registerItem("mario_shirt",
                () -> new MarioCostumeItem(ArmorMaterials.LEATHER, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.CHESTPLATE.getDurability(10))));
        MARIO_PANTS = registerItem("mario_pants",
                () -> new MarioCostumeItem(ArmorMaterials.LEATHER, ArmorItem.Type.LEGGINGS, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.LEGGINGS.getDurability(10))));
        MARIO_SHOES = registerItem("mario_shoes",
                () -> new MarioCostumeItem(ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.BOOTS.getDurability(10))));

        LUIGI_HAT = registerItem("luigi_hat",
                () -> new LuigiCostumeItem(ArmorMaterials.LEATHER, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.HELMET.getDurability(10))));
        LUIGI_SHIRT = registerItem("luigi_shirt",
                () -> new LuigiCostumeItem(ArmorMaterials.LEATHER, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.CHESTPLATE.getDurability(10))));
        LUIGI_PANTS = registerItem("luigi_pants",
                () -> new LuigiCostumeItem(ArmorMaterials.LEATHER, ArmorItem.Type.LEGGINGS, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.LEGGINGS.getDurability(10))));
        LUIGI_SHOES = registerItem("luigi_shoes",
                () -> new LuigiCostumeItem(ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.BOOTS.getDurability(10))));

        FIRE_FLOWER = registerItem("fire_flower",
                () -> new BasePowerUpItem(EntityRegistry.FIRE_FLOWER, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        MARIO_FIRE_HAT = registerItem("mario_fire_hat",
                () -> new FireCostumeItem(ArmorMaterials.LEATHER, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.HELMET.getDurability(10))));
        MARIO_FIRE_SHIRT = registerItem("mario_fire_shirt",
                () -> new FireCostumeItem(ArmorMaterials.LEATHER, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.CHESTPLATE.getDurability(10))));
        MARIO_FIRE_PANTS = registerItem("mario_fire_pants",
                () -> new FireCostumeItem(ArmorMaterials.LEATHER, ArmorItem.Type.LEGGINGS, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.LEGGINGS.getDurability(10))));
        MARIO_FIRE_SHOES = registerItem("mario_fire_shoes",
                () -> new FireCostumeItem(ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.BOOTS.getDurability(10))));

        ICE_FLOWER = registerItem("ice_flower",
                () -> new BasePowerUpItem(EntityRegistry.ICE_FLOWER, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        MARIO_ICE_HAT = registerItem("mario_ice_hat",
                () -> new IceCostumeItem(ArmorMaterials.LEATHER, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.HELMET.getDurability(10))));
        MARIO_ICE_SHIRT = registerItem("mario_ice_shirt",
                () -> new IceCostumeItem(ArmorMaterials.LEATHER, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.CHESTPLATE.getDurability(10))));
        MARIO_ICE_PANTS = registerItem("mario_ice_pants",
                () -> new IceCostumeItem(ArmorMaterials.LEATHER, ArmorItem.Type.LEGGINGS, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.LEGGINGS.getDurability(10))));
        MARIO_ICE_SHOES = registerItem("mario_ice_shoes",
                () -> new IceCostumeItem(ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS, new Item.Properties().stacksTo(1)
                        .durability(ArmorItem.Type.BOOTS.getDurability(10))));

        BOWSER_BANNER_PATTERN = registerItem("bowser_banner_pattern",
                () -> new BannerPatternItem(TagRegistry.BOWSER_BANNER_PATTERN, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
        BOWSER_POTTERY_SHERD = registerItem("bowser_pottery_sherd",
                () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

        PLUMBER_BANNER_PATTERN = registerItem("plumber_banner_pattern",
                () -> new BannerPatternItem(TagRegistry.PLUMBER_BANNER_PATTERN, new Item.Properties().stacksTo(1)));
        PLUMBER_POTTERY_SHERD = registerItem("plumber_pottery_sherd",
                () -> new Item(new Item.Properties()));

        FIRE_GOOMBA_SPAWN_EGG = registerItem("fire_goomba_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.FIRE_GOOMBA, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        GOOMBA_SPAWN_EGG = registerItem("goomba_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.GOOMBA, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        HEFTY_GOOMBA_SPAWN_EGG = registerItem("hefty_goomba_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.HEFTY_GOOMBA, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        MEGA_GOOMBA_SPAWN_EGG = registerItem("mega_goomba_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.MEGA_GOOMBA, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        MINI_GOOMBA_SPAWN_EGG = registerItem("mini_goomba_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.MINI_GOOMBA, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        PIRANHA_PLANT_SPAWN_EGG = registerItem("piranha_plant_spawn_egg",
                () -> new BetterSpawnEggItem(EntityRegistry.PIRANHA_PLANT, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
    }

    public static <T extends Item> DeferredItem<T> registerItem(String name, Supplier<T> item) {
        return Marioverse.ITEMS.register(name, item);
    }

    public static void init()
    {}
}
