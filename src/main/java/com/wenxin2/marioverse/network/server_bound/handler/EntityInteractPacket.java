//package com.wenxin2.marioverse.network.server_bound.handler;
//
//import com.wenxin2.marioverse.blocks.WarpPipeBlock;
//import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
//import com.wenxin2.marioverse.entities.PiranhaPlantEntity;
//import com.wenxin2.marioverse.network.server_bound.data.ClosePipeButtonPayload;
//import com.wenxin2.marioverse.network.server_bound.data.EntityInteractPayload;
//import java.util.Optional;
//import net.minecraft.core.BlockPos;
//import net.minecraft.network.protocol.game.ServerboundInteractPacket;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.world.InteractionHand;
//import net.minecraft.world.InteractionResult;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.state.BlockState;
//import net.neoforged.neoforge.entity.PartEntity;
//import net.neoforged.neoforge.network.handling.IPayloadContext;
//import org.jetbrains.annotations.Nullable;
//
//public class EntityInteractPacket {
//    public static final EntityInteractPacket INSTANCE = new EntityInteractPacket();
//
//    public static EntityInteractPacket get() {
//        return INSTANCE;
//    }
//
//    public void handle(final EntityInteractPayload payload, IPayloadContext context) {
//        if (context.flow().isServerbound()) {
//            final ServerLevel serverWorld = (ServerLevel) context.player().level();
//
//            final Entity entity = payload.entity();
//
//            final double d0 = 36.0D;
//            context.enqueueWork(() -> {
//                if (entity != null)
//                {
//                    // Convert to the relevant part if found.
//                    if (entity.isMultipartEntity()) for (final PartEntity<?> p : entity.getParts())
//                    if (context.player().distanceToSqr(entity) < d0)
//                    {
//                        final InteractionHand hand = context.player().getUsedItemHand();
//                        final ItemStack itemstack = context.player().getItemInHand(hand).copy();
//                        Optional<InteractionResult> optional = Optional.empty();
//
//                        if (this.getAction() == ServerboundInteractPacket.Handler.INTERACT)
//                            optional = Optional.of(context.player().interactOn(entity, hand));
//                        else if (this.getAction() == ServerboundInteractPacket.ActionType.INTERACT_AT)
//                        {
//                            if (net.minecraftforge.common.ForgeHooks.onInteractEntityAt(player, entity, this.getHitVec(),
//                                    hand) != null)
//                                return;
//                            optional = Optional.of(entity.interactAt(player, this.getHitVec(), hand));
//                        }
//                        else if (this.getAction() == ServerboundInteractPacket.ActionType.ATTACK) context.player().attack(entity);
//
//                        if (optional.isPresent() && optional.get().consumesAction())
//                        {
//                            CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY.trigger(player, itemstack, entity);
//                            if (optional.get().shouldSwing()) context.player().swing(hand, true);
//                        }
//                    }
//                }
//            });
//        }
//    }
//
//    @Nullable
//    public Entity getEntityFromWorld(final Level world) {
//        return EntityPro.provider.getEntity(world, entityId);
//    }
//
//    public void changeState(ServerPlayer player, WarpPipeBlockEntity pipeBlockEntity) {
//        Level world = pipeBlockEntity.getLevel();
//        if (world == null)
//            return;
//        BlockPos pos = pipeBlockEntity.getBlockPos();
//        BlockState state = world.getBlockState(pos);
//
//        if (!(state.getBlock() instanceof WarpPipeBlock))
//            return;
//
//        pipeBlockEntity.closePipe(player);
//    }
//
//    public static EntityInteractPacket openPipe(BlockPos pos, Boolean closePipe) {
//        EntityInteractPacket packet = new EntityInteractPacket();
//        closePipe = false;
//        return packet;
//    }
//
//    public static EntityInteractPacket closePipe(BlockPos pos) {
//        EntityInteractPacket packet = new EntityInteractPacket();
//        Boolean closePipe = true;
//        return packet;
//    }
//}
