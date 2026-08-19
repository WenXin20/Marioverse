package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.entities.WoodTypeBoat;
import com.wenxin2.marioverse.entities.WoodTypeChestBoat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;

public class WoodTypeBoatItem extends BoatItem {
    private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);
    private final EntityType<? extends Boat> boatEntityType;

    public WoodTypeBoatItem(boolean hasChest, EntityType<? extends Boat> boatEntityType, Properties properties) {
        super(hasChest, Boat.Type.OAK, properties);
        this.boatEntityType = boatEntityType;
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        HitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);

        if (hitResult.getType() == HitResult.Type.MISS)
            return InteractionResultHolder.pass(itemStack);

        Vec3 viewVec = player.getViewVector(1.0F);
        List<Entity> nearbyEntities = level.getEntities(player,
                player.getBoundingBox().expandTowards(viewVec.scale(5.0)).inflate(1.0), ENTITY_PREDICATE);

        if (!nearbyEntities.isEmpty()) {
            Vec3 eyePos = player.getEyePosition();
            for (Entity entity : nearbyEntities) {
                AABB aabb = entity.getBoundingBox().inflate(entity.getPickRadius());
                if (aabb.contains(eyePos))
                    return InteractionResultHolder.pass(itemStack);
            }
        }

        if (hitResult.getType() != HitResult.Type.BLOCK)
            return InteractionResultHolder.pass(itemStack);

        Boat boat = this.createBoat(level, hitResult, itemStack, player);
        boat.setYRot(player.getYRot());

        if (!level.noCollision(boat, boat.getBoundingBox()))
            return InteractionResultHolder.fail(itemStack);

        if (!level.isClientSide) {
            level.addFreshEntity(boat);
            level.gameEvent(player, GameEvent.ENTITY_PLACE, hitResult.getLocation());
            itemStack.consume(1, player);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    @SuppressWarnings("unchecked")
    private Boat createBoat(Level level, HitResult hitResult, ItemStack stack, Player player) {
        Vec3 vec3 = hitResult.getLocation();

        Boat boat = this.hasChest
                ? new WoodTypeChestBoat((EntityType<ChestBoat>) this.boatEntityType, level, this)
                : new WoodTypeBoat(this.boatEntityType, level, this);

        boat.setPos(vec3.x, vec3.y, vec3.z);
        if (level instanceof ServerLevel serverLevel)
            EntityType.<Boat>createDefaultStackConfig(serverLevel, stack, player).accept(boat);
        return boat;
    }
}