package com.wenxin2.marioverse.event_handlers;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.BlueMushroomTrampolineBlock;
import com.wenxin2.marioverse.blocks.OnOffSwitchBlock;
import com.wenxin2.marioverse.blocks.RedMushroomTrampolineBlock;
import com.wenxin2.marioverse.blocks.CheckpointFlagBlock;
import com.wenxin2.marioverse.blocks.OnBlock;
import com.wenxin2.marioverse.blocks.PottedPiranhaPlantBlock;
import com.wenxin2.marioverse.blocks.ToggleableBlock;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.client.WarpPipeScreen;
import com.wenxin2.marioverse.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.marioverse.blocks.entities.CheckpointFlagBlockEntity;
import com.wenxin2.marioverse.blocks.entities.DisguisedBlockEntity;
import com.wenxin2.marioverse.blocks.entities.PottedPiranhaPlantBlockEntity;
import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.entities.FireGoombaEntity;
import com.wenxin2.marioverse.entities.IceCubeEntity;
import com.wenxin2.marioverse.entities.KoopaShellEntity;
import com.wenxin2.marioverse.entities.KoopaTroopaEntity;
import com.wenxin2.marioverse.entities.PorcupufferEntity;
import com.wenxin2.marioverse.entities.ai.goals.ChaseTargetGoal;
import com.wenxin2.marioverse.entities.ai.goals.CollectBlockGoal;
import com.wenxin2.marioverse.entities.ai.goals.PickupAndThrowShellGoal;
import com.wenxin2.marioverse.entities.ai.goals.ShootBouncingFireballGoal;
import com.wenxin2.marioverse.entities.ai.goals.ShootBouncingIceBallGoal;
import com.wenxin2.marioverse.entities.power_ups.FireFlowerEntity;
import com.wenxin2.marioverse.entities.power_ups.IceFlowerEntity;
import com.wenxin2.marioverse.entities.power_ups.MushroomEntity;
import com.wenxin2.marioverse.entities.power_ups.OneUpMushroomEntity;
import com.wenxin2.marioverse.entities.power_ups.SuperStarEntity;
import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.integration.SupplementariesCompat;
import com.wenxin2.marioverse.integration.sable_compat.SableProvider;
import com.wenxin2.marioverse.inventory.QuestionBlockMenu;
import com.wenxin2.marioverse.items.LinkerItem;
import com.wenxin2.marioverse.items.PiranhaPlantPodItem;
import com.wenxin2.marioverse.items.WarpDisruptorItem;
import com.wenxin2.marioverse.network.server_bound.data.BouncePayload;
import com.wenxin2.marioverse.network.server_bound.data.SquashEntityPayload;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.KeybindRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.network.server_bound.data.FireballShootPayload;
import com.wenxin2.marioverse.network.server_bound.data.IceBallShootPayload;
import com.wenxin2.marioverse.sounds.MarioverseSoundTypes;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import com.wenxin2.marioverse.world.GlobalSwitchSavedData;
import com.wenxin2.marioverse.world.LinkedSwitchSavedData;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.data.SlotTypeLoader;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.CriteriaTriggers;
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
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
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
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.NameTagItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = Marioverse.MOD_ID)
public class MarioverseEventHandlers {
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (stack.is(TagRegistry.SMASHABLE_BLOCK_ITEMS))
            event.getToolTip().add(Component.translatable("tooltip.marioverse.smashable_blocks"));
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        level.getServer().execute(() -> processChunk(level, event.getChunk().getPos()));
        level.getServer().execute(() -> processLinkedChunk(level, event.getChunk().getPos()));
    }

    private static void processChunk(ServerLevel level, ChunkPos chunkPos) {
        GlobalSwitchSavedData data = GlobalSwitchSavedData.get(level);
        boolean isActive = data.isActive();

        for (BlockPos pos : List.copyOf(data.getPositions(chunkPos))) {
            if (!level.isLoaded(pos)) continue;
            BlockState state = level.getBlockState(pos);

            if (!(state.getBlock() instanceof ToggleableBlock)) {
                data.unlink(pos);
                continue;
            }

            if (state.getValue(OnBlock.ACTIVE) != isActive)
                level.setBlock(pos, state.setValue(OnBlock.ACTIVE, isActive), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
        }
    }

    private static void processLinkedChunk(ServerLevel level, ChunkPos chunkPos) {
        LinkedSwitchSavedData data = LinkedSwitchSavedData.get(level);
        Map<BlockPos, Set<BlockPos>> switchMap = data.getChunk(chunkPos);

        if (switchMap.isEmpty())
            return;

        for (var entry : switchMap.entrySet()) {
            BlockPos switchPos = entry.getKey();
            Set<BlockPos> positions = entry.getValue();
            BlockState switchState = level.getBlockState(switchPos);

            if (!(switchState.getBlock() instanceof OnOffSwitchBlock))
                continue;

            boolean isActive = switchState.getValue(OnOffSwitchBlock.ACTIVE);

            for (BlockPos pos : List.copyOf(positions)) {
                if (!level.isLoaded(pos)) continue;
                BlockState state = level.getBlockState(pos);

                if (!(state.getBlock() instanceof ToggleableBlock)) {
                    data.unlink(pos);
                    continue;
                }

                if (state.getValue(OnBlock.ACTIVE) != isActive)
                    level.setBlock(pos, state.setValue(OnBlock.ACTIVE, isActive), Block.UPDATE_CLIENTS);
            }
        }
    }

    @SubscribeEvent
    public static void onJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity)) return;
        CompoundTag tag = entity.getPersistentData();

        // TODO: Remove in 26.1+
        if (tag.contains("marioverse:has_fire_flower")) {
            if (tag.getBoolean("marioverse:has_fire_flower"))
                entity.setData(DataAttachmentRegistry.HAS_FIRE_FLOWER, true);
            tag.remove("marioverse:has_fire_flower");
        }

        if (tag.contains("marioverse:has_ice_flower")) {
            if (tag.getBoolean("marioverse:has_ice_flower"))
                entity.setData(DataAttachmentRegistry.HAS_ICE_FLOWER, true);
            tag.remove("marioverse:has_ice_flower");
        }

        if (tag.contains("marioverse:has_super_mushroom")) {
            entity.setData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM, tag.getBoolean("marioverse:has_super_mushroom"));
            tag.remove("marioverse:has_super_mushroom");
        }

        if (tag.contains("marioverse:has_super_mushroom_override")) {
            entity.setData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM_OVERRIDE, tag.getBoolean("marioverse:has_super_mushroom_override"));
            tag.remove("marioverse:has_super_mushroom_override");
        }

        if (tag.contains("marioverse:prevent_warp")) {
            entity.setData(DataAttachmentRegistry.PREVENT_WARP, tag.getBoolean("marioverse:prevent_warp"));
            tag.remove("marioverse:prevent_warp");
        }

        if (tag.contains("marioverse:warp_cooldown")) {
            entity.setData(DataAttachmentRegistry.WARP_COOLDOWN, tag.getInt("marioverse:warp_cooldown"));
            tag.remove("marioverse:warp_cooldown");
        }

        if (tag.contains("marioverse:break_painting")) {
            entity.setData(DataAttachmentRegistry.BREAK, tag.getBoolean("marioverse:break_painting"));
            tag.remove("marioverse:break_painting");
        }

        if (tag.contains("marioverse:is_waxed")) {
            entity.setData(DataAttachmentRegistry.IS_WAXED, tag.getBoolean("marioverse:is_waxed"));
            tag.remove("marioverse:is_waxed");
        }

        if (tag.contains("marioverse:warp_fuel_count")) {
            entity.setData(DataAttachmentRegistry.WARP_FUEL_COUNT, tag.getInt("marioverse:warp_fuel_count"));
            tag.remove("marioverse:warp_fuel_count");
        }

        if (entity.hasData(DataAttachmentRegistry.HAS_HIT_BLOCK.get())) {
            boolean oldValue = entity.getData(DataAttachmentRegistry.HAS_HIT_BLOCK.get());

            if (oldValue)
                entity.setData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get(), 0);
            entity.removeData(DataAttachmentRegistry.HAS_HIT_BLOCK.get());
        }

        if (entity.hasData(DataAttachmentRegistry.SUPER_STAR_COOLDOWN.get())) {
            int oldValue = entity.getData(DataAttachmentRegistry.SUPER_STAR_COOLDOWN.get());

            entity.setData(DataAttachmentRegistry.SUPER_STAR_DURATION.get(), oldValue);
            entity.removeData(DataAttachmentRegistry.SUPER_STAR_COOLDOWN.get());
        }

        if (!entity.getData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM_OVERRIDE)) {
            if (entity.getType().is(TagRegistry.CAN_CONSUME_SUPER_MUSHROOMS)
                    || ConfigRegistry.SUPER_MUSHROOM_POWERS_ALL_MOBS.get()) {
                if (entity instanceof Player player) {
                    if (player.getHealth() > ConfigRegistry.SHRINK_PLAYERS_AT_HEALTH.get())
                        entity.setData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM, true);
                } else if (entity instanceof LivingEntity livingEntity) {
                    if (livingEntity.getHealth() > livingEntity.getMaxHealth() * ConfigRegistry.SHRINK_MOBS_AT_HEALTH.get())
                        entity.setData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM, true);
                }
            } else entity.setData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM, true);
        }

        if (entity instanceof Mob mob) {
            if (mob.getType().is(TagRegistry.CAN_SHOOT_SUPPLEMENTARIES_CANNON)
                    && ModList.get().isLoaded("supplementaries"))
                SupplementariesCompat.addGoals(mob);

            if (mob.getType().is(TagRegistry.CAN_PICKUP_AND_THROW_SHELLS))
                mob.goalSelector.addGoal(0, new PickupAndThrowShellGoal(mob));

            if (mob.getType().is(TagRegistry.CAN_COLLECT_COINS))
                mob.goalSelector.addGoal(5, new CollectBlockGoal(mob, 0.25F, 5, 0.4F,
                        state -> state.is(BlockRegistry.COIN)));

            if (mob.getType().is(TagRegistry.CAN_COLLECT_STAR_COINS))
                mob.goalSelector.addGoal(5, new CollectBlockGoal(mob, 0.15F, 5, 0.4F,
                        state -> state.is(BlockRegistry.STAR_COIN)));

            if (!(mob instanceof KoopaShellEntity) && mob instanceof AbilitiesHandler) {
                if (!(mob instanceof FireGoombaEntity)) {
                    mob.goalSelector.addGoal(5, new ShootBouncingFireballGoal(mob, ConfigRegistry.MAX_MOB_FIREBALLS.get(),
                            0, true));
                    mob.goalSelector.addGoal(5, new ShootBouncingIceBallGoal(mob, ConfigRegistry.MAX_MOB_ICE_BALLS.get(),
                            0, true));
                }

                if (mob instanceof PathfinderMob pathfinderMob && !(mob instanceof KoopaTroopaEntity))
                    mob.goalSelector.addGoal(3, new AvoidEntityGoal<>(pathfinderMob, KoopaShellEntity.class, 3.0F, 1.0, 1.2));
            }

            if (!mob.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)) {
                if (mob.getType().is(TagRegistry.CAN_CONSUME_FIRE_FLOWERS) || ConfigRegistry.FIRE_FLOWER_POWERS_ALL_MOBS.get())
                    mob.goalSelector.addGoal(3, new ChaseTargetGoal<>(mob, 0.6F, FireFlowerEntity.class));

                if (mob.getType().is(TagRegistry.CAN_CONSUME_ICE_FLOWERS) || ConfigRegistry.ICE_FLOWER_POWERS_ALL_MOBS.get())
                    mob.goalSelector.addGoal(3, new ChaseTargetGoal<>(mob, 0.6F, IceFlowerEntity.class));

                if (mob.getType().is(TagRegistry.CAN_CONSUME_ONE_UPS) || ConfigRegistry.ONE_UP_HEALS_ALL_MOBS.get())
                    mob.goalSelector.addGoal(3, new ChaseTargetGoal<>(mob, 0.6F, OneUpMushroomEntity.class));

                if (mob.getType().is(TagRegistry.CAN_CONSUME_SUPER_MUSHROOMS) || ConfigRegistry.SUPER_MUSHROOM_POWERS_ALL_MOBS.get())
                    mob.goalSelector.addGoal(3, new ChaseTargetGoal<>(mob, 0.6F, MushroomEntity.class));

                if (mob.getType().is(TagRegistry.CAN_CONSUME_SUPER_STARS) || ConfigRegistry.SUPER_STAR_POWERS_ALL_MOBS.get())
                    mob.goalSelector.addGoal(3, new ChaseTargetGoal<>(mob, 0.6F, SuperStarEntity.class));
            }
        }
    }

    @SubscribeEvent
    public static void onEntityHeal(LivingHealEvent event) {
        Entity entity = event.getEntity();

        if (!entity.getData(DataAttachmentRegistry.HAS_MINI_MUSHROOM)
                && (entity.getType().is(TagRegistry.CAN_CONSUME_SUPER_MUSHROOMS)
                    || ConfigRegistry.SUPER_MUSHROOM_POWERS_ALL_MOBS.get())) {
            if (entity instanceof Player player) {
                if (player.getHealth() > ConfigRegistry.SHRINK_PLAYERS_AT_HEALTH.get())
                    entity.setData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM, true);
            } else if (entity instanceof LivingEntity livingEntity) {
                if (livingEntity.getHealth() > livingEntity.getMaxHealth() * ConfigRegistry.SHRINK_MOBS_AT_HEALTH.get())
                    entity.setData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM, true);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityDamaged(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        Level world = entity.level();
        DamageSource source = event.getSource();

        if (entity instanceof Player player && !player.isDamageSourceBlocked(event.getSource())) {
            float healthAfterDamage = player.getHealth() - event.getAmount();
            SoundSource soundSource = SoundSource.PLAYERS;

            if (world instanceof ServerLevel serverWorld) {
                if (entity.getData(DataAttachmentRegistry.HAS_FIRE_FLOWER)
                        || entity.getData(DataAttachmentRegistry.HAS_ICE_FLOWER))
                    ServerParticleUtils.spawnPoweredUpParticles(ParticleTypes.CRIT, serverWorld, player, 10);
            }

            if (entity.getData(DataAttachmentRegistry.HAS_FIRE_FLOWER)) {
                entity.setData(DataAttachmentRegistry.HAS_FIRE_FLOWER, false);
                world.playSound(null, player.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(),
                        soundSource, 1.0F, 1.0F);
            }

            if (entity.getData(DataAttachmentRegistry.HAS_ICE_FLOWER)) {
                entity.setData(DataAttachmentRegistry.HAS_ICE_FLOWER, false);
                world.playSound(null, player.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(),
                        soundSource, 1.0F, 1.0F);
            }

            if (entity.getData(DataAttachmentRegistry.HAS_SUPER_STAR)) {
                if (!source.is(TagRegistry.BYPASSES_SUPER_STAR) && !source.is(TagRegistry.IS_SUPER_STAR))
                    event.setCanceled(true);
            }

            if (healthAfterDamage <= ConfigRegistry.SHRINK_PLAYERS_AT_HEALTH.get()) {
                entity.setData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM, false);
                if (entity.getData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM))
                    world.playSound(null, player.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(),
                            soundSource, 1.0F, 1.0F);
            }

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
        } else if (!entity.isDamageSourceBlocked(event.getSource())) {
            float maxHealth = entity.getMaxHealth();
            float healthAfterDamage = entity.getHealth() - event.getAmount();
            float threshold = maxHealth * ConfigRegistry.SHRINK_MOBS_AT_HEALTH.get().floatValue();

            if (world instanceof ServerLevel serverWorld) {
                if (entity.getData(DataAttachmentRegistry.HAS_FIRE_FLOWER)
                        || entity.getData(DataAttachmentRegistry.HAS_ICE_FLOWER))
                    ServerParticleUtils.spawnPoweredUpParticles(ParticleTypes.CRIT, serverWorld, entity, 10);
            }

            if (entity.getData(DataAttachmentRegistry.HAS_FIRE_FLOWER)
                    && !entity.getType().is(TagRegistry.CANNOT_LOSE_POWER_UP)) {
                entity.setData(DataAttachmentRegistry.HAS_FIRE_FLOWER, false);
                world.playSound(null, entity.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(),
                        SoundSource.HOSTILE, 1.0F, 1.0F);
            }

            if (entity.getData(DataAttachmentRegistry.HAS_ICE_FLOWER)
                    && !entity.getType().is(TagRegistry.CANNOT_LOSE_POWER_UP)) {
                entity.setData(DataAttachmentRegistry.HAS_ICE_FLOWER, false);
                world.playSound(null, entity.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(),
                        SoundSource.HOSTILE, 1.0F, 1.0F);
            }

            if (entity.getData(DataAttachmentRegistry.HAS_SUPER_STAR)) {
                if (!source.is(TagRegistry.BYPASSES_SUPER_STAR) && !source.is(TagRegistry.IS_SUPER_STAR))
                    event.setCanceled(true);
            }

            if (entity.getData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM)) {
                if (!source.is(TagRegistry.BYPASSES_MEGA_MUSHROOM) && !source.is(TagRegistry.IS_MEGA_MUSHROOM_SQUASH))
                    event.setCanceled(true);
            }

            if (healthAfterDamage <= threshold)
                entity.setData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM, false);

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

        if (entity.getType().is(TagRegistry.EQUIP_COSTUMES_IN_ARMOR_SLOTS)) {
            if (entity.getItemBySlot(EquipmentSlot.HEAD).is(TagRegistry.POWER_UP_COSTUMES))
                entity.getItemBySlot(EquipmentSlot.HEAD).shrink(1);
            if (entity.getItemBySlot(EquipmentSlot.CHEST).is(TagRegistry.POWER_UP_COSTUMES))
                entity.getItemBySlot(EquipmentSlot.CHEST).shrink(1);
            if (entity.getItemBySlot(EquipmentSlot.LEGS).is(TagRegistry.POWER_UP_COSTUMES))
                entity.getItemBySlot(EquipmentSlot.LEGS).shrink(1);
            if (entity.getItemBySlot(EquipmentSlot.FEET).is(TagRegistry.POWER_UP_COSTUMES))
                entity.getItemBySlot(EquipmentSlot.FEET).shrink(1);
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (ConfigRegistry.ENABLE_STOMPABLE_ENEMIES.get()
                && (entity.getType().is(TagRegistry.CAN_STOMP_ENEMIES) || ConfigRegistry.ALL_MOBS_CAN_STOMP.get()
                    || entity.level().getGameRules().getBoolean(Marioverse.ALL_MOBS_CAN_STOMP))
                && entity.getData(DataAttachmentRegistry.ONE_UPS_COOLDOWN) > 0) {
            entity.setData(DataAttachmentRegistry.ONE_UPS_COOLDOWN, 0);
            entity.setData(DataAttachmentRegistry.ONE_UPS_REWARDED, 0);
        }

        if (entity.hasData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM)
                && entity.getData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM))
            entity.setData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM, false);

        if (entity.hasData(DataAttachmentRegistry.PLAYED_MEGA_MUSHROOM_THEME)
                && entity.getData(DataAttachmentRegistry.PLAYED_MEGA_MUSHROOM_THEME))
            entity.setData(DataAttachmentRegistry.PLAYED_MEGA_MUSHROOM_THEME, false);

        if (entity.hasData(DataAttachmentRegistry.MEGA_MUSHROOM_DURATION)
                && entity.getData(DataAttachmentRegistry.MEGA_MUSHROOM_DURATION) > 0)
            entity.setData(DataAttachmentRegistry.MEGA_MUSHROOM_DURATION, 0);

        if (entity.hasData(DataAttachmentRegistry.HAS_SUPER_STAR)
                && entity.getData(DataAttachmentRegistry.HAS_SUPER_STAR))
            entity.setData(DataAttachmentRegistry.HAS_SUPER_STAR, false);

        if (entity.hasData(DataAttachmentRegistry.PLAYED_SUPER_STAR_THEME)
                && entity.getData(DataAttachmentRegistry.PLAYED_SUPER_STAR_THEME))
            entity.setData(DataAttachmentRegistry.PLAYED_SUPER_STAR_THEME, false);

        if (entity.hasData(DataAttachmentRegistry.SUPER_STAR_DURATION)
                && entity.getData(DataAttachmentRegistry.SUPER_STAR_DURATION) > 0)
            entity.setData(DataAttachmentRegistry.SUPER_STAR_DURATION, 0);
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
    public static void breakBlockEvent(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        Level level = (Level) event.getLevel();
        ItemStack stack = player.getMainHandItem();
        BlockPos pos = event.getPos();
        Direction face = MarioverseEventHandlers.getBreakFace(level, player);

        if (event.isCanceled()) return;

        if (!level.isClientSide && !player.isSpectator() && !player.isShiftKeyDown()
                && player.getData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM)) {
            if (ConfigRegistry.MEGA_MUSHROOM_MINING_RADIUS.get() > 0 && player.getType().is(TagRegistry.CAN_BREAK_BLOCKS_AS_MEGA))
                MarioverseEventHandlers.breakArea(level, player, pos, face, stack);
        }
    }

    @Nullable
    private static Direction getBreakFace(Level level, Player player) {
        double reach = player.blockInteractionRange();
        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(player.getLookAngle().scale(reach));
        ClipContext context = new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player);

        BlockHitResult result = level.clip(context);
        return result.getType() == HitResult.Type.BLOCK ? result.getDirection() : null;
    }

    private static void breakArea(Level level, Player player, BlockPos pos, Direction face, ItemStack stack) {
        int radius = ConfigRegistry.MEGA_MUSHROOM_MINING_RADIUS.get();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {

                BlockPos target = switch (face) {
                    case UP, DOWN -> pos.offset(dx, 0, dy);
                    case NORTH, SOUTH -> pos.offset(dx, dy, 0);
                    case EAST, WEST -> pos.offset(0, dy, dx);
                };

                if (target.equals(pos))
                    continue;
                BlockState state = level.getBlockState(target);

                if(!player.isCreative() && state.requiresCorrectToolForDrops() && !stack.isCorrectToolForDrops(state))
                    continue;
                if (state.isAir())
                    continue;
                if (!player.mayBuild())
                    continue;

                if (player.isCreative())
                    level.removeBlock(target, true);
                else level.destroyBlock(target, true, player);
            }
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level world = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = world.getBlockState(pos);
        BlockEntity blockEntity = world.getBlockEntity(pos);
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

            if (blockEntity instanceof PottedPiranhaPlantBlockEntity piranhaPlantBE) {
                piranhaPlantBE.setOwner(player);
                piranhaPlantBE.setChanged();
            }

            world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            player.awardStat(Stats.POT_FLOWER);
            player.swing(InteractionHand.MAIN_HAND);
            heldItem.consume(1, player);
        }

        if (player.isCreative() && player.isShiftKeyDown() && heldItem.getItem() instanceof BlockItem blockItem
                && !blockItem.getBlock().defaultBlockState().is(TagRegistry.CANNOT_USE_AS_DISGUISE)
                && blockEntity instanceof DisguisedBlockEntity disguiseBE) {
            ItemStack stackCopy = heldItem.copy();
            BlockState placementState = disguiseBE.getPlacementState(player, stackCopy, event.getHitVec());

            if (placementState != null) {
                BlockState currentState = placementState;

                for (Direction direction : Direction.values()) {
                    BlockPos neighborPos = pos.relative(direction);
                    BlockState neighborState = world.getBlockState(neighborPos);

                    if (world.getBlockEntity(neighborPos) instanceof DisguisedBlockEntity neighborBE) {
                        BlockState neighborDisguise = neighborBE.getDisguiseState();
                        if (neighborDisguise != null && !neighborDisguise.isAir())
                            neighborState = neighborDisguise;
                    }
                    currentState = currentState.updateShape(direction, neighborState, world, pos, neighborPos);
                }
                placementState = currentState;

                disguiseBE.setDisguiseState(placementState);
                disguiseBE.setItem(0, stackCopy);
                heldItem.consume(1, player);
                disguiseBE.requestModelDataUpdate();
                world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), Block.UPDATE_ALL);
                world.playSound(null, pos, blockItem.getBlock().defaultBlockState()
                        .getSoundType(world, pos, player).getPlaceSound(), SoundSource.BLOCKS);
            }
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }

        if (player.isShiftKeyDown() && heldItem.getItem() == ItemRegistry.CREATIVE_WRENCH.get()) {
            if (blockEntity instanceof QuestionBlockEntity questionBE) {
                player.openMenu(new SimpleMenuProvider((id, playerInventory, playerIn) ->
                        new QuestionBlockMenu(id, playerInventory, questionBE,
                                questionBE.getDataAccess(), ContainerLevelAccess.create(world, pos)),
                        ((QuestionBlockEntity) blockEntity).getDisplayName()));
                if (player instanceof ServerPlayer serverPlayer) {
                    CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, heldItem);
                    player.awardStat(Stats.ITEM_USED.get(heldItem.getItem()));
                }
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }

        if (heldItem.getItem() instanceof SpawnEggItem && state.getBlock() instanceof WarpPipeBlock
                && world.getBlockEntity(pos) instanceof BaseWarpBlockEntity warpBE) {
            if (warpBE.isWaxed() || !ConfigRegistry.WARP_PIPE_SPAWNS_MOBS.get() || !player.isCreative()) {
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
            }
        }

        if (heldItem.getItem() instanceof HoneycombItem && world.getBlockEntity(pos) instanceof BaseWarpBlockEntity warpBE
                && (ConfigRegistry.WAX_DISABLES_BUBBLES.get() || ConfigRegistry.WAX_DISABLES_CLOSING.get()
                || ConfigRegistry.WAX_DISABLES_RENAMING.get() || ConfigRegistry.WAX_DISABLES_WATER_SPOUTS.get()
                || ConfigRegistry.WAX_DISABLES_WARP_LINKING.get())) {
            if (!warpBE.isWaxed()) {
                warpBE.setWaxed(true);
                warpBE.markUpdated();
                heldItem.consume(1, player);

                if (state.getBlock() instanceof DoorBlock && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER)
                    ParticleUtils.spawnParticlesOnBlockFaces(world, pos.above(), ParticleTypes.WAX_ON, UniformInt.of(3, 5));
                if (state.getBlock() instanceof DoorBlock && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER)
                    ParticleUtils.spawnParticlesOnBlockFaces(world, pos.below(), ParticleTypes.WAX_ON, UniformInt.of(3, 5));

                ParticleUtils.spawnParticlesOnBlockFaces(world, pos, ParticleTypes.WAX_ON, UniformInt.of(3, 5));
                world.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
                world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                world.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);

                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        }

        if (heldItem.getItem() instanceof AxeItem && world.getBlockEntity(pos) instanceof BaseWarpBlockEntity warpBE
                && ConfigRegistry.ALLOW_WARP_UNWAXING.get()) {
            if (warpBE.isWaxed()) {
                warpBE.setWaxed(false);
                warpBE.markUpdated();
                heldItem.hurtAndBreak(1, player, Player.getSlotForHand(player.getUsedItemHand()));

                if (state.getBlock() instanceof DoorBlock && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER)
                    ParticleUtils.spawnParticlesOnBlockFaces(world, pos.above(), ParticleTypes.WAX_OFF, UniformInt.of(3, 5));
                if (state.getBlock() instanceof DoorBlock && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER)
                    ParticleUtils.spawnParticlesOnBlockFaces(world, pos.below(), ParticleTypes.WAX_OFF, UniformInt.of(3, 5));

                ParticleUtils.spawnParticlesOnBlockFaces(world, pos, ParticleTypes.WAX_OFF, UniformInt.of(3, 5));
                world.playSound(null, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
                world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                world.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);

                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        }

        if (world.isClientSide()) {
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

        boolean isPainting = target instanceof Painting
                || target.getType() == CompatRegistry.IMMERSIVE_GLOW_GRAFFITI.get()
                || target.getType() == CompatRegistry.IMMERSIVE_GLOW_PAINTING.get()
                || target.getType() == CompatRegistry.IMMERSIVE_GRAFFITI.get()
                || target.getType() == CompatRegistry.IMMERSIVE_PAINTING.get()
                || target.getType() == CompatRegistry.MAGIC_PAINTING.get();

        if (stack.getItem() instanceof NameTagItem && target instanceof IceCubeEntity) {
            event.setCancellationResult(InteractionResult.PASS);
            event.setCanceled(true);
        }

        if (stack.getItem() instanceof LinkerItem linker) {
            if (isPainting || stack.is(ItemRegistry.CREATIVE_WRENCH))
                linker.linkEntity(stack, player, target, world, pos);
        }

        if (stack.getItem() instanceof WarpDisruptorItem disruptorItem)
            WarpDisruptorItem.disruptEntity(disruptorItem, target, world, player, stack);

        if ((!ConfigRegistry.DISABLE_WARP_PAINTINGS.get() || player.isCreative())
                && isPainting && stack.is(TagRegistry.CRAFTS_WARP_PAINTING)
                && target.getData(DataAttachmentRegistry.WARP_FUEL_COUNT.get()) < ConfigRegistry.WARP_PAINTING_FUEL_AMT.getAsInt()) {
            target.setData(DataAttachmentRegistry.WARP_FUEL_COUNT.get(), target.getData(DataAttachmentRegistry.WARP_FUEL_COUNT.get()) + 1);
            if (target.getData(DataAttachmentRegistry.WARP_FUEL_COUNT.get()) < ConfigRegistry.WARP_PAINTING_FUEL_AMT.getAsInt())
                world.playSound(null, pos, SoundRegistry.WARP_FUEL_FILLS.get(), SoundSource.BLOCKS);
            else if (target.getData(DataAttachmentRegistry.WARP_FUEL_COUNT.get()) == ConfigRegistry.WARP_PAINTING_FUEL_AMT.getAsInt())
                world.playSound(null, pos, SoundRegistry.WARP_COMPLETED.get(), SoundSource.BLOCKS);
            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnOneLayerBlockParticles(ParticleTypes.PORTAL, serverWorld, target, pos, 16);
            player.swing(player.getUsedItemHand());
            stack.consume(1, player);
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        } else if (stack.getItem() instanceof HoneycombItem) {
            if (!ConfigRegistry.WAX_DISABLES_WARP_LINKING.get()) {
                if (!target.getData(DataAttachmentRegistry.IS_WAXED.get())) {
                    if (world instanceof ServerLevel serverWorld) {
                        world.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
                        ServerParticleUtils.spawnParticlesOnEntityRandomly(ParticleTypes.WAX_ON, serverWorld, target, 0.5, 64);
                        stack.consume(1, player);
                    }
                    target.setData(DataAttachmentRegistry.IS_WAXED.get(), true);
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
                        if (player.getHealth() <= ConfigRegistry.SHRINK_PLAYERS_AT_HEALTH.get())
                            player.setData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM, false);
                    }

                    if (world instanceof ServerLevel serverWorld)
                        serverWorld.sendParticles(ParticleRegistry.GLOWING_STAR.get(),
                                respawnPos.getX() + 0.5, respawnPos.getY() + 0.5, respawnPos.getZ() + 0.5,
                                10, 0.4, 0.5, 0.4, 0.6);
                }

                if (state.getBlock() instanceof CheckpointFlagBlock
                        && world.getBlockEntity(respawnPos) instanceof CheckpointFlagBlockEntity flagBE
                        && ConfigRegistry.CHECKPOINT_FLAG_RESPAWN_USES_ITEMS.get()) {
                    ItemStack storedItem = flagBE.getTheItem();

                    if (!storedItem.isEmpty()) {
                        CheckpointFlagBlock.spawnFromCheckpointFlag(world, respawnPos, storedItem, player, true);
                        MarioverseSoundTypes.playSounds(world, respawnPos, storedItem);
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

        if (player != null) {
            Vec3 motion = player.getDeltaMovement();
            Level level = player.level();

            if (!player.isSpectator()) {
                AABB belowBox = player.getBoundingBox()
                        .expandTowards(0, Math.max(motion.y, -0.2), 0);
                BlockPos min = BlockPos.containing(belowBox.minX, belowBox.minY, belowBox.minZ);
                BlockPos max = BlockPos.containing(belowBox.maxX, belowBox.maxY, belowBox.maxZ);

                Object object = null;
                if (ModList.get().isLoaded("sable"))
                    object = SableProvider.getContext(level, player);

                for (BlockPos posBelow : BlockPos.betweenClosed(min, max)) {
                    BlockState stateBelow = level.getBlockState(posBelow);

                    if (object instanceof SableProvider.SableContext context) {
                        BlockPos posEmbedded = context.posEmbedded.below()
                                .offset(posBelow.getX() - min.getX(),
                                        posBelow.getY() - min.getY(),
                                        posBelow.getZ() - min.getZ());
                        BlockPos posWorld = context.posWorld.below()
                                .offset(posBelow.getX() - min.getX(),
                                        posBelow.getY() - min.getY(),
                                        posBelow.getZ() - min.getZ());
                        stateBelow = context.accessor.getBlockState(posEmbedded);

                        if (level instanceof ServerLevel)
                            stateBelow = context.accessor.getServerBlockState(posWorld);
                    }

                    Block blockBelow = stateBelow.getBlock();
                    boolean canBounce = (stateBelow.is(TagRegistry.BOUNCY_BLOCKS)
                            && !player.getType().is(TagRegistry.CANNOT_BOUNCE_ON_BLOCKS)
                            && !player.isSuppressingBounce() && !player.isNoGravity()
                            && !player.getAbilities().flying)

                            || (blockBelow instanceof BlueMushroomTrampolineBlock
                                && !stateBelow.getValue(OnBlock.ACTIVE)
                                && !player.isSuppressingBounce() && !player.isNoGravity()
                                && !player.getAbilities().flying)

                            || (blockBelow instanceof RedMushroomTrampolineBlock
                                && !(blockBelow instanceof BlueMushroomTrampolineBlock)
                                && stateBelow.getValue(OnBlock.ACTIVE)
                                && !player.isSuppressingBounce() && !player.isNoGravity()
                                && !player.getAbilities().flying);

                    if (canBounce)
                        PacketDistributor.sendToServer(new BouncePayload(Minecraft.getInstance().options.keyJump.isDown()));
                }
            }

            if (!player.isSpectator()) {
                if (KeybindRegistry.ACTIVATE_POWER_UP.isDown()
                        || (player.isSprinting() && ConfigRegistry.RUNNING_ACTIVATES_POWER_UPS.get())) {
                    PacketDistributor.sendToServer(new FireballShootPayload(player.blockPosition()));
                    PacketDistributor.sendToServer(new IceBallShootPayload(player.blockPosition()));
                }
            }

            if (ConfigRegistry.ENABLE_STOMPABLE_ENEMIES.get()
                    && (player.getType().is(TagRegistry.CAN_STOMP_ENEMIES) || ConfigRegistry.ALL_MOBS_CAN_STOMP.get()
                        || level.getGameRules().getBoolean(Marioverse.ALL_MOBS_CAN_STOMP))
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

        if (event.getEntityBeingMounted() instanceof PorcupufferEntity porcupuffer
                && porcupuffer.isAlive() && porcupuffer.isEating()) {
            if (event.isDismounting()) {
                if (event.getEntityMounting() instanceof Player player && !player.isCreative() && player.isAlive()
                        && porcupuffer.isEating())
                    event.setCanceled(true);
                else if (!(event.getEntityMounting() instanceof Player))
                    event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJump(LivingEvent.LivingJumpEvent event) {
        LivingEntity entity = event.getEntity();
        Level world = entity.level();
        BlockPos pos = entity.blockPosition();

        if (!ConfigRegistry.DISABLE_JUMP_SOUND.get() && !entity.isShiftKeyDown() && entity instanceof AbilitiesHandler handler
                && (handler.mv$hasMarioCostume(entity) || handler.mv$hasLuigiCostume(entity)
                    || handler.mv$hasPeachCostume(entity))) {
            entity.level().playSound(null, entity.blockPosition(),
                    entity instanceof Player ? SoundRegistry.PLAYER_JUMP.get() : SoundRegistry.MOB_JUMP.get(),
                    entity instanceof Player ? SoundSource.PLAYERS : SoundSource.NEUTRAL);
        }
    }

    private static final ResourceLocation SLOWDOWN_MODIFIER =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "mini_goomba_slowdown");
    private static void removeMiniGoombaSpeedModifier(LivingEntity player) {
        AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null && speedAttribute.hasModifier(SLOWDOWN_MODIFIER))
            speedAttribute.removeModifier(SLOWDOWN_MODIFIER);
    }
}