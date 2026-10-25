package com.wenxin2.marioverse.blocks.states;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum ConnectionBlockStates implements StringRepresentable
{
    NONE,
    FLAT,
    DIAGONAL;

    public String toString()
    {
        return this.getSerializedName();
    }

    @NotNull
    public String getSerializedName()
    {
        if (this == FLAT)
            return "flat";
        else if (this == DIAGONAL)
            return "diagonal";
        else return "none";
    }
}
