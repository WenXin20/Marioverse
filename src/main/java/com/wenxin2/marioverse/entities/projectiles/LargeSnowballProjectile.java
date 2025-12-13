package com.wenxin2.marioverse.entities.projectiles;

import com.wenxin2.marioverse.entities.part_entities.PiranhaPlantPart;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
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
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class LargeSnowballProjectile extends ThrowableProjectile implements GeoEntity, TraceableEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public Vec3 slidingMovement = new Vec3(this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z);
    private boolean leftOwner;

    public LargeSnowballProjectile(EntityType<? extends LargeSnowballProjectile> entityType, Level world) {
        super(entityType, world);
    }

    public LargeSnowballProjectile(Level world, LivingEntity entity) {
        super(EntityRegistry.LARGE_SNOWBALL.get(), entity, world);
    }

    public LargeSnowballProjectile(Level world, double x, double y, double z) {
        super(EntityRegistry.LARGE_SNOWBALL.get(), x, y, z, world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    public void setSliding(boolean isSliding) {
        this.setData(DataAttachmentRegistry.IS_SLIDING.get(), isSliding);
    }

    public boolean isSliding() {
        return this.getData(DataAttachmentRegistry.IS_SLIDING.get());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Nullable
    @Override
    public ItemStack getPickedResult(@NotNull HitResult target) {
        return new ItemStack(ItemRegistry.LARGE_SNOWBALL.get());
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        Level world = this.level();
        Vec3 motion = this.getDeltaMovement();

        this.collideWithEntity();
        this.onHitFluid(world, this.blockPosition());

        if (this.getDeltaMovement().horizontalDistance() > 0.1) {
//            this.spawnSnowParticles();
            for (int i = 0; i < 1; i++) {
                double x = this.getX();
                double y = this.getY() + this.getBbHeight() / 2;
                double z = this.getZ();
                world.addParticle(ParticleTypes.SNOWFLAKE, x, y, z, 0, 0, 0);
            }
        }

        if (this.isSliding() && this.isAlive()) {
            BlockPos posBelow = this.blockPosition().below();
            BlockState stateBelow = level().getBlockState(posBelow);
            float friction = stateBelow.getFriction(level(), posBelow, this);
            double slideFriction = /*(friction > 0.8) ? 0.4 + friction / 1.5 :*/ 1.0;
            Vec3 slideMotion = this.slidingMovement.scale(slideFriction);

            if ((this.onGround()) && this.getDeltaMovement().horizontalDistance() > 0.0001) {
                this.setDeltaMovement(slideMotion.x, this.getDeltaMovement().y, slideMotion.z);
                this.slidingMovement = new Vec3(slideMotion.x, this.getDeltaMovement().y, slideMotion.z);
                this.hasImpulse = true;
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        Level world = this.level();
        BlockPos posBelow = this.blockPosition().below();
        BlockState stateBelow = world.getBlockState(posBelow);

        if (this.level().isClientSide || this.isRemoved()) {
            return true;
        } else if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            float friction = stateBelow.getFriction(world, posBelow, this);
            double slideSpeed;

            if (friction > 0.6)
                slideSpeed = 0.4 + friction / 1.5;
            else slideSpeed = 1.0;

            Vec3 slideDirection = new Vec3(this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z);

            if (source.getEntity() != null) {
                Vec3 attackerPos = source.getEntity().position();
                Vec3 hitPos = this.position();
                Vec3 slideDirRaw = hitPos.subtract(attackerPos).normalize();
                slideDirection = new Vec3(slideDirRaw.x, this.getDeltaMovement().y, slideDirRaw.z).normalize();
            } else if (source.getDirectEntity() != null)
                slideDirection = source.getDirectEntity().getDeltaMovement().normalize();

            Vec3 movement = slideDirection.scale(slideSpeed);

            this.setDeltaMovement(movement.x, this.getDeltaMovement().y, movement.z);
            this.slidingMovement = new Vec3(movement.x, this.getDeltaMovement().y, movement.z);
            this.hasImpulse = true;
            this.setOwner(source.getEntity());
            this.setSliding(true);
            this.leftOwner = false;

            return true;
        }
    }

    @Override
    public boolean mayInteract(Level p_150167_, BlockPos p_150168_) {
        Entity entity = this.getOwner();
        return true;
    }

    public void discardEffects(Level world) {
        if (this.level() instanceof ServerLevel serverWorld)
            ServerParticleUtils.spawnParticleRingOnEntity(ParticleTypes.SNOWFLAKE, serverWorld, this, this.getBbWidth() / 2, 0.0, 10);
        world.playSound(null, this.blockPosition(), SoundEvents.SNOW_BREAK, SoundSource.AMBIENT);
        world.gameEvent(this.getOwner(), GameEvent.PROJECTILE_LAND, this.position());
        this.remove(RemovalReason.DISCARDED);
    }

    public void discardEffectsOnSideHit(Level world, BlockPos hitPos) {
        if (world instanceof ServerLevel serverWorld)
            ServerParticleUtils.spawnParticleRingOnEntity(ParticleTypes.SNOWFLAKE, serverWorld, this, this.getBbWidth() / 2, 0.0, 10);
        world.playSound(null, this.blockPosition(), SoundEvents.SNOW_BREAK, SoundSource.AMBIENT);
        world.gameEvent(this.getOwner(), GameEvent.PROJECTILE_LAND, hitPos);
        this.remove(RemovalReason.DISCARDED); // Despawn on side hit
    }

    protected void onHitFluid(Level world, BlockPos pos) {
        FluidState fluidStateBelow = world.getFluidState(pos.below());

        if (fluidStateBelow.getType().is(FluidTags.LAVA))
            this.discardEffects(world);
    }

    @Override
    public void onHitBlock(BlockHitResult hit) {
        Level world = this.level();
        BlockPos hitPos = hit.getBlockPos();
        BlockState state = world.getBlockState(hitPos);
        BlockState stateAbove = world.getBlockState(hitPos.above());

        if (hit.getDirection().getAxis() == Direction.Axis.X || hit.getDirection().getAxis() == Direction.Axis.Z)
            this.discardEffectsOnSideHit(world, hitPos);
        else if (hit.getDirection().getAxis() == Direction.Axis.Y) {
            this.setSliding(true);

            Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x, 0.0, motion.z);
            this.hasImpulse = true;
            return;
        }
        else super.onHitBlock(hit);

        if (state.is(TagRegistry.ICE_BALL_EXTINGUISHES) && state.hasProperty(BlockStateProperties.LIT)) { // TODO new tag
            if (this.level() instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnParticleRingOnBlock(ParticleTypes.SMOKE, serverWorld, hitPos, 0.25D, 10);
            world.setBlock(hitPos, state.setValue(BlockStateProperties.LIT, Boolean.FALSE), 3);
            world.playSound(null, hitPos, state.getBlock() instanceof CandleBlock || state.getBlock() instanceof CandleCakeBlock
                    ? SoundEvents.CANDLE_EXTINGUISH : SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
            this.discardEffects(world);
        } else if (state.is(TagRegistry.ICE_BALL_EXTINGUISHES) && state.getBlock() instanceof FireBlock) {
            if (this.level() instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnParticleRingOnBlock(ParticleTypes.SMOKE, serverWorld, hitPos, 0.25D, 10);
            world.removeBlock(hitPos, true);
            world.playSound(null, hitPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
            this.discardEffects(world);
        } else if (stateAbove.is(TagRegistry.ICE_BALL_EXTINGUISHES) && stateAbove.getBlock() instanceof FireBlock) {
            if (this.level() instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnParticleRingOnBlock(ParticleTypes.SMOKE, serverWorld, hitPos.above(), 0.25D, 10);
            world.removeBlock(hitPos.above(), true);
            world.playSound(null, hitPos.above(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
            this.discardEffects(world);
        }
        super.onHitBlock(hit);
    }

    @Override
    protected void onHitEntity(EntityHitResult hit) {
        Level world = this.level();
        Entity entity = hit.getEntity();

        if (entity instanceof LivingEntity livingEntity
                && livingEntity != this.getOwner() /*&& !livingEntity.getType().is(TagRegistry.ICE_BALL_IMMUNE)*/ // TODO
                && !entity.getData(DataAttachmentRegistry.HAS_SUPER_STAR)) {
            ItemStack shield = livingEntity.getUseItem();
            if ((livingEntity instanceof TamableAnimal tamableAnimal
                    && tamableAnimal.getOwner() == this.getOwner())
                    || (this.getOwner() != null && livingEntity.getTeam() != null && this.getOwner().getTeam() != null
                    && livingEntity.getTeam() == this.getOwner().getTeam()))
                return;

            if (this.getOwner() != null && livingEntity.isDamageSourceBlocked(DamageSourceRegistry.iceBall(entity, this.getOwner())))
                this.deflectProjectile(livingEntity, shield, entity, world);
            else if (this.getOwner() != null) {
                if (livingEntity.getType().is(TagRegistry.ICE_BALL_CAN_INSTAKILL)) // TODO
                    livingEntity.hurt(DamageSourceRegistry.iceBall(entity, this.getOwner()), livingEntity.getHealth() * 1.25F); // TODO
                else livingEntity.hurt(DamageSourceRegistry.iceBall(entity, this.getOwner()), ConfigRegistry.ICE_BALL_DAMAGE.get().floatValue());
                livingEntity.extinguishFire();
            }
            world.playSound(null, this.blockPosition(), SoundRegistry.ICE_BALL_FROZE_ENEMY.get(), SoundSource.AMBIENT); // TODO
            world.gameEvent(entity, GameEvent.PROJECTILE_LAND, entity.position());
            this.remove(RemovalReason.DISCARDED);
        } else if (entity instanceof PiranhaPlantPart partEntity
                && partEntity != this.getOwner() && !partEntity.getType().is(TagRegistry.ICE_BALL_IMMUNE)
                && !entity.getData(DataAttachmentRegistry.HAS_SUPER_STAR)) {
            ItemStack shield = partEntity.getParent().getUseItem();

            if (this.getOwner() != null && partEntity.getParent().isDamageSourceBlocked(DamageSourceRegistry.iceBall(entity, this.getOwner())))
                this.deflectProjectile(partEntity.getParent(), shield, entity, world);
            else if (this.getOwner() != null) {
                if (partEntity.getType().is(TagRegistry.ICE_BALL_CAN_INSTAKILL))
                    partEntity.hurt(DamageSourceRegistry.iceBall(entity, this.getOwner()), partEntity.getParent().getHealth() * 1.25F);
                else partEntity.hurt(DamageSourceRegistry.iceBall(entity, this.getOwner()), ConfigRegistry.ICE_BALL_DAMAGE.get().floatValue());
                partEntity.extinguishFire();
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

    private void deflectProjectile(LivingEntity livingEntity, ItemStack shield, Entity entity, Level world) {
        if (shield.getItem() instanceof ShieldItem
                || (entity instanceof AbilitiesHandler handler && handler.mv$hasIceFlower())) {
            this.deflect(ProjectileDeflection.REVERSE, entity, this.getOwner(), true);
            this.setDeltaMovement(this.getDeltaMovement().reverse());
            shield.hurtAndBreak(1, livingEntity, LivingEntity.getSlotForHand(livingEntity.getUsedItemHand()));
            world.playSound(null, this.blockPosition(), SoundEvents.SHIELD_BLOCK,
                    SoundSource.NEUTRAL, 1.0F, 1.0F);
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

    protected void spawnSnowParticles() {
        BlockPos posLegacy = this.getOnPosLegacy();
        BlockState state = this.level().getBlockState(posLegacy);

        if (!state.addRunningEffects(this.level(), posLegacy, this)) {
            if (state.getRenderShape() != RenderShape.INVISIBLE) {
                Vec3 vec3 = this.getDeltaMovement();
                BlockPos pos = this.blockPosition();
                double x = this.getX() + (this.random.nextDouble() - 0.5);
                double z = this.getZ() + (this.random.nextDouble() - 0.5);
                if (pos.getX() != posLegacy.getX())
                    x = Mth.clamp(x, posLegacy.getX(), posLegacy.getX() + 1.0);

                if (pos.getZ() != posLegacy.getZ())
                    z = Mth.clamp(z, posLegacy.getZ(), posLegacy.getZ() + 1.0);

                if (!this.isInWaterOrBubble())
                    this.level().addParticle(ParticleTypes.SNOWFLAKE, x, this.getY() + 0.1, z, vec3.x * -2.0, 0, vec3.z * -2.0);
            }
        }
    }
}
