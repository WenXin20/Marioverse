package com.wenxin2.marioverse.integration;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.integration.stone_zone_compat.StoneZoneModule;
import net.mehvahdjukaar.every_compat.api.EveryCompatAPI;

public class StoneZoneCompat {
    public static void init() {
        EveryCompatAPI.registerModule(new StoneZoneModule(Marioverse.MOD_ID, "mv"));
    }
}
