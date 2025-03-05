package com.wenxin2.marioverse.world;

import com.mojang.logging.LogUtils;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public abstract class BaseSpawnerExtended extends BaseSpawner {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int EVENT_SPAWN = 1;
    private int spawnDelay = 20;
    private SimpleWeightedRandomList<SpawnData> spawnPotentials = SimpleWeightedRandomList.empty();
    @Nullable private SpawnData nextSpawnData;
    private double spin;
    private double oSpin;
    private int minSpawnDelay = 200;
    private int maxSpawnDelay = 800;
    private int spawnCount = 4;
    @Nullable private Entity displayEntity;
    private int maxNearbyEntities = 6;
    private int requiredPlayerRange = 16;
    private int spawnRange = 1;

    private boolean isNearPlayer(Level world, BlockPos pos) {
        boolean within16Blocks = world.hasNearbyAlivePlayer(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, this.requiredPlayerRange);
        boolean within1Block = world.hasNearbyAlivePlayer(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 1);

        return within16Blocks && !within1Block;
    }

    @Override
    public void clientTick(Level world, BlockPos pos) {
        if (!this.isNearPlayer(world, pos)) {
            this.oSpin = this.spin;
        } else if (this.displayEntity != null) {
            if (this.spawnDelay > 0)
                this.spawnDelay--;

            this.oSpin = this.spin;
            this.spin = (this.spin + (double)(1000.0F / ((float)this.spawnDelay + 200.0F))) % 360.0;
        }
    }

    @Override
    public void serverTick(@NotNull ServerLevel serverWorld, BlockPos pos) {
        if (this.isNearPlayer(serverWorld, pos)) {
            if (this.spawnDelay == -1)
                this.delay(serverWorld, pos);

            if (this.spawnDelay > 0) {
                this.spawnDelay--;
            } else {
                boolean spawned = false;
                RandomSource random = serverWorld.getRandom();
                SpawnData spawnData = this.getOrCreateNextSpawnData(serverWorld, random, pos);
                BlockState state = serverWorld.getBlockState(pos);
                boolean hasFacing = state.hasProperty(BlockStateProperties.FACING);
                Direction facing = state.getOptionalValue(BlockStateProperties.FACING).orElse(Direction.UP); // Default to UP if missing

                for (int i = 0; i < this.spawnCount; i++) {
                    CompoundTag entityTag = spawnData.getEntityToSpawn();
                    Optional<EntityType<?>> entityTypeOpt = EntityType.by(entityTag);
                    if (entityTypeOpt.isEmpty()) {
                        this.delay(serverWorld, pos);
                        return;
                    }

                    EntityType<?> entityType = entityTypeOpt.get();

                    // Check if the same entity type is already within 1 block
                    int nearbyEntities = serverWorld.getEntities(
                            EntityTypeTest.forExactClass(entityType.getBaseClass()),
                            new AABB(pos).inflate(1), // 1 block radius
                            EntitySelector.NO_SPECTATORS
                    ).size();

                    if (nearbyEntities >= this.maxNearbyEntities) {
                        this.delay(serverWorld, pos);
                        return;
                    }

                    BlockPos spawnPos;
                    if (hasFacing) {
                        // Spawn only on the designated block face
                        spawnPos = pos.relative(facing);
                    } else {
                        // Default spawner behavior: spawn in a random nearby spot
                        spawnPos = pos.offset(
                                random.nextInt(3) - 1,
                                random.nextInt(3) - 1,
                                random.nextInt(3) - 1
                        );
                    }

                    double x = spawnPos.getX() + 0.5;
                    double y = spawnPos.getY() + 0.5;
                    double z = spawnPos.getZ() + 0.5;

                    if (serverWorld.noCollision(entityType.getSpawnAABB(x, y, z))) {
                        Entity entity = EntityType.loadEntityRecursive(entityTag, serverWorld, e -> {
                            e.moveTo(x, y, z, e.getYRot(), e.getXRot());
                            return e;
                        });

                        if (entity == null) {
                            this.delay(serverWorld, pos);
                            return;
                        }

                        entity.moveTo(x, y, z, random.nextFloat() * 360.0F, 0.0F);

                        if (entity instanceof Mob mob) {
                            if (!EventHooks.checkSpawnPositionSpawner(mob, serverWorld, MobSpawnType.SPAWNER, spawnData, this)) {
                                continue;
                            }

                            EventHooks.finalizeMobSpawnSpawner(mob, serverWorld,
                                    serverWorld.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.SPAWNER, null, this, true);

                            spawnData.getEquipment().ifPresent(mob::equip);
                        }

                        if (!serverWorld.tryAddFreshEntityWithPassengers(entity)) {
                            this.delay(serverWorld, pos);
                            return;
                        }

                        serverWorld.levelEvent(2004, pos, 0);
                        serverWorld.gameEvent(entity, GameEvent.ENTITY_PLACE, spawnPos);
                        if (entity instanceof Mob) {
                            ((Mob) entity).spawnAnim();
                        }

                        spawned = true;
                    }
                }

                if (spawned) {
                    this.delay(serverWorld, pos);
                }
            }
        }
    }

    private void delay(Level world, BlockPos pos) {
        RandomSource randomsource = world.random;
        if (this.maxSpawnDelay <= this.minSpawnDelay) {
            this.spawnDelay = this.minSpawnDelay;
        } else {
            this.spawnDelay = this.minSpawnDelay + randomsource.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
        }

        this.spawnPotentials.getRandom(randomsource).ifPresent(p_337965_ -> this.setNextSpawnData(world, pos, p_337965_.data()));
        this.broadcastEvent(world, pos, 1);
    }

    protected void setNextSpawnData(@Nullable Level world, BlockPos pos, SpawnData data) {
        this.nextSpawnData = data;
    }

    private SpawnData getOrCreateNextSpawnData(@Nullable Level world, RandomSource random, BlockPos pos) {
        if (this.nextSpawnData != null) {
            return this.nextSpawnData;
        } else {
            this.setNextSpawnData(world, pos, this.spawnPotentials.getRandom(random).map(WeightedEntry.Wrapper::data).orElseGet(SpawnData::new));
            return this.nextSpawnData;
        }
    }
}
