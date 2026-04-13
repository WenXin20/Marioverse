package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.entities.KoopaShellEntity;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;

public class SmashableBrickBlock extends Block {
    protected static final VoxelShape SHAPE =
            Block.box(0.1, 0.1, 0.1, 15.9, 15.9, 15.9).optimize();

    public SmashableBrickBlock(Properties properties) {
        super(properties);
    }
//
//    @NotNull
//    @Override
//    protected VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
//        return SHAPE;
//    }
//
//    @NotNull
//    @Override
//    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter blockGetter, BlockPos pos) {
//        return Shapes.block();
//    }

//    @Override
//    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
//        if (level.isClientSide) return;
//        if (!(entity instanceof LivingEntity)) return;
//
//        double dx = entity.getX() - entity.xOld;
//        double dz = entity.getZ() - entity.zOld;
//        boolean movingUp = entity.getY() > entity.yOld;
//        boolean belowBlock = entity.getY() + entity.getBbHeight() - 0.1 < pos.getY();
//        boolean canHitBlock = entity.getType().is(TagRegistry.CAN_SMASH_BLOCKS);
//        boolean canGrief = EventHooks.canEntityGrief(level, entity)
//                || (entity instanceof Player player && !player.getAbilities().flying);
//        boolean isMovingHorizontal = Math.abs(dx) > 1e-4 || Math.abs(dz) > 1e-4;
//        boolean canHitBlockOnSide = entity.getType().is(TagRegistry.CAN_SMASH_BLOCKS_FROM_SIDE);
//
//        if (!entity.onGround() && !entity.isSpectator()
//                && movingUp && belowBlock && canHitBlock && canGrief
//                && entity.getData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get()) == 0) {
//            SmashableBrickBlock.smashBlock(level, pos, state, entity);
//            return;
//        }
//
//        if (isMovingHorizontal && canHitBlockOnSide && canGrief
//                && entity.getData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get()) == 0) {
//            Direction direction = Direction.getNearest(dx, 0, dz);
//            SmashableBrickBlock.smashBlockFromSide(level, pos, state, entity, direction);
//        }
//    }

    public static void smashBlock(Level world, BlockPos pos, BlockState state, Entity entity) {
        BlockState stateAbove = world.getBlockState(pos.above());
        ItemStack coinItem = new ItemStack(stateAbove.getBlock().asItem());

        QuestionBlock.hitEntityAbove(pos, world, entity);

        if (entity.getData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM)) {
            if (state.getBlock() instanceof SlabBlock) {
                if (state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE) {
                    world.setBlock(pos, state.setValue(SlabBlock.TYPE, SlabType.TOP), 3);
                } else world.destroyBlock(pos, false);
                world.levelEvent(2001, pos, Block.getId(state));
            } else {
                if (state.getBlock() instanceof DecoratedPotBlock) {
                    world.setBlock(pos, state.setValue(DecoratedPotBlock.CRACKED, true), 4);
                    world.destroyBlock(pos, true, entity);
                } else world.destroyBlock(pos, false);
            }

            entity.setData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get(), 1);
            world.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);

            if (state.is(BlockTags.CRYSTAL_SOUND_BLOCKS))
                world.playSound(null, pos, SoundType.AMETHYST.getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            else if (state.getBlock() instanceof DecoratedPotBlock)
                world.playSound(null, pos, SoundType.DECORATED_POT.getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            else world.playSound(null, pos, SoundRegistry.BLOCK_SMASH.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

            if (stateAbove.getBlock() instanceof StarCoinBlock starCoin)
                StarCoinBlock.collectCoin(starCoin, world, stateAbove, pos.above(), entity, coinItem);
            else if (stateAbove.getBlock() instanceof CoinBlock)
                CoinBlock.collectCoin(world, stateAbove, pos.above(), entity, coinItem);
        } else {
            world.playSound(null, pos, SoundRegistry.BLOCK_SMASH_FAIL.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!(state.getBlock() instanceof QuestionBlock)) {
                if (stateAbove.getBlock() instanceof StarCoinBlock starCoin)
                    StarCoinBlock.collectCoin(starCoin, world, stateAbove, pos.above(), entity, coinItem);
                else if (stateAbove.getBlock() instanceof CoinBlock)
                    CoinBlock.collectCoin(world, stateAbove, pos.above(), entity, coinItem);
            }
        }
    }

    public static void smashBlockFromSide(Level world, BlockPos pos, BlockState state, Entity entity, Direction direction) {
        if (state.is(TagRegistry.SMASHABLE_BLOCKS)) {
            if (entity instanceof KoopaShellEntity shell)
                shell.bounceShell(world, direction);
            smashBlock(world, pos, state, entity);
        }
    }

    @Override
    public boolean isConduitFrame(BlockState state, LevelReader level, BlockPos pos, BlockPos conduit) {
        return state.getBlock() == BlockRegistry.SMASHABLE_DARK_PRISMARINE.get()
                || state.getBlock() == BlockRegistry.SMASHABLE_PRISMARINE_BRICKS.get();
    }
}