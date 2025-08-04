package com.wenxin2.marioverse.integration;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.integration.stone_zone_compat.MudModule;
import com.wenxin2.marioverse.integration.stone_zone_compat.StoneModule;
import net.mehvahdjukaar.every_compat.api.EveryCompatAPI;

public class StoneZoneCompat {
    public static void init() {
        EveryCompatAPI.registerModule(new StoneModule(Marioverse.MOD_ID, "mv"));
        EveryCompatAPI.registerModule(new MudModule(Marioverse.MOD_ID, "mv"));
    }
}
