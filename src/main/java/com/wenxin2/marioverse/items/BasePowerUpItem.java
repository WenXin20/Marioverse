package com.wenxin2.marioverse.items;

import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
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

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        BlockHitResult blockhitresult = getPlayerPOVHitResult(world, player, ClipContext.Fluid.SOURCE_ONLY);
        if (blockhitresult.getType() != HitResult.Type.BLOCK) {
            ItemStack stack = player.getItemInHand(hand);
            FoodProperties foodproperties = stack.getFoodProperties(player);
            if (foodproperties != null) {
                if (player.canEat(foodproperties.canAlwaysEat())) {
                    player.startUsingItem(hand);
                    return InteractionResultHolder.consume(stack);
                } else {
                    return InteractionResultHolder.fail(stack);
                }
            } else return InteractionResultHolder.pass(player.getItemInHand(hand));
        } else if (!(world instanceof ServerLevel)) {
            return InteractionResultHolder.success(itemstack);
        } else {
            BlockPos blockpos = blockhitresult.getBlockPos();
            if (!(world.getBlockState(blockpos).getBlock() instanceof LiquidBlock)) {
                return InteractionResultHolder.pass(itemstack);
            } else if (world.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos, blockhitresult.getDirection(), itemstack)) {
                EntityType<?> entitytype = this.getType(itemstack);
                Entity entity = entitytype.spawn((ServerLevel)world, itemstack, player, blockpos, MobSpawnType.SPAWN_EGG, false, false);
                if (entity == null) {
                    return InteractionResultHolder.pass(itemstack);
                } else {
                    itemstack.consume(1, player);
                    player.awardStat(Stats.ITEM_USED.get(this));
                    world.gameEvent(player, GameEvent.ENTITY_PLACE, entity.position());
                    return InteractionResultHolder.consume(itemstack);
                }
            } else {
                return InteractionResultHolder.fail(itemstack);
            }
        }
    }
}
