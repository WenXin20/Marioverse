package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;

public class GoldKoopaShellEntity extends KoopaShellEntity implements CrackableEntity, GeoEntity, TraceableEntity {
    public GoldKoopaShellEntity(EntityType<? extends GoldKoopaShellEntity> type, Level world) {
        super(type, world);
    }
    private int coinCount = 0;

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(0, new HurtByTargetGoal(this).setAlertOthers(GoldKoopaTroopaEntity.class));
    }

    @Override
    public void tick() {
        super.tick();
        Level world = this.level();
        BlockPos pos = this.blockPosition();
        FluidState fluidState = world.getFluidState(pos);
        BlockState coinState = BlockRegistry.COIN.get().defaultBlockState();

        if (!world.isClientSide && this.level().getGameTime() % 4 == 0 && world.getBlockState(pos).canBeReplaced()
                && coinCount <= ConfigRegistry.MAX_GOLD_KOOPA_SHELL_TRAIL_COINS.get()
                && this.getDeltaMovement().length() > 0.25 && this.isAlive()) {
            world.setBlock(this.blockPosition(), coinState.setValue(BlockStateProperties.WATERLOGGED,
                    fluidState.getType() == Fluids.WATER), 3);
            world.playSound(null, pos, BlockRegistry.COIN.get().getSoundType(coinState, world, pos, null).getPlaceSound(),
                    SoundSource.BLOCKS, 1.0F, 1.0F);
            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnParticlesOnBlockFaces(ParticleRegistry.COIN_GLINT.get(), serverWorld, pos, UniformInt.of(1, 1));
            coinCount++;
        }

        if (coinCount >= ConfigRegistry.MAX_GOLD_KOOPA_SHELL_TRAIL_COINS.get()) {
            this.playDeathAnimation(this);
            this.discard();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("CoinCount", this.coinCount);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.coinCount = tag.getInt("CoinCount");
    }

    @NotNull
    @Override
    public SimpleParticleType getShatterParticle() {
        return ParticleRegistry.GOLD_KOOPA_SHELL_SHATTER.get();
    }

    @NotNull
    @Override
    public KoopaTroopaEntity getKoopaTroopaEntity() {
        return new GoldKoopaTroopaEntity(EntityRegistry.GOLD_KOOPA_TROOPA.get(), this.level());
    }

    @Override
    public TagKey<EntityType<?>> getInstakillEntityTag() {
        return TagRegistry.GOLD_KOOPA_SHELL_CAN_INSTAKILL;
    }

    @Override
    public float getShellDamage() {
        return ConfigRegistry.GOLD_KOOPA_SHELL_DAMAGE.get().floatValue();
    }

    @Override
    public void playDeathAnimation(Entity entity) {
        super.playDeathAnimation(entity);

        if (!this.level().isClientSide()) {
            BlockPos center = this.blockPosition();
            this.placeCoinCircle(this.level(), center);
        }
    }

    @Override
    public void trailParticles(BlockState state, BlockPos pos, double x, double z, Vec3 vec3) {
        super.trailParticles(state, pos, x, z, vec3);
        if (this.level().getGameTime() % 4 == 0)
            this.level().addParticle(ParticleRegistry.COIN_GLINT.get(), x, this.getY() + 0.1, z, vec3.x * -4.0, 1.5, vec3.z * -4.0);
    }

    public void placeCoinCircle(Level world, BlockPos center) {
        int radius = ConfigRegistry.GOLD_KOOPA_SHELL_COIN_CIRCLE_RADIUS.get();
        int coinCount = ConfigRegistry.MAX_GOLD_KOOPA_SHELL_CIRCLE_COINS.get();

        for (int i = 0; i < coinCount; i++) {
            double angle = 2 * Math.PI * i / coinCount;
            int x = center.getX() + (int) Math.round(radius * Math.cos(angle));
            int z = center.getZ() + (int) Math.round(radius * Math.sin(angle));
            BlockPos basePos = new BlockPos(x, center.getY(), z);

            BlockPos coinPos = this.findValidCoinPosition(world, basePos);
            BlockState coinState = world.getBlockState(coinPos);
            if (coinState.canBeReplaced() || coinState.getFluidState().is(FluidTags.WATER) || coinState.getBlock() == BlockRegistry.COIN.get()) {
                world.setBlock(coinPos, BlockRegistry.COIN.get().defaultBlockState()
                        .setValue(BlockStateProperties.WATERLOGGED, coinState.getFluidState().is(FluidTags.WATER)), 3);
                world.playSound(null, coinPos, BlockRegistry.COIN.get().getSoundType(coinState, world, coinPos, null).getPlaceSound(),
                        SoundSource.BLOCKS, 1.0F, 1.0F);
                if (world instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnParticlesOnBlockFaces(ParticleRegistry.COIN_GLINT.get(), serverWorld, coinPos, UniformInt.of(1, 1));
            }
        }
    }

    private BlockPos findValidCoinPosition(Level world, BlockPos basePos) {
        BlockPos pos = basePos;
        BlockState state = world.getBlockState(pos);
        boolean isAirOrWater = state.canBeReplaced() || state.getFluidState().is(FluidTags.WATER) || state.getBlock() == BlockRegistry.COIN.get();

        if (isAirOrWater) {
            return pos;
        }

        for (int i = 1; i <= 3; i++) {
            BlockPos posAbove = pos.above(i);
            BlockState stateAbove = world.getBlockState(posAbove);
            if (stateAbove.canBeReplaced() || stateAbove.getFluidState().is(FluidTags.WATER) || stateAbove.getBlock() == BlockRegistry.COIN.get()) {
                pos = posAbove;
                break;
            }
        }
        return pos;
    }
}