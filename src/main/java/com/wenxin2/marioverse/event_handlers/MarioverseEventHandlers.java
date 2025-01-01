package com.wenxin2.marioverse.event_handlers;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.client.WarpPipeScreen;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.entities.FireGoombaEntity;
import com.wenxin2.marioverse.entities.GoombaEntity;
import com.wenxin2.marioverse.entities.ai.goals.ShootBouncingFireballGoal;
import com.wenxin2.marioverse.init.AttributesRegistry;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.KeybindRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import com.wenxin2.marioverse.items.BaseCostumeItem;
import com.wenxin2.marioverse.network.PacketHandler;
import com.wenxin2.marioverse.network.server_bound.data.FireballShootPayload;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.data.SlotTypeLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = Marioverse.MOD_ID)
public class MarioverseEventHandlers {
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
            tag.putBoolean("marioverse:has_mushroom", true);

        if (!tag.contains("marioverse:has_mega_mushroom"))
            tag.putBoolean("marioverse:has_mega_mushroom", false);

        if (!tag.contains("marioverse:has_super_star")
                && (entity.getType().is(TagRegistry.CAN_CONSUME_SUPER_STARS)
                || ConfigRegistry.SUPER_STAR_POWERS_ALL_MOBS.get()))
            tag.putBoolean("marioverse:has_super_star", false);

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
        DamageSource source = event.getSource();

        if (event.getEntity() instanceof Player player && !player.isDamageSourceBlocked(event.getSource())) {
            float healthAfterDamage = player.getHealth() - event.getAmount();

            if (tag.getBoolean("marioverse:has_fire_flower")) {
                tag.putBoolean("marioverse:has_fire_flower", false);
                world.playSound(null, player.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(),
                        SoundSource.PLAYERS, 1.0F, 1.0F);
            }

            if (player.getPersistentData().getBoolean("marioverse:has_super_star")) {
                if (!source.is(DamageTypeTags.BYPASSES_RESISTANCE) && !source.is(TagRegistry.IS_SUPER_STAR))
                    event.setCanceled(true);
            }

            if (healthAfterDamage <= ConfigRegistry.HEALTH_SHRINK_PLAYERS.get()) {
                tag.putBoolean("marioverse:has_mushroom", false);

                if (!tag.getBoolean("marioverse:has_mushroom") && ConfigRegistry.DAMAGE_SHRINKS_PLAYERS.get()
                        && !player.getType().is(TagRegistry.CANNOT_LOSE_POWER_UP)
                        && !player.getType().is(TagRegistry.DAMAGE_CANNOT_SHRINK)) {

                    AttributeInstance eyeHeightScale = player.getAttribute(AttributesRegistry.EYE_HEIGHT_SCALE);
                    AttributeInstance heightScale = player.getAttribute(AttributesRegistry.HEIGHT_SCALE);
                    AttributeInstance widthScale = player.getAttribute(AttributesRegistry.WIDTH_SCALE);

                    if (eyeHeightScale != null && heightScale != null && widthScale != null) {
                        if (!eyeHeightScale.hasModifier(AttributesRegistry.EYE_HEIGHT_SCALE_DAMAGE_MODIFIER.id())
                                && !heightScale.hasModifier(AttributesRegistry.HEIGHT_SCALE_DAMAGE_MODIFIER.id())
                                && !widthScale.hasModifier(AttributesRegistry.WIDTH_SCALE_DAMAGE_MODIFIER.id()))
                            world.playSound(null, player.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(),
                                    SoundSource.PLAYERS, 1.0F, 1.0F);
                    }

                    if (eyeHeightScale != null) {
                        if (!eyeHeightScale.hasModifier(AttributesRegistry.EYE_HEIGHT_SCALE_DAMAGE_MODIFIER.id()))
                            eyeHeightScale.addPermanentModifier(AttributesRegistry.EYE_HEIGHT_SCALE_DAMAGE_MODIFIER);
                    }
                    if (heightScale != null) {
                        if (!heightScale.hasModifier(AttributesRegistry.HEIGHT_SCALE_DAMAGE_MODIFIER.id()))
                            heightScale.addPermanentModifier(AttributesRegistry.HEIGHT_SCALE_DAMAGE_MODIFIER);
                    }
                    if (widthScale != null) {
                        if (!widthScale.hasModifier(AttributesRegistry.WIDTH_SCALE_DAMAGE_MODIFIER.id()))
                            widthScale.addPermanentModifier(AttributesRegistry.WIDTH_SCALE_DAMAGE_MODIFIER);
                    }
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
        } else if (event.getEntity() instanceof LivingEntity entity && !entity.isDamageSourceBlocked(event.getSource())) {
            float maxHealth = entity.getMaxHealth();
            float healthAfterDamage = entity.getHealth() - event.getAmount();
            float threshold = maxHealth * ConfigRegistry.HEALTH_SHRINK_MOBS.get().floatValue();

            if (tag.getBoolean("marioverse:has_fire_flower")
                    && !entity.getType().is(TagRegistry.CANNOT_LOSE_POWER_UP)) {
                tag.putBoolean("marioverse:has_fire_flower", false);
                world.playSound(null, entity.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(),
                        SoundSource.HOSTILE, 1.0F, 1.0F);
            }

            if (entity.getPersistentData().getBoolean("marioverse:has_super_star")) {
                if (!source.is(DamageTypeTags.BYPASSES_RESISTANCE) && !source.is(TagRegistry.IS_SUPER_STAR))
                    event.setCanceled(true);
            }

            if (healthAfterDamage <= threshold) {
                tag.putBoolean("marioverse:has_mushroom", false);

                if (!tag.getBoolean("marioverse:has_mushroom")
                        && ConfigRegistry.DAMAGE_SHRINKS_ALL_MOBS.get()
                        && !entity.getType().is(TagRegistry.DAMAGE_CANNOT_SHRINK)) {

                    AttributeInstance eyeHeightScale = entity.getAttribute(AttributesRegistry.EYE_HEIGHT_SCALE);
                    AttributeInstance heightScale = entity.getAttribute(AttributesRegistry.HEIGHT_SCALE);
                    AttributeInstance widthScale = entity.getAttribute(AttributesRegistry.WIDTH_SCALE);

                    if (eyeHeightScale != null && heightScale != null && widthScale != null) {
                        if (!eyeHeightScale.hasModifier(AttributesRegistry.EYE_HEIGHT_SCALE_DAMAGE_MODIFIER.id())
                                && !heightScale.hasModifier(AttributesRegistry.HEIGHT_SCALE_DAMAGE_MODIFIER.id())
                                && !widthScale.hasModifier(AttributesRegistry.WIDTH_SCALE_DAMAGE_MODIFIER.id()))
                            world.playSound(null, entity.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(),
                                    SoundSource.HOSTILE, 1.0F, 1.0F);
                    }

                    if (eyeHeightScale != null) {
                        if (!eyeHeightScale.hasModifier(AttributesRegistry.EYE_HEIGHT_SCALE_DAMAGE_MODIFIER.id()))
                            eyeHeightScale.addPermanentModifier(AttributesRegistry.EYE_HEIGHT_SCALE_DAMAGE_MODIFIER);
                    }
                    if (heightScale != null) {
                        if (!heightScale.hasModifier(AttributesRegistry.HEIGHT_SCALE_DAMAGE_MODIFIER.id()))
                            heightScale.addPermanentModifier(AttributesRegistry.HEIGHT_SCALE_DAMAGE_MODIFIER);
                    }
                    if (widthScale != null) {
                        if (!widthScale.hasModifier(AttributesRegistry.WIDTH_SCALE_DAMAGE_MODIFIER.id()))
                            widthScale.addPermanentModifier(AttributesRegistry.WIDTH_SCALE_DAMAGE_MODIFIER);
                    }
                }
            }

            AccessoriesCapability capability = AccessoriesCapability.get(entity);
            if (capability != null && ConfigRegistry.EQUIP_COSTUMES_MOBS.get()
                    && !entity.getType().is(TagRegistry.CANNOT_LOSE_POWER_UP)) {
                AccessoriesContainer containerHat = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_hat"));
                AccessoriesContainer containerShirt = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shirt"));
                AccessoriesContainer containerPants = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_pants"));
                AccessoriesContainer containerShoes = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shoes"));

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
                if (tag.getBoolean("marioverse:has_mushroom") && ConfigRegistry.DAMAGE_SHRINKS_PLAYERS.get()) {
                    AttributeInstance eyeHeightScale = player.getAttribute(AttributesRegistry.EYE_HEIGHT_SCALE);
                    AttributeInstance heightScale = player.getAttribute(AttributesRegistry.HEIGHT_SCALE);
                    AttributeInstance widthScale = player.getAttribute(AttributesRegistry.WIDTH_SCALE);

                    if (eyeHeightScale != null) {
                        if (eyeHeightScale.hasModifier(AttributesRegistry.EYE_HEIGHT_SCALE_DAMAGE_MODIFIER.id()))
                            eyeHeightScale.removeModifier(AttributesRegistry.EYE_HEIGHT_SCALE_DAMAGE_MODIFIER);
                    }
                    if (heightScale != null) {
                        if (heightScale.hasModifier(AttributesRegistry.HEIGHT_SCALE_DAMAGE_MODIFIER.id()))
                            heightScale.removeModifier(AttributesRegistry.HEIGHT_SCALE_DAMAGE_MODIFIER);
                    }
                    if (widthScale != null) {
                        if (widthScale.hasModifier(AttributesRegistry.WIDTH_SCALE_DAMAGE_MODIFIER.id()))
                            widthScale.removeModifier(AttributesRegistry.WIDTH_SCALE_DAMAGE_MODIFIER);
                    }
                }
            } else if (tag.getBoolean("marioverse:has_mushroom") && ConfigRegistry.DAMAGE_SHRINKS_PLAYERS.get()) {
                AttributeInstance eyeHeightScale = player.getAttribute(AttributesRegistry.EYE_HEIGHT_SCALE);
                AttributeInstance heightScale = player.getAttribute(AttributesRegistry.HEIGHT_SCALE);
                AttributeInstance widthScale = player.getAttribute(AttributesRegistry.WIDTH_SCALE);

                if (eyeHeightScale != null) {
                    if (eyeHeightScale.hasModifier(AttributesRegistry.EYE_HEIGHT_SCALE_DAMAGE_MODIFIER.id()))
                        eyeHeightScale.removeModifier(AttributesRegistry.EYE_HEIGHT_SCALE_DAMAGE_MODIFIER);
                }
                if (heightScale != null) {
                    if (heightScale.hasModifier(AttributesRegistry.HEIGHT_SCALE_DAMAGE_MODIFIER.id()))
                        heightScale.removeModifier(AttributesRegistry.HEIGHT_SCALE_DAMAGE_MODIFIER);
                }
                if (widthScale != null) {
                    if (widthScale.hasModifier(AttributesRegistry.WIDTH_SCALE_DAMAGE_MODIFIER.id()))
                        widthScale.removeModifier(AttributesRegistry.WIDTH_SCALE_DAMAGE_MODIFIER);
                }
            }
        } else if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.getHealth() > livingEntity.getMaxHealth() * ConfigRegistry.HEALTH_SHRINK_MOBS.get()) {
                tag.putBoolean("marioverse:has_mushroom", true);
                if (tag.getBoolean("marioverse:has_mushroom") && ConfigRegistry.DAMAGE_SHRINKS_PLAYERS.get()) {
                    AttributeInstance eyeHeightScale = livingEntity.getAttribute(AttributesRegistry.EYE_HEIGHT_SCALE);
                    AttributeInstance heightScale = livingEntity.getAttribute(AttributesRegistry.HEIGHT_SCALE);
                    AttributeInstance widthScale = livingEntity.getAttribute(AttributesRegistry.WIDTH_SCALE);

                    if (eyeHeightScale != null) {
                        if (eyeHeightScale.hasModifier(AttributesRegistry.EYE_HEIGHT_SCALE_DAMAGE_MODIFIER.id()))
                            eyeHeightScale.removeModifier(AttributesRegistry.EYE_HEIGHT_SCALE_DAMAGE_MODIFIER);
                    }
                    if (heightScale != null) {
                        if (heightScale.hasModifier(AttributesRegistry.HEIGHT_SCALE_DAMAGE_MODIFIER.id()))
                            heightScale.removeModifier(AttributesRegistry.HEIGHT_SCALE_DAMAGE_MODIFIER);
                    }
                    if (widthScale != null) {
                        if (widthScale.hasModifier(AttributesRegistry.WIDTH_SCALE_DAMAGE_MODIFIER.id()))
                            widthScale.removeModifier(AttributesRegistry.WIDTH_SCALE_DAMAGE_MODIFIER);
                    }
                }
            } else if (tag.getBoolean("marioverse:has_mushroom") && ConfigRegistry.DAMAGE_SHRINKS_PLAYERS.get()) {
                AttributeInstance eyeHeightScale = livingEntity.getAttribute(AttributesRegistry.EYE_HEIGHT_SCALE);
                AttributeInstance heightScale = livingEntity.getAttribute(AttributesRegistry.HEIGHT_SCALE);
                AttributeInstance widthScale = livingEntity.getAttribute(AttributesRegistry.WIDTH_SCALE);

                if (eyeHeightScale != null) {
                    if (eyeHeightScale.hasModifier(AttributesRegistry.EYE_HEIGHT_SCALE_DAMAGE_MODIFIER.id()))
                        eyeHeightScale.removeModifier(AttributesRegistry.EYE_HEIGHT_SCALE_DAMAGE_MODIFIER);
                }
                if (heightScale != null) {
                    if (heightScale.hasModifier(AttributesRegistry.HEIGHT_SCALE_DAMAGE_MODIFIER.id()))
                        heightScale.removeModifier(AttributesRegistry.HEIGHT_SCALE_DAMAGE_MODIFIER);
                }
                if (widthScale != null) {
                    if (widthScale.hasModifier(AttributesRegistry.WIDTH_SCALE_DAMAGE_MODIFIER.id()))
                        widthScale.removeModifier(AttributesRegistry.WIDTH_SCALE_DAMAGE_MODIFIER);
                }
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


    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        removeMiniGoombaSpeedModifier(player);
    }

    @SubscribeEvent
    public static void onPlayerTeleport(EntityTeleportEvent event) {
        if (event.getEntity() instanceof LivingEntity entity) {
            removeMiniGoombaSpeedModifier(entity);
        }
    }

    private static final ResourceLocation SLOWDOWN_MODIFIER =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "mini_goomba_slowdown");
    private static void removeMiniGoombaSpeedModifier(LivingEntity entity) {
        AttributeInstance speedAttribute = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null && speedAttribute.hasModifier(SLOWDOWN_MODIFIER)) {
            speedAttribute.removeModifier(SLOWDOWN_MODIFIER);
        }
    }
}
