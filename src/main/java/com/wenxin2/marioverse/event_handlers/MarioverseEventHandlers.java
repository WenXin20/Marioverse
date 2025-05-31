package com.wenxin2.marioverse.event_handlers;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.CheckpointFlagBlock;
import com.wenxin2.marioverse.blocks.PottedPiranhaPlantBlock;
import com.wenxin2.marioverse.blocks.client.WarpPipeScreen;
import com.wenxin2.marioverse.blocks.entities.CheckpointFlagBlockEntity;
import com.wenxin2.marioverse.blocks.entities.PottedPiranhaPlantBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.entities.FireGoombaEntity;
import com.wenxin2.marioverse.entities.IceCubeEntity;
import com.wenxin2.marioverse.entities.KoopaShellEntity;
import com.wenxin2.marioverse.entities.KoopaTroopaEntity;
import com.wenxin2.marioverse.entities.WarpLinkableEntity;
import com.wenxin2.marioverse.entities.ai.goals.ShootBouncingFireballGoal;
import com.wenxin2.marioverse.entities.ai.goals.ShootBouncingIceBallGoal;
import com.wenxin2.marioverse.items.LinkerItem;
import com.wenxin2.marioverse.items.PiranhaPlantPodItem;
import com.wenxin2.marioverse.items.WarpDisruptorItem;
import com.wenxin2.marioverse.network.server_bound.data.BouncePayload;
import com.wenxin2.marioverse.network.server_bound.data.SquashEntityPayload;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.KeybindRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.network.server_bound.data.FireballShootPayload;
import com.wenxin2.marioverse.network.server_bound.data.IceBallShootPayload;
import com.wenxin2.marioverse.utils.PowerUpHandler;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.data.SlotTypeLoader;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Marioverse.MOD_ID)
public class MarioverseEventHandlers {
    private static final float SCALING_SPEED = 0.1F;

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (stack.is(TagRegistry.SMASHABLE_BLOCK_ITEMS))
            event.getToolTip().add(Component.translatable("tooltip.marioverse.smashable_blocks"));
    }

    @SubscribeEvent
    public static void onJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        CompoundTag tag = entity.getPersistentData();

        if (!(entity instanceof LivingEntity)) return;

        if (entity instanceof PowerUpHandler handler) {
            if (entity instanceof Player player) {
                if (player.getHealth() > ConfigRegistry.SHRINK_PLAYERS_AT_HEALTH.get())
                    handler.mv$setMushroom(true);
            } else if (entity instanceof LivingEntity livingEntity) {
                if (livingEntity.getHealth() > livingEntity.getMaxHealth() * ConfigRegistry.SHRINK_MOBS_AT_HEALTH.get())
                    handler.mv$setMushroom(true);
            }
        }

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

        if (!tag.contains("marioverse:ice_ball_ready")
                && (entity.getType().is(TagRegistry.CAN_CONSUME_ICE_FLOWERS)
                || ConfigRegistry.ICE_FLOWER_POWERS_ALL_MOBS.get()))
            tag.putBoolean("marioverse:ice_ball_ready", false);

        if (!tag.contains("marioverse:ice_ball_cooldown")
                && (entity.getType().is(TagRegistry.CAN_CONSUME_ICE_FLOWERS)
                || ConfigRegistry.ICE_FLOWER_POWERS_ALL_MOBS.get()))
            tag.putInt("marioverse:ice_ball_cooldown", 0);

        if (!tag.contains("marioverse:ice_ball_count")
                && (entity.getType().is(TagRegistry.CAN_CONSUME_ICE_FLOWERS)
                    || ConfigRegistry.ICE_FLOWER_POWERS_ALL_MOBS.get()))
            tag.putInt("marioverse:ice_ball_count", 0);

        if (!tag.contains("marioverse:claimed_checkpoint_flag_cooldown")
                && (entity.getType().is(TagRegistry.CAN_CLAIM_CHECKPOINT_FLAGS)))
            tag.putInt("marioverse:claimed_checkpoint_flag_cooldown", 0);

//        if (!tag.contains("marioverse:warp_cooldown")
//                && !entity.getType().is(TagRegistry.CANNOT_WARP)
//                && (ConfigRegistry.TELEPORT_MOBS.get()
//                    || ConfigRegistry.TELEPORT_PLAYERS.get())
//                    || ConfigRegistry.TELEPORT_NON_MOBS.get())
//            tag.putInt("marioverse:warp_cooldown", 0);

        if (entity instanceof Mob mob && !(mob instanceof KoopaShellEntity)) {
            if (!(mob instanceof FireGoombaEntity)) {
                if ((entity.getType().is(TagRegistry.CAN_CONSUME_FIRE_FLOWERS)
                        || ConfigRegistry.FIRE_FLOWER_POWERS_ALL_MOBS.get())) {
                    mob.goalSelector.addGoal(0, new ShootBouncingFireballGoal(mob, ConfigRegistry.MAX_MOB_FIREBALLS.get(),
                            0, true));
                }

                if ((entity.getType().is(TagRegistry.CAN_CONSUME_ICE_FLOWERS)
                        || ConfigRegistry.ICE_FLOWER_POWERS_ALL_MOBS.get())) {
                    mob.goalSelector.addGoal(0, new ShootBouncingIceBallGoal(mob, ConfigRegistry.MAX_MOB_ICE_BALLS.get(),
                            0, true));
                }
            }

            if (mob instanceof PathfinderMob pathfinderMob && !(mob instanceof KoopaTroopaEntity))
                mob.goalSelector.addGoal(3, new AvoidEntityGoal<>(pathfinderMob, KoopaShellEntity.class, 3.0F, 1.0, 1.2));
        }
    }

    @SubscribeEvent
    public static void entityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        int spinningTicks = entity.getPersistentData().getInt("marioverse:spinning_ticks");

        if (entity.isVehicle() && spinningTicks > 0) {
            entity.setYRot(entity.getYRot() + 30);
            entity.getPersistentData().putInt("marioverse:spinning_ticks", spinningTicks - 1);

            for (Entity rider : entity.getPassengers())
                rider.setYHeadRot(rider.getYHeadRot() + 30);
        }
    }

    @SubscribeEvent
    public static void onEntityHeal(LivingHealEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof PowerUpHandler handler) {
            if (entity instanceof Player player) {
                if (player.getHealth() > ConfigRegistry.SHRINK_PLAYERS_AT_HEALTH.get())
                    handler.mv$setMushroom(true);
            } else if (entity instanceof LivingEntity livingEntity) {
                if (livingEntity.getHealth() > livingEntity.getMaxHealth() * ConfigRegistry.SHRINK_MOBS_AT_HEALTH.get())
                    handler.mv$setMushroom(true);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityDamaged(LivingIncomingDamageEvent event) {
        CompoundTag tag = event.getEntity().getPersistentData();
        Level world = event.getEntity().level();
        DamageSource source = event.getSource();

        if (event.getEntity() instanceof Player player && !player.isDamageSourceBlocked(event.getSource())
                && player instanceof PowerUpHandler handler) {
            float healthAfterDamage = player.getHealth() - event.getAmount();

            if (world instanceof ServerLevel serverWorld) {
                if (handler.mv$hasFireFlower() || handler.mv$hasIceFlower())
                    ServerParticleUtils.spawnPoweredUpParticles(ParticleTypes.CRIT, serverWorld, player, 10);
            }

            if (handler.mv$hasFireFlower()) {
                handler.mv$setFireFlower(false);
                world.playSound(null, player.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(),
                        SoundSource.PLAYERS, 1.0F, 1.0F);
            }

            if (handler.mv$hasIceFlower()) {
                handler.mv$setIceFlower(false);
                world.playSound(null, player.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(),
                        SoundSource.PLAYERS, 1.0F, 1.0F);
            }

            if (handler.mv$hasSuperStar()) {
                if (!source.is(TagRegistry.BYPASSES_SUPER_STAR) && !source.is(TagRegistry.IS_SUPER_STAR))
                    event.setCanceled(true);
            }

            if (healthAfterDamage <= ConfigRegistry.SHRINK_PLAYERS_AT_HEALTH.get())
                handler.mv$setMushroom(false);

            AccessoriesCapability capability = AccessoriesCapability.get(player);
            if (capability != null && ConfigRegistry.EQUIP_COSTUMES_PLAYERS.get()
                    && !player.getType().is(TagRegistry.CANNOT_LOSE_POWER_UP)) {
                String[] slotTypes = {"costume_hat", "costume_shirt", "costume_pants", "costume_shoes"};
                for (String slotType : slotTypes) {
                    AccessoriesContainer container = capability.getContainer(SlotTypeLoader.getSlotType(player, slotType));
                    if (container != null) {
                        ItemStack stack = container.getAccessories().getItem(0);
                        if (stack.is(TagRegistry.POWER_UP_COSTUMES))
                            removeCostume(player, capability);
                    }
                }
            }
        } else if (event.getEntity() instanceof LivingEntity entity && !entity.isDamageSourceBlocked(event.getSource())
                && entity instanceof PowerUpHandler handler) {
            float maxHealth = entity.getMaxHealth();
            float healthAfterDamage = entity.getHealth() - event.getAmount();
            float threshold = maxHealth * ConfigRegistry.SHRINK_MOBS_AT_HEALTH.get().floatValue();

            if (world instanceof ServerLevel serverWorld) {
                if (handler.mv$hasFireFlower() || handler.mv$hasIceFlower())
                    ServerParticleUtils.spawnPoweredUpParticles(ParticleTypes.CRIT, serverWorld, entity, 10);
            }

            if (handler.mv$hasFireFlower()
                    && !entity.getType().is(TagRegistry.CANNOT_LOSE_POWER_UP)) {
                handler.mv$setFireFlower(false);
                world.playSound(null, entity.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(),
                        SoundSource.HOSTILE, 1.0F, 1.0F);
            }

            if (handler.mv$hasIceFlower() && !entity.getType().is(TagRegistry.CANNOT_LOSE_POWER_UP)) {
                handler.mv$setIceFlower(false);
                world.playSound(null, entity.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(),
                        SoundSource.HOSTILE, 1.0F, 1.0F);
            }

            if (handler.mv$hasSuperStar()) {
                if (!source.is(DamageTypeTags.BYPASSES_RESISTANCE) && !source.is(TagRegistry.IS_SUPER_STAR))
                    event.setCanceled(true);
            }

            if (healthAfterDamage <= threshold)
                handler.mv$setMushroom(false);

            AccessoriesCapability capability = AccessoriesCapability.get(entity);
            if (capability != null && ConfigRegistry.EQUIP_COSTUMES_MOBS.get()
                    && !entity.getType().is(TagRegistry.CANNOT_LOSE_POWER_UP)) {
                String[] slotTypes = {"costume_hat", "costume_shirt", "costume_pants", "costume_shoes"};
                for (String slotType : slotTypes) {
                    AccessoriesContainer container = capability.getContainer(SlotTypeLoader.getSlotType(entity, slotType));
                    if (container != null) {
                        ItemStack stack = container.getAccessories().getItem(0);
                        if (stack.is(TagRegistry.POWER_UP_COSTUMES))
                            removeCostume(entity, capability);
                    }
                }
            }
        }

        if (event.getEntity().getType().is(TagRegistry.EQUIP_COSTUMES_IN_ARMOR_SLOTS)) {
            if (event.getEntity().getItemBySlot(EquipmentSlot.HEAD).is(TagRegistry.POWER_UP_COSTUMES))
                event.getEntity().getItemBySlot(EquipmentSlot.HEAD).shrink(1);
            if (event.getEntity().getItemBySlot(EquipmentSlot.CHEST).is(TagRegistry.POWER_UP_COSTUMES))
                event.getEntity().getItemBySlot(EquipmentSlot.CHEST).shrink(1);
            if (event.getEntity().getItemBySlot(EquipmentSlot.LEGS).is(TagRegistry.POWER_UP_COSTUMES))
                event.getEntity().getItemBySlot(EquipmentSlot.LEGS).shrink(1);
            if (event.getEntity().getItemBySlot(EquipmentSlot.FEET).is(TagRegistry.POWER_UP_COSTUMES))
                event.getEntity().getItemBySlot(EquipmentSlot.FEET).shrink(1);
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

    public static void onDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (ConfigRegistry.ENABLE_STOMPABLE_ENEMIES.get()
                && (entity.getType().is(TagRegistry.CAN_STOMP_ENEMIES) || ConfigRegistry.ALL_MOBS_CAN_STOMP.get())
                && entity instanceof PowerUpHandler handler
                && handler.mv$getOneUpsRewarded() > 0) {
            handler.mv$setOneUpsRewarded(0);
        }
    }

    private static void removeCostume(LivingEntity entity, AccessoriesCapability capability) {
        AccessoriesContainer containerHat = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_hat"));
        AccessoriesContainer containerShirt = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shirt"));
        AccessoriesContainer containerPants = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_pants"));
        AccessoriesContainer containerShoes = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shoes"));

        if (containerHat != null) {
            ItemStack stack = containerHat.getAccessories().getItem(0);
            ItemStack hatItem = new ItemStack(ItemRegistry.MARIO_HAT.get());

            if (stack.is(TagRegistry.MARIO_COSTUMES))
                containerHat.getAccessories().setItem(0, hatItem);

            hatItem = new ItemStack(ItemRegistry.LUIGI_HAT.get());
            if (stack.is(TagRegistry.LUIGI_COSTUMES))
                containerHat.getAccessories().setItem(0, hatItem);

            hatItem = new ItemStack(ItemRegistry.PEACH_CROWN.get());
            if (stack.is(TagRegistry.PEACH_COSTUMES))
                containerHat.getAccessories().setItem(0, hatItem);

            hatItem.applyComponents(stack.getComponents());
        }
        if (containerShirt != null) {
            ItemStack stack = containerShirt.getAccessories().getItem(0);
            ItemStack shirtItem = new ItemStack(ItemRegistry.MARIO_SHIRT.get());

            if (stack.is(TagRegistry.MARIO_COSTUMES))
                containerShirt.getAccessories().setItem(0, shirtItem);

            shirtItem = new ItemStack(ItemRegistry.LUIGI_SHIRT.get());
            if (stack.is(TagRegistry.LUIGI_COSTUMES))
                containerShirt.getAccessories().setItem(0, shirtItem);

            shirtItem = new ItemStack(ItemRegistry.PEACH_BODICE.get());
            if (stack.is(TagRegistry.PEACH_COSTUMES))
                containerShirt.getAccessories().setItem(0, shirtItem);

            shirtItem.applyComponents(stack.getComponents());
        }
        if (containerPants != null) {
            ItemStack stack = containerPants.getAccessories().getItem(0);
            ItemStack pantsItem = new ItemStack(ItemRegistry.MARIO_PANTS.get());

            if (stack.is(TagRegistry.MARIO_COSTUMES))
                containerPants.getAccessories().setItem(0, pantsItem);

            pantsItem = new ItemStack(ItemRegistry.LUIGI_PANTS.get());
            if (stack.is(TagRegistry.LUIGI_COSTUMES))
                containerPants.getAccessories().setItem(0, pantsItem);

            pantsItem = new ItemStack(ItemRegistry.PEACH_DRESS.get());
            if (stack.is(TagRegistry.PEACH_COSTUMES))
                containerPants.getAccessories().setItem(0, pantsItem);

            pantsItem.applyComponents(stack.getComponents());
        }
        if (containerShoes != null) {
            ItemStack stack = containerShoes.getAccessories().getItem(0);
            ItemStack shoesItem = new ItemStack(ItemRegistry.MARIO_SHOES.get());

            if (stack.is(TagRegistry.MARIO_COSTUMES))
                containerShoes.getAccessories().setItem(0, shoesItem);

            shoesItem = new ItemStack(ItemRegistry.LUIGI_SHOES.get());
            if (stack.is(TagRegistry.LUIGI_COSTUMES))
                containerShoes.getAccessories().setItem(0, shoesItem);

            shoesItem = new ItemStack(ItemRegistry.PEACH_SHOES.get());
            if (stack.is(TagRegistry.PEACH_COSTUMES))
                containerShoes.getAccessories().setItem(0, shoesItem);

            shoesItem.applyComponents(stack.getComponents());
        }
    }

    @SubscribeEvent
    public static void onPlayerRightClick(PlayerInteractEvent.RightClickBlock event) {
        Level world = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = world.getBlockState(pos);
        ItemStack heldItem = event.getItemStack();
        Player player = event.getEntity();

        Direction.Axis axis = event.getEntity().getDirection().getAxis();
        BlockState newState = BlockRegistry.POTTED_PIRANHA_PLANT.get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_AXIS, axis);

        if (heldItem.getItem() instanceof PiranhaPlantPodItem
                && state.getBlock() instanceof FlowerPotBlock flowerPot
                && !(state.getBlock() instanceof PottedPiranhaPlantBlock)
                && flowerPot.getPotted() == Blocks.AIR
                && !player.isShiftKeyDown()) {

            world.setBlock(pos, newState, 3);

            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof PottedPiranhaPlantBlockEntity piranhaPlantBE) {
                piranhaPlantBE.setOwner(player);
                piranhaPlantBE.setChanged();
            }

            world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            player.awardStat(Stats.POT_FLOWER);
            player.swing(InteractionHand.MAIN_HAND);
            heldItem.consume(1, player);
        }

        if (world.isClientSide()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof WarpPipeBlockEntity) {
                // Update the last clicked position
                WarpPipeScreen.lastClickedPos = pos;
            }
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Level world = event.getLevel();
        Entity target = event.getTarget();
        Player player = event.getEntity();
        BlockPos pos = target.blockPosition();
        ItemStack stack = event.getItemStack();

        if (stack.getItem() instanceof LinkerItem linker) {
            if (!player.isCreative() && ConfigRegistry.CREATIVE_WRENCH_LINKING.get()
                    && !ConfigRegistry.DISABLE_WARP_PAINTINGS.get()) {
                player.displayClientMessage(Component.translatable(stack.getDescriptionId() + ".message.requires_creative"), true);
                player.swing(player.getUsedItemHand());
            } else if (!ConfigRegistry.DISABLE_WARP_PAINTINGS.get()) {
                if (player.isShiftKeyDown()) {
                    UUID uuid = target.getUUID();

                    if (world instanceof ServerLevel serverWorld) {
                        if (target instanceof WarpLinkableEntity linkableEntity && linkableEntity.mv$isWaxed()
                                && ConfigRegistry.WAX_DISABLES_WARP_LINKING.get()) {
                            player.displayClientMessage(Component.translatable(linker.getDescriptionId() + ".message.waxed",
                                    target.getName()).withStyle(ChatFormatting.GOLD), true);
                        } else if (!LinkerItem.getIsBound(stack)) {
                            if (target instanceof Painting painting) {
                                int width = painting.getVariant().value().width();
                                Direction direction = painting.getDirection();
                                WarpLinkableEntity.setWarpPos(uuid, pos, direction, width);
                                LinkerItem.setWarpPos(stack, pos);
                            } else {
                                WarpLinkableEntity.setWarpPos(uuid, pos, Direction.NORTH, 1);
                                LinkerItem.setWarpPos(stack, pos);
                            }

                            LinkerItem.setWarpDimension(stack, target.level().dimension().toString());
                            LinkerItem.setWarpUUID(stack, uuid);
                            LinkerItem.setIsBound(stack, true);

                            WarpLinkableEntity.WARP_ENTITY_LOCATIONS.put(pos, target);

                            player.displayClientMessage(Component.translatable(stack.getDescriptionId() + ".message.bound",
                                    target.getName()).withStyle(ChatFormatting.GREEN), true);

                            ServerParticleUtils.spawnParticlesOnEntityRandomly(ParticleTypes.ENCHANT, serverWorld, target, 128);
                            linker.playSound(world, pos, SoundRegistry.WRENCH_BOUND.get(), SoundSource.PLAYERS, 1.0F, 0.1F);
                        } else {
                            BlockPos warpPos = LinkerItem.getWarpPos(stack);
                            UUID warpUUID = LinkerItem.getWarpUUID(stack);
                        //  if (dimension.equals(getWarpDimension(stack))) {
                            Entity warpEntity = serverWorld.getEntity(warpUUID);
                            if (warpEntity == null) {
                                WarpLinkableEntity.WarpTarget warpTarget = WarpLinkableEntity.WARP_LOCATIONS.get(warpUUID);
                                if (warpTarget != null)
                                    warpPos = warpTarget.pos();
                            }

                            if (target instanceof Painting painting) {
                                int width = painting.getVariant().value().width();
                                WarpLinkableEntity.setWarpPos(warpUUID, warpPos, painting.getDirection(), width);
                            }

                            linker.link(stack, warpEntity, target, warpPos);

                            // TODO: Fix paintings not linking in unloaded chunks
//                            if (target.level() instanceof ServerLevel && target.getServer() != null) {
//                                final ServerLevel serverLevel = target.getServer().getLevel(target.level().dimension());
//                                if (serverLevel != null) {
//                                    ChunkAccess chunk = serverLevel.getChunk(warpPos.getX() >> 4, warpPos.getZ() >> 4, ChunkStatus.FULL, true);
//                                    serverLevel.getChunk(warpPos);
//
//                                    final AABB box = new AABB(warpPos).inflate(1);
//                                    final List<Painting> list = serverLevel.getEntitiesOfClass(Painting.class, box);
//                                    for (final Painting warpPainting : list) {
//                                        linker.link(stack, warpPainting, target, warpPos);
//                                    }
//                                }
//                            }

                            player.displayClientMessage(Component.translatable(stack.getDescriptionId() + ".message.linked_warp_block",
                                    target.getName(), warpEntity.getName()).withStyle(ChatFormatting.GOLD), true);

                            ServerParticleUtils.spawnParticlesOnEntityRandomly(ParticleTypes.ENCHANT, serverWorld, target, 128); // TODO: fix pos
                            linker.playSound(world, pos, SoundRegistry.PIPES_LINKED.get(), SoundSource.BLOCKS, 1.0F, 0.1F);
                        //  }
                            LinkerItem.setIsBound(stack, false);
                        }
                    }
                    player.swing(player.getUsedItemHand());
                }
            }
        } else if (stack.getItem() instanceof WarpDisruptorItem disruptorItem) {
            if (!ConfigRegistry.DISABLE_WARP_PAINTINGS.get() && target instanceof WarpLinkableEntity linkableEntity
                    && (!linkableEntity.mv$getPreventWarp() || !linkableEntity.mv$isBreakPainting())) {
                if (world instanceof ServerLevel serverWorld) {
                    if (linkableEntity.mv$isWaxed() && ConfigRegistry.WAX_DISABLES_WARP_LINKING.get()) {
                        player.displayClientMessage(Component.translatable(disruptorItem.getDescriptionId() + ".message.waxed",
                                target.getName()).withStyle(ChatFormatting.GOLD), true);
                    } else if (linkableEntity.mv$getPreventWarp()) {
                        ServerParticleUtils.spawnPoweredUpParticles(ParticleTypes.WARPED_SPORE, serverWorld, target, 16); // TODO: fix pos
                        player.displayClientMessage(Component.translatable(disruptorItem.getDescriptionId() + ".message.break_painting",
                                target.getName()).withStyle(ChatFormatting.DARK_AQUA), true);
                        linkableEntity.mv$setBreakPainting(Boolean.TRUE);
                    } else {
                        ServerParticleUtils.spawnPoweredUpParticles(ParticleTypes.CRIMSON_SPORE, serverWorld, target, 16); // TODO: fix pos
                        player.displayClientMessage(Component.translatable(disruptorItem.getDescriptionId() + ".message.prevent_painting_warp",
                                target.getDisplayName()).withStyle(ChatFormatting.RED), true);
                        linkableEntity.mv$setPreventWarp(Boolean.TRUE);
                    }

                    if (!player.isCreative())
                        stack.hurtAndBreak(1, player, Player.getSlotForHand(player.getUsedItemHand()));
                }
                player.swing(player.getUsedItemHand());
            }
        } else if (stack.getItem() instanceof HoneycombItem) {
            if (target instanceof WarpLinkableEntity linkableEntity
                    && ConfigRegistry.WAX_DISABLES_WARP_LINKING.get()) {
                if (!linkableEntity.mv$isWaxed()) {
                    if (world instanceof ServerLevel serverWorld) {
                        world.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
                        ServerParticleUtils.spawnParticlesOnEntityRandomly(ParticleTypes.WAX_ON, serverWorld, target, 64); // TODO: fix pos
                        stack.consume(1, player);
                    }
                    linkableEntity.mv$setWaxed(true);
                    player.swing(player.getUsedItemHand());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            BlockPos respawnPos = player.getRespawnPosition();

            if (respawnPos != null) {
                Level world = player.level();
                BlockState state = world.getBlockState(respawnPos);

                if (state.getBlock() instanceof CheckpointFlagBlock) {
                    if (ConfigRegistry.CHECKPOINT_FLAG_MODIFY_HEALTH.get()) {
                        player.setHealth(ConfigRegistry.CHECKPOINT_FLAG_RESPAWN_HEALTH.get().floatValue());
                        player.getFoodData().setFoodLevel(ConfigRegistry.CHECKPOINT_FLAG_FOOD_AMT.get());
                        if (player.getHealth() <= ConfigRegistry.SHRINK_PLAYERS_AT_HEALTH.get()
                                && player instanceof PowerUpHandler handler)
                            handler.mv$setMushroom(false);
                    }

                    if (world instanceof ServerLevel serverWorld)
                        serverWorld.sendParticles(ParticleRegistry.GLOWING_STAR.get(),
                                respawnPos.getX() + 0.5, respawnPos.getY() + 0.5, respawnPos.getZ() + 0.5,
                                10, 0.4, 0.5, 0.4, 0.6);
                }

                if (state.getBlock() instanceof CheckpointFlagBlock flagBlock
                        && world.getBlockEntity(respawnPos) instanceof CheckpointFlagBlockEntity flagBE
                        && ConfigRegistry.CHECKPOINT_FLAG_RESPAWN_USES_ITEMS.get()) {
                    ItemStack storedItem = flagBE.getTheItem();

                    if (!storedItem.isEmpty()) {
                        flagBlock.spawnFromCheckpointFlag(world, respawnPos, storedItem, player, true);
                        flagBlock.playSounds(world, respawnPos, storedItem);
                        flagBE.splitTheItem(1);
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Player player = Minecraft.getInstance().player;

        if (player != null ) {
            BlockPos posBelowEntity = BlockPos.containing(player.position().x, player.position().y - 0.3, player.position().z);
            BlockState stateBelowEntity = player.level().getBlockState(posBelowEntity);

            if (KeybindRegistry.ACTIVATE_POWER_UP.isDown()
                    || (player.isSprinting() && ConfigRegistry.RUNNING_ACTIVATES_POWER_UPS.get())) {
                PacketDistributor.sendToServer(new FireballShootPayload(player.blockPosition()));
                PacketDistributor.sendToServer(new IceBallShootPayload(player.blockPosition()));
            }

            if (stateBelowEntity.is(TagRegistry.BOUNCY_BLOCKS)
                    && !player.getType().is(TagRegistry.CANNOT_BOUNCE_ON_BLOCKS)
                    && !player.isSuppressingBounce() && !player.isNoGravity()) {
                if (Minecraft.getInstance().options.keyJump.isDown())
                    PacketDistributor.sendToServer(new BouncePayload(true));
                else PacketDistributor.sendToServer(new BouncePayload(false));
            }

            if (ConfigRegistry.ENABLE_STOMPABLE_ENEMIES.get()
                    && (player.getType().is(TagRegistry.CAN_STOMP_ENEMIES) || ConfigRegistry.ALL_MOBS_CAN_STOMP.get())
                    && (player.fallDistance > 0 || player.isInWaterOrBubble())) {
                if (Minecraft.getInstance().options.keyJump.isDown())
                    PacketDistributor.sendToServer(new SquashEntityPayload(true));
                else PacketDistributor.sendToServer(new SquashEntityPayload(false));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        removeMiniGoombaSpeedModifier(player);
    }

    @SubscribeEvent
    public static void onPlayerTeleport(EntityTeleportEvent event) {
        if (event.getEntity() instanceof LivingEntity entity)
            removeMiniGoombaSpeedModifier(entity);
    }

    @SubscribeEvent
    public static void onDismount(EntityMountEvent event) {
        if (event.getEntityBeingMounted() instanceof IceCubeEntity iceCube && iceCube.isAlive()) {
            if (event.isDismounting()) {
                if (event.getEntityMounting() instanceof Player player && !player.isCreative() && player.isAlive())
                    event.setCanceled(true);
                else if (!(event.getEntityMounting() instanceof Player))
                    event.setCanceled(true);
            }
        }
    }

    private static final ResourceLocation SLOWDOWN_MODIFIER =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "mini_goomba_slowdown");
    private static void removeMiniGoombaSpeedModifier(LivingEntity entity) {
        AttributeInstance speedAttribute = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null && speedAttribute.hasModifier(SLOWDOWN_MODIFIER))
            speedAttribute.removeModifier(SLOWDOWN_MODIFIER);
    }
}
