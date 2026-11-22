package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SnowGolem.class)
public abstract class SnowGolemMixin {

    @ModifyArg(method = "shear", require = 0, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/animal/SnowGolem;spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;"),
            index = 0)
    private ItemStack mv$shear(ItemStack original) {
        SnowGolem golem = (SnowGolem) (Object) this;
        String head = golem.getData(DataAttachmentRegistry.SNOW_GOLEM_HEAD);
        if (head.isEmpty())
            return original;

        golem.removeData(DataAttachmentRegistry.SNOW_GOLEM_HEAD);
        ResourceLocation id = ResourceLocation.tryParse(head);
        if (id == null || !BuiltInRegistries.BLOCK.containsKey(id))
            return original;
        return new ItemStack(BuiltInRegistries.BLOCK.get(id));
    }
}
