package com.wenxin2.marioverse.event_handlers;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.client.WarpPipeScreen;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import virtuoel.pehkui.api.ScaleTypes;

@EventBusSubscriber(modid = Marioverse.MOD_ID)
public class MarioverseEventHandlers {

    @SubscribeEvent
    public static void onJoinWorld(EntityJoinLevelEvent event) {
        CompoundTag tag = event.getEntity().getPersistentData();
        if (!(event.getEntity() instanceof LivingEntity) && !(event.getEntity() instanceof Player)) return;

        if (!tag.contains("marioverse:prevent_warp"))
            tag.putBoolean("marioverse:prevent_warp", false);

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
            tag.putBoolean("marioverse:has_mushroom", false);
            if (healthAfterDamage <= ConfigRegistry.HEALTH_SHRINK_PLAYERS.get()) {
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
        CompoundTag tag = event.getEntity().getPersistentData();

        if (event.getEntity() instanceof Player player && player.getHealth() > ConfigRegistry.HEALTH_SHRINK_PLAYERS.get()) {
            if (!tag.getBoolean("marioverse:has_mushroom")) {
                tag.putBoolean("marioverse:has_mushroom", true);
                if (ConfigRegistry.DAMAGE_SHRINKS_PLAYERS.get()) {
                    ScaleTypes.HEIGHT.getScaleData(event.getEntity()).setTargetScale(1.0F);
                    ScaleTypes.WIDTH.getScaleData(event.getEntity()).setTargetScale(1.0F);
                }
            }
        } else if (event.getEntity() instanceof LivingEntity livingEntity && !(event.getEntity() instanceof Player)
                && livingEntity.getHealth() > livingEntity.getMaxHealth() * ConfigRegistry.HEALTH_SHRINK_MOBS.get()) {
            if (!tag.getBoolean("marioverse:has_mushroom")) {
                tag.putBoolean("marioverse:has_mushroom", true);
                if (ConfigRegistry.DAMAGE_SHRINKS_PLAYERS.get()) {
                    ScaleTypes.HEIGHT.getScaleData(event.getEntity()).setTargetScale(1.0F);
                    ScaleTypes.WIDTH.getScaleData(event.getEntity()).setTargetScale(1.0F);
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
}
