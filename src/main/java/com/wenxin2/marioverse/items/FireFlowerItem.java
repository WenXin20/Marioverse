package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.entities.projectiles.BouncingFireballProjectile;
import java.util.function.Supplier;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;

public class FireFlowerItem extends BasePowerUpItem implements ProjectileItem {
    public FireFlowerItem(Supplier<? extends EntityType<? extends Mob>> entityType,
                          int primaryColor, int secondaryColor, Properties properties) {
        super(entityType, primaryColor, secondaryColor, properties);
    }

    @Override
    public Projectile asProjectile(Level world, Position pos, ItemStack stack, Direction direction) {
        RandomSource randomsource = world.getRandom();
        double d0 = randomsource.triangle(direction.getStepX(), 0.11485000000000001);
        double d1 = randomsource.triangle(direction.getStepY(), 0.11485000000000001);
        double d2 = randomsource.triangle(direction.getStepZ(), 0.11485000000000001);
        Vec3 vec3 = new Vec3(d0, d1, d2);

        BouncingFireballProjectile fireballProjectile = new BouncingFireballProjectile(world, pos.x(), pos.y(), pos.z());
        fireballProjectile.setDeltaMovement(vec3);
        return fireballProjectile;
    }

    @Override
    public ProjectileItem.DispenseConfig createDispenseConfig() {
        return ProjectileItem.DispenseConfig.builder()
                .positionFunction((p_338288_, p_338801_) -> DispenserBlock.getDispensePosition(p_338288_, 1.0, Vec3.ZERO))
                .uncertainty(6.6666665F)
                .power(1.0F)
                .overrideDispenseEvent(1051)
                .build();
    }
}
