package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.DryBonesPartEntity;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DryBonesPartModel extends GeoModel<DryBonesPartEntity> {
    public DryBonesPartModel() {
        super();
    }

    @Override
    public RenderType getRenderType(DryBonesPartEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }

    @Override
    public ResourceLocation getModelResource(DryBonesPartEntity animatable) {
        if (animatable.getType() == EntityRegistry.DRY_BONES_HEAD.get())
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/entity/dry_bones/dry_bones_head.geo.json");
        else if (animatable.getType() == EntityRegistry.DRY_BONES_SHELL.get())
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/entity/dry_bones/dry_bones_shell.geo.json");
        else if (animatable.getType() == EntityRegistry.DRY_BONES_LEFT_ARM.get())
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/entity/dry_bones/dry_bones_left_arm.geo.json");
        else if (animatable.getType() == EntityRegistry.DRY_BONES_LEFT_LEG.get())
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/entity/dry_bones/dry_bones_left_leg.geo.json");
        else if (animatable.getType() == EntityRegistry.DRY_BONES_RIGHT_ARM.get())
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/entity/dry_bones/dry_bones_right_arm.geo.json");
        else if (animatable.getType() == EntityRegistry.DRY_BONES_RIGHT_LEG.get())
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/entity/dry_bones/dry_bones_right_leg.geo.json");
        else return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/entity/dry_bones/dry_bones_tail.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DryBonesPartEntity animatable) {
        if (animatable.getData(DataAttachmentRegistry.DEATH_DURATION) == 0)
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/dry_bones/dry_bones.png");
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/dry_bones/dry_bones_dead.png");
    }

    @Override
    public ResourceLocation getAnimationResource(DryBonesPartEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/entity/dry_bones/dry_bones_head.animation.json");
    }
}
