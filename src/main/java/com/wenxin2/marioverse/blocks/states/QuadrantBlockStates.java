package com.wenxin2.marioverse.blocks.states;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum QuadrantBlockStates implements StringRepresentable
{
    NORTH_EAST,
    NORTH_WEST,
    SOUTH_EAST,
    SOUTH_WEST;

    public String toString()
    {
        return this.getSerializedName();
    }

    @NotNull
    public String getSerializedName()
    {
        if (this == NORTH_EAST)
            return "north_east";
        else if (this == NORTH_WEST)
            return "north_west";
        else if (this == SOUTH_EAST)
            return "south_east";
        else return "south_west";
    }
}
