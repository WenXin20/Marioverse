package com.wenxin2.marioverse.integration.wood_good_compat;

import com.wenxin2.marioverse.MarioverseCreativeTabs;
import com.wenxin2.marioverse.blocks.BridgeBlock;
import com.wenxin2.marioverse.blocks.BridgeStairBlock;
import com.wenxin2.marioverse.blocks.PicketFenceBlock;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.mehvahdjukaar.every_compat.EveryCompat;
import net.mehvahdjukaar.every_compat.api.PaletteStrategies;
import net.mehvahdjukaar.every_compat.api.SimpleEntrySet;
import net.mehvahdjukaar.every_compat.modules.EveryCompatModule;
import net.mehvahdjukaar.moonlight.api.set.wood.VanillaWoodTypes;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;

public class WoodModule extends EveryCompatModule {
    public final SimpleEntrySet<WoodType, Block> bridge;
    public final SimpleEntrySet<WoodType, Block> bridgeStairs;
    public final SimpleEntrySet<WoodType, Block> picketFence;
    public final SimpleEntrySet<WoodType, Block> strippedBridge;
    public final SimpleEntrySet<WoodType, Block> strippedBridgeStairs;

    public WoodModule(String modId) {
        super(modId, "mv");
        DeferredHolder<CreativeModeTab, CreativeModeTab> buildingBlocksTab = MarioverseCreativeTabs.MARIOVERSE_BUILDING_BLOCKS_TAB;

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
    }
}
