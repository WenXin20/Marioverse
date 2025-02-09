package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.init.BlockRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class MarioverseItemModelProvider extends ItemModelProvider {
    public MarioverseItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Marioverse.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        registerInvisibleQuestionBlock("invisible_copper_question_block",
                modLoc("block/invisible_copper_question_block"));
        registerStorageBrick("storage_cut_copper",
                mcLoc("minecraft:block/cut_copper"), modLoc("block/copper_question_block_overlay"));
    }

    private void registerStorageBrick(String modelName, ResourceLocation mainTexture, ResourceLocation overlayTexture) {
        getBuilder(modelName).parent(getExistingFile(modLoc("item/template_storage_bricks")))
                .texture("all", mainTexture).texture("overlay", overlayTexture);
    }

    private void registerInvisibleQuestionBlock(String modelName, ResourceLocation mainTexture) {
        getBuilder(modelName).parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", mainTexture);
    }
}
