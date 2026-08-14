package com.wenxin2.marioverse.world.grower;

import com.wenxin2.marioverse.world.feature.SuperTreeFeatures;
import java.util.Optional;
import net.minecraft.world.level.block.grower.TreeGrower;

public class SuperTreeGrower {
    public static final TreeGrower MUSHROOT = new TreeGrower(
            "mushroot", 0.1F,
            Optional.of(SuperTreeFeatures.MEGA_MUSHROOT),
            Optional.of(SuperTreeFeatures.MEGA_MUSHROOT_BEES_002),
            Optional.of(SuperTreeFeatures.MUSHROOT),
            Optional.of(SuperTreeFeatures.MUSHROOT_BEES_002),
            Optional.empty(),
            Optional.empty()
    );
}
