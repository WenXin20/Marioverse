package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.client.renderers.costumes.MaleCostumeRenderer;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.gui.screens.Screen;
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
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

public class MaleCostumeItem extends BaseCostumeItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    String tooltipName;

    public MaleCostumeItem(Ingredient repairIngredient, Holder<ArmorMaterial> armorMaterial, Type armorType,
                           String tooltipName, int tooltipLineAmt, Properties properties) {
        super(repairIngredient, armorMaterial, armorType, tooltipLineAmt, properties);
        this.tooltipLineAmt = tooltipLineAmt;
        this.tooltipName = tooltipName;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
        if (Screen.hasShiftDown() && this.tooltipLineAmt > 0) {
            list.add(Component.literal(""));

            for (int lineAmt = 1; lineAmt <= tooltipLineAmt; lineAmt++)
                list.add(Component.translatable("item.marioverse." + this.tooltipName + ".tooltip.line" + lineAmt));

            list.add(Component.literal(""));
        } else if (this.tooltipLineAmt > 0)
            list.add(Component.translatable("item.marioverse." + this.tooltipName + ".tooltip"));
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

    public static void resetCostumes(LivingEntity entity) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (!slot.isArmor())
                continue;
            ItemStack stack = entity.getItemBySlot(slot);
            MaleCostumeItem.clearPowerUps(stack);
        }

        Optional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(entity);
        if (curiosInventory.isPresent()) {
            Map<String, ICurioStacksHandler> curios = curiosInventory.get().getCurios();
            for (String slotName : new String[]{"costume_hat", "costume_shirt", "costume_pants", "costume_shoes"}) {
                ICurioStacksHandler slotHandler = curios.get(slotName);
                if (slotHandler == null)
                    continue;

                ItemStack stack = slotHandler.getStacks().getStackInSlot(0);
                MaleCostumeItem.clearPowerUps(stack);
            }
        }
    }

    private static void clearPowerUps(ItemStack stack) {
        if (!stack.isEmpty() && stack.is(TagRegistry.COSTUMES))
            stack.remove(DataComponentRegistry.POWER_UP_TYPE.get());
    }
}
