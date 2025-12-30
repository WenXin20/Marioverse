package com.wenxin2.marioverse.client;

import com.wenxin2.marioverse.registries.BlockRegistry;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class QuicksandClient {
    private static float overlayProgress = 0.0F;

    public static void clientTick(Minecraft mc) {
        if (mc.player == null) return;

        boolean inQuicksand = mc.player.getInBlockState().is(BlockRegistry.QUICKSAND.get());

        float target = inQuicksand ? 1.0F : 0.0F;
        overlayProgress += (target - overlayProgress) * 0.08F;
    }

    public static float getOverlayProgress() {
        return overlayProgress;
    }
}
