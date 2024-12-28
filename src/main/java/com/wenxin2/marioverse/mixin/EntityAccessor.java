package com.wenxin2.marioverse.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor("dimensions")
    void setDimensions(EntityDimensions dimensions);

    @Accessor("eyeHeight")
    void setEyeHeight(float eyeHeight);

    @Accessor("dimensions")
    EntityDimensions getDimensions();
}
