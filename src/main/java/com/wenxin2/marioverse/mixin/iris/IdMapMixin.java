package com.wenxin2.marioverse.mixin.iris;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.registries.BlockRegistry;
import net.irisshaders.iris.shaderpack.IdMap;
import net.irisshaders.iris.shaderpack.materialmap.BlockEntry;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collections;
import java.util.List;

@Mixin(IdMap.class)
public class IdMapMixin {
    @WrapOperation(method = "lambda$parseBlockMap$2", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"), remap = false)
    private static boolean addBlockEntry(List instance, Object entry, Operation<Boolean> original) {
        if (entry instanceof BlockEntry blockEntry) {
            if (blockEntry.id().getNamespace().equals("minecraft") && blockEntry.id().getName().equals("water")) {
                original.call(instance, new BlockEntry(new NamespacedId(Marioverse.MOD_ID, BlockRegistry.WATER_SPOUT.getId().getPath()), Collections.emptyMap()));
            }
        }
        return original.call(instance, entry);
    }
}
