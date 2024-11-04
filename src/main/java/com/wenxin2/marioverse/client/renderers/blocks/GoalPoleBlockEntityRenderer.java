package com.wenxin2.marioverse.client.renderers.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.blocks.GoalPoleBlock;
import com.wenxin2.marioverse.blocks.entities.GoalPoleBlockEntity;
import com.wenxin2.marioverse.client.models.blocks.CoinBlockModel;
import com.wenxin2.marioverse.client.models.blocks.GoalPoleBlockModel;
import com.wenxin2.marioverse.entities.FireGoombaEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class GoalPoleBlockEntityRenderer extends GeoBlockRenderer<GoalPoleBlockEntity> {
    public GoalPoleBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new GoalPoleBlockModel());
    }

    @Override
    public void render(GoalPoleBlockEntity animatable, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        BlockState state = animatable.getBlockState();
        if (state.getValue(GoalPoleBlock.FLAG)) {
            super.render(animatable, partialTicks, stack, buffer, packedLight, packedOverlay);
            if (animatable.getLevel() != null && state.getValue(GoalPoleBlock.LOWERED)
                    && !animatable.hasPlayedParticle()) {
                this.model.getBone("poof_particle").ifPresent(bone -> {
                    animatable.getLevel().addParticle(ParticleTypes.POOF,
                            bone.getWorldPosition().x, bone.getWorldPosition().y, bone.getWorldPosition().z,
                            0, 0, 0);
                });
                animatable.setHasPlayedParticle(Boolean.TRUE);
            }
        }
    }
}
