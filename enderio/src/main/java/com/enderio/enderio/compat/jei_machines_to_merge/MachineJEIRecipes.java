//package com.enderio.enderio.compat.jei_machines_to_merge;
//
//import com.enderio.enderio.compat.jei_machines_to_merge.util.WrappedEnchanterRecipe;
//import com.enderio.enderio.content.machines.alloy.AlloySmeltingRecipe;
//import com.enderio.enderio.content.machines.obelisks.weather.WeatherChangeRecipe;
//import com.enderio.enderio.content.machines.sag_mill.SagMillingRecipe;
//import com.enderio.enderio.content.machines.slicer.SlicingRecipe;
//import com.enderio.enderio.content.machines.soul_binder.SoulBindingRecipe;
//import com.enderio.enderio.content.machines.vat.FermentingRecipe;
//import com.enderio.enderio.content.storage.fluid_tank.TankRecipe;
//import com.enderio.enderio.foundation.souldata.EngineSoul;
//import com.enderio.enderio.init.EIORecipes;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.multiplayer.ClientLevel;
//import net.minecraft.world.item.crafting.RecipeHolder;
//import net.minecraft.world.item.crafting.RecipeManager;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
//public class MachineJEIRecipes {
//    private final RecipeManager recipeManager;
//
//    public MachineJEIRecipes() {
//        Minecraft minecraft = Minecraft.getInstance();
//        ClientLevel level = minecraft.level;
//        this.recipeManager = Objects.requireNonNull(level).getRecipeManager();
//    }
//
//    public List<RecipeHolder<AlloySmeltingRecipe>> getAlloySmeltingRecipes() {
//        List<RecipeHolder<AlloySmeltingRecipe>> recipes = new ArrayList<>();
//        recipes.addAll(recipeManager.getAllRecipesFor(EIORecipes.ALLOY_SMELTING.type().get()));
//        return recipes;
//    }
//
//    public List<RecipeHolder<SlicingRecipe>> getSlicingRecipes() {
//        return recipeManager.getAllRecipesFor(EIORecipes.SLICING.type().get());
//    }
//
//    public List<RecipeHolder<SoulBindingRecipe>> getSoulBindingRecipes() {
//        return recipeManager.getAllRecipesFor(EIORecipes.SOUL_BINDING.type().get());
//    }
//
//    public List<RecipeHolder<TankRecipe>> getTankRecipes() {
//        return recipeManager.getAllRecipesFor(EIORecipes.TANK.type().get());
//    }
//
//    public List<WrappedEnchanterRecipe> getEnchanterRecipes() {
//        return recipeManager.getAllRecipesFor(EIORecipes.ENCHANTING.type().get())
//                .stream()
//                .<WrappedEnchanterRecipe>mapMulti((recipe, consumer) -> {
//                    for (int i = 1; i <= recipe.value().enchantment().value().getMaxLevel(); i++) {
//                        consumer.accept(new WrappedEnchanterRecipe(recipe, i));
//                    }
//                })
//                .toList();
//    }
//
//    public List<RecipeHolder<SagMillingRecipe>> getSagMillingRecipes() {
//        return recipeManager.getAllRecipesFor(EIORecipes.SAG_MILLING.type().get());
//    }
//
//    public List<EngineSoul.SoulData> getMobGeneratorRecipes() {
//        return EngineSoul.RELOAD_LISTENER.map.values().stream().toList();
//    }
//
//    public List<RecipeHolder<FermentingRecipe>> getVATRecipes() {
//        return recipeManager.getAllRecipesFor(EIORecipes.VAT_FERMENTING.type().get());
//    }
//
//    public List<RecipeHolder<WeatherChangeRecipe>> getWeatherRecipes() {
//        return recipeManager.getAllRecipesFor(EIORecipes.WEATHER_CHANGE.type().get());
//    }
//}
