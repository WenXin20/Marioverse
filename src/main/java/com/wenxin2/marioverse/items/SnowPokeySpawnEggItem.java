package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import java.util.function.Supplier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

public class SnowPokeySpawnEggItem extends DeferredSpawnEggItem {
    public SnowPokeySpawnEggItem(Supplier<? extends EntityType<? extends Mob>> entityType,
                                 int primaryColor, int secondaryColor, Properties properties) {
        super(entityType, primaryColor, secondaryColor, properties);
    }

    @Override
    public EntityType<?> getType(ItemStack stack) {
        if (ConfigRegistry.MAX_SNOW_POKEY_HEIGHT.get() == 1)
            return EntityRegistry.SNOW_POKEY.get();
        return super.getType(stack);
    }
}
