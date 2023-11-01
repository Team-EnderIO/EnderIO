package com.enderio.machines.common.recipe;

import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.utility.RecipeInputCache;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.wrapper.RecipeWrapper;


@Mod.EventBusSubscriber
public class RecipeCaches {
    public static final RecipeInputCache<AlloySmeltingRecipe.ContainerWrapper, AlloySmeltingRecipe> ALLOY_SMELTING
        = new RecipeInputCache<>(MachineRecipes.ALLOY_SMELTING.type());

    public static final RecipeInputCache<Container, SmeltingRecipe> SMELTING
        = new RecipeInputCache<>(() -> RecipeType.SMELTING);

    public static final RecipeInputCache<RecipeWrapper, PaintingRecipe> PAINTING
        = new RecipeInputCache<>(MachineRecipes.PAINTING.type());

    public static final RecipeInputCache<SagMillingRecipe.Container, SagMillingRecipe> SAG_MILLING
        = new RecipeInputCache<>(MachineRecipes.SAG_MILLING.type());

    public static final RecipeInputCache<SoulBindingRecipe.Container, SoulBindingRecipe> SOUL_BINDING
        = new RecipeInputCache<>(MachineRecipes.SOUL_BINDING.type());

    public static final RecipeInputCache<VatReagentRecipe.Container, VatReagentRecipe> VAT_REAGENT = new RecipeInputCache<>(MachineRecipes.VAT_REAGENT.type());

    public static final RecipeInputCache<VatFermentingRecipe.Container, VatFermentingRecipe> VAT_FERMENTING = new RecipeInputCache<>(
        MachineRecipes.VAT_FERMENTING.type());

    @SubscribeEvent
    public static void registerReloadListener(AddReloadListenerEvent event) {
        ALLOY_SMELTING.markCacheDirty();
        SMELTING.markCacheDirty();
        PAINTING.markCacheDirty();
        SAG_MILLING.markCacheDirty();
        SOUL_BINDING.markCacheDirty();
        VAT_REAGENT.markCacheDirty();
        VAT_FERMENTING.markCacheDirty();
    }

    @SubscribeEvent
    public static void onRecipesUpdated(RecipesUpdatedEvent event) {
        ALLOY_SMELTING.rebuildCache(event.getRecipeManager());
        SMELTING.rebuildCache(event.getRecipeManager());
        PAINTING.rebuildCache(event.getRecipeManager());
        SAG_MILLING.rebuildCache(event.getRecipeManager());
        SOUL_BINDING.rebuildCache(event.getRecipeManager());
        VAT_REAGENT.rebuildCache(event.getRecipeManager());
        VAT_FERMENTING.rebuildCache(event.getRecipeManager());
    }
}
