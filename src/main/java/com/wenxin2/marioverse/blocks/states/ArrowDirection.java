package com.wenxin2.marioverse.blocks.states;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum ArrowDirection implements StringRepresentable {
    UP("up"), TOP_RIGHT("top_right"), RIGHT("right"), BOTTOM_RIGHT("bottom_right"),
    DOWN("down"), BOTTOM_LEFT("bottom_left"), LEFT("left"), TOP_LEFT("top_left"),
    NONE("none");

    private static final ArrowDirection[] DIRECTIONAL = {
            UP, TOP_RIGHT, RIGHT, BOTTOM_RIGHT, DOWN, BOTTOM_LEFT, LEFT, TOP_LEFT
    };

    private final String name;

    ArrowDirection(String name) {
        this.name = name;
    }

    public ArrowDirection next() {
        if (this == NONE)
            return UP;

        int index = 0;
        for (int i = 0; i < DIRECTIONAL.length; i++) {
            if (DIRECTIONAL[i] == this) {
                index = i;
                break;
            }
        }
        return DIRECTIONAL[(index + 1) % DIRECTIONAL.length];
    }

    public ArrowDirection previous() {
        if (this == NONE)
            return TOP_LEFT;

        int index = 0;
        for (int i = 0; i < DIRECTIONAL.length; i++) {
            if (DIRECTIONAL[i] == this) {
                index = i;
                break;
            }
        }
        return DIRECTIONAL[(index - 1 + DIRECTIONAL.length) % DIRECTIONAL.length];
    }

    @NotNull
    @Override
    public String getSerializedName() {
        return this.name;
    }
}
