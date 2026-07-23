package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.power_up.PowerUpType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NewRegistryEvent;

public class PowerUpTypeRegistry {
    public static final DeferredHolder<PowerUpType, PowerUpType> FIRE_FLOWER = Marioverse.POWER_UP_TYPES
            .register("fire_flower", PowerUpType::new);
    public static final DeferredHolder<PowerUpType, PowerUpType> ICE_FLOWER = Marioverse.POWER_UP_TYPES
            .register("ice_flower", PowerUpType::new);

    public static void registerRegistry(NewRegistryEvent event) {
        event.register(Marioverse.POWER_UP_REGISTRY);
    }
}