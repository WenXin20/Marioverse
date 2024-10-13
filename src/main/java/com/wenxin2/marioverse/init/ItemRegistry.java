package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.items.BasePowerUpItem;
import com.wenxin2.marioverse.items.FireCostumeItem;
import com.wenxin2.marioverse.items.OneUpMushroomItem;
import com.wenxin2.marioverse.items.WrenchItem;
import java.util.function.Supplier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;

public class ItemRegistry {
    public static final DeferredItem<Item> FIRE_FLOWER;
    public static final DeferredItem<Item> FIRE_GOOMBA_SPAWN_EGG;
    public static final DeferredItem<Item> FIRE_HAT;
    public static final DeferredItem<Item> FIRE_OVERALLS;
    public static final DeferredItem<Item> FIRE_SHIRT;
    public static final DeferredItem<Item> FIRE_SHOES;
    public static final DeferredItem<Item> GOOMBA_SPAWN_EGG;
    public static final DeferredItem<Item> MUSHROOM;
    public static final DeferredItem<Item> ONE_UP_MUSHROOM;
    public static final DeferredItem<Item> PIPE_WRENCH;

    static {
        PIPE_WRENCH = registerItem("pipe_wrench",
                () -> new WrenchItem(new Item.Properties()
                        .attributes(WrenchItem.createAttributes(Tiers.IRON, 3, -3.2F))
                        .durability(128), Tiers.IRON));
        MUSHROOM = registerItem("mushroom",
                () -> new BasePowerUpItem(EntityRegistry.MUSHROOM, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        ONE_UP_MUSHROOM = registerItem("one_up_mushroom",
                () -> new OneUpMushroomItem(EntityRegistry.ONE_UP_MUSHROOM, 0xFFFFFF, 0xFFFFFF, new Item.Properties().stacksTo(1)));

        FIRE_FLOWER = registerItem("fire_flower",
                () -> new BasePowerUpItem(EntityRegistry.FIRE_FLOWER, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        FIRE_HAT = registerItem("fire_hat",
                () -> new FireCostumeItem(ArmorMaterials.LEATHER, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1)));
        FIRE_SHIRT = registerItem("fire_shirt",
                () -> new FireCostumeItem(ArmorMaterials.LEATHER, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1)));
        FIRE_OVERALLS = registerItem("fire_overalls",
                () -> new FireCostumeItem(ArmorMaterials.LEATHER, ArmorItem.Type.LEGGINGS, new Item.Properties().stacksTo(1)));
        FIRE_SHOES = registerItem("fire_shoes",
                () -> new FireCostumeItem(ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS, new Item.Properties().stacksTo(1)));

        FIRE_GOOMBA_SPAWN_EGG = registerItem("fire_goomba_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.FIRE_GOOMBA, 0x713030, 0xEAABB1, new Item.Properties()));
        GOOMBA_SPAWN_EGG = registerItem("goomba_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.GOOMBA, 0xCC5F51, 0xF7CDA5, new Item.Properties()));
    }

    public static <T extends Item> DeferredItem<T> registerItem(String name, Supplier<T> item)
    {
        return Marioverse.ITEMS.register(name, item);
    }

    public static void init()
    {}
}
