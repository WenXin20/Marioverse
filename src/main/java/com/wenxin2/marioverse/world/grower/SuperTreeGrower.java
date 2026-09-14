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

    public static final TreeGrower SPOOKROOT = new TreeGrower(
            "spookroot", 0.1F,
            Optional.of(SuperTreeFeatures.MEGA_SPOOKROOT),
            Optional.of(SuperTreeFeatures.MEGA_SPOOKROOT_BEES_002),
            Optional.of(SuperTreeFeatures.SPOOKROOT),
            Optional.of(SuperTreeFeatures.SPOOKROOT_BEES_002),
            Optional.empty(),
            Optional.empty()
    );

    public static final TreeGrower SPOOKY_SPOOKROOT = new TreeGrower(
            "spooky_spookroot", 0.1F,
            Optional.of(SuperTreeFeatures.MEGA_SPOOKY_SPOOKROOT),
            Optional.of(SuperTreeFeatures.MEGA_SPOOKY_SPOOKROOT_BEES_002),
            Optional.of(SuperTreeFeatures.SPOOKY_SPOOKROOT),
            Optional.of(SuperTreeFeatures.SPOOKY_SPOOKROOT_BEES_002),
            Optional.empty(),
            Optional.empty()
    );
}
