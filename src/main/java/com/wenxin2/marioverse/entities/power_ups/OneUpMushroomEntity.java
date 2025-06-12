package com.wenxin2.marioverse.entities.power_ups;

import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.items.OneUpMushroomItem;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;

public class OneUpMushroomEntity extends MushroomEntity implements GeoEntity {
    private long lastCollisionTime = 0;

    public OneUpMushroomEntity(EntityType<? extends OneUpMushroomEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();
        checkForCollisions();
    }

    @Override
    public void collideWithEntity(Entity entity) {
        if (!this.level().isClientSide) {
            long currentTime = System.currentTimeMillis();
            ItemLike item = ItemRegistry.ONE_UP_MUSHROOM;

            if (currentTime - lastCollisionTime < 500) {
                return; // Skip if called too soon
            }
            lastCollisionTime = currentTime;

            if (entity instanceof Player player && !player.isSpectator()
                    && !player.getType().is(TagRegistry.DAMAGE_CANNOT_SHRINK)
                    && player.getType().is(TagRegistry.CAN_CONSUME_ONE_UPS)) {
                AccessoriesCapability capability = AccessoriesCapability.get(player);
                ItemStack offhandStack = player.getOffhandItem();

                if (capability != null && !capability.isEquipped(ItemRegistry.ONE_UP_MUSHROOM.get())) {
                    capability.attemptToEquipAccessory(new ItemStack(ItemRegistry.ONE_UP_MUSHROOM.get()));
                } else if (offhandStack.isEmpty())
                    player.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(item));
                else if (offhandStack.getItem() instanceof OneUpMushroomItem) {
                    if (offhandStack.getCount() >= 8) {
                        player.drop(new ItemStack(ItemRegistry.ONE_UP_MUSHROOM.get()), Boolean.FALSE);
                    } else offhandStack.grow(1);
                }

                if (!player.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)) {
                    this.level().playSound(null, this.blockPosition(), SoundRegistry.ONE_UP_COLLECTED.get(),
                            SoundSource.PLAYERS, 1.0F, 1.0F);
                    if (entity.level() instanceof ServerLevel serverWorld) {
                        ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.POWERED_UP.get(), serverWorld, entity, 25);
                        ServerParticleUtils.spawnRewardParticle(ParticleRegistry.ONE_UP.get(), serverWorld, entity);
                    }
                    this.remove(RemovalReason.KILLED);
                }
            } else if (entity instanceof LivingEntity livingEntity
                    && !entity.getType().is(TagRegistry.DAMAGE_CANNOT_SHRINK)
                    && (entity.getType().is(TagRegistry.CAN_CONSUME_ONE_UPS) || ConfigRegistry.ONE_UP_HEALS_ALL_MOBS.get())) {
                AccessoriesCapability capability = AccessoriesCapability.get(livingEntity);
                ItemStack offhandStack = livingEntity.getOffhandItem();

                if (capability != null && !capability.isEquipped(ItemRegistry.ONE_UP_MUSHROOM.get())) {
                    capability.attemptToEquipAccessory(new ItemStack(ItemRegistry.ONE_UP_MUSHROOM.get()));
                } else if (offhandStack.isEmpty())
                    livingEntity.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(item));
                else if (offhandStack.getItem() instanceof OneUpMushroomItem) {
                    offhandStack.grow(1);
                }

                if (!livingEntity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)) {
                    this.level().playSound(null, this.blockPosition(), SoundRegistry.ONE_UP_COLLECTED.get(),
                            SoundSource.NEUTRAL, 1.0F, 1.0F);
                    if (entity.level() instanceof ServerLevel serverWorld) {
                        ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.POWERED_UP.get(), serverWorld, entity, 25);
                        ServerParticleUtils.spawnRewardParticle(ParticleRegistry.ONE_UP.get(), serverWorld, entity);
                    }
                    this.remove(RemovalReason.KILLED);
                }
            }
        }
    }
}
