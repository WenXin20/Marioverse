package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.blocks.ClearWarpPipeBlock;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
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
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
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

        if (player != null && !player.isCreative() && ConfigRegistry.CREATIVE_WRENCH_PIPE_LINKING.get()) {
            player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.requires_creative"), true);
            return InteractionResult.sidedSuccess(Boolean.TRUE);
        } else if (player != null) {
            if (player.isShiftKeyDown() && blockEntity instanceof BaseWarpBlockEntity warpBE
                    && (state.getBlock() instanceof DoorBlock
                        || state.getBlock() instanceof TrapDoorBlock
                        || state.getBlock() instanceof ClearWarpPipeBlock
                        || (state.getBlock() instanceof WarpPipeBlock && state.getValue(WarpPipeBlock.ENTRANCE)))) {
                UUID uuid = warpBE.getUuid();

                if (warpBE.isWaxed() && ConfigRegistry.WAX_DISABLES_WARP_LINKING.get()) {
                    player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.waxed",
                            state.getBlock().getName()).withStyle(ChatFormatting.GOLD), true);
                    return InteractionResult.sidedSuccess(Boolean.TRUE);
                } else if (!getIsBound(stack)) {

                    if (!world.isClientSide && uuid == null) {
                        uuid = UUID.randomUUID();
                        warpBE.setUuid(uuid);
                        warpBE.setChanged();
                    }
                    // First interaction: Bind the first block
                    setWarpPos(stack, pos);
                    setWarpDimension(stack, dimension);
                    setWarpUUID(stack, uuid);
                    setIsBound(stack, true);  // Mark the item as bound

                    player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.bound",
                                    state.getBlock().getName()).withStyle(ChatFormatting.GREEN), true);

                    this.spawnParticles(world, pos, ParticleTypes.ENCHANT);
                    this.playSound(world, pos, SoundRegistry.WRENCH_BOUND.get(), SoundSource.PLAYERS, 1.0F, 0.1F);
                } else {

                    if (!world.isClientSide && uuid == null) {
                        uuid = UUID.randomUUID();
                        warpBE.setUuid(uuid);
                        warpBE.setChanged();
                    }

                    // Second interaction: Link the blocks
                    BlockPos firstPos = getWarpPos(stack);
                    BlockState firstState = world.getBlockState(firstPos);
                    String firstDim = getWarpDimension(stack);

//                    if (dimension.equals(getWarpDimension(stack))) {
                        BlockEntity firstBlockEntity = world.getBlockEntity(firstPos);
                        if (firstBlockEntity instanceof BaseWarpBlockEntity firstWarpBlockEntity) {

                            // Perform the linking logic
                            this.link(stack, firstWarpBlockEntity, warpBE);

                            player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.linked_warp_block",
                                            state.getBlock().getName(), firstState.getBlock().getName()).withStyle(ChatFormatting.GOLD), true);

                            this.spawnParticles(world, pos, ParticleTypes.ENCHANT);
                            this.playSound(world, pos, SoundRegistry.PIPES_LINKED.get(), SoundSource.BLOCKS, 1.0F, 0.1F);
                        }
//                    }
                    setIsBound(stack, false);  // Reset binding
                }
                return InteractionResult.sidedSuccess(Boolean.TRUE);
            }
        }
        return super.useOn(useOnContext);
    }

    public void link(ItemStack stack, BaseWarpBlockEntity firstPipeBlockEntity, BaseWarpBlockEntity secondPipeBlockEntity) {
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

        firstPipeBlockEntity.markUpdated();
        secondPipeBlockEntity.markUpdated();
        clearItemComponents(stack);
    }

    public void clearItemComponents(ItemStack stack) {
        setWarpPos(stack, null);
        setWarpDimension(stack, "");
        setWarpUUID(stack, null);
    }

    public static boolean getIsBound(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.IS_BOUND.get(), Boolean.FALSE);
    }

    public static void setIsBound(ItemStack stack, boolean isBound) {
        stack.set(DataComponentRegistry.IS_BOUND.get(), isBound);
    }

    public static BlockPos getWarpPos(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.WARP_POS, null);
    }

    public static void setWarpPos(ItemStack stack, BlockPos warpPos) {
        stack.set(DataComponentRegistry.WARP_POS, warpPos);
    }

    public static String getWarpDimension(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.WARP_DIMENSION.get(), "");
    }

    public static void setWarpDimension(ItemStack stack, String dimension) {
        stack.set(DataComponentRegistry.WARP_DIMENSION.get(), dimension);
    }

    public static UUID getWarpUUID(ItemStack stack) {
        UUID uuid = UUID.randomUUID();
        return stack.getOrDefault(DataComponentRegistry.WARP_UUID.get(), null);
    }

    public static UUID setWarpUUID(ItemStack stack, UUID warpUUID) {
        stack.set(DataComponentRegistry.WARP_UUID.get(), warpUUID);
        return warpUUID;
    }

    public static GlobalPos getGlobalWarpPos(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.GLOBAL_WARP_POS.get(), null);
    }

    public static void setGlobalWarpPos(ItemStack stack, GlobalPos globalPos) {
        stack.set(DataComponentRegistry.GLOBAL_WARP_POS.get(), globalPos);
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
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.bound",
                    getWarpPos(stack).getX(), getWarpPos(stack).getY(), getWarpPos(stack).getZ(), getWarpDimension(stack), true)
                    .withStyle(ChatFormatting.GOLD));
        } else {
            list.add(Component.translatable("", true));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.not_bound", true)
                    .withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
        }
    }
}
