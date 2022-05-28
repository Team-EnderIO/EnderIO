package com.enderio.machines.common.compat.vanilla;

import com.enderio.api.recipe.DataGenSerializer;
import com.enderio.api.recipe.IMachineRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;

import java.util.List;

public class VanillaSmeltingRecipe implements IMachineRecipe<VanillaSmeltingRecipe, Container> {
    private SmeltingRecipe recipe;

    // TODO: IMPLEMENT MEEEE

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ResourceLocation getId() {
        return null;
    }

    @Override
    public DataGenSerializer<VanillaSmeltingRecipe, Container> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return null;
    }

    @Override
    public void consumeInputs(Container container) {

    }

    @Override
    public List<ItemStack> craft(Container container) {
        return null;
    }

    @Override
    public int getOutputCount(Container container) {
        return 0;
    }

    @Override
    public List<List<ItemStack>> getAllInputs() {
        return null;
    }

    @Override
    public List<ItemStack> getAllOutputs() {
        return null;
    }

    @Override
    public int getEnergyCost() {
        return 0;
    }
}
