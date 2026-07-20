package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import java.util.List;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SuperMushroomItem extends Item {
    int tooltipLineAmt = 0;

    public SuperMushroomItem(Properties properties) {
        super(properties);
    }

    public SuperMushroomItem(int tooltipLineAmt, Properties properties) {
        super(properties);
        this.tooltipLineAmt = tooltipLineAmt;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
        if (Screen.hasShiftDown() && this.tooltipLineAmt > 0) {
            list.add(Component.literal(""));

            for (int lineAmt = 1; lineAmt <= tooltipLineAmt; lineAmt++) {
                MutableComponent abilityText = Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt);

                if (stack.is(ItemRegistry.SUPER_MUSHROOM) && lineAmt == 5)
                    abilityText = abilityText.append(Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt + ".hearts",
                            ConfigRegistry.SUPER_MUSHROOM_HEALTH_HEALED.get().floatValue() / 2).withStyle(ChatFormatting.RED));

                list.add(abilityText);
            }
            list.add(Component.literal(""));

        } else if (this.tooltipLineAmt > 0)
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip"));
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        LivingEntity rider = player.getControllingPassenger();
        ItemStack stack = player.getItemInHand(hand);

        if (player.getType().is(TagRegistry.POWERS_UP_RIDER) && player.hasControllingPassenger()
                && rider instanceof AbilitiesHandler handler) {
            handler.applySuperMushroomPowerUp(level, rider, null,
                    ConfigRegistry.SUPER_MUSHROOM_HEALTH_HEALED.get().floatValue());
            return InteractionResultHolder.success(stack);
        } else if (player instanceof LivingEntity livingEntity && player instanceof AbilitiesHandler handler) {
            handler.applySuperMushroomPowerUp(level, livingEntity, null,
                    ConfigRegistry.SUPER_MUSHROOM_HEALTH_HEALED.get().floatValue());
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }
}
