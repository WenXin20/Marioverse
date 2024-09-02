package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.blocks.ClearWarpPipeBlock;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.init.Config;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.items.data_components.LinkerDataComponents;
import java.util.List;
import java.util.UUID;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LinkerItem extends TieredItem {
    public LinkerItem(final Properties properties, Tier tier) {
        super(tier, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Player player = useOnContext.getPlayer();
        Level world = useOnContext.getLevel();
        BlockPos pos = useOnContext.getClickedPos();
        BlockState state = world.getBlockState(pos);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        ItemStack stack = useOnContext.getItemInHand();
        String dimension = world.dimension().location().toString();

        if (player != null && !player.isCreative() && Config.CREATIVE_WRENCH_PIPE_LINKING.get()) {
            player.displayClientMessage(Component.translatable("display.marioverse.linker.requires_creative")
                    .withStyle(), true);
            return InteractionResult.sidedSuccess(world.isClientSide);
        } else if (player != null) {
            if ((state.getBlock() instanceof ClearWarpPipeBlock || ((state.getBlock() instanceof WarpPipeBlock)
                    && state.getValue(WarpPipeBlock.ENTRANCE))) && player.isShiftKeyDown() && blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity) {

                UUID uuid = pipeBlockEntity.getUuid();

                if (!getIsBound(stack)) {
                    // First interaction: Bind the first block
                    setWarpPos(stack, pos);
                    setWarpDimension(stack, dimension);
                    setWarpUUID(stack, uuid);
                    setIsBound(stack, true);  // Mark the item as bound

                    player.displayClientMessage(Component.translatable("display.marioverse.linker.bound",
                                    pos.getX(), pos.getY(), pos.getZ(), dimension)
                            .withStyle(ChatFormatting.DARK_GREEN), true);

                    this.spawnParticles(world, pos, ParticleTypes.ENCHANT);
                    this.playSound(world, pos, SoundRegistry.WRENCH_BOUND.get(), SoundSource.PLAYERS, 1.0F, 0.1F);
                } else {
                    // Second interaction: Link the blocks
                    BlockPos firstPos = getWarpPos(stack);
                    String firstDim = getWarpDimension(stack);
//                    if (dimension.equals(getWarpDimension(stack))) {
                        BlockEntity firstBlockEntity = world.getBlockEntity(firstPos);
                        if (firstBlockEntity instanceof WarpPipeBlockEntity firstPipeBlockEntity) {

                            // Perform the linking logic
                            this.link(stack, firstPipeBlockEntity, pipeBlockEntity);

                            player.displayClientMessage(Component.translatable("display.marioverse.linker.linked",
                                            pos.getX(), pos.getY(), pos.getZ(), dimension)
                                    .withStyle(ChatFormatting.GOLD), true);

                            this.spawnParticles(world, pos, ParticleTypes.ENCHANT);
                            this.playSound(world, pos, SoundRegistry.PIPES_LINKED.get(), SoundSource.BLOCKS, 1.0F, 0.1F);
                        }
//                    }
                    setIsBound(stack, false);  // Reset binding
                }
                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }
        return super.useOn(useOnContext);
    }

    public void link(ItemStack stack, WarpPipeBlockEntity firstPipeBlockEntity, WarpPipeBlockEntity secondPipeBlockEntity) {
        UUID firstUuid = firstPipeBlockEntity.getUuid();
        UUID secondUuid = secondPipeBlockEntity.getUuid();

        BlockPos firstPos = firstPipeBlockEntity.getBlockPos();
        BlockPos secondPos = secondPipeBlockEntity.getBlockPos();
        ResourceKey<Level> firstDim = firstPipeBlockEntity.getDestinationDim();
        ResourceKey<Level> secondDim = secondPipeBlockEntity.getDestinationDim();

        // Linking logic
        firstPipeBlockEntity.setDestinationPos(secondPos);
        secondPipeBlockEntity.setDestinationPos(firstPos);

        if (secondDim != null)
            firstPipeBlockEntity.setDestinationDim(secondDim);
        if (firstDim != null)
            secondPipeBlockEntity.setDestinationDim(firstDim);

        if (firstUuid != null)
            secondPipeBlockEntity.setWarpUuid(firstUuid);
        if (secondUuid != null)
            firstPipeBlockEntity.setWarpUuid(secondUuid);

        firstPipeBlockEntity.setChanged();
        secondPipeBlockEntity.setChanged();

        clearItemComponents(stack);  // Clear tags after linking
    }

    public void clearItemComponents(ItemStack stack) {
        setWarpPos(stack, null);
        setWarpDimension(stack, "");
        setWarpUUID(stack, null);
    }

    public static boolean getIsBound(ItemStack stack) {
        return stack.getOrDefault(LinkerDataComponents.IS_BOUND.get(), Boolean.FALSE);
    }

    public static void setIsBound(ItemStack stack, boolean isBound) {
        stack.set(LinkerDataComponents.IS_BOUND.get(), isBound);
    }

    public static BlockPos getWarpPos(ItemStack stack) {
        return stack.getOrDefault(LinkerDataComponents.WARP_POS, null);
    }

    public static void setWarpPos(ItemStack stack, BlockPos warpPos) {
        stack.set(LinkerDataComponents.WARP_POS, warpPos);
    }

    public static String getWarpDimension(ItemStack stack) {
        return stack.getOrDefault(LinkerDataComponents.WARP_DIMENSION.get(), "");
    }

    public static void setWarpDimension(ItemStack stack, String dimension) {
        stack.set(LinkerDataComponents.WARP_DIMENSION.get(), dimension);
    }

    public static UUID getWarpUUID(ItemStack stack) {
        UUID uuid = UUID.randomUUID();
        return stack.getOrDefault(LinkerDataComponents.WARP_UUID.get(), null);
    }

    public static UUID setWarpUUID(ItemStack stack, UUID warpUUID) {
        stack.set(LinkerDataComponents.WARP_UUID.get(), warpUUID);
        return warpUUID;
    }

    public static GlobalPos getGlobalWarpPos(ItemStack stack) {
        return stack.getOrDefault(LinkerDataComponents.GLOBAL_WARP_POS.get(), null);
    }

    public static void setGlobalWarpPos(ItemStack stack, GlobalPos globalPos) {
        stack.set(LinkerDataComponents.GLOBAL_WARP_POS.get(), globalPos);
    }


    public void playSound(Level world, BlockPos pos, SoundEvent soundEvent, SoundSource source, float volume, float pitch) {
        world.playSound(null, pos, soundEvent, source, volume, pitch);
    }

    private void spawnParticles(Level world, BlockPos pos, ParticleOptions particleOptions) {
        if (world.isClientSide()) {
            RandomSource random = world.getRandom();

            for (int i = 0; i < 40; ++i) {
                world.addParticle(particleOptions,
                        pos.getX() + 0.5D + (0.5D * (random.nextBoolean() ? 1 : -1)), pos.getY() + 1.5D,
                        pos.getZ() + 0.5D + (0.5D * (random.nextBoolean() ? 1 : -1)),
                        (random.nextDouble() - 0.5D) * 2.0D, -random.nextDouble(),
                        (random.nextDouble() - 0.5D) * 2.0D);
            }
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
        if (getIsBound(stack)) {
            list.add(Component.translatable("", true));
            list.add(Component.translatable("display.marioverse.linker.bound_tooltip",
                    getWarpPos(stack).getX(), getWarpPos(stack).getY(), getWarpPos(stack).getZ(), getWarpDimension(stack), true)
                    .withStyle(ChatFormatting.GOLD));
        }
        else {
            list.add(Component.translatable("", true));
            list.add(Component.translatable("display.marioverse.linker.not_bound_tooltip", true)
                    .withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
        }
    }
}
