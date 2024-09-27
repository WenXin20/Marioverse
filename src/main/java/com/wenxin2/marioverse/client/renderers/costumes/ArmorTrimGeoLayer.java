package com.wenxin2.marioverse.client.renderers.costumes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class ArmorTrimGeoLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {

    public ArmorTrimGeoLayer(GeoRenderer<T> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType,
                       MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        if (animatable instanceof ArmorItem armorItem) {
            ItemStack stack = new ItemStack(armorItem);
            ArmorTrim armorTrim = stack.get(DataComponents.TRIM);
            if (armorTrim != null)
                this.renderTrim(armorItem.getMaterial(), poseStack, bufferSource, packedLight,
                        armorTrim, /*new Model() ,*/ this.usesInnerModel(armorItem.getEquipmentSlot()));
        }
    }

    private void renderTrim(Holder<ArmorMaterial> armorMaterial, PoseStack poseStack, MultiBufferSource bufferSource,
                            int i, ArmorTrim armorTrim, /*Model model,*/ boolean humanoidModel) {
        TextureAtlasSprite textureatlassprite = ModelManager.getAtlas(Sheets.ARMOR_TRIMS_SHEET)
                .getSprite(humanoidModel ? armorTrim.innerTexture(armorMaterial) : armorTrim.outerTexture(armorMaterial));
        VertexConsumer vertexconsumer = textureatlassprite.wrap(bufferSource.getBuffer(Sheets.armorTrimsSheet(armorTrim.pattern().value().decal())));
//        model.renderToBuffer(poseStack, vertexconsumer, i, OverlayTexture.NO_OVERLAY);
    }

    private boolean usesInnerModel(EquipmentSlot slot) {
        return slot == EquipmentSlot.LEGS;
    }
}
