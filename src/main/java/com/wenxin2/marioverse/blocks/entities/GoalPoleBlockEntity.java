package com.wenxin2.marioverse.blocks.entities;

import com.wenxin2.marioverse.blocks.GoalPoleBlock;
import com.wenxin2.marioverse.blocks.states.ColumnBlockStates;
import com.wenxin2.marioverse.init.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.util.GeckoLibUtil;

public class GoalPoleBlockEntity extends BlockEntity implements GeoBlockEntity {
    protected static final RawAnimation WAVE_ANIM = RawAnimation.begin().thenLoop("animation.goal_pole.wave");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean hasPlayedParticle = false;

    public GoalPoleBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.GOAL_POLE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, this::waveAnimController));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    protected <E extends GeoAnimatable> PlayState waveAnimController(final AnimationState<E> state) {
        state.getController().setAnimation(WAVE_ANIM);
        return PlayState.CONTINUE;
    }

    public boolean hasPlayedParticle() {
        return hasPlayedParticle;
    }

    public void setHasPlayedParticle(boolean hasPlayedParticle) {
        this.hasPlayedParticle = hasPlayedParticle;
    }
}
