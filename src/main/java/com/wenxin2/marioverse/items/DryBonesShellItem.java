package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.entities.DryBonesPartEntity;
import com.wenxin2.marioverse.entities.HoldableEntity;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class DryBonesShellItem extends Item {
    private static final int TOOLTIP_LINE_AMOUNT = 2;

    public DryBonesShellItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
        if (Screen.hasShiftDown()) {
            list.add(Component.literal(""));
            for (int lineAmt = 1; lineAmt <= TOOLTIP_LINE_AMOUNT; lineAmt++)
                list.add(Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt));
            list.add(Component.literal(""));
        } else list.add(Component.translatable(this.getDescriptionId() + ".tooltip"));
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();

        if (player == null || !player.isSecondaryUseActive() || !player.getType().is(TagRegistry.CAN_PICKUP_AND_THROW_SHELLS))
            return InteractionResult.PASS;

        DryBonesPartEntity shell = new DryBonesPartEntity(EntityRegistry.DRY_BONES_SHELL.get(), level);
        Vec3 placePos = HoldableEntity.getPlacePosition(shell, player, context.getClickedPos().relative(context.getClickedFace()));

        if (placePos == null)
            return InteractionResult.FAIL;

        if (!level.isClientSide) {
            float pitch = 0.9F + level.random.nextFloat() * 0.2F;
            float placerYaw = HoldableEntity.getHolderYaw(player, 1.0F);

            shell.setPartType("shell");
            shell.moveTo(placePos.x, placePos.y, placePos.z, placerYaw, 0.0F);
            shell.yBodyRot = placerYaw;
            shell.setYHeadRot(placerYaw);
            level.addFreshEntity(shell);
            level.gameEvent(player, GameEvent.ENTITY_PLACE, shell.position());
            level.playSound(null, shell.blockPosition(), SoundEvents.ARMOR_EQUIP_TURTLE.value(), SoundSource.PLAYERS, 1.0F, pitch);
            context.getItemInHand().consume(1, player);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isSecondaryUseActive())
            return InteractionResultHolder.pass(stack);
        if (!player.getType().is(TagRegistry.CAN_PICKUP_AND_THROW_SHELLS))
            return InteractionResultHolder.fail(stack);

        if (!level.isClientSide) {
            DryBonesPartEntity shell = new DryBonesPartEntity(EntityRegistry.DRY_BONES_SHELL.get(), level);

            shell.setPartType("shell");
            shell.throwFrom(player);
            level.addFreshEntity(shell);
            level.gameEvent(player, GameEvent.ENTITY_PLACE, shell.position());

            if (!player.getType().is(TagRegistry.HAS_INFINITE_SHELL_AMMO))
                stack.consume(1, player);
        }

        player.swing(hand);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
