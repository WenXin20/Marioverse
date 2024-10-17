package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.FireGoombaEntity;
import com.wenxin2.marioverse.entities.GoombaEntity;
import com.wenxin2.marioverse.entities.MiniGoombaEntity;
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

    public static final DeferredHolder<EntityType<?>, EntityType<FireGoombaEntity>> FIRE_GOOMBA =
            Marioverse.ENTITIES.register("fire_goomba", () -> EntityType.Builder.of(FireGoombaEntity::new, MobCategory.MONSTER)
                    .sized(0.625F, 1.0F).eyeHeight(0.8F).fireImmune().build("fire_goomba"));
    public static final DeferredHolder<EntityType<?>, EntityType<GoombaEntity>> GOOMBA =
            Marioverse.ENTITIES.register("goomba", () -> EntityType.Builder.of(GoombaEntity::new, MobCategory.MONSTER)
                    .sized(0.625F, 1.0F).eyeHeight(0.8F).build("goomba"));
    public static final DeferredHolder<EntityType<?>, EntityType<MiniGoombaEntity>> MINI_GOOMBA =
            Marioverse.ENTITIES.register("mini_goomba", () -> EntityType.Builder.of(MiniGoombaEntity::new, MobCategory.MONSTER)
                    .sized(0.156F, 0.25F).eyeHeight(0.2F).build("mini_goomba"));

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        AttributeSupplier.Builder genericMushroomAttribs = PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1)
                .add(Attributes.MOVEMENT_SPEED, 0.4F);
        AttributeSupplier.Builder genericPowerUpAttribs = PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1);

        event.put(EntityRegistry.FIRE_FLOWER.get(), genericPowerUpAttribs.build());
        event.put(EntityRegistry.MUSHROOM.get(), genericMushroomAttribs.build());
        event.put(EntityRegistry.ONE_UP_MUSHROOM.get(), genericMushroomAttribs.build());

        event.put(EntityRegistry.GOOMBA.get(), PathfinderMob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.0F)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5F)
                .add(Attributes.ATTACK_SPEED, 1.0F)
                .add(Attributes.FOLLOW_RANGE, 8.0F)
                .add(Attributes.MAX_HEALTH, 4)
                .add(Attributes.MOVEMENT_SPEED, 0.4F)
                .add(Attributes.SAFE_FALL_DISTANCE, 10.0F).build());

        event.put(EntityRegistry.FIRE_GOOMBA.get(), PathfinderMob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.5F)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5F)
                .add(Attributes.ATTACK_SPEED, 0.8F)
                .add(Attributes.FOLLOW_RANGE, 8.0F)
                .add(Attributes.MAX_HEALTH, 6)
                .add(Attributes.MOVEMENT_SPEED, 0.4F)
                .add(Attributes.SAFE_FALL_DISTANCE, 10.0F).build());

        event.put(EntityRegistry.MINI_GOOMBA.get(), PathfinderMob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 0.25F)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5F)
                .add(Attributes.ATTACK_SPEED, 1.0F)
                .add(Attributes.FOLLOW_RANGE, 4.0F)
                .add(Attributes.MAX_HEALTH, 1)
                .add(Attributes.MOVEMENT_SPEED, 0.2F)
                .add(Attributes.SAFE_FALL_DISTANCE, 12.0F).build());

    }

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String name, EntityType.EntityFactory<T> entity, MobCategory category,
                                                                                              float width, float height) {
        return Marioverse.ENTITIES.register(name, () -> EntityType.Builder.of(entity, category).sized(width, height).build(name));
    }

    public static void init()
    {}
}
