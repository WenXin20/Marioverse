package com.wenxin2.marioverse.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.WarpDoorBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpTrapDoorBlockEntity;
import com.wenxin2.marioverse.entities.IceCubeEntity;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.EntityWarpEntityHandler;
import com.wenxin2.marioverse.utils.BlockWarpEntityHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements BlockWarpEntityHandler, EntityWarpEntityHandler {
    @Shadow public abstract Level level();
    @Shadow public abstract double getX();
    @Shadow public abstract double getY();
    @Shadow public abstract double getZ();
    @Shadow public abstract float getBbHeight();
    @Shadow public abstract int getId();
    @Shadow public abstract BlockPos blockPosition();
    @Shadow public abstract EntityType<?> getType();
    @Shadow public abstract void setPos(Vec3 vec3);
    @Unique protected float mv$appliedHeightScale = 1.0F;
    @Unique protected float mv$appliedWidthScale = 1.0F;
    @Unique private boolean mv$preventWarp;
    @Unique private int mv$preventWarpCooldown;
    @Unique private int mv$warpCooldown;

    @Override
    public boolean mv$getBlockWarpTeleportConfig() {
        return ConfigRegistry.TELEPORT_NON_MOBS.get();
    }

    @Override
    public boolean mv$getEntityWarpTeleportConfig() {
        return ConfigRegistry.TELEPORT_NON_MOBS.get();
    }

    @Inject(method = "save", at = @At("TAIL"))
    public void save(CompoundTag tag, CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;

        if (!entity.getType().is(TagRegistry.CANNOT_WARP)
                && ConfigRegistry.TELEPORT_NON_MOBS.get()) {
            tag.putBoolean("marioverse:prevent_warp", this.mv$doPreventWarp());
            tag.putInt("marioverse:warp_cooldown", this.mv$getWarpCooldown());
        }
    }

    @Inject(method = "load", at = @At("TAIL"))
    public void load(CompoundTag tag, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;

        if (!entity.getType().is(TagRegistry.CANNOT_WARP)
                && ConfigRegistry.TELEPORT_NON_MOBS.get()) {
            this.mv$setPreventWarp(tag.getBoolean("marioverse:prevent_warp"));
            this.mv$setWarpCooldown(tag.getInt("marioverse:warp_cooldown"));
        }
    }

    @Inject(at = @At("TAIL"), method = "tick")
    public void tick(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        Level world = entity.level();
        BlockPos pos = entity.blockPosition();
        BlockPos posAboveEntity = pos.above(Math.round(entity.getBbHeight()));
        BlockPos posInBlock = pos.above(Math.round(entity.getBbHeight()) - 1);
        BlockState state = world.getBlockState(pos);
        BlockState stateAboveEntity = world.getBlockState(posAboveEntity);
        BlockState stateInBlock = world.getBlockState(posInBlock);

        if (this.mv$getWarpCooldown() > 0)
            this.mv$setWarpCooldown(this.mv$getWarpCooldown() - 1);

        mv$rideIceCube(entity);

        for (Direction facing : Direction.values()) {
            BlockPos offsetPos = pos.relative(facing);
            BlockState offsetState = world.getBlockState(offsetPos);

            if (!this.mv$doPreventWarp() || entity instanceof Player) {
                if (offsetState.getBlock() instanceof WarpPipeBlock && !offsetState.getValue(WarpPipeBlock.CLOSED))
                    this.enterWarp(entity, world, offsetPos);
                if (state.getBlock() instanceof WarpPipeBlock && !state.getValue(WarpPipeBlock.CLOSED))
                    this.enterWarp(entity, world, pos);
            }
        }

        if (stateAboveEntity.getBlock() instanceof WarpPipeBlock && !stateAboveEntity.getValue(WarpPipeBlock.CLOSED)
                && !this.mv$doPreventWarp())
            this.enterWarp(entity, world, pos);

        if (!ConfigRegistry.DISABLE_WARP_DOORS.get()
                && world.getBlockEntity(pos) instanceof WarpDoorBlockEntity
                && state.getBlock() instanceof DoorBlock && state.getValue(DoorBlock.OPEN)
                && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER
                && !this.mv$doPreventWarp())
            this.enterWarp(entity, world, pos);

        if (!ConfigRegistry.DISABLE_WARP_TRAPDOORS.get()
                && world.getBlockEntity(pos) instanceof WarpTrapDoorBlockEntity
                && state.getBlock() instanceof TrapDoorBlock && state.getValue(TrapDoorBlock.OPEN)
                && !this.mv$doPreventWarp())
            this.enterWarp(entity, world, pos);

        if (!ConfigRegistry.DISABLE_WARP_TRAPDOORS.get()
                && world.getBlockEntity(posInBlock) instanceof WarpTrapDoorBlockEntity
                && stateInBlock.getBlock() instanceof TrapDoorBlock && stateInBlock.getValue(TrapDoorBlock.OPEN)
                && !this.mv$doPreventWarp())
            this.enterWarp(entity, world, posInBlock);

        if (!ConfigRegistry.DISABLE_WARP_PAINTINGS.get()
                && !this.mv$doPreventWarp()) {
            this.enterWarp(entity, world);
        }

        float f6 = this.mv$getHeightScale();
        if (f6 != this.mv$appliedHeightScale) {
            this.mv$appliedHeightScale = f6;
            entity.refreshDimensions();
        }

        float f7 = this.mv$getWidthScale();
        if (f7 != this.mv$appliedWidthScale) {
            this.mv$appliedWidthScale = f6;
            entity.refreshDimensions();
        }
    }

    @ModifyReturnValue(method = "isInWaterOrBubble", at = @At("RETURN"))
    private boolean isInWaterOrBubble(boolean original) {
        BlockState state = this.level().getBlockState(this.blockPosition());
        if (!original) {
            if (state.is(BlockRegistry.PIPE_BUBBLES.get()))
                return true;
        }
        return original;
    }

    @ModifyReturnValue(method = "isInWaterRainOrBubble", at = @At("RETURN"))
    private boolean isInWaterRainOrBubble(boolean original) {
        BlockState state = this.level().getBlockState(this.blockPosition());
        if (!original) {
            if (state.is(BlockRegistry.PIPE_BUBBLES.get()))
                return true;
        }
        return original;
    }

    @Override
    public boolean mv$doPreventWarp() {
        return this.mv$preventWarp;
    }

    @Override
    public void mv$setPreventWarp(boolean preventWarp) {
        this.mv$preventWarp = preventWarp;
    }

    @Override
    public int mv$getPreventWarpCooldown() {
        return this.mv$preventWarpCooldown;
    }

    @Override
    public void mv$setPreventWarpCooldown(int preventWarpCooldown) {
        this.mv$preventWarpCooldown = preventWarpCooldown;
    }

    @Override
    public int mv$getWarpCooldown() {
        return this.mv$warpCooldown;
    }

    @Override
    public void mv$setWarpCooldown(int warpCooldown) {
        this.mv$warpCooldown = warpCooldown;
    }

    @Inject(method = "handleEntityEvent", at = @At("HEAD"))
    private void handleEntityEvent(byte id, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        RandomSource random = entity.getRandom();

        if (id == 120) {
            for(int i = 0; i < 100; ++i) {
                this.level().addParticle(ParticleTypes.ENCHANT,
                        entity.getRandomX(0.5D), entity.getRandomY(), entity.getRandomZ(0.5D),
                        (random.nextDouble() - 0.5D) * 2.0D, -random.nextDouble(),
                        (random.nextDouble() - 0.5D) * 2.0D);
            }
        }
    }

    @Inject(method = "getBbHeight", at = @At("HEAD"), cancellable = true)
    private void getBbHeight(CallbackInfoReturnable<Float> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity livingEntity) {
            AttributeMap attributeMap = livingEntity.getAttributes();
            if (attributeMap != null) {
                float height = entity.getDimensions(entity.getPose()).height();
                cir.setReturnValue(height);
            }
        }
    }

    @Inject(method = "getBbWidth", at = @At("HEAD"), cancellable = true)
    private void getBbWidth(CallbackInfoReturnable<Float> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity livingEntity) {
            AttributeMap attributeMap = livingEntity.getAttributes();
            if (attributeMap != null) {
                float width = entity.getDimensions(entity.getPose()).width();
                cir.setReturnValue(width);
            }
        }
    }

    @Inject(method = "isInWall", at = @At("HEAD"), cancellable = true)
    public void modifyIsInWall(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity.noPhysics) {
            cir.setReturnValue(false);
            return;
        }

        if (entity instanceof LivingEntity livingEntity) {
            AttributeMap attributeMap = livingEntity.getAttributes();
            if (attributeMap != null) {
                float widthScale = (float) attributeMap.getValue(AttributesRegistry.WIDTH_SCALE);

                if (widthScale != 1.0F) {
                    float scaledWidth = entity.getDimensions(entity.getPose()).width() * 0.8F * widthScale;
                    AABB aabb = AABB.ofSize(entity.getEyePosition(), scaledWidth, 1.0E-6, scaledWidth);

                    boolean isInWall = BlockPos.betweenClosedStream(aabb)
                            .anyMatch(
                                    pos -> {
                                        BlockState blockState = entity.level().getBlockState(pos);
                                        return !blockState.isAir()
                                                && blockState.isSuffocating(entity.level(), pos)
                                                && Shapes.joinIsNotEmpty(
                                                blockState.getCollisionShape(entity.level(), pos)
                                                        .move(pos.getX(), pos.getY(), pos.getZ()),
                                                Shapes.create(aabb), BooleanOp.AND
                                        );
                                    }
                            );
                    cir.setReturnValue(isInWall);
                } else cir.setReturnValue(cir.getReturnValue());
            }
        }
    }

    @Unique
    private static void mv$rideIceCube(Entity entity) {
        Entity belowEntity = null;
        for (Entity e : entity.level().getEntities(entity, entity.getBoundingBox().move(0, -1, 0))) {
            if (e instanceof IceCubeEntity) {
                belowEntity = e;
                break;
            }
        }

        if (belowEntity instanceof IceCubeEntity iceCube) {
            Vec3 iceMovement = iceCube.getDeltaMovement();

            if (!iceMovement.equals(Vec3.ZERO)) {
                entity.setDeltaMovement(iceMovement.x, 0, iceMovement.z);
                entity.move(MoverType.SELF, iceMovement);

                if (entity instanceof Mob mob) {
                    mob.getNavigation().stop();
                }
            }
        }
    }

    @Unique
    public float mv$getHeightScale() {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity livingEntity) {
            AttributeMap attributeMap = livingEntity.getAttributes();
            return attributeMap == null ? 1.0F : this.mv$sanitizeScales((float) attributeMap.getValue(AttributesRegistry.HEIGHT_SCALE));
        }
        return 1.0F;
    }

    @Unique
    public float mv$getWidthScale() {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity livingEntity) {
            AttributeMap attributeMap = livingEntity.getAttributes();
            return attributeMap == null ? 1.0F : this.mv$sanitizeScales((float) attributeMap.getValue(AttributesRegistry.WIDTH_SCALE));
        }
        return 1.0F;
    }

    @Unique
    public float mv$sanitizeScales(float scale) {
        return scale;
    }
}