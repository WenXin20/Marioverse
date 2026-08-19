package com.wenxin2.marioverse.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class WoodTypeChestBoat extends ChestBoat {
    private final Item dropItem;

    public WoodTypeChestBoat(EntityType<? extends ChestBoat> entityType, Level level, Item dropItem) {
        super(entityType, level);
        this.dropItem = dropItem;
    }

    @NotNull
    @Override
    public Item getDropItem() {
        return this.dropItem;
    }
}
