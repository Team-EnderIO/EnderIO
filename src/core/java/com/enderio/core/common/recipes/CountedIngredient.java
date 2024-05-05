package com.enderio.core.common.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

// TODO: Javadocs

/**
 * An ingredient with an item count associated with it.
 */
public record CountedIngredient(Ingredient ingredient, int count) implements Predicate<ItemStack> {

    public static final Codec<CountedIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Ingredient.CODEC.fieldOf("Ingredient").forGetter(CountedIngredient::ingredient),
        Codec.INT.fieldOf("Count").forGetter(CountedIngredient::count)
    ).apply(instance, CountedIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CountedIngredient> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC,
        CountedIngredient::ingredient,
        ByteBufCodecs.INT,
        CountedIngredient::count,
        CountedIngredient::new
    );

    /**
     * An empty ingredient.
     */
    public static final CountedIngredient EMPTY = new CountedIngredient(Ingredient.EMPTY, 0);

    public static CountedIngredient of() {
        return EMPTY;
    }

    public static CountedIngredient of(ItemLike... items) {
        return of(Arrays.stream(items).map(ItemStack::new));
    }

    public static CountedIngredient of(int count, ItemLike... items) {
        return of(count, Arrays.stream(items).map(ItemStack::new));
    }

    public static CountedIngredient of(ItemStack... stacks) {
        return of(Arrays.stream(stacks));
    }

    public static CountedIngredient of(int count, ItemStack... stacks) {
        return of(count, Arrays.stream(stacks));
    }

    public static CountedIngredient of(Stream<ItemStack> stacks) {
        return of(1, stacks);
    }

    public static CountedIngredient of(int count, Stream<ItemStack> stacks) {
        return new CountedIngredient(Ingredient.of(stacks), count);
    }

    public static CountedIngredient of(TagKey<Item> tag) {
        return of(1, tag);
    }

    public static CountedIngredient of(int count, TagKey<Item> tag) {
        return new CountedIngredient(Ingredient.fromValues(Stream.of(new Ingredient.TagValue(tag))), count);
    }

    public static CountedIngredient of(Ingredient ingredient) {
        return new CountedIngredient(ingredient, 1);
    }

    public static CountedIngredient of(int count, Ingredient ingredient) {
        return new CountedIngredient(ingredient, count);
    }

    public List<ItemStack> getItems() {
        ItemStack[] matchingStacks = ingredient.getItems();
        for (ItemStack matchingStack : matchingStacks) {
            matchingStack.setCount(count);
        }
        return List.of(matchingStacks);
    }

    @Override
    public boolean test(@Nullable ItemStack itemStack) {
        return ingredient.test(itemStack) && itemStack.getCount() >= count;
    }
}
