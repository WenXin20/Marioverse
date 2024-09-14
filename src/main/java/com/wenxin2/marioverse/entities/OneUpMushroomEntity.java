package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.ItemRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import com.wenxin2.marioverse.items.OneUpMushroomItem;
import java.util.Optional;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class OneUpMushroomEntity extends MushroomEntity implements GeoEntity {
    private long lastCollisionTime = 0;

    public OneUpMushroomEntity(EntityType<? extends OneUpMushroomEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();
        checkForCollisions();
    }

    @Override
    public void handleCollision(Entity entity) {
        if (!this.level().isClientSide) {
            long currentTime = System.currentTimeMillis();
            ItemLike item = ItemRegistry.ONE_UP_MUSHROOM;

            if (currentTime - lastCollisionTime < 500) {
                return; // Skip if called too soon
            }
            lastCollisionTime = currentTime;

            if (entity instanceof Player player && !player.isSpectator()
                    && ConfigRegistry.DAMAGE_SHRINKS_PLAYERS.get()
                    && !player.getType().is(TagRegistry.DAMAGE_SHRINKS_ENTITY_BLACKLIST)) {
                Optional<ICuriosItemHandler> curiosHandler = CuriosApi.getCuriosInventory(player);
                ItemStack offhandStack = player.getOffhandItem();

                if (curiosHandler.isPresent()) {
                    ICuriosItemHandler handler = curiosHandler.get();
                    Optional<SlotResult> charmSlot = handler.findCurio("charm", 0);
                    if (charmSlot.isEmpty()) {
                        handler.setEquippedCurio("charm", 0, item.asItem().getDefaultInstance());
                    } else if (!(offhandStack.getCount() >= offhandStack.getMaxStackSize() + 1)) {
                        ItemStack itemInSlot = charmSlot.get().stack();
                        itemInSlot.grow(1);
                    } else if (offhandStack.isEmpty())
                        player.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(item));
                    else if (offhandStack.getItem() instanceof OneUpMushroomItem) {
                        if (offhandStack.getCount() >= offhandStack.getMaxStackSize() + 1) {
                            player.drop(offhandStack.split(1), Boolean.FALSE);
                        } else offhandStack.grow(1);
                    }
                }

                this.level().playSound(null, this.blockPosition(), SoundRegistry.ONE_UP_COLLECTED.get(),
                        SoundSource.PLAYERS, 1.0F, 1.0F);
                this.level().broadcastEntityEvent(this, (byte) 60); // Mushroom Transform particle
                this.level().broadcastEntityEvent(this, (byte) 61); // 1-Up Collected particle
                if (!player.getType().is(TagRegistry.CONSUME_POWER_UPS_ENTITY_BLACKLIST))
                    this.remove(RemovalReason.KILLED);

            } else if (entity instanceof LivingEntity livingEntity && ConfigRegistry.DAMAGE_SHRINKS_ALL_MOBS.get()
                    && !(entity instanceof Player)) {
                ItemStack offhandStack = livingEntity.getOffhandItem();

                if (offhandStack.isEmpty())
                    livingEntity.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(item));
                else if (offhandStack.getItem() instanceof OneUpMushroomItem) {
                    offhandStack.grow(1);
                }

                this.level().playSound(null, this.blockPosition(), SoundRegistry.ONE_UP_COLLECTED.get(),
                        SoundSource.PLAYERS, 1.0F, 1.0F);
                this.level().broadcastEntityEvent(this, (byte) 60); // Mushroom Transform particle
                this.level().broadcastEntityEvent(this, (byte) 61); // 1-Up Collected particle
                if (!livingEntity.getType().is(TagRegistry.CONSUME_POWER_UPS_ENTITY_BLACKLIST))
                    this.remove(RemovalReason.KILLED);
            }
        }
    }
}
