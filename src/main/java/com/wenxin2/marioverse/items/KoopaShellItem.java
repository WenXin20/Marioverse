package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.entities.KoopaShellEntity;
import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.integration.automobility_compat.AutomobilityEntityProvider;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.function.Supplier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
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

        if (entity instanceof KoopaShellEntity shell
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

        if (entity instanceof KoopaShellEntity shell
                && player.getType().is(TagRegistry.CAN_PICKUP_AND_THROW_SHELLS)) {
            this.throwShell(player.level(), player, shell, stack);
            return InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(stack, player, livingEntity, hand);
    }

    public void throwShell(Level level, LivingEntity entity, KoopaShellEntity shell, ItemStack stack) {
        float pitch = 0.9F + level.random.nextFloat() * 0.2F;
        boolean isAutomobile = entity.getRootVehicle().getType() == CompatRegistry.AUTOMOBILE.get()
                || entity.getRootVehicle().getType() == CompatRegistry.AUTOMOBILITY_HITBOX.get();
        double minSpeed = 1.25;
        double spawnDistance = 1.0;

        Vec3 look = entity.getLookAngle();
        double horizontalSpeed = Math.sqrt(entity.getRootVehicle().getDeltaMovement().horizontalDistanceSqr());

        if (ModList.get().isLoaded("automobility") && isAutomobile)
            horizontalSpeed = AutomobilityEntityProvider.getHSpeed(entity.getRootVehicle());
        double speed = Math.max(minSpeed, horizontalSpeed * 3);

        Vec3 spawnPos = entity.position()
                .add(look.x * spawnDistance, entity.getEyeHeight() - 0.6 + look.y * spawnDistance, look.z * spawnDistance);

        shell.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        level.addFreshEntity(shell);

        if (look.y >= 0.9) {
            shell.setDeltaMovement(look.x, minSpeed, look.z);
            level.playSound(entity, entity.blockPosition(), SoundRegistry.KOOPA_SHELL_THROWN_UP.get(),
                    SoundSource.PLAYERS, 1.0F, pitch);
        } else {
            shell.setDeltaMovement(look.x * speed, look.y * Math.max(minSpeed, speed), look.z * speed);
            level.playSound(entity, entity.blockPosition(), SoundRegistry.KOOPA_SHELL_THROWN.get(),
                    SoundSource.PLAYERS, 1.0F, pitch);
        }

        shell.hasImpulse = true;
        shell.setOwner(entity);
        level.gameEvent(entity, GameEvent.ENTITY_PLACE, spawnPos);

        if (!entity.getType().is(TagRegistry.HAS_INFINITE_SHELL_AMMO))
            stack.consume(1, entity);
    }
}