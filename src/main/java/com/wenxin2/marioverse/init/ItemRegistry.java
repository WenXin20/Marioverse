package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.items.BasePowerUpItem;
import com.wenxin2.marioverse.items.FireCostumeItem;
import com.wenxin2.marioverse.items.OneUpMushroomItem;
import com.wenxin2.marioverse.items.WarpDoorItem;
import com.wenxin2.marioverse.items.WrenchItem;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.DoorBlock;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;

public class ItemRegistry {
    public static final Map<String, DeferredItem<Item>> WARP_DOORS = new HashMap<>();

    public static final DeferredItem<Item> FIRE_FLOWER;
    public static final DeferredItem<Item> FIRE_GOOMBA_SPAWN_EGG;
    public static final DeferredItem<Item> FIRE_HAT;
    public static final DeferredItem<Item> FIRE_OVERALLS;
    public static final DeferredItem<Item> FIRE_SHIRT;
    public static final DeferredItem<Item> FIRE_SHOES;
    public static final DeferredItem<Item> GOOMBA_SPAWN_EGG;
    public static final DeferredItem<Item> HEFTY_GOOMBA_SPAWN_EGG;
    public static final DeferredItem<Item> MEGA_GOOMBA_SPAWN_EGG;
    public static final DeferredItem<Item> MINI_GOOMBA_SPAWN_EGG;
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
                () -> new DeferredSpawnEggItem(EntityRegistry.FIRE_GOOMBA, 0xB2333C, 0xF9A728, new Item.Properties()));
        GOOMBA_SPAWN_EGG = registerItem("goomba_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.GOOMBA, 0xCC5F51, 0xF7CDA5, new Item.Properties()));
        HEFTY_GOOMBA_SPAWN_EGG = registerItem("hefty_goomba_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.HEFTY_GOOMBA, 0xCC5F51, 0xF7CDA5, new Item.Properties()));
        MEGA_GOOMBA_SPAWN_EGG = registerItem("mega_goomba_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.MEGA_GOOMBA, 0xCC5F51, 0xF7CDA5, new Item.Properties()));
        MINI_GOOMBA_SPAWN_EGG = registerItem("mini_goomba_spawn_egg",
                () -> new DeferredSpawnEggItem(EntityRegistry.MINI_GOOMBA, 0xCC5F51, 0xF7CDA5, new Item.Properties()));

//        BuiltInRegistries.ITEM.stream()
//                .filter(item -> (item instanceof DoubleHighBlockItem blockItem && blockItem.getBlock() instanceof DoorBlock) && !(item instanceof WarpDoorItem)) // Only process DoorBlock instances
//                .forEach(door -> registerWarpDoor((DoubleHighBlockItem) door));
    }

    public static <T extends Item> DeferredItem<T> registerItem(String name, Supplier<T> item) {
        return Marioverse.ITEMS.register(name, item);
    }

    private static void registerWarpDoor(DoubleHighBlockItem baseItem) {
        ResourceLocation location = BuiltInRegistries.ITEM.getKey(baseItem);
        String path = location.getPath();


        String modifiedPath;
        if (path.endsWith("_door")) {
            int splitIndex = path.lastIndexOf("_door");
            modifiedPath = path.substring(0, splitIndex) + "_warp" + path.substring(splitIndex);
        } else {
            // Fallback if the path does not end with "_door"
            modifiedPath = "warp_" + path;
        }

        String name = location.getNamespace().equals("minecraft")
                ? modifiedPath
                : location.getNamespace() + "_" + modifiedPath;

        WARP_DOORS.put(name, registerItem(name,
                () -> new WarpDoorItem(BlockRegistry.WARP_DOORS.get(name).get(), new Item.Properties(), baseItem)));
    }

    public static void init()
    {}
}
