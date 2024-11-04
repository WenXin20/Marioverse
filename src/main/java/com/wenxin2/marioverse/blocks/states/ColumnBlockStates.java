package com.wenxin2.marioverse.blocks.states;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum ColumnBlockStates implements StringRepresentable
{
    TOP,
    MIDDLE,
    BOTTOM,
    NONE;

    public String toString()
    {
        return this.getSerializedName();
    }

    @NotNull
    public String getSerializedName()
    {
        if (this == TOP)
            return "top";
        else if (this == MIDDLE)
            return "middle";
        else if (this == BOTTOM)
            return "bottom";
        return "none";
    }
}
