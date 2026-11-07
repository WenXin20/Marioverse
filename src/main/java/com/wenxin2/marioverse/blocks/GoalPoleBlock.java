package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.blocks.entities.GoalPoleBlockEntity;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import com.wenxin2.marioverse.blocks.states.ColumnBlockStates;
import com.wenxin2.marioverse.registries.BlockEntityRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.sounds.GoalPoleFlagSoundInstance;
import com.wenxin2.marioverse.network.client_bound.data.AmericaNamePayload;
import com.wenxin2.marioverse.network.client_bound.data.WonderNamePayload;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoalPoleBlock extends Block implements SimpleWaterloggedBlock, EntityBlock {
    public static final MapCodec<GoalPoleBlock> CODEC = simpleCodec(GoalPoleBlock::new);
    public static final EnumProperty<ColumnBlockStates> COLUMN = BlockStatePropertyRegistry.COLUMN;
    public static final BooleanProperty FLAG = BooleanProperty.create("flag");
    public static final BooleanProperty LOWERED = BooleanProperty.create("lowered");
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final int MAX = RotationSegment.getMaxSegmentIndex();
    private static final int ROTATIONS = MAX + 1;
    private static final double FLAG_TOP_OFFSET = 0.625;
    private static final double FLAG_HEIGHT = 1.375;
    private static final double FLAG_BOTTOM_MARGIN = 0.125;

    protected static final VoxelShape GOAL_POLE_MIDDLE = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0).optimize();
    protected static final VoxelShape GOAL_POLE_TOP = Shapes.or(
            Block.box(6.0, 0.0, 6.0, 10.0, 8.0, 10.0),
            Block.box(4.0, 8.0, 4.0, 12.0, 16.0, 12.0)).optimize();
    protected static final VoxelShape GOAL_POLE_TOP_COLLISION = Shapes.or(
            Block.box(6.0, 0.0, 6.0, 10.0, 8.0, 10.0),
            Block.box(4.0, 8.0, 4.0, 12.0, 15.95, 12.0)).optimize();
    protected static final VoxelShape GOAL_POLE_NONE = Shapes.or(
            Block.box(6.0, 0.0, 6.0, 10.0, 10.0, 10.0),
            Block.box(5.0, 10.0, 5.0, 11.0, 16.0, 11.0)).optimize();
    protected static final VoxelShape GOAL_POLE_NONE_COLLISION = Shapes.or(
            Block.box(6.0, 0.0, 6.0, 10.0, 10.0, 10.0),
            Block.box(5.0, 10.0, 5.0, 11.0, 15.95, 11.0)).optimize();

    public GoalPoleBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(COLUMN, ColumnBlockStates.NONE)
                .setValue(FLAG, Boolean.TRUE).setValue(LOWERED, Boolean.FALSE).setValue(ROTATION, 0).setValue(WATERLOGGED, Boolean.FALSE));
    }

    @NotNull
    @Override
    public MapCodec<GoalPoleBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(COLUMN, FLAG, LOWERED, ROTATION, WATERLOGGED);
    }

    @NotNull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        if (state.getValue(COLUMN) == ColumnBlockStates.TOP)
            return GOAL_POLE_TOP;
        if (state.getValue(COLUMN) == ColumnBlockStates.NONE)
            return GOAL_POLE_NONE;
        else return GOAL_POLE_MIDDLE;
    }

    @NotNull
    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        if (state.getValue(COLUMN) == ColumnBlockStates.TOP)
            return GOAL_POLE_TOP_COLLISION;
        else if (state.getValue(COLUMN) == ColumnBlockStates.NONE)
            return GOAL_POLE_NONE_COLLISION;
        else return super.getCollisionShape(state, blockGetter, pos, context);
    }

    @NotNull
    @Override
    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return GOAL_POLE_MIDDLE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GoalPoleBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        if (!world.isClientSide() || type != BlockEntityRegistry.GOAL_POLE_BLOCK_ENTITY.get())
            return null;
        return (lvl, pos, st, be) -> GoalPoleFlagSoundInstance.tickFlagSounds((GoalPoleBlockEntity) be);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());

        return this.defaultBlockState().setValue(WATERLOGGED, fluidState.is(FluidTags.WATER) && fluidState.getAmount() == 8)
                .setValue(ROTATION, RotationSegment.convertToSegment(context.getRotation()));
    }

    @NotNull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor worldAccessor, BlockPos pos, BlockPos neighborPos) {
        Block blockAbove = worldAccessor.getBlockState(pos.above()).getBlock();
        Block blockBelow = worldAccessor.getBlockState(pos.below()).getBlock();

        if (!worldAccessor.isClientSide()) {
            if (blockAbove instanceof GoalPoleBlock) {
                if (blockBelow instanceof GoalPoleBlock)
                    return state.setValue(COLUMN, ColumnBlockStates.MIDDLE).setValue(FLAG, Boolean.FALSE);
                return state.setValue(COLUMN, ColumnBlockStates.BOTTOM).setValue(FLAG, Boolean.FALSE);
            }

            if (blockBelow instanceof GoalPoleBlock)
                return state.setValue(COLUMN, ColumnBlockStates.TOP).setValue(FLAG, Boolean.TRUE);

            if (state.getValue(WATERLOGGED))
                worldAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldAccessor));

            return state.setValue(COLUMN, ColumnBlockStates.NONE).setValue(FLAG, Boolean.TRUE);
        } else return Blocks.AIR.defaultBlockState();
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(world, pos, state, entity, stack);
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof GoalPoleBlockEntity goalPoleBE) {
            if (stack.has(DataComponents.CUSTOM_NAME)) {
                goalPoleBE.setCustomName(stack.getHoverName());
                goalPoleBE.markUpdated();

                if (goalPoleBE.isWonderFlag()) {
                    goalPoleBE.setWonderFlag(Boolean.TRUE);
                    if (!world.isClientSide())
                        PacketDistributor.sendToAllPlayers(new WonderNamePayload(pos, goalPoleBE.hasWonderFlag()));
                } else if (goalPoleBE.isAmericanFlag()) {
                    goalPoleBE.setAmericanFlag(Boolean.TRUE);
                    if (!world.isClientSide())
                        PacketDistributor.sendToAllPlayers(new AmericaNamePayload(pos, goalPoleBE.hasAmericanFlag()));
                }
            }
        }
    }

    @NotNull
    @Override
    public FluidState getFluidState(final BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(ROTATION, rotation.rotate(state.getValue(ROTATION), ROTATIONS));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(ROTATION, mirror.mirror(state.getValue(ROTATION), ROTATIONS));
    }

    @Override
    protected void tick(BlockState state, ServerLevel serverWorld, BlockPos pos, RandomSource random) {
        if (serverWorld.getBlockEntity(pos) instanceof GoalPoleBlockEntity goalPoleBE
                && state.getValue(GoalPoleBlock.COLUMN) != ColumnBlockStates.MIDDLE) {
            if (goalPoleBE.isAmericanFlag() || state.getBlock() == BlockRegistry.CLASSIC_GOAL_POLE.get())
                goalPoleBE.stopTriggeredAnim("appear_controller", "appear");
            else goalPoleBE.stopTriggeredAnim("switch_controller", "switch");

            if (goalPoleBE.hasPendingSwapPoof()) {
                int ticksUntilSwap = Mth.ceil(goalPoleBE.getFlagPreMoveTicks() - goalPoleBE.getFlagElapsed(0.0F));
                if (ticksUntilSwap > 0)
                    serverWorld.scheduleTick(pos, this, ticksUntilSwap);
                else {
                    goalPoleBE.setPendingSwapPoof(false);
                    ServerParticleUtils.spawnParticleRingOnBlock(ParticleTypes.POOF, serverWorld,
                            pos.below(1 + Mth.floor(goalPoleBE.getFlagOffset(0.0F))), 0.5D, 10);
                }
            }
        }
        super.tick(state, serverWorld, pos, random);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity.getType().is(TagRegistry.CAN_LOWER_FLAGS)) {
            float pitch = 0.9F + level.random.nextFloat() * 0.2F;
            int flagPoleHeight = this.calculateFlagPoleHeight(level, pos);
            BlockPos posTop = this.findTopOfGoalPole(level, pos);
            BlockPos posBottom = this.findBottomOfGoalPole(level, pos);
            double relativeHeight = (entity.getY() + entity.getEyeHeight() - posBottom.getY()) / flagPoleHeight;
            relativeHeight = Mth.clamp(relativeHeight, 0.0, 1.0);

            if (!state.getValue(LOWERED)) {
                double flagRestTop = posTop.getY() + GoalPoleBlock.FLAG_TOP_OFFSET;
                double flagStartTop = Math.min(posBottom.getY() + GoalPoleBlock.FLAG_HEIGHT + GoalPoleBlock.FLAG_BOTTOM_MARGIN, flagRestTop);
                double flagTargetTop = Mth.clamp(entity.getY() + entity.getBbHeight(), flagStartTop, flagRestTop);

                if (level instanceof ServerLevel serverWorld) {
                    double particleY = Mth.clamp(entity.getY() + entity.getBbHeight(), posBottom.getY(), posTop.getY() + 1.0) + 0.75;
                    serverWorld.sendParticles(this.getParticleForHeight(relativeHeight),
                            posTop.getX() + 0.5, particleY, posTop.getZ() + 0.5, 1, 0.0, 0.0, 0.0, 0.0);
                    this.updateConnectedFlags(level, pos, true);
                }

                if (level.getBlockEntity(posTop) != null && level.getBlockEntity(posTop) instanceof GoalPoleBlockEntity goalPoleTopBE) {
                    float currentOffset = goalPoleTopBE.getFlagOffset(0.0F);
                    if (goalPoleTopBE.isAmericanFlag() || state.getBlock() == BlockRegistry.CLASSIC_GOAL_POLE.get())
                        goalPoleTopBE.startFlagMove(level.getGameTime(), currentOffset, (float) (flagRestTop - flagStartTop));
                    else {
                        if (level instanceof ServerLevel serverWorld)
                            ServerParticleUtils.spawnParticleRingOnBlock(ParticleTypes.POOF, serverWorld,
                                    posTop.below(1 + Mth.floor(currentOffset)), 0.5D, 10);
                        goalPoleTopBE.startFlagSwap(level.getGameTime(), true, currentOffset, currentOffset,
                                (float) (flagRestTop - flagStartTop), (float) (flagRestTop - flagTargetTop));
                    }
                }

                if (level.getBlockEntity(pos) != null && level.getBlockEntity(pos) instanceof GoalPoleBlockEntity goalPoleBE)
                    goalPoleBE.markUpdated();

                level.scheduleTick(pos, this, 20);
                level.setBlock(pos, state.setValue(LOWERED, true), 3);
                boolean slidingFlag = state.getBlock() == BlockRegistry.CLASSIC_GOAL_POLE.get()
                        || (level.getBlockEntity(posTop) instanceof GoalPoleBlockEntity goalPoleTopBE && goalPoleTopBE.isAmericanFlag());
                if (!slidingFlag)
                    level.playSound(null, entity.blockPosition(), SoundRegistry.GOAL_POLE_FINISH.get(),
                            SoundSource.BLOCKS, 1.0F, pitch);
            }

            if (!entity.onGround() && !(entity.getDeltaMovement().y > 0))
                entity.setDeltaMovement(entity.getDeltaMovement().x, Math.max(entity.getDeltaMovement().y * 0.8, -0.1), entity.getDeltaMovement().z);
            entity.resetFallDistance();
        }
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        if (state.getValue(LOWERED))
            return this.calculateFlagPoleLoweredHeight(world, pos);
        else return super.getAnalogOutputSignal(state, world, pos);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag options) {
        super.appendHoverText(stack, tooltipContext, list, options);
        list.add(Component.translatable(this.getDescriptionId() + ".tooltip"));
    }

    public boolean canResetFlag(BlockState state, Player player, InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND && player.isSecondaryUseActive()
                && player.getType().is(TagRegistry.CAN_LOWER_FLAGS) && state.getValue(LOWERED)
                && player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty();
    }

    public void resetFlag(BlockState state, Level world, BlockPos pos) {
        BlockPos posTop = this.findTopOfGoalPole(world, pos);
        BlockState stateTop = world.getBlockState(posTop);

        if (world instanceof ServerLevel)
            this.updateConnectedFlags(world, pos, false);

        if (world.getBlockEntity(posTop) != null && world.getBlockEntity(posTop) instanceof GoalPoleBlockEntity goalPoleTopBE) {
            float currentOffset = goalPoleTopBE.getFlagOffset(0.0F);
            if (goalPoleTopBE.isAmericanFlag() || stateTop.getBlock() == BlockRegistry.CLASSIC_GOAL_POLE.get())
                goalPoleTopBE.startFlagMove(world.getGameTime(), currentOffset, 0.0F);
            else {
                float lowestOffset = this.getLowestFlagOffset(posTop, this.findBottomOfGoalPole(world, pos));
                goalPoleTopBE.startFlagSwap(world.getGameTime(), false, currentOffset, lowestOffset, lowestOffset, 0.0F);
                goalPoleTopBE.setPendingSwapPoof(true);
                world.scheduleTick(posTop, this, Math.max(Mth.ceil(goalPoleTopBE.getFlagPreMoveTicks()), 1));
            }
        }

        if (world.getBlockEntity(pos) != null && world.getBlockEntity(pos) instanceof GoalPoleBlockEntity goalPoleBE)
            goalPoleBE.markUpdated();

        world.scheduleTick(pos, this, 20);
        world.setBlock(pos, state.setValue(LOWERED, false), 3);
    }

    private float getLowestFlagOffset(BlockPos posTop, BlockPos posBottom) {
        double flagRestTop = posTop.getY() + GoalPoleBlock.FLAG_TOP_OFFSET;
        double flagStartTop = Math.min(posBottom.getY() + GoalPoleBlock.FLAG_HEIGHT + GoalPoleBlock.FLAG_BOTTOM_MARGIN, flagRestTop);
        return (float) (flagRestTop - flagStartTop);
    }

    private BlockPos findTopOfGoalPole(Level world, BlockPos pos) {
        while (world.getBlockState(pos.above()).getBlock() instanceof GoalPoleBlock) {
            pos = pos.above();
        }
        return pos;
    }

    private BlockPos findBottomOfGoalPole(Level world, BlockPos pos) {
        while (world.getBlockState(pos.below()).getBlock() instanceof GoalPoleBlock) {
            pos = pos.below();
        }
        return pos;
    }

    private int calculateFlagPoleHeight(Level world, BlockPos pos) {
        int height = 0;
        BlockPos checkPos = pos;

        // Check upward to count the flag pole's height
        while (world.getBlockState(checkPos).getBlock() instanceof GoalPoleBlock) {
            height++;
            checkPos = checkPos.above();
        }
        checkPos = pos.below();
        while (world.getBlockState(checkPos).getBlock() instanceof GoalPoleBlock) {
            height++;
            checkPos = checkPos.below();
        }
        return height;
    }

    private int calculateFlagPoleLoweredHeight(Level world, BlockPos pos) {
        int height = 0;
        BlockPos checkPos = pos;

        // Check upward to count the flag pole's height
        while (world.getBlockState(checkPos).getBlock() instanceof GoalPoleBlock
                && world.getBlockState(checkPos).getValue(LOWERED) && height <= 15) {
            height++;
            checkPos = checkPos.above();
        }
        return height;
    }

    private ParticleOptions getParticleForHeight(double relativeHeight) {
        if (relativeHeight >= 1.0) return ParticleRegistry.WONDERFUL.get();
        if (relativeHeight >= 0.875) return ParticleRegistry.INCREDIBLE.get();
        if (relativeHeight >= 0.75) return ParticleRegistry.EXCELLENT.get();
        if (relativeHeight >= 0.625) return ParticleRegistry.FANTASTIC.get();
        if (relativeHeight >= 0.375) return ParticleRegistry.SUPER.get();
        if (relativeHeight >= 0.25) return ParticleRegistry.GREAT.get();
        return ParticleRegistry.GOOD.get();
    }

    private void updateConnectedFlags(Level world, BlockPos pos, boolean isFlagLowered) {
        BlockPos posAbove = pos.above();
        while (world.getBlockState(posAbove).getBlock() instanceof GoalPoleBlock) {
            BlockState stateAbove = world.getBlockState(posAbove);
            BlockState stateAbove2 = world.getBlockState(posAbove.above());

            world.setBlock(pos, stateAbove.setValue(LOWERED, isFlagLowered), 3);
            world.setBlock(posAbove, stateAbove.setValue(LOWERED, isFlagLowered), 3);
            if (world.getBlockEntity(pos) != null && world.getBlockEntity(pos) instanceof GoalPoleBlockEntity goalPoleBE)
                goalPoleBE.markUpdated();

            if (!isFlagLowered) {
                if (world.getBlockEntity(pos) != null && world.getBlockEntity(pos) instanceof GoalPoleBlockEntity goalPoleBE) {
                    if (world.getBlockState(pos).getBlock() == BlockRegistry.CLASSIC_GOAL_POLE.get()
                            || goalPoleBE.isAmericanFlag()) {
                        if (stateAbove.getValue(COLUMN) == ColumnBlockStates.TOP)
                            world.setBlock(posAbove, stateAbove.setValue(FLAG, true), 3);
                        if ((goalPoleBE.isAmericanFlag() || stateAbove.getBlock() == BlockRegistry.CLASSIC_GOAL_POLE.get())
                                && stateAbove.getValue(GoalPoleBlock.COLUMN) != ColumnBlockStates.MIDDLE
                                && world.getBlockEntity(posAbove) instanceof GoalPoleBlockEntity goalPoleAboveBE) {
                            goalPoleAboveBE.stopTriggeredAnim("disappear_controller", "disappear");
                            goalPoleAboveBE.triggerAnim("appear_controller", "appear");
                            goalPoleAboveBE.updateConnectedDisappearFlags(world, posAbove, false);
                        }
                        world.scheduleTick(posAbove, this, 20);
                    }
                }

                if (world.getBlockEntity(posAbove.above()) != null && world.getBlockEntity(posAbove.above()) instanceof GoalPoleBlockEntity goalPoleTopBE) {
                    goalPoleTopBE.markUpdated();

                    if (!goalPoleTopBE.isAmericanFlag() && stateAbove2.getBlock() != BlockRegistry.CLASSIC_GOAL_POLE.get())
                        goalPoleTopBE.triggerAnim("switch_controller", "switch");
                    world.scheduleTick(posAbove.above(), this, 15);
                }
            }

            posAbove = posAbove.above();
        }

        BlockPos posBelow = pos.below();
        while (world.getBlockState(posBelow).getBlock() instanceof GoalPoleBlock) {
            world.setBlock(posBelow, world.getBlockState(posBelow).setValue(LOWERED, isFlagLowered), 3);
            if (world.getBlockEntity(pos) != null && world.getBlockEntity(pos) instanceof GoalPoleBlockEntity blockEntity)
                blockEntity.markUpdated();

            posBelow = posBelow.below();
        }
    }
}
