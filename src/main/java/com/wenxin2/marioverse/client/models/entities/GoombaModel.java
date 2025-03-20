package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.GoombaEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.loading.json.raw.Bone;
import software.bernie.geckolib.model.GeoModel;

public class GoombaModel extends GeoModel<GoombaEntity> {
    public GoombaModel() {
        super();
    }

    @Override
    public RenderType getRenderType(GoombaEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }

    @Override
    public ResourceLocation getModelResource(GoombaEntity animatable) {
        if (animatable.hasCustomName() && ("Goombella".equals(ChatFormatting.stripFormatting(animatable.getName().getString()))
                || "goombella".equals(ChatFormatting.stripFormatting(animatable.getName().getString())))) {
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/entity/goomba/goombella.geo.json");
        } else return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/entity/goomba/goomba.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GoombaEntity animatable) {
        if (animatable.hasCustomName() && ("Goombella".equals(ChatFormatting.stripFormatting(animatable.getName().getString()))
                || "goombella".equals(ChatFormatting.stripFormatting(animatable.getName().getString())))) {
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/goomba/goombella.png");
        } else return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/goomba/goomba.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GoombaEntity animatable) {
        if (animatable.hasCustomName() && ("Goombella".equals(ChatFormatting.stripFormatting(animatable.getName().getString()))
                || "goombella".equals(ChatFormatting.stripFormatting(animatable.getName().getString())))) {
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/entity/goomba/goombella.animation.json");
        } else return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/entity/goomba/goomba.animation.json");
    }

    @Override
    public void setCustomAnimations(GoombaEntity entity, long instanceId, AnimationState<GoombaEntity> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);

        var controller = animationState.getController();

        if (controller.getCurrentRawAnimation() != null) {
            if (controller.getCurrentRawAnimation().equals(GoombaEntity.SLEEP_ANIM) ) {
                if (controller.hasAnimationFinished()) {
                    spawnParticleAtBone(entity, "bubble_pop");
                }
            }
        }
    }

    private void spawnParticleAtBone(GoombaEntity entity, String boneName) {
        if (entity.level().isClientSide) {
            Vec3 bonePos = getBonePosition(entity, boneName);

            if (bonePos != null) {
                entity.level().addParticle(ParticleTypes.BUBBLE_POP,
                        bonePos.x, bonePos.y, bonePos.z,
                        (entity.getRandom().nextDouble() - 0.5) * 0.1, 0.1,
                        (entity.getRandom().nextDouble() - 0.5) * 0.1);
            }
        }
    }

    private Vec3 getBonePosition(GoombaEntity entity, String boneName) {
        GeoBone bone = getAnimationProcessor().getBone(boneName);

        if (bone != null) {
            Vec3 offset = new Vec3(bone.getPosX(), bone.getPosY(), bone.getPosZ());
            Vec3 entityPos = entity.position(); // Get entity world position

            // Adjust for entity rotation
            return entityPos.add(offset.yRot((float) Math.toRadians(-entity.getYRot())));
        }
        return null;
    }
}
