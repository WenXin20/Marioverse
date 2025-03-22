package com.wenxin2.marioverse.utils;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ServerParticleUtils {
    public static void spawnParticlesOnBlockFaces(ParticleOptions particleOptions, ServerLevel serverWorld, BlockPos pos, IntProvider count) {
        for (Direction direction : Direction.values()) {
            spawnParticlesOnBlockFace(particleOptions, serverWorld, pos, direction, count,
                    () -> getRandomSpeedRanges(serverWorld.getRandom()), 0.55);
        }
    }

    public static void spawnParticlesOnBlockFace(ParticleOptions particleOptions, ServerLevel serverWorld, BlockPos pos, Direction direction,
                                                 IntProvider count, Supplier<Vec3> motionSupplier, double offset) {
        int particleAmount = count.sample(serverWorld.random);

        for (int i = 0; i < particleAmount; i++) {
            spawnParticleOnFace(particleOptions, serverWorld, pos, direction, motionSupplier.get(), offset);
        }
    }

    public static void spawnParticleOnFace(ParticleOptions particleOptions, ServerLevel serverWorld, BlockPos pos, Direction face, Vec3 motion, double offset) {
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

    public static void spawnParticlesOnEntityRandomly(ParticleOptions particleOptions, ServerLevel serverWorld, Entity entity) {
        RandomSource rand = RandomSource.create();
        double offsetX = rand.nextDouble() * entity.getBbWidth() - (entity.getBbWidth() / 2.0);
        double offsetY = rand.nextDouble() * entity.getBbHeight();
        double offsetZ = rand.nextDouble() * entity.getBbWidth() - (entity.getBbWidth() / 2.0);

        serverWorld.sendParticles(particleOptions, entity.getX() + offsetX, entity.getY() + offsetY, entity.getZ() + offsetZ, 1, 0, 0, 0, 0.0);
    }

    public static void spawnIceCubeParticles(ParticleOptions particleOptions, ServerLevel serverWorld, Entity entity, float height, float width) {
        RandomSource rand = RandomSource.create();
        double offsetX = rand.nextDouble() * width - (width / 2);
        double offsetY = rand.nextDouble() * height;
        double offsetZ = rand.nextDouble() * width - (width / 2);

        serverWorld.sendParticles(particleOptions, entity.getX() + offsetX, entity.getY() + offsetY, entity.getZ() + offsetZ, 1, 0, 0, 0, 0.0);
    }

    public static void spawnPoweredUpParticles(ParticleOptions particleOptions, ServerLevel serverWorld, Entity entity, int avgAmount) {
        float scaleFactor = entity.getBbWidth();
        int numParticles = (int) (scaleFactor * avgAmount);
        double radius = entity.getBbWidth() / 2;

        for (int i = 0; i < numParticles; i++) {
            // Calculate angle for each particle
            double angle = 2 * Math.PI * i / numParticles;
            // Calculate the X and Z offset using sine and cosine to spread in an ellipse
            double offsetX = Math.cos(angle) * radius;
            double offsetY = entity.getBbHeight();
            double offsetZ = Math.sin(angle) * radius;

            double x = entity.getX() + offsetX;
            double y = entity.getY();
            double z = entity.getZ() + offsetZ;

            serverWorld.sendParticles(particleOptions, x, y + offsetY - 0.2, z, 1, 0, 0, 0, 0.0);
            serverWorld.sendParticles(particleOptions, x, y + offsetY / 2, z, 1, 0, 0, 0, 0.0);
            serverWorld.sendParticles(particleOptions, x, y + 0.2, z, 1, 0, 0, 0, 0.0);
        }
    }

    public static void spawnParticleRingOnEntity(ParticleOptions particleOptions, ServerLevel serverWorld, Entity entity, double radius, int particleAmt) {
        for (int i = 0; i < particleAmt; i++) {
            double angle = 2 * Math.PI * i / particleAmt;

            double offsetX = Math.cos(angle) * radius;
            double offsetY = Math.sin(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;

            double x = entity.getX() + offsetX;
            double y = entity.getY() + offsetY;
            double z = entity.getZ() + offsetZ;

            serverWorld.sendParticles(particleOptions, x, y, z, 1, 0, 0, 0, 0.0);
        }
    }

    public static void spawnParticleRingAboveEntity(ParticleOptions particleOptions, ServerLevel serverWorld, Entity entity, double radius, int particleAmt) {
        for (int i = 0; i < particleAmt; i++) {
            double angle = 2 * Math.PI * i / particleAmt;

            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;

            double x = entity.getX() + offsetX;
            double y = entity.getY() + entity.getBbHeight();
            double z = entity.getZ() + offsetZ;

            serverWorld.sendParticles(particleOptions, x, y, z, 1, 0, 0, 0, 0.0);
        }
    }

    public static void spawnParticleRingBelowEntity(ParticleOptions particleOptions, ServerLevel serverWorld, Entity entity, double radius, int particleAmt) {
        for (int i = 0; i < particleAmt; i++) {
            double angle = 2 * Math.PI * i / particleAmt;

            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;

            double x = entity.getX() + offsetX;
            double y = entity.getY();
            double z = entity.getZ() + offsetZ;

            serverWorld.sendParticles(particleOptions, x, y, z, 1, 0, 0, 0, 0.0);
        }
    }

    public static Vec3 getRandomSpeedRanges(RandomSource random) {
        return new Vec3(
                Mth.nextDouble(random, -0.5, 0.5),
                Mth.nextDouble(random, -0.5, 0.5),
                Mth.nextDouble(random, -0.5, 0.5)
        );
    }
}
