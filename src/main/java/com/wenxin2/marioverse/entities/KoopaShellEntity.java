package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

public class KoopaShellEntity extends ShellEntity {
    private static final EntityDataAccessor<Byte> DATA_IS_HIDING = SynchedEntityData.defineId(KoopaShellEntity.class, EntityDataSerializers.BYTE);
    public static final RawAnimation EMERGE = RawAnimation.begin().thenPlayAndHold("move.emerge");

    private int hideTicks = -1;
    public int emergeAnimationTicks = -1;

    public KoopaShellEntity(EntityType<? extends KoopaShellEntity> type, Level world) {
        super(type, world);
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(0, new HurtByTargetGoal(this).setAlertOthers(KoopaTroopaEntity.class));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);
        controllers.add(new AnimationController<>(this, "emerge_controller", 5, state -> PlayState.CONTINUE)
                .triggerableAnim("emerge", EMERGE));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_IS_HIDING, (byte) 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("HideFlags", this.entityData.get(DATA_IS_HIDING));
        tag.putInt("HideTicks", this.hideTicks);
        tag.putInt("EmergeTicks", this.emergeAnimationTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(DATA_IS_HIDING, tag.getByte("HideFlags"));
        this.hideTicks = tag.getInt("HideTicks");
        this.emergeAnimationTicks = tag.getInt("EmergeTicks");
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 motion = this.getDeltaMovement();

        if (this.isRemoved())
            return;

        if (hideTicks > 0 && motion.horizontalDistance() == 0)
            hideTicks--;

        if (emergeAnimationTicks > 0)
            emergeAnimationTicks--;

        if (!this.level().isClientSide && emergeAnimationTicks == 1)
            this.spawnKoopaTroopa();

        if (hideTicks == 0 && emergeAnimationTicks == 0 && !this.isHeld() && !this.isMovingShell()
                && this.getDeltaMovement().horizontalDistance() == 0 && (this.onGround() || this.isInWaterOrBubble())) {
            this.triggerAnim("emerge_controller", "emerge");
            this.emergeAnimationTicks = 60;
        }
    }

    public static boolean checkKoopaSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor serverWorld,
                                               MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return (MobSpawnType.ignoresLightRequirements(spawnType) || isDarkEnoughToSpawn(serverWorld, pos, random))
                && checkMobSpawnRules(entityType, serverWorld, spawnType, pos, random);
    }

    @Override
    public boolean isEmptyShell() {
        return this.hideTicks < 0;
    }

    @Override
    public boolean canStayHeld() {
        return this.hideTicks != 0;
    }

    @Override
    protected void onStartMoving() {
        if (this.isEmptyShell())
            return;

        if (this.emergeAnimationTicks > 0)
            this.stopTriggeredAnim("emerge_controller", "emerge");

        this.hideTicks = this.getHideDuration();
        this.emergeAnimationTicks = 0;
    }

    @Override
    protected void onPlaced() {
        if (this.isEmptyShell())
            return;

        this.hideTicks = this.getHideDuration();
        this.emergeAnimationTicks = 0;
    }

    @NotNull
    @Override
    public SimpleParticleType getShatterParticle() {
        return ParticleRegistry.GREEN_KOOPA_SHELL_SHATTER.get();
    }

    @Override
    public TagKey<EntityType<?>> getInstaKillEntityTag() {
        return TagRegistry.GREEN_KOOPA_SHELL_CAN_INSTAKILL;
    }

    @Override
    public float getShellDamage() {
        return ConfigRegistry.GREEN_KOOPA_SHELL_DAMAGE.get().floatValue();
    }

    @NotNull
    public KoopaTroopaEntity getKoopaTroopaEntity() {
        return new KoopaTroopaEntity(EntityRegistry.GREEN_KOOPA_TROOPA.get(), this.level());
    }

    public int getHideDuration() {
        return ConfigRegistry.GREEN_KOOPA_TROOPA_HIDE_DURATION.get();
    }

    public void setHideTicks(int hideTicks) {
        this.hideTicks = hideTicks;
    }

    private void spawnKoopaTroopa() {
        KoopaTroopaEntity troopa = this.getKoopaTroopaEntity();

        troopa.setBounceCount(this.getBounceCount());
        troopa.setPos(this.getX(), this.getY(), this.getZ());
        troopa.setYRot(this.getYRot());
        troopa.setXRot(this.getXRot());
        troopa.yBodyRot = this.yBodyRot;
        troopa.setYHeadRot(this.getYHeadRot());
        troopa.setHealth(this.getHealth());
        troopa.setNoAi(this.isNoAi());

        troopa.setData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM, this.getData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM));
        troopa.setData(DataAttachmentRegistry.HAS_FIRE_FLOWER, this.getData(DataAttachmentRegistry.HAS_FIRE_FLOWER));
        troopa.setData(DataAttachmentRegistry.HAS_ICE_FLOWER, this.getData(DataAttachmentRegistry.HAS_ICE_FLOWER));
        troopa.setData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM, this.getData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM));
        troopa.setData(DataAttachmentRegistry.HAS_MINI_MUSHROOM, this.getData(DataAttachmentRegistry.HAS_MINI_MUSHROOM));
        troopa.setData(DataAttachmentRegistry.HAS_SUPER_STAR, this.getData(DataAttachmentRegistry.HAS_SUPER_STAR));
        troopa.setData(DataAttachmentRegistry.SUPER_STAR_DURATION, this.getData(DataAttachmentRegistry.SUPER_STAR_DURATION));

        this.copyAttributeWithModifiers(troopa, Attributes.MAX_HEALTH);
        this.copyAttributeWithModifiers(troopa, Attributes.SAFE_FALL_DISTANCE);
        this.copyAttributeWithModifiers(troopa, Attributes.SCALE);
        this.copyAttributeWithModifiers(troopa, AttributesRegistry.EYE_HEIGHT_SCALE);
        this.copyAttributeWithModifiers(troopa, AttributesRegistry.HEIGHT_SCALE);
        this.copyAttributeWithModifiers(troopa, AttributesRegistry.WIDTH_SCALE);

        for (EquipmentSlot slot : EquipmentSlot.values())
            troopa.setItemSlot(slot, this.getItemBySlot(slot).copy());

        Optional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(this);
        Optional<ICuriosItemHandler> curiosInventoryTroopa = CuriosApi.getCuriosInventory(troopa);
        if (curiosInventory.isPresent() && curiosInventoryTroopa.isPresent() && ConfigRegistry.EQUIP_COSTUMES_MOBS.get()
                && !this.getType().is(TagRegistry.CANNOT_LOSE_POWER_UP)) {
            Map<String, ICurioStacksHandler> curios = curiosInventory.get().getCurios();
            Map<String, ICurioStacksHandler> curiosTroopa = curiosInventoryTroopa.get().getCurios();

            String[] slotTypes = {"costume_hat", "costume_shirt", "costume_pants", "costume_shoes"};
            for (String slotType : slotTypes) {
                ICurioStacksHandler slotHandler = curios.get(slotType);
                ICurioStacksHandler slotHandlerTroopa = curiosTroopa.get(slotType);
                if (slotHandler != null) {
                    ItemStack stack = slotHandler.getStacks().getStackInSlot(0);
                    if (slotHandlerTroopa != null)
                        slotHandlerTroopa.getStacks().setStackInSlot(0, stack.copy());
                }
            }
        }

        this.level().addFreshEntity(troopa);

        LivingEntity angerTarget = this.getAngerTarget();
        if (angerTarget != null && angerTarget.isAlive() && troopa.canAttack(angerTarget)) {
            troopa.setLastHurtByMob(angerTarget);
            troopa.setTarget(angerTarget);
        }

        this.discard();
    }

    private void copyAttributeWithModifiers(LivingEntity entity, Holder<Attribute> attribute) {
        AttributeInstance originalAttribute = this.getAttribute(attribute);
        AttributeInstance newAttribute = entity.getAttribute(attribute);

        if (originalAttribute != null && newAttribute != null) {
            newAttribute.setBaseValue(originalAttribute.getBaseValue());
            for (AttributeModifier modifier : originalAttribute.getModifiers())
                newAttribute.addPermanentModifier(modifier);
        }
    }
}
