package com.wenxin2.marioverse.event_handlers;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.client.WarpPipeScreen;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
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
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import virtuoel.pehkui.api.ScaleTypes;

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
                && entity.getType().is(TagRegistry.FIRE_FLOWER_WHITELIST))
            tag.putBoolean("marioverse:has_fire_flower", false);

        if (!tag.contains("marioverse:fireball_ready") && entity instanceof Player
                && entity.getType().is(TagRegistry.FIRE_FLOWER_WHITELIST))
            tag.putBoolean("marioverse:fireball_ready", false);

        if (!tag.contains("marioverse:fireball_cooldown") && entity instanceof Player
                && entity.getType().is(TagRegistry.FIRE_FLOWER_WHITELIST))
            tag.putInt("marioverse:fireball_cooldown", 0);

        if (!tag.contains("marioverse:fireball_count") && entity instanceof Player
                && entity.getType().is(TagRegistry.FIRE_FLOWER_WHITELIST))
            tag.putInt("marioverse:fireball_count", 0);

        if (!tag.contains("marioverse:has_mushroom"))
            tag.putBoolean("marioverse:has_mushroom", false);

        if (!tag.contains("marioverse:has_mega_mushroom"))
            tag.putBoolean("marioverse:has_mega_mushroom", false);
    }

    @SubscribeEvent
    public static void onEntityDamaged(LivingIncomingDamageEvent event) {
        CompoundTag tag = event.getEntity().getPersistentData();
        Level world = event.getEntity().level();

        if (event.getEntity() instanceof Player player) {
            float healthAfterDamage = player.getHealth() - event.getAmount();

            tag.putBoolean("marioverse:has_fire_flower", false);

            if (healthAfterDamage <= ConfigRegistry.HEALTH_SHRINK_PLAYERS.get()) {
                tag.putBoolean("marioverse:has_mushroom", false);

                if (!tag.getBoolean("marioverse:has_mushroom") && ConfigRegistry.DAMAGE_SHRINKS_PLAYERS.get()
                        && !player.getType().is(TagRegistry.DAMAGE_SHRINKS_ENTITY_BLACKLIST)
                        && (ScaleTypes.HEIGHT.getScaleData(event.getEntity()).getTargetScale() > 0.5F
                        || ScaleTypes.WIDTH.getScaleData(event.getEntity()).getTargetScale() > 0.75F)) {
                    ScaleTypes.HEIGHT.getScaleData(event.getEntity()).setTargetScale(0.5F);
                    ScaleTypes.WIDTH.getScaleData(event.getEntity()).setTargetScale(0.75F);
                    world.playSound(null, player.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(),
                            SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }

            AccessoriesCapability capability = AccessoriesCapability.get(player);
            if (capability != null) {
                AccessoriesContainer containerHat = capability.getContainer(SlotTypeLoader.getSlotType(player, "costume_hat"));
                AccessoriesContainer containerShirt = capability.getContainer(SlotTypeLoader.getSlotType(player, "costume_shirt"));
                AccessoriesContainer containerPants = capability.getContainer(SlotTypeLoader.getSlotType(player, "costume_pants"));
                AccessoriesContainer containerShoes = capability.getContainer(SlotTypeLoader.getSlotType(player, "costume_shoes"));

                if (containerHat != null) {
                    ItemStack stack = containerHat.getAccessories().getItem(0);
                    if (stack.getItem() instanceof BaseCostumeItem) {
                        containerHat.getAccessories().setItem(0, ItemStack.EMPTY);
                        world.playSound(null, player.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(),
                                SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                }
                if (containerShirt != null) {
                    ItemStack stack = containerShirt.getAccessories().getItem(0);
                    if (stack.getItem() instanceof BaseCostumeItem) {
                        containerShirt.getAccessories().setItem(0, ItemStack.EMPTY);
                        world.playSound(null, player.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(),
                                SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                }
                if (containerPants != null) {
                    ItemStack stack = containerPants.getAccessories().getItem(0);
                    if (stack.getItem() instanceof BaseCostumeItem) {
                        containerPants.getAccessories().setItem(0, ItemStack.EMPTY);
                        world.playSound(null, player.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(),
                                SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                }
                if (containerShoes != null) {
                    ItemStack stack = containerShoes.getAccessories().getItem(0);
                    if (stack.getItem() instanceof BaseCostumeItem) {
                        containerShoes.getAccessories().setItem(0, ItemStack.EMPTY);
                        world.playSound(null, player.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(),
                                SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                }
            }
        } else if (event.getEntity() instanceof LivingEntity livingEntity && ConfigRegistry.DAMAGE_SHRINKS_ALL_MOBS.get()) {
            float maxHealth = livingEntity.getMaxHealth();
            float healthAfterDamage = livingEntity.getHealth() - event.getAmount();
            float threshold = maxHealth * ConfigRegistry.HEALTH_SHRINK_MOBS.get().floatValue();

            tag.putBoolean("marioverse:has_mushroom", false);
            if (healthAfterDamage <= threshold) {
                if (!tag.getBoolean("marioverse:has_mushroom")
                        && !livingEntity.getType().is(TagRegistry.DAMAGE_SHRINKS_ENTITY_BLACKLIST)
                        && (ScaleTypes.HEIGHT.getScaleData(event.getEntity()).getTargetScale() > 0.5F
                        || ScaleTypes.WIDTH.getScaleData(event.getEntity()).getTargetScale() > 0.75F)) {
                    ScaleTypes.HEIGHT.getScaleData(event.getEntity()).setTargetScale(0.5F);
                    ScaleTypes.WIDTH.getScaleData(event.getEntity()).setTargetScale(0.75F);
                    world.playSound(null, livingEntity.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(),
                            SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }
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
}
