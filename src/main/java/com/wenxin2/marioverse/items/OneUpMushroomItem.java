package com.wenxin2.marioverse.items;

import java.util.function.Supplier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

public class OneUpMushroomItem extends BasePowerUpItem {
    public OneUpMushroomItem(Supplier<? extends EntityType<? extends Mob>> entityType,
                             int primaryColor, int secondaryColor, Properties properties) {
        super(entityType, primaryColor, secondaryColor, properties);
    }
}
