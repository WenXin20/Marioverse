package com.wenxin2.marioverse.integration;

import com.wenxin2.marioverse.integration.rei_compat.WarpDoorCraftingDisplay;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public class REICompat implements REIClientPlugin {
    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.add(new WarpDoorCraftingDisplay());
    }
}
