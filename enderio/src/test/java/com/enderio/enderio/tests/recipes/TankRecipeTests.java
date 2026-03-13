package com.enderio.enderio.tests.recipes;

import com.enderio.enderio.content.storage.fluid_tank.TankRecipe;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(EphemeralTestServerProvider.class)
public class TankRecipeTests {
    @Test
    public void fillRecipeMatches(MinecraftServer server) {
        var recipe = new TankRecipe(Ingredient.of(Items.SAND), new ItemStackTemplate(Items.SALMON, 1), SizedFluidIngredient.of(Fluids.WATER, 1000),
            TankRecipe.Mode.FILL);

        var matchingInput = new TankRecipe.Input(new ItemStack(Items.SAND, 2), ItemStack.EMPTY, new FluidStack(Fluids.WATER, 1000), 5000);
        Assertions.assertTrue(recipe.matches(matchingInput, server.overworld()));

        var inputIncorrectFluid = new TankRecipe.Input(new ItemStack(Items.SAND, 2), ItemStack.EMPTY, new FluidStack(Fluids.LAVA, 1000), 5000);
        Assertions.assertFalse(recipe.matches(inputIncorrectFluid, server.overworld()));

        var inputIncorrectItem = new TankRecipe.Input(new ItemStack(Items.COAL, 2), ItemStack.EMPTY, new FluidStack(Fluids.WATER, 1000), 5000);
        Assertions.assertFalse(recipe.matches(inputIncorrectItem, server.overworld()));

        var inputNotEnoughFluid = new TankRecipe.Input(new ItemStack(Items.SAND, 2), ItemStack.EMPTY, new FluidStack(Fluids.WATER, 500), 5000);
        Assertions.assertFalse(recipe.matches(inputNotEnoughFluid, server.overworld()));
    }

    @Test
    public void emptyRecipeMatches(MinecraftServer server) {
        var recipe = new TankRecipe(Ingredient.of(Items.SAND), new ItemStackTemplate(Items.SALMON, 1), SizedFluidIngredient.of(Fluids.WATER, 1000),
            TankRecipe.Mode.EMPTY);

        var matchingInput = new TankRecipe.Input(ItemStack.EMPTY, new ItemStack(Items.SAND, 2), new FluidStack(Fluids.WATER, 1000), 5000);
        Assertions.assertTrue(recipe.matches(matchingInput, server.overworld()));

        var inputTooFull = new TankRecipe.Input(ItemStack.EMPTY, new ItemStack(Items.SAND, 2), new FluidStack(Fluids.WATER, 5000), 5000);
        Assertions.assertFalse(recipe.matches(inputTooFull, server.overworld()));

        var inputIncorrectItem = new TankRecipe.Input(ItemStack.EMPTY, new ItemStack(Items.COAL, 2), new FluidStack(Fluids.WATER, 1000), 5000);
        Assertions.assertFalse(recipe.matches(inputIncorrectItem, server.overworld()));

        var inputIncompatibleFluidContents = new TankRecipe.Input(ItemStack.EMPTY, new ItemStack(Items.SAND, 2), new FluidStack(Fluids.LAVA, 1000), 5000);
        Assertions.assertFalse(recipe.matches(inputIncompatibleFluidContents, server.overworld()));
    }
}
