package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PottedTrampolineCapBlock extends FlowerPotBlock implements ToggleableBlock {
    protected static final VoxelShape CAP_SHAPE = Shapes
            .or(Block.box(5, 0, 5, 11, 6, 11),
                    Block.box(7, 6, 7, 9, 10, 9),
                    Block.box(4, 10, 4, 12, 12, 12)).optimize();

    public PottedTrampolineCapBlock(@Nullable Supplier<FlowerPotBlock> emptyPot, Supplier<? extends Block> block, Properties properties) {
        super(emptyPot, block, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStatePropertyRegistry.ACTIVE, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStatePropertyRegistry.ACTIVE);
    }

    @NotNull
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        if (this.getPotted() instanceof MegaMushroomBlock)
            return PottedSuperMushroomBlock.MEGA_SHAPE;
        if (this.getPotted() instanceof SuperMushroomBlock)
            return PottedSuperMushroomBlock.SUPER_SHAPE;
        return CAP_SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        return this.getStateForPlacementSavedData(this.defaultBlockState(), placeContext);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        this.onPlaceSavedData(level, pos);
        if (oldState.is(this) && !this.isOn(oldState) && this.isOn(state))
            this.launchEntities(level, pos);
        super.onPlace(state, level, pos, oldState, isMoving);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        this.onRemoveSavedData(level, pos);
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @NotNull
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                              InteractionHand hand, BlockHitResult hitResult) {
        if (!player.getItemInHand(hand).is(ItemRegistry.CREATIVE_WRENCH.get()))
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        super.fallOn(level, state, pos, entity, this.isOn(state) ? fallDistance * 0.5F : fallDistance);
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter blockGetter, Entity entity) {
        BlockState state = blockGetter.getBlockState(entity.getOnPos());
        Vec3 motion = entity.getDeltaMovement();

        if (!entity.isSuppressingBounce() && motion.y < 0.0 && state.is(this) && this.isOn(state))
            entity.setDeltaMovement(motion.x, -motion.y * 0.5F * (entity instanceof LivingEntity ? 1.0 : 0.8), motion.z);
        else super.updateEntityAfterFallOn(blockGetter, entity);
    }

    private boolean isOn(BlockState state) {
        Block potted = this.getPotted();
        boolean isBlue = potted instanceof BlueSuperMushroomTrampolineBlock || potted instanceof BlueMegaMushroomTrampolineBlock
                || potted == BlockRegistry.BLUE_ON_OFF_MUSHROOM_TRAMPOLINE_CAP.get();
        return state.getValue(BlockStatePropertyRegistry.ACTIVE) != isBlue;
    }

    protected void launchEntities(Level level, BlockPos pos) {
        for (Entity entity : level.getEntities(null, new AABB(pos).expandTowards(0.0, 0.5, 0.0))) {
            if (!entity.onGround() || entity.isSuppressingBounce() || !entity.getOnPos().equals(pos))
                continue;

            Vec3 motion = entity.getDeltaMovement();
            entity.setDeltaMovement(motion.x, 0.8, motion.z);
            entity.resetFallDistance();
            entity.hurtMarked = true;
        }
    }
}
