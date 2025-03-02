package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.blocks.entities.CheckpointFlagBlockEntity;
import com.wenxin2.marioverse.blocks.states.TripleBlockStates;
import com.wenxin2.marioverse.init.BlockRegistry;
import com.wenxin2.marioverse.init.ParticleRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import com.wenxin2.marioverse.network.PacketHandler;
import com.wenxin2.marioverse.network.client_bound.data.AmericaNamePayload;
import com.wenxin2.marioverse.network.client_bound.data.WonderNamePayload;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
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
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CheckpointFlagBlock extends Block implements SimpleWaterloggedBlock, EntityBlock {
    public static final MapCodec<CheckpointFlagBlock> CODEC = simpleCodec(CheckpointFlagBlock::new);
    public static final EnumProperty<TripleBlockStates> PART = EnumProperty.create("part", TripleBlockStates.class);
    public static final BooleanProperty CLAIMED = BooleanProperty.create("claimed");
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final int MAX = RotationSegment.getMaxSegmentIndex();
    private static final int ROTATIONS = MAX + 1;

    protected static final VoxelShape CHECKPOINT_FLAG_TOP =
            Shapes.or(Block.box(7, 0, 7, 9, 4, 9),
            Block.box(6, 4, 6, 10, 8, 10)).optimize();
    protected static final VoxelShape CHECKPOINT_FLAG_MIDDLE =
            Block.box(7, 0, 7, 9, 16, 9).optimize();
    protected static final VoxelShape CHECKPOINT_FLAG_BOTTOM =
            Shapes.or(Block.box(4, 0, 4, 12, 2, 12),
            Block.box(7, 2, 7, 9, 16, 9)).optimize();

    public CheckpointFlagBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(CLAIMED, Boolean.FALSE)
                .setValue(PART, TripleBlockStates.BOTTOM).setValue(ROTATION, 0).setValue(WATERLOGGED, Boolean.FALSE));
    }

    @NotNull
    @Override
    public MapCodec<CheckpointFlagBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(CLAIMED, PART, ROTATION, WATERLOGGED);
    }

    @NotNull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        if (state.getValue(PART) == TripleBlockStates.TOP)
            return CHECKPOINT_FLAG_TOP;
        if (state.getValue(PART) == TripleBlockStates.MIDDLE)
            return CHECKPOINT_FLAG_MIDDLE;
        else return CHECKPOINT_FLAG_BOTTOM;
    }

    @NotNull
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CheckpointFlagBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        final BlockPos pos = context.getClickedPos();
        final Level world = context.getLevel();
        final FluidState fluidState = world.getFluidState(pos);

        if (!this.canPlaceBlock(world, pos) || !this.canPlaceBlock(world, pos.above())
                || !this.canPlaceBlock(world, pos.above(2))) {
            return null;
        }
        return this.defaultBlockState().setValue(WATERLOGGED, fluidState.is(FluidTags.WATER) && fluidState.getAmount() == 8)
                .setValue(ROTATION, RotationSegment.convertToSegment(context.getRotation()));
    }

    @NotNull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor worldAccessor, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED))
            worldAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldAccessor));

        return super.updateShape(state, direction, neighborState, worldAccessor, pos, neighborPos);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(world, pos, state, entity, stack);
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (entity != null && this.canPlaceBlock(world, pos) && this.canPlaceBlock(world, pos.above())
                && this.canPlaceBlock(world, pos.above(2))) {
            world.setBlock(pos.above(), state.setValue(PART, TripleBlockStates.MIDDLE)
                    .setValue(WATERLOGGED, world.getFluidState(pos.above()).getType() == Fluids.WATER), 3);
            world.setBlock(pos.above(2), state.setValue(PART, TripleBlockStates.TOP)
                    .setValue(WATERLOGGED, world.getFluidState(pos.above(2)).getType() == Fluids.WATER), 3);
        }

        if (blockEntity instanceof CheckpointFlagBlockEntity checkpointFlagBE) {
            if (stack.has(DataComponents.CUSTOM_NAME) && checkpointFlagBE.getCustomName() != null) {
                checkpointFlagBE.setCustomName(stack.getHoverName());
                checkpointFlagBE.markUpdated();
                checkpointFlagBE.markUpdatedClients();


                // Ensure the top and middle block entities are also updated for the new states
                BlockEntity middleBlockEntity = world.getBlockEntity(pos.above());
                if (middleBlockEntity instanceof CheckpointFlagBlockEntity middleFlagBE) {
                    if (stack.has(DataComponents.CUSTOM_NAME) && middleFlagBE.getCustomName() == null) {
                        middleFlagBE.setCustomName(stack.getHoverName());
                        middleFlagBE.markUpdated();
                        middleFlagBE.markUpdatedClients();

                        if (middleFlagBE.isWonderFlag()) {
                            middleFlagBE.setWonderFlag(Boolean.TRUE);
                            PacketHandler.sendToAllClients(new WonderNamePayload(pos, middleFlagBE.hasWonderFlag()));
                        } else if (middleFlagBE.isAmericanFlag()) {
                            middleFlagBE.setAmericanFlag(Boolean.TRUE);
                            PacketHandler.sendToAllClients(new AmericaNamePayload(pos, middleFlagBE.hasAmericanFlag()));
                        }
                    }
                }

                BlockEntity topBlockEntity = world.getBlockEntity(pos.above(2));
                if (topBlockEntity instanceof CheckpointFlagBlockEntity topFlagBE) {
                    if (stack.has(DataComponents.CUSTOM_NAME) && topFlagBE.getCustomName() == null) {
                        topFlagBE.setCustomName(stack.getHoverName());
                        topFlagBE.markUpdated();
                        topFlagBE.markUpdatedClients();

                        if (topFlagBE.isWonderFlag()) {
                            topFlagBE.setWonderFlag(Boolean.TRUE);
                            PacketHandler.sendToAllClients(new WonderNamePayload(pos, topFlagBE.hasWonderFlag()));
                        } else if (topFlagBE.isAmericanFlag()) {
                            topFlagBE.setAmericanFlag(Boolean.TRUE);
                            PacketHandler.sendToAllClients(new AmericaNamePayload(pos, topFlagBE.hasAmericanFlag()));
                        }
                    }
                }

                if (checkpointFlagBE.isWonderFlag()) {
                    checkpointFlagBE.setWonderFlag(Boolean.TRUE);
                    PacketHandler.sendToAllClients(new WonderNamePayload(pos, checkpointFlagBE.hasWonderFlag()));
                } else if (checkpointFlagBE.isAmericanFlag()) {
                    checkpointFlagBE.setAmericanFlag(Boolean.TRUE);
                    PacketHandler.sendToAllClients(new AmericaNamePayload(pos, checkpointFlagBE.hasAmericanFlag()));
                }
            }
        }
    }

    @Override
    public void destroy(LevelAccessor worldAccessor, BlockPos pos, BlockState state) {
        if (!worldAccessor.isClientSide()) {
            if (state.getValue(PART) == TripleBlockStates.BOTTOM) {
                worldAccessor.removeBlock(pos.above(), false);
                worldAccessor.removeBlock(pos.above(2), false);
                worldAccessor.levelEvent(2001, pos.above(), Block.getId(worldAccessor.getBlockState(pos.above())));
                worldAccessor.levelEvent(2001, pos.above(2), Block.getId(worldAccessor.getBlockState(pos.above(2))));
            } else if (state.getValue(PART) == TripleBlockStates.MIDDLE) {
                worldAccessor.removeBlock(pos.below(), false);
                worldAccessor.removeBlock(pos.above(), false);
                worldAccessor.levelEvent(2001, pos.below(), Block.getId(worldAccessor.getBlockState(pos.below())));
                worldAccessor.levelEvent(2001, pos.above(), Block.getId(worldAccessor.getBlockState(pos.above())));
            } else if (state.getValue(PART) == TripleBlockStates.TOP) {
                worldAccessor.removeBlock(pos.below(), false);
                worldAccessor.removeBlock(pos.below(2), false);
                worldAccessor.levelEvent(2001, pos.below(), Block.getId(worldAccessor.getBlockState(pos.below())));
                worldAccessor.levelEvent(2001, pos.below(2), Block.getId(worldAccessor.getBlockState(pos.below(2))));
            }
        }
        super.destroy(worldAccessor, pos, state);
    }

    @NotNull
    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (!world.isClientSide) {
            if (player.isCreative() || !player.hasCorrectToolForDrops(state, world, pos)) {
                if (state.getValue(PART) == TripleBlockStates.BOTTOM) {
                    world.removeBlock(pos.above(), false);
                    world.removeBlock(pos.above(2), false);
                    this.spawnDestroyParticles(world, player, pos.above(), world.getBlockState(pos.above()));
                    this.spawnDestroyParticles(world, player, pos.above(2), world.getBlockState(pos.above(2)));
                } else if (state.getValue(PART) == TripleBlockStates.MIDDLE) {
                    world.removeBlock(pos.below(), false);
                    world.removeBlock(pos.above(), false);
                    this.spawnDestroyParticles(world, player, pos.below(), world.getBlockState(pos.below()));
                    this.spawnDestroyParticles(world, player, pos.above(), world.getBlockState(pos.above()));
                } else if (state.getValue(PART) == TripleBlockStates.TOP) {
                    world.removeBlock(pos.below(), false);
                    world.removeBlock(pos.below(2), false);
                    this.spawnDestroyParticles(world, player, pos.below(), world.getBlockState(pos.below()));
                    this.spawnDestroyParticles(world, player, pos.below(2), world.getBlockState(pos.below(2)));
                }
            }
        }
        return super.playerWillDestroy(world, pos, state, player);
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
        if (serverWorld.getBlockEntity(pos) instanceof CheckpointFlagBlockEntity checkpointFlagBE)
            checkpointFlagBE.stopTriggeredAnim("claim_controller", "claim");
        super.tick(state, serverWorld, pos, random);
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        BlockPos respawnPos = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
        BlockPos statePos = switch (state.getValue(PART)) {
            case TOP -> pos.below(2);
            case MIDDLE -> pos.below();
            default -> pos;
        };
        BlockState statePart = world.getBlockState(statePos);


        if (entity.getType().is(TagRegistry.CAN_CLAIM_CHECKPOINT_FLAGS)
                && entity.getPersistentData().getInt("marioverse:claimed_checkpoint_flag_cooldown") <= 0) {
            if (!statePart.getValue(CLAIMED)) {
                if (world.getBlockEntity(statePos) instanceof CheckpointFlagBlockEntity checkpointFlagBE) {
                    checkpointFlagBE.markUpdated();

                    if (!(entity instanceof Player)) {
                        entity.level().broadcastEntityEvent(entity, (byte) 113); // Coin Glint TODO: add glowing star particle
                    } else ParticleUtils.spawnParticlesOnBlockFaces(world, statePos, ParticleRegistry.COIN_GLINT.get(), UniformInt.of(1, 1));

                    if (!checkpointFlagBE.isAmericanFlag() && statePart.getBlock() != BlockRegistry.CLASSIC_GOAL_POLE.get())
                        checkpointFlagBE.triggerAnim("switch_controller", "switch");

                    world.scheduleTick(statePos, this, 40);
                    checkpointFlagBE.triggerAnim("claim_controller", "claim");
                }

                world.scheduleTick(statePos, this, 3);
                world.setBlock(statePos, statePart.setValue(CLAIMED, Boolean.TRUE), 3);
            }

            if (entity instanceof ServerPlayer player && !pos.equals(player.getRespawnPosition())
                    && player.getPersistentData().getInt("marioverse:claimed_checkpoint_flag_cooldown") <= 0) {
                BlockPos playerRespawnPos = player.getRespawnPosition();
                BlockPos newRespawnPos = switch (state.getValue(PART)) {
                    case TOP -> respawnPos.below(2);
                    case MIDDLE -> respawnPos.below();
                    default -> respawnPos;
                };

                if (world.getBlockEntity(respawnPos) instanceof CheckpointFlagBlockEntity checkpointFlagBE
                        && !(newRespawnPos.equals(playerRespawnPos))) {
                    world.scheduleTick(newRespawnPos, this, 40);
                    checkpointFlagBE.triggerAnim("claim_controller", "claim");

                    world.playSound(null, newRespawnPos, SoundRegistry.GOAL_POLE_FINISH.get(), SoundSource.BLOCKS); // TODO
                    ParticleUtils.spawnParticlesOnBlockFaces(world, newRespawnPos, ParticleRegistry.COIN_GLINT.get(), UniformInt.of(1, 1));
                    player.setRespawnPosition(world.dimension(), newRespawnPos, player.getYRot(), false, true);
                    player.getPersistentData().putInt("marioverse:claimed_checkpoint_flag_cooldown", 40);

                    if (world instanceof  ServerLevel serverWorld)
                        serverWorld.sendParticles(ParticleRegistry.COIN_GLINT.get(),
                                newRespawnPos.getX() + 0.5, newRespawnPos.getY() + 0.5, newRespawnPos.getZ() + 0.5,
                                10, 0.4, 0.4, 0.4, 0.2);
                }
            }
        }
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return super.getAnalogOutputSignal(state, world, pos);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag options) {
        super.appendHoverText(stack, tooltipContext, list, options);
        list.add(Component.translatable(this.getDescriptionId() + ".tooltip"));
    }

    private boolean canPlaceBlock(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);

        return (state.isAir() || state.canBeReplaced() || state.is(this))
                && world.getWorldBorder().isWithinBounds(pos);
    }
}
