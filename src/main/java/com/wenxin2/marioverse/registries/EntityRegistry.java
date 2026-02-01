package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.BooEntity;
import com.wenxin2.marioverse.entities.DryBonesEntity;
import com.wenxin2.marioverse.entities.DryBonesPartEntity;
import com.wenxin2.marioverse.entities.FireGoombaEntity;
import com.wenxin2.marioverse.entities.GoldKoopaShellEntity;
import com.wenxin2.marioverse.entities.GoldKoopaTroopaEntity;
import com.wenxin2.marioverse.entities.GoombaEntity;
import com.wenxin2.marioverse.entities.GreenKoopaTroopaEntity;
import com.wenxin2.marioverse.entities.HeftyGoombaEntity;
import com.wenxin2.marioverse.entities.IceCubeEntity;
import com.wenxin2.marioverse.entities.KoopaShellEntity;
import com.wenxin2.marioverse.entities.KoopaTroopaEntity;
import com.wenxin2.marioverse.entities.MegaGoombaEntity;
import com.wenxin2.marioverse.entities.MiniGoombaEntity;
import com.wenxin2.marioverse.entities.PiranhaPlantEntity;
import com.wenxin2.marioverse.entities.PokeyBodyEntity;
import com.wenxin2.marioverse.entities.PokeyEntity;
import com.wenxin2.marioverse.entities.RedKoopaShellEntity;
import com.wenxin2.marioverse.entities.RedKoopaTroopaEntity;
import com.wenxin2.marioverse.entities.SnowPokeyBodyEntity;
import com.wenxin2.marioverse.entities.SnowPokeyEntity;
import com.wenxin2.marioverse.entities.SplunkinEntity;
import com.wenxin2.marioverse.entities.power_ups.FireFlowerEntity;
import com.wenxin2.marioverse.entities.power_ups.IceFlowerEntity;
import com.wenxin2.marioverse.entities.power_ups.MegaMushroomEntity;
import com.wenxin2.marioverse.entities.power_ups.MiniMushroomEntity;
import com.wenxin2.marioverse.entities.power_ups.MushroomEntity;
import com.wenxin2.marioverse.entities.power_ups.OneUpMushroomEntity;
import com.wenxin2.marioverse.entities.power_ups.SuperMushroomEntity;
import com.wenxin2.marioverse.entities.power_ups.SuperStarEntity;
import com.wenxin2.marioverse.entities.projectiles.BouncingFireballProjectile;
import com.wenxin2.marioverse.entities.projectiles.BouncingIceBallProjectile;
import com.wenxin2.marioverse.entities.projectiles.LargeSnowballProjectile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

@EventBusSubscriber(modid = Marioverse.MOD_ID)
public class EntityRegistry {
    public static final DeferredHolder<EntityType<?>, EntityType<BouncingFireballProjectile>> BOUNCING_FIREBALL =
            register("bouncing_fireball", BouncingFireballProjectile::new, MobCategory.MISC, 0.3f, 0.3f);
    public static final DeferredHolder<EntityType<?>, EntityType<BouncingIceBallProjectile>> BOUNCING_ICE_BALL =
            register("bouncing_ice_ball", BouncingIceBallProjectile::new, MobCategory.MISC, 0.5f, 0.5f);
    public static final DeferredHolder<EntityType<?>, EntityType<LargeSnowballProjectile>> LARGE_SNOWBALL =
            register("large_snowball", LargeSnowballProjectile::new, MobCategory.MISC, 0.75F, 0.75F);
    public static final DeferredHolder<EntityType<?>, EntityType<IceCubeEntity>> ICE_CUBE =
            Marioverse.ENTITIES.register("ice_cube", () -> EntityType.Builder.of(IceCubeEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F).passengerAttachments(0.5F).build("ice_cube"));

    public static final DeferredHolder<EntityType<?>, EntityType<FireFlowerEntity>> FIRE_FLOWER =
            Marioverse.ENTITIES.register("fire_flower", () -> EntityType.Builder.of(FireFlowerEntity::new, MobCategory.AMBIENT)
                    .sized(0.6F, 0.6F).build("fire_flower"));
    public static final DeferredHolder<EntityType<?>, EntityType<IceFlowerEntity>> ICE_FLOWER =
            Marioverse.ENTITIES.register("ice_flower", () -> EntityType.Builder.of(IceFlowerEntity::new, MobCategory.AMBIENT)
                    .sized(0.6F, 0.6F).build("ice_flower"));
    public static final DeferredHolder<EntityType<?>, EntityType<MegaMushroomEntity>> MEGA_MUSHROOM =
            Marioverse.ENTITIES.register("mega_mushroom", () -> EntityType.Builder.of(MegaMushroomEntity::new, MobCategory.AMBIENT)
                    .sized(1.875F, 1.75F).build("mega_mushroom"));
    public static final DeferredHolder<EntityType<?>, EntityType<MiniMushroomEntity>> MINI_MUSHROOM =
            Marioverse.ENTITIES.register("mini_mushroom", () -> EntityType.Builder.of(MiniMushroomEntity::new, MobCategory.AMBIENT)
                    .sized(0.5F, 0.5F).build("mini_mushroom"));
    public static final DeferredHolder<EntityType<?>, EntityType<OneUpMushroomEntity>> ONE_UP_MUSHROOM =
            Marioverse.ENTITIES.register("one_up_mushroom", () -> EntityType.Builder.of(OneUpMushroomEntity::new, MobCategory.AMBIENT)
                    .sized(0.8F, 0.8F).build("one_up_mushroom"));
    public static final DeferredHolder<EntityType<?>, EntityType<SuperMushroomEntity>> SUPER_MUSHROOM =
            Marioverse.ENTITIES.register("super_mushroom", () -> EntityType.Builder.of(SuperMushroomEntity::new, MobCategory.AMBIENT)
                    .sized(0.8F, 0.8F).build("super_mushroom"));
    public static final DeferredHolder<EntityType<?>, EntityType<SuperStarEntity>> SUPER_STAR =
            Marioverse.ENTITIES.register("super_star", () -> EntityType.Builder.of(SuperStarEntity::new, MobCategory.AMBIENT)
                    .sized(0.8F, 0.8F).eyeHeight(0.625F).fireImmune().build("super_star"));

    public static final DeferredHolder<EntityType<?>, EntityType<DryBonesPartEntity>> DRY_BONES_HEAD =
            Marioverse.ENTITIES.register("dry_bones_head", () -> EntityType.Builder.of(DryBonesPartEntity::new, MobCategory.MONSTER)
                    .sized(0.8125F, 0.5625F).fireImmune().build("dry_bones_head"));
    public static final DeferredHolder<EntityType<?>, EntityType<DryBonesPartEntity>> DRY_BONES_SHELL =
            Marioverse.ENTITIES.register("dry_bones_shell", () -> EntityType.Builder.of(DryBonesPartEntity::new, MobCategory.MONSTER)
                    .sized(0.7F, 0.7F).fireImmune().build("dry_bones_shell"));
    public static final DeferredHolder<EntityType<?>, EntityType<DryBonesPartEntity>> DRY_BONES_TAIL =
            Marioverse.ENTITIES.register("dry_bones_tail", () -> EntityType.Builder.of(DryBonesPartEntity::new, MobCategory.MONSTER)
                    .sized(0.3125F, 0.3125F).fireImmune().build("dry_bones_tail"));
    public static final DeferredHolder<EntityType<?>, EntityType<DryBonesPartEntity>> DRY_BONES_LEFT_ARM =
            Marioverse.ENTITIES.register("dry_bones_left_arm", () -> EntityType.Builder.of(DryBonesPartEntity::new, MobCategory.MONSTER)
                    .sized(0.5F, 0.3125F).fireImmune().build("dry_bones_left_arm"));
    public static final DeferredHolder<EntityType<?>, EntityType<DryBonesPartEntity>> DRY_BONES_RIGHT_ARM =
            Marioverse.ENTITIES.register("dry_bones_right_arm", () -> EntityType.Builder.of(DryBonesPartEntity::new, MobCategory.MONSTER)
                    .sized(0.5F, 0.3125F).fireImmune().build("dry_bones_right_arm"));
    public static final DeferredHolder<EntityType<?>, EntityType<DryBonesPartEntity>> DRY_BONES_LEFT_LEG =
            Marioverse.ENTITIES.register("dry_bones_left_leg", () -> EntityType.Builder.of(DryBonesPartEntity::new, MobCategory.MONSTER)
                    .sized(0.5F, 0.3125F).fireImmune().build("dry_bones_left_leg"));
    public static final DeferredHolder<EntityType<?>, EntityType<DryBonesPartEntity>> DRY_BONES_RIGHT_LEG =
            Marioverse.ENTITIES.register("dry_bones_right_leg", () -> EntityType.Builder.of(DryBonesPartEntity::new, MobCategory.MONSTER)
                    .sized(0.5F, 0.3125F).fireImmune().build("dry_bones_right_leg"));

    public static final DeferredHolder<EntityType<?>, EntityType<BooEntity>> BOO =
            Marioverse.ENTITIES.register("boo", () -> EntityType.Builder.of(BooEntity::new, MobCategory.MONSTER)
                    .sized(0.6875F, 0.6875F).ridingOffset(0.1F).fireImmune().build("boo"));
    public static final DeferredHolder<EntityType<?>, EntityType<DryBonesEntity>> DRY_BONES =
            Marioverse.ENTITIES.register("dry_bones", () -> EntityType.Builder.of(DryBonesEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 1.65F).ridingOffset(0.1F).fireImmune().build("dry_bones"));
    public static final DeferredHolder<EntityType<?>, EntityType<FireGoombaEntity>> FIRE_GOOMBA =
            Marioverse.ENTITIES.register("fire_goomba", () -> EntityType.Builder.of(FireGoombaEntity::new, MobCategory.MONSTER)
                    .sized(0.625F, 0.8F).eyeHeight(0.75F).ridingOffset(0.075F).fireImmune().build("fire_goomba"));
    public static final DeferredHolder<EntityType<?>, EntityType<HeftyGoombaEntity>> HEFTY_GOOMBA =
            Marioverse.ENTITIES.register("hefty_goomba", () -> EntityType.Builder.of(HeftyGoombaEntity::new, MobCategory.MONSTER)
                    .sized(1.4375F, 1.625F).eyeHeight(1.3F).ridingOffset(0.075F).build("hefty_goomba"));
    public static final DeferredHolder<EntityType<?>, EntityType<GoombaEntity>> GOOMBA =
            Marioverse.ENTITIES.register("goomba", () -> EntityType.Builder.of(GoombaEntity::new, MobCategory.MONSTER)
                    .sized(0.625F, 0.8F).eyeHeight(0.75F).ridingOffset(0.075F).build("goomba"));
    public static final DeferredHolder<EntityType<?>, EntityType<GoldKoopaShellEntity>> GOLD_KOOPA_SHELL =
            Marioverse.ENTITIES.register("gold_koopa_shell", () -> EntityType.Builder.of(GoldKoopaShellEntity::new, MobCategory.AMBIENT)
                    .sized(0.7F, 0.7F).eyeHeight(0.6F).ridingOffset(-0.075F).build("gold_koopa_shell"));
    public static final DeferredHolder<EntityType<?>, EntityType<GoldKoopaTroopaEntity>> GOLD_KOOPA_TROOPA =
            Marioverse.ENTITIES.register("gold_koopa_troopa", () -> EntityType.Builder.of(GoldKoopaTroopaEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 1.65F).eyeHeight(1.4F).build("gold_koopa_troopa"));
    public static final DeferredHolder<EntityType<?>, EntityType<KoopaShellEntity>> GREEN_KOOPA_SHELL =
            Marioverse.ENTITIES.register("green_koopa_shell", () -> EntityType.Builder.of(KoopaShellEntity::new, MobCategory.AMBIENT)
                    .sized(0.7F, 0.7F).eyeHeight(0.6F).ridingOffset(-0.075F).build("green_koopa_shell"));
    public static final DeferredHolder<EntityType<?>, EntityType<GreenKoopaTroopaEntity>> GREEN_KOOPA_TROOPA =
            Marioverse.ENTITIES.register("green_koopa_troopa", () -> EntityType.Builder.of(GreenKoopaTroopaEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 1.65F).eyeHeight(1.4F).build("green_koopa_troopa"));
    public static final DeferredHolder<EntityType<?>, EntityType<MegaGoombaEntity>> MEGA_GOOMBA =
            Marioverse.ENTITIES.register("mega_goomba", () -> EntityType.Builder.of(MegaGoombaEntity::new, MobCategory.MONSTER)
                    .sized(2.875F, 3.25F).eyeHeight(2.625F).ridingOffset(0.125F).build("mega_goomba"));
    public static final DeferredHolder<EntityType<?>, EntityType<MiniGoombaEntity>> MINI_GOOMBA =
            Marioverse.ENTITIES.register("mini_goomba", () -> EntityType.Builder.of(MiniGoombaEntity::new, MobCategory.MONSTER)
                    .sized(0.25F, 0.3125F).eyeHeight(0.3125F).ridingOffset(0.1F)
                    .nameTagOffset(-0.05F).build("mini_goomba"));
    public static final DeferredHolder<EntityType<?>, EntityType<PiranhaPlantEntity>> PIRANHA_PLANT =
            Marioverse.ENTITIES.register("piranha_plant", () -> EntityType.Builder.of(PiranhaPlantEntity::new, MobCategory.MONSTER)
                    .sized(1.0F, 2.3125F).eyeHeight(0.8F).ridingOffset(0.1F).build("piranha_plant"));
    public static final DeferredHolder<EntityType<?>, EntityType<PokeyEntity>> POKEY =
            Marioverse.ENTITIES.register("pokey", () -> EntityType.Builder.of(PokeyEntity::new, MobCategory.MONSTER)
                    .sized(0.75F, 0.75F).eyeHeight(0.5F).build("pokey"));
    public static final DeferredHolder<EntityType<?>, EntityType<PokeyBodyEntity>> POKEY_BODY =
            Marioverse.ENTITIES.register("pokey_body", () -> EntityType.Builder.of(PokeyBodyEntity::new, MobCategory.MONSTER)
                    .sized(0.75F, 0.75F).eyeHeight(0.5F).build("pokey_body"));
    public static final DeferredHolder<EntityType<?>, EntityType<RedKoopaShellEntity>> RED_KOOPA_SHELL =
            Marioverse.ENTITIES.register("red_koopa_shell", () -> EntityType.Builder.of(RedKoopaShellEntity::new, MobCategory.AMBIENT)
                    .sized(0.7F, 0.7F).eyeHeight(0.6F).ridingOffset(-0.075F).build("red_koopa_shell"));
    public static final DeferredHolder<EntityType<?>, EntityType<RedKoopaTroopaEntity>> RED_KOOPA_TROOPA =
            Marioverse.ENTITIES.register("red_koopa_troopa", () -> EntityType.Builder.of(RedKoopaTroopaEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 1.65F).eyeHeight(1.4F).build("red_koopa_troopa"));
    public static final DeferredHolder<EntityType<?>, EntityType<SnowPokeyEntity>> SNOW_POKEY =
            Marioverse.ENTITIES.register("snow_pokey", () -> EntityType.Builder.of(SnowPokeyEntity::new, MobCategory.MONSTER)
                    .sized(0.75F, 0.75F).eyeHeight(0.5F).build("snow_pokey"));
    public static final DeferredHolder<EntityType<?>, EntityType<SnowPokeyBodyEntity>> SNOW_POKEY_BODY =
            Marioverse.ENTITIES.register("snow_pokey_body", () -> EntityType.Builder.of(SnowPokeyBodyEntity::new, MobCategory.MONSTER)
                    .sized(0.75F, 0.75F).eyeHeight(0.5F).build("snow_pokey_body"));
    public static final DeferredHolder<EntityType<?>, EntityType<SplunkinEntity>> SPLUNKIN =
            Marioverse.ENTITIES.register("splunkin", () -> EntityType.Builder.of(SplunkinEntity::new, MobCategory.MONSTER)
                    .sized(0.875F, 0.875F).ridingOffset(0.075F).build("splunkin"));

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(EntityRegistry.BOO.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING, BooEntity::checkBooSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(EntityRegistry.DRY_BONES.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DryBonesEntity::checkDryBonesSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
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
        event.register(EntityRegistry.POKEY.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, PokeyEntity::checkPokeySpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(EntityRegistry.POKEY_BODY.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, PokeyEntity::checkPokeySpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(EntityRegistry.GREEN_KOOPA_TROOPA.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, KoopaTroopaEntity::checkKoopaSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(EntityRegistry.RED_KOOPA_TROOPA.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, KoopaTroopaEntity::checkKoopaSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(EntityRegistry.GOLD_KOOPA_TROOPA.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, KoopaTroopaEntity::checkKoopaSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(EntityRegistry.SNOW_POKEY.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SnowPokeyEntity::checkSnowPokeySpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(EntityRegistry.SNOW_POKEY_BODY.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SnowPokeyEntity::checkSnowPokeySpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(EntityRegistry.SPLUNKIN.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SplunkinEntity::checkSplunkinSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
    }

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        AttributeSupplier.Builder dryBonesAttributes = Monster.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.3F)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0F)
                .add(Attributes.MAX_HEALTH, 12)
                .add(Attributes.MOVEMENT_SPEED, 0.35F)
                .add(Attributes.SAFE_FALL_DISTANCE, 8.0F);
        AttributeSupplier.Builder dryBonesPartAttributes = Monster.createMobAttributes()
                .add(Attributes.ATTACK_SPEED, 0.0F)
                .add(Attributes.MOVEMENT_SPEED, 0.0F)
                .add(Attributes.MAX_HEALTH, 2)
                .add(Attributes.SAFE_FALL_DISTANCE, 16.0F);
        AttributeSupplier.Builder dryBonesHeadAttributes = Monster.createMobAttributes()
                .add(Attributes.ATTACK_SPEED, 0.0F)
                .add(Attributes.MOVEMENT_SPEED, 0.0F)
                .add(Attributes.MAX_HEALTH, 10)
                .add(Attributes.SAFE_FALL_DISTANCE, 16.0F);
        AttributeSupplier.Builder koopaAttributes = Monster.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.2F)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0F)
                .add(Attributes.MAX_HEALTH, 8)
                .add(Attributes.MOVEMENT_SPEED, 0.3F)
                .add(Attributes.SAFE_FALL_DISTANCE, 8.0F);
        AttributeSupplier.Builder koopaShellAttributes = Monster.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.2F)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0F)
                .add(Attributes.ATTACK_SPEED, 0.0F)
                .add(Attributes.GRAVITY, 0.08F)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0F)
                .add(Attributes.MAX_HEALTH, 8)
                .add(Attributes.MOVEMENT_SPEED, 0.0F)
                .add(Attributes.SAFE_FALL_DISTANCE, 8.0F)
                .add(Attributes.STEP_HEIGHT, 0.6F);
        AttributeSupplier.Builder redKoopaShellAttributes = Monster.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.2F)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0F)
                .add(Attributes.ATTACK_SPEED, 1.0F)
                .add(Attributes.GRAVITY, 0.08F)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0F)
                .add(Attributes.MAX_HEALTH, 8)
                .add(Attributes.MOVEMENT_SPEED, 0.0F)
                .add(Attributes.SAFE_FALL_DISTANCE, 8.0F)
                .add(Attributes.STEP_HEIGHT, 1.1F);
        AttributeSupplier.Builder piranhaPlantAttributes = PathfinderMob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 5.0F)
                .add(Attributes.JUMP_STRENGTH, 0.0F)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.MAX_HEALTH, 20.0F)
                .add(Attributes.MOVEMENT_SPEED, 0.0F)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.0F);
        AttributeSupplier.Builder pokeyAttributes = Monster.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.0F)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0F)
                .add(Attributes.ATTACK_SPEED, 1.0F)
                .add(Attributes.MAX_HEALTH, 8)
                .add(Attributes.MOVEMENT_SPEED, 0.1F)
                .add(Attributes.SAFE_FALL_DISTANCE, 10.0F);
        AttributeSupplier.Builder mushroomAttributes = PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6)
                .add(Attributes.MOVEMENT_SPEED, 0.4F)
                .add(Attributes.SAFE_FALL_DISTANCE, 10.0F)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.3F);
        AttributeSupplier.Builder megaMushroomAttributes = PathfinderMob.createMobAttributes()
                .add(Attributes.JUMP_STRENGTH, 0.55F)
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.MOVEMENT_SPEED, 0.5F)
                .add(Attributes.SAFE_FALL_DISTANCE, 20.0F)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.2F);
        AttributeSupplier.Builder miniMushroomAttributes = PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 2)
                .add(Attributes.MOVEMENT_SPEED, 0.3F)
                .add(Attributes.SAFE_FALL_DISTANCE, 10.0F)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.4F);
        AttributeSupplier.Builder powerUpAttributes = PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 2)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.3F);
        AttributeSupplier.Builder starAttributes = PathfinderMob.createMobAttributes()
                .add(Attributes.JUMP_STRENGTH, 0.5F)
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.MOVEMENT_SPEED, 0.8F)
                .add(Attributes.SAFE_FALL_DISTANCE, 20.0F)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.3F);

        event.put(EntityRegistry.FIRE_FLOWER.get(), powerUpAttributes.build());
        event.put(EntityRegistry.ICE_FLOWER.get(), powerUpAttributes.build());
        event.put(EntityRegistry.MEGA_MUSHROOM.get(), megaMushroomAttributes.build());
        event.put(EntityRegistry.MINI_MUSHROOM.get(), miniMushroomAttributes.build());
        event.put(EntityRegistry.ONE_UP_MUSHROOM.get(), mushroomAttributes.build());
        event.put(EntityRegistry.SUPER_MUSHROOM.get(), mushroomAttributes.build());
        event.put(EntityRegistry.SUPER_STAR.get(), starAttributes.build());

        event.put(EntityRegistry.DRY_BONES_HEAD.get(), dryBonesHeadAttributes.build());
        event.put(EntityRegistry.DRY_BONES_SHELL.get(), dryBonesHeadAttributes.build());
        event.put(EntityRegistry.DRY_BONES_TAIL.get(), dryBonesPartAttributes.build());
        event.put(EntityRegistry.DRY_BONES_LEFT_ARM.get(), dryBonesPartAttributes.build());
        event.put(EntityRegistry.DRY_BONES_LEFT_LEG.get(), dryBonesPartAttributes.build());
        event.put(EntityRegistry.DRY_BONES_RIGHT_ARM.get(), dryBonesPartAttributes.build());
        event.put(EntityRegistry.DRY_BONES_RIGHT_LEG.get(), dryBonesPartAttributes.build());

        event.put(EntityRegistry.DRY_BONES.get(), dryBonesAttributes.build());
        event.put(EntityRegistry.GOLD_KOOPA_SHELL.get(), redKoopaShellAttributes.build());
        event.put(EntityRegistry.GOLD_KOOPA_TROOPA.get(), koopaAttributes.build());
        event.put(EntityRegistry.GREEN_KOOPA_SHELL.get(), koopaShellAttributes.build());
        event.put(EntityRegistry.GREEN_KOOPA_TROOPA.get(), koopaAttributes.build());
        event.put(EntityRegistry.RED_KOOPA_SHELL.get(), redKoopaShellAttributes.build());
        event.put(EntityRegistry.RED_KOOPA_TROOPA.get(), koopaAttributes.build());

        event.put(EntityRegistry.PIRANHA_PLANT.get(), piranhaPlantAttributes.build());
        event.put(EntityRegistry.POKEY.get(), pokeyAttributes.build());
        event.put(EntityRegistry.POKEY_BODY.get(), pokeyAttributes.build());
        event.put(EntityRegistry.SNOW_POKEY.get(), pokeyAttributes.build());
        event.put(EntityRegistry.SNOW_POKEY_BODY.get(), pokeyAttributes.build());

        event.put(EntityRegistry.ICE_CUBE.get(), PathfinderMob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 0.0F)
                .add(Attributes.MAX_HEALTH, 2.0F).build());

        event.put(EntityRegistry.BOO.get(), Monster.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 2.0F)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5F)
                .add(Attributes.ATTACK_SPEED, 0.8F)
                .add(Attributes.MAX_HEALTH, 10)
                .add(Attributes.MOVEMENT_SPEED, 0.6F).build());

        event.put(EntityRegistry.FIRE_GOOMBA.get(), Monster.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.5F)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5F)
                .add(Attributes.ATTACK_SPEED, 0.8F)
                .add(Attributes.MAX_HEALTH, 6)
                .add(Attributes.MOVEMENT_SPEED, 0.4F)
                .add(Attributes.SAFE_FALL_DISTANCE, 10.0F).build());

        event.put(EntityRegistry.HEFTY_GOOMBA.get(), Monster.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 3.0F)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5F)
                .add(Attributes.ATTACK_SPEED, 0.8F)
                .add(Attributes.MAX_HEALTH, 12)
                .add(Attributes.MOVEMENT_SPEED, 0.6F)
                .add(Attributes.SAFE_FALL_DISTANCE, 9.0F).build());

        event.put(EntityRegistry.GOOMBA.get(), Monster.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.0F)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0F)
                .add(Attributes.ATTACK_SPEED, 1.0F)
                .add(Attributes.FOLLOW_RANGE, 8.0F)
                .add(Attributes.MAX_HEALTH, 4)
                .add(Attributes.MOVEMENT_SPEED, 0.4F)
                .add(Attributes.SAFE_FALL_DISTANCE, 10.0F).build());

        event.put(EntityRegistry.MEGA_GOOMBA.get(), Monster.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 5.0F)
                .add(Attributes.ATTACK_KNOCKBACK, 1.2F)
                .add(Attributes.ATTACK_SPEED, 0.6F)
                .add(Attributes.MAX_HEALTH, 16)
                .add(Attributes.MOVEMENT_SPEED, 0.8F)
                .add(Attributes.SAFE_FALL_DISTANCE, 8.0F).build());

        event.put(EntityRegistry.MINI_GOOMBA.get(), Monster.createMobAttributes()
                .add(Attributes.ATTACK_KNOCKBACK, 0.0F)
                .add(Attributes.ATTACK_SPEED, 0.5F)
                .add(Attributes.FOLLOW_RANGE, 8.0F)
                .add(Attributes.MAX_HEALTH, 1)
                .add(Attributes.MOVEMENT_SPEED, 0.4F)
                .add(Attributes.SAFE_FALL_DISTANCE, 12.0F).build());

        event.put(EntityRegistry.SPLUNKIN.get(), Monster.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.0F)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0F)
                .add(Attributes.ATTACK_SPEED, 1.2F)
                .add(Attributes.MAX_HEALTH, 8)
                .add(Attributes.MOVEMENT_SPEED, 0.6F).build());
    }

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String name, EntityType.EntityFactory<T> entity,
            MobCategory category, float width, float height) {
        return Marioverse.ENTITIES.register(name, () -> EntityType.Builder.of(entity, category).sized(width, height).build(name));
    }

    public static void init() {
    }
}
