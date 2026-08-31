package com.wenxin2.marioverse.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.wenxin2.marioverse.blocks.entities.ArrowSignBlockEntity;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import com.wenxin2.marioverse.blocks.states.ArrowDirection;
import com.wenxin2.marioverse.blocks.states.HalfBlockStates;
import com.wenxin2.marioverse.blocks.states.SideBlockStates;
import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.Arrays;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class LargeWallArrowSignBlock extends WallArrowSignBlock {
    public static final EnumProperty<HalfBlockStates> HALF = BlockStatePropertyRegistry.HALF;
    public static final EnumProperty<SideBlockStates> SIDE = BlockStatePropertyRegistry.SIDE;

    private static final Map<Direction, VoxelShape> BOTTOM_LEFT_SHAPE = Maps.newEnumMap(ImmutableMap
            .of(Direction.NORTH, Shapes.or(Block.box(2, 6, 14, 28, 16, 16), Block.box(4, 16, 14, 30, 26, 16)).optimize(),
                    Direction.EAST, Shapes.or(Block.box(0, 6, 2, 2, 16, 28), Block.box(0, 16, 4, 2, 26, 30)).optimize(),
                    Direction.SOUTH, Shapes.or(Block.box(-12, 6, 0, 14, 16, 2), Block.box(-14, 16, 0, 12, 26, 2)).optimize(),
                    Direction.WEST, Shapes.or(Block.box(14, 6, -12, 16, 16, 14), Block.box(14, 16, -14, 16, 26, 12)).optimize()));
    private static final Map<Direction, VoxelShape> BOTTOM_RIGHT_SHAPE = Maps.newEnumMap(ImmutableMap
            .of(Direction.NORTH, Shapes.or(Block.box(-14, 6, 14, 12, 16, 16), Block.box(-12, 16, 14, 14, 26, 16)).optimize(),
                    Direction.EAST, Shapes.or(Block.box(0, 6, -14, 2, 16, 12), Block.box(0, 16, -12, 2, 26, 14)).optimize(),
                    Direction.SOUTH, Shapes.or(Block.box(4, 6, 0, 30, 16, 2), Block.box(2, 16, 0, 28, 26, 2)).optimize(),
                    Direction.WEST, Shapes.or(Block.box(14, 6, 4, 16, 16, 30), Block.box(14, 16, 2, 16, 26, 28)).optimize()));
    private static final Map<Direction, VoxelShape> TOP_LEFT_SHAPE = Maps.newEnumMap(ImmutableMap
            .of(Direction.NORTH, Shapes.or(Block.box(2, -10, 14, 28, 0, 16), Block.box(4, 0, 14, 30, 10, 16)).optimize(),
                    Direction.EAST, Shapes.or(Block.box(0, -10, 2, 2, 0, 28), Block.box(0, 0, 4, 2, 10, 30)).optimize(),
                    Direction.SOUTH, Shapes.or(Block.box(-12, -10, 0, 14, 0, 2), Block.box(-14, 0, 0, 12, 10, 2)).optimize(),
                    Direction.WEST, Shapes.or(Block.box(14, -10, -12, 16, 0, 14), Block.box(14, 0, -14, 16, 10, 12)).optimize()));
    private static final Map<Direction, VoxelShape> TOP_RIGHT_SHAPE = Maps.newEnumMap(ImmutableMap
            .of(Direction.NORTH, Shapes.or(Block.box(-14, -10, 14, 12, 0, 16), Block.box(-12, 0, 14, 14, 10, 16)).optimize(),
                    Direction.EAST, Shapes.or(Block.box(0, -10, -14, 2, 0, 12), Block.box(0, 0, -12, 2, 10, 14)).optimize(),
                    Direction.SOUTH, Shapes.or(Block.box(4, -10, 0, 30, 0, 2), Block.box(2, 0, 0, 28, 10, 2)).optimize(),
                    Direction.WEST, Shapes.or(Block.box(14, -10, 4, 16, 0, 30), Block.box(14, 0, 2, 16, 10, 28)).optimize()));

    public LargeWallArrowSignBlock(WoodType woodType, BlockBehaviour.Properties properties) {
        super(woodType, properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(HALF, HalfBlockStates.BOTTOM).setValue(SIDE, SideBlockStates.LEFT));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF, SIDE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null)
            return null;

        Direction rightDir = state.getValue(FACING).getClockWise();
        BlockPos pos = context.getClickedPos();
        LevelReader level = context.getLevel();

        BlockPos rightPos = pos.relative(rightDir);
        BlockPos abovePos = pos.above();
        BlockPos aboveRightPos = rightPos.above();

        if (!level.getBlockState(rightPos).canBeReplaced(context)
                || !level.getBlockState(abovePos).canBeReplaced(context)
                || !level.getBlockState(aboveRightPos).canBeReplaced(context))
            return null;

        return state.setValue(HALF, HalfBlockStates.BOTTOM).setValue(SIDE, SideBlockStates.LEFT);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        Direction rightDir = state.getValue(FACING).getClockWise();
        BlockPos rightPos = pos.relative(rightDir);
        BlockPos abovePos = pos.above();
        BlockPos aboveRightPos = rightPos.above();

        level.setBlock(rightPos, state.setValue(SIDE, SideBlockStates.RIGHT)
                .setValue(BlockStateProperties.WATERLOGGED, level.getFluidState(rightPos).getType() == Fluids.WATER), Block.UPDATE_ALL);
        level.setBlock(abovePos, state.setValue(HALF, HalfBlockStates.TOP)
                .setValue(BlockStateProperties.WATERLOGGED, level.getFluidState(abovePos).getType() == Fluids.WATER), Block.UPDATE_ALL);
        level.setBlock(aboveRightPos, state.setValue(HALF, HalfBlockStates.TOP).setValue(SIDE, SideBlockStates.RIGHT)
                .setValue(BlockStateProperties.WATERLOGGED, level.getFluidState(aboveRightPos).getType() == Fluids.WATER), Block.UPDATE_ALL);
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @NotNull
    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @NotNull
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        BlockState rotated = this.rotate(state, mirror.getRotation(state.getValue(FACING)));
        SideBlockStates flippedSide = rotated.getValue(SIDE) == SideBlockStates.LEFT
                ? SideBlockStates.RIGHT : SideBlockStates.LEFT;
        return rotated.setValue(SIDE, flippedSide);
    }

    @NotNull
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        boolean isBottom = state.getValue(HALF) == HalfBlockStates.BOTTOM;
        boolean isLeft = state.getValue(SIDE) == SideBlockStates.LEFT;

        Map<Direction, VoxelShape> shapeMap = isBottom
                ? (isLeft ? BOTTOM_LEFT_SHAPE : BOTTOM_RIGHT_SHAPE)
                : (isLeft ? TOP_LEFT_SHAPE : TOP_RIGHT_SHAPE);
        return shapeMap.get(facing);
    }

    @NotNull
    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        HalfBlockStates half = state.getValue(HALF);
        SideBlockStates side = state.getValue(SIDE);
        Direction towardPartner = side == SideBlockStates.LEFT
                ? state.getValue(FACING).getClockWise() : state.getValue(FACING).getCounterClockWise();

        if (direction.getAxis() == Direction.Axis.Y && (half == HalfBlockStates.BOTTOM) == (direction == Direction.UP)) {
            if (!(neighborState.is(this) && neighborState.getValue(HALF) != half && neighborState.getValue(SIDE) == side))
                return Blocks.AIR.defaultBlockState();
        } else if (direction == towardPartner) {
            if (!(neighborState.is(this) && neighborState.getValue(SIDE) != side && neighborState.getValue(HALF) == half))
                return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    private BlockPos primaryPos(BlockState state, BlockPos pos) {
        BlockPos posBase = state.getValue(SIDE) == SideBlockStates.RIGHT
                ? pos.relative(state.getValue(FACING).getCounterClockWise()) : pos;
        return state.getValue(HALF) == HalfBlockStates.TOP ? posBase.below() : posBase;
    }

    public BlockPos[] siblingPositions(BlockState state, BlockPos pos) {
        BlockPos posMain = this.primaryPos(state, pos);
        Direction directionClockwise = state.getValue(FACING).getClockWise();
        BlockPos posRight = posMain.relative(directionClockwise);

        return Arrays.stream(new BlockPos[] { posMain, posRight, posMain.above(), posRight.above() })
                .filter(sibling -> !sibling.equals(pos))
                .toArray(BlockPos[]::new);
    }

    @Override
    protected boolean wax(Level level, BlockPos pos, ItemStack stack, Player player) {
        boolean result = super.wax(level, pos, stack, player);
        if (result && !level.isClientSide) {
            BlockState state = level.getBlockState(pos);
            Direction facing = state.getValue(FACING).getOpposite();

            for (BlockPos posOther : this.siblingPositions(state, pos)) {
                if (level.getBlockEntity(posOther) instanceof ArrowSignBlockEntity otherEntity) {
                    otherEntity.setWaxed(true);
                    level.playSound(null, posOther, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS);
                    StandingArrowSignBlock.spawnParticles(level, posOther, facing, ParticleTypes.WAX_ON, UniformInt.of(3, 5));
                }
            }
        }
        return result;
    }

    @Override
    protected boolean glow(Level level, BlockPos pos, ItemStack stack, Player player) {
        boolean result = super.glow(level, pos, stack, player);

        if (result && !level.isClientSide
                && level.getBlockEntity(pos) instanceof ArrowSignBlockEntity thisEntity) {
            BlockState state = level.getBlockState(pos);
            Direction facing = state.getValue(FACING).getOpposite();

            for (BlockPos posOther : this.siblingPositions(state, pos)) {
                if (level.getBlockEntity(posOther) instanceof ArrowSignBlockEntity otherEntity)
                    otherEntity.setGlowingArrow(thisEntity.hasGlowingArrow());

                if (thisEntity.hasGlowingArrow())
                    ServerParticleUtils.spawnParticlesOnBlockFace(ParticleTypes.GLOW, (ServerLevel) level, posOther,
                            facing, UniformInt.of(3, 5), () -> Vec3.ZERO, 0.55);
                else ServerParticleUtils.spawnParticlesOnBlockFace(new DustParticleOptions(new Vector3f(0, 0, 0), 0.5F),
                            (ServerLevel) level, posOther, facing, UniformInt.of(8, 12), () -> Vec3.ZERO, 0.55);
            }
        }
        return result;
    }

    @Override
    protected boolean dye(Level level, BlockPos pos, ItemStack stack, Player player) {
        boolean result = super.dye(level, pos, stack, player);
        if (!(stack.getItem() instanceof DyeItem dyeItem))
            return false;

        if (result && !level.isClientSide
                && level.getBlockEntity(pos) instanceof ArrowSignBlockEntity thisEntity) {
            BlockState state = level.getBlockState(pos);
            Direction facing = state.getValue(FACING).getOpposite();
            for (BlockPos posOther : this.siblingPositions(state, pos)) {
                if (level.getBlockEntity(posOther) instanceof ArrowSignBlockEntity otherEntity)
                    otherEntity.setArrowDyeColor(thisEntity.getArrowDyeColor());

                int textColor = dyeItem.getDyeColor().getTextColor();
                Vector3f colorVec = new Vector3f((float) (textColor >> 16 & 255) / 255.0F,
                        (float) (textColor >> 8 & 255) / 255.0F, (float) (textColor & 255) / 255.0F);
                ServerParticleUtils.spawnParticlesOnBlockFace(new DustParticleOptions(colorVec, 0.5F),
                        (ServerLevel) level, posOther, facing, UniformInt.of(8, 12), () -> Vec3.ZERO, 0.55);
            }
        }
        return result;
    }

    @Override
    protected boolean rotateArrow(Level level, BlockState state, BlockPos pos, boolean isReverse) {
        boolean result = super.rotateArrow(level, state, pos, isReverse);

        if (result && !level.isClientSide) {
            ArrowDirection direction = level.getBlockState(pos).getValue(ARROW_DIRECTION);
            for (BlockPos posOther : this.siblingPositions(state, pos)) {
                BlockState stateOther = level.getBlockState(posOther);
                if (stateOther.is(this))
                    level.setBlock(posOther, stateOther.setValue(ARROW_DIRECTION, direction), Block.UPDATE_CLIENTS);
                if (level.getBlockEntity(posOther) instanceof ArrowSignBlockEntity otherSignBE)
                    otherSignBE.setArrowDirection(direction);
            }
        }
        return result;
    }

    @Override
    protected boolean removeArrow(Level level, BlockState state, BlockPos pos, ItemStack stack) {
        boolean result = super.removeArrow(level, state, pos, stack);

        if (result && !level.isClientSide) {
            for (BlockPos posOther : this.siblingPositions(state, pos)) {
                BlockState stateOther = level.getBlockState(posOther);

                if (stateOther.is(this) && stateOther.getValue(ARROW_DIRECTION) != ArrowDirection.NONE)
                    level.setBlock(posOther, stateOther.setValue(ARROW_DIRECTION, ArrowDirection.NONE), Block.UPDATE_CLIENTS);
                if (level.getBlockEntity(posOther) instanceof ArrowSignBlockEntity otherSignBE)
                    otherSignBE.setArrowDirection(ArrowDirection.NONE);
                if (stack.is(CompatRegistry.SOAP.get())) {
                    level.playSound(null, posOther, CompatRegistry.SOAP_WASH.get(), SoundSource.BLOCKS);
                    StandingArrowSignBlock.spawnParticles(level, posOther, null,
                            (ParticleOptions) CompatRegistry.SUDS_PARTICLE.get(), UniformInt.of(5, 8));
                }
            }
        }
        return result;
    }

    @Override
    public void destroy(LevelAccessor levelAccessor, BlockPos pos, BlockState state) {
        if (levelAccessor instanceof Level level)
            this.removeOtherParts(level, state, pos, false);
        super.destroy(levelAccessor, pos, state);
    }

    @NotNull
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (player.isCreative() || !player.hasCorrectToolForDrops(state, level, pos))
            this.removeOtherParts(level, state, pos, false);
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        this.removeOtherParts(level, state, pos, false);
        super.onBlockExploded(state, level, pos, explosion);
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return level.getBlockEntity(pos) instanceof ArrowSignBlockEntity signBE && !signBE.isWaxed()
                && state.is(TagRegistry.FLAMMABLE_LARGE_ARROW_SIGNS);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 20;
    }

    private void removeOtherParts(Level level, BlockState state, BlockPos pos, boolean dropResources) {
        for (BlockPos posOther : this.siblingPositions(state, pos)) {
            if (level.getBlockState(posOther).is(this)) {
                level.levelEvent(2001, posOther, Block.getId(level.getBlockState(posOther)));
                level.destroyBlock(posOther, dropResources);
            }
        }
    }
}
