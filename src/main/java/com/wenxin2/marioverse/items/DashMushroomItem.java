package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.power_up.PowerUpSource;
import com.wenxin2.marioverse.power_up.PowerUpType;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.PowerUpTypeRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DashMushroomItem extends PowerUpItem implements PowerUpSource {

    public DashMushroomItem(Properties properties) {
        super(properties);
    }

    public DashMushroomItem(int tooltipLineAmt, Properties properties) {
        super(tooltipLineAmt, properties);
    }

    @Override
    public Holder<PowerUpType> getPowerUpType() {
        return PowerUpTypeRegistry.DASH_MUSHROOM;
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        return DashMushroomItem.mushroomAbilities(stack, world, player, ConfigRegistry.DASH_MUSHROOM_BOOST_STRENGTH.get(), true, false);
    }

    @NotNull
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        if (!(entity instanceof Player) && !entity.getType().is(TagRegistry.DASH_MUSHROOM_CANNOT_BOOST)) {
            if (entity.isVehicle())
                DashMushroomItem.mushroomAbilities(stack, world, entity, ConfigRegistry.VEHICLE_MUSHROOM_BOOST_STRENGTH.get(), true, false);
            else DashMushroomItem.mushroomAbilities(stack, world, entity, ConfigRegistry.DASH_MUSHROOM_BOOST_STRENGTH.get(), true, false);
        }
        return super.finishUsingItem(stack, world, entity);
    }

    public static InteractionResultHolder<ItemStack> mushroomAbilities(@Nullable ItemStack stack, Level world, Entity entity, double boostStrength, boolean nerfBoost, boolean isCommand) {
        ItemStack notNullStack = Objects.requireNonNullElseGet(stack, () -> new ItemStack(ItemRegistry.DASH_MUSHROOM.get()));

        if (boostStrength > 0) {
            if (entity instanceof AbilitiesHandler handler && !entity.getType().is(TagRegistry.DASH_MUSHROOM_CANNOT_BOOST)) {
                BlockPos posBelow = entity.blockPosition().below();
                BlockState stateBelow = world.getBlockState(posBelow);

                float friction = stateBelow.getBlock().getFriction();
                if (entity.isInWaterOrBubble() || stateBelow.isAir()
                        || (entity instanceof LivingEntity livingEntity && livingEntity.isFallFlying()))
                    friction = 1.5F;
                if (entity instanceof Player player && player.getAbilities().flying)
                    friction = 1.5F;

                double baseBoost = boostStrength;
                double boost = baseBoost / friction;
                Vec3 direction = entity.getLookAngle().normalize();

                Entity vehicle = entity.getVehicle();
                if (stack != null && entity instanceof LivingEntity livingEntity)
                    stack.consume(1, livingEntity);

                if (entity instanceof LivingEntity livingEntity)
                    handler.applySuperMushroomPowerUp(world, livingEntity, null, ConfigRegistry.DASH_MUSHROOM_HEALTH_HEALED.get().floatValue());

                if (vehicle != null
                        && (!vehicle.getType().is(TagRegistry.DASH_MUSHROOM_CANNOT_BOOST) || isCommand)) {
                    posBelow = vehicle.blockPosition().below();
                    stateBelow = world.getBlockState(posBelow);
                    friction = stateBelow.getBlock().getFriction();
                    if (vehicle instanceof Boat && friction <= 0.7F)
                        friction = stateBelow.getBlock().getFriction() / 1.5F;
                    if (vehicle instanceof Boat && friction > 0.7F)
                        friction = stateBelow.getBlock().getFriction() * 0.5F;

                    if (vehicle instanceof AbstractMinecart && nerfBoost)
                        baseBoost = boostStrength / 10;
                    boost = baseBoost / friction;
                    direction = vehicle.getLookAngle().normalize();
                    if (vehicle instanceof AbstractMinecart)
                        direction = entity.getLookAngle().normalize();

                    entity.setData(DataAttachmentRegistry.HAS_DASH_MUSHROOM_BOOST, true);
                    if (!(vehicle instanceof Boat))
                        vehicle.setDeltaMovement(direction.x * boost, 0, direction.z * boost);
                    if (entity instanceof Player player && stack != null)
                        player.getCooldowns().addCooldown(stack.getItem(), (int) (boost));

                    if (vehicle.level().isClientSide && vehicle instanceof Boat && vehicle.isControlledByLocalInstance())
                        vehicle.setDeltaMovement(direction.x * boost, 0, direction.z * boost);

                    return InteractionResultHolder.sidedSuccess(notNullStack, world.isClientSide());
                } else {
                    entity.setData(DataAttachmentRegistry.HAS_DASH_MUSHROOM_BOOST, true);
                    entity.setDeltaMovement(direction.x * boost, entity.getDeltaMovement().y, direction.z * boost);
                    if (entity instanceof ServerPlayer player)
                        player.connection.send(new ClientboundSetEntityMotionPacket(player));
                    if (entity instanceof Player player && stack != null)
                        player.getCooldowns().addCooldown(stack.getItem(), (int) (boost));
                    return InteractionResultHolder.sidedSuccess(notNullStack, world.isClientSide());
                }
            }
        }
        return InteractionResultHolder.fail(notNullStack);
    }
}
