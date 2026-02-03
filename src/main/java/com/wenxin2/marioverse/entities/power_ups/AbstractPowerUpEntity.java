package com.wenxin2.marioverse.entities.power_ups;

import java.util.List;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class AbstractPowerUpEntity extends BasePowerUpEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public AbstractPowerUpEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public abstract TagKey<Item> getPowerUpCostumeTag();

    public abstract List<ItemStack> getPowerUpHatItems();

    public abstract List<ItemStack> getPowerUpShirtItems();

    public abstract List<ItemStack> getPowerUpPantsItems();

    public abstract List<ItemStack> getPowerUpShoesItems();
}
