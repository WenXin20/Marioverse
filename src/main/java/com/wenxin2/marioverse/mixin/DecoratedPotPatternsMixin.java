package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.registries.ItemRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DecoratedPotPatterns.class)
public class DecoratedPotPatternsMixin {
    @Unique private static final ResourceKey<DecoratedPotPattern> MV$BOWSER = mv$create("bowser");
    @Unique private static final ResourceKey<DecoratedPotPattern> MV$PLUMBER = mv$create("plumber");


    @Inject(method = "getPatternFromItem", at = @At("TAIL"), cancellable = true)
    private static void getResourceKey(Item item, CallbackInfoReturnable<ResourceKey<DecoratedPotPattern>> cir) {
        if (cir.getReturnValue() == null) {
            if (item == ItemRegistry.BOWSER_POTTERY_SHERD.get())
                cir.setReturnValue(MV$BOWSER);
            if (item == ItemRegistry.PLUMBER_POTTERY_SHERD.get())
                cir.setReturnValue(MV$PLUMBER);
        }
    }

    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void bootstrap(Registry<DecoratedPotPattern> registry, CallbackInfoReturnable<DecoratedPotPattern> cir) {
        mv$register(registry, MV$BOWSER, "bowser_pottery_pattern");
        mv$register(registry, MV$PLUMBER, "plumber_pottery_pattern");
    }

    @Unique
    private static ResourceKey<DecoratedPotPattern> mv$create(String name) {
        return ResourceKey.create(Registries.DECORATED_POT_PATTERN, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, name));
    }

    @Unique
    private static void mv$register(Registry<DecoratedPotPattern> registry, ResourceKey<DecoratedPotPattern> pattern, String name) {
        Registry.register(registry, pattern, new DecoratedPotPattern(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, name)));
    }
}
