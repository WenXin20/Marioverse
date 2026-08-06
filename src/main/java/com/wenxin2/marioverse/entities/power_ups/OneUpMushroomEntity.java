package com.wenxin2.marioverse.entities.power_ups;

import com.wenxin2.marioverse.entities.PiranhaPlantEntity;
import com.wenxin2.marioverse.power_up.PowerUpSource;
import com.wenxin2.marioverse.power_up.PowerUpType;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.PowerUpTypeRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
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
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

public class OneUpMushroomEntity extends MushroomEntity implements GeoEntity, PowerUpSource {
    private long lastCollisionTime = 0;

    public OneUpMushroomEntity(EntityType<? extends OneUpMushroomEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public Holder<PowerUpType> getPowerUpType() {
        return PowerUpTypeRegistry.ONE_UP_MUSHROOM;
    }

    public static void consecutiveReward(LivingEntity attackingEntity, LivingEntity damagedEntity, int consecutiveBounces) {
        Level level = damagedEntity.level();
        int oneUpsRewarded = attackingEntity.getData(DataAttachmentRegistry.ONE_UPS_REWARDED);
        attackingEntity.setData(DataAttachmentRegistry.CONSECUTIVE_BOUNCES, consecutiveBounces + 1);

        if (damagedEntity instanceof PiranhaPlantEntity piranhaPlant && piranhaPlant.isHiding())
            return;

        if (consecutiveBounces == 0) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                if (level instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnRewardParticle(ParticleRegistry.GOOD.get(), serverWorld, damagedEntity, 1.0);
            } else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.good"), Boolean.TRUE);
        } else if (consecutiveBounces == 1) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                if (level instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnRewardParticle(ParticleRegistry.GREAT.get(), serverWorld, damagedEntity, 1.0);
            } else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.great"), Boolean.TRUE);
        } else if (consecutiveBounces == 2) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                if (level instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnRewardParticle(ParticleRegistry.SUPER.get(), serverWorld, damagedEntity, 1.0);
            } else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.super"), Boolean.TRUE);
        } else if (consecutiveBounces == 3) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                if (level instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnRewardParticle(ParticleRegistry.FANTASTIC.get(), serverWorld, damagedEntity, 1.0);
            } else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.fantastic"), Boolean.TRUE);
        } else if (consecutiveBounces == 4) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                if (level instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnRewardParticle(ParticleRegistry.EXCELLENT.get(), serverWorld, damagedEntity, 1.0);
            } else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.excellent"), Boolean.TRUE);
        } else if (consecutiveBounces == 5) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                if (level instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnRewardParticle(ParticleRegistry.INCREDIBLE.get(), serverWorld, damagedEntity, 1.0);
            } else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.incredible"), Boolean.TRUE);
        } else if (consecutiveBounces == 6) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                if (level instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnRewardParticle(ParticleRegistry.WONDERFUL.get(), serverWorld, damagedEntity, 1.0);
            } else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.wonderful"), Boolean.TRUE);
        } else if (consecutiveBounces >= 7 && ConfigRegistry.MAX_ONE_UP_BOUNCE_REWARD.get() > oneUpsRewarded
                && !attackingEntity.getType().is(TagRegistry.CANNOT_REWARD_ONE_UPS)) {
            attackingEntity.setData(DataAttachmentRegistry.ONE_UPS_REWARDED, oneUpsRewarded + 1);
            attackingEntity.setData(DataAttachmentRegistry.ONE_UPS_COOLDOWN, ConfigRegistry.ONE_UP_COOLDOWN.get());
            OneUpMushroomEntity.bounceReward(attackingEntity);

            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                if (level instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnRewardParticle(ParticleRegistry.ONE_UP.get(), serverWorld, damagedEntity, 1.0);
            } else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.one_up"), Boolean.TRUE);
        }
    }

    public static void bounceReward(LivingEntity entity) {
        float pitch = 0.9F + entity.level().random.nextFloat() * 0.2F;
        ItemLike item = ItemRegistry.ONE_UP_MUSHROOM;

        if (!entity.isSpectator()
                && (ConfigRegistry.ONE_UP_HEALS_ALL_MOBS.get() || entity.getType().is(TagRegistry.CAN_CONSUME_ONE_UPS))) {
            Optional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(entity);
            ItemStack offhandStack = entity.getOffhandItem();

            ICurioStacksHandler slotCharm = curiosInventory.map(handler -> handler.getCurios().get("charm")).orElse(null);
            ItemStack stackCharm = slotCharm != null ? slotCharm.getStacks().getStackInSlot(0) : ItemStack.EMPTY;
            boolean equippedInCurios = curiosInventory.map(handler -> handler.isEquipped(ItemRegistry.ONE_UP_MUSHROOM.get())).orElse(false);

            if (slotCharm != null && !equippedInCurios && stackCharm.isEmpty())
                slotCharm.getStacks().setStackInSlot(0, new ItemStack(ItemRegistry.ONE_UP_MUSHROOM.get()));
            else if (offhandStack.isEmpty())
                entity.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(item));
            else if (offhandStack.getCount() >= 1 && entity instanceof Player player)
                player.addItem(new ItemStack(ItemRegistry.ONE_UP_MUSHROOM.get()));
            entity.level().playSound(null, entity.blockPosition(), SoundRegistry.ONE_UP_COLLECTED.get(),
                    SoundSource.PLAYERS, 1.0F, pitch);
        }
    }

    @Override
    public void collideWithEntity(Entity entity) {
        LivingEntity rider = entity.getControllingPassenger();

        if (!this.level().isClientSide) {
            ItemLike item = ItemRegistry.ONE_UP_MUSHROOM;
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastCollisionTime < 500) {
                return; // Skip if called too soon
                // Prevents item dupe
            }
            lastCollisionTime = currentTime;

            if (entity.getType().is(TagRegistry.POWERS_UP_RIDER) && entity.hasControllingPassenger()
                    && rider instanceof AbilitiesHandler handler)
                handler.applyOneUpMushroomPowerUp(this.level(), new ItemStack(item), rider, this);
            else if (entity instanceof LivingEntity livingEntity && entity instanceof AbilitiesHandler handler)
                handler.applyOneUpMushroomPowerUp(this.level(), new ItemStack(item), livingEntity, this);
        }
    }
}
