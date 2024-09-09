package com.wenxin2.marioverse.items;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

public class BasePowerUpItem extends SpawnEggItem {
    /**
     * @param entityType
     * @param primaryColor
     * @param secondaryColor
     * @param properties
     * @deprecated Forge: Use {@link DeferredSpawnEggItem} instead for suppliers
     */
    public BasePowerUpItem(EntityType<? extends Mob> entityType, int primaryColor, int secondaryColor, Properties properties) {
        super(entityType, primaryColor, secondaryColor, properties);
    }
}
