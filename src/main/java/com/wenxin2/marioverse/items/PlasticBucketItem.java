package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.client.renderers.costumes.PlasticBucketRenderer;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.slot.SlotReference;
import java.util.function.Consumer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jetbrains.annotations.NotNull;
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

public class PlasticBucketItem extends BaseCostumeItem implements Accessory, GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public PlasticBucketItem(Ingredient repairIngredient, Holder<ArmorMaterial> armorMaterial, Type armorType, Properties properties) {
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
                    this.renderer = new PlasticBucketRenderer();
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

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = level.getBlockState(pos);
        FluidState fluidState = level.getFluidState(pos);

        if (hitResult.getType() != HitResult.Type.BLOCK
                && state.getBlock() instanceof BucketPickup)
            return InteractionResultHolder.pass(stack);
        else if (state.getBlock() instanceof BucketPickup bucketPickup && fluidState.is(Fluids.WATER)) {
            ItemStack stackPickup = bucketPickup.pickupBlock(player, level, pos, state);
            ItemStack newStack = new ItemStack(ItemRegistry.PLASTIC_WATER_BUCKET.get());

            if (!stackPickup.isEmpty()) {
                newStack.applyComponents(stack.getComponents());
                bucketPickup.getPickupSound(state).ifPresent(soundEvent -> player.playSound(soundEvent, 1.0F, 1.0F));
                player.setItemInHand(hand, newStack);

                if (!level.isClientSide)
                    CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, stackPickup);
                player.awardStat(Stats.ITEM_USED.get(this));
                level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
            }
            return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
        }

        if (player.isShiftKeyDown())
            return InteractionResultHolder.pass(stack);
        return this.swapWithEquipmentSlot(this, level, player, hand);
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (player != null && state.getBlock() instanceof BucketPickup bucketPickup) {
            ItemStack newStack;
            if (state.is(BlockRegistry.QUICKSAND.get()))
                newStack = new ItemStack(ItemRegistry.PLASTIC_QUICKSAND_BUCKET.get());
            else if (state.is(BlockRegistry.RED_QUICKSAND.get()))
                newStack = new ItemStack(ItemRegistry.PLASTIC_RED_QUICKSAND_BUCKET.get());
            else if (state.is(Blocks.POWDER_SNOW))
                newStack = new ItemStack(ItemRegistry.PLASTIC_POWDER_SNOW_BUCKET.get());
            else return InteractionResult.PASS;
            newStack.applyComponents(stack.getComponents());

            bucketPickup.getPickupSound(state).ifPresent(soundEvent -> player.playSound(soundEvent, 1.0F, 1.0F));
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
            player.setItemInHand(context.getHand(), newStack);
            stack.consume(1, player);

            if (!level.isClientSide)
                level.levelEvent(2001, pos, Block.getId(state));
            if (!level.isClientSide)
                CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)player, newStack);level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
            player.awardStat(Stats.ITEM_USED.get(this));

            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }

    public static ItemStack getPlasticBucketForFluid(FluidStack fluid) {
        if (fluid.isEmpty())
            return new ItemStack(ItemRegistry.PLASTIC_BUCKET.get());

        ItemStack vanillaBucket = FluidUtil.getFilledBucket(fluid);

        if (vanillaBucket.is(Items.WATER_BUCKET))
            return new ItemStack(ItemRegistry.PLASTIC_WATER_BUCKET.get());

        return new ItemStack(ItemRegistry.PLASTIC_BUCKET.get());
    }

    @Override
    public void onEquipFromUse(ItemStack stack, SlotReference reference) {
        if (isPickingUpFluid(reference.entity())) {
            reference.setStack(ItemStack.EMPTY);
            if (reference.entity() instanceof Player player)
                player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            return;
        }
        Accessory.super.onEquipFromUse(stack, reference);
    }

    private boolean isPickingUpFluid(LivingEntity entity) {
        if (!(entity instanceof Player player))
            return false;

        BlockHitResult hit = Item.getPlayerPOVHitResult(player.level(), player, ClipContext.Fluid.SOURCE_ONLY);
        if (hit.getType() != HitResult.Type.BLOCK)
            return false;

        return player.level().getFluidState(hit.getBlockPos()).is(Fluids.WATER);
    }
}