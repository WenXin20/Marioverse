package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.entities.ShellEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public class KoopaShellItem extends BetterSpawnEggItem {
    public KoopaShellItem(Supplier<? extends EntityType<? extends Mob>> entityType,
                          int primaryColor, int secondaryColor, Properties properties) {
        super(entityType, primaryColor, secondaryColor, properties);
    }

    public KoopaShellItem(int tooltipLineAmt, Supplier<? extends EntityType<? extends Mob>> entityType,
                          int primaryColor, int secondaryColor, Properties properties) {
        super(entityType, primaryColor, secondaryColor, properties);
        this.tooltipLineAmt = tooltipLineAmt;
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() != null
                && context.getPlayer().getType().is(TagRegistry.CAN_PICKUP_AND_THROW_SHELLS))
            return super.useOn(context);
        else return InteractionResult.PASS;
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        EntityType<?> entityType = this.getType(stack);
        Entity entity = entityType.create(world);

        if (entity instanceof ShellEntity shell
                && player.getType().is(TagRegistry.CAN_PICKUP_AND_THROW_SHELLS)) {
            this.throwShell(world, player, shell, stack);
            return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
        }

        return InteractionResultHolder.fail(stack);
    }

    @NotNull
    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity livingEntity, InteractionHand hand) {
        EntityType<?> entityType = this.getType(stack);
        Entity entity = entityType.create(player.level());

        if (entity instanceof ShellEntity shell
                && player.getType().is(TagRegistry.CAN_PICKUP_AND_THROW_SHELLS)) {
            this.throwShell(player.level(), player, shell, stack);
            return InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(stack, player, livingEntity, hand);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return ConfigRegistry.MAX_KOOPA_SHELL_DAMAGE_POINTS.get() > 0 && KoopaShellItem.getBounceCount(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        float maxDamage = ConfigRegistry.MAX_KOOPA_SHELL_DAMAGE_POINTS.get();
        return Math.round(13.0F - Math.min(KoopaShellItem.getBounceCount(stack), maxDamage) * 13.0F / maxDamage);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float maxDamage = ConfigRegistry.MAX_KOOPA_SHELL_DAMAGE_POINTS.get();
        float remaining = Math.max(0.0F, (maxDamage - KoopaShellItem.getBounceCount(stack)) / maxDamage);
        return Mth.hsvToRgb(remaining / 3.0F, 1.0F, 1.0F);
    }

    public void throwShell(Level level, LivingEntity entity, ShellEntity shell, ItemStack stack) {
        CustomData entityData = stack.get(DataComponents.ENTITY_DATA);

        if (entityData != null)
            EntityType.updateCustomEntityTag(level, entity instanceof Player player ? player : null, shell, entityData);

        shell.throwFrom(entity);
        level.addFreshEntity(shell);
        level.gameEvent(entity, GameEvent.ENTITY_PLACE, shell.position());

        if (!entity.getType().is(TagRegistry.HAS_INFINITE_SHELL_AMMO))
            stack.consume(1, entity);
    }

    public static int getBounceCount(ItemStack stack) {
        CustomData entityData = stack.get(DataComponents.ENTITY_DATA);
        return entityData == null ? 0 : entityData.copyTag().getInt("BounceCount");
    }
}
