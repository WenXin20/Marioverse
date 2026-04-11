package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.client.renderers.costumes.PlasticBucketRenderer;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
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

public class PlasticBucketItem extends BaseCostumeItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    int tooltipLineAmt = 0;

    public PlasticBucketItem(Ingredient repairIngredient, Holder<ArmorMaterial> armorMaterial, Type armorType, Properties properties) {
        super(repairIngredient, armorMaterial, armorType, properties);
    }

    public PlasticBucketItem(int tooltipLineAmt, Ingredient repairIngredient, Holder<ArmorMaterial> armorMaterial, Type armorType, Properties properties) {
        super(repairIngredient, armorMaterial, armorType, properties);
        this.tooltipLineAmt = tooltipLineAmt;
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

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
        if (Screen.hasShiftDown() && this.tooltipLineAmt > 0) {
            list.add(Component.literal(""));
            for (int lineAmt = 1; lineAmt <= tooltipLineAmt; lineAmt++)
                list.add(Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt));
            list.add(Component.literal(""));
        } else if (this.tooltipLineAmt > 0)
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip"));
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        ItemStack stack = player.getItemInHand(hand);
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = level.getBlockState(pos);
        FluidState fluidState = level.getFluidState(pos);

        if (hitResult.getType() != HitResult.Type.BLOCK && state.getBlock() instanceof BucketPickup)
            return InteractionResultHolder.pass(stack);
        else if (state.getBlock() instanceof BucketPickup bucketPickup && fluidState.is(Fluids.WATER)) {
            ItemStack stackPickup = bucketPickup.pickupBlock(player, level, pos, state);
            ItemStack newStack = new ItemStack(ItemRegistry.PLASTIC_WATER_BUCKET.get());

            if (!stackPickup.isEmpty()) {
                newStack.applyComponents(stack.getComponents());
                bucketPickup.getPickupSound(state).ifPresent(soundEvent -> player.playSound(soundEvent, 1.0F, 1.0F));

                if (!player.isCreative())
                    player.setItemInHand(hand, newStack);
                else ItemUtils.createFilledResult(stack, player, newStack);

                if (!level.isClientSide) {
                    CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, newStack);
                    CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer) player, pos, newStack);
                }
                player.awardStat(Stats.ITEM_USED.get(this));
                level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
            }
            return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
        } else if (state.getBlock() instanceof BucketPickup bucketPickup && fluidState.is(Fluids.LAVA)) {
            ItemStack stackPickup = bucketPickup.pickupBlock(player, level, pos, state);

            if (!stackPickup.isEmpty()) {
                bucketPickup.getPickupSound(state).ifPresent(soundEvent -> player.playSound(soundEvent, 1.0F, 1.0F));

                if (!player.isCreative() && level.getBlockState(player.blockPosition()).canBeReplaced())
                    level.setBlock(player.blockPosition(), Blocks.LAVA.defaultBlockState(), 3);
                else if (!player.isCreative() && state.canBeReplaced())
                    level.setBlock(pos, Blocks.LAVA.defaultBlockState(), 3);

                if (!level.isClientSide) {
                    CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) player, stack);

                    if (!player.isCreative())
                        level.broadcastEntityEvent(player, (byte) 47);
                }

                stack.hurtAndBreak(Integer.MAX_VALUE, player, Player.getSlotForHand(player.getUsedItemHand()));
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
            else if (state.is(BlockRegistry.WATER_SPOUT))
                newStack = new ItemStack(ItemRegistry.PLASTIC_WATER_BUCKET.get());
            else return InteractionResult.PASS;
            newStack.applyComponents(stack.getComponents());

            bucketPickup.getPickupSound(state).ifPresent(soundEvent -> player.playSound(soundEvent, 1.0F, 1.0F));
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);

            if (!player.isCreative())
                player.setItemInHand(context.getHand(), newStack);
            else ItemUtils.createFilledResult(stack, player, newStack);

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
}