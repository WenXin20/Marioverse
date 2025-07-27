package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.entities.power_ups.MushroomEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DashMushroomItem extends Item {
    public DashMushroomItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        if (!(entity instanceof Player) && !entity.getType().is(TagRegistry.DASH_MUSHROOM_CANNOT_BOOST)) {
            if (entity.isVehicle())
                DashMushroomItem.mushroomAbilities(stack, world, entity, ConfigRegistry.VEHICLE_MUSHROOM_BOOST_STRENGTH.get(), true, false);
            else DashMushroomItem.mushroomAbilities(stack, world, entity, ConfigRegistry.DASH_MUSHROOM_BOOST_STRENGTH.get(), true, false);
        }
        return super.finishUsingItem(stack, world, entity);
    }

    public static void mushroomAbilities(@Nullable ItemStack stack, Level world, LivingEntity entity, double boostStrength, boolean nerfBoost, boolean isCommand) {
        if (boostStrength > 0) {
            if (entity instanceof AbilitiesHandler handler) {
                BlockPos posBelow = entity.blockPosition().below();
                BlockState stateBelow = world.getBlockState(posBelow);

                float friction = stateBelow.getBlock().getFriction();
                if (entity.isInWaterOrBubble() || entity.isFallFlying() || stateBelow.isAir()) friction = 1.5F;

                double baseBoost = boostStrength;
                double boost = baseBoost / friction;
                Vec3 direction = entity.getLookAngle().normalize();

                Entity vehicle = entity.getVehicle();
                if (stack != null)
                    stack.consume(1, entity);
                MushroomEntity.powerUp(world, entity, null, ConfigRegistry.DASH_MUSHROOM_HEALTH_HEALED.get().floatValue());

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

                    handler.mv$setDashMushroomBoost(true);

                    if (vehicle.level().isClientSide && vehicle instanceof Boat && vehicle.isControlledByLocalInstance())
                        vehicle.setDeltaMovement(direction.x * boost, 0, direction.z * boost);
                    else vehicle.setDeltaMovement(direction.x * boost, 0, direction.z * boost);
                    if (vehicle instanceof ServerPlayer serverPlayer)
                        serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(vehicle));
                    else if (world instanceof ServerLevel serverWorld)
                        serverWorld.getChunkSource().broadcast(vehicle, new ClientboundSetEntityMotionPacket(vehicle));
                    vehicle.hasImpulse = true;
                } else {
                    handler.mv$setDashMushroomBoost(true);
                    entity.setDeltaMovement(direction.x * boost, entity.getDeltaMovement().y, direction.z * boost);
                    if (entity instanceof ServerPlayer serverPlayer)
                        serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(entity));
                    else if (world instanceof ServerLevel serverWorld)
                        serverWorld.getChunkSource().broadcast(entity, new ClientboundSetEntityMotionPacket(entity));
                    entity.hasImpulse = true;
                }
            }
        }
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (ConfigRegistry.DASH_MUSHROOM_BOOST_STRENGTH.get() > 0 || ConfigRegistry.VEHICLE_MUSHROOM_BOOST_STRENGTH.get() > 0) {
            if (player instanceof AbilitiesHandler handler && !player.getType().is(TagRegistry.DASH_MUSHROOM_CANNOT_BOOST)) {
                BlockPos posBelow = player.blockPosition().below();
                BlockState stateBelow = world.getBlockState(posBelow);

                float friction = stateBelow.getBlock().getFriction();
                if (player.isInWaterOrBubble() || player.isFallFlying() || player.getAbilities().flying || stateBelow.isAir())
                    friction = 1.5F;

                double baseBoost = ConfigRegistry.DASH_MUSHROOM_BOOST_STRENGTH.get();
                double boost = baseBoost / friction;
                Vec3 direction = player.getLookAngle().normalize();

                Entity vehicle = player.getVehicle();
                stack.consume(1, player);
                MushroomEntity.powerUp(world, player, null, ConfigRegistry.DASH_MUSHROOM_HEALTH_HEALED.get().floatValue());

                if (vehicle != null) {
                    posBelow = vehicle.blockPosition().below();
                    stateBelow = world.getBlockState(posBelow);
                    friction = stateBelow.getBlock().getFriction();
                    if (vehicle instanceof Boat && friction <= 0.7F)
                        friction = stateBelow.getBlock().getFriction() / 1.5F;
                    if (vehicle instanceof Boat && friction > 0.7F)
                        friction = stateBelow.getBlock().getFriction() * 0.5F;

                    baseBoost = ConfigRegistry.VEHICLE_MUSHROOM_BOOST_STRENGTH.get();
                    if (vehicle instanceof AbstractMinecart)
                        baseBoost = ConfigRegistry.VEHICLE_MUSHROOM_BOOST_STRENGTH.get() / 10;
                    boost = baseBoost / friction;
                    direction = vehicle.getLookAngle().normalize();
                    if (vehicle instanceof AbstractMinecart)
                        direction = player.getLookAngle().normalize();

                    handler.mv$setDashMushroomBoost(true);
                    if (!(vehicle instanceof Boat))
                        vehicle.setDeltaMovement(direction.x * boost, 0, direction.z * boost);
                    player.getCooldowns().addCooldown(stack.getItem(), (int) (boost));

                    if (vehicle.level().isClientSide && vehicle instanceof Boat && vehicle.isControlledByLocalInstance())
                        vehicle.setDeltaMovement(direction.x * boost, 0, direction.z * boost);

                    return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
                } else {
                    handler.mv$setDashMushroomBoost(true);
                    player.setDeltaMovement(direction.x * boost, player.getDeltaMovement().y, direction.z * boost);
                    player.getCooldowns().addCooldown(stack.getItem(), (int) (boost));
                    return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
                }
            }
        }
        return InteractionResultHolder.fail(stack);
    }
}
