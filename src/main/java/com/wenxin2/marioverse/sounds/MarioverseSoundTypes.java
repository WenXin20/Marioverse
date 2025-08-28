package com.wenxin2.marioverse.sounds;

import com.wenxin2.marioverse.registries.SoundRegistry;
import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.common.util.DeferredSoundType;

public class MarioverseSoundTypes {
    public static final SoundType CLEAR_PIPE = new DeferredSoundType(4.0F, 1.0F,  () -> SoundEvents.GLASS_BREAK,
            SoundRegistry.GLASS_STEP,  () -> SoundEvents.GLASS_PLACE, SoundRegistry.GLASS_HIT, SoundRegistry.GLASS_FALL);

    public static final SoundType COIN_TYPE = new DeferredSoundType(1.0F, 1.0F, () -> SoundEvents.NETHERITE_BLOCK_BREAK,
            SoundRegistry.COIN_PICKUP, SoundRegistry.COIN_PLACE, SoundRegistry.COIN_PLACE, SoundRegistry.COIN_PLACE);

    public static final SoundType WATER_SPOUT_TYPE = new DeferredSoundType(1.0F, 1.0F, () -> SoundEvents.BUCKET_FILL,
            () -> SoundEvents.BUCKET_FILL, () -> SoundEvents.BUCKET_EMPTY, () -> SoundEvents.BUCKET_FILL, () -> SoundEvents.BUCKET_FILL);
}
