package com.wenxin2.marioverse.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.wenxin2.marioverse.blocks.entities.ArrowSignBlockEntity;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import com.wenxin2.marioverse.blocks.states.ArrowDirection;
import com.wenxin2.marioverse.items.WrenchItem;
import com.wenxin2.marioverse.registries.BlockEntityRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import org.joml.Vector3f;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import javax.annotation.Nullable;

public class WallArrowSignBlock extends WallSignBlock {
    public static final EnumProperty<ArrowDirection> ARROW_DIRECTION = BlockStatePropertyRegistry.ARROW_DIRECTION;
    public static final BooleanProperty BOARD = BlockStatePropertyRegistry.BOARD;

    private static final Map<Direction, VoxelShape> SHAPE = Maps.newEnumMap(ImmutableMap
            .of(Direction.NORTH,
                    Shapes.or(Block.box(1, 3, 14, 17, 8, 16),
                            Block.box(-1, 8, 14, 15, 13, 16)).optimize(),
                    Direction.SOUTH,
                    Shapes.or(Block.box(-1, 3, 0, 15, 8, 2),
                            Block.box(1, 8, 0, 17, 13, 2)).optimize(),
                    Direction.EAST,
                    Shapes.or(Block.box(0, 3, 1, 2, 8, 17),
                            Block.box(0, 8, -1, 2, 13, 15)).optimize(),
                    Direction.WEST,
                    Shapes.or(Block.box(14, 3, -1, 16, 8, 15),
                                    Block.box(14, 8, 1, 16, 13, 17)).optimize()));
    
    public WallArrowSignBlock(WoodType woodType, BlockBehaviour.Properties properties) {
        super(woodType, properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(ARROW_DIRECTION, ArrowDirection.UP)
                .setValue(BOARD, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ARROW_DIRECTION, BOARD);
    }

    @NotNull
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArrowSignBlockEntity(BlockEntityRegistry.ARROW_SIGN.get(), pos, state);
    }

    @NotNull
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return SHAPE.get(state.getValue(FACING));
    }

    @NotNull
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!player.getMainHandItem().isEmpty())
            return InteractionResult.PASS;
        return this.rotateArrow(level, state, pos)
                ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    @NotNull
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                              InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof ArrowSignBlockEntity signBlockEntity
                && signBlockEntity.isWaxed())
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        if (this.wax(level, pos, stack, player))
            return ItemInteractionResult.SUCCESS;
        if (this.dye(level, pos, stack, player))
            return ItemInteractionResult.SUCCESS;
        if (this.removeArrow(level, state, pos, stack))
            return ItemInteractionResult.SUCCESS;

        if (stack.is(TagRegistry.WRENCHES)) {
            return this.rotateArrow(level, state, pos)
                    ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.is(TagRegistry.FLAMMABLE_ARROW_SIGNS);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 20;
    }

    protected boolean wax(Level level, BlockPos pos, ItemStack stack, Player player) {
        if (!stack.is(Items.HONEYCOMB))
            return false;
        if (!(level.getBlockEntity(pos) instanceof ArrowSignBlockEntity signBlockEntity)
                || signBlockEntity.isWaxed())
            return false;

        if (!level.isClientSide) {
            signBlockEntity.setWaxed(true);
            stack.consume(1, player);
            level.levelEvent(null, LevelEvent.PARTICLES_AND_SOUND_WAX_ON, pos, 0);
            level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
        }
        return true;
    }

    protected boolean dye(Level level, BlockPos pos, ItemStack stack, Player player) {
        if (!(level.getBlockEntity(pos) instanceof ArrowSignBlockEntity signBlockEntity)
                || signBlockEntity.isWaxed())
            return false;
        if (!(stack.getItem() instanceof DyeItem dyeItem))
            return false;
        if (signBlockEntity.getArrowDyeColor() == dyeItem.getDyeColor())
            return false;

        if (!level.isClientSide) {
            signBlockEntity.setArrowDyeColor(dyeItem.getDyeColor());
            stack.consume(1, player);
            level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS);
            level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);

            int textColor = dyeItem.getDyeColor().getTextColor();
            Vector3f colorVec = new Vector3f((float) (textColor >> 16 & 255) / 255.0F,
                    (float) (textColor >> 8 & 255) / 255.0F, (float) (textColor & 255) / 255.0F);
            ParticleUtils.spawnParticlesOnBlockFaces(level, pos, new DustParticleOptions(colorVec, 1.0F), UniformInt.of(8, 12));
        }
        return true;
    }

    protected boolean rotateArrow(Level level, BlockState state, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof ArrowSignBlockEntity signBlockEntity)
                || signBlockEntity.isWaxed())
            return false;
        if (!state.getValue(BOARD))
            return false;
        if (level.isClientSide)
            return true;

        var direction = state.getValue(ARROW_DIRECTION).next();
        level.setBlock(pos, state.setValue(ARROW_DIRECTION, direction), Block.UPDATE_CLIENTS);
        level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
        level.playSound(null, pos, SoundRegistry.ARROW_ROTATES.get(), SoundSource.BLOCKS);
        signBlockEntity.setArrowDirection(direction);
        return true;
    }

    protected boolean removeArrow(Level level, BlockState state, BlockPos pos, ItemStack stack) {
        if (!(level.getBlockEntity(pos) instanceof ArrowSignBlockEntity signBlockEntity)
                || signBlockEntity.isWaxed())
            return false;
        if (!stack.is(TagRegistry.ARROW_ERASERS))
            return false;
        if (state.getValue(ARROW_DIRECTION) == ArrowDirection.NONE)
            return false;

        if (stack.getItem() instanceof BrushItem)
            level.playSound(null, pos, SoundEvents.BRUSH_GENERIC, SoundSource.BLOCKS);
        if (stack.getItem() instanceof ShearsItem)
            level.playSound(null, pos, SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS);

        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(ARROW_DIRECTION, ArrowDirection.NONE), Block.UPDATE_CLIENTS);
            level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
            signBlockEntity.setArrowDirection(ArrowDirection.NONE);
        }
        return true;
    }
}