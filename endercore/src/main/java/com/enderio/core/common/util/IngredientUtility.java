package com.enderio.core.common.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;
import java.util.function.UnaryOperator;

public class IngredientUtility {
    public static List<ItemStack> getItemStacks(Ingredient ingredient) {
        return ingredient.items().map(ItemStack::new).toList();
    }

    public static List<ItemStack> getItemStacks(Ingredient ingredient, int count) {
        return ingredient.items().map(item -> new ItemStack(item, count)).toList();
    }

    public static List<ItemStack> getItemStacks(SizedIngredient ingredient) {
        return ingredient.ingredient().items().map(item -> new ItemStack(item, ingredient.count())).toList();
    }

    public static List<ItemStack> getItemStacks(SizedIngredient ingredient, UnaryOperator<Integer> countModifier) {
        return ingredient.ingredient().items().map(item -> new ItemStack(item, countModifier.apply(ingredient.count()))).toList();
    }
}
