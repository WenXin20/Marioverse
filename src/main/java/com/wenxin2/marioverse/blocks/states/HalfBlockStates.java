package com.wenxin2.marioverse.blocks.states;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum HalfBlockStates implements StringRepresentable
{
    TOP,
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
        else return "bottom";
    }
}
