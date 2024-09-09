package com.wenxin2.marioverse.items;

import java.util.function.Supplier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

public class BasePowerUpItem extends DeferredSpawnEggItem {
    public BasePowerUpItem(Supplier<? extends EntityType<? extends Mob>> entityType, int primaryColor, int secondaryColor, Properties properties) {
        super(entityType, primaryColor, secondaryColor, properties);
    }
}
