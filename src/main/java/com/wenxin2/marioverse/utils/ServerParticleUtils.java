package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.registries.AttributesRegistry;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public static void spawnParticlesAboveBlock(ParticleOptions particleOptions, ServerLevel serverWorld, BlockPos pos) {
        RandomSource random = serverWorld.getRandom();

        for (int i = 0; i < 40; ++i) {
            serverWorld.sendParticles(particleOptions,
                    pos.getX() + 0.5D + (0.5D * (random.nextBoolean() ? 1 : -1)),
                    pos.getY() + 1.5D,
                    pos.getZ() + 0.5D + (0.5D * (random.nextBoolean() ? 1 : -1)),
                    1,
                    (random.nextDouble() - 0.5D) * 2.0D,
                    -random.nextDouble(),
                    (random.nextDouble() - 0.5D) * 2.0D, 1.0);
        }
    }

    public static void spawnParticleRingOnBlock(ParticleOptions particleOptions, ServerLevel serverWorld, BlockPos pos, double radius, int avgAmount) {
        float scaleFactor = 1.0F;
        int numParticles = (int) (scaleFactor * Math.max(3, avgAmount));

        for (int i = 0; i < numParticles; i++) {
            // Calculate angle for each particle
            double angle = 2 * Math.PI * i / numParticles;
            // Calculate the X and Z offset using sine and cosine to spread in an ellipse
            double offsetX = Math.cos(angle) * radius;
            double offsetY = 0.5D;
            double offsetZ = Math.sin(angle) * radius;

            double x = pos.getX() + 0.5 + offsetX;
            double y = pos.getY() + offsetY;
            double z = pos.getZ() + 0.5 + offsetZ;

            serverWorld.sendParticles(particleOptions, x, y, z, 1, 0, 0, 0, 0.0);
        }
    }

    public static void spawnThreeLayerBlockParticles(ParticleOptions particleOptions, ServerLevel serverWorld, @Nullable Entity entity, BlockPos pos, int avgAmount) {
        float scaleFactor = 1;
        int numParticles = (int) (scaleFactor * Math.max(3, avgAmount));
        double radius = 0.65;
        if (entity instanceof Painting painting)
            radius = (double) painting.getVariant().value().width() / 2;

        for (int i = 0; i < numParticles; i++) {
            double angle = 2 * Math.PI * i / numParticles;
            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;

            double x = pos.getX() + 0.5 + offsetX;
            double y = pos.getY();
            double z = pos.getZ() + 0.5 + offsetZ;

            serverWorld.sendParticles(particleOptions, x, y, z, 1, 0, 0, 0, 0.0);
            serverWorld.sendParticles(particleOptions, x, y + 0.5, z, 1, 0, 0, 0, 0.0);
            serverWorld.sendParticles(particleOptions, x, y + 1.0, z, 1, 0, 0, 0, 0.0);
        }
    }

    public static void spawnOneLayerBlockParticles(ParticleOptions particleOptions, ServerLevel serverWorld, @Nullable Entity entity, BlockPos pos, int avgAmount) {
        float scaleFactor = 1;
        int numParticles = (int) (scaleFactor * Math.max(3, avgAmount));
        double radius = 0.65;
        if (entity instanceof Painting painting)
            radius = (double) painting.getVariant().value().width() / 2;

        for (int i = 0; i < numParticles; i++) {
            double angle = 2 * Math.PI * i / numParticles;
            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;

            double x = pos.getX() + 0.5 + offsetX;
            double y = pos.getY();
            double z = pos.getZ() + 0.5 + offsetZ;

            serverWorld.sendParticles(particleOptions, x, y, z, 1, 0, 0, 0, 0.0);
        }
    }

    public static void spawnRewardParticle(ParticleOptions particleOptions, ServerLevel serverWorld, Entity entity, double yHeight) {
        ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(particleOptions, true,
                entity.getX(), entity.getY() + entity.getBbHeight() + yHeight, entity.getZ(),
                0.0f, 1.0F, 0.0F, 0.5F, 1);
        for (ServerPlayer player : serverWorld.players())
            player.connection.send(packet);
    }

    public static void spawnAnimParticles(ParticleOptions particleOptions, ServerLevel serverWorld, Entity entity) {
        for (int i = 0; i < 10; i++) {
            double dx = serverWorld.random.nextGaussian() * 0.02;
            double dy = serverWorld.random.nextGaussian() * 0.02;
            double dz = serverWorld.random.nextGaussian() * 0.02;
            serverWorld.sendParticles(particleOptions, entity.getRandomX(1.0) - dx * 10.0,
                    entity.getRandomY() - dy * 5.0, entity.getRandomZ(1.0) - dz * 10.0,
                    1, dx, dy, dz, 0.02);
        }
    }

    public static void spawnSingleParticleOnEntityRandomly(ParticleOptions particleOptions, ServerLevel serverWorld, Entity entity) {
        RandomSource rand = RandomSource.create();
        double offsetX = rand.nextDouble() * entity.getBbWidth() - (entity.getBbWidth() / 2.0);
        double offsetY = rand.nextDouble() * entity.getBbHeight();
        double offsetZ = rand.nextDouble() * entity.getBbWidth() - (entity.getBbWidth() / 2.0);

        serverWorld.sendParticles(particleOptions, entity.getX() + offsetX, entity.getY() + offsetY, entity.getZ() + offsetZ,
                1, 0, 0, 0, 1.0);
    }

    public static void spawnParticlesOnEntityRandomly(ParticleOptions particleOptions, ServerLevel serverWorld, Entity entity, double speed, int avgAmount) {
        RandomSource rand = RandomSource.create();

        float scaleFactor = entity.getBbWidth() * entity.getBbHeight();
        int numParticles = (int) (scaleFactor * Math.max(3, avgAmount));

        for (int i = 0; i < numParticles; i++) {
            double offsetX = rand.nextDouble() * entity.getBbWidth() - (entity.getBbWidth() / 2.0);
            double offsetY = rand.nextDouble() * entity.getBbHeight();
            double offsetZ = rand.nextDouble() * entity.getBbWidth() - (entity.getBbWidth() / 2.0);

            if (entity instanceof Painting painting) {
                int width = painting.getVariant().value().width();
                int height = painting.getVariant().value().height();

                offsetX = rand.nextDouble() * width - (width / 2.0);
                offsetY = rand.nextDouble() * height - (height / 2.0);
                offsetZ = rand.nextDouble() * width - (width / 2.0);

                switch (painting.getDirection()) {
                    case NORTH -> offsetZ = -0.2;
                    case SOUTH -> offsetZ = 0.2;
                    case EAST  -> offsetX = 0.2;
                    case WEST  -> offsetX = -0.2;
                }
            }

            serverWorld.sendParticles(particleOptions, entity.getX() + offsetX, entity.getY() + offsetY, entity.getZ() + offsetZ,
                    1, 0, 0, 0, speed);
        }
    }

    public static void spawnEntityBreakParticles(ParticleOptions particleOptions, ServerLevel serverWorld, Entity entity, float height, float width) {
        RandomSource rand = RandomSource.create();
        double offsetX = rand.nextDouble() * width - (width / 2);
        double offsetY = rand.nextDouble() * height;
        double offsetZ = rand.nextDouble() * width - (width / 2);

        serverWorld.sendParticles(particleOptions, entity.getX() + offsetX, entity.getY() + offsetY, entity.getZ() + offsetZ, 1, 0, 0, 0, 0.0);
    }

    public static void spawnPoweredUpParticles(ParticleOptions particleOptions, ServerLevel serverWorld, Entity entity, int avgAmount) {
        float scaleFactor = entity.getBbWidth();
        int numParticles = (int) (scaleFactor * Math.max(3, avgAmount));
        double radius = entity.getBbWidth() / 2;
        if (entity instanceof Painting painting)
            radius = (double) painting.getVariant().value().width() / 2;

        for (int i = 0; i < numParticles; i++) {
            // Calculate angle for each particle
            double angle = 2 * Math.PI * i / numParticles;
            // Calculate the X and Z offset using sine and cosine to spread in an ellipse
            double offsetX = Math.cos(angle) * radius;
            double offsetY = entity.getBbHeight();
            double offsetZ = Math.sin(angle) * radius;

            if (entity instanceof Painting painting)
                offsetY = painting.getVariant().value().height();

            double x = entity.getX() + offsetX;
            double y = entity.getY();
            double z = entity.getZ() + offsetZ;

            serverWorld.sendParticles(particleOptions, x, y + offsetY - 0.2, z, 1, 0, 0, 0, 0.0);
            serverWorld.sendParticles(particleOptions, x, y + offsetY / 2, z, 1, 0, 0, 0, 0.0);
            serverWorld.sendParticles(particleOptions, x, y + 0.2, z, 1, 0, 0, 0, 0.0);
        }
    }

    public static void spawnSprayUpwardsParticles(ParticleOptions particleOptions, ServerLevel serverLevel, Entity entity,
                                                  int avgAmount, double heightMultiplier, double velocity) {
        RandomSource rand = serverLevel.getRandom();
        float scaleFactor = entity.getBbWidth() * entity.getBbHeight();
        int numParticles = (int) (scaleFactor * Math.max(3, avgAmount));
        double radius = entity.getBbWidth() / 2 * 1.1;
        double height = entity.getBbHeight() * heightMultiplier;

        for (int i = 0; i < numParticles; i++) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            double distance = rand.nextDouble() * radius;
            double offsetX = Math.cos(angle) * distance;
            double offsetZ = Math.sin(angle) * distance;

            double x = entity.getX() + offsetX;
            double y = entity.getY();
            double z = entity.getZ() + offsetZ;

            double motionY = (height / 2.0) + rand.nextDouble() * (height / 2.0);

            serverLevel.sendParticles(particleOptions, x, y, z, 0, 0.0, motionY, 0.0, velocity);
        }
    }

    public static void spawnParticleRingOnEntity(ParticleOptions particleOptions, ServerLevel serverWorld, Entity entity, double radius, double speed, int particleAmt) {
        for (int i = 0; i < particleAmt; i++) {
            double angle = 2 * Math.PI * i / particleAmt;

            double offsetX = Math.cos(angle) * radius;
            double offsetY = Math.sin(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;

            double x = entity.getX() + offsetX;
            double y = entity.getY() + offsetY;
            double z = entity.getZ() + offsetZ;

            serverWorld.sendParticles(particleOptions, x, y, z, 1, 0, 0, 0, speed);
        }
    }

    public static void spawnParticleRingAboveEntity(ParticleOptions particleOptions, ServerLevel serverWorld, Entity entity, double radius, double speed, int particleAmt) {
        for (int i = 0; i < particleAmt; i++) {
            double angle = 2 * Math.PI * i / particleAmt;

            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;

            double x = entity.getX() + offsetX;
            double y = entity.getY() + entity.getBbHeight();
            double z = entity.getZ() + offsetZ;

            serverWorld.sendParticles(particleOptions, x, y, z, 1, 0, 0, 0, speed);
        }
    }

    public static void spawnParticleRingBelowEntity(ParticleOptions particleOptions, ServerLevel serverWorld, Entity entity, double radius, double speed, int particleAmt) {
        for (int i = 0; i < particleAmt; i++) {
            double angle = 2 * Math.PI * i / particleAmt;

            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;

            double x = entity.getX() + offsetX;
            double y = entity.getY();
            double z = entity.getZ() + offsetZ;

            serverWorld.sendParticles(particleOptions, x, y, z, 1, 0, 0, 0, speed);
        }
    }

    public static void spawnParticleTrail(ParticleOptions particleOptions, ServerLevel serverWorld, Entity entity,
                                          boolean particlesInAir, boolean particlesOnFluids, int particleAmt, double particleHeight) {
        BlockPos posLegacy = entity.getOnPosLegacy();
        BlockState state = entity.level().getBlockState(posLegacy);

        if (state.getRenderShape() != RenderShape.INVISIBLE || particlesOnFluids || particlesInAir) {
            Vec3 vec3 = entity.getDeltaMovement();
            BlockPos pos = entity.blockPosition();
            double x = entity.getX() + (entity.getRandom().nextDouble() - 0.5);
            double z = entity.getZ() + (entity.getRandom().nextDouble() - 0.5);
            double dOffset = -4.0;
            if (entity instanceof AbstractMinecart)
                dOffset = -16.0;

            double dx = (vec3.x / 2) * dOffset;
            double dy = 0.5;
            double dz = (vec3.z / 2) * dOffset;
            double speed = Math.sqrt(dx * dx + dy * dy + dz * dz);
            double offsetX = dx / speed;
            double offsetY = dy / speed;
            double offsetZ = dz / speed;

            if (entity instanceof LivingEntity livingEntity) {
                float scale = (float) livingEntity.getAttributeValue(Attributes.SCALE);
                float widthScale = (float) livingEntity.getAttributeValue(AttributesRegistry.WIDTH_SCALE);

                x = entity.getX() + (entity.getRandom().nextDouble() - 0.5) * scale * widthScale;
                z = entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * scale * widthScale;
            }

            if (pos.getX() != posLegacy.getX())
                x = Mth.clamp(x, posLegacy.getX(), posLegacy.getX() + 1.0);

            if (pos.getZ() != posLegacy.getZ())
                z = Mth.clamp(z, posLegacy.getZ(), posLegacy.getZ() + 1.0);

            serverWorld.sendParticles(particleOptions, x, entity.getY() + particleHeight, z, particleAmt, offsetX, offsetY, offsetZ, speed);
        }
    }

    public static void spawnClientParticleTrail(ParticleOptions particleOptions, @NotNull Entity entity, boolean particlesInAir, int particleAmt, double particleHeight, double particleAtSpeed) {
        BlockPos posLegacy = entity.getOnPosLegacy();
        BlockState state = entity.level().getBlockState(posLegacy);
        Vec3 deltaMovement = entity.getDeltaMovement();

        if (deltaMovement.horizontalDistance() >= particleAtSpeed) {
            for (int i = 0; i < particleAmt; i++) {
                if (state.getRenderShape() != RenderShape.INVISIBLE || particlesInAir) {
                    BlockPos pos = entity.blockPosition();
                    double x = entity.getX() + (entity.getRandom().nextDouble() - 0.5);
                    double z = entity.getZ() + (entity.getRandom().nextDouble() - 0.5);
                    double dOffset = -4.0;

                    if (entity instanceof AbstractMinecart)
                        dOffset = -16.0;

                    if (entity instanceof LivingEntity livingEntity) {
                        float scale = (float) livingEntity.getAttributeValue(Attributes.SCALE);
                        float widthScale = (float) livingEntity.getAttributeValue(AttributesRegistry.WIDTH_SCALE);

                        x = entity.getX() + (entity.getRandom().nextDouble() - 0.5) * scale * widthScale;
                        z = entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * scale * widthScale;
                    }

                    if (pos.getX() != posLegacy.getX())
                        x = Mth.clamp(x, posLegacy.getX(), posLegacy.getX() + 1.0);

                    if (pos.getZ() != posLegacy.getZ())
                        z = Mth.clamp(z, posLegacy.getZ(), posLegacy.getZ() + 1.0);

                    entity.level().addParticle(particleOptions, x, entity.getY() + particleHeight, z, deltaMovement.x * dOffset, 0.5, deltaMovement.z * dOffset);
                }
            }
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
