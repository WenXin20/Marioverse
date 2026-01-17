package com.wenxin2.marioverse.integration.rei_compat;

import com.wenxin2.marioverse.data.WarpDoorRecipe;
import com.wenxin2.marioverse.event_handlers.RegistryEventHandlers;
import java.util.List;
import java.util.Optional;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.minecraft.world.level.ItemLike;

public class WarpDoorCraftingDisplay extends DefaultCraftingDisplay<WarpDoorRecipe> {
    public WarpDoorCraftingDisplay() {
        super(List.of(EntryIngredients.ofItems(RegistryEventHandlers.WARP_DOORS.keySet()
                                .stream().map(block -> (ItemLike) block).toList()),
                        EntryIngredients.ofTag(WarpDoorRecipe.INGREDIENTS,
                                holder -> EntryStacks.of(holder.value()))),

                RegistryEventHandlers.WARP_DOORS.values().stream().map(EntryIngredients::of).toList(),

                Optional.empty()
        );
    }

    @Override
    public int getWidth() {
        return 2;
    }

    @Override
    public int getHeight() {
        return 2;
    }

    @Override
    public boolean isShapeless() {
        return true;
    }
}
