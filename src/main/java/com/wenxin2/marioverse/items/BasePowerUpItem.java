package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.KeybindRegistry;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
import net.minecraft.world.item.TooltipFlag;
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
    int tooltipLineAmt = 0;

    public BasePowerUpItem(Supplier<? extends EntityType<? extends Mob>> entityType,
                           int primaryColor, int secondaryColor, Properties properties) {
        super(entityType, primaryColor, secondaryColor, properties);
    }

    public BasePowerUpItem(int tooltipLineAmt, Supplier<? extends EntityType<? extends Mob>> entityType,
                           int primaryColor, int secondaryColor, Properties properties) {
        super(entityType, primaryColor, secondaryColor, properties);
        this.tooltipLineAmt = tooltipLineAmt;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
        if (Screen.hasShiftDown() && this.tooltipLineAmt > 0) {
            list.add(Component.literal(""));

            for (int lineAmt = 1; lineAmt <= tooltipLineAmt; lineAmt++) {
                MutableComponent abilityText = Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt);

                if (stack.is(ItemRegistry.FIRE_FLOWER) && lineAmt == 2)
                    abilityText = abilityText.append(Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt + ".keybind",
                            KeybindRegistry.ACTIVATE_POWER_UP.getKey().getDisplayName()).withStyle(ChatFormatting.GREEN));

                if (stack.is(ItemRegistry.ICE_FLOWER) && lineAmt == 2)
                    abilityText = abilityText.append(Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt + ".keybind",
                            KeybindRegistry.ACTIVATE_POWER_UP.getKey().getDisplayName()).withStyle(ChatFormatting.GREEN));

                if (stack.is(ItemRegistry.MEGA_MUSHROOM) && lineAmt == 3)
                    abilityText = abilityText.append(Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt + ".hearts",
                            ConfigRegistry.MEGA_MUSHROOM_HEALTH.get() / 2).withStyle(ChatFormatting.RED));

                if (stack.is(ItemRegistry.MINI_MUSHROOM) && lineAmt == 3)
                    abilityText = abilityText.append(Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt + ".hearts",
                            ConfigRegistry.MINI_MUSHROOM_HEALTH.get() / 2).withStyle(ChatFormatting.RED));

                if (stack.is(ItemRegistry.ONE_UP_MUSHROOM) && lineAmt == 4)
                    abilityText = abilityText.append(Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt + ".hearts",
                            ConfigRegistry.ONE_UP_HEALTH_HEALED.get().floatValue() / 2).withStyle(ChatFormatting.GREEN));

                if (stack.is(ItemRegistry.SUPER_MUSHROOM) && lineAmt == 2)
                    abilityText = abilityText.append(Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt + ".hearts",
                            ConfigRegistry.SUPER_MUSHROOM_HEALTH_HEALED.get().floatValue() / 2).withStyle(ChatFormatting.RED));

                if (stack.is(ItemRegistry.SUPER_STAR) && lineAmt == 2)
                    abilityText = abilityText.append(Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt + ".instakill",
                            Math.round(ConfigRegistry.SUPER_STAR_DURATION.get() / 20.0F * 10.0F) / 10.0F).withStyle(ChatFormatting.GRAY));

                if (stack.is(ItemRegistry.SUPER_STAR) && lineAmt == 3)
                    abilityText = abilityText.append(Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt + ".speed",
                            Math.round(ConfigRegistry.SUPER_STAR_SPEED_DURATION.get() / 20.0F * 10.0F) / 10.0F)).withStyle(ChatFormatting.GRAY);
                list.add(abilityText);
            }
            list.add(Component.literal(""));

        } else if (this.tooltipLineAmt > 0)
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip"));
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
