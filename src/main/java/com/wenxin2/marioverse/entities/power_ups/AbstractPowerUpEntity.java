package com.wenxin2.marioverse.entities.power_ups;

import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.data.SlotTypeLoader;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
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
    public void collideWithEntity(Entity entity) {
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

    public void applyPowerUp(LivingEntity entity) {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);

        if (this.level() instanceof ServerLevel serverWorld)
                this.spawnPowerUpParticles(entity, serverWorld);

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
        if (capability != null) {
            if (entity instanceof Player && ConfigRegistry.EQUIP_COSTUMES_PLAYERS.get())
                this.updateCostume(entity, capability);
            else if (!(entity instanceof Player) && ConfigRegistry.EQUIP_COSTUMES_MOBS.get())
                this.updateCostume(entity, capability);
        }
    }

    private void updateCostume(LivingEntity entity, AccessoriesCapability capability) {
        AccessoriesContainer containerHat = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_hat"));
        AccessoriesContainer containerShirt = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shirt"));
        AccessoriesContainer containerPants = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_pants"));
        AccessoriesContainer containerShoes = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shoes"));

        int randomIndex = (int) (Math.random() * this.getHatItems().size());

        if (entity.getType().is(TagRegistry.EQUIP_COSTUMES_IN_ARMOR_SLOTS)) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (!slot.isArmor()) continue;
                ItemStack currentStack = entity.getItemBySlot(slot);

                switch (slot) {
                    case HEAD -> {
                        ItemStack stackArmor = entity.getItemBySlot(EquipmentSlot.HEAD);
                        ItemStack newStack = this.getHatItems().get(randomIndex);
                        if (stackArmor.isEmpty() || stackArmor.is(TagRegistry.COSTUMES))
                            this.equipCostumesInArmorSlots(entity, slot, stackArmor, this.getHatItems(), newStack, currentStack);
                    }
                    case CHEST -> {
                        ItemStack stackArmor = entity.getItemBySlot(EquipmentSlot.CHEST);
                        ItemStack newStack = this.getShirtItems().get(randomIndex);
                        if (stackArmor.isEmpty() || stackArmor.is(TagRegistry.COSTUMES))
                            this.equipCostumesInArmorSlots(entity, slot, stackArmor, this.getShirtItems(), newStack, currentStack);
                    }
                    case LEGS -> {
                        ItemStack stackArmor = entity.getItemBySlot(EquipmentSlot.LEGS);
                        ItemStack newStack = this.getPantsItems().get(randomIndex);
                        if (stackArmor.isEmpty() || stackArmor.is(TagRegistry.COSTUMES))
                            this.equipCostumesInArmorSlots(entity, slot, stackArmor, this.getPantsItems(), newStack, currentStack);
                    }
                    case FEET -> {
                        ItemStack stackArmor = entity.getItemBySlot(EquipmentSlot.FEET);
                        ItemStack newStack = this.getShoesItems().get(randomIndex);
                        if (stackArmor.isEmpty() || stackArmor.is(TagRegistry.COSTUMES))
                            this.equipCostumesInArmorSlots(entity, slot, stackArmor, this.getShoesItems(), newStack, currentStack);
                    }
                }
            }
        }

        if (containerHat != null && !containerHat.getAccessories().getItem(0).is(this.getPowerUpCostumeTag())) {
            ItemStack stack = containerHat.getAccessories().getItem(0);
            ItemStack stackArmor = entity.getItemBySlot(EquipmentSlot.HEAD);
            ItemStack newStack = !(entity instanceof Player)
                    ? this.getHatItems().get(randomIndex) : stack;

            for (ItemStack item : this.getHatItems())
                newStack = this.equipCostumesInAccessorySlots(entity, item, stackArmor, newStack, stack);

            newStack.applyComponents(stack.getComponents());
            containerHat.getAccessories().setItem(0, newStack);
        }

        if (containerShirt != null && !containerShirt.getAccessories().getItem(0).is(this.getPowerUpCostumeTag())) {
            ItemStack stack = containerShirt.getAccessories().getItem(0);
            ItemStack stackArmor = entity.getItemBySlot(EquipmentSlot.BODY);
            ItemStack newStack = !(entity instanceof Player)
                    ? this.getShirtItems().get(randomIndex)
                    : stack;

            for (ItemStack item : this.getShirtItems())
                newStack = this.equipCostumesInAccessorySlots(entity, item, stackArmor, newStack, stack);

            newStack.applyComponents(stack.getComponents());
            containerShirt.getAccessories().setItem(0, newStack);
        }

        if (containerPants != null && !containerPants.getAccessories().getItem(0).is(this.getPowerUpCostumeTag())) {
            ItemStack stack = containerPants.getAccessories().getItem(0);
            ItemStack stackArmor = entity.getItemBySlot(EquipmentSlot.LEGS);
            ItemStack newStack = !(entity instanceof Player)
                    ? this.getPantsItems().get(randomIndex)
                    : stack;

            for (ItemStack item : this.getPantsItems())
                newStack = this.equipCostumesInAccessorySlots(entity, item, stackArmor, newStack, stack);

            newStack.applyComponents(stack.getComponents());
            containerPants.getAccessories().setItem(0, newStack);
        }

        if (containerShoes != null && !containerShoes.getAccessories().getItem(0).is(this.getPowerUpCostumeTag())) {
            ItemStack stack = containerShoes.getAccessories().getItem(0);
            ItemStack stackArmor = entity.getItemBySlot(EquipmentSlot.FEET);
            ItemStack newStack = !(entity instanceof Player)
                    ? this.getShoesItems().get(randomIndex)
                    : stack;

            for (ItemStack item : this.getShoesItems())
                newStack = this.equipCostumesInAccessorySlots(entity, item, stackArmor, newStack, stack);

            newStack.applyComponents(stack.getComponents());
            containerShoes.getAccessories().setItem(0, newStack);
        }
    }

    private ItemStack equipCostumesInAccessorySlots(LivingEntity entity, ItemStack item, ItemStack stackArmor, ItemStack newStack, ItemStack stack) {
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
        entity.level().playSound(entity, entity.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.NEUTRAL, 1.0F, 1.0F);
        return newStack;
    }

    private void equipCostumesInArmorSlots(LivingEntity entity, EquipmentSlot slot, ItemStack stackArmor, List<ItemStack> costumeList, ItemStack newStack, ItemStack currentStack) {
        if (stackArmor.isEmpty() || stackArmor.is(TagRegistry.COSTUMES)) {
            for (ItemStack item : costumeList) {
                if (stackArmor.is(TagRegistry.MARIO_COSTUMES) && item.is(TagRegistry.MARIO_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                } else if (stackArmor.is(TagRegistry.LUIGI_COSTUMES) && item.is(TagRegistry.LUIGI_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                } else if (stackArmor.is(TagRegistry.PEACH_COSTUMES) && item.is(TagRegistry.PEACH_COSTUMES)) {
                    if (item.is(this.getPowerUpCostumeTag()))
                        newStack = item.copy();
                }
            }

            newStack.applyComponents(currentStack.getComponents());
            entity.setItemSlot(slot, newStack);
            entity.level().playSound(entity, entity.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.NEUTRAL, 1.0F, 1.0F);
        }
    }
}
