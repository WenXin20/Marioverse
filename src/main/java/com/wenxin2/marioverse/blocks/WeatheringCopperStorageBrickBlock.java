package com.wenxin2.marioverse.blocks;

import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.WeatheringCopper;

public class WeatheringCopperStorageBrickBlock extends WeatheringCopperQuestionBlock implements EntityBlock, WeatheringCopper {
    public WeatheringCopperStorageBrickBlock(WeatheringCopper.WeatherState weatherState, Properties properties) {
        super(weatherState, properties);
    }
}
