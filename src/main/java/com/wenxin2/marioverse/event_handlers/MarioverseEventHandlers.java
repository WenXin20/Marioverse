package com.wenxin2.marioverse.event_handlers;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.WarpDoorBlock;
import com.wenxin2.marioverse.blocks.client.WarpPipeScreen;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.datagen.MarioverseBlockStateProvider;
import com.wenxin2.marioverse.entities.FireGoombaEntity;
import com.wenxin2.marioverse.entities.GoombaEntity;
import com.wenxin2.marioverse.entities.ai.goals.ShootBouncingFireballGoal;
import com.wenxin2.marioverse.init.BlockRegistry;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.KeybindRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import com.wenxin2.marioverse.items.BaseCostumeItem;
import com.wenxin2.marioverse.items.WarpDoorItem;
import com.wenxin2.marioverse.network.PacketHandler;
import com.wenxin2.marioverse.network.server_bound.data.FireballShootPayload;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.data.SlotTypeLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import virtuoel.pehkui.api.ScaleTypes;

@EventBusSubscriber(modid = Marioverse.MOD_ID)
public class MarioverseEventHandlers {
    public static void onRegister(RegisterEvent event) {
        event.register(Registries.BLOCK, helper -> {
            BuiltInRegistries.BLOCK.stream()
                    .filter(block -> block instanceof DoorBlock && !(block instanceof WarpDoorBlock))
                    .forEach(baseDoor -> registerWarpDoor((DoorBlock) baseDoor, helper));
        });
        event.register(Registries.ITEM, helper -> {
            BuiltInRegistries.ITEM.stream()
                    .filter(item -> (item instanceof DoubleHighBlockItem blockItem && blockItem.getBlock() instanceof DoorBlock)
                            && !(item instanceof WarpDoorItem))
                    .forEach(door -> registerWarpDoorItem((DoubleHighBlockItem) door, helper));
        });
    }

    private static void registerWarpDoor(DoorBlock baseDoor, RegisterEvent.RegisterHelper<Block> helper) {
        ResourceLocation location = BuiltInRegistries.BLOCK.getKey(baseDoor);
        String path = location.getPath();

        String modifiedPath;
        if (path.endsWith("_door")) {
            int splitIndex = path.lastIndexOf("_door");
            modifiedPath = path.substring(0, splitIndex) + "_warp" + path.substring(splitIndex);
        } else {
            // Fallback if the path does not end with "_door"
            modifiedPath = "warp_" + path;
        }

        String name = location.getNamespace().equals("minecraft")
                ? modifiedPath
                : location.getNamespace() + "_" + modifiedPath;

        BlockSetType blockSetType = baseDoor.type();
        Block warpDoorBlock = new WarpDoorBlock(blockSetType, BlockBehaviour.Properties.ofFullCopy(baseDoor), baseDoor);

        helper.register(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, name), warpDoorBlock);
    }

    private static void registerWarpDoorItem(DoubleHighBlockItem baseDoorItem, RegisterEvent.RegisterHelper<Item> helper) {
        ResourceLocation location = BuiltInRegistries.ITEM.getKey(baseDoorItem);
        String path = location.getPath();

        String modifiedPath;
        if (path.endsWith("_door")) {
            int splitIndex = path.lastIndexOf("_door");
            modifiedPath = path.substring(0, splitIndex) + "_warp" + path.substring(splitIndex);
        } else {
            // Fallback if the path does not end with "_door"
            modifiedPath = "warp_" + path;
        }

        String name = location.getNamespace().equals("minecraft")
                ? modifiedPath
                : location.getNamespace() + "_" + modifiedPath;

        Item warpDoorItem = new WarpDoorItem(BlockRegistry.WARP_DOORS.get(name).get(), new Item.Properties(), baseDoorItem);
        helper.register(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, name), warpDoorItem);
    }

    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(
                event.includeClient(),
                new MarioverseBlockStateProvider(output, existingFileHelper)
        );
    }

    @SubscribeEvent
    public static void onJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        CompoundTag tag = entity.getPersistentData();

        if (!(entity instanceof LivingEntity)) return;

        if (!tag.contains("marioverse:prevent_warp"))
            tag.putBoolean("marioverse:prevent_warp", false);

        if (!tag.contains("marioverse:has_fire_flower")
                && (entity.getType().is(TagRegistry.CAN_CONSUME_FIRE_FLOWERS)
                    || ConfigRegistry.FIRE_FLOWER_POWERS_ALL_MOBS.get()))
            tag.putBoolean("marioverse:has_fire_flower", false);

        if (!tag.contains("marioverse:fireball_ready")
                && (entity.getType().is(TagRegistry.CAN_CONSUME_FIRE_FLOWERS)
                    || ConfigRegistry.FIRE_FLOWER_POWERS_ALL_MOBS.get()))
            tag.putBoolean("marioverse:fireball_ready", false);

        if (!tag.contains("marioverse:fireball_cooldown")
                && (entity.getType().is(TagRegistry.CAN_CONSUME_FIRE_FLOWERS)
                    || ConfigRegistry.FIRE_FLOWER_POWERS_ALL_MOBS.get()))
            tag.putInt("marioverse:fireball_cooldown", 0);

        if (!tag.contains("marioverse:fireball_count")
                && (entity.getType().is(TagRegistry.CAN_CONSUME_FIRE_FLOWERS)
                    || ConfigRegistry.FIRE_FLOWER_POWERS_ALL_MOBS.get()))
            tag.putInt("marioverse:fireball_count", 0);

        if (!tag.contains("marioverse:has_mushroom"))
            tag.putBoolean("marioverse:has_mushroom", false);

        if (!tag.contains("marioverse:has_mega_mushroom"))
            tag.putBoolean("marioverse:has_mega_mushroom", false);

        if (entity instanceof Mob mob && !(mob instanceof FireGoombaEntity)) {
            if ((entity.getType().is(TagRegistry.CAN_CONSUME_FIRE_FLOWERS)
                        || ConfigRegistry.FIRE_FLOWER_POWERS_ALL_MOBS.get())) {
                mob.goalSelector.addGoal(0, new ShootBouncingFireballGoal(mob, ConfigRegistry.MAX_MOB_FIREBALLS.get(),
                        0, true));
            }
        }
    }

    @SubscribeEvent
    public static void onEntityDamaged(LivingIncomingDamageEvent event) {
        CompoundTag tag = event.getEntity().getPersistentData();
        Level world = event.getEntity().level();

        if (event.getEntity() instanceof Player player && !player.isDamageSourceBlocked(event.getSource())) {
            float healthAfterDamage = player.getHealth() - event.getAmount();

            if (tag.getBoolean("marioverse:has_fire_flower")) {
                tag.putBoolean("marioverse:has_fire_flower", false);
                world.playSound(null, player.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(),
                        SoundSource.PLAYERS, 1.0F, 1.0F);
            }

            if (healthAfterDamage <= ConfigRegistry.HEALTH_SHRINK_PLAYERS.get()) {
                tag.putBoolean("marioverse:has_mushroom", false);

                if (!tag.getBoolean("marioverse:has_mushroom") && ConfigRegistry.DAMAGE_SHRINKS_PLAYERS.get()
                        && !player.getType().is(TagRegistry.CANNOT_LOSE_POWER_UP)
                        && !player.getType().is(TagRegistry.DAMAGE_CANNOT_SHRINK)
                        && (ScaleTypes.HEIGHT.getScaleData(event.getEntity()).getTargetScale() > 0.5F
                        || ScaleTypes.WIDTH.getScaleData(event.getEntity()).getTargetScale() > 0.75F)) {
                    ScaleTypes.HEIGHT.getScaleData(event.getEntity()).setTargetScale(0.5F);
                    ScaleTypes.WIDTH.getScaleData(event.getEntity()).setTargetScale(0.75F);
                    world.playSound(null, player.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(),
                            SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }

            AccessoriesCapability capability = AccessoriesCapability.get(player);
            if (capability != null && ConfigRegistry.EQUIP_COSTUMES_PLAYERS.get()
                    && !player.getType().is(TagRegistry.CANNOT_LOSE_POWER_UP)) {
                AccessoriesContainer containerHat = capability.getContainer(SlotTypeLoader.getSlotType(player, "costume_hat"));
                AccessoriesContainer containerShirt = capability.getContainer(SlotTypeLoader.getSlotType(player, "costume_shirt"));
                AccessoriesContainer containerPants = capability.getContainer(SlotTypeLoader.getSlotType(player, "costume_pants"));
                AccessoriesContainer containerShoes = capability.getContainer(SlotTypeLoader.getSlotType(player, "costume_shoes"));

                if (containerHat != null) {
                    ItemStack stack = containerHat.getAccessories().getItem(0);
                    if (stack.getItem() instanceof BaseCostumeItem)
                        containerHat.getAccessories().setItem(0, ItemStack.EMPTY);
                }
                if (containerShirt != null) {
                    ItemStack stack = containerShirt.getAccessories().getItem(0);
                    if (stack.getItem() instanceof BaseCostumeItem)
                        containerShirt.getAccessories().setItem(0, ItemStack.EMPTY);
                }
                if (containerPants != null) {
                    ItemStack stack = containerPants.getAccessories().getItem(0);
                    if (stack.getItem() instanceof BaseCostumeItem)
                        containerPants.getAccessories().setItem(0, ItemStack.EMPTY);
                }
                if (containerShoes != null) {
                    ItemStack stack = containerShoes.getAccessories().getItem(0);
                    if (stack.getItem() instanceof BaseCostumeItem)
                        containerShoes.getAccessories().setItem(0, ItemStack.EMPTY);
                }
            }
        } else if (event.getEntity() instanceof LivingEntity livingEntity && !livingEntity.isDamageSourceBlocked(event.getSource())) {
            float maxHealth = livingEntity.getMaxHealth();
            float healthAfterDamage = livingEntity.getHealth() - event.getAmount();
            float threshold = maxHealth * ConfigRegistry.HEALTH_SHRINK_MOBS.get().floatValue();

            if (tag.getBoolean("marioverse:has_fire_flower")
                    && !livingEntity.getType().is(TagRegistry.CANNOT_LOSE_POWER_UP)) {
                tag.putBoolean("marioverse:has_fire_flower", false);
                world.playSound(null, livingEntity.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(),
                        SoundSource.HOSTILE, 1.0F, 1.0F);
            }

            if (healthAfterDamage <= threshold) {
                tag.putBoolean("marioverse:has_mushroom", false);

                if (!tag.getBoolean("marioverse:has_mushroom")
                        && ConfigRegistry.DAMAGE_SHRINKS_ALL_MOBS.get()
                        && !livingEntity.getType().is(TagRegistry.DAMAGE_CANNOT_SHRINK)
                        && (ScaleTypes.HEIGHT.getScaleData(event.getEntity()).getTargetScale() > 0.5F
                        || ScaleTypes.WIDTH.getScaleData(event.getEntity()).getTargetScale() > 0.75F)) {
                    ScaleTypes.HEIGHT.getScaleData(event.getEntity()).setTargetScale(0.5F);
                    ScaleTypes.WIDTH.getScaleData(event.getEntity()).setTargetScale(0.75F);
                    world.playSound(null, livingEntity.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(),
                            SoundSource.HOSTILE, 1.0F, 1.0F);
                }
            }

            AccessoriesCapability capability = AccessoriesCapability.get(livingEntity);
            if (capability != null && ConfigRegistry.EQUIP_COSTUMES_MOBS.get()
                    && !livingEntity.getType().is(TagRegistry.CANNOT_LOSE_POWER_UP)) {
                AccessoriesContainer containerHat = capability.getContainer(SlotTypeLoader.getSlotType(livingEntity, "costume_hat"));
                AccessoriesContainer containerShirt = capability.getContainer(SlotTypeLoader.getSlotType(livingEntity, "costume_shirt"));
                AccessoriesContainer containerPants = capability.getContainer(SlotTypeLoader.getSlotType(livingEntity, "costume_pants"));
                AccessoriesContainer containerShoes = capability.getContainer(SlotTypeLoader.getSlotType(livingEntity, "costume_shoes"));

                if (containerHat != null) {
                    ItemStack stack = containerHat.getAccessories().getItem(0);
                    if (stack.getItem() instanceof BaseCostumeItem)
                        containerHat.getAccessories().setItem(0, ItemStack.EMPTY);
                }
                if (containerShirt != null) {
                    ItemStack stack = containerShirt.getAccessories().getItem(0);
                    if (stack.getItem() instanceof BaseCostumeItem)
                        containerShirt.getAccessories().setItem(0, ItemStack.EMPTY);
                }
                if (containerPants != null) {
                    ItemStack stack = containerPants.getAccessories().getItem(0);
                    if (stack.getItem() instanceof BaseCostumeItem)
                        containerPants.getAccessories().setItem(0, ItemStack.EMPTY);
                }
                if (containerShoes != null) {
                    ItemStack stack = containerShoes.getAccessories().getItem(0);
                    if (stack.getItem() instanceof BaseCostumeItem)
                        containerShoes.getAccessories().setItem(0, ItemStack.EMPTY);
                }
            }
        }

        if (event.getEntity() instanceof GoombaEntity goomba
                && event.getEntity().getItemBySlot(EquipmentSlot.HEAD).is(TagRegistry.POWER_UP_COSTUME_ITEMS)) {
            goomba.getItemBySlot(EquipmentSlot.HEAD).shrink(1);
        }

//        if (tag.getBoolean("marioverse:has_mega_mushroom")) {
//            tag.putBoolean("marioverse:has_mega_mushroom", false);
//            ScaleTypes.WIDTH.getScaleData(event.getEntity()).setTargetScale(1.0F);
//            ScaleTypes.HEIGHT.getScaleData(event.getEntity()).setTargetScale(1.0F);
//            ScaleTypes.JUMP_HEIGHT.getScaleData(event.getEntity()).setTargetScale(1.0F);
//            ScaleTypes.STEP_HEIGHT.getScaleData(event.getEntity()).setTargetScale(1.0F);
//            ScaleTypes.REACH.getScaleData(event.getEntity()).setTargetScale(1.0F);
//            ScaleTypes.ATTACK.getScaleData(event.getEntity()).setTargetScale(1.0F);
//        }
    }

    @SubscribeEvent
    public static void onEntityHeal(LivingHealEvent event) {
        Entity entity = event.getEntity();
        CompoundTag tag = entity.getPersistentData();

        if (entity instanceof Player player) {
            if (player.getHealth() > ConfigRegistry.HEALTH_SHRINK_PLAYERS.get()) {
                tag.putBoolean("marioverse:has_mushroom", true);
                if (tag.getBoolean("marioverse:has_mushroom") && ConfigRegistry.DAMAGE_SHRINKS_PLAYERS.get()
                        && (ScaleTypes.HEIGHT.getScaleData(entity).getTargetScale() < 1.0F
                        || ScaleTypes.WIDTH.getScaleData(entity).getTargetScale() < 1.0F)) {
                    ScaleTypes.HEIGHT.getScaleData(entity).setTargetScale(1.0F);
                    ScaleTypes.WIDTH.getScaleData(entity).setTargetScale(1.0F);
                }
            } else if (tag.getBoolean("marioverse:has_mushroom") && ConfigRegistry.DAMAGE_SHRINKS_PLAYERS.get()
                        && (ScaleTypes.HEIGHT.getScaleData(entity).getTargetScale() < 1.0F
                        || ScaleTypes.WIDTH.getScaleData(entity).getTargetScale() < 1.0F)) {
                ScaleTypes.HEIGHT.getScaleData(entity).setTargetScale(1.0F);
                ScaleTypes.WIDTH.getScaleData(entity).setTargetScale(1.0F);
            }
        } else if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.getHealth() > livingEntity.getMaxHealth() * ConfigRegistry.HEALTH_SHRINK_MOBS.get()) {
                tag.putBoolean("marioverse:has_mushroom", true);
                if (tag.getBoolean("marioverse:has_mushroom") && ConfigRegistry.DAMAGE_SHRINKS_PLAYERS.get()
                        && (ScaleTypes.HEIGHT.getScaleData(entity).getTargetScale() < 1.0F
                        || ScaleTypes.WIDTH.getScaleData(entity).getTargetScale() < 1.0F)) {
                    ScaleTypes.HEIGHT.getScaleData(entity).setTargetScale(1.0F);
                    ScaleTypes.WIDTH.getScaleData(entity).setTargetScale(1.0F);
                }
            } else if (tag.getBoolean("marioverse:has_mushroom") && ConfigRegistry.DAMAGE_SHRINKS_PLAYERS.get()
                        && (ScaleTypes.HEIGHT.getScaleData(entity).getTargetScale() < 1.0F
                        || ScaleTypes.WIDTH.getScaleData(entity).getTargetScale() < 1.0F)) {
                ScaleTypes.HEIGHT.getScaleData(entity).setTargetScale(1.0F);
                ScaleTypes.WIDTH.getScaleData(entity).setTargetScale(1.0F);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide()) {
            BlockPos clickedPos = event.getPos();
            BlockEntity blockEntity = event.getLevel().getBlockEntity(clickedPos);
            if (blockEntity instanceof WarpPipeBlockEntity) {
                // Update the last clicked position
                WarpPipeScreen.lastClickedPos = clickedPos;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Player player = Minecraft.getInstance().player;
        if (player != null && ((player.isSprinting() && ConfigRegistry.RUNNING_ACTIVATES_POWER_UPS.get())
                || KeybindRegistry.FIREBALL_SHOOT_KEY.isDown())) {
            PacketHandler.sendToServer(new FireballShootPayload(player.blockPosition()));
        }
    }

    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        BlockRegistry.WARP_DOORS.forEach((name, deferredBlock) -> {
            Block warpDoor = deferredBlock.get();
            if (!(warpDoor instanceof WarpDoorBlock warpDoorBlock)) return;
            Block baseDoor = warpDoorBlock.getOriginalBlock();

            for (DoorHingeSide hinge : DoorHingeSide.values()) {
                for (boolean open : new boolean[]{true, false}) {
                    for (Direction facing : Direction.Plane.HORIZONTAL) {
                        BlockState baseDoorLowerState = baseDoor.defaultBlockState()
                                .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER)
                                .setValue(DoorBlock.HINGE, hinge)
                                .setValue(DoorBlock.OPEN, open)
                                .setValue(DoorBlock.FACING, facing);
                        BlockState baseDoorUpperState = baseDoor.defaultBlockState()
                                .setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER)
                                .setValue(DoorBlock.HINGE, hinge)
                                .setValue(DoorBlock.OPEN, open)
                                .setValue(DoorBlock.FACING, facing);

                        ModelResourceLocation baseDoorLowerModelLocation = BlockModelShaper.stateToModelLocation(baseDoorLowerState);
                        ModelResourceLocation baseDoorUpperModelLocation = BlockModelShaper.stateToModelLocation(baseDoorUpperState);

                        BakedModel baseDoorLowerModel = event.getModels().get(baseDoorLowerModelLocation);
                        BakedModel baseDoorUpperModel = event.getModels().get(baseDoorUpperModelLocation);

                        if (baseDoorLowerModel != null && baseDoorUpperModel != null) {
                            warpDoor.getStateDefinition().getPossibleStates().forEach(state -> {
                                if (state.getValue(DoorBlock.HINGE) == hinge
                                        && state.getValue(DoorBlock.OPEN) == open
                                        && state.getValue(DoorBlock.FACING) == facing) {
                                    ModelResourceLocation modelLocation = BlockModelShaper.stateToModelLocation(state);

                                    if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER)
                                        event.getModels().put(modelLocation, baseDoorUpperModel);
                                    else if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER)
                                        event.getModels().put(modelLocation, baseDoorLowerModel);
                                }
                            });
                        }
                    }
                }
            }
        });

        BlockRegistry.WARP_DOORS.forEach((name, deferredBlock) -> {
            Block warpDoor = deferredBlock.get();
            if (!(warpDoor instanceof WarpDoorBlock warpDoorBlock)) return;
            Block baseDoor = warpDoorBlock.getOriginalBlock();

            ModelResourceLocation baseDoorModelLocation = ModelResourceLocation.inventory(BlockModelShaper.stateToModelLocation(baseDoor.defaultBlockState()).id());
            BakedModel baseDoorModel = event.getModels().get(baseDoorModelLocation);

            if (baseDoorModel != null) {
                warpDoor.getStateDefinition().getPossibleStates().forEach(state -> {
                    ResourceLocation modelLocation = BlockModelShaper.stateToModelLocation(state).id();
                    event.getModels().put(ModelResourceLocation.inventory(modelLocation), baseDoorModel);
                });
            }
        });
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        removeMiniGoombaSpeedModifier(player); // Custom method to remove the modifier
    }

    @SubscribeEvent
    public static void onPlayerTeleport(EntityTeleportEvent event) {
        if (event.getEntity() instanceof LivingEntity entity) {
            removeMiniGoombaSpeedModifier(entity); // Custom method to remove the modifier
        }
    }

    private static final ResourceLocation SLOWDOWN_MODIFIER_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "mini_goomba_slow");
    private static void removeMiniGoombaSpeedModifier(LivingEntity entity) {
        AttributeInstance speedAttribute = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null && speedAttribute.hasModifier(SLOWDOWN_MODIFIER_RESOURCE)) {
            speedAttribute.removeModifier(SLOWDOWN_MODIFIER_RESOURCE);
        }
    }
}
