package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class WeatheringCopperInvisibleQuestionBlock extends InvisibleQuestionBlock implements EntityBlock, SimpleWaterloggedBlock, WeatheringCopper {
    public static final MapCodec<WeatheringCopperInvisibleQuestionBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state")
                            .forGetter(ChangeOverTimeBlock::getAge), propertiesCodec())
                    .apply(instance, WeatheringCopperInvisibleQuestionBlock::new)
    );
    private final WeatheringCopper.WeatherState weatherState;

    @NotNull
    @Override
    public MapCodec<WeatheringCopperInvisibleQuestionBlock> codec() {
        return CODEC;
    }

    public WeatheringCopperInvisibleQuestionBlock(WeatheringCopper.WeatherState weatherState, BlockBehaviour.Properties properties) {
        super(properties);
        this.weatherState = weatherState;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel serverWorld, BlockPos pos, RandomSource random) {
        this.changeOverTime(state, serverWorld, pos, random);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return WeatheringCopper.getNext(state.getBlock()).isPresent();
    }

    @NotNull
    public WeatheringCopper.WeatherState getAge() {
        return this.weatherState;
    }
}