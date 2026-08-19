package com.wenxin2.marioverse.client.renderers.entities;

import com.mojang.datafixers.util.Pair;
import com.wenxin2.marioverse.Marioverse;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.NotNull;

public class WoodTypeBoatRenderer extends BoatRenderer {
    public static final ModelLayerLocation MUSHROOT_BOAT_LAYER =
            new ModelLayerLocation(Marioverse.id("boat/mushroot"), "main");
    public static final ModelLayerLocation MUSHROOT_CHEST_BOAT_LAYER =
            new ModelLayerLocation(Marioverse.id("chest_boat/mushroot"), "main");
    private final Pair<ResourceLocation, ListModel<Boat>> boatResource;

    public WoodTypeBoatRenderer(EntityRendererProvider.Context context, boolean chestBoat, ModelLayerLocation modelLayer, ResourceLocation texture) {
        super(context, chestBoat);
        ModelPart modelPart = context.bakeLayer(modelLayer);
        ListModel<Boat> model = chestBoat ? new ChestBoatModel(modelPart) : new BoatModel(modelPart);
        this.boatResource = Pair.of(texture, model);
    }

    @NotNull
    @Override
    public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat boat) {
        return this.boatResource;
    }
}
