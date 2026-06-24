package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.blocks.CoinBlock;
import com.wenxin2.marioverse.blocks.StarCoinBlock;
import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.entities.PiranhaPlantEntity;
import com.wenxin2.marioverse.entities.projectiles.LargeSnowballProjectile;
import com.wenxin2.marioverse.items.DashMushroomItem;
import com.wenxin2.marioverse.items.LargeSnowballItem;
import com.wenxin2.marioverse.items.PiranhaPlantPodItem;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.items.BasePowerUpItem;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
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
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.EndCrystalItem;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ExperienceBottleItem;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.LingeringPotionItem;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.WindChargeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Containers.class)
public class ContainersMixin {

    @Inject(method = "dropContents(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/Container;)V", at = @At("HEAD"))
    private static void dropContents(Level world, double x, double y, double z, Container container, CallbackInfo ci) {
        int mv$stackCount;
        if (container instanceof DecoratedPotBlockEntity decoratedPotBE && !ConfigRegistry.DISABLE_DECORATED_POT_TWEAKS.get()) {
            for (int i = 0; i < container.getContainerSize(); i++) {

                mv$playSounds(world, decoratedPotBE.getBlockPos(), container.getItem(i), decoratedPotBE);

                mv$stackCount = decoratedPotBE.getTheItem().getCount();
                for (int j = 0; j < mv$stackCount; j++) {
                    mv$spawnFromContainer2(world, decoratedPotBE.getBlockPos(), container.getItem(i),
                            ConfigRegistry.DECORATED_POT_SPAWNS_MOBS.get(), ConfigRegistry.DECORATED_POT_SPAWNS_POWER_UPS.get(),
                            ConfigRegistry.DECORATED_POT_BUCKET_TWEAKS.get(), TagRegistry.DECORATED_POT_CANNOT_SPAWN);
                }
                decoratedPotBE.removeTheItem();
            }
        } else if (container instanceof QuestionBlockEntity questionBE) {
            for (int i = 0; i < container.getContainerSize(); i++) {

                mv$playSounds(world, questionBE.getBlockPos(), container.getItem(i), questionBE);

                mv$stackCount = questionBE.getTheItem().getCount();
                for (int j = 0; j < mv$stackCount; j++) {
                    mv$spawnFromContainer2(world, questionBE.getBlockPos(), container.getItem(i),
                            ConfigRegistry.QUESTION_SPAWNS_MOBS.get(), ConfigRegistry.QUESTION_SPAWNS_POWER_UPS.get(),
                            ConfigRegistry.QUESTION_BUCKET_TWEAKS.get(), TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN);
                }

                for (int j = 0; j < mv$stackCount; j++)
                    questionBE.removeTheItem();
            }
        }
    }

    @Unique
    private static boolean mv$useItem(ServerLevel level, BlockPos pos, ItemStack stack) {
        FakePlayer fakePlayer = FakePlayerFactory.getMinecraft(level);
        UseAnim anim = stack.getUseAnimation();

        fakePlayer.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 90.0F);
        fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, stack);

        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
        InteractionResult result = stack.use(level, fakePlayer, InteractionHand.MAIN_HAND).getResult();

        if (stack.getItem() instanceof Equipable || stack.getItem() instanceof EnderpearlItem
                || stack.get(DataComponents.FOOD) != null)
            return false;

        if (stack.getItem() instanceof PotionItem && !(stack.getItem() instanceof ThrowablePotionItem))
            return false;

        if (anim == UseAnim.BOW || anim == UseAnim.CROSSBOW || anim == UseAnim.SPEAR
                || anim == UseAnim.BLOCK || anim == UseAnim.SPYGLASS || anim == UseAnim.TOOT_HORN
                || anim == UseAnim.EAT)
            return false;

        if (!result.consumesAction()) {
            BlockPos targetPos = pos.below();
            hit = new BlockHitResult(Vec3.atCenterOf(targetPos).add(0.0D, 0.5D, 0.0D),
                    Direction.UP, targetPos, false);
            fakePlayer.moveTo(pos.getX() + 0.5D, pos.getY() + 1.5D, pos.getZ() + 0.5D, 0.0F, 90.0F);
            result = stack.useOn(new UseOnContext(fakePlayer, InteractionHand.MAIN_HAND, hit));
        }

        return result.consumesAction() || stack.isEmpty() || stack.getCount() != 1;
    }

    @Unique
    private static void mv$spawnFromContainer2(Level level, BlockPos pos, ItemStack stack, boolean spawnMobs,
                                               boolean spawnPowerUps, boolean canEmptyBuckets, TagKey<EntityType<?>> cannotSpawn) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        ItemStack stackCopy = stack.copyWithCount(1);

        if (!spawnMobs && stackCopy.getItem() instanceof SpawnEggItem spawnEgg) {
            EntityType<?> entityType = spawnEgg.getType(stack);
            if (entityType.is(cannotSpawn))
                return;
            mv$spawnItem(level, pos, stackCopy);
            return;
        }

        if (!spawnPowerUps && stackCopy.getItem() instanceof BasePowerUpItem powerUpItem) {
            EntityType<?> entityType = powerUpItem.getType(stack);
            if (entityType.is(cannotSpawn))
                return;
            mv$spawnItem(level, pos, stackCopy);
            return;
        }

        if (!canEmptyBuckets && (stackCopy.getItem() instanceof BucketItem
                || stackCopy.getItem() instanceof SolidBucketItem)) {
            mv$spawnItem(level, pos, stackCopy);
            return;
        }

        if (stackCopy.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof TntBlock) {
            PrimedTnt primedtnt = new PrimedTnt(serverLevel, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, null);

            if (!primedtnt.getType().is(cannotSpawn)) {
                primedtnt.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                level.addFreshEntity(primedtnt);
                serverLevel.gameEvent(null, GameEvent.PRIME_FUSE, pos);
                return;
            }
        }

        if (stack.getItem() instanceof MinecartItem cart) {
            AbstractMinecart abstractMinecart =
                    AbstractMinecart.createMinecart(serverLevel, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, cart.type, stack, null);

            if (!abstractMinecart.getType().is(cannotSpawn)) {
                abstractMinecart.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                level.addFreshEntity(abstractMinecart);
                stack.copyWithCount(1);
                return;
            }
        }

        if (stack.getItem() == CompatRegistry.BOMB_ITEM.get()) {
            Entity entity = CompatRegistry.BOMB.get().create(serverLevel);

            if (entity != null && !entity.getType().is(cannotSpawn)) {
                if (level.getBlockState(pos.above()).isAir() || level.getFluidState(pos.above()).is(FluidTags.WATER)) {
                    entity.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                    entity.setDeltaMovement(new Vec3(
                            level.random.triangle(0.0, 0.2),
                            level.random.triangle(0.5, 0.2),
                            level.random.triangle(0.0, 0.2)));
                } else {
                    entity.setPos(pos.getX() + 0.5D, pos.getY() - entity.getBbHeight(), pos.getZ() + 0.5D);
                    entity.setDeltaMovement(new Vec3(0, -0.5, 0));
                }
                level.addFreshEntity(entity);
            }
            return;
        } else if (stack.getItem() == CompatRegistry.BOMB_BLUE_ITEM.get()) {
            Entity entity = CompatRegistry.BOMB.get().create(serverLevel);

            if (entity != null && !entity.getType().is(cannotSpawn)) {
                CompoundTag nbt = new CompoundTag();
                entity.save(nbt);
                nbt.putInt("Type", 1);
                entity.load(nbt);

                entity.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                entity.setDeltaMovement(new Vec3(
                        level.random.triangle(0.0, 0.2),
                        level.random.triangle(0.5, 0.2),
                        level.random.triangle(0.0, 0.2)));
                level.addFreshEntity(entity);
            }
            return;
        } else if (stack.getItem() == CompatRegistry.BOMB_SPIKY_ITEM.get()) {
            Entity entity = CompatRegistry.BOMB.get().create(serverLevel);

            if (entity != null && !entity.getType().is(cannotSpawn)) {
                CompoundTag nbt = new CompoundTag();
                entity.save(nbt);
                nbt.putInt("Type", 2);
                entity.load(nbt);

                entity.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                entity.setDeltaMovement(new Vec3(
                        level.random.triangle(0.0, 0.2),
                        level.random.triangle(0.5, 0.2),
                        level.random.triangle(0.0, 0.2)));
                level.addFreshEntity(entity);
            }
            return;
        } else if (stackCopy.getItem() == CompatRegistry.CONFETTI_POPPER_ITEM.get()) {
            Creeper entity = EntityType.CREEPER.create(serverLevel);

            if (entity != null) {
                CompoundTag nbt = new CompoundTag();
                entity.save(nbt);
                nbt.putBoolean("Party", true);
                nbt.putInt("Fuse", 0);

                entity.setNoAi(true);
                entity.ignite();
                entity.setInvisible(true);
                entity.setSilent(true);
                entity.load(nbt);

                entity.setPos(pos.getX() + 0.5D, pos.getY() - 1.0D, pos.getZ() + 0.5D);
                level.broadcastEntityEvent(entity, (byte) 113);
                level.addFreshEntity(entity);
            }
            level.gameEvent(null, GameEvent.EXPLODE, pos);
        } else if (stack.getItem() == CompatRegistry.CANNONBALL_ITEM.get()) {
            Entity entity = CompatRegistry.CANNONBALL.get().create(serverLevel);

            if (entity != null && !entity.getType().is(cannotSpawn)) {
                entity.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                entity.setDeltaMovement(new Vec3(
                        level.random.triangle(0.0, 0.3),
                        level.random.triangle(0.5, 0.3),
                        level.random.triangle(0.0, 0.3)));
                level.addFreshEntity(entity);
                stack.copyWithCount(1);
            } else mv$spawnItem(level, pos, stack);
        }

        if (stackCopy.getItem() instanceof BlockItem blockItem
                && !blockItem.getBlock().defaultBlockState().is(TagRegistry.QUESTION_BLOCKS_CAN_PLACE)) {
            mv$spawnItem(level, pos, stackCopy);
            return;
        }

        if (!mv$useItem(serverLevel, pos, stackCopy))
            mv$spawnItem(level, pos, stackCopy);
    }

    @Unique
    private static void mv$spawnFromContainer(Level level, BlockPos pos, ItemStack stack, boolean spawnMobs, boolean spawnPowerUps,
                                                      boolean canEmptyBuckets, TagKey<EntityType<?>> cannotSpawn) {
        if (level instanceof ServerLevel serverLevel) {
            if (stack.getItem() instanceof FireChargeItem) {
                SmallFireball fireball = new SmallFireball(serverLevel, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                        new Vec3(0, -0.5, 0));

                if (!fireball.getType().is(cannotSpawn)) {
                    fireball.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                    level.addFreshEntity(fireball);
                    stack.copyWithCount(1);
                } else mv$spawnItem(level, pos, stack);
            } else if (stack.getItem() instanceof EndCrystalItem) {
                EndCrystal endCrystal = new EndCrystal(serverLevel, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

                if (!endCrystal.getType().is(cannotSpawn)) {
                    endCrystal.setPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
                    endCrystal.setDeltaMovement(new Vec3(0, -0.5, 0));
                    endCrystal.setShowBottom(false);
                    level.addFreshEntity(endCrystal);
                    level.gameEvent(null, GameEvent.ENTITY_PLACE, pos);
                    stack.copyWithCount(1);
                } else mv$spawnItem(level, pos, stack);
            } else mv$spawnItem(level, pos, stack);
        }
    }

    @Unique
    private static void mv$spawnItem(Level world, BlockPos pos, ItemStack stack) {
        ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, stack.split(world.random.nextInt(21) + 10));
        itemEntity.setDeltaMovement(
                world.random.triangle(0.0, 0.11485000171139836),
                world.random.triangle(0.2, 0.11485000171139836),
                world.random.triangle(0.0, 0.11485000171139836)
        );
        world.addFreshEntity(itemEntity);
    }

    @Unique
    private static void mv$playSounds(Level world, BlockPos pos, ItemStack stack, Container container) {
        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof StarCoinBlock)
            world.playSound(null, pos, SoundRegistry.STAR_COIN_PICKUP.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CoinBlock)
            world.playSound(null, pos, SoundRegistry.COIN_PICKUP.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof TntBlock)
            world.playSound(null, pos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof ArmorStandItem)
            world.playSound(null, pos, SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.is(ItemRegistry.MEGA_MUSHROOM))
            world.playSound(null, pos, SoundRegistry.MEGA_MUSHROOM_SPAWNS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof BasePowerUpItem || stack.getItem() instanceof DashMushroomItem)
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
        else if (stack.getItem() == CompatRegistry.BOMB_ITEM.get() || stack.getItem() == CompatRegistry.BOMB_BLUE_ITEM.get()
                || stack.getItem() == CompatRegistry.BOMB_SPIKY_ITEM.get() && CompatRegistry.BOMB_SOUND.get() != null)
            world.playSound(null, pos, CompatRegistry.BOMB_SOUND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() == CompatRegistry.CANNONBALL_ITEM.get() && CompatRegistry.CANNON_SOUND.get() != null)
            world.playSound(null, pos, CompatRegistry.CANNON_SOUND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() == CompatRegistry.CONFETTI_POPPER_ITEM.get() && CompatRegistry.CONFETTI_POPPER_SOUND.get() != null)
            world.playSound(null, pos, CompatRegistry.CONFETTI_POPPER_SOUND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() == CompatRegistry.HAT_STAND_ITEM.get())
            world.playSound(null, pos, SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() == CompatRegistry.ICE_BOMB_ITEM.get() && CompatRegistry.ICE_BOMB_SOUND.get() != null)
            world.playSound(null, pos, CompatRegistry.ICE_BOMB_SOUND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (!stack.isEmpty() && !(container instanceof DecoratedPotBlockEntity))
            world.playSound(null, pos, SoundRegistry.ITEM_SPAWNS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}
