package com.wenxin2.marioverse.client.models.geometry;

import com.mojang.math.Transformation;
import com.wenxin2.marioverse.Marioverse;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.CompositeModel;
import net.neoforged.neoforge.client.model.DynamicFluidContainerModel;
import net.neoforged.neoforge.client.model.SimpleModelState;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.StandaloneGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SolidBucketGeometry implements IUnbakedGeometry<SolidBucketGeometry> {
    private static final Transformation SOLID_TRANSFORM = new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1.02F), new Quaternionf());
    private static final Transformation COVER_TRANSFORM = new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1.04F), new Quaternionf());
    private final ResourceLocation block;

    public SolidBucketGeometry(ResourceLocation block) {
        this.block = block;
    }

    @NotNull
    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material,
            TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
        TextureAtlasSprite baseSprite = spriteGetter.apply(context.getMaterial("base"));
        TextureAtlasSprite blockSprite = spriteGetter.apply(this.getBlockMaterial(baker));
        RenderTypeGroup renderTypes = DynamicFluidContainerModel.getLayerRenderTypes(false);

        IGeometryBakingContext itemContext = StandaloneGeometryBakingContext.builder(context).withGui3d(false)
                .withUseBlockLight(false).build(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "solid_bucket"));
        CompositeModel.Baked.Builder modelBuilder = CompositeModel.Baked.builder(itemContext, baseSprite, overrides, context.getTransforms());

        modelBuilder.addQuads(renderTypes, UnbakedGeometryHelper.bakeElements(
                UnbakedGeometryHelper.createUnbakedItemElements(0, baseSprite), material -> baseSprite, modelState));

        if (context.hasMaterial("solid")) {
            TextureAtlasSprite maskSprite = spriteGetter.apply(context.getMaterial("solid"));
            modelBuilder.addQuads(renderTypes, UnbakedGeometryHelper.bakeElements(
                    UnbakedGeometryHelper.createUnbakedItemMaskElements(1, maskSprite), material -> blockSprite,
                    new SimpleModelState(modelState.getRotation().compose(SOLID_TRANSFORM), modelState.isUvLocked())));
        }

        if (context.hasMaterial("cover")) {
            TextureAtlasSprite coverSprite = spriteGetter.apply(context.getMaterial("cover"));
            modelBuilder.addQuads(renderTypes, UnbakedGeometryHelper.bakeElements(
                    UnbakedGeometryHelper.createUnbakedItemMaskElements(2, coverSprite), material -> baseSprite,
                    new SimpleModelState(modelState.getRotation().compose(COVER_TRANSFORM), modelState.isUvLocked())));
        }

        modelBuilder.setParticle(blockSprite);
        return modelBuilder.build();
    }

    private Material getBlockMaterial(ModelBaker baker) {
        UnbakedModel model = baker.getModel(this.block.withPrefix("block/"));

        if (model instanceof BlockModel blockModel && blockModel.hasTexture("particle")) {
            Material material = blockModel.getMaterial("particle");
            if (!material.texture().equals(MissingTextureAtlasSprite.getLocation()))
                return material;
        }
        return new Material(InventoryMenu.BLOCK_ATLAS, this.block.withPrefix("block/"));
    }
}
