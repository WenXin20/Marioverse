package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.entities.projectiles.LargeSnowballProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class LargeSnowballItem extends Item implements ProjectileItem {
    public LargeSnowballItem(Properties properties) {
        super(properties);
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        HitResult hit = player.pick(5.0D, 0.0F, false);

        if (hit.getType() == HitResult.Type.BLOCK && player.isShiftKeyDown())
            return InteractionResultHolder.pass(stack);

        if (!world.isClientSide) {
            LargeSnowballProjectile snowball = new LargeSnowballProjectile(world, player);
            snowball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 0.5F, 1.0F);
            world.addFreshEntity(snowball);
        }

        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
        player.awardStat(Stats.ITEM_USED.get(this));
        stack.consume(1, player);
        return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (player != null && player.isShiftKeyDown()) {
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
            player.awardStat(Stats.ITEM_USED.get(this));
            stack.consume(1, player);

            if (!world.isClientSide) {
                LargeSnowballProjectile snowball = new LargeSnowballProjectile(world, player);
                snowball.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                world.addFreshEntity(snowball);
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(context);
    }

    @Override
    public Projectile asProjectile(Level world, Position position, ItemStack stack, Direction direction) {
        return new LargeSnowballProjectile(world, position.x(), position.y(), position.z());
    }
}
