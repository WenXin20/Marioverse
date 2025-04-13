package com.wenxin2.marioverse.client.renderers.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wenxin2.marioverse.registries.TagRegistry;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.data.SlotTypeLoader;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.ItemArmorGeoLayer;

//TODO: Fix this
public class AccessoryGeoArmorLayer<T extends LivingEntity & GeoAnimatable> extends ItemArmorGeoLayer<T> {
    public AccessoryGeoArmorLayer(GeoRenderer<T> entityRenderer) {
        super(entityRenderer);
    }

    @NotNull
    @Override
    protected EquipmentSlot getEquipmentSlotForBone(GeoBone bone, ItemStack stack, T animatable) {
        AccessoriesCapability capability = AccessoriesCapability.get(animatable);
        if (capability != null) {
            AccessoriesContainer hat = capability.getContainer(SlotTypeLoader.getSlotType(animatable, "costume_hat"));
            AccessoriesContainer shirt = capability.getContainer(SlotTypeLoader.getSlotType(animatable, "costume_shirt"));
            AccessoriesContainer pants = capability.getContainer(SlotTypeLoader.getSlotType(animatable, "costume_pants"));
            AccessoriesContainer shoes = capability.getContainer(SlotTypeLoader.getSlotType(animatable, "costume_shoes"));

            if (hat != null && stack.is(TagRegistry.COSTUME_HAT))
                return EquipmentSlot.HEAD;
            if (shirt != null && stack.is(TagRegistry.COSTUME_SHIRT))
                return EquipmentSlot.CHEST;
            if (pants != null && stack.is(TagRegistry.COSTUME_PANTS))
                return EquipmentSlot.LEGS;
            if (shoes != null && stack.is(TagRegistry.COSTUME_SHOES))
                return EquipmentSlot.FEET;
        }
        return super.getEquipmentSlotForBone(bone, stack, animatable);
    }

    @Nullable
    @Override
    protected ItemStack getArmorItemForBone(GeoBone bone, T animatable) {
        return switch (bone.getName()) {
            case "armorHead", "armorBipedHead" -> helmetStack;
            case "armorBody", "armorLeftArm", "armorRightArm" -> chestplateStack;
            case "armorLeftLeg", "armorRightLeg" -> leggingsStack;
            case "armorLeftBoot", "armorRightBoot" -> bootsStack;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public void preRender(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType,
                          MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        AccessoriesCapability capability = AccessoriesCapability.get(animatable);
        boolean hasArmorHead = bakedModel.getBone("armorHead").isPresent() || bakedModel.getBone("armorBipedHead").isPresent();
        boolean hasArmorBody = bakedModel.getBone("armorBody").isPresent() || bakedModel.getBone("armorLeftArm").isPresent()
                || bakedModel.getBone("armorRightArm").isPresent();
        boolean hasArmorLegs = bakedModel.getBone("armorLeftLeg").isPresent() || bakedModel.getBone("armorRightLeg").isPresent();
        boolean hasArmorBoots = bakedModel.getBone("armorLeftBoot").isPresent() || bakedModel.getBone("armorRightBoot").isPresent();

        if (capability != null) {
            this.helmetStack = hasArmorHead ? getAccessoryStack(capability, animatable, "costume_hat") : ItemStack.EMPTY;
            this.chestplateStack = hasArmorBody ? getAccessoryStack(capability, animatable, "costume_shirt") : ItemStack.EMPTY;
            this.leggingsStack = hasArmorLegs ? getAccessoryStack(capability, animatable, "costume_pants") : ItemStack.EMPTY;
            this.bootsStack = hasArmorBoots ? getAccessoryStack(capability, animatable, "costume_shoes") : ItemStack.EMPTY;
        } else {
            this.helmetStack = ItemStack.EMPTY;
            this.chestplateStack = ItemStack.EMPTY;
            this.leggingsStack = ItemStack.EMPTY;
            this.bootsStack = ItemStack.EMPTY;
        }

        this.mainHandStack = animatable.getItemBySlot(EquipmentSlot.MAINHAND);
        this.offhandStack = animatable.getItemBySlot(EquipmentSlot.OFFHAND);
    }

    @Nullable
    private ItemStack getAccessoryStack(AccessoriesCapability capability, LivingEntity entity, String slotName) {
        AccessoriesContainer container = capability.getContainer(SlotTypeLoader.getSlotType(entity, slotName));
        if (container != null && !container.getAccessories().isEmpty()) {
            return container.getAccessories().getItem(0);
        }
        return ItemStack.EMPTY;
    }
}
