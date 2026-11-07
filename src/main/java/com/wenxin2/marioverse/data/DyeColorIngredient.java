package com.wenxin2.marioverse.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/** Resolves a DyeColor from a dye-tagged crafting slot (e.g. Tags.Items.DYES), mirrors TagColorIngredient. */
public record DyeColorIngredient(TagKey<Item> tag) {
    public static final Codec<DyeColorIngredient> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(TagKey.codec(Registries.ITEM).fieldOf("color_tag").forGetter(DyeColorIngredient::tag))
            .apply(instance, DyeColorIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DyeColorIngredient> STREAM_CODEC = StreamCodec
            .of((buf, ci) -> buf.writeResourceLocation(ci.tag().location()),
            buf -> new DyeColorIngredient(TagKey.create(Registries.ITEM, buf.readResourceLocation())));

    public boolean test(ItemStack stack) {
        return stack.is(this.tag);
    }

    public DyeColor colorOf(ItemStack stack) {
        if (stack.isEmpty())
            return null;
        return stack.getItem() instanceof DyeItem dyeItem ? dyeItem.getDyeColor() : null;
    }

    public Ingredient toIngredient() {
        return Ingredient.of(this.tag);
    }
}
