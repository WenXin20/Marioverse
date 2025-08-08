package com.wenxin2.marioverse.integration.stone_zone_compat;

import com.wenxin2.marioverse.MarioverseCreativeTabs;
import com.wenxin2.marioverse.blocks.BrickPedestalBlock;
import com.wenxin2.marioverse.blocks.InvisibleQuestionBlock;
import com.wenxin2.marioverse.blocks.QuestionBlock;
import com.wenxin2.marioverse.blocks.StorageBrickBlock;
import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.io.IOException;
import java.util.List;
import net.mehvahdjukaar.every_compat.EveryCompat;
import net.mehvahdjukaar.every_compat.api.SimpleEntrySet;
import net.mehvahdjukaar.every_compat.dynamicpack.ClientDynamicResourcesHandler;
import net.mehvahdjukaar.moonlight.api.resources.RPUtils;
import net.mehvahdjukaar.moonlight.api.resources.textures.Palette;
import net.mehvahdjukaar.moonlight.api.resources.textures.PaletteColor;
import net.mehvahdjukaar.moonlight.api.resources.textures.Respriter;
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.stone_zone.StoneZone;
import net.mehvahdjukaar.stone_zone.api.StoneZoneEntrySet;
import net.mehvahdjukaar.stone_zone.api.StoneZoneModule;
import net.mehvahdjukaar.stone_zone.api.set.MudType;
import net.mehvahdjukaar.stone_zone.api.set.MudTypeRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;

public class MudModule extends StoneZoneModule {
    public final SimpleEntrySet<MudType, Block> brickPedestal;
    public final SimpleEntrySet<MudType, Block> invisibleQuestionBlock;
    public final SimpleEntrySet<MudType, Block> questionBlock;
    public final SimpleEntrySet<MudType, Block> smashableBricks;
    public final SimpleEntrySet<MudType, Block> storageBricks;

    public MudModule(String modId, String shortId) {
        super(modId, shortId);
        ResourceKey<CreativeModeTab> tab = MarioverseCreativeTabs.MARIOVERSE_BLOCKS_TAB.getKey();

        brickPedestal = StoneZoneEntrySet.of(MudType.class, "brick_pedestal",
                        BlockRegistry.MUD_BRICK_PEDESTAL, MudTypeRegistry::getMudType,
                        mudType -> new BrickPedestalBlock(Utils.copyPropertySafe(mudType.mud)))
                .createPaletteFromBricks()
                .addTag(BlockTags.MINEABLE_WITH_PICKAXE, Registries.BLOCK)
                .addTag(TagRegistry.BRICK_PEDESTAL_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.BRICK_PEDESTAL_ITEMS, Registries.ITEM)
                .addRecipe(modRes("mud_brick_pedestal_from_packed_mud_stonecutting"))
                .addRecipe(modRes("mud_brick_pedestal_stonecutting"))
                .defaultRecipe()
                .setTabKey(tab)
                .build();
        this.addEntry(brickPedestal);

        invisibleQuestionBlock = StoneZoneEntrySet.of(MudType.class, "question_bricks", "invisible",
                        BlockRegistry.INVISIBLE_MUD_QUESTION_BRICKS, MudTypeRegistry::getMudType,
                        mudType -> new InvisibleQuestionBlock(Utils.copyPropertySafe(mudType.mud)))
                .createPaletteFromBricks()
                .addTexture(modRes("block/invisible_mud_question_bricks"))
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
                .setTabKey(tab)
                .build();
        this.addEntry(invisibleQuestionBlock);

        questionBlock = StoneZoneEntrySet.of(MudType.class, "question_bricks",
                        BlockRegistry.MUD_QUESTION_BRICKS, MudTypeRegistry::getMudType,
                        mudType -> new QuestionBlock(Utils.copyPropertySafe(mudType.mud)))
                .createPaletteFromBricks()
                .addTexture(modRes("block/empty_mud_question_bricks"))
                .addTexture(modRes("block/mud_question_bricks"))
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
                .defaultRecipe()
                .setTabKey(tab)
                .build();
        this.addEntry(questionBlock);

        smashableBricks = StoneZoneEntrySet.of(MudType.class, "bricks", "smashable",
                        BlockRegistry.SMASHABLE_MUD_BRICKS, MudTypeRegistry::getMudType,
                        mudType -> new Block(Utils.copyPropertySafe(mudType.mud)))
                .createPaletteFromBricks()
                .addTexture(modRes("block/smashable_mud_bricks_overlay"))
                .addTag(BlockTags.MINEABLE_WITH_PICKAXE, Registries.BLOCK)
                .addTag(TagRegistry.SMASHABLE_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.SMASHABLE_BLOCK_ITEMS, Registries.ITEM)
                .addRecipe(modRes("smashable_mud_bricks_from_packed_mud_stonecutting"))
                .addRecipe(modRes("smashable_mud_bricks_stonecutting"))
                .setTabKey(tab)
                .build();
        this.addEntry(smashableBricks);

        storageBricks = StoneZoneEntrySet.of(MudType.class, "bricks", "storage",
                        BlockRegistry.STORAGE_MUD_BRICKS, MudTypeRegistry::getMudType,
                        mudType -> new StorageBrickBlock(Utils.copyPropertySafe(mudType.mud)))
                .createPaletteFromBricks()
                .addTexture(modRes("block/mud_question_bricks_overlay"))
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
                .defaultRecipe()
                .setTabKey(tab)
                .build();
        this.addEntry(storageBricks);
    }

    @Override
    public void addDynamicClientResources(ClientDynamicResourcesHandler handler, ResourceManager manager) {
        super.addDynamicClientResources(handler, manager);

        try (TextureImage questionOverlay = TextureImage.open(manager, modRes("block/mud_question_bricks_overlay"));
             TextureImage bricks = TextureImage.open(manager, ResourceLocation.parse("block/mud_bricks"))) {
            storageBricks.blocks.forEach((mudType, block) -> {
                try (TextureImage mudTexture = TextureImage.open(manager, RPUtils.findFirstBlockTextureLocation(manager, mudType.mud));
                     TextureImage bricksTexture = TextureImage.open(manager, RPUtils.findFirstBlockTextureLocation(manager, mudType.bricksOrStone()))) {
                    ResourceLocation newResLoc = StoneZone.res("block/" + this.shortenedId() + "/" + mudType.getAppendableId() + "_question_bricks_overlay");
                    Respriter respriterSIDE = Respriter.of(questionOverlay);
                    Respriter respriterTOP = Respriter.of(bricks); // ITEM

                    List<Palette> listStone = Palette.fromAnimatedImage(mudTexture);
                    List<Palette> listBricks = Palette.fromAnimatedImage(bricksTexture);

                    // Recoloring ITEM textures
                    TextureImage recoloredITEM = respriterSIDE.recolor(listBricks);
                    TextureImage recoloredTOP = respriterTOP.recolor(listBricks);
                    recoloredTOP.applyOverlay(recoloredITEM);

                    // Item Texture
                    handler.dynamicPack.addAndCloseTexture(newResLoc, recoloredITEM);

                } catch (IOException e) {
                    handler.getLogger().error("Failed to get Mud Brick Texture for {} : {}", block, e);
                }
            });
        }
        catch (IOException e) {
            handler.getLogger().error("Failed to get Storage Brick Item Texture for ", e);
        }
    }
}
