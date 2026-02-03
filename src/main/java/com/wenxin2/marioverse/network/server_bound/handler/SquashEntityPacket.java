package com.wenxin2.marioverse.network.server_bound.handler;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.KoopaShellEntity;
import com.wenxin2.marioverse.entities.KoopaTroopaEntity;
import com.wenxin2.marioverse.entities.power_ups.OneUpMushroomEntity;
import com.wenxin2.marioverse.entities.projectiles.LargeSnowballProjectile;
import com.wenxin2.marioverse.network.server_bound.data.SquashEntityPayload;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SquashEntityPacket {
    public static final SquashEntityPacket INSTANCE = new SquashEntityPacket();

    public static SquashEntityPacket get() {
        return INSTANCE;
    }

    public void handle(final SquashEntityPayload payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                Player player = context.player();
                if (ConfigRegistry.ENABLE_STOMPABLE_ENEMIES.get()
                        && (player.getType().is(TagRegistry.CAN_STOMP_ENEMIES) || ConfigRegistry.ALL_MOBS_CAN_STOMP.get()
                            || player.level().getGameRules().getBoolean(Marioverse.ALL_MOBS_CAN_STOMP))
                        && (player.fallDistance > 0 || player.isInWaterOrBubble()))
                    this.squashEntity(player, payload.isHoldingJump());
            });
        }
    }

    public void squashEntity(Player stompingPlayer, boolean isHoldingJump) {
        List<Entity> nearbyEntities = stompingPlayer.level().getEntities(stompingPlayer, stompingPlayer.getBoundingBox().inflate(0, 0.5, 0));

        if (!nearbyEntities.isEmpty()) {
            for (Entity damagedEntity : nearbyEntities) {
                if (!damagedEntity.isVehicle() && (stompingPlayer.getType().is(TagRegistry.CAN_STOMP_ENEMIES) || ConfigRegistry.ALL_MOBS_CAN_STOMP.get()
                            || stompingPlayer.level().getGameRules().getBoolean(Marioverse.ALL_MOBS_CAN_STOMP))
                        && !damagedEntity.getType().is(TagRegistry.POWER_UP_ENTITIES)
                        && (damagedEntity.getType().is(TagRegistry.CAN_BE_STOMPED)
                        || damagedEntity.getType().is(TagRegistry.CAN_BE_INSTAKILL_STOMPED)
                        || ConfigRegistry.STOMP_ALL_MOBS.get()
                        || stompingPlayer.level().getGameRules().getBoolean(Marioverse.STOMP_ALL_MOBS))) {

                    if (stompingPlayer instanceof Player player && player.getAbilities().flying)
                        return;

                    if (stompingPlayer.getData(DataAttachmentRegistry.HAS_SUPER_STAR)
                            || damagedEntity.getData(DataAttachmentRegistry.HAS_SUPER_STAR))
                        return;

                    if (stompingPlayer.getY() >= damagedEntity.getY() + damagedEntity.getEyeHeight()
                            && (stompingPlayer.fallDistance > 0 || stompingPlayer.isInWaterOrBubble())) {
                        double bounceBlockHeight = ConfigRegistry.STOMP_BOUNCE_HEIGHT.getAsDouble();

                        if (isHoldingJump)
                            bounceBlockHeight = ConfigRegistry.STOMP_BOUNCE_HEIGHT_JUMP.getAsDouble();
                        double gravity = 0.08;
                        double bounceVelocity = Math.sqrt(2 * gravity * bounceBlockHeight);

                        if (damagedEntity.isAlive()) {
                            stompingPlayer.setDeltaMovement(stompingPlayer.getDeltaMovement().x, bounceVelocity, stompingPlayer.getDeltaMovement().z);
                            stompingPlayer.hasImpulse = true;
                            if (stompingPlayer instanceof ServerPlayer serverPlayer)
                                serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(stompingPlayer));
                        }

                        float scaleFactor = damagedEntity.getBbHeight() * damagedEntity.getBbWidth();
                        int numParticles = (int) (scaleFactor * 20);
                        double radius = damagedEntity.getBbWidth() / 2;

                        if (stompingPlayer.level() instanceof ServerLevel serverWorld)
                            ServerParticleUtils.spawnParticleRingAboveEntity(ParticleTypes.CRIT, serverWorld, damagedEntity, radius, 0, numParticles);

                        if (damagedEntity instanceof LargeSnowballProjectile snowball)
                            snowball.discardEffectsOnSideHit(damagedEntity.level(), damagedEntity.blockPosition(), null);

                        boolean hasNoArmor = true;
                        if (damagedEntity instanceof LivingEntity livingEntity) {
                            for (ItemStack armorSlot : livingEntity.getArmorSlots()) {
                                if (!armorSlot.isEmpty()) {
                                    hasNoArmor = false;
                                    break;
                                }
                            }
                        }

                        if (!stompingPlayer.level().isClientSide() && damagedEntity.isAlive()) {
                            if (damagedEntity.getType().is(TagRegistry.CAN_BE_INSTAKILL_STOMPED) && hasNoArmor
                                    && damagedEntity instanceof LivingEntity livingEntity
                                    && !stompingPlayer.getData(DataAttachmentRegistry.HAS_MINI_MUSHROOM))
                                damagedEntity.hurt(DamageSourceRegistry.stomp(damagedEntity, stompingPlayer), livingEntity.getHealth());
                            else if (damagedEntity.getType().is(TagRegistry.CAN_BE_STOMPED) || ConfigRegistry.STOMP_ALL_MOBS.get()
                                    || damagedEntity.level().getGameRules().getBoolean(Marioverse.STOMP_ALL_MOBS)) {
                                if (stompingPlayer.getData(DataAttachmentRegistry.HAS_MINI_MUSHROOM)
                                        || damagedEntity instanceof KoopaTroopaEntity
                                        || damagedEntity instanceof KoopaShellEntity)
                                    damagedEntity.hurt(DamageSourceRegistry.stomp(damagedEntity, stompingPlayer), 0);
                                else damagedEntity.hurt(DamageSourceRegistry.stomp(damagedEntity, stompingPlayer), ConfigRegistry.STOMP_DAMAGE.get().floatValue());
                            }
                            if (!ConfigRegistry.DISABLE_CONSECUTIVE_BOUNCING.get() && !stompingPlayer.getData(DataAttachmentRegistry.HAS_MINI_MUSHROOM)
                                    && damagedEntity instanceof LivingEntity livingEntity)
                                OneUpMushroomEntity.consecutiveReward(stompingPlayer, livingEntity, stompingPlayer.getData(DataAttachmentRegistry.CONSECUTIVE_BOUNCES));
                            break;
                        }
                    }
                }
            }
        }
    }
}