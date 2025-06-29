package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.entities.KoopaShellEntity;
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
import org.jetbrains.annotations.NotNull;

public class KoopaShellItem extends BasePowerUpItem {
    public KoopaShellItem(Supplier<? extends EntityType<? extends Mob>> entityType,
                          int primaryColor, int secondaryColor, Properties properties) {
        super(entityType, primaryColor, secondaryColor, properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
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

    public void throwShell(Level world, LivingEntity entity, KoopaShellEntity shell, ItemStack stack) {
        double speed = 1.0;
        double spawnDistance = 1.0;

        Vec3 look = entity.getLookAngle();
        Vec3 spawnPos = entity.position()
                .add(look.x * spawnDistance, entity.getEyeHeight() - 0.6 + look.y * spawnDistance, look.z * spawnDistance);

        shell.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        world.addFreshEntity(shell);

        if (look.y >= 0.9) {
            shell.setDeltaMovement(look.x, 1.25, look.z);
            world.playSound(entity, entity.blockPosition(), SoundRegistry.KOOPA_SHELL_THROWN_UP.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        } else {
            shell.setDeltaMovement(look.x * speed, look.y * speed, look.z * speed);
            world.playSound(entity, entity.blockPosition(), SoundRegistry.KOOPA_SHELL_THROWN.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        shell.hasImpulse = true;
        shell.setOwner(entity);
        world.gameEvent(entity, GameEvent.ENTITY_PLACE, spawnPos);

        if (!entity.getType().is(TagRegistry.HAS_INFINITE_SHELL_AMMO))
            stack.consume(1, entity);
    }
}
