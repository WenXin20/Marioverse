package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.KeybindRegistry;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import org.jetbrains.annotations.NotNull;

public class PowerUpSpawnEggItem extends DeferredSpawnEggItem {
    int tooltipLineAmt = 0;

    public PowerUpSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> entityType,
                               int primaryColor, int secondaryColor, Properties properties) {
        super(entityType, primaryColor, secondaryColor, properties);
    }

    public PowerUpSpawnEggItem(int tooltipLineAmt, Supplier<? extends EntityType<? extends Mob>> entityType,
                               int primaryColor, int secondaryColor, Properties properties) {
        super(entityType, primaryColor, secondaryColor, properties);
        this.tooltipLineAmt = tooltipLineAmt;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
        if (Screen.hasShiftDown() && this.tooltipLineAmt > 0) {
            list.add(Component.literal(""));

            for (int lineAmt = 1; lineAmt <= tooltipLineAmt; lineAmt++) {
                MutableComponent abilityText = Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt);

                if (stack.is(ItemRegistry.FIRE_FLOWER) && lineAmt == 5)
                    abilityText = abilityText.append(Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt + ".keybind",
                            KeybindRegistry.ACTIVATE_POWER_UP.getKey().getDisplayName()).withStyle(ChatFormatting.GREEN));

                if (stack.is(ItemRegistry.ICE_FLOWER) && lineAmt == 5)
                    abilityText = abilityText.append(Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt + ".keybind",
                            KeybindRegistry.ACTIVATE_POWER_UP.getKey().getDisplayName()).withStyle(ChatFormatting.GREEN));

                if (stack.is(ItemRegistry.MEGA_MUSHROOM) && lineAmt == 6)
                    abilityText = abilityText.append(Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt + ".hearts",
                            ConfigRegistry.MEGA_MUSHROOM_HEALTH.get() / 2).withStyle(ChatFormatting.RED));

                if (stack.is(ItemRegistry.MINI_MUSHROOM) && lineAmt == 6)
                    abilityText = abilityText.append(Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt + ".hearts",
                            ConfigRegistry.MINI_MUSHROOM_HEALTH.get() / 2).withStyle(ChatFormatting.RED));

                if (stack.is(ItemRegistry.ONE_UP_MUSHROOM) && lineAmt == 5)
                    abilityText = abilityText.append(Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt + ".hearts",
                            ConfigRegistry.ONE_UP_HEALTH_HEALED.get().floatValue() / 2).withStyle(ChatFormatting.GREEN));

                if (stack.is(ItemRegistry.SUPER_MUSHROOM) && lineAmt == 5)
                    abilityText = abilityText.append(Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt + ".hearts",
                            ConfigRegistry.SUPER_MUSHROOM_HEALTH_HEALED.get().floatValue() / 2).withStyle(ChatFormatting.RED));

                if (stack.is(ItemRegistry.SUPER_STAR) && lineAmt == 5)
                    abilityText = abilityText.append(Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt + ".instakill",
                            Math.round(ConfigRegistry.SUPER_STAR_DURATION.get() / 20.0F * 10.0F) / 10.0F).withStyle(ChatFormatting.GRAY));

                if (stack.is(ItemRegistry.SUPER_STAR) && lineAmt == 6)
                    abilityText = abilityText.append(Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt + ".speed",
                            Math.round(ConfigRegistry.SUPER_STAR_SPEED_DURATION.get() / 20.0F * 10.0F) / 10.0F)).withStyle(ChatFormatting.GRAY);
                list.add(abilityText);
            }
            list.add(Component.literal(""));

        } else if (this.tooltipLineAmt > 0)
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip"));
    }
}
