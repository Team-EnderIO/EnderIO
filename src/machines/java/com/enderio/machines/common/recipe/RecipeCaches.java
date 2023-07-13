package com.enderio.machines.common.recipe;

import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.utility.RecipeInputCache;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
public class RecipeCaches {
    public static final RecipeInputCache<AlloySmeltingRecipe.ContainerWrapper, AlloySmeltingRecipe> ALLOY_SMELTING = new RecipeInputCache<>(
        MachineRecipes.ALLOY_SMELTING.type()) {
        @Override
        protected List<Item> getRecipeItems(AlloySmeltingRecipe recipe) {
            return recipe.getInputs().stream()
                .flatMap(ingredient -> ingredient.getItems().stream())
                .map(ItemStack::getItem)
                .collect(Collectors.toList());
        }
    };

    @SubscribeEvent
    public static void registerReloadListener(AddReloadListenerEvent event) {
        ALLOY_SMELTING.markCacheDirty();
    }

    @SubscribeEvent
    public static void onRecipesUpdated(RecipesUpdatedEvent event) {
        ALLOY_SMELTING.rebuildCache(event.getRecipeManager());
    }
}
