package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.data.BlockFamilyExtended;
import com.wenxin2.marioverse.init.BlockFamiliesRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ItemModelGen extends ItemModelProvider {
    public ItemModelGen(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Marioverse.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        this.genStorageBrickVariants();

        registerInvisibleQuestionBlock("invisible_copper_question_block",
                modLoc("block/invisible_copper_question_block"));

        registerInvisibleQuestionBlock("invisible_exposed_copper_question_block",
                modLoc("block/invisible_exposed_copper_question_block"));

        registerInvisibleQuestionBlock("invisible_weathered_copper_question_block",
                modLoc("block/invisible_weathered_copper_question_block"));

        registerInvisibleQuestionBlock("invisible_oxidized_copper_question_block",
                modLoc("block/invisible_oxidized_copper_question_block"));

        registerInvisibleQuestionBlock("invisible_waxed_copper_question_block",
                modLoc("block/invisible_copper_question_block"));

        registerInvisibleQuestionBlock("invisible_waxed_exposed_copper_question_block",
                modLoc("block/invisible_exposed_copper_question_block"));

        registerInvisibleQuestionBlock("invisible_waxed_weathered_copper_question_block",
                modLoc("block/invisible_weathered_copper_question_block"));

        registerInvisibleQuestionBlock("invisible_waxed_oxidized_copper_question_block",
                modLoc("block/invisible_oxidized_copper_question_block"));
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
                    ResourceLocation sideTexture;
                    ResourceLocation topTexture;
                    ResourceLocation overlayTexture;

                    if (block == BlockFamiliesRegistry.AMETHYST_BRICKS.get(storageBrick)
                            || block == BlockFamiliesRegistry.DEEP_FUNGAL_BRICKS.get(storageBrick)
                            || block == BlockFamiliesRegistry.FUNGAL_BRICKS.get(storageBrick)) {
                        sideTexture = modLoc("block/" + blockName);
                        topTexture = modLoc("block/" + removeStorageName);
                        registerCubeBottomTop(blockName, sideTexture, topTexture);
                    } else if (removeStorageName.startsWith("waxed_")) {
                        String unWaxedName = blockName.replace("waxed_", "");
                        removeStorageName = unWaxedName.replace("storage_", "");
                        questionBlockName = removeStorageName
                                .replace("cut_copper", "copper_question_block");
                        mainTexture = mcLoc("minecraft:block/" + removeStorageName);
                        overlayTexture = modLoc("block/" + questionBlockName + "_overlay");
                        registerStorageBrick(blockName, mainTexture, overlayTexture);
                    } else if (questionBlockName.startsWith("blackstone_")) {
                        String crackedBlockName = removeStorageName.replace("blackstone_", "polished_blackstone_");
                        mainTexture = mcLoc("minecraft:block/" + crackedBlockName);
                        overlayTexture = modLoc("block/" + questionBlockName + "_overlay");
                        registerStorageBrick(blockName, mainTexture, overlayTexture);
                    } else {
                        mainTexture = mcLoc("minecraft:block/" + removeStorageName);
                        overlayTexture = modLoc("block/" + questionBlockName + "_overlay");
                        registerStorageBrick(blockName, mainTexture, overlayTexture);
                    }
                }
            });
        });
    }

    private void registerStorageBrick(String modelName, ResourceLocation mainTexture, ResourceLocation overlayTexture) {
        getBuilder(modelName).parent(getExistingFile(modLoc("item/template_storage_bricks")))
                .texture("all", mainTexture).texture("overlay", overlayTexture);
    }

    private void registerCubeBottomTop(String modelName, ResourceLocation sideTexture, ResourceLocation topTexture) {
        getBuilder(modelName).parent(getExistingFile(mcLoc("minecraft:block/cube_bottom_top"))).texture("bottom", topTexture)
                .texture("side", sideTexture).texture("top", topTexture);
    }

    private void registerInvisibleQuestionBlock(String modelName, ResourceLocation mainTexture) {
        getBuilder(modelName).parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", mainTexture);
    }
}
