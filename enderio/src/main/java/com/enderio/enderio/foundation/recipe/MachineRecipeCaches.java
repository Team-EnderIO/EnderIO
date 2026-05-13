package com.enderio.enderio.foundation.recipe;

import com.enderio.enderio.api.recipes.alloy.AlloySmeltingInput;
import com.enderio.enderio.content.machines.alloy.AlloySmeltingRecipe;
import com.enderio.enderio.content.machines.painting.PaintingRecipe;
import com.enderio.enderio.content.machines.sag_mill.SagMillingRecipe;
import com.enderio.enderio.content.machines.soul_binder.SoulBindingRecipe;
import com.enderio.enderio.content.machines.vat.FermentingRecipe;
import com.enderio.enderio.init.EIORecipeTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

@EventBusSubscriber
public class MachineRecipeCaches {
    public static final RecipeInputCache<AlloySmeltingInput, AlloySmeltingRecipe> ALLOY_SMELTING_ONLY_ALLOY = new RecipeInputCache<>(
            EIORecipeTypes.ALLOY_SMELTING, recipe -> !recipe.isSmelting());

    public static final RecipeInputCache<AlloySmeltingInput, AlloySmeltingRecipe> ALLOY_SMELTING_ONLY_SMELTING = new RecipeInputCache<>(
            EIORecipeTypes.ALLOY_SMELTING, AlloySmeltingRecipe::isSmelting);

    public static final RecipeInputCache<PaintingRecipe.Input, PaintingRecipe> PAINTING = new RecipeInputCache<>(
            EIORecipeTypes.PAINTING);

    public static final RecipeInputCache<SagMillingRecipe.Input, SagMillingRecipe> SAG_MILLING = new RecipeInputCache<>(
            EIORecipeTypes.SAG_MILLING);

    public static final RecipeInputCache<SoulBindingRecipe.Input, SoulBindingRecipe> SOUL_BINDING = new RecipeInputCache<>(
            EIORecipeTypes.SOUL_BINDING);

    public static final RecipeInputCache<FermentingRecipe.Input, FermentingRecipe> FERMENTING = new RecipeInputCache<>(
            EIORecipeTypes.VAT_FERMENTING);

    @SubscribeEvent
    public static void registerReloadListener(AddServerReloadListenersEvent event) {
        ALLOY_SMELTING_ONLY_ALLOY.markCacheDirty();
        ALLOY_SMELTING_ONLY_SMELTING.markCacheDirty();
        PAINTING.markCacheDirty();
        SAG_MILLING.markCacheDirty();
        SOUL_BINDING.markCacheDirty();
        FERMENTING.markCacheDirty();
    }

    @SubscribeEvent
    public static void onRecipesUpdated(OnDatapackSyncEvent event) {
        var recipeManager = event.getPlayerList().getServer().getRecipeManager();
        ALLOY_SMELTING_ONLY_ALLOY.rebuildCache(recipeManager);
        ALLOY_SMELTING_ONLY_SMELTING.rebuildCache(recipeManager);
        PAINTING.rebuildCache(recipeManager);
        SAG_MILLING.rebuildCache(recipeManager);
        SOUL_BINDING.rebuildCache(recipeManager);
        FERMENTING.rebuildCache(recipeManager);
    }
}
