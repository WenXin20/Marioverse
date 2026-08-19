package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.data.BlockFamilyExtended;
import com.wenxin2.marioverse.registries.BlockFamilyRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import java.util.Map;
import java.util.Objects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ItemModelGen extends ItemModelProvider {
    public ItemModelGen(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Marioverse.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        this.genInvisibleQuestionBlockVariants();
        this.genStorageBrickVariants();

        this.basicBlockItem(BlockRegistry.MUSHROOT_SAPLING.asItem());
        this.basicBlockItem(BlockRegistry.MUSHROOT_FRAMED_WINDOW_PANE.asItem());

        this.basicItem(BlockRegistry.BLUE_TRAMPOLINE_CAP.asItem());
        this.basicItem(BlockRegistry.COIN.asItem());
        this.basicItem(BlockRegistry.DANGO_BLOSSOM.asItem());
        this.basicItem(BlockRegistry.IRON_SPIKE.asItem());
        this.basicItem(BlockRegistry.MUSHROOT_DOOR.asItem());
        this.basicItem(BlockRegistry.RED_TRAMPOLINE_CAP.asItem());
        this.basicItem(BlockRegistry.SPIKE_PANEL.asItem());
        this.largeItem(BlockRegistry.STAR_COIN.asItem());

        this.basicTwoLayerItem(ItemRegistry.CHRISTMAS_HAT.get());

        this.basicItem(ItemRegistry.BOO_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.BOWSER_BANNER_PATTERN.get());
        this.basicItem(ItemRegistry.BOWSER_POTTERY_SHERD.get());
        this.basicItem(ItemRegistry.CHEEP_CHEEP.get());
        this.basicItem(ItemRegistry.CHEEP_CHEEP_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.COLD_CHEEP_CHEEP.get());
        this.basicItem(ItemRegistry.COOKED_CHEEP_CHEEP.get());
        this.basicItem(ItemRegistry.COOKED_PORCUPUFFER.get());
        this.basicItem(ItemRegistry.COOKED_SPINY_CHEEP_CHEEP.get());
        this.basicItem(ItemRegistry.DASH_MUSHROOM.get());
        this.basicItem(ItemRegistry.DASH_MUSHROOM_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.DEEP_CHEEP.get());
        this.basicItem(ItemRegistry.DEEP_CHEEP_BUCKET.get());
        this.basicItem(ItemRegistry.DEEP_CHEEP_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.DRY_BONES_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.EEP_CHEEP.get());
        this.basicItem(ItemRegistry.EEP_CHEEP_BUCKET.get());
        this.basicItem(ItemRegistry.EEP_CHEEP_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.FIRE_FLOWER.get());
        this.basicItem(ItemRegistry.FIRE_FLOWER_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.FIRE_GOOMBA_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.GOLDEN_KOOPA_SHOES.get());
        this.basicItem(ItemRegistry.GOLD_KOOPA_SHELL.get());
        this.basicItem(ItemRegistry.GOLD_KOOPA_TROOPA_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.GREEN_KOOPA_SHELL.get());
        this.basicItem(ItemRegistry.GREEN_KOOPA_SHOES.get());
        this.basicItem(ItemRegistry.GREEN_KOOPA_TROOPA_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.HEFTY_GOOMBA_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.ICE_FLOWER.get());
        this.basicItem(ItemRegistry.ICE_FLOWER_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.LARGE_SNOWBALL.get());
        this.basicItem(ItemRegistry.LUIGI_ARMOR_TRIM_SMITHING_TEMPLATE.get());
        this.basicItem(ItemRegistry.MARIO_ARMOR_TRIM_SMITHING_TEMPLATE.get());;
        this.basicItem(ItemRegistry.MEGA_GOOMBA_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.MINI_GOOMBA_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.MINI_MUSHROOM.get());
        this.basicItem(ItemRegistry.MINI_MUSHROOM_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.MUSHROOT_BOAT.get());
        this.basicItem(ItemRegistry.MUSHROOT_CHEST_BOAT.get());
        this.basicItem(ItemRegistry.MUSHROOT_HANGING_SIGN.get());
        this.basicItem(ItemRegistry.MUSHROOT_SIGN.get());
        this.basicItem(ItemRegistry.ONE_UP_MUSHROOM.get());
        this.basicItem(ItemRegistry.ONE_UP_MUSHROOM_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.PIRANHA_PLANT_POD.get());
        this.basicItem(ItemRegistry.PIRANHA_PLANT_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.PLASTIC_POWDER_SNOW_BUCKET.get());
        this.basicItem(ItemRegistry.PLASTIC_QUICKSAND_BUCKET.get());
        this.basicItem(ItemRegistry.PLASTIC_RED_QUICKSAND_BUCKET.get());
        this.basicItem(ItemRegistry.PLUMBER_BANNER_PATTERN.get());
        this.basicItem(ItemRegistry.PLUMBER_POTTERY_SHERD.get());
        this.basicItem(ItemRegistry.POKEY_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.PORCUPUFFER.get());
        this.basicItem(ItemRegistry.PRINCESS_ARMOR_TRIM_SMITHING_TEMPLATE.get());
        this.basicItem(ItemRegistry.QUICKSAND_BUCKET.get());
        this.basicItem(ItemRegistry.RED_KOOPA_SHELL.get());
        this.basicItem(ItemRegistry.RED_KOOPA_SHOES.get());
        this.basicItem(ItemRegistry.RED_KOOPA_TROOPA_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.RED_QUICKSAND_BUCKET.get());
        this.basicItem(ItemRegistry.SNOW_POKEY_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.SPINY_CHEEP_CHEEP.get());
        this.basicItem(ItemRegistry.SPINY_CHEEP_CHEEP_BUCKET.get());
        this.basicItem(ItemRegistry.SPINY_CHEEP_CHEEP_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.SPLUNKIN_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.SUPER_MUSHROOM.get());
        this.basicItem(ItemRegistry.SUPER_MUSHROOM_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.SUPER_STAR.get());
        this.basicItem(ItemRegistry.SUPER_STAR_SPAWN_EGG.get());
        this.basicItem(ItemRegistry.WALUIGI_ARMOR_TRIM_SMITHING_TEMPLATE.get());
        this.basicItem(ItemRegistry.WARIO_ARMOR_TRIM_SMITHING_TEMPLATE.get());
        this.basicItem(ItemRegistry.WARM_CHEEP_CHEEP.get());
        this.basicItem(ItemRegistry.WHITE_KOOPA_SHOES.get());
        this.basicItem(ItemRegistry.WHITE_KOOPA_SHOES.get());

        this.coralTowerItem(BlockRegistry.BRAIN_CORAL_TOWER.asItem());
        this.coralTowerItem(BlockRegistry.BUBBLE_CORAL_TOWER.asItem());
        this.coralTowerItem(BlockRegistry.DEAD_BRAIN_CORAL_TOWER.asItem());
        this.coralTowerItem(BlockRegistry.DEAD_BUBBLE_CORAL_TOWER.asItem());
        this.coralTowerItem(BlockRegistry.DEAD_FIRE_CORAL_TOWER.asItem());
        this.coralTowerItem(BlockRegistry.DEAD_HORN_CORAL_TOWER.asItem());
        this.coralTowerItem(BlockRegistry.DEAD_TUBE_CORAL_TOWER.asItem());
        this.coralTowerItem(BlockRegistry.FIRE_CORAL_TOWER.asItem());
        this.coralTowerItem(BlockRegistry.HORN_CORAL_TOWER.asItem());
        this.coralTowerItem(BlockRegistry.TUBE_CORAL_TOWER.asItem());

        this.handheldItem(ItemRegistry.CREATIVE_WRENCH.get());
        this.handheldItem(ItemRegistry.WRENCH.get());
        this.handheldItem(ItemRegistry.WARP_DISRUPTOR.get());

        this.largeItem(ItemRegistry.MEGA_MUSHROOM.get());
        this.largeItem(ItemRegistry.MEGA_MUSHROOM_SPAWN_EGG.get());

        this.plasticFluidBucketItem(ItemRegistry.PLASTIC_BUCKET.get(), Fluids.EMPTY, false, false);
        this.plasticFluidBucketItem(ItemRegistry.PLASTIC_WATER_BUCKET.get(), Fluids.WATER, true, false);

        for (Map.Entry<DyeColor, DeferredBlock<Block>> entry : BlockRegistry.CHECKPOINT_FLAGS.entrySet())
            this.handheldItem(entry.getValue().asItem()).override()
                    .model(new ModelFile.UncheckedModelFile(modLoc("item/american_checkpoint_flag")))
                    .predicate(modLoc("custom_name"), 1.0F).end().override()
                    .model(new ModelFile.UncheckedModelFile(modLoc("item/wonder_checkpoint_flag")))
                    .predicate(modLoc("custom_name"), 2.0F).end();
        this.getBuilder("american_checkpoint_flag").parent(new ModelFile.UncheckedModelFile("item/handheld"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "item/american_checkpoint_flag"));
        this.getBuilder("wonder_checkpoint_flag").parent(new ModelFile.UncheckedModelFile("item/handheld"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "item/wonder_checkpoint_flag"));

        for (Map.Entry<DyeColor, DeferredBlock<Block>> entry : BlockRegistry.GOAL_POLES.entrySet())
            this.handheldItem(entry.getValue().asItem()).override()
                    .model(new ModelFile.UncheckedModelFile(modLoc("item/american_goal_pole")))
                    .predicate(modLoc("custom_name"), 1.0F).end().override()
                    .model(new ModelFile.UncheckedModelFile(modLoc("item/wonder_goal_pole")))
                    .predicate(modLoc("custom_name"), 2.0F).end();
        this.getBuilder("american_goal_pole").parent(new ModelFile.UncheckedModelFile("item/handheld"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "item/american_goal_pole"));
        this.getBuilder("wonder_goal_pole").parent(new ModelFile.UncheckedModelFile("item/handheld"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "item/wonder_goal_pole"));

        this.basicItem(ItemRegistry.CHEEP_CHEEP_BUCKET.asItem()).override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/cold_cheep_cheep_bucket")))
                .predicate(modLoc("variant"), 1.0F).end().override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/warm_cheep_cheep_bucket")))
                .predicate(modLoc("variant"), 2.0F).end();
        this.getBuilder("cold_cheep_cheep_bucket")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/cold_cheep_cheep_bucket"));
        this.getBuilder("warm_cheep_cheep_bucket")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/warm_cheep_cheep_bucket"));

        this.basicItem(ItemRegistry.CHEEP_CHEEP_SPAWN_EGG.asItem()).override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/cold_cheep_cheep_spawn_egg")))
                .predicate(modLoc("variant"), 1.0F).end().override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/warm_cheep_cheep_spawn_egg")))
                .predicate(modLoc("variant"), 2.0F).end();
        this.getBuilder("cold_cheep_cheep_spawn_egg")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/cold_cheep_cheep_spawn_egg"));
        this.getBuilder("warm_cheep_cheep_spawn_egg")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/warm_cheep_cheep_spawn_egg"));

        this.handheldItem(BlockRegistry.CLASSIC_CHECKPOINT_FLAG.asItem()).override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/american_checkpoint_flag")))
                .predicate(modLoc("custom_name"), 1.0F).end().override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/wonder_checkpoint_flag")))
                .predicate(modLoc("custom_name"), 2.0F).end();

        this.handheldItem(BlockRegistry.CLASSIC_GOAL_POLE.asItem()).override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/american_classic_goal_pole")))
                .predicate(modLoc("custom_name"), 1.0F).end().override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/wonder_classic_goal_pole")))
                .predicate(modLoc("custom_name"), 2.0F).end();
        this.getBuilder("american_classic_goal_pole").parent(new ModelFile.UncheckedModelFile("item/handheld"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "item/american_classic_goal_pole"));
        this.getBuilder("wonder_classic_goal_pole").parent(new ModelFile.UncheckedModelFile("item/handheld"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "item/wonder_classic_goal_pole"));

        this.basicItem(ItemRegistry.GOOMBA_SPAWN_EGG.get()).override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/goombella_spawn_egg")))
                .predicate(modLoc("custom_name"), 1.0F).end();
        this.getBuilder("goombella_spawn_egg").parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "item/goombella_spawn_egg"));

        this.basicItem(ItemRegistry.PIRANHA_PLANT_POD.asItem()).override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/chomper_pod")))
                .predicate(modLoc("variant"), 1.0F).end().override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/cave_piranha_plant_pod")))
                .predicate(modLoc("variant"), 2.0F).end().override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/deep_cave_piranha_plant_pod")))
                .predicate(modLoc("variant"), 3.0F).end().override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/tropical_piranha_plant_pod")))
                .predicate(modLoc("variant"), 4.0F).end();
        this.getBuilder("chomper_pod")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/chomper_pod"));
        this.getBuilder("cave_piranha_plant_pod")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/cave_piranha_plant_pod"));
        this.getBuilder("deep_cave_piranha_plant_pod")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/deep_cave_piranha_plant_pod"));
        this.getBuilder("tropical_piranha_plant_pod")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/tropical_piranha_plant_pod"));

        this.basicItem(ItemRegistry.PIRANHA_PLANT_SPAWN_EGG.asItem()).override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/chomper_spawn_egg")))
                .predicate(modLoc("variant"), 1.0F).end().override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/cave_piranha_plant_spawn_egg")))
                .predicate(modLoc("variant"), 2.0F).end().override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/deep_cave_piranha_plant_spawn_egg")))
                .predicate(modLoc("variant"), 3.0F).end().override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/tropical_piranha_plant_spawn_egg")))
                .predicate(modLoc("variant"), 4.0F).end();
        this.getBuilder("chomper_spawn_egg")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/chomper_spawn_egg"));
        this.getBuilder("cave_piranha_plant_spawn_egg")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/cave_piranha_plant_spawn_egg"));
        this.getBuilder("deep_cave_piranha_plant_spawn_egg")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/deep_cave_piranha_plant_spawn_egg"));
        this.getBuilder("tropical_piranha_plant_spawn_egg")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/tropical_piranha_plant_spawn_egg"));

        this.basicItem(ItemRegistry.PORCUPUFFER_SPAWN_EGG.get()).override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/mrs_puff_spawn_egg")))
                .predicate(modLoc("custom_name"), 1.0F).end().override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/qwilfish_spawn_egg")))
                .predicate(modLoc("custom_name"), 2.0F).end();
        this.getBuilder("mrs_puff_spawn_egg").parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "item/mrs_puff_spawn_egg"));
        this.getBuilder("qwilfish_spawn_egg").parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "item/qwilfish_spawn_egg"));


        this.basicTwoLayerItem(ItemRegistry.HAT.asItem()).override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/fire_hat")))
                .predicate(modLoc("power_up_type"), 1.0F).end().override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/ice_hat")))
                .predicate(modLoc("power_up_type"), 2.0F).end();
        this.getBuilder("fire_hat")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/fire_hat"))
                .texture("layer1", modLoc("item/fire_hat_overlay"));
        this.getBuilder("ice_hat")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/ice_hat"))
                .texture("layer1", modLoc("item/ice_hat_overlay"));

        this.basicTwoLayerItem(ItemRegistry.SHIRT.asItem()).override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/fire_shirt")))
                .predicate(modLoc("power_up_type"), 1.0F).end().override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/ice_shirt")))
                .predicate(modLoc("power_up_type"), 2.0F).end();
        this.getBuilder("fire_shirt")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/fire_shirt"))
                .texture("layer1", modLoc("item/fire_shirt_overlay"));
        this.getBuilder("ice_shirt")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/ice_shirt"))
                .texture("layer1", modLoc("item/ice_shirt_overlay"));

        this.basicTwoLayerItem(ItemRegistry.PANTS.asItem()).override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/fire_pants")))
                .predicate(modLoc("power_up_type"), 1.0F).end().override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/ice_pants")))
                .predicate(modLoc("power_up_type"), 2.0F).end();
        this.getBuilder("fire_pants")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/fire_pants"))
                .texture("layer1", modLoc("item/fire_pants_overlay"));
        this.getBuilder("ice_pants")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/ice_pants"))
                .texture("layer1", modLoc("item/ice_pants_overlay"));

        this.basicTwoLayerItem(ItemRegistry.SHOES.asItem()).override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/fire_shoes")))
                .predicate(modLoc("power_up_type"), 1.0F).end().override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/ice_shoes")))
                .predicate(modLoc("power_up_type"), 2.0F).end();
        this.getBuilder("fire_shoes")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/fire_shoes"))
                .texture("layer1", modLoc("item/fire_shoes_overlay"));
        this.getBuilder("ice_shoes")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/ice_shoes"))
                .texture("layer1", modLoc("item/ice_shoes_overlay"));


        this.basicTwoLayerItem(ItemRegistry.CROWN.asItem()).override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/fire_crown")))
                .predicate(modLoc("power_up_type"), 1.0F).end().override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/ice_crown")))
                .predicate(modLoc("power_up_type"), 2.0F).end();
        this.getBuilder("fire_crown")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/fire_crown"))
                .texture("layer1", modLoc("item/fire_crown_overlay"));
        this.getBuilder("ice_crown")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/ice_crown"))
                .texture("layer1", modLoc("item/ice_crown_overlay"));

        this.basicTwoLayerItem(ItemRegistry.BODICE.asItem()).override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/fire_bodice")))
                .predicate(modLoc("power_up_type"), 1.0F).end().override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/ice_bodice")))
                .predicate(modLoc("power_up_type"), 2.0F).end();
        this.getBuilder("fire_bodice")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/fire_bodice"))
                .texture("layer1", modLoc("item/fire_bodice_overlay"));
        this.getBuilder("ice_bodice")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/ice_bodice"))
                .texture("layer1", modLoc("item/ice_bodice_overlay"));

        this.basicTwoLayerItem(ItemRegistry.DRESS.asItem()).override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/fire_dress")))
                .predicate(modLoc("power_up_type"), 1.0F).end().override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/ice_dress")))
                .predicate(modLoc("power_up_type"), 2.0F).end();
        this.getBuilder("fire_dress")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/fire_dress"))
                .texture("layer1", modLoc("item/fire_dress_overlay"));
        this.getBuilder("ice_dress")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/ice_dress"))
                .texture("layer1", modLoc("item/ice_dress_overlay"));

        this.basicTwoLayerItem(ItemRegistry.HEELS.asItem()).override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/fire_heels")))
                .predicate(modLoc("power_up_type"), 1.0F).end().override()
                .model(new ModelFile.UncheckedModelFile(modLoc("item/ice_heels")))
                .predicate(modLoc("power_up_type"), 2.0F).end();
        this.getBuilder("fire_heels")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/fire_heels"))
                .texture("layer1", modLoc("item/fire_heels_overlay"));
        this.getBuilder("ice_heels")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/ice_heels"))
                .texture("layer1", modLoc("item/ice_heels_overlay"));
    }

    public void coralTowerItem(Item item) {
        ResourceLocation location = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item));
        this.getBuilder(location.toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "block/" + location.getPath() + "_top"));
    }

    public ItemModelBuilder basicTwoLayerItem(Item item) {
        ResourceLocation location = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item));
        return this.getBuilder(location.toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "item/" + location.getPath()))
                .texture("layer1", ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "item/" + location.getPath() + "_overlay"));
    }

    public ItemModelBuilder basicBlockItem(Item item) {
        return basicBlockItem(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)));
    }

    public ItemModelBuilder basicBlockItem(ResourceLocation item) {
        String removeName = item.getPath().replace("_pane", "");
        return this.getBuilder(item.toString()).parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(item.getNamespace(), "block/" + removeName));
    }

    public void largeItem(Item item) {
        this.largeItem(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)));
    }

    public void plasticFluidBucketItem(Item item, Fluid fluid, boolean applyTint, boolean coverIsMask) {
        this.plasticFluidBucketItem(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)), fluid, applyTint, coverIsMask);
    }

    public void largeItem(ResourceLocation item) {
        this.getBuilder(item.toString())
                .parent(new ModelFile.UncheckedModelFile("marioverse:item/template_large_dropped_item"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(item.getNamespace(), "item/" + item.getPath()));
    }

    public void plasticFluidBucketItem(ResourceLocation item, Fluid fluid, boolean applyTint, boolean coverIsMask) {
        ItemModelBuilder builder = this.getBuilder(item.getPath())
                .parent(new ModelFile.UncheckedModelFile("neoforge:item/default"))
                .texture("base", ResourceLocation.fromNamespaceAndPath(item.getNamespace(), "item/" + item.getPath()))
                .texture("fluid", ResourceLocation.fromNamespaceAndPath(item.getNamespace(), "item/mask/plastic_bucket_fluid"));

        if (coverIsMask)
            builder.texture("cover", ResourceLocation.fromNamespaceAndPath(item.getNamespace(), "item/mask/plastic_bucket_fluid_cover"));

        builder.customLoader((parent, existingFileHelper) ->
                DynamicFluidContainerModelBuilder.begin(parent, existingFileHelper)
                        .applyTint(applyTint)
                        .coverIsMask(coverIsMask)
                        .fluid(fluid));
    }

    public void storageBrickModel(String modelName, ResourceLocation mainTexture, ResourceLocation overlayTexture) {
        getBuilder(modelName).parent(getExistingFile(modLoc("block/template_storage_bricks")))
                .texture("all", mainTexture).texture("overlay", overlayTexture);
    }

    public void cubeBottomTopModel(String modelName, ResourceLocation sideTexture, ResourceLocation topTexture) {
        getBuilder(modelName).parent(getExistingFile(mcLoc("minecraft:block/cube_bottom_top"))).texture("bottom", topTexture)
                .texture("side", sideTexture).texture("top", topTexture);
    }

    public void invisibleQuestionBlockModel(String modelName, ResourceLocation mainTexture) {
        getBuilder(modelName).parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", mainTexture);
    }

    public void spawnEggItem(Item item, String parentModelPath) {
        getBuilder(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)).toString()).parent(new ModelFile
                .UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, parentModelPath)));
    }

    private void genInvisibleQuestionBlockVariants() {
        BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> {
            blockFamily.getVariants().forEach((variant, block) -> {
                BlockFamilyExtended.Variant questionBlock = BlockFamilyExtended.Variant.INVISIBLE_QUESTION_BLOCK;

                if (variant == questionBlock) {
                    String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
                    ResourceLocation mainTexture;

                    if (blockName.startsWith("invisible_waxed_")) {
                        String removeWaxedName = blockName.replace("waxed_", "");
                        mainTexture = modLoc("block/" + removeWaxedName);

                        this.invisibleQuestionBlockModel(blockName, mainTexture);
                    } else {
                        mainTexture = modLoc("block/" + blockName);

                        this.invisibleQuestionBlockModel(blockName, mainTexture);
                    }
                }
            });
        });
    }

    private void genStorageBrickVariants() {
        BlockFamilyRegistry.getAllExtendedFamilies().forEach(blockFamily -> {
            blockFamily.getVariants().forEach((variant, block) -> {
                BlockFamilyExtended.Variant storageBrick = BlockFamilyExtended.Variant.STORAGE_BRICKS;

                if (variant == storageBrick) {
                    String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
                    String removeStorageName = blockName.replace("storage_", "");
                    String questionBlockName = removeStorageName
                            .replace("block", "question_block")
                            .replace("bricks", "question_bricks")
                            .replace("cut_copper", "copper_question_block")
                            .replace("dark_prismarine", "dark_prismarine_question_block")
                            .replace("tiles", "question_tiles");
                    ResourceLocation mainTexture;
                    ResourceLocation sideTexture;
                    ResourceLocation topTexture;
                    ResourceLocation overlayTexture;

                    if (block == BlockFamilyRegistry.BRICKS.get(storageBrick)
                            || block == BlockFamilyRegistry.CUT_COPPER.get(storageBrick)
                            || block == BlockFamilyRegistry.DARK_PRISMARINE.get(storageBrick)
                            || block == BlockFamilyRegistry.DEEPSLATE_BRICKS.get(storageBrick)
                            || block == BlockFamilyRegistry.DEEPSLATE_TILES.get(storageBrick)
                            || block == BlockFamilyRegistry.END_STONE_BRICKS.get(storageBrick)
                            || block == BlockFamilyRegistry.EXPOSED_CUT_COPPER.get(storageBrick)
                            || block == BlockFamilyRegistry.MOSSY_STONE_BRICKS.get(storageBrick)
                            || block == BlockFamilyRegistry.MUD_BRICKS.get(storageBrick)
                            || block == BlockFamilyRegistry.NETHER_BRICKS.get(storageBrick)
                            || block == BlockFamilyRegistry.OXIDIZED_CUT_COPPER.get(storageBrick)
                            || block == BlockFamilyRegistry.PRISMARINE_BRICKS.get(storageBrick)
                            || block == BlockFamilyRegistry.PURPUR_BLOCK.get(storageBrick)
                            || block == BlockFamilyRegistry.QUARTZ_BRICKS.get(storageBrick)
                            || block == BlockFamilyRegistry.RED_NETHER_BRICKS.get(storageBrick)
                            || block == BlockFamilyRegistry.STONE_BRICKS.get(storageBrick)
                            || block == BlockFamilyRegistry.TUFF_BRICKS.get(storageBrick)
                            || block == BlockFamilyRegistry.WEATHERED_CUT_COPPER.get(storageBrick)) {
                        mainTexture = mcLoc("minecraft:block/" + removeStorageName);
                        overlayTexture = modLoc("block/" + questionBlockName + "_overlay");

                        this.storageBrickModel(blockName, mainTexture, overlayTexture);
                    } else if (removeStorageName.startsWith("waxed_")) {
                        String unWaxedName = blockName.replace("waxed_", "");
                        removeStorageName = unWaxedName.replace("storage_", "");
                        questionBlockName = removeStorageName
                                .replace("cut_copper", "copper_question_block");
                        mainTexture = mcLoc("minecraft:block/" + removeStorageName);
                        overlayTexture = modLoc("block/" + questionBlockName + "_overlay");

                        this.storageBrickModel(blockName, mainTexture, overlayTexture);
                    } else if (questionBlockName.startsWith("blackstone_")) {
                        String crackedBlockName = removeStorageName.replace("blackstone_", "polished_blackstone_");
                        mainTexture = mcLoc("minecraft:block/" + crackedBlockName);
                        overlayTexture = modLoc("block/" + questionBlockName + "_overlay");

                        this.storageBrickModel(blockName, mainTexture, overlayTexture);
                    } else {
                        sideTexture = modLoc("block/" + blockName);
                        topTexture = modLoc("block/" + removeStorageName);

                        this.cubeBottomTopModel(blockName, sideTexture, topTexture);
                    }
                }
            });
        });
    }
}
