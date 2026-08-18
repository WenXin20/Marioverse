package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.world.level.block.state.properties.WoodType;

public class WoodTypeRegistry {
    public static final WoodType MUSHROOT;

    static {
        MUSHROOT = WoodType.register(new WoodType(Marioverse.MOD_ID + ":mushroot", BlockSetTypeRegistry.MUSHROOT));
    }
}