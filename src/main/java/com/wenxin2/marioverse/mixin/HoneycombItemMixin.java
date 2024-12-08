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
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoneycombItem.class)
public class HoneycombItemMixin {

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    public void onUseOnBlock(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        ItemStack itemStack = context.getItemInHand();
        Player player = context.getPlayer();

        if (world.getBlockEntity(pos) instanceof BaseWarpBlockEntity warpBE
                && (ConfigRegistry.WAX_DISABLES_BUBBLES.get() || ConfigRegistry.WAX_DISABLES_CLOSING.get()
                    || ConfigRegistry.WAX_DISABLES_RENAMING.get() || ConfigRegistry.WAX_DISABLES_WATER_SPOUTS.get()
                    || ConfigRegistry.WAX_DISABLES_WARP_LINKING.get())) {
            if (!warpBE.isWaxed()) {
                warpBE.setWaxed(true);
                world.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
                ParticleUtils.spawnParticlesOnBlockFaces(world, pos, ParticleTypes.WAX_ON, UniformInt.of(3, 5));

                if (state.getBlock() instanceof DoorBlock && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER)
                    ParticleUtils.spawnParticlesOnBlockFaces(world, pos.above(), ParticleTypes.WAX_ON, UniformInt.of(3, 5));
                if (state.getBlock() instanceof DoorBlock && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER)
                    ParticleUtils.spawnParticlesOnBlockFaces(world, pos.below(), ParticleTypes.WAX_ON, UniformInt.of(3, 5));

                warpBE.markUpdated();

                if (player != null)
                    itemStack.shrink(1);

                world.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
                cir.setReturnValue(InteractionResult.sidedSuccess(Boolean.TRUE));
            }
        }
    }
}
