package com.wenxin2.marioverse.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wenxin2.marioverse.Marioverse;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class AddItemsModifier extends LootModifier {
    public static final MapCodec<AddItemsModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            LootModifier.codecStart(instance).and(ExtraCodecs.nonEmptyList(BuiltInRegistries.ITEM.byNameCodec().listOf())
                    .fieldOf("items").forGetter(o -> o.itemList)).apply(instance, AddItemsModifier::new));

    public static final Supplier<MapCodec<AddItemsModifier>> ADD_ITEMS_MODIFIER =
            Marioverse.GLOBAL_LOOT_MODIFIERS.register("add_items_modifier", () -> AddItemsModifier.CODEC);

    private final List<Item> itemList;

    public AddItemsModifier(LootItemCondition[] conditions, List<Item> itemList) {
        super(conditions);
        this.itemList = itemList;
    }

    @NotNull
    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return ADD_ITEMS_MODIFIER.get();
    }

    @NotNull
    @Override
    protected ObjectArrayList<ItemStack> doApply(@NotNull ObjectArrayList<ItemStack> generatedLoot, @NotNull LootContext context) {
        ObjectArrayList<ItemStack> newLoot = new ObjectArrayList<>();

        for (LootItemCondition condition : this.conditions) {
            if (!condition.test(context))
                return generatedLoot;
        }

        itemList.forEach(item -> generatedLoot.add(item.getDefaultInstance()));
        newLoot.add(Util.getRandom(generatedLoot, context.getRandom()));
        return newLoot;
    }

    public static void init() {}
}
