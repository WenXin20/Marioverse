package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.init.BlockRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.DoorBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class WarpDoorBlockStateProvider extends BlockStateProvider {
    public WarpDoorBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Marioverse.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        BuiltInRegistries.BLOCK.stream()
                .filter(block -> block instanceof DoorBlock)  // Filter only DoorBlock instances
                .forEach(originalDoor -> {
                    // Get the registry name for the door block
                    ResourceLocation registryName = BuiltInRegistries.BLOCK.getKey(originalDoor);

                    String baseName = registryName.getPath();  // Extract the base name of the door
                    ResourceLocation bottomTexture = ResourceLocation.fromNamespaceAndPath(registryName.getNamespace(), "block/" + baseName + "_bottom");
                    ResourceLocation topTexture = ResourceLocation.fromNamespaceAndPath(registryName.getNamespace(), "block/" + baseName + "_top");

                    // If the door is an instance of DoorBlock, apply the door model generation
                    try {
                        // Log the texture paths for debugging
                        System.out.println("Registering door: {} with bottom texture: {} and top texture: {}" + baseName + bottomTexture + topTexture);

                        // Ensure the block is an instance of DoorBlock before proceeding
                        if (originalDoor instanceof DoorBlock doorBlock) {
                            // Register the door block with the corresponding textures
                            this.doorBlock(doorBlock, bottomTexture, topTexture);
                        }
                    } catch (Exception e) {
                        System.out.println("Failed to load textures for door: {}. Skipping." + baseName + e);
                        // Optionally, register a default texture or do nothing
                    }
                });
    }
}
