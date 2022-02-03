package com.enderio.base.common.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.stream.Stream;

public record EnderIngredient(Ingredient ingredient, int count) {

    public static EnderIngredient EMPTY = new EnderIngredient(Ingredient.EMPTY, 0);

    public static EnderIngredient of() {
        return EMPTY;
    }

    public static EnderIngredient of(ItemLike... items) {
        return of(Arrays.stream(items).map(ItemStack::new));
    }

    public static EnderIngredient of(int count, ItemLike... items) {
        return of(count, Arrays.stream(items).map(ItemStack::new));
    }

    public static EnderIngredient of(ItemStack... stacks) {
        return of(Arrays.stream(stacks));
    }

    public static EnderIngredient of(int count, ItemStack... stacks) {
        return of(count, Arrays.stream(stacks));
    }

    public static EnderIngredient of(Stream<ItemStack> stacks) {
        return of(1, stacks);
    }

    public static EnderIngredient of(int count, Stream<ItemStack> stacks) {
        return new EnderIngredient(Ingredient.of(stacks), count);
    }

    public static EnderIngredient of(Tag<Item> tag) {
        return of(1, tag);
    }

    public static EnderIngredient of(int count, Tag<Item> tag) {
        return new EnderIngredient(Ingredient.fromValues(Stream.of(new Ingredient.TagValue(tag))), count);
    }

    public ItemStack[] getItems() {
        ItemStack[] matchingStacks = ingredient.getItems();
        for (ItemStack matchingStack : matchingStacks) {
            matchingStack.setCount(count);
        }
        return matchingStacks;
    }

    public boolean test(@Nullable ItemStack itemStack) {
        return ingredient.test(itemStack) && itemStack.getCount() >= count;
    }

    public static EnderIngredient fromJson(JsonObject json) {
        return new EnderIngredient(Ingredient.fromJson(json.get("ingredient")), json.get("count").getAsInt());
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("ingredient", ingredient.toJson());
        json.addProperty("count", count);
        return json;
    }

    public static EnderIngredient fromNetwork(FriendlyByteBuf buffer) {
        Ingredient ingredient = Ingredient.fromNetwork(buffer);
        return new EnderIngredient(ingredient, buffer.readShort());
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        ingredient.toNetwork(buffer);
        buffer.writeShort(count());
    }
}