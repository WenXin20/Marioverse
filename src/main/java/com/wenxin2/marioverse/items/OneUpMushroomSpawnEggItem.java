package com.wenxin2.marioverse.items;

import java.util.function.Supplier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

public class OneUpMushroomSpawnEggItem extends PowerUpSpawnEggItem {
    public OneUpMushroomSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> entityType,
                                     int primaryColor, int secondaryColor, Properties properties) {
        super(entityType, primaryColor, secondaryColor, properties);
    }
}
