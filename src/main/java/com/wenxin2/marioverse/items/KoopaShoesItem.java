package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.client.renderers.costumes.KoopaShoesRenderer;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ServerLevelAccessor;
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

public class KoopaShoesItem extends BaseCostumeItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public KoopaShoesItem(Ingredient repairIngredient, Holder<ArmorMaterial> armorMaterial, Type armorType, Properties properties) {
        super(repairIngredient, armorMaterial, armorType, properties);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(@Nullable T livingEntity,
                ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original) {
                if(this.renderer == null)
                    this.renderer = new KoopaShoesRenderer();
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

    public static void applyRandomTrim(ServerLevelAccessor levelAccessor, RandomSource random, ItemStack stack) {
        RegistryAccess registryAccess = levelAccessor.registryAccess();
        Registry<TrimMaterial> materials = registryAccess.registryOrThrow(Registries.TRIM_MATERIAL);
        Registry<TrimPattern> patterns = registryAccess.registryOrThrow(Registries.TRIM_PATTERN);

        Optional<Holder.Reference<TrimMaterial>> material = materials.getRandom(random);
        Optional<Holder.Reference<TrimPattern>> pattern = patterns.getRandom(random);

        if (material.isPresent() && pattern.isPresent())
            stack.set(DataComponents.TRIM, new ArmorTrim(material.get(), pattern.get()));
    }
}
