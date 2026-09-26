package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.states.HalfBlockStates;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.VoxelShapeUtils;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LogBridgeBlock extends BridgeBlock implements SimpleWaterloggedBlock {
    public Block logBlock;

    protected static final VoxelShape BOTTOM_AABB_Z =
            Shapes.or(Block.box(0, 0, 1, 16, 3, 4),
                    Block.box(0, 0, 6, 16, 4, 10),
                    Block.box(0, 0, 12, 16, 3, 15)).optimize();

    protected static final VoxelShape TOP_AABB_Z =
            Shapes.or(Block.box(0, 13, 1, 16, 16, 4),
                    Block.box(0, 12, 6, 16, 16, 10),
                    Block.box(0, 13, 12, 16, 16, 15)).optimize();

    protected static final VoxelShape BOTTOM_AABB_X = VoxelShapeUtils.rotateShape(Direction.NORTH, Direction.EAST, BOTTOM_AABB_Z);

    protected static final VoxelShape TOP_AABB_X = VoxelShapeUtils.rotateShape(Direction.NORTH, Direction.EAST, TOP_AABB_Z);

    protected static final VoxelShape BOTTOM_COLLISION = Block.box(0.0, 1.0, 0.0, 16.0, 8.0, 16.0);

    protected static final VoxelShape TOP_COLLISION = Block.box(0.0, 9.0, 0.0, 16.0, 16.0, 16.0);

    public LogBridgeBlock(Block logBlock, Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X)
                .setValue(HALF, HalfBlockStates.BOTTOM).setValue(WATERLOGGED, false));
        this.logBlock = logBlock;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(AXIS, HALF, WATERLOGGED);
    }

    @NotNull
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        Direction.Axis axis = state.getValue(AXIS);
        HalfBlockStates stateValue = state.getValue(HALF);

        if (axis == Direction.Axis.X) {
            if (stateValue == HalfBlockStates.TOP)
                return TOP_AABB_X;
            return BOTTOM_AABB_X;
        } else {
            if (stateValue == HalfBlockStates.TOP)
                return TOP_AABB_Z;
            return BOTTOM_AABB_Z;
        }
    }

    @NotNull
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        HalfBlockStates stateValue = state.getValue(HALF);

        if (collisionContext instanceof EntityCollisionContext context && context.getEntity() != null) {
            if (stateValue == HalfBlockStates.TOP) {
                if (!context.isAbove(BOTTOM_COLLISION, pos, false))
                    return Shapes.empty();
            } else if (stateValue == HalfBlockStates.BOTTOM) {
                if (!context.isAbove(TOP_COLLISION, pos.below(), false))
                    return Shapes.empty();
            }
        }
        return super.getCollisionShape(state, blockGetter, pos, collisionContext);
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if (itemAbility.equals(ItemAbilities.AXE_STRIP)) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
            String path = id.getPath();
            String logName = BuiltInRegistries.BLOCK.getKey(logBlock).getPath();

            if (!path.contains("stripped_")
                    && (path.contains("bamboo_bridge") || path.contains("log_bridge") || path.contains("stem_bridge"))) {
                String removeBlockName = logName.replace("_block", "");
                String strippedPath = path.replace(removeBlockName, "stripped_" + removeBlockName);
                ResourceLocation strippedId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), strippedPath);

                Block strippedBlock = BuiltInRegistries.BLOCK.get(strippedId);

                if (strippedBlock != Blocks.AIR && strippedId.getPath().contains("stripped_")) {
                    return strippedBlock.defaultBlockState()
                            .setValue(AXIS, state.getValue(AXIS))
                            .setValue(HALF, state.getValue(HALF))
                            .setValue(WATERLOGGED, state.getValue(WATERLOGGED));
                }
            }
        }
        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.is(TagRegistry.FLAMMABLE_BRIDGE_BLOCKS);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }
}
