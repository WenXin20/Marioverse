package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.client.renderers.costumes.MaleCostumeRenderer;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.data.SlotTypeLoader;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MaleCostumeItem extends BaseCostumeItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    int tooltipLineAmt = 0;
    String tooltipName;

    public MaleCostumeItem(Ingredient repairIngredient, Holder<ArmorMaterial> armorMaterial, Type armorType, Properties properties) {
        super(repairIngredient, armorMaterial, armorType, properties);
    }

    public MaleCostumeItem(Ingredient repairIngredient, Holder<ArmorMaterial> armorMaterial, Type armorType,
                           String tooltipName, int tooltipLineAmt, Properties properties) {
        super(repairIngredient, armorMaterial, armorType, properties);
        this.tooltipLineAmt = tooltipLineAmt;
        this.tooltipName = tooltipName;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(@Nullable T livingEntity,
                ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original) {
                if(this.renderer == null)
                    this.renderer = new MaleCostumeRenderer();
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, 20, state -> {
            state.getController().setAnimation(DefaultAnimations.IDLE);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
        if (this.tooltipLineAmt > 0) {
            list.add(Component.literal(""));
            for (int lineAmt = 1; lineAmt <= tooltipLineAmt; lineAmt++)
                list.add(Component.translatable("item.marioverse." + this.tooltipName + ".tooltip.line" + lineAmt));
            list.add(Component.literal(""));
        }
    }

    public static void resetCostumes(LivingEntity entity) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (!slot.isArmor())
                continue;
            ItemStack stack = entity.getItemBySlot(slot);
            MaleCostumeItem.clearPowerUps(stack);
        }

        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability != null) {
            for (String slotName : new String[]{"costume_hat", "costume_shirt", "costume_pants", "costume_shoes"}) {
                AccessoriesContainer container = capability.getContainer(SlotTypeLoader.getSlotType(entity, slotName));
                if (container == null)
                    continue;

                ItemStack stack = container.getAccessories().getItem(0);
                MaleCostumeItem.clearPowerUps(stack);
            }
        }
    }

    private static void clearPowerUps(ItemStack stack) {
        if (!stack.isEmpty() && stack.is(TagRegistry.COSTUMES)) {
            if (stack.getOrDefault(DataComponentRegistry.HAS_FIRE_FLOWER, false))
                stack.set(DataComponentRegistry.HAS_FIRE_FLOWER, false);
            if (stack.getOrDefault(DataComponentRegistry.HAS_ICE_FLOWER, false))
                stack.set(DataComponentRegistry.HAS_ICE_FLOWER, false);
        }
    }
}
