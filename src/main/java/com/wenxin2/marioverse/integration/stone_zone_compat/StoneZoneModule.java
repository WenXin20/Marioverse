package com.wenxin2.marioverse.integration.stone_zone_compat;

import com.wenxin2.marioverse.MarioverseCreativeTabs;
import com.wenxin2.marioverse.blocks.QuestionBlock;
import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.mehvahdjukaar.every_compat.api.SimpleEntrySet;
import net.mehvahdjukaar.every_compat.api.SimpleModule;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.stone_zone.api.intergration.CompatStoneType;
import net.mehvahdjukaar.stone_zone.api.set.StoneType;
import net.mehvahdjukaar.stone_zone.api.set.StoneTypeRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;

public class StoneZoneModule extends SimpleModule {
    public final SimpleEntrySet<StoneType, Block> questionBlock;

    public StoneZoneModule(String modId, String shortId) {
        super(modId, shortId);
        ResourceKey<CreativeModeTab> tab = MarioverseCreativeTabs.MARIOVERSE_BLOCKS_TAB.getKey();

        CompatStoneType.simpleStoneFinder("minecraft", "amethyst_block");

        questionBlock = SimpleEntrySet.builder(StoneType.class, "question_bricks",
                        BlockRegistry.STONE_QUESTION_BRICKS, () -> StoneTypeRegistry.STONE_TYPE,
                        stoneType -> new QuestionBlock(Utils.copyPropertySafe(stoneType.stone)))
                .excludeBlockTypes("minecraft", "amethyst_block")
                .addTexture(modRes("block/empty_stone_question_bricks"))
                .addTexture(modRes("block/stone_question_bricks"))
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(BlockTags.FEATURES_CANNOT_REPLACE, Registries.BLOCK)
                .addTag(BlockTags.GUARDED_BY_PIGLINS, Registries.BLOCK)
                .addTag(TagRegistry.BONKABLE_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.QUESTION_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.BONKABLE_BLOCK_ITEMS, Registries.ITEM)
                .addTag(TagRegistry.QUESTION_BLOCK_ITEMS, Registries.ITEM)
                .addTag(TagRegistry.COPYCAT_ALLOW, Registries.ITEM)
                .addTile(QuestionBlockEntity::new)
                .defaultRecipe()
                .setTabKey(tab)
                .build();
        this.addEntry(questionBlock);
    }
}
