package com.wenxin2.marioverse.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;

public class DamageSourceRegistry {
    public final Registry<DamageType> damageTypes;
    private final DamageSource spiked;

    public DamageSourceRegistry(RegistryAccess access) {
        this.damageTypes = access.registryOrThrow(Registries.DAMAGE_TYPE);
        this.spiked = this.source(DamageTypeRegistry.SPIKED);
    }

    public DamageSource source(ResourceKey<DamageType> p_270957_) {
        return new DamageSource(this.damageTypes.getHolderOrThrow(p_270957_));
    }
}
