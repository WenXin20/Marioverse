package com.wenxin2.marioverse.entities.power_ups;

import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import java.util.List;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class IceFlowerEntity extends AbstractPowerUpEntity implements GeoEntity {
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.ice_flower.idle");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public IceFlowerEntity(EntityType<? extends IceFlowerEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle", 0, this::idleAnimController));
    }

    protected <E extends GeoAnimatable> PlayState idleAnimController(final AnimationState<E> event) {
        event.setAndContinue(IDLE_ANIM);
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public void collideWithEntity(Entity entity) {
        if (entity instanceof LivingEntity livingEntity && entity instanceof AbilitiesHandler handler)
            handler.applyIceFlowerPowerUp(this.level(), livingEntity, this);
    }

    @Override
    public TagKey<Item> getPowerUpCostumeTag() {
        return TagRegistry.ICE_COSTUMES;
    }

    @Override
    public List<ItemStack> getPowerUpHatItems() {
        return List.of(ItemRegistry.MARIO_ICE_HAT.toStack(),
                ItemRegistry.LUIGI_ICE_HAT.toStack(),
                ItemRegistry.PEACH_CROWN.toStack());
    }

    @Override
    public List<ItemStack> getPowerUpShirtItems() {
        return List.of(ItemRegistry.MARIO_ICE_SHIRT.toStack(),
                ItemRegistry.LUIGI_ICE_SHIRT.toStack(),
                ItemRegistry.PEACH_ICE_BODICE.toStack());
    }

    @Override
    public List<ItemStack> getPowerUpPantsItems() {
        return List.of(ItemRegistry.MARIO_ICE_PANTS.toStack(),
                ItemRegistry.LUIGI_ICE_PANTS.toStack(),
                ItemRegistry.PEACH_ICE_DRESS.toStack());
    }

    @Override
    public List<ItemStack> getPowerUpShoesItems() {
        return List.of(ItemRegistry.MARIO_ICE_SHOES.toStack(),
                ItemRegistry.LUIGI_ICE_SHOES.toStack(),
                ItemRegistry.PEACH_ICE_SHOES.toStack());
    }
}
