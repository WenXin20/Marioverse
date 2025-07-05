package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.entities.power_ups.MushroomEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class MushroomItem extends BasePowerUpItem {
    public MushroomItem(Supplier<? extends EntityType<? extends Mob>> entityType,
                        int primaryColor, int secondaryColor, Properties properties) {
        super(entityType, primaryColor, secondaryColor, properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        if (!(entity instanceof Player))
            mushroomAbilities(stack, world, entity);
        return super.finishUsingItem(stack, world, entity);
    }

    public void mushroomAbilities(ItemStack stack, Level world, LivingEntity entity) {
        if (ConfigRegistry.MUSHROOM_BOOST_STRENGTH.get() > 0 || ConfigRegistry.VEHICLE_MUSHROOM_BOOST_STRENGTH.get() > 0) {
            if (entity instanceof AbilitiesHandler handler) {
                BlockPos posBelow = entity.blockPosition().below();
                BlockState stateBelow = world.getBlockState(posBelow);

                float friction = stateBelow.getBlock().getFriction();
                if (entity.isInWaterOrBubble() || entity.isFallFlying() || stateBelow.isAir()) friction = 1.5F;

                double baseBoost = ConfigRegistry.MUSHROOM_BOOST_STRENGTH.get();
                double boost = baseBoost / friction;
                Vec3 direction = entity.getLookAngle().normalize();

                Entity vehicle = entity.getVehicle();
                stack.consume(1, entity);
                MushroomEntity.powerUp(world, entity, null);

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
                        direction = entity.getLookAngle().normalize();

                    handler.mv$setMushroomBoost(true);
                    if (!(vehicle instanceof Boat))
                        vehicle.setDeltaMovement(direction.x * boost, 0, direction.z * boost);

                    if (vehicle.level().isClientSide && vehicle instanceof Boat && vehicle.isControlledByLocalInstance())
                        vehicle.setDeltaMovement(direction.x * boost, 0, direction.z * boost);
                } else {
                    handler.mv$setMushroomBoost(true);
                    entity.setDeltaMovement(direction.x * boost, entity.getDeltaMovement().y, direction.z * boost);
                }
            }
        }
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (ConfigRegistry.MUSHROOM_BOOST_STRENGTH.get() > 0 || ConfigRegistry.VEHICLE_MUSHROOM_BOOST_STRENGTH.get() > 0) {
            if (player instanceof AbilitiesHandler handler) {
                BlockPos posBelow = player.blockPosition().below();
                BlockState stateBelow = world.getBlockState(posBelow);

                float friction = stateBelow.getBlock().getFriction();
                if (player.isInWaterOrBubble() || player.isFallFlying() || player.getAbilities().flying || stateBelow.isAir())
                    friction = 1.5F;

                double baseBoost = ConfigRegistry.MUSHROOM_BOOST_STRENGTH.get();
                double boost = baseBoost / friction;
                Vec3 direction = player.getLookAngle().normalize();

                Entity vehicle = player.getVehicle();
                stack.consume(1, player);
                MushroomEntity.powerUp(world, player, null);

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

                    handler.mv$setMushroomBoost(true);
                    if (!(vehicle instanceof Boat))
                        vehicle.setDeltaMovement(direction.x * boost, 0, direction.z * boost);
                    player.getCooldowns().addCooldown(stack.getItem(), (int) (boost));

                    if (vehicle.level().isClientSide && vehicle instanceof Boat && vehicle.isControlledByLocalInstance())
                        vehicle.setDeltaMovement(direction.x * boost, 0, direction.z * boost);

                    return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
                } else {
                    handler.mv$setMushroomBoost(true);
                    player.setDeltaMovement(direction.x * boost, player.getDeltaMovement().y, direction.z * boost);
                    player.getCooldowns().addCooldown(stack.getItem(), (int) (boost));
                    return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
                }
            }
        }
        return InteractionResultHolder.fail(stack);
    }
}
