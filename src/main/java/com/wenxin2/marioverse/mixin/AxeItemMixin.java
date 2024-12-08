package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.marioverse.init.ConfigRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AxeItem.class)
public class AxeItemMixin {

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    public void onUseOnBlock(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();

        if (world.getBlockEntity(pos) instanceof BaseWarpBlockEntity warpBE
                && ConfigRegistry.ALLOW_WARP_UNWAXING.get()) {
            if (warpBE.isWaxed()) {
                warpBE.setWaxed(Boolean.FALSE);
                world.playSound(null, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
                ParticleUtils.spawnParticlesOnBlockFaces(world, pos, ParticleTypes.WAX_OFF, UniformInt.of(3, 5));

                if (state.getBlock() instanceof DoorBlock && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER)
                    ParticleUtils.spawnParticlesOnBlockFaces(world, pos.above(), ParticleTypes.WAX_OFF, UniformInt.of(3, 5));
                if (state.getBlock() instanceof DoorBlock && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER)
                    ParticleUtils.spawnParticlesOnBlockFaces(world, pos.below(), ParticleTypes.WAX_OFF, UniformInt.of(3, 5));

                warpBE.markUpdated();

                if (player != null)
                    stack.hurtAndBreak(1, player, Player.getSlotForHand(player.getUsedItemHand()));

                world.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
                cir.setReturnValue(InteractionResult.sidedSuccess(world.isClientSide()));
            }
        }
    }
}
