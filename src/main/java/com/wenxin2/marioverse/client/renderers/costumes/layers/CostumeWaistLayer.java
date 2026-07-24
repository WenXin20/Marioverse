package com.wenxin2.marioverse.client.renderers.costumes.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.wenxin2.marioverse.client.renderers.costumes.CostumeTextureAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class CostumeWaistLayer<T extends Item & GeoAnimatable> extends GeoRenderLayer<T> {
    public CostumeWaistLayer(GeoRenderer<T> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType,
                       MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {
        CostumeTextureAccess renderer = (CostumeTextureAccess) this.getRenderer();
        ItemStack stack = renderer.getCurrentStack();
        LivingEntity wearer = renderer.getCurrentWearer();

        if (wearer == null || !CostumeWaistLayer.wearerHasWaistBone(wearer))
            return;
        if (stack == null || stack.isEmpty() || renderer.getCurrentSlot() != EquipmentSlot.LEGS)
            return;
        if (bakedModel.getBone("armorWaist").isEmpty())
            return;

        int dyeColour = renderer.getDyeColor();

        this.renderBone(poseStack, animatable, bakedModel, bufferSource, partialTick, packedLight, packedOverlay,
                "armorWaist", renderer.getWaistTextureLocation(), dyeColour);
        this.renderBone(poseStack, animatable, bakedModel, bufferSource, partialTick, packedLight, packedOverlay,
                "armorDress", renderer.getDressTextureLocation(), dyeColour);
        this.renderBone(poseStack, animatable, bakedModel, bufferSource, partialTick, packedLight, packedOverlay,
                "armorDressOverlay", renderer.getDressOverlayTextureLocation(), dyeColour);
        this.renderBone(poseStack, animatable, bakedModel, bufferSource, partialTick, packedLight, packedOverlay,
                "armorDressBack", renderer.getDressBackTextureLocation(), dyeColour);
        this.renderTrim(poseStack, animatable, bakedModel, bufferSource, stack, partialTick, packedLight);
    }

    private void renderBone(PoseStack poseStack, T animatable, BakedGeoModel bakedModel,
                            MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay,
                            String boneName, ResourceLocation texture, int colour) {
        RenderType type = RenderType.entityCutoutNoCull(texture);
        VertexConsumer consumer = bufferSource.getBuffer(type);

        bakedModel.getBone(boneName).ifPresent(bone -> {
            boolean wasHidden = bone.isHidden();
            bone.setHidden(false);
            poseStack.pushPose();
                poseStack.mulPose(Axis.XP.rotationDegrees(180));
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
                poseStack.translate(0, -1.5, 0);

                this.getRenderer().renderRecursively(poseStack, animatable, bone, type, bufferSource,
                        consumer, false, partialTick, packedLight, packedOverlay, colour);

                ResourceLocation overlayTexture = this.deriveOverlayPath(texture);
                RenderType overlayType = RenderType.entityCutoutNoCull(overlayTexture);
                this.getRenderer().renderRecursively(poseStack, animatable, bone, overlayType, bufferSource,
                        bufferSource.getBuffer(overlayType), false, partialTick, packedLight, packedOverlay, 0xFFFFFFFF);
            poseStack.popPose();
            bone.setHidden(wasHidden);
        });
    }

    private ResourceLocation deriveOverlayPath(ResourceLocation base) {
        String path = base.getPath();
        String overlayPath = path.endsWith(".png")
                ? path.substring(0, path.length() - 4) + "_overlay.png"
                : path + "_overlay";
        return ResourceLocation.fromNamespaceAndPath(base.getNamespace(), overlayPath);
    }

    private void renderTrim(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, MultiBufferSource bufferSource,
                            ItemStack stack, float partialTick, int packedLight) {
        ArmorTrim trim = stack.get(DataComponents.TRIM);
        if (trim == null)
            return;

        Holder<ArmorMaterial> material = ArmorMaterials.LEATHER;
        TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager()
                .getAtlas(Sheets.ARMOR_TRIMS_SHEET).getSprite(trim.innerTexture(material));
        RenderType type = Sheets.armorTrimsSheet(trim.pattern().value().decal());

        bakedModel.getBone("armorWaist").ifPresent(bone -> {
            boolean wasHidden = bone.isHidden();
            bone.setHidden(false);

            poseStack.pushPose();
                poseStack.mulPose(Axis.XP.rotationDegrees(180));
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
                poseStack.translate(0, -1.5, 0);

                this.getRenderer().renderRecursively(poseStack, animatable, bone, type, bufferSource,
                        sprite.wrap(bufferSource.getBuffer(type)), false, partialTick, packedLight,
                        OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
            poseStack.popPose();

            bone.setHidden(wasHidden);
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static boolean wearerHasWaistBone(LivingEntity wearer) {
        EntityRenderer<?> entityRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(wearer);

        if (!(wearer instanceof GeoAnimatable animatable))
            return true;
        if (!(entityRenderer instanceof GeoRenderer geoRenderer))
            return true;

        try {
            GeoModel model = geoRenderer.getGeoModel();
            ResourceLocation modelLocation = model.getModelResource(animatable, geoRenderer);
            BakedGeoModel wearerBakedModel = model.getBakedModel(modelLocation);
            return wearerBakedModel.getBone("armorWaist").isPresent();
        } catch (Exception e) {
            return false;
        }
    }
}