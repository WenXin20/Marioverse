package com.wenxin2.marioverse.client.renderers.blocks.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.LargeStandingArrowSignBlock;
import com.wenxin2.marioverse.blocks.LargeWallArrowSignBlock;
import com.wenxin2.marioverse.blocks.entities.ArrowSignBlockEntity;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import com.wenxin2.marioverse.client.ArrowAtlas;
import com.wenxin2.marioverse.client.renderers.blocks.ArrowSignBlockEntityRenderer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class ArrowOverlayGeoLayer extends GeoRenderLayer<ArrowSignBlockEntity> {
    private record BoneHideInfo(List<GeoBone> othersToHide, Set<GeoBone> arrowAncestors) {}
    private static final Map<GeoBone, BoneHideInfo> HIDE_INFO_CACHE = new IdentityHashMap<>();

    public ArrowOverlayGeoLayer(GeoRenderer<ArrowSignBlockEntity> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, ArrowSignBlockEntity animatable, BakedGeoModel bakedModel,
                        RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                        float partialTick, int packedLight, int packedOverlay) {
        BlockState state = animatable.getBlockState();
        var arrowDirection = state.getValue(BlockStatePropertyRegistry.ARROW_DIRECTION);

        if (arrowDirection.getSerializedName().equals("none"))
            return;
        Optional<GeoBone> arrowBone = bakedModel.getBone("arrow");
        if (arrowBone.isEmpty())
            return;

        if (!ArrowSignBlockEntityRenderer.isPrimaryPart(state))
            return;

        boolean isLarge = state.getBlock() instanceof LargeStandingArrowSignBlock
                || state.getBlock() instanceof LargeWallArrowSignBlock;
        String textureFolder = isLarge ? "large_arrow" : "arrow";

        DyeColor dyeColor = animatable.getArrowDyeColor();
        String colorName = (dyeColor != null ? dyeColor : DyeColor.RED).getSerializedName();

        // Sprite baked in-game by the "arrow" atlas' paletted_permutations source (see ArrowAtlasGen),
        // recoloring the shared grayscale pattern per dye color - no per-color art.
        ResourceLocation spriteLocation = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID,
                "entity/signs/" + textureFolder + "/pattern/" + arrowDirection.getSerializedName() + "_" + colorName);
        TextureAtlasSprite sprite = ArrowAtlas.get().getSprite(spriteLocation);

        RenderType arrowRenderType = RenderType.entityCutout(ArrowAtlas.TEXTURE_LOCATION);
        VertexConsumer arrowBuffer = sprite.wrap(bufferSource.getBuffer(arrowRenderType));
        int arrowPackedLight = animatable.hasGlowingArrow() ? LightTexture.FULL_BRIGHT : packedLight;

        // Hide every other bone so reRender only draws "arrow". setHidden(true) also hides children,
        // so ancestors of "arrow" need setChildrenHidden(false) after to keep it reachable.
        BoneHideInfo hideInfo = HIDE_INFO_CACHE.computeIfAbsent(arrowBone.get(),
                arrow -> computeHideInfo(bakedModel, arrow));

        List<GeoBone> temporarilyHidden = new ArrayList<>();
        for (GeoBone bone : hideInfo.othersToHide()) {
            if (!bone.isHidden()) {
                bone.setHidden(true);
                if (hideInfo.arrowAncestors().contains(bone))
                    bone.setChildrenHidden(false);
                temporarilyHidden.add(bone);
            }
        }

        // ArrowSignBlockModel keeps "arrow" hidden during the normal pass - reveal it just for this call.
        boolean arrowWasHidden = arrowBone.get().isHidden();
        arrowBone.get().setHidden(false);

        this.getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, arrowRenderType,
                arrowBuffer, partialTick, arrowPackedLight, packedOverlay, -1);

        arrowBone.get().setHidden(arrowWasHidden);
        for (GeoBone bone : temporarilyHidden) {
            bone.setHidden(false);
        }
    }

    private static BoneHideInfo computeHideInfo(BakedGeoModel bakedModel, GeoBone arrow) {
        Set<GeoBone> arrowAncestors = new HashSet<>();
        for (GeoBone ancestor = arrow.getParent(); ancestor != null; ancestor = ancestor.getParent())
            arrowAncestors.add(ancestor);

        List<GeoBone> allBones = new ArrayList<>();
        collectBones(bakedModel.topLevelBones(), allBones);
        allBones.remove(arrow);

        return new BoneHideInfo(allBones, arrowAncestors);
    }

    private static void collectBones(List<GeoBone> bones, List<GeoBone> out) {
        for (GeoBone bone : bones) {
            out.add(bone);
            collectBones(bone.getChildBones(), out);
        }
    }
}
