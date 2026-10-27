package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.integration.sable_compat.SableProvider;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class WarpXpCost {
    public static int getCost(Entity entity, Level level, BlockPos source, @Nullable BlockPos destination, double multiplier) {
        if (multiplier <= 0.0 || destination == null || !(entity instanceof Player player) || player.getAbilities().instabuild)
            return 0;

        BlockPos worldSource = SableProvider.isLoaded() ? SableProvider.toWorldPos(level, source) : source;
        BlockPos worldDestination = SableProvider.isLoaded() ? SableProvider.toWorldPos(level, destination) : destination;
        if (worldSource == null || worldDestination == null)
            return 0;

        return Mth.ceil(Math.sqrt(worldSource.distSqr(worldDestination)) * multiplier);
    }

    public static double getBlockMultiplier(BlockState state) {
        if (state.getBlock() instanceof DoorBlock)
            return ConfigRegistry.WARP_DOOR_XP_COST_MULTIPLIER.get();
        if (state.getBlock() instanceof TrapDoorBlock)
            return ConfigRegistry.WARP_TRAPDOOR_XP_COST_MULTIPLIER.get();
        if (state.getBlock() instanceof WarpPipeBlock)
            return ConfigRegistry.WARP_PIPE_XP_COST_MULTIPLIER.get();
        return 0.0;
    }

    public static boolean canAfford(Entity entity, int cost) {
        if (cost <= 0 || !(entity instanceof Player player))
            return true;

        if (WarpXpCost.getTotalExperience(player) >= cost)
            return true;

        if (!player.level().isClientSide)
            player.displayClientMessage(Component.translatable("display.marioverse.warp_not_enough_xp", WarpXpCost.getLevelForExperience(cost))
                    .withStyle(ChatFormatting.RED), true);
        return false;
    }

    public static void chargeIfWarped(Entity entity, Vec3 startPos, int cost) {
        if (cost > 0 && entity instanceof Player player && !player.level().isClientSide && !player.position().equals(startPos))
            player.giveExperiencePoints(-cost);
    }

    private static int getTotalExperience(Player player) {
        int total = 0;

        for (int level = 0; level < player.experienceLevel; level++)
            total += WarpXpCost.getXpNeededForLevel(level);
        return total + Mth.floor(player.experienceProgress * player.getXpNeededForNextLevel());
    }

    private static int getLevelForExperience(int experience) {
        int level = 0;

        for (int total = 0; total < experience; level++)
            total += WarpXpCost.getXpNeededForLevel(level);
        return level;
    }

    private static int getXpNeededForLevel(int level) {
        if (level >= 30)
            return 112 + (level - 30) * 9;
        return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
    }
}
