package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.utils.AbilitiesHandler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MegaMushroomItem extends PowerUpItem {

    public MegaMushroomItem(Properties properties) {
        super(properties);
    }

    public MegaMushroomItem(int tooltipLineAmt, Properties properties) {
        super(tooltipLineAmt, properties);
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player instanceof AbilitiesHandler handler) {
            handler.applyMegaMushroomPowerUp(level, player, null);
            stack.consume(1, player);
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }
}
