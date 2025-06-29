package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.KoopaShellEntity;
import com.wenxin2.marioverse.registries.EntityRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Crackiness;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class KoopaShellModel extends GeoModel<KoopaShellEntity> {
    public KoopaShellModel() {
        super();
    }

    @Override
    public RenderType getRenderType(KoopaShellEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }

    @Override
    public ResourceLocation getModelResource(KoopaShellEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/entity/koopa_shell/koopa_shell.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KoopaShellEntity animatable) {
        if (animatable.getType() == EntityRegistry.GOLD_KOOPA_SHELL.get())
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/koopa_troopa/gold_koopa_troopa" + getCrackiness(animatable));
        else if (animatable.getType() == EntityRegistry.RED_KOOPA_SHELL.get())
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/koopa_troopa/red_koopa_troopa" + getCrackiness(animatable));
        else return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/koopa_troopa/green_koopa_troopa" + getCrackiness(animatable));
    }

    private static @NotNull String getCrackiness(KoopaShellEntity animatable) {
        if (animatable.getCrackiness() == Crackiness.Level.LOW)
            return  "_crackiness_low.png";
        else if (animatable.getCrackiness() == Crackiness.Level.MEDIUM)
            return  "_crackiness_medium.png";
        else if (animatable.getCrackiness() == Crackiness.Level.HIGH)
            return  "_crackiness_high.png";
        else return ".png";
    }

    @Override
    public ResourceLocation getAnimationResource(KoopaShellEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/entity/koopa_shell/koopa_shell.animation.json");
    }

    @Override
    public void setCustomAnimations(KoopaShellEntity animatable, long instanceId, AnimationState<KoopaShellEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        GeoBone head = this.getAnimationProcessor().getBone("bipedHeadBaseRotater");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * 0.017453292F);
            head.setRotY(entityData.netHeadYaw() * 0.017453292F);
        }
    }
}
