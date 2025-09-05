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
import net.mehvahdjukaar.moonlight.api.resources.textures.Palette;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.stone_zone.api.StonePaletteStrategies;
import net.mehvahdjukaar.stone_zone.api.StoneZoneEntrySet;
import net.mehvahdjukaar.stone_zone.api.StoneZoneModule;
import net.mehvahdjukaar.stone_zone.api.set.mud.MudType;
import net.mehvahdjukaar.stone_zone.api.set.mud.VanillaMudTypes;
import net.mehvahdjukaar.stone_zone.api.set.stone.VanillaStoneChildKeys;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;

public class MudModule extends StoneZoneModule {
    public final SimpleEntrySet<MudType, Block> brickPedestal;
    public final SimpleEntrySet<MudType, Block> invisibleQuestionBlock;
    public final SimpleEntrySet<MudType, Block> questionBlock;
    public final SimpleEntrySet<MudType, Block> smashableBricks;
    public final SimpleEntrySet<MudType, Block> storageBricks;

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

    public static final PaletteStrategy questionPalette = PaletteStrategies.registerCached((blockType, resourceManager) -> {
        return PaletteStrategies.makePaletteFromChild(blockType, resourceManager, VanillaStoneChildKeys.BRICKS, null, Palette::increaseUp);
    });

    public MudModule(String modId, String shortId) {
        super(modId, shortId);
        ResourceKey<CreativeModeTab> tab = MarioverseCreativeTabs.MARIOVERSE_BLOCKS_TAB.getKey();

        brickPedestal = StoneZoneEntrySet.of(MudType.class, "brick_pedestal",
                        BlockRegistry.MUD_BRICK_PEDESTAL, () -> VanillaMudTypes.MUD,
                        mudType -> new BrickPedestalBlock(Utils.copyPropertySafe(mudType.mud)))
                .addTag(BlockTags.MINEABLE_WITH_PICKAXE, Registries.BLOCK)
                .addTag(TagRegistry.BRICK_PEDESTAL_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.BRICK_PEDESTAL_ITEMS, Registries.ITEM)
                .addRecipe(modRes("mud_brick_pedestal_from_packed_mud_stonecutting"))
                .addRecipe(modRes("mud_brick_pedestal_stonecutting"))
                .requiresChildren("bricks")
                .defaultRecipe()
                .setTabKey(tab)
                .build();
        this.addEntry(brickPedestal);

        invisibleQuestionBlock = StoneZoneEntrySet.of(MudType.class, "question_bricks", "invisible",
                        BlockRegistry.INVISIBLE_MUD_QUESTION_BRICKS, () -> VanillaMudTypes.MUD,
                        mudType -> new InvisibleQuestionBlock(Utils.copyPropertySafe(mudType.mud)))
                .addTexture(modRes("block/invisible_mud_question_bricks"), StonePaletteStrategies.BRICKS_STANDARD)
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

        questionBlock = StoneZoneEntrySet.of(MudType.class, "question_bricks",
                        BlockRegistry.MUD_QUESTION_BRICKS, () -> VanillaMudTypes.MUD,
                        mudType -> new QuestionBlock(Utils.copyPropertySafe(mudType.mud)))
                .addTexture(modRes("block/empty_mud_question_bricks"), StonePaletteStrategies.BRICKS_STANDARD)
                .addTexture(modRes("block/mud_question_bricks"), questionPalette)
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

        smashableBricks = StoneZoneEntrySet.of(MudType.class, "bricks", "smashable",
                        BlockRegistry.SMASHABLE_MUD_BRICKS, () -> VanillaMudTypes.MUD,
                        mudType -> new Block(Utils.copyPropertySafe(mudType.mud)))
                .addTexture(modRes("block/smashable_mud_bricks_overlay"), darkerPalette)
                .addTag(BlockTags.MINEABLE_WITH_PICKAXE, Registries.BLOCK)
                .addTag(TagRegistry.SMASHABLE_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.SMASHABLE_BLOCK_ITEMS, Registries.ITEM)
                .addRecipe(modRes("smashable_mud_bricks_from_packed_mud_stonecutting"))
                .addRecipe(modRes("smashable_mud_bricks_stonecutting"))
                .requiresChildren("bricks")
                .setTabKey(tab)
                .build();
        this.addEntry(smashableBricks);

        storageBricks = StoneZoneEntrySet.of(MudType.class, "bricks", "storage",
                        BlockRegistry.STORAGE_MUD_BRICKS, () -> VanillaMudTypes.MUD,
                        mudType -> new StorageBrickBlock(Utils.copyPropertySafe(mudType.mud)))
                .addTexture(modRes("block/mud_question_bricks_overlay"), lighterPalette)
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
