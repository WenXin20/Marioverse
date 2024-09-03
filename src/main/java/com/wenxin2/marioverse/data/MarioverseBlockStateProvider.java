package com.wenxin2.marioverse.data;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.init.BlockRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class MarioverseBlockStateProvider extends BlockStateProvider {
    public MarioverseBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Marioverse.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // Placeholders, their usages should be replaced with real values. See above for how to use the model builder,
        // and below for the helpers the model builder offers.
        ModelFile exampleModel = models().withExistingParent("coin", "minecraft:block/cobblestone");
        Block block = BlockRegistry.COIN.get();
        ResourceLocation exampleTexture = modLoc("block/coin");
        ResourceLocation bottomTexture = modLoc("block/coin");
        ResourceLocation topTexture = modLoc("block/coin");
        ResourceLocation sideTexture = modLoc("block/coin");
        ResourceLocation frontTexture = modLoc("block/coin");

        simpleBlock(block);
        simpleBlock(block, exampleModel);
        simpleBlockItem(block, exampleModel);
    }
}
