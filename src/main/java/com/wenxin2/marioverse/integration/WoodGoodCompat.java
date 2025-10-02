package com.wenxin2.marioverse.integration;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.integration.wood_good_compat.WoodModule;
import net.mehvahdjukaar.every_compat.api.EveryCompatAPI;

public class WoodGoodCompat {
    public static void init() {
        EveryCompatAPI.registerModule(new WoodModule(Marioverse.MOD_ID, "mv"));
    }
}
