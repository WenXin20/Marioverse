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
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
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

        if (!world.isClientSide && world.getBlockState(pos).canBeReplaced()
                && coinCount <= ConfigRegistry.MAX_GOLD_KOOPA_SHELL_COINS.get()
                && this.getDeltaMovement().horizontalDistance() > 0.25 && this.isAlive()) {
            world.setBlock(this.blockPosition(), coinState.setValue(BlockStateProperties.WATERLOGGED,
                    fluidState.getType() == Fluids.WATER), 3);
            world.playSound(null, pos, BlockRegistry.COIN.get().getSoundType(coinState, world, pos, null).getPlaceSound(),
                    SoundSource.BLOCKS, 1.0F, 1.0F);
            coinCount++;
        }

        if (coinCount >= ConfigRegistry.MAX_GOLD_KOOPA_SHELL_COINS.get())
            this.kill();

        if (world instanceof ServerLevel serverWorld
                && this.tickCount % 4 == 0 && this.isAlive())
            ServerParticleUtils.spawnSingleParticleOnEntityRandomly(ParticleRegistry.COIN_GLINT.get(), serverWorld, this);
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
}