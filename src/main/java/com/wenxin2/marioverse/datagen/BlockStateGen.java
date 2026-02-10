package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.BrickPedestalBlock;
import com.wenxin2.marioverse.blocks.BridgeBlock;
import com.wenxin2.marioverse.blocks.ClearWarpPipeBlock;
import com.wenxin2.marioverse.blocks.GoalPoleBlock;
import com.wenxin2.marioverse.blocks.InvisibleQuestionBlock;
import com.wenxin2.marioverse.blocks.OnBlock;
import com.wenxin2.marioverse.blocks.PanelBlock;
import com.wenxin2.marioverse.blocks.QuestionBlock;
import com.wenxin2.marioverse.blocks.QuestionPanelBlock;
import com.wenxin2.marioverse.blocks.SpikePanelBlock;
import com.wenxin2.marioverse.blocks.SplunkinCarvedPumpkinBlock;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.WaterSpoutBlock;
import com.wenxin2.marioverse.blocks.states.ColumnBlockStates;
import com.wenxin2.marioverse.blocks.states.HalfBlockStates;
import com.wenxin2.marioverse.data.BlockFamilyExtended;
import com.wenxin2.marioverse.registries.BlockFamilyRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class BlockStateGen extends BlockStateProvider {
    public BlockStateGen(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Marioverse.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        String blueBlockName = BuiltInRegistries.BLOCK.getKey(BlockRegistry.BLUE_DOTTED_LINE_BLOCK.get()).getPath();
        String blueMushroomTrampolineName = BuiltInRegistries.BLOCK.getKey(BlockRegistry.BLUE_MUSHROOM_TRAMPOLINE.get()).getPath();
        String blueTrampolineCapName = BuiltInRegistries.BLOCK.getKey(BlockRegistry.BLUE_TRAMPOLINE_CAP.get()).getPath();
        String calciteCheckeredName = BuiltInRegistries.BLOCK.getKey(BlockRegistry.CALCITE_CHECKERED_TILES.get()).getPath();
        String classicCheckpointName = BuiltInRegistries.BLOCK.getKey(BlockRegistry.CLASSIC_CHECKPOINT_FLAG.get()).getPath();
        String classicGoalPoleName = BuiltInRegistries.BLOCK.getKey(BlockRegistry.CLASSIC_GOAL_POLE.get()).getPath();
        String coinName = BuiltInRegistries.BLOCK.getKey(BlockRegistry.COIN.get()).getPath();
        String dangoBlossomName = BuiltInRegistries.BLOCK.getKey(BlockRegistry.DANGO_BLOSSOM.get()).getPath();
        String deepFungalStoneName = BuiltInRegistries.BLOCK.getKey(BlockRegistry.DEEP_FUNGAL_STONE.get()).getPath();
        String fungalStoneName = BuiltInRegistries.BLOCK.getKey(BlockRegistry.FUNGAL_STONE.get()).getPath();
        String glowBlockName = BuiltInRegistries.BLOCK.getKey(BlockRegistry.GLOW_BLOCK.get()).getPath();
        String ironSpikeName = BuiltInRegistries.BLOCK.getKey(BlockRegistry.IRON_SPIKE.get()).getPath();
        String offSwitchName = "off_switch";
        String onSwitchName = "on_switch";
        String pumpkinName = BuiltInRegistries.BLOCK.getKey(Blocks.PUMPKIN).getPath();
        String quicksandName = BuiltInRegistries.BLOCK.getKey(BlockRegistry.QUICKSAND.get()).getPath();
        String redBlockName = BuiltInRegistries.BLOCK.getKey(BlockRegistry.RED_DOTTED_LINE_BLOCK.get()).getPath();
        String redMushroomTrampolineName = BuiltInRegistries.BLOCK.getKey(BlockRegistry.RED_MUSHROOM_TRAMPOLINE.get()).getPath();
        String redQuicksandName = BuiltInRegistries.BLOCK.getKey(BlockRegistry.RED_QUICKSAND.get()).getPath();
        String redTrampolineCapName = BuiltInRegistries.BLOCK.getKey(BlockRegistry.RED_TRAMPOLINE_CAP.get()).getPath();
        String spikePanelName = BuiltInRegistries.BLOCK.getKey(BlockRegistry.SPIKE_PANEL.get()).getPath();
        String splunkinCarvedPumpkinName = BuiltInRegistries.BLOCK.getKey(BlockRegistry.SPLUNKIN_CARVED_PUMPKIN.get()).getPath();
        String splunkinOLanternName = BuiltInRegistries.BLOCK.getKey(BlockRegistry.SPLUNKIN_O_LANTERN.get()).getPath();
        String starCoinName = BuiltInRegistries.BLOCK.getKey(BlockRegistry.STAR_COIN.get()).getPath();
        String waterSpoutName = BuiltInRegistries.BLOCK.getKey(BlockRegistry.WATER_SPOUT.get()).getPath();

        this.cubeAllModel(BlockRegistry.DEEP_FUNGAL_STONE.get(), modLoc("block/" + deepFungalStoneName));
        this.cubeAllModel(BlockRegistry.FUNGAL_STONE.get(), modLoc("block/" + fungalStoneName));
        this.cubeInnerOverlayModel(BlockRegistry.QUICKSAND.get(), modLoc("block/" + quicksandName + "_top"),
                modLoc("block/" + quicksandName), modLoc("block/" + quicksandName + "_top"));
        this.cubeInnerOverlayModel(BlockRegistry.RED_QUICKSAND.get(), modLoc("block/" + redQuicksandName + "_top"),
                modLoc("block/" + redQuicksandName), modLoc("block/" + redQuicksandName + "_top"));
        this.cubeMirroredNSModel(BlockRegistry.CALCITE_CHECKERED_TILES.get(), modLoc("block/" + calciteCheckeredName));
        this.blossomModel(BlockRegistry.DANGO_BLOSSOM.get(), modLoc("block/" + dangoBlossomName),
                modLoc("block/" + dangoBlossomName + "_leaves"));
        this.dottedLineBlockModel(BlockRegistry.BLUE_DOTTED_LINE_BLOCK.get(), modLoc("block/" + blueBlockName + "_off"), modLoc("block/" + blueBlockName), false);
        this.dottedLineBlockModel(BlockRegistry.RED_DOTTED_LINE_BLOCK.get(), modLoc("block/" + redBlockName), modLoc("block/" + redBlockName + "_off"), true);
        this.emptyModel(BlockRegistry.CLASSIC_CHECKPOINT_FLAG.get(), modLoc("item/" + classicCheckpointName));
        this.emptyModel(BlockRegistry.COIN.get(), modLoc("block/" + coinName));
        this.emptyModel(BlockRegistry.STAR_COIN.get(), modLoc("block/" + starCoinName));
        this.goalPoleModel(BlockRegistry.CLASSIC_GOAL_POLE.get(), classicGoalPoleName, modLoc("block/" + classicGoalPoleName));
        this.horizontalModel(BlockRegistry.GLOW_BLOCK.get(), modLoc("block/" + glowBlockName + "_front"),
                modLoc("block/" + glowBlockName), modLoc("block/" + glowBlockName));
        this.horizontalModel(BlockRegistry.SPLUNKIN_CARVED_PUMPKIN.get(), modLoc("block/" + splunkinCarvedPumpkinName),
                mcLoc("block/" + pumpkinName + "_side"), mcLoc("block/" + pumpkinName + "_top"));
        this.ironSpikeModel(BlockRegistry.IRON_SPIKE.get(), modLoc("block/" + ironSpikeName));
        this.mushroomTrampolineCapModel(BlockRegistry.RED_TRAMPOLINE_CAP.get(), modLoc("block/" + redTrampolineCapName));
        this.mushroomTrampolineCapSwappedModel(BlockRegistry.BLUE_TRAMPOLINE_CAP.get(), modLoc("block/" + blueTrampolineCapName));
        this.mushroomTrampolineModel(BlockRegistry.RED_MUSHROOM_TRAMPOLINE.get(), modLoc("block/" + redMushroomTrampolineName));
        this.mushroomTrampolineSwappedModel(BlockRegistry.BLUE_MUSHROOM_TRAMPOLINE.get(), modLoc("block/" + blueMushroomTrampolineName));
        this.onOffSwitchModel(BlockRegistry.ON_OFF_SWITCH.get(), modLoc("block/" + onSwitchName), modLoc("block/" + onSwitchName + "_top"),
                modLoc("block/" + offSwitchName), modLoc("block/" + offSwitchName + "_top"));
        this.pipeBubblesModel(BlockRegistry.PIPE_BUBBLES.get());
        this.pottedBlossomModel(BlockRegistry.POTTED_DANGO_BLOSSOM.get(), modLoc("block/" + "potted_" + dangoBlossomName),
                modLoc("block/" + "potted_" + dangoBlossomName + "_leaves"));
        this.spikePanelModel(BlockRegistry.SPIKE_PANEL.get(), modLoc("block/" + spikePanelName));
        this.splunkinOLanternModel(BlockRegistry.SPLUNKIN_O_LANTERN.get(), modLoc("block/" + splunkinOLanternName),
                mcLoc("block/" + pumpkinName + "_side"), mcLoc("block/" + pumpkinName + "_top"),
                modLoc("block/" + splunkinOLanternName + "_cracked"),
                modLoc("block/" + splunkinOLanternName + "_cracked_side"), modLoc("block/" + splunkinOLanternName + "_cracked_top"));
        this.waterSpoutModel(BlockRegistry.WATER_SPOUT.get(), modLoc("block/" + waterSpoutName + "_flow"),
                modLoc("block/" + waterSpoutName + "_still"), modLoc("block/" + waterSpoutName + "_splash"));

        this.genBridges();
        this.genBridgeStairs();
        this.genButtons();
        this.genInvisibleQuestionBlocks();
        this.genPedestals();
        this.genPressurePlates();
        this.genQuestionBlocks();
        this.genQuestionPanels();
        this.genSimpleBlockWithItem();
        this.genSlabs();
        this.genSmashableBlocks();
        this.genStairs();
        this.genStorageBricks();
        this.genWalls();

        for (Map.Entry<DyeColor, DeferredBlock<Block>> entry : BlockRegistry.CALCITE.entrySet()) {
            String blockName = BuiltInRegistries.BLOCK.getKey(entry.getValue().get()).getPath();
            ResourceLocation texture = modLoc("block/" + blockName);

            this.cubeAllModel(entry.getValue().get(), texture);
        }

        for (Map.Entry<DyeColor, DeferredBlock<Block>> entry : BlockRegistry.CHECKPOINT_FLAGS.entrySet()) {
            String blockName = BuiltInRegistries.BLOCK.getKey(entry.getValue().get()).getPath();
            ResourceLocation texture = modLoc("item/" + blockName);

            this.emptyModel(entry.getValue().get(), texture);
        }

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

            this.warpPipeModel(entry.getValue().get(), entranceTexture, bottomTexture, sideTexture, topTexture, topClosedTexture);
        }
    }

    private void genBridges() {
        BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> blockFamily.getVariants().forEach((variant, block) -> {
            BlockFamilyExtended.Variant bridge = BlockFamilyExtended.Variant.BRIDGE;
            String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
            String removeBridgeName = blockName.replace("_bridge", "");

            ResourceLocation sideTexture = mcLoc("block/" + removeBridgeName);
            ResourceLocation topTexture = mcLoc("block/" + removeBridgeName + "_top");
            ResourceLocation ropeTexture = modLoc("block/bridge_rope");
            ResourceLocation ropeSideTexture = modLoc("block/bridge_rope_side");

            if (variant == bridge) {
                if (block == BlockFamilyRegistry.BAMBOO_BLOCK.get(bridge)
                        || block == BlockFamilyRegistry.STRIPPED_BAMBOO_BLOCK.get(bridge)) {
                    removeBridgeName = blockName.replace("_bridge", "_block");
                    sideTexture = mcLoc("block/" + removeBridgeName);
                    topTexture = mcLoc("block/" + removeBridgeName + "_top");
                    ResourceLocation sideBridgeTexture = modLoc("block/" + blockName + "_side");

                    this.bambooBridgeModel(block, sideTexture, topTexture, sideBridgeTexture, ropeTexture, ropeSideTexture);
                } else this.bridgeModel(block, sideTexture, topTexture, ropeTexture, ropeSideTexture);
            }
        }));
    }

    private void genBridgeStairs() {
        BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> blockFamily.getVariants().forEach((variant, block) -> {
            BlockFamilyExtended.Variant bridgeStairs = BlockFamilyExtended.Variant.BRIDGE_STAIRS;
            String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
            String removeBridgeName = blockName.replace("_bridge_stairs", "");

            ResourceLocation sideTexture = mcLoc("block/" + removeBridgeName);
            ResourceLocation topTexture = mcLoc("block/" + removeBridgeName + "_top");
            ResourceLocation ropeTexture = modLoc("block/bridge_rope");
            ResourceLocation ropeSideTexture = modLoc("block/bridge_rope_side");
            ResourceLocation ropeKnotTexture = modLoc("block/bridge_rope_knot");

            if (variant == bridgeStairs) {
                if (block == BlockFamilyRegistry.BAMBOO_BLOCK.get(bridgeStairs)
                        || block == BlockFamilyRegistry.STRIPPED_BAMBOO_BLOCK.get(bridgeStairs)) {
                    removeBridgeName = blockName.replace("_bridge_stairs", "_block");
                    String removeStairsName = blockName.replace("_stairs", "");
                    sideTexture = mcLoc("block/" + removeBridgeName);
                    topTexture = mcLoc("block/" + removeBridgeName + "_top");
                    ResourceLocation sideBridgeTexture = modLoc("block/" + removeStairsName + "_side");

                    this.bambooBridgeStairsModel(block, sideTexture, topTexture, sideBridgeTexture, ropeTexture, ropeSideTexture, ropeKnotTexture);
                } else this.bridgeStairsModel(block, sideTexture, topTexture, ropeTexture, ropeSideTexture, ropeKnotTexture);
            }
        }));
    }

    private void genButtons() {
        BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> blockFamily.getVariants().forEach((variant, block) -> {
            BlockFamilyExtended.Variant button = BlockFamilyExtended.Variant.BUTTON;

            if (variant == button && block instanceof ButtonBlock buttonBlock) {
                String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
                String removeButtonName = blockName.replace("_button", "").replace("brick", "bricks");
                ResourceLocation texture;

                if (block == BlockFamilyRegistry.AMETHYST.get(button)) {
                    texture = mcLoc("block/" + removeButtonName + "_block");
                    this.buttonBlock(buttonBlock, texture);
                    this.itemModels().buttonInventory(blockName, texture);
                } else if (block == BlockFamilyRegistry.CALCITE.get(button)) {
                    texture = mcLoc("block/" + removeButtonName);
                    this.buttonBlock(buttonBlock, texture);
                    this.itemModels().buttonInventory(blockName, texture);
                } else {
                    texture = modLoc("block/" + removeButtonName);
                    this.buttonBlock(buttonBlock, texture);
                    this.itemModels().buttonInventory(blockName, texture);
                }
            }
        }));
    }

    private void genSimpleBlockWithItem() {
        BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> blockFamily.getVariants().forEach((variant, block) -> {
            BlockFamilyExtended.Variant bricks = BlockFamilyExtended.Variant.BRICKS;
            BlockFamilyExtended.Variant chiseled = BlockFamilyExtended.Variant.CHISELED;
            BlockFamilyExtended.Variant cracked = BlockFamilyExtended.Variant.CRACKED;
            BlockFamilyExtended.Variant polished = BlockFamilyExtended.Variant.POLISHED;

            if (variant == bricks || variant == chiseled || variant == cracked || variant == polished) {
                String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
                ResourceLocation mainTexture = modLoc("block/" + blockName);
                ResourceLocation topTexture = modLoc("block/" + blockName + "_top");

                if (blockName.startsWith("chiseled_deep_fungal_bricks")
                        || blockName.startsWith("chiseled_fungal_bricks"))
                    this.cubeBottomTopModel(block, topTexture, mainTexture, topTexture);
                else this.cubeAllModel(block, mainTexture);
            }
        }));
    }

    private void genInvisibleQuestionBlocks() {
        BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> blockFamily.getVariants().forEach((variant, block) -> {
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
                } else if (block == BlockFamilyRegistry.POLISHED_CALCITE.get(questionBlock)) {
                    String removeInvisibleName = blockName.replace("invisible_", "");
                    sideTexture = modLoc("block/" + removeInvisibleName + "_side");
                    topTexture = modLoc("block/" + removeInvisibleName + "_top");
                    emptyTexture = modLoc("block/empty_" + removeInvisibleName);
                    invisibleTexture = modLoc("block/invisible_question_block");

                    this.invisibleQuestionBlockModel(block, removeInvisibleName, sideTexture, topTexture, emptyTexture, invisibleTexture);
                } else if (block == BlockFamilyRegistry.CUT_RED_SANDSTONE.get(questionBlock)
                        || block == BlockFamilyRegistry.CUT_SANDSTONE.get(questionBlock)) {
                    String removeInvisibleName = blockName.replace("invisible_", "");
                    String removeQuestionBlockName = removeInvisibleName.replace("_question_block", "");
                    sideTexture = modLoc("block/" + removeInvisibleName + "_side");
                    topTexture = mcLoc("block/" + removeQuestionBlockName + "_top");
                    emptyTexture = modLoc("block/empty_" + removeInvisibleName);
                    invisibleTexture = modLoc("block/invisible_question_block");

                    this.invisibleQuestionBlockSandstoneModel(block, removeInvisibleName, sideTexture, topTexture, emptyTexture, invisibleTexture);
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
        }));
    }

    private void genPedestals() {
        BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> blockFamily.getVariants().forEach((variant, block) -> {
            BlockFamilyExtended.Variant pedestal = BlockFamilyExtended.Variant.PEDESTAL;

            if (variant == pedestal) {
                String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
                String removePedestalName = blockName.replace("_pedestal", "s");
                ResourceLocation texture;

                if (block == BlockFamilyRegistry.BRICKS.get(pedestal)
                        || block == BlockFamilyRegistry.DEEPSLATE_BRICKS.get(pedestal)
                        || block == BlockFamilyRegistry.DEEPSLATE_TILES.get(pedestal)
                        || block == BlockFamilyRegistry.END_STONE_BRICKS.get(pedestal)
                        || block == BlockFamilyRegistry.MOSSY_STONE_BRICKS.get(pedestal)
                        || block == BlockFamilyRegistry.MUD_BRICKS.get(pedestal)
                        || block == BlockFamilyRegistry.NETHER_BRICKS.get(pedestal)
                        || block == BlockFamilyRegistry.PRISMARINE_BRICKS.get(pedestal)
                        || block == BlockFamilyRegistry.QUARTZ_BRICKS.get(pedestal)
                        || block == BlockFamilyRegistry.RED_NETHER_BRICKS.get(pedestal)
                        || block == BlockFamilyRegistry.STONE_BRICKS.get(pedestal)
                        || block == BlockFamilyRegistry.TUFF_BRICKS.get(pedestal)) {
                    texture = mcLoc("minecraft:block/" + removePedestalName);

                    this.pedestalModel(block, texture);
                } else if (block == BlockFamilyRegistry.POLISHED_DEEP_FUNGAL_BRICKS.get(pedestal)
                        || block == BlockFamilyRegistry.POLISHED_FUNGAL_BRICKS.get(pedestal)) {
                    texture = modLoc("block/" + removePedestalName);

                    this.largeBrickPedestalModel(block, texture);
                } else if (blockName.startsWith("waxed_")) {
                    String unWaxedName = blockName.replace("waxed_", "");
                    removePedestalName = unWaxedName.replace("_pedestal", "");
                    texture = mcLoc("minecraft:block/" + removePedestalName);

                    this.pedestalModel(block, texture);
                } else if (blockName.endsWith("_copper_pedestal") || blockName.endsWith("_block_pedestal")
                        || blockName.endsWith("_prismarine_pedestal")) {
                    String unWaxedName = blockName.replace("waxed_", "");
                    removePedestalName = unWaxedName.replace("_pedestal", "");
                    texture = mcLoc("minecraft:block/" + removePedestalName);

                    this.pedestalModel(block, texture);
                } else if (blockName.startsWith("blackstone_")) {
                    String blackstoneName = blockName.replace("blackstone_", "polished_blackstone_");
                    removePedestalName = blackstoneName.replace("_pedestal", "s");
                    texture = mcLoc("minecraft:block/" + removePedestalName);

                    this.pedestalModel(block, texture);
                } else {
                    texture = modLoc("block/" + removePedestalName);
                    this.pedestalModel(block, texture);
                }
            }
        }));
    }

    private void genPressurePlates() {
        BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> blockFamily.getVariants().forEach((variant, block) -> {
            BlockFamilyExtended.Variant pressurePlate = BlockFamilyExtended.Variant.PRESSURE_PLATE;

            if (variant == pressurePlate && block instanceof PressurePlateBlock pressurePlateBlock) {
                String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
                String removePressurePlateName = blockName.replace("_pressure_plate", "").replace("brick", "bricks");
                ResourceLocation texture;

                if (block == BlockFamilyRegistry.AMETHYST.get(pressurePlate)) {
                    texture = mcLoc("block/" + removePressurePlateName + "_block");
                    this.pressurePlateBlock(pressurePlateBlock, texture);
                    this.itemModels().pressurePlate(blockName, texture);
                } else if (block == BlockFamilyRegistry.CALCITE.get(pressurePlate)) {
                    texture = mcLoc("block/" + removePressurePlateName);
                    this.pressurePlateBlock(pressurePlateBlock, texture);
                    this.itemModels().pressurePlate(blockName, texture);
                } else {
                    texture = modLoc("block/" + removePressurePlateName);
                    this.pressurePlateBlock(pressurePlateBlock, texture);
                    this.itemModels().pressurePlate(blockName, texture);
                }
            }
        }));
    }

    private void genQuestionBlocks() {
        BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> blockFamily.getVariants().forEach((variant, block) -> {
            BlockFamilyExtended.Variant questionBlock = BlockFamilyExtended.Variant.QUESTION_BLOCK;
            BlockFamilyExtended.Variant questionBlockTag = BlockFamilyExtended.Variant.QUESTION_BLOCK_TAG;

            if (variant == questionBlock || variant == questionBlockTag) {
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

                    this.questionBlockModel(block, sideTexture, topTexture, emptyTexture);
                } else if (block == BlockFamilyRegistry.POLISHED_CALCITE.get(questionBlockTag)) {
                    sideTexture = modLoc("block/" + blockName + "_side");
                    topTexture = modLoc("block/" + blockName + "_top");
                    emptyTexture = modLoc("block/empty_" + blockName);

                    this.questionBlockModel(block, sideTexture, topTexture, emptyTexture);
                } else if (block == BlockFamilyRegistry.PRISMARINE_BRICKS.get(questionBlock)) {
                    sideTexture = modLoc("block/" + blockName);
                    topTexture = modLoc("block/" + blockName + "_top");
                    emptyTexture = modLoc("block/empty_" + blockName);

                    this.questionBlockModel(block, sideTexture, topTexture, emptyTexture);
                } else if (block == BlockFamilyRegistry.CUT_RED_SANDSTONE.get(questionBlock)
                        || block == BlockFamilyRegistry.CUT_SANDSTONE.get(questionBlock)) {
                    String removeQuestionBlockName = blockName.replace("_question_block", "");

                    sideTexture = modLoc("block/" + blockName + "_side");
                    topTexture = mcLoc("block/" + removeQuestionBlockName + "_top");
                    emptyTexture = modLoc("block/empty_" + blockName);

                    this.questionBlockSandstoneModel(block, sideTexture, topTexture, emptyTexture);
                } else if (blockName.startsWith("waxed_")) {
                    String unWaxedName = blockName.replace("waxed_", "");
                    mainTexture = modLoc("block/" + unWaxedName);
                    emptyTexture = modLoc("block/empty_" + unWaxedName);

                    this.questionBlockModel(block, mainTexture, emptyTexture);
                } else {
                    mainTexture = modLoc("block/" + blockName);
                    emptyTexture = modLoc("block/empty_" + blockName);

                    this.questionBlockModel(block, mainTexture, emptyTexture);
                }
            }
        }));
    }

    private void genQuestionPanels() {
        BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> blockFamily.getVariants().forEach((variant, block) -> {
            BlockFamilyExtended.Variant questionPanel = BlockFamilyExtended.Variant.QUESTION_PANEL;

            if (variant == questionPanel) {
                String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
                ResourceLocation bottomTexture = modLoc("block/" + blockName + "_bottom");
                ResourceLocation offTexture = modLoc("block/" + blockName + "_off");
                ResourceLocation topTexture = modLoc("block/" + blockName);

                this.questionPanelModel(block, bottomTexture, topTexture, offTexture);
            }
        }));
    }

    private void genSlabs() {
        BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> blockFamily.getVariants().forEach((variant, block) -> {
            BlockFamilyExtended.Variant slab = BlockFamilyExtended.Variant.SLAB;

            if (variant == slab && block instanceof SlabBlock slabBlock) {
                String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
                String removeSlabName = blockName.replace("_slab", "").replace("brick", "bricks")
                        .replace("tile", "tiles");
                ResourceLocation texture;
                ResourceLocation topTexture;

                if (block == BlockFamilyRegistry.AMETHYST.get(slab)) {
                    texture = mcLoc("block/" + removeSlabName + "_block");
                    this.slabBlock(slabBlock, texture, texture);
                    this.itemModels().slab(blockName, texture, texture, texture);
                } else if (block == BlockFamilyRegistry.CALCITE.get(slab)) {
                    texture = mcLoc("block/" + removeSlabName );
                    this.slabBlock(slabBlock, texture, texture);
                    this.itemModels().slab(blockName, texture, texture, texture);
                } else if (block == BlockFamilyRegistry.CALCITE_CHECKERED_TILES.get(slab)) {
                    texture = modLoc("block/" + removeSlabName );
                    this.slabMirroredNSModel(slabBlock, texture);
                } else if (block == BlockFamilyRegistry.POLISHED_AMETHYST.get(slab)
                        || block == BlockFamilyRegistry.POLISHED_CALCITE.get(slab)
                        || block == BlockFamilyRegistry.POLISHED_DEEP_FUNGAL_BRICKS.get(slab)
                        || block == BlockFamilyRegistry.POLISHED_DEEP_FUNGAL_STONE.get(slab)
                        || block == BlockFamilyRegistry.POLISHED_FUNGAL_BRICKS.get(slab)
                        || block == BlockFamilyRegistry.POLISHED_FUNGAL_STONE.get(slab)) {
                    texture = modLoc("block/" + blockName);
                    topTexture = modLoc("block/" + removeSlabName);
                    this.slabDoubleBlock(slabBlock, texture, topTexture, topTexture);
                    this.itemModels().slab(blockName, texture, topTexture, topTexture);
                } else {
                    texture = modLoc("block/" + removeSlabName);
                    this.slabBlock(slabBlock, texture, texture);
                    this.itemModels().slab(blockName, texture, texture, texture);
                }
            }
        }));
    }

    private void genSmashableBlocks() {
        BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> blockFamily.getVariants().forEach((variant, block) -> {
            BlockFamilyExtended.Variant smashableBlock = BlockFamilyExtended.Variant.SMASHABLE_BLOCKS;

            if (variant == smashableBlock) {
                String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
                String removeSmashableName = blockName.replace("smashable_", "");
                ResourceLocation mainTexture;
                ResourceLocation overlayTexture;

                if (block == BlockFamilyRegistry.AMETHYST_BRICKS.get(smashableBlock)
                        || block == BlockFamilyRegistry.DEEP_FUNGAL_BRICKS.get(smashableBlock)
                        || block == BlockFamilyRegistry.FUNGAL_BRICKS.get(smashableBlock)
                        || block == BlockFamilyRegistry.POLISHED_DEEP_FUNGAL_BRICKS.get(smashableBlock)
                        || block == BlockFamilyRegistry.POLISHED_FUNGAL_BRICKS.get(smashableBlock)) {
                    mainTexture = modLoc("block/" + removeSmashableName);
                    overlayTexture = modLoc("block/" + blockName + "_overlay");

                    this.cubeOverlayModel(block, mainTexture, overlayTexture);
                } else if (removeSmashableName.startsWith("waxed_")) {
                    String unWaxedName = blockName.replace("waxed_", "");
                    removeSmashableName = unWaxedName.replace("smashable_", "");
                    mainTexture = mcLoc("minecraft:block/" + removeSmashableName);
                    overlayTexture = modLoc("block/" + unWaxedName + "_overlay");

                    this.cubeOverlayModel(block, mainTexture, overlayTexture);
                } else if (removeSmashableName.startsWith("blackstone_")) {
                    String crackedBlockName = removeSmashableName.replace("blackstone_", "cracked_polished_blackstone_");
                    mainTexture = mcLoc("minecraft:block/" + crackedBlockName);

                    this.cubeAllModel(block, mainTexture);
                } else if (removeSmashableName.startsWith("deepslate_tiles")) {
                    String crackedBlockName = removeSmashableName.replace("deepslate_tiles", "cracked_deepslate_tiles");
                    mainTexture = mcLoc("minecraft:block/" + crackedBlockName);

                    this.cubeAllModel(block, mainTexture);
                } else if (removeSmashableName.startsWith("nether_")) {
                    String crackedBlockName = removeSmashableName.replace("nether_", "cracked_nether_");
                    mainTexture = mcLoc("minecraft:block/" + crackedBlockName);

                    this.cubeAllModel(block, mainTexture);
                } else if (removeSmashableName.startsWith("stone_")) {
                    String crackedBlockName = removeSmashableName.replace("stone_", "cracked_stone_");
                    mainTexture = mcLoc("minecraft:block/" + crackedBlockName);

                    this.cubeAllModel(block, mainTexture);
                } else {
                    mainTexture = mcLoc("minecraft:block/" + removeSmashableName);
                    overlayTexture = modLoc("block/" + blockName + "_overlay");

                    this.cubeOverlayModel(block, mainTexture, overlayTexture);
                }
            }
        }));
    }

    private void genStairs() {
        BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> blockFamily.getVariants().forEach((variant, block) -> {
            BlockFamilyExtended.Variant stairs = BlockFamilyExtended.Variant.STAIRS;

            if (variant == stairs && block instanceof StairBlock stairBlock) {
                String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
                String removeStairName = blockName.replace("_stairs", "").replace("brick", "bricks")
                        .replace("tile", "tiles");
                ResourceLocation texture;

                if (block == BlockFamilyRegistry.AMETHYST.get(stairs)) {
                    texture = mcLoc("block/" + removeStairName + "_block");
                    this.stairsBlock(stairBlock, removeStairName, texture);
                    this.itemModels().stairs(blockName, texture, texture, texture);
                } else if (block == BlockFamilyRegistry.CALCITE.get(stairs)) {
                    texture = mcLoc("block/" + removeStairName);
                    this.stairsBlock(stairBlock, removeStairName, texture);
                    this.itemModels().stairs(blockName, texture, texture, texture);
                } else if (block == BlockFamilyRegistry.CALCITE_CHECKERED_TILES.get(stairs)) {
                    texture = modLoc("block/" + removeStairName);
                    this.stairsMirroredNSModel(stairBlock, texture);
                } else {
                    texture = modLoc("block/" + removeStairName);
                    this.stairsBlock(stairBlock, removeStairName, texture);
                    this.itemModels().stairs(blockName, texture, texture, texture);
                }
            }
        }));
    }

    private void genStorageBricks() {
        BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> blockFamily.getVariants().forEach((variant, block) -> {
            BlockFamilyExtended.Variant storageBrick = BlockFamilyExtended.Variant.STORAGE_BRICKS;

            if (variant == storageBrick) {
                String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
                String removeStorageName = blockName.replace("storage_", "");
                String questionBlockName = removeStorageName
                        .replace("block", "question_block")
                        .replace("sandstone_bricks", "sandstone_question_block")
                        .replace("bricks", "question_bricks")
                        .replace("cut_copper", "copper_question_block")
                        .replace("dark_prismarine", "dark_prismarine_question_block")
                        .replace("tiles", "question_tiles");
                ResourceLocation mainTexture;
                ResourceLocation emptyTexture;
                ResourceLocation topTexture;

                if (block == BlockFamilyRegistry.BRICKS.get(storageBrick)
                        || block == BlockFamilyRegistry.CUT_COPPER.get(storageBrick)
                        || block == BlockFamilyRegistry.DARK_PRISMARINE.get(storageBrick)
                        || block == BlockFamilyRegistry.DEEPSLATE_BRICKS.get(storageBrick)
                        || block == BlockFamilyRegistry.DEEPSLATE_TILES.get(storageBrick)
                        || block == BlockFamilyRegistry.END_STONE_BRICKS.get(storageBrick)
                        || block == BlockFamilyRegistry.EXPOSED_CUT_COPPER.get(storageBrick)
                        || block == BlockFamilyRegistry.MOSSY_STONE_BRICKS.get(storageBrick)
                        || block == BlockFamilyRegistry.MUD_BRICKS.get(storageBrick)
                        || block == BlockFamilyRegistry.NETHER_BRICKS.get(storageBrick)
                        || block == BlockFamilyRegistry.OXIDIZED_CUT_COPPER.get(storageBrick)
                        || block == BlockFamilyRegistry.PRISMARINE_BRICKS.get(storageBrick)
                        || block == BlockFamilyRegistry.PURPUR_BLOCK.get(storageBrick)
                        || block == BlockFamilyRegistry.QUARTZ_BRICKS.get(storageBrick)
                        || block == BlockFamilyRegistry.RED_NETHER_BRICKS.get(storageBrick)
                        || block == BlockFamilyRegistry.STONE_BRICKS.get(storageBrick)
                        || block == BlockFamilyRegistry.TUFF_BRICKS.get(storageBrick)
                        || block == BlockFamilyRegistry.WEATHERED_CUT_COPPER.get(storageBrick)) {
                    mainTexture = mcLoc("minecraft:block/" + removeStorageName);
                    emptyTexture = modLoc("block/empty_" + questionBlockName);

                    this.storageBrickModel(block, mainTexture, emptyTexture);
                } else if (block == BlockFamilyRegistry.RED_SANDSTONE_BRICKS.get(storageBrick)
                        || block == BlockFamilyRegistry.SANDSTONE_BRICKS.get(storageBrick)) {
                    String removeBricksName = removeStorageName.replace("_bricks", "");
                    questionBlockName = removeStorageName
                            .replace("bricks", "question_block");

                    mainTexture = modLoc("block/" + removeStorageName);
                    emptyTexture = modLoc("block/empty_" + questionBlockName);
                    topTexture = mcLoc("block/" + removeBricksName + "_top");

                    this.storageBrickSandstoneModel(block, mainTexture, topTexture, emptyTexture);
                } else if (removeStorageName.startsWith("waxed_")) {
                    String unWaxedName = blockName.replace("waxed_", "");
                    removeStorageName = unWaxedName.replace("storage_", "");
                    questionBlockName = removeStorageName
                            .replace("cut_copper", "copper_question_block");
                    mainTexture = mcLoc("minecraft:block/" + removeStorageName);
                    emptyTexture = modLoc("block/empty_" + questionBlockName);

                    this.storageBrickModel(block, mainTexture, emptyTexture);
                } else if (questionBlockName.startsWith("blackstone_")) {
                    String crackedBlockName = removeStorageName.replace("blackstone_", "polished_blackstone_");
                    mainTexture = mcLoc("minecraft:block/" + crackedBlockName);
                    emptyTexture = modLoc("block/empty_" + questionBlockName);

                    this.storageBrickModel(block, mainTexture, emptyTexture);
                } else if (questionBlockName.contains("calcite_")) {
                    String emptyName = blockName.replace("storage_", "polished_")
                            .replace("_bricks", "");
                    mainTexture = modLoc("block/" + removeStorageName);
                    emptyTexture = modLoc("block/" + emptyName);

                    this.storageBrickModel(block, mainTexture, emptyTexture);
                } else {
                    questionBlockName = removeStorageName
                            .replace("bricks", "question_block");
                    if (questionBlockName.contains("polished_"))
                        questionBlockName = removeStorageName
                                .replace("bricks", "question_block")
                                .replace("polished_", "");
                    mainTexture = modLoc("block/" + removeStorageName);
                    emptyTexture = modLoc("block/empty_" + questionBlockName);

                    this.storageBrickModel(block, mainTexture, emptyTexture);
                }
            }
        }));
    }

    private void genWalls() {
        BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> blockFamily.getVariants().forEach((variant, block) -> {
            BlockFamilyExtended.Variant wall = BlockFamilyExtended.Variant.WALL;

            if (variant == wall && block instanceof WallBlock wallBlock) {
                String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
                String removeWallName = blockName.replace("_wall", "").replace("brick", "bricks")
                        .replace("tile", "tiles");
                ResourceLocation texture;

                if (block == BlockFamilyRegistry.AMETHYST.get(wall)) {
                    texture = mcLoc("block/" + removeWallName + "_block");
                    this.wallBlock(wallBlock, removeWallName, texture);
                    this.itemModels().wallInventory(blockName, texture);
                } else if (block == BlockFamilyRegistry.CALCITE.get(wall)) {
                    texture = mcLoc("block/" + removeWallName);
                    this.wallBlock(wallBlock, removeWallName, texture);
                    this.itemModels().wallInventory(blockName, texture);
                } else if (block == BlockFamilyRegistry.CALCITE_CHECKERED_TILES.get(wall)) {
                    texture = modLoc("block/" + removeWallName);
                    this.wallMirroredNSModel(wallBlock, texture);
                } else {
                    texture = modLoc("block/" + removeWallName);
                    this.wallBlock(wallBlock, removeWallName, texture);
                    this.itemModels().wallInventory(blockName, texture);
                }
            }
        }));
    }

    private void blossomModel(Block block, ResourceLocation blossomTexture, ResourceLocation leavesTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, modLoc("block/template_blossom"))
                .texture("blossom", blossomTexture).texture("leaves", leavesTexture);

        VariantBlockStateBuilder variantBuilder = this.getVariantBuilder(block);
        variantBuilder.partialState().addModels(new ConfiguredModel(model));

        this.simpleBlockItem(block, model);
    }

    private void bambooBridgeModel(Block block, ResourceLocation sideTexture, ResourceLocation topTexture, ResourceLocation sideBridgeTexture,
                                   ResourceLocation ropeTexture, ResourceLocation ropeSideTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile modelBottom = models()
                .withExistingParent(modelName, modLoc("block/template_bamboo_bridge"))
                .texture("side", sideTexture).texture("top", topTexture).texture("bridge_side", sideBridgeTexture)
                .texture("rope", ropeTexture).texture("rope_side", ropeSideTexture);
        ModelFile modelTop = models()
                .withExistingParent(modelName + "_top", modLoc("block/template_bamboo_bridge_top"))
                .texture("side", sideTexture).texture("top", topTexture).texture("bridge_side", sideBridgeTexture)
                .texture("rope", ropeTexture).texture("rope_side", ropeSideTexture);

        this.simpleBlockItem(block, modelBottom);

        this.getVariantBuilder(block).forAllStates(state -> {
            Direction.Axis axis = state.getValue(BridgeBlock.AXIS);
            HalfBlockStates half = state.getValue(BridgeBlock.HALF);
            ModelFile model = (half == HalfBlockStates.TOP ? modelTop : modelBottom);

            int yRot = (axis == Direction.Axis.X ? 90 : 0);

            return ConfiguredModel.builder().modelFile(model).rotationY(yRot).uvLock(false).build();
        });
    }

    private void bridgeModel(Block block, ResourceLocation sideTexture, ResourceLocation topTexture, ResourceLocation ropeTexture, ResourceLocation ropeSideTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile modelBottom = models()
                .withExistingParent(modelName, modLoc("block/template_log_bridge"))
                .texture("side", sideTexture).texture("top", topTexture)
                .texture("rope", ropeTexture).texture("rope_side", ropeSideTexture);
        ModelFile modelTop = models()
                .withExistingParent(modelName + "_top", modLoc("block/template_log_bridge_top"))
                .texture("side", sideTexture).texture("top", topTexture)
                .texture("rope", ropeTexture).texture("rope_side", ropeSideTexture);

        this.simpleBlockItem(block, modelBottom);

        this.getVariantBuilder(block).forAllStates(state -> {
            Direction.Axis axis = state.getValue(BridgeBlock.AXIS);
            HalfBlockStates half = state.getValue(BridgeBlock.HALF);
            ModelFile model = (half == HalfBlockStates.TOP ? modelTop : modelBottom);

            int yRot = (axis == Direction.Axis.X ? 90 : 0);

            return ConfiguredModel.builder().modelFile(model).rotationY(yRot).uvLock(false).build();
        });
    }

    private void bambooBridgeStairsModel(Block block, ResourceLocation sideTexture, ResourceLocation topTexture, ResourceLocation sideBridgeTexture,
                                         ResourceLocation ropeTexture, ResourceLocation ropeSideTexture, ResourceLocation ropeKnotTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, modLoc("block/template_bamboo_bridge_stairs"))
                .texture("side", sideTexture).texture("top", topTexture).texture("bridge_side", sideBridgeTexture)
                .texture("rope", ropeTexture).texture("rope_side", ropeSideTexture);
        ModelFile modelInner = models()
                .withExistingParent(modelName + "_inner", modLoc("block/template_bamboo_bridge_stairs_inner"))
                .texture("side", sideTexture).texture("top", topTexture).texture("bridge_side", sideBridgeTexture)
                .texture("rope", ropeTexture).texture("rope_side", ropeSideTexture)
                .texture("rope_knot", ropeKnotTexture);
        ModelFile modelOuter = models()
                .withExistingParent(modelName + "_outer", modLoc("block/template_bamboo_bridge_stairs_outer"))
                .texture("side", sideTexture).texture("top", topTexture).texture("bridge_side", sideBridgeTexture)
                .texture("rope", ropeTexture).texture("rope_side", ropeSideTexture)
                .texture("rope_knot", ropeKnotTexture);

        this.simpleBlockItem(block, model);

        this.getVariantBuilder(block)
                .forAllStatesExcept(state -> {
                    Direction facing = state.getValue(StairBlock.FACING);
                    Half half = state.getValue(StairBlock.HALF);
                    StairsShape shape = state.getValue(StairBlock.SHAPE);

                    int yRot = (int) facing.getClockWise().toYRot();
                    if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT)
                        yRot += 270;
                    if (shape != StairsShape.STRAIGHT && half == Half.TOP)
                        yRot += 90;
                    yRot %= 360;

                    return ConfiguredModel.builder()
                            .modelFile(shape == StairsShape.STRAIGHT
                                    ? model : shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? modelInner : modelOuter)
                            .rotationX(half == Half.BOTTOM ? 0 : 180).rotationY(yRot).uvLock(false).build();
                }, StairBlock.WATERLOGGED);
    }

    private void bridgeStairsModel(Block block, ResourceLocation sideTexture, ResourceLocation topTexture,
                                   ResourceLocation ropeTexture, ResourceLocation ropeSideTexture, ResourceLocation ropeKnotTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, modLoc("block/template_log_bridge_stairs"))
                .texture("side", sideTexture).texture("top", topTexture)
                .texture("rope", ropeTexture).texture("rope_side", ropeSideTexture);
        ModelFile modelInner = models()
                .withExistingParent(modelName + "_inner", modLoc("block/template_log_bridge_stairs_inner"))
                .texture("side", sideTexture).texture("top", topTexture)
                .texture("rope", ropeTexture).texture("rope_side", ropeSideTexture)
                .texture("rope_knot", ropeKnotTexture);
        ModelFile modelOuter = models()
                .withExistingParent(modelName + "_outer", modLoc("block/template_log_bridge_stairs_outer"))
                .texture("side", sideTexture).texture("top", topTexture)
                .texture("rope", ropeTexture).texture("rope_side", ropeSideTexture)
                .texture("rope_knot", ropeKnotTexture);

        this.simpleBlockItem(block, model);

        this.getVariantBuilder(block)
                .forAllStatesExcept(state -> {
                    Direction facing = state.getValue(StairBlock.FACING);
                    Half half = state.getValue(StairBlock.HALF);
                    StairsShape shape = state.getValue(StairBlock.SHAPE);

                    int yRot = (int) facing.getClockWise().toYRot();
                    if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT)
                        yRot += 270;
                    if (shape != StairsShape.STRAIGHT && half == Half.TOP)
                        yRot += 90;
                    yRot %= 360;

                    return ConfiguredModel.builder()
                            .modelFile(shape == StairsShape.STRAIGHT
                                    ? model : shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? modelInner : modelOuter)
                            .rotationX(half == Half.BOTTOM ? 0 : 180).rotationY(yRot).uvLock(false).build();
                }, StairBlock.WATERLOGGED);
    }

    private void cubeAllModel(Block block, ResourceLocation mainTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, mcLoc("minecraft:block/cube_all"))
                .texture("all", mainTexture);

        simpleBlockWithItem(block, model);
    }

    private void cubeBottomTopModel(Block block, ResourceLocation bottomTexture, ResourceLocation sideTexture,
                                    ResourceLocation topTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, mcLoc("minecraft:block/cube_bottom_top"))
                .texture("bottom", bottomTexture)
                .texture("side", sideTexture)
                .texture("top", topTexture);

        simpleBlockWithItem(block, model);
    }

    private void cubeInnerOverlayModel(Block block, ResourceLocation bottomTexture, ResourceLocation sideTexture,
                                    ResourceLocation topTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();
        ModelFile model = models()
                .withExistingParent(modelName, modLoc("block/template_inner_overlay"))
                .texture("bottom", bottomTexture)
                .texture("side", sideTexture)
                .texture("top", topTexture);

        simpleBlockWithItem(block, model);
    }

    private void cubeOverlayModel(Block block, ResourceLocation mainTexture, ResourceLocation overlayTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, modLoc("block/cube_all_overlay"))
                .texture("all", mainTexture).texture("overlay", overlayTexture).renderType("cutout_mipped");

        simpleBlockWithItem(block, model);
    }

    private void cubeMirroredNSModel(Block block, ResourceLocation mainTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, modLoc("block/cube_mirrored_ns"))
                .texture("main", mainTexture).texture("mirrored", mainTexture + "_mirrored");

        simpleBlockWithItem(block, model);
    }

    private void dottedLineBlockModel(Block block, ResourceLocation activeTexture, ResourceLocation inActiveTexture, boolean isItemOn) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, mcLoc("block/cube_all"))
                .texture("all", activeTexture).renderType("cutout_mipped");
        ModelFile modelOff = models()
                .withExistingParent(modelName + "_off", mcLoc("block/cube_all"))
                .texture("all", inActiveTexture).renderType("cutout_mipped");

        if (isItemOn)
            simpleBlockItem(block, modelOff);
        else simpleBlockItem(block, model);

        this.getVariantBuilder(block).forAllStates(state -> {
            boolean isActive = state.getValue(OnBlock.ACTIVE);
            return ConfiguredModel.builder().modelFile(isActive ? model : modelOff).build();
        });
    }

    private void mushroomTrampolineCapModel(Block block, ResourceLocation activeTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, modLoc("block/template_mushroom"))
                .texture("mushroom", activeTexture);
        ModelFile modelOff = models()
                .withExistingParent(modelName + "_off", modLoc("block/template_mushroom"))
                .texture("bottom", activeTexture + "_off");

        simpleBlockItem(block, model);

        this.getVariantBuilder(block).forAllStates(state -> {
            boolean isActive = state.getValue(OnBlock.ACTIVE);
            return ConfiguredModel.builder().modelFile(isActive ? model : modelOff).build();
        });
    }

    private void mushroomTrampolineCapSwappedModel(Block block, ResourceLocation activeTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, modLoc("block/template_mushroom"))
                .texture("mushroom", activeTexture);
        ModelFile modelOff = models()
                .withExistingParent(modelName + "_off", modLoc("block/template_mushroom"))
                .texture("bottom", activeTexture + "_off");

        simpleBlockItem(block, model);

        this.getVariantBuilder(block).forAllStates(state -> {
            boolean isActive = !state.getValue(OnBlock.ACTIVE);
            return ConfiguredModel.builder().modelFile(isActive ? model : modelOff).build();
        });
    }

    private void mushroomTrampolineModel(Block block, ResourceLocation activeTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, mcLoc("block/cube_bottom_top"))
                .texture("bottom", activeTexture + "_bottom").texture("side", activeTexture)
                .texture("top", activeTexture + "_top");
        ModelFile modelOff = models()
                .withExistingParent(modelName + "_off", mcLoc("block/cube_bottom_top"))
                .texture("bottom", activeTexture + "_bottom_off").texture("side", activeTexture + "_off")
                .texture("top", activeTexture + "_top_off");

        simpleBlockItem(block, model);

        this.getVariantBuilder(block).forAllStates(state -> {
            boolean isActive = state.getValue(OnBlock.ACTIVE);
            return ConfiguredModel.builder().modelFile(isActive ? model : modelOff).build();
        });
    }

    private void mushroomTrampolineSwappedModel(Block block, ResourceLocation activeTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, mcLoc("block/cube_bottom_top"))
                .texture("bottom", activeTexture + "_bottom").texture("side", activeTexture)
                .texture("top", activeTexture + "_top");
        ModelFile modelOff = models()
                .withExistingParent(modelName + "_off", mcLoc("block/cube_bottom_top"))
                .texture("bottom", activeTexture + "_bottom_off").texture("side", activeTexture + "_off")
                .texture("top", activeTexture + "_top_off");

        simpleBlockItem(block, model);

        this.getVariantBuilder(block).forAllStates(state -> {
            boolean isActive = !state.getValue(OnBlock.ACTIVE);
            return ConfiguredModel.builder().modelFile(isActive ? model : modelOff).build();
        });
    }

    private void emptyModel(Block block, ResourceLocation mainTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models().getBuilder(modelName).texture("particle", mainTexture).renderType("cutout_mipped");

        VariantBlockStateBuilder variantBuilder = this.getVariantBuilder(block);
        variantBuilder.partialState().addModels(new ConfiguredModel(model));
    }

    private void horizontalModel(Block block, ResourceLocation frontTexture, ResourceLocation sideTexture, ResourceLocation topTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, mcLoc("block/orientable"))
                .texture("side", sideTexture).texture("front", frontTexture).texture("top", topTexture);

        simpleBlockItem(block, model);
        this.getVariantBuilder(block)
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(model)
                        .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                        .build());
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
                .texture("all", invisibleTexture).renderType("translucent");

        VariantBlockStateBuilder variantBuilder = this.getVariantBuilder(block);
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
                .texture("all", invisibleTexture).renderType("translucent");

        VariantBlockStateBuilder variantBuilder = this.getVariantBuilder(block);
        variantBuilder.partialState().with(QuestionBlock.EMPTY, false).with(InvisibleQuestionBlock.INVISIBLE, false)
                .addModels(new ConfiguredModel(model));
        variantBuilder.partialState().with(QuestionBlock.EMPTY, true).with(InvisibleQuestionBlock.INVISIBLE, false)
                .addModels(new ConfiguredModel(modelEmpty));
        variantBuilder.partialState().with(QuestionBlock.EMPTY, false).with(InvisibleQuestionBlock.INVISIBLE, true)
                .addModels(new ConfiguredModel(modelInvisible));
        variantBuilder.partialState().with(QuestionBlock.EMPTY, true).with(InvisibleQuestionBlock.INVISIBLE, true)
                .addModels(new ConfiguredModel(modelEmpty));
    }

    private void invisibleQuestionBlockSandstoneModel(Block block, String modelName, ResourceLocation sideTexture, ResourceLocation topTexture,
                                                      ResourceLocation emptyTexture, ResourceLocation invisibleTexture) {
        ModelFile model = models()
                .withExistingParent(modelName, mcLoc("block/cube_bottom_top"))
                .texture("bottom", topTexture).texture("side", sideTexture).texture("top", topTexture);
        ModelFile modelEmpty = models()
                .withExistingParent("empty_invisible_" + modelName, mcLoc("block/cube_bottom_top"))
                .texture("bottom", topTexture).texture("side", emptyTexture).texture("top", topTexture);
        ModelFile modelInvisible = models()
                .withExistingParent("invisible_" + modelName, mcLoc("block/cube_all"))
                .texture("all", invisibleTexture).renderType("translucent");

        VariantBlockStateBuilder variantBuilder = this.getVariantBuilder(block);
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
                .withExistingParent(modelName + "_none",
                        modLoc("block/template_goal_pole_none"))
                .texture("side", mainTexture + "_none");
        ModelFile modelTop = models()
                .withExistingParent(modelName + "_top",
                        modLoc("block/template_goal_pole_top"))
                .texture("side", mainTexture + "_top");

        VariantBlockStateBuilder variantBuilder = this.getVariantBuilder(block);
        variantBuilder.partialState().with(GoalPoleBlock.COLUMN, ColumnBlockStates.BOTTOM).addModels(new ConfiguredModel(model));
        variantBuilder.partialState().with(GoalPoleBlock.COLUMN, ColumnBlockStates.MIDDLE).addModels(new ConfiguredModel(model));
        variantBuilder.partialState().with(GoalPoleBlock.COLUMN, ColumnBlockStates.TOP).addModels(new ConfiguredModel(modelTop));
        variantBuilder.partialState().with(GoalPoleBlock.COLUMN, ColumnBlockStates.NONE).addModels(new ConfiguredModel(modelNone));
    }

    private void ironSpikeModel(Block block, ResourceLocation mainTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, modLoc("block/template_spike"))
                .texture("spikes", mainTexture)
                .texture("center", mainTexture + "_center")
                .renderType("cutout_mipped");

        VariantBlockStateBuilder variantBuilder = this.getVariantBuilder(block);
        variantBuilder.partialState().addModels(new ConfiguredModel(model));
    }

    private void pedestalModel(Block block, ResourceLocation mainTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile modelTop = models()
                .withExistingParent(modelName + "_top",
                        modLoc("block/template_brick_pedestal_top"))
                .texture("bricks", mainTexture);
        ModelFile modelBottom = models()
                .withExistingParent(modelName,
                        modLoc("block/template_brick_pedestal"))
                .texture("bricks", mainTexture);

        simpleBlockItem(block, modelTop);

        VariantBlockStateBuilder variantBuilder = this.getVariantBuilder(block);
        variantBuilder.partialState().with(BrickPedestalBlock.TOP, true).addModels(new ConfiguredModel(modelTop));
        variantBuilder.partialState().with(BrickPedestalBlock.TOP, false).addModels(new ConfiguredModel(modelBottom));
    }

    private void largeBrickPedestalModel(Block block, ResourceLocation mainTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile modelTop = models()
                .withExistingParent(modelName + "_top", modLoc("block/template_large_brick_pedestal_top"))
                .texture("bricks", mainTexture);
        ModelFile modelBottom = models()
                .withExistingParent(modelName, modLoc("block/template_brick_pedestal"))
                .texture("bricks", mainTexture);

        simpleBlockItem(block, modelTop);

        VariantBlockStateBuilder variantBuilder = this.getVariantBuilder(block);
        variantBuilder.partialState().with(BrickPedestalBlock.TOP, true).addModels(new ConfiguredModel(modelTop));
        variantBuilder.partialState().with(BrickPedestalBlock.TOP, false).addModels(new ConfiguredModel(modelBottom));
    }

    private void onOffSwitchModel(Block block, ResourceLocation onSideTexture, ResourceLocation onTopTexture,
                                       ResourceLocation offSideTexture, ResourceLocation offTopTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, mcLoc("block/cube_column"))
                .texture("side", onSideTexture).texture("end", onTopTexture);
        ModelFile modelOff = models()
                .withExistingParent(modelName + "_off", mcLoc("block/cube_column"))
                .texture("side", offSideTexture).texture("end", offTopTexture);

        simpleBlockItem(block, model);

        this.getVariantBuilder(block).forAllStates(state -> {
            boolean isActive = state.getValue(OnBlock.ACTIVE);;
            return ConfiguredModel.builder().modelFile(isActive ? model : modelOff).build();
        });
    }

    private void pipeBubblesModel(Block block) {
        ModelFile model = models().getExistingFile(ResourceLocation
                .fromNamespaceAndPath("minecraft", "block/water"));

        VariantBlockStateBuilder variantBuilder = this.getVariantBuilder(block);
        variantBuilder.partialState().addModels(new ConfiguredModel(model));
    }

    private void pottedBlossomModel(Block block, ResourceLocation blossomTexture, ResourceLocation leavesTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, modLoc("block/template_potted_blossom"))
                .texture("blossom", blossomTexture).texture("leaves", leavesTexture)
                .texture("flower_pot", mcLoc("block/flower_pot"))
                .texture("dirt", mcLoc("block/dirt"));

        VariantBlockStateBuilder variantBuilder = this.getVariantBuilder(block);
        variantBuilder.partialState().addModels(new ConfiguredModel(model));
    }

    private void questionBlockModel(Block block, ResourceLocation sideTexture, ResourceLocation topTexture,
                                    ResourceLocation emptyTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, mcLoc("block/cube_bottom_top"))
                .texture("bottom", topTexture).texture("side", sideTexture).texture("top", topTexture);
        ModelFile modelEmpty = models()
                .withExistingParent("empty_" + modelName, mcLoc("block/cube_all"))
                .texture("all", emptyTexture);

        simpleBlockItem(block, model);

        VariantBlockStateBuilder variantBuilder = this.getVariantBuilder(block);
        variantBuilder.partialState().with(QuestionBlock.EMPTY, false).addModels(new ConfiguredModel(model));
        variantBuilder.partialState().with(QuestionBlock.EMPTY, true).addModels(new ConfiguredModel(modelEmpty));
    }

    private void questionBlockModel(Block block, ResourceLocation mainTexture, ResourceLocation emptyTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, mcLoc("block/cube_bottom_top"))
                .texture("bottom", emptyTexture).texture("side", mainTexture).texture("top", emptyTexture);
        ModelFile modelEmpty = models()
                .withExistingParent("empty_" + modelName, mcLoc("block/cube_all"))
                .texture("all", emptyTexture);

        simpleBlockItem(block, model);

        VariantBlockStateBuilder variantBuilder = this.getVariantBuilder(block);
        variantBuilder.partialState().with(QuestionBlock.EMPTY, false).addModels(new ConfiguredModel(model));
        variantBuilder.partialState().with(QuestionBlock.EMPTY, true).addModels(new ConfiguredModel(modelEmpty));
    }

    private void questionBlockSandstoneModel(Block block, ResourceLocation sideTexture, ResourceLocation topTexture,
                                             ResourceLocation emptyTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, mcLoc("block/cube_bottom_top"))
                .texture("bottom", topTexture).texture("side", sideTexture).texture("top", topTexture);
        ModelFile modelEmpty = models()
                .withExistingParent("empty_" + modelName, mcLoc("block/cube_bottom_top"))
                .texture("bottom", topTexture).texture("side", emptyTexture).texture("top", topTexture);

        simpleBlockItem(block, model);

        VariantBlockStateBuilder variantBuilder = this.getVariantBuilder(block);
        variantBuilder.partialState().with(QuestionBlock.EMPTY, false).addModels(new ConfiguredModel(model));
        variantBuilder.partialState().with(QuestionBlock.EMPTY, true).addModels(new ConfiguredModel(modelEmpty));
    }

    private void questionPanelModel(Block block, ResourceLocation bottomTexture, ResourceLocation topTexture, ResourceLocation offTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, modLoc("block/template_question_panel"))
                .texture("bottom", bottomTexture).texture("top", topTexture);
        ModelFile modeloff = models()
                .withExistingParent(modelName + "_off", modLoc("block/template_question_panel_off"))
                .texture("bottom", bottomTexture).texture("top", offTexture);

        this.simpleBlockItem(block, model);

        this.getVariantBuilder(block).forAllStates(state -> {
            Direction facing = state.getValue(QuestionPanelBlock.FACING);
            AttachFace face = state.getValue(QuestionPanelBlock.FACE);
            boolean powered = state.getValue(QuestionPanelBlock.POWERED);

            return ConfiguredModel.builder()
                    .modelFile(powered ? modeloff : model)
                    .rotationX(face == AttachFace.FLOOR ? 0 : (face == AttachFace.WALL ? 90 : 180))
                    .rotationY((int) (face == AttachFace.CEILING ? facing : facing.getOpposite()).toYRot())
                    .uvLock(face == AttachFace.WALL)
                    .build();
        });
    }

    public void slabDoubleBlock(SlabBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        slabBlock(block, models().slab(modelName, side, bottom, top),
                models().slabTop(modelName + "_top", side, bottom, top),
                models().withExistingParent(modelName + "_double", mcLoc("block/cube_bottom_top"))
                        .texture("side", side)
                        .texture("bottom", bottom)
                        .texture("top", top));
    }

    private void slabMirroredNSModel(Block block, ResourceLocation mainTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, modLoc("block/slab_mirrored_ns"))
                .texture("main", mainTexture).texture("mirrored", mainTexture + "_mirrored");
        ModelFile modelTop = models()
                .withExistingParent(modelName + "_top", modLoc("block/slab_top_mirrored_ns"))
                .texture("main", mainTexture).texture("mirrored", mainTexture + "_mirrored");
        ModelFile modelDouble = models().getExistingFile(mainTexture);

        simpleBlockItem(block, model);

        this.getVariantBuilder(block)
                .partialState().with(SlabBlock.TYPE, SlabType.BOTTOM).addModels(new ConfiguredModel(model))
                .partialState().with(SlabBlock.TYPE, SlabType.TOP).addModels(new ConfiguredModel(modelTop))
                .partialState().with(SlabBlock.TYPE, SlabType.DOUBLE).addModels(new ConfiguredModel(modelDouble));
    }

    private void spikePanelModel(Block block, ResourceLocation mainTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, modLoc("block/template_panel"))
                .texture("top", mainTexture)
                .texture("bottom", mainTexture + "_bottom")
                .renderType("solid");
        ModelFile modelSpikes = models()
                .withExistingParent(modelName + "_spikes", modLoc("block/template_spike_panel"))
                .texture("top", mainTexture)
                .texture("bottom", mainTexture + "_bottom")
                .texture("spikes", mainTexture + "_spikes")
                .renderType("cutout_mipped");

        this.getVariantBuilder(block).forAllStates(state -> {
            Direction facing = state.getValue(PanelBlock.FACING);
            AttachFace face = state.getValue(PanelBlock.FACE);
            boolean spikes = state.getValue(SpikePanelBlock.SPIKES);

            return ConfiguredModel.builder()
                    .modelFile(spikes ? modelSpikes : model)
                    .rotationX(face == AttachFace.FLOOR ? 0 : (face == AttachFace.WALL ? 90 : 180))
                    .rotationY((int) (face == AttachFace.CEILING ? facing : facing.getOpposite()).toYRot())
                    .uvLock(false)
                    .build();
        });
    }

    private void splunkinOLanternModel(Block block, ResourceLocation frontTexture, ResourceLocation sideTexture, ResourceLocation topTexture,
                                       ResourceLocation frontCrackedTexture, ResourceLocation sideCrackedTexture, ResourceLocation topCrackedTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, mcLoc("block/orientable"))
                .texture("side", sideTexture).texture("front", frontTexture).texture("top", topTexture);
        ModelFile modelCracked = models()
                .withExistingParent(modelName + "_cracked", mcLoc("block/orientable"))
                .texture("side", sideCrackedTexture).texture("front", frontCrackedTexture)
                .texture("top", topCrackedTexture);

        simpleBlockItem(block, model);

        this.getVariantBuilder(block).forAllStates(state -> {
            boolean cracked = state.getValue(SplunkinCarvedPumpkinBlock.CRACKED);
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);

            return ConfiguredModel.builder()
                    .modelFile(cracked ? modelCracked : model)
                    .rotationY(((int) facing.toYRot() + 180) % 360)
                    .build();
        });
    }

    private void stairsMirroredNSModel(Block block, ResourceLocation mainTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, modLoc("block/stairs_mirrored_ns"))
                .texture("main", mainTexture).texture("mirrored", mainTexture + "_mirrored");
        ModelFile modelInner = models()
                .withExistingParent(modelName + "_inner", modLoc("block/inner_stairs_mirrored_ns"))
                .texture("main", mainTexture).texture("mirrored", mainTexture + "_mirrored");
        ModelFile modelOuter = models()
                .withExistingParent(modelName + "_outer", modLoc("block/outer_stairs_mirrored_ns"))
                .texture("main", mainTexture).texture("mirrored", mainTexture + "_mirrored");

        simpleBlockItem(block, model);

        this.getVariantBuilder(block)
                .forAllStatesExcept(state -> {
                    Direction facing = state.getValue(StairBlock.FACING);
                    Half half = state.getValue(StairBlock.HALF);
                    StairsShape shape = state.getValue(StairBlock.SHAPE);
                    int yRot = (int) facing.getClockWise().toYRot();
                    if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT)
                        yRot += 270;
                    if (shape != StairsShape.STRAIGHT && half == Half.TOP)
                        yRot += 90;
                    yRot %= 360;
                    boolean uvlock = yRot != 0 || half == Half.TOP;

                    return ConfiguredModel.builder()
                            .modelFile(shape == StairsShape.STRAIGHT
                                    ? model : shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? modelInner : modelOuter)
                            .rotationX(half == Half.BOTTOM ? 0 : 180)
                            .rotationY(yRot)
                            .uvLock(true)
                            .build();
                }, StairBlock.WATERLOGGED);
    }

    private void storageBrickModel(Block block, ResourceLocation mainTexture, ResourceLocation emptyTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, mcLoc("block/cube_all"))
                .texture("all", mainTexture);
        ModelFile modelEmpty = models()
                .withExistingParent("empty_" + modelName, mcLoc("block/cube_all"))
                .texture("all", emptyTexture);

        VariantBlockStateBuilder variantBuilder = this.getVariantBuilder(block);
        variantBuilder.partialState().with(QuestionBlock.EMPTY, false).addModels(new ConfiguredModel(model));
        variantBuilder.partialState().with(QuestionBlock.EMPTY, true).addModels(new ConfiguredModel(modelEmpty));
    }

    private void storageBrickSandstoneModel(Block block, ResourceLocation mainTexture, ResourceLocation topTexture, ResourceLocation emptyTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, mcLoc("block/cube_all"))
                .texture("all", mainTexture);
        ModelFile modelEmpty = models()
                .withExistingParent("empty_" + modelName, mcLoc("block/cube_bottom_top"))
                .texture("bottom", topTexture).texture("side", emptyTexture).texture("top", topTexture);

        VariantBlockStateBuilder variantBuilder = this.getVariantBuilder(block);
        variantBuilder.partialState().with(QuestionBlock.EMPTY, false).addModels(new ConfiguredModel(model));
        variantBuilder.partialState().with(QuestionBlock.EMPTY, true).addModels(new ConfiguredModel(modelEmpty));
    }

    private void wallMirroredNSModel(Block block, ResourceLocation mainTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, modLoc("block/template_wall_post_mirrored_ns"))
                .texture("main", mainTexture).texture("mirrored", mainTexture + "_mirrored");
        ModelFile modelSide = models()
                .withExistingParent(modelName + "_side", modLoc("block/template_wall_side_mirrored_ns"))
                .texture("main", mainTexture).texture("mirrored", mainTexture + "_mirrored");
        ModelFile modelSideTall = models()
                .withExistingParent(modelName + "_side_tall", modLoc("block/template_wall_side_tall_mirrored_ns"))
                .texture("main", mainTexture).texture("mirrored", mainTexture + "_mirrored");
        ModelFile modelInventory = models()
                .withExistingParent(modelName + "_inventory", modLoc("block/wall_mirrored_ns_inventory"))
                .texture("main", mainTexture).texture("mirrored", mainTexture + "_mirrored");

        simpleBlockItem(block, modelInventory);

        MultiPartBlockStateBuilder builder = this.getMultipartBuilder(block)
                .part().modelFile(model).addModel()
                .condition(WallBlock.UP, true).end();
        WALL_PROPS.entrySet().stream()
                .filter(e -> e.getKey().getAxis().isHorizontal())
                .forEach(e -> {
                    wallSidePart(builder, modelSide, e, WallSide.LOW);
                    wallSidePart(builder, modelSideTall, e, WallSide.TALL);
                });
    }

    private void wallSidePart(MultiPartBlockStateBuilder builder, ModelFile model, Map.Entry<Direction, Property<WallSide>> entry, WallSide height) {
        builder.part()
                .modelFile(model)
                .rotationY((((int) entry.getKey().toYRot()) + 180) % 360)
                .uvLock(false)
                .addModel()
                .condition(entry.getValue(), height);
    }

    private void warpPipeModel(Block block, ResourceLocation entranceTexture, ResourceLocation bottomTexture,
                               ResourceLocation sideTexture, ResourceLocation topTexture, ResourceLocation topClosedTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

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

        VariantBlockStateBuilder variantBuilder = this.getVariantBuilder(block);

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

    private void waterSpoutModel(Block block, ResourceLocation sideTexture, ResourceLocation topTexture, ResourceLocation splashTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, modLoc("block/template_water_spout"))
                .texture("side", sideTexture);
        ModelFile modelTop = models()
                .withExistingParent(modelName + "_top", modLoc("block/template_water_spout_top"))
                .texture("splash", splashTexture).texture("side", sideTexture).texture("top", topTexture);

        VariantBlockStateBuilder variantBuilder = this.getVariantBuilder(block);
        variantBuilder.partialState().with(WaterSpoutBlock.TOP, false).addModels(new ConfiguredModel(model));
        variantBuilder.partialState().with(WaterSpoutBlock.TOP, true).addModels(new ConfiguredModel(modelTop));
    }

    // Unfinished
    private void clearWarpPipeModel(Block block) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

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

        VariantBlockStateBuilder variantBuilder = this.getVariantBuilder(block);

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
