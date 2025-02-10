package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class WeatheringCopperQuestionBlock extends QuestionBlock implements EntityBlock, WeatheringCopper {
    public static final MapCodec<WeatheringCopperQuestionBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state")
                            .forGetter(ChangeOverTimeBlock::getAge), propertiesCodec())
                    .apply(instance, WeatheringCopperQuestionBlock::new)
    );
    private final WeatheringCopper.WeatherState weatherState;

    @NotNull
    @Override
    public MapCodec<WeatheringCopperQuestionBlock> codec() {
        return CODEC;
    }

    public WeatheringCopperQuestionBlock(WeatheringCopper.WeatherState weatherState, BlockBehaviour.Properties properties) {
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

    @Override
    public void onRemove(BlockState oldState, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        boolean isOxidizing = WeatheringCopper.getNext(oldState.getBlock()).isPresent();
        boolean isScraping = WeatheringCopper.getPrevious(oldState.getBlock()).isPresent();

        if (!oldState.is(newState.getBlock()) && !isOxidizing && !isScraping) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof QuestionBlockEntity questionBlock)
                Containers.dropContents(world, pos, questionBlock);
        }

        if (!isOxidizing && !isScraping || newState.isAir())
            super.onRemove(oldState, world, pos, newState, isMoving);
    }
}