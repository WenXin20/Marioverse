package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import java.util.function.Supplier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MushroomItem extends BasePowerUpItem {
    public MushroomItem(Supplier<? extends EntityType<? extends Mob>> entityType,
                        int primaryColor, int secondaryColor, Properties properties) {
        super(entityType, primaryColor, secondaryColor, properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        if (entity instanceof AbilitiesHandler handler)
            handler.mv$setMushroomBoostDuration(ConfigRegistry.MUSHROOM_BOOST_DURATION.get());
        return super.finishUsingItem(stack, world, entity);
    }
}
