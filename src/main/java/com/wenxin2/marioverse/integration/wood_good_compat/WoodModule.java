package com.wenxin2.marioverse.integration.wood_good_compat;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.MarioverseCreativeTabs;
import com.wenxin2.marioverse.blocks.BridgeBlock;
import com.wenxin2.marioverse.blocks.BridgeStairBlock;
import com.wenxin2.marioverse.blocks.HangingArrowSignBlock;
import com.wenxin2.marioverse.blocks.LargeStandingArrowSignBlock;
import com.wenxin2.marioverse.blocks.LargeWallArrowSignBlock;
import com.wenxin2.marioverse.blocks.PicketFenceBlock;
import com.wenxin2.marioverse.blocks.StandingArrowSignBlock;
import com.wenxin2.marioverse.blocks.WallArrowSignBlock;
import com.wenxin2.marioverse.items.ArrowSignItem;
import com.wenxin2.marioverse.items.LargeArrowSignItem;
import com.wenxin2.marioverse.registries.BlockEntityRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.function.Consumer;
import net.mehvahdjukaar.every_compat.EveryCompat;
import net.mehvahdjukaar.every_compat.api.PaletteStrategies;
import net.mehvahdjukaar.every_compat.api.SimpleEntrySet;
import net.mehvahdjukaar.every_compat.modules.EveryCompatModule;
import net.mehvahdjukaar.moonlight.api.resources.RPUtils;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceGenTask;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceSink;
import net.mehvahdjukaar.moonlight.api.set.wood.VanillaWoodTypes;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;

public class WoodModule extends EveryCompatModule {
    // Moonlight auto-detects our own mushroot as a compat wood too; re-registering its WoodType here
    // collides with WoodTypeRegistry.MUSHROOT and crashes HangingSignRenderer. Skip it - it already
    // has full native support.
    private static final String EXCLUDED_WOOD_TYPES = "superbb:mushroot";

    public final SimpleEntrySet<WoodType, Block> bridge;
    public final SimpleEntrySet<WoodType, Block> bridgeStairs;
    public final SimpleEntrySet<WoodType, Block> picketFence;
    public final SimpleEntrySet<WoodType, Block> strippedBridge;
    public final SimpleEntrySet<WoodType, Block> strippedBridgeStairs;
    public final SimpleEntrySet<WoodType, Block> wallArrowSign;
    public final SimpleEntrySet<WoodType, Block> hangingArrowSign;
    public final SimpleEntrySet<WoodType, Block> arrowSign;
    public final SimpleEntrySet<WoodType, Block> largeWallArrowSign;
    public final SimpleEntrySet<WoodType, Block> largeArrowSign;

    public WoodModule(String modId) {
        super(modId, "mv");
        DeferredHolder<CreativeModeTab, CreativeModeTab> buildingBlocksTab = MarioverseCreativeTabs.MARIOVERSE_BUILDING_BLOCKS_TAB;
        DeferredHolder<CreativeModeTab, CreativeModeTab> functionalBlocksTab = MarioverseCreativeTabs.MARIOVERSE_FUNCTIONAL_BLOCKS_TAB;

        bridge = SimpleEntrySet.builder(WoodType.class, "log_bridge",
                        BlockRegistry.OAK_LOG_BRIDGE, () -> VanillaWoodTypes.OAK,
                        woodType -> new BridgeBlock(woodType.log, Utils.copyPropertySafe(woodType.log)))
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(TagRegistry.FLAMMABLE_BRIDGE_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.WOODEN_BRIDGE_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.FLAMMABLE_BRIDGE_ITEMS, Registries.ITEM)
                .addTag(TagRegistry.WOODEN_BRIDGE_ITEMS, Registries.ITEM)
                .defaultRecipe()
                .setTab(buildingBlocksTab)
                .build();
        this.addEntry(bridge);

        strippedBridge = SimpleEntrySet.builder(WoodType.class, "log_bridge", "stripped",
                        BlockRegistry.STRIPPED_OAK_LOG_BRIDGE, () -> VanillaWoodTypes.OAK,
                        woodType -> new BridgeBlock(woodType.log, Utils.copyPropertySafe(woodType.log)))
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(TagRegistry.FLAMMABLE_BRIDGE_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.WOODEN_BRIDGE_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.FLAMMABLE_BRIDGE_ITEMS, Registries.ITEM)
                .addTag(TagRegistry.WOODEN_BRIDGE_ITEMS, Registries.ITEM)
                .requiresChildren("stripped_log")
                .defaultRecipe()
                .setTab(buildingBlocksTab)
                .build();
        this.addEntry(strippedBridge);

        bridgeStairs = SimpleEntrySet.builder(WoodType.class, "log_bridge_stairs",
                        BlockRegistry.OAK_LOG_BRIDGE_STAIRS, () -> VanillaWoodTypes.OAK,
                        woodType -> new BridgeStairBlock(woodType.log.defaultBlockState(), Utils.copyPropertySafe(woodType.log)))
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(BlockTags.WOODEN_STAIRS, Registries.BLOCK)
                .addTag(TagRegistry.FLAMMABLE_BRIDGE_STAIR_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.WOODEN_BRIDGE_STAIR_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.FLAMMABLE_BRIDGE_STAIR_ITEMS, Registries.ITEM)
                .addTag(TagRegistry.WOODEN_BRIDGE_STAIR_ITEMS, Registries.ITEM)
                .addTag(ItemTags.WOODEN_STAIRS, Registries.ITEM)
                .defaultRecipe()
                .setTab(buildingBlocksTab)
                .build();
        this.addEntry(bridgeStairs);

        strippedBridgeStairs = SimpleEntrySet.builder(WoodType.class, "log_bridge_stairs", "stripped",
                        BlockRegistry.STRIPPED_OAK_LOG_BRIDGE_STAIRS, () -> VanillaWoodTypes.OAK,
                        woodType -> new BridgeStairBlock(woodType.log.defaultBlockState(), Utils.copyPropertySafe(woodType.log)))
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(BlockTags.WOODEN_STAIRS, Registries.BLOCK)
                .addTag(TagRegistry.FLAMMABLE_BRIDGE_STAIR_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.WOODEN_BRIDGE_STAIR_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.FLAMMABLE_BRIDGE_STAIR_ITEMS, Registries.ITEM)
                .addTag(TagRegistry.WOODEN_BRIDGE_STAIR_ITEMS, Registries.ITEM)
                .addTag(ItemTags.WOODEN_STAIRS, Registries.ITEM)
                .requiresChildren("stripped_log")
                .defaultRecipe()
                .setTab(buildingBlocksTab)
                .build();
        this.addEntry(strippedBridgeStairs);

        picketFence = SimpleEntrySet.builder(WoodType.class, "picket_fence",
                        BlockRegistry.OAK_PICKET_FENCE, () -> VanillaWoodTypes.OAK,
                        woodType -> new PicketFenceBlock(Utils.copyPropertySafe(woodType.planks)))
                .addTexture(modRes("block/oak_picket_fence"), PaletteStrategies.PLANKS_STANDARD)
                .addTextureM(modRes("block/oak_picket_fence_back"), EveryCompat.res("block/mv/oak_picket_fence_back_mask"),
                        PaletteStrategies.PLANKS_STANDARD)
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(TagRegistry.FLAMMABLE_PICKET_FENCES, Registries.BLOCK)
                .addTag(TagRegistry.WOODEN_PICKET_FENCES, Registries.BLOCK)
                .addTag(TagRegistry.FLAMMABLE_PICKET_FENCE_ITEMS, Registries.ITEM)
                .addTag(TagRegistry.WOODEN_PICKET_FENCE_ITEMS, Registries.ITEM)
                .addTag(BlockTags.WOODEN_FENCES, Registries.ITEM)
                .addTag(Tags.Blocks.FENCES_WOODEN, Registries.ITEM)
                .requiresChildren("planks")
                .setTab(buildingBlocksTab)
                .defaultRecipe()
                .build();
        this.addEntry(picketFence);

        wallArrowSign = SimpleEntrySet.builder(WoodType.class, "wall_arrow_sign",
                        BlockRegistry.OAK_WALL_ARROW_SIGN, () -> VanillaWoodTypes.OAK,
                        woodType -> new WallArrowSignBlock(woodType.toVanillaOrOak(), Utils.copyPropertySafe(woodType.planks)))
                .addTile(BlockEntityRegistry.ARROW_SIGN)
                .noItem()
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .excludeBlockTypes(EXCLUDED_WOOD_TYPES)
                .setTab(functionalBlocksTab)
                .build();
        this.addEntry(wallArrowSign);

        hangingArrowSign = SimpleEntrySet.builder(WoodType.class, "hanging_arrow_sign",
                        BlockRegistry.OAK_HANGING_ARROW_SIGN, () -> VanillaWoodTypes.OAK,
                        woodType -> new HangingArrowSignBlock(woodType.toVanillaOrOak(), Utils.copyPropertySafe(woodType.planks)))
                .addTile(BlockEntityRegistry.ARROW_SIGN)
                .noItem()
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .excludeBlockTypes(EXCLUDED_WOOD_TYPES)
                .setTab(functionalBlocksTab)
                .build();
        this.addEntry(hangingArrowSign);

        arrowSign = SimpleEntrySet.builder(WoodType.class, "arrow_sign",
                        BlockRegistry.OAK_ARROW_SIGN, () -> VanillaWoodTypes.OAK,
                        woodType -> new StandingArrowSignBlock(woodType.toVanillaOrOak(), Utils.copyPropertySafe(woodType.planks)))
                .addTile(BlockEntityRegistry.ARROW_SIGN)
                .addTextureM(modRes("entity/signs/arrow/oak_arrow_sign"), EveryCompat.res("block/mv/oak_arrow_sign_mask"),
                        PaletteStrategies.SIGN_LIKE)
                .addTexture(modRes("item/arrow_sign/oak_arrow_sign"), PaletteStrategies.SIGN_LIKE)
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .excludeBlockTypes(EXCLUDED_WOOD_TYPES)
                .addCustomItem((woodType, block, properties) -> new ArrowSignItem(properties.stacksTo(16),
                        block, this.wallArrowSign.blocks.get(woodType), this.hangingArrowSign.blocks.get(woodType)))
                .setTab(functionalBlocksTab)
                .build();
        this.addEntry(arrowSign);

        largeWallArrowSign = SimpleEntrySet.builder(WoodType.class, "wall_arrow_sign", "large",
                        BlockRegistry.LARGE_OAK_WALL_ARROW_SIGN, () -> VanillaWoodTypes.OAK,
                        woodType -> new LargeWallArrowSignBlock(woodType.toVanillaOrOak(), Utils.copyPropertySafe(woodType.planks)))
                .addTile(BlockEntityRegistry.ARROW_SIGN)
                .noItem()
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .excludeBlockTypes(EXCLUDED_WOOD_TYPES)
                .setTab(functionalBlocksTab)
                .build();
        this.addEntry(largeWallArrowSign);

        largeArrowSign = SimpleEntrySet.builder(WoodType.class, "arrow_sign", "large",
                        BlockRegistry.LARGE_OAK_ARROW_SIGN, () -> VanillaWoodTypes.OAK,
                        woodType -> new LargeStandingArrowSignBlock(woodType.toVanillaOrOak(), Utils.copyPropertySafe(woodType.planks)))
                .addTile(BlockEntityRegistry.ARROW_SIGN)
                .addTexture(modRes("entity/signs/large_arrow/large_oak_arrow_sign"), PaletteStrategies.SIGN_LIKE)
                .addTexture(modRes("item/large_arrow_sign/large_oak_arrow_sign"), PaletteStrategies.SIGN_LIKE)
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .excludeBlockTypes(EXCLUDED_WOOD_TYPES)
                .addCustomItem((woodType, block, properties) -> new LargeArrowSignItem(properties.stacksTo(16),
                        block, this.largeWallArrowSign.blocks.get(woodType)))
                .setTab(functionalBlocksTab)
                .build();
        this.addEntry(largeArrowSign);
    }

    private static net.minecraft.world.level.block.state.properties.WoodType vanillaWoodType(WoodType woodType) {
        return net.minecraft.world.level.block.state.properties.WoodType.register(
                new net.minecraft.world.level.block.state.properties.WoodType(
                        Marioverse.MOD_ID + ":" + woodType.getId().getPath(), woodType.toVanillaOrOak().setType()));
    }

    // EveryCompat's automatic per-property blockstate generation fails silently for these blocks;
    // write the same single-variant blockstate/model our own datagen uses instead.
    @Override
    public void addDynamicClientResources(Consumer<ResourceGenTask> executor) {
        super.addDynamicClientResources(executor);
        executor.accept((manager, sink) -> {
            writeSimpleBlockModels(manager, sink, arrowSign);
            writeSimpleBlockModels(manager, sink, wallArrowSign);
            writeSimpleBlockModels(manager, sink, hangingArrowSign);
            writeSimpleBlockModels(manager, sink, largeArrowSign);
            writeSimpleBlockModels(manager, sink, largeWallArrowSign);
        });
    }

    private static void writeSimpleBlockModels(ResourceManager manager, ResourceSink sink,
                                               SimpleEntrySet<WoodType, Block> entrySet) {
        for (Map.Entry<WoodType, Block> entry : entrySet.blocks.entrySet()) {
            WoodType woodType = entry.getKey();
            Block block = entry.getValue();
            ResourceLocation blockId = Utils.getID(block);
            ResourceLocation modelId = blockId.withPrefix("block/");

            JsonObject variant = new JsonObject();
            variant.addProperty("model", modelId.toString());
            JsonObject variants = new JsonObject();
            variants.add("", variant);
            JsonObject blockstate = new JsonObject();
            blockstate.add("variants", variants);
            sink.addBlockState(blockId, blockstate);

            JsonObject textures = new JsonObject();
            try {
                ResourceLocation particle = RPUtils.findFirstBlockTextureLocation(manager, woodType.planks);
                textures.addProperty("particle", particle.toString());
            } catch (Exception ignored) {
            }
            JsonObject model = new JsonObject();
            model.add("textures", textures);
            sink.addBlockModel(modelId, model);
        }
    }
}
