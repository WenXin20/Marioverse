package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.blocks.OnOffSwitchBlock;
import com.wenxin2.marioverse.blocks.ToggleableBlock;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.world.GlobalSwitchSavedData;
import com.wenxin2.marioverse.world.LinkedSwitchSavedData;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CreativeWrenchItem extends WrenchItem {
    private final Tier tier;
    public CreativeWrenchItem(final Properties properties, Tier tier) {
        super(properties.component(DataComponents.TOOL, CreativeWrenchItem.createToolProperties()), tier);
        this.tier = tier;
    }

    public CreativeWrenchItem(Properties properties, Tier tier, Tool toolComponentData) {
        super(properties.component(DataComponents.TOOL, toolComponentData), tier);
        this.tier = tier;
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Player player = useOnContext.getPlayer();
        Level level = useOnContext.getLevel();
        ItemStack stack = useOnContext.getItemInHand();
        BlockPos pos = useOnContext.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (level instanceof ServerLevel serverLevel) {
            LinkedSwitchSavedData data = LinkedSwitchSavedData.get(serverLevel);

            if (player != null && player.isShiftKeyDown() && state.getBlock() instanceof OnOffSwitchBlock) {
                player.displayClientMessage(Component.literal("Switch selected"), true);
                setLinkedPos(stack, pos);
                if (level instanceof ServerLevel server)
                    GlobalSwitchSavedData.get(server).unlink(pos);
                return InteractionResult.SUCCESS;
            }

            if (state.getBlock() instanceof ToggleableBlock) {
                BlockPos switchPos = getLinkedPos(stack);

                if (player != null && !stack.has(DataComponentRegistry.LINKED_POS)) {
                    player.displayClientMessage(Component.literal("No switch selected"), true);
                    return InteractionResult.FAIL;
                }

                if (!(level.getBlockState(switchPos).getBlock() instanceof OnOffSwitchBlock)) {
                    if (player != null)
                        player.displayClientMessage(Component.literal("Stored switch is invalid"), true);
                    stack.remove(DataComponentRegistry.LINKED_POS);
                    return InteractionResult.FAIL;
                }

                if (player != null && !player.isShiftKeyDown()) {
                    player.displayClientMessage(Component.literal("Block linked"), true);

                    if (level instanceof ServerLevel server)
                        GlobalSwitchSavedData.get(server).unlink(pos);
                    data.link(switchPos, pos);
                } else {
                    if (player != null)
                        player.displayClientMessage(Component.literal("Block unlinked"), true);

                    if (level instanceof ServerLevel server)
                        GlobalSwitchSavedData.get(server).link(pos);
                    data.unlink(pos);
                }

                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
        MutableComponent warpableText = Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click.binds");

        if (Screen.hasShiftDown()) {
            list.add(Component.literal(""));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.right_click"));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.right_click.gui"));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.right_click.on_off_switch"));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.right_click.on_off_switch.line2"));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click"));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click.bind_switch"));

            warpableText = warpableText.append(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click.pipe"));
            if (!ConfigRegistry.DISABLE_WARP_DOORS.get())
                warpableText = warpableText.append(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click.door"));

            if (!ConfigRegistry.DISABLE_WARP_TRAPDOORS.get())
                warpableText = warpableText.append(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click.trapdoor"));

            if (!ConfigRegistry.DISABLE_WARP_PAINTINGS.get())
                warpableText = warpableText.append(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click.painting"));

            if (stack.is(ItemRegistry.CREATIVE_WRENCH.get()))
                warpableText = warpableText.append(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click.entities"));

            list.add(warpableText);

        } else list.add(Component.translatable(this.getDescriptionId() + ".tooltip"));
    }

    @Override
    public int getEnchantmentValue() {
        return this.tier.getEnchantmentValue();
    }

    public static BlockPos getLinkedPos(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.LINKED_POS, null);
    }

    public static void setLinkedPos(ItemStack stack, BlockPos warpPos) {
        stack.set(DataComponentRegistry.LINKED_POS, warpPos);
    }
}