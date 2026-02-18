package com.wenxin2.marioverse.entities.ai.controls;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import org.jetbrains.annotations.Nullable;

public class JumpInPlaceMoveControl extends MoveControl {
    private final Mob entity;
    private final RandomSource random = RandomSource.create();

    private final int mobJumpDelay;
    private final @Nullable SoundEvent jumpSound;
    private final float soundPitch;
    private final float soundVolume;

    private int jumpDelay;
    private boolean isAggressive;

    public JumpInPlaceMoveControl(Mob mob, int mobJumpDelay, @Nullable SoundEvent jumpSound, float soundPitch, float soundVolume) {
        super(mob);
        this.entity = mob;
        this.mobJumpDelay = mobJumpDelay;
        this.jumpSound = jumpSound;
        this.soundPitch = soundPitch;
        this.soundVolume = soundVolume;
    }

    public void setAggressive(boolean aggressive) {
        this.isAggressive = aggressive;
    }

    public void triggerJump(double speedModifier) {
        this.speedModifier = speedModifier;
        this.operation = MoveControl.Operation.MOVE_TO;
    }

    protected int getJumpDelay() {
        return this.random.nextInt(this.mobJumpDelay);
    }

    @Override
    public void tick() {
        this.mob.setXxa(0.0F);
        this.mob.setZza(0.0F);
        this.mob.setSpeed(0.0F);

        if (this.operation != MoveControl.Operation.MOVE_TO)
            return;

        this.operation = MoveControl.Operation.WAIT;

        if (this.mob.onGround()) {
            if (this.jumpDelay-- <= 0) {
                this.jumpDelay = this.getJumpDelay();

                if (this.isAggressive)
                    this.jumpDelay /= 3;

                this.entity.getJumpControl().jump();

                if (this.jumpSound != null)
                    this.entity.playSound(this.jumpSound, this.soundVolume, this.soundPitch);
            }
        }
    }
}