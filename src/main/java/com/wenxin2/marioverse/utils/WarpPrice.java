package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.blocks.WarpDoorBlock;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.integration.sable_compat.SableProvider;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class WarpPrice {
    public static final int STAR_COIN_VALUE = 9;
    public static final Price FREE = new Price(0, 0);

    public record Price(int xp, int coins, ItemStack item, int itemCount) {
        public Price(int xp, int coins) {
            this(xp, coins, ItemStack.EMPTY, 0);
        }
    }

    public static Price getBlockPrice(Level level, BlockPos source, @Nullable BlockPos destination, BlockState state) {
        if (state.getBlock() instanceof WarpPipeBlock && !WarpPrice.hasConfigPipePrice()
                && level.getBlockEntity(source) instanceof WarpPipeBlockEntity pipeBE)
            return pipeBE.getWarpPrice();
        return WarpPrice.getBlockPrice(WarpPrice.getDistance(level, source, destination), state);
    }

    public static Price getBlockPrice(double distance, BlockState state) {
        if (WarpDoorBlock.isDoor(state))
            return WarpPrice.getPrice(distance, ConfigRegistry.WARP_DOOR_XP_PRICE_MULTIPLIER.get(), ConfigRegistry.WARP_DOOR_FUEL_PRICE_MULTIPLIER.get());
        if (state.getBlock() instanceof TrapDoorBlock)
            return WarpPrice.getPrice(distance, ConfigRegistry.WARP_TRAPDOOR_XP_PRICE_MULTIPLIER.get(), ConfigRegistry.WARP_TRAPDOOR_FUEL_PRICE_MULTIPLIER.get());
        if (state.getBlock() instanceof WarpPipeBlock)
            return WarpPrice.getPrice(distance, ConfigRegistry.WARP_PIPE_XP_PRICE_MULTIPLIER.get(), ConfigRegistry.WARP_PIPE_FUEL_PRICE_MULTIPLIER.get());
        return FREE;
    }

    public static boolean hasConfigPipePrice() {
        return ConfigRegistry.WARP_PIPE_XP_PRICE_MULTIPLIER.get() > 0.0 || ConfigRegistry.WARP_PIPE_FUEL_PRICE_MULTIPLIER.get() > 0.0;
    }

    public static Price getConfigPipePrice(Level level, BlockPos source, @Nullable BlockPos destination) {
        return WarpPrice.getPrice(WarpPrice.getDistance(level, source, destination),
                ConfigRegistry.WARP_PIPE_XP_PRICE_MULTIPLIER.get(), ConfigRegistry.WARP_PIPE_FUEL_PRICE_MULTIPLIER.get());
    }

    public static Price getEntityPrice(Level level, BlockPos source, @Nullable BlockPos destination, boolean isPainting) {
        return WarpPrice.getEntityPrice(WarpPrice.getDistance(level, source, destination), isPainting);
    }

    public static Price getEntityPrice(double distance, boolean isPainting) {
        if (isPainting)
            return WarpPrice.getPrice(distance, ConfigRegistry.WARP_PAINTING_XP_PRICE_MULTIPLIER.get(), ConfigRegistry.WARP_PAINTING_FUEL_PRICE_MULTIPLIER.get());
        return WarpPrice.getPrice(distance, ConfigRegistry.WARP_ENTITY_XP_PRICE_MULTIPLIER.get(), ConfigRegistry.WARP_ENTITY_FUEL_PRICE_MULTIPLIER.get());
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

    public static boolean canAfford(Entity entity, Price price) {
        return WarpPrice.canAfford(WarpPrice.getPayingPlayers(entity), price);
    }

    public static boolean canAfford(List<Player> players, Price price) {
        if (price.xp() <= 0 && price.coins() <= 0 && price.itemCount() <= 0)
            return true;

        boolean missingXp = false;
        boolean missingCoins = false;
        boolean missingItems = false;

        for (Player player : players) {
            if (player.getAbilities().instabuild || player.isSpectator())
                continue;
            if (price.xp() > 0 && WarpPrice.getTotalExperience(player) < price.xp())
                missingXp = true;
            if (price.coins() > 0 && WarpPrice.countCoins(player) < price.coins())
                missingCoins = true;
            if (price.itemCount() > 0 && WarpPrice.countItems(player, price.item()) < price.itemCount())
                missingItems = true;
        }

        if (!missingXp && !missingCoins && !missingItems)
            return true;

        MutableComponent message = (missingItems
                ? Component.translatable("display.marioverse.warp_not_enough_items", price.itemCount(),
                    price.item().getHoverName(), WarpPrice.getItemPlural(price.item(), price.itemCount()))
                : WarpPrice.getMissingMessage(price, missingXp, missingCoins)).withStyle(ChatFormatting.RED);
        for (Player player : players) {
            if (!player.level().isClientSide)
                player.displayClientMessage(message, true);
        }
        return false;
    }

    public static void chargeIfWarped(Entity entity, Vec3 startPos, Price price) {
        if (!entity.level().isClientSide && !entity.position().equals(startPos))
            WarpPrice.charge(WarpPrice.getPayingPlayers(entity), price);
    }

    public static void chargeIfWarped(Entity entity, Vec3 startPos, Price price, BaseWarpBlockEntity warpBE) {
        if (entity.level().isClientSide || entity.position().equals(startPos))
            return;

        int payers = WarpPrice.charge(WarpPrice.getPayingPlayers(entity), price);
        if (warpBE instanceof WarpPipeBlockEntity pipeBE)
            pipeBE.receivePayment(price, payers);
    }

    public static int charge(List<Player> players, Price price) {
        int payers = 0;

        for (Player player : players) {
            if (player.level().isClientSide || player.getAbilities().instabuild || player.isSpectator())
                continue;
            if (price.xp() > 0)
                player.giveExperiencePoints(-price.xp());
            if (price.coins() > 0)
                WarpPrice.consumeCoins(player, price.coins());
            if (price.itemCount() > 0)
                WarpPrice.consumeItems(player, price.item(), price.itemCount());
            payers++;
        }
        return payers;
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

    private static Price getPrice(double distance, double xpMultiplier, double coinMultiplier) {
        if (distance < 0.0 || (xpMultiplier <= 0.0 && coinMultiplier <= 0.0))
            return FREE;
        return new Price(xpMultiplier > 0.0 ? Mth.ceil(distance * xpMultiplier) : 0,
                coinMultiplier > 0.0 ? Mth.ceil(distance * coinMultiplier) : 0);
    }

    private static MutableComponent getMissingMessage(Price price, boolean missingXp, boolean missingCoins) {
        int requiredLevel = WarpPrice.getLevelForExperience(price.xp());
        Component levelPlural = WarpPrice.getPlural(requiredLevel);
        Component fuelPlural = WarpPrice.getPlural(price.coins());

        if (missingXp && missingCoins)
            return Component.translatable("display.marioverse.warp_not_enough_xp_and_fuel",
                    requiredLevel, levelPlural, price.coins(), WarpPrice.getFuelName(), fuelPlural);
        if (missingXp)
            return Component.translatable("display.marioverse.warp_not_enough_xp", requiredLevel, levelPlural);
        return Component.translatable("display.marioverse.warp_not_enough_fuel", price.coins(), WarpPrice.getFuelName(), fuelPlural);
    }

    private static Component getPlural(int amount) {
        return amount == 1 ? Component.empty() : Component.translatable("display.marioverse.warp_price.plural");
    }

    public static Component getItemPlural(ItemStack item, int amount) {
        if (item.getHoverName().getString().toLowerCase(Locale.ROOT).endsWith("s"))
            return Component.empty();
        return WarpPrice.getPlural(amount);
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

    public static int getCoinValue(ItemStack stack) {
        if (!stack.is(TagRegistry.WARP_FUEL))
            return 0;
        return stack.is(ItemRegistry.STAR_COIN.get()) ? STAR_COIN_VALUE : 1;
    }

    private static int countCoins(Player player) {
        Inventory inventory = player.getInventory();
        int total = 0;

        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            total += WarpPrice.getCoinValue(stack) * stack.getCount();
        }
        return total;
    }

    private static void consumeCoins(Player player, int price) {
        Inventory inventory = player.getInventory();
        int remaining = price;

        for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; slot++) {
            ItemStack stack = inventory.getItem(slot);

            if (WarpPrice.getCoinValue(stack) == 1) {
                int used = Math.min(stack.getCount(), remaining);
                stack.shrink(used);
                remaining -= used;
            }
        }

        for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; slot++) {
            ItemStack stack = inventory.getItem(slot);
            int value = WarpPrice.getCoinValue(stack);

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

    private static int countItems(Player player, ItemStack item) {
        Inventory inventory = player.getInventory();
        int total = 0;

        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (ItemStack.isSameItem(stack, item))
                total += stack.getCount();
        }
        return total;
    }

    private static void consumeItems(Player player, ItemStack item, int count) {
        Inventory inventory = player.getInventory();
        int remaining = count;

        for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; slot++) {
            ItemStack stack = inventory.getItem(slot);

            if (ItemStack.isSameItem(stack, item)) {
                int used = Math.min(stack.getCount(), remaining);
                stack.shrink(used);
                remaining -= used;
            }
        }
        inventory.setChanged();
    }

    private static int getTotalExperience(Player player) {
        int total = 0;

        for (int level = 0; level < player.experienceLevel; level++)
            total += WarpPrice.getXpNeededForLevel(level);
        return total + Mth.floor(player.experienceProgress * player.getXpNeededForNextLevel());
    }

    public static int getLevelForExperience(int experience) {
        int level = 0;

        for (int total = 0; total < experience; level++)
            total += WarpPrice.getXpNeededForLevel(level);
        return level;
    }

    public static int getExperienceForLevel(int level) {
        int total = 0;

        for (int currentLevel = 0; currentLevel < level; currentLevel++)
            total += WarpPrice.getXpNeededForLevel(currentLevel);
        return total;
    }

    private static int getXpNeededForLevel(int level) {
        if (level >= 30)
            return 112 + (level - 30) * 9;
        return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
    }
}
