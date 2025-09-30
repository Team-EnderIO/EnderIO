package com.enderio.enderio.machines.common.recipe;

import com.enderio.enderio.machines.EnderIOMachines;
import com.enderio.enderio.machines.common.blocks.alloy.AlloySmeltingRecipe;
import com.enderio.enderio.machines.common.blocks.painting.PaintingRecipe;
import com.enderio.enderio.machines.common.blocks.sag_mill.SagMillingRecipe;
import com.enderio.enderio.machines.common.blocks.soul_binder.SoulBindingRecipe;
import com.enderio.enderio.machines.common.blocks.vat.FermentingRecipe;
import com.enderio.enderio.machines.common.init.MachineRecipes;
import com.enderio.enderio.machines.common.utility.RecipeInputCache;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@EventBusSubscriber
public class RecipeCaches {
    public static final RecipeInputCache<AlloySmeltingRecipe.Input, AlloySmeltingRecipe> ALLOY_SMELTING_ONLY_ALLOY = new RecipeInputCache<>(
            MachineRecipes.ALLOY_SMELTING.type(), recipe -> !recipe.isSmelting());

    public static final RecipeInputCache<AlloySmeltingRecipe.Input, AlloySmeltingRecipe> ALLOY_SMELTING_ONLY_SMELTING = new RecipeInputCache<>(
            MachineRecipes.ALLOY_SMELTING.type(), AlloySmeltingRecipe::isSmelting);

    public static final RecipeInputCache<PaintingRecipe.Input, PaintingRecipe> PAINTING = new RecipeInputCache<>(
            MachineRecipes.PAINTING.type());

    public static final RecipeInputCache<SagMillingRecipe.Input, SagMillingRecipe> SAG_MILLING = new RecipeInputCache<>(
            MachineRecipes.SAG_MILLING.type());

    public static final RecipeInputCache<SoulBindingRecipe.Input, SoulBindingRecipe> SOUL_BINDING = new RecipeInputCache<>(
            MachineRecipes.SOUL_BINDING.type());

    public static final RecipeInputCache<FermentingRecipe.Input, FermentingRecipe> FERMENTING = new RecipeInputCache<>(
            MachineRecipes.VAT_FERMENTING.type());

    @SubscribeEvent
    public static void registerReloadListener(AddReloadListenerEvent event) {
        ALLOY_SMELTING_ONLY_ALLOY.markCacheDirty();
        ALLOY_SMELTING_ONLY_SMELTING.markCacheDirty();
        PAINTING.markCacheDirty();
        SAG_MILLING.markCacheDirty();
        SOUL_BINDING.markCacheDirty();
        FERMENTING.markCacheDirty();
    }

    @SubscribeEvent
    public static void onRecipesUpdated(RecipesUpdatedEvent event) {
        ALLOY_SMELTING_ONLY_ALLOY.rebuildCache(event.getRecipeManager());
        ALLOY_SMELTING_ONLY_SMELTING.rebuildCache(event.getRecipeManager());
        PAINTING.rebuildCache(event.getRecipeManager());
        SAG_MILLING.rebuildCache(event.getRecipeManager());
        SOUL_BINDING.rebuildCache(event.getRecipeManager());
        FERMENTING.rebuildCache(event.getRecipeManager());
    }
}
