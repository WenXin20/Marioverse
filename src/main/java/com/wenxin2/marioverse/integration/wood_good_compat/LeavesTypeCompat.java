package com.wenxin2.marioverse.integration.wood_good_compat;

import net.mehvahdjukaar.moonlight.api.set.leaves.LeavesTypeRegistry;

public class LeavesTypeCompat {
    public static void init() {
        LeavesTypeRegistry registry = LeavesTypeRegistry.INSTANCE;
        registry.addLeavesToWoodMapping("marioverse:dark_spookroot", "marioverse:spookroot");
    }
}
