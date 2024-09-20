package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.items.BasePowerUpItem;
import com.wenxin2.marioverse.items.FireFlowerCostumeItem;
import com.wenxin2.marioverse.items.OneUpMushroomItem;
import com.wenxin2.marioverse.items.WrenchItem;
import java.util.function.Supplier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.registries.DeferredItem;

public class ItemRegistry {
    public static final DeferredItem<Item> FIRE_FLOWER;
    public static final DeferredItem<Item> FIRE_FLOWER_HAT;
    public static final DeferredItem<Item> FIRE_FLOWER_SHIRT;
    public static final DeferredItem<Item> PIPE_WRENCH;
    public static final DeferredItem<Item> MUSHROOM;
    public static final DeferredItem<Item> ONE_UP_MUSHROOM;

    static
    {
        PIPE_WRENCH = registerItem("pipe_wrench",
                () -> new WrenchItem(new Item.Properties()
                        .attributes(WrenchItem.createAttributes(Tiers.IRON, 3, -3.2F))
                        .durability(128), Tiers.IRON));
        MUSHROOM = registerItem("mushroom",
                () -> new BasePowerUpItem(EntityRegistry.MUSHROOM, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        ONE_UP_MUSHROOM = registerItem("one_up_mushroom",
                () -> new OneUpMushroomItem(EntityRegistry.ONE_UP_MUSHROOM, 0xFFFFFF, 0xFFFFFF, new Item.Properties().stacksTo(8)));
        FIRE_FLOWER = registerItem("fire_flower",
                () -> new BasePowerUpItem(EntityRegistry.FIRE_FLOWER, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));
        FIRE_FLOWER_HAT = registerItem("fire_flower_hat",
                () -> new FireFlowerCostumeItem(ArmorMaterials.LEATHER, ArmorItem.Type.HELMET, new Item.Properties()));
        FIRE_FLOWER_SHIRT = registerItem("fire_flower_shirt",
                () -> new FireFlowerCostumeItem(ArmorMaterials.LEATHER, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    }

    public static <T extends Item> DeferredItem<T> registerItem(String name, Supplier<T> item)
    {
        return Marioverse.ITEMS.register(name, item);
    }

    public static void init()
    {}
}
