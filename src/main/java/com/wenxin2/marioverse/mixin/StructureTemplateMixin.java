package com.wenxin2.marioverse.mixin;

import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.entity.PartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(StructureTemplate.class)
public class StructureTemplateMixin {
    @ModifyArg(method = "fillEntityList", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"),
            index = 2)
    private Predicate<Entity> mv$skipPartEntities(Predicate<Entity> predicate) {
        return predicate.and(entity -> !(entity instanceof PartEntity));
    }
}
