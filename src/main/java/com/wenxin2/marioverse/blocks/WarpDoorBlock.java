package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.entities.WarpDoorBlockEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.utils.BlockWarpPlayerHandler;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WarpDoorBlock extends DoorBlock implements EntityBlock, BlockWarpPlayerHandler {
    private final DoorBlock source;

    public WarpDoorBlock(DoorBlock source) {
        super(source.type(), BlockBehaviour.Properties.ofFullCopy(source));
        this.source = source;
    }

    public DoorBlock source() {
        return this.source;
    }

    @NotNull
    @Override
    public MutableComponent getName() {
        return Component.translatable("block.marioverse.warp_door", Component.translatable(this.source.getDescriptionId()));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WarpDoorBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        BlockEntity blockEntityAbove = world.getBlockEntity(pos.above());
        BlockEntity blockEntityBelow = world.getBlockEntity(pos.below());
        UUID uuid = UUID.randomUUID();

        if (blockEntity instanceof WarpDoorBlockEntity doorBE) {
            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnThreeLayerBlockParticles(ParticleTypes.PORTAL, serverWorld, null, pos, 16);
            CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);

            if (data != null && data.copyTag().hasUUID("UUID")) {
                doorBE.setUUID(data.copyTag().getUUID("UUID"));
                doorBE.setChanged();

                if (blockEntityAbove instanceof WarpDoorBlockEntity doorBEAbove) {
                    if (world instanceof ServerLevel serverWorld)
                        ServerParticleUtils.spawnThreeLayerBlockParticles(ParticleTypes.PORTAL, serverWorld, null, pos.above(), 16);
                    doorBEAbove.setUUID(data.copyTag().getUUID("UUID"));
                    doorBEAbove.setChanged();
                } else if (blockEntityBelow instanceof WarpDoorBlockEntity doorBEBelow) {
                    if (world instanceof ServerLevel serverWorld)
                        ServerParticleUtils.spawnThreeLayerBlockParticles(ParticleTypes.PORTAL, serverWorld, null, pos.below(), 16);
                    doorBEBelow.setUUID(data.copyTag().getUUID("UUID"));
                    doorBEBelow.setChanged();
                }
            } else {
                doorBE.setUUID(uuid);
                doorBE.setChanged();

                if (blockEntityAbove instanceof WarpDoorBlockEntity doorBEAbove) {
                    if (world instanceof ServerLevel serverWorld)
                        ServerParticleUtils.spawnThreeLayerBlockParticles(ParticleTypes.PORTAL, serverWorld, null, pos.above(), 16);
                    doorBEAbove.setUUID(uuid);
                    doorBEAbove.setChanged();
                } else if (blockEntityBelow instanceof WarpDoorBlockEntity doorBEBelow) {
                    if (world instanceof ServerLevel serverWorld)
                        ServerParticleUtils.spawnThreeLayerBlockParticles(ParticleTypes.PORTAL, serverWorld, null, pos.below(), 16);
                    doorBEBelow.setUUID(uuid);
                    doorBEBelow.setChanged();
                }
            }
            doorBE.onLoad();
        }
        super.setPlacedBy(world, pos, state, entity, stack);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!ConfigRegistry.DISABLE_WARP_DOORS.get()
                && level.getBlockEntity(pos) instanceof WarpDoorBlockEntity
                && state.getBlock() instanceof DoorBlock && state.getValue(DoorBlock.OPEN)
                && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER
                && !entity.getData(DataAttachmentRegistry.PREVENT_WARP))
            this.enterWarp(entity, level, pos, pos, state, null);
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
}

