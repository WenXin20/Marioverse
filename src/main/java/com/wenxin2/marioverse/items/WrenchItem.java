package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.blocks.OnOffSwitchBlock;
import com.wenxin2.marioverse.blocks.entities.ArrowSignBlockEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import gardensofthedead.block.entity.HangingSignBlockEntity;
import gardensofthedead.block.entity.SignBlockEntity;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

public class WrenchItem extends LinkerItem {
    private final Tier tier;
    public WrenchItem(final Item.Properties properties, Tier tier) {
        super(properties.component(DataComponents.TOOL, WrenchItem.createToolProperties()), tier);
        this.tier = tier;
    }

    public WrenchItem(Item.Properties properties, Tier tier, Tool toolComponentData) {
        super(properties.component(DataComponents.TOOL, toolComponentData), tier);
        this.tier = tier;
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Player player = useOnContext.getPlayer();
        Level level = useOnContext.getLevel();
        BlockPos pos = useOnContext.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof OnOffSwitchBlock) {
            if (state.getValue(OnOffSwitchBlock.RADIUS) < 16) {
                if (player != null) {
                    if (player.isShiftKeyDown() && state.getValue(OnOffSwitchBlock.RADIUS) == 1) {
                        player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.global"), true);
                        level.setBlock(pos, state.setValue(OnOffSwitchBlock.RADIUS, state.getValue(OnOffSwitchBlock.RADIUS) - 1), 3);
                    } else if (player.isShiftKeyDown() && state.getValue(OnOffSwitchBlock.RADIUS) > 0) {
                        player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.radius",
                                state.getValue(OnOffSwitchBlock.RADIUS) - 1).withStyle(ChatFormatting.BLUE), true);
                        level.setBlock(pos, state.setValue(OnOffSwitchBlock.RADIUS, state.getValue(OnOffSwitchBlock.RADIUS) - 1), 3);
                    } else if (player.isShiftKeyDown() && state.getValue(OnOffSwitchBlock.RADIUS) == 0) {
                        player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.radius",
                                state.getValue(OnOffSwitchBlock.RADIUS) + 16).withStyle(ChatFormatting.BLUE), true);
                        level.setBlock(pos, state.setValue(OnOffSwitchBlock.RADIUS, 16), 3);
                    } else {
                        player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.radius_subtracted",
                                state.getValue(OnOffSwitchBlock.RADIUS) + 1).withStyle(ChatFormatting.BLUE), true);
                        level.setBlock(pos, state.setValue(OnOffSwitchBlock.RADIUS, state.getValue(OnOffSwitchBlock.RADIUS) + 1), 3);
                    }
                }
            } else {
                if (player != null)
                    player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.global"), true);
                level.setBlock(pos, state.setValue(OnOffSwitchBlock.RADIUS, 0), 3);
            }
            int soundPitch = state.getValue(OnOffSwitchBlock.RADIUS) / 16;
            level.playSound(null, pos, SoundRegistry.SWITCH_RADIUS_TOGGLED.get(),
                    SoundSource.BLOCKS, 1.0F, 0.7F + 0.5F * soundPitch);
            return InteractionResult.sidedSuccess(true);
        } else if (state.hasProperty(BlockStateProperties.ROTATION_16)) {
            WrenchItem.rotateRotation16(level, state, pos);
            return InteractionResult.sidedSuccess(true);
        }
        return super.useOn(useOnContext);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
        MutableComponent warpableText = Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click.binds");

        if (Screen.hasShiftDown()) {
            list.add(Component.literal(""));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.right_click"));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.right_click.gui"));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.right_click.on_off_switch"));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click"));

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

        super.appendHoverText(stack, tooltipContext, list, tooltip);
    }

    @Override
    public int getEnchantmentValue() {
        return this.tier.getEnchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairStack) {
        return repairStack.is(Tags.Items.INGOTS_IRON) || super.isValidRepairItem(stack, repairStack);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity livingEntity, LivingEntity hurtEntity) {
        stack.hurtAndBreak(2, hurtEntity, LivingEntity.getSlotForHand(livingEntity.getUsedItemHand()));
        return true;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    public static Tool createToolProperties() {
        return new Tool(List.of(Tool.Rule.overrideSpeed(TagRegistry.WRENCH_EFFICIENT, 1.5F)), 1.0F, 2);
    }

    public static ItemAttributeModifiers createAttributes(Tier tier, int attackDamage, float attackSpeed) {
        return createAttributes(tier, (float) attackDamage, attackSpeed);
    }

    public static ItemAttributeModifiers createAttributes(Tier tier, float attackDamage, float attackSpeed) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage + tier.getAttackDamageBonus(),
                        AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)

                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed,
                        AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build();
    }

    public static boolean rotateRotation16(Level level, BlockState state, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof ArrowSignBlockEntity arrowSignBE
                && arrowSignBE.isWaxed())
            return false;
        if (level.getBlockEntity(pos) instanceof SignBlockEntity signBE
                && signBE.isWaxed())
            return false;
        if (level.getBlockEntity(pos) instanceof HangingSignBlockEntity hangingSignBE
                && hangingSignBE.isWaxed())
            return false;

        if (!level.isClientSide) {
            int current = state.getValue(BlockStateProperties.ROTATION_16);
            level.setBlock(pos, state.setValue(BlockStateProperties.ROTATION_16,
                    (current + 1) % 16), Block.UPDATE_CLIENTS);
        }
        return true;
    }
}
