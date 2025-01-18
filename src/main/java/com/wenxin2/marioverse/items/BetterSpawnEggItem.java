package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.entities.PiranhaPlantEntity;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
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

public class BetterSpawnEggItem extends DeferredSpawnEggItem {
    public BetterSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> entityType,
                              int primaryColor, int secondaryColor, Properties properties) {
        super(entityType, primaryColor, secondaryColor, properties);
    }

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

            if (world.getBlockEntity(pos) instanceof Spawner spawner) {
                EntityType<?> entityType1 = this.getType(stack);
                spawner.setEntityId(entityType1, world.getRandom());
                world.sendBlockUpdated(pos, state, state, 3);
                world.gameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, pos);
                stack.shrink(1);
                return InteractionResult.CONSUME;
            } else {

                EntityType<?> entityType = this.getType(stack);
                BlockPos spawnPos = pos.relative(context.getClickedFace());

                Entity entity = entityType.spawn((ServerLevel) world, stack, context.getPlayer(), spawnPos, MobSpawnType.SPAWN_EGG, true,
                        direction == Direction.UP);
                if (entity != null) {
                    stack.shrink(1);
                    world.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, pos);

                    if (entity instanceof PiranhaPlantEntity piranhaPlant)
                        piranhaPlant.setAttachedSide(context.getClickedFace().getOpposite());
                }

                return InteractionResult.CONSUME;
            }
        }
    }
}
