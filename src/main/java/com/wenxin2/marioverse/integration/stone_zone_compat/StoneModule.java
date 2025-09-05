package com.wenxin2.marioverse.integration.stone_zone_compat;

import com.wenxin2.marioverse.MarioverseCreativeTabs;
import com.wenxin2.marioverse.blocks.BrickPedestalBlock;
import com.wenxin2.marioverse.blocks.InvisibleQuestionBlock;
import com.wenxin2.marioverse.blocks.QuestionBlock;
import com.wenxin2.marioverse.blocks.StorageBrickBlock;
import com.wenxin2.marioverse.registries.BlockEntityRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.mehvahdjukaar.every_compat.api.PaletteStrategies;
import net.mehvahdjukaar.every_compat.api.PaletteStrategy;
import net.mehvahdjukaar.every_compat.api.SimpleEntrySet;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.stone_zone.api.StonePaletteStrategies;
import net.mehvahdjukaar.stone_zone.api.StoneZoneEntrySet;
import net.mehvahdjukaar.stone_zone.api.StoneZoneModule;
import net.mehvahdjukaar.stone_zone.api.set.stone.StoneType;
import net.mehvahdjukaar.stone_zone.api.set.stone.VanillaStoneChildKeys;
import net.mehvahdjukaar.stone_zone.api.set.stone.VanillaStoneTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;

public class StoneModule extends StoneZoneModule {
    public final SimpleEntrySet<StoneType, Block> brickPedestal;
    public final SimpleEntrySet<StoneType, Block> invisibleQuestionBlock;
    public final SimpleEntrySet<StoneType, Block> questionBlock;
    public final SimpleEntrySet<StoneType, Block> smashableBricks;
    public final SimpleEntrySet<StoneType, Block> storageBricks;

    public static final PaletteStrategy darkerPalette = PaletteStrategies.registerCached((blockType, resourceManager) -> {
        return PaletteStrategies.makePaletteFromChild(blockType, resourceManager, VanillaStoneChildKeys.BRICKS, null, p -> {
            while (p.size() > 6)
                p.reduce();

            p.reduceUp();
            p.reduceUp();
            p.reduceUp();
        });
    });

    public static final PaletteStrategy lighterPalette = PaletteStrategies.registerCached((blockType, resourceManager) -> {
        return PaletteStrategies.makePaletteFromChild(blockType, resourceManager, VanillaStoneChildKeys.BRICKS, null, p -> {
            while(p.size() > 2)
                p.reduce();

            p.increaseDown();
            p.increaseDown();
            p.increaseUp();
            p.increaseUp();
            p.increaseUp();
        });
    });

    public StoneModule(String modId, String shortId) {
        super(modId, shortId);
        ResourceKey<CreativeModeTab> tab = MarioverseCreativeTabs.MARIOVERSE_BLOCKS_TAB.getKey();

        brickPedestal = StoneZoneEntrySet.of(StoneType.class, "brick_pedestal",
                        BlockRegistry.STONE_BRICK_PEDESTAL, () -> VanillaStoneTypes.STONE,
                        stoneType -> new BrickPedestalBlock(Utils.copyPropertySafe(stoneType.stone)))
                .addTag(BlockTags.MINEABLE_WITH_PICKAXE, Registries.BLOCK)
                .addTag(TagRegistry.BRICK_PEDESTAL_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.BRICK_PEDESTAL_ITEMS, Registries.ITEM)
                .addRecipe(modRes("stone_brick_pedestal_from_stone_stonecutting"))
                .addRecipe(modRes("stone_brick_pedestal_stonecutting"))
                .requiresChildren("bricks")
                .defaultRecipe()
                .setTabKey(tab)
                .build();
        this.addEntry(brickPedestal);

        invisibleQuestionBlock = StoneZoneEntrySet.of(StoneType.class, "question_bricks", "invisible",
                        BlockRegistry.INVISIBLE_STONE_QUESTION_BRICKS, () -> VanillaStoneTypes.STONE,
                        stoneType -> new InvisibleQuestionBlock(Utils.copyPropertySafe(stoneType.stone)))
                .addTexture(modRes("block/invisible_stone_question_bricks"), StonePaletteStrategies.BRICKS_STANDARD)
                .addTag(TagRegistry.MOVABLE_EMPTY_COLLIDER, Registries.BLOCK)
                .addTag(TagRegistry.SIMPLE_MOUNTED_STORAGE, Registries.BLOCK)
                .addTag(BlockTags.MINEABLE_WITH_PICKAXE, Registries.BLOCK)
                .addTag(BlockTags.FEATURES_CANNOT_REPLACE, Registries.BLOCK)
                .addTag(BlockTags.GUARDED_BY_PIGLINS, Registries.BLOCK)
                .addTag(TagRegistry.BONKABLE_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.INVISIBLE_QUESTION_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.BONKABLE_BLOCK_ITEMS, Registries.ITEM)
                .addTag(TagRegistry.INVISIBLE_QUESTION_BLOCK_ITEMS, Registries.ITEM)
                .addTile(BlockEntityRegistry.INVISIBLE_QUESTION_BLOCK_ENTITY)
                .requiresChildren("bricks")
                .setTabKey(tab)
                .build();
        this.addEntry(invisibleQuestionBlock);

        questionBlock = StoneZoneEntrySet.of(StoneType.class, "question_bricks",
                        BlockRegistry.STONE_QUESTION_BRICKS, () -> VanillaStoneTypes.STONE,
                        stoneType -> new QuestionBlock(Utils.copyPropertySafe(stoneType.stone)))
                .addTexture(modRes("block/empty_stone_question_bricks"), StonePaletteStrategies.BRICKS_STANDARD)
                .addTexture(modRes("block/stone_question_bricks"), StonePaletteStrategies.BRICKS_STANDARD)
                .addTag(TagRegistry.COPYCAT_ALLOW, Registries.BLOCK)
                .addTag(TagRegistry.SIMPLE_MOUNTED_STORAGE, Registries.BLOCK)
                .addTag(BlockTags.MINEABLE_WITH_PICKAXE, Registries.BLOCK)
                .addTag(BlockTags.FEATURES_CANNOT_REPLACE, Registries.BLOCK)
                .addTag(BlockTags.GUARDED_BY_PIGLINS, Registries.BLOCK)
                .addTag(TagRegistry.BONKABLE_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.QUESTION_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.BONKABLE_BLOCK_ITEMS, Registries.ITEM)
                .addTag(TagRegistry.QUESTION_BLOCK_ITEMS, Registries.ITEM)
                .addTile(BlockEntityRegistry.QUESTION_BLOCK_ENTITY)
                .requiresChildren("bricks")
                .defaultRecipe()
                .setTabKey(tab)
                .build();
        this.addEntry(questionBlock);

        smashableBricks = StoneZoneEntrySet.of(StoneType.class, "bricks", "smashable",
                        BlockRegistry.SMASHABLE_TUFF_BRICKS, () -> VanillaStoneTypes.TUFF,
                        stoneType -> new Block(Utils.copyPropertySafe(stoneType.stone)))
                .addTexture(modRes("block/smashable_tuff_bricks_overlay"), darkerPalette)
                .addTag(BlockTags.MINEABLE_WITH_PICKAXE, Registries.BLOCK)
                .addTag(TagRegistry.SMASHABLE_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.SMASHABLE_BLOCK_ITEMS, Registries.ITEM)
                .addRecipe(modRes("smashable_tuff_bricks_from_tuff_stonecutting"))
                .addRecipe(modRes("smashable_tuff_bricks_stonecutting"))
                .requiresChildren("bricks")
                .setTabKey(tab)
                .build();
        this.addEntry(smashableBricks);

        storageBricks = StoneZoneEntrySet.of(StoneType.class, "bricks", "storage",
                        BlockRegistry.STORAGE_STONE_BRICKS, () -> VanillaStoneTypes.STONE,
                        stoneType -> new StorageBrickBlock(Utils.copyPropertySafe(stoneType.stone)))
                .addTexture(modRes("block/stone_question_bricks_overlay"), lighterPalette)
                .addTag(TagRegistry.COPYCAT_ALLOW, Registries.BLOCK)
                .addTag(TagRegistry.SIMPLE_MOUNTED_STORAGE, Registries.BLOCK)
                .addTag(BlockTags.MINEABLE_WITH_PICKAXE, Registries.BLOCK)
                .addTag(BlockTags.FEATURES_CANNOT_REPLACE, Registries.BLOCK)
                .addTag(BlockTags.GUARDED_BY_PIGLINS, Registries.BLOCK)
                .addTag(TagRegistry.BONKABLE_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.STORAGE_BRICK_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.BONKABLE_BLOCK_ITEMS, Registries.ITEM)
                .addTag(TagRegistry.STORAGE_BRICK_ITEMS, Registries.ITEM)
                .addTile(BlockEntityRegistry.STORAGE_BRICKS_BLOCK_ENTITY)
                .requiresChildren("bricks")
                .defaultRecipe()
                .setTabKey(tab)
                .build();
        this.addEntry(storageBricks);
    }
}
