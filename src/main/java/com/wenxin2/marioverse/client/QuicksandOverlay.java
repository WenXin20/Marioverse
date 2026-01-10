package com.wenxin2.marioverse.client;

import com.wenxin2.marioverse.registries.BlockRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class QuicksandOverlay {
    private static float overlayProgress = 0.0F;
    private static final float FADE_TIME = 40.0F;
    private static boolean wasInQuicksand = false;

    public static void clientTick(Minecraft mc) {
        if (mc.player == null) return;

        boolean inQuicksand = mc.level != null
                && !mc.player.isSpectator()
                && mc.level.getBlockState(BlockPos.containing(mc.player.getEyePosition()))
                    .is(BlockRegistry.QUICKSAND.get());
        boolean inWater = mc.level != null
                && mc.level.getFluidState(mc.player.blockPosition()).is(FluidTags.WATER);
        float step = 1.0F / (FADE_TIME * 20.0F);

        if (!inQuicksand && wasInQuicksand || inWater)
            overlayProgress = Math.max(0.0F, overlayProgress - step * 3.0F);
        else if (inQuicksand)
            overlayProgress = Math.min(1.0F, overlayProgress + step);
        else overlayProgress = Math.max(0.0F, overlayProgress - step);

        wasInQuicksand = inQuicksand;
    }

    public static float getOverlayProgress() {
        return overlayProgress;
    }
}
