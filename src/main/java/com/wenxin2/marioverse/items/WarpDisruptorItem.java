package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpDoorBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpTrapDoorBlockEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class WarpDisruptorItem extends Item {
    public WarpDisruptorItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
        MutableComponent rCText = Component.translatable(this.getDescriptionId() + ".tooltip.right_click.selected");
        MutableComponent shiftRCText = Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click.prevents");
        MutableComponent shiftRCx2Text = Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click_2.breaks");

        if (Screen.hasShiftDown()) {
            list.add(Component.literal(""));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.right_click"));

            rCText = rCText.append(Component.translatable(this.getDescriptionId() + ".tooltip.right_click.mob"));
            if (!ConfigRegistry.DISABLE_PLAYER_WARP_DISRUPTING.get())
                rCText = rCText.append(Component.translatable(this.getDescriptionId() + ".tooltip.right_click.player"));
            rCText = rCText.append(Component.translatable(this.getDescriptionId() + ".tooltip.right_click.warping"));
            list.add(rCText);

            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click"));

            shiftRCText = shiftRCText.append(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click.pipe"));
            if (!ConfigRegistry.DISABLE_WARP_DOORS.get())
                shiftRCText = shiftRCText.append(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click.door"));
            if (!ConfigRegistry.DISABLE_WARP_TRAPDOORS.get())
                shiftRCText = shiftRCText.append(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click.trapdoor"));
            if (!ConfigRegistry.DISABLE_WARP_PAINTINGS.get())
                shiftRCText = shiftRCText.append(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click.painting"));
            shiftRCText = shiftRCText.append(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click.warping"));
            list.add(shiftRCText);

            if (!ConfigRegistry.DISABLE_WARP_TRAPDOORS.get() || !ConfigRegistry.DISABLE_WARP_DOORS.get() || !ConfigRegistry.DISABLE_WARP_PAINTINGS.get()) {
                list.add(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click_2"));
                if (!ConfigRegistry.DISABLE_WARP_DOORS.get())
                    shiftRCx2Text = shiftRCx2Text.append(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click_2.door"));
                if (!ConfigRegistry.DISABLE_WARP_TRAPDOORS.get())
                    shiftRCx2Text = shiftRCx2Text.append(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click_2.trapdoor"));
                if (!ConfigRegistry.DISABLE_WARP_PAINTINGS.get())
                    shiftRCx2Text = shiftRCx2Text.append(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click_2.painting"));
                shiftRCx2Text = shiftRCx2Text.append(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click_2.warping"));
                list.add(shiftRCx2Text);
            }
        } else
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip"));

        super.appendHoverText(stack, tooltipContext, list, tooltip);
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Player player = useOnContext.getPlayer();
        Level world = useOnContext.getLevel();
        BlockPos pos = useOnContext.getClickedPos();
        BlockState state = world.getBlockState(pos);
        ItemStack stack = useOnContext.getItemInHand();
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (!ConfigRegistry.DISABLE_WARP_DOORS.get() && state.getBlock() instanceof DoorBlock) {
            BlockEntity blockEntityBelow = world.getBlockEntity(pos.below());

            if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER
                    && blockEntity instanceof WarpDoorBlockEntity doorBE
                    && (!doorBE.preventWarp || !doorBE.breakDoor)) {
                if (doorBE.isWaxed() && ConfigRegistry.WAX_DISABLES_WARP_LINKING.get()) {
                    if (player != null)
                        player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.waxed",
                            state.getBlock().getName()).withStyle(ChatFormatting.GOLD), true);
                    return InteractionResult.sidedSuccess(Boolean.TRUE);
                } else if (doorBE.preventWarp) {
                    spawnParticles(ParticleTypes.WARPED_SPORE, world, pos, 16);
                    if (player != null)
                        player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.break_door",
                                state.getBlock().getName()).withStyle(ChatFormatting.DARK_AQUA), true);
                    doorBE.setBreakDoor(Boolean.TRUE);
                } else {
                    spawnParticles(ParticleTypes.CRIMSON_SPORE, world, pos, 16);
                    if (player != null)
                        player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.prevent_door_warp", 50), true);
                    doorBE.setPreventWarp(Boolean.TRUE);
                }

                if (player != null) {
                    if (!player.isCreative())
                        stack.hurtAndBreak(1, player, Player.getSlotForHand(player.getUsedItemHand()));
                }
                doorBE.markUpdated();
                world.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
                return InteractionResult.SUCCESS;
            } else if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER
                    && blockEntityBelow instanceof WarpDoorBlockEntity doorBE
                    && (!doorBE.preventWarp || !doorBE.breakDoor)) {
                if (doorBE.isWaxed() && ConfigRegistry.WAX_DISABLES_WARP_LINKING.get()) {
                    if (player != null)
                        player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.waxed",
                                state.getBlock().getName()).withStyle(ChatFormatting.GOLD), true);
                    return InteractionResult.sidedSuccess(Boolean.TRUE);
                } else if (doorBE.preventWarp) {
                    spawnParticles(ParticleTypes.WARPED_SPORE, world, pos, 16);
                    if (player != null)
                        player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.break_door",
                                state.getBlock().getName()).withStyle(ChatFormatting.DARK_AQUA), true);
                    doorBE.setBreakDoor(Boolean.TRUE);
                } else {
                    spawnParticles(ParticleTypes.CRIMSON_SPORE, world, pos, 16);
                    if (player != null)
                        player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.prevent_door_warp"), true);
                    doorBE.setPreventWarp(Boolean.TRUE);
                }

                if (player != null) {
                    if (!player.isCreative())
                        stack.hurtAndBreak(1, player, Player.getSlotForHand(player.getUsedItemHand()));
                }
                doorBE.markUpdated();
                world.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
                return InteractionResult.SUCCESS;
            }
        } else if (!ConfigRegistry.DISABLE_WARP_TRAPDOORS.get() && blockEntity instanceof WarpTrapDoorBlockEntity warpTrapdoorBE
                && (!warpTrapdoorBE.preventWarp || !warpTrapdoorBE.breakTrapdoor)) {
            if (warpTrapdoorBE.isWaxed() && ConfigRegistry.WAX_DISABLES_WARP_LINKING.get()) {
                if (player != null)
                    player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.waxed",
                            state.getBlock().getName()).withStyle(ChatFormatting.GOLD), true);
                return InteractionResult.sidedSuccess(Boolean.TRUE);
            } else if (warpTrapdoorBE.preventWarp) {
                spawnParticles(ParticleTypes.WARPED_SPORE, world, pos, 16);
                if (player != null)
                    player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.break_trapdoor",
                            state.getBlock().getName()).withStyle(ChatFormatting.DARK_AQUA), true);
                warpTrapdoorBE.setBreakTrapdoor(Boolean.TRUE);
            } else {
                spawnParticles(ParticleTypes.CRIMSON_SPORE, world, pos, 16);
                if (player != null)
                    player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.prevent_trapdoor_warp"), true);
                warpTrapdoorBE.setPreventWarp(Boolean.TRUE);
            }

            if (player != null) {
                if (!player.isCreative())
                    stack.hurtAndBreak(1, player, Player.getSlotForHand(player.getUsedItemHand()));
            }
            warpTrapdoorBE.markUpdated();
            world.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
            return InteractionResult.SUCCESS;
        } else if (blockEntity instanceof BaseWarpBlockEntity warpBE && !warpBE.preventWarp) {
            warpBE.setPreventWarp(Boolean.TRUE);
            spawnParticles(ParticleTypes.CRIMSON_SPORE, world, pos, 16);

            if (player != null) {
                if (warpBE.isWaxed() && ConfigRegistry.WAX_DISABLES_WARP_LINKING.get()) {
                    player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.waxed",
                            state.getBlock().getName()).withStyle(ChatFormatting.GOLD), true);
                    return InteractionResult.sidedSuccess(Boolean.TRUE);
                } else if (state.getBlock() instanceof WarpPipeBlock)
                    player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.prevent_pipe_warp"), true);
                if (!player.isCreative())
                    stack.hurtAndBreak(1, player, Player.getSlotForHand(player.getUsedItemHand()));
            }
            warpBE.markUpdated();
            world.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
            return InteractionResult.SUCCESS;
        }
        return super.useOn(useOnContext);
    }

    @NotNull
    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity livingEntity, InteractionHand hand) {
        if (!livingEntity.getPersistentData().getBoolean("marioverse:prevent_warp")) {
            if (livingEntity instanceof Player && !ConfigRegistry.DISABLE_PLAYER_WARP_DISRUPTING.get()) {
                livingEntity.getPersistentData().putBoolean("marioverse:prevent_warp", true);
                livingEntity.getPersistentData().putInt("marioverse:prevent_warp_cooldown", ConfigRegistry.WARP_DISRUPTING_COOLDOWN.get());
                player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.prevent_player_warp",
                        player.getDisplayName(), ConfigRegistry.WARP_DISRUPTING_COOLDOWN.get()).withStyle(ChatFormatting.RED), true);
                this.spawnEntityParticles(ParticleTypes.CRIMSON_SPORE, player, livingEntity.level(), 16);

                if (!player.isCreative())
                    stack.hurtAndBreak(1, player, Player.getSlotForHand(player.getUsedItemHand()));
                return InteractionResult.SUCCESS;
            } else {
                livingEntity.getPersistentData().putBoolean("marioverse:prevent_warp", true);
                player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.prevent_entity_warp",
                        livingEntity.getDisplayName()).withStyle(ChatFormatting.RED), true);
                this.spawnEntityParticles(ParticleTypes.CRIMSON_SPORE, livingEntity, livingEntity.level(), 16);

                if (!player.isCreative())
                    stack.hurtAndBreak(1, player, Player.getSlotForHand(player.getUsedItemHand()));
                return InteractionResult.SUCCESS;
            }
        } return InteractionResult.PASS;
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        AttributeInstance reachAttribute = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        double reachDistance = player.isCreative() ? 5.0D : 4.5D;
        if (reachAttribute != null)
            reachDistance = reachAttribute.getValue();
        HitResult hitResult = player.pick(reachDistance, 0.0F, false);

        if (hitResult.getType() == HitResult.Type.MISS) {
            if (!ConfigRegistry.DISABLE_PLAYER_WARP_DISRUPTING.get()) {
                if (!player.getPersistentData().getBoolean("marioverse:prevent_warp")) {
                    player.getPersistentData().putBoolean("marioverse:prevent_warp", true);
                    player.getPersistentData().putInt("marioverse:prevent_warp_cooldown", ConfigRegistry.WARP_DISRUPTING_COOLDOWN.get());
                    player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.prevent_player_warp",
                            player.getDisplayName(), ConfigRegistry.WARP_DISRUPTING_COOLDOWN.get()).withStyle(ChatFormatting.RED), true);
                    this.spawnEntityParticles(ParticleTypes.CRIMSON_SPORE, player, world, 16);

                    if (!player.isCreative())
                        stack.hurtAndBreak(1, player, Player.getSlotForHand(player.getUsedItemHand()));
                    return InteractionResultHolder.success(stack);
                }
            }
        } else return InteractionResultHolder.pass(stack);
        return super.use(world, player, hand);
    }

    public static void spawnParticles(ParticleOptions particleType, Level world, BlockPos pos, int avgAmount) {
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

    public void spawnEntityParticles(ParticleOptions particleType, Entity entity, Level world, int avgAmount) {
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
