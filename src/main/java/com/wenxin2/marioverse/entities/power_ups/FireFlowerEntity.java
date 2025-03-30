package com.wenxin2.marioverse.entities.power_ups;

import com.wenxin2.marioverse.entities.GoombaEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.data.SlotTypeLoader;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
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

public class FireFlowerEntity extends AbstractPowerUpEntity implements GeoEntity {
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.fire_flower.idle");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public FireFlowerEntity(EntityType<? extends FireFlowerEntity> entityType, Level world) {
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

//    @Override
//    public void handleCollision(Entity entity) {
//        if (!this.level().isClientSide) {
//
//            if (entity instanceof Player player && !player.isSpectator()
//                    && !player.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
//                    && player.getType().is(TagRegistry.CAN_CONSUME_FIRE_FLOWERS)) {
//                AccessoriesCapability capability = AccessoriesCapability.get(player);
//
//                if (!player.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)) {
//                    if (player.getPersistentData().getBoolean("marioverse:has_fire_flower"))
//                        this.level().broadcastEntityEvent(this, (byte) 20); // Poof particle
//                    else this.level().broadcastEntityEvent(player, (byte) 123); // Fire Powered Up particle
//                }
//
//                if (player.getHealth() < player.getMaxHealth())
//                    player.heal(ConfigRegistry.MUSHROOM_HEALTH_HEALED.get().floatValue());
//                player.getPersistentData().putBoolean("marioverse:has_mushroom", Boolean.TRUE);
//                player.getPersistentData().putBoolean("marioverse:has_fire_flower", Boolean.TRUE);
//                player.getPersistentData().putBoolean("marioverse:has_fire_flower", Boolean.FALSE);
//                this.level().playSound(null, this.blockPosition(), SoundRegistry.PLAYER_POWERS_UP.get(),
//                        SoundSource.PLAYERS, 1.0F, 1.0F);
//                this.remove(RemovalReason.KILLED);
//
//                if (capability != null && ConfigRegistry.EQUIP_COSTUMES_PLAYERS.get()) {
//                    AccessoriesContainer containerHat = capability.getContainer(SlotTypeLoader.getSlotType(player, "costume_hat"));
//                    AccessoriesContainer containerShirt = capability.getContainer(SlotTypeLoader.getSlotType(player, "costume_shirt"));
//                    AccessoriesContainer containerPants = capability.getContainer(SlotTypeLoader.getSlotType(player, "costume_pants"));
//                    AccessoriesContainer containerShoes = capability.getContainer(SlotTypeLoader.getSlotType(player, "costume_shoes"));
//
//                    if (containerHat != null && !containerHat.getAccessories().getItem(0).is(TagRegistry.FIRE_COSTUMES)) {
//                        ItemStack stack = containerHat.getAccessories().getItem(0);
//                        ItemStack newStack = ItemRegistry.MARIO_FIRE_HAT.toStack();
//
//                        if (containerHat.getAccessories().getItem(0).is(TagRegistry.MARIO_COSTUMES))
//                            newStack = ItemRegistry.MARIO_FIRE_HAT.toStack();
//                        else if (containerHat.getAccessories().getItem(0).is(TagRegistry.LUIGI_COSTUMES))
//                            newStack = ItemRegistry.LUIGI_FIRE_HAT.toStack();
//
//                        newStack.applyComponents(stack.getComponents());
//                        containerHat.getAccessories().setItem(0, newStack);
//                    }
//
//                    if (containerShirt != null && !containerShirt.getAccessories().getItem(0).is(TagRegistry.FIRE_COSTUMES)) {
//                        ItemStack stack = containerShirt.getAccessories().getItem(0);
//                        ItemStack newStack = ItemRegistry.MARIO_FIRE_SHIRT.toStack();
//
//                        if (containerShirt.getAccessories().getItem(0).is(TagRegistry.MARIO_COSTUMES))
//                            newStack = ItemRegistry.MARIO_FIRE_SHIRT.toStack();
//                        else if (containerShirt.getAccessories().getItem(0).is(TagRegistry.LUIGI_COSTUMES))
//                            newStack = ItemRegistry.LUIGI_FIRE_SHIRT.toStack();
//
//                        newStack.applyComponents(stack.getComponents());
//                        containerShirt.getAccessories().setItem(0, newStack);
//                    }
//
//                    if (containerPants != null && !containerPants.getAccessories().getItem(0).is(TagRegistry.FIRE_COSTUMES)) {
//                        ItemStack stack = containerPants.getAccessories().getItem(0);
//                        ItemStack newStack = ItemRegistry.MARIO_FIRE_PANTS.toStack();
//
//                        if (containerPants.getAccessories().getItem(0).is(TagRegistry.MARIO_COSTUMES))
//                            newStack = ItemRegistry.MARIO_FIRE_PANTS.toStack();
//                        else if (containerPants.getAccessories().getItem(0).is(TagRegistry.LUIGI_COSTUMES))
//                            newStack = ItemRegistry.LUIGI_FIRE_PANTS.toStack();
//
//                        newStack.applyComponents(stack.getComponents());
//                        containerPants.getAccessories().setItem(0, newStack);
//                    }
//
//                    if (containerShoes != null && !containerShoes.getAccessories().getItem(0).is(TagRegistry.FIRE_COSTUMES)) {
//                        ItemStack stack = containerShoes.getAccessories().getItem(0);
//                        ItemStack newStack = ItemRegistry.MARIO_FIRE_SHOES.toStack();
//
//                        if (containerShoes.getAccessories().getItem(0).is(TagRegistry.MARIO_COSTUMES))
//                            newStack = ItemRegistry.MARIO_FIRE_SHOES.toStack();
//                        else if (containerShoes.getAccessories().getItem(0).is(TagRegistry.LUIGI_COSTUMES))
//                            newStack = ItemRegistry.LUIGI_FIRE_SHOES.toStack();
//
//                        newStack.applyComponents(stack.getComponents());
//                        containerShoes.getAccessories().setItem(0, newStack);
//                    }
//                }
//            } else if (entity instanceof LivingEntity livingEntity
//                    && !livingEntity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
//                    && (livingEntity.getType().is(TagRegistry.CAN_CONSUME_FIRE_FLOWERS)
//                        || ConfigRegistry.FIRE_FLOWER_POWERS_ALL_MOBS.get())
//                    && !(livingEntity instanceof Player)) {
//                AccessoriesCapability capability = AccessoriesCapability.get(livingEntity);
//
//                if (livingEntity.getPersistentData().getBoolean("marioverse:has_fire_flower"))
//                    this.level().broadcastEntityEvent(this, (byte) 20); // Poof particle
//                else this.level().broadcastEntityEvent(livingEntity, (byte) 123); // Fire Powered Up particle
//
//                if (livingEntity.getHealth() > livingEntity.getMaxHealth() * ConfigRegistry.SHRINK_MOBS_AT_HEALTH.get()) {
//                    livingEntity.getPersistentData().putBoolean("marioverse:has_mushroom", Boolean.TRUE);
//                    livingEntity.getPersistentData().putBoolean("marioverse:has_fire_flower", Boolean.TRUE);
//                    livingEntity.getPersistentData().putBoolean("marioverse:has_fire_flower", Boolean.FALSE);
//                }
//
//                if (livingEntity.getHealth() < livingEntity.getMaxHealth())
//                    livingEntity.heal(ConfigRegistry.MUSHROOM_HEALTH_HEALED.get().floatValue());
//                this.level().playSound(null, this.blockPosition(), SoundRegistry.PLAYER_POWERS_UP.get(),
//                        SoundSource.PLAYERS, 1.0F, 1.0F);
//                this.remove(RemovalReason.KILLED);
//
//                if (livingEntity instanceof GoombaEntity goomba
//                        && (goomba.getItemBySlot(EquipmentSlot.HEAD).isEmpty()
//                            || goomba.getItemBySlot(EquipmentSlot.HEAD).is(TagRegistry.MARIO_COSTUMES))) {
//                    ItemStack stack = goomba.getItemBySlot(EquipmentSlot.HEAD).getItem().getDefaultInstance();
//                    ItemStack newStack = ItemRegistry.MARIO_FIRE_HAT.toStack();
//
//                    newStack.applyComponents(stack.getComponents());
//                    goomba.setItemSlot(EquipmentSlot.HEAD, newStack);
//                }
//
//                if (capability != null && ConfigRegistry.EQUIP_COSTUMES_MOBS.get()) {
//                    AccessoriesContainer containerHat = capability.getContainer(SlotTypeLoader.getSlotType(livingEntity, "costume_hat"));
//                    AccessoriesContainer containerShirt = capability.getContainer(SlotTypeLoader.getSlotType(livingEntity, "costume_shirt"));
//                    AccessoriesContainer containerPants = capability.getContainer(SlotTypeLoader.getSlotType(livingEntity, "costume_pants"));
//                    AccessoriesContainer containerShoes = capability.getContainer(SlotTypeLoader.getSlotType(livingEntity, "costume_shoes"));
//
//                    if (containerHat != null && !containerHat.getAccessories().getItem(0).is(TagRegistry.FIRE_COSTUMES)) {
//                        List<ItemStack> fireCostumes = List.of(
//                                ItemRegistry.MARIO_FIRE_HAT.toStack(),
//                                ItemRegistry.LUIGI_FIRE_HAT.toStack());
//
//                        ItemStack stack = containerHat.getAccessories().getItem(0);
//                        ItemStack newStack = fireCostumes.get((int) (Math.random() * fireCostumes.size()));
//
//                        if (containerHat.getAccessories().getItem(0).is(TagRegistry.MARIO_COSTUMES))
//                            newStack = ItemRegistry.MARIO_FIRE_HAT.toStack();
//                        else if (containerHat.getAccessories().getItem(0).is(TagRegistry.LUIGI_COSTUMES))
//                            newStack = ItemRegistry.LUIGI_FIRE_HAT.toStack();
//
//                        newStack.applyComponents(stack.getComponents());
//                        containerHat.getAccessories().setItem(0, newStack);
//                    }
//
//                    if (containerShirt != null && !containerShirt.getAccessories().getItem(0).is(TagRegistry.FIRE_COSTUMES)) {
//                        List<ItemStack> fireCostumes = List.of(
//                                ItemRegistry.MARIO_FIRE_SHIRT.toStack(),
//                                ItemRegistry.MARIO_FIRE_SHIRT.toStack());
//
//                        ItemStack stack = containerShirt.getAccessories().getItem(0);
//                        ItemStack newStack = fireCostumes.get((int) (Math.random() * fireCostumes.size()));
//
//                        if (containerShirt.getAccessories().getItem(0).is(TagRegistry.MARIO_COSTUMES))
//                            newStack = ItemRegistry.LUIGI_FIRE_SHIRT.toStack();
//                        else if (containerShirt.getAccessories().getItem(0).is(TagRegistry.LUIGI_COSTUMES))
//                            newStack = ItemRegistry.LUIGI_FIRE_SHIRT.toStack();
//
//                        newStack.applyComponents(stack.getComponents());
//                        containerShirt.getAccessories().setItem(0, newStack);
//                    }
//
//                    if (containerPants != null && !containerPants.getAccessories().getItem(0).is(TagRegistry.FIRE_COSTUMES)) {
//                        List<ItemStack> fireCostumes = List.of(
//                                ItemRegistry.LUIGI_FIRE_PANTS.toStack(),
//                                ItemRegistry.LUIGI_FIRE_PANTS.toStack());
//
//                        ItemStack stack = containerPants.getAccessories().getItem(0);
//                        ItemStack newStack = fireCostumes.get((int) (Math.random() * fireCostumes.size()));
//
//                        if (containerPants.getAccessories().getItem(0).is(TagRegistry.MARIO_COSTUMES))
//                            newStack = ItemRegistry.LUIGI_FIRE_PANTS.toStack();
//                        else if (containerPants.getAccessories().getItem(0).is(TagRegistry.LUIGI_COSTUMES))
//                            newStack = ItemRegistry.LUIGI_FIRE_PANTS.toStack();
//
//                        newStack.applyComponents(stack.getComponents());
//                        containerPants.getAccessories().setItem(0, newStack);
//                    }
//
//                    if (containerShoes != null && !containerShoes.getAccessories().getItem(0).is(TagRegistry.FIRE_COSTUMES)) {
//                        List<ItemStack> fireCostumes = List.of(
//                                ItemRegistry.MARIO_FIRE_SHOES.toStack(),
//                                ItemRegistry.LUIGI_FIRE_SHOES.toStack());
//
//                        ItemStack stack = containerShoes.getAccessories().getItem(0);
//                        ItemStack newStack = fireCostumes.get((int) (Math.random() * fireCostumes.size()));
//
//                        if (containerShoes.getAccessories().getItem(0).is(TagRegistry.MARIO_COSTUMES))
//                            newStack = ItemRegistry.MARIO_FIRE_SHOES.toStack();
//                        else if (containerShoes.getAccessories().getItem(0).is(TagRegistry.LUIGI_COSTUMES))
//                            newStack = ItemRegistry.LUIGI_FIRE_SHOES.toStack();
//
//                        newStack.applyComponents(stack.getComponents());
//                        containerShoes.getAccessories().setItem(0, newStack);
//                    }
//                }
//            }
//        }
//    }

    @Override
    protected boolean canPowerUpPlayer(Player player) {
        return !player.isSpectator()
                && !player.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && player.getType().is(TagRegistry.CAN_CONSUME_FIRE_FLOWERS);
    }

    @Override
    protected boolean canPowerUpMob(LivingEntity entity) {
        return !entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && (entity.getType().is(TagRegistry.CAN_CONSUME_FIRE_FLOWERS)
                || ConfigRegistry.FIRE_FLOWER_POWERS_ALL_MOBS.get());
    }

    @Override
    protected String getPowerUpTag() {
        return "marioverse:has_fire_flower";
    }

    @Override
    protected List<String> disablePowerUpTags() {
        return List.of("marioverse:has_ice_flower");
    }

    @Override
    protected TagKey<Item> getPowerUpCostumeTag() {
        return TagRegistry.FIRE_COSTUMES;
    }

    @Override
    protected List<ItemStack> getHatItems() {
        return List.of(ItemRegistry.MARIO_HAT.toStack(), ItemRegistry.LUIGI_HAT.toStack(),
                ItemRegistry.MARIO_FIRE_HAT.toStack(), ItemRegistry.LUIGI_FIRE_HAT.toStack());
    }

    @Override
    protected List<ItemStack> getShirtItems() {
        return List.of(ItemRegistry.MARIO_SHIRT.toStack(), ItemRegistry.LUIGI_SHIRT.toStack(),
                ItemRegistry.MARIO_FIRE_SHIRT.toStack(), ItemRegistry.LUIGI_FIRE_SHIRT.toStack());
    }

    @Override
    protected List<ItemStack> getPantsItems() {
        return List.of(ItemRegistry.MARIO_PANTS.toStack(), ItemRegistry.LUIGI_PANTS.toStack(),
                ItemRegistry.MARIO_FIRE_PANTS.toStack(), ItemRegistry.LUIGI_FIRE_PANTS.toStack());
    }

    @Override
    protected List<ItemStack> getShoesItems() {
        return List.of(ItemRegistry.MARIO_SHOES.toStack(), ItemRegistry.LUIGI_SHOES.toStack(),
                ItemRegistry.MARIO_FIRE_SHOES.toStack(), ItemRegistry.LUIGI_FIRE_SHOES.toStack());
    }

    @Override
    protected void spawnPowerUpParticles(LivingEntity entity, ServerLevel serverWorld) {
        ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.FIRE_POWERED_UP.get(), serverWorld, entity, 10);
    }
}
