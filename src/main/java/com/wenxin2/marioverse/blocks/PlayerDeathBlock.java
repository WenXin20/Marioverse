package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class PlayerDeathBlock extends DeathBlock {
    public static final MapCodec<PlayerDeathBlock> CODEC = simpleCodec(PlayerDeathBlock::new);

    @NotNull
    @Override
    protected MapCodec<? extends PlayerDeathBlock> codec() {
        return CODEC;
    }

    public PlayerDeathBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any());
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof Player)
            super.entityInside(state, level, pos, entity);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
        if (Screen.hasShiftDown()) {
            list.add(Component.literal(""));

            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.ability"));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.unbreakable"));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.description"));

            list.add(Component.literal(""));
        } else list.add(Component.translatable(this.getDescriptionId() + ".tooltip"));
    }
}
