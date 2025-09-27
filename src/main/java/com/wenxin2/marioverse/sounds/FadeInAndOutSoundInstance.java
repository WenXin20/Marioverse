package com.wenxin2.marioverse.sounds;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FadeInAndOutSoundInstance extends AbstractTickableSoundInstance {
    private final Entity entity;
    private final float fadeInDuration;
    private final float fadeOutDuration;
    private boolean fadingOut = false;
    private float ticksSinceFade = 0F;

    public FadeInAndOutSoundInstance(Entity entity, SoundEvent soundEvent, SoundSource soundSource,
                                     float fadeInDuration, float fadeOutDuration) {
        super(soundEvent, soundSource, entity.getRandom());
        this.entity = entity;
        this.fadeInDuration = fadeInDuration;
        this.fadeOutDuration = fadeOutDuration;
        this.looping = true;
        this.delay = 0;
        this.volume = 1.0F;
    }

    @Override
    public void tick() {
        if (fadingOut) {
            ticksSinceFade++;
            this.volume = Math.max(0.0F, 1.0F - (ticksSinceFade / fadeOutDuration));
        } else {
            ticksSinceFade++;
            this.volume = Math.min(1.0F, ticksSinceFade / fadeInDuration);
        }

        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
    }

    public void startFadeIn() {
        if (fadingOut) {
            fadingOut = false;
            ticksSinceFade = 0f;
        }
    }

    public void startFadeOut() {
        if (!fadingOut) {
            fadingOut = true;
            ticksSinceFade = 0f;
        }
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