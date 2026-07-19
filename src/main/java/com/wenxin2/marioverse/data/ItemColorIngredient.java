package com.wenxin2.marioverse.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wenxin2.marioverse.utils.TextureColorSampler;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.Ingredient;

public record ItemColorIngredient(Item item) {
    public static final Codec<ItemColorIngredient> CODEC = RecordCodecBuilder
            .create(instance -> instance.group(BuiltInRegistries.ITEM.byNameCodec().fieldOf("color_item").forGetter(ItemColorIngredient::item))
                    .apply(instance, ItemColorIngredient::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemColorIngredient> STREAM_CODEC = StreamCodec
            .of((buf, ingredient) -> buf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(ingredient.item())),
                    buf -> new ItemColorIngredient(BuiltInRegistries.ITEM.get(buf.readResourceLocation())));

    public boolean test(ItemStack stack) {
        return stack.is(this.item);
    }

    public Integer colorOf(ItemStack stack) {
        if (stack.isEmpty())
            return null;

        Integer dyed = this.colorFromDyedComponent(stack);
        if (dyed != null)
            return dyed;

        return TextureColorSampler.averageColorOf(stack.getItem());
    }

    private Integer colorFromDyedComponent(ItemStack stack) {
        DyedItemColor dyed = stack.get(DataComponents.DYED_COLOR);
        return dyed != null ? dyed.rgb() : null;
    }

    public Ingredient toIngredient() {
        return Ingredient.of(this.item);
    }
}