package com.enderio.machines.common.recipe;

import com.enderio.machines.EnderIOMachines;
import com.enderio.machines.common.blockentity.AlloySmelterMode;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.utility.RecipeInputCache;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;


@EventBusSubscriber(modid = EnderIOMachines.MODULE_MOD_ID)
public class RecipeCaches {
    public static final RecipeInputCache<AlloySmeltingRecipe.Input, AlloySmeltingRecipe> ALL_ALLOY_SMELTING
        = new RecipeInputCache<>(MachineRecipes.ALLOY_SMELTING.type());

    public static final RecipeInputCache<AlloySmeltingRecipe.Input, AlloySmeltingRecipe> ALLOY_SMELTING_ONLY_ALLOY
        = new RecipeInputCache<>(MachineRecipes.ALLOY_SMELTING.type(), recipe -> !recipe.isSmelting());

    public static final RecipeInputCache<AlloySmeltingRecipe.Input, AlloySmeltingRecipe> ALLOY_SMELTING_ONLY_SMELTING
        = new RecipeInputCache<>(MachineRecipes.ALLOY_SMELTING.type(), AlloySmeltingRecipe::isSmelting);

    public static final RecipeInputCache<SingleRecipeInput, SmeltingRecipe> SMELTING
        = new RecipeInputCache<>(() -> RecipeType.SMELTING);

    public static final RecipeInputCache<PaintingRecipe.Input, PaintingRecipe> PAINTING
        = new RecipeInputCache<>(MachineRecipes.PAINTING.type());

    public static final RecipeInputCache<SagMillingRecipe.Input, SagMillingRecipe> SAG_MILLING
        = new RecipeInputCache<>(MachineRecipes.SAG_MILLING.type());

    public static final RecipeInputCache<SoulBindingRecipe.Input, SoulBindingRecipe> SOUL_BINDING
        = new RecipeInputCache<>(MachineRecipes.SOUL_BINDING.type());

    public static final RecipeInputCache<FermentingRecipe.Input, FermentingRecipe> FERMENTING = new RecipeInputCache<>(
        MachineRecipes.VAT_FERMENTING.type());

    public static RecipeInputCache<AlloySmeltingRecipe.Input, AlloySmeltingRecipe> getAlloySmeltingCache(AlloySmelterMode mode) {
        if (mode.canSmelt() && mode.canAlloy()) {
            return ALL_ALLOY_SMELTING;
        } else if (mode.canSmelt()) {
            return ALLOY_SMELTING_ONLY_SMELTING;
        }

        return ALLOY_SMELTING_ONLY_ALLOY;
    }

    @SubscribeEvent
    public static void registerReloadListener(AddReloadListenerEvent event) {
        ALL_ALLOY_SMELTING.markCacheDirty();
        ALLOY_SMELTING_ONLY_ALLOY.markCacheDirty();
        ALLOY_SMELTING_ONLY_SMELTING.markCacheDirty();
        SMELTING.markCacheDirty();
        PAINTING.markCacheDirty();
        SAG_MILLING.markCacheDirty();
        SOUL_BINDING.markCacheDirty();
        FERMENTING.markCacheDirty();
    }

    @SubscribeEvent
    public static void onRecipesUpdated(RecipesUpdatedEvent event) {
        ALL_ALLOY_SMELTING.rebuildCache(event.getRecipeManager());
        ALLOY_SMELTING_ONLY_ALLOY.rebuildCache(event.getRecipeManager());
        ALLOY_SMELTING_ONLY_SMELTING.rebuildCache(event.getRecipeManager());
        SMELTING.rebuildCache(event.getRecipeManager());
        PAINTING.rebuildCache(event.getRecipeManager());
        SAG_MILLING.rebuildCache(event.getRecipeManager());
        SOUL_BINDING.rebuildCache(event.getRecipeManager());
        FERMENTING.rebuildCache(event.getRecipeManager());
    }
}
