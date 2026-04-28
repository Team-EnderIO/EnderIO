package com.enderio.enderio.foundation.task.crafting;

import com.enderio.enderio.foundation.task.MachineTaskContext;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public interface CraftingMachineTaskContext<T extends Recipe<U>, U extends RecipeInput> extends MachineTaskContext {
    U recipeInput();
    float getOperationTimeMultiplier();
    boolean onCraftingTick();
    boolean consumeRecipeInputs(T recipe, TransactionContext transaction);
    boolean insertRecipeOutputs(T recipe, U recipeInput, RandomSource random, TransactionContext transaction);
}
