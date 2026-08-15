package com.wenxin2.marioverse.registries;

import net.minecraft.world.level.block.state.properties.WoodType;

public class WoodTypeRegistry {
    public static final WoodType MUSHROOT;

    static {
        MUSHROOT = WoodType.register(new WoodType("mushroot", BlockSetTypeRegistry.MUSHROOT));
    }
}