package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.blocks.entities.WarpDoorBlockEntity;
import java.util.List;
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
import net.minecraft.world.level.block.DoorBlock;
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

            if (!(blockEntity instanceof WarpDoorBlockEntity)) {
                if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
                    world.setBlockEntity(new WarpDoorBlockEntity(pos.below(), state));
                    this.spawnParticles(ParticleTypes.PORTAL, world, pos.below(), 30);
                }
                if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
                    world.setBlockEntity(new WarpDoorBlockEntity(pos, state));
                    this.spawnParticles(ParticleTypes.PORTAL, world, pos, 30);
                }
                if (player != null) {
                    if (!player.isCreative())
                        stack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(useOnContext);
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

            world.addParticle(particleType, x, y, z, 0, 1.0, 0);
            world.addParticle(particleType, x, y + 0.5, z, 0, 1.0, 0);
            world.addParticle(particleType, x, y + 1.0, z, 0, 1.0, 0);
        }
    }
}
