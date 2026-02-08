package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.world.SwitchSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class OffBlock extends OnBlock {
    public static final MapCodec<OffBlock> CODEC = simpleCodec(OffBlock::new);

    public OffBlock(Properties properties) {
        super(properties);
    }

    @NotNull
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        if (!state.getValue(ACTIVE))
            return Shapes.block();
        return Shapes.empty();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        SwitchSavedData data = SwitchSavedData.get((ServerLevel) placeContext.getLevel());
        Player player = placeContext.getPlayer();

        if (player.isShiftKeyDown())
            return this.defaultBlockState().setValue(ACTIVE, data.isActive());
        return this.defaultBlockState().setValue(ACTIVE, !data.isActive());
    }
}
