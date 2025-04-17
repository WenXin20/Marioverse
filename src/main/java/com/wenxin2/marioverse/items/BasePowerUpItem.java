package com.wenxin2.marioverse.items;

import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import org.jetbrains.annotations.NotNull;

public class BasePowerUpItem extends DeferredSpawnEggItem {
    public BasePowerUpItem(Supplier<? extends EntityType<? extends Mob>> entityType,
                           int primaryColor, int secondaryColor, Properties properties) {
        super(entityType, primaryColor, secondaryColor, properties);
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (!(world instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        } else {
            ItemStack stack = context.getItemInHand();
            BlockPos pos = context.getClickedPos();
            Direction direction = context.getClickedFace();
            BlockState state = world.getBlockState(pos);

            if (world.getBlockEntity(pos) instanceof Spawner spawner
                    && (context.getPlayer() != null && context.getPlayer().isCreative()
                        && !context.getPlayer().isShiftKeyDown())) {
                EntityType<?> entityType = this.getType(stack);
                spawner.setEntityId(entityType, world.getRandom());
                world.sendBlockUpdated(pos, state, state, 3);
                world.gameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, pos);
                stack.shrink(1);
                return InteractionResult.CONSUME;
            } else if (world.getBlockEntity(pos) instanceof Spawner spawner
                    && context.getPlayer() == null) {
                EntityType<?> entityType = this.getType(stack);
                spawner.setEntityId(entityType, world.getRandom());
                world.sendBlockUpdated(pos, state, state, 3);
                world.gameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, pos);
                stack.shrink(1);
                return InteractionResult.CONSUME;
            } else {
                BlockPos pos1;
                if (state.getCollisionShape(world, pos).isEmpty()) {
                    pos1 = pos;
                } else {
                    pos1 = pos.relative(direction);
                }

                EntityType<?> entitytype = this.getType(stack);
                if (entitytype.spawn((ServerLevel) world, stack, context.getPlayer(), pos1, MobSpawnType.SPAWN_EGG, true,
                        !Objects.equals(pos, pos1) && direction == Direction.UP) != null) {
                    stack.shrink(1);
                    world.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, pos);
                }

                return InteractionResult.CONSUME;
            }
        }
    }
}
