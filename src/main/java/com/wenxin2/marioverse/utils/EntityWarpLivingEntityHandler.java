package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.registries.ConfigRegistry;
import org.jetbrains.annotations.NotNull;

public interface EntityWarpLivingEntityHandler extends EntityWarpEntityHandler {
    @NotNull
    @Override
    default Boolean getEntityWarpTeleportConfig() {
        return ConfigRegistry.TELEPORT_MOBS.get();
    }
}
