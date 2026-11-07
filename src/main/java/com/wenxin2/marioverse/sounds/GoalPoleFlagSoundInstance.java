package com.wenxin2.marioverse.sounds;

import com.wenxin2.marioverse.blocks.GoalPoleBlock;
import com.wenxin2.marioverse.blocks.entities.GoalPoleBlockEntity;
import com.wenxin2.marioverse.blocks.states.ColumnBlockStates;
import com.wenxin2.marioverse.registries.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GoalPoleFlagSoundInstance extends AbstractTickableSoundInstance {
    private static final float FADE_TICKS = 3.0F;
    private final GoalPoleBlockEntity blockEntity;
    private final int animId;
    private final float endTick;

    public GoalPoleFlagSoundInstance(GoalPoleBlockEntity blockEntity, SoundEvent soundEvent, RandomSource random, float endTick) {
        super(soundEvent, SoundSource.BLOCKS, random);
        this.blockEntity = blockEntity;
        this.animId = blockEntity.getClientFlagAnimId();
        this.endTick = endTick;
        this.attenuation = Attenuation.LINEAR;
        this.looping = true;
        this.relative = false;
        this.delay = 0;
        this.volume = 1.0F;
        this.pitch = 0.9F + random.nextFloat() * 0.2F;
        this.updatePosition();
    }

    @Override
    public void tick() {
        if (this.blockEntity.isRemoved() || this.blockEntity.getClientFlagAnimId() != this.animId) {
            this.stop();
            return;
        }

        float remaining = this.endTick - this.blockEntity.getFlagElapsed(0.0F);
        if (remaining <= 0.0F) {
            this.stop();
            return;
        }

        this.volume = Mth.clamp(remaining / FADE_TICKS, 0.0F, 1.0F);
        this.updatePosition();
    }

    public static void tickFlagSounds(GoalPoleBlockEntity blockEntity) {
        if (blockEntity.getBlockState().getValue(GoalPoleBlock.COLUMN) == ColumnBlockStates.MIDDLE)
            return;
        float elapsed = blockEntity.getFlagElapsed(0.0F);
        GoalPoleFlagSoundInstance.tryPlayPhase(blockEntity, 0, 0.0F, blockEntity.getFlagPreMoveTicks(),
                blockEntity.isFlagPreMoveLowering(), elapsed);
        GoalPoleFlagSoundInstance.tryPlayPhase(blockEntity, 1, blockEntity.getFlagMainMoveStart(), blockEntity.getFlagMainMoveTicks(),
                blockEntity.isFlagMainMoveLowering(), elapsed);
    }

    private void updatePosition() {
        this.x = this.blockEntity.getBlockPos().getX() + 0.5;
        this.y = this.blockEntity.getBlockPos().getY() - this.blockEntity.getFlagOffset(0.0F);
        this.z = this.blockEntity.getBlockPos().getZ() + 0.5;
    }

    private static void tryPlayPhase(GoalPoleBlockEntity blockEntity, int phase, float start, float ticks, boolean lowering, float elapsed) {
        if (ticks <= 0.0F || elapsed < start || !blockEntity.claimFlagSoundPhase(phase) || elapsed >= start + ticks)
            return;

        SoundEvent sound;
        if (lowering)
            sound = SoundRegistry.GOAL_POLE_FINISH.get();
        else if (blockEntity.isFlagResetting())
            sound = SoundRegistry.GOAL_POLE_RESET.get();
        else return;

        Minecraft.getInstance().getSoundManager().play(new GoalPoleFlagSoundInstance(blockEntity, sound,
                RandomSource.create(), start + ticks));
    }
}
