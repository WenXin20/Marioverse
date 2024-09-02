package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.init.ModTags;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;

public class WrenchItem extends LinkerItem {
    private final Tier tier;
    public WrenchItem(final Item.Properties properties, Tier tier) {
        super(properties.component(DataComponents.TOOL, createToolProperties()), tier);
        this.tier = tier;
    }

    public WrenchItem(Item.Properties properties, Tier tier, Tool toolComponentData) {
        super(properties.component(DataComponents.TOOL, toolComponentData), tier);
        this.tier = tier;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {

        list.add(Component.literal(""));

        if (Screen.hasShiftDown()) {
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.right_click").withStyle(ChatFormatting.DARK_GREEN));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click").withStyle(ChatFormatting.BLUE));

        } else {
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
        }

        super.appendHoverText(stack, tooltipContext, list, tooltip);
    }

    @Override
    public int getEnchantmentValue() {
        return this.tier.getEnchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairStack) {
        return repairStack.is(Tags.Items.INGOTS_COPPER) || super.isValidRepairItem(stack, repairStack);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity livingEntity, LivingEntity hurtEntity) {
        stack.hurtAndBreak(2, hurtEntity, EquipmentSlot.MAINHAND);
        return true;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    public static Tool createToolProperties() {
        return new Tool(List.of(Tool.Rule.overrideSpeed(ModTags.WRENCH_EFFICIENT, 1.5F)), 1.0F, 2);
    }

    public static ItemAttributeModifiers createAttributes(Tier tier, int attackDamage, float attackSpeed) {
        return createAttributes(tier, (float) attackDamage, attackSpeed);
    }

    public static ItemAttributeModifiers createAttributes(Tier tier, float attackDamage, float attackSpeed) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID,
                                attackDamage + tier.getAttackDamageBonus(),
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)

                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID,
                                attackSpeed,
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND).build();
    }
}
