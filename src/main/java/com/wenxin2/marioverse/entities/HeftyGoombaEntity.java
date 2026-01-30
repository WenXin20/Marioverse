package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.entities.ai.controls.AmphibiousMoveControl;
import com.wenxin2.marioverse.entities.ai.goals.GoombaRideGoal;
import com.wenxin2.marioverse.entities.ai.goals.GoombaSitGoal;
import com.wenxin2.marioverse.entities.ai.goals.GoombaSleepGoal;
import com.wenxin2.marioverse.entities.ai.goals.LookAtTagGoal;
import com.wenxin2.marioverse.entities.ai.goals.NearestAttackableTagGoal;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;

public class HeftyGoombaEntity extends GoombaEntity implements GeoEntity {
    public HeftyGoombaEntity(EntityType<? extends HeftyGoombaEntity> type, Level world) {
        super(type, world);
        this.setPathfindingMalus(PathType.DOOR_OPEN, 1.0F);
        this.setPathfindingMalus(PathType.WATER, 2.0F);
        this.moveControl = new AmphibiousMoveControl(this, 85, 10, 0.6F, 1.0F, true);
        this.xpReward = 6;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.HEFTY_GOOMBA_HURT.get();
    }

    @NotNull
    @Override
    public SoundEvent getStompSound() {
        return SoundRegistry.HEFTY_GOOMBA_STOMP.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        SoundType soundtype = state.getSoundType(this.level(), pos, this);

        this.playSound(SoundRegistry.HEFTY_GOOMBA_STEP.get(), 1.0F, 1.0F);
        this.playSound(soundtype.getStepSound(), soundtype.getVolume() * 0.15F, soundtype.getPitch());
    }

    @Override
    protected SoundEvent getBumpSound() {
        return SoundRegistry.HEFTY_GOOMBA_BUMP.get();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 0.6D, false));
        this.goalSelector.addGoal(1, new RandomStrollGoal(this, 0.4D));
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new LookAtTagGoal(this, TagRegistry.HEFTY_GOOMBA_CAN_ATTACK, 8.0F, 1.0F));
        this.goalSelector.addGoal(4, new GoombaSitGoal(this, 0.25F, 1200, 3000, 300));
        this.goalSelector.addGoal(5, new GoombaSleepGoal(this, 0.1F, 2400, 6000));
        this.goalSelector.addGoal(6, new GoombaRideGoal(this, 0.01F));
        this.targetSelector.addGoal(0, new NearestAttackableTagGoal(this, TagRegistry.HEFTY_GOOMBA_CAN_ATTACK, true));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
    }

    @Override
    public void remove(Entity.RemovalReason removalReason) {
        if (!this.level().isClientSide && this.isDeadOrDying()) {
            if (this.getLastDamageSource() != null && !this.getLastDamageSource().is(DamageTypeTags.IS_FIRE)) {
                Component component = this.getCustomName();
                boolean flag = this.isNoAi();
                float width = this.getDimensions(this.getPose()).width() / 2.0F;
                int amtSpawned = ConfigRegistry.GOOMBA_SPLIT_COUNT.get() + this.random.nextInt(ConfigRegistry.GOOMBA_SPLIT_RANDOM_COUNT.get());
                var spawnedGoombas = new ArrayList<Mob>();

                for (int i = 0; i <= amtSpawned; i++) {
                    double angle = this.random.nextDouble() * Math.PI * 2;
                    double xOffset = Math.cos(angle) * width;
                    double zOffset = Math.sin(angle) * width;
                    double upwardMotion = 0.3 + this.random.nextDouble() * 0.2;

                    GoombaEntity goomba = EntityRegistry.GOOMBA.get().create(this.level());
                    if (goomba != null) {
                        if (this.isPersistenceRequired())
                            goomba.setPersistenceRequired();

                        goomba.setCustomName(component);
                        goomba.setNoAi(flag);
                        goomba.setInvulnerable(this.isInvulnerable());
                        goomba.moveTo(this.getX() + xOffset, this.getY() + 0.5, this.getZ() + zOffset, this.random.nextFloat() * 360.0F, 0.0F);
                        goomba.setDeltaMovement(xOffset * 0.5, upwardMotion, zOffset * 0.5);
                        goomba.move(MoverType.SELF, goomba.getDeltaMovement());

                        this.copyAttributeWithModifiers(goomba, Attributes.MAX_HEALTH);
                        this.copyAttributeWithModifiers(goomba, Attributes.SAFE_FALL_DISTANCE);
                        this.copyAttributeWithModifiers(goomba, Attributes.SCALE);
                        this.copyAttributeWithModifiers(goomba, AttributesRegistry.EYE_HEIGHT_SCALE);
                        this.copyAttributeWithModifiers(goomba, AttributesRegistry.HEIGHT_SCALE);
                        this.copyAttributeWithModifiers(goomba, AttributesRegistry.WIDTH_SCALE);

                        if (this instanceof AbilitiesHandler handler && goomba instanceof AbilitiesHandler entityHandler) {
                            entityHandler.mv$setSuperMushroom(handler.mv$hasSuperMushroom());
                            goomba.setData(DataAttachmentRegistry.HAS_FIRE_FLOWER, this.getData(DataAttachmentRegistry.HAS_FIRE_FLOWER));
                            goomba.setData(DataAttachmentRegistry.HAS_ICE_FLOWER, this.getData(DataAttachmentRegistry.HAS_ICE_FLOWER));
                            goomba.setData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM, this.getData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM));
                            goomba.setData(DataAttachmentRegistry.HAS_MINI_MUSHROOM, this.getData(DataAttachmentRegistry.HAS_MINI_MUSHROOM));
                            goomba.setData(DataAttachmentRegistry.HAS_SUPER_STAR, this.getData(DataAttachmentRegistry.HAS_SUPER_STAR));
                            goomba.setData(DataAttachmentRegistry.SUPER_STAR_COOLDOWN, this.getData(DataAttachmentRegistry.SUPER_STAR_COOLDOWN));
                        }

                        spawnedGoombas.add(goomba);
                    }
                }

                if (!EventHooks.onMobSplit(this, spawnedGoombas).isCanceled())
                    spawnedGoombas.forEach(this.level()::addFreshEntity);
            }
        }
        super.remove(removalReason);
    }

    public void copyAttributeWithModifiers(LivingEntity entity, Holder<Attribute> attribute) {
        AttributeInstance originalAttribute = this.getAttribute(attribute);
        AttributeInstance newAttribute = entity.getAttribute(attribute);

        if (originalAttribute != null && newAttribute != null) {
            newAttribute.setBaseValue(originalAttribute.getBaseValue());
            for (AttributeModifier modifier : originalAttribute.getModifiers())
                newAttribute.addPermanentModifier(modifier);
        }
    }
}
