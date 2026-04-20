package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.entities.WarpDoorBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpTrapDoorBlockEntity;
import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.utils.BlockWarpPlayerHandler;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WarpTrapDoorBlock extends TrapDoorBlock implements BlockWarpPlayerHandler, EntityBlock {
    private final TrapDoorBlock source;

    public WarpTrapDoorBlock(TrapDoorBlock source) {
        super(source.getType(), Properties.ofFullCopy(source));
        this.source = source;
    }

    public TrapDoorBlock source() {
        return this.source;
    }

    @NotNull
    @Override
    public MutableComponent getName() {
        return Component.translatable("block.marioverse.warp_trapdoor", Component.translatable(this.source.getDescriptionId()));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
        ResourceLocation blockID = BuiltInRegistries.BLOCK.getKey(this.source);
        Component modName = ModList.get().getModContainerById(blockID.getNamespace())
                .map(c -> Component.literal(c.getModInfo().getDisplayName()))
                .orElse(Component.literal(blockID.getNamespace()));

        if (Screen.hasShiftDown()) {
            list.add(Component.literal(""));

            list.add(Component.translatable("block.marioverse.warp_trapdoor.tooltip.ability"));
            list.add(Component.translatable("block.marioverse.warp_trapdoor.tooltip.description"));
            list.add(Component.translatable("block.marioverse.warp_trapdoor.tooltip.guide"));
            list.add(Component.translatable("block.marioverse.warp_trapdoor.tooltip.mod"));
            list.add(Component.translatable("block.marioverse.warp_trapdoor.tooltip.source_mod", modName).withStyle(ChatFormatting.BLUE));

            list.add(Component.literal(""));
        } else list.add(Component.translatable("block.marioverse.warp_trapdoor.tooltip"));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WarpTrapDoorBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof WarpTrapDoorBlockEntity doorBE) {
            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnOneLayerBlockParticles(ParticleTypes.PORTAL, serverWorld, null, pos, 16);

            CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
            if (data != null && data.copyTag().hasUUID("UUID")) {
                doorBE.setUUID(data.copyTag().getUUID("UUID"));
                doorBE.setChanged();
            } else {
                UUID uuid = UUID.randomUUID();
                doorBE.setUUID(uuid);
                doorBE.setChanged();
            }
            doorBE.onLoad();
        }
        super.setPlacedBy(world, pos, state, entity, stack);
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState neighborState, Direction direction) {
        return state.is(CompatRegistry.MV_FRAMED_GLASS_TRAPDOOR.get()) == neighborState.is(CompatRegistry.MV_FRAMED_GLASS_TRAPDOOR.get())
                && isConnected(state, neighborState, direction);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        BlockPos posInBlock = pos.above(Math.round(entity.getBbHeight()) - 1);
        BlockState stateInBlock = level.getBlockState(posInBlock);

        if (!ConfigRegistry.DISABLE_WARP_TRAPDOORS.get()
                && level.getBlockEntity(pos) instanceof WarpTrapDoorBlockEntity
                && state.getBlock() instanceof TrapDoorBlock && state.getValue(TrapDoorBlock.OPEN)
                && !entity.getData(DataAttachmentRegistry.PREVENT_WARP))
            this.enterWarp(entity, level, pos);

        if (!ConfigRegistry.DISABLE_WARP_TRAPDOORS.get()
                && level.getBlockEntity(posInBlock) instanceof WarpTrapDoorBlockEntity
                && stateInBlock.getBlock() instanceof TrapDoorBlock && stateInBlock.getValue(TrapDoorBlock.OPEN)
                && !entity.getData(DataAttachmentRegistry.PREVENT_WARP))
            this.enterWarp(entity, level, posInBlock);
        super.entityInside(state, level, pos, entity);
    }

    @Override
    public boolean mv$getBlockWarpTeleportConfig(Entity entity) {
        if (entity instanceof Player)
            return ConfigRegistry.TELEPORT_PLAYERS.get();
        else if (entity instanceof LivingEntity)
            return ConfigRegistry.TELEPORT_MOBS.get();
        return ConfigRegistry.TELEPORT_NON_MOBS.get();
    }

    public static boolean isConnected(BlockState state, BlockState neighborState, Direction direction) {
        if (state.is(CompatRegistry.MV_FRAMED_GLASS_TRAPDOOR.get()) && neighborState.is(CompatRegistry.MV_FRAMED_GLASS_TRAPDOOR.get())) {
            state = state.setValue(WATERLOGGED, false).setValue(POWERED, false);
            neighborState = neighborState.setValue(WATERLOGGED, false).setValue(POWERED, false);

            boolean isOpen = state.getValue(OPEN);
            Half half = state.getValue(HALF);
            Direction facing = state.getValue(FACING);

            if (isOpen != neighborState.getValue(OPEN))
                return false;
            else if (!isOpen && half == neighborState.getValue(HALF))
                return direction.getAxis() != Direction.Axis.Y;
            else if (!isOpen && half != neighborState.getValue(HALF) && direction.getAxis() == Direction.Axis.Y)
                return true;
            else if (isOpen && facing.getOpposite() == neighborState.getValue(FACING) && direction.getAxis() == facing.getAxis())
                return true;
            else if ((isOpen ? state.setValue(HALF, Half.TOP) : state) != (isOpen ? neighborState.setValue(HALF, Half.TOP) : neighborState))
                return false;
            else return direction.getAxis() != facing.getAxis();
        } else return false;
    }
}

