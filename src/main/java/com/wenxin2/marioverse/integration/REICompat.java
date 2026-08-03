package com.wenxin2.marioverse.integration;

import com.wenxin2.marioverse.data.HexColorShapedRecipe;
import com.wenxin2.marioverse.integration.rei_compat.HexColorCraftingDisplay;
import com.wenxin2.marioverse.integration.rei_compat.HexColorRecipeTransferHandler;
import com.wenxin2.marioverse.integration.rei_compat.WarpDoorCraftingDisplay;
import com.wenxin2.marioverse.integration.rei_compat.WarpTrapDoorCraftingDisplay;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.forge.REIPluginClient;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;
import net.minecraft.world.item.crafting.RecipeType;

@REIPluginClient
public class REICompat implements REIClientPlugin {
    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(HexColorShapedRecipe.class,
                RecipeType.CRAFTING, HexColorCraftingDisplay::new);
        registry.add(new WarpDoorCraftingDisplay());
        registry.add(new WarpTrapDoorCraftingDisplay());
    }

    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry) {
        registry.register(new HexColorRecipeTransferHandler(BuiltinPlugin.CRAFTING));
    }
}
