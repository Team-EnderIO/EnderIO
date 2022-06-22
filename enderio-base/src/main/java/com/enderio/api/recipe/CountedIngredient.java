package com.enderio.api.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

// TODO: Javadocs

/**
 * An ingredient with an item count associated with it.
 */
public record CountedIngredient(Ingredient ingredient, int count) implements Predicate<ItemStack> {

    /**
     * An empty ingredient.
     */
    public static CountedIngredient EMPTY = new CountedIngredient(Ingredient.EMPTY, 0);

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

    public ItemStack[] getItems() {
        ItemStack[] matchingStacks = ingredient.getItems();
        for (ItemStack matchingStack : matchingStacks) {
            matchingStack.setCount(count);
        }
        return matchingStacks;
    }

    @Override
    public boolean test(@Nullable ItemStack itemStack) {
        return ingredient.test(itemStack) && itemStack.getCount() >= count;
    }

    public static CountedIngredient fromJson(JsonObject json) {
        return new CountedIngredient(Ingredient.fromJson(json.get("ingredient")), json.get("count").getAsInt());
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("ingredient", ingredient.toJson());
        json.addProperty("count", count);
        return json;
    }

    public static CountedIngredient fromNetwork(FriendlyByteBuf buffer) {
        Ingredient ingredient = Ingredient.fromNetwork(buffer);
        return new CountedIngredient(ingredient, buffer.readShort());
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        ingredient.toNetwork(buffer);
        buffer.writeShort(count());
    }
}