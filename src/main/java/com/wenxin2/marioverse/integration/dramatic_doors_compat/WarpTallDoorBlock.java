package com.wenxin2.marioverse.integration.dramatic_doors_compat;

import com.fizzware.dramaticdoors.neoforge.blocks.TallDoorBlock;
import com.fizzware.dramaticdoors.neoforge.state.properties.TripleBlockPart;
import com.wenxin2.marioverse.blocks.MultiPartWarpDoor;
import com.wenxin2.marioverse.blocks.WarpDoorBlock;
import com.wenxin2.marioverse.blocks.entities.WarpDoorBlockEntity;
import com.wenxin2.marioverse.utils.BlockWarpPlayerHandler;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WarpTallDoorBlock extends TallDoorBlock implements EntityBlock, BlockWarpPlayerHandler, MultiPartWarpDoor {
    private final TallDoorBlock source;

    public WarpTallDoorBlock(TallDoorBlock source) {
        super(source.type(), BlockBehaviour.Properties.ofFullCopy(source));
        this.source = source;
    }

    public Block source() {
        return this.source;
    }

    @NotNull
    @Override
    public MutableComponent getName() {
        return Component.translatable("block.marioverse.warp_door", Component.translatable(this.source.getDescriptionId()));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WarpDoorBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(level, pos, state, entity, stack);
        WarpDoorBlock.assignUUIDs(level, pos, state, stack);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (WarpDoorBlock.canEnterWarp(state, level, pos, entity))
            this.enterWarp(entity, level, pos, pos, state, null);
        super.entityInside(state, level, pos, entity);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        WarpDoorBlock.closeAfterWarp(level.getBlockState(pos), level, pos);
    }

    @Override
    public boolean mv$getBlockWarpTeleportConfig(Entity entity) {
        return WarpDoorBlock.canTeleport(entity);
    }

    @Override
    public List<BlockPos> mv$getParts(BlockState state, BlockPos pos) {
        return switch (state.getValue(THIRD)) {
            case LOWER -> List.of(pos, pos.above(), pos.above(2));
            case MIDDLE -> List.of(pos.below(), pos, pos.above());
            case UPPER -> List.of(pos.below(2), pos.below(), pos);
        };
    }

    @Override
    public boolean mv$isOpen(BlockState state) {
        return state.getValue(OPEN);
    }

    @Override
    public BlockState mv$setOpen(BlockState state, boolean open) {
        return state.setValue(OPEN, open);
    }

    @Override
    public Direction mv$getFacing(BlockState state) {
        return state.getValue(FACING);
    }

    @Override
    public BlockSetType mv$getType() {
        return this.type();
    }

    @Override
    public String mv$getFamily() {
        return "tall";
    }

    @Override
    public Map<String, String> mv$getDropProperties() {
        return Map.of("third", TripleBlockPart.LOWER.getSerializedName());
    }
}
