package com.wenxin2.marioverse.entities.projectiles;

import com.wenxin2.marioverse.entities.IceCubeEntity;
import com.wenxin2.marioverse.entities.part_entities.PiranhaPlantPart;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
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

public class BouncingFireballProjectile extends ThrowableProjectile implements GeoEntity {
    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.bouncing_fireball.idle");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public BouncingFireballProjectile(EntityType<? extends BouncingFireballProjectile> entityType, Level world) {
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
        Vec3 motion = this.getDeltaMovement();

        this.collideWithEntity();

        if (!this.isInWater())
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.04D, 0)); // Gravity
        else this.setDeltaMovement(this.getDeltaMovement().add(motion.x, -0.04D, motion.y)); // Gravity

        if (motion.lengthSqr() > 0.0001) {
            this.setYRot((float) Math.toDegrees(Math.atan2(-motion.x, motion.z)));
            this.setXRot((float) Math.toDegrees(Math.atan2(-motion.y, Math.sqrt(motion.x * motion.x + motion.z * motion.z))));
        }

        if (this.onGround() || this.tickCount > 400)
            this.discardEffects();

        for (int i = 0; i < 1; i++) {
            double x = this.getX();
            double y = this.getY() + this.getBbHeight() / 2;
            double z = this.getZ();
            this.level().addParticle(ParticleTypes.FLAME, x, y, z, 0, 0, 0);
            if (this.level().getFluidState(this.blockPosition()).is(FluidTags.WATER))
                this.level().addParticle(ParticleTypes.BUBBLE, x, y, z, 0, 0, 0);
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
        BlockState state = this.level().getBlockState(hitPos);
        BlockState stateAbove = this.level().getBlockState(hitPos.above());

        if (hit.getDirection().getAxis() == Direction.Axis.X || hit.getDirection().getAxis() == Direction.Axis.Z)
            this.discardEffectsOnSideHit(world, hitPos);
        else {
            Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x, 0.4, motion.z); // Bounce
            this.bounceEffects(world, hitPos);
        }

        if (state.is(Blocks.SNOW)) {
            ParticleUtils.spawnParticleOnFace(world, hitPos, Direction.UP, ParticleTypes.WHITE_SMOKE, Vec3.ZERO, 5D);
            world.removeBlock(hitPos, Boolean.FALSE);
        } else if (state.is(TagRegistry.MELTS))
            world.removeBlock(hitPos, Boolean.FALSE);
        else if (stateAbove.is(Blocks.SNOW) || stateAbove.is(Blocks.POWDER_SNOW)) {
            ParticleUtils.spawnParticleOnFace(world, hitPos.above(), Direction.UP, ParticleTypes.WHITE_SMOKE, Vec3.ZERO, 5D);
            world.removeBlock(hitPos.above(), Boolean.FALSE);
        } else if (state.is(TagRegistry.MELTS_INTO_WATER))
            world.setBlock(hitPos, Blocks.WATER.defaultBlockState(), 3);
        else if (state.is(TagRegistry.MELTS_INTO_ICE))
            world.setBlock(hitPos, Blocks.ICE.defaultBlockState(), 3);
        else if (state.is(TagRegistry.MELTS_INTO_PACKED_ICE))
            world.setBlock(hitPos, Blocks.PACKED_ICE.defaultBlockState(), 3);
        else if (state.is(TagRegistry.FIREBALL_SETS_ON_FIRE) && state.getBlock() instanceof TntBlock) {
            PrimedTnt primedtnt = new PrimedTnt(world, hitPos.getX() + 0.5, hitPos.getY(), hitPos.getZ() + 0.5, null);
            world.removeBlock(hitPos, Boolean.FALSE);
            world.addFreshEntity(primedtnt);
        } else if (state.is(TagRegistry.FIREBALL_SETS_ON_FIRE) && state.hasProperty(BlockStateProperties.LIT))
            world.setBlock(hitPos, state.setValue(BlockStateProperties.LIT, Boolean.TRUE), 3);
        else if (state.is(BlockTags.SOUL_FIRE_BASE_BLOCKS) && world.getBlockState(hitPos.above()).isAir())
            world.setBlock(hitPos.above(), Blocks.SOUL_FIRE.defaultBlockState(), 3);
        else if (state.is(TagRegistry.FIREBALL_SETS_ON_FIRE) && world.getBlockState(hitPos.above()).isAir())
            world.setBlock(hitPos.above(), Blocks.FIRE.defaultBlockState(), 3);
        else if (state.is(Blocks.OBSIDIAN) && world.getBlockState(hitPos.above()).isAir())
            world.setBlock(hitPos.above(), Blocks.FIRE.defaultBlockState(), 3);
        else if (state.is(Blocks.WET_SPONGE))
            world.setBlock(hitPos, Blocks.SPONGE.defaultBlockState(), 3);
        super.onHitBlock(hit);
    }

    @Override
    protected void onHitEntity(EntityHitResult hit) {
        Entity entity = hit.getEntity();
        Level world = this.level();
        BlockPos pos = this.blockPosition();

        if (entity instanceof Player player && !player.isSpectator() && !player.fireImmune() && player != this.getOwner()
                && !player.getType().is(TagRegistry.FIREBALL_IMMUNE)) {
            ItemStack shield = player.getUseItem();
            if (this.getOwner() != null && player.getTeam() != null && this.getOwner().getTeam() != null
                    && player.getTeam() == this.getOwner().getTeam())
                return;

            if (this.getOwner() != null && player.isDamageSourceBlocked(DamageSourceRegistry.fireball(entity, this.getOwner()))) {
                if (shield.getItem() instanceof ShieldItem
                        || (entity instanceof AbilitiesHandler handler && handler.mv$hasFireFlower())) {
                    this.deflect(ProjectileDeflection.REVERSE, entity, this.getOwner(), true);
                    this.setDeltaMovement(this.getDeltaMovement().reverse());
                    shield.hurtAndBreak(1, player, Player.getSlotForHand(player.getUsedItemHand()));
                    world.playSound(null, pos, SoundEvents.SHIELD_BLOCK,
                            SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            } else if (this.getOwner() != null) {
                if (player.getType().is(TagRegistry.FIREBALL_CAN_INSTAKILL))
                    player.hurt(DamageSourceRegistry.fireball(entity, this.getOwner()), player.getHealth() * 1.25F);
                else
                    player.hurt(DamageSourceRegistry.fireball(entity, this.getOwner()), ConfigRegistry.FIREBALL_DAMAGE.get().floatValue());
                player.igniteForSeconds(2.0F);
            }
            world.playSound(null, pos, SoundRegistry.FIREBALL_EXTINGUISHED.get(),
                    SoundSource.AMBIENT, 1.0F, 1.0F);
            world.gameEvent(entity, GameEvent.PROJECTILE_LAND, this.position());
            this.remove(RemovalReason.DISCARDED);
        } else if (entity instanceof LivingEntity livingEntity && !livingEntity.fireImmune() && livingEntity != this.getOwner()
                && !livingEntity.getType().is(TagRegistry.FIREBALL_IMMUNE)) {
            ItemStack shield = livingEntity.getUseItem();
            if ((livingEntity instanceof TamableAnimal tamableAnimal
                    && tamableAnimal.getOwner() == this.getOwner())
                    || (this.getOwner() != null && livingEntity.getTeam() != null && this.getOwner().getTeam() != null
                    && livingEntity.getTeam() == this.getOwner().getTeam()))
                return;

            if (this.getOwner() != null && livingEntity.isDamageSourceBlocked(DamageSourceRegistry.fireball(entity, this.getOwner()))) {
                if (shield.getItem() instanceof ShieldItem
                        || (entity instanceof AbilitiesHandler handler && handler.mv$hasFireFlower())) {
                    this.deflect(ProjectileDeflection.REVERSE, entity, this.getOwner(), true);
                    this.setDeltaMovement(this.getDeltaMovement().reverse());
                    shield.hurtAndBreak(1, livingEntity, LivingEntity.getSlotForHand(livingEntity.getUsedItemHand()));
                    world.playSound(null, pos, SoundEvents.SHIELD_BLOCK,
                            SoundSource.NEUTRAL, 1.0F, 1.0F);
                }
            } else if (this.getOwner() != null) {
                if (livingEntity.getType().is(TagRegistry.FIREBALL_CAN_INSTAKILL))
                    livingEntity.hurt(DamageSourceRegistry.fireball(entity, this.getOwner()), livingEntity.getHealth() * 1.25F);
                else
                    livingEntity.hurt(DamageSourceRegistry.fireball(entity, this.getOwner()), ConfigRegistry.FIREBALL_DAMAGE.get().floatValue());
                livingEntity.igniteForSeconds(2.0F);
            }
            world.playSound(null, pos, SoundRegistry.FIREBALL_EXTINGUISHED.get(),
                    SoundSource.AMBIENT, 1.0F, 1.0F);
            world.gameEvent(entity, GameEvent.PROJECTILE_LAND, this.position());
            this.remove(RemovalReason.DISCARDED);
        } else if (entity instanceof PiranhaPlantPart partEntity && !partEntity.fireImmune() && partEntity != this.getOwner()
                && !partEntity.getType().is(TagRegistry.FIREBALL_IMMUNE)) {
            ItemStack shield = partEntity.getParent().getUseItem();

            if (this.getOwner() != null && partEntity.getParent().isDamageSourceBlocked(DamageSourceRegistry.fireball(entity, this.getOwner()))) {
                if (shield.getItem() instanceof ShieldItem
                        || (entity instanceof AbilitiesHandler handler && handler.mv$hasFireFlower())) {
                    this.deflect(ProjectileDeflection.REVERSE, entity, this.getOwner(), true);
                    this.setDeltaMovement(this.getDeltaMovement().reverse());
                    shield.hurtAndBreak(1, partEntity.getParent(), LivingEntity.getSlotForHand(partEntity.getParent().getUsedItemHand()));
                    world.playSound(null, pos, SoundEvents.SHIELD_BLOCK,
                            SoundSource.NEUTRAL, 1.0F, 1.0F);
                }
            } else if (this.getOwner() != null) {
                if (partEntity.getType().is(TagRegistry.FIREBALL_CAN_INSTAKILL))
                    partEntity.hurt(DamageSourceRegistry.fireball(entity, this.getOwner()), partEntity.getParent().getHealth() * 1.25F);
                else
                    partEntity.hurt(DamageSourceRegistry.fireball(entity, this.getOwner()), ConfigRegistry.FIREBALL_DAMAGE.get().floatValue());
                partEntity.igniteForSeconds(2.0F);
            }
            world.playSound(null, pos, SoundRegistry.FIREBALL_EXTINGUISHED.get(),
                    SoundSource.AMBIENT, 1.0F, 1.0F);
            world.gameEvent(entity, GameEvent.PROJECTILE_LAND, this.position());
            this.remove(RemovalReason.DISCARDED);
        } else if (entity instanceof MinecartTNT tnt)
            tnt.activateMinecart(0, 0, 0, Boolean.TRUE);
        else if (entity instanceof IceCubeEntity iceCube) {
            iceCube.shatterIceCube(this, false, false, false);
            world.playSound(null, pos, SoundRegistry.FIREBALL_EXTINGUISHED.get(),
                    SoundSource.AMBIENT, 1.0F, 1.0F);
            if (this.level() instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnParticleRingOnEntity(ParticleTypes.SMOKE, serverWorld, this, this.getBbWidth() / 2, 0.0, 10);
            world.gameEvent(entity, GameEvent.PROJECTILE_LAND, this.position());
            this.remove(RemovalReason.DISCARDED);
        } else if (entity.fireImmune() || entity.getType().is(TagRegistry.FIREBALL_IMMUNE)) {
            world.playSound(null, pos, SoundRegistry.FIREBALL_EXTINGUISHED.get(),
                    SoundSource.AMBIENT, 1.0F, 1.0F);
            if (this.level() instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnParticleRingOnEntity(ParticleTypes.SMOKE, serverWorld, this, this.getBbWidth() / 2, 0.0, 10);
            world.gameEvent(entity, GameEvent.PROJECTILE_LAND, this.position());
            this.remove(RemovalReason.DISCARDED);
        }

        if (entity instanceof Player player && !player.isSpectator() && !player.fireImmune() && player != this.getOwner()
                && !player.getType().is(TagRegistry.FIREBALL_IMMUNE)) {
            if (this.level() instanceof ServerLevel serverWorld) {
                ServerParticleUtils.spawnParticleRingOnEntity(ParticleTypes.SMOKE, serverWorld, this, this.getBbWidth() / 2, 0.0, 10);
                ServerParticleUtils.spawnParticleRingOnEntity(ParticleTypes.FLAME, serverWorld, this, this.getBbWidth() / 2, 0.1, 10);
            }
        } else if (entity instanceof LivingEntity livingEntity && !livingEntity.fireImmune() && livingEntity != this.getOwner()
                && !livingEntity.getType().is(TagRegistry.FIREBALL_IMMUNE)) {
            if (this.level() instanceof ServerLevel serverWorld) {
                ServerParticleUtils.spawnParticleRingOnEntity(ParticleTypes.SMOKE, serverWorld, this, this.getBbWidth() / 2, 0.0, 10);
                ServerParticleUtils.spawnParticleRingOnEntity(ParticleTypes.FLAME, serverWorld, this, this.getBbWidth() / 2, 0.1, 10);
            }
        }
    }

    @Override
    public boolean deflect(@NotNull ProjectileDeflection deflection, @Nullable Entity entity, @Nullable Entity owner, boolean shouldDeflect) {
        if (entity instanceof LivingEntity) {
            if (!this.level().isClientSide) {
                deflection.deflect(this, entity, this.random);
                this.setOwner(entity);
                this.onDeflection(entity, shouldDeflect);
                return true;
            }
        }
        return false;
    }

    public void bounceEffects(Level world, BlockPos pos) {
        if (this.level() instanceof ServerLevel serverWorld)
            ServerParticleUtils.spawnParticleRingBelowEntity(ParticleTypes.SMOKE, serverWorld, this, this.getBbWidth() / 2, 0.0, 10);
        world.playSound(null, pos, SoundRegistry.FIREBALL_SIZZLES.get(),
                SoundSource.AMBIENT, 1.0F, 1.0F);
        world.gameEvent(this.getOwner(), GameEvent.PROJECTILE_LAND, pos);
    }

    public void discardEffects() {
        if (this.level() instanceof ServerLevel serverWorld)
            ServerParticleUtils.spawnParticleRingOnEntity(ParticleTypes.SMOKE, serverWorld, this, this.getBbWidth() / 2, 0.0, 10);
        this.level().playSound(null, this.blockPosition(), SoundRegistry.FIREBALL_EXTINGUISHED.get(),
                SoundSource.AMBIENT, 1.0F, 1.0F);
        this.level().gameEvent(this.getOwner(), GameEvent.PROJECTILE_LAND, this.position());
        this.remove(RemovalReason.DISCARDED); // Despawn
    }

    public void discardEffectsOnSideHit(Level world, BlockPos hitPos) {
        if (this.level() instanceof ServerLevel serverWorld) {
            ServerParticleUtils.spawnParticleRingOnEntity(ParticleTypes.SMOKE, serverWorld, this, this.getBbWidth() / 2, 0.0, 10);
            ServerParticleUtils.spawnParticleRingOnEntity(ParticleTypes.FLAME, serverWorld, this, this.getBbWidth() / 2, 0.1, 10);
            if (this.level().getFluidState(this.blockPosition()).is(FluidTags.WATER))
                ServerParticleUtils.spawnParticleRingOnEntity(ParticleTypes.BUBBLE, serverWorld, this, this.getBbWidth() / 2, 0.1, 10);
        }
        world.playSound(null, this.blockPosition(), SoundRegistry.FIREBALL_EXTINGUISHED.get(), SoundSource.AMBIENT, 1.0F, 1.0F);
        world.gameEvent(this.getOwner(), GameEvent.PROJECTILE_LAND, hitPos);
        this.remove(RemovalReason.DISCARDED); // Despawn on side hit
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
