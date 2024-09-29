package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.power_ups.FireFlowerEntity;
import com.wenxin2.marioverse.entities.power_ups.MushroomEntity;
import com.wenxin2.marioverse.entities.power_ups.OneUpMushroomEntity;
import com.wenxin2.marioverse.entities.projectiles.BouncingFireballProjectile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

@EventBusSubscriber(modid = Marioverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class EntityRegistry {

    public static final DeferredHolder<EntityType<?>, EntityType<BouncingFireballProjectile>> BOUNCING_FIREBALL = register("bouncing_fireball", BouncingFireballProjectile::new,
            MobCategory.AMBIENT, 0.3f, 0.3f);
    public static final DeferredHolder<EntityType<?>, EntityType<FireFlowerEntity>> FIRE_FLOWER = register("fire_flower", FireFlowerEntity::new,
            MobCategory.AMBIENT, 0.6f, 0.6f);
    public static final DeferredHolder<EntityType<?>, EntityType<MushroomEntity>> MUSHROOM = register("mushroom", MushroomEntity::new,
            MobCategory.AMBIENT, 0.8f, 0.8f);
    public static final DeferredHolder<EntityType<?>, EntityType<OneUpMushroomEntity>> ONE_UP_MUSHROOM = register("one_up_mushroom", OneUpMushroomEntity::new,
            MobCategory.AMBIENT, 0.8f, 0.8f);

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        AttributeSupplier.Builder genericMushroomAttribs = PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1)
                .add(Attributes.MOVEMENT_SPEED, 0.4f);
        AttributeSupplier.Builder genericPowerUpAttribs = PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1);

        event.put(EntityRegistry.FIRE_FLOWER.get(), genericPowerUpAttribs.build());
        event.put(EntityRegistry.MUSHROOM.get(), genericMushroomAttribs.build());
        event.put(EntityRegistry.ONE_UP_MUSHROOM.get(), genericMushroomAttribs.build());

    }

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String name, EntityType.EntityFactory<T> entity, MobCategory category,
                                                                                              float width, float height) {
        return Marioverse.ENTITIES.register(name, () -> EntityType.Builder.of(entity, category).sized(width, height).build(name));
    }

    public static void init()
    {}
}
