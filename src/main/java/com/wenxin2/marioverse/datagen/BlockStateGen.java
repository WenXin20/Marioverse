package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.BrickPedestalBlock;
import com.wenxin2.marioverse.blocks.InvisibleQuestionBlock;
import com.wenxin2.marioverse.blocks.QuestionBlock;
import com.wenxin2.marioverse.data.BlockFamilyExtended;
import com.wenxin2.marioverse.init.BlockFamiliesRegistry;
import com.wenxin2.marioverse.init.BlockRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BlockStateGen extends BlockStateProvider {
    public BlockStateGen(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Marioverse.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        genInvisibleQuestionBlockVariants();
        genPedestalVariants();
        genQuestionBlockVariants();
        genSmashableBlockVariants();

        registerStorageBrick(BlockRegistry.STORAGE_CUT_COPPER.get(), "storage_cut_copper",
                mcLoc("minecraft:block/cut_copper"), modLoc("block/empty_copper_question_block"));

        registerStorageBrick(BlockRegistry.STORAGE_EXPOSED_CUT_COPPER.get(), "storage_exposed_cut_copper",
                mcLoc("minecraft:block/exposed_cut_copper"), modLoc("block/empty_exposed_copper_question_block"));

        registerStorageBrick(BlockRegistry.STORAGE_WEATHERED_CUT_COPPER.get(), "storage_weathered_cut_copper",
                mcLoc("minecraft:block/weathered_cut_copper"), modLoc("block/empty_weathered_copper_question_block"));

        registerStorageBrick(BlockRegistry.STORAGE_OXIDIZED_CUT_COPPER.get(), "storage_oxidized_cut_copper",
                mcLoc("minecraft:block/oxidized_cut_copper"), modLoc("block/empty_oxidized_copper_question_block"));

        registerStorageBrick(BlockRegistry.STORAGE_WAXED_CUT_COPPER.get(), "storage_waxed_cut_copper",
                mcLoc("minecraft:block/cut_copper"), modLoc("block/empty_copper_question_block"));

        registerStorageBrick(BlockRegistry.STORAGE_WAXED_EXPOSED_CUT_COPPER.get(), "storage_waxed_exposed_cut_copper",
                mcLoc("minecraft:block/exposed_cut_copper"), modLoc("block/empty_exposed_copper_question_block"));

        registerStorageBrick(BlockRegistry.STORAGE_WAXED_WEATHERED_CUT_COPPER.get(), "storage_waxed_weathered_cut_copper",
                mcLoc("minecraft:block/weathered_cut_copper"), modLoc("block/empty_weathered_copper_question_block"));

        registerStorageBrick(BlockRegistry.STORAGE_WAXED_OXIDIZED_CUT_COPPER.get(), "storage_waxed_oxidized_cut_copper",
                mcLoc("minecraft:block/oxidized_cut_copper"), modLoc("block/empty_oxidized_copper_question_block"));
    }

    private void genPedestalVariants() {
        BlockFamiliesRegistry.getAllExtendedFamilies().forEach(blockFamily -> {
            blockFamily.getVariants().forEach((variant, block) -> {
                BlockFamilyExtended.Variant pedestal = BlockFamilyExtended.Variant.PEDESTAL;

                if (variant == pedestal) {
                    String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
                    String removePedestalName = blockName.replace("_pedestal", "s");
                    ResourceLocation texture;

                    if (block == BlockFamiliesRegistry.AMETHYST_BRICKS.get(pedestal)
                            || block == BlockFamiliesRegistry.DEEP_FUNGAL_BRICKS.get(pedestal)
                            || block == BlockFamiliesRegistry.FUNGAL_BRICKS.get(pedestal)) {
                        texture = modLoc("block/" + removePedestalName);
                        registerPedestalBlock(block, blockName, texture);
                    } else if (blockName.startsWith("waxed_")) {
                        String unWaxedName = blockName.replace("waxed_", "");
                        removePedestalName = unWaxedName.replace("_pedestal", "");
                        texture = mcLoc("minecraft:block/" + removePedestalName);
                        registerPedestalBlock(block, blockName, texture);
                    } else if (blockName.endsWith("_copper_pedestal") || blockName.endsWith("_block_pedestal")
                            || blockName.endsWith("_prismarine_pedestal")) {
                        String unWaxedName = blockName.replace("waxed_", "");
                        removePedestalName = unWaxedName.replace("_pedestal", "");
                        texture = mcLoc("minecraft:block/" + removePedestalName);
                        registerPedestalBlock(block, blockName, texture);
                    } else if (blockName.startsWith("blackstone_")) {
                        String blackstoneName = blockName.replace("blackstone_", "polished_blackstone_");
                        removePedestalName = blackstoneName.replace("_pedestal", "s");
                        texture = mcLoc("minecraft:block/" + removePedestalName);
                        registerPedestalBlock(block, blockName, texture);
                    } else {
                        texture = mcLoc("minecraft:block/" + removePedestalName);
                        registerPedestalBlock(block, blockName, texture);
                    }
                }
            });
        });
    }

    private void genQuestionBlockVariants() {
        BlockFamiliesRegistry.getAllExtendedFamilies().forEach(blockFamily -> {
            blockFamily.getVariants().forEach((variant, block) -> {
                BlockFamilyExtended.Variant questionBlock = BlockFamilyExtended.Variant.QUESTION_BLOCK;

                if (variant == questionBlock) {
                    String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
                    ResourceLocation emptyTexture;
                    ResourceLocation mainTexture;
                    ResourceLocation sideTexture;
                    ResourceLocation topTexture;

                    if (block == BlockFamiliesRegistry.POLISHED_AMETHYST.get(questionBlock)
                            || block == BlockFamiliesRegistry.POLISHED_DEEP_FUNGAL_STONE.get(questionBlock)
                            || block == BlockFamiliesRegistry.POLISHED_FUNGAL_STONE.get(questionBlock)) {
                        sideTexture = modLoc("block/" + blockName + "_side");
                        topTexture = modLoc("block/" + blockName + "_top");
                        emptyTexture = modLoc("block/empty_" + blockName);
                        registerQuestionBlock(block, blockName, sideTexture, topTexture, emptyTexture);
                    } else if (blockName.startsWith("waxed_")) {
                        String unWaxedName = blockName.replace("waxed_", "");
                        mainTexture = modLoc("block/" + unWaxedName);
                        emptyTexture = modLoc("block/empty_" + unWaxedName);
                        registerQuestionBlock(block, blockName, mainTexture, emptyTexture);
                    } else {
                        mainTexture = modLoc("block/" + blockName);
                        emptyTexture = modLoc("block/empty_" + blockName);
                        registerQuestionBlock(block, blockName, mainTexture, emptyTexture);
                    }
                }
            });
        });
    }

    private void genInvisibleQuestionBlockVariants() {
        BlockFamiliesRegistry.getAllExtendedFamilies().forEach(blockFamily -> {
            blockFamily.getVariants().forEach((variant, block) -> {
                BlockFamilyExtended.Variant questionBlock = BlockFamilyExtended.Variant.INVISIBLE_QUESTION_BLOCK;

                if (variant == questionBlock) {
                    String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
                    ResourceLocation emptyTexture;
                    ResourceLocation mainTexture;
                    ResourceLocation sideTexture;
                    ResourceLocation topTexture;
                    ResourceLocation invisibleTexture;

                    if (block == BlockFamiliesRegistry.POLISHED_AMETHYST.get(questionBlock)
                            || block == BlockFamiliesRegistry.POLISHED_DEEP_FUNGAL_STONE.get(questionBlock)
                            || block == BlockFamiliesRegistry.POLISHED_FUNGAL_STONE.get(questionBlock)) {
                        String removeInvisibleName = blockName.replace("invisible_", "");
                        sideTexture = modLoc("block/" + removeInvisibleName + "_side");
                        topTexture = modLoc("block/" + removeInvisibleName + "_top");
                        emptyTexture = modLoc("block/empty_" + removeInvisibleName);
                        invisibleTexture = modLoc("block/invisible_question_block");
                        registerInvisibleQuestionBlock(block, removeInvisibleName, sideTexture, topTexture, emptyTexture, invisibleTexture);
                    } else if (blockName.startsWith("invisible_waxed_")) {
                        String removeInvisibleName = blockName.replace("invisible_", "");
                        String removeWaxedName = removeInvisibleName.replace("waxed_", "");
                        mainTexture = modLoc("block/" + removeWaxedName);
                        emptyTexture = modLoc("block/empty_" + removeWaxedName);
                        invisibleTexture = modLoc("block/invisible_question_block");
                        registerInvisibleQuestionBlock(block, removeInvisibleName, mainTexture, emptyTexture, invisibleTexture);
                    } else {
                        String removeInvisibleName = blockName.replace("invisible_", "");
                        mainTexture = modLoc("block/" + removeInvisibleName);
                        emptyTexture = modLoc("block/empty_" + removeInvisibleName);
                        invisibleTexture = modLoc("block/invisible_question_block");
                        registerInvisibleQuestionBlock(block, removeInvisibleName, mainTexture, emptyTexture, invisibleTexture);
                    }
                }
            });
        });
    }

    private void genSmashableBlockVariants() {
        BlockFamiliesRegistry.getAllExtendedFamilies().forEach(blockFamily -> {
            blockFamily.getVariants().forEach((variant, block) -> {
                BlockFamilyExtended.Variant smashableBlock = BlockFamilyExtended.Variant.SMASHABLE_BLOCKS;

                if (variant == smashableBlock) {
                    String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
                    String removeSmashableName = blockName.replace("smashable_", "");
                    ResourceLocation mainTexture;
                    ResourceLocation overlayTexture;

                    if (block == BlockFamiliesRegistry.AMETHYST_BRICKS.get(smashableBlock)
                            || block == BlockFamiliesRegistry.DEEP_FUNGAL_BRICKS.get(smashableBlock)
                            || block == BlockFamiliesRegistry.FUNGAL_BRICKS.get(smashableBlock)) {
                        mainTexture = modLoc("block/" + removeSmashableName);
                        overlayTexture = modLoc("block/" + blockName + "_overlay");
                        registerOverlayBlock(block, blockName, mainTexture, overlayTexture);
                    } else if (removeSmashableName.startsWith("waxed_")) {
                        String unWaxedName = blockName.replace("waxed_", "");
                        removeSmashableName = unWaxedName.replace("smashable_", "");
                        mainTexture = mcLoc("minecraft:block/" + removeSmashableName);
                        overlayTexture = modLoc("block/" + unWaxedName + "_overlay");
                        registerOverlayBlock(block, blockName, mainTexture, overlayTexture);
                    } else if (removeSmashableName.startsWith("blackstone_")) {
                        String crackedBlockName = removeSmashableName.replace("blackstone_", "cracked_polished_blackstone_");
                        mainTexture = mcLoc("minecraft:block/" + crackedBlockName);
                        registerBlock(block, blockName, mainTexture);
                    } else if (removeSmashableName.startsWith("deepslate_tiles")) {
                        String crackedBlockName = removeSmashableName.replace("deepslate_tiles", "cracked_deepslate_tiles");
                        mainTexture = mcLoc("minecraft:block/" + crackedBlockName);
                        registerBlock(block, blockName, mainTexture);
                    } else if (removeSmashableName.startsWith("nether_")) {
                        String crackedBlockName = removeSmashableName.replace("nether_", "cracked_nether_");
                        mainTexture = mcLoc("minecraft:block/" + crackedBlockName);
                        registerBlock(block, blockName, mainTexture);
                    } else if (removeSmashableName.startsWith("stone_")) {
                        String crackedBlockName = removeSmashableName.replace("stone_", "cracked_stone_");
                        mainTexture = mcLoc("minecraft:block/" + crackedBlockName);
                        registerBlock(block, blockName, mainTexture);
                    } else {
                        mainTexture = mcLoc("minecraft:block/" + removeSmashableName);
                        overlayTexture = modLoc("block/" + blockName + "_overlay");
                        registerOverlayBlock(block, blockName, mainTexture, overlayTexture);
                    }
                }
            });
        });
    }

    private void registerInvisibleQuestionBlock(Block block, String modelName, ResourceLocation sideTexture, ResourceLocation topTexture,
                                                ResourceLocation emptyTexture, ResourceLocation invisibleTexture) {
        ModelFile model = models()
                .withExistingParent(modelName, mcLoc("block/cube_bottom_top"))
                .texture("bottom", topTexture).texture("side", sideTexture).texture("top", topTexture);
        ModelFile modelEmpty = models()
                .withExistingParent("empty_invisible_" + modelName, mcLoc("block/cube_all"))
                .texture("all", emptyTexture);
        ModelFile modelInvisible = models()
                .withExistingParent("invisible_" + modelName, mcLoc("block/cube_all"))
                .texture("all", invisibleTexture).renderType("tripwire");

        VariantBlockStateBuilder variantBuilder = getVariantBuilder(block);
        variantBuilder.partialState().with(QuestionBlock.EMPTY, false).with(InvisibleQuestionBlock.INVISIBLE, false)
                .addModels(new ConfiguredModel(model));
        variantBuilder.partialState().with(QuestionBlock.EMPTY, true).with(InvisibleQuestionBlock.INVISIBLE, false)
                .addModels(new ConfiguredModel(modelEmpty));
        variantBuilder.partialState().with(QuestionBlock.EMPTY, false).with(InvisibleQuestionBlock.INVISIBLE, true)
                .addModels(new ConfiguredModel(modelInvisible));
        variantBuilder.partialState().with(QuestionBlock.EMPTY, true).with(InvisibleQuestionBlock.INVISIBLE, true)
                .addModels(new ConfiguredModel(modelEmpty));
    }

    private void registerInvisibleQuestionBlock(Block block, String modelName, ResourceLocation mainTexture, ResourceLocation emptyTexture,
                                                ResourceLocation invisibleTexture) {
        ModelFile model = models()
                .withExistingParent(modelName, mcLoc("block/cube_bottom_top"))
                .texture("bottom", emptyTexture).texture("side", mainTexture).texture("top", emptyTexture);
        ModelFile modelEmpty = models()
                .withExistingParent("empty_invisible_" + modelName, mcLoc("block/cube_all"))
                .texture("all", emptyTexture);
        ModelFile modelInvisible = models()
                .withExistingParent("invisible_" + modelName, mcLoc("block/cube_all"))
                .texture("all", invisibleTexture).renderType("tripwire");

        VariantBlockStateBuilder variantBuilder = getVariantBuilder(block);
        variantBuilder.partialState().with(QuestionBlock.EMPTY, false).with(InvisibleQuestionBlock.INVISIBLE, false)
                .addModels(new ConfiguredModel(model));
        variantBuilder.partialState().with(QuestionBlock.EMPTY, true).with(InvisibleQuestionBlock.INVISIBLE, false)
                .addModels(new ConfiguredModel(modelEmpty));
        variantBuilder.partialState().with(QuestionBlock.EMPTY, false).with(InvisibleQuestionBlock.INVISIBLE, true)
                .addModels(new ConfiguredModel(modelInvisible));
        variantBuilder.partialState().with(QuestionBlock.EMPTY, true).with(InvisibleQuestionBlock.INVISIBLE, true)
                .addModels(new ConfiguredModel(modelEmpty));
    }

    private void registerBlock(Block block, String modelName, ResourceLocation mainTexture) {
        ModelFile model = models()
                .withExistingParent(modelName, mcLoc("minecraft:block/cube_all"))
                .texture("all", mainTexture);

        simpleBlockWithItem(block, model);
    }

    private void registerOverlayBlock(Block block, String modelName, ResourceLocation mainTexture, ResourceLocation overlayTexture) {
        ModelFile model = models()
                .withExistingParent(modelName, modLoc("block/cube_all_overlay"))
                .texture("all", mainTexture).texture("overlay", overlayTexture).renderType("cutout_mipped");

        simpleBlockWithItem(block, model);
    }

    private void registerPedestalBlock(Block block, String modelName, ResourceLocation mainTexture) {
        ModelFile modelTop = models()
                .withExistingParent(modelName + "_top", modLoc("block/template_brick_pedestal_top"))
                .texture("bricks", mainTexture);
        ModelFile modelBottom = models()
                .withExistingParent(modelName, modLoc("block/template_brick_pedestal"))
                .texture("bricks", mainTexture);

        simpleBlockItem(block, modelTop);

        VariantBlockStateBuilder variantBuilder = getVariantBuilder(block);
        variantBuilder.partialState().with(BrickPedestalBlock.TOP, true).addModels(new ConfiguredModel(modelTop));
        variantBuilder.partialState().with(BrickPedestalBlock.TOP, false).addModels(new ConfiguredModel(modelBottom));
    }

    private void registerQuestionBlock(Block block, String modelName, ResourceLocation sideTexture, ResourceLocation topTexture,
                                       ResourceLocation emptyTexture) {
        ModelFile model = models()
                .withExistingParent(modelName, mcLoc("block/cube_bottom_top"))
                .texture("bottom", topTexture).texture("side", sideTexture).texture("top", topTexture);
        ModelFile modelEmpty = models()
                .withExistingParent("empty_" + modelName, mcLoc("block/cube_all"))
                .texture("all", emptyTexture);

        simpleBlockItem(block, model);

        VariantBlockStateBuilder variantBuilder = getVariantBuilder(block);
        variantBuilder.partialState().with(QuestionBlock.EMPTY, false).addModels(new ConfiguredModel(model));
        variantBuilder.partialState().with(QuestionBlock.EMPTY, true).addModels(new ConfiguredModel(modelEmpty));
    }

    private void registerQuestionBlock(Block block, String modelName, ResourceLocation mainTexture, ResourceLocation emptyTexture) {
        ModelFile model = models()
                .withExistingParent(modelName, mcLoc("block/cube_bottom_top"))
                .texture("bottom", emptyTexture).texture("side", mainTexture).texture("top", emptyTexture);
        ModelFile modelEmpty = models()
                .withExistingParent("empty_" + modelName, mcLoc("block/cube_all"))
                .texture("all", emptyTexture);

        simpleBlockItem(block, model);

        VariantBlockStateBuilder variantBuilder = getVariantBuilder(block);
        variantBuilder.partialState().with(QuestionBlock.EMPTY, false).addModels(new ConfiguredModel(model));
        variantBuilder.partialState().with(QuestionBlock.EMPTY, true).addModels(new ConfiguredModel(modelEmpty));
    }

    private void registerStorageBrick(Block block, String modelName, ResourceLocation mainTexture, ResourceLocation emptyTexture) {
        ModelFile model = models()
                .withExistingParent(modelName, mcLoc("block/cube_all"))
                .texture("all", mainTexture);
        ModelFile modelEmpty = models()
                .withExistingParent("empty_" + modelName, mcLoc("block/cube_all"))
                .texture("all", emptyTexture);

        VariantBlockStateBuilder variantBuilder = getVariantBuilder(block);
        variantBuilder.partialState().with(QuestionBlock.EMPTY, false).addModels(new ConfiguredModel(model));
        variantBuilder.partialState().with(QuestionBlock.EMPTY, true).addModels(new ConfiguredModel(modelEmpty));
    }
}
