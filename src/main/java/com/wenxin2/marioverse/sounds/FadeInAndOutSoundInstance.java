package com.wenxin2.marioverse.sounds;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public class FadeInAndOutSoundInstance extends AbstractTickableSoundInstance {
    private final Entity entity;
    private final float fadeInDuration;
    private final float fadeOutDuration;
    private boolean fadingOut = false;
    private float remainingTicks;
    private float ticksSinceStart = 0f;
    public boolean wasInEntrance = false;

    public FadeInAndOutSoundInstance(Entity entity, SoundEvent soundEvent, SoundSource soundSource,
                                     float totalDuration, float fadeInDuration, float fadeOutDuration) {
        super(soundEvent, soundSource, entity.getRandom());
        this.entity = entity;
        this.fadeInDuration = fadeInDuration;
        this.fadeOutDuration = fadeOutDuration;
        this.remainingTicks = totalDuration;
        this.looping = true;
        this.delay = 0;
        this.volume = 1.0F;
    }

    @Override
    public void tick() {
        ticksSinceStart++;
        remainingTicks--;

        if (!fadingOut) {
            if (ticksSinceStart <= fadeInDuration) { // Fade-in
                this.volume = Math.min(1.0F, ticksSinceStart / fadeInDuration);
            } else this.volume = 1.0F;
        } else this.volume = Math.max(0.0F, remainingTicks / fadeOutDuration); // Fade-out

        if (remainingTicks <= 0) this.stop();

        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
    }

    public void startFadeOut() {
        fadingOut = true;
        ticksSinceStart = 0;
        remainingTicks = fadeOutDuration;
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