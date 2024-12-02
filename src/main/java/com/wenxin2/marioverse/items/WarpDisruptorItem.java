package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.WarpDoorBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.NotNull;

public class WarpDisruptorItem extends Item {
    public WarpDisruptorItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
        if (Screen.hasShiftDown()) {
            list.add(Component.literal(""));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click"));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click.line2"));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click_2"));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click_2.line2"));
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

        if (state.getBlock() instanceof DoorBlock) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            BlockEntity blockEntityBelow = world.getBlockEntity(pos.below());

            if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER
                    && blockEntity instanceof WarpDoorBlockEntity doorBlockEntity) {
                if (doorBlockEntity.preventWarp) {
                    this.spawnParticles(ParticleTypes.WARPED_SPORE, world, pos, 16);
                    if (player != null)
                        player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.prevent_door_warp", 50), true);
                    doorBlockEntity.setBreakDoor(Boolean.TRUE);
                } else {
                    this.spawnParticles(ParticleTypes.CRIMSON_SPORE, world, pos, 16);
                    if (player != null)
                        player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.break_door"), true);
                    doorBlockEntity.setPreventWarp(Boolean.TRUE);
                }

                if (player != null) {
                    if (!player.isCreative())
                        stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                }
                doorBlockEntity.markUpdated();
                return InteractionResult.SUCCESS;
            } else if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER
                    && blockEntityBelow instanceof WarpDoorBlockEntity doorBlockEntity) {
                if (doorBlockEntity.preventWarp) {
                    this.spawnParticles(ParticleTypes.WARPED_SPORE, world, pos, 16);
                    if (player != null)
                        player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.prevent_door_warp"), true);
                    doorBlockEntity.setBreakDoor(Boolean.TRUE);
                } else {
                    this.spawnParticles(ParticleTypes.CRIMSON_SPORE, world, pos, 16);
                    if (player != null)
                        player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.break_door"), true);
                    doorBlockEntity.setPreventWarp(Boolean.TRUE);
                }

                if (player != null) {
                    if (!player.isCreative())
                        stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                }
                doorBlockEntity.markUpdated();
                return InteractionResult.SUCCESS;
            }
        } else if (state.getBlock() instanceof WarpPipeBlock) {
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity) {
                pipeBlockEntity.setPreventWarp(Boolean.TRUE);
                pipeBlockEntity.markUpdated();
                this.spawnParticles(ParticleTypes.CRIMSON_SPORE, world, pos, 16);
                if (player != null) {
                    player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.prevent_pipe_warp"), true);
                    if (!player.isCreative())
                        stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(useOnContext);
    }

    @NotNull
    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity livingEntity, InteractionHand hand) {
        if (livingEntity instanceof Player) {
            livingEntity.getPersistentData().putBoolean("marioverse:prevent_warp", true);
            livingEntity.getPersistentData().putInt("marioverse:prevent_warp_cooldown", 50); // TODO
            player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.prevent_player_warp", player.getDisplayName(), 50).withStyle(ChatFormatting.RED), true);
            this.spawnEntityParticles(ParticleTypes.CRIMSON_SPORE, player, livingEntity.level(), livingEntity.blockPosition(), 16);
            if (!player.isCreative())
                stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            return InteractionResult.SUCCESS;
        } else {
            livingEntity.getPersistentData().putBoolean("marioverse:prevent_warp", true);
            player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.prevent_entity_warp", livingEntity.getDisplayName()).withStyle(ChatFormatting.RED), true);
            this.spawnEntityParticles(ParticleTypes.CRIMSON_SPORE, livingEntity, livingEntity.level(), livingEntity.blockPosition(), 16);
            if (!player.isCreative())
                stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            return InteractionResult.SUCCESS;
        }
    }

    public void spawnParticles(ParticleOptions particleType, Level world, BlockPos pos, int avgAmount) {
        float scaleFactor = 1;
        int numParticles = (int) (scaleFactor * avgAmount);
        double radius = 0.65;

        for (int i = 0; i < numParticles; i++) {
            // Calculate angle for each particle
            double angle = 2 * Math.PI * i / numParticles;
            // Calculate the X and Z offset using sine and cosine to spread in an ellipse
            double offsetX = Math.cos(angle) * radius;
            double offsetY = 0.5;
            double offsetZ = Math.sin(angle) * radius;

            double x = pos.getX() + 0.5 + offsetX;
            double y = pos.getY();
            double z = pos.getZ() + 0.5 + offsetZ;

            world.addParticle(particleType, x, y, z, 0, 0.5, 0);
            world.addParticle(particleType, x, y + 0.5, z, 0, 0.5, 0);
            world.addParticle(particleType, x, y + 1.0, z, 0, 0.5, 0);
        }
    }

    public void spawnEntityParticles(ParticleOptions particleType, Entity entity, Level world, BlockPos pos, int avgAmount) {
        float scaleFactor = entity.getBbWidth();
        int numParticles = (int) (scaleFactor * avgAmount);
        double radius = entity.getBbWidth() / 2;

        for (int i = 0; i < numParticles; i++) {
            // Calculate angle for each particle
            double angle = 2 * Math.PI * i / numParticles;
            // Calculate the X and Z offset using sine and cosine to spread in an ellipse
            double offsetX = Math.cos(angle) * radius;
            double offsetY = entity.getBbHeight();
            double offsetZ = Math.sin(angle) * radius;

            double x = entity.getX() + offsetX;
            double y = entity.getY();
            double z = entity.getZ() + offsetZ;

            world.addParticle(particleType, x, y + 0.2, z, 0, 1.0, 0);
            world.addParticle(particleType, x, y + offsetY / 2, z, 0, 1.0, 0);
            world.addParticle(particleType, x, y + offsetY - 0.2, z, 0, 1.0, 0);
        }
    }
}
