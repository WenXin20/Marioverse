package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.PlayerInteractTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class AdvancementDataGen extends AdvancementProvider {
    public AdvancementDataGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, existingFileHelper, List.of(new MyAdvancementGenerator()));
    }

    private static final class MyAdvancementGenerator implements AdvancementProvider.AdvancementGenerator {
        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {
            AdvancementHolder WRENCH = Advancement.Builder.advancement().parent(AdvancementSubProvider.createPlaceholder("minecraft:adventure/root"))
                    .display(new ItemStack(ItemRegistry.WRENCH.get()),
                            Component.translatable("advancements.marioverse.wrench.title"),
                            Component.translatable("advancements.marioverse.wrench.description"),
                            null, AdvancementType.TASK, true, true, false)
                    .addCriterion("wrench", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.WRENCH.get()))
                    .rewards(AdvancementRewards.Builder.experience(50))
                    .save(saver, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "obtain_wrench"), existingFileHelper);

            AdvancementHolder GOT_YOUR_NOSE = Advancement.Builder.advancement().parent(WRENCH)
                    .display(new ItemStack(Items.CARROT),
                            Component.translatable("advancements.marioverse.got_your_nose.title"),
                            Component.translatable("advancements.marioverse.got_your_nose.description"),
                            null, AdvancementType.TASK, true, true, false)
                    .addCriterion("got_your_nose", PlayerInteractTrigger.TriggerInstance
                            .itemUsedOnEntity(ItemPredicate.Builder.item().of(Items.SHEARS),
                                    Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(EntityRegistry.SNOW_POKEY.get())))))
                    .rewards(AdvancementRewards.Builder.experience(100))
                    .save(saver, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "got_your_nose"), existingFileHelper);

            AdvancementHolder CONFIGURE_PIPES = Advancement.Builder.advancement().parent(WRENCH)
                    .display(new ItemStack(BlockRegistry.WARP_PIPES.get(DyeColor.GREEN)),
                            Component.translatable("advancements.marioverse.configure_pipes.title"),
                            Component.translatable("advancements.marioverse.configure_pipes.description"),
                            null, AdvancementType.TASK, true, true, false)
                    .addCriterion("configure_pipes", ItemUsedOnLocationTrigger.TriggerInstance
                            .itemUsedOnBlock(LocationPredicate.Builder.location()
                                    .setBlock(BlockPredicate.Builder.block().of(TagRegistry.WARP_PIPE_BLOCKS)),
                                        ItemPredicate.Builder.item().of(ItemRegistry.WRENCH)))
                    .rewards(AdvancementRewards.Builder.experience(100))
                    .save(saver, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "configure_pipes"), existingFileHelper);

            AdvancementHolder BRUSH_A_PIPE = Advancement.Builder.advancement().parent(CONFIGURE_PIPES)
                    .display(new ItemStack(Items.BRUSH),
                            Component.translatable("advancements.marioverse.brush_a_pipe.title"),
                            Component.translatable("advancements.marioverse.brush_a_pipe.description"),
                            null, AdvancementType.GOAL, true, true, false)
                    .addCriterion("brush_a_pipe", ItemUsedOnLocationTrigger.TriggerInstance
                            .itemUsedOnBlock(LocationPredicate.Builder.location()
                                    .setBlock(BlockPredicate.Builder.block().of(TagRegistry.WARP_PIPE_BLOCKS)),
                                        ItemPredicate.Builder.item().of(Items.BRUSH)))
                    .rewards(AdvancementRewards.Builder.experience(125))
                    .save(saver, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "brush_a_pipe"), existingFileHelper);

            AdvancementHolder MAKE_A_PIPE_GLOW = Advancement.Builder.advancement().parent(CONFIGURE_PIPES)
                    .display(new ItemStack(Items.GLOW_INK_SAC),
                            Component.translatable("advancements.marioverse.make_a_pipe_glow.title"),
                            Component.translatable("advancements.marioverse.make_a_pipe_glow.description"),
                            null, AdvancementType.GOAL, true, true, false)
                    .addCriterion("make_a_pipe_glow", ItemUsedOnLocationTrigger.TriggerInstance
                            .itemUsedOnBlock(LocationPredicate.Builder.location()
                                    .setBlock(BlockPredicate.Builder.block().of(TagRegistry.WARP_PIPE_BLOCKS)),
                                        ItemPredicate.Builder.item().of(Items.GLOW_INK_SAC)))
                    .rewards(AdvancementRewards.Builder.experience(125))
                    .save(saver, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "make_a_pipe_glow"), existingFileHelper);

            AdvancementHolder DYE_A_PIPE = Advancement.Builder.advancement().parent(CONFIGURE_PIPES)
                    .display(new ItemStack(Items.RED_DYE),
                            Component.translatable("advancements.marioverse.dye_a_pipe.title"),
                            Component.translatable("advancements.marioverse.dye_a_pipe.description"),
                            null, AdvancementType.GOAL, true, true, false)
                    .addCriterion("dye_a_pipe", ItemUsedOnLocationTrigger.TriggerInstance
                            .itemUsedOnBlock(LocationPredicate.Builder.location()
                                    .setBlock(BlockPredicate.Builder.block().of(TagRegistry.DYEABLE_WARP_PIPE_BLOCKS)),
                                        ItemPredicate.Builder.item().of(Tags.Items.DYES)))
                    .rewards(AdvancementRewards.Builder.experience(125))
                    .save(saver, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "dye_a_pipe"), existingFileHelper);

            AdvancementHolder ALL_DYEABLE_WARP_PIPES = Advancement.Builder.advancement().parent(CONFIGURE_PIPES)
                    .display(new ItemStack(BlockRegistry.WARP_PIPES.get(DyeColor.RED)),
                            Component.translatable("advancements.marioverse.dyeable_warp_pipes.title"),
                            Component.translatable("advancements.marioverse.dyeable_warp_pipes.description"),
                            null, AdvancementType.CHALLENGE, true, true, false)
                    .addCriterion(BlockRegistry.WARP_PIPES.get(DyeColor.WHITE).getRegisteredName(), InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.WARP_PIPES.get(DyeColor.WHITE)))
                    .addCriterion(BlockRegistry.WARP_PIPES.get(DyeColor.LIGHT_GRAY).getRegisteredName(), InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.WARP_PIPES.get(DyeColor.LIGHT_GRAY)))
                    .addCriterion(BlockRegistry.WARP_PIPES.get(DyeColor.GRAY).getRegisteredName(), InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.WARP_PIPES.get(DyeColor.GRAY)))
                    .addCriterion(BlockRegistry.WARP_PIPES.get(DyeColor.BLACK).getRegisteredName(), InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.WARP_PIPES.get(DyeColor.BLACK)))
                    .addCriterion(BlockRegistry.WARP_PIPES.get(DyeColor.BROWN).getRegisteredName(), InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.WARP_PIPES.get(DyeColor.BROWN)))
                    .addCriterion(BlockRegistry.WARP_PIPES.get(DyeColor.RED).getRegisteredName(), InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.WARP_PIPES.get(DyeColor.RED)))
                    .addCriterion(BlockRegistry.WARP_PIPES.get(DyeColor.ORANGE).getRegisteredName(), InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.WARP_PIPES.get(DyeColor.ORANGE)))
                    .addCriterion(BlockRegistry.WARP_PIPES.get(DyeColor.YELLOW).getRegisteredName(), InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.WARP_PIPES.get(DyeColor.YELLOW)))
                    .addCriterion(BlockRegistry.WARP_PIPES.get(DyeColor.LIME).getRegisteredName(), InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.WARP_PIPES.get(DyeColor.LIME)))
                    .addCriterion(BlockRegistry.WARP_PIPES.get(DyeColor.GREEN).getRegisteredName(), InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.WARP_PIPES.get(DyeColor.GREEN)))
                    .addCriterion(BlockRegistry.WARP_PIPES.get(DyeColor.CYAN).getRegisteredName(), InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.WARP_PIPES.get(DyeColor.CYAN)))
                    .addCriterion(BlockRegistry.WARP_PIPES.get(DyeColor.LIGHT_BLUE).getRegisteredName(), InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.WARP_PIPES.get(DyeColor.LIGHT_BLUE)))
                    .addCriterion(BlockRegistry.WARP_PIPES.get(DyeColor.BLUE).getRegisteredName(), InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.WARP_PIPES.get(DyeColor.BLUE)))
                    .addCriterion(BlockRegistry.WARP_PIPES.get(DyeColor.PURPLE).getRegisteredName(), InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.WARP_PIPES.get(DyeColor.PURPLE)))
                    .addCriterion(BlockRegistry.WARP_PIPES.get(DyeColor.MAGENTA).getRegisteredName(), InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.WARP_PIPES.get(DyeColor.MAGENTA)))
                    .addCriterion(BlockRegistry.WARP_PIPES.get(DyeColor.PINK).getRegisteredName(), InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.WARP_PIPES.get(DyeColor.PINK)))
                    .rewards(AdvancementRewards.Builder.experience(150))
                    .save(saver, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "obtain_all_dyeable_warp_pipes"), existingFileHelper);

            AdvancementHolder TRAVEL_IN_CLEAR_PIPE = Advancement.Builder.advancement().parent(ALL_DYEABLE_WARP_PIPES)
                    .display(new ItemStack(BlockRegistry.CLEAR_WARP_PIPE.get()),
                            Component.translatable("advancements.marioverse.travel_in_clear_pipe.title"),
                            Component.translatable("advancements.marioverse.travel_in_clear_pipe.description"),
                            null, AdvancementType.CHALLENGE, true, true, false)
                    .addCriterion("travel_in_clear_pipe", EnterBlockTrigger.TriggerInstance.entersBlock(BlockRegistry.CLEAR_WARP_PIPE.get()))
                    .rewards(AdvancementRewards.Builder.experience(125))
                    .save(saver, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "travel_in_clear_pipe"), existingFileHelper);
        }
    }
}