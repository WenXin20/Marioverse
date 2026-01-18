package com.wenxin2.marioverse.integration.rei_compat;

import com.wenxin2.marioverse.data.WarpTrapDoorRecipe;
import com.wenxin2.marioverse.event_handlers.RegistryEventHandlers;
import java.util.List;
import java.util.Optional;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.minecraft.world.level.ItemLike;

public class WarpTrapDoorCraftingDisplay extends DefaultCraftingDisplay<WarpTrapDoorRecipe> {
    public WarpTrapDoorCraftingDisplay() {
        super(List.of(EntryIngredients.ofItems(RegistryEventHandlers.WARP_TRAPDOORS.keySet()
                                .stream().map(block -> (ItemLike) block).toList()),
                        EntryIngredients.ofTag(WarpTrapDoorRecipe.INGREDIENTS,
                                holder -> EntryStacks.of(holder.value()))),

                List.of(EntryIngredients.ofItems(RegistryEventHandlers.WARP_TRAPDOORS.values()
                        .stream().map(block -> (ItemLike) block).toList())),

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
