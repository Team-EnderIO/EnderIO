package com.enderio.enderio.content.machines.slicer;

import com.enderio.enderio.init.EIORecipes;
import net.minecraft.core.Holder;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EventBusSubscriber
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
    public static void registerReloadListener(AddServerReloadListenersEvent event) {
        // Fired on datapack reload
        clearCache = true;
    }

    @SubscribeEvent
    public static void onRecipesUpdated(OnDatapackSyncEvent event) {
        rebuildCache(event.getPlayerList().getServer().getRecipeManager());
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

        for (RecipeHolder<SlicingRecipe> slicingRecipe : manager
                .recipeMap().byType(EIORecipes.SLICING.get())) {
            for (int i = 0; i < 6; i++) {
                Ingredient ingredient = slicingRecipe.value().inputs().get(i);
                if (ingredient.isSimple()) {
                    Set<Item> itemset = ITEMS.get(i);
                    ingredient.items().map(Holder::value).forEach(itemset::add);
                } else {
                    NON_OPTIMIZABLE_INGREDIENTS.get(i).add(ingredient);
                }
            }
        }
    }
}
