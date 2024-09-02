package com.wenxin2.marioverse.sounds;

import com.wenxin2.marioverse.blocks.PipeBubblesBlock;
import com.wenxin2.marioverse.init.BlockRegistry;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AmbientSoundHandler;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)

public class PipeBubblesSoundHandler implements AmbientSoundHandler {
    private final LocalPlayer player;
    private boolean wasInBubbleColumn;
    private boolean firstTick = true;

    public PipeBubblesSoundHandler(LocalPlayer player) {
        this.player = player;
    }

    public void tick() {
        Level world = this.player.level();
        BlockState stateLoaded = world.getBlockStatesIfLoaded(this.player.getBoundingBox()
                .inflate(0.0D, (double)-0.4F, 0.0D).deflate(1.0E-6D)).filter((state) -> {
            return state.is(BlockRegistry.PIPE_BUBBLES.get());
        }).findFirst().orElse(null);
        if (stateLoaded != null) {
            if (!this.wasInBubbleColumn && !this.firstTick && !this.player.isSpectator()
                    && (stateLoaded.is(BlockRegistry.PIPE_BUBBLES.get()) || stateLoaded.is(BlockRegistry.WATER_SPOUT.get()))) {
                boolean flag = stateLoaded.getValue(PipeBubblesBlock.DRAG_DOWN);
                if (flag) {
                    this.player.playSound(SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_INSIDE, 1.0F, 1.0F);
                } else {
                    this.player.playSound(SoundEvents.BUBBLE_COLUMN_UPWARDS_INSIDE, 1.0F, 1.0F);
                }
            }

            this.wasInBubbleColumn = true;
        } else {
            this.wasInBubbleColumn = false;
        }

        this.firstTick = false;
    }

    public static void init()
    {}
}
