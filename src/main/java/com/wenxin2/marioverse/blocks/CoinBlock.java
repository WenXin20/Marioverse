package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.entities.CoinBlockEntity;
import com.wenxin2.marioverse.entities.GoldKoopaShellEntity;
import com.wenxin2.marioverse.entities.KoopaShellEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CoinBlock extends Block implements SimpleWaterloggedBlock, EntityBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape COIN_SHAPE = Block.box(3.0, 2.5, 3.0, 14.0, 14.5, 14.0).optimize();

    public CoinBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(WATERLOGGED);
    }

    @NotNull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return COIN_SHAPE;
    }

    @NotNull
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CoinBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        FluidState fluidState = placeContext.getLevel().getFluidState(placeContext.getClickedPos());

        return this.defaultBlockState().setValue(WATERLOGGED, fluidState.is(FluidTags.WATER) && fluidState.getAmount() == 8);
    }

    @NotNull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor worldAccessor, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED))
            worldAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldAccessor));

        return super.updateShape(state, direction, neighborState, worldAccessor, pos, neighborPos);
    }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        ItemStack coinItem = new ItemStack(this.asItem());

        if (entity instanceof KoopaShellEntity koopaShell && !(entity instanceof GoldKoopaShellEntity)
                && koopaShell.getOwner() != null && koopaShell.getOwner().getType().is(TagRegistry.CAN_COLLECT_COINS))
            CoinBlock.collectCoin(world, state, pos, koopaShell.getOwner(), coinItem);
        else if (entity.getType().is(TagRegistry.CAN_COLLECT_COINS) && ConfigRegistry.COINS_COLLECTED_ON_COLLISION.get()) {
            if (entity instanceof Player player && player.isCreative() && !ConfigRegistry.COINS_COLLECTED_IN_CREATIVE.get())
                return;
            CoinBlock.collectCoin(world, state, pos, entity, coinItem);
        }
    }

    public static void collectCoin(Level world, BlockState state, BlockPos pos, Entity entity, ItemStack coinItem) {
        boolean itemAdded = false;

        if (world instanceof ServerLevel serverWorld)
            ServerParticleUtils.spawnParticlesOnBlockFaces(ParticleRegistry.COIN_GLINT.get(), serverWorld, pos, UniformInt.of(1, 1));

        world.playSound(null, pos, SoundRegistry.COIN_PICKUP.get(), SoundSource.BLOCKS);
        world.removeBlock(pos, false);

        if (entity instanceof Player player) {
            itemAdded = player.addItem(coinItem);

            if (!itemAdded)
                player.drop(coinItem, false);

            if (state.is(BlockTags.GUARDED_BY_PIGLINS))
                PiglinAi.angerNearbyPiglins(player, false);
        } else if (entity instanceof LivingEntity livingEntity && livingEntity.getMainHandItem().isEmpty()) {
            livingEntity.setItemInHand(InteractionHand.MAIN_HAND, coinItem);
            livingEntity.swing(InteractionHand.MAIN_HAND);
            itemAdded = true;
        } else if (entity instanceof LivingEntity livingEntity && livingEntity.getOffhandItem().isEmpty()) {
            livingEntity.setItemInHand(InteractionHand.OFF_HAND, coinItem);
            livingEntity.swing(InteractionHand.OFF_HAND);
            itemAdded = true;
        } else if (entity instanceof InventoryCarrier carrier) {
            SimpleContainer inventory = carrier.getInventory();

            if (entity instanceof LivingEntity livingEntity)
                livingEntity.swing(InteractionHand.MAIN_HAND);

            for (int i = 0; i < inventory.getContainerSize(); i++) {
                if (inventory.getItem(i).isEmpty()) {
                    inventory.setItem(i, coinItem);
                    itemAdded = true;
                    break;
                }
            }
        } else if (entity instanceof Container container) {
            if (entity instanceof LivingEntity livingEntity)
                livingEntity.swing(InteractionHand.MAIN_HAND);

            for (int i = 0; i < container.getContainerSize(); i++) {
                if (container.getItem(i).isEmpty()) {
                    container.setItem(i, coinItem);
                    itemAdded = true;
                    break;
                }
            }
        }

        if (!itemAdded)
            entity.spawnAtLocation(coinItem);
    }

    @NotNull
    @Override
    public FluidState getFluidState(final BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}
