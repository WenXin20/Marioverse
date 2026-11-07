package com.wenxin2.marioverse.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.wenxin2.marioverse.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.marioverse.utils.StructureWarpUuids;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
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

    @WrapMethod(method = "placeInWorld")
    private boolean mv$useWarpPlacementSeed(ServerLevelAccessor level, BlockPos offset, BlockPos pos, StructurePlaceSettings settings,
                                            RandomSource random, int flags, Operation<Boolean> original) {
        return StructureWarpUuids.callWithPlacementSeed(() -> original.call(level, offset, pos, settings, random, flags));
    }

    @WrapOperation(method = "placeInWorld", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/entity/BlockEntity;loadWithComponents(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;)V"))
    private void mv$remapWarpBlockUuids(BlockEntity blockEntity, CompoundTag tag, HolderLookup.Provider provider, Operation<Void> original) {
        original.call(blockEntity, blockEntity instanceof BaseWarpBlockEntity ? StructureWarpUuids.remapBlockEntityTag(tag) : tag, provider);
    }

    @WrapOperation(method = "addEntitiesToWorld", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/nbt/CompoundTag;remove(Ljava/lang/String;)V"))
    private void mv$remapWarpEntityUuids(CompoundTag tag, String key, Operation<Void> original) {
        if (!"UUID".equals(key) || !StructureWarpUuids.remapEntityTag(tag))
            original.call(tag, key);
    }
}
