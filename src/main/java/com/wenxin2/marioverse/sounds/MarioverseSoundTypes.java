package com.wenxin2.marioverse.sounds;

import com.wenxin2.marioverse.init.SoundRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;

public class MarioverseSoundTypes {
    public static final SoundType WATER_SPOUT_TYPE = new SoundType(1.0F, 1.0F, SoundEvents.BUCKET_FILL,
            SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY, SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_FILL);
    public static final SoundType COIN_TYPE = new SoundType(1.0F, 1.0F, SoundRegistry.COIN_PICKUP.get(),
            SoundRegistry.COIN_PICKUP.get(), SoundRegistry.COIN_PLACE.get(), SoundRegistry.COIN_PLACE.get(), SoundRegistry.COIN_PLACE.get());
}
