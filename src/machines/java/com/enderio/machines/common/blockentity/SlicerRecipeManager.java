package com.enderio.machines.common.blockentity;

import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.recipe.SlicingRecipe;
import net.minecraft.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber
public class SlicerRecipeManager {
    private static final List<Set<Item>> ITEMS = Util.make(() -> {
        List<Set<Item>> tempList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            tempList.add(new HashSet<>());
        }
        return tempList;
    });

    private static final List<Set<Ingredient>> NON_OPTIMIZABLE_INGREDIENTS = Util.make(() -> {
        List<Set<Ingredient>> tempList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            tempList.add(new HashSet<>());
        }
        return tempList;
    });

    private static boolean clearCache = false;

    public static boolean isSlicerValid(ItemStack stack, int slot) {
        checkCacheRebuild();
        if (ITEMS.get(slot).contains(stack.getItem())) {
            return true;
        }

        for (Ingredient ingredient : NON_OPTIMIZABLE_INGREDIENTS.get(slot)) {
            if (ingredient.test(stack)) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void registerReloadListener(AddReloadListenerEvent event) {
        //Fired on datapack reload
        clearCache = true;
    }
    @SubscribeEvent
    public static void onRecipesUpdated(RecipesUpdatedEvent event) {
        rebuildCache(event.getRecipeManager());
    }

    private static void checkCacheRebuild() {
        if (clearCache && EffectiveSide.get().isServer()) {
            rebuildCache(ServerLifecycleHooks.getCurrentServer().getRecipeManager());
            clearCache = false;
        }
    }

    private static void rebuildCache(RecipeManager manager) {

        // Wipe the lookup table
        for (Set<Item> item : ITEMS) {
            item.clear();
        }
        for (Set<Ingredient> nonoptimizableingredient : NON_OPTIMIZABLE_INGREDIENTS) {
            nonoptimizableingredient.clear();
        }

        for (SlicingRecipe slicingRecipe : manager.getAllRecipesFor(MachineRecipes.SLICING.type().get())) {
            for (int i = 0; i < 6; i++) {
                Ingredient ingredient = slicingRecipe.getInputs().get(i);
                if (ingredient.isSimple()) {
                    Set<Item> itemset = ITEMS.get(i);
                    Arrays.stream(ingredient.getItems()).map(ItemStack::getItem).forEach(itemset::add);
                } else {
                    NON_OPTIMIZABLE_INGREDIENTS.get(i).add(ingredient);
                }
            }
        }
    }
}
