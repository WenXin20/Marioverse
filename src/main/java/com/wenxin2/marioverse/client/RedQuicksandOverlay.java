package com.wenxin2.marioverse.client;

import com.wenxin2.marioverse.registries.BlockRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public final class RedQuicksandOverlay {
    private static float overlayProgress = 0.0F;
    private static final float FADE_TIME = 40.0F;

    public static void clientTick(Minecraft mc) {
        if (mc.player == null) return;

        boolean inRedQuicksand = mc.level != null && mc.level
                .getBlockState(BlockPos.containing(mc.player.getEyePosition()))
                .is(BlockRegistry.RED_QUICKSAND.get());
        float step = 1.0F / (FADE_TIME * 20.0F);

        if (inRedQuicksand)
            overlayProgress = Math.min(1.0F, overlayProgress + step);
        else overlayProgress = Math.max(0.0F, overlayProgress - step);
    }

    public static float getOverlayProgress() {
        return overlayProgress;
    }
}
