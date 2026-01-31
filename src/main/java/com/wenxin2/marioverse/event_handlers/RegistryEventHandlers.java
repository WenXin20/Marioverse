package com.wenxin2.marioverse.event_handlers;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.accessories.PlasticBucketAccessory;
import com.wenxin2.marioverse.blocks.WarpDoorBlock;
import com.wenxin2.marioverse.blocks.WarpTrapDoorBlock;
import com.wenxin2.marioverse.blocks.behaviors.DispenserBehaviors;
import com.wenxin2.marioverse.commands.PowerUpCommand;
import com.wenxin2.marioverse.datagen.AdvancementDataGen;
import com.wenxin2.marioverse.datagen.DataMapGen;
import com.wenxin2.marioverse.datagen.BannerPatternTagsGen;
import com.wenxin2.marioverse.datagen.BiomeTagsGen;
import com.wenxin2.marioverse.datagen.BlockLootTableGen;
import com.wenxin2.marioverse.datagen.EnchantmentTagsGen;
import com.wenxin2.marioverse.datagen.RecipeGen;
import com.wenxin2.marioverse.datagen.BlockStateGen;
import com.wenxin2.marioverse.datagen.BlockTagsGen;
import com.wenxin2.marioverse.datagen.DamageTypeTagsGen;
import com.wenxin2.marioverse.datagen.EntityTypeTagsGen;
import com.wenxin2.marioverse.datagen.FluidTagsGen;
import com.wenxin2.marioverse.datagen.ItemModelGen;
import com.wenxin2.marioverse.datagen.ItemTagsGen;
import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.items.WarpDoorBlockItem;
import com.wenxin2.marioverse.items.WarpTrapDoorBlockItem;
import com.wenxin2.marioverse.items.fluids.PlasticBucketWrapper;
import com.wenxin2.marioverse.registries.BannerPatternRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.DamageTypeRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import io.wispforest.accessories.api.AccessoriesAPI;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = Marioverse.MOD_ID)
public class RegistryEventHandlers {
    public static final Map<Block, Block> WARP_DOORS = new IdentityHashMap<>();
    public static final Map<Block, Block> WARP_TRAPDOORS = new IdentityHashMap<>();
    private static final Set<String> DOOR_STATES = Set.of("facing", "half", "hinge", "open", "powered");
    private static final Set<String> TRAPDOOR_STATES = Set.of("facing", "half", "open", "powered");
    private static final String WATERLOGGED = "waterlogged";

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        PowerUpCommand.register(event.getDispatcher());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRegisterBlocks(RegisterEvent event) {
        event.register(Registries.BLOCK, helper -> {
            for (Block block : BuiltInRegistries.BLOCK) {
                if (!(block instanceof DoorBlock source))
                    continue;
                if (block instanceof WarpDoorBlock)
                    continue;

                StateDefinition<Block, BlockState> stateDefinition = source.getStateDefinition();

                Set<String> propertyNames = stateDefinition.getProperties().stream()
                        .map(Property::getName).collect(Collectors.toSet());
                boolean hasStates = propertyNames.equals(DOOR_STATES)
                        || propertyNames.equals(Stream.concat(DOOR_STATES.stream(),
                                Stream.of(WATERLOGGED)).collect(Collectors.toSet()));
                if (!hasStates)
                    continue;

                ResourceLocation sourceID = BuiltInRegistries.BLOCK.getKey(source);
                ResourceLocation warpID = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, sourceID.getNamespace() + "_warp_" + sourceID.getPath());
                WarpDoorBlock warpDoor = new WarpDoorBlock(source);

                helper.register(warpID, warpDoor);
                WARP_DOORS.put(source, warpDoor);
            }
        });

        event.register(Registries.BLOCK, helper -> {
            for (Block block : BuiltInRegistries.BLOCK) {
                if (!(block instanceof TrapDoorBlock source))
                    continue;
                if (block instanceof WarpTrapDoorBlock)
                    continue;
                if (block.defaultBlockState().is(CompatRegistry.COPYCATS_IRON_TRAPDOOR.get()))
                    continue;
                if (block.defaultBlockState().is(CompatRegistry.COPYCATS_TRAPDOOR.get()))
                    continue;

                StateDefinition<Block, BlockState> stateDefinition = source.getStateDefinition();

                Set<String> propertyNames = stateDefinition.getProperties().stream()
                        .map(Property::getName).collect(Collectors.toSet());
                boolean hasStates = propertyNames.equals(TRAPDOOR_STATES)
                        || propertyNames.equals(Stream.concat(TRAPDOOR_STATES.stream(),
                                Stream.of(WATERLOGGED)).collect(Collectors.toSet()));
                if (!hasStates)
                    continue;

                ResourceLocation sourceID = BuiltInRegistries.BLOCK.getKey(source);
                ResourceLocation warpID = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, sourceID.getNamespace() + "_warp_" + sourceID.getPath());
                WarpTrapDoorBlock warpTrapDoor = new WarpTrapDoorBlock(source);

                helper.register(warpID, warpTrapDoor);
                WARP_TRAPDOORS.put(source, warpTrapDoor);
            }
        });

        event.register(Registries.ITEM, helper -> {
            for (Map.Entry<Block, Block> block : WARP_DOORS.entrySet()) {
                Block source = block.getKey();
                Block warp = block.getValue();
                ResourceLocation warpID = BuiltInRegistries.BLOCK.getKey(warp);

                helper.register(warpID, new WarpDoorBlockItem(warp, source, new Item.Properties()));
            }
        });

        event.register(Registries.ITEM, helper -> {
            for (Map.Entry<Block, Block> block : WARP_TRAPDOORS.entrySet()) {
                Block source = block.getKey();
                Block warp = block.getValue();
                ResourceLocation warpID = BuiltInRegistries.BLOCK.getKey(warp);

                helper.register(warpID, new WarpTrapDoorBlockItem(warp, source, new Item.Properties()));
            }
        });
    }

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(DispenserBehaviors::register);
        event.enqueueWork(() -> AccessoriesAPI
                .registerAccessory(ItemRegistry.PLASTIC_BUCKET.get(), new PlasticBucketAccessory()));
    }

    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        BlockTagsGen blockTags = new BlockTagsGen(output, lookupProvider, existingFileHelper);

        generator.addProvider(event.includeClient(), new BlockStateGen(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new ItemModelGen(output, existingFileHelper));

        generator.addProvider(event.includeServer(), new AdvancementDataGen(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new DataMapGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(output, lookupProvider,
                new RegistrySetBuilder()
                .add(Registries.BANNER_PATTERN, BannerPatternRegistry::bootstrap)
                .add(Registries.DAMAGE_TYPE, DamageTypeRegistry::bootstrap), Set.of(Marioverse.MOD_ID)));

        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new BannerPatternTagsGen(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new BiomeTagsGen(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new BlockLootTableGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new DamageTypeTagsGen(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new EnchantmentTagsGen(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new EntityTypeTagsGen(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new FluidTagsGen(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ItemTagsGen(output, lookupProvider, blockTags.contentsGetter(), existingFileHelper));
        generator.addProvider(event.includeServer(), new RecipeGen(output, lookupProvider));
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.FluidHandler.ITEM,
                (stack, contex) -> new PlasticBucketWrapper(stack), ItemRegistry.PLASTIC_BUCKET);
        event.registerItem(Capabilities.FluidHandler.ITEM,
                (stack, contex) -> new PlasticBucketWrapper(stack), ItemRegistry.PLASTIC_WATER_BUCKET);
    }

    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {
        Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

        if (event.getType() == VillagerProfession.CARTOGRAPHER) {
            for (Map.Entry<DyeColor, DeferredBlock<Block>> entry : BlockRegistry.GOAL_POLES.entrySet()) {
                trades.get(3).add((entity, random) -> new MerchantOffer(
                        new ItemCost(Items.EMERALD, 1),
                        new ItemStack(entry.getValue(), 1),
                        15, 16, 0.05F));
            }

            for (Map.Entry<DyeColor, DeferredBlock<Block>> entry : BlockRegistry.CHECKPOINT_FLAGS.entrySet()) {
                trades.get(3).add((entity, random) -> new MerchantOffer(
                        new ItemCost(Items.EMERALD, 5),
                        new ItemStack(entry.getValue(), 1),
                        15, 16, 0.05F));
            }

            trades.get(5).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 5),
                    new ItemStack(BlockRegistry.CLASSIC_GOAL_POLE, 1),
                    10, 30, 0.05F));

            trades.get(5).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 10),
                    new ItemStack(BlockRegistry.CLASSIC_CHECKPOINT_FLAG, 1),
                    10, 30, 0.05F));
        }

        if (event.getType() == VillagerProfession.CLERIC) {
            trades.get(1).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 5),
                    new ItemStack(ItemRegistry.DASH_MUSHROOM.get(), 3),
                    5, 16, 0.05F));

            trades.get(3).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 8),
                    new ItemStack(ItemRegistry.MINI_MUSHROOM.get(), 1),
                    3, 16, 0.2F));

            trades.get(1).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 8),
                    new ItemStack(ItemRegistry.SUPER_MUSHROOM.get(), 1),
                    5, 16, 0.05F));

            trades.get(4).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 25),
                    new ItemStack(ItemRegistry.ONE_UP_MUSHROOM.get(), 1),
                    1, 30, 0.1F));
        }
    }

    @SubscribeEvent
    public static void addWandererTrades(WandererTradesEvent event) {
        List<VillagerTrades.ItemListing> genericTrades = event.getGenericTrades();
        List<VillagerTrades.ItemListing> rareTrades = event.getRareTrades();

        genericTrades.add((entity, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 8),
                new ItemStack(ItemRegistry.FIRE_FLOWER.get(), 1),
                8, 16, 0.1F));

        genericTrades.add((entity, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 8),
                new ItemStack(ItemRegistry.ICE_FLOWER.get(), 1),
                8, 16, 0.1F));

        genericTrades.add((entity, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 5),
                new ItemStack(ItemRegistry.DASH_MUSHROOM.get(), 3),
                15, 10, 0.1F));

        genericTrades.add((entity, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 8),
                new ItemStack(ItemRegistry.MINI_MUSHROOM.get(), 1),
                3, 16, 0.2F));

        genericTrades.add((entity, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 8),
                new ItemStack(ItemRegistry.SUPER_MUSHROOM.get(), 1),
                8, 16, 0.2F));

        genericTrades.add((entity, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                new ItemStack(BlockRegistry.COIN, 9),
                8, 10, 0.2F));

        genericTrades.add((entity, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                new ItemStack(BlockRegistry.STAR_COIN, 1),
                8, 10, 0.2F));

        rareTrades.add((entity, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 5),
                new ItemStack(BlockRegistry.CLASSIC_GOAL_POLE, 1),
                8, 10, 0.2F));

        rareTrades.add((entity, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 10),
                new ItemStack(BlockRegistry.CLASSIC_CHECKPOINT_FLAG, 1),
                8, 10, 0.2F));

        rareTrades.add((entity, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 25),
                new ItemStack(ItemRegistry.ONE_UP_MUSHROOM.get(), 1),
                1, 30, 0.1F));

        rareTrades.add((entity, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 32),
                new ItemStack(ItemRegistry.MEGA_MUSHROOM.get(), 1),
                1, 30, 0.2F));

        rareTrades.add((entity, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 32),
                new ItemStack(ItemRegistry.SUPER_STAR.get(), 1),
                1, 30, 0.2F));
    }
}
