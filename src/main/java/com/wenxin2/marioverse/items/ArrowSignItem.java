package com.wenxin2.marioverse.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ArrowSignItem extends Item {
    private final Block hangingSign;
    private final Block standingSign;
    private final Block wallSign;

    public ArrowSignItem(Properties properties, Block standingSign, Block wallSign, Block hangingSign) {
        super(properties);
        this.standingSign = standingSign;
        this.wallSign = wallSign;
        this.hangingSign = hangingSign;
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPlaceContext placeContext = new BlockPlaceContext(context);
        Direction clickedFace = context.getClickedFace();
        Block targetBlock = this.setSignType(clickedFace);
        BlockPos placePos = placeContext.getClickedPos();
        Level level = context.getLevel();
        BlockState newState = targetBlock.getStateForPlacement(placeContext);

        if (newState == null || !placeContext.canPlace())
            return InteractionResult.FAIL;

        if (!newState.canSurvive(level, placePos))
            return InteractionResult.FAIL;

        level.setBlock(placePos, newState, Block.UPDATE_ALL);
        SoundType soundType = newState.getSoundType();
        level.playSound(null, placePos, soundType.getPlaceSound(), SoundSource.BLOCKS,
                (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);

        Player player = context.getPlayer();
        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild)
                context.getItemInHand().shrink(1);
        }

        return InteractionResult.SUCCESS;
    }

    private Block setSignType(Direction clickedFace) {
        return switch (clickedFace) {
            case UP -> this.standingSign;
            case DOWN -> this.hangingSign;
            default -> this.wallSign;
        };
    }
}