package com.wenxin2.marioverse.entities.power_ups;

import com.wenxin2.marioverse.entities.ai.controls.JumpInPlaceMoveControl;
import com.wenxin2.marioverse.entities.ai.goals.ContinuousJumpGoal;
import com.wenxin2.marioverse.entities.ai.goals.LookAtEntityTagGoal;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import java.util.List;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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

public class FireFlowerEntity extends AbstractPowerUpEntity implements GeoEntity {
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.fire_flower.idle");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public FireFlowerEntity(EntityType<? extends FireFlowerEntity> entityType, Level world) {
        super(entityType, world);
        this.moveControl = new JumpInPlaceMoveControl(this, 100, null, 1.0F, 1.0F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new ContinuousJumpGoal(this));
        this.goalSelector.addGoal(1, new LookAtEntityTagGoal(this, TagRegistry.CAN_CONSUME_FIRE_FLOWERS, 8.0F, 1.0F));
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
        LivingEntity rider = entity.getControllingPassenger();

        if (entity.getType().is(TagRegistry.POWERS_UP_RIDER) && entity.hasControllingPassenger()
                && rider instanceof AbilitiesHandler handler)
            handler.applyFireFlowerPowerUp(this.level(), rider, this);
        else if (entity instanceof LivingEntity livingEntity && entity instanceof AbilitiesHandler handler)
            handler.applyFireFlowerPowerUp(this.level(), livingEntity, this);
    }

    @Override
    public TagKey<Item> getPowerUpCostumeTag() {
        return TagRegistry.FIRE_COSTUMES;
    }

    @Override
    public List<ItemStack> getPowerUpHatItems() {
        return List.of(ItemRegistry.MARIO_FIRE_HAT.toStack(),
                ItemRegistry.LUIGI_FIRE_HAT.toStack(),
                ItemRegistry.PEACH_CROWN.toStack());
    }

    @Override
    public List<ItemStack> getPowerUpShirtItems() {
        return List.of(ItemRegistry.MARIO_FIRE_SHIRT.toStack(),
                ItemRegistry.LUIGI_FIRE_SHIRT.toStack(),
                ItemRegistry.PEACH_FIRE_BODICE.toStack());
    }

    @Override
    public List<ItemStack> getPowerUpPantsItems() {
        return List.of(ItemRegistry.MARIO_FIRE_PANTS.toStack(),
                ItemRegistry.LUIGI_FIRE_PANTS.toStack(),
                ItemRegistry.PEACH_FIRE_DRESS.toStack());
    }

    @Override
    public List<ItemStack> getPowerUpShoesItems() {
        return List.of(ItemRegistry.MARIO_FIRE_SHOES.toStack(),
                ItemRegistry.LUIGI_FIRE_SHOES.toStack(),
                ItemRegistry.PEACH_FIRE_SHOES.toStack());
    }
}
