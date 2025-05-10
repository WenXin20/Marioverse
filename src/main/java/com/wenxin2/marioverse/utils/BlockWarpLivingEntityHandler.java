package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.registries.ConfigRegistry;
import org.jetbrains.annotations.NotNull;

public interface BlockWarpLivingEntityHandler extends BlockWarpEntityHandler {
    @NotNull
    default Boolean getBlockWarpTeleportConfig() {
        return ConfigRegistry.TELEPORT_MOBS.get();
    }
}
