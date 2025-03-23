package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.data.BlockFamilyExtended;
import com.wenxin2.marioverse.registries.BlockFamilyRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import java.util.Map;
import java.util.Objects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ItemModelGen extends ItemModelProvider {
    public ItemModelGen(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Marioverse.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        this.genInvisibleQuestionBlockVariants();
        this.genStorageBrickVariants();

        this.basicItem(BlockRegistry.COIN.asItem());
        this.basicItem(BlockRegistry.STAR_COIN.asItem());

        this.basicItem(ItemRegistry.BOWSER_BANNER_PATTERN.get());
        this.basicItem(ItemRegistry.BOWSER_POTTERY_SHERD.get());
        this.basicItem(ItemRegistry.FIRE_FLOWER.get());
        this.basicItem(ItemRegistry.FIRE_GOOMBA_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.GOOMBA_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.HEFTY_GOOMBA_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.ICE_COSTUME_SMITHING_TEMPLATE.get());
        this.basicItem(ItemRegistry.ICE_FLOWER.get());
        this.basicItem(ItemRegistry.MARIO_COSTUME_SMITHING_TEMPLATE.get());
        this.basicItem(ItemRegistry.MARIO_FIRE_HAT.get());
        this.basicItem(ItemRegistry.MARIO_FIRE_PANTS.get());
        this.basicItem(ItemRegistry.MARIO_FIRE_SHIRT.get());
        this.basicItem(ItemRegistry.MARIO_FIRE_SHOES.get());
        this.basicItem(ItemRegistry.MARIO_HAT.get());
        this.basicItem(ItemRegistry.MARIO_ICE_HAT.get());
        this.basicItem(ItemRegistry.MARIO_ICE_PANTS.get());
        this.basicItem(ItemRegistry.MARIO_ICE_SHIRT.get());
        this.basicItem(ItemRegistry.MARIO_ICE_SHOES.get());
        this.basicItem(ItemRegistry.MARIO_PANTS.get());
        this.basicItem(ItemRegistry.MARIO_SHIRT.get());
        this.basicItem(ItemRegistry.MARIO_SHOES.get());
        this.basicItem(ItemRegistry.MEGA_GOOMBA_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.MINI_GOOMBA_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.MUSHROOM.get());
        this.basicItem(ItemRegistry.ONE_UP_MUSHROOM.get());
        this.basicItem(ItemRegistry.PIRANHA_PLANT_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.PLUMBER_BANNER_PATTERN.get());
        this.basicItem(ItemRegistry.PLUMBER_POTTERY_SHERD.get());
        this.basicItem(ItemRegistry.SUPER_STAR.get());

        this.handheldItem(BlockRegistry.CLASSIC_CHECKPOINT_FLAG.asItem());
        this.handheldItem(BlockRegistry.CLASSIC_GOAL_POLE.asItem());
        this.handheldItem(ItemRegistry.PIPE_WRENCH.get());
        this.handheldItem(ItemRegistry.WARP_DISRUPTOR.get());

        for (Map.Entry<DyeColor, DeferredBlock<Block>> entry : BlockRegistry.CHECKPOINT_FLAGS.entrySet())
            this.handheldItem(entry.getValue().asItem());

        for (Map.Entry<DyeColor, DeferredBlock<Block>> entry : BlockRegistry.GOAL_POLES.entrySet())
            this.handheldItem(entry.getValue().asItem());
    }

    public void storageBrickModel(String modelName, ResourceLocation mainTexture, ResourceLocation overlayTexture) {
        getBuilder(modelName).parent(getExistingFile(modLoc("block/template_storage_bricks")))
                .texture("all", mainTexture).texture("overlay", overlayTexture);
    }

    public void cubeBottomTopModel(String modelName, ResourceLocation sideTexture, ResourceLocation topTexture) {
        getBuilder(modelName).parent(getExistingFile(mcLoc("minecraft:block/cube_bottom_top"))).texture("bottom", topTexture)
                .texture("side", sideTexture).texture("top", topTexture);
    }

    public void invisibleQuestionBlockModel(String modelName, ResourceLocation mainTexture) {
        getBuilder(modelName).parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", mainTexture);
    }

    public void spawnEggItem(Item item, String parentModelPath) {
        getBuilder(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)).toString()).parent(new ModelFile
                .UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, parentModelPath)));
    }

    private void genInvisibleQuestionBlockVariants() {
        BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> {
            blockFamily.getVariants().forEach((variant, block) -> {
                BlockFamilyExtended.Variant questionBlock = BlockFamilyExtended.Variant.INVISIBLE_QUESTION_BLOCK;

                if (variant == questionBlock) {
                    String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
                    ResourceLocation mainTexture;

                    if (blockName.startsWith("invisible_waxed_")) {
                        String removeWaxedName = blockName.replace("waxed_", "");
                        mainTexture = modLoc("block/" + removeWaxedName);

                        this.invisibleQuestionBlockModel(blockName, mainTexture);
                    } else {
                        mainTexture = modLoc("block/" + blockName);

                        this.invisibleQuestionBlockModel(blockName, mainTexture);
                    }
                }
            });
        });
    }

    private void genStorageBrickVariants() {
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
                    ResourceLocation sideTexture;
                    ResourceLocation topTexture;
                    ResourceLocation overlayTexture;

                    if (block == BlockFamilyRegistry.AMETHYST_BRICKS.get(storageBrick)
                            || block == BlockFamilyRegistry.DEEP_FUNGAL_BRICKS.get(storageBrick)
                            || block == BlockFamilyRegistry.FUNGAL_BRICKS.get(storageBrick)) {
                        sideTexture = modLoc("block/" + blockName);
                        topTexture = modLoc("block/" + removeStorageName);

                        this.cubeBottomTopModel(blockName, sideTexture, topTexture);
                    } else if (removeStorageName.startsWith("waxed_")) {
                        String unWaxedName = blockName.replace("waxed_", "");
                        removeStorageName = unWaxedName.replace("storage_", "");
                        questionBlockName = removeStorageName
                                .replace("cut_copper", "copper_question_block");
                        mainTexture = mcLoc("minecraft:block/" + removeStorageName);
                        overlayTexture = modLoc("block/" + questionBlockName + "_overlay");

                        this.storageBrickModel(blockName, mainTexture, overlayTexture);
                    } else if (questionBlockName.startsWith("blackstone_")) {
                        String crackedBlockName = removeStorageName.replace("blackstone_", "polished_blackstone_");
                        mainTexture = mcLoc("minecraft:block/" + crackedBlockName);
                        overlayTexture = modLoc("block/" + questionBlockName + "_overlay");

                        this.storageBrickModel(blockName, mainTexture, overlayTexture);
                    } else {
                        mainTexture = mcLoc("minecraft:block/" + removeStorageName);
                        overlayTexture = modLoc("block/" + questionBlockName + "_overlay");

                        this.storageBrickModel(blockName, mainTexture, overlayTexture);
                    }
                }
            });
        });
    }
}
