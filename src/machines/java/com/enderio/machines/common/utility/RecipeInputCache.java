package com.enderio.machines.common.utility;

import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MultiSlotAccess;
import com.enderio.machines.common.recipe.MachineRecipe;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class RecipeInputCache<C extends Container, T extends Recipe<C>> {
    private final Supplier<RecipeType<T>> recipeType;
    private final HashMap<Item, HashSet<T>> itemToRecipesCache;
    private final HashMap<T, List<Ingredient>> recipeToIngredientCache;
    private boolean isDirty;

    public RecipeInputCache(Supplier<RecipeType<T>> recipeType) {
        this.recipeType = recipeType;
        this.itemToRecipesCache = new HashMap<>();
        this.recipeToIngredientCache = new HashMap<>();
    }

    /**
     * Test if there is a valid recipe if toAdd was added to the inventory.
     */
    public boolean hasValidRecipeIf(MachineInventory inventory, MultiSlotAccess inputs, int slot, ItemStack toAdd) {
        // Collect the list of items that the recipe will match against
        var currentItems = new ArrayList<ItemStack>();

        // Build the new inventory state after the addition
        for (int i = 0; i < inputs.size(); i++) {
            var invStack = inputs.get(i).getItemStack(inventory);
            if (i == slot) {
                currentItems.add(toAdd);
            } else if (!invStack.isEmpty()) {
                currentItems.add(invStack);
            }
        }

        // Try and match the items list
        return hasRecipe(currentItems);
    }

    public boolean hasRecipe(List<ItemStack> inputs) {
        checkCacheRebuild();

        Set<T> possibleMatches = null;

        for (var input : inputs) {
            var matches = itemToRecipesCache.get(input.getItem());
            if (matches == null) {
                return false;
            }

            if (possibleMatches == null) {
                possibleMatches = matches;
            } else {
                possibleMatches = possibleMatches.stream()
                    .filter(matches::contains)
                    .collect(Collectors.toSet());
            }

            if (possibleMatches.size() == 0) {
                return false;
            }

            boolean anyMatches = false;
            for (var match : possibleMatches) {
                var ingredients = recipeToIngredientCache.get(match);
                var checked = new boolean[inputs.size()];
                int matchCount = 0;

                for (Ingredient ingredient : ingredients) {
                    for (int i = 0; i < inputs.size(); i++) {
                        if (checked[i])
                            continue;

                        if (ingredient.test(inputs.get(i))) {
                            checked[i] = true;
                            matchCount++;
                            break;
                        }
                    }
                }

                if (matchCount >= inputs.size()) {
                    anyMatches = true;
                }
            }

            if (!anyMatches) {
                return false;
            }
        }

        return true;
    }

    public void markCacheDirty() {
        isDirty = true;
    }

    private void checkCacheRebuild() {
        if (isDirty && EffectiveSide.get().isServer()) {
            rebuildCache(ServerLifecycleHooks.getCurrentServer().getRecipeManager());
            isDirty = false;
        }
    }

    public void rebuildCache(RecipeManager recipeManager) {
        itemToRecipesCache.clear();
        recipeToIngredientCache.clear();

        var recipeType = this.recipeType.get();
        recipeManager.getAllRecipesFor(recipeType)
            .forEach(recipe -> {
                var items = recipe.getIngredients().stream()
                    .flatMap(ingredient -> Arrays.stream(ingredient.getItems()))
                    .map(ItemStack::getItem)
                    .toList();

                recipeToIngredientCache.put(recipe, recipe.getIngredients());
                for (Item item : items) {
                    itemToRecipesCache.computeIfAbsent(item, (i) -> new HashSet<>())
                        .add(recipe);
                }
            });
    }
}
