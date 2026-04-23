package com.wenxin2.marioverse.integration.sable_compat;

import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SableProvider {
    private static ContextGetter CONTEXT_GETTER = (l, e) -> null;

    public static class SableContext {
        public final BlockGetter accessor;
        public final BlockPos posEmbedded;
        public final BlockPos posPlot;
        public final SubLevel sub;
        public final Vec3 posLocal;

        public SableContext(BlockPos posPlot, BlockPos posEmbedded, Vec3 posLocal, BlockGetter accessor, SubLevel sub) {
            this.accessor = accessor;
            this.posEmbedded = posEmbedded;
            this.posLocal = posLocal;
            this.posPlot = posPlot;
            this.sub = sub;
        }
    }

    public interface ContextGetter {
        SableContext get(Level level, Entity entity);
    }

    public static SableContext getContext(Level level, Entity entity) {
        return CONTEXT_GETTER.get(level, entity);
    }

    public static void set(ContextGetter getter) {
        CONTEXT_GETTER = getter;
    }
}