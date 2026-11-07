package com.wenxin2.marioverse.blocks.properties;

import com.wenxin2.marioverse.blocks.states.ArrowDirection;
import com.wenxin2.marioverse.blocks.states.ColumnBlockStates;
import com.wenxin2.marioverse.blocks.states.ConnectionBlockStates;
import com.wenxin2.marioverse.blocks.states.HalfBlockStates;
import com.wenxin2.marioverse.blocks.states.SideBlockStates;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class BlockStatePropertyRegistry {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final BooleanProperty BOARD = BooleanProperty.create("board");
    public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");
    public static final BooleanProperty DISGUISED = BooleanProperty.create("disguised");
    public static final BooleanProperty END = BooleanProperty.create("end");
    public static final BooleanProperty FLOWERS = BooleanProperty.create("flowers");
    public static final BooleanProperty INVISIBLE = BooleanProperty.create("invisible");
    public static final BooleanProperty POST = BooleanProperty.create("post");
    public static final BooleanProperty TALL = BooleanProperty.create("tall");
    public static final BooleanProperty TOP = BooleanProperty.create("top");
    public static final EnumProperty<ConnectionBlockStates> ABOVE = EnumProperty
            .create("above", ConnectionBlockStates.class);
    public static final EnumProperty<ArrowDirection> ARROW_DIRECTION = EnumProperty
            .create("arrow_direction", ArrowDirection.class);
    public static final EnumProperty<ConnectionBlockStates> BELOW = EnumProperty
            .create("below", ConnectionBlockStates.class);
    public static final EnumProperty<ColumnBlockStates> COLUMN = EnumProperty
            .create("column", ColumnBlockStates.class);
    public static final EnumProperty<HalfBlockStates> HALF = EnumProperty
            .create("half", HalfBlockStates.class);
    public static final EnumProperty<ConnectionBlockStates> NEGATIVE = EnumProperty
            .create("negative", ConnectionBlockStates.class);
    public static final EnumProperty<ConnectionBlockStates> POSITIVE = EnumProperty
            .create("positive", ConnectionBlockStates.class);
    public static final EnumProperty<SideBlockStates> SIDE = EnumProperty
            .create("side", SideBlockStates.class);
    public static final IntegerProperty FLOWER_AMOUNT = IntegerProperty.create("flower_amount", 1, 3);
    public static final IntegerProperty HEIGHT_8 = IntegerProperty.create("height", 0, 8);
}
