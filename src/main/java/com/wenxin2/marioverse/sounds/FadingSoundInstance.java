package com.wenxin2.marioverse.sounds;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FadingSoundInstance extends AbstractTickableSoundInstance {
    private final LivingEntity entity;
    private final float fadeDuration;
    private final IntSupplier duration;
    private final BooleanSupplier hasPowerUp;
    private int lastDuration;
    private float fadeTicks = -1;

    public FadingSoundInstance(LivingEntity entity, SoundEvent soundEvent, SoundSource soundSource, RandomSource random,
                               float fadeDuration, IntSupplier duration, BooleanSupplier hasPowerUp) {
        super(soundEvent, soundSource, random);
        this.entity = entity;
        this.fadeDuration = fadeDuration;
        this.duration = duration;
        this.hasPowerUp = hasPowerUp;
        this.lastDuration = duration.getAsInt();
        this.attenuation = Attenuation.LINEAR;
        this.looping = true;
        this.relative = false;
        this.delay = 0;
        this.volume = 1.0F;
    }

    @Override
    public void tick() {
        int currentDuration = duration.getAsInt();

        if (entity == null || !entity.isAlive() || entity.isRemoved()) {
            this.stop();
            return;
        }

        if (currentDuration > lastDuration) {
            fadeTicks = -1;
            volume = 1.0F;
        }

        if (!hasPowerUp.getAsBoolean()) {
            if (fadeTicks < 0)
                fadeTicks = fadeDuration / 2;
        }

        if (fadeTicks >= 0) {
            volume = Math.max(0.0F, fadeTicks / fadeDuration);
            fadeTicks--;

            if (fadeTicks <= 0)
                stop();
        }
        lastDuration = currentDuration;

        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
    }

    @Override
    public float getVolume() {
        return this.volume;
    }

    @Override
    public float getPitch() {
        return this.pitch;
    }
}