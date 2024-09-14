package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.ItemRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import com.wenxin2.marioverse.items.OneUpMushroomItem;
import net.minecraft.core.Holder;
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

    public OneUpMushroomEntity(EntityType<? extends OneUpMushroomEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();
        checkForCollisions();
    }

    @Override
    public void handleCollision(Entity entity) {
        if (!this.level().isClientSide) {
            ItemLike item = ItemRegistry.ONE_UP_MUSHROOM;

            if (entity instanceof Player player && !player.isSpectator()
                    && ConfigRegistry.DAMAGE_SHRINKS_PLAYERS.get()
                    && !player.getType().is(TagRegistry.DAMAGE_SHRINKS_ENTITY_BLACKLIST)) {
                player.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(item));
                this.level().playSound(null, this.blockPosition(), SoundRegistry.ONE_UP_COLLECTED.get(),
                        SoundSource.PLAYERS, 1.0F, 1.0F);
                this.level().broadcastEntityEvent(this, (byte) 60); // Mushroom Transform particle
                this.level().broadcastEntityEvent(this, (byte) 61); // 1-Up Collected particle
                if (!player.getType().is(TagRegistry.CONSUME_POWER_UPS_ENTITY_BLACKLIST))
                    this.remove(RemovalReason.KILLED);
            } else if (entity instanceof LivingEntity livingEntity && ConfigRegistry.DAMAGE_SHRINKS_ALL_MOBS.get()
                    && !(entity instanceof Player)) {
                if (livingEntity.getHealth() < livingEntity.getMaxHealth())
                    livingEntity.heal(ConfigRegistry.MUSHROOM_HEAL_AMT.get().floatValue());
                this.level().playSound(null, this.blockPosition(), SoundRegistry.ONE_UP_COLLECTED.get(),
                        SoundSource.PLAYERS, 1.0F, 1.0F);
                this.level().broadcastEntityEvent(this, (byte) 60); // Mushroom Transform particle
                this.level().broadcastEntityEvent(this, (byte) 61); // 1-Up Collected particle
                if (!livingEntity.getType().is(TagRegistry.CONSUME_POWER_UPS_ENTITY_BLACKLIST))
                    this.remove(RemovalReason.KILLED);
            }
        }
    }
}
