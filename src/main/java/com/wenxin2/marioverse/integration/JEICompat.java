package com.wenxin2.marioverse.integration;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.data.WarpDoorRecipe;
import com.wenxin2.marioverse.integration.jei_compat.WarpDoorCraftingExtension;
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
    }
}
