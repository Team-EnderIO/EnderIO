package com.enderio.machines.client.patchouli;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIORecipes;
import com.enderio.base.common.recipe.FireCraftingRecipe;
import com.enderio.core.common.recipes.CountedIngredient;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.recipe.AlloySmeltingRecipe;
import com.google.gson.JsonElement;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class AlloySmelterProcessor implements IComponentProcessor {
    private AlloySmeltingRecipe recipe;

    @Override
    public void setup(IVariableProvider variables) {
        if (variables.has("recipe")) {
            if (Minecraft.getInstance().level != null) {
                RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
                recipe = (AlloySmeltingRecipe) recipeManager
                    .byKey(new ResourceLocation(variables.get("recipe").asString()))
                    .filter(recipe -> recipe.getType() == MachineRecipes.ALLOY_SMELTING.type().get())
                    .orElse(null);
            }

            if (recipe == null)
                EnderIO.LOGGER.error("Recipe: {} doesn't exist", variables.get("recipe").asString());
        } else {
            EnderIO.LOGGER.error("Alloy Smelting page contains no recipe");
        }

        EnderIO.LOGGER.debug("Recipe INput 0: {}", recipe.getInputs().get(0));
    }

    @Override
    public IVariable process(@NotNull String key) {
        if (recipe == null) return null;

        return switch (key) {
            case "exp" -> IVariable.wrap(recipe.getExperience());
            case "energy" -> IVariable.wrap(recipe.getEnergyCost(null)); // Null should be fine, I presume
            case "result" -> recipe.getResultStacks().get(0).stack().left().map(IVariable::from).orElse(null);
            case "input1" -> processCountedIngredient(recipe.getInputs().get(0));
            case "input2" -> Optional.ofNullable(recipe.getInputs().get(1)).map(AlloySmelterProcessor::processCountedIngredient).orElse(IVariable.empty());
            case "input3" -> Optional.ofNullable(recipe.getInputs().get(2)).map(AlloySmelterProcessor::processCountedIngredient).orElse(IVariable.empty());
            default -> null;
        };
    }

    private static IVariable processCountedIngredient(CountedIngredient ingredient) {
        Ingredient ing = ingredient.ingredient();
        int count = ingredient.count();
        return IVariable.wrap(
            Arrays.stream(ing.getItems())
            .map(stack -> ForgeRegistries.ITEMS.getKey(stack.getItem()).toString() + '#' + count + stack.getOrCreateTag())
            .collect(Collectors.joining(",")));
    }
}
