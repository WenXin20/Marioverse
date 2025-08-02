package com.wenxin2.marioverse.integration.stone_zone_compat;

import com.wenxin2.marioverse.MarioverseCreativeTabs;
import com.wenxin2.marioverse.blocks.BrickPedestalBlock;
import com.wenxin2.marioverse.blocks.InvisibleQuestionBlock;
import com.wenxin2.marioverse.blocks.QuestionBlock;
import com.wenxin2.marioverse.blocks.StorageBrickBlock;
import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.mehvahdjukaar.every_compat.api.SimpleEntrySet;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.stone_zone.api.StoneZoneModule;
import net.mehvahdjukaar.stone_zone.api.set.StoneType;
import net.mehvahdjukaar.stone_zone.api.set.StoneTypeRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;

public class MarioverseModule extends StoneZoneModule {
    public final SimpleEntrySet<StoneType, Block> brickPedestal;
    public final SimpleEntrySet<StoneType, Block> invisibleQuestionBlock;
    public final SimpleEntrySet<StoneType, Block> questionBlock;
    public final SimpleEntrySet<StoneType, Block> smashableBricks;
    public final SimpleEntrySet<StoneType, Block> storageBricks;

    public MarioverseModule(String modId, String shortId) {
        super(modId, shortId);
        ResourceKey<CreativeModeTab> tab = MarioverseCreativeTabs.MARIOVERSE_BLOCKS_TAB.getKey();

        brickPedestal = SimpleEntrySet.builder(StoneType.class, "brick_pedestal",
                        BlockRegistry.STONE_BRICK_PEDESTAL, () -> StoneTypeRegistry.STONE_TYPE,
                        stoneType -> new BrickPedestalBlock(Utils.copyPropertySafe(stoneType.stone)))
                .addTag(BlockTags.MINEABLE_WITH_PICKAXE, Registries.BLOCK)
                .addTag(TagRegistry.BRICK_PEDESTAL_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.BRICK_PEDESTAL_ITEMS, Registries.ITEM)
                .requiresChildren("bricks")
                .defaultRecipe()
                .setTabKey(tab)
                .build();
        this.addEntry(brickPedestal);

        invisibleQuestionBlock = SimpleEntrySet.builder(StoneType.class, "question_bricks", "invisible",
                        BlockRegistry.INVISIBLE_STONE_QUESTION_BRICKS, () -> StoneTypeRegistry.STONE_TYPE,
                        stoneType -> new InvisibleQuestionBlock(Utils.copyPropertySafe(stoneType.stone)))
                .addTexture(modRes("block/invisible_stone_question_bricks"))
                .addTag(TagRegistry.COPYCAT_ALLOW, Registries.BLOCK)
                .addTag(TagRegistry.MOVABLE_EMPTY_COLLIDER, Registries.BLOCK)
                .addTag(TagRegistry.SIMPLE_MOUNTED_STORAGE, Registries.BLOCK)
                .addTag(BlockTags.MINEABLE_WITH_PICKAXE, Registries.BLOCK)
                .addTag(BlockTags.FEATURES_CANNOT_REPLACE, Registries.BLOCK)
                .addTag(BlockTags.GUARDED_BY_PIGLINS, Registries.BLOCK)
                .addTag(TagRegistry.BONKABLE_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.INVISIBLE_QUESTION_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.BONKABLE_BLOCK_ITEMS, Registries.ITEM)
                .addTag(TagRegistry.INVISIBLE_QUESTION_BLOCK_ITEMS, Registries.ITEM)
                .addTile(QuestionBlockEntity::new)
                .requiresChildren("bricks")
                .setTabKey(tab)
                .build();
        this.addEntry(invisibleQuestionBlock);

        questionBlock = SimpleEntrySet.builder(StoneType.class, "question_bricks",
                        BlockRegistry.STONE_QUESTION_BRICKS, () -> StoneTypeRegistry.STONE_TYPE,
                        stoneType -> new QuestionBlock(Utils.copyPropertySafe(stoneType.stone)))
                .excludeBlockTypes("minecraft", "amethyst_block")
                .addTexture(modRes("block/empty_stone_question_bricks"))
                .addTexture(modRes("block/stone_question_bricks"))
                .addTag(TagRegistry.COPYCAT_ALLOW, Registries.BLOCK)
                .addTag(TagRegistry.SIMPLE_MOUNTED_STORAGE, Registries.BLOCK)
                .addTag(BlockTags.MINEABLE_WITH_PICKAXE, Registries.BLOCK)
                .addTag(BlockTags.FEATURES_CANNOT_REPLACE, Registries.BLOCK)
                .addTag(BlockTags.GUARDED_BY_PIGLINS, Registries.BLOCK)
                .addTag(TagRegistry.BONKABLE_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.QUESTION_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.BONKABLE_BLOCK_ITEMS, Registries.ITEM)
                .addTag(TagRegistry.QUESTION_BLOCK_ITEMS, Registries.ITEM)
                .addTile(QuestionBlockEntity::new)
                .requiresChildren("bricks")
                .defaultRecipe()
                .setTabKey(tab)
                .build();
        this.addEntry(questionBlock);

        smashableBricks = SimpleEntrySet.builder(StoneType.class, "bricks", "smashable",
                        BlockRegistry.SMASHABLE_STONE_BRICKS, () -> StoneTypeRegistry.STONE_TYPE,
                        stoneType -> new Block(Utils.copyPropertySafe(stoneType.stone)))
                .addTexture(modRes("block/stone_question_bricks_overlay"))
                .addTag(BlockTags.MINEABLE_WITH_PICKAXE, Registries.BLOCK)
                .addTag(TagRegistry.SMASHABLE_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.SMASHABLE_BLOCK_ITEMS, Registries.ITEM)
                .requiresChildren("bricks")
                .defaultRecipe()
                .setTabKey(tab)
                .build();
        this.addEntry(smashableBricks);

        storageBricks = SimpleEntrySet.builder(StoneType.class, "bricks", "storage",
                        BlockRegistry.STORAGE_STONE_BRICKS, () -> StoneTypeRegistry.STONE_TYPE,
                        stoneType -> new StorageBrickBlock(Utils.copyPropertySafe(stoneType.stone)))
                .addTexture(modRes("block/stone_question_bricks_overlay"))
                .addTag(TagRegistry.COPYCAT_ALLOW, Registries.BLOCK)
                .addTag(TagRegistry.SIMPLE_MOUNTED_STORAGE, Registries.BLOCK)
                .addTag(BlockTags.MINEABLE_WITH_PICKAXE, Registries.BLOCK)
                .addTag(BlockTags.FEATURES_CANNOT_REPLACE, Registries.BLOCK)
                .addTag(BlockTags.GUARDED_BY_PIGLINS, Registries.BLOCK)
                .addTag(TagRegistry.BONKABLE_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.STORAGE_BRICK_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.BONKABLE_BLOCK_ITEMS, Registries.ITEM)
                .addTag(TagRegistry.STORAGE_BRICK_ITEMS, Registries.ITEM)
                .addTile(QuestionBlockEntity::new)
                .requiresChildren("bricks")
                .defaultRecipe()
                .setTabKey(tab)
                .build();
        this.addEntry(storageBricks);
    }
}
