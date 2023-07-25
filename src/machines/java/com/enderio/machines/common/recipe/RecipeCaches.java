package com.enderio.machines.common.recipe;

import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.integrations.vanilla.VanillaAlloySmeltingRecipe;
import com.enderio.machines.common.utility.RecipeInputCache;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
public class RecipeCaches {
    public static final RecipeInputCache<AlloySmeltingRecipe.ContainerWrapper, AlloySmeltingRecipe> ALLOY_SMELTING
        = new RecipeInputCache<>(MachineRecipes.ALLOY_SMELTING.type());

    public static final RecipeInputCache<Container, SmeltingRecipe> SMELTING
        = new RecipeInputCache<>(() -> RecipeType.SMELTING);

    public static final RecipeInputCache<RecipeWrapper, PaintingRecipe> PAINTING
        = new RecipeInputCache<>(MachineRecipes.PAINTING.type());

    public static final RecipeInputCache<SagMillingRecipe.Container, SagMillingRecipe> SAGMILLING
        = new RecipeInputCache<>(MachineRecipes.SAGMILLING.type());

    public static final RecipeInputCache<Container, SlicingRecipe> SLICING
        = new RecipeInputCache<>(MachineRecipes.SLICING.type());

    public static final RecipeInputCache<SoulBindingRecipe.Container, SoulBindingRecipe> SOUL_BINDING
        = new RecipeInputCache<>(MachineRecipes.SOUL_BINDING.type());

    @SubscribeEvent
    public static void registerReloadListener(AddReloadListenerEvent event) {
        ALLOY_SMELTING.markCacheDirty();
        SMELTING.markCacheDirty();
        PAINTING.markCacheDirty();
        SAGMILLING.markCacheDirty();
        SLICING.markCacheDirty();
        SOUL_BINDING.markCacheDirty();
    }

    @SubscribeEvent
    public static void onRecipesUpdated(RecipesUpdatedEvent event) {
        ALLOY_SMELTING.rebuildCache(event.getRecipeManager());
        SMELTING.rebuildCache(event.getRecipeManager());
        PAINTING.rebuildCache(event.getRecipeManager());
        SAGMILLING.rebuildCache(event.getRecipeManager());
        SLICING.rebuildCache(event.getRecipeManager());
        SOUL_BINDING.rebuildCache(event.getRecipeManager());
    }
}
