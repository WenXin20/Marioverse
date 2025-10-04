package com.wenxin2.marioverse.integration.wood_good_compat;

import com.wenxin2.marioverse.MarioverseCreativeTabs;
import com.wenxin2.marioverse.blocks.BridgeBlock;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.mehvahdjukaar.every_compat.EveryCompat;
import net.mehvahdjukaar.every_compat.api.SimpleEntrySet;
import net.mehvahdjukaar.every_compat.api.SimpleModule;
import net.mehvahdjukaar.moonlight.api.set.wood.VanillaWoodTypes;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;

public class WoodModule extends SimpleModule {
    public final SimpleEntrySet<WoodType, Block> bridge;
    public final SimpleEntrySet<WoodType, Block> strippedBridge;

    public WoodModule(String modId) {
        super(modId, "mv", EveryCompat.MOD_ID);
        ResourceKey<CreativeModeTab> tab = MarioverseCreativeTabs.MARIOVERSE_BLOCKS_TAB.getKey();

        bridge = SimpleEntrySet.builder(WoodType.class, "log_bridge",
                        BlockRegistry.OAK_LOG_BRIDGE, () -> VanillaWoodTypes.OAK,
                        woodType -> new BridgeBlock(woodType.log, Utils.copyPropertySafe(woodType.log)))
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(TagRegistry.WOODEN_BRIDGE_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.WOODEN_BRIDGE_ITEMS, Registries.ITEM)
                .defaultRecipe()
                .setTabKey(tab)
                .build();
        this.addEntry(bridge);

        strippedBridge = SimpleEntrySet.builder(WoodType.class, "log_bridge", "stripped",
                        BlockRegistry.STRIPPED_OAK_LOG_BRIDGE, () -> VanillaWoodTypes.OAK,
                        woodType -> new BridgeBlock(woodType.log, Utils.copyPropertySafe(woodType.log)))
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(TagRegistry.WOODEN_BRIDGE_BLOCKS, Registries.BLOCK)
                .addTag(TagRegistry.WOODEN_BRIDGE_ITEMS, Registries.ITEM)
                .requiresChildren("stripped_log")
                .defaultRecipe()
                .setTabKey(tab)
                .build();
        this.addEntry(strippedBridge);
    }
}
