package com.enderio.machines.common.integrations.crafttweaker.recipe.manager;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.recipe.SlicingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.openzen.zencode.java.ZenCodeType;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;

@ZenRegister
@Document("mods/enderio/recipe/SliceAndSpliceRecipeManager")
@ZenCodeType.Name("mods.enderio.recipe.manager.SliceAndSpliceRecipeManager")
public class SliceAndSpliceRecipeManager implements IRecipeManager<SlicingRecipe> {

    @Override
    public RecipeType<SlicingRecipe> getRecipeType() {
        return MachineRecipes.SLICING.type().get();
    }

    /**
     *
     * @param name The recipe name
     * @param output The output item
     * @param input The input items, maximum of 6
     * @param energy The required energy
     * @docParam name "redstone_torches"
     * @docParam output <item:minecraft:redstone_torch>
     * @docParam input [<tag:item:minecraft:planks>, <item:minecraft:redstone>]
     * @docParam energy 10000
     */
    @ZenCodeType.Method
    public void addRecipe(String name, Item output, IIngredient[] input, int energy) {
        final ResourceLocation location = ResourceLocation.parse("crafttweaker:" + name);
        ArrayList<Ingredient> ingredientList = new ArrayList<>(Arrays.stream(input).map(IIngredient::asVanillaIngredient).toList());
        if (ingredientList.size() > 6) throw new InvalidParameterException("Too many slots");
        while (ingredientList.size() < 6) ingredientList.add(Ingredient.EMPTY);
        final SlicingRecipe recipe = new SlicingRecipe(output, ingredientList, energy);
        final RecipeHolder<SlicingRecipe> holder = new RecipeHolder<>(location, recipe);
        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, holder));
    }
}
