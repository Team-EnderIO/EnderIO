package com.enderio.machines.common.integrations.crafttweaker.recipe.manager;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredientWithAmount;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.recipe.AlloySmeltingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.openzen.zencode.java.ZenCodeType;

import java.util.ArrayList;

@ZenRegister
@Document("mods/enderio/recipe/AlloySmelter")
@ZenCodeType.Name("mods.enderio.recipe.manager.AlloySmelterRecipeManager")
public class AlloySmelterRecipeManager implements IRecipeManager<AlloySmeltingRecipe> {

    /**
     *
     * @param name The recipe name
     * @param inputs The input items
     * @param output The output item
     * @param energy The required energy
     * @param experience The experience given
     * @docParam name "iron_alloy_smelting"
     * @docParam inputs [<item:minecraft:cobblestone> * 2, <item:minecraft:gravel>]
     * @docParam output <item:minecraft:iron_ingot>
     * @docParam energy 10000
     * @docParam experience 0.7
     */
    @ZenCodeType.Method
    public void addRecipe(String name, IIngredientWithAmount[] inputs, IItemStack output, int energy, @ZenCodeType.OptionalFloat float experience) {
        final ResourceLocation location = ResourceLocation.parse("crafttweaker:" + name);
        final ArrayList<SizedIngredient> internalInputs = new ArrayList<>();
        for (IIngredientWithAmount ingredient : inputs) {
            SizedIngredient sizedIngredient = new SizedIngredient(ingredient.ingredient().asVanillaIngredient(), ingredient.amount());
            internalInputs.add(sizedIngredient);
        }
        final AlloySmeltingRecipe recipe = new AlloySmeltingRecipe(internalInputs, output.getInternal(), energy, experience);
        final RecipeHolder<AlloySmeltingRecipe> holder = new RecipeHolder<>(location, recipe);
        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, holder));
    }

    @Override
    public RecipeType<AlloySmeltingRecipe> getRecipeType() {
        return MachineRecipes.ALLOY_SMELTING.type().get();
    }
}
