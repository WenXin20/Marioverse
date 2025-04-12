package com.wenxin2.marioverse.entities.power_ups;

import com.wenxin2.marioverse.entities.GoombaEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class AbstractPowerUpEntity extends BasePowerUpEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public AbstractPowerUpEntity(EntityType<? extends Mob> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void handleCollision(Entity entity) {
        if (entity instanceof Player player && this.canPowerUpPlayer(player))
            this.applyPowerUp(player);
        else if (entity instanceof LivingEntity livingEntity && this.canPowerUpMob(livingEntity))
            this.applyPowerUp(livingEntity);
    }

    protected abstract boolean canPowerUpPlayer(Player player);

    protected abstract boolean canPowerUpMob(LivingEntity entity);

    protected abstract String getPowerUpTag();

    protected abstract List<String> disablePowerUpTags();

    protected abstract TagKey<Item> getPowerUpCostumeTag();

    protected abstract List<ItemStack> getHatItems();

    protected abstract List<ItemStack> getShirtItems();

    protected abstract List<ItemStack> getPantsItems();

    protected abstract List<ItemStack> getShoesItems();

    protected abstract void spawnPowerUpParticles(LivingEntity entity, ServerLevel serverWorld);

    private void applyPowerUp(LivingEntity entity) {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);

        if (entity.getPersistentData().getBoolean(getPowerUpTag()))
            this.level().broadcastEntityEvent(this, (byte) 20); // Poof particle
        if (this.level() instanceof ServerLevel serverWorld
                && !entity.getPersistentData().getBoolean(getPowerUpTag()))
            spawnPowerUpParticles(entity, serverWorld);

        if (entity.getHealth() < entity.getMaxHealth())
            entity.heal(ConfigRegistry.MUSHROOM_HEALTH_HEALED.get().floatValue());

        entity.getPersistentData().putBoolean("marioverse:has_mushroom", Boolean.TRUE);
        entity.getPersistentData().putBoolean(getPowerUpTag(), Boolean.TRUE);
        for (String tag : disablePowerUpTags())
            entity.getPersistentData().putBoolean(tag, Boolean.FALSE);

        this.level().playSound(null, this.blockPosition(), SoundRegistry.PLAYER_POWERS_UP.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        this.remove(RemovalReason.KILLED);

        this.applyCostumeChange(entity, capability);

    }

    private void applyCostumeChange(LivingEntity entity, AccessoriesCapability capability) {
        if (capability != null && ConfigRegistry.EQUIP_COSTUMES_PLAYERS.get())
            this.updateCostume(entity, capability);
    }

    private void updateCostume(LivingEntity entity, AccessoriesCapability capability) {
        AccessoriesContainer containerHat = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_hat"));
        AccessoriesContainer containerShirt = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shirt"));
        AccessoriesContainer containerPants = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_pants"));
        AccessoriesContainer containerShoes = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shoes"));

        int randomIndex = (int) (Math.random() * this.getHatItems().size());

        if (entity instanceof GoombaEntity goomba
                && (goomba.getItemBySlot(EquipmentSlot.HEAD).isEmpty()
                    || goomba.getItemBySlot(EquipmentSlot.HEAD).is(this.getPowerUpCostumeTag()))) {
            ItemStack stack = goomba.getItemBySlot(EquipmentSlot.HEAD).getItem().getDefaultInstance();
            ItemStack newStack = goomba.getItemBySlot(EquipmentSlot.HEAD).isEmpty()
                    ? this.getHatItems().get((int) (Math.random() * this.getHatItems().size()))
                    : this.getHatItems().getFirst();

            for (ItemStack item : this.getHatItems()) {
                if (stack.is(TagRegistry.MARIO_COSTUMES) && item.is(TagRegistry.MARIO_POWER_UP_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                } else if (stack.is(TagRegistry.LUIGI_COSTUMES) && item.is(TagRegistry.LUIGI_POWER_UP_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                } else if (stack.is(TagRegistry.PEACH_COSTUMES) && item.is(TagRegistry.PEACH_POWER_UP_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                }
            }

            newStack.applyComponents(stack.getComponents());
            goomba.setItemSlot(EquipmentSlot.HEAD, newStack);
        }

        if (containerHat != null && !containerHat.getAccessories().getItem(0).is(this.getPowerUpCostumeTag())) {
            ItemStack stack = containerHat.getAccessories().getItem(0);
            ItemStack stackArmor = entity.getItemBySlot(EquipmentSlot.HEAD).getItem().getDefaultInstance();
            ItemStack newStack = !(entity instanceof Player)
                    ? this.getHatItems().get(randomIndex)
                    : stack;

            for (ItemStack item : this.getHatItems()) {
                if (stackArmor.is(TagRegistry.MARIO_COSTUMES) && item.is(TagRegistry.MARIO_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                } else if (stackArmor.is(TagRegistry.LUIGI_COSTUMES) && item.is(TagRegistry.LUIGI_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                } else if (stackArmor.is(TagRegistry.PEACH_COSTUMES) && item.is(TagRegistry.PEACH_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                } else if (stack.is(TagRegistry.MARIO_COSTUMES) && item.is(TagRegistry.MARIO_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                } else if (stack.is(TagRegistry.LUIGI_COSTUMES) && item.is(TagRegistry.LUIGI_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                } else if (stack.is(TagRegistry.PEACH_COSTUMES) && item.is(TagRegistry.PEACH_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                }
            }

            newStack.applyComponents(stack.getComponents());
            containerHat.getAccessories().setItem(0, newStack);
        }

        if (containerShirt != null && !containerShirt.getAccessories().getItem(0).is(this.getPowerUpCostumeTag())) {
            ItemStack stack = containerShirt.getAccessories().getItem(0);
            ItemStack stackArmor = entity.getItemBySlot(EquipmentSlot.BODY).getItem().getDefaultInstance();
            ItemStack newStack = !(entity instanceof Player)
                    ? this.getShirtItems().get(randomIndex)
                    : stack;

            for (ItemStack item : this.getShirtItems()) {
                if (stackArmor.is(TagRegistry.MARIO_COSTUMES) && item.is(TagRegistry.MARIO_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                } else if (stackArmor.is(TagRegistry.LUIGI_COSTUMES) && item.is(TagRegistry.LUIGI_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                } else if (stackArmor.is(TagRegistry.PEACH_COSTUMES) && item.is(TagRegistry.PEACH_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                } else if (stack.is(TagRegistry.MARIO_COSTUMES) && item.is(TagRegistry.MARIO_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                } else if (stack.is(TagRegistry.LUIGI_COSTUMES) && item.is(TagRegistry.LUIGI_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                } else if (stack.is(TagRegistry.PEACH_COSTUMES) && item.is(TagRegistry.PEACH_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                }
            }

            newStack.applyComponents(stack.getComponents());
            containerShirt.getAccessories().setItem(0, newStack);
        }

        if (containerPants != null && !containerPants.getAccessories().getItem(0).is(this.getPowerUpCostumeTag())) {
            ItemStack stack = containerPants.getAccessories().getItem(0);
            ItemStack stackArmor = entity.getItemBySlot(EquipmentSlot.LEGS).getItem().getDefaultInstance();
            ItemStack newStack = !(entity instanceof Player)
                    ? this.getPantsItems().get(randomIndex)
                    : stack;

            for (ItemStack item : this.getPantsItems()) {
                if (stackArmor.is(TagRegistry.MARIO_COSTUMES) && item.is(TagRegistry.MARIO_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                } else if (stackArmor.is(TagRegistry.LUIGI_COSTUMES) && item.is(TagRegistry.LUIGI_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                } else if (stackArmor.is(TagRegistry.PEACH_COSTUMES) && item.is(TagRegistry.PEACH_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                } else if (stack.is(TagRegistry.MARIO_COSTUMES) && item.is(TagRegistry.MARIO_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                } else if (stack.is(TagRegistry.LUIGI_COSTUMES) && item.is(TagRegistry.LUIGI_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                } else if (stack.is(TagRegistry.PEACH_COSTUMES) && item.is(TagRegistry.PEACH_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                }
            }

            newStack.applyComponents(stack.getComponents());
            containerPants.getAccessories().setItem(0, newStack);
        }

        if (containerShoes != null && !containerShoes.getAccessories().getItem(0).is(this.getPowerUpCostumeTag())) {
            ItemStack stack = containerShoes.getAccessories().getItem(0);
            ItemStack stackArmor = entity.getItemBySlot(EquipmentSlot.FEET).getItem().getDefaultInstance();
            ItemStack newStack = !(entity instanceof Player)
                    ? this.getShoesItems().get(randomIndex)
                    : stack;

            for (ItemStack item : this.getShoesItems()) {
                if (stackArmor.is(TagRegistry.MARIO_COSTUMES) && item.is(TagRegistry.MARIO_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                } else if (stackArmor.is(TagRegistry.LUIGI_COSTUMES) && item.is(TagRegistry.LUIGI_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                } else if (stackArmor.is(TagRegistry.PEACH_COSTUMES) && item.is(TagRegistry.PEACH_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                } else if (stack.is(TagRegistry.MARIO_COSTUMES) && item.is(TagRegistry.MARIO_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                } else if (stack.is(TagRegistry.LUIGI_COSTUMES) && item.is(TagRegistry.LUIGI_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                } else if (stack.is(TagRegistry.PEACH_COSTUMES) && item.is(TagRegistry.PEACH_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                }
            }

            newStack.applyComponents(stack.getComponents());
            containerShoes.getAccessories().setItem(0, newStack);
        }
    }
}
