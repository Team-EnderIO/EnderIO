package com.enderio.machines.common.utility;

import com.enderio.machines.common.blocks.base.inventory.MachineInventory;
import com.enderio.machines.common.blocks.base.inventory.MultiSlotAccess;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class RecipeInputCache<T extends RecipeInput, R extends Recipe<T>> {
    private final Supplier<RecipeType<R>> recipeType;
    private final Predicate<R> filter;

    private final HashMap<Item, HashSet<RecipeHolder<R>>> itemToRecipesCache;
    private final HashMap<RecipeHolder<R>, List<Ingredient>> recipeToIngredientCache;
    private boolean isDirty;

    public RecipeInputCache(Supplier<RecipeType<R>> recipeType) {
        this(recipeType, recipe -> true);
    }

    public RecipeInputCache(Supplier<RecipeType<R>> recipeType, Predicate<R> filter) {
        this.recipeType = recipeType;
        this.filter = filter;
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
            var input = inputs.get(i);
            var invStack = input.getItemStack(inventory);
            if (input.getIndex() == slot) {
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

        Set<RecipeHolder<R>> possibleMatches = null;

        for (var input : inputs) {
            var matches = itemToRecipesCache.get(input.getItem());
            if (matches == null) {
                return false;
            }

            if (possibleMatches == null) {
                possibleMatches = matches;
            } else {
                possibleMatches = possibleMatches.stream().filter(matches::contains).collect(Collectors.toSet());
            }

            if (possibleMatches.isEmpty()) {
                return false;
            }

            boolean anyMatches = false;
            for (var match : possibleMatches) {
                var ingredients = recipeToIngredientCache.get(match);
                var checked = new boolean[inputs.size()];
                int matchCount = 0;

                for (Ingredient ingredient : ingredients) {
                    for (int i = 0; i < inputs.size(); i++) {
                        if (checked[i]) {
                            continue;
                        }

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
        recipeManager.getAllRecipesFor(recipeType.get())
                .stream()
                .filter(recipeHolder -> filter.test(recipeHolder.value()))
                .forEach(recipe -> {
                    var items = recipe.value()
                            .getIngredients()
                            .stream()
                            .flatMap(ingredient -> Arrays.stream(ingredient.getItems()))
                            .map(ItemStack::getItem)
                            .toList();

                    recipeToIngredientCache.put(recipe, recipe.value().getIngredients());
                    for (Item item : items) {
                        itemToRecipesCache.computeIfAbsent(item, i -> new HashSet<>()).add(recipe);
                    }
                });
    }
}
