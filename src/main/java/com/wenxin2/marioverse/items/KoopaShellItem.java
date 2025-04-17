package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.entities.KoopaShellEntity;
import com.wenxin2.marioverse.entities.PiranhaPlantEntity;
import com.wenxin2.marioverse.registries.EntityRegistry;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
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
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import org.jetbrains.annotations.NotNull;

public class KoopaShellItem extends BasePowerUpItem {
    public KoopaShellItem(Supplier<? extends EntityType<? extends Mob>> entityType,
                          int primaryColor, int secondaryColor, Properties properties) {
        super(entityType, primaryColor, secondaryColor, properties);
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!world.isClientSide) {
            KoopaShellEntity shell = new KoopaShellEntity(EntityRegistry.GREEN_KOOPA_SHELL.get(), world);
            Vec3 look = player.getLookAngle();
            double speed = 1.5;

            shell.setPos(player.getX(), player.getEyeY() - 0.5, player.getZ());
            shell.setDeltaMovement(look.scale(speed));
            world.addFreshEntity(shell);
        }

        stack.consume(1, player);

        return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
    }
}
