package com.wenxin2.marioverse.entities.projectiles;

import com.wenxin2.marioverse.entities.IceCubeEntity;
import com.wenxin2.marioverse.entities.MiniGoombaEntity;
import com.wenxin2.marioverse.entities.part_entities.PiranhaPlantPart;
import com.wenxin2.marioverse.entities.power_ups.BaseMushroomEntity;
import com.wenxin2.marioverse.entities.power_ups.BasePowerUpEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
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
    private int bounceCount;

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
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("BounceCount", this.getBounceCount());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setBounceCount(tag.getInt("BounceCount"));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void tick() {
        super.tick();
        Level world = this.level();
        Vec3 motion = this.getDeltaMovement();

        this.collideWithEntity();
        this.onHitFluid(world, this.blockPosition());

        if (!this.isInWater())
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.04D, 0)); // Gravity
        else this.setDeltaMovement(this.getDeltaMovement().add(motion.x, -0.04D, motion.y)); // Gravity

        if (motion.length() > 0) {
            this.setYRot((float) Math.toDegrees(Math.atan2(motion.z, motion.x)) + 270);
            this.setXRot((float) Math.toDegrees(Math.atan2(-motion.y, Math.sqrt(motion.x * motion.x + motion.z * motion.z))));
        }

        if (this.onGround() || this.tickCount > 400)
            this.discardEffects(world);


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

    private int getBounceCount() {
        return this.bounceCount;
    }

    private void setBounceCount(int bounceCount) {
        this.bounceCount = bounceCount;
    }

    public void bounceEffects(Level world, BlockPos pos) {
        if (world instanceof ServerLevel serverWorld)
            ServerParticleUtils.spawnParticleRingBelowEntity(ParticleTypes.SNOWFLAKE, serverWorld, this, this.getBbWidth() / 2, 0.0, 10);
        world.playSound(null, pos, SoundRegistry.ICE_BALL_BOUNCED.get(), SoundSource.AMBIENT, 1.0F, 1.0F);
        world.gameEvent(this.getOwner(), GameEvent.PROJECTILE_LAND, pos);
    }

    public void discardEffects(Level world) {
        if (this.level() instanceof ServerLevel serverWorld)
            ServerParticleUtils.spawnParticleRingOnEntity(ParticleTypes.SNOWFLAKE, serverWorld, this, this.getBbWidth() / 2, 0.0, 10);
        world.playSound(null, this.blockPosition(), SoundRegistry.ICE_BALL_SHATTERED.get(),
                SoundSource.AMBIENT, 1.0F, 1.0F);
        world.gameEvent(this.getOwner(), GameEvent.PROJECTILE_LAND, this.position());
        this.remove(RemovalReason.DISCARDED);
    }

    public void discardEffectsOnSideHit(Level world, BlockPos hitPos) {
        if (world instanceof ServerLevel serverWorld)
            ServerParticleUtils.spawnParticleRingOnEntity(ParticleTypes.SNOWFLAKE, serverWorld, this, this.getBbWidth() / 2, 0.0, 10);
        world.playSound(null, this.blockPosition(), SoundRegistry.ICE_BALL_SHATTERED.get(),
                SoundSource.AMBIENT, 1.0F, 1.0F);
        world.gameEvent(this.getOwner(), GameEvent.PROJECTILE_LAND, hitPos);
        this.remove(RemovalReason.DISCARDED); // Despawn on side hit
    }

    protected void onHitFluid(Level world, BlockPos pos) {
        FluidState fluidState = world.getFluidState(pos);
        FluidState fluidStateBelow = world.getFluidState(pos.below());
        BlockState state = world.getBlockState(pos);

        if (fluidStateBelow.getType().is(TagRegistry.FREEZES_INTO_FROSTED_ICE)) {
            if (fluidStateBelow.getType() == Fluids.WATER && fluidState.getType() != Fluids.WATER && state.canBeReplaced())
                world.setBlock(pos.below(), Blocks.FROSTED_ICE.defaultBlockState(), 3);
        } else if (fluidStateBelow.getType().is(TagRegistry.FREEZES_INTO_OBSIDIAN)) {
            world.setBlock(pos.below(), Blocks.OBSIDIAN.defaultBlockState(), 3);
            this.discardEffects(world);
        } else if (fluidStateBelow.getType().is(TagRegistry.FREEZES_INTO_COBBLESTONE)) {
            world.setBlock(pos.below(), Blocks.COBBLESTONE.defaultBlockState(), 3);
            this.discardEffects(world);
        }
    }

    @Override
    public void onHitBlock(BlockHitResult hit) {
        Level world = this.level();
        BlockPos hitPos = hit.getBlockPos();
        BlockState state = world.getBlockState(hitPos);
        BlockState stateAbove = world.getBlockState(hitPos.above());

        if (hit.getDirection().getAxis() == Direction.Axis.X || hit.getDirection().getAxis() == Direction.Axis.Z)
            this.discardEffectsOnSideHit(world, hitPos);
        else if (this.getBounceCount() < ConfigRegistry.MAX_ICE_BALL_BOUNCES.get()) {
            Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x, 0.5, motion.z); // Bounce
            this.bounceEffects(world, hitPos);

            this.setBounceCount(this.getBounceCount() + 1);
        } else this.discardEffectsOnSideHit(world, hitPos);

        if (state.getBlock() == Blocks.FROSTED_ICE)
            world.setBlock(hitPos, Blocks.FROSTED_ICE.defaultBlockState(), 3);
        else if (state.is(TagRegistry.FREEZES_INTO_PACKED_ICE))
            world.setBlock(hitPos, Blocks.PACKED_ICE.defaultBlockState(), 3);
        else if (state.is(TagRegistry.MELTS_INTO_PACKED_ICE))
            world.setBlock(hitPos, Blocks.PACKED_ICE.defaultBlockState(), 3);
        else if (state.is(TagRegistry.ICE_BALL_EXTINGUISHES) && state.hasProperty(BlockStateProperties.LIT)) {
            if (this.level() instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnParticleRingOnBlock(ParticleTypes.SMOKE, serverWorld, hitPos, 0.25D, 10);
            world.setBlock(hitPos, state.setValue(BlockStateProperties.LIT, Boolean.FALSE), 3);
            world.playSound(null, hitPos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
            this.discardEffects(world);
        } else if (state.is(TagRegistry.ICE_BALL_EXTINGUISHES) && state.getBlock() instanceof FireBlock) {
            if (this.level() instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnParticleRingOnBlock(ParticleTypes.SMOKE, serverWorld, hitPos, 0.25D, 10);
            world.removeBlock(hitPos, true);
            world.playSound(null, hitPos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
            this.discardEffects(world);
        } else if (stateAbove.is(TagRegistry.ICE_BALL_EXTINGUISHES) && stateAbove.getBlock() instanceof FireBlock) {
            if (this.level() instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnParticleRingOnBlock(ParticleTypes.SMOKE, serverWorld, hitPos.above(), 0.25D, 10);
            world.removeBlock(hitPos.above(), true);
            world.playSound(null, hitPos.above(), SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
            this.discardEffects(world);
        }
        super.onHitBlock(hit);
    }

    @Override
    protected void onHitEntity(EntityHitResult hit) {
        Level world = this.level();
        Entity entity = hit.getEntity();

        if (entity instanceof Player player && player instanceof AbilitiesHandler handler && !player.isSpectator()
                && player != this.getOwner() && !player.getType().is(TagRegistry.ICE_BALL_IMMUNE)
                && !entity.getData(DataAttachmentRegistry.HAS_SUPER_STAR)) {
            ItemStack shield = player.getUseItem();
            float width = player.getBbWidth() * 2.55F;
            float height = player.getBbHeight() * 1.55F;
            if (this.getOwner() != null && player.getTeam() != null && this.getOwner().getTeam() != null
                    && player.getTeam() == this.getOwner().getTeam())
                return;

            if (this.getOwner() != null && player.isDamageSourceBlocked(DamageSourceRegistry.iceBall(entity, this.getOwner()))) {
                if (shield.getItem() instanceof ShieldItem || handler.mv$hasIceFlower()) {
                    this.deflect(ProjectileDeflection.REVERSE, entity, this.getOwner(), true);
                    this.setDeltaMovement(this.getDeltaMovement().reverse());
                    shield.hurtAndBreak(1, player, Player.getSlotForHand(player.getUsedItemHand()));
                    world.playSound(null, this.blockPosition(), SoundEvents.SHIELD_BLOCK,
                            SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            } else {
                if (this.getOwner() != null) {
                    if (player.getType().is(TagRegistry.ICE_BALL_CAN_INSTAKILL))
                        player.hurt(DamageSourceRegistry.iceBall(entity, this.getOwner()), player.getHealth() * 1.25F);
                    else player.hurt(DamageSourceRegistry.iceBall(entity, this.getOwner()), ConfigRegistry.ICE_BALL_DAMAGE.get().floatValue());
                }
                player.extinguishFire();

                if (player.isAlive() && handler.mv$getFrozenCooldown() == 0 && handler.mv$getFreezeImmunityCooldown() == 0) {
                    IceCubeEntity iceCube = new IceCubeEntity(EntityRegistry.ICE_CUBE.get(), player.level());
                    iceCube.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
                    if (player.getType().is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES)
                            || player.getType().is(TagRegistry.ICE_CUBE_SHATTERS_INSTANTLY)) {
                        iceCube.setFrozenCooldown(2);
                        handler.mv$setFrozenCooldown(2);
                    } else {
                        iceCube.setFrozenCooldown(ConfigRegistry.ICE_CUBE_LIFESPAN.get());
                        handler.mv$setFrozenCooldown(ConfigRegistry.ICE_CUBE_LIFESPAN.get());
                    }
                    if (!player.onGround() && !player.isInWaterOrBubble())
                        iceCube.setTicksInAir(120);
                    iceCube.setSize(width, height);
                    iceCube.setOwner(this.getOwner());
                    player.level().addFreshEntity(iceCube);
                    player.startRiding(iceCube, false);
                }
            }
            world.playSound(null, this.blockPosition(), SoundRegistry.ICE_BALL_FROZE_ENEMY.get(),
                    SoundSource.AMBIENT, 1.0F, 1.0F);
            world.gameEvent(entity, GameEvent.PROJECTILE_LAND, entity.position());
            this.remove(RemovalReason.DISCARDED);
        } else if (entity instanceof LivingEntity livingEntity
                && livingEntity != this.getOwner() && !livingEntity.getType().is(TagRegistry.ICE_BALL_IMMUNE)
                && !entity.getData(DataAttachmentRegistry.HAS_SUPER_STAR)) {
            ItemStack shield = livingEntity.getUseItem();
            if ((livingEntity instanceof TamableAnimal tamableAnimal
                    && tamableAnimal.getOwner() == this.getOwner())
                    || (this.getOwner() != null && livingEntity.getTeam() != null && this.getOwner().getTeam() != null
                    && livingEntity.getTeam() == this.getOwner().getTeam()))
                return;

            if (this.getOwner() != null && livingEntity.isDamageSourceBlocked(DamageSourceRegistry.iceBall(entity, this.getOwner()))) {
                if (shield.getItem() instanceof ShieldItem
                        || (entity instanceof AbilitiesHandler handler && handler.mv$hasIceFlower())) {
                    this.deflect(ProjectileDeflection.REVERSE, entity, this.getOwner(), true);
                    this.setDeltaMovement(this.getDeltaMovement().reverse());
                    shield.hurtAndBreak(1, livingEntity, LivingEntity.getSlotForHand(livingEntity.getUsedItemHand()));
                    world.playSound(null, this.blockPosition(), SoundEvents.SHIELD_BLOCK,
                            SoundSource.NEUTRAL, 1.0F, 1.0F);
                }
            } else if (this.getOwner() != null) {
                if (livingEntity.getType().is(TagRegistry.ICE_BALL_CAN_INSTAKILL))
                    livingEntity.hurt(DamageSourceRegistry.iceBall(entity, this.getOwner()), livingEntity.getHealth() * 1.25F);
                else livingEntity.hurt(DamageSourceRegistry.iceBall(entity, this.getOwner()), ConfigRegistry.ICE_BALL_DAMAGE.get().floatValue());
                livingEntity.extinguishFire();

                if (livingEntity.isAlive() || livingEntity instanceof MiniGoombaEntity
                        || livingEntity instanceof BasePowerUpEntity || livingEntity instanceof BaseMushroomEntity) {
                    IceCubeEntity iceCube = new IceCubeEntity(EntityRegistry.ICE_CUBE.get(), livingEntity.level());
                    if (livingEntity.getType().is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES)
                            || livingEntity.getType().is(TagRegistry.ICE_CUBE_SHATTERS_INSTANTLY))
                        iceCube.setFrozenEntity(livingEntity, 2);
                    else iceCube.setFrozenEntity(livingEntity, ConfigRegistry.ICE_CUBE_LIFESPAN.get());
                    if (!livingEntity.onGround() && !livingEntity.isInWaterOrBubble())
                        iceCube.setTicksInAir(120);
                    iceCube.moveTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), livingEntity.getYRot(), livingEntity.getXRot());
                    iceCube.setOwner(this.getOwner());
                    livingEntity.level().addFreshEntity(iceCube);
                }
            }
            world.playSound(null, this.blockPosition(), SoundRegistry.ICE_BALL_FROZE_ENEMY.get(),
                    SoundSource.AMBIENT, 1.0F, 1.0F);
            world.gameEvent(entity, GameEvent.PROJECTILE_LAND, entity.position());
            this.remove(RemovalReason.DISCARDED);
        } else if (entity instanceof PiranhaPlantPart partEntity
                && partEntity != this.getOwner() && !partEntity.getType().is(TagRegistry.ICE_BALL_IMMUNE)
                && !entity.getData(DataAttachmentRegistry.HAS_SUPER_STAR)) {
            ItemStack shield = partEntity.getParent().getUseItem();

            if (this.getOwner() != null && partEntity.getParent().isDamageSourceBlocked(DamageSourceRegistry.iceBall(entity, this.getOwner()))) {
                if (shield.getItem() instanceof ShieldItem
                        || (entity instanceof AbilitiesHandler handler && handler.mv$hasIceFlower())) {
                    this.deflect(ProjectileDeflection.REVERSE, entity, this.getOwner(), true);
                    this.setDeltaMovement(this.getDeltaMovement().reverse());
                    shield.hurtAndBreak(1, partEntity.getParent(), LivingEntity.getSlotForHand(partEntity.getParent().getUsedItemHand()));
                    world.playSound(null, this.blockPosition(), SoundEvents.SHIELD_BLOCK,
                            SoundSource.NEUTRAL, 1.0F, 1.0F);
                }
            } else if (this.getOwner() != null) {
                if (partEntity.getType().is(TagRegistry.ICE_BALL_CAN_INSTAKILL))
                    partEntity.hurt(DamageSourceRegistry.iceBall(entity, this.getOwner()), partEntity.getParent().getHealth() * 1.25F);
                else partEntity.hurt(DamageSourceRegistry.iceBall(entity, this.getOwner()), ConfigRegistry.ICE_BALL_DAMAGE.get().floatValue());
                partEntity.extinguishFire();

                if (partEntity.isAlive()) {
                    IceCubeEntity iceCube = new IceCubeEntity(EntityRegistry.ICE_CUBE.get(), partEntity.level());
                    if (partEntity.getType().is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES)
                            || partEntity.getType().is(TagRegistry.ICE_CUBE_SHATTERS_INSTANTLY))
                        iceCube.setFrozenEntity(partEntity, 2);
                    else iceCube.setFrozenEntity(partEntity, ConfigRegistry.ICE_CUBE_LIFESPAN.get());
                    iceCube.moveTo(partEntity.getX(), partEntity.getY(), partEntity.getZ(), partEntity.getYRot(), partEntity.getXRot());
                    iceCube.setOwner(this.getOwner());
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

        if (world instanceof ServerLevel serverWorld) {
            if (entity instanceof Player player && !player.isSpectator() && player.canFreeze() && player != this.getOwner()
                    && !player.getType().is(TagRegistry.ICE_BALL_IMMUNE)
                    && !entity.getData(DataAttachmentRegistry.HAS_SUPER_STAR)) {
                ServerParticleUtils.spawnParticleRingOnEntity(ParticleTypes.SNOWFLAKE, serverWorld, this, this.getBbWidth() / 2, 0.0, 10);
            } else if (entity instanceof LivingEntity livingEntity && livingEntity.canFreeze() && livingEntity != this.getOwner()
                    && !livingEntity.getType().is(TagRegistry.ICE_BALL_IMMUNE)
                    && !entity.getData(DataAttachmentRegistry.HAS_SUPER_STAR)) {
                ServerParticleUtils.spawnParticleRingOnEntity(ParticleTypes.SNOWFLAKE, serverWorld, this, this.getBbWidth() / 2, 0.0, 10);
            }
        }
    }

    @Override
    public boolean deflect(@NotNull ProjectileDeflection deflection, @Nullable Entity entity, @Nullable Entity owner, boolean shouldDeflect) {
        Level world = this.level();

        if (entity instanceof LivingEntity) {
            if (!world.isClientSide) {
                deflection.deflect(this, entity, this.random);
                this.setOwner(entity);
                this.onDeflection(entity, shouldDeflect);
                return true;
            }
        }
        return false;
    }

    public void collideWithEntity() {
        AABB collisionBox = this.getBoundingBox().inflate(0.01, 0, 0.01);
        List<Entity> collidingEntities = this.level().getEntities(this, collisionBox);

        for (Entity entity : collidingEntities) {
            if (entity instanceof Breeze) {
                this.deflect(ProjectileDeflection.REVERSE, entity, this.getOwner(), true);
                this.level().playSound(null, entity.blockPosition(), SoundEvents.BREEZE_DEFLECT,
                        entity.getSoundSource(), 1.0F, 1.0F);
                return;
            }
        }
    }
}
