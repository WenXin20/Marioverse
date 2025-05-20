package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.entities.PottedPiranhaPlantBlockEntity;
import com.wenxin2.marioverse.entities.PiranhaPlantEntity;
import com.wenxin2.marioverse.registries.BlockEntityRegistry;
import com.wenxin2.marioverse.registries.DamageTypeRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PottedPiranhaPlantBlock extends FlowerPotBlock implements EntityBlock {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    public PottedPiranhaPlantBlock(@Nullable Supplier<FlowerPotBlock> emptyPot, Supplier<? extends Block> block, Properties properties) {
        super(emptyPot, block, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AXIS);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PottedPiranhaPlantBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return BaseEntityDirectionalBlock.createTickerHelper(type, BlockEntityRegistry.POTTED_PIRANHA_PLANT_BLOCK_ENTITY.get(), PottedPiranhaPlantBlockEntity::tick);
    }

    @NotNull
    @Override
    public ItemStack getCloneItemStack(LevelReader worldReader, BlockPos pos, BlockState state) {
        return new ItemStack(ItemRegistry.PIRANHA_PLANT_POD.get());
    }

    @NotNull
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (this.getPotted() != Blocks.AIR)
            return ItemInteractionResult.CONSUME;
        else return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @NotNull
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            ItemStack stack = new ItemStack(ItemRegistry.PIRANHA_PLANT_POD.get());
            if (!player.addItem(stack))
                player.drop(stack, false);

            world.setBlock(pos, Blocks.FLOWER_POT.defaultBlockState(), 3);
            world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            return InteractionResult.sidedSuccess(world.isClientSide);
        } else return InteractionResult.PASS;
    }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        super.entityInside(state, world, pos, entity);

        if (world.getBlockEntity(pos) instanceof PottedPiranhaPlantBlockEntity blockEntity
                && entity instanceof LivingEntity) {
            if (blockEntity.attackCooldown > 0)
                return;

            if (entity instanceof PiranhaPlantEntity || entity.isSpectator())
                return;

            if (blockEntity.getOwner() != null && blockEntity.getOwner().getUUID().equals(entity.getUUID()))
                return;

            if (blockEntity.getOwner() != null && entity.getTeam() != null && blockEntity.getOwner().getTeam() != null
                    && entity.getTeam() == blockEntity.getOwner().getTeam())
                return;

            if ((blockEntity.getOwner() != null && !((entity instanceof Monster)
                        || entity.getType().is(TagRegistry.PIRANHA_PLANT_CAN_ATTACK)))
                    || (blockEntity.getOwner() == null && !entity.getType().is(TagRegistry.PIRANHA_PLANT_CAN_ATTACK)))
                return;

            float attackDamage = 2.0F;

            if (blockEntity.getOwner() != null)
                entity.hurt(DamageTypeRegistry.piranhaChomp(entity, blockEntity.getOwner()), attackDamage);
            else entity.hurt(world.damageSources().source(DamageTypeRegistry.PIRANHA_CHOMP), attackDamage);

            blockEntity.attackCooldown = 20;
            blockEntity.triggerAnim("bite_controller", "bite");
            world.playSound(null, pos, SoundRegistry.PIRANHA_PLANT_CHOMP.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
        }
    }
}
