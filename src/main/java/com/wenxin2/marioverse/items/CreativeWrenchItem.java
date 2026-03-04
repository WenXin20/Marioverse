package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.blocks.OnOffSwitchBlock;
import com.wenxin2.marioverse.blocks.ToggleableBlock;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import com.wenxin2.marioverse.world.GlobalSwitchSavedData;
import com.wenxin2.marioverse.world.LinkedSwitchSavedData;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.valueproviders.UniformInt;
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
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

public class CreativeWrenchItem extends LinkerItem {
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
        float pitch = 0.9F + level.random.nextFloat() * 0.2F;

        if (player != null && !player.isShiftKeyDown() && state.getBlock() instanceof ToggleableBlock
                && !stack.has(DataComponentRegistry.LINKED_POS)) {
            player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.no_switch"), true);

            if (level instanceof ServerLevel server)
                ServerParticleUtils.spawnParticlesOnBlockFaces(ParticleTypes.CRIT,
                        server, pos, UniformInt.of(6, 8));
            this.playSound(level, pos, SoundRegistry.WRENCH_LINK_FAILED.get(), SoundSource.BLOCKS, 1.0F, pitch);
            return InteractionResult.SUCCESS;
        }

        if (state.getBlock() instanceof ToggleableBlock && stack.has(DataComponentRegistry.LINKED_POS)) {
            BlockPos posSwitch = getLinkedPos(stack);
            BlockState stateSwitch = level.getBlockState(posSwitch);

            if (!(stateSwitch.getBlock() instanceof OnOffSwitchBlock)) {
                if (player != null)
                    player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.not_switch"), true);

                if (level instanceof ServerLevel server)
                    ServerParticleUtils.spawnParticlesOnBlockFaces(ParticleTypes.CRIT,
                            server, pos, UniformInt.of(6, 8));
                this.playSound(level, pos, SoundRegistry.WRENCH_LINK_FAILED.get(), SoundSource.BLOCKS, 1.0F, pitch);

                stack.remove(DataComponentRegistry.LINKED_BLOCK);
                stack.remove(DataComponentRegistry.LINKED_POS);
                setLinked(stack, false);
                return InteractionResult.SUCCESS;
            }

            if (level instanceof ServerLevel server) {
                LinkedSwitchSavedData data = LinkedSwitchSavedData.get(server);

                if (player != null && !data.isLinked(posSwitch, pos) && !player.isShiftKeyDown()) {
                    player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.block_linked",
                            stateSwitch.getBlock().getName(), state.getBlock().getName()).withStyle(ChatFormatting.GOLD), true);

                    ServerParticleUtils.spawnParticlesOnBlockFaces(ParticleRegistry.RED_STAR.get(),
                            server, pos, UniformInt.of(6, 8));
                    this.playSound(level, pos, SoundRegistry.WRENCH_LINKED_BLOCK.get(), SoundSource.BLOCKS, 1.0F, pitch);

                    GlobalSwitchSavedData.get(server).unlink(pos);
                    data.link(posSwitch, pos);

                    return InteractionResult.SUCCESS;
                } else if (player != null && data.isLinked(pos) && player.isShiftKeyDown()) {
                    player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.block_unlinked",
                            stateSwitch.getBlock().getName(), state.getBlock().getName()).withStyle(ChatFormatting.RED), true);

                    ServerParticleUtils.spawnParticlesOnBlockFaces(ParticleRegistry.BLUE_STAR.get(),
                            server, pos, UniformInt.of(6, 8));
                    this.playSound(level, pos, SoundRegistry.WRENCH_UNLINKED_BLOCK.get(), SoundSource.BLOCKS, 1.0F, pitch);

                    GlobalSwitchSavedData.get(server).link(pos);
                    data.unlink(pos);

                    return InteractionResult.SUCCESS;
                }
            }
        }

        if (player != null && player.isShiftKeyDown() && state.getBlock() instanceof OnOffSwitchBlock) {
            player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.bound",
                    state.getBlock().getName()).withStyle(ChatFormatting.GREEN), true);

            if (level instanceof ServerLevel server) {
                ServerParticleUtils.spawnParticlesOnBlockFaces(ParticleRegistry.BLUE_STAR.get(),
                        server, pos, UniformInt.of(3, 4));
                ServerParticleUtils.spawnParticlesOnBlockFaces(ParticleRegistry.RED_STAR.get(),
                        server, pos, UniformInt.of(3, 4));
            }
            this.playSound(level, pos, SoundRegistry.WRENCH_LINK_CREATED.get(), SoundSource.BLOCKS, 1.0F, pitch);

            setLinkedPos(stack, pos);
            setLinkedBlock(stack, state);
            setLinked(stack, true);
            if (level instanceof ServerLevel server)
                GlobalSwitchSavedData.get(server).unlink(pos);
            return InteractionResult.SUCCESS;
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
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.right_click.link_switch"));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.right_click.link_switch.line2"));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click"));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click.bind_switch"));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click.unlink_switch"));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click.unlink_switch.line2"));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click.refill"));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click.refill.line2"));

            warpableText = warpableText.append(Component.translatable(this.getDescriptionId() + ".tooltip." +
                    "shift_right_click.pipe"));
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

        if (getIsLinked(stack) && stack.has(DataComponentRegistry.LINKED_POS)) {
            list.add(Component.literal(""));

            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.bound", true)
                    .withStyle(ChatFormatting.GOLD));

            if (stack.has(DataComponentRegistry.LINKED_BLOCK.get()))
                list.add(Component.translatable(this.getDescriptionId() + ".tooltip.bound.block",
                        getLinkedBlock(stack), true).withStyle(ChatFormatting.GRAY));

            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.bound.x",
                    getLinkedPos(stack).getX(), true).withStyle(ChatFormatting.GRAY));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.bound.y",
                    getLinkedPos(stack).getY(), true).withStyle(ChatFormatting.GRAY));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.bound.z",
                    getLinkedPos(stack).getZ(), true).withStyle(ChatFormatting.GRAY));
        }
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

    public static BlockPos getLinkedPos(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.LINKED_POS, null);
    }

    public static void setLinkedPos(ItemStack stack, BlockPos warpPos) {
        stack.set(DataComponentRegistry.LINKED_POS, warpPos);
    }

    public static void removeLinkedPos(ItemStack stack) {
        stack.remove(DataComponentRegistry.LINKED_POS);
    }

    public static String getLinkedBlock(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.LINKED_BLOCK, "");
    }

    public static void setLinkedBlock(ItemStack stack, BlockState blockState) {
        String blockName = blockState.getBlock().getName().getString();
        stack.set(DataComponentRegistry.LINKED_BLOCK.get(), blockName);
    }

    public static boolean getIsLinked(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.IS_LINKED.get(), false);
    }

    public static void setLinked(ItemStack stack, boolean isLinked) {
        stack.set(DataComponentRegistry.IS_LINKED.get(), isLinked);
    }
}