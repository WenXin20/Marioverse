package com.wenxin2.warp_pipes.integration;

import com.google.common.base.Suppliers;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

public class CompatRegistry {
    public static final Supplier<Item> ANTIQUE_INK = make("supplementaries:antique_ink", BuiltInRegistries.ITEM);
    public static final Supplier<Item> BUBBLE_BLOWER = make("supplementaries:bubble_blower", BuiltInRegistries.ITEM);
    public static final Supplier<Item> SOAP = make("supplementaries:soap", BuiltInRegistries.ITEM);
    public static final Supplier<SoundEvent> BUBBLE_BLOWER_SOUND = make("supplementaries:item.bubble_blower", BuiltInRegistries.SOUND_EVENT);
    public static final Supplier<ParticleType<?>> SUDS_PARTICLE = make("supplementaries:suds", BuiltInRegistries.PARTICLE_TYPE);

    private static <T> Supplier<@Nullable T> make(String name, Registry<T> registry) {
        return Suppliers.memoize(() -> registry.getOptional(ResourceLocation.parse(name)).orElse(null));
    }
}
