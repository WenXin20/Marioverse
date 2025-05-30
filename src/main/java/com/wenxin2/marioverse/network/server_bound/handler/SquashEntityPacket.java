package com.wenxin2.marioverse.network.server_bound.handler;

import com.wenxin2.marioverse.entities.KoopaShellEntity;
import com.wenxin2.marioverse.entities.KoopaTroopaEntity;
import com.wenxin2.marioverse.items.OneUpMushroomItem;
import com.wenxin2.marioverse.network.server_bound.data.SquashEntityPayload;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageTypeRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.PowerUpHandler;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import io.wispforest.accessories.api.AccessoriesCapability;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.spongepowered.asm.mixin.Unique;

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
                        && (player.getType().is(TagRegistry.CAN_STOMP_ENEMIES) || ConfigRegistry.ALL_MOBS_CAN_STOMP.get())
                        && (player.fallDistance > 0 || player.isInWaterOrBubble()))
                    this.squashEntity(player, payload.isHoldingJump());
            });
        }
    }

    public void squashEntity(Player stompingPlayer, boolean isHoldingJump) {
        List<Entity> nearbyEntities = stompingPlayer.level().getEntities(stompingPlayer, stompingPlayer.getBoundingBox().inflate(0, 0.5, 0));

        if (!nearbyEntities.isEmpty()) {
            for (Entity entity : nearbyEntities) {
                if (entity instanceof LivingEntity damagedEntity && !damagedEntity.isVehicle()
                        && (stompingPlayer.getType().is(TagRegistry.CAN_STOMP_ENEMIES) || ConfigRegistry.ALL_MOBS_CAN_STOMP.get())
                        && !damagedEntity.getType().is(TagRegistry.POWER_UP_ENTITIES)
                        && (damagedEntity.getType().is(TagRegistry.CAN_BE_STOMPED)
                        || damagedEntity.getType().is(TagRegistry.CAN_BE_INSTAKILL_STOMPED)
                        || ConfigRegistry.STOMP_ALL_MOBS.get())) {

                    if (stompingPlayer instanceof Player player && player.getAbilities().flying)
                        return;

                    if (stompingPlayer instanceof PowerUpHandler handler && handler.mv$hasSuperStar())
                        return;

                    if (damagedEntity instanceof PowerUpHandler handler && handler.mv$hasSuperStar())
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

                        boolean hasNoArmor = true;
                        for (ItemStack armorSlot : damagedEntity.getArmorSlots()) {
                            if (!armorSlot.isEmpty()) {
                                hasNoArmor = false;
                                break;
                            }
                        }

                        if (!stompingPlayer.level().isClientSide() && !damagedEntity.isDeadOrDying()) {
                            if (damagedEntity.getType().is(TagRegistry.CAN_BE_INSTAKILL_STOMPED) && hasNoArmor)
                                damagedEntity.hurt(DamageTypeRegistry.stomp(damagedEntity, stompingPlayer), damagedEntity.getHealth());
                            else if (damagedEntity.getType().is(TagRegistry.CAN_BE_STOMPED) || ConfigRegistry.STOMP_ALL_MOBS.get()) {
                                if (damagedEntity instanceof KoopaTroopaEntity
                                        || damagedEntity instanceof KoopaShellEntity)
                                    damagedEntity.hurt(DamageTypeRegistry.stomp(damagedEntity, stompingPlayer), 0);
                                else damagedEntity.hurt(DamageTypeRegistry.stomp(damagedEntity, stompingPlayer), ConfigRegistry.STOMP_DAMAGE.get().floatValue());
                            }
                            if (!ConfigRegistry.DISABLE_CONSECUTIVE_BOUNCING.get())
                                this.consecutiveReward(stompingPlayer, damagedEntity);
                        }
                    }
                }
            }
        }
    }

    @Unique
    public void consecutiveReward(Player attackingPlayer, LivingEntity damagedEntity) {

        if (attackingPlayer instanceof PowerUpHandler handler) {
            int oneUpsRewarded = handler.mv$getOneUpsRewarded();
            int consecutiveBounces = handler.mv$getConsecutiveBounces();
            handler.mv$setConsecutiveBounces(consecutiveBounces + 1);

            if (consecutiveBounces == 0) {
                if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                    if (damagedEntity.level() instanceof ServerLevel serverWorld)
                        ServerParticleUtils.spawnRewardParticle(ParticleRegistry.GOOD.get(), serverWorld, damagedEntity);
                } else
                    attackingPlayer.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.good"), Boolean.TRUE);
            } else if (consecutiveBounces == 1) {
                if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                    if (damagedEntity.level() instanceof ServerLevel serverWorld)
                        ServerParticleUtils.spawnRewardParticle(ParticleRegistry.GREAT.get(), serverWorld, damagedEntity);
                } else
                    attackingPlayer.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.great"), Boolean.TRUE);
            } else if (consecutiveBounces == 2) {
                if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                    if (damagedEntity.level() instanceof ServerLevel serverWorld)
                        ServerParticleUtils.spawnRewardParticle(ParticleRegistry.SUPER.get(), serverWorld, damagedEntity);
                } else
                    attackingPlayer.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.super"), Boolean.TRUE);
            } else if (consecutiveBounces == 3) {
                if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                    if (damagedEntity.level() instanceof ServerLevel serverWorld)
                        ServerParticleUtils.spawnRewardParticle(ParticleRegistry.FANTASTIC.get(), serverWorld, damagedEntity);
                } else
                    attackingPlayer.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.fantastic"), Boolean.TRUE);
            } else if (consecutiveBounces == 4) {
                if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                    if (damagedEntity.level() instanceof ServerLevel serverWorld)
                        ServerParticleUtils.spawnRewardParticle(ParticleRegistry.EXCELLENT.get(), serverWorld, damagedEntity);
                } else
                    attackingPlayer.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.excellent"), Boolean.TRUE);
            } else if (consecutiveBounces == 5) {
                if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                    if (damagedEntity.level() instanceof ServerLevel serverWorld)
                        ServerParticleUtils.spawnRewardParticle(ParticleRegistry.INCREDIBLE.get(), serverWorld, damagedEntity);
                } else
                    attackingPlayer.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.incredible"), Boolean.TRUE);
            } else if (consecutiveBounces == 6) {
                if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                    if (damagedEntity.level() instanceof ServerLevel serverWorld)
                        ServerParticleUtils.spawnRewardParticle(ParticleRegistry.WONDERFUL.get(), serverWorld, damagedEntity);
                } else
                    attackingPlayer.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.wonderful"), Boolean.TRUE);
            } else if (consecutiveBounces >= 7 && ConfigRegistry.MAX_ONE_UP_BOUNCE_REWARD.get() > oneUpsRewarded) {
                handler.mv$setOneUpsRewarded(oneUpsRewarded + 1);
                this.bounceReward(attackingPlayer);
                if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                    if (damagedEntity.level() instanceof ServerLevel serverWorld)
                        ServerParticleUtils.spawnRewardParticle(ParticleRegistry.ONE_UP.get(), serverWorld, damagedEntity);
                } else
                    attackingPlayer.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.one_up"), Boolean.TRUE);
            }
        }
    }

    @Unique
    public void bounceReward(Player player) {
        ItemLike item = ItemRegistry.ONE_UP_MUSHROOM;
        if (!player.isSpectator()) {
            AccessoriesCapability capability = AccessoriesCapability.get(player);
            ItemStack offhandStack = player.getOffhandItem();

            if (capability != null && !capability.isEquipped(ItemRegistry.ONE_UP_MUSHROOM.get()))
                capability.attemptToEquipAccessory(new ItemStack(ItemRegistry.ONE_UP_MUSHROOM.get()));
            else if (offhandStack.isEmpty())
                player.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(item));
            else if (offhandStack.getCount() >= 1)
                player.addItem(new ItemStack(ItemRegistry.ONE_UP_MUSHROOM.get()));
            player.level().playSound(null, player.blockPosition(), SoundRegistry.ONE_UP_COLLECTED.get(),
                    SoundSource.PLAYERS, 1.0F, 1.0F);

        }
    }
}