package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.items.BasePowerUpItem;
import com.wenxin2.marioverse.items.BetterSpawnEggItem;
import com.wenxin2.marioverse.items.FireCostumeItem;
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
    public static final DeferredItem<Item> FIRE_FLOWER;
    public static final DeferredItem<Item> FIRE_GOOMBA_SPAWN_EGG;
    public static final DeferredItem<Item> FIRE_HAT;
    public static final DeferredItem<Item> FIRE_PANTS;
    public static final DeferredItem<Item> FIRE_SHIRT;
    public static final DeferredItem<Item> FIRE_SHOES;
    public static final DeferredItem<Item> GOOMBA_SPAWN_EGG;
    public static final DeferredItem<Item> HEFTY_GOOMBA_SPAWN_EGG;
    public static final DeferredItem<Item> ICE_FLOWER;
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

        FIRE_FLOWER = registerItem("fire_flower",
                () -> new BasePowerUpItem(EntityRegistry.FIRE_FLOWER, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        FIRE_HAT = registerItem("fire_hat",
                () -> new FireCostumeItem(ArmorMaterials.LEATHER, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1)));
        FIRE_SHIRT = registerItem("fire_shirt",
                () -> new FireCostumeItem(ArmorMaterials.LEATHER, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1)));
        FIRE_PANTS = registerItem("fire_pants",
                () -> new FireCostumeItem(ArmorMaterials.LEATHER, ArmorItem.Type.LEGGINGS, new Item.Properties().stacksTo(1)));
        FIRE_SHOES = registerItem("fire_shoes",
                () -> new FireCostumeItem(ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS, new Item.Properties().stacksTo(1)));

        ICE_FLOWER = registerItem("ice_flower",
                () -> new BasePowerUpItem(EntityRegistry.ICE_FLOWER, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));

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
