package com.wenxin2.marioverse.utils;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.phys.Vec3;

public class ServerParticleUtils {
    public static void spawnParticlesOnBlockFaces(ServerLevel serverWorld, BlockPos pos, ParticleOptions particleOptions, IntProvider count) {
        for (Direction direction : Direction.values()) {
            spawnParticlesOnBlockFace(serverWorld, pos, direction, particleOptions, count,
                    () -> getRandomSpeedRanges(serverWorld.getRandom()), 0.55);
        }
    }

    public static void spawnParticlesOnBlockFace(
            ServerLevel serverWorld, BlockPos pos, Direction direction, ParticleOptions particleOptions, IntProvider count,
            Supplier<Vec3> motionSupplier, double offset
    ) {
        int particleAmount = count.sample(serverWorld.random);

        for (int i = 0; i < particleAmount; i++) {
            spawnParticleOnFace(serverWorld, pos, direction, particleOptions, motionSupplier.get(), offset);
        }
    }

    public static void spawnParticleOnFace(
            ServerLevel serverWorld, BlockPos pos, Direction face, ParticleOptions particleOptions, Vec3 motion, double offset
    ) {
        Vec3 center = Vec3.atCenterOf(pos);
        int xStep = face.getStepX();
        int yStep = face.getStepY();
        int zStep = face.getStepZ();

        double x = center.x + (xStep == 0 ? Mth.nextDouble(serverWorld.random, -0.5, 0.5) : xStep * offset);
        double y = center.y + (yStep == 0 ? Mth.nextDouble(serverWorld.random, -0.5, 0.5) : yStep * offset);
        double z = center.z + (zStep == 0 ? Mth.nextDouble(serverWorld.random, -0.5, 0.5) : zStep * offset);

        double motionX = xStep == 0 ? motion.x() : 0.0;
        double motionY = yStep == 0 ? motion.y() : 0.0;
        double motionZ = zStep == 0 ? motion.z() : 0.0;

        serverWorld.sendParticles(particleOptions, x, y, z, 1, motionX, motionY, motionZ, 0.0);
    }

    public static Vec3 getRandomSpeedRanges(RandomSource random) {
        return new Vec3(
                Mth.nextDouble(random, -0.5, 0.5),
                Mth.nextDouble(random, -0.5, 0.5),
                Mth.nextDouble(random, -0.5, 0.5)
        );
    }
}
