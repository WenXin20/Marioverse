package com.wenxin2.marioverse.entities.ai.controls;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.monster.Slime;

public class BounceMoveControl extends MoveControl {
    private float yRot;
    private float soundPitch;
    private float soundVolume;
    private int jumpDelay;
    private int mobJumpDelay;
    private final Mob entity;
    private boolean isAggressive;
    private final SoundEvent jumpSound;
    protected final RandomSource random = RandomSource.create();

    public BounceMoveControl(Mob mob, int mobJumpDelay, SoundEvent jumpSound, float soundPitch, float soundVolume) {
        super(mob);
        this.entity = mob;
        this.mobJumpDelay = mobJumpDelay;
        this.jumpSound = jumpSound;
        this.soundPitch = soundPitch;
        this.soundVolume = soundVolume;
        this.yRot = 180.0F * mob.getYRot() / (float) Math.PI;
    }

    public void setDirection(float yRot, boolean isAggressive) {
        this.yRot = yRot;
        this.isAggressive = isAggressive;
    }

    public void setWantedMovement(double speedModifier) {
        this.speedModifier = speedModifier;
        this.operation = MoveControl.Operation.MOVE_TO;
    }

    protected int getJumpDelay() {
        return this.random.nextInt(this.mobJumpDelay);
    }

    @Override
    public void tick() {
        this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.yRot, 90.0F));
        this.mob.yHeadRot = this.mob.getYRot();
        this.mob.yBodyRot = this.mob.getYRot();
        if (this.operation != MoveControl.Operation.MOVE_TO) {
            this.mob.setZza(0.0F);
        } else {
            this.operation = MoveControl.Operation.WAIT;
            if (this.mob.onGround()) {
                this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                if (this.jumpDelay-- <= 0) {
                    this.jumpDelay = this.getJumpDelay();
                    if (this.isAggressive) {
                        this.jumpDelay /= 3;
                    }

                    this.entity.getJumpControl().jump();
                    this.entity.playSound(this.jumpSound, this.soundVolume, this.soundPitch);
                } else {
                    this.entity.xxa = 0.0F;
                    this.entity.zza = 0.0F;
                    this.mob.setSpeed(0.0F);
                }
            } else {
                this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            }
        }
    }
}