package com.wenxin2.marioverse.integration;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.data.ArrowColorShapedRecipe;
import com.wenxin2.marioverse.data.ArrowColorShapelessRecipe;
import com.wenxin2.marioverse.data.HexColorShapedRecipe;
import com.wenxin2.marioverse.data.HexColorShapelessRecipe;
import com.wenxin2.marioverse.data.WarpDoorRecipe;
import com.wenxin2.marioverse.data.WarpTrapDoorRecipe;
import com.wenxin2.marioverse.integration.jei_compat.ArrowColorShapedRecipeExtension;
import com.wenxin2.marioverse.integration.jei_compat.ArrowColorShapelessRecipeExtension;
import com.wenxin2.marioverse.integration.jei_compat.HexColorShapedRecipeExtension;
import com.wenxin2.marioverse.integration.jei_compat.HexColorShapelessRecipeExtension;
import com.wenxin2.marioverse.integration.jei_compat.WarpDoorCraftingExtension;
import com.wenxin2.marioverse.integration.jei_compat.WarpTrapDoorCraftingExtension;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class JEICompat implements IModPlugin {
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "jei_plugin");

    @NotNull
    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addExtension(WarpDoorRecipe.class,
                new WarpDoorCraftingExtension(registration.getJeiHelpers().getGuiHelper()));
        registration.getCraftingCategory().addExtension(WarpTrapDoorRecipe.class,
                new WarpTrapDoorCraftingExtension(registration.getJeiHelpers().getGuiHelper()));
        registration.getCraftingCategory().addExtension(HexColorShapedRecipe.class,
                new HexColorShapedRecipeExtension(registration.getJeiHelpers().getGuiHelper()));
        registration.getCraftingCategory().addExtension(HexColorShapelessRecipe.class,
                new HexColorShapelessRecipeExtension(registration.getJeiHelpers().getGuiHelper()));
        registration.getCraftingCategory().addExtension(ArrowColorShapedRecipe.class,
                new ArrowColorShapedRecipeExtension(registration.getJeiHelpers().getGuiHelper()));
        registration.getCraftingCategory().addExtension(ArrowColorShapelessRecipe.class,
                new ArrowColorShapelessRecipeExtension(registration.getJeiHelpers().getGuiHelper()));
    }
}
