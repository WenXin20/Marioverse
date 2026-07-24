package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.power_up.PowerUpSource;
import com.wenxin2.marioverse.power_up.PowerUpType;
import com.wenxin2.marioverse.registries.PowerUpTypeRegistry;
import net.minecraft.core.Holder;

public class OneUpMushroomItem extends PowerUpItem implements PowerUpSource {
    public OneUpMushroomItem(Properties properties) {
        super(properties);
    }

    public OneUpMushroomItem(int tooltipLineAmt, Properties properties) {
        super(tooltipLineAmt, properties);
    }

    @Override
    public Holder<PowerUpType> getPowerUpType() {
        return PowerUpTypeRegistry.ONE_UP_MUSHROOM;
    }
}