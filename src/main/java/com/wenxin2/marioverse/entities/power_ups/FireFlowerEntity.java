package com.wenxin2.marioverse.entities.power_ups;

import com.wenxin2.marioverse.entities.GoombaEntity;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.ItemRegistry;
import com.wenxin2.marioverse.init.ParticleRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.data.SlotTypeLoader;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.player.Player;
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

public class FireFlowerEntity extends BasePowerUpEntity implements GeoEntity {
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.fire_flower.idle");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public FireFlowerEntity(EntityType<? extends FireFlowerEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
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
    public void handleCollision(Entity entity) {
        if (!this.level().isClientSide) {

            if (entity instanceof Player player && !player.isSpectator()
                    && !player.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                    && player.getType().is(TagRegistry.CAN_CONSUME_FIRE_FLOWERS)) {
                AccessoriesCapability capability = AccessoriesCapability.get(player);

                if (!player.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)) {
                    if (player.getPersistentData().getBoolean("marioverse:has_fire_flower"))
                        this.level().broadcastEntityEvent(this, (byte) 20); // Poof particle
                    else this.level().broadcastEntityEvent(player, (byte) 123); // Fire Powered Up particle
                }

                if (player.getHealth() < player.getMaxHealth())
                    player.heal(ConfigRegistry.MUSHROOM_HEALTH_HEALED.get().floatValue());
                player.getPersistentData().putBoolean("marioverse:has_fire_flower", Boolean.TRUE);
                player.getPersistentData().putBoolean("marioverse:has_mushroom", Boolean.TRUE);
                this.level().playSound(null, this.blockPosition(), SoundRegistry.PLAYER_POWERS_UP.get(),
                        SoundSource.PLAYERS, 1.0F, 1.0F);
                this.remove(RemovalReason.KILLED);

                if (capability != null && ConfigRegistry.EQUIP_COSTUMES_PLAYERS.get()) {
                    AccessoriesContainer containerHat = capability.getContainer(SlotTypeLoader.getSlotType(player, "costume_hat"));
                    AccessoriesContainer containerShirt = capability.getContainer(SlotTypeLoader.getSlotType(player, "costume_shirt"));
                    AccessoriesContainer containerPants = capability.getContainer(SlotTypeLoader.getSlotType(player, "costume_pants"));
                    AccessoriesContainer containerShoes = capability.getContainer(SlotTypeLoader.getSlotType(player, "costume_shoes"));

                    if (containerHat != null && containerHat.getAccessories().getItem(0).getItem() != ItemRegistry.FIRE_HAT.get())
                        containerHat.getAccessories().setItem(0, new ItemStack(ItemRegistry.FIRE_HAT.get()));
                    if (containerShirt != null && containerShirt.getAccessories().getItem(0).getItem() != ItemRegistry.FIRE_SHIRT.get())
                        containerShirt.getAccessories().setItem(0, new ItemStack(ItemRegistry.FIRE_SHIRT.get()));
                    if (containerPants != null && containerPants.getAccessories().getItem(0).getItem() != ItemRegistry.FIRE_OVERALLS.get())
                        containerPants.getAccessories().setItem(0, new ItemStack(ItemRegistry.FIRE_OVERALLS.get()));
                    if (containerShoes != null && containerShoes.getAccessories().getItem(0).getItem() != ItemRegistry.FIRE_SHOES.get())
                        containerShoes.getAccessories().setItem(0, new ItemStack(ItemRegistry.FIRE_SHOES.get()));
                }
            } else if (entity instanceof LivingEntity livingEntity
                    && !livingEntity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                    && (livingEntity.getType().is(TagRegistry.CAN_CONSUME_FIRE_FLOWERS)
                        || ConfigRegistry.FIRE_FLOWER_POWERS_ALL_MOBS.get())
                    && !(livingEntity instanceof Player)) {
                AccessoriesCapability capability = AccessoriesCapability.get(livingEntity);

                if (livingEntity.getPersistentData().getBoolean("marioverse:has_fire_flower"))
                    this.level().broadcastEntityEvent(this, (byte) 20); // Poof particle
                else this.level().broadcastEntityEvent(livingEntity, (byte) 123); // Fire Powered Up particle

                if (livingEntity.getHealth() > livingEntity.getMaxHealth() * ConfigRegistry.HEALTH_SHRINK_MOBS.get()) {
                    livingEntity.getPersistentData().putBoolean("marioverse:has_fire_flower", Boolean.TRUE);
                    livingEntity.getPersistentData().putBoolean("marioverse:has_mushroom", Boolean.TRUE);
                } else {
                    livingEntity.getPersistentData().putBoolean("marioverse:has_mushroom", Boolean.TRUE);
                    livingEntity.getPersistentData().putBoolean("marioverse:has_fire_flower", Boolean.TRUE);
                    this.level().broadcastEntityEvent(livingEntity, (byte) 123); // Fire Powered Up particle
                    float scaleFactor = livingEntity.getBbHeight() * livingEntity.getBbWidth();
                    int numParticles = (int) (scaleFactor * 20);
                    double radius = livingEntity.getBbWidth() / 2;

                    for (int i = 0; i < numParticles; i++) {
                        // Calculate angle for each particle
                        double angle = 2 * Math.PI * i / numParticles;
                        // Calculate the X and Z offset using sine and cosine to spread in an ellipse
                        double offsetX = Math.cos(angle) * radius;
                        double offsetY = livingEntity.getBbHeight() / 2;
                        double offsetZ = Math.sin(angle) * radius;

                        double x = livingEntity.getX() + offsetX;
                        double y = livingEntity.getY() + offsetY;
                        double z = livingEntity.getZ() + offsetZ;

                        livingEntity.level().addParticle(ParticleRegistry.POWERED_UP.get(), x, y, z, 0, 100.0, 0);
                    }
                }

                if (livingEntity.getHealth() < livingEntity.getMaxHealth())
                    livingEntity.heal(ConfigRegistry.MUSHROOM_HEALTH_HEALED.get().floatValue());
                this.level().playSound(null, this.blockPosition(), SoundRegistry.PLAYER_POWERS_UP.get(),
                        SoundSource.PLAYERS, 1.0F, 1.0F);
                this.remove(RemovalReason.KILLED);

                if (livingEntity instanceof GoombaEntity goomba && goomba.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
                    goomba.equipItemIfPossible(new ItemStack(ItemRegistry.FIRE_HAT.get()));
                }

                if (capability != null && ConfigRegistry.EQUIP_COSTUMES_MOBS.get()) {
                    AccessoriesContainer containerHat = capability.getContainer(SlotTypeLoader.getSlotType(livingEntity, "costume_hat"));
                    AccessoriesContainer containerShirt = capability.getContainer(SlotTypeLoader.getSlotType(livingEntity, "costume_shirt"));
                    AccessoriesContainer containerPants = capability.getContainer(SlotTypeLoader.getSlotType(livingEntity, "costume_pants"));
                    AccessoriesContainer containerShoes = capability.getContainer(SlotTypeLoader.getSlotType(livingEntity, "costume_shoes"));

                    if (containerHat != null && containerHat.getAccessories().getItem(0).getItem() != ItemRegistry.FIRE_HAT.get())
                        containerHat.getAccessories().setItem(0, new ItemStack(ItemRegistry.FIRE_HAT.get()));
                    if (containerShirt != null && containerShirt.getAccessories().getItem(0).getItem() != ItemRegistry.FIRE_SHIRT.get())
                        containerShirt.getAccessories().setItem(0, new ItemStack(ItemRegistry.FIRE_SHIRT.get()));
                    if (containerPants != null && containerPants.getAccessories().getItem(0).getItem() != ItemRegistry.FIRE_OVERALLS.get())
                        containerPants.getAccessories().setItem(0, new ItemStack(ItemRegistry.FIRE_OVERALLS.get()));
                    if (containerShoes != null && containerShoes.getAccessories().getItem(0).getItem() != ItemRegistry.FIRE_SHOES.get())
                        containerShoes.getAccessories().setItem(0, new ItemStack(ItemRegistry.FIRE_SHOES.get()));
                }
            }
        }
    }
}
