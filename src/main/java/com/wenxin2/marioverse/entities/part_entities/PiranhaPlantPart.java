package com.wenxin2.marioverse.entities.part_entities;

import com.wenxin2.marioverse.entities.PiranhaPlantEntity;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.jetbrains.annotations.NotNull;

public class PiranhaPlantPart extends PartEntity<PiranhaPlantEntity> implements Leashable {
    public final PiranhaPlantEntity parentMob;
    public final String name;
    private EntityDimensions size;
    private Leashable.LeashData leashData;

    public PiranhaPlantPart(PiranhaPlantEntity parentMob, String name, float width, float height) {
        super(parentMob);
        this.size = EntityDimensions.scalable(width, height);
        this.parentMob = parentMob;
        this.name = name;
        this.refreshDimensions();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        this.leashData = this.readLeashData(tag);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        this.writeLeashData(tag, this.leashData);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        return this.parentMob.getPickResult();
    }

    @Override
    public void tick() {
        super.tick();
        this.biteEntity();
    }

    @NotNull
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
        return new ClientboundAddEntityPacket(this, entity);
    }

    @NotNull
    @Override
    public AABB makeBoundingBox() {
        return super.makeBoundingBox();
    }

    @NotNull
    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return this.size;
    }

    @Override
    public boolean hurt(DamageSource source, float damageAmount) {
        if (this.level().isClientSide && source.getDirectEntity() instanceof Player player)
            this.level().sendPacketToServer(ServerboundInteractPacket.createAttackPacket(this.getParent(), player.isShiftKeyDown()));
        return !this.isInvulnerableTo(source) && this.getParent().hurt(source, damageAmount);
    }

    @Override
    public boolean canBeHitByProjectile() {
        return this.isAlive();
    }

    @Override
    public void push(final Entity entity) {
        if (!this.getParent().isSleeping() && !this.getParent().isHiding())
            super.push(entity);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @NotNull
    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (this.level().isClientSide)
            this.level().sendPacketToServer(ServerboundInteractPacket.createInteractionPacket(this.getParent(), player.isShiftKeyDown(), hand));
        return super.interact(player, hand);
    }

    @NotNull
    @Override
    public InteractionResult interactAt(Player player, Vec3 vec3, InteractionHand hand) {
        if (this.level().isClientSide)
            this.level().sendPacketToServer(ServerboundInteractPacket.createInteractionPacket(this.getParent(), player.isShiftKeyDown(), hand, vec3));
        return super.interactAt(player, vec3, hand);
    }

    @Override
    public boolean canRiderInteract() {
        return this.getParent().canRiderInteract();
    }

    @Nullable
    @Override
    public LeashData getLeashData() {
        return this.leashData;
    }

    @Override
    public void setLeashData(@Nullable Leashable.LeashData leashData) {
        this.leashData = leashData;
    }

    @Override
    public boolean canBeLeashed() {
        return true;
    }

    @Override
    public boolean canCollideWith(final Entity entity) {
        return !this.isPassengerOfSameVehicle(entity);
    }

    public void biteEntity() {
        if (this.getParent().attackCooldown > 0)
            return;

        List<Entity> nearbyEntities = this.level().getEntities(this,
                this.getBoundingBox().inflate(0.01D), entity -> !entity.isSpectator()
                        && entity instanceof LivingEntity && !(entity instanceof PiranhaPlantEntity)
                        && !this.level().isClientSide());
        float pitch = 0.9F + this.level().random.nextFloat() * 0.2F;

        if (!nearbyEntities.isEmpty() && !this.getParent().isHiding()) {
            for (Entity collidingEntity : nearbyEntities) {
                if (collidingEntity instanceof PiranhaPlantEntity
                        || (this.getParent().getOwner() != null && this.getParent().getOwner().getUUID().equals(collidingEntity.getUUID())))
                    continue;

                if ((this.getParent().getOwner() != null && !((collidingEntity instanceof Monster)
                        || collidingEntity.getType().is(TagRegistry.PIRANHA_PLANT_CAN_ATTACK) || (this.getParent().isBaby() && collidingEntity instanceof Animal)))
                        || (this.getParent().getOwner() == null && !collidingEntity.getType().is(TagRegistry.PIRANHA_PLANT_CAN_ATTACK)))
                    continue;

                if (this.getParent().getOwner() != null && collidingEntity.getTeam() != null && this.getParent().getOwner().getTeam() != null
                        && collidingEntity.getTeam() == this.getParent().getOwner().getTeam())
                    continue;

                this.getParent().swing(InteractionHand.MAIN_HAND);

                float attackDamage = this.getParent().isBaby() ? (float) this.getParent().getAttributeValue(Attributes.ATTACK_DAMAGE) / 2
                        : (float) this.getParent().getAttributeValue(Attributes.ATTACK_DAMAGE);

                if (this.getParent().getOwner() != null)
                    collidingEntity.hurt(DamageSourceRegistry.piranhaChomp(collidingEntity, this.getParent().getOwner()), attackDamage);
                else collidingEntity.hurt(DamageSourceRegistry.piranhaChomp(null, this), attackDamage);

                if (collidingEntity instanceof NeutralMob neutralMob) {
                    neutralMob.isAngryAt(this.getParent());
                    neutralMob.setTarget(this.getParent());
                    neutralMob.setPersistentAngerTarget(this.getParent().getUUID());
                }

                int age = this.getParent().getAge();
                if (this.getParent().isBaby()) {
                    this.getParent().ageUp(PiranhaPlantEntity.getSpeedUpSecondsWhenFeeding(-age), 20, true);
                    if (this.getParent().level() instanceof ServerLevel serverWorld)
                        ServerParticleUtils.spawnParticlesOnEntityRandomly(ParticleTypes.HAPPY_VILLAGER, serverWorld, this, 0.5, 5);
                }

                this.playSound(SoundRegistry.PIRANHA_PLANT_CHOMP.get(), 1.0F, pitch);
                this.getParent().attackCooldown = 20;
                break;
            }
        }
    }
}
