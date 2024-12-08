package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.blocks.entities.WarpDoorBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpTrapDoorBlockEntity;
import com.wenxin2.marioverse.init.ConfigRegistry;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class WarpDoorRuneItem extends Item {
    public WarpDoorRuneItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
        if (Screen.hasShiftDown()) {
            list.add(Component.literal(""));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click"));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click.line2"));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click.line3"));
        } else
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip"));

        super.appendHoverText(stack, tooltipContext, list, tooltip);
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Player player = useOnContext.getPlayer();
        Level world = useOnContext.getLevel();
        BlockPos pos = useOnContext.getClickedPos();
        BlockState state = world.getBlockState(pos);
        ItemStack stack = useOnContext.getItemInHand();
        BlockEntity blockEntity = world.getBlockEntity(pos);
        BlockEntity blockEntityBelow = world.getBlockEntity(pos.below());

        if (state.getBlock() instanceof DoorBlock) {
            if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER && !(blockEntity instanceof WarpDoorBlockEntity)) {
                world.setBlockEntity(new WarpDoorBlockEntity(pos, state));
                world.blockEntityChanged(pos);
                world.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
                this.spawnParticles(ParticleTypes.PORTAL, world, pos, 30);
                if (player != null) {
                    player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.convert_door",
                            state.getBlock().getName()).withStyle(ChatFormatting.AQUA), true);
                    if (!player.isCreative())
                        stack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            } else if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER && !(blockEntityBelow instanceof WarpDoorBlockEntity)) {
                world.setBlockEntity(new WarpDoorBlockEntity(pos.below(), state));
                world.blockEntityChanged(pos.below());
                world.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
                this.spawnParticles(ParticleTypes.PORTAL, world, pos.below(), 30);
                if (player != null) {
                    player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.convert_door",
                            state.getBlock().getName()).withStyle(ChatFormatting.AQUA), true);
                    if (!player.isCreative())
                        stack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        } else if (state.getBlock() instanceof TrapDoorBlock) {
            if (!(blockEntity instanceof WarpTrapDoorBlockEntity)) {
                world.setBlockEntity(new WarpTrapDoorBlockEntity(pos, state));
                world.blockEntityChanged(pos);
                world.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
                this.spawnParticles(ParticleTypes.PORTAL, world, pos, 30);
                if (player != null) {
                    player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.convert_trapdoor",
                            state.getBlock().getName()).withStyle(ChatFormatting.AQUA), true);
                    if (!player.isCreative())
                        stack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public void spawnParticles(ParticleOptions particleType, Level world, BlockPos pos, int avgAmount) {
        BlockState state = world.getBlockState(pos);
        float scaleFactor = 1;
        int numParticles = (int) (scaleFactor * avgAmount);
        double radius = 0.65;

        for (int i = 0; i < numParticles; i++) {
            // Calculate angle for each particle
            double angle = 2 * Math.PI * i / numParticles;
            // Calculate the X and Z offset using sine and cosine to spread in an ellipse
            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;

            double x = pos.getX() + 0.5 + offsetX;
            double y = pos.getY();
            double z = pos.getZ() + 0.5 + offsetZ;

            if (state.getBlock() instanceof DoorBlock) {
                world.addParticle(particleType, x, y, z, 0, 1.0, 0);
                world.addParticle(particleType, x, y + 0.5, z, 0, 1.0, 0);
                world.addParticle(particleType, x, y + 1.0, z, 0, 1.0, 0);
            } else {
                world.addParticle(particleType, x, y, z, 0, 1.0, 0);
                world.addParticle(particleType, x, y - 0.5, z, 0, 1.0, 0);
            }
        }
    }
}
