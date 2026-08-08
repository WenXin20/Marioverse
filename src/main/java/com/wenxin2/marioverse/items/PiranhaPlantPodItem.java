package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.entities.PiranhaPlantEntity;
import com.wenxin2.marioverse.entities.variants.PiranhaPlantVariants;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;

public class PiranhaPlantPodItem extends BetterSpawnEggItem {
    public PiranhaPlantPodItem(Supplier<? extends EntityType<? extends Mob>> entityType,
                               int primaryColor, int secondaryColor, Properties properties) {
        super(entityType, primaryColor, secondaryColor, properties);
    }

    public PiranhaPlantPodItem(int tooltipLineAmt, Supplier<? extends EntityType<? extends Mob>> entityType,
                              int primaryColor, int secondaryColor, Properties properties) {
        super(entityType, primaryColor, secondaryColor, properties);
        this.tooltipLineAmt = tooltipLineAmt;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (!(world instanceof ServerLevel))
            return InteractionResult.SUCCESS;
        else {
            ItemStack stack = context.getItemInHand();
            BlockPos pos = context.getClickedPos();
            Direction direction = context.getClickedFace();
            BlockState state = world.getBlockState(pos);
            Player player = context.getPlayer();

            if (world.getBlockEntity(pos) instanceof Spawner spawner
                    && (player != null && player.isCreative() && !player.isShiftKeyDown())) {
                EntityType<?> entityType1 = this.getType(stack);
                spawner.setEntityId(entityType1, world.getRandom());
                world.sendBlockUpdated(pos, state, state, 3);
                world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                stack.shrink(1);
                return InteractionResult.CONSUME;
            } else if (world.getBlockEntity(pos) instanceof Spawner spawner
                    && player == null) {
                EntityType<?> entityType = this.getType(stack);
                spawner.setEntityId(entityType, world.getRandom());
                world.sendBlockUpdated(pos, state, state, 3);
                world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                stack.shrink(1);
                return InteractionResult.CONSUME;
            } else {
                EntityType<?> entityType = this.getType(stack);
                BlockPos spawnPos = pos.relative(context.getClickedFace());
                Entity entity = entityType.spawn((ServerLevel) world, stack, player, spawnPos, MobSpawnType.MOB_SUMMONED, true,
                        direction == Direction.UP);

                if (entity instanceof PiranhaPlantEntity piranhaPlant) {
                    BlockPos newPos = piranhaPlant.findValidBlockPos();
                    String variant = stack.get(DataComponentRegistry.VARIANT.get());

                    if (variant != null)
                        piranhaPlant.setVariant(variant);

                    piranhaPlant.attachToBlock(newPos, context.getClickedFace().getOpposite());
                    piranhaPlant.setOwner(player);
                    piranhaPlant.setPersistenceRequired();
                    stack.shrink(1);
                    world.gameEvent(player, GameEvent.ENTITY_PLACE, pos);

                    if (player instanceof FakePlayer) {
                        UUID ownerId = player.getData(DataAttachmentRegistry.OWNER_UUID);
                        Entity owner = ((ServerLevel) player.level()).getEntity(ownerId);

                        if (owner != null)
                            piranhaPlant.setOwner(owner);
                    }
                }
                return InteractionResult.CONSUME;
            }
        }
    }
}
