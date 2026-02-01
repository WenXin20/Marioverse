package com.wenxin2.marioverse.sounds;

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
    private float remainingTicks;
    private boolean fadeEarly;

    public FadingSoundInstance(LivingEntity entity, SoundEvent soundEvent, SoundSource soundSource, RandomSource random,
                               float totalDuration, float fadeDuration, boolean fadeEarly) {
        super(soundEvent, soundSource, random);
        this.entity = entity;
        this.fadeDuration = fadeDuration;
        this.remainingTicks = totalDuration;
        this.fadeEarly = fadeEarly;
        this.looping = true;
        this.delay = 0;
        this.volume = 1.0F;
    }

    @Override
    public void tick() {
        if (this.remainingTicks < this.fadeDuration || this.fadeEarly)
            this.volume = Math.max(0.0F, this.remainingTicks / this.fadeDuration);
        else this.volume = 1.0F;

        this.remainingTicks--;
        if (this.remainingTicks <= 0)
            this.stop();

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