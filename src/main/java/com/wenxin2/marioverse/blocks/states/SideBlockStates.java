package com.wenxin2.marioverse.blocks.states;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum SideBlockStates implements StringRepresentable
{
    LEFT,
    RIGHT;

    public String toString()
    {
        return this.getSerializedName();
    }

    @NotNull
    public String getSerializedName()
    {
        if (this == LEFT)
            return "left";
        else return "right";
    }
}
