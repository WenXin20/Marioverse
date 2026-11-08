package com.wenxin2.marioverse.mixin;

import com.google.gson.JsonElement;
import com.wenxin2.marioverse.data.WarpDoorRecipe;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At("TAIL"))
    private void marioverse$removeReplacedWarpDoorRecipes(Map<ResourceLocation, JsonElement> recipes,
                                                          ResourceManager resourceManager, ProfilerFiller profiler,
                                                          CallbackInfo ci) {
        WarpDoorRecipe.removeReplacedRecipes((RecipeManager) (Object) this);
    }
}
