package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import java.util.function.Supplier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

public class PokeySpawnEggItem extends BetterSpawnEggItem {
    public PokeySpawnEggItem(Supplier<? extends EntityType<? extends Mob>> entityType,
                             int primaryColor, int secondaryColor, Properties properties) {
        super(entityType, primaryColor, secondaryColor, properties);
    }

    @Override
    public EntityType<?> getType(ItemStack stack) {
        if (ConfigRegistry.MAX_POKEY_HEIGHT.get() == 1)
            return EntityRegistry.POKEY.get();
        return super.getType(stack);
    }
}
