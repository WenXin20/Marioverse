package com.wenxin2.marioverse.event_handlers;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.datagen.AdvancementDataGen;
import com.wenxin2.marioverse.datagen.CopperMapDataGen;
import com.wenxin2.marioverse.datagen.BannerPatternTagsGen;
import com.wenxin2.marioverse.datagen.BiomeTagsGen;
import com.wenxin2.marioverse.datagen.BlockLootTableGen;
import com.wenxin2.marioverse.datagen.BlockRecipeGen;
import com.wenxin2.marioverse.datagen.BlockStateGen;
import com.wenxin2.marioverse.datagen.BlockTagsGen;
import com.wenxin2.marioverse.datagen.DamageTypeTagsGen;
import com.wenxin2.marioverse.datagen.EntityTypeTagsGen;
import com.wenxin2.marioverse.datagen.ItemModelGen;
import com.wenxin2.marioverse.datagen.ItemTagsGen;
import com.wenxin2.marioverse.init.BannerPatternRegistry;
import com.wenxin2.marioverse.init.BlockRegistry;
import com.wenxin2.marioverse.init.DamageTypeRegistry;
import com.wenxin2.marioverse.init.ItemRegistry;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;
import net.neoforged.neoforge.registries.DeferredBlock;

@EventBusSubscriber(modid = Marioverse.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class RegistryEventHandlers {
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        BlockTagsGen blockTags = new BlockTagsGen(output, lookupProvider, existingFileHelper);

        generator.addProvider(event.includeClient(), new BlockStateGen(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new ItemModelGen(output, existingFileHelper));

        generator.addProvider(event.includeServer(), new AdvancementDataGen(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new CopperMapDataGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(output, lookupProvider,
                new RegistrySetBuilder()
                .add(Registries.BANNER_PATTERN, BannerPatternRegistry::bootstrap)
                .add(Registries.DAMAGE_TYPE, DamageTypeRegistry::bootstrap), Set.of(Marioverse.MOD_ID)));

        generator.addProvider(event.includeServer(), new BannerPatternTagsGen(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new BiomeTagsGen(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new BlockLootTableGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new BlockRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new EntityTypeTagsGen(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ItemTagsGen(output, lookupProvider, blockTags.contentsGetter(), existingFileHelper));
        generator.addProvider(event.includeServer(), new DamageTypeTagsGen(output, lookupProvider, existingFileHelper));
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

            trades.get(5).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 5),
                    new ItemStack(BlockRegistry.CLASSIC_GOAL_POLE, 1),
                    10, 30, 0.05F));
        }

        if (event.getType() == VillagerProfession.CLERIC) {
            trades.get(2).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 5),
                    new ItemStack(ItemRegistry.MUSHROOM.get(), 1),
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
                1, 16, 0.2F));

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
    }
}
