package com.enderio.enderio.content.machines.alloy;

import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.init.EIORecipeTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Wraps vanilla smelting recipes for use in the alloy smelter.
 * This used to be done with a mixin that injects runtime recipes, but 26.3 made that impossible.
 */
@EventBusSubscriber
public final class AlloySmeltingRecipeManager {
    private static final ConcurrentMap<SmeltingRecipe, AlloySmeltingRecipe> WRAPPERS = new ConcurrentHashMap<>();

    private AlloySmeltingRecipeManager() {}

    public static Optional<RecipeHolder<AlloySmeltingRecipe>> findRecipe(RecipeMap recipes, AlloySmeltingRecipe.Input input, Level level) {
        // Short-cut to avoid wasted time.
        if (input.isEmpty()) {
            return Optional.empty();
        }

        var alloyRecipe = recipes.getRecipesFor(EIORecipeTypes.ALLOY_SMELTING.get(), input, level).findFirst();
        if (alloyRecipe.isPresent() || !input.mode().canSmelt()) {
            return alloyRecipe;
        }

        return recipes.byType(RecipeType.SMELTING).stream()
            .filter(recipe -> WRAPPERS.containsKey(recipe.value()))
            .map(recipe -> new RecipeHolder<>(recipe.id(), WRAPPERS.get(recipe.value())))
            .filter(recipe -> recipe.value().matches(input, level))
            .findFirst();
    }

    public static List<RecipeHolder<AlloySmeltingRecipe>> getRecipes(RecipeMap recipes) {
        var result = new ArrayList<>(recipes.byType(EIORecipeTypes.ALLOY_SMELTING.get()));
        for (var recipe : recipes.byType(RecipeType.SMELTING)) {
            var wrapper = WRAPPERS.get(recipe.value());
            if (wrapper != null) {
                result.add(new RecipeHolder<>(recipe.id(), wrapper));
            }
        }
        return result;
    }

    @Nullable
    public static RecipeHolder<AlloySmeltingRecipe> byKey(RecipeMap recipes, Identifier id) {
        var recipe = recipes.byKey(ResourceKey.create(Registries.RECIPE, id));
        if (recipe == null) {
            return null;
        }

        if (recipe.value() instanceof AlloySmeltingRecipe alloyRecipe) {
            return new RecipeHolder<>(recipe.id(), alloyRecipe);
        }

        if (recipe.value() instanceof SmeltingRecipe smeltingRecipe) {
            var wrapper = WRAPPERS.get(smeltingRecipe);
            if (wrapper != null) {
                return new RecipeHolder<>(recipe.id(), wrapper);
            }
        }

        return null;
    }

    public static AlloySmeltingRecipe createWrapper(SmeltingRecipe recipe) {
        return new WrappedAlloySmeltingRecipe(recipe, MachinesConfig.COMMON.ENERGY.ALLOY_SMELTER_VANILLA_ITEM_ENERGY.get());
    }

    @SubscribeEvent
    public static void onRecipeReload(TagsUpdatedEvent.ServerDataLoad event) {
        WRAPPERS.clear();

        var recipes = event.getServerResources().getRecipeManager().recipeMap();
        for (var recipe : recipes.byType(RecipeType.SMELTING)) {
            WRAPPERS.put(recipe.value(), createWrapper(recipe.value()));
        }
    }
}
