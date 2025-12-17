package com.wenxin2.marioverse.entities.projectiles;

import com.wenxin2.marioverse.entities.part_entities.PiranhaPlantPart;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
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
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
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
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

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
    public float getPickRadius() {
        return 0.01F;
    }

    @Override
    public void tick() {
        super.tick();
        Level world = this.level();
        Vec3 motion = this.getDeltaMovement();
        BlockPos pos = this.blockPosition();
        BlockPos posBelow = this.blockPosition().below();
        BlockState state = world.getBlockState(pos);
        BlockState stateBelow = world.getBlockState(posBelow);

        this.collideWithEntity();
        this.onHitFluid(world, this.blockPosition());

        if (this.getDeltaMovement().horizontalDistance() > 0.001) {
            for (int i = 0; i < 1; i++) {
                double x = this.getX();
                double y = this.getY() + this.getBbHeight() / 2;
                double z = this.getZ();
                world.addParticle(ParticleTypes.SNOWFLAKE, x, y, z, 0, 0, 0);
            }
        }

        if (motion.lengthSqr() > 0.0001) {
            this.setYRot((float) Math.toDegrees(Math.atan2(-motion.x, motion.z)));
            this.setXRot((float) Math.toDegrees(Math.atan2(-motion.y, Math.sqrt(motion.x * motion.x + motion.z * motion.z))));
        }

        if (this.isSliding()) {
            float friction = stateBelow.getFriction(world, posBelow, this);
            if (stateBelow.is(Blocks.SNOW_BLOCK)
                    || state.getBlock() instanceof SnowLayerBlock
                    || stateBelow.getBlock() instanceof SnowLayerBlock
                    || stateBelow.getBlock() instanceof PowderSnowBlock)
                friction *= 0.4F;
            double slideFriction = Math.max(0.98F, 0.6 + friction / 2.5);

            if (this.getDeltaMovement().horizontalDistance() > 0.0001) {
                this.setDeltaMovement(this.getDeltaMovement().x * slideFriction, this.getDeltaMovement().y, this.getDeltaMovement().z * slideFriction);
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
                slideSpeed = 0.4 + friction / 2.5;
            else slideSpeed = 1.0;

            Vec3 slideDirection = new Vec3(this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z);

            if (source.getEntity() != null) {
                Vec3 attackerPos = source.getEntity().position();
                Vec3 hitPos = this.position();
                Vec3 slideDirRaw = hitPos.subtract(attackerPos).normalize();
                slideDirection = new Vec3(slideDirRaw.x, this.getDeltaMovement().y, slideDirRaw.z).normalize();
            } else if (source.getDirectEntity() != null)
                slideDirection = source.getDirectEntity().getDeltaMovement().normalize();

            Vec3 movement = slideDirection.scale(friction);

            this.setDeltaMovement(movement.x, this.getDeltaMovement().y, movement.z);
            this.hasImpulse = true;
            this.setOwner(source.getEntity());
            this.setSliding(true);

            return true;
        }
    }

    public void discardEffects(Level world) {
        BlockPos pos = this.blockPosition();

        if (this.level() instanceof ServerLevel serverWorld)
            ServerParticleUtils.spawnParticleRingOnEntity(ParticleTypes.SNOWFLAKE, serverWorld, this, this.getBbWidth() / 2, 0.0, 10);
        world.playSound(null, pos, SoundEvents.SNOW_BREAK, SoundSource.AMBIENT);
        world.gameEvent(this.getOwner(), GameEvent.PROJECTILE_LAND, pos);
        this.remove(RemovalReason.DISCARDED);
    }

    public void discardEffectsOnSideHit(Level world, BlockPos hitPos) {
        BlockPos pos = this.blockPosition();
        BlockState state = world.getBlockState(pos);
        BlockState stateAbove = world.getBlockState(pos.above());
        BlockState stateBelow = world.getBlockState(pos.below());

        if (world instanceof ServerLevel serverWorld)
            ServerParticleUtils.spawnParticleRingOnEntity(ParticleTypes.SNOWFLAKE, serverWorld, this, this.getBbWidth() / 2, 0.0, 15);
        world.playSound(null, this.blockPosition(), SoundEvents.SNOW_BREAK, SoundSource.AMBIENT);
        world.gameEvent(this.getOwner(), GameEvent.PROJECTILE_LAND, hitPos);

        if (state.getBlock() instanceof SnowLayerBlock && state.getValue(SnowLayerBlock.LAYERS) != 8) {
            int i = state.getValue(SnowLayerBlock.LAYERS);
            world.setBlock(pos, state.setValue(SnowLayerBlock.LAYERS, Math.min(8, i + 1)), 3);
        } else if (state.getBlock() instanceof SnowLayerBlock && stateAbove.getBlock() instanceof SnowLayerBlock
                && state.getValue(SnowLayerBlock.LAYERS) == 8) {
            int i = state.getValue(SnowLayerBlock.LAYERS);
            world.setBlock(pos.above(), state.setValue(SnowLayerBlock.LAYERS, Math.min(8, i + 1)), 3);
        } else if (!state.isSolid() && stateBelow.isSolid())
            world.setBlock(pos, Blocks.SNOW.defaultBlockState(), 3);

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
        BlockPos posBelow = this.blockPosition().below();
        BlockState stateBelow = world.getBlockState(posBelow);
        Vec3 horizontal = this.getDeltaMovement().multiply(1.0, 0.0, 1.0);

        if (hit.getDirection().getAxis() == Direction.Axis.X || hit.getDirection().getAxis() == Direction.Axis.Z)
            this.discardEffectsOnSideHit(world, hitPos);
        else if (hit.getDirection().getAxis() == Direction.Axis.Y) {
            Vec3 correction = hit.getLocation().subtract(this.getX(), this.getY(), this.getZ());

            this.setDeltaMovement(correction);
            Vec3 back = correction.normalize().scale(0.05F);

            this.setPosRaw(this.getX() - back.x, this.getY() - back.y, this.getZ() - back.z);

            this.setDeltaMovement(horizontal.x, this.getDeltaMovement().y, horizontal.z);
            this.setSliding(true);
            this.hasImpulse = true;
        }

        if (state.is(TagRegistry.ICE_BALL_EXTINGUISHES) && state.hasProperty(BlockStateProperties.LIT)
                && state.getValue(BlockStateProperties.LIT)) { // TODO new tag
            if (this.level() instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnParticleRingOnBlock(ParticleTypes.SMOKE, serverWorld, hitPos, 0.25D, 15);
            world.setBlock(hitPos, state.setValue(BlockStateProperties.LIT, Boolean.FALSE), 3);
            world.playSound(null, hitPos, state.getBlock() instanceof CandleBlock || state.getBlock() instanceof CandleCakeBlock
                    ? SoundEvents.CANDLE_EXTINGUISH : SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
            this.discardEffects(world);
        } else if (state.is(TagRegistry.ICE_BALL_EXTINGUISHES) && state.getBlock() instanceof FireBlock) {
            if (this.level() instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnParticleRingOnBlock(ParticleTypes.SMOKE, serverWorld, hitPos, 0.25D, 15);
            world.removeBlock(hitPos, true);
            world.playSound(null, hitPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
            this.discardEffects(world);
        } else if (stateAbove.is(TagRegistry.ICE_BALL_EXTINGUISHES) && stateAbove.getBlock() instanceof FireBlock) {
            if (this.level() instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnParticleRingOnBlock(ParticleTypes.SMOKE, serverWorld, hitPos.above(), 0.25D, 15);
            world.removeBlock(hitPos.above(), true);
            world.playSound(null, hitPos.above(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
            this.discardEffects(world);
        }
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

    @NotNull
    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (this.getDeltaMovement().horizontalDistance() < 0.1) {
            ItemStack stack = new ItemStack(ItemRegistry.LARGE_SNOWBALL.get());

            if (player.getItemInHand(hand).isEmpty())
                player.setItemInHand(hand, stack);
            else if (player.getItemInHand(hand).equals(stack)
                    && player.getItemInHand(hand).getCount() < player.getItemInHand(hand).getMaxStackSize())
                stack.grow(1);
            else {
                boolean itemAdded = player.addItem(stack.copyWithCount(1));
                if (!itemAdded)
                    player.drop(stack.copyWithCount(1), false);
            }

            player.level().playSound(player, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS);
            this.discard();
            return InteractionResult.SUCCESS;
        } else return super.interact(player, hand);
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
}
