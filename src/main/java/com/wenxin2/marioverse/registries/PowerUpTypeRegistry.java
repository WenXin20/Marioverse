package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.power_up.PowerUpType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NewRegistryEvent;

public class PowerUpTypeRegistry {
    public static final DeferredHolder<PowerUpType, PowerUpType> DASH_MUSHROOM;
    public static final DeferredHolder<PowerUpType, PowerUpType> FIRE_FLOWER;
    public static final DeferredHolder<PowerUpType, PowerUpType> ICE_FLOWER;
    public static final DeferredHolder<PowerUpType, PowerUpType> MEGA_MUSHROOM;
    public static final DeferredHolder<PowerUpType, PowerUpType> MINI_MUSHROOM;
    public static final DeferredHolder<PowerUpType, PowerUpType> ONE_UP_MUSHROOM;
    public static final DeferredHolder<PowerUpType, PowerUpType> SUPER_MUSHROOM;
    public static final DeferredHolder<PowerUpType, PowerUpType> SUPER_STAR;

    static {
        DASH_MUSHROOM = Marioverse.POWER_UP_TYPES.register("dash_mushroom", PowerUpType::new);
        FIRE_FLOWER = Marioverse.POWER_UP_TYPES.register("fire_flower", PowerUpType::new);
        ICE_FLOWER = Marioverse.POWER_UP_TYPES.register("ice_flower", PowerUpType::new);
        MEGA_MUSHROOM = Marioverse.POWER_UP_TYPES.register("mega_mushroom", PowerUpType::new);
        MINI_MUSHROOM = Marioverse.POWER_UP_TYPES.register("mini_mushroom", PowerUpType::new);
        ONE_UP_MUSHROOM = Marioverse.POWER_UP_TYPES.register("one_up_mushroom", PowerUpType::new);
        SUPER_MUSHROOM = Marioverse.POWER_UP_TYPES.register("super_mushroom", PowerUpType::new);
        SUPER_STAR = Marioverse.POWER_UP_TYPES.register("super_star", PowerUpType::new);
    }

    public static void registerRegistry(NewRegistryEvent event) {
        event.register(Marioverse.POWER_UP_REGISTRY);
    }
}