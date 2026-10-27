package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public class LootTableRegistry {
    public static final ResourceKey<LootTable> CONSECUTIVE_BOUNCE_REWARD = lootTable("gameplay/consecutive_bounce_reward");
    public static final ResourceKey<LootTable> HEDGE_SNOW = lootTable("gameplay/hedge_snow");

    public static ResourceKey<LootTable> lootTable(String name) {
        return ResourceKey.create(Registries.LOOT_TABLE, Marioverse.id(name));
    }
}
