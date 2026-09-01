package com.wenxin2.marioverse.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.wenxin2.marioverse.blocks.entities.ArrowSignBlockEntity;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import com.wenxin2.marioverse.blocks.states.ArrowDirection;
import com.wenxin2.marioverse.blocks.states.HalfBlockStates;
import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class LargeStandingArrowSignBlock extends StandingArrowSignBlock {
    public static final EnumProperty<HalfBlockStates> HALF = BlockStatePropertyRegistry.HALF;

    protected static final VoxelShape BOTTOM_DEFAULT_SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 32.0, 14.0);
    protected static final VoxelShape TOP_DEFAULT_SHAPE = Block.box(2.0, -16.0, 2.0, 14.0, 16.0, 14.0);
    private static final Map<Integer, VoxelShape> BOTTOM_POST_ROTATION_SHAPE = Maps.newHashMap(ImmutableMap
            .of(0, Block.box(5.0, 0.0, 6.5, 11.0, 32.0, 9.5),
                    4, Block.box(6.5, 0.0, 5.0, 9.5, 32.0, 11.0),
                    8, Block.box(5.0, 0.0, 6.5, 11.0, 32.0, 9.5),
                    12, Block.box(6.5, 0.0, 5.0, 9.5, 32.0, 11.0)));
    private static final Map<Integer, VoxelShape> TOP_POST_ROTATION_SHAPE = Maps.newHashMap(ImmutableMap
            .of(0, Block.box(5.0, -16.0, 6.5, 11.0, 16.0, 9.5),
                    4, Block.box(6.5, -16.0, 5.0, 9.5, 16.0, 11.0),
                    8, Block.box(5.0, -16.0, 6.5, 11.0, 16.0, 9.5),
                    12, Block.box(6.5, -16.0, 5.0, 9.5, 16.0, 11.0)));
    private static final Map<Integer, VoxelShape> BOTTOM_BOARD_ROTATION_SHAPE = Maps.newHashMap(ImmutableMap
            .of(0, Shapes.or(Block.box(-6.0, 9.0, 4.5, 20.0, 19.0, 6.5),
                            Block.box(-4.0, 19.0, 4.5, 22.0, 29.0, 6.5)).optimize(),
                    4, Shapes.or(Block.box(9.5, 9.0, -6.0, 11.5, 19.0, 20.0),
                            Block.box(9.5, 19.0, -4.0, 11.5, 29.0, 22.0)).optimize(),
                    8, Shapes.or(Block.box(-4.0, 9.0, 9.5, 22.0, 19.0, 11.5),
                            Block.box(-6.0, 19.0, 9.5, 20.0, 29.0, 11.5)).optimize(),
                    12, Shapes.or(Block.box(4.5, 9.0, -4.0, 6.5, 19.0, 22.0),
                            Block.box(4.5, 19.0, -6.0, 6.5, 29.0, 20.0)).optimize()));
    private static final Map<Integer, VoxelShape> TOP_BOARD_ROTATION_SHAPE = Maps.newHashMap(ImmutableMap
            .of(0, Shapes.or(Block.box(-6.0, -7.0, 4.5, 20.0, 3.0, 6.5),
                            Block.box(-4.0, 3.0, 4.5, 22.0, 13.0, 6.5)).optimize(),
                    4, Shapes.or(Block.box(9.5, -7.0, -6.0, 11.5, 3.0, 20.0),
                            Block.box(9.5, 3.0, -4.0, 11.5, 13.0, 22.0)).optimize(),
                    8, Shapes.or(Block.box(-4.0, -7.0, 9.5, 22.0, 3.0, 11.5),
                            Block.box(-6.0, 3.0, 9.5, 20.0, 13.0, 11.5)).optimize(),
                    12, Shapes.or(Block.box(4.5, -7.0, -4.0, 6.5, 3.0, 22.0),
                            Block.box(4.5, 3.0, -6.0, 6.5, 13.0, 20.0)).optimize()));

    public LargeStandingArrowSignBlock(WoodType woodType, Properties properties) {
        super(woodType, properties);
        this.registerDefaultState(this.defaultBlockState().setValue(HALF, HalfBlockStates.BOTTOM));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF);
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return state.setValue(HALF, HalfBlockStates.BOTTOM);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        BlockPos abovePos = pos.above();
        level.setBlock(abovePos, state.setValue(HALF, HalfBlockStates.TOP)
                .setValue(BlockStateProperties.WATERLOGGED, level.getFluidState(abovePos).getType() == Fluids.WATER), Block.UPDATE_ALL);
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(HALF) == HalfBlockStates.TOP) {
            BlockState below = level.getBlockState(pos.below());
            return below.is(this) && below.getValue(HALF) == HalfBlockStates.BOTTOM;
        }
        return super.canSurvive(state, level, pos);
    }

    @NotNull
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        boolean isBottom = state.getValue(HALF) == HalfBlockStates.BOTTOM;
        VoxelShape defaultShape = isBottom ? BOTTOM_DEFAULT_SHAPE : TOP_DEFAULT_SHAPE;
        int rotation = state.getValue(BlockStateProperties.ROTATION_16);
        VoxelShape postShape = (isBottom ? BOTTOM_POST_ROTATION_SHAPE : TOP_POST_ROTATION_SHAPE).get(rotation);

        if (!state.getValue(BOARD))
            return postShape != null ? postShape : defaultShape;

        VoxelShape boardShape = (isBottom ? BOTTOM_BOARD_ROTATION_SHAPE : TOP_BOARD_ROTATION_SHAPE).get(rotation);

        if (!state.getValue(POST))
            return boardShape != null ? boardShape : defaultShape;

        return boardShape != null && postShape != null
                ? Shapes.or(boardShape, postShape).optimize() : defaultShape;
    }

    @NotNull
    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        HalfBlockStates half = state.getValue(HALF);
        if (direction.getAxis() == Direction.Axis.Y && (half == HalfBlockStates.BOTTOM) == (direction == Direction.UP)) {
            if (!(neighborState.is(this) && neighborState.getValue(HALF) != half))
                return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected boolean wax(Level level, BlockPos pos, ItemStack stack, Player player) {
        boolean result = super.wax(level, pos, stack, player);

        if (result && !level.isClientSide) {
            BlockPos posOther = this.otherHalfPos(level.getBlockState(pos), pos);
            if (level.getBlockEntity(posOther) instanceof ArrowSignBlockEntity otherEntity) {
                otherEntity.setWaxed(true);
                level.levelEvent(null, LevelEvent.PARTICLES_AND_SOUND_WAX_ON, posOther, 0);
            }
        }
        return result;
    }

    @Override
    protected boolean glow(Level level, BlockPos pos, ItemStack stack, Player player) {
        boolean result = super.glow(level, pos, stack, player);

        if (result && !level.isClientSide
                && level.getBlockEntity(pos) instanceof ArrowSignBlockEntity thisEntity) {
            BlockPos posOther = this.otherHalfPos(level.getBlockState(pos), pos);
            if (level.getBlockEntity(posOther) instanceof ArrowSignBlockEntity otherEntity)
                otherEntity.setGlowingArrow(thisEntity.hasGlowingArrow());

            if (thisEntity.hasGlowingArrow())
                ServerParticleUtils.spawnParticlesOnBlockFaces(ParticleTypes.GLOW, (ServerLevel) level, posOther, UniformInt.of(3, 5));
            else
                ServerParticleUtils.spawnParticlesOnBlockFaces(new DustParticleOptions(new Vector3f(0, 0, 0), 0.5F),
                        (ServerLevel) level, posOther, UniformInt.of(8, 12));
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
            BlockPos posOther = this.otherHalfPos(level.getBlockState(pos), pos);
            if (level.getBlockEntity(posOther) instanceof ArrowSignBlockEntity otherEntity)
                otherEntity.setArrowDyeColor(thisEntity.getArrowDyeColor());

            int textColor = dyeItem.getDyeColor().getTextColor();
            Vector3f colorVec = new Vector3f((float) (textColor >> 16 & 255) / 255.0F,
                    (float) (textColor >> 8 & 255) / 255.0F, (float) (textColor & 255) / 255.0F);
            ServerParticleUtils.spawnParticlesOnBlockFaces(new DustParticleOptions(colorVec, 0.5F),
                    (ServerLevel) level, posOther, UniformInt.of(8, 12));
        }
        return result;
    }

    @Override
    protected boolean toggleBoard(Level level, BlockState state, BlockPos pos, ItemStack stack, LivingEntity entity) {
        boolean result = super.toggleBoard(level, state, pos, stack, entity);

        if (result && !level.isClientSide) {
            BlockPos otherPos = this.otherHalfPos(state, pos);
            BlockState otherState = level.getBlockState(otherPos);
            if (otherState.is(this))
                level.setBlock(otherPos, nextToggleBoardState(otherState), Block.UPDATE_CLIENTS);
        }
        return result;
    }

    @Override
    protected boolean rotateArrow(Level level, BlockState state, BlockPos pos, boolean isReverse) {
        boolean result = super.rotateArrow(level, state, pos, isReverse);

        if (result && !level.isClientSide) {
            ArrowDirection direction = level.getBlockState(pos).getValue(ARROW_DIRECTION);
            BlockPos posOther = this.otherHalfPos(state, pos);
            BlockState stateOther = level.getBlockState(posOther);
            if (stateOther.is(this))
                level.setBlock(posOther, stateOther.setValue(ARROW_DIRECTION, direction), Block.UPDATE_CLIENTS);
            if (level.getBlockEntity(posOther) instanceof ArrowSignBlockEntity otherSignBE)
                otherSignBE.setArrowDirection(direction);
        }
        return result;
    }

    @Override
    protected boolean removeArrow(Level level, BlockState state, BlockPos pos, ItemStack stack, LivingEntity entity) {
        boolean result = super.removeArrow(level, state, pos, stack, entity);

        if (result && !level.isClientSide) {
            BlockPos posOther = this.otherHalfPos(state, pos);
            BlockState stateOther = level.getBlockState(posOther);

            if (stateOther.is(this) && stateOther.getValue(ARROW_DIRECTION) != ArrowDirection.NONE)
                level.setBlock(posOther, stateOther.setValue(ARROW_DIRECTION, ArrowDirection.NONE), Block.UPDATE_CLIENTS);
            if (level.getBlockEntity(posOther) instanceof ArrowSignBlockEntity otherSignBE)
                otherSignBE.setArrowDirection(ArrowDirection.NONE);
            if (stack.is(CompatRegistry.SOAP.get())) {
                level.playSound(null, posOther, CompatRegistry.SOAP_WASH_SOUND.get(), SoundSource.BLOCKS);
                StandingArrowSignBlock.spawnParticles(level, posOther, null,
                        (ParticleOptions) CompatRegistry.SUDS_PARTICLE.get(), UniformInt.of(5, 8));
            }
        }
        return result;
    }

    @Override
    public void destroy(LevelAccessor levelAccessor, BlockPos pos, BlockState state) {
        if (levelAccessor instanceof Level level) {
            BlockPos posOther = this.otherHalfPos(state, pos);
            if (level.getBlockState(posOther).is(this)) {
                levelAccessor.levelEvent(2001, posOther, Block.getId(levelAccessor.getBlockState(posOther)));
                level.destroyBlock(posOther, false);
            }
        }
        super.destroy(levelAccessor, pos, state);
    }

    @NotNull
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (player.isCreative() || !player.hasCorrectToolForDrops(state, level, pos)) {
            BlockPos posOther = this.otherHalfPos(state, pos);
            if (level.getBlockState(posOther).is(this)) {
                level.levelEvent(2001, posOther, Block.getId(level.getBlockState(posOther)));
                level.destroyBlock(posOther, false);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        BlockPos posOther = this.otherHalfPos(state, pos);
        if (level.getBlockState(posOther).is(this)) {
            level.levelEvent(2001, posOther, Block.getId(level.getBlockState(posOther)));
            level.destroyBlock(posOther, false);
        }
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

    public BlockPos otherHalfPos(BlockState state, BlockPos pos) {
        return state.getValue(HALF) == HalfBlockStates.BOTTOM ? pos.above() : pos.below();
    }
}
