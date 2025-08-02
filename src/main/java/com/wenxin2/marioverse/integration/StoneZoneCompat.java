package com.wenxin2.marioverse.integration;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.integration.stone_zone_compat.MarioverseModule;
import net.mehvahdjukaar.every_compat.api.EveryCompatAPI;

public class StoneZoneCompat {
    public static void init() {
        EveryCompatAPI.registerModule(new MarioverseModule(Marioverse.MOD_ID, "mv"));
    }
}
