package com.wenxin2.marioverse.sounds;

import com.wenxin2.marioverse.registries.SoundRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;

public class MarioverseSoundTypes {
    public static final SoundType CLEAR_PIPE = new SoundType(1.0F, 1.0F, SoundEvents.GLASS_BREAK,
            SoundRegistry.GLASS_STEP.get(), SoundEvents.GLASS_PLACE, SoundRegistry.GLASS_HIT.get(), SoundRegistry.GLASS_FALL.get());

    public static final SoundType COIN_TYPE = new SoundType(1.0F, 1.0F, SoundEvents.NETHERITE_BLOCK_BREAK,
            SoundRegistry.COIN_PICKUP.get(), SoundRegistry.COIN_PLACE.get(), SoundRegistry.COIN_PLACE.get(), SoundRegistry.COIN_PLACE.get());

    public static final SoundType WATER_SPOUT_TYPE = new SoundType(1.0F, 1.0F, SoundEvents.BUCKET_FILL,
            SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY, SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_FILL);
}
