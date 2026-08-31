package com.wenxin2.marioverse.client.renderers.blocks.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.LargeStandingArrowSignBlock;
import com.wenxin2.marioverse.blocks.LargeWallArrowSignBlock;
import com.wenxin2.marioverse.blocks.entities.ArrowSignBlockEntity;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import com.wenxin2.marioverse.blocks.states.ArrowDirection;
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

    private static final String[] SPRITE_FOLDERS = {"arrow", "large_arrow"};
    private static final ResourceLocation[][][] SPRITE_LOCATIONS = buildSpriteLocations();

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

        DyeColor dyeColor = animatable.getArrowDyeColor();
        DyeColor resolvedColor = dyeColor != null ? dyeColor : DyeColor.RED;

        ResourceLocation spriteLocation = SPRITE_LOCATIONS[isLarge ? 1 : 0][arrowDirection.ordinal()][resolvedColor.ordinal()];
        TextureAtlasSprite sprite = ArrowAtlas.get().getSprite(spriteLocation);

        RenderType arrowRenderType = RenderType.entityCutout(ArrowAtlas.TEXTURE_LOCATION);
        VertexConsumer arrowBuffer = sprite.wrap(bufferSource.getBuffer(arrowRenderType));
        int arrowPackedLight = animatable.hasGlowingArrow() ? LightTexture.FULL_BRIGHT : packedLight;

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

    private static ResourceLocation[][][] buildSpriteLocations() {
        ArrowDirection[] directions = ArrowDirection.values();
        DyeColor[] colors = DyeColor.values();
        ResourceLocation[][][] table = new ResourceLocation[SPRITE_FOLDERS.length][directions.length][colors.length];

        for (int f = 0; f < SPRITE_FOLDERS.length; f++)
            for (ArrowDirection direction : directions)
                for (DyeColor color : colors)
                    table[f][direction.ordinal()][color.ordinal()] = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID,
                            "entity/signs/" + SPRITE_FOLDERS[f] + "/pattern/" + direction.getSerializedName() + "_" + color.getSerializedName());

        return table;
    }
}
