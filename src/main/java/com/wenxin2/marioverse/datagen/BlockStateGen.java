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
                        String unWaxedName = blockName.replace("waxed_", ""); // Get the corresponding regular copper name
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

        registerPedestalBlock(BlockRegistry.CUT_COPPER_PEDESTAL.get(), "cut_copper_pedestal",
                mcLoc("minecraft:block/cut_copper"));
        registerInvisibleQuestionBlock(BlockRegistry.INVISIBLE_COPPER_QUESTION_BLOCK.get(), "copper_question_block",
                modLoc("block/copper_question_block"), modLoc("block/empty_copper_question_block"),
                modLoc("block/invisible_question_block"));
        registerOverlayBlock(BlockRegistry.SMASHABLE_CUT_COPPER.get(), "smashable_cut_copper",
                mcLoc("minecraft:block/cut_copper"), modLoc("block/smashable_cut_copper_overlay"));
        registerStorageBrick(BlockRegistry.STORAGE_CUT_COPPER.get(), "storage_cut_copper",
                mcLoc("minecraft:block/cut_copper"), modLoc("block/empty_copper_question_block"));

        registerPedestalBlock(BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL.get(), "exposed_cut_copper_pedestal",
                mcLoc("minecraft:block/exposed_cut_copper"));
        registerInvisibleQuestionBlock(BlockRegistry.INVISIBLE_EXPOSED_COPPER_QUESTION_BLOCK.get(), "exposed_copper_question_block",
                modLoc("block/exposed_copper_question_block"), modLoc("block/empty_exposed_copper_question_block"),
                modLoc("block/invisible_question_block"));
        registerOverlayBlock(BlockRegistry.SMASHABLE_EXPOSED_CUT_COPPER.get(), "smashable_exposed_cut_copper",
                mcLoc("minecraft:block/exposed_cut_copper"), modLoc("block/smashable_exposed_cut_copper_overlay"));
        registerStorageBrick(BlockRegistry.STORAGE_EXPOSED_CUT_COPPER.get(), "storage_exposed_cut_copper",
                mcLoc("minecraft:block/exposed_cut_copper"), modLoc("block/empty_exposed_copper_question_block"));

        registerPedestalBlock(BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL.get(), "weathered_cut_copper_pedestal",
                mcLoc("minecraft:block/weathered_cut_copper"));
        registerInvisibleQuestionBlock(BlockRegistry.INVISIBLE_WEATHERED_COPPER_QUESTION_BLOCK.get(), "weathered_copper_question_block",
                modLoc("block/weathered_copper_question_block"), modLoc("block/empty_weathered_copper_question_block"),
                modLoc("block/invisible_question_block"));
        registerOverlayBlock(BlockRegistry.SMASHABLE_WEATHERED_CUT_COPPER.get(), "smashable_weathered_cut_copper",
                mcLoc("minecraft:block/weathered_cut_copper"), modLoc("block/smashable_weathered_cut_copper_overlay"));
        registerStorageBrick(BlockRegistry.STORAGE_WEATHERED_CUT_COPPER.get(), "storage_weathered_cut_copper",
                mcLoc("minecraft:block/weathered_cut_copper"), modLoc("block/empty_weathered_copper_question_block"));

        registerPedestalBlock(BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL.get(), "oxidized_cut_copper_pedestal",
                mcLoc("minecraft:block/oxidized_cut_copper"));
        registerInvisibleQuestionBlock(BlockRegistry.INVISIBLE_OXIDIZED_COPPER_QUESTION_BLOCK.get(), "oxidized_copper_question_block",
                modLoc("block/oxidized_copper_question_block"), modLoc("block/empty_oxidized_copper_question_block"),
                modLoc("block/invisible_question_block"));
        registerOverlayBlock(BlockRegistry.SMASHABLE_OXIDIZED_CUT_COPPER.get(), "smashable_oxidized_cut_copper",
                mcLoc("minecraft:block/oxidized_cut_copper"), modLoc("block/smashable_oxidized_cut_copper_overlay"));
        registerStorageBrick(BlockRegistry.STORAGE_OXIDIZED_CUT_COPPER.get(), "storage_oxidized_cut_copper",
                mcLoc("minecraft:block/oxidized_cut_copper"), modLoc("block/empty_oxidized_copper_question_block"));

        registerPedestalBlock(BlockRegistry.WAXED_CUT_COPPER_PEDESTAL.get(), "waxed_cut_copper_pedestal",
                mcLoc("minecraft:block/cut_copper"));
        registerInvisibleQuestionBlock(BlockRegistry.INVISIBLE_WAXED_COPPER_QUESTION_BLOCK.get(), "waxed_copper_question_block",
                modLoc("block/copper_question_block"), modLoc("block/empty_copper_question_block"),
                modLoc("block/invisible_question_block"));
        registerOverlayBlock(BlockRegistry.SMASHABLE_WAXED_CUT_COPPER.get(), "smashable_waxed_cut_copper",
                mcLoc("minecraft:block/cut_copper"), modLoc("block/smashable_cut_copper_overlay"));
        registerStorageBrick(BlockRegistry.STORAGE_WAXED_CUT_COPPER.get(), "storage_waxed_cut_copper",
                mcLoc("minecraft:block/cut_copper"), modLoc("block/empty_copper_question_block"));

        registerPedestalBlock(BlockRegistry.WAXED_EXPOSED_CUT_COPPER_PEDESTAL.get(), "exposed_cut_copper_pedestal",
                mcLoc("minecraft:block/exposed_cut_copper"));
        registerInvisibleQuestionBlock(BlockRegistry.INVISIBLE_WAXED_EXPOSED_COPPER_QUESTION_BLOCK.get(), "waxed_exposed_copper_question_block",
                modLoc("block/exposed_copper_question_block"), modLoc("block/empty_exposed_copper_question_block"),
                modLoc("block/invisible_question_block"));
        registerOverlayBlock(BlockRegistry.SMASHABLE_WAXED_EXPOSED_CUT_COPPER.get(), "smashable_waxed_exposed_cut_copper",
                mcLoc("minecraft:block/exposed_cut_copper"), modLoc("block/smashable_exposed_cut_copper_overlay"));
        registerStorageBrick(BlockRegistry.STORAGE_WAXED_EXPOSED_CUT_COPPER.get(), "storage_waxed_exposed_cut_copper",
                mcLoc("minecraft:block/exposed_cut_copper"), modLoc("block/empty_exposed_copper_question_block"));

        registerPedestalBlock(BlockRegistry.WAXED_WEATHERED_CUT_COPPER_PEDESTAL.get(), "waxed_weathered_cut_copper_pedestal",
                mcLoc("minecraft:block/weathered_cut_copper"));
        registerInvisibleQuestionBlock(BlockRegistry.INVISIBLE_WAXED_WEATHERED_COPPER_QUESTION_BLOCK.get(), "waxed_weathered_copper_question_block",
                modLoc("block/weathered_copper_question_block"), modLoc("block/empty_weathered_copper_question_block"),
                modLoc("block/invisible_question_block"));
        registerOverlayBlock(BlockRegistry.SMASHABLE_WAXED_WEATHERED_CUT_COPPER.get(), "smashable_waxed_weathered_cut_copper",
                mcLoc("minecraft:block/weathered_cut_copper"), modLoc("block/smashable_weathered_cut_copper_overlay"));
        registerStorageBrick(BlockRegistry.STORAGE_WAXED_WEATHERED_CUT_COPPER.get(), "storage_waxed_weathered_cut_copper",
                mcLoc("minecraft:block/weathered_cut_copper"), modLoc("block/empty_weathered_copper_question_block"));

        registerPedestalBlock(BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL.get(), "waxed_oxidized_cut_copper_pedestal",
                mcLoc("minecraft:block/oxidized_cut_copper"));
        registerInvisibleQuestionBlock(BlockRegistry.INVISIBLE_WAXED_OXIDIZED_COPPER_QUESTION_BLOCK.get(), "waxed_oxidized_copper_question_block",
                modLoc("block/oxidized_copper_question_block"), modLoc("block/empty_oxidized_copper_question_block"),
                modLoc("block/invisible_question_block"));
        registerOverlayBlock(BlockRegistry.SMASHABLE_WAXED_OXIDIZED_CUT_COPPER.get(), "smashable_waxed_oxidized_cut_copper",
                mcLoc("minecraft:block/oxidized_cut_copper"), modLoc("block/smashable_oxidized_cut_copper_overlay"));
        registerStorageBrick(BlockRegistry.STORAGE_WAXED_OXIDIZED_CUT_COPPER.get(), "storage_waxed_oxidized_cut_copper",
                mcLoc("minecraft:block/oxidized_cut_copper"), modLoc("block/empty_oxidized_copper_question_block"));
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

    private void registerOverlayBlock(Block block, String modelName, ResourceLocation mainTexture, ResourceLocation overlayTexture) {
        ModelFile model = models()
                .withExistingParent(modelName, modLoc("block/cube_all_overlay"))
                .texture("all", mainTexture).texture("overlay", overlayTexture).renderType("cutout_mipped");

        simpleBlockWithItem(block, model);

        VariantBlockStateBuilder variantBuilder = getVariantBuilder(block);
        variantBuilder.partialState().addModels(new ConfiguredModel(model));
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
