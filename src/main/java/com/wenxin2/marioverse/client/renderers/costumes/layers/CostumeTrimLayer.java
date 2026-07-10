package com.wenxin2.marioverse.client.renderers.costumes.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.client.renderers.costumes.DyeableCostumeRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class CostumeTrimLayer<T extends Item & GeoAnimatable>
        extends GeoRenderLayer<T> {

    public CostumeTrimLayer(GeoRenderer<T> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel,
                       RenderType renderType, MultiBufferSource bufferSource,
                       VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {
        DyeableCostumeRenderer renderer = (DyeableCostumeRenderer) this.getRenderer();
        ItemStack stack = renderer.getCurrentStack();
        if (stack == null || stack.isEmpty()) return;

        ArmorTrim trim = stack.get(DataComponents.TRIM);
        if (trim == null) return;
        Holder<ArmorMaterial> material = ArmorMaterials.LEATHER;

        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getModelManager()
                .getAtlas(Sheets.ARMOR_TRIMS_SHEET)
                .getSprite(renderer.getCurrentSlot() == EquipmentSlot.LEGS
                        ? trim.innerTexture(material)
                        : trim.outerTexture(material));
        RenderType type = Sheets.armorTrimsSheet(trim.pattern().value().decal());

        VertexConsumer consumer = sprite.wrap(bufferSource.getBuffer(type));
        this.getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, type,
                consumer, partialTick, packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
    }
}