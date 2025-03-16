package com.wenxin2.marioverse.entities.projectiles;

import com.wenxin2.marioverse.entities.IceCubeEntity;
import com.wenxin2.marioverse.entities.MiniGoombaEntity;
import com.wenxin2.marioverse.entities.part_entities.PiranhaPlantPart;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.DamageTypeRegistry;
import com.wenxin2.marioverse.init.EntityRegistry;
import com.wenxin2.marioverse.init.ParticleRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BouncingIceBallProjectile extends ThrowableProjectile implements GeoEntity {
    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.bouncing_ice_ball.idle");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private final String BOUNCE_COUNT = "marioverse:ice_ball_bounce_count";

    public BouncingIceBallProjectile(EntityType<? extends BouncingIceBallProjectile> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Walk", 0, state -> state.setAndContinue(IDLE)));
    }

    @Override
    protected boolean updateInWaterStateAndDoFluidPushing() {
        return false;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void tick() {
        super.tick();
        Level world = this.level();
        Vec3 motion = this.getDeltaMovement();

        if (!this.getPersistentData().contains(BOUNCE_COUNT))
            this.getPersistentData().putInt(BOUNCE_COUNT, 0);

        if (!this.isInWater())
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.04D, 0)); // Gravity
        else this.setDeltaMovement(this.getDeltaMovement().add(motion.x, -0.04D, motion.y)); // Gravity

        if (motion.length() > 0) {
            this.setYRot((float) Math.toDegrees(Math.atan2(motion.z, motion.x)) + 270);
            this.setXRot((float) Math.toDegrees(Math.atan2(-motion.y, Math.sqrt(motion.x * motion.x + motion.z * motion.z))));
        }

        if (this.onGround() || this.tickCount > 400) {
            if (!world.isClientSide) {
                world.broadcastEntityEvent(this, (byte) 60); // Smoke particle
            }
            world.playSound(null, this.blockPosition(), SoundRegistry.ICE_BALL_SHATTERED.get(),
                    SoundSource.AMBIENT, 1.0F, 1.0F);
            world.gameEvent(this.getOwner(), GameEvent.PROJECTILE_LAND, this.position());
            this.discard(); // Despawn
        }

        for (int i = 0; i < 1; i++) {
            double x = this.getX();
            double y = this.getY() + this.getBbHeight() / 2;
            double z = this.getZ();
            world.addParticle(ParticleTypes.SNOWFLAKE, x, y, z, 0, 0, 0);
            world.addParticle(ParticleRegistry.ICE_STAR.get(), x, y, z, 0.2, 0, 0.2);
        }
    }

    @Override
    public boolean isPushedByFluid(FluidType type) {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public void onHitBlock(BlockHitResult hit) {
        Level world = this.level();
        BlockPos hitPos = hit.getBlockPos();
        BlockState state = world.getBlockState(hitPos);
        BlockState stateAbove = world.getBlockState(hitPos.above());
        FluidState fluidState = world.getFluidState(hitPos.above());
        FluidState fluidStateAbove = world.getFluidState(hitPos.above(2));

        if (hit.getDirection().getAxis() == Direction.Axis.X || hit.getDirection().getAxis() == Direction.Axis.Z) {
            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnEntityRingParticles(ParticleTypes.SNOWFLAKE, serverWorld, this, this.getBbWidth() / 2, 10);
            world.playSound(null, this.blockPosition(), SoundRegistry.ICE_BALL_SHATTERED.get(),
                    SoundSource.AMBIENT, 1.0F, 1.0F);
            world.gameEvent(this.getOwner(), GameEvent.PROJECTILE_LAND, hitPos);
            this.remove(RemovalReason.DISCARDED); // Despawn on side hit
        } else if (this.getPersistentData().getInt(BOUNCE_COUNT) < ConfigRegistry.MAX_ICE_BALL_BOUNCES.get()) {
            Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x, 0.5, motion.z); // Bounce
            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnEntityRingBelowParticles(ParticleTypes.SNOWFLAKE, serverWorld, this, this.getBbWidth() / 2, 10);
            world.playSound(null, this.blockPosition(), SoundRegistry.ICE_BALL_BOUNCED.get(),
                    SoundSource.AMBIENT, 1.0F, 1.0F);

            if (this.getPersistentData().contains(BOUNCE_COUNT))
                this.getPersistentData().putInt(BOUNCE_COUNT, this.getPersistentData().getInt(BOUNCE_COUNT) + 1);
        } else {
            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnEntityRingParticles(ParticleTypes.SNOWFLAKE, serverWorld, this, this.getBbWidth() / 2, 10);
            world.playSound(null, this.blockPosition(), SoundRegistry.ICE_BALL_SHATTERED.get(),
                    SoundSource.AMBIENT, 1.0F, 1.0F);
            world.gameEvent(this.getOwner(), GameEvent.PROJECTILE_LAND, hitPos);
            this.remove(RemovalReason.DISCARDED);
        }

        if (fluidState.getType().is(TagRegistry.FREEZES_INTO_FROSTED_ICE)) {
            if (fluidState.getType() == Fluids.WATER && fluidStateAbove.getType() != Fluids.WATER && stateAbove.canBeReplaced())
                world.setBlock(hitPos.above(), Blocks.FROSTED_ICE.defaultBlockState(), 3);
            else if (fluidState.getType() != Fluids.WATER)
                world.setBlock(hitPos.above(), Blocks.ICE.defaultBlockState(), 3);
        } else if (fluidState.getType().is(TagRegistry.FREEZES_INTO_OBSIDIAN)) {
            world.setBlock(hitPos.above(), Blocks.OBSIDIAN.defaultBlockState(), 3);
        } else if (state.is(TagRegistry.FREEZES_INTO_PACKED_ICE))
            world.setBlock(hitPos, Blocks.PACKED_ICE.defaultBlockState(), 3);
        else if (state.is(TagRegistry.MELTS_INTO_PACKED_ICE))
            world.setBlock(hitPos, Blocks.PACKED_ICE.defaultBlockState(), 3);
        else if (state.getBlock() instanceof CampfireBlock && state.hasProperty(BlockStateProperties.LIT)) {
            world.levelEvent(null, 1009, hitPos, 0);
            world.setBlock(hitPos, state.setValue(BlockStateProperties.LIT, Boolean.FALSE), 3);
            world.playSound(null, hitPos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnEntityRingParticles(ParticleTypes.SNOWFLAKE, serverWorld, this, this.getBbWidth() / 2, 10);
            world.gameEvent(this.getOwner(), GameEvent.PROJECTILE_LAND, hitPos);
            this.remove(RemovalReason.DISCARDED);
        }
        else if (state.getBlock() instanceof CandleBlock && state.hasProperty(BlockStateProperties.LIT)) {
            world.levelEvent(null, 1009, hitPos, 0);
            world.setBlock(hitPos, state.setValue(BlockStateProperties.LIT, Boolean.FALSE), 3);
            world.playSound(null, hitPos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnEntityRingParticles(ParticleTypes.SNOWFLAKE, serverWorld, this, this.getBbWidth() / 2, 10);
            world.gameEvent(this.getOwner(), GameEvent.PROJECTILE_LAND, hitPos);
            this.remove(RemovalReason.DISCARDED);
        }
        else if (state.getBlock() instanceof CandleCakeBlock && state.hasProperty(BlockStateProperties.LIT)) {
            world.levelEvent(null, 1009, hitPos, 0);
            world.setBlock(hitPos, state.setValue(BlockStateProperties.LIT, Boolean.FALSE), 3);
            world.playSound(null, hitPos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnEntityRingParticles(ParticleTypes.SNOWFLAKE, serverWorld, this, this.getBbWidth() / 2, 10);
            world.gameEvent(this.getOwner(), GameEvent.PROJECTILE_LAND, hitPos);
            this.remove(RemovalReason.DISCARDED);
        }
        else if (state.is(CompatRegistry.CANDLE_HOLDERS_BLOCK_TAG) && state.hasProperty(BlockStateProperties.LIT)) {
            world.levelEvent(null, 1009, hitPos, 0);
            world.setBlock(hitPos, state.setValue(BlockStateProperties.LIT, Boolean.FALSE), 3);
            world.playSound(null, hitPos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnEntityRingParticles(ParticleTypes.SNOWFLAKE, serverWorld, this, this.getBbWidth() / 2, 10);
            world.gameEvent(this.getOwner(), GameEvent.PROJECTILE_LAND, hitPos);
            this.remove(RemovalReason.DISCARDED);
        }
        super.onHitBlock(hit);
    }

    @Override
    protected void onHitEntity(EntityHitResult hit) {
        Level world = this.level();
        Entity entity = hit.getEntity();
        
        if (!world.isClientSide) {
            if (entity instanceof Player player && !player.isSpectator()
                    && player != this.getOwner() && !player.getType().is(TagRegistry.ICE_BALL_IMMUNE)) {
                ItemStack shield = player.getUseItem();
                float width = player.getBbWidth() * 2.55F;
                float height = player.getBbHeight() * 1.55F;
                if (this.getOwner() != null && player.getTeam() != null && this.getOwner().getTeam() != null
                        && player.getTeam() == this.getOwner().getTeam())
                    return;

                if (this.getOwner() != null && player.isDamageSourceBlocked(DamageTypeRegistry.iceBall(entity, this.getOwner()))) {
                    if (shield.getItem() instanceof ShieldItem || player.getPersistentData().getBoolean("marioverse:has_ice_flower")) {
                        this.deflect(ProjectileDeflection.REVERSE, this.getOwner(), this.getOwner(), true);
                        this.setDeltaMovement(this.getDeltaMovement().reverse());
                        shield.hurtAndBreak(1, player, Player.getSlotForHand(player.getUsedItemHand()));
                        world.playSound(null, this.blockPosition(), SoundEvents.SHIELD_BLOCK,
                                SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                } else {
                    if (this.getOwner() != null) {
                        if (player.getType().is(TagRegistry.ICE_BALL_CAN_INSTAKILL))
                            player.hurt(DamageTypeRegistry.iceBall(entity, this.getOwner()), player.getHealth());
                        else player.hurt(DamageTypeRegistry.iceBall(entity, this.getOwner()), ConfigRegistry.ICE_BALL_DAMAGE.get().floatValue());
                    }
                    player.extinguishFire();

                    if (player.isAlive() && player.getPersistentData().getInt("marioverse:frozen_in_ice_cube_cooldown") == 0) {
                        IceCubeEntity iceCube = new IceCubeEntity(EntityRegistry.ICE_CUBE.get(), player.level());
                        iceCube.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
                        if (player.getType().is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES)
                                || player.getType().is(TagRegistry.ICE_CUBE_SHATTERS_INSTANTLY)) {
                            if (!iceCube.getPersistentData().contains("marioverse:frozen_in_ice_cube_cooldown"))
                                iceCube.getPersistentData().putInt("marioverse:frozen_in_ice_cube_cooldown", 2);
                            player.getPersistentData().putInt("marioverse:frozen_in_ice_cube_cooldown", 2);
                        } else {
                            if (!iceCube.getPersistentData().contains("marioverse:frozen_in_ice_cube_cooldown"))
                                iceCube.getPersistentData().putInt("marioverse:frozen_in_ice_cube_cooldown", ConfigRegistry.ICE_CUBE_LIFESPAN.get());
                            player.getPersistentData().putInt("marioverse:frozen_in_ice_cube_cooldown", ConfigRegistry.ICE_CUBE_LIFESPAN.get());
                        }
                        iceCube.setSize(width, height);
                        player.level().addFreshEntity(iceCube);
                        player.startRiding(iceCube, false);
                    }
                }
                world.playSound(null, this.blockPosition(), SoundRegistry.ICE_BALL_FROZE_ENEMY.get(),
                        SoundSource.AMBIENT, 1.0F, 1.0F);
                world.gameEvent(entity, GameEvent.PROJECTILE_LAND, entity.position());
                this.remove(RemovalReason.DISCARDED);
            } else if (entity instanceof LivingEntity livingEntity
                    && livingEntity != this.getOwner() && !livingEntity.getType().is(TagRegistry.ICE_BALL_IMMUNE)) {
                ItemStack shield = livingEntity.getUseItem();
                if ((livingEntity instanceof TamableAnimal tamableAnimal
                        && tamableAnimal.getOwner() == this.getOwner())
                        || (this.getOwner() != null && livingEntity.getTeam() != null && this.getOwner().getTeam() != null
                        && livingEntity.getTeam() == this.getOwner().getTeam()))
                    return;

                if (this.getOwner() != null && livingEntity.isDamageSourceBlocked(DamageTypeRegistry.iceBall(entity, this.getOwner()))) {
                    if (shield.getItem() instanceof ShieldItem || livingEntity.getPersistentData().getBoolean("marioverse:has_ice_flower")) {
                        this.deflect(ProjectileDeflection.REVERSE, this.getOwner(), this.getOwner(), true);
                        this.setDeltaMovement(this.getDeltaMovement().reverse());
                        shield.hurtAndBreak(1, livingEntity, LivingEntity.getSlotForHand(livingEntity.getUsedItemHand()));
                        world.playSound(null, this.blockPosition(), SoundEvents.SHIELD_BLOCK,
                                SoundSource.NEUTRAL, 1.0F, 1.0F);
                    }
                } else if (this.getOwner() != null) {
                    if (livingEntity.getType().is(TagRegistry.ICE_BALL_CAN_INSTAKILL))
                        livingEntity.hurt(DamageTypeRegistry.iceBall(entity, this.getOwner()), livingEntity.getHealth());
                    else livingEntity.hurt(DamageTypeRegistry.iceBall(entity, this.getOwner()), ConfigRegistry.ICE_BALL_DAMAGE.get().floatValue());
                    livingEntity.extinguishFire();

                    if (livingEntity.isAlive() || livingEntity instanceof MiniGoombaEntity) {
                        IceCubeEntity iceCube = new IceCubeEntity(EntityRegistry.ICE_CUBE.get(), livingEntity.level());
                        if (livingEntity.getType().is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES)
                                || livingEntity.getType().is(TagRegistry.ICE_CUBE_SHATTERS_INSTANTLY))
                            iceCube.setFrozenEntity(livingEntity, 2);
                        else iceCube.setFrozenEntity(livingEntity, ConfigRegistry.ICE_CUBE_LIFESPAN.get());
                        iceCube.moveTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), livingEntity.getYRot(), livingEntity.getXRot());
                        livingEntity.level().addFreshEntity(iceCube);
                    }
                }
                world.playSound(null, this.blockPosition(), SoundRegistry.ICE_BALL_FROZE_ENEMY.get(),
                        SoundSource.AMBIENT, 1.0F, 1.0F);
                world.gameEvent(entity, GameEvent.PROJECTILE_LAND, entity.position());
                this.remove(RemovalReason.DISCARDED);
            } else if (entity instanceof PiranhaPlantPart partEntity
                    && partEntity != this.getOwner() && !partEntity.getType().is(TagRegistry.ICE_BALL_IMMUNE)) {
                ItemStack shield = partEntity.getParent().getUseItem();

                if (this.getOwner() != null && partEntity.getParent().isDamageSourceBlocked(DamageTypeRegistry.iceBall(entity, this.getOwner()))) {
                    if (shield.getItem() instanceof ShieldItem || partEntity.getPersistentData().getBoolean("marioverse:has_ice_flower")) {
                        this.deflect(ProjectileDeflection.REVERSE, this.getOwner(), this.getOwner(), true);
                        this.setDeltaMovement(this.getDeltaMovement().reverse());
                        shield.hurtAndBreak(1, partEntity.getParent(), LivingEntity.getSlotForHand(partEntity.getParent().getUsedItemHand()));
                        world.playSound(null, this.blockPosition(), SoundEvents.SHIELD_BLOCK,
                                SoundSource.NEUTRAL, 1.0F, 1.0F);
                    }
                } else if (this.getOwner() != null) {
                    if (partEntity.getType().is(TagRegistry.ICE_BALL_CAN_INSTAKILL))
                        partEntity.hurt(DamageTypeRegistry.iceBall(entity, this.getOwner()), partEntity.getParent().getHealth());
                    else partEntity.hurt(DamageTypeRegistry.iceBall(entity, this.getOwner()), ConfigRegistry.ICE_BALL_DAMAGE.get().floatValue());
                    partEntity.extinguishFire();

                    if (partEntity.isAlive()) {
                        IceCubeEntity iceCube = new IceCubeEntity(EntityRegistry.ICE_CUBE.get(), partEntity.level());
                        if (partEntity.getType().is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES)
                                || partEntity.getType().is(TagRegistry.ICE_CUBE_SHATTERS_INSTANTLY))
                            iceCube.setFrozenEntity(partEntity, 2);
                        else iceCube.setFrozenEntity(partEntity, ConfigRegistry.ICE_CUBE_LIFESPAN.get());
                        iceCube.moveTo(partEntity.getX(), partEntity.getY(), partEntity.getZ(), partEntity.getYRot(), partEntity.getXRot());
                        partEntity.level().addFreshEntity(iceCube);
                    }
                }
                world.playSound(null, this.blockPosition(), SoundRegistry.ICE_BALL_FROZE_ENEMY.get(),
                        SoundSource.AMBIENT, 1.0F, 1.0F);
                world.gameEvent(entity, GameEvent.PROJECTILE_LAND, entity.position());
                this.remove(RemovalReason.DISCARDED);
            } else if (entity instanceof BouncingFireballProjectile fireball) {
                fireball.kill();
                world.gameEvent(entity, GameEvent.PROJECTILE_LAND, entity.position());
                world.playSound(null, this.blockPosition(), SoundRegistry.ICE_BALL_EXTINGUISHED_FIREBALL.get(),
                        SoundSource.AMBIENT, 1.0F, 1.0F);
                this.remove(RemovalReason.DISCARDED);
            } else {
                world.gameEvent(entity, GameEvent.PROJECTILE_LAND, entity.position());
                world.playSound(null, this.blockPosition(), SoundRegistry.ICE_BALL_SHATTERED_ON_ENEMY.get(),
                        SoundSource.AMBIENT, 1.0F, 1.0F);
                this.remove(RemovalReason.DISCARDED);
            }
        }

        if (world instanceof ServerLevel serverWorld) {
            if (entity instanceof Player player && !player.isSpectator() && player.canFreeze() && player != this.getOwner()
                    && !player.getType().is(TagRegistry.ICE_BALL_IMMUNE)) {
                ServerParticleUtils.spawnEntityRingParticles(ParticleTypes.SNOWFLAKE, serverWorld, this, this.getBbWidth() / 2, 10);
            } else if (entity instanceof LivingEntity livingEntity && livingEntity.canFreeze() && livingEntity != this.getOwner()
                    && !livingEntity.getType().is(TagRegistry.ICE_BALL_IMMUNE)) {
                ServerParticleUtils.spawnEntityRingParticles(ParticleTypes.SNOWFLAKE, serverWorld, this, this.getBbWidth() / 2, 10);
            }
        }
    }

    @Override
    public boolean deflect(@NotNull ProjectileDeflection deflection, @Nullable Entity entity, @Nullable Entity owner, boolean shouldDeflect) {
        Level world = this.level();

        if (entity instanceof LivingEntity livingEntity) {
            ItemStack shield = livingEntity.getUseItem();
            if (!world.isClientSide) {
                if (shield.getItem() instanceof ShieldItem
                        || livingEntity.getPersistentData().getBoolean("marioverse:has_ice_flower")) {
                    deflection.deflect(this, entity, this.random);
                    this.setOwner(owner);
                    this.onDeflection(entity, shouldDeflect);
                    return true;
                }
            }
        }
        return false;
    }
}
