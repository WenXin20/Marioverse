package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.inventory.WarpPipeMenu;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class MenuRegistry {
    public static final DeferredHolder<MenuType<?>, MenuType<WarpPipeMenu>> WARP_PIPE_MENU;

    static
    {
        WARP_PIPE_MENU = Marioverse.MENUS.register("warp_pipe", () -> new MenuType<>(WarpPipeMenu::new, FeatureFlags.REGISTRY.allFlags()));
    }

    public static void init()
    {}
}
