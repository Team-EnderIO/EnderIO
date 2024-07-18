package com.enderio.machines.common.integrations.crafttweaker.recipe.manager;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.blamejared.crafttweaker.api.util.random.Percentaged;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.recipe.SagMillingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.openzen.zencode.java.ZenCodeType;

import java.util.ArrayList;
import java.util.Optional;

@ZenRegister
@Document("mods/enderio/recipe/SagMill")
@ZenCodeType.Name("mods.enderio.managers.SagMillRecipeManager")
public class SagMillRecipeManager implements IRecipeManager<SagMillingRecipe> {

    @Override
    public RecipeType<SagMillingRecipe> getRecipeType() {
        return MachineRecipes.SAG_MILLING.type().get();
    }

    /**
     *
     * @param name The name of the recipe
     * @param input The input item
     * @param output The possible outputs
     * @param energy The energy required
     * @param type The BonusType used
     * @docParam name "gravel_crushing"
     * @docParam input <item:minecraft:gravel>
     * @docParam output [<item:minecraft:sand> % 95, <item:enderio:iron_grit>
     * @docParam energy 1000
     */
    @ZenCodeType.Method
    public void addRecipe(String name, IIngredient input, Percentaged<IItemStack>[] output, int energy, @ZenCodeType.Optional("<constant:enderio:bonus_type:none>") SagMillingRecipe.BonusType type) {
        final ResourceLocation location = ResourceLocation.parse("crafttweaker:" + name);
        ArrayList<SagMillingRecipe.OutputItem> outputs = new ArrayList<>();
        for (Percentaged<IItemStack> item : output) {
            outputs.add(SagMillingRecipe.OutputItem.of(Optional.empty(), Optional.of(item.getData().getDefinition()), item.getData().amount(), (float) item.getPercentage(), false));
        }
        final SagMillingRecipe recipe = new SagMillingRecipe(input.asVanillaIngredient(), outputs, energy, type);
        final RecipeHolder<SagMillingRecipe> holder = new RecipeHolder<>(location, recipe);
        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, holder));
    }
}
