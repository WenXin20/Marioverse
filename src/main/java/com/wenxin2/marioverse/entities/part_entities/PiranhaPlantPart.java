package com.wenxin2.marioverse.entities.part_entities;

import com.wenxin2.marioverse.entities.PiranhaPlantEntity;
import com.wenxin2.marioverse.init.DamageTypeRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
        this.checkForCollisions();
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
        if (!this.getParent().isSleeping())
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

    public void checkForCollisions() {
        List<Entity> nearbyEntities = this.level().getEntities(this,
                this.getBoundingBox().inflate(0.01D), entity -> !entity.isSpectator()
                        && entity instanceof LivingEntity && !(entity instanceof PiranhaPlantEntity));

        if (!nearbyEntities.isEmpty() && !this.getParent().isHiding()) {
            for (Entity collidingEntity : nearbyEntities) {
                if (collidingEntity instanceof PiranhaPlantEntity
                        || !(collidingEntity.getType().is(TagRegistry.PIRANHA_PLANT_CAN_ATTACK)))
                    return;

                this.getParent().swing(InteractionHand.MAIN_HAND);
                collidingEntity.hurt(DamageTypeRegistry.piranhaChomp(collidingEntity, this.getParent()), (float) this.getParent().getAttributeValue(Attributes.ATTACK_DAMAGE));
                break;
            }
        }
    }
}
