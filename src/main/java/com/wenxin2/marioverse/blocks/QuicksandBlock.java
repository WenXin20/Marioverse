package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.registries.ItemRegistry;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class QuicksandBlock extends ColoredFallingBlock implements BucketPickup {
    private static final VoxelShape FALLING_COLLISION_SHAPE =
            Shapes.box(0.0, 0.0, 0.0, 1.0, 0.9F, 1.0);

    public QuicksandBlock(ColorRGBA dustColor, Properties properties) {
        super(dustColor, properties);
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState neighborState, Direction direction) {
        return neighborState.is(this) ? true : super.skipRendering(state, neighborState, direction);
    }

    @NotNull
    @Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return Shapes.empty();
    }

    @NotNull
    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityCollisionContext) {
            Entity entity = entityCollisionContext.getEntity();
            if (entity != null) {
                if (entity.fallDistance > 2.5F)
                    return FALLING_COLLISION_SHAPE;

                boolean flag = entity instanceof FallingBlockEntity;
                if (flag || canEntityWalkOnQuicksand(entity)
                        && context.isAbove(Shapes.block(), pos, false) && !context.isDescending())
                    return super.getCollisionShape(state, blockGetter, pos, context);
            }
        }
        return Shapes.empty();
    }

    @NotNull
    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected void tick(BlockState state, ServerLevel serverLevel, BlockPos pos, RandomSource random) {
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!(entity instanceof LivingEntity) || entity.getInBlockState().is(this)) {
            entity.makeStuckInBlock(state, new Vec3(0.9F, 0.25, 0.9F));

            if (level.isClientSide) {
                RandomSource random = level.getRandom();
                boolean isNotOld = entity.xOld != entity.getX() || entity.zOld != entity.getZ();
                if (isNotOld && random.nextFloat() < 0.5F) {
                    level.addParticle(new BlockParticleOption(ParticleTypes.FALLING_DUST, state),
                            entity.getX(), pos.getY() + 1, entity.getZ(),
                            Mth.randomBetween(random, -1.0F, 1.0F) * 0.083333336F,
                            0.05F,
                            Mth.randomBetween(random, -1.0F, 1.0F) * 0.083333336F);
                }
            }
        }
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (!((double) fallDistance < 4.0) && entity instanceof LivingEntity livingentity) {
            LivingEntity.Fallsounds soundType = livingentity.getFallSounds();
            SoundEvent soundevent = (double) fallDistance < 7.0 ? soundType.small() : soundType.big();
            entity.playSound(soundevent, 1.0F, 1.0F);
        }
    }

    public static boolean canEntityWalkOnQuicksand(Entity entity) {
        if (entity.getType().is(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS)) // TODO
            return true;
        else return false;
    }

    @NotNull
    @Override
    public ItemStack pickupBlock(@Nullable Player player, LevelAccessor levelAccessor, BlockPos pos, BlockState state) {
        levelAccessor.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
        if (!levelAccessor.isClientSide())
            levelAccessor.levelEvent(2001, pos, Block.getId(state));

        return new ItemStack(ItemRegistry.QUICKSAND_BUCKET.get());
    }

    @NotNull
    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.BUCKET_FILL_POWDER_SNOW); // TODO
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType path) {
        return true;
    }
}
