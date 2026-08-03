package com.wenxin2.marioverse.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wenxin2.marioverse.utils.TextureColorSampler;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.Ingredient;

public record TagColorIngredient(TagKey<Item> tag) {
    public static final Codec<TagColorIngredient> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(TagKey.codec(Registries.ITEM).fieldOf("color_tag").forGetter(TagColorIngredient::tag))
            .apply(instance, TagColorIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TagColorIngredient> STREAM_CODEC = StreamCodec
            .of((buf, ci) -> buf.writeResourceLocation(ci.tag().location()),
            buf -> new TagColorIngredient(TagKey.create(net.minecraft.core.registries.Registries.ITEM, buf.readResourceLocation())));

    public boolean test(ItemStack stack) {
        return stack.is(this.tag);
    }

    public Integer colorOf(ItemStack stack) {
        if (stack.isEmpty())
            return null;

        Integer dyed = this.colorFromDyedComponent(stack);
        if (dyed != null)
            return dyed;

        if (this.tag.equals(ItemTags.WOOL)) {
            DyeColor named = this.colorFromName(stack);
            if (named != null) {
                Integer override = TextureColorSampler.woolColorOverride(named);
                return override != null ? override : named.getTextureDiffuseColor();
            }
            return null;
        }
        return TextureColorSampler.averageColorOf(stack.getItem());
    }

    private DyeColor colorFromName(ItemStack stack) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        String[] pathSegments = id.getPath().split("_");

        DyeColor best = null;
        int bestSegmentCount = 0;

        for (DyeColor color : DyeColor.values()) {
            String[] colorSegments = color.getName().split("_");
            if (colorSegments.length <= bestSegmentCount) continue;

            if (TagColorIngredient.containsSubsequence(pathSegments, colorSegments)) {
                best = color;
                bestSegmentCount = colorSegments.length;
            }
        }

        return best;
    }

    private Integer colorFromDyedComponent(ItemStack stack) {
        DyedItemColor dyed = stack.get(DataComponents.DYED_COLOR);
        return dyed != null ? dyed.rgb() : null;
    }

    private static boolean containsSubsequence(String[] haystack, String[] needle) {
        outer:
        for (int i = 0; i <= haystack.length - needle.length; i++) {
            for (int j = 0; j < needle.length; j++) {
                if (!haystack[i + j].equals(needle[j])) continue outer;
            }
            return true;
        }
        return false;
    }

    public Ingredient toIngredient() {
        return Ingredient.of(this.tag);
    }
}