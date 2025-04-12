package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.entities.ai.controls.AmphibiousMoveControl;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.data.SlotTypeLoader;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;

public class GreenKoopaTroopaEntity extends KoopaTroopaEntity implements NeutralMob {
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    private int remainingPersistentAngerTime;
    @Nullable private UUID persistentAngerTarget;

    public GreenKoopaTroopaEntity(EntityType<? extends GreenKoopaTroopaEntity> type, Level world) {
        super(type, world);
        this.setPathfindingMalus(PathType.DOOR_OPEN, 1.0F);
        this.setPathfindingMalus(PathType.WATER, 2.0F);
        this.moveControl = new AmphibiousMoveControl(this, 85, 10, 0.6F, 1.0F, true);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(2, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.addPersistentAngerSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.readPersistentAngerSaveData(this.level(), tag);
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.remainingPersistentAngerTime;
    }

    @Override
    public void setRemainingPersistentAngerTime(int angerTime) {
        this.remainingPersistentAngerTime = angerTime;
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID angerTarget) {
        this.persistentAngerTarget = angerTarget;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    @Override
    public void spawnKoopaShell() {
        if (hideAnimationTicks == 0) {
            KoopaShellEntity entity = new KoopaShellEntity(EntityRegistry.GREEN_KOOPA_SHELL.get(), this.level());

            entity.setHideTicks(80);
            entity.setPos(this.getX(), this.getY(), this.getZ());
            entity.setYRot(this.getYRot());
            entity.setXRot(this.getXRot());
            entity.yBodyRot = this.yBodyRot;
            entity.setYHeadRot(this.getYHeadRot());
            entity.setHealth(this.getHealth());

            entity.getPersistentData().putBoolean("marioverse:has_fire_flower",
                    this.getPersistentData().getBoolean("marioverse:has_fire_flower"));
            entity.getPersistentData().putBoolean("marioverse:has_ice_flower",
                    this.getPersistentData().getBoolean("marioverse:has_ice_flower"));
            entity.getPersistentData().putBoolean("marioverse:has_mushroom",
                    this.getPersistentData().getBoolean("marioverse:has_mushroom"));
            entity.getPersistentData().putBoolean("marioverse:has_mega_mushroom",
                    this.getPersistentData().getBoolean("marioverse:has_mega_mushroom"));
            entity.getPersistentData().putBoolean("marioverse:has_super_star",
                    this.getPersistentData().getBoolean("marioverse:has_super_star"));
            entity.getPersistentData().putInt("marioverse:super_star_cooldown",
                    this.getPersistentData().getInt("marioverse:super_star_cooldown"));

            this.copyAttributeWithModifiers(entity, Attributes.SAFE_FALL_DISTANCE);
            this.copyAttributeWithModifiers(entity, Attributes.SCALE);
            this.copyAttributeWithModifiers(entity, AttributesRegistry.HEIGHT_SCALE);
            this.copyAttributeWithModifiers(entity, AttributesRegistry.WIDTH_SCALE);

            for (EquipmentSlot slot : EquipmentSlot.values())
                entity.setItemSlot(slot, this.getItemBySlot(slot).copy());

            AccessoriesCapability capability = AccessoriesCapability.get(this);
            if (capability != null && ConfigRegistry.EQUIP_COSTUMES_MOBS.get()
                    && !this.getType().is(TagRegistry.CANNOT_LOSE_POWER_UP)) {
                String[] slotTypes = {"costume_hat", "costume_shirt", "costume_pants", "costume_shoes"};
                for (String slotType : slotTypes) {
                    AccessoriesContainer container = capability.getContainer(SlotTypeLoader.getSlotType(this, slotType));
                    AccessoriesContainer containerEntity = capability.getContainer(SlotTypeLoader.getSlotType(entity, slotType));
                    if (container != null) {
                        ItemStack stack = container.getAccessories().getItem(0);
                        if (containerEntity != null)
                            containerEntity.getAccessories().setItem(0, stack);
                    }
                }
            }

            this.level().addFreshEntity(entity);
            this.remove(RemovalReason.DISCARDED);
            hideAnimationTicks = -1;
        }
    }
}
