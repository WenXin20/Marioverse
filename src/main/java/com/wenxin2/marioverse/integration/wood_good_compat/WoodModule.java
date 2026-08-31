package com.wenxin2.marioverse.integration.wood_good_compat;

import com.mojang.datafixers.util.Either;
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
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import com.wenxin2.marioverse.blocks.states.HalfBlockStates;
import com.wenxin2.marioverse.blocks.states.SideBlockStates;
import com.wenxin2.marioverse.data.ArrowColorShapedRecipe;
import com.wenxin2.marioverse.data.ArrowColorShapelessRecipe;
import com.wenxin2.marioverse.data.DyeColorIngredient;
import com.wenxin2.marioverse.items.ArrowSignItem;
import com.wenxin2.marioverse.items.LargeArrowSignItem;
import com.wenxin2.marioverse.registries.BlockEntityRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.mehvahdjukaar.every_compat.EveryCompat;
import net.mehvahdjukaar.every_compat.api.PaletteStrategies;
import net.mehvahdjukaar.every_compat.api.SimpleEntrySet;
import net.mehvahdjukaar.every_compat.modules.EveryCompatModule;
import net.mehvahdjukaar.moonlight.api.resources.RecipeTemplate;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceGenTask;
import net.mehvahdjukaar.moonlight.api.resources.recipe.BlockTypeSwapIngredient;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.api.set.wood.VanillaWoodTypes;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;

public class WoodModule extends EveryCompatModule {
    // EveryCompat's recipe copier (RecipeTemplate.makeSimilarRecipe) only knows how to remap recipe
    // classes explicitly registered here - without this, compat wood types silently get no recipe at
    // all for our custom recipe types (the failure is caught and logged, not thrown).
    static {
        RecipeTemplate.register(ArrowColorShapedRecipe.class, WoodModule::remapArrowColorShaped);
        RecipeTemplate.register(ArrowColorShapelessRecipe.class, WoodModule::remapArrowColorShapeless);
    }

    private static ArrowColorShapedRecipe remapArrowColorShaped(ArrowColorShapedRecipe recipe, BlockType from, BlockType to) {
        Map<String, Either<DyeColorIngredient, Ingredient>> newKey = new HashMap<>();
        for (Map.Entry<String, Either<DyeColorIngredient, Ingredient>> entry : recipe.getKey().entrySet())
            newKey.put(entry.getKey(), remapSlot(entry.getValue(), from, to));

        ItemStack originalResult = recipe.getResultItem(RegistryAccess.EMPTY);
        Item newResult = BlockType.changeItemType(originalResult.getItem(), from, to);
        return new ArrowColorShapedRecipe(recipe.getGroup(), recipe.category(), recipe.getPattern(), newKey, newResult, originalResult.getCount());
    }

    private static ArrowColorShapelessRecipe remapArrowColorShapeless(ArrowColorShapelessRecipe recipe, BlockType from, BlockType to) {
        List<Either<DyeColorIngredient, Ingredient>> newSlots = new ArrayList<>();
        for (Either<DyeColorIngredient, Ingredient> slot : recipe.getSlots())
            newSlots.add(remapSlot(slot, from, to));

        ItemStack originalResult = recipe.getResultItem(RegistryAccess.EMPTY);
        Item newResult = BlockType.changeItemType(originalResult.getItem(), from, to);
        return new ArrowColorShapelessRecipe(recipe.getGroup(), recipe.category(), newSlots, newResult, originalResult.getCount());
    }

    // Dye slots aren't wood-specific and stay untouched; plain slots (planks, chains, the sign item
    // itself for the recolor recipe) get the same ingredient-swap treatment vanilla shaped/shapeless
    // compat recipes get.
    private static Either<DyeColorIngredient, Ingredient> remapSlot(Either<DyeColorIngredient, Ingredient> slot, BlockType from, BlockType to) {
        return slot.map(Either::left, ingredient -> Either.right(BlockTypeSwapIngredient.create(ingredient, from, to)));
    }

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
                .setTab(buildingBlocksTab)
                .copyParentDrop()
                .defaultRecipe()
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
                .setTab(buildingBlocksTab)
                .copyParentDrop()
                .defaultRecipe()
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
                .setTab(buildingBlocksTab)
                .copyParentDrop()
                .defaultRecipe()
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
                .setTab(buildingBlocksTab)
                .copyParentDrop()
                .defaultRecipe()
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
                .copyParentDrop()
                .defaultRecipe()
                .build();
        this.addEntry(picketFence);

        wallArrowSign = SimpleEntrySet.builder(WoodType.class, "wall_arrow_sign",
                        BlockRegistry.OAK_WALL_ARROW_SIGN, () -> VanillaWoodTypes.OAK,
                        woodType -> new WallArrowSignBlock(woodType.toVanillaOrOak(), Utils.copyPropertySafe(woodType.planks)))
                .addTile(BlockEntityRegistry.ARROW_SIGN)
                .addModelTransform(transform -> transform.replaceItemType("oak"))
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .requiresChildren("planks")
                .setTab(functionalBlocksTab)
                .copyParentDrop()
                .noItem()
                .build();
        this.addEntry(wallArrowSign);

        hangingArrowSign = SimpleEntrySet.builder(WoodType.class, "hanging_arrow_sign",
                        BlockRegistry.OAK_HANGING_ARROW_SIGN, () -> VanillaWoodTypes.OAK,
                        woodType -> new HangingArrowSignBlock(woodType.toVanillaOrOak(), Utils.copyPropertySafe(woodType.planks)))
                .addTile(BlockEntityRegistry.ARROW_SIGN)
                .addModelTransform(transform -> transform.replaceItemType("oak"))
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .requiresChildren("planks")
                .setTab(functionalBlocksTab)
                .copyParentDrop()
                .noItem()
                .build();
        this.addEntry(hangingArrowSign);

        arrowSign = SimpleEntrySet.builder(WoodType.class, "arrow_sign",
                        BlockRegistry.OAK_ARROW_SIGN, () -> VanillaWoodTypes.OAK,
                        woodType -> new StandingArrowSignBlock(woodType.toVanillaOrOak(), Utils.copyPropertySafe(woodType.planks)))
                .addTile(BlockEntityRegistry.ARROW_SIGN)
                .addTextureM(modRes("entity/signs/arrow/oak_arrow_sign"), EveryCompat.res("block/mv/oak_arrow_sign_mask"),
                        PaletteStrategies.SIGN_LIKE)
                .addTexture(modRes("item/arrow_sign/oak_arrow_sign"), PaletteStrategies.SIGN_LIKE)
                .addModelTransform(transform -> transform.replaceItemType("oak"))
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addCustomItem((woodType, block, properties) -> new ArrowSignItem(properties.stacksTo(16),
                        block, this.wallArrowSign.blocks.get(woodType), this.hangingArrowSign.blocks.get(woodType)))
                .addRecipe(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "oak_arrow_sign_from_dye"))
                .requiresChildren("planks")
                .setTab(functionalBlocksTab)
                .copyParentDrop()
                .defaultRecipe()
                .build();
        this.addEntry(arrowSign);

        largeWallArrowSign = SimpleEntrySet.builder(WoodType.class, "wall_arrow_sign", "large",
                        BlockRegistry.LARGE_OAK_WALL_ARROW_SIGN, () -> VanillaWoodTypes.OAK,
                        woodType -> new LargeWallArrowSignBlock(woodType.toVanillaOrOak(), Utils.copyPropertySafe(woodType.planks)))
                .addTile(BlockEntityRegistry.ARROW_SIGN)
                .addModelTransform(transform -> transform.replaceItemType("oak"))
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .requiresChildren("planks")
                .setTab(functionalBlocksTab)
                .noDrops()
                .noItem()
                .build();
        this.addEntry(largeWallArrowSign);

        largeArrowSign = SimpleEntrySet.builder(WoodType.class, "arrow_sign", "large",
                        BlockRegistry.LARGE_OAK_ARROW_SIGN, () -> VanillaWoodTypes.OAK,
                        woodType -> new LargeStandingArrowSignBlock(woodType.toVanillaOrOak(), Utils.copyPropertySafe(woodType.planks)))
                .addTile(BlockEntityRegistry.ARROW_SIGN)
                .addCustomItem((woodType, block, properties) -> new LargeArrowSignItem(properties.stacksTo(16),
                        block, this.largeWallArrowSign.blocks.get(woodType)))
                .addTexture(modRes("entity/signs/large_arrow/large_oak_arrow_sign"), PaletteStrategies.SIGN_LIKE)
                .addTexture(modRes("item/large_arrow_sign/large_oak_arrow_sign"), PaletteStrategies.SIGN_LIKE)
                .addModelTransform(transform -> transform.replaceItemType("oak"))
                .addRecipe(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "large_oak_arrow_sign_from_dye"))
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .requiresChildren("planks")
                .setTab(functionalBlocksTab)
                .copyParentDrop()
                .defaultRecipe()
                .build();
        this.addEntry(largeArrowSign);
    }

    @Override
    public void addDynamicServerResources(Consumer<ResourceGenTask> executor) {
        super.addDynamicServerResources(executor);
        executor.accept((manager, sink) -> {
            for (Block block : largeWallArrowSign.blocks.values()) {
                StatePropertiesPredicate.Builder properties = StatePropertiesPredicate.Builder.properties()
                        .hasProperty(BlockStatePropertyRegistry.HALF, HalfBlockStates.BOTTOM)
                        .hasProperty(BlockStatePropertyRegistry.SIDE, SideBlockStates.LEFT);

                LootTable.Builder table = LootTable.lootTable().withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(block)
                                .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                        .include(DataComponentRegistry.ARROW_SIGN_DIRECTION.get())
                                        .include(DataComponentRegistry.DYE_COLOR.get())
                                        .include(DataComponentRegistry.GLOWING.get())
                                        .include(DataComponentRegistry.WAXED.get()))
                                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                        .setProperties(properties)))
                        .when(ExplosionCondition.survivesExplosion()));
                sink.addLootTable(block, table);
            }
        });
    }
}
