package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.FireGoombaEntity;
import com.wenxin2.marioverse.entities.GoombaEntity;
import com.wenxin2.marioverse.entities.HeftyGoombaEntity;
import com.wenxin2.marioverse.entities.IceCubeEntity;
import com.wenxin2.marioverse.entities.KoopaShellEntity;
import com.wenxin2.marioverse.entities.KoopaTroopaEntity;
import com.wenxin2.marioverse.entities.MegaGoombaEntity;
import com.wenxin2.marioverse.entities.MiniGoombaEntity;
import com.wenxin2.marioverse.entities.PiranhaPlantEntity;
import com.wenxin2.marioverse.entities.power_ups.FireFlowerEntity;
import com.wenxin2.marioverse.entities.power_ups.IceFlowerEntity;
import com.wenxin2.marioverse.entities.power_ups.MushroomEntity;
import com.wenxin2.marioverse.entities.power_ups.OneUpMushroomEntity;
import com.wenxin2.marioverse.entities.power_ups.SuperStarEntity;
import com.wenxin2.marioverse.entities.projectiles.BouncingFireballProjectile;
import com.wenxin2.marioverse.entities.projectiles.BouncingIceBallProjectile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

@EventBusSubscriber(modid = Marioverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class EntityRegistry {
    public static final DeferredHolder<EntityType<?>, EntityType<BouncingFireballProjectile>> BOUNCING_FIREBALL = register("bouncing_fireball", BouncingFireballProjectile::new,
            MobCategory.AMBIENT, 0.3f, 0.3f);
    public static final DeferredHolder<EntityType<?>, EntityType<BouncingIceBallProjectile>> BOUNCING_ICE_BALL = register("bouncing_ice_ball", BouncingIceBallProjectile::new,
            MobCategory.AMBIENT, 0.5f, 0.5f);
    public static final DeferredHolder<EntityType<?>, EntityType<IceCubeEntity>> ICE_CUBE = Marioverse.ENTITIES.register("ice_cube", () -> EntityType.Builder.of(IceCubeEntity::new,
            MobCategory.AMBIENT).sized(1.0F, 1.0F).passengerAttachments(0.5F).build("ice_cube"));

    public static final DeferredHolder<EntityType<?>, EntityType<FireFlowerEntity>> FIRE_FLOWER = register("fire_flower", FireFlowerEntity::new,
            MobCategory.AMBIENT, 0.6f, 0.6f);
    public static final DeferredHolder<EntityType<?>, EntityType<IceFlowerEntity>> ICE_FLOWER = register("ice_flower", IceFlowerEntity::new,
            MobCategory.AMBIENT, 0.6f, 0.6f);
    public static final DeferredHolder<EntityType<?>, EntityType<MushroomEntity>> MUSHROOM = register("mushroom", MushroomEntity::new,
            MobCategory.AMBIENT, 0.8f, 0.8f);
    public static final DeferredHolder<EntityType<?>, EntityType<OneUpMushroomEntity>> ONE_UP_MUSHROOM = register("one_up_mushroom", OneUpMushroomEntity::new,
            MobCategory.AMBIENT, 0.8f, 0.8f);
    public static final DeferredHolder<EntityType<?>, EntityType<SuperStarEntity>> SUPER_STAR = Marioverse.ENTITIES.register("super_star", () -> EntityType.Builder.of(SuperStarEntity::new, MobCategory.AMBIENT)
            .sized(0.8F, 0.8F).eyeHeight(0.625F).fireImmune().build("super_star"));

    public static final DeferredHolder<EntityType<?>, EntityType<FireGoombaEntity>> FIRE_GOOMBA =
            Marioverse.ENTITIES.register("fire_goomba", () -> EntityType.Builder.of(FireGoombaEntity::new, MobCategory.MONSTER)
                    .sized(0.625F, 0.8F).eyeHeight(0.75F).ridingOffset(0.075F).fireImmune().build("fire_goomba"));
    public static final DeferredHolder<EntityType<?>, EntityType<HeftyGoombaEntity>> HEFTY_GOOMBA =
            Marioverse.ENTITIES.register("hefty_goomba", () -> EntityType.Builder.of(HeftyGoombaEntity::new, MobCategory.MONSTER)
                    .sized(1.4375F, 1.625F).eyeHeight(1.3F).ridingOffset(0.075F).build("hefty_goomba"));
    public static final DeferredHolder<EntityType<?>, EntityType<GoombaEntity>> GOOMBA =
            Marioverse.ENTITIES.register("goomba", () -> EntityType.Builder.of(GoombaEntity::new, MobCategory.MONSTER)
                    .sized(0.625F, 0.8F).eyeHeight(0.75F).ridingOffset(0.075F).build("goomba"));
    public static final DeferredHolder<EntityType<?>, EntityType<KoopaShellEntity>> GREEN_KOOPA_SHELL =
            Marioverse.ENTITIES.register("green_koopa_shell", () -> EntityType.Builder.of(KoopaShellEntity::new, MobCategory.MONSTER)
                    .sized(0.7F, 0.7F).eyeHeight(0.6F).build("green_koopa_shell"));
    public static final DeferredHolder<EntityType<?>, EntityType<KoopaTroopaEntity>> GREEN_KOOPA_TROOPA =
            Marioverse.ENTITIES.register("green_koopa_troopa", () -> EntityType.Builder.of(KoopaTroopaEntity::new, MobCategory.MONSTER)
                    .sized(1.0F, 1.65F).eyeHeight(1.4F).build("green_koopa_troopa"));
    public static final DeferredHolder<EntityType<?>, EntityType<MegaGoombaEntity>> MEGA_GOOMBA =
            Marioverse.ENTITIES.register("mega_goomba", () -> EntityType.Builder.of(MegaGoombaEntity::new, MobCategory.MONSTER)
                    .sized(2.875F, 3.25F).eyeHeight(2.625F).ridingOffset(0.125F).build("mega_goomba"));
    public static final DeferredHolder<EntityType<?>, EntityType<MiniGoombaEntity>> MINI_GOOMBA =
            Marioverse.ENTITIES.register("mini_goomba", () -> EntityType.Builder.of(MiniGoombaEntity::new, MobCategory.MONSTER)
                    .sized(0.25F, 0.3125F).eyeHeight(0.3125F).ridingOffset(0.1F)
                    .nameTagOffset(-0.05F).build("mini_goomba"));
    public static final DeferredHolder<EntityType<?>, EntityType<PiranhaPlantEntity>> PIRANHA_PLANT =
            Marioverse.ENTITIES.register("piranha_plant", () -> EntityType.Builder.of(PiranhaPlantEntity::new, MobCategory.MONSTER)
                    .sized(1.0F, 2.3125F).eyeHeight(2.0F).ridingOffset(0.1F).build("piranha_plant"));

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(EntityRegistry.FIRE_GOOMBA.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, FireGoombaEntity::checkFireGoombaSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(EntityRegistry.GOOMBA.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GoombaEntity::checkGoombaSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(EntityRegistry.HEFTY_GOOMBA.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GoombaEntity::checkGoombaSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(EntityRegistry.MEGA_GOOMBA.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GoombaEntity::checkGoombaSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(EntityRegistry.MINI_GOOMBA.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GoombaEntity::checkGoombaSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(EntityRegistry.PIRANHA_PLANT.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING, PiranhaPlantEntity::checkPiranhaPlantSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
    }

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        AttributeSupplier.Builder mushroomAttributes = PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1)
                .add(Attributes.MOVEMENT_SPEED, 0.4F)
                .add(Attributes.SAFE_FALL_DISTANCE, 10.0F)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.3F);
        AttributeSupplier.Builder piranhaPlantAttributes = PathfinderMob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 5.0F)
                .add(Attributes.JUMP_STRENGTH, 0.0F)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.MAX_HEALTH, 20.0F)
                .add(Attributes.MOVEMENT_SPEED, 0.0F)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.0F);
        AttributeSupplier.Builder powerUpAttributes = PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.3F);
        AttributeSupplier.Builder starAttributes = PathfinderMob.createMobAttributes()
                .add(Attributes.JUMP_STRENGTH, 0.5F)
                .add(Attributes.MAX_HEALTH, 1)
                .add(Attributes.MOVEMENT_SPEED, 0.8F)
                .add(Attributes.SAFE_FALL_DISTANCE, 10.0F)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.3F);

        event.put(EntityRegistry.FIRE_FLOWER.get(), powerUpAttributes.build());
        event.put(EntityRegistry.ICE_FLOWER.get(), powerUpAttributes.build());
        event.put(EntityRegistry.MUSHROOM.get(), mushroomAttributes.build());
        event.put(EntityRegistry.ONE_UP_MUSHROOM.get(), mushroomAttributes.build());
        event.put(EntityRegistry.PIRANHA_PLANT.get(), piranhaPlantAttributes.build());
        event.put(EntityRegistry.SUPER_STAR.get(), starAttributes.build());

        event.put(EntityRegistry.FIRE_GOOMBA.get(), PathfinderMob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.5F)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5F)
                .add(Attributes.ATTACK_SPEED, 0.8F)
                .add(Attributes.FOLLOW_RANGE, 8.0F)
                .add(Attributes.MAX_HEALTH, 6)
                .add(Attributes.MOVEMENT_SPEED, 0.4F)
                .add(Attributes.SAFE_FALL_DISTANCE, 10.0F).build());

        event.put(EntityRegistry.HEFTY_GOOMBA.get(), PathfinderMob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 3.0F)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5F)
                .add(Attributes.ATTACK_SPEED, 1.0F)
                .add(Attributes.FOLLOW_RANGE, 12.0F)
                .add(Attributes.MAX_HEALTH, 12)
                .add(Attributes.MOVEMENT_SPEED, 0.6F)
                .add(Attributes.SAFE_FALL_DISTANCE, 9.0F).build());

        event.put(EntityRegistry.GOOMBA.get(), PathfinderMob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.0F)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0F)
                .add(Attributes.ATTACK_SPEED, 1.0F)
                .add(Attributes.FOLLOW_RANGE, 8.0F)
                .add(Attributes.MAX_HEALTH, 4)
                .add(Attributes.MOVEMENT_SPEED, 0.4F)
                .add(Attributes.SAFE_FALL_DISTANCE, 10.0F).build());

        event.put(EntityRegistry.GREEN_KOOPA_SHELL.get(), PathfinderMob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.2F)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0F)
                .add(Attributes.ATTACK_SPEED, 1.0F)
                .add(Attributes.FOLLOW_RANGE, 8.0F)
                .add(Attributes.GRAVITY, 0.8F)
                .add(Attributes.MAX_HEALTH, 8)
                .add(Attributes.MOVEMENT_SPEED, 0.5F)
                .add(Attributes.SAFE_FALL_DISTANCE, 8.0F)
                .add(Attributes.STEP_HEIGHT, 0.5F).build());

        event.put(EntityRegistry.GREEN_KOOPA_TROOPA.get(), PathfinderMob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.2F)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0F)
                .add(Attributes.ATTACK_SPEED, 1.0F)
                .add(Attributes.FOLLOW_RANGE, 8.0F)
                .add(Attributes.MAX_HEALTH, 8)
                .add(Attributes.MOVEMENT_SPEED, 0.5F)
                .add(Attributes.SAFE_FALL_DISTANCE, 8.0F).build());

        event.put(EntityRegistry.MEGA_GOOMBA.get(), PathfinderMob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 5.0F)
                .add(Attributes.ATTACK_KNOCKBACK, 1.2F)
                .add(Attributes.ATTACK_SPEED, 1.0F)
                .add(Attributes.FOLLOW_RANGE, 16.0F)
                .add(Attributes.MAX_HEALTH, 16)
                .add(Attributes.MOVEMENT_SPEED, 0.8F)
                .add(Attributes.SAFE_FALL_DISTANCE, 8.0F).build());

        event.put(EntityRegistry.MINI_GOOMBA.get(), PathfinderMob.createMobAttributes()
                .add(Attributes.ATTACK_KNOCKBACK, 0.0F)
                .add(Attributes.ATTACK_SPEED, 0.0F)
                .add(Attributes.FOLLOW_RANGE, 4.0F)
                .add(Attributes.MAX_HEALTH, 1)
                .add(Attributes.MOVEMENT_SPEED, 0.2F)
                .add(Attributes.SAFE_FALL_DISTANCE, 12.0F).build());
    }

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String name, EntityType.EntityFactory<T> entity, MobCategory category,
                                                                                              float width, float height) {
        return Marioverse.ENTITIES.register(name, () -> EntityType.Builder.of(entity, category).sized(width, height).build(name));
    }

    public static void init() {}
}
