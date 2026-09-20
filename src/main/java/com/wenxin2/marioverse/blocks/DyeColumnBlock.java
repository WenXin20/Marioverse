package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public interface DyeColumnBlock {
    Block getColoredBlock(DyeColor color);

    int getPaintRange();

    int getPaintRate();

    default boolean isInColumn(Block other) {
        return this.getClass().isInstance(other);
    }

    default void tickDye(ServerLevel level, BlockPos pos, BlockState state) {
        PendingDye pending = PendingDyes.MAP.remove(GlobalPos.of(level.dimension(), pos.immutable()));
        if (pending == null || pending.dyeStack().isEmpty() || state.is(pending.coloredBlock()))
            return;

        this.dyeOnly(level, pos, pending.coloredBlock(), pending.particleFace(), pending.particleOptions());
    }

    default ItemInteractionResult useDyeItem(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                             Player player, BlockHitResult hitResult) {
        if (!(stack.getItem() instanceof DyeItem dyeItem))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (this.getPaintRange() == 0)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        DyeColor color = dyeItem.getDyeColor();
        Block coloredBlock = this.getColoredBlock(color);

        int remainingBlocks = Math.max(0, this.getPaintRange() - 1);
        int aboveBlocks = (remainingBlocks + 1) / 2;
        int belowBlocks = remainingBlocks / 2;

        List<BlockPos> abovePos = this.findColumn(level, pos, Direction.UP, aboveBlocks);
        List<BlockPos> belowPos = this.findColumn(level, pos, Direction.DOWN, belowBlocks);

        int aboveDeficit = aboveBlocks - abovePos.size();
        int belowDeficit = belowBlocks - belowPos.size();
        if (belowDeficit > 0)
            abovePos = this.findColumn(level, pos, Direction.UP, aboveBlocks + belowDeficit);
        if (aboveDeficit > 0)
            belowPos = this.findColumn(level, pos, Direction.DOWN, belowBlocks + aboveDeficit);

        boolean isCenterDyed = state.is(coloredBlock);
        boolean hasBlocksToDye = !isCenterDyed
                || abovePos.stream().anyMatch(target -> !level.getBlockState(target).is(coloredBlock))
                || belowPos.stream().anyMatch(target -> !level.getBlockState(target).is(coloredBlock));

        if (!hasBlocksToDye)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (level.isClientSide)
            return ItemInteractionResult.SUCCESS;

        Direction particleFace = hitResult.getDirection();
        DustParticleOptions particleOptions = this.dustOptions(color);

        stack.consume(1, player);

        if (!isCenterDyed)
            this.dyeOnly(level, pos, coloredBlock, particleFace, particleOptions);

        for (int i = 0; i < abovePos.size(); i++)
            this.dyeColumn(level, abovePos.get(i), coloredBlock, stack, player, particleFace,
                    particleOptions, (i + 1) * this.getPaintRate());
        for (int i = 0; i < belowPos.size(); i++)
            this.dyeColumn(level, belowPos.get(i), coloredBlock, stack, player, particleFace,
                    particleOptions, (i + 1) * this.getPaintRate());

        return ItemInteractionResult.SUCCESS;
    }

    default boolean dyeSingleBlock(Level level, BlockPos pos, BlockState state, ItemStack stack,
                                   Player player, Direction particleFace) {
        if (!(stack.getItem() instanceof DyeItem dyeItem))
            return false;

        DyeColor color = dyeItem.getDyeColor();
        Block coloredBlock = this.getColoredBlock(color);
        if (state.is(coloredBlock))
            return false;

        if (!level.isClientSide) {
            stack.consume(1, player);
            this.dyeOnly(level, pos, coloredBlock, particleFace, this.dustOptions(color));
        }
        return true;
    }

    private List<BlockPos> findColumn(Level level, BlockPos origin, Direction direction, int maxSteps) {
        List<BlockPos> found = new ArrayList<>();
        BlockPos.MutableBlockPos current = origin.mutable();

        for (int step = 0; step < maxSteps; step++) {
            current.move(direction);
            if (!this.isInColumn(level.getBlockState(current).getBlock()))
                break;
            found.add(current.immutable());
        }
        return found;
    }

    private void dyeColumn(Level level, BlockPos pos, Block coloredBlock, ItemStack dyeStack, Player player,
                           Direction particleFace, DustParticleOptions particleOptions, int delayTicks) {
        if (!(level instanceof ServerLevel serverLevel))
            return;

        Block currentBlock = level.getBlockState(pos).getBlock();
        if (!this.isInColumn(currentBlock))
            return;

        PendingDyes.MAP.put(GlobalPos.of(serverLevel.dimension(), pos.immutable()),
                new PendingDye(coloredBlock, dyeStack, player, particleFace, particleOptions));
        serverLevel.scheduleTick(pos, currentBlock, delayTicks);
    }

    private void dyeOnly(Level level, BlockPos pos, Block coloredBlock, Direction particleFace,
                         DustParticleOptions particleOptions) {
        level.setBlock(pos, this.recolor(level.getBlockState(pos), coloredBlock), Block.UPDATE_ALL);

        float pitch = 0.9F + level.random.nextFloat() * 0.2F;
        level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, pitch);
        level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
        this.spawnDyeParticles(level, pos, particleFace, particleOptions);
    }

    private BlockState recolor(BlockState oldState, Block coloredBlock) {
        BlockState newState = coloredBlock.defaultBlockState();
        for (Property<?> property : oldState.getProperties())
            newState = this.copyProperty(oldState, newState, property);
        return newState;
    }

    private <T extends Comparable<T>> BlockState copyProperty(BlockState from, BlockState to, Property<T> property) {
        return to.hasProperty(property) ? to.setValue(property, from.getValue(property)) : to;
    }

    private void spawnDyeParticles(Level level, BlockPos pos, Direction particleFace, DustParticleOptions particleOptions) {
        if (!(level instanceof ServerLevel serverLevel))
            return;

        RandomSource random = level.getRandom();
        ServerParticleUtils.spawnParticlesOnBlockFace(particleOptions, serverLevel, pos, particleFace, UniformInt.of(8, 12),
                () -> new Vec3(Mth.nextDouble(random, -0.005F, 0.005F),
                        Mth.nextDouble(random, -0.005F, 0.005F),
                        Mth.nextDouble(random, -0.005F, 0.005F)), 0.45);
    }

    private DustParticleOptions dustOptions(DyeColor color) {
        int textColor = color.getTextColor();
        Vector3f colorVec = new Vector3f((float) (textColor >> 16 & 255) / 255.0F,
                (float) (textColor >> 8 & 255) / 255.0F, (float) (textColor & 255) / 255.0F);
        return new DustParticleOptions(colorVec, 0.5F);
    }

    record PendingDye(Block coloredBlock, ItemStack dyeStack, Player player,
                      Direction particleFace, DustParticleOptions particleOptions) {
    }

    final class PendingDyes {
        static final Map<GlobalPos, PendingDye> MAP = new HashMap<>();

        private PendingDyes() {
        }
    }
}
