package com.wenxin2.marioverse.integration;

import com.google.common.base.Suppliers;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class CompatRegistry {
    public static final Supplier<Item> ANTIQUE_INK = make("supplementaries:antique_ink", BuiltInRegistries.ITEM);
    public static final Supplier<Item> BOMB_ITEM = make("supplementaries:bomb", BuiltInRegistries.ITEM);
    public static final Supplier<Item> BOMB_BLUE_ITEM = make("supplementaries:bomb_blue", BuiltInRegistries.ITEM);
    public static final Supplier<Item> BOMB_SPIKY_ITEM = make("supplementaries:bomb_spiky", BuiltInRegistries.ITEM);
    public static final Supplier<Item> BUBBLE_BLOWER = make("supplementaries:bubble_blower", BuiltInRegistries.ITEM);
    public static final Supplier<Item> CANNONBALL_ITEM = make("supplementaries:cannonball", BuiltInRegistries.ITEM);
    public static final Supplier<Item> CONFETTI_POPPER_ITEM = make("supplementaries:confetti_popper", BuiltInRegistries.ITEM);
    public static final Supplier<Item> HAT_STAND_ITEM = make("supplementaries:hat_stand", BuiltInRegistries.ITEM);
    public static final Supplier<Item> ICE_BOMB_ITEM = make("twilightforest:ice_bomb", BuiltInRegistries.ITEM);
    public static final Supplier<Item> SOAP = make("supplementaries:soap", BuiltInRegistries.ITEM);

    public static final Supplier<EntityType<?>> TEST_DUMMY = make("dummmmmmy:test_dummy", BuiltInRegistries.ENTITY_TYPE);
    public static final Supplier<EntityType<?>> BOMB = make("supplementaries:bomb", BuiltInRegistries.ENTITY_TYPE);
    public static final Supplier<EntityType<?>> CANNONBALL = make("supplementaries:cannonball", BuiltInRegistries.ENTITY_TYPE);
    public static final Supplier<EntityType<?>> HAT_STAND = make("supplementaries:hat_stand", BuiltInRegistries.ENTITY_TYPE);
    public static final Supplier<EntityType<?>> ICE_BOMB = make("twilightforest:thrown_ice", BuiltInRegistries.ENTITY_TYPE);

    public static final Supplier<ParticleType<?>> CONFETTI_PARTICLE = make("supplementaries:confetti", BuiltInRegistries.PARTICLE_TYPE);
    public static final Supplier<ParticleType<?>> STREAMER_PARTICLE = make("supplementaries:streamer", BuiltInRegistries.PARTICLE_TYPE);
    public static final Supplier<ParticleType<?>> SUDS_PARTICLE = make("supplementaries:suds", BuiltInRegistries.PARTICLE_TYPE);

    public static final Supplier<SoundEvent> BOMB_SOUND = make("supplementaries:item.bomb", BuiltInRegistries.SOUND_EVENT);
    public static final Supplier<SoundEvent> BUBBLE_BLOWER_SOUND = make("supplementaries:item.bubble_blower", BuiltInRegistries.SOUND_EVENT);
    public static final Supplier<SoundEvent> CANNON_SOUND = make("supplementaries:block.cannon.fire", BuiltInRegistries.SOUND_EVENT);
    public static final Supplier<SoundEvent> CONFETTI_POPPER_SOUND = make("supplementaries:item.confetti_popper", BuiltInRegistries.SOUND_EVENT);
    public static final Supplier<SoundEvent> ICE_BOMB_SOUND = make("twilightforest:item.twilightforest.ice_bomb.fired", BuiltInRegistries.SOUND_EVENT);

    public static final TagKey<Block> CANDLE_HOLDERS_BLOCK_TAG = blockTags("supplementaries", "candle_holders");
    public static final TagKey<EntityType<?>> ACCESSORIES_DEFAULT_TARGETS = entityTypeTags("accessories", "defaulted_targets");
    public static final TagKey<EntityType<?>> TWILIGHT_FOREST_BOSSES = entityTypeTags("twilightforest", "bosses");

    private static <T> Supplier<@Nullable T> make(String name, Registry<T> registry) {
        return Suppliers.memoize(() -> registry.getOptional(ResourceLocation.parse(name)).orElse(null));
    }

    public static TagKey<Block> blockTags(String mod_id, String name) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(mod_id, name));
    }

    public static TagKey<EntityType<?>> entityTypeTags(String mod_id, String name) {
        return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(mod_id, name));
    }
}
