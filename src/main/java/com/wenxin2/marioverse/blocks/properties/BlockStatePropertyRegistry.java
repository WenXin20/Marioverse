package com.wenxin2.marioverse.blocks.properties;

import com.wenxin2.marioverse.blocks.states.HalfBlockStates;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class BlockStatePropertyRegistry {
    public static final BooleanProperty DISGUISED = BooleanProperty.create("disguised");
    public static final EnumProperty<HalfBlockStates> HALF = EnumProperty.create("half", HalfBlockStates.class);
    public static final BooleanProperty INVISIBLE = BooleanProperty.create("invisible");
    public static final BooleanProperty TALL = BooleanProperty.create("tall");
    public static final BooleanProperty TOP = BooleanProperty.create("top");
}
