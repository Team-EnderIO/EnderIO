package com.enderio.base.data.recipe;

import com.enderio.core.common.recipes.CountedIngredient;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

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

    public static Set<String> getCountedIngredientsModIds(List<CountedIngredient> ingredients) {
        Set<String> mods = new HashSet<>();
        for (CountedIngredient ingredient : ingredients) {
            putIngredientModIds(mods, ingredient.ingredient());
        }
        return mods;
    }

    public static Set<String> getIngredientModIds(Ingredient ingredient) {
        Set<String> mods = new HashSet<>();
        putIngredientModIds(mods, ingredient);
        return mods;
    }

    public static Set<String> getCountedIngredientModIds(CountedIngredient ingredient) {
        Set<String> mods = new HashSet<>();
        putIngredientModIds(mods, ingredient.ingredient());
        return mods;
    }

    private static void putIngredientModIds(Set<String> mods, Ingredient ingredient) {
        for (Ingredient.Value value : ingredient.values) {
            if (value instanceof Ingredient.ItemValue itemValue) {
                itemValue.getItems().forEach(item -> {
                    var itemId = ForgeRegistries.ITEMS.getKey(item.getItem());
                    if (itemId != null) {
                        mods.add(itemId.getNamespace());
                    }
                });
            }
        }
    }
}
