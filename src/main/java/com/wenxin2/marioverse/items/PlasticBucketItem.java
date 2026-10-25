package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.client.renderers.costumes.PlasticBucketRenderer;
import com.wenxin2.marioverse.event_handlers.RegistryEventHandlers;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.Tags;
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
    int tooltipLineAmt;

    public PlasticBucketItem(Ingredient repairIngredient, Holder<ArmorMaterial> armorMaterial, Type armorType,
                             int tooltipLineAmt, Properties properties) {
        super(repairIngredient, armorMaterial, armorType, tooltipLineAmt, properties);
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
        else if (state.getBlock() instanceof BucketPickup bucketPickup
                && PlasticBucketItem.canPickUp(stack, fluidState.getType())) {
            ItemStack newStack = new ItemStack(PlasticBucketItem.getPlasticBucket(fluidState.getType()));
            ItemStack stackPickup = bucketPickup.pickupBlock(player, level, pos, state);

            if (!stackPickup.isEmpty()) {
                newStack.applyComponents(stack.getComponents());
                bucketPickup.getPickupSound(state).ifPresent(player::playSound);

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
        } else if (state.getBlock() instanceof BucketPickup bucketPickup
                && PlasticBucketItem.requiresFireProof(fluidState.getType())) {
            BlockState fluidBlock = fluidState.createLegacyBlock();
            ItemStack stackPickup = bucketPickup.pickupBlock(player, level, pos, state);

            if (!stackPickup.isEmpty()) {
                bucketPickup.getPickupSound(state).ifPresent(player::playSound);

                if (!player.isCreative() && level.getBlockState(player.blockPosition()).canBeReplaced())
                    level.setBlock(player.blockPosition(), fluidBlock, 3);
                else if (!player.isCreative() && state.canBeReplaced())
                    level.setBlock(pos, fluidBlock, 3);

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
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity livingEntity, InteractionHand hand) {
        ItemStack plasticStack = stack.copy();
        List<ItemStack> inventory = player.getInventory().items.stream().map(ItemStack::copy).toList();

        player.setItemInHand(hand, new ItemStack(Items.BUCKET));
        InteractionResult result = livingEntity.interact(player, hand);
        ItemStack filledStack = player.getItemInHand(hand).is(Items.BUCKET) ? ItemStack.EMPTY : player.getItemInHand(hand);

        for (int slot = 0; filledStack.isEmpty() && slot < inventory.size(); slot++) {
            if (hand == InteractionHand.MAIN_HAND && slot == player.getInventory().selected)
                continue;
            if (!ItemStack.matches(inventory.get(slot), player.getInventory().items.get(slot))) {
                filledStack = player.getInventory().items.get(slot).copyWithCount(1);
                player.getInventory().items.set(slot, inventory.get(slot));
            }
        }
        player.setItemInHand(hand, plasticStack);

        Item plasticBucket = PlasticBucketItem.getPlasticBucket(filledStack);
        if (!result.consumesAction())
            return super.interactLivingEntity(plasticStack, player, livingEntity, hand);
        if (plasticBucket == Items.AIR)
            return result;

        ItemStack newStack = new ItemStack(plasticBucket);
        newStack.applyComponents(plasticStack.getComponents());
        player.setItemInHand(hand, ItemUtils.createFilledResult(plasticStack, player, newStack));
        return result;
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
            else if (RegistryEventHandlers.PLASTIC_SOLID_BUCKETS.containsKey(state.getBlock()))
                newStack = new ItemStack(RegistryEventHandlers.PLASTIC_SOLID_BUCKETS.get(state.getBlock()));
            else return InteractionResult.PASS;
            newStack.applyComponents(stack.getComponents());

            bucketPickup.getPickupSound(state).ifPresent(player::playSound);
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

        Item bucket = PlasticBucketItem.getPlasticBucket(fluid.getFluid());
        return new ItemStack(bucket == Items.AIR ? ItemRegistry.PLASTIC_BUCKET.get() : bucket);
    }

    public static Item getPlasticBucket(Fluid fluid) {
        if (fluid.isSame(Fluids.WATER))
            return ItemRegistry.PLASTIC_WATER_BUCKET.get();
        if (fluid.isSame(Fluids.LAVA))
            return ItemRegistry.PLASTIC_LAVA_BUCKET.get();
        if (fluid.is(Tags.Fluids.MILK))
            return ItemRegistry.PLASTIC_MILK_BUCKET.get();
        if (fluid instanceof FlowingFluid flowingFluid)
            fluid = flowingFluid.getSource();
        return RegistryEventHandlers.PLASTIC_FLUID_BUCKETS.getOrDefault(fluid, Items.AIR);
    }

    public static Item getPlasticBucket(ItemStack filledStack) {
        if (filledStack.isEmpty())
            return Items.AIR;
        if (filledStack.getItem() instanceof MilkBucketItem || filledStack.is(Tags.Items.BUCKETS_MILK))
            return ItemRegistry.PLASTIC_MILK_BUCKET.get();
        if (filledStack.getItem() instanceof BucketItem bucketItem && !(filledStack.getItem() instanceof MobBucketItem))
            return PlasticBucketItem.getPlasticBucket(bucketItem.content);
        return FluidUtil.getFluidContained(filledStack)
                .map(fluidStack -> PlasticBucketItem.getPlasticBucket(fluidStack.getFluid())).orElse(Items.AIR);
    }

    public static boolean requiresFireProof(Fluid fluid) {
        return fluid.is(FluidTags.LAVA) || fluid.getFluidType().getTemperature() >= 1000;
    }

    public static boolean canPickUp(ItemStack stack, Fluid fluid) {
        if (PlasticBucketItem.getPlasticBucket(fluid) == Items.AIR)
            return false;
        return !PlasticBucketItem.requiresFireProof(fluid) || PlasticBucketItem.isFireProof(stack);
    }

    public static boolean isFireProof(ItemStack stack) {
        return stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).keySet().stream()
                .anyMatch(enchantment -> enchantment.is(TagRegistry.FIRE_PROOF_ENCHANTS));
    }

    public static void applyRandomTrim(ServerLevelAccessor levelAccessor, RandomSource random, ItemStack stack) {
        RegistryAccess registryAccess = levelAccessor.registryAccess();
        Registry<TrimMaterial> materials = registryAccess.registryOrThrow(Registries.TRIM_MATERIAL);
        Registry<TrimPattern> patterns = registryAccess.registryOrThrow(Registries.TRIM_PATTERN);

        List<Holder.Reference<TrimPattern>> allowedPatterns = patterns.holders()
                .filter(holder -> Marioverse.MOD_ID.equals(holder.key().location().getNamespace())
                        || "minecraft".equals(holder.key().location().getNamespace()))
                .toList();

        Optional<Holder.Reference<TrimMaterial>> material = materials.getRandom(random);

        if (material.isPresent() && !allowedPatterns.isEmpty()) {
            Holder.Reference<TrimPattern> pattern = Util.getRandom(allowedPatterns, random);
            stack.set(DataComponents.TRIM, new ArmorTrim(material.get(), pattern));
        }
    }
}