package com.wenxin2.marioverse.entities.power_ups;

import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
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
    public void tick() {
        super.tick();
        this.checkForCollisions();
    }

    @Override
    protected boolean canPowerUpPlayer(Player player) {
        return !player.isSpectator()
                && !player.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && player.getType().is(TagRegistry.CAN_CONSUME_ICE_FLOWERS);
    }

    @Override
    protected boolean canPowerUpMob(LivingEntity entity) {
        return !entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && (entity.getType().is(TagRegistry.CAN_CONSUME_ICE_FLOWERS)
                || ConfigRegistry.ICE_FLOWER_POWERS_ALL_MOBS.get());
    }

    @Override
    protected String getPowerUpTag() {
        return "marioverse:has_ice_flower";
    }

    @Override
    protected List<String> disablePowerUpTags() {
        return List.of("marioverse:has_fire_flower");
    }

    @Override
    protected TagKey<Item> getPowerUpCostumeTag() {
        return TagRegistry.ICE_COSTUMES;
    }

    @Override
    protected List<ItemStack> getHatItems() {
        return List.of(ItemRegistry.MARIO_HAT.toStack(),
                ItemRegistry.LUIGI_HAT.toStack(),
                ItemRegistry.PEACH_CROWN.toStack(),
                ItemRegistry.MARIO_ICE_HAT.toStack(),
                ItemRegistry.LUIGI_ICE_HAT.toStack());
    }

    @Override
    protected List<ItemStack> getShirtItems() {
        return List.of(ItemRegistry.MARIO_SHIRT.toStack(),
                ItemRegistry.LUIGI_SHIRT.toStack(),
                ItemRegistry.PEACH_BODICE.toStack(),
                ItemRegistry.MARIO_ICE_SHIRT.toStack(),
                ItemRegistry.LUIGI_ICE_SHIRT.toStack(),
                ItemRegistry.PEACH_ICE_BODICE.toStack());
    }

    @Override
    protected List<ItemStack> getPantsItems() {
        return List.of(ItemRegistry.MARIO_PANTS.toStack(),
                ItemRegistry.LUIGI_PANTS.toStack(),
                ItemRegistry.PEACH_DRESS.toStack(),
                ItemRegistry.MARIO_ICE_PANTS.toStack(),
                ItemRegistry.LUIGI_ICE_PANTS.toStack(),
                ItemRegistry.PEACH_ICE_DRESS.toStack());
    }

    @Override
    protected List<ItemStack> getShoesItems() {
        return List.of(ItemRegistry.MARIO_SHOES.toStack(),
                ItemRegistry.LUIGI_SHOES.toStack(),
                ItemRegistry.PEACH_SHOES.toStack(),
                ItemRegistry.MARIO_ICE_SHOES.toStack(),
                ItemRegistry.LUIGI_ICE_SHOES.toStack(),
                ItemRegistry.PEACH_ICE_SHOES.toStack());
    }

    @Override
    protected void spawnPowerUpParticles(LivingEntity entity, ServerLevel serverWorld) {
        ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.ICE_POWERED_UP.get(), serverWorld, entity, 10);
    }
}
