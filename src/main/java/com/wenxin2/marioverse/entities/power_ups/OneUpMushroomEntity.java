package com.wenxin2.marioverse.entities.power_ups;

import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
        ItemLike item = ItemRegistry.ONE_UP_MUSHROOM;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCollisionTime < 500) {
            return; // Skip if called too soon
                    // Prevents item dupe
        }
        lastCollisionTime = currentTime;

        if (entity instanceof LivingEntity livingEntity && entity instanceof AbilitiesHandler handler) {
            handler.applyOneUpMushroomPowerUp(this.level(), new ItemStack(item), livingEntity);
            this.remove(RemovalReason.DISCARDED);
        }
    }
}
