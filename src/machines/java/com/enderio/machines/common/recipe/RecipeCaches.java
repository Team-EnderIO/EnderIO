package com.enderio.machines.common.recipe;

import com.enderio.machines.common.blockentity.AlloySmelterMode;
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
    public static final RecipeInputCache<AlloySmeltingRecipe.ContainerWrapper, AlloySmeltingRecipe> ALL_ALLOY_SMELTING
        = new RecipeInputCache<>(MachineRecipes.ALLOY_SMELTING.type());

    public static final RecipeInputCache<AlloySmeltingRecipe.ContainerWrapper, AlloySmeltingRecipe> ALLOY_SMELTING_ONLY_ALLOY
        = new RecipeInputCache<>(MachineRecipes.ALLOY_SMELTING.type(), recipe -> !recipe.isSmelting());

    public static final RecipeInputCache<AlloySmeltingRecipe.ContainerWrapper, AlloySmeltingRecipe> ALLOY_SMELTING_ONLY_SMELTING
        = new RecipeInputCache<>(MachineRecipes.ALLOY_SMELTING.type(), AlloySmeltingRecipe::isSmelting);

    public static final RecipeInputCache<Container, SmeltingRecipe> SMELTING
        = new RecipeInputCache<>(() -> RecipeType.SMELTING);

    public static final RecipeInputCache<RecipeWrapper, PaintingRecipe> PAINTING
        = new RecipeInputCache<>(MachineRecipes.PAINTING.type());

    public static final RecipeInputCache<SagMillingRecipe.Container, SagMillingRecipe> SAG_MILLING
        = new RecipeInputCache<>(MachineRecipes.SAG_MILLING.type());

    public static final RecipeInputCache<SoulBindingRecipe.Container, SoulBindingRecipe> SOUL_BINDING
        = new RecipeInputCache<>(MachineRecipes.SOUL_BINDING.type());

    public static RecipeInputCache<AlloySmeltingRecipe.ContainerWrapper, AlloySmeltingRecipe> getAlloySmeltingCache(AlloySmelterMode mode) {
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
    }
}
