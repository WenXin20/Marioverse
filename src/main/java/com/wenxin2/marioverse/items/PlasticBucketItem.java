package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.client.renderers.costumes.PlasticBucketRenderer;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import java.util.function.Consumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
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
        HitResult hit = player.pick(5.0D, 0.0F, false);
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;
            BlockState state = level.getBlockState(blockHit.getBlockPos());

            if (state.getBlock() instanceof BucketPickup
                    || state.getFluidState().is(Fluids.WATER))
                return InteractionResultHolder.pass(player.getItemInHand(hand));
        }

        if (player.isShiftKeyDown())
            return InteractionResultHolder.pass(player.getItemInHand(hand));
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

        if (player != null && state.getBlock() instanceof BucketPickup) {
            ItemStack newStack;
            if (state.is(BlockRegistry.QUICKSAND.get()))
                newStack = new ItemStack(ItemRegistry.QUICKSAND_PLASTIC_BUCKET.get());
            else if (state.is(BlockRegistry.RED_QUICKSAND.get()))
                newStack = new ItemStack(ItemRegistry.RED_QUICKSAND_PLASTIC_BUCKET.get());
            else if (state.is(Blocks.POWDER_SNOW))
                newStack = new ItemStack(ItemRegistry.POWDER_SNOW_PLASTIC_BUCKET.get());
            else if (state.getFluidState().is(Fluids.WATER))
                newStack = new ItemStack(ItemRegistry.PLASTIC_WATER_BUCKET.get());
            else return InteractionResult.PASS;
            newStack.applyComponents(stack.getComponents());

            if (state.getFluidState().is(Fluids.WATER))
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BUCKET_FILL, SoundSource.NEUTRAL, 0.5F,
                        0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            else level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BUCKET_FILL_POWDER_SNOW, SoundSource.NEUTRAL, 0.5F,
                    0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
            player.awardStat(Stats.ITEM_USED.get(this));
            player.setItemInHand(context.getHand(), newStack);
            stack.consume(1, player);

            if (!level.isClientSide())
                level.levelEvent(2001, pos, Block.getId(state));

            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }
}
