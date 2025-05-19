package com.wenxin2.marioverse.blocks.entities;

import com.wenxin2.marioverse.entities.PiranhaPlantEntity;
import com.wenxin2.marioverse.registries.BlockEntityRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PottedPiranhaPlantBlockEntity extends BlockEntity implements GeoBlockEntity, TraceableEntity {
    public static final RawAnimation BITE = RawAnimation.begin().thenPlay("attack.bite");
    public static final RawAnimation CONSTANT_BITES = RawAnimation.begin().thenLoop("piranha_plant.constant_bite");
    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("piranha_plant.idle");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public int attackCooldown = 0;

    @Nullable private UUID ownerUUID;
    @Nullable private Entity cachedOwner;
    private boolean leftOwner;

    public PottedPiranhaPlantBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.POTTED_PIRANHA_PLANT_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle", 10, this::biteAnimation));
        controllers.add(new AnimationController<>(this, "bite_controller", 5, state -> PlayState.STOP)
                .triggerableAnim("bite", BITE));
    }

    protected <E extends GeoAnimatable> PlayState biteAnimation(final AnimationState<E> event) {
        if (this.level != null) {
            List<Entity> nearbyEntities = this.level.getEntitiesOfClass(Entity.class,
                    new AABB(this.getBlockPos()).inflate(5.0D), entity -> !entity.isSpectator()
                            && entity instanceof LivingEntity && !(entity instanceof PiranhaPlantEntity));

            if (!nearbyEntities.isEmpty()) {
                for (Entity collidingEntity : nearbyEntities) {
                    if (this.attackCooldown > 0)
                        continue;

                    if (this.getOwner() != null && this.getOwner().getUUID().equals(collidingEntity.getUUID()))
                        continue;

                    if ((this.getOwner() != null && !((collidingEntity instanceof Monster)
                                || collidingEntity.getType().is(TagRegistry.PIRANHA_PLANT_CAN_ATTACK)))
                            || (this.getOwner() == null && !collidingEntity.getType().is(TagRegistry.PIRANHA_PLANT_CAN_ATTACK)))
                        continue;

                    if (this.getOwner() != null && collidingEntity.getTeam() != null && this.getOwner().getTeam() != null
                            && collidingEntity.getTeam() == this.getOwner().getTeam())
                        continue;

                    event.setAndContinue(CONSTANT_BITES);
                }
            }
        } else event.setAndContinue(IDLE);
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);

        if (this.ownerUUID != null)
            tag.putUUID("Owner", this.ownerUUID);
        if (this.leftOwner)
            tag.putBoolean("LeftOwner", true);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.leftOwner = tag.getBoolean("LeftOwner");

        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
            this.cachedOwner = null;
        }
    }

    @Nullable
    @Override
    public Entity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level instanceof ServerLevel serverWorld) {
            this.cachedOwner = serverWorld.getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else return null;
    }

    public void setOwner(@javax.annotation.Nullable Entity ownerEntity) {
        if (ownerEntity != null) {
            this.ownerUUID = ownerEntity.getUUID();
            this.cachedOwner = ownerEntity;
        }
    }

    protected boolean ownedBy(Entity entity) {
        return entity.getUUID().equals(this.ownerUUID);
    }

    public static void tick(Level world, BlockPos pos, BlockState state, PottedPiranhaPlantBlockEntity blockEntity) {
        if (blockEntity.attackCooldown > 0)
            blockEntity.attackCooldown--;
    }
}
