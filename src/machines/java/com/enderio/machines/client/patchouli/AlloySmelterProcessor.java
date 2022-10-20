package com.enderio.machines.client.patchouli;

import com.enderio.base.client.compat.patchouli.ARecipeProcessor;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.recipe.AlloySmeltingRecipe;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.api.IVariable;

import java.util.Optional;

public class AlloySmelterProcessor extends ARecipeProcessor<AlloySmeltingRecipe> {
    public AlloySmelterProcessor() {
        super(MachineRecipes.ALLOY_SMELTING.type().get());
    }

    @Override
    public IVariable process(@NotNull String key) {
        if (recipe == null) return null;

        return switch (key) {
            case "exp" -> IVariable.wrap(recipe.getExperience());
            case "energy" -> IVariable.wrap(recipe.getEnergyCost(null)); // Null should be fine, I presume
            case "result" -> recipe.getResultStacks().get(0).stack().left().map(IVariable::from).orElse(IVariable.empty());
            case "input1" -> processCountedIngredient(recipe.getInputs().get(0));
            case "input2" -> Optional.ofNullable(recipe.getInputs().get(1)).map(ARecipeProcessor::processCountedIngredient).orElse(IVariable.empty());
            case "input3" -> Optional.ofNullable(recipe.getInputs().get(2)).map(ARecipeProcessor::processCountedIngredient).orElse(IVariable.empty());
            default -> null;
        };
    }
}
