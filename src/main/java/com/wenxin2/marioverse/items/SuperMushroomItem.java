package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.power_up.PowerUpSource;
import com.wenxin2.marioverse.power_up.PowerUpType;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.PowerUpTypeRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class SuperMushroomItem extends PowerUpItem implements PowerUpSource {

    public SuperMushroomItem(Properties properties) {
        super(properties);
    }

    public SuperMushroomItem(int tooltipLineAmt, Properties properties) {
        super(tooltipLineAmt, properties);
    }

    @Override
    public Holder<PowerUpType> getPowerUpType() {
        return PowerUpTypeRegistry.SUPER_MUSHROOM;
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player instanceof AbilitiesHandler handler) {
            handler.applySuperMushroomPowerUp(level, player, null,
                    ConfigRegistry.SUPER_MUSHROOM_HEALTH_HEALED.get().floatValue());
            stack.consume(1, player);
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }
}
