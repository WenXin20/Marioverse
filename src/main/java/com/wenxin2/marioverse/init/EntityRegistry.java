package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.MushroomEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
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

    public static final DeferredHolder<EntityType<?>, EntityType<MushroomEntity>> MUSHROOM = register("mushroom", MushroomEntity::new,
            MobCategory.AMBIENT, 1.5f, 1.5f, 0x302219, 0xACACAC);

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        AttributeSupplier.Builder genericPowerUpAttribs = PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1)
                .add(Attributes.MOVEMENT_SPEED, 1.0f);

        event.put(EntityRegistry.MUSHROOM.get(), genericPowerUpAttribs.build());

    }

    private static <T extends Mob> DeferredHolder<EntityType<?>, EntityType<T>> register(String name, EntityType.EntityFactory<T> entity, MobCategory category,
                                                                                         float width, float height, int primaryEggColor, int secondaryEggColor) {
        return Marioverse.ENTITIES.register(name, () -> EntityType.Builder.of(entity, category).sized(width, height).build(name));
    }

    public static void init()
    {}
}
