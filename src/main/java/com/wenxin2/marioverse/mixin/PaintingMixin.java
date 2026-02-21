package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.entities.WarpLinkableEntity;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Painting.class)
public abstract class PaintingMixin extends HangingEntity implements WarpLinkableEntity {
    @Unique private static final String BREAK_PAINTING = "BreakPainting";
    @Unique private static final String IS_WAXED = "IsWaxed";
    @Unique private static final String PREVENT_WARP = "PreventWarp";
    @Unique private static final String WARP_FUEL_COUNT = "WarpFuelCount";
    @Unique public boolean mv$breakPainting = Boolean.FALSE;
    @Unique public boolean mv$isWaxed;
    @Unique public boolean mv$preventWarp = Boolean.FALSE;
    @Unique private int mv$warpFuelCount;

    public PaintingMixin(EntityType<? extends HangingEntity> type, Level world) {
        super(type, world);
    }

//    @Override
//    public boolean mv$isBreakPainting() {
//        return this.mv$breakPainting;
//    }
//
//    @Override
//    public void mv$setBreakPainting(boolean breakPainting) {
//        this.mv$breakPainting = breakPainting;
//    }
//
//    @Override
//    public boolean mv$getPreventWarp() {
//        return this.mv$preventWarp;
//    }
//
//    @Override
//    public void mv$setPreventWarp(boolean preventWarp) {
//        this.mv$preventWarp = preventWarp;
//    }
}
