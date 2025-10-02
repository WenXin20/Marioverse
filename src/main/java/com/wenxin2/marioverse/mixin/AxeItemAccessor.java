package com.wenxin2.marioverse.mixin;

import java.util.Map;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AxeItem.class)
public class AxeItemAccessor {
    @Accessor("STRIPPABLES")
    public static Map<Block, Block> getStrippables() { throw new AssertionError(); }

    @Accessor("STRIPPABLES")
    public static void setStrippables(Map<Block, Block> map) { throw new AssertionError(); }
}
