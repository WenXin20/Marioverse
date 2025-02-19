package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.BrickPedestalBlock;
import com.wenxin2.marioverse.blocks.ClearWarpPipeBlock;
import com.wenxin2.marioverse.blocks.GoalPoleBlock;
import com.wenxin2.marioverse.blocks.InvisibleQuestionBlock;
import com.wenxin2.marioverse.blocks.QuestionBlock;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.states.ColumnBlockStates;
import com.wenxin2.marioverse.data.BlockFamilyExtended;
import com.wenxin2.marioverse.init.BlockFamilyRegistry;
import com.wenxin2.marioverse.init.BlockRegistry;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class BlockStateGen extends BlockStateProvider {
    public BlockStateGen(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Marioverse.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        String classicGoalPoleName = BuiltInRegistries.BLOCK.getKey(BlockRegistry.CLASSIC_GOAL_POLE.get()).getPath();
        String coinName = BuiltInRegistries.BLOCK.getKey(BlockRegistry.COIN.get()).getPath();
        String fungalStoneName = BuiltInRegistries.BLOCK.getKey(BlockRegistry.FUNGAL_STONE.get()).getPath();
        String deepFungalStoneName = BuiltInRegistries.BLOCK.getKey(BlockRegistry.DEEP_FUNGAL_STONE.get()).getPath();

        this.genButtons();
        this.genInvisibleQuestionBlocks();
        this.genPedestals();
        this.genPressurePlates();
        this.genQuestionBlocks();
        this.genSimpleBlockWithItem();
        this.genSlabs();
        this.genSmashableBlocks();
        this.genStairs();
        this.genStorageBricks();
        this.genWalls();

        this.coinModel(BlockRegistry.COIN.get(), coinName, modLoc("block/" + coinName));
        this.cubeAllModel(BlockRegistry.DEEP_FUNGAL_STONE.get(), deepFungalStoneName, modLoc("block/" + deepFungalStoneName));
        this.cubeAllModel(BlockRegistry.FUNGAL_STONE.get(), fungalStoneName, modLoc("block/" + fungalStoneName));
        this.goalPoleModel(BlockRegistry.CLASSIC_GOAL_POLE.get(), classicGoalPoleName, modLoc("block/" + classicGoalPoleName));

        for (Map.Entry<DyeColor, DeferredBlock<Block>> entry : BlockRegistry.GOAL_POLES.entrySet()) {
            String blockName = BuiltInRegistries.BLOCK.getKey(entry.getValue().get()).getPath();
            String removeColorName = blockName.replace(entry.getKey() + "_", "");
            ResourceLocation texture = modLoc("block/" + removeColorName);

            this.goalPoleModel(entry.getValue().get(), removeColorName, texture);
        }

        for (Map.Entry<DyeColor, DeferredBlock<Block>> entry : BlockRegistry.WARP_PIPES.entrySet()) {
            String blockName = BuiltInRegistries.BLOCK.getKey(entry.getValue().get()).getPath();
            ResourceLocation entranceTexture = modLoc("block/" + blockName + "_entrance_side");
            ResourceLocation sideTexture = modLoc("block/" + blockName + "_side");
            ResourceLocation bottomTexture = modLoc("block/" + blockName + "_bottom");
            ResourceLocation topTexture = modLoc("block/" + blockName + "_top");
            ResourceLocation topClosedTexture = modLoc("block/" + blockName + "_top_closed");

            this.warpPipeModel(entry.getValue().get(), blockName, entranceTexture, sideTexture, bottomTexture, topTexture, topClosedTexture);
        }
    }

    private void genButtons() {
        BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> {
            blockFamily.getVariants().forEach((variant, block) -> {
                BlockFamilyExtended.Variant button = BlockFamilyExtended.Variant.BUTTON;

                if (variant == button && block instanceof ButtonBlock buttonBlock) {
                    String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
                    String removeButtonName = blockName.replace("_button", "").replace("brick", "bricks");
                    ResourceLocation texture;

                    if (block == BlockFamilyRegistry.AMETHYST.get(button)) {
                        texture = mcLoc("block/" + removeButtonName + "_block");
                        this.buttonBlock(buttonBlock, texture);
                        this.itemModels().buttonInventory(blockName, texture);
                    } else {
                        texture = modLoc("block/" + removeButtonName);
                        this.buttonBlock(buttonBlock, texture);
                        this.itemModels().buttonInventory(blockName, texture);
                    }
                }
            });
        });
    }

    private void genSimpleBlockWithItem() {
        BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> {
            blockFamily.getVariants().forEach((variant, block) -> {
                BlockFamilyExtended.Variant bricks = BlockFamilyExtended.Variant.BRICKS;
                BlockFamilyExtended.Variant chiseled = BlockFamilyExtended.Variant.CHISELED;
                BlockFamilyExtended.Variant cracked = BlockFamilyExtended.Variant.CRACKED;
                BlockFamilyExtended.Variant polished = BlockFamilyExtended.Variant.POLISHED;

                if (variant == bricks || variant == chiseled || variant == cracked || variant == polished) {
                    String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
                    ResourceLocation texture = modLoc("block/" + blockName);
                    this.cubeAllModel(block, blockName, texture);
                }
            });
        });
    }

    private void genInvisibleQuestionBlocks() {
        BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> {
            blockFamily.getVariants().forEach((variant, block) -> {
                BlockFamilyExtended.Variant questionBlock = BlockFamilyExtended.Variant.INVISIBLE_QUESTION_BLOCK;

                if (variant == questionBlock) {
                    String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
                    ResourceLocation emptyTexture;
                    ResourceLocation mainTexture;
                    ResourceLocation sideTexture;
                    ResourceLocation topTexture;
                    ResourceLocation invisibleTexture;

                    if (block == BlockFamilyRegistry.POLISHED_AMETHYST.get(questionBlock)
                            || block == BlockFamilyRegistry.POLISHED_DEEP_FUNGAL_STONE.get(questionBlock)
                            || block == BlockFamilyRegistry.POLISHED_FUNGAL_STONE.get(questionBlock)) {
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

    private void genPedestals() {
        BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> {
            blockFamily.getVariants().forEach((variant, block) -> {
                BlockFamilyExtended.Variant pedestal = BlockFamilyExtended.Variant.PEDESTAL;

                if (variant == pedestal) {
                    String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
                    String removePedestalName = blockName.replace("_pedestal", "s");
                    ResourceLocation texture;

                    if (block == BlockFamilyRegistry.AMETHYST_BRICKS.get(pedestal)
                            || block == BlockFamilyRegistry.DEEP_FUNGAL_BRICKS.get(pedestal)
                            || block == BlockFamilyRegistry.FUNGAL_BRICKS.get(pedestal)) {
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

    private void genPressurePlates() {
        BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> {
            blockFamily.getVariants().forEach((variant, block) -> {
                BlockFamilyExtended.Variant pressurePlate = BlockFamilyExtended.Variant.PRESSURE_PLATE;

                if (variant == pressurePlate && block instanceof PressurePlateBlock pressurePlateBlock) {
                    String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
                    String removePressurePlateName = blockName.replace("_pressure_plate", "").replace("brick", "bricks");
                    ResourceLocation texture;

                    if (block == BlockFamilyRegistry.AMETHYST.get(pressurePlate)) {
                        texture = mcLoc("block/" + removePressurePlateName + "_block");
                        this.pressurePlateBlock(pressurePlateBlock, texture);
                        this.itemModels().withExistingParent(blockName, modLoc("block/" + blockName));
                    } else {
                        texture = modLoc("block/" + removePressurePlateName);
                        this.pressurePlateBlock(pressurePlateBlock, texture);
                        this.itemModels().withExistingParent(blockName, modLoc("block/" + blockName));
                    }
                }
            });
        });
    }

    private void genQuestionBlocks() {
        BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> {
            blockFamily.getVariants().forEach((variant, block) -> {
                BlockFamilyExtended.Variant questionBlock = BlockFamilyExtended.Variant.QUESTION_BLOCK;

                if (variant == questionBlock) {
                    String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
                    ResourceLocation emptyTexture;
                    ResourceLocation mainTexture;
                    ResourceLocation sideTexture;
                    ResourceLocation topTexture;

                    if (block == BlockFamilyRegistry.POLISHED_AMETHYST.get(questionBlock)
                            || block == BlockFamilyRegistry.POLISHED_DEEP_FUNGAL_STONE.get(questionBlock)
                            || block == BlockFamilyRegistry.POLISHED_FUNGAL_STONE.get(questionBlock)) {
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

    private void genSlabs() {
        BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> {
            blockFamily.getVariants().forEach((variant, block) -> {
                BlockFamilyExtended.Variant slab = BlockFamilyExtended.Variant.SLAB;

                if (variant == slab && block instanceof SlabBlock slabBlock) {
                    String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
                    String removeSlabName = blockName.replace("_slab", "").replace("brick", "bricks");
                    ResourceLocation texture;
                    ResourceLocation topTexture;

                    if (block == BlockFamilyRegistry.AMETHYST.get(slab)) {
                        texture = mcLoc("block/" + removeSlabName + "_block");
                        this.slabBlock(slabBlock, texture, texture);
                        this.itemModels().withExistingParent(blockName, modLoc("block/" + blockName));
                    } else if (block == BlockFamilyRegistry.POLISHED_AMETHYST.get(slab)
                            || block == BlockFamilyRegistry.POLISHED_DEEP_FUNGAL_STONE.get(slab)
                            || block == BlockFamilyRegistry.POLISHED_FUNGAL_STONE.get(slab)) {
                        texture = modLoc("block/" + blockName);
                        topTexture = modLoc("block/" + removeSlabName);
                        this.slabDoubleBlock(slabBlock, blockName, texture, topTexture, topTexture);
                        this.itemModels().withExistingParent(blockName, texture);
                    } else {
                        texture = modLoc("block/" + removeSlabName);
                        this.slabBlock(slabBlock, texture, texture);
                        this.itemModels().withExistingParent(blockName, modLoc("block/" + blockName));
                    }
                }
            });
        });
    }

    private void genSmashableBlocks() {
        BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> {
            blockFamily.getVariants().forEach((variant, block) -> {
                BlockFamilyExtended.Variant smashableBlock = BlockFamilyExtended.Variant.SMASHABLE_BLOCKS;

                if (variant == smashableBlock) {
                    String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
                    String removeSmashableName = blockName.replace("smashable_", "");
                    ResourceLocation mainTexture;
                    ResourceLocation overlayTexture;

                    if (block == BlockFamilyRegistry.AMETHYST_BRICKS.get(smashableBlock)
                            || block == BlockFamilyRegistry.DEEP_FUNGAL_BRICKS.get(smashableBlock)
                            || block == BlockFamilyRegistry.FUNGAL_BRICKS.get(smashableBlock)) {
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

    private void genStairs() {
        BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> {
            blockFamily.getVariants().forEach((variant, block) -> {
                BlockFamilyExtended.Variant stairs = BlockFamilyExtended.Variant.STAIRS;

                if (variant == stairs && block instanceof StairBlock stairBlock) {
                    String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
                    String removeStairName = blockName.replace("_stairs", "").replace("brick", "bricks");
                    ResourceLocation texture;

                    if (block == BlockFamilyRegistry.AMETHYST.get(stairs)) {
                        texture = mcLoc("block/" + removeStairName + "_block");
                        this.stairsBlock(stairBlock, removeStairName, texture);
                        this.itemModels().withExistingParent(blockName, modLoc("block/" + blockName));
                    } else {
                        texture = modLoc("block/" + removeStairName);
                        this.stairsBlock(stairBlock, removeStairName, texture);
                        this.itemModels().withExistingParent(blockName, modLoc("block/" + blockName));
                    }
                }
            });
        });
    }

    private void genStorageBricks() {
        BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> {
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

                    if (block == BlockFamilyRegistry.AMETHYST_BRICKS.get(storageBrick)
                            || block == BlockFamilyRegistry.DEEP_FUNGAL_BRICKS.get(storageBrick)
                            || block == BlockFamilyRegistry.FUNGAL_BRICKS.get(storageBrick)) {
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

    private void genWalls() {
        BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> {
            blockFamily.getVariants().forEach((variant, block) -> {
                BlockFamilyExtended.Variant wall = BlockFamilyExtended.Variant.WALL;

                if (variant == wall && block instanceof WallBlock wallBlock) {
                    String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
                    String removeWallName = blockName.replace("_wall", "").replace("brick", "bricks");
                    ResourceLocation texture;

                    if (block == BlockFamilyRegistry.AMETHYST.get(wall)) {
                        texture = mcLoc("block/" + removeWallName + "_block");
                        this.wallBlock(wallBlock, removeWallName, texture);
                        this.itemModels().wallInventory(blockName, texture);
                    } else {
                        texture = modLoc("block/" + removeWallName);
                        this.wallBlock(wallBlock, removeWallName, texture);
                        this.itemModels().wallInventory(blockName, texture);
                    }
                }
            });
        });
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

    private void coinModel(Block block, String modelName, ResourceLocation mainTexture) {
        ModelFile model = models().getBuilder(modelName).texture("particle", mainTexture).renderType("cutout");

        VariantBlockStateBuilder variantBuilder = getVariantBuilder(block);
        variantBuilder.partialState().addModels(new ConfiguredModel(model));
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

    private void goalPoleModel(Block block, String modelName, ResourceLocation mainTexture) {
        ModelFile model = models()
                .withExistingParent(modelName, modLoc("block/template_goal_pole"))
                .texture("side", mainTexture);
        ModelFile modelNone = models()
                .withExistingParent(modelName + "_none", modLoc("block/template_goal_pole_none"))
                .texture("side", mainTexture + "_none");
        ModelFile modelTop = models()
                .withExistingParent(modelName + "_top", modLoc("block/template_goal_pole_top"))
                .texture("side", mainTexture + "_top");

        VariantBlockStateBuilder variantBuilder = getVariantBuilder(block);
        variantBuilder.partialState().with(GoalPoleBlock.COLUMN, ColumnBlockStates.BOTTOM).addModels(new ConfiguredModel(model));
        variantBuilder.partialState().with(GoalPoleBlock.COLUMN, ColumnBlockStates.MIDDLE).addModels(new ConfiguredModel(model));
        variantBuilder.partialState().with(GoalPoleBlock.COLUMN, ColumnBlockStates.TOP).addModels(new ConfiguredModel(modelTop));
        variantBuilder.partialState().with(GoalPoleBlock.COLUMN, ColumnBlockStates.NONE).addModels(new ConfiguredModel(modelNone));
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

    public void slabDoubleBlock(SlabBlock block, String modelName, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        slabBlock(block, models().slab(modelName, side, bottom, top),
                models().slabTop(modelName + "_top", side, bottom, top),
                models().withExistingParent(modelName + "_double", mcLoc("block/cube_bottom_top"))
                        .texture("side", side)
                        .texture("bottom", bottom)
                        .texture("top", top));
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

    private void warpPipeModel(Block block, String modelName, ResourceLocation entranceTexture, ResourceLocation bottomTexture,
                               ResourceLocation sideTexture, ResourceLocation topTexture, ResourceLocation topClosedTexture) {
        ModelFile model = models()
                .withExistingParent(modelName, mcLoc("minecraft:block/cube_bottom_top"))
                .texture("bottom", bottomTexture).texture("side", sideTexture).texture("top", bottomTexture);
        ModelFile modelEntrance = models()
                .withExistingParent(modelName + "_entrance", mcLoc("minecraft:block/cube_bottom_top"))
                .texture("bottom", bottomTexture).texture("side", entranceTexture).texture("top", topTexture);
        ModelFile modelClosed = models()
                .withExistingParent(modelName + "_entrance_closed", mcLoc("minecraft:block/cube_bottom_top"))
                .texture("bottom", bottomTexture).texture("side", entranceTexture).texture("top", topClosedTexture);

        simpleBlockItem(block, modelEntrance);

        VariantBlockStateBuilder variantBuilder = getVariantBuilder(block);

        for (Direction direction : Direction.values()) {
            int xRot = getXRotation(direction);
            int yRot = getYRotation(direction);

            for (boolean entrance : new boolean[]{false, true}) {
                for (boolean closed : new boolean[]{false, true}) {
                    for (boolean bubbles : new boolean[]{false, true}) {
                        for (boolean waterSpout : new boolean[]{false, true}) {
                            ModelFile selectedModel = getModelForState(model, modelEntrance, modelClosed, entrance, closed);

                            variantBuilder.partialState()
                                    .with(WarpPipeBlock.FACING, direction)
                                    .with(WarpPipeBlock.ENTRANCE, entrance)
                                    .with(WarpPipeBlock.CLOSED, closed)
                                    .with(WarpPipeBlock.BUBBLES, bubbles)
                                    .with(WarpPipeBlock.WATER_SPOUT, waterSpout)
                                    .addModels(new ConfiguredModel(selectedModel, xRot, yRot, false));
                        }
                    }
                }
            }
        }
    }

    private void clearWarpPipeModel(Block block, String modelName) {
        ModelFile baseModel = models()
                .withExistingParent(modelName, modLoc("block/clear_warp_pipe/clear_warp_pipe"));

        ModelFile entranceModel = models()
                .withExistingParent(modelName + "_entrance", modLoc("block/clear_warp_pipe/clear_warp_pipe_entrance"));

        ModelFile closedModel = models()
                .withExistingParent(modelName + "_closed", modLoc("block/clear_warp_pipe/clear_warp_pipe_closed"));

        ModelFile entranceClosedModel = models()
                .withExistingParent(modelName + "_entrance_closed", modLoc("block/clear_warp_pipe/clear_warp_pipe_entrance_closed"));

        // Directional Models
        ModelFile northModel = models()
                .withExistingParent(modelName + "_n", modLoc("block/clear_warp_pipe/clear_warp_pipe_n"));
        ModelFile southModel = models()
                .withExistingParent(modelName + "_s", modLoc("block/clear_warp_pipe/clear_warp_pipe_s"));
        ModelFile eastModel = models()
                .withExistingParent(modelName + "_e", modLoc("block/clear_warp_pipe/clear_warp_pipe_e"));
        ModelFile westModel = models()
                .withExistingParent(modelName + "_w", modLoc("block/clear_warp_pipe/clear_warp_pipe_w"));
        ModelFile upModel = models()
                .withExistingParent(modelName + "_u", modLoc("block/clear_warp_pipe/clear_warp_pipe_u"));
        ModelFile downModel = models()
                .withExistingParent(modelName + "_d", modLoc("block/clear_warp_pipe/clear_warp_pipe_d"));

        // Multi-direction Models
        ModelFile nsModel = models()
                .withExistingParent(modelName + "_ns", modLoc("block/clear_warp_pipe/clear_warp_pipe_ns"));
        ModelFile ewModel = models()
                .withExistingParent(modelName + "_ew", modLoc("block/clear_warp_pipe/clear_warp_pipe_ew"));
        ModelFile udModel = models()
                .withExistingParent(modelName + "_ud", modLoc("block/clear_warp_pipe/clear_warp_pipe_ud"));
        ModelFile nsewModel = models()
                .withExistingParent(modelName + "_nsew", modLoc("block/clear_warp_pipe/clear_warp_pipe_nsew"));

        VariantBlockStateBuilder variantBuilder = getVariantBuilder(block);

        // Base Pipe (No entrance or closed)
        variantBuilder.partialState()
                .addModels(new ConfiguredModel(baseModel));

        // Entrance Open and Closed Variants
        variantBuilder.partialState().with(ClearWarpPipeBlock.ENTRANCE, true)
                .with(ClearWarpPipeBlock.CLOSED, false)
                .addModels(new ConfiguredModel(entranceModel));

        variantBuilder.partialState().with(ClearWarpPipeBlock.ENTRANCE, true)
                .with(ClearWarpPipeBlock.CLOSED, true)
                .addModels(new ConfiguredModel(entranceClosedModel));

        // Closed Pipe Variant (without "entrance")
        variantBuilder.partialState().with(ClearWarpPipeBlock.CLOSED, true)
                .addModels(new ConfiguredModel(closedModel));

        // Single Connection States
        variantBuilder.partialState().with(ClearWarpPipeBlock.NORTH, true)
                .addModels(new ConfiguredModel(northModel));
        variantBuilder.partialState().with(ClearWarpPipeBlock.SOUTH, true)
                .addModels(new ConfiguredModel(southModel));
        variantBuilder.partialState().with(ClearWarpPipeBlock.EAST, true)
                .addModels(new ConfiguredModel(eastModel));
        variantBuilder.partialState().with(ClearWarpPipeBlock.WEST, true)
                .addModels(new ConfiguredModel(westModel));
        variantBuilder.partialState().with(ClearWarpPipeBlock.UP, true)
                .addModels(new ConfiguredModel(upModel));
        variantBuilder.partialState().with(ClearWarpPipeBlock.DOWN, true)
                .addModels(new ConfiguredModel(downModel));

        // Multi-Directional Connection States
        variantBuilder.partialState().with(ClearWarpPipeBlock.NORTH, true).with(ClearWarpPipeBlock.SOUTH, true)
                .addModels(new ConfiguredModel(nsModel));
        variantBuilder.partialState().with(ClearWarpPipeBlock.EAST, true).with(ClearWarpPipeBlock.WEST, true)
                .addModels(new ConfiguredModel(ewModel));
        variantBuilder.partialState().with(ClearWarpPipeBlock.UP, true).with(ClearWarpPipeBlock.DOWN, true)
                .addModels(new ConfiguredModel(udModel));
        variantBuilder.partialState().with(ClearWarpPipeBlock.NORTH, true).with(ClearWarpPipeBlock.SOUTH, true)
                .with(ClearWarpPipeBlock.EAST, true).with(ClearWarpPipeBlock.WEST, true)
                .addModels(new ConfiguredModel(nsewModel));
    }

    private int getXRotation(Direction direction) {
        return switch (direction) {
            case UP -> 0;
            case DOWN -> 180;
            case NORTH, SOUTH, EAST, WEST -> 90;
        };
    }

    private int getYRotation(Direction direction) {
        return switch (direction) {
            case NORTH -> 0;
            case SOUTH -> 180;
            case EAST -> 90;
            case WEST -> 270;
            default -> 0;
        };
    }

    private ModelFile getModelForState(ModelFile model, ModelFile modelEntrance, ModelFile modelClosed, boolean entrance, boolean closed) {
        if (entrance)
            return closed ? modelClosed : modelEntrance;
        return model;
    }
}
