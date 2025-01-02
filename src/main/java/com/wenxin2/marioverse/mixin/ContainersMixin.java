package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.blocks.CoinBlock;
import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import com.wenxin2.marioverse.items.BasePowerUpItem;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.ArmorStandItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.EndCrystalItem;
import net.minecraft.world.item.ExperienceBottleItem;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.LingeringPotionItem;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.WindChargeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Containers.class)
public class ContainersMixin {

    @Inject(method = "dropContents(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/Container;)V", at = @At("HEAD"))
    private static void dropContents(Level world, double x, double y, double z, Container container, CallbackInfo ci) {
        int marioverse$stackCount;
        if (container instanceof DecoratedPotBlockEntity decoratedPotBE && !ConfigRegistry.DISABLE_DECORATED_POT_TWEAKS.get()) {
            for (int i = 0; i < container.getContainerSize(); i++) {

                marioverse$playSounds(world, decoratedPotBE.getBlockPos(), container.getItem(i));

                marioverse$stackCount = decoratedPotBE.getTheItem().getCount();
                for (int j = 0; j < marioverse$stackCount; j++) {
                    marioverse$spawnFromContainer(world, decoratedPotBE.getBlockPos(), container.getItem(i), null,
                            ConfigRegistry.DECORATED_POT_SPAWNS_MOBS.get(), ConfigRegistry.DECORATED_POT_SPAWNS_POWER_UPS.get(),
                            TagRegistry.DECORATED_POT_CANNOT_SPAWN);
                }

                decoratedPotBE.removeTheItem();
            }
        } else if (container instanceof QuestionBlockEntity questionBE) {
            for (int i = 0; i < container.getContainerSize(); i++) {

                marioverse$playSounds(world, questionBE.getBlockPos(), container.getItem(i));

                marioverse$stackCount = questionBE.getStackInSlot().getCount();
                for (int j = 0; j < marioverse$stackCount; j++) {
                    marioverse$spawnFromContainer(world, questionBE.getBlockPos(), container.getItem(i), null,
                            ConfigRegistry.QUESTION_SPAWNS_MOBS.get(), ConfigRegistry.QUESTION_SPAWNS_POWER_UPS.get(),
                            TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN);
                }

                for (int j = 0; j < marioverse$stackCount; j++)
                    questionBE.removeItems();
            }
        }
    }

    @Unique
    private static void marioverse$spawnFromContainer(Level world, BlockPos pos, ItemStack stack, Entity entityHitBlock, boolean spawnMobs, boolean spawnPowerUps,
                                                      TagKey<EntityType<?>> cannotSpawn) {
        if (world instanceof ServerLevel serverWorld) {
            if (stack.getItem() instanceof BasePowerUpItem powerUpItem && spawnPowerUps) {
                EntityType<?> entityType = powerUpItem.getType(stack);

                if (!entityType.is(cannotSpawn)) {
                    entityType.spawn(serverWorld, stack, null, pos, MobSpawnType.SPAWN_EGG, true, false);
                    stack.copyWithCount(1);
                } else marioverse$spawnItem(world, pos, stack);
            } else if (stack.getItem() instanceof SpawnEggItem spawnEgg && spawnMobs
                    && !(stack.getItem() instanceof BasePowerUpItem)) {
                EntityType<?> entityType = spawnEgg.getType(stack);

                if (!entityType.is(cannotSpawn)) {
                    entityType.spawn(serverWorld, stack, null, pos, MobSpawnType.SPAWN_EGG, true, false);
                    stack.copyWithCount(1);
                } else marioverse$spawnItem(world, pos, stack);
            } else if (stack.getItem() instanceof ArmorStandItem) {
                Consumer<ArmorStand> consumer = EntityType.createDefaultStackConfig(serverWorld, stack, null);
                ArmorStand armorStand = EntityType.ARMOR_STAND.create(serverWorld, consumer, pos, MobSpawnType.SPAWN_EGG, true, true);

                if (armorStand != null && !armorStand.getType().is(cannotSpawn)) {
                    armorStand.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                    world.addFreshEntity(armorStand);
                    stack.copyWithCount(1);
                } else marioverse$spawnItem(world, pos, stack);
            } else if (stack.getItem() instanceof MinecartItem cart) {
                AbstractMinecart abstractMinecart =
                        AbstractMinecart.createMinecart(serverWorld, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, cart.type, stack, null);

                if (!abstractMinecart.getType().is(cannotSpawn)) {
                    abstractMinecart.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                    world.addFreshEntity(abstractMinecart);
                    stack.copyWithCount(1);
                } else marioverse$spawnItem(world, pos, stack);
            } else if (stack.getItem() instanceof BoatItem boatItem) {
                Boat boat = boatItem.hasChest ? new ChestBoat(serverWorld, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D)
                        : new Boat(serverWorld, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);

                if (!boat.getType().is(cannotSpawn)) {
                    boat.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                    boat.setVariant(boatItem.type);
                    world.addFreshEntity(boat);
                    stack.copyWithCount(1);
                } else marioverse$spawnItem(world, pos, stack);
            } else if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof TntBlock) {
                PrimedTnt primedtnt = new PrimedTnt(serverWorld, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, null);

                if (!primedtnt.getType().is(cannotSpawn)) {
                    primedtnt.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                    world.addFreshEntity(primedtnt);
                    stack.copyWithCount(1);
                    serverWorld.gameEvent(null, GameEvent.PRIME_FUSE, pos);
                } else marioverse$spawnItem(world, pos, stack);
            } else if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CoinBlock
                    && entityHitBlock instanceof Player player) {
                player.addItem(stack.copyWithCount(1));
            } else if (stack.getItem() instanceof WindChargeItem) {
                WindCharge windCharge = new WindCharge(serverWorld, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                        new Vec3(0, -0.5, 0));

                if (!windCharge.getType().is(cannotSpawn)) {
                    windCharge.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                    world.addFreshEntity(windCharge);
                    stack.copyWithCount(1);
                } else marioverse$spawnItem(world, pos, stack);
            } else if (stack.getItem() instanceof FireChargeItem) {
                SmallFireball fireball = new SmallFireball(serverWorld, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                        new Vec3(0, -0.5, 0));

                if (!fireball.getType().is(cannotSpawn)) {
                    fireball.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                    world.addFreshEntity(fireball);
                    stack.copyWithCount(1);
                } else marioverse$spawnItem(world, pos, stack);
            } else if (stack.getItem() instanceof ThrowablePotionItem) {
                ThrownPotion potion = new ThrownPotion(serverWorld, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

                if (!potion.getType().is(cannotSpawn)) {
                    potion.setPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
                    potion.setItem(stack);
                    world.addFreshEntity(potion);
                    stack.copyWithCount(1);
                } else marioverse$spawnItem(world, pos, stack);
            } else if (stack.getItem() instanceof ExperienceBottleItem) {
                ThrownExperienceBottle xpBottle = new ThrownExperienceBottle(serverWorld, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

                if (!xpBottle.getType().is(cannotSpawn)) {
                    xpBottle.setPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
                    xpBottle.setItem(stack);
                    world.addFreshEntity(xpBottle);
                    stack.copyWithCount(1);
                } else marioverse$spawnItem(world, pos, stack);
            } else if (stack.getItem() instanceof EndCrystalItem) {
                EndCrystal endCrystal = new EndCrystal(serverWorld, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

                if (!endCrystal.getType().is(cannotSpawn)) {
                    endCrystal.setPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
                    world.addFreshEntity(endCrystal);
                    world.gameEvent(null, GameEvent.ENTITY_PLACE, pos);
                    stack.copyWithCount(1);
                } else marioverse$spawnItem(world, pos, stack);
            } else if (stack.getItem() instanceof EggItem) {
                ThrownEgg egg = new ThrownEgg(serverWorld, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

                if (!egg.getType().is(cannotSpawn)) {
                    egg.setPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
                    egg.setItem(stack);
                    world.addFreshEntity(egg);
                    stack.copyWithCount(1);
                } else marioverse$spawnItem(world, pos, stack);
            } else marioverse$spawnItem(world, pos, stack);
        }
    }

    @Unique
    private static void marioverse$spawnItem(Level world, BlockPos pos, ItemStack stack) {
        ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, stack.split(world.random.nextInt(21) + 10));
        itemEntity.setDeltaMovement(
                world.random.triangle(0.0, 0.11485000171139836),
                world.random.triangle(0.2, 0.11485000171139836),
                world.random.triangle(0.0, 0.11485000171139836)
        );
        world.addFreshEntity(itemEntity);
    }

    @Unique
    private static void marioverse$playSounds(Level world, BlockPos pos, ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CoinBlock)
            world.playSound(null, pos, SoundRegistry.COIN_PICKUP.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof TntBlock)
            world.playSound(null, pos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof ArmorStandItem)
            world.playSound(null, pos, SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof BasePowerUpItem)
            world.playSound(null, pos, SoundRegistry.POWER_UP_SPAWNS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof BoatItem)
            world.playSound(null, pos, SoundEvents.BOAT_PADDLE_WATER, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof EggItem)
            world.playSound(null, pos, SoundEvents.EGG_THROW, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof ExperienceBottleItem)
            world.playSound(null, pos, SoundEvents.EXPERIENCE_BOTTLE_THROW, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof FireChargeItem)
            world.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof LingeringPotionItem)
            world.playSound(null, pos, SoundEvents.LINGERING_POTION_THROW, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof MinecartItem)
            world.playSound(null, pos, SoundEvents.MINECART_RIDING, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof PotionItem)
            world.playSound(null, pos, SoundEvents.SPLASH_POTION_THROW, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof SpawnEggItem)
            world.playSound(null, pos, SoundRegistry.MOB_SPAWNS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof WindChargeItem)
            world.playSound(null, pos, SoundEvents.WIND_CHARGE_THROW, SoundSource.BLOCKS, 1.0F, 1.0F);
        else world.playSound(null, pos, SoundRegistry.ITEM_SPAWNS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}
