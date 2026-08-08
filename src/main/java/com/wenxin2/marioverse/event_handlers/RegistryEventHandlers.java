package com.wenxin2.marioverse.event_handlers;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.curios.PlasticBucketCurio;
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
import com.wenxin2.marioverse.dynamic_pack.DynamicServerResources;
import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.items.WarpDoorBlockItem;
import com.wenxin2.marioverse.items.WarpTrapDoorBlockItem;
import com.wenxin2.marioverse.items.fluids.PlasticBucketWrapper;
import com.wenxin2.marioverse.registries.BannerPatternRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.DamageTypeRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.RegisterEvent;
import top.theillusivec4.curios.api.CuriosApi;

@EventBusSubscriber(modid = Marioverse.MOD_ID)
public class RegistryEventHandlers {
    public static final Map<Block, Block> WARP_DOORS = new IdentityHashMap<>();
    public static final Map<Block, Block> WARP_TRAPDOORS = new IdentityHashMap<>();
    private static final Set<String> DOOR_STATES = Set.of("facing", "half", "hinge", "open", "powered");
    private static final Set<String> TRAPDOOR_STATES = Set.of("facing", "half", "open", "powered");
    private static final String WATERLOGGED = "waterlogged";
    private static final Set<Item> REGISTERED = new HashSet<>();

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        PowerUpCommand.register(event.getDispatcher());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerBlocks(RegisterEvent event) {
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
        event.enqueueWork(() -> CuriosApi.registerCurio(ItemRegistry.PLASTIC_BUCKET.get(), new PlasticBucketCurio()));
    }

    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
        BlockTagsGen blockTags = new BlockTagsGen(output, provider, existingFileHelper);

        generator.addProvider(event.includeClient(), new BlockStateGen(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new ItemModelGen(output, existingFileHelper));

        generator.addProvider(event.includeServer(), new AdvancementDataGen(output, provider, existingFileHelper));
        generator.addProvider(event.includeServer(), new DataMapGen(output, provider));
        generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(output, provider,
                new RegistrySetBuilder()
                .add(Registries.BANNER_PATTERN, BannerPatternRegistry::bootstrap)
                .add(Registries.DAMAGE_TYPE, DamageTypeRegistry::bootstrap), Set.of(Marioverse.MOD_ID)));

        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new BannerPatternTagsGen(output, provider, existingFileHelper));
        generator.addProvider(event.includeServer(), new BiomeTagsGen(output, provider, existingFileHelper));
        generator.addProvider(event.includeServer(), new BlockLootTableGen(output, provider));
        generator.addProvider(event.includeServer(), new DamageTypeTagsGen(output, provider, existingFileHelper));
        generator.addProvider(event.includeServer(), new EnchantmentTagsGen(output, provider, existingFileHelper));
        generator.addProvider(event.includeServer(), new EntityTypeTagsGen(output, provider, existingFileHelper));
        generator.addProvider(event.includeServer(), new FluidTagsGen(output, provider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ItemTagsGen(output, provider, blockTags.contentsGetter(), existingFileHelper));
        generator.addProvider(event.includeServer(), new RecipeGen(output, provider));
    }

    public static void addPackFinder(final AddPackFindersEvent event) {
        if (event.getPackType() == PackType.SERVER_DATA) {
            ResourceLocation packLocation = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "dynamic_resources");
            Component packNameDisplay = Component.translatable("datapack.marioverse.dynamic_resources");
            PackLocationInfo packLocationInfo = new PackLocationInfo(Marioverse.MOD_ID + ":dynamic_resources",
                    packNameDisplay, PackSource.BUILT_IN, Optional.of(new KnownPack("marioverse", "mod/" + packLocation, "22")));

            event.addRepositorySource((consumer) -> consumer.accept(Pack.readMetaAndCreate(packLocationInfo,
                            new Pack.ResourcesSupplier() {
                                @Override
                                public PackResources openPrimary(PackLocationInfo info) {
                                    return new DynamicServerResources(info);
                                }

                                @Override
                                public PackResources openFull(PackLocationInfo info, Pack.Metadata metadata) {
                                    return new DynamicServerResources(info);
                                }
                            },
                            PackType.SERVER_DATA,
                            new PackSelectionConfig(true, Pack.Position.TOP, false)
                    )
            ));
        }
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.FluidHandler.ITEM,
                (stack, contex) -> new PlasticBucketWrapper(stack), ItemRegistry.PLASTIC_BUCKET);
        event.registerItem(Capabilities.FluidHandler.ITEM,
                (stack, contex) -> new PlasticBucketWrapper(stack), ItemRegistry.PLASTIC_WATER_BUCKET);
    }

    @SubscribeEvent
    public static void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        builder.addMix(Potions.AWKWARD, ItemRegistry.SPINY_CHEEP_CHEEP.get(), Potions.POISON);
        builder.addMix(Potions.AWKWARD, ItemRegistry.PORCUPUFFER.get(), Potions.STRONG_POISON);
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (!event.getName().equals(ResourceLocation.withDefaultNamespace("gameplay/fishing/fish")))
            return;

        try {
            LootPool pool = event.getTable().getPool("main");
            if (pool == null) return;

            Field entriesField = LootPool.class.getDeclaredField("entries");
            entriesField.setAccessible(true);

            @SuppressWarnings("unchecked")
            List<LootPoolEntryContainer> oldEntries = (List<LootPoolEntryContainer>) entriesField.get(pool);
            List<LootPoolEntryContainer> newEntries = new ArrayList<>(oldEntries);

            newEntries.add(LootItem.lootTableItem(ItemRegistry.CHEEP_CHEEP.get()).setWeight(20).build());
            newEntries.add(LootItem.lootTableItem(ItemRegistry.COLD_CHEEP_CHEEP.get()).setWeight(10).build());
            newEntries.add(LootItem.lootTableItem(ItemRegistry.DEEP_CHEEP.get()).setWeight(10).build());
            newEntries.add(LootItem.lootTableItem(ItemRegistry.EEP_CHEEP.get()).setWeight(10).build());
            newEntries.add(LootItem.lootTableItem(ItemRegistry.WARM_CHEEP_CHEEP.get()).setWeight(10).build());
            newEntries.add(LootItem.lootTableItem(ItemRegistry.SPINY_CHEEP_CHEEP.get()).setWeight(8).build());
            newEntries.add(LootItem.lootTableItem(ItemRegistry.PORCUPUFFER.get()).setWeight(2).build());
            entriesField.set(pool, newEntries);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Marioverse.LOGGER.error("Failed to modify fishing loot table", e);
        }
    }

    /** (ItemCost cost, ItemCost secondCost, ItemStack result, int maxUses, int xp, float priceMultiplier) **/

    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {
        Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

        if (event.getType() == VillagerProfession.ARMORER) {
            ItemStack crownStack = new ItemStack(ItemRegistry.CROWN.get());
            int[] colorCrown = { 0xFFFF647D, 0xFFA4FDF0, 0xFFFF647E };

            trades.get(3).add((entity, random) -> {
                crownStack.set(DataComponents.DYED_COLOR, new DyedItemColor(colorCrown[random.nextInt(colorCrown.length)], true));
                return new MerchantOffer(new ItemCost(Items.EMERALD, 5), crownStack,
                        5, 25, 0.2F);
            });
        }

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
                    5, 8, 0.05F));

            trades.get(3).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 8),
                    new ItemStack(ItemRegistry.MINI_MUSHROOM.get(), 1),
                    3, 25, 0.2F));

            trades.get(1).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 8),
                    new ItemStack(ItemRegistry.SUPER_MUSHROOM.get(), 1),
                    5, 16, 0.05F));

            trades.get(4).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 25),
                    new ItemStack(ItemRegistry.ONE_UP_MUSHROOM.get(), 1),
                    1, 30, 0.1F));
        }

        if (event.getType() == VillagerProfession.SHEPHERD) {
            int[] colors = { 0xFFF6343A, 0xFF43B237, 0xFFFFCD00, 0xFF8800FD };
            int[] colorShoes = { 0xFFA94535, 0xFF9C6042, 0xFFA94536, 0xFFA94537 };
            ItemStack hatStack = new ItemStack(ItemRegistry.HAT.get());
            ItemStack shirtStack = new ItemStack(ItemRegistry.SHIRT.get());
            ItemStack pantsStack = new ItemStack(ItemRegistry.PANTS.get());
            ItemStack shoesStack = new ItemStack(ItemRegistry.SHOES.get());

            int[] colorDress = { 0xFFFFC1D7, 0xFFFF992B, 0xFF89F4EB };
            ItemStack bodiceStack = new ItemStack(ItemRegistry.BODICE.get());
            ItemStack dressStack = new ItemStack(ItemRegistry.DRESS.get());
            ItemStack heelsStack = new ItemStack(ItemRegistry.HEELS.get());

            trades.get(3).add((entity, random) -> {
                hatStack.set(DataComponents.DYED_COLOR, new DyedItemColor(colors[random.nextInt(colors.length)], true));
                return new MerchantOffer(new ItemCost(Items.EMERALD, 5), hatStack,
                        5, 25, 0.2F);
            });

            trades.get(3).add((entity, random) -> {
                shirtStack.set(DataComponents.DYED_COLOR, new DyedItemColor(colors[random.nextInt(colors.length)], true));
                return new MerchantOffer(new ItemCost(Items.EMERALD, 8), shirtStack,
                        5, 25, 0.2F);
            });

            trades.get(3).add((entity, random) -> {
                pantsStack.set(DataComponents.DYED_COLOR, new DyedItemColor(colors[random.nextInt(colors.length)], true));
                return new MerchantOffer(new ItemCost(Items.EMERALD, 7), pantsStack,
                        5, 25, 0.2F);
            });

            trades.get(3).add((entity, random) -> {
                shoesStack.set(DataComponents.DYED_COLOR, new DyedItemColor(colorShoes[random.nextInt(colorShoes.length)], true));
                return new MerchantOffer(new ItemCost(Items.EMERALD, 5), shoesStack,
                        5, 4, 0.2F);
            });

            trades.get(3).add((entity, random) -> {
                bodiceStack.set(DataComponents.DYED_COLOR, new DyedItemColor(colorDress[random.nextInt(colorDress.length)], true));
                return new MerchantOffer(new ItemCost(Items.EMERALD, 8), bodiceStack,
                        5, 25, 0.2F);
            });

            trades.get(3).add((entity, random) -> {
                dressStack.set(DataComponents.DYED_COLOR, new DyedItemColor(colorDress[random.nextInt(colorDress.length)], true));
                return new MerchantOffer(new ItemCost(Items.EMERALD, 7), dressStack,
                        5, 25, 0.2F);
            });

            trades.get(3).add((entity, random) -> {
                heelsStack.set(DataComponents.DYED_COLOR, new DyedItemColor(colorShoes[random.nextInt(colorShoes.length)], true));
                return new MerchantOffer(new ItemCost(Items.EMERALD, 5), heelsStack,
                        5, 4, 0.2F);
            });
        }

        if (event.getType() == VillagerProfession.WEAPONSMITH) {
            trades.get(2).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 3),
                    new ItemStack(ItemRegistry.GREEN_KOOPA_SHELL.get(), 1),
                    9, 16, 0.1F));
            trades.get(2).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 9),
                    new ItemStack(ItemRegistry.GREEN_KOOPA_SHELL.get(), 3),
                    3, 16, 0.15F));
            trades.get(3).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 16),
                    new ItemStack(ItemRegistry.RED_KOOPA_SHELL.get(), 1),
                    9, 24, 0.2F));
            trades.get(3).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 16),
                    new ItemStack(ItemRegistry.RED_KOOPA_SHELL.get(), 3),
                    3, 24, 0.25F));
            trades.get(4).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 32),
                    new ItemStack(ItemRegistry.GOLD_KOOPA_SHELL.get(), 1),
                    3, 30, 0.25F));
        }
    }

    @SubscribeEvent
    public static void addWandererTrades(WandererTradesEvent event) {
        List<VillagerTrades.ItemListing> genericTrades = event.getGenericTrades();
        List<VillagerTrades.ItemListing> rareTrades = event.getRareTrades();

        genericTrades.add((entity, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                new ItemStack(BlockRegistry.RED_TRAMPOLINE_CAP, 1),
                16, 10, 0.2F));

        genericTrades.add((entity, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                new ItemStack(BlockRegistry.BLUE_TRAMPOLINE_CAP, 1),
                16, 10, 0.2F));

        genericTrades.add((entity, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                new ItemStack(BlockRegistry.COIN, 9),
                8, 10, 0.2F));

        genericTrades.add((entity, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                new ItemStack(BlockRegistry.STAR_COIN, 1),
                8, 10, 0.2F));

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
