package com.wenxin2.marioverse.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.wenxin2.marioverse.utils.StructureWarpUuids;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StructureStart.class)
public abstract class StructureStartMixin {
    @Shadow
    public abstract Structure getStructure();

    @Shadow
    public abstract ChunkPos getChunkPos();

    @WrapMethod(method = "placeInChunk")
    private void mv$useStructureWarpSeed(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random,
                                         BoundingBox box, ChunkPos chunkPos, Operation<Void> original) {
        long seed = StructureWarpUuids.getStructureSeed(level.getSeed(),
                level.registryAccess().registryOrThrow(Registries.STRUCTURE).getKey(this.getStructure()), this.getChunkPos());

        StructureWarpUuids.callWithSeed(seed, () -> {
            original.call(level, structureManager, generator, random, box, chunkPos);
            return null;
        });
    }
}
