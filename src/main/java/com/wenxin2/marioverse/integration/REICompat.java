package com.wenxin2.marioverse.integration;

import com.wenxin2.marioverse.data.ArrowColorShapedRecipe;
import com.wenxin2.marioverse.data.ArrowColorShapelessRecipe;
import com.wenxin2.marioverse.data.ArrowSignUpgradeRecipe;
import com.wenxin2.marioverse.data.HexColorShapedRecipe;
import com.wenxin2.marioverse.data.HexColorShapelessRecipe;
import com.wenxin2.marioverse.integration.rei_compat.ArrowColorCraftingDisplay;
import com.wenxin2.marioverse.integration.rei_compat.ArrowColorRecipeTransferHandler;
import com.wenxin2.marioverse.integration.rei_compat.ArrowColorShapelessCraftingDisplay;
import com.wenxin2.marioverse.integration.rei_compat.ArrowColorShapelessRecipeTransferHandler;
import com.wenxin2.marioverse.integration.rei_compat.ArrowSignUpgradeCraftingDisplay;
import com.wenxin2.marioverse.integration.rei_compat.HexColorCraftingDisplay;
import com.wenxin2.marioverse.integration.rei_compat.HexColorRecipeTransferHandler;
import com.wenxin2.marioverse.integration.rei_compat.HexColorShapelessCraftingDisplay;
import com.wenxin2.marioverse.integration.rei_compat.HexColorShapelessRecipeTransferHandler;
import com.wenxin2.marioverse.integration.rei_compat.WarpDoorCraftingDisplay;
import com.wenxin2.marioverse.integration.rei_compat.WarpTrapDoorCraftingDisplay;
import dev.architectury.event.EventResult;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.forge.REIPluginClient;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

@REIPluginClient
public class REICompat implements REIClientPlugin {
    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(HexColorShapedRecipe.class,
                RecipeType.CRAFTING, HexColorCraftingDisplay::new);
        registry.registerRecipeFiller(HexColorShapelessRecipe.class,
                RecipeType.CRAFTING, HexColorShapelessCraftingDisplay::new);
        registry.registerRecipeFiller(ArrowColorShapedRecipe.class,
                RecipeType.CRAFTING, ArrowColorCraftingDisplay::new);
        registry.registerRecipeFiller(ArrowColorShapelessRecipe.class,
                RecipeType.CRAFTING, ArrowColorShapelessCraftingDisplay::new);
        registry.registerRecipeFiller(ArrowSignUpgradeRecipe.class,
                RecipeType.CRAFTING, ArrowSignUpgradeCraftingDisplay::new);
        registry.add(new WarpDoorCraftingDisplay());
        registry.add(new WarpTrapDoorCraftingDisplay());

        // REI's own DefaultCraftingCategory generates a plain DefaultCraftingDisplay.of(...) for every
        // RecipeType.CRAFTING recipe regardless of registerRecipeFiller - without this, our color-cycling
        // displays above show up alongside a second, static (non-cycling) generic display for the same recipe.
        registry.registerVisibilityPredicate((category, display) -> {
            if (!(display instanceof DefaultCraftingDisplay<?> craftingDisplay))
                return EventResult.pass();
            if (craftingDisplay instanceof ArrowColorCraftingDisplay || craftingDisplay instanceof ArrowColorShapelessCraftingDisplay
                    || craftingDisplay instanceof HexColorCraftingDisplay || craftingDisplay instanceof HexColorShapelessCraftingDisplay
                    || craftingDisplay instanceof ArrowSignUpgradeCraftingDisplay)
                return EventResult.pass();

            Object recipeValue = craftingDisplay.getOptionalRecipe().map(RecipeHolder::value).orElse(null);
            boolean isOurRecipe = recipeValue instanceof ArrowColorShapedRecipe || recipeValue instanceof ArrowColorShapelessRecipe
                    || recipeValue instanceof HexColorShapedRecipe || recipeValue instanceof HexColorShapelessRecipe
                    || recipeValue instanceof ArrowSignUpgradeRecipe;

            return isOurRecipe ? EventResult.interruptFalse() : EventResult.pass();
        });
    }

    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry) {
        registry.register(new HexColorRecipeTransferHandler(BuiltinPlugin.CRAFTING));
        registry.register(new HexColorShapelessRecipeTransferHandler(BuiltinPlugin.CRAFTING));
        registry.register(new ArrowColorRecipeTransferHandler(BuiltinPlugin.CRAFTING));
        registry.register(new ArrowColorShapelessRecipeTransferHandler(BuiltinPlugin.CRAFTING));
    }
}
