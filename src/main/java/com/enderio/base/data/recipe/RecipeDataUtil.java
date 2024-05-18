package com.enderio.base.data.recipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecipeDataUtil {
    public static Set<String> getIngredientsModIds(List<Ingredient> ingredients) {
        Set<String> mods = new HashSet<>();
        for (Ingredient ingredient : ingredients) {
            putIngredientModIds(mods, ingredient);
        }
        return mods;
    }

    public static Set<String> getCountedIngredientsModIds(List<SizedIngredient> ingredients) {
        Set<String> mods = new HashSet<>();
        for (SizedIngredient ingredient : ingredients) {
            putIngredientModIds(mods, ingredient.ingredient());
        }
        return mods;
    }

    public static Set<String> getIngredientModIds(Ingredient ingredient) {
        Set<String> mods = new HashSet<>();
        putIngredientModIds(mods, ingredient);
        return mods;
    }

    public static Set<String> getCountedIngredientModIds(SizedIngredient ingredient) {
        Set<String> mods = new HashSet<>();
        putIngredientModIds(mods, ingredient.ingredient());
        return mods;
    }

    private static void putIngredientModIds(Set<String> mods, Ingredient ingredient) {
        for (Ingredient.Value value : ingredient.values) {
            if (value instanceof Ingredient.ItemValue itemValue) {
                itemValue.getItems().forEach(item -> {
                    var itemId = BuiltInRegistries.ITEM.getKey(item.getItem());
                    if (itemId != null) {
                        mods.add(itemId.getNamespace());
                    }
                });
            }
        }
    }
}
