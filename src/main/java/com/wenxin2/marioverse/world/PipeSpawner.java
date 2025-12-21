package com.wenxin2.marioverse.world;

import com.mojang.logging.LogUtils;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public abstract class PipeSpawner extends BaseSpawner {
    public static final String SPAWN_DATA_TAG = "SpawnData";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int EVENT_SPAWN = 1;
    private SimpleWeightedRandomList<SpawnData> spawnPotentials = SimpleWeightedRandomList.empty();
    @Nullable private Entity displayEntity;
    @Nullable private SpawnData nextSpawnData;
    private double oSpin;
    private double spin;
    public int maxNearbyEntities = 1;
    private int maxSpawnDelay = 200;
    private int minSpawnDelay = 300;
    private int requiredPlayerRange = 16;
    private int spawnCount = 1;
    private int spawnDelay = 20;
    private int spawnRange = 1;

    public void setEntityId(EntityType<?> entityType, @Nullable Level world, RandomSource random, BlockPos pos) {
        this.getOrCreateNextSpawnData(world, random, pos)
                .getEntityToSpawn()
                .putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString());
    }

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
                Direction facing = state.getOptionalValue(BlockStateProperties.FACING).orElse(Direction.UP);

                for (int i = 0; i < this.spawnCount; i++) {
                    CompoundTag tag = spawnData.getEntityToSpawn();
                    Optional<EntityType<?>> entityTypeOpt = EntityType.by(tag);
                    if (entityTypeOpt.isEmpty()) {
                        this.delay(serverWorld, pos);
                        return;
                    }

                    EntityType<?> entityType = entityTypeOpt.get();
                    int nearbyEntities = serverWorld.getEntities(entityType,
                            new AABB(pos).inflate(1), EntitySelector.NO_SPECTATORS).size();
                    if (nearbyEntities >= this.maxNearbyEntities || entityType.is(TagRegistry.WARP_PIPE_CANNOT_SPAWN)
                            || state.hasProperty(WarpPipeBlock.CLOSED) && state.getValue(WarpPipeBlock.CLOSED)) {
                        this.delay(serverWorld, pos);
                        return;
                    }

                    ListTag listTag = tag.getList("Pos", 6);
                    int j = listTag.size();
                    double x = j >= 1 ? listTag.getDouble(0)
                            : (double) pos.getX() + (random.nextDouble() - random.nextDouble()) * (double) this.spawnRange + 0.5;
                    double y = j >= 2 ? listTag.getDouble(1) : (double) (pos.getY() + random.nextInt(3) - 1);
                    double z = j >= 3 ? listTag.getDouble(2)
                            : (double) pos.getZ() + (random.nextDouble() - random.nextDouble()) * (double) this.spawnRange + 0.5;

                    BlockPos spawnPos;
                    if (state.hasProperty(BlockStateProperties.FACING)) {
                        spawnPos = pos.relative(facing);
                        double entityWidth = entityType.getWidth() / 2.0;
                        double entityHeight = entityType.getHeight();

                        switch (facing) {
                            case UP:
                                x = spawnPos.getX() + 0.5;
                                y = spawnPos.getY() + 0.1;
                                z = spawnPos.getZ() + 0.5;
                                break;
                            case DOWN:
                                x = spawnPos.getX() + 0.5;
                                y = spawnPos.getY() + 1.0 - entityHeight - 0.1;
                                z = spawnPos.getZ() + 0.5;
                                break;
                            case NORTH:
                                x = spawnPos.getX() + 0.5;
                                y = spawnPos.getY();
                                z = spawnPos.getZ() + entityWidth + 0.1;
                                break;
                            case SOUTH:
                                x = spawnPos.getX() + 0.5;
                                y = spawnPos.getY();
                                z = spawnPos.getZ() + entityWidth + 0.1;
                                break;
                            case WEST:
                                x = spawnPos.getX() + entityWidth + 0.1;
                                y = spawnPos.getY();
                                z = spawnPos.getZ() + 0.5;
                                break;
                            case EAST:
                                x = spawnPos.getX() + entityWidth + 0.1;
                                y = spawnPos.getY();
                                z = spawnPos.getZ() + 0.5;
                                break;
                        }
                    } else spawnPos = BlockPos.containing(x, y, z);

                    if (serverWorld.noCollision(entityType.getSpawnAABB(x, y, z))) {
                        double finalX = x;
                        double finalY = y;
                        double finalZ = z;
                        Entity entity = EntityType.loadEntityRecursive(tag, serverWorld, e -> {
                            e.moveTo(finalX, finalY, finalZ, e.getYRot(), e.getXRot());
                            return e;
                        });

                        if (entity == null) {
                            this.delay(serverWorld, pos);
                            return;
                        }

                        entity.moveTo(x, y, z, random.nextFloat() * 360.0F, 0.0F);

                        if (entity instanceof Mob mob) {
                            EventHooks.finalizeMobSpawnSpawner(mob, serverWorld,
                                    serverWorld.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.SPAWNER, null, this, true);

                            spawnData.getEquipment().ifPresent(mob::equip);
                        }

                        if (!serverWorld.tryAddFreshEntityWithPassengers(entity)) {
                            this.delay(serverWorld, pos);
                            return;
                        }
                        serverWorld.gameEvent(entity, GameEvent.ENTITY_PLACE, spawnPos);

                        if (entity instanceof Mob mob)
                            mob.spawnAnim();
                        spawned = true;
                    }
                }

                if (spawned)
                    this.delay(serverWorld, pos);
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

    public void load(@Nullable Level world, BlockPos pos, CompoundTag tag) {
        this.spawnDelay = tag.getShort("Delay");
        boolean hasSpawnData = tag.contains("SpawnData", 10);
        if (hasSpawnData) {
            SpawnData spawndata = SpawnData.CODEC
                    .parse(NbtOps.INSTANCE, tag.getCompound("SpawnData"))
                    .resultOrPartial(warn -> LOGGER.warn("Invalid SpawnData: {}", warn))
                    .orElseGet(SpawnData::new);
            this.setNextSpawnData(world, pos, spawndata);
        }

        boolean hasSpawnPotentials = tag.contains("SpawnPotentials", 9);
        if (hasSpawnPotentials) {
            ListTag listTag = tag.getList("SpawnPotentials", 10);
            this.spawnPotentials = SpawnData.LIST_CODEC
                    .parse(NbtOps.INSTANCE, listTag)
                    .resultOrPartial(warn -> LOGGER.warn("Invalid SpawnPotentials list: {}", warn))
                    .orElseGet(SimpleWeightedRandomList::empty);
        } else this.spawnPotentials = SimpleWeightedRandomList.single(this.nextSpawnData != null
                ? this.nextSpawnData : new SpawnData());

        if (tag.contains("MinSpawnDelay", 99)) {
            this.minSpawnDelay = tag.getShort("MinSpawnDelay");
            this.maxSpawnDelay = tag.getShort("MaxSpawnDelay");
            this.spawnCount = tag.getShort("SpawnCount");
        }

        if (tag.contains("MaxNearbyEntities", 99)) {
            this.maxNearbyEntities = tag.getShort("MaxNearbyEntities");
            this.requiredPlayerRange = tag.getShort("RequiredPlayerRange");
        }

        if (tag.contains("SpawnRange", 99))
            this.spawnRange = tag.getShort("SpawnRange");

        this.displayEntity = null;
    }

    public CompoundTag save(CompoundTag tag) {
        tag.putShort("Delay", (short)this.spawnDelay);
        tag.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
        tag.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
        tag.putShort("SpawnCount", (short)this.spawnCount);
        tag.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
        tag.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
        tag.putShort("SpawnRange", (short)this.spawnRange);
        if (this.nextSpawnData != null) {
            tag.put("SpawnData", SpawnData.CODEC
                    .encodeStart(NbtOps.INSTANCE, this.nextSpawnData)
                    .getOrThrow(warn -> new IllegalStateException("Invalid SpawnData: " + warn))
            );
        }

        tag.put("SpawnPotentials", SpawnData.LIST_CODEC.encodeStart(NbtOps.INSTANCE, this.spawnPotentials).getOrThrow());
        return tag;
    }
}
