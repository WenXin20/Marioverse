package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.entities.CheepCheepEntity;
import com.wenxin2.marioverse.entities.PiranhaPlantEntity;
import com.wenxin2.marioverse.entities.PorcupufferEntity;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import org.jetbrains.annotations.NotNull;

public class BetterSpawnEggItem extends DeferredSpawnEggItem {
    int tooltipLineAmt = 0;

    public BetterSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> entityType,
                              int primaryColor, int secondaryColor, Properties properties) {
        super(entityType, primaryColor, secondaryColor, properties);
    }

    public BetterSpawnEggItem(int tooltipLineAmt, Supplier<? extends EntityType<? extends Mob>> entityType,
                          int primaryColor, int secondaryColor, Properties properties) {
        super(entityType, primaryColor, secondaryColor, properties);
        this.tooltipLineAmt = tooltipLineAmt;
    }

    @NotNull
    @Override
    public Component getName(ItemStack stack) {
        String variant = stack.get(DataComponentRegistry.VARIANT.get());

        if (variant != null)
            return Component.translatable(this.getDescriptionId(stack) + "." + variant);

        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
        if (Screen.hasShiftDown() && this.tooltipLineAmt > 0) {
            list.add(Component.literal(""));
            for (int lineAmt = 1; lineAmt <= tooltipLineAmt; lineAmt++)
                list.add(Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt));
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
                    && (context.getPlayer() != null && !context.getPlayer().isShiftKeyDown())) {
                EntityType<?> entityType1 = this.getType(stack);
                spawner.setEntityId(entityType1, world.getRandom());
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

                EntityType<?> entityType = this.getType(stack);
                BlockPos spawnPos = pos.relative(context.getClickedFace());

                Entity entity = entityType.spawn((ServerLevel) world, stack, context.getPlayer(), spawnPos, MobSpawnType.SPAWN_EGG, true,
                        direction == Direction.UP);
                if (entity != null) {
                    if (entity instanceof CheepCheepEntity cheepCheep) {
                        String variant = stack.get(DataComponentRegistry.VARIANT.get());

                        if (variant != null)
                            cheepCheep.setVariant(variant);
                    }

                    if (entity instanceof PiranhaPlantEntity piranhaPlant) {
                        BlockPos newPos = piranhaPlant.findValidBlockPos();
                        String variant = stack.get(DataComponentRegistry.VARIANT.get());

                        if (variant != null)
                            piranhaPlant.setVariant(variant);
                        piranhaPlant.attachToBlock(newPos, context.getClickedFace().getOpposite());
                    }

                    if (entity instanceof PorcupufferEntity porcupuffer) {
                        String variant = stack.get(DataComponentRegistry.VARIANT.get());

                        if (variant != null)
                            porcupuffer.setVariant(variant);
                    }
                    stack.shrink(1);
                    world.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, pos);
                }

                return InteractionResult.CONSUME;
            }
        }
    }
}
