package com.wenxin2.marioverse.integration.iris_compat;

import net.irisshaders.iris.api.v0.IrisApi;

public class IrisShaderProvider {
    public static boolean isShaderPackInUse() {
        return IrisApi.getInstance().isShaderPackInUse();
    }
}
