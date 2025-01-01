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
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.ArmorStandItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Containers.class)
public class ContainersMixin {

    private static int stackCount = 0;

    @Inject(method = "dropContents(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/Container;)V", at = @At("HEAD"))
    private static void dropContents(Level world, double x, double y, double z, Container container, CallbackInfo ci) {
        if (container instanceof DecoratedPotBlockEntity decoratedPotBE && !ConfigRegistry.DISABLE_DECORATED_POT_TWEAKS.get()) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                stackCount = decoratedPotBE.getTheItem().getCount();
                for (int j = 0; j < stackCount; j++) {
                    marioverse$spawnFromContainer(world, new BlockPos((int) x, (int) y, (int) z), container.getItem(i), null,
                            ConfigRegistry.DECORATED_POT_SPAWNS_MOBS.get(), ConfigRegistry.DECORATED_POT_SPAWNS_POWER_UPS.get(),
                            TagRegistry.DECORATED_POT_CANNOT_SPAWN);
                }

                if (container.getItem(i).getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CoinBlock)
                    marioverse$playCoinSound(world, new BlockPos((int) x, (int) y, (int) z));
                else if (container.getItem(i).getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof TntBlock)
                    marioverse$playPrimedTNTSound(world, new BlockPos((int) x, (int) y, (int) z));
                else if (container.getItem(i).getItem() instanceof ArmorStandItem)
                    marioverse$playArmorStandSound(world, new BlockPos((int) x, (int) y, (int) z));
                else if (container.getItem(i).getItem() instanceof BoatItem)
                    marioverse$playBoatSound(world, new BlockPos((int) x, (int) y, (int) z));
                else if (container.getItem(i).getItem() instanceof MinecartItem)
                    marioverse$playMinecartSound(world, new BlockPos((int) x, (int) y, (int) z));

                decoratedPotBE.removeTheItem();
            }
        } else if (container instanceof QuestionBlockEntity questionBE) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                stackCount = questionBE.getStackInSlot().getCount();
                for (int j = 0; j < stackCount; j++) {
                    marioverse$spawnFromContainer(world, new BlockPos((int) x, (int) y, (int) z), container.getItem(i), null,
                            ConfigRegistry.QUESTION_SPAWNS_MOBS.get(), ConfigRegistry.QUESTION_SPAWNS_POWER_UPS.get(),
                            TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN);
                }

                if (container.getItem(i).getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CoinBlock)
                    marioverse$playCoinSound(world, new BlockPos((int) x, (int) y, (int) z));
                else if (container.getItem(i).getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof TntBlock)
                    marioverse$playPrimedTNTSound(world, new BlockPos((int) x, (int) y, (int) z));
                else if (container.getItem(i).getItem() instanceof BasePowerUpItem)
                    marioverse$playPowerUpSound(world, new BlockPos((int) x, (int) y, (int) z));
                else if (container.getItem(i).getItem() instanceof SpawnEggItem)
                    marioverse$playMobSound(world, new BlockPos((int) x, (int) y, (int) z));
                else if (container.getItem(i).getItem() instanceof ArmorStandItem)
                    marioverse$playArmorStandSound(world, new BlockPos((int) x, (int) y, (int) z));
                else if (container.getItem(i).getItem() instanceof BoatItem)
                    marioverse$playBoatSound(world, new BlockPos((int) x, (int) y, (int) z));
                else if (container.getItem(i).getItem() instanceof MinecartItem)
                    marioverse$playMinecartSound(world, new BlockPos((int) x, (int) y, (int) z));
                else marioverse$playItemSound(world, new BlockPos((int) x, (int) y, (int) z));

                for (int j = 0; j < stackCount; j++)
                    questionBE.removeItems();
            }
        }
    }

    @Unique
    private static void marioverse$spawnFromContainer(Level world, BlockPos pos, ItemStack stack, Entity entityHitBlock, boolean spawnMobs, boolean spawnPowerUps,
                                                      TagKey<EntityType<?>> cannotSpawn) {
        if (stack.getItem() instanceof BasePowerUpItem powerUpItem && spawnPowerUps) {
            EntityType<?> entityType = powerUpItem.getType(stack);

            if (world instanceof ServerLevel serverWorld && !entityType.is(cannotSpawn)) {
                entityType.spawn(serverWorld, stack, null, pos, MobSpawnType.SPAWN_EGG, true, false);
                stack.copyWithCount(1);
            } else marioverse$spawnItem(world, pos, stack);
        } else if (stack.getItem() instanceof SpawnEggItem spawnEgg && spawnMobs
                && !(stack.getItem() instanceof BasePowerUpItem)) {
            EntityType<?> entityType = spawnEgg.getType(stack);

            if (world instanceof ServerLevel serverWorld && !entityType.is(cannotSpawn)) {
                entityType.spawn(serverWorld, stack, null, pos, MobSpawnType.SPAWN_EGG, true, false);
                stack.copyWithCount(1);
            } else marioverse$spawnItem(world, pos, stack);
        } else if (stack.getItem() instanceof ArmorStandItem && world instanceof ServerLevel serverWorld) {
            Consumer<ArmorStand> consumer = EntityType.createDefaultStackConfig(serverWorld, stack, null);
            ArmorStand armorStand = EntityType.ARMOR_STAND.create(serverWorld, consumer, pos, MobSpawnType.SPAWN_EGG, true, true);

            if (armorStand != null && !armorStand.getType().is(cannotSpawn)) {
                armorStand.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                world.addFreshEntity(armorStand);
                stack.copyWithCount(1);
            } else marioverse$spawnItem(world, pos, stack);
        } else if (stack.getItem() instanceof MinecartItem cart && world instanceof ServerLevel serverWorld) {
            AbstractMinecart abstractMinecart =
                    AbstractMinecart.createMinecart(serverWorld, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, cart.type, stack, null);

            if (!abstractMinecart.getType().is(cannotSpawn)) {
                abstractMinecart.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                world.addFreshEntity(abstractMinecart);
                stack.copyWithCount(1);
            } else marioverse$spawnItem(world, pos, stack);
        } else if (stack.getItem() instanceof BoatItem boatItem && world instanceof ServerLevel serverWorld) {
            Boat boat = boatItem.hasChest ? new ChestBoat(serverWorld, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D)
                    : new Boat(serverWorld, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);

            if (!boat.getType().is(cannotSpawn)) {
                boat.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                boat.setVariant(boatItem.type);
                world.addFreshEntity(boat);
                stack.copyWithCount(1);
            } else marioverse$spawnItem(world, pos, stack);
        } else if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof TntBlock && world instanceof ServerLevel serverWorld) {
            PrimedTnt primedtnt = new PrimedTnt(serverWorld, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, null);

            if (!primedtnt.getType().is(cannotSpawn)) {
                primedtnt.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                world.addFreshEntity(primedtnt);
                stack.copyWithCount(1);
                serverWorld.gameEvent(null, GameEvent.PRIME_FUSE, pos);
            } else marioverse$spawnItem(world, pos, stack);
        } else if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CoinBlock
                && entityHitBlock instanceof Player player && world instanceof ServerLevel) {
            player.addItem(stack.copyWithCount(1));
        } else marioverse$spawnItem(world, pos, stack);
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
    private static void marioverse$playArmorStandSound(Level world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Unique
    private static void marioverse$playBoatSound(Level world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.BOAT_PADDLE_WATER, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Unique
    private static void marioverse$playCoinSound(Level world, BlockPos pos) {
        world.playSound(null, pos, SoundRegistry.COIN_PICKUP.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Unique
    private static void marioverse$playItemSound(Level world, BlockPos pos) {
        world.playSound(null, pos, SoundRegistry.ITEM_SPAWNS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Unique
    private static void marioverse$playMinecartSound(Level world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.MINECART_RIDING, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Unique
    private static void marioverse$playMobSound(Level world, BlockPos pos) {
        world.playSound(null, pos, SoundRegistry.MOB_SPAWNS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Unique
    private static void marioverse$playPowerUpSound(Level world, BlockPos pos) {
        world.playSound(null, pos, SoundRegistry.POWER_UP_SPAWNS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Unique
    private static void marioverse$playPrimedTNTSound(Level world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}
