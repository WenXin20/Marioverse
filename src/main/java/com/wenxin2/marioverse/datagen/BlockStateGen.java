package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.BrickPedestalBlock;
import com.wenxin2.marioverse.blocks.InvisibleQuestionBlock;
import com.wenxin2.marioverse.blocks.QuestionBlock;
import com.wenxin2.marioverse.data.BlockFamilyExtended;
import com.wenxin2.marioverse.init.BlockFamiliesRegistry;
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
        this.genInvisibleQuestionBlockVariants();
        this.genPedestalVariants();
        this.genQuestionBlockVariants();
        this.genSmashableBlockVariants();
        this.genStorageBrickVariants();
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

                        this.invisibleQuestionBlockModel(block, removeInvisibleName, sideTexture, topTexture, emptyTexture, invisibleTexture);
                    } else if (blockName.startsWith("invisible_waxed_")) {
                        String removeInvisibleName = blockName.replace("invisible_", "");
                        String removeWaxedName = removeInvisibleName.replace("waxed_", "");
                        mainTexture = modLoc("block/" + removeWaxedName);
                        emptyTexture = modLoc("block/empty_" + removeWaxedName);
                        invisibleTexture = modLoc("block/invisible_question_block");

                        this.invisibleQuestionBlockModel(block, removeInvisibleName, mainTexture, emptyTexture, invisibleTexture);
                    } else {
                        String removeInvisibleName = blockName.replace("invisible_", "");
                        mainTexture = modLoc("block/" + removeInvisibleName);
                        emptyTexture = modLoc("block/empty_" + removeInvisibleName);
                        invisibleTexture = modLoc("block/invisible_question_block");

                        this.invisibleQuestionBlockModel(block, removeInvisibleName, mainTexture, emptyTexture, invisibleTexture);
                    }
                }
            });
        });
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

                        this.pedestalModel(block, blockName, texture);
                    } else if (blockName.startsWith("waxed_")) {
                        String unWaxedName = blockName.replace("waxed_", "");
                        removePedestalName = unWaxedName.replace("_pedestal", "");
                        texture = mcLoc("minecraft:block/" + removePedestalName);

                        this.pedestalModel(block, blockName, texture);
                    } else if (blockName.endsWith("_copper_pedestal") || blockName.endsWith("_block_pedestal")
                            || blockName.endsWith("_prismarine_pedestal")) {
                        String unWaxedName = blockName.replace("waxed_", "");
                        removePedestalName = unWaxedName.replace("_pedestal", "");
                        texture = mcLoc("minecraft:block/" + removePedestalName);

                        this.pedestalModel(block, blockName, texture);
                    } else if (blockName.startsWith("blackstone_")) {
                        String blackstoneName = blockName.replace("blackstone_", "polished_blackstone_");
                        removePedestalName = blackstoneName.replace("_pedestal", "s");
                        texture = mcLoc("minecraft:block/" + removePedestalName);

                        this.pedestalModel(block, blockName, texture);
                    } else {
                        texture = mcLoc("minecraft:block/" + removePedestalName);

                        this.pedestalModel(block, blockName, texture);
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

                        this.questionBlockModel(block, blockName, sideTexture, topTexture, emptyTexture);
                    } else if (blockName.startsWith("waxed_")) {
                        String unWaxedName = blockName.replace("waxed_", "");
                        mainTexture = modLoc("block/" + unWaxedName);
                        emptyTexture = modLoc("block/empty_" + unWaxedName);

                        this.questionBlockModel(block, blockName, mainTexture, emptyTexture);
                    } else {
                        mainTexture = modLoc("block/" + blockName);
                        emptyTexture = modLoc("block/empty_" + blockName);

                        this.questionBlockModel(block, blockName, mainTexture, emptyTexture);
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

                        this.cubeOverlayModel(block, blockName, mainTexture, overlayTexture);
                    } else if (removeSmashableName.startsWith("waxed_")) {
                        String unWaxedName = blockName.replace("waxed_", "");
                        removeSmashableName = unWaxedName.replace("smashable_", "");
                        mainTexture = mcLoc("minecraft:block/" + removeSmashableName);
                        overlayTexture = modLoc("block/" + unWaxedName + "_overlay");

                        this.cubeOverlayModel(block, blockName, mainTexture, overlayTexture);
                    } else if (removeSmashableName.startsWith("blackstone_")) {
                        String crackedBlockName = removeSmashableName.replace("blackstone_", "cracked_polished_blackstone_");
                        mainTexture = mcLoc("minecraft:block/" + crackedBlockName);

                        this.cubeAllModel(block, blockName, mainTexture);
                    } else if (removeSmashableName.startsWith("deepslate_tiles")) {
                        String crackedBlockName = removeSmashableName.replace("deepslate_tiles", "cracked_deepslate_tiles");
                        mainTexture = mcLoc("minecraft:block/" + crackedBlockName);

                        this.cubeAllModel(block, blockName, mainTexture);
                    } else if (removeSmashableName.startsWith("nether_")) {
                        String crackedBlockName = removeSmashableName.replace("nether_", "cracked_nether_");
                        mainTexture = mcLoc("minecraft:block/" + crackedBlockName);

                        this.cubeAllModel(block, blockName, mainTexture);
                    } else if (removeSmashableName.startsWith("stone_")) {
                        String crackedBlockName = removeSmashableName.replace("stone_", "cracked_stone_");
                        mainTexture = mcLoc("minecraft:block/" + crackedBlockName);

                        this.cubeAllModel(block, blockName, mainTexture);
                    } else {
                        mainTexture = mcLoc("minecraft:block/" + removeSmashableName);
                        overlayTexture = modLoc("block/" + blockName + "_overlay");

                        this.cubeOverlayModel(block, blockName, mainTexture, overlayTexture);
                    }
                }
            });
        });
    }

    private void genStorageBrickVariants() {
        BlockFamiliesRegistry.getAllExtendedFamilies().forEach(blockFamily -> {
            blockFamily.getVariants().forEach((variant, block) -> {
                BlockFamilyExtended.Variant storageBrick = BlockFamilyExtended.Variant.STORAGE_BRICKS;

                if (variant == storageBrick) {
                    String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
                    String removeStorageName = blockName.replace("storage_", "");
                    String questionBlockName = removeStorageName
                            .replace("block", "question_block")
                            .replace("bricks", "question_bricks")
                            .replace("cut_copper", "copper_question_block")
                            .replace("dark_prismarine", "dark_prismarine_question_block")
                            .replace("tiles", "question_tiles");
                    ResourceLocation mainTexture;
                    ResourceLocation emptyTexture;

                    if (block == BlockFamiliesRegistry.AMETHYST_BRICKS.get(storageBrick)
                            || block == BlockFamiliesRegistry.DEEP_FUNGAL_BRICKS.get(storageBrick)
                            || block == BlockFamiliesRegistry.FUNGAL_BRICKS.get(storageBrick)) {
                        questionBlockName = removeStorageName
                                .replace("bricks", "question_block");
                        mainTexture = modLoc("block/" + blockName);
                        emptyTexture = modLoc("block/empty_" + questionBlockName);

                        this.storageBrickModel(block, blockName, mainTexture, emptyTexture);
                    } else if (removeStorageName.startsWith("waxed_")) {
                        String unWaxedName = blockName.replace("waxed_", "");
                        removeStorageName = unWaxedName.replace("storage_", "");
                        questionBlockName = removeStorageName
                                .replace("cut_copper", "copper_question_block");
                        mainTexture = mcLoc("minecraft:block/" + removeStorageName);
                        emptyTexture = modLoc("block/empty_" + questionBlockName);

                        this.storageBrickModel(block, blockName, mainTexture, emptyTexture);
                    } else if (questionBlockName.startsWith("blackstone_")) {
                        String crackedBlockName = removeStorageName.replace("blackstone_", "polished_blackstone_");
                        mainTexture = mcLoc("minecraft:block/" + crackedBlockName);
                        emptyTexture = modLoc("block/empty_" + questionBlockName);

                        this.storageBrickModel(block, blockName, mainTexture, emptyTexture);
                    } else {
                        mainTexture = mcLoc("minecraft:block/" + removeStorageName);
                        emptyTexture = modLoc("block/empty_" + questionBlockName);

                        this.storageBrickModel(block, blockName, mainTexture, emptyTexture);
                    }
                }
            });
        });
    }

    private void invisibleQuestionBlockModel(Block block, String modelName, ResourceLocation sideTexture, ResourceLocation topTexture,
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

    private void invisibleQuestionBlockModel(Block block, String modelName, ResourceLocation mainTexture, ResourceLocation emptyTexture,
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

    private void cubeAllModel(Block block, String modelName, ResourceLocation mainTexture) {
        ModelFile model = models()
                .withExistingParent(modelName, mcLoc("minecraft:block/cube_all"))
                .texture("all", mainTexture);

        simpleBlockWithItem(block, model);
    }

    private void cubeOverlayModel(Block block, String modelName, ResourceLocation mainTexture, ResourceLocation overlayTexture) {
        ModelFile model = models()
                .withExistingParent(modelName, modLoc("block/cube_all_overlay"))
                .texture("all", mainTexture).texture("overlay", overlayTexture).renderType("cutout_mipped");

        simpleBlockWithItem(block, model);
    }

    private void pedestalModel(Block block, String modelName, ResourceLocation mainTexture) {
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

    private void questionBlockModel(Block block, String modelName, ResourceLocation sideTexture, ResourceLocation topTexture,
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

    private void questionBlockModel(Block block, String modelName, ResourceLocation mainTexture, ResourceLocation emptyTexture) {
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

    private void storageBrickModel(Block block, String modelName, ResourceLocation mainTexture, ResourceLocation emptyTexture) {
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
