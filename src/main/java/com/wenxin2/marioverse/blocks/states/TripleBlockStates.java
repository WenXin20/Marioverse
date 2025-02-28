package com.wenxin2.marioverse.blocks.states;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum TripleBlockStates implements StringRepresentable
{
    TOP,
    MIDDLE,
    BOTTOM;

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
        else return "bottom";
    }
}
