package com.enderio.machines.common.utility;

import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MultiSlotAccess;
import com.enderio.machines.common.recipe.MachineRecipe;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class RecipeInputCache<C extends Container, T extends MachineRecipe<C>> {
    private final Supplier<RecipeType<T>> recipeType;
    private final HashMap<Item, HashSet<T>> ingredientToItemCache;
    private final HashMap<T, List<Item>> recipeToIngredientCache;
    private boolean isDirty;

    public RecipeInputCache(Supplier<RecipeType<T>> recipeType) {
        this.recipeType = recipeType;
        this.ingredientToItemCache = new HashMap<>();
        this.recipeToIngredientCache = new HashMap<>();
    }

    /**
     * Test if there is a valid recipe if toAdd was added to the inventory.
     */
    public boolean hasValidRecipeIf(MachineInventory inventory, MultiSlotAccess inputs, int slot, ItemStack toAdd) {
        // Collect the list of items that the recipe will match against
        var currentItems = new ArrayList<Item>();

        // This defines whether toAdd is being added as a new discrete ingredient or joining another
        var isNewIngredient = true;

        for (int i = 0; i < inputs.size(); i++) {
            var invStack = inputs.get(i).getItemStack(inventory);
            if (!invStack.isEmpty()) {
                if (i == slot && invStack.is(toAdd.getItem())) {
                    // Just test if it could actually take any more at all
                    if (invStack.getCount() + 1 <= invStack.getMaxStackSize()) {
                        isNewIngredient = false;
                    }
                }
                currentItems.add(invStack.getItem());
            }
        }

        // If it wouldn't join an existing input, its a new one.
        if (isNewIngredient) {
            currentItems.add(toAdd.getItem());
        }

        // Try and match the items list
        return hasRecipe(currentItems);
    }

    private boolean hasRecipe(List<Item> inputs) {
        checkCacheRebuild();

        Set<T> possibleMatches = null;

        Set<Item> distinctInputs = new HashSet<>();

        for (var input : inputs) {
            var matches = ingredientToItemCache.get(input);
            if (matches == null) {
                return false;
            }

            distinctInputs.add(input);

            if (possibleMatches == null) {
                possibleMatches = matches;
            } else {
                possibleMatches = possibleMatches.stream()
                    .filter(matches::contains)
                    .collect(Collectors.toSet());
            }

            // Check for duplicates
            for (var match : possibleMatches) {
                var items = recipeToIngredientCache.get(match);
                for (var in : distinctInputs) {
                    if (Collections.frequency(inputs, in) > Collections.frequency(items, in)) {
                        return false;
                    }
                }
            }

            if (possibleMatches.size() == 0) {
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

    protected abstract List<Item> getRecipeItems(T recipe);

    public void rebuildCache(RecipeManager recipeManager) {
        ingredientToItemCache.clear();
        recipeToIngredientCache.clear();

        var recipeType = this.recipeType.get();
        recipeManager.getAllRecipesFor(recipeType)
            .forEach(recipe -> {
                var items = getRecipeItems(recipe);
                recipeToIngredientCache.put(recipe, items);
                for (Item item : items) {
                    ingredientToItemCache.computeIfAbsent(item, (i) -> new HashSet<>())
                        .add(recipe);
                }
            });
    }
}
