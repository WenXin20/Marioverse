package com.wenxin2.marioverse.entities.projectiles;

import com.wenxin2.marioverse.entities.part_entities.PiranhaPlantPart;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.DamageTypeRegistry;
import com.wenxin2.marioverse.init.ParticleRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import com.wenxin2.marioverse.integration.CompatRegistry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BouncingIceBallProjectile extends ThrowableProjectile implements GeoEntity {
    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.bouncing_ice_ball.idle");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private String BOUNCE_COUNT = "marioverse:ice_ball_bounce_count";

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
        Vec3 motion = this.getDeltaMovement();

        if (!this.getPersistentData().contains(BOUNCE_COUNT))
            this.getPersistentData().putInt(BOUNCE_COUNT, 0);

        if (!this.isInWater()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.04D, 0)); // Gravity
        } else {
            this.setDeltaMovement(this.getDeltaMovement().add(motion.x, -0.04D, motion.y)); // Gravity
        }

        if (motion.length() > 0) {
            this.setYRot((float) Math.toDegrees(Math.atan2(motion.z, motion.x)) + 270);
            this.setXRot((float) Math.toDegrees(Math.atan2(-motion.y, Math.sqrt(motion.x * motion.x + motion.z * motion.z))));
        }

        if (this.onGround() || this.tickCount > 400) {
            if (!this.level().isClientSide) {
                this.level().broadcastEntityEvent(this, (byte) 60); // Smoke particle
            }
            this.level().playSound(null, this.blockPosition(), SoundRegistry.FIREBALL_EXTINGUISHED.get(),
                    SoundSource.AMBIENT, 1.0F, 1.0F);
            this.discard(); // Despawn
        }

        for (int i = 0; i < 1; i++) {
            double x = this.getX();
            double y = this.getY() + this.getBbHeight() / 2;
            double z = this.getZ();
            this.level().addParticle(ParticleTypes.SNOWFLAKE, x, y, z, 0, 0, 0);
            this.level().addParticle(ParticleRegistry.ICE_STAR.get(), x, y, z, 0, 0, 0);
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

        if (hit.getDirection().getAxis() == Direction.Axis.X || hit.getDirection().getAxis() == Direction.Axis.Z) {
            if (!world.isClientSide)
                world.broadcastEntityEvent(this, (byte) 60); // Smoke particle
            world.playSound(null, this.blockPosition(), SoundEvents.GLASS_BREAK,
                    SoundSource.AMBIENT, 1.0F, 1.0F);
            this.discard(); // Despawn on side hit
        } else if (this.getPersistentData().getInt(BOUNCE_COUNT) < ConfigRegistry.MAX_ICE_BALL_BOUNCES.get()) {
            Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x, 0.5, motion.z); // Bounce
            world.broadcastEntityEvent(this, (byte) 60); // Snow particle
            world.playSound(null, this.blockPosition(), SoundRegistry.FIREBALL_SIZZLES.get(),
                    SoundSource.AMBIENT, 1.0F, 1.0F);

            if (this.getPersistentData().contains(BOUNCE_COUNT))
                this.getPersistentData().putInt(BOUNCE_COUNT, this.getPersistentData().getInt(BOUNCE_COUNT) + 1);
        } else {
            if (!world.isClientSide)
                world.broadcastEntityEvent(this, (byte) 60); // Smoke particle
            world.playSound(null, this.blockPosition(), SoundEvents.GLASS_BREAK,
                    SoundSource.AMBIENT, 1.0F, 1.0F);
            this.discard();
        }

        if (state.is(TagRegistry.FREEZES_INTO_ICE)) {
            if (state.getBlock() == Blocks.WATER && stateAbove.getBlock() != Blocks.WATER && stateAbove.canBeReplaced())
                world.setBlock(hitPos, Blocks.ICE.defaultBlockState(), 3);
            else if (state.getBlock() != Blocks.WATER)
                world.setBlock(hitPos, Blocks.ICE.defaultBlockState(), 3);
        } else if (state.is(TagRegistry.FREEZES_INTO_PACKED_ICE))
            world.setBlock(hitPos, Blocks.ICE.defaultBlockState(), 3);
        else if (state.is(TagRegistry.MELTS_INTO_PACKED_ICE))
            world.setBlock(hitPos, Blocks.PACKED_ICE.defaultBlockState(), 3);
        else if (state.getBlock() instanceof CampfireBlock)
            world.setBlock(hitPos, state.setValue(CampfireBlock.LIT, Boolean.FALSE), 3);
        else if (state.getBlock() instanceof CandleBlock)
            world.setBlock(hitPos, state.setValue(CandleBlock.LIT, Boolean.FALSE), 3);
        else if (state.getBlock() instanceof CandleCakeBlock)
            world.setBlock(hitPos, state.setValue(CandleCakeBlock.LIT, Boolean.FALSE), 3);
        else if (state.is(CompatRegistry.CANDLE_HOLDERS_BLOCK_TAG))
            world.setBlock(hitPos, state.setValue(BlockStateProperties.LIT, Boolean.FALSE), 3);
        super.onHitBlock(hit);
    }

    @Override
    protected void onHitEntity(EntityHitResult hit) {
        Entity entity = hit.getEntity();
        if (!this.level().isClientSide) {
            if (entity instanceof Player player && !player.isSpectator() && !player.fireImmune() && player != this.getOwner()
                    && !player.getType().is(TagRegistry.ICE_BALL_IMMUNE)) {
                ItemStack shield = player.getUseItem();
                if (this.getOwner() != null && player.getTeam() != null && this.getOwner().getTeam() != null
                        && player.getTeam() == this.getOwner().getTeam())
                    return;

                if (this.getOwner() != null && player.isDamageSourceBlocked(DamageTypeRegistry.iceBall(entity, this.getOwner()))) {
                    if (shield.getItem() instanceof ShieldItem || player.getPersistentData().getBoolean("marioverse:has_fire_flower")) {
                        this.deflect(ProjectileDeflection.REVERSE, this.getOwner(), this.getOwner(), true);
                        this.setDeltaMovement(this.getDeltaMovement().reverse());
                        shield.hurtAndBreak(1, player, Player.getSlotForHand(player.getUsedItemHand()));
                        this.level().playSound(null, this.blockPosition(), SoundEvents.SHIELD_BLOCK,
                                SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                } else if (this.getOwner() != null) {
                    if (player.getType().is(TagRegistry.ICE_BALL_CAN_INSTAKILL))
                        player.hurt(DamageTypeRegistry.iceBall(entity, this.getOwner()), player.getHealth());
                    else player.hurt(DamageTypeRegistry.iceBall(entity, this.getOwner()), 2.0F); //TODO
                }
                this.level().playSound(null, this.blockPosition(), SoundEvents.GLASS_BREAK,
                        SoundSource.AMBIENT, 1.0F, 1.0F);
                this.remove(RemovalReason.KILLED);
            } else if (entity instanceof LivingEntity livingEntity && !livingEntity.fireImmune() && livingEntity != this.getOwner()
                    && !livingEntity.getType().is(TagRegistry.ICE_BALL_IMMUNE)) {
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
                        this.level().playSound(null, this.blockPosition(), SoundEvents.SHIELD_BLOCK,
                                SoundSource.NEUTRAL, 1.0F, 1.0F);
                    }
                } else if (this.getOwner() != null) {
                    if (livingEntity.getType().is(TagRegistry.ICE_BALL_CAN_INSTAKILL))
                        livingEntity.hurt(DamageTypeRegistry.iceBall(entity, this.getOwner()), livingEntity.getHealth());
                    else livingEntity.hurt(DamageTypeRegistry.iceBall(entity, this.getOwner()), 2.0F); // TODO
                }
                this.level().playSound(null, this.blockPosition(), SoundEvents.GLASS_BREAK,
                        SoundSource.AMBIENT, 1.0F, 1.0F);
                this.remove(RemovalReason.KILLED);
            } else if (entity instanceof PiranhaPlantPart partEntity && !partEntity.fireImmune() && partEntity != this.getOwner()
                    && !partEntity.getType().is(TagRegistry.ICE_BALL_IMMUNE)) {
                ItemStack shield = partEntity.getParent().getUseItem();

                if (this.getOwner() != null && partEntity.getParent().isDamageSourceBlocked(DamageTypeRegistry.iceBall(entity, this.getOwner()))) {
                    if (shield.getItem() instanceof ShieldItem || partEntity.getPersistentData().getBoolean("marioverse:has_ice_flower")) {
                        this.deflect(ProjectileDeflection.REVERSE, this.getOwner(), this.getOwner(), true);
                        this.setDeltaMovement(this.getDeltaMovement().reverse());
                        shield.hurtAndBreak(1, partEntity.getParent(), LivingEntity.getSlotForHand(partEntity.getParent().getUsedItemHand()));
                        this.level().playSound(null, this.blockPosition(), SoundEvents.SHIELD_BLOCK,
                                SoundSource.NEUTRAL, 1.0F, 1.0F);
                    }
                } else if (this.getOwner() != null) {
                    if (partEntity.getType().is(TagRegistry.ICE_BALL_CAN_INSTAKILL))
                        partEntity.hurt(DamageTypeRegistry.iceBall(entity, this.getOwner()), partEntity.getParent().getHealth());
                    else partEntity.hurt(DamageTypeRegistry.iceBall(entity, this.getOwner()), 2.0F);
                    partEntity.igniteForSeconds(2.0F);
                }
                this.level().playSound(null, this.blockPosition(), SoundEvents.GLASS_BREAK,
                        SoundSource.AMBIENT, 1.0F, 1.0F);
                this.remove(RemovalReason.KILLED);
            } else if (entity instanceof MinecartTNT tnt)
                tnt.activateMinecart(0, 0, 0, Boolean.TRUE);
        }

        if (entity instanceof Player player && !player.isSpectator() && !player.fireImmune() && player != this.getOwner()
                && !player.getType().is(TagRegistry.ICE_BALL_IMMUNE)) {
            this.level().broadcastEntityEvent(this, (byte) 60); // Smoke particle
        } else if (entity instanceof LivingEntity livingEntity && !livingEntity.fireImmune() && livingEntity != this.getOwner()
                && !livingEntity.getType().is(TagRegistry.ICE_BALL_IMMUNE)) {
            this.level().broadcastEntityEvent(this, (byte) 60); // Smoke particle
        }
    }

    @Override
    public boolean deflect(@NotNull ProjectileDeflection deflection, @Nullable Entity entity, @Nullable Entity owner, boolean shouldDeflect) {
        if (entity instanceof LivingEntity livingEntity) {
            ItemStack shield = livingEntity.getUseItem();
            if (!this.level().isClientSide) {
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

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 60) {
            if (this.level().isClientSide) {
                int numParticles = 10; // Number of particles to spawn in the circle
                double radius = 0.2;  // Radius of the circle around the ice ball

                for (int i = 0; i < numParticles; i++) {
                    // Calculate angle for each particle
                    double angle = 2 * Math.PI * i / numParticles;

                    // Calculate the X and Z offset using sine and cosine to spread in a circle
                    double offsetX = Math.cos(angle) * radius;
                    double offsetY = Math.sin(angle) * radius;
                    double offsetZ = Math.sin(angle) * radius;

                    double x = this.getX() + offsetX;
                    double y = this.getY() + offsetY;
                    double z = this.getZ() + offsetZ;

                    this.level().addParticle(ParticleTypes.SNOWFLAKE, x, y, z, 0, 0, 0);
                }
            }
        } else if (id == 61) {
            if (this.level().isClientSide) {
                int numParticles = 10; // Number of particles to spawn in the circle
                double radius = 0.15;  // Radius of the circle around the ice ball

                for (int i = 0; i < numParticles; i++) {
                    // Calculate angle for each particle
                    double angle = 2 * Math.PI * i / numParticles;

                    // Calculate the X and Z offset using sine and cosine to spread in a circle
                    double offsetX = Math.cos(angle) * radius;
                    double offsetZ = Math.sin(angle) * radius;

                    double x = this.getX() + offsetX;
                    double y = this.getY() - this.getBbHeight();
                    double z = this.getZ() + offsetZ;

                    this.level().addParticle(ParticleTypes.SNOWFLAKE, x, y, z, 0, 0, 0);
                }
            }
        } else super.handleEntityEvent(id);
    }
}
