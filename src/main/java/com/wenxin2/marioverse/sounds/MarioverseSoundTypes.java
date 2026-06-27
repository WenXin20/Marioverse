package com.wenxin2.marioverse.sounds;

import com.wenxin2.marioverse.blocks.CoinBlock;
import com.wenxin2.marioverse.blocks.StarCoinBlock;
import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.items.BasePowerUpItem;
import com.wenxin2.marioverse.items.DashMushroomItem;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ArmorStandItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.ExperienceBottleItem;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.LingeringPotionItem;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.WindChargeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.neoforged.neoforge.common.util.DeferredSoundType;

public class MarioverseSoundTypes {
    public static final SoundType CLEAR_PIPE = new DeferredSoundType(4.0F, 1.0F,  () -> SoundEvents.GLASS_BREAK,
            SoundRegistry.GLASS_STEP,  () -> SoundEvents.GLASS_PLACE, SoundRegistry.GLASS_HIT, SoundRegistry.GLASS_FALL);

    public static final SoundType COIN_TYPE = new DeferredSoundType(1.0F, 1.0F, () -> SoundEvents.NETHERITE_BLOCK_BREAK,
            SoundRegistry.COIN_PICKUP, SoundRegistry.COIN_PLACE, SoundRegistry.COIN_PLACE, SoundRegistry.COIN_PLACE);

    public static final SoundType WATER_SPOUT_TYPE = new DeferredSoundType(1.0F, 1.0F, () -> SoundEvents.BUCKET_FILL,
            () -> SoundEvents.BUCKET_FILL, () -> SoundEvents.BUCKET_EMPTY, () -> SoundEvents.BUCKET_FILL, () -> SoundEvents.BUCKET_FILL);

    public static void playSounds(Level world, BlockPos pos, ItemStack stack, Container container) {
        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof StarCoinBlock)
            world.playSound(null, pos, SoundRegistry.STAR_COIN_PICKUP.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CoinBlock)
            world.playSound(null, pos, SoundRegistry.COIN_PICKUP.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof TntBlock)
            world.playSound(null, pos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof ArmorStandItem)
            world.playSound(null, pos, SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.is(ItemRegistry.MEGA_MUSHROOM))
            world.playSound(null, pos, SoundRegistry.MEGA_MUSHROOM_SPAWNS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof BasePowerUpItem || stack.getItem() instanceof DashMushroomItem)
            world.playSound(null, pos, SoundRegistry.POWER_UP_SPAWNS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof BoatItem)
            world.playSound(null, pos, SoundEvents.BOAT_PADDLE_WATER, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof EggItem)
            world.playSound(null, pos, SoundEvents.EGG_THROW, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof ExperienceBottleItem)
            world.playSound(null, pos, SoundEvents.EXPERIENCE_BOTTLE_THROW, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof FireChargeItem)
            world.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof LingeringPotionItem)
            world.playSound(null, pos, SoundEvents.LINGERING_POTION_THROW, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof MinecartItem)
            world.playSound(null, pos, SoundEvents.MINECART_RIDING, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof PotionItem)
            world.playSound(null, pos, SoundEvents.SPLASH_POTION_THROW, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof SpawnEggItem)
            world.playSound(null, pos, SoundRegistry.MOB_SPAWNS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof WindChargeItem)
            world.playSound(null, pos, SoundEvents.WIND_CHARGE_THROW, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() == CompatRegistry.BOMB_ITEM.get()
                || stack.getItem() == CompatRegistry.BOMB_BLUE_ITEM.get()
                || stack.getItem() == CompatRegistry.BOMB_SPIKY_ITEM.get())
            world.playSound(null, pos, CompatRegistry.BOMB_SOUND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() == CompatRegistry.CANNONBALL_ITEM.get())
            world.playSound(null, pos, CompatRegistry.CANNON_SOUND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() == CompatRegistry.CONFETTI_POPPER_ITEM.get())
            world.playSound(null, pos, CompatRegistry.CONFETTI_POPPER_SOUND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() == CompatRegistry.HAT_STAND_ITEM.get())
            world.playSound(null, pos, SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() == CompatRegistry.ICE_BOMB_ITEM.get())
            world.playSound(null, pos, CompatRegistry.ICE_BOMB_SOUND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (!stack.isEmpty() && !(container instanceof DecoratedPotBlockEntity))
            world.playSound(null, pos, SoundRegistry.ITEM_SPAWNS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}
