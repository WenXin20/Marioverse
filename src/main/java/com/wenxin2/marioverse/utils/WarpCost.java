package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.integration.sable_compat.SableProvider;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.ArrayList;
import java.util.List;
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

    public static Cost getBlockCost(Level level, BlockPos source, @Nullable BlockPos destination, BlockState state) {
        return WarpCost.getBlockCost(WarpCost.getDistance(level, source, destination), state);
    }

    public static Cost getBlockCost(double distance, BlockState state) {
        if (state.getBlock() instanceof DoorBlock)
            return WarpCost.getCost(distance, ConfigRegistry.WARP_DOOR_XP_COST_MULTIPLIER.get(), ConfigRegistry.WARP_DOOR_FUEL_COST_MULTIPLIER.get());
        if (state.getBlock() instanceof TrapDoorBlock)
            return WarpCost.getCost(distance, ConfigRegistry.WARP_TRAPDOOR_XP_COST_MULTIPLIER.get(), ConfigRegistry.WARP_TRAPDOOR_FUEL_COST_MULTIPLIER.get());
        if (state.getBlock() instanceof WarpPipeBlock)
            return WarpCost.getCost(distance, ConfigRegistry.WARP_PIPE_XP_COST_MULTIPLIER.get(), ConfigRegistry.WARP_PIPE_FUEL_COST_MULTIPLIER.get());
        return FREE;
    }

    public static Cost getEntityCost(Level level, BlockPos source, @Nullable BlockPos destination, boolean isPainting) {
        return WarpCost.getEntityCost(WarpCost.getDistance(level, source, destination), isPainting);
    }

    public static Cost getEntityCost(double distance, boolean isPainting) {
        if (isPainting)
            return WarpCost.getCost(distance, ConfigRegistry.WARP_PAINTING_XP_COST_MULTIPLIER.get(), ConfigRegistry.WARP_PAINTING_FUEL_COST_MULTIPLIER.get());
        return WarpCost.getCost(distance, ConfigRegistry.WARP_ENTITY_XP_COST_MULTIPLIER.get(), ConfigRegistry.WARP_ENTITY_FUEL_COST_MULTIPLIER.get());
    }

    public static List<Player> getPayingPlayers(Entity entity) {
        Entity rootVehicle = entity.getRootVehicle();
        List<Player> players = new ArrayList<>();

        if (rootVehicle instanceof Player player)
            players.add(player);
        for (Entity passenger : rootVehicle.getIndirectPassengers()) {
            if (passenger instanceof Player player)
                players.add(player);
        }
        return players;
    }

    public static boolean canAfford(Entity entity, Cost cost) {
        return WarpCost.canAfford(WarpCost.getPayingPlayers(entity), cost);
    }

    public static boolean canAfford(List<Player> players, Cost cost) {
        if (cost.xp() <= 0 && cost.coins() <= 0)
            return true;

        boolean missingXp = false;
        boolean missingCoins = false;

        for (Player player : players) {
            if (player.getAbilities().instabuild || player.isSpectator())
                continue;
            if (cost.xp() > 0 && WarpCost.getTotalExperience(player) < cost.xp())
                missingXp = true;
            if (cost.coins() > 0 && WarpCost.countCoins(player) < cost.coins())
                missingCoins = true;
        }

        if (!missingXp && !missingCoins)
            return true;

        MutableComponent message = WarpCost.getMissingMessage(cost, missingXp, missingCoins).withStyle(ChatFormatting.RED);
        for (Player player : players) {
            if (!player.level().isClientSide)
                player.displayClientMessage(message, true);
        }
        return false;
    }

    public static void chargeIfWarped(Entity entity, Vec3 startPos, Cost cost) {
        if (!entity.level().isClientSide && !entity.position().equals(startPos))
            WarpCost.charge(WarpCost.getPayingPlayers(entity), cost);
    }

    public static void charge(List<Player> players, Cost cost) {
        for (Player player : players) {
            if (player.level().isClientSide || player.getAbilities().instabuild || player.isSpectator())
                continue;
            if (cost.xp() > 0)
                player.giveExperiencePoints(-cost.xp());
            if (cost.coins() > 0)
                WarpCost.consumeCoins(player, cost.coins());
        }
    }

    private static double getDistance(Level level, BlockPos source, @Nullable BlockPos destination) {
        if (destination == null)
            return -1.0;

        BlockPos worldSource = SableProvider.isLoaded() ? SableProvider.toWorldPos(level, source) : source;
        BlockPos worldDestination = SableProvider.isLoaded() ? SableProvider.toWorldPos(level, destination) : destination;
        if (worldSource == null || worldDestination == null)
            return -1.0;
        return Math.sqrt(worldSource.distSqr(worldDestination));
    }

    private static Cost getCost(double distance, double xpMultiplier, double coinMultiplier) {
        if (distance < 0.0 || (xpMultiplier <= 0.0 && coinMultiplier <= 0.0))
            return FREE;
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
