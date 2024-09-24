package com.wenxin2.marioverse.init;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.Marioverse;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;

public class LootCodecRegistry {

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<AddTableLootModifier>> ADD_TABLE_LOOT_MODIFIER_TYPE =
            Marioverse.LOOT_CODECS.register("add_table", () -> AddTableLootModifier.CODEC);

    public static void register() {}
}
