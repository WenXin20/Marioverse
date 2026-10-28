package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.integration.sable_compat.SableProvider;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class WarpCost {
    public static final int STAR_COIN_VALUE = 9;
    public static final Cost FREE = new Cost(0, 0);

    public record Cost(int xp, int coins) {}

    public static Cost getBlockCost(Entity entity, Level level, BlockPos source, @Nullable BlockPos destination, BlockState state) {
        if (state.getBlock() instanceof DoorBlock)
            return WarpCost.getCost(entity, level, source, destination, ConfigRegistry.WARP_DOOR_XP_COST_MULTIPLIER.get(),
                    ConfigRegistry.WARP_DOOR_FUEL_COST_MULTIPLIER.get());
        if (state.getBlock() instanceof TrapDoorBlock)
            return WarpCost.getCost(entity, level, source, destination, ConfigRegistry.WARP_TRAPDOOR_XP_COST_MULTIPLIER.get(),
                    ConfigRegistry.WARP_TRAPDOOR_FUEL_COST_MULTIPLIER.get());
        if (state.getBlock() instanceof WarpPipeBlock)
            return WarpCost.getCost(entity, level, source, destination, ConfigRegistry.WARP_PIPE_XP_COST_MULTIPLIER.get(),
                    ConfigRegistry.WARP_PIPE_FUEL_COST_MULTIPLIER.get());
        return FREE;
    }

    public static Cost getEntityCost(Entity entity, Level level, BlockPos source, @Nullable BlockPos destination, boolean isPainting) {
        if (isPainting)
            return WarpCost.getCost(entity, level, source, destination, ConfigRegistry.WARP_PAINTING_XP_COST_MULTIPLIER.get(),
                    ConfigRegistry.WARP_PAINTING_FUEL_COST_MULTIPLIER.get());
        return WarpCost.getCost(entity, level, source, destination, ConfigRegistry.WARP_ENTITY_XP_COST_MULTIPLIER.get(),
                ConfigRegistry.WARP_ENTITY_FUEL_COST_MULTIPLIER.get());
    }

    public static boolean canAfford(Entity entity, Cost cost) {
        if (!(entity instanceof Player player) || (cost.xp() <= 0 && cost.coins() <= 0))
            return true;

        boolean hasXp = cost.xp() <= 0 || WarpCost.getTotalExperience(player) >= cost.xp();
        boolean hasCoins = cost.coins() <= 0 || WarpCost.countCoins(player) >= cost.coins();

        if (hasXp && hasCoins)
            return true;

        if (!player.level().isClientSide)
            player.displayClientMessage(WarpCost.getMissingMessage(cost, !hasXp, !hasCoins).withStyle(ChatFormatting.RED), true);
        return false;
    }

    public static void chargeIfWarped(Entity entity, Vec3 startPos, Cost cost) {
        if (!(entity instanceof Player player) || player.level().isClientSide || player.position().equals(startPos))
            return;

        if (cost.xp() > 0)
            player.giveExperiencePoints(-cost.xp());
        if (cost.coins() > 0)
            WarpCost.consumeCoins(player, cost.coins());
    }

    private static Cost getCost(Entity entity, Level level, BlockPos source, @Nullable BlockPos destination,
                                double xpMultiplier, double coinMultiplier) {
        if ((xpMultiplier <= 0.0 && coinMultiplier <= 0.0) || destination == null
                || !(entity instanceof Player player) || player.getAbilities().instabuild)
            return FREE;

        BlockPos worldSource = SableProvider.isLoaded() ? SableProvider.toWorldPos(level, source) : source;
        BlockPos worldDestination = SableProvider.isLoaded() ? SableProvider.toWorldPos(level, destination) : destination;
        if (worldSource == null || worldDestination == null)
            return FREE;

        double distance = Math.sqrt(worldSource.distSqr(worldDestination));
        return new Cost(xpMultiplier > 0.0 ? Mth.ceil(distance * xpMultiplier) : 0,
                coinMultiplier > 0.0 ? Mth.ceil(distance * coinMultiplier) : 0);
    }

    private static MutableComponent getMissingMessage(Cost cost, boolean missingXp, boolean missingCoins) {
        int requiredLevel = WarpCost.getLevelForExperience(cost.xp());
        Component levelPlural = WarpCost.getPlural(requiredLevel);
        Component fuelPlural = WarpCost.getPlural(cost.coins());

        if (missingXp && missingCoins)
            return Component.translatable("display.marioverse.warp_not_enough_xp_and_fuel",
                    requiredLevel, levelPlural, cost.coins(), WarpCost.getFuelName(), fuelPlural);
        if (missingXp)
            return Component.translatable("display.marioverse.warp_not_enough_xp", requiredLevel, levelPlural);
        return Component.translatable("display.marioverse.warp_not_enough_fuel", cost.coins(), WarpCost.getFuelName(), fuelPlural);
    }

    private static Component getPlural(int amount) {
        return amount == 1 ? Component.empty() : Component.translatable("display.marioverse.warp_cost.plural");
    }

    private static Component getFuelName() {
        ItemStack coin = new ItemStack(BlockRegistry.COIN.asItem());

        if (coin.is(TagRegistry.WARP_FUEL))
            return coin.getHoverName();
        return BuiltInRegistries.ITEM.getTag(TagRegistry.WARP_FUEL)
                .flatMap(items -> items.stream().findFirst())
                .map(item -> new ItemStack(item).getHoverName())
                .orElse(coin.getHoverName());
    }

    private static int getCoinValue(ItemStack stack) {
        if (!stack.is(TagRegistry.WARP_FUEL))
            return 0;
        return stack.is(ItemRegistry.STAR_COIN.get()) ? STAR_COIN_VALUE : 1;
    }

    private static int countCoins(Player player) {
        Inventory inventory = player.getInventory();
        int total = 0;

        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            total += WarpCost.getCoinValue(stack) * stack.getCount();
        }
        return total;
    }

    private static void consumeCoins(Player player, int cost) {
        Inventory inventory = player.getInventory();
        int remaining = cost;

        for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; slot++) {
            ItemStack stack = inventory.getItem(slot);

            if (WarpCost.getCoinValue(stack) == 1) {
                int used = Math.min(stack.getCount(), remaining);
                stack.shrink(used);
                remaining -= used;
            }
        }

        for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; slot++) {
            ItemStack stack = inventory.getItem(slot);
            int value = WarpCost.getCoinValue(stack);

            if (value > 1) {
                int used = Math.min(stack.getCount(), (remaining + value - 1) / value);
                stack.shrink(used);
                remaining -= used * value;
            }
        }

        if (remaining < 0)
            inventory.placeItemBackInInventory(new ItemStack(BlockRegistry.COIN.asItem(), -remaining));
        inventory.setChanged();
    }

    private static int getTotalExperience(Player player) {
        int total = 0;

        for (int level = 0; level < player.experienceLevel; level++)
            total += WarpCost.getXpNeededForLevel(level);
        return total + Mth.floor(player.experienceProgress * player.getXpNeededForNextLevel());
    }

    private static int getLevelForExperience(int experience) {
        int level = 0;

        for (int total = 0; total < experience; level++)
            total += WarpCost.getXpNeededForLevel(level);
        return level;
    }

    private static int getXpNeededForLevel(int level) {
        if (level >= 30)
            return 112 + (level - 30) * 9;
        return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
    }
}
