package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
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
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class QuicksandBlock extends PowderSnowBlock implements BucketPickup {
    public static final MapCodec<PowderSnowBlock> CODEC = simpleCodec(PowderSnowBlock::new);
    private static final VoxelShape FALLING_COLLISION_SHAPE =
            Shapes.box(0.0, 0.0, 0.0, 1.0, 0.9F, 1.0);

    @Override
    public MapCodec<PowderSnowBlock> codec() {
        return CODEC;
    }

    public QuicksandBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!(entity instanceof LivingEntity) || entity.getInBlockState().is(this)) {
            entity.makeStuckInBlock(state, new Vec3(0.9F, 0.5, 0.9F));
            if (level.isClientSide) {
                RandomSource random = level.getRandom();
                boolean isNotOld = entity.xOld != entity.getX() || entity.zOld != entity.getZ();
                if (isNotOld && random.nextBoolean()) {
                    level.addParticle(new DustParticleOptions(
                            new Vector3f(Mth.randomBetween(random, -1.0F, 1.0F) * 0.083333336F,
                                    0.05F,
                                    Mth.randomBetween(random, -1.0F, 1.0F) * 0.083333336F),
                                    14406560), // TODO
                            entity.getX(), pos.getY() + 1, entity.getZ(),
                            Mth.randomBetween(random, -1.0F, 1.0F) * 0.083333336F,
                            0.05F,
                            Mth.randomBetween(random, -1.0F, 1.0F) * 0.083333336F);
                }
            }
        }
        entity.setIsInPowderSnow(true);
    }

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

    public static boolean canEntityWalkOnQuicksand(Entity entity) {
        if (entity.getType().is(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS)) // TODO
            return true;
        else return entity instanceof LivingEntity livingEntity
                && livingEntity.getItemBySlot(EquipmentSlot.FEET).canWalkOnPowderedSnow(livingEntity);
    }

    @Override
    public ItemStack pickupBlock(@Nullable Player player, LevelAccessor levelAccessor, BlockPos pos, BlockState state) {
        levelAccessor.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
        if (!levelAccessor.isClientSide())
            levelAccessor.levelEvent(2001, pos, Block.getId(state));

        return new ItemStack(Items.POWDER_SNOW_BUCKET); // TODO
    }

    @NotNull
    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.BUCKET_FILL_POWDER_SNOW); // TODO
    }
}
