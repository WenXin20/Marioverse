package com.wenxin2.marioverse.entities.projectiles;

import com.wenxin2.marioverse.entities.part_entities.PiranhaPlantPart;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
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
import net.minecraft.world.entity.projectile.Projectile;
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
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class LargeSnowballProjectile extends ThrowableProjectile implements GeoEntity, TraceableEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public float prevVisualYaw;
    public float roll;
    public float rollVelocity;
    public float visualYaw;

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

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("VisualYaw", this.visualYaw);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.visualYaw = tag.getFloat("VisualYaw");
        this.prevVisualYaw = this.visualYaw;
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
    public boolean isPickable() {
        return true;
    }

    @Override
    public float getPickRadius() {
        return 0.01F;
    }

    @Override
    public float maxUpStep() {
        return 0.6F;
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
        float horizontalSpeed = (float) motion.horizontalDistance();

        this.stompSnowball();
        this.collideWithEntity();
        this.onHitFluid(world, this.blockPosition());
        this.prevVisualYaw = this.visualYaw;

        float targetYaw = (float) Math.atan2(motion.x, motion.z);
        this.visualYaw = Mth.lerp(0.25F, this.visualYaw, targetYaw);

        if (this.getDeltaMovement().horizontalDistance() > 0.01) {
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
            float reduceFriction = 1.0F;
            if (stateBelow.is(Blocks.SNOW_BLOCK)
                    || state.getBlock() instanceof SnowLayerBlock
                    || stateBelow.getBlock() instanceof SnowLayerBlock
                    || stateBelow.getBlock() instanceof PowderSnowBlock)
                reduceFriction *= 1.02F;
            double slideFriction = Math.max(0.98F, 0.6 + friction / 2.5);

            if (this.getDeltaMovement().horizontalDistance() > 0.0001) {
                this.setDeltaMovement(this.getDeltaMovement().x * slideFriction * reduceFriction,
                        this.getDeltaMovement().y, this.getDeltaMovement().z * slideFriction * reduceFriction);
                this.hasImpulse = true;
            }
        }

        if (this.getDeltaMovement().horizontalDistance() > 0.15) {
            float radius = 0.25F;
            float maxRollSpeed = 0.2F;
            this.rollVelocity = Mth.clamp(horizontalSpeed / radius, 0.0F, maxRollSpeed);
        } else {
            this.rollVelocity *= 0.95F;

            if (Math.abs(this.rollVelocity) < 0.01F) {
                float target = Math.round(this.roll / Mth.HALF_PI) * Mth.HALF_PI;
                this.roll = Mth.approach(this.roll, target, 0.05F);
                this.rollVelocity = 0.0F;
                return;
            }
        }
        this.roll += this.rollVelocity;
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        if (this.level().isClientSide || this.isRemoved()) {
            return true;
        } else if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Vec3 movement = new Vec3(this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z);

            if (source.getEntity() != null) {
                Vec3 attackerPos = source.getEntity().position();
                Vec3 hitPos = this.position();
                Vec3 slideDirRaw = hitPos.subtract(attackerPos).normalize();
                movement = new Vec3(slideDirRaw.x * 0.35D, this.getDeltaMovement().y, slideDirRaw.z * 0.35D).normalize();
            } else if (source.getDirectEntity() instanceof Projectile) {
                Vec3 direct = source.getDirectEntity().getDeltaMovement();
                double speed = direct.length();
                double strength = Mth.clamp(speed * 0.6D, 0.05D, 0.8D);

                movement = new Vec3(direct.x * strength, this.getDeltaMovement().y, direct.z * strength);
            } else if (source.getDirectEntity() != null) {
                Vec3 direct = source.getDirectEntity().getDeltaMovement();

                movement = new Vec3(direct.x * 0.35D, this.getDeltaMovement().y, direct.z * 0.35D);
            }

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

    public void discardEffectsOnSideHit(Level world, BlockPos hitPos, Direction direction) {
        BlockPos posRelative = hitPos.relative(direction);
        BlockState state = world.getBlockState(posRelative);
        BlockState stateAbove = world.getBlockState(posRelative.above());
        BlockState stateBelow = world.getBlockState(posRelative.below());

        if (world instanceof ServerLevel serverWorld)
            ServerParticleUtils.spawnParticleRingOnEntity(ParticleTypes.SNOWFLAKE, serverWorld, this, this.getBbWidth() / 2, 0.0, 15);
        world.playSound(null, this.blockPosition(), SoundEvents.SNOW_BREAK, SoundSource.AMBIENT);
        world.gameEvent(this.getOwner(), GameEvent.PROJECTILE_LAND, hitPos);

        if (state.getBlock() instanceof SnowLayerBlock && state.getValue(SnowLayerBlock.LAYERS) != 8) {
            int i = state.getValue(SnowLayerBlock.LAYERS);
            world.setBlock(posRelative, state.setValue(SnowLayerBlock.LAYERS, Math.min(8, i + 1)), 3);
        } else if (state.getBlock() instanceof SnowLayerBlock && stateAbove.getBlock() instanceof SnowLayerBlock
                && state.getValue(SnowLayerBlock.LAYERS) == 8) {
            int i = state.getValue(SnowLayerBlock.LAYERS);
            world.setBlock(posRelative.above(), state.setValue(SnowLayerBlock.LAYERS, Math.min(8, i + 1)), 3);
        } else if (state.canBeReplaced() && stateBelow.isSolid())
            world.setBlock(posRelative, Blocks.SNOW.defaultBlockState(), 3);

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
        Vec3 horizontal = this.getDeltaMovement().multiply(1.0, 0.0, 1.0);
        VoxelShape shape = state.getCollisionShape(world, hitPos);

        double entityBottomY = this.getBoundingBox().minY;
        double posTop = hitPos.getY() + shape.max(Direction.Axis.Y);
        double stepHeight = posTop - entityBottomY;

        if (hit.getDirection().getAxis().isHorizontal() && !shape.isEmpty() && stepHeight < this.maxUpStep()) {
            this.setPos(this.getX(), posTop + 0.2F, this.getZ());
            super.onHitBlock(hit);
        } else if (hit.getDirection().getAxis().isHorizontal() && state.isSolid()) {
            this.discardEffectsOnSideHit(world, hitPos, hit.getDirection());
            super.onHitBlock(hit);
        } else if (hit.getDirection() == Direction.DOWN)
            this.discardEffects(world);
        else if (hit.getDirection() == Direction.UP) {
            Vec3 correction = hit.getLocation().subtract(this.getX(), this.getY(), this.getZ());

            this.setDeltaMovement(correction);
            Vec3 back = correction.normalize().scale(0.05F);

            this.setPosRaw(this.getX() - back.x, this.getY() - back.y, this.getZ() - back.z);

            this.setDeltaMovement(horizontal.x, this.getDeltaMovement().y, horizontal.z);
            this.setSliding(true);
            this.hasImpulse = true;
        }

        if ((state.is(TagRegistry.SNOWBALL_EXTINGUISHES) || state.is(TagRegistry.MELTS_SNOWBALL)) && state.hasProperty(BlockStateProperties.LIT)
                && state.getValue(BlockStateProperties.LIT)) {
            if (this.level() instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnParticleRingOnBlock(ParticleTypes.SMOKE, serverWorld, hitPos, 0.25D, 15);
            world.setBlock(hitPos, state.setValue(BlockStateProperties.LIT, Boolean.FALSE), 3);
            world.playSound(null, hitPos, state.getBlock() instanceof CandleBlock || state.getBlock() instanceof CandleCakeBlock
                    ? SoundEvents.CANDLE_EXTINGUISH : SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
            this.discardEffects(world);
        } else if (state.is(TagRegistry.SNOWBALL_EXTINGUISHES) && state.getBlock() instanceof FireBlock) {
            if (this.level() instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnParticleRingOnBlock(ParticleTypes.SMOKE, serverWorld, hitPos, 0.25D, 15);
            world.removeBlock(hitPos, true);
            world.playSound(null, hitPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
            this.discardEffects(world);
        } else if (stateAbove.is(TagRegistry.SNOWBALL_EXTINGUISHES) && stateAbove.getBlock() instanceof FireBlock) {
            if (this.level() instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnParticleRingOnBlock(ParticleTypes.SMOKE, serverWorld, hitPos.above(), 0.25D, 15);
            world.removeBlock(hitPos.above(), true);
            world.playSound(null, hitPos.above(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
            this.discardEffects(world);
        } else if (state.is(TagRegistry.MELTS_SNOWBALL) && !state.hasProperty(BlockStateProperties.LIT)) {
            world.playSound(null, hitPos.above(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
            this.discardEffects(world);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult hit) {
        Level world = this.level();
        Entity entity = hit.getEntity();

        if (entity instanceof LivingEntity livingEntity
                && this.canHitEntity(livingEntity) && !livingEntity.getType().is(TagRegistry.SNOWBALL_IMMUNE)
                && !entity.getData(DataAttachmentRegistry.HAS_SUPER_STAR)) {
            ItemStack shield = livingEntity.getUseItem();
            if ((livingEntity instanceof TamableAnimal tamableAnimal
                    && tamableAnimal.getOwner() == this.getOwner())
                    || (this.getOwner() != null && livingEntity.getTeam() != null && this.getOwner().getTeam() != null
                    && livingEntity.getTeam() == this.getOwner().getTeam()))
                return;

            if (this.getOwner() != null && livingEntity.isDamageSourceBlocked(DamageSourceRegistry.largeSnowball(entity, this.getOwner())))
                this.deflectProjectile(livingEntity, shield, entity, world);
            else if (this.getOwner() != null) {
                if (livingEntity.getType().is(TagRegistry.SNOWBALL_CAN_INSTAKILL))
                    livingEntity.hurt(DamageSourceRegistry.largeSnowball(entity, this.getOwner()), livingEntity.getHealth() * 1.25F);
                else livingEntity.hurt(DamageSourceRegistry.largeSnowball(entity, this.getOwner()), ConfigRegistry.LARGE_SNOWBALL_DAMAGE.get().floatValue());
                if (entity.canFreeze())
                    entity.setTicksFrozen(ConfigRegistry.LARGE_SNOWBALL_FREEZE_DURATION.get());
                entity.setIsInPowderSnow(true);
                livingEntity.extinguishFire();
            }
            world.playSound(null, this.blockPosition(), SoundEvents.SNOW_BREAK, SoundSource.AMBIENT);
            world.gameEvent(entity, GameEvent.PROJECTILE_LAND, entity.position());
            this.remove(RemovalReason.DISCARDED);
        } else if (entity instanceof PiranhaPlantPart partEntity
                && this.canHitEntity(partEntity) && !partEntity.getType().is(TagRegistry.SNOWBALL_IMMUNE)
                && !entity.getData(DataAttachmentRegistry.HAS_SUPER_STAR)) {
            ItemStack shield = partEntity.getParent().getUseItem();

            if (this.getOwner() != null && partEntity.getParent().isDamageSourceBlocked(DamageSourceRegistry.largeSnowball(entity, this.getOwner())))
                this.deflectProjectile(partEntity.getParent(), shield, entity, world);
            else if (this.getOwner() != null) {
                if (partEntity.getType().is(TagRegistry.SNOWBALL_CAN_INSTAKILL))
                    partEntity.hurt(DamageSourceRegistry.largeSnowball(entity, this.getOwner()), partEntity.getParent().getHealth() * 1.25F);
                else partEntity.hurt(DamageSourceRegistry.largeSnowball(entity, this.getOwner()), ConfigRegistry.LARGE_SNOWBALL_DAMAGE.get().floatValue());
                if (entity.canFreeze())
                    entity.setTicksFrozen(ConfigRegistry.LARGE_SNOWBALL_FREEZE_DURATION.get());
                entity.setIsInPowderSnow(true);
                partEntity.extinguishFire();
            }
            world.playSound(null, this.blockPosition(), SoundEvents.SNOW_BREAK,
                    SoundSource.AMBIENT, 1.0F, 1.0F);
            world.gameEvent(entity, GameEvent.PROJECTILE_LAND, entity.position());
            this.remove(RemovalReason.DISCARDED);
        } else if (entity instanceof BouncingFireballProjectile fireball) {
            fireball.kill();
            world.gameEvent(entity, GameEvent.PROJECTILE_LAND, entity.position());
            world.playSound(null, this.blockPosition(), SoundEvents.SNOW_HIT,
                    SoundSource.AMBIENT, 1.0F, 1.0F);
            this.remove(RemovalReason.DISCARDED);
        } else if (!(entity instanceof LargeSnowballProjectile)) {
            world.gameEvent(entity, GameEvent.PROJECTILE_LAND, entity.position());
            world.playSound(null, this.blockPosition(), SoundEvents.SNOW_HIT,
                    SoundSource.AMBIENT, 1.0F, 1.0F);
            this.remove(RemovalReason.DISCARDED);
        }

        if (world instanceof ServerLevel serverWorld) {
            if (entity instanceof Player player && !player.isSpectator() && player.canFreeze() && this.canHitEntity(player)
                    && !player.getType().is(TagRegistry.SNOWBALL_CAN_INSTAKILL)
                    && !entity.getData(DataAttachmentRegistry.HAS_SUPER_STAR)) {
                ServerParticleUtils.spawnParticleRingOnEntity(ParticleTypes.SNOWFLAKE, serverWorld, this, this.getBbWidth() / 2, 0.0, 10);
            } else if (entity instanceof LivingEntity livingEntity && livingEntity.canFreeze() && this.canHitEntity(livingEntity)
                    && !livingEntity.getType().is(TagRegistry.SNOWBALL_IMMUNE)
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
        if (shield.getItem() instanceof ShieldItem) {
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

    public void stompSnowball() {
        Level world = this.level();
        BlockPos pos = this.blockPosition();

        List<Entity> nearbyEntities = world.getEntities(this, this.getBoundingBox()
                .inflate(0.5, 1.25, 0.5));

        if (nearbyEntities.isEmpty())
            return;

        for (Entity entity : nearbyEntities) {
            if (!(entity instanceof LivingEntity stompingEntity))
                continue;
            if (stompingEntity instanceof Player)
                continue;
            if (stompingEntity.isSpectator())
                continue;
            if (!(stompingEntity.fallDistance > 0 || stompingEntity.isInWaterOrBubble()))
                continue;
            if (stompingEntity.getY() < this.getY() + this.getBbHeight())
                continue;
            if (stompingEntity.getData(DataAttachmentRegistry.HAS_SUPER_STAR))
                return;

            double bounceBlockHeight = ConfigRegistry.STOMP_BOUNCE_HEIGHT.getAsDouble();
            double gravity = 0.08;
            double bounceVelocity = Math.sqrt(2 * gravity * bounceBlockHeight);

            stompingEntity.setDeltaMovement(stompingEntity.getDeltaMovement().x, bounceVelocity, stompingEntity.getDeltaMovement().z);
            stompingEntity.hasImpulse = true;

            if (stompingEntity instanceof ServerPlayer serverPlayer)
                serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(stompingEntity));

            double radius = this.getBbWidth() / 2;
            int numParticles = Math.max(5, (int) (this.getBbWidth() * 20));

            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnParticleRingAboveEntity(ParticleTypes.CRIT, serverWorld, this, radius, 0, numParticles);

            this.discardEffects(world);
            break;
        }
    }

}
