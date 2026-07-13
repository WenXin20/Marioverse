package com.wenxin2.marioverse.integration;

import com.wenxin2.marioverse.data.ColorSwappableShapedRecipe;
import com.wenxin2.marioverse.integration.rei_compat.ColorSwappableCraftingDisplay;
import com.wenxin2.marioverse.integration.rei_compat.WarpDoorCraftingDisplay;
import com.wenxin2.marioverse.integration.rei_compat.WarpTrapDoorCraftingDisplay;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.world.item.crafting.RecipeType;

@REIPluginClient
public class REICompat implements REIClientPlugin {
    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(ColorSwappableShapedRecipe.class,
                RecipeType.CRAFTING, ColorSwappableCraftingDisplay::new);
        registry.add(new WarpDoorCraftingDisplay());
        registry.add(new WarpTrapDoorCraftingDisplay());
    }
}
