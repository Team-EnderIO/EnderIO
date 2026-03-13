package com.enderio.enderio.compat.jei_machines_to_merge;

import com.enderio.enderio.compat.jei_machines_to_merge.util.WrappedEnchanterRecipe;
import com.enderio.enderio.content.machines.alloy.AlloySmeltingRecipe;
import com.enderio.enderio.content.machines.obelisks.weather.WeatherChangeRecipe;
import com.enderio.enderio.content.machines.sag_mill.SagMillingRecipe;
import com.enderio.enderio.content.machines.slicer.SlicingRecipe;
import com.enderio.enderio.content.machines.soul_binder.SoulBindingRecipe;
import com.enderio.enderio.content.machines.vat.FermentingRecipe;
import com.enderio.enderio.content.storage.fluid_tank.TankRecipe;
import com.enderio.enderio.foundation.souldata.EngineSoul;
import com.enderio.enderio.init.EIORecipeTypes;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EventBusSubscriber(Dist.CLIENT)
public class MachineJEIRecipes {
    private static RecipeMap RECIPE_MAP;
    private static final Set<RecipeType<?>> KNOWN_TYPES = new HashSet<>();

    public MachineJEIRecipes() {

    }

    public List<RecipeHolder<AlloySmeltingRecipe>> getAlloySmeltingRecipes() {
        return new ArrayList<>(RECIPE_MAP.byType(EIORecipeTypes.ALLOY_SMELTING.get()));
    }

    public List<RecipeHolder<SlicingRecipe>> getSlicingRecipes() {
        return new ArrayList<>(RECIPE_MAP.byType(EIORecipeTypes.SLICING.get()));
    }

    public List<RecipeHolder<SoulBindingRecipe>> getSoulBindingRecipes() {
        return new ArrayList<>(RECIPE_MAP.byType(EIORecipeTypes.SOUL_BINDING.get()));
    }

    public List<RecipeHolder<TankRecipe>> getTankRecipes() {
        return new ArrayList<>(RECIPE_MAP.byType(EIORecipeTypes.TANK.get()));
    }

    public List<WrappedEnchanterRecipe> getEnchanterRecipes() {
        return RECIPE_MAP.byType(EIORecipeTypes.ENCHANTING.get())
                .stream()
                .<WrappedEnchanterRecipe>mapMulti((recipe, consumer) -> {
                    for (int i = 1; i <= recipe.value().enchantment().value().getMaxLevel(); i++) {
                        consumer.accept(new WrappedEnchanterRecipe(recipe, i));
                    }
                })
                .toList();
    }

    public List<RecipeHolder<SagMillingRecipe>> getSagMillingRecipes() {
        return new ArrayList<>(RECIPE_MAP.byType(EIORecipeTypes.SAG_MILLING.get()));
    }

    public List<EngineSoul.SoulData> getMobGeneratorRecipes() {
        return EngineSoul.RELOAD_LISTENER.map.values().stream().toList();
    }

    public List<RecipeHolder<FermentingRecipe>> getVATRecipes() {
        return new ArrayList<>(RECIPE_MAP.byType(EIORecipeTypes.VAT_FERMENTING.get()));
    }

    public List<RecipeHolder<WeatherChangeRecipe>> getWeatherRecipes() {
        return new ArrayList<>(RECIPE_MAP.byType(EIORecipeTypes.WEATHER_CHANGE.get()));
    }

    @SubscribeEvent
    public static void getRecipes(RecipesReceivedEvent event) {
        RECIPE_MAP = event.getRecipeMap();
        KNOWN_TYPES.clear();
        KNOWN_TYPES.addAll(event.getRecipeTypes());
    }
}
